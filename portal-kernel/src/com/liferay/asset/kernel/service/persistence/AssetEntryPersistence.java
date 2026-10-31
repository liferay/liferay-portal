/**
 * SPDX-FileCopyrightText: (c) 2000 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

package com.liferay.asset.kernel.service.persistence;

import com.liferay.asset.kernel.exception.NoSuchEntryException;
import com.liferay.asset.kernel.model.AssetEntry;
import com.liferay.portal.kernel.service.persistence.BasePersistence;
import com.liferay.portal.kernel.service.persistence.change.tracking.CTPersistence;

import org.osgi.annotation.versioning.ProviderType;

/**
 * The persistence interface for the asset entry service.
 *
 * <p>
 * Caching information and settings can be found in <code>portal.properties</code>
 * </p>
 *
 * @author Brian Wing Shun Chan
 * @see AssetEntryUtil
 * @generated
 */
@ProviderType
public interface AssetEntryPersistence
	extends BasePersistence<AssetEntry>, CTPersistence<AssetEntry> {

	/*
	 * NOTE FOR DEVELOPERS:
	 *
	 * Never modify or reference this interface directly. Always use {@link AssetEntryUtil} to access the asset entry persistence. Modify <code>service.xml</code> and rerun ServiceBuilder to regenerate this interface.
	 */

	/**
	 * Returns an ordered range of all the asset entries where groupId = &#63;.
	 *
	 * <p>
	 * Useful when paginating results. Returns a maximum of <code>end - start</code> instances. <code>start</code> and <code>end</code> are not primary keys, they are indexes in the result set. Thus, <code>0</code> refers to the first result in the set. Setting both <code>start</code> and <code>end</code> to <code>com.liferay.portal.kernel.dao.orm.QueryUtil#ALL_POS</code> will return the full result set. If <code>orderByComparator</code> is specified, then the query will include the given ORDER BY logic. If <code>orderByComparator</code> is absent, then the query will include the default ORDER BY logic from <code>com.liferay.portlet.asset.model.impl.AssetEntryModelImpl</code>.
	 * </p>
	 *
	 * @param groupId the group ID
	 * @param start the lower bound of the range of asset entries
	 * @param end the upper bound of the range of asset entries (not inclusive)
	 * @param orderByComparator the comparator to order the results by (optionally <code>null</code>)
	 * @param useFinderCache whether to use the finder cache
	 * @return the ordered range of matching asset entries
	 */
	public java.util.List<AssetEntry> findByGroupId(
		long groupId, int start, int end,
		com.liferay.portal.kernel.util.OrderByComparator<AssetEntry>
			orderByComparator,
		boolean useFinderCache);

	/**
	 * Returns the first asset entry in the ordered set where groupId = &#63;.
	 *
	 * @param groupId the group ID
	 * @param orderByComparator the comparator to order the set by (optionally <code>null</code>)
	 * @return the first matching asset entry
	 * @throws NoSuchEntryException if a matching asset entry could not be found
	 */
	public AssetEntry findByGroupId_First(
			long groupId,
			com.liferay.portal.kernel.util.OrderByComparator<AssetEntry>
				orderByComparator)
		throws NoSuchEntryException;

	/**
	 * Returns the first asset entry in the ordered set where groupId = &#63;.
	 *
	 * @param groupId the group ID
	 * @param orderByComparator the comparator to order the set by (optionally <code>null</code>)
	 * @return the first matching asset entry, or <code>null</code> if a matching asset entry could not be found
	 */
	public AssetEntry fetchByGroupId_First(
		long groupId,
		com.liferay.portal.kernel.util.OrderByComparator<AssetEntry>
			orderByComparator);

	/**
	 * Removes all the asset entries where groupId = &#63; from the database.
	 *
	 * @param groupId the group ID
	 */
	public void removeByGroupId(long groupId);

	/**
	 * Returns the number of asset entries where groupId = &#63;.
	 *
	 * @param groupId the group ID
	 * @return the number of matching asset entries
	 */
	public int countByGroupId(long groupId);

	/**
	 * Returns an ordered range of all the asset entries where companyId = &#63;.
	 *
	 * <p>
	 * Useful when paginating results. Returns a maximum of <code>end - start</code> instances. <code>start</code> and <code>end</code> are not primary keys, they are indexes in the result set. Thus, <code>0</code> refers to the first result in the set. Setting both <code>start</code> and <code>end</code> to <code>com.liferay.portal.kernel.dao.orm.QueryUtil#ALL_POS</code> will return the full result set. If <code>orderByComparator</code> is specified, then the query will include the given ORDER BY logic. If <code>orderByComparator</code> is absent, then the query will include the default ORDER BY logic from <code>com.liferay.portlet.asset.model.impl.AssetEntryModelImpl</code>.
	 * </p>
	 *
	 * @param companyId the company ID
	 * @param start the lower bound of the range of asset entries
	 * @param end the upper bound of the range of asset entries (not inclusive)
	 * @param orderByComparator the comparator to order the results by (optionally <code>null</code>)
	 * @param useFinderCache whether to use the finder cache
	 * @return the ordered range of matching asset entries
	 */
	public java.util.List<AssetEntry> findByCompanyId(
		long companyId, int start, int end,
		com.liferay.portal.kernel.util.OrderByComparator<AssetEntry>
			orderByComparator,
		boolean useFinderCache);

	/**
	 * Returns the first asset entry in the ordered set where companyId = &#63;.
	 *
	 * @param companyId the company ID
	 * @param orderByComparator the comparator to order the set by (optionally <code>null</code>)
	 * @return the first matching asset entry
	 * @throws NoSuchEntryException if a matching asset entry could not be found
	 */
	public AssetEntry findByCompanyId_First(
			long companyId,
			com.liferay.portal.kernel.util.OrderByComparator<AssetEntry>
				orderByComparator)
		throws NoSuchEntryException;

	/**
	 * Returns the first asset entry in the ordered set where companyId = &#63;.
	 *
	 * @param companyId the company ID
	 * @param orderByComparator the comparator to order the set by (optionally <code>null</code>)
	 * @return the first matching asset entry, or <code>null</code> if a matching asset entry could not be found
	 */
	public AssetEntry fetchByCompanyId_First(
		long companyId,
		com.liferay.portal.kernel.util.OrderByComparator<AssetEntry>
			orderByComparator);

	/**
	 * Removes all the asset entries where companyId = &#63; from the database.
	 *
	 * @param companyId the company ID
	 */
	public void removeByCompanyId(long companyId);

	/**
	 * Returns the number of asset entries where companyId = &#63;.
	 *
	 * @param companyId the company ID
	 * @return the number of matching asset entries
	 */
	public int countByCompanyId(long companyId);

	/**
	 * Returns an ordered range of all the asset entries where classUuid = &#63;.
	 *
	 * <p>
	 * Useful when paginating results. Returns a maximum of <code>end - start</code> instances. <code>start</code> and <code>end</code> are not primary keys, they are indexes in the result set. Thus, <code>0</code> refers to the first result in the set. Setting both <code>start</code> and <code>end</code> to <code>com.liferay.portal.kernel.dao.orm.QueryUtil#ALL_POS</code> will return the full result set. If <code>orderByComparator</code> is specified, then the query will include the given ORDER BY logic. If <code>orderByComparator</code> is absent, then the query will include the default ORDER BY logic from <code>com.liferay.portlet.asset.model.impl.AssetEntryModelImpl</code>.
	 * </p>
	 *
	 * @param classUuid the class uuid
	 * @param start the lower bound of the range of asset entries
	 * @param end the upper bound of the range of asset entries (not inclusive)
	 * @param orderByComparator the comparator to order the results by (optionally <code>null</code>)
	 * @param useFinderCache whether to use the finder cache
	 * @return the ordered range of matching asset entries
	 */
	public java.util.List<AssetEntry> findByClassUuid(
		String classUuid, int start, int end,
		com.liferay.portal.kernel.util.OrderByComparator<AssetEntry>
			orderByComparator,
		boolean useFinderCache);

	/**
	 * Returns the first asset entry in the ordered set where classUuid = &#63;.
	 *
	 * @param classUuid the class uuid
	 * @param orderByComparator the comparator to order the set by (optionally <code>null</code>)
	 * @return the first matching asset entry
	 * @throws NoSuchEntryException if a matching asset entry could not be found
	 */
	public AssetEntry findByClassUuid_First(
			String classUuid,
			com.liferay.portal.kernel.util.OrderByComparator<AssetEntry>
				orderByComparator)
		throws NoSuchEntryException;

	/**
	 * Returns the first asset entry in the ordered set where classUuid = &#63;.
	 *
	 * @param classUuid the class uuid
	 * @param orderByComparator the comparator to order the set by (optionally <code>null</code>)
	 * @return the first matching asset entry, or <code>null</code> if a matching asset entry could not be found
	 */
	public AssetEntry fetchByClassUuid_First(
		String classUuid,
		com.liferay.portal.kernel.util.OrderByComparator<AssetEntry>
			orderByComparator);

	/**
	 * Removes all the asset entries where classUuid = &#63; from the database.
	 *
	 * @param classUuid the class uuid
	 */
	public void removeByClassUuid(String classUuid);

	/**
	 * Returns the number of asset entries where classUuid = &#63;.
	 *
	 * @param classUuid the class uuid
	 * @return the number of matching asset entries
	 */
	public int countByClassUuid(String classUuid);

	/**
	 * Returns the asset entry where groupId = &#63; and classUuid = &#63; or throws a <code>NoSuchEntryException</code> if it could not be found.
	 *
	 * @param groupId the group ID
	 * @param classUuid the class uuid
	 * @return the matching asset entry
	 * @throws NoSuchEntryException if a matching asset entry could not be found
	 */
	public AssetEntry findByG_CU(long groupId, String classUuid)
		throws NoSuchEntryException;

	/**
	 * Returns the asset entry where groupId = &#63; and classUuid = &#63; or returns <code>null</code> if it could not be found, optionally using the finder cache.
	 *
	 * @param groupId the group ID
	 * @param classUuid the class uuid
	 * @param useFinderCache whether to use the finder cache
	 * @return the matching asset entry, or <code>null</code> if a matching asset entry could not be found
	 */
	public AssetEntry fetchByG_CU(
		long groupId, String classUuid, boolean useFinderCache);

	/**
	 * Removes the asset entry where groupId = &#63; and classUuid = &#63; from the database.
	 *
	 * @param groupId the group ID
	 * @param classUuid the class uuid
	 * @return the asset entry that was removed
	 */
	public AssetEntry removeByG_CU(long groupId, String classUuid)
		throws NoSuchEntryException;

	/**
	 * Returns the number of asset entries where groupId = &#63; and classUuid = &#63;.
	 *
	 * @param groupId the group ID
	 * @param classUuid the class uuid
	 * @return the number of matching asset entries
	 */
	public int countByG_CU(long groupId, String classUuid);

	/**
	 * Returns the asset entry where classNameId = &#63; and classPK = &#63; or throws a <code>NoSuchEntryException</code> if it could not be found.
	 *
	 * @param classNameId the class name ID
	 * @param classPK the class pk
	 * @return the matching asset entry
	 * @throws NoSuchEntryException if a matching asset entry could not be found
	 */
	public AssetEntry findByC_C(long classNameId, long classPK)
		throws NoSuchEntryException;

	/**
	 * Returns the asset entry where classNameId = &#63; and classPK = &#63; or returns <code>null</code> if it could not be found, optionally using the finder cache.
	 *
	 * @param classNameId the class name ID
	 * @param classPK the class pk
	 * @param useFinderCache whether to use the finder cache
	 * @return the matching asset entry, or <code>null</code> if a matching asset entry could not be found
	 */
	public AssetEntry fetchByC_C(
		long classNameId, long classPK, boolean useFinderCache);

	/**
	 * Removes the asset entry where classNameId = &#63; and classPK = &#63; from the database.
	 *
	 * @param classNameId the class name ID
	 * @param classPK the class pk
	 * @return the asset entry that was removed
	 */
	public AssetEntry removeByC_C(long classNameId, long classPK)
		throws NoSuchEntryException;

	/**
	 * Returns the number of asset entries where classNameId = &#63; and classPK = &#63;.
	 *
	 * @param classNameId the class name ID
	 * @param classPK the class pk
	 * @return the number of matching asset entries
	 */
	public int countByC_C(long classNameId, long classPK);

	/**
	 * Returns an ordered range of all the asset entries where groupId = &#63; and classNameId = &#63; and visible = &#63;.
	 *
	 * <p>
	 * Useful when paginating results. Returns a maximum of <code>end - start</code> instances. <code>start</code> and <code>end</code> are not primary keys, they are indexes in the result set. Thus, <code>0</code> refers to the first result in the set. Setting both <code>start</code> and <code>end</code> to <code>com.liferay.portal.kernel.dao.orm.QueryUtil#ALL_POS</code> will return the full result set. If <code>orderByComparator</code> is specified, then the query will include the given ORDER BY logic. If <code>orderByComparator</code> is absent, then the query will include the default ORDER BY logic from <code>com.liferay.portlet.asset.model.impl.AssetEntryModelImpl</code>.
	 * </p>
	 *
	 * @param groupId the group ID
	 * @param classNameId the class name ID
	 * @param visible the visible
	 * @param start the lower bound of the range of asset entries
	 * @param end the upper bound of the range of asset entries (not inclusive)
	 * @param orderByComparator the comparator to order the results by (optionally <code>null</code>)
	 * @param useFinderCache whether to use the finder cache
	 * @return the ordered range of matching asset entries
	 */
	public java.util.List<AssetEntry> findByG_C_V(
		long groupId, long classNameId, boolean visible, int start, int end,
		com.liferay.portal.kernel.util.OrderByComparator<AssetEntry>
			orderByComparator,
		boolean useFinderCache);

	/**
	 * Returns the first asset entry in the ordered set where groupId = &#63; and classNameId = &#63; and visible = &#63;.
	 *
	 * @param groupId the group ID
	 * @param classNameId the class name ID
	 * @param visible the visible
	 * @param orderByComparator the comparator to order the set by (optionally <code>null</code>)
	 * @return the first matching asset entry
	 * @throws NoSuchEntryException if a matching asset entry could not be found
	 */
	public AssetEntry findByG_C_V_First(
			long groupId, long classNameId, boolean visible,
			com.liferay.portal.kernel.util.OrderByComparator<AssetEntry>
				orderByComparator)
		throws NoSuchEntryException;

	/**
	 * Returns the first asset entry in the ordered set where groupId = &#63; and classNameId = &#63; and visible = &#63;.
	 *
	 * @param groupId the group ID
	 * @param classNameId the class name ID
	 * @param visible the visible
	 * @param orderByComparator the comparator to order the set by (optionally <code>null</code>)
	 * @return the first matching asset entry, or <code>null</code> if a matching asset entry could not be found
	 */
	public AssetEntry fetchByG_C_V_First(
		long groupId, long classNameId, boolean visible,
		com.liferay.portal.kernel.util.OrderByComparator<AssetEntry>
			orderByComparator);

	/**
	 * Removes all the asset entries where groupId = &#63; and classNameId = &#63; and visible = &#63; from the database.
	 *
	 * @param groupId the group ID
	 * @param classNameId the class name ID
	 * @param visible the visible
	 */
	public void removeByG_C_V(long groupId, long classNameId, boolean visible);

	/**
	 * Returns the number of asset entries where groupId = &#63; and classNameId = &#63; and visible = &#63;.
	 *
	 * @param groupId the group ID
	 * @param classNameId the class name ID
	 * @param visible the visible
	 * @return the number of matching asset entries
	 */
	public int countByG_C_V(long groupId, long classNameId, boolean visible);

	/**
	 * Creates a new asset entry with the primary key. Does not add the asset entry to the database.
	 *
	 * @param entryId the primary key for the new asset entry
	 * @return the new asset entry
	 */
	public AssetEntry create(long entryId);

	/**
	 * Removes the asset entry with the primary key from the database. Also notifies the appropriate model listeners.
	 *
	 * @param entryId the primary key of the asset entry
	 * @return the asset entry that was removed
	 * @throws NoSuchEntryException if a asset entry with the primary key could not be found
	 */
	public AssetEntry remove(long entryId) throws NoSuchEntryException;

	public AssetEntry updateImpl(AssetEntry assetEntry);

	/**
	 * Returns the asset entry with the primary key or throws a <code>NoSuchEntryException</code> if it could not be found.
	 *
	 * @param entryId the primary key of the asset entry
	 * @return the asset entry
	 * @throws NoSuchEntryException if a asset entry with the primary key could not be found
	 */
	public AssetEntry findByPrimaryKey(long entryId)
		throws NoSuchEntryException;

	/**
	 * Returns the asset entry with the primary key or returns <code>null</code> if it could not be found.
	 *
	 * @param entryId the primary key of the asset entry
	 * @return the asset entry, or <code>null</code> if a asset entry with the primary key could not be found
	 */
	public AssetEntry fetchByPrimaryKey(long entryId);

	/**
	 * Returns the primaryKeys of asset tags associated with the asset entry.
	 *
	 * @param pk the primary key of the asset entry
	 * @return long[] of the primaryKeys of asset tags associated with the asset entry
	 */
	public long[] getAssetTagPrimaryKeys(long pk);

	/**
	 * Returns all the asset tags associated with the asset entry.
	 *
	 * @param pk the primary key of the asset entry
	 * @return the asset tags associated with the asset entry
	 */
	public java.util.List<com.liferay.asset.kernel.model.AssetTag> getAssetTags(
		long pk);

	/**
	 * Returns a range of all the asset tags associated with the asset entry.
	 *
	 * <p>
	 * Useful when paginating results. Returns a maximum of <code>end - start</code> instances. <code>start</code> and <code>end</code> are not primary keys, they are indexes in the result set. Thus, <code>0</code> refers to the first result in the set. Setting both <code>start</code> and <code>end</code> to <code>com.liferay.portal.kernel.dao.orm.QueryUtil#ALL_POS</code> will return the full result set. If <code>orderByComparator</code> is specified, then the query will include the given ORDER BY logic. If <code>orderByComparator</code> is absent, then the query will include the default ORDER BY logic from <code>com.liferay.portlet.asset.model.impl.AssetEntryModelImpl</code>.
	 * </p>
	 *
	 * @param pk the primary key of the asset entry
	 * @param start the lower bound of the range of asset entries
	 * @param end the upper bound of the range of asset entries (not inclusive)
	 * @return the range of asset tags associated with the asset entry
	 */
	public java.util.List<com.liferay.asset.kernel.model.AssetTag> getAssetTags(
		long pk, int start, int end);

	/**
	 * Returns an ordered range of all the asset tags associated with the asset entry.
	 *
	 * <p>
	 * Useful when paginating results. Returns a maximum of <code>end - start</code> instances. <code>start</code> and <code>end</code> are not primary keys, they are indexes in the result set. Thus, <code>0</code> refers to the first result in the set. Setting both <code>start</code> and <code>end</code> to <code>com.liferay.portal.kernel.dao.orm.QueryUtil#ALL_POS</code> will return the full result set. If <code>orderByComparator</code> is specified, then the query will include the given ORDER BY logic. If <code>orderByComparator</code> is absent, then the query will include the default ORDER BY logic from <code>com.liferay.portlet.asset.model.impl.AssetEntryModelImpl</code>.
	 * </p>
	 *
	 * @param pk the primary key of the asset entry
	 * @param start the lower bound of the range of asset entries
	 * @param end the upper bound of the range of asset entries (not inclusive)
	 * @param orderByComparator the comparator to order the results by (optionally <code>null</code>)
	 * @return the ordered range of asset tags associated with the asset entry
	 */
	public java.util.List<com.liferay.asset.kernel.model.AssetTag> getAssetTags(
		long pk, int start, int end,
		com.liferay.portal.kernel.util.OrderByComparator
			<com.liferay.asset.kernel.model.AssetTag> orderByComparator);

	/**
	 * Returns the number of asset tags associated with the asset entry.
	 *
	 * @param pk the primary key of the asset entry
	 * @return the number of asset tags associated with the asset entry
	 */
	public int getAssetTagsSize(long pk);

	/**
	 * Returns <code>true</code> if the asset tag is associated with the asset entry.
	 *
	 * @param pk the primary key of the asset entry
	 * @param assetTagPK the primary key of the asset tag
	 * @return <code>true</code> if the asset tag is associated with the asset entry; <code>false</code> otherwise
	 */
	public boolean containsAssetTag(long pk, long assetTagPK);

	/**
	 * Returns <code>true</code> if the asset entry has any asset tags associated with it.
	 *
	 * @param pk the primary key of the asset entry to check for associations with asset tags
	 * @return <code>true</code> if the asset entry has any asset tags associated with it; <code>false</code> otherwise
	 */
	public boolean containsAssetTags(long pk);

	/**
	 * Adds an association between the asset entry and the asset tag. Also notifies the appropriate model listeners and clears the mapping table finder cache.
	 *
	 * @param pk the primary key of the asset entry
	 * @param assetTagPK the primary key of the asset tag
	 * @return <code>true</code> if an association between the asset entry and the asset tag was added; <code>false</code> if they were already associated
	 */
	public boolean addAssetTag(long pk, long assetTagPK);

	/**
	 * Adds an association between the asset entry and the asset tag. Also notifies the appropriate model listeners and clears the mapping table finder cache.
	 *
	 * @param pk the primary key of the asset entry
	 * @param assetTag the asset tag
	 * @return <code>true</code> if an association between the asset entry and the asset tag was added; <code>false</code> if they were already associated
	 */
	public boolean addAssetTag(
		long pk, com.liferay.asset.kernel.model.AssetTag assetTag);

	/**
	 * Adds an association between the asset entry and the asset tags. Also notifies the appropriate model listeners and clears the mapping table finder cache.
	 *
	 * @param pk the primary key of the asset entry
	 * @param assetTagPKs the primary keys of the asset tags
	 * @return <code>true</code> if at least one association between the asset entry and the asset tags was added; <code>false</code> if they were all already associated
	 */
	public boolean addAssetTags(long pk, long[] assetTagPKs);

	/**
	 * Adds an association between the asset entry and the asset tags. Also notifies the appropriate model listeners and clears the mapping table finder cache.
	 *
	 * @param pk the primary key of the asset entry
	 * @param assetTags the asset tags
	 * @return <code>true</code> if at least one association between the asset entry and the asset tags was added; <code>false</code> if they were all already associated
	 */
	public boolean addAssetTags(
		long pk,
		java.util.List<com.liferay.asset.kernel.model.AssetTag> assetTags);

	/**
	 * Clears all associations between the asset entry and its asset tags. Also notifies the appropriate model listeners and clears the mapping table finder cache.
	 *
	 * @param pk the primary key of the asset entry to clear the associated asset tags from
	 */
	public void clearAssetTags(long pk);

	/**
	 * Removes the association between the asset entry and the asset tag. Also notifies the appropriate model listeners and clears the mapping table finder cache.
	 *
	 * @param pk the primary key of the asset entry
	 * @param assetTagPK the primary key of the asset tag
	 */
	public void removeAssetTag(long pk, long assetTagPK);

	/**
	 * Removes the association between the asset entry and the asset tag. Also notifies the appropriate model listeners and clears the mapping table finder cache.
	 *
	 * @param pk the primary key of the asset entry
	 * @param assetTag the asset tag
	 */
	public void removeAssetTag(
		long pk, com.liferay.asset.kernel.model.AssetTag assetTag);

	/**
	 * Removes the association between the asset entry and the asset tags. Also notifies the appropriate model listeners and clears the mapping table finder cache.
	 *
	 * @param pk the primary key of the asset entry
	 * @param assetTagPKs the primary keys of the asset tags
	 */
	public void removeAssetTags(long pk, long[] assetTagPKs);

	/**
	 * Removes the association between the asset entry and the asset tags. Also notifies the appropriate model listeners and clears the mapping table finder cache.
	 *
	 * @param pk the primary key of the asset entry
	 * @param assetTags the asset tags
	 */
	public void removeAssetTags(
		long pk,
		java.util.List<com.liferay.asset.kernel.model.AssetTag> assetTags);

	/**
	 * Sets the asset tags associated with the asset entry, removing and adding associations as necessary. Also notifies the appropriate model listeners and clears the mapping table finder cache.
	 *
	 * @param pk the primary key of the asset entry
	 * @param assetTagPKs the primary keys of the asset tags to be associated with the asset entry
	 */
	public void setAssetTags(long pk, long[] assetTagPKs);

	/**
	 * Sets the asset tags associated with the asset entry, removing and adding associations as necessary. Also notifies the appropriate model listeners and clears the mapping table finder cache.
	 *
	 * @param pk the primary key of the asset entry
	 * @param assetTags the asset tags to be associated with the asset entry
	 */
	public void setAssetTags(
		long pk,
		java.util.List<com.liferay.asset.kernel.model.AssetTag> assetTags);

	/**
	 * Returns the asset entry where groupId = &#63; and classUuid = &#63; or returns <code>null</code> if it could not be found. Uses the finder cache.
	 *
	 * @param groupId the group ID
	 * @param classUuid the class uuid
	 * @return the matching asset entry, or <code>null</code> if a matching asset entry could not be found
	 */
	public default AssetEntry fetchByG_CU(long groupId, String classUuid) {
		return fetchByG_CU(groupId, classUuid, true);
	}

	/**
	 * Returns the asset entry where classNameId = &#63; and classPK = &#63; or returns <code>null</code> if it could not be found. Uses the finder cache.
	 *
	 * @param classNameId the class name ID
	 * @param classPK the class pk
	 * @return the matching asset entry, or <code>null</code> if a matching asset entry could not be found
	 */
	public default AssetEntry fetchByC_C(long classNameId, long classPK) {
		return fetchByC_C(classNameId, classPK, true);
	}

	/**
	 * Returns all the asset entries where groupId = &#63;.
	 *
	 * @param groupId the group ID
	 * @return the matching asset entries
	 */
	public default java.util.List<AssetEntry> findByGroupId(long groupId) {
		return findByGroupId(
			groupId, com.liferay.portal.kernel.dao.orm.QueryUtil.ALL_POS,
			com.liferay.portal.kernel.dao.orm.QueryUtil.ALL_POS, null, true);
	}

	/**
	 * Returns a range of all the asset entries where groupId = &#63;.
	 *
	 * <p>
	 * Useful when paginating results. Returns a maximum of <code>end - start</code> instances. <code>start</code> and <code>end</code> are not primary keys, they are indexes in the result set. Thus, <code>0</code> refers to the first result in the set. Setting both <code>start</code> and <code>end</code> to <code>com.liferay.portal.kernel.dao.orm.QueryUtil#ALL_POS</code> will return the full result set. If <code>orderByComparator</code> is specified, then the query will include the given ORDER BY logic. If <code>orderByComparator</code> is absent, then the query will include the default ORDER BY logic from <code>com.liferay.portlet.asset.model.impl.AssetEntryModelImpl</code>.
	 * </p>
	 *
	 * @param groupId the group ID
	 * @param start the lower bound of the range of asset entries
	 * @param end the upper bound of the range of asset entries (not inclusive)
	 * @return the range of matching asset entries
	 */
	public default java.util.List<AssetEntry> findByGroupId(
		long groupId, int start, int end) {

		return findByGroupId(groupId, start, end, null, true);
	}

	/**
	 * Returns an ordered range of all the asset entries where groupId = &#63;.
	 *
	 * <p>
	 * Useful when paginating results. Returns a maximum of <code>end - start</code> instances. <code>start</code> and <code>end</code> are not primary keys, they are indexes in the result set. Thus, <code>0</code> refers to the first result in the set. Setting both <code>start</code> and <code>end</code> to <code>com.liferay.portal.kernel.dao.orm.QueryUtil#ALL_POS</code> will return the full result set. If <code>orderByComparator</code> is specified, then the query will include the given ORDER BY logic. If <code>orderByComparator</code> is absent, then the query will include the default ORDER BY logic from <code>com.liferay.portlet.asset.model.impl.AssetEntryModelImpl</code>.
	 * </p>
	 *
	 * @param groupId the group ID
	 * @param start the lower bound of the range of asset entries
	 * @param end the upper bound of the range of asset entries (not inclusive)
	 * @param orderByComparator the comparator to order the results by (optionally <code>null</code>)
	 * @return the ordered range of matching asset entries
	 */
	public default java.util.List<AssetEntry> findByGroupId(
		long groupId, int start, int end,
		com.liferay.portal.kernel.util.OrderByComparator<AssetEntry>
			orderByComparator) {

		return findByGroupId(groupId, start, end, orderByComparator, true);
	}

	/**
	 * Returns all the asset entries where companyId = &#63;.
	 *
	 * @param companyId the company ID
	 * @return the matching asset entries
	 */
	public default java.util.List<AssetEntry> findByCompanyId(long companyId) {
		return findByCompanyId(
			companyId, com.liferay.portal.kernel.dao.orm.QueryUtil.ALL_POS,
			com.liferay.portal.kernel.dao.orm.QueryUtil.ALL_POS, null, true);
	}

	/**
	 * Returns a range of all the asset entries where companyId = &#63;.
	 *
	 * <p>
	 * Useful when paginating results. Returns a maximum of <code>end - start</code> instances. <code>start</code> and <code>end</code> are not primary keys, they are indexes in the result set. Thus, <code>0</code> refers to the first result in the set. Setting both <code>start</code> and <code>end</code> to <code>com.liferay.portal.kernel.dao.orm.QueryUtil#ALL_POS</code> will return the full result set. If <code>orderByComparator</code> is specified, then the query will include the given ORDER BY logic. If <code>orderByComparator</code> is absent, then the query will include the default ORDER BY logic from <code>com.liferay.portlet.asset.model.impl.AssetEntryModelImpl</code>.
	 * </p>
	 *
	 * @param companyId the company ID
	 * @param start the lower bound of the range of asset entries
	 * @param end the upper bound of the range of asset entries (not inclusive)
	 * @return the range of matching asset entries
	 */
	public default java.util.List<AssetEntry> findByCompanyId(
		long companyId, int start, int end) {

		return findByCompanyId(companyId, start, end, null, true);
	}

	/**
	 * Returns an ordered range of all the asset entries where companyId = &#63;.
	 *
	 * <p>
	 * Useful when paginating results. Returns a maximum of <code>end - start</code> instances. <code>start</code> and <code>end</code> are not primary keys, they are indexes in the result set. Thus, <code>0</code> refers to the first result in the set. Setting both <code>start</code> and <code>end</code> to <code>com.liferay.portal.kernel.dao.orm.QueryUtil#ALL_POS</code> will return the full result set. If <code>orderByComparator</code> is specified, then the query will include the given ORDER BY logic. If <code>orderByComparator</code> is absent, then the query will include the default ORDER BY logic from <code>com.liferay.portlet.asset.model.impl.AssetEntryModelImpl</code>.
	 * </p>
	 *
	 * @param companyId the company ID
	 * @param start the lower bound of the range of asset entries
	 * @param end the upper bound of the range of asset entries (not inclusive)
	 * @param orderByComparator the comparator to order the results by (optionally <code>null</code>)
	 * @return the ordered range of matching asset entries
	 */
	public default java.util.List<AssetEntry> findByCompanyId(
		long companyId, int start, int end,
		com.liferay.portal.kernel.util.OrderByComparator<AssetEntry>
			orderByComparator) {

		return findByCompanyId(companyId, start, end, orderByComparator, true);
	}

	/**
	 * Returns all the asset entries where classUuid = &#63;.
	 *
	 * @param classUuid the class uuid
	 * @return the matching asset entries
	 */
	public default java.util.List<AssetEntry> findByClassUuid(
		String classUuid) {

		return findByClassUuid(
			classUuid, com.liferay.portal.kernel.dao.orm.QueryUtil.ALL_POS,
			com.liferay.portal.kernel.dao.orm.QueryUtil.ALL_POS, null, true);
	}

	/**
	 * Returns a range of all the asset entries where classUuid = &#63;.
	 *
	 * <p>
	 * Useful when paginating results. Returns a maximum of <code>end - start</code> instances. <code>start</code> and <code>end</code> are not primary keys, they are indexes in the result set. Thus, <code>0</code> refers to the first result in the set. Setting both <code>start</code> and <code>end</code> to <code>com.liferay.portal.kernel.dao.orm.QueryUtil#ALL_POS</code> will return the full result set. If <code>orderByComparator</code> is specified, then the query will include the given ORDER BY logic. If <code>orderByComparator</code> is absent, then the query will include the default ORDER BY logic from <code>com.liferay.portlet.asset.model.impl.AssetEntryModelImpl</code>.
	 * </p>
	 *
	 * @param classUuid the class uuid
	 * @param start the lower bound of the range of asset entries
	 * @param end the upper bound of the range of asset entries (not inclusive)
	 * @return the range of matching asset entries
	 */
	public default java.util.List<AssetEntry> findByClassUuid(
		String classUuid, int start, int end) {

		return findByClassUuid(classUuid, start, end, null, true);
	}

	/**
	 * Returns an ordered range of all the asset entries where classUuid = &#63;.
	 *
	 * <p>
	 * Useful when paginating results. Returns a maximum of <code>end - start</code> instances. <code>start</code> and <code>end</code> are not primary keys, they are indexes in the result set. Thus, <code>0</code> refers to the first result in the set. Setting both <code>start</code> and <code>end</code> to <code>com.liferay.portal.kernel.dao.orm.QueryUtil#ALL_POS</code> will return the full result set. If <code>orderByComparator</code> is specified, then the query will include the given ORDER BY logic. If <code>orderByComparator</code> is absent, then the query will include the default ORDER BY logic from <code>com.liferay.portlet.asset.model.impl.AssetEntryModelImpl</code>.
	 * </p>
	 *
	 * @param classUuid the class uuid
	 * @param start the lower bound of the range of asset entries
	 * @param end the upper bound of the range of asset entries (not inclusive)
	 * @param orderByComparator the comparator to order the results by (optionally <code>null</code>)
	 * @return the ordered range of matching asset entries
	 */
	public default java.util.List<AssetEntry> findByClassUuid(
		String classUuid, int start, int end,
		com.liferay.portal.kernel.util.OrderByComparator<AssetEntry>
			orderByComparator) {

		return findByClassUuid(classUuid, start, end, orderByComparator, true);
	}

	/**
	 * Returns all the asset entries where groupId = &#63; and classNameId = &#63; and visible = &#63;.
	 *
	 * @param groupId the group ID
	 * @param classNameId the class name ID
	 * @param visible the visible
	 * @return the matching asset entries
	 */
	public default java.util.List<AssetEntry> findByG_C_V(
		long groupId, long classNameId, boolean visible) {

		return findByG_C_V(
			groupId, classNameId, visible,
			com.liferay.portal.kernel.dao.orm.QueryUtil.ALL_POS,
			com.liferay.portal.kernel.dao.orm.QueryUtil.ALL_POS, null, true);
	}

	/**
	 * Returns a range of all the asset entries where groupId = &#63; and classNameId = &#63; and visible = &#63;.
	 *
	 * <p>
	 * Useful when paginating results. Returns a maximum of <code>end - start</code> instances. <code>start</code> and <code>end</code> are not primary keys, they are indexes in the result set. Thus, <code>0</code> refers to the first result in the set. Setting both <code>start</code> and <code>end</code> to <code>com.liferay.portal.kernel.dao.orm.QueryUtil#ALL_POS</code> will return the full result set. If <code>orderByComparator</code> is specified, then the query will include the given ORDER BY logic. If <code>orderByComparator</code> is absent, then the query will include the default ORDER BY logic from <code>com.liferay.portlet.asset.model.impl.AssetEntryModelImpl</code>.
	 * </p>
	 *
	 * @param groupId the group ID
	 * @param classNameId the class name ID
	 * @param visible the visible
	 * @param start the lower bound of the range of asset entries
	 * @param end the upper bound of the range of asset entries (not inclusive)
	 * @return the range of matching asset entries
	 */
	public default java.util.List<AssetEntry> findByG_C_V(
		long groupId, long classNameId, boolean visible, int start, int end) {

		return findByG_C_V(
			groupId, classNameId, visible, start, end, null, true);
	}

	/**
	 * Returns an ordered range of all the asset entries where groupId = &#63; and classNameId = &#63; and visible = &#63;.
	 *
	 * <p>
	 * Useful when paginating results. Returns a maximum of <code>end - start</code> instances. <code>start</code> and <code>end</code> are not primary keys, they are indexes in the result set. Thus, <code>0</code> refers to the first result in the set. Setting both <code>start</code> and <code>end</code> to <code>com.liferay.portal.kernel.dao.orm.QueryUtil#ALL_POS</code> will return the full result set. If <code>orderByComparator</code> is specified, then the query will include the given ORDER BY logic. If <code>orderByComparator</code> is absent, then the query will include the default ORDER BY logic from <code>com.liferay.portlet.asset.model.impl.AssetEntryModelImpl</code>.
	 * </p>
	 *
	 * @param groupId the group ID
	 * @param classNameId the class name ID
	 * @param visible the visible
	 * @param start the lower bound of the range of asset entries
	 * @param end the upper bound of the range of asset entries (not inclusive)
	 * @param orderByComparator the comparator to order the results by (optionally <code>null</code>)
	 * @return the ordered range of matching asset entries
	 */
	public default java.util.List<AssetEntry> findByG_C_V(
		long groupId, long classNameId, boolean visible, int start, int end,
		com.liferay.portal.kernel.util.OrderByComparator<AssetEntry>
			orderByComparator) {

		return findByG_C_V(
			groupId, classNameId, visible, start, end, orderByComparator, true);
	}

}
// LIFERAY-SERVICE-BUILDER-HASH:882501217