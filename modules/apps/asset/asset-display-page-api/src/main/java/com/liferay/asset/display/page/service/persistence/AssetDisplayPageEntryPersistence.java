/**
 * SPDX-FileCopyrightText: (c) 2000 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

package com.liferay.asset.display.page.service.persistence;

import com.liferay.asset.display.page.exception.NoSuchDisplayPageEntryException;
import com.liferay.asset.display.page.model.AssetDisplayPageEntry;
import com.liferay.portal.kernel.service.persistence.BasePersistence;
import com.liferay.portal.kernel.service.persistence.change.tracking.CTPersistence;

import org.osgi.annotation.versioning.ProviderType;

/**
 * The persistence interface for the asset display page entry service.
 *
 * <p>
 * Caching information and settings can be found in <code>portal.properties</code>
 * </p>
 *
 * @author Brian Wing Shun Chan
 * @see AssetDisplayPageEntryUtil
 * @generated
 */
@ProviderType
public interface AssetDisplayPageEntryPersistence
	extends BasePersistence<AssetDisplayPageEntry>,
			CTPersistence<AssetDisplayPageEntry> {

	/*
	 * NOTE FOR DEVELOPERS:
	 *
	 * Never modify or reference this interface directly. Always use {@link AssetDisplayPageEntryUtil} to access the asset display page entry persistence. Modify <code>service.xml</code> and rerun ServiceBuilder to regenerate this interface.
	 */

	/**
	 * Returns an ordered range of all the asset display page entries where uuid = &#63;.
	 *
	 * <p>
	 * Useful when paginating results. Returns a maximum of <code>end - start</code> instances. <code>start</code> and <code>end</code> are not primary keys, they are indexes in the result set. Thus, <code>0</code> refers to the first result in the set. Setting both <code>start</code> and <code>end</code> to <code>com.liferay.portal.kernel.dao.orm.QueryUtil#ALL_POS</code> will return the full result set. If <code>orderByComparator</code> is specified, then the query will include the given ORDER BY logic. If <code>orderByComparator</code> is absent, then the query will include the default ORDER BY logic from <code>com.liferay.asset.display.page.model.impl.AssetDisplayPageEntryModelImpl</code>.
	 * </p>
	 *
	 * @param uuid the uuid
	 * @param start the lower bound of the range of asset display page entries
	 * @param end the upper bound of the range of asset display page entries (not inclusive)
	 * @param orderByComparator the comparator to order the results by (optionally <code>null</code>)
	 * @param useFinderCache whether to use the finder cache
	 * @return the ordered range of matching asset display page entries
	 */
	public java.util.List<AssetDisplayPageEntry> findByUuid(
		String uuid, int start, int end,
		com.liferay.portal.kernel.util.OrderByComparator<AssetDisplayPageEntry>
			orderByComparator,
		boolean useFinderCache);

	/**
	 * Returns the first asset display page entry in the ordered set where uuid = &#63;.
	 *
	 * @param uuid the uuid
	 * @param orderByComparator the comparator to order the set by (optionally <code>null</code>)
	 * @return the first matching asset display page entry
	 * @throws NoSuchDisplayPageEntryException if a matching asset display page entry could not be found
	 */
	public AssetDisplayPageEntry findByUuid_First(
			String uuid,
			com.liferay.portal.kernel.util.OrderByComparator
				<AssetDisplayPageEntry> orderByComparator)
		throws NoSuchDisplayPageEntryException;

	/**
	 * Returns the first asset display page entry in the ordered set where uuid = &#63;.
	 *
	 * @param uuid the uuid
	 * @param orderByComparator the comparator to order the set by (optionally <code>null</code>)
	 * @return the first matching asset display page entry, or <code>null</code> if a matching asset display page entry could not be found
	 */
	public AssetDisplayPageEntry fetchByUuid_First(
		String uuid,
		com.liferay.portal.kernel.util.OrderByComparator<AssetDisplayPageEntry>
			orderByComparator);

	/**
	 * Removes all the asset display page entries where uuid = &#63; from the database.
	 *
	 * @param uuid the uuid
	 */
	public void removeByUuid(String uuid);

	/**
	 * Returns the number of asset display page entries where uuid = &#63;.
	 *
	 * @param uuid the uuid
	 * @return the number of matching asset display page entries
	 */
	public int countByUuid(String uuid);

	/**
	 * Returns the asset display page entry where uuid = &#63; and groupId = &#63; or throws a <code>NoSuchDisplayPageEntryException</code> if it could not be found.
	 *
	 * @param uuid the uuid
	 * @param groupId the group ID
	 * @return the matching asset display page entry
	 * @throws NoSuchDisplayPageEntryException if a matching asset display page entry could not be found
	 */
	public AssetDisplayPageEntry findByUUID_G(String uuid, long groupId)
		throws NoSuchDisplayPageEntryException;

	/**
	 * Returns the asset display page entry where uuid = &#63; and groupId = &#63; or returns <code>null</code> if it could not be found, optionally using the finder cache.
	 *
	 * @param uuid the uuid
	 * @param groupId the group ID
	 * @param useFinderCache whether to use the finder cache
	 * @return the matching asset display page entry, or <code>null</code> if a matching asset display page entry could not be found
	 */
	public AssetDisplayPageEntry fetchByUUID_G(
		String uuid, long groupId, boolean useFinderCache);

	/**
	 * Removes the asset display page entry where uuid = &#63; and groupId = &#63; from the database.
	 *
	 * @param uuid the uuid
	 * @param groupId the group ID
	 * @return the asset display page entry that was removed
	 */
	public AssetDisplayPageEntry removeByUUID_G(String uuid, long groupId)
		throws NoSuchDisplayPageEntryException;

	/**
	 * Returns the number of asset display page entries where uuid = &#63; and groupId = &#63;.
	 *
	 * @param uuid the uuid
	 * @param groupId the group ID
	 * @return the number of matching asset display page entries
	 */
	public int countByUUID_G(String uuid, long groupId);

	/**
	 * Returns an ordered range of all the asset display page entries where uuid = &#63; and companyId = &#63;.
	 *
	 * <p>
	 * Useful when paginating results. Returns a maximum of <code>end - start</code> instances. <code>start</code> and <code>end</code> are not primary keys, they are indexes in the result set. Thus, <code>0</code> refers to the first result in the set. Setting both <code>start</code> and <code>end</code> to <code>com.liferay.portal.kernel.dao.orm.QueryUtil#ALL_POS</code> will return the full result set. If <code>orderByComparator</code> is specified, then the query will include the given ORDER BY logic. If <code>orderByComparator</code> is absent, then the query will include the default ORDER BY logic from <code>com.liferay.asset.display.page.model.impl.AssetDisplayPageEntryModelImpl</code>.
	 * </p>
	 *
	 * @param uuid the uuid
	 * @param companyId the company ID
	 * @param start the lower bound of the range of asset display page entries
	 * @param end the upper bound of the range of asset display page entries (not inclusive)
	 * @param orderByComparator the comparator to order the results by (optionally <code>null</code>)
	 * @param useFinderCache whether to use the finder cache
	 * @return the ordered range of matching asset display page entries
	 */
	public java.util.List<AssetDisplayPageEntry> findByUuid_C(
		String uuid, long companyId, int start, int end,
		com.liferay.portal.kernel.util.OrderByComparator<AssetDisplayPageEntry>
			orderByComparator,
		boolean useFinderCache);

	/**
	 * Returns the first asset display page entry in the ordered set where uuid = &#63; and companyId = &#63;.
	 *
	 * @param uuid the uuid
	 * @param companyId the company ID
	 * @param orderByComparator the comparator to order the set by (optionally <code>null</code>)
	 * @return the first matching asset display page entry
	 * @throws NoSuchDisplayPageEntryException if a matching asset display page entry could not be found
	 */
	public AssetDisplayPageEntry findByUuid_C_First(
			String uuid, long companyId,
			com.liferay.portal.kernel.util.OrderByComparator
				<AssetDisplayPageEntry> orderByComparator)
		throws NoSuchDisplayPageEntryException;

	/**
	 * Returns the first asset display page entry in the ordered set where uuid = &#63; and companyId = &#63;.
	 *
	 * @param uuid the uuid
	 * @param companyId the company ID
	 * @param orderByComparator the comparator to order the set by (optionally <code>null</code>)
	 * @return the first matching asset display page entry, or <code>null</code> if a matching asset display page entry could not be found
	 */
	public AssetDisplayPageEntry fetchByUuid_C_First(
		String uuid, long companyId,
		com.liferay.portal.kernel.util.OrderByComparator<AssetDisplayPageEntry>
			orderByComparator);

	/**
	 * Removes all the asset display page entries where uuid = &#63; and companyId = &#63; from the database.
	 *
	 * @param uuid the uuid
	 * @param companyId the company ID
	 */
	public void removeByUuid_C(String uuid, long companyId);

	/**
	 * Returns the number of asset display page entries where uuid = &#63; and companyId = &#63;.
	 *
	 * @param uuid the uuid
	 * @param companyId the company ID
	 * @return the number of matching asset display page entries
	 */
	public int countByUuid_C(String uuid, long companyId);

	/**
	 * Returns an ordered range of all the asset display page entries where layoutPageTemplateEntryId = &#63;.
	 *
	 * <p>
	 * Useful when paginating results. Returns a maximum of <code>end - start</code> instances. <code>start</code> and <code>end</code> are not primary keys, they are indexes in the result set. Thus, <code>0</code> refers to the first result in the set. Setting both <code>start</code> and <code>end</code> to <code>com.liferay.portal.kernel.dao.orm.QueryUtil#ALL_POS</code> will return the full result set. If <code>orderByComparator</code> is specified, then the query will include the given ORDER BY logic. If <code>orderByComparator</code> is absent, then the query will include the default ORDER BY logic from <code>com.liferay.asset.display.page.model.impl.AssetDisplayPageEntryModelImpl</code>.
	 * </p>
	 *
	 * @param layoutPageTemplateEntryId the layout page template entry ID
	 * @param start the lower bound of the range of asset display page entries
	 * @param end the upper bound of the range of asset display page entries (not inclusive)
	 * @param orderByComparator the comparator to order the results by (optionally <code>null</code>)
	 * @param useFinderCache whether to use the finder cache
	 * @return the ordered range of matching asset display page entries
	 */
	public java.util.List<AssetDisplayPageEntry>
		findByLayoutPageTemplateEntryId(
			long layoutPageTemplateEntryId, int start, int end,
			com.liferay.portal.kernel.util.OrderByComparator
				<AssetDisplayPageEntry> orderByComparator,
			boolean useFinderCache);

	/**
	 * Returns the first asset display page entry in the ordered set where layoutPageTemplateEntryId = &#63;.
	 *
	 * @param layoutPageTemplateEntryId the layout page template entry ID
	 * @param orderByComparator the comparator to order the set by (optionally <code>null</code>)
	 * @return the first matching asset display page entry
	 * @throws NoSuchDisplayPageEntryException if a matching asset display page entry could not be found
	 */
	public AssetDisplayPageEntry findByLayoutPageTemplateEntryId_First(
			long layoutPageTemplateEntryId,
			com.liferay.portal.kernel.util.OrderByComparator
				<AssetDisplayPageEntry> orderByComparator)
		throws NoSuchDisplayPageEntryException;

	/**
	 * Returns the first asset display page entry in the ordered set where layoutPageTemplateEntryId = &#63;.
	 *
	 * @param layoutPageTemplateEntryId the layout page template entry ID
	 * @param orderByComparator the comparator to order the set by (optionally <code>null</code>)
	 * @return the first matching asset display page entry, or <code>null</code> if a matching asset display page entry could not be found
	 */
	public AssetDisplayPageEntry fetchByLayoutPageTemplateEntryId_First(
		long layoutPageTemplateEntryId,
		com.liferay.portal.kernel.util.OrderByComparator<AssetDisplayPageEntry>
			orderByComparator);

	/**
	 * Removes all the asset display page entries where layoutPageTemplateEntryId = &#63; from the database.
	 *
	 * @param layoutPageTemplateEntryId the layout page template entry ID
	 */
	public void removeByLayoutPageTemplateEntryId(
		long layoutPageTemplateEntryId);

	/**
	 * Returns the number of asset display page entries where layoutPageTemplateEntryId = &#63;.
	 *
	 * @param layoutPageTemplateEntryId the layout page template entry ID
	 * @return the number of matching asset display page entries
	 */
	public int countByLayoutPageTemplateEntryId(long layoutPageTemplateEntryId);

	/**
	 * Returns an ordered range of all the asset display page entries where groupId = &#63; and classNameId = &#63;.
	 *
	 * <p>
	 * Useful when paginating results. Returns a maximum of <code>end - start</code> instances. <code>start</code> and <code>end</code> are not primary keys, they are indexes in the result set. Thus, <code>0</code> refers to the first result in the set. Setting both <code>start</code> and <code>end</code> to <code>com.liferay.portal.kernel.dao.orm.QueryUtil#ALL_POS</code> will return the full result set. If <code>orderByComparator</code> is specified, then the query will include the given ORDER BY logic. If <code>orderByComparator</code> is absent, then the query will include the default ORDER BY logic from <code>com.liferay.asset.display.page.model.impl.AssetDisplayPageEntryModelImpl</code>.
	 * </p>
	 *
	 * @param groupId the group ID
	 * @param classNameId the class name ID
	 * @param start the lower bound of the range of asset display page entries
	 * @param end the upper bound of the range of asset display page entries (not inclusive)
	 * @param orderByComparator the comparator to order the results by (optionally <code>null</code>)
	 * @param useFinderCache whether to use the finder cache
	 * @return the ordered range of matching asset display page entries
	 */
	public java.util.List<AssetDisplayPageEntry> findByG_CN(
		long groupId, long classNameId, int start, int end,
		com.liferay.portal.kernel.util.OrderByComparator<AssetDisplayPageEntry>
			orderByComparator,
		boolean useFinderCache);

	/**
	 * Returns the first asset display page entry in the ordered set where groupId = &#63; and classNameId = &#63;.
	 *
	 * @param groupId the group ID
	 * @param classNameId the class name ID
	 * @param orderByComparator the comparator to order the set by (optionally <code>null</code>)
	 * @return the first matching asset display page entry
	 * @throws NoSuchDisplayPageEntryException if a matching asset display page entry could not be found
	 */
	public AssetDisplayPageEntry findByG_CN_First(
			long groupId, long classNameId,
			com.liferay.portal.kernel.util.OrderByComparator
				<AssetDisplayPageEntry> orderByComparator)
		throws NoSuchDisplayPageEntryException;

	/**
	 * Returns the first asset display page entry in the ordered set where groupId = &#63; and classNameId = &#63;.
	 *
	 * @param groupId the group ID
	 * @param classNameId the class name ID
	 * @param orderByComparator the comparator to order the set by (optionally <code>null</code>)
	 * @return the first matching asset display page entry, or <code>null</code> if a matching asset display page entry could not be found
	 */
	public AssetDisplayPageEntry fetchByG_CN_First(
		long groupId, long classNameId,
		com.liferay.portal.kernel.util.OrderByComparator<AssetDisplayPageEntry>
			orderByComparator);

	/**
	 * Removes all the asset display page entries where groupId = &#63; and classNameId = &#63; from the database.
	 *
	 * @param groupId the group ID
	 * @param classNameId the class name ID
	 */
	public void removeByG_CN(long groupId, long classNameId);

	/**
	 * Returns the number of asset display page entries where groupId = &#63; and classNameId = &#63;.
	 *
	 * @param groupId the group ID
	 * @param classNameId the class name ID
	 * @return the number of matching asset display page entries
	 */
	public int countByG_CN(long groupId, long classNameId);

	/**
	 * Returns the asset display page entry where groupId = &#63; and classNameId = &#63; and classPK = &#63; or throws a <code>NoSuchDisplayPageEntryException</code> if it could not be found.
	 *
	 * @param groupId the group ID
	 * @param classNameId the class name ID
	 * @param classPK the class pk
	 * @return the matching asset display page entry
	 * @throws NoSuchDisplayPageEntryException if a matching asset display page entry could not be found
	 */
	public AssetDisplayPageEntry findByG_C_C(
			long groupId, long classNameId, long classPK)
		throws NoSuchDisplayPageEntryException;

	/**
	 * Returns the asset display page entry where groupId = &#63; and classNameId = &#63; and classPK = &#63; or returns <code>null</code> if it could not be found, optionally using the finder cache.
	 *
	 * @param groupId the group ID
	 * @param classNameId the class name ID
	 * @param classPK the class pk
	 * @param useFinderCache whether to use the finder cache
	 * @return the matching asset display page entry, or <code>null</code> if a matching asset display page entry could not be found
	 */
	public AssetDisplayPageEntry fetchByG_C_C(
		long groupId, long classNameId, long classPK, boolean useFinderCache);

	/**
	 * Removes the asset display page entry where groupId = &#63; and classNameId = &#63; and classPK = &#63; from the database.
	 *
	 * @param groupId the group ID
	 * @param classNameId the class name ID
	 * @param classPK the class pk
	 * @return the asset display page entry that was removed
	 */
	public AssetDisplayPageEntry removeByG_C_C(
			long groupId, long classNameId, long classPK)
		throws NoSuchDisplayPageEntryException;

	/**
	 * Returns the number of asset display page entries where groupId = &#63; and classNameId = &#63; and classPK = &#63;.
	 *
	 * @param groupId the group ID
	 * @param classNameId the class name ID
	 * @param classPK the class pk
	 * @return the number of matching asset display page entries
	 */
	public int countByG_C_C(long groupId, long classNameId, long classPK);

	/**
	 * Creates a new asset display page entry with the primary key. Does not add the asset display page entry to the database.
	 *
	 * @param assetDisplayPageEntryId the primary key for the new asset display page entry
	 * @return the new asset display page entry
	 */
	public AssetDisplayPageEntry create(long assetDisplayPageEntryId);

	/**
	 * Removes the asset display page entry with the primary key from the database. Also notifies the appropriate model listeners.
	 *
	 * @param assetDisplayPageEntryId the primary key of the asset display page entry
	 * @return the asset display page entry that was removed
	 * @throws NoSuchDisplayPageEntryException if a asset display page entry with the primary key could not be found
	 */
	public AssetDisplayPageEntry remove(long assetDisplayPageEntryId)
		throws NoSuchDisplayPageEntryException;

	public AssetDisplayPageEntry updateImpl(
		AssetDisplayPageEntry assetDisplayPageEntry);

	/**
	 * Returns the asset display page entry with the primary key or throws a <code>NoSuchDisplayPageEntryException</code> if it could not be found.
	 *
	 * @param assetDisplayPageEntryId the primary key of the asset display page entry
	 * @return the asset display page entry
	 * @throws NoSuchDisplayPageEntryException if a asset display page entry with the primary key could not be found
	 */
	public AssetDisplayPageEntry findByPrimaryKey(long assetDisplayPageEntryId)
		throws NoSuchDisplayPageEntryException;

	/**
	 * Returns the asset display page entry with the primary key or returns <code>null</code> if it could not be found.
	 *
	 * @param assetDisplayPageEntryId the primary key of the asset display page entry
	 * @return the asset display page entry, or <code>null</code> if a asset display page entry with the primary key could not be found
	 */
	public AssetDisplayPageEntry fetchByPrimaryKey(
		long assetDisplayPageEntryId);

	/**
	 * Returns the asset display page entry where uuid = &#63; and groupId = &#63; or returns <code>null</code> if it could not be found. Uses the finder cache.
	 *
	 * @param uuid the uuid
	 * @param groupId the group ID
	 * @return the matching asset display page entry, or <code>null</code> if a matching asset display page entry could not be found
	 */
	public default AssetDisplayPageEntry fetchByUUID_G(
		String uuid, long groupId) {

		return fetchByUUID_G(uuid, groupId, true);
	}

	/**
	 * Returns the asset display page entry where groupId = &#63; and classNameId = &#63; and classPK = &#63; or returns <code>null</code> if it could not be found. Uses the finder cache.
	 *
	 * @param groupId the group ID
	 * @param classNameId the class name ID
	 * @param classPK the class pk
	 * @return the matching asset display page entry, or <code>null</code> if a matching asset display page entry could not be found
	 */
	public default AssetDisplayPageEntry fetchByG_C_C(
		long groupId, long classNameId, long classPK) {

		return fetchByG_C_C(groupId, classNameId, classPK, true);
	}

	/**
	 * Returns all the asset display page entries where uuid = &#63;.
	 *
	 * @param uuid the uuid
	 * @return the matching asset display page entries
	 */
	public default java.util.List<AssetDisplayPageEntry> findByUuid(
		String uuid) {

		return findByUuid(
			uuid, com.liferay.portal.kernel.dao.orm.QueryUtil.ALL_POS,
			com.liferay.portal.kernel.dao.orm.QueryUtil.ALL_POS, null, true);
	}

	/**
	 * Returns a range of all the asset display page entries where uuid = &#63;.
	 *
	 * <p>
	 * Useful when paginating results. Returns a maximum of <code>end - start</code> instances. <code>start</code> and <code>end</code> are not primary keys, they are indexes in the result set. Thus, <code>0</code> refers to the first result in the set. Setting both <code>start</code> and <code>end</code> to <code>com.liferay.portal.kernel.dao.orm.QueryUtil#ALL_POS</code> will return the full result set. If <code>orderByComparator</code> is specified, then the query will include the given ORDER BY logic. If <code>orderByComparator</code> is absent, then the query will include the default ORDER BY logic from <code>com.liferay.asset.display.page.model.impl.AssetDisplayPageEntryModelImpl</code>.
	 * </p>
	 *
	 * @param uuid the uuid
	 * @param start the lower bound of the range of asset display page entries
	 * @param end the upper bound of the range of asset display page entries (not inclusive)
	 * @return the range of matching asset display page entries
	 */
	public default java.util.List<AssetDisplayPageEntry> findByUuid(
		String uuid, int start, int end) {

		return findByUuid(uuid, start, end, null, true);
	}

	/**
	 * Returns an ordered range of all the asset display page entries where uuid = &#63;.
	 *
	 * <p>
	 * Useful when paginating results. Returns a maximum of <code>end - start</code> instances. <code>start</code> and <code>end</code> are not primary keys, they are indexes in the result set. Thus, <code>0</code> refers to the first result in the set. Setting both <code>start</code> and <code>end</code> to <code>com.liferay.portal.kernel.dao.orm.QueryUtil#ALL_POS</code> will return the full result set. If <code>orderByComparator</code> is specified, then the query will include the given ORDER BY logic. If <code>orderByComparator</code> is absent, then the query will include the default ORDER BY logic from <code>com.liferay.asset.display.page.model.impl.AssetDisplayPageEntryModelImpl</code>.
	 * </p>
	 *
	 * @param uuid the uuid
	 * @param start the lower bound of the range of asset display page entries
	 * @param end the upper bound of the range of asset display page entries (not inclusive)
	 * @param orderByComparator the comparator to order the results by (optionally <code>null</code>)
	 * @return the ordered range of matching asset display page entries
	 */
	public default java.util.List<AssetDisplayPageEntry> findByUuid(
		String uuid, int start, int end,
		com.liferay.portal.kernel.util.OrderByComparator<AssetDisplayPageEntry>
			orderByComparator) {

		return findByUuid(uuid, start, end, orderByComparator, true);
	}

	/**
	 * Returns all the asset display page entries where uuid = &#63; and companyId = &#63;.
	 *
	 * @param uuid the uuid
	 * @param companyId the company ID
	 * @return the matching asset display page entries
	 */
	public default java.util.List<AssetDisplayPageEntry> findByUuid_C(
		String uuid, long companyId) {

		return findByUuid_C(
			uuid, companyId,
			com.liferay.portal.kernel.dao.orm.QueryUtil.ALL_POS,
			com.liferay.portal.kernel.dao.orm.QueryUtil.ALL_POS, null, true);
	}

	/**
	 * Returns a range of all the asset display page entries where uuid = &#63; and companyId = &#63;.
	 *
	 * <p>
	 * Useful when paginating results. Returns a maximum of <code>end - start</code> instances. <code>start</code> and <code>end</code> are not primary keys, they are indexes in the result set. Thus, <code>0</code> refers to the first result in the set. Setting both <code>start</code> and <code>end</code> to <code>com.liferay.portal.kernel.dao.orm.QueryUtil#ALL_POS</code> will return the full result set. If <code>orderByComparator</code> is specified, then the query will include the given ORDER BY logic. If <code>orderByComparator</code> is absent, then the query will include the default ORDER BY logic from <code>com.liferay.asset.display.page.model.impl.AssetDisplayPageEntryModelImpl</code>.
	 * </p>
	 *
	 * @param uuid the uuid
	 * @param companyId the company ID
	 * @param start the lower bound of the range of asset display page entries
	 * @param end the upper bound of the range of asset display page entries (not inclusive)
	 * @return the range of matching asset display page entries
	 */
	public default java.util.List<AssetDisplayPageEntry> findByUuid_C(
		String uuid, long companyId, int start, int end) {

		return findByUuid_C(uuid, companyId, start, end, null, true);
	}

	/**
	 * Returns an ordered range of all the asset display page entries where uuid = &#63; and companyId = &#63;.
	 *
	 * <p>
	 * Useful when paginating results. Returns a maximum of <code>end - start</code> instances. <code>start</code> and <code>end</code> are not primary keys, they are indexes in the result set. Thus, <code>0</code> refers to the first result in the set. Setting both <code>start</code> and <code>end</code> to <code>com.liferay.portal.kernel.dao.orm.QueryUtil#ALL_POS</code> will return the full result set. If <code>orderByComparator</code> is specified, then the query will include the given ORDER BY logic. If <code>orderByComparator</code> is absent, then the query will include the default ORDER BY logic from <code>com.liferay.asset.display.page.model.impl.AssetDisplayPageEntryModelImpl</code>.
	 * </p>
	 *
	 * @param uuid the uuid
	 * @param companyId the company ID
	 * @param start the lower bound of the range of asset display page entries
	 * @param end the upper bound of the range of asset display page entries (not inclusive)
	 * @param orderByComparator the comparator to order the results by (optionally <code>null</code>)
	 * @return the ordered range of matching asset display page entries
	 */
	public default java.util.List<AssetDisplayPageEntry> findByUuid_C(
		String uuid, long companyId, int start, int end,
		com.liferay.portal.kernel.util.OrderByComparator<AssetDisplayPageEntry>
			orderByComparator) {

		return findByUuid_C(
			uuid, companyId, start, end, orderByComparator, true);
	}

	/**
	 * Returns all the asset display page entries where layoutPageTemplateEntryId = &#63;.
	 *
	 * @param layoutPageTemplateEntryId the layout page template entry ID
	 * @return the matching asset display page entries
	 */
	public default java.util.List<AssetDisplayPageEntry>
		findByLayoutPageTemplateEntryId(long layoutPageTemplateEntryId) {

		return findByLayoutPageTemplateEntryId(
			layoutPageTemplateEntryId,
			com.liferay.portal.kernel.dao.orm.QueryUtil.ALL_POS,
			com.liferay.portal.kernel.dao.orm.QueryUtil.ALL_POS, null, true);
	}

	/**
	 * Returns a range of all the asset display page entries where layoutPageTemplateEntryId = &#63;.
	 *
	 * <p>
	 * Useful when paginating results. Returns a maximum of <code>end - start</code> instances. <code>start</code> and <code>end</code> are not primary keys, they are indexes in the result set. Thus, <code>0</code> refers to the first result in the set. Setting both <code>start</code> and <code>end</code> to <code>com.liferay.portal.kernel.dao.orm.QueryUtil#ALL_POS</code> will return the full result set. If <code>orderByComparator</code> is specified, then the query will include the given ORDER BY logic. If <code>orderByComparator</code> is absent, then the query will include the default ORDER BY logic from <code>com.liferay.asset.display.page.model.impl.AssetDisplayPageEntryModelImpl</code>.
	 * </p>
	 *
	 * @param layoutPageTemplateEntryId the layout page template entry ID
	 * @param start the lower bound of the range of asset display page entries
	 * @param end the upper bound of the range of asset display page entries (not inclusive)
	 * @return the range of matching asset display page entries
	 */
	public default java.util.List<AssetDisplayPageEntry>
		findByLayoutPageTemplateEntryId(
			long layoutPageTemplateEntryId, int start, int end) {

		return findByLayoutPageTemplateEntryId(
			layoutPageTemplateEntryId, start, end, null, true);
	}

	/**
	 * Returns an ordered range of all the asset display page entries where layoutPageTemplateEntryId = &#63;.
	 *
	 * <p>
	 * Useful when paginating results. Returns a maximum of <code>end - start</code> instances. <code>start</code> and <code>end</code> are not primary keys, they are indexes in the result set. Thus, <code>0</code> refers to the first result in the set. Setting both <code>start</code> and <code>end</code> to <code>com.liferay.portal.kernel.dao.orm.QueryUtil#ALL_POS</code> will return the full result set. If <code>orderByComparator</code> is specified, then the query will include the given ORDER BY logic. If <code>orderByComparator</code> is absent, then the query will include the default ORDER BY logic from <code>com.liferay.asset.display.page.model.impl.AssetDisplayPageEntryModelImpl</code>.
	 * </p>
	 *
	 * @param layoutPageTemplateEntryId the layout page template entry ID
	 * @param start the lower bound of the range of asset display page entries
	 * @param end the upper bound of the range of asset display page entries (not inclusive)
	 * @param orderByComparator the comparator to order the results by (optionally <code>null</code>)
	 * @return the ordered range of matching asset display page entries
	 */
	public default java.util.List<AssetDisplayPageEntry>
		findByLayoutPageTemplateEntryId(
			long layoutPageTemplateEntryId, int start, int end,
			com.liferay.portal.kernel.util.OrderByComparator
				<AssetDisplayPageEntry> orderByComparator) {

		return findByLayoutPageTemplateEntryId(
			layoutPageTemplateEntryId, start, end, orderByComparator, true);
	}

	/**
	 * Returns all the asset display page entries where groupId = &#63; and classNameId = &#63;.
	 *
	 * @param groupId the group ID
	 * @param classNameId the class name ID
	 * @return the matching asset display page entries
	 */
	public default java.util.List<AssetDisplayPageEntry> findByG_CN(
		long groupId, long classNameId) {

		return findByG_CN(
			groupId, classNameId,
			com.liferay.portal.kernel.dao.orm.QueryUtil.ALL_POS,
			com.liferay.portal.kernel.dao.orm.QueryUtil.ALL_POS, null, true);
	}

	/**
	 * Returns a range of all the asset display page entries where groupId = &#63; and classNameId = &#63;.
	 *
	 * <p>
	 * Useful when paginating results. Returns a maximum of <code>end - start</code> instances. <code>start</code> and <code>end</code> are not primary keys, they are indexes in the result set. Thus, <code>0</code> refers to the first result in the set. Setting both <code>start</code> and <code>end</code> to <code>com.liferay.portal.kernel.dao.orm.QueryUtil#ALL_POS</code> will return the full result set. If <code>orderByComparator</code> is specified, then the query will include the given ORDER BY logic. If <code>orderByComparator</code> is absent, then the query will include the default ORDER BY logic from <code>com.liferay.asset.display.page.model.impl.AssetDisplayPageEntryModelImpl</code>.
	 * </p>
	 *
	 * @param groupId the group ID
	 * @param classNameId the class name ID
	 * @param start the lower bound of the range of asset display page entries
	 * @param end the upper bound of the range of asset display page entries (not inclusive)
	 * @return the range of matching asset display page entries
	 */
	public default java.util.List<AssetDisplayPageEntry> findByG_CN(
		long groupId, long classNameId, int start, int end) {

		return findByG_CN(groupId, classNameId, start, end, null, true);
	}

	/**
	 * Returns an ordered range of all the asset display page entries where groupId = &#63; and classNameId = &#63;.
	 *
	 * <p>
	 * Useful when paginating results. Returns a maximum of <code>end - start</code> instances. <code>start</code> and <code>end</code> are not primary keys, they are indexes in the result set. Thus, <code>0</code> refers to the first result in the set. Setting both <code>start</code> and <code>end</code> to <code>com.liferay.portal.kernel.dao.orm.QueryUtil#ALL_POS</code> will return the full result set. If <code>orderByComparator</code> is specified, then the query will include the given ORDER BY logic. If <code>orderByComparator</code> is absent, then the query will include the default ORDER BY logic from <code>com.liferay.asset.display.page.model.impl.AssetDisplayPageEntryModelImpl</code>.
	 * </p>
	 *
	 * @param groupId the group ID
	 * @param classNameId the class name ID
	 * @param start the lower bound of the range of asset display page entries
	 * @param end the upper bound of the range of asset display page entries (not inclusive)
	 * @param orderByComparator the comparator to order the results by (optionally <code>null</code>)
	 * @return the ordered range of matching asset display page entries
	 */
	public default java.util.List<AssetDisplayPageEntry> findByG_CN(
		long groupId, long classNameId, int start, int end,
		com.liferay.portal.kernel.util.OrderByComparator<AssetDisplayPageEntry>
			orderByComparator) {

		return findByG_CN(
			groupId, classNameId, start, end, orderByComparator, true);
	}

}
// LIFERAY-SERVICE-BUILDER-HASH:-921160051