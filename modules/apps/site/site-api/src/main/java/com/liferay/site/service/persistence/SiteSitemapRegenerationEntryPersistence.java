/**
 * SPDX-FileCopyrightText: (c) 2026 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

package com.liferay.site.service.persistence;

import com.liferay.portal.kernel.service.persistence.BasePersistence;
import com.liferay.site.exception.NoSuchSitemapRegenerationEntryException;
import com.liferay.site.model.SiteSitemapRegenerationEntry;

import org.osgi.annotation.versioning.ProviderType;

/**
 * The persistence interface for the site sitemap regeneration entry service.
 *
 * <p>
 * Caching information and settings can be found in <code>portal.properties</code>
 * </p>
 *
 * @author Brian Wing Shun Chan
 * @see SiteSitemapRegenerationEntryUtil
 * @generated
 */
@ProviderType
public interface SiteSitemapRegenerationEntryPersistence
	extends BasePersistence<SiteSitemapRegenerationEntry> {

	/*
	 * NOTE FOR DEVELOPERS:
	 *
	 * Never modify or reference this interface directly. Always use {@link SiteSitemapRegenerationEntryUtil} to access the site sitemap regeneration entry persistence. Modify <code>service.xml</code> and rerun ServiceBuilder to regenerate this interface.
	 */

	/**
	 * Returns an ordered range of all the site sitemap regeneration entries where companyId = &#63;.
	 *
	 * <p>
	 * Useful when paginating results. Returns a maximum of <code>end - start</code> instances. <code>start</code> and <code>end</code> are not primary keys, they are indexes in the result set. Thus, <code>0</code> refers to the first result in the set. Setting both <code>start</code> and <code>end</code> to <code>com.liferay.portal.kernel.dao.orm.QueryUtil#ALL_POS</code> will return the full result set. If <code>orderByComparator</code> is specified, then the query will include the given ORDER BY logic. If <code>orderByComparator</code> is absent, then the query will include the default ORDER BY logic from <code>com.liferay.site.model.impl.SiteSitemapRegenerationEntryModelImpl</code>.
	 * </p>
	 *
	 * @param companyId the company ID
	 * @param start the lower bound of the range of site sitemap regeneration entries
	 * @param end the upper bound of the range of site sitemap regeneration entries (not inclusive)
	 * @param orderByComparator the comparator to order the results by (optionally <code>null</code>)
	 * @param useFinderCache whether to use the finder cache
	 * @return the ordered range of matching site sitemap regeneration entries
	 */
	public java.util.List<SiteSitemapRegenerationEntry> findByCompanyId(
		long companyId, int start, int end,
		com.liferay.portal.kernel.util.OrderByComparator
			<SiteSitemapRegenerationEntry> orderByComparator,
		boolean useFinderCache);

	/**
	 * Returns the first site sitemap regeneration entry in the ordered set where companyId = &#63;.
	 *
	 * @param companyId the company ID
	 * @param orderByComparator the comparator to order the set by (optionally <code>null</code>)
	 * @return the first matching site sitemap regeneration entry
	 * @throws NoSuchSitemapRegenerationEntryException if a matching site sitemap regeneration entry could not be found
	 */
	public SiteSitemapRegenerationEntry findByCompanyId_First(
			long companyId,
			com.liferay.portal.kernel.util.OrderByComparator
				<SiteSitemapRegenerationEntry> orderByComparator)
		throws NoSuchSitemapRegenerationEntryException;

	/**
	 * Returns the first site sitemap regeneration entry in the ordered set where companyId = &#63;.
	 *
	 * @param companyId the company ID
	 * @param orderByComparator the comparator to order the set by (optionally <code>null</code>)
	 * @return the first matching site sitemap regeneration entry, or <code>null</code> if a matching site sitemap regeneration entry could not be found
	 */
	public SiteSitemapRegenerationEntry fetchByCompanyId_First(
		long companyId,
		com.liferay.portal.kernel.util.OrderByComparator
			<SiteSitemapRegenerationEntry> orderByComparator);

	/**
	 * Removes all the site sitemap regeneration entries where companyId = &#63; from the database.
	 *
	 * @param companyId the company ID
	 */
	public void removeByCompanyId(long companyId);

	/**
	 * Returns the number of site sitemap regeneration entries where companyId = &#63;.
	 *
	 * @param companyId the company ID
	 * @return the number of matching site sitemap regeneration entries
	 */
	public int countByCompanyId(long companyId);

	/**
	 * Returns an ordered range of all the site sitemap regeneration entries where groupId = &#63; and companyId = &#63; and assetTypeKey = &#63;.
	 *
	 * <p>
	 * Useful when paginating results. Returns a maximum of <code>end - start</code> instances. <code>start</code> and <code>end</code> are not primary keys, they are indexes in the result set. Thus, <code>0</code> refers to the first result in the set. Setting both <code>start</code> and <code>end</code> to <code>com.liferay.portal.kernel.dao.orm.QueryUtil#ALL_POS</code> will return the full result set. If <code>orderByComparator</code> is specified, then the query will include the given ORDER BY logic. If <code>orderByComparator</code> is absent, then the query will include the default ORDER BY logic from <code>com.liferay.site.model.impl.SiteSitemapRegenerationEntryModelImpl</code>.
	 * </p>
	 *
	 * @param groupId the group ID
	 * @param companyId the company ID
	 * @param assetTypeKey the asset type key
	 * @param start the lower bound of the range of site sitemap regeneration entries
	 * @param end the upper bound of the range of site sitemap regeneration entries (not inclusive)
	 * @param orderByComparator the comparator to order the results by (optionally <code>null</code>)
	 * @param useFinderCache whether to use the finder cache
	 * @return the ordered range of matching site sitemap regeneration entries
	 */
	public java.util.List<SiteSitemapRegenerationEntry> findByG_C_A(
		long groupId, long companyId, String assetTypeKey, int start, int end,
		com.liferay.portal.kernel.util.OrderByComparator
			<SiteSitemapRegenerationEntry> orderByComparator,
		boolean useFinderCache);

	/**
	 * Returns the first site sitemap regeneration entry in the ordered set where groupId = &#63; and companyId = &#63; and assetTypeKey = &#63;.
	 *
	 * @param groupId the group ID
	 * @param companyId the company ID
	 * @param assetTypeKey the asset type key
	 * @param orderByComparator the comparator to order the set by (optionally <code>null</code>)
	 * @return the first matching site sitemap regeneration entry
	 * @throws NoSuchSitemapRegenerationEntryException if a matching site sitemap regeneration entry could not be found
	 */
	public SiteSitemapRegenerationEntry findByG_C_A_First(
			long groupId, long companyId, String assetTypeKey,
			com.liferay.portal.kernel.util.OrderByComparator
				<SiteSitemapRegenerationEntry> orderByComparator)
		throws NoSuchSitemapRegenerationEntryException;

	/**
	 * Returns the first site sitemap regeneration entry in the ordered set where groupId = &#63; and companyId = &#63; and assetTypeKey = &#63;.
	 *
	 * @param groupId the group ID
	 * @param companyId the company ID
	 * @param assetTypeKey the asset type key
	 * @param orderByComparator the comparator to order the set by (optionally <code>null</code>)
	 * @return the first matching site sitemap regeneration entry, or <code>null</code> if a matching site sitemap regeneration entry could not be found
	 */
	public SiteSitemapRegenerationEntry fetchByG_C_A_First(
		long groupId, long companyId, String assetTypeKey,
		com.liferay.portal.kernel.util.OrderByComparator
			<SiteSitemapRegenerationEntry> orderByComparator);

	/**
	 * Removes all the site sitemap regeneration entries where groupId = &#63; and companyId = &#63; and assetTypeKey = &#63; from the database.
	 *
	 * @param groupId the group ID
	 * @param companyId the company ID
	 * @param assetTypeKey the asset type key
	 */
	public void removeByG_C_A(
		long groupId, long companyId, String assetTypeKey);

	/**
	 * Returns the number of site sitemap regeneration entries where groupId = &#63; and companyId = &#63; and assetTypeKey = &#63;.
	 *
	 * @param groupId the group ID
	 * @param companyId the company ID
	 * @param assetTypeKey the asset type key
	 * @return the number of matching site sitemap regeneration entries
	 */
	public int countByG_C_A(long groupId, long companyId, String assetTypeKey);

	/**
	 * Creates a new site sitemap regeneration entry with the primary key. Does not add the site sitemap regeneration entry to the database.
	 *
	 * @param siteSitemapRegenerationEntryId the primary key for the new site sitemap regeneration entry
	 * @return the new site sitemap regeneration entry
	 */
	public SiteSitemapRegenerationEntry create(
		long siteSitemapRegenerationEntryId);

	/**
	 * Removes the site sitemap regeneration entry with the primary key from the database. Also notifies the appropriate model listeners.
	 *
	 * @param siteSitemapRegenerationEntryId the primary key of the site sitemap regeneration entry
	 * @return the site sitemap regeneration entry that was removed
	 * @throws NoSuchSitemapRegenerationEntryException if a site sitemap regeneration entry with the primary key could not be found
	 */
	public SiteSitemapRegenerationEntry remove(
			long siteSitemapRegenerationEntryId)
		throws NoSuchSitemapRegenerationEntryException;

	public SiteSitemapRegenerationEntry updateImpl(
		SiteSitemapRegenerationEntry siteSitemapRegenerationEntry);

	/**
	 * Returns the site sitemap regeneration entry with the primary key or throws a <code>NoSuchSitemapRegenerationEntryException</code> if it could not be found.
	 *
	 * @param siteSitemapRegenerationEntryId the primary key of the site sitemap regeneration entry
	 * @return the site sitemap regeneration entry
	 * @throws NoSuchSitemapRegenerationEntryException if a site sitemap regeneration entry with the primary key could not be found
	 */
	public SiteSitemapRegenerationEntry findByPrimaryKey(
			long siteSitemapRegenerationEntryId)
		throws NoSuchSitemapRegenerationEntryException;

	/**
	 * Returns the site sitemap regeneration entry with the primary key or returns <code>null</code> if it could not be found.
	 *
	 * @param siteSitemapRegenerationEntryId the primary key of the site sitemap regeneration entry
	 * @return the site sitemap regeneration entry, or <code>null</code> if a site sitemap regeneration entry with the primary key could not be found
	 */
	public SiteSitemapRegenerationEntry fetchByPrimaryKey(
		long siteSitemapRegenerationEntryId);

	/**
	 * Returns all the site sitemap regeneration entries where companyId = &#63;.
	 *
	 * @param companyId the company ID
	 * @return the matching site sitemap regeneration entries
	 */
	public default java.util.List<SiteSitemapRegenerationEntry> findByCompanyId(
		long companyId) {

		return findByCompanyId(
			companyId, com.liferay.portal.kernel.dao.orm.QueryUtil.ALL_POS,
			com.liferay.portal.kernel.dao.orm.QueryUtil.ALL_POS, null, true);
	}

	/**
	 * Returns a range of all the site sitemap regeneration entries where companyId = &#63;.
	 *
	 * <p>
	 * Useful when paginating results. Returns a maximum of <code>end - start</code> instances. <code>start</code> and <code>end</code> are not primary keys, they are indexes in the result set. Thus, <code>0</code> refers to the first result in the set. Setting both <code>start</code> and <code>end</code> to <code>com.liferay.portal.kernel.dao.orm.QueryUtil#ALL_POS</code> will return the full result set. If <code>orderByComparator</code> is specified, then the query will include the given ORDER BY logic. If <code>orderByComparator</code> is absent, then the query will include the default ORDER BY logic from <code>com.liferay.site.model.impl.SiteSitemapRegenerationEntryModelImpl</code>.
	 * </p>
	 *
	 * @param companyId the company ID
	 * @param start the lower bound of the range of site sitemap regeneration entries
	 * @param end the upper bound of the range of site sitemap regeneration entries (not inclusive)
	 * @return the range of matching site sitemap regeneration entries
	 */
	public default java.util.List<SiteSitemapRegenerationEntry> findByCompanyId(
		long companyId, int start, int end) {

		return findByCompanyId(companyId, start, end, null, true);
	}

	/**
	 * Returns an ordered range of all the site sitemap regeneration entries where companyId = &#63;.
	 *
	 * <p>
	 * Useful when paginating results. Returns a maximum of <code>end - start</code> instances. <code>start</code> and <code>end</code> are not primary keys, they are indexes in the result set. Thus, <code>0</code> refers to the first result in the set. Setting both <code>start</code> and <code>end</code> to <code>com.liferay.portal.kernel.dao.orm.QueryUtil#ALL_POS</code> will return the full result set. If <code>orderByComparator</code> is specified, then the query will include the given ORDER BY logic. If <code>orderByComparator</code> is absent, then the query will include the default ORDER BY logic from <code>com.liferay.site.model.impl.SiteSitemapRegenerationEntryModelImpl</code>.
	 * </p>
	 *
	 * @param companyId the company ID
	 * @param start the lower bound of the range of site sitemap regeneration entries
	 * @param end the upper bound of the range of site sitemap regeneration entries (not inclusive)
	 * @param orderByComparator the comparator to order the results by (optionally <code>null</code>)
	 * @return the ordered range of matching site sitemap regeneration entries
	 */
	public default java.util.List<SiteSitemapRegenerationEntry> findByCompanyId(
		long companyId, int start, int end,
		com.liferay.portal.kernel.util.OrderByComparator
			<SiteSitemapRegenerationEntry> orderByComparator) {

		return findByCompanyId(companyId, start, end, orderByComparator, true);
	}

	/**
	 * Returns all the site sitemap regeneration entries where groupId = &#63; and companyId = &#63; and assetTypeKey = &#63;.
	 *
	 * @param groupId the group ID
	 * @param companyId the company ID
	 * @param assetTypeKey the asset type key
	 * @return the matching site sitemap regeneration entries
	 */
	public default java.util.List<SiteSitemapRegenerationEntry> findByG_C_A(
		long groupId, long companyId, String assetTypeKey) {

		return findByG_C_A(
			groupId, companyId, assetTypeKey,
			com.liferay.portal.kernel.dao.orm.QueryUtil.ALL_POS,
			com.liferay.portal.kernel.dao.orm.QueryUtil.ALL_POS, null, true);
	}

	/**
	 * Returns a range of all the site sitemap regeneration entries where groupId = &#63; and companyId = &#63; and assetTypeKey = &#63;.
	 *
	 * <p>
	 * Useful when paginating results. Returns a maximum of <code>end - start</code> instances. <code>start</code> and <code>end</code> are not primary keys, they are indexes in the result set. Thus, <code>0</code> refers to the first result in the set. Setting both <code>start</code> and <code>end</code> to <code>com.liferay.portal.kernel.dao.orm.QueryUtil#ALL_POS</code> will return the full result set. If <code>orderByComparator</code> is specified, then the query will include the given ORDER BY logic. If <code>orderByComparator</code> is absent, then the query will include the default ORDER BY logic from <code>com.liferay.site.model.impl.SiteSitemapRegenerationEntryModelImpl</code>.
	 * </p>
	 *
	 * @param groupId the group ID
	 * @param companyId the company ID
	 * @param assetTypeKey the asset type key
	 * @param start the lower bound of the range of site sitemap regeneration entries
	 * @param end the upper bound of the range of site sitemap regeneration entries (not inclusive)
	 * @return the range of matching site sitemap regeneration entries
	 */
	public default java.util.List<SiteSitemapRegenerationEntry> findByG_C_A(
		long groupId, long companyId, String assetTypeKey, int start, int end) {

		return findByG_C_A(
			groupId, companyId, assetTypeKey, start, end, null, true);
	}

	/**
	 * Returns an ordered range of all the site sitemap regeneration entries where groupId = &#63; and companyId = &#63; and assetTypeKey = &#63;.
	 *
	 * <p>
	 * Useful when paginating results. Returns a maximum of <code>end - start</code> instances. <code>start</code> and <code>end</code> are not primary keys, they are indexes in the result set. Thus, <code>0</code> refers to the first result in the set. Setting both <code>start</code> and <code>end</code> to <code>com.liferay.portal.kernel.dao.orm.QueryUtil#ALL_POS</code> will return the full result set. If <code>orderByComparator</code> is specified, then the query will include the given ORDER BY logic. If <code>orderByComparator</code> is absent, then the query will include the default ORDER BY logic from <code>com.liferay.site.model.impl.SiteSitemapRegenerationEntryModelImpl</code>.
	 * </p>
	 *
	 * @param groupId the group ID
	 * @param companyId the company ID
	 * @param assetTypeKey the asset type key
	 * @param start the lower bound of the range of site sitemap regeneration entries
	 * @param end the upper bound of the range of site sitemap regeneration entries (not inclusive)
	 * @param orderByComparator the comparator to order the results by (optionally <code>null</code>)
	 * @return the ordered range of matching site sitemap regeneration entries
	 */
	public default java.util.List<SiteSitemapRegenerationEntry> findByG_C_A(
		long groupId, long companyId, String assetTypeKey, int start, int end,
		com.liferay.portal.kernel.util.OrderByComparator
			<SiteSitemapRegenerationEntry> orderByComparator) {

		return findByG_C_A(
			groupId, companyId, assetTypeKey, start, end, orderByComparator,
			true);
	}

}
// LIFERAY-SERVICE-BUILDER-HASH:899841980