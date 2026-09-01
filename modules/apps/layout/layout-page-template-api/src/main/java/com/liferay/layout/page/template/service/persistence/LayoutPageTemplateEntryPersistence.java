/**
 * SPDX-FileCopyrightText: (c) 2000 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

package com.liferay.layout.page.template.service.persistence;

import com.liferay.layout.page.template.exception.NoSuchPageTemplateEntryException;
import com.liferay.layout.page.template.model.LayoutPageTemplateEntry;
import com.liferay.portal.kernel.service.persistence.BasePersistence;
import com.liferay.portal.kernel.service.persistence.change.tracking.CTPersistence;

import org.osgi.annotation.versioning.ProviderType;

/**
 * The persistence interface for the layout page template entry service.
 *
 * <p>
 * Caching information and settings can be found in <code>portal.properties</code>
 * </p>
 *
 * @author Brian Wing Shun Chan
 * @see LayoutPageTemplateEntryUtil
 * @generated
 */
@ProviderType
public interface LayoutPageTemplateEntryPersistence
	extends BasePersistence<LayoutPageTemplateEntry>,
			CTPersistence<LayoutPageTemplateEntry> {

	/*
	 * NOTE FOR DEVELOPERS:
	 *
	 * Never modify or reference this interface directly. Always use {@link LayoutPageTemplateEntryUtil} to access the layout page template entry persistence. Modify <code>service.xml</code> and rerun ServiceBuilder to regenerate this interface.
	 */

	/**
	 * Returns an ordered range of all the layout page template entries where uuid = &#63;.
	 *
	 * <p>
	 * Useful when paginating results. Returns a maximum of <code>end - start</code> instances. <code>start</code> and <code>end</code> are not primary keys, they are indexes in the result set. Thus, <code>0</code> refers to the first result in the set. Setting both <code>start</code> and <code>end</code> to <code>com.liferay.portal.kernel.dao.orm.QueryUtil#ALL_POS</code> will return the full result set. If <code>orderByComparator</code> is specified, then the query will include the given ORDER BY logic. If <code>orderByComparator</code> is absent, then the query will include the default ORDER BY logic from <code>com.liferay.layout.page.template.model.impl.LayoutPageTemplateEntryModelImpl</code>.
	 * </p>
	 *
	 * @param uuid the uuid
	 * @param start the lower bound of the range of layout page template entries
	 * @param end the upper bound of the range of layout page template entries (not inclusive)
	 * @param orderByComparator the comparator to order the results by (optionally <code>null</code>)
	 * @param useFinderCache whether to use the finder cache
	 * @return the ordered range of matching layout page template entries
	 */
	public java.util.List<LayoutPageTemplateEntry> findByUuid(
		String uuid, int start, int end,
		com.liferay.portal.kernel.util.OrderByComparator
			<LayoutPageTemplateEntry> orderByComparator,
		boolean useFinderCache);

	/**
	 * Returns the first layout page template entry in the ordered set where uuid = &#63;.
	 *
	 * @param uuid the uuid
	 * @param orderByComparator the comparator to order the set by (optionally <code>null</code>)
	 * @return the first matching layout page template entry
	 * @throws NoSuchPageTemplateEntryException if a matching layout page template entry could not be found
	 */
	public LayoutPageTemplateEntry findByUuid_First(
			String uuid,
			com.liferay.portal.kernel.util.OrderByComparator
				<LayoutPageTemplateEntry> orderByComparator)
		throws NoSuchPageTemplateEntryException;

	/**
	 * Returns the first layout page template entry in the ordered set where uuid = &#63;.
	 *
	 * @param uuid the uuid
	 * @param orderByComparator the comparator to order the set by (optionally <code>null</code>)
	 * @return the first matching layout page template entry, or <code>null</code> if a matching layout page template entry could not be found
	 */
	public LayoutPageTemplateEntry fetchByUuid_First(
		String uuid,
		com.liferay.portal.kernel.util.OrderByComparator
			<LayoutPageTemplateEntry> orderByComparator);

	/**
	 * Removes all the layout page template entries where uuid = &#63; from the database.
	 *
	 * @param uuid the uuid
	 */
	public void removeByUuid(String uuid);

	/**
	 * Returns the number of layout page template entries where uuid = &#63;.
	 *
	 * @param uuid the uuid
	 * @return the number of matching layout page template entries
	 */
	public int countByUuid(String uuid);

	/**
	 * Returns the layout page template entry where uuid = &#63; and groupId = &#63; or throws a <code>NoSuchPageTemplateEntryException</code> if it could not be found.
	 *
	 * @param uuid the uuid
	 * @param groupId the group ID
	 * @return the matching layout page template entry
	 * @throws NoSuchPageTemplateEntryException if a matching layout page template entry could not be found
	 */
	public LayoutPageTemplateEntry findByUUID_G(String uuid, long groupId)
		throws NoSuchPageTemplateEntryException;

	/**
	 * Returns the layout page template entry where uuid = &#63; and groupId = &#63; or returns <code>null</code> if it could not be found, optionally using the finder cache.
	 *
	 * @param uuid the uuid
	 * @param groupId the group ID
	 * @param useFinderCache whether to use the finder cache
	 * @return the matching layout page template entry, or <code>null</code> if a matching layout page template entry could not be found
	 */
	public LayoutPageTemplateEntry fetchByUUID_G(
		String uuid, long groupId, boolean useFinderCache);

	/**
	 * Removes the layout page template entry where uuid = &#63; and groupId = &#63; from the database.
	 *
	 * @param uuid the uuid
	 * @param groupId the group ID
	 * @return the layout page template entry that was removed
	 */
	public LayoutPageTemplateEntry removeByUUID_G(String uuid, long groupId)
		throws NoSuchPageTemplateEntryException;

	/**
	 * Returns the number of layout page template entries where uuid = &#63; and groupId = &#63;.
	 *
	 * @param uuid the uuid
	 * @param groupId the group ID
	 * @return the number of matching layout page template entries
	 */
	public int countByUUID_G(String uuid, long groupId);

	/**
	 * Returns an ordered range of all the layout page template entries where uuid = &#63; and companyId = &#63;.
	 *
	 * <p>
	 * Useful when paginating results. Returns a maximum of <code>end - start</code> instances. <code>start</code> and <code>end</code> are not primary keys, they are indexes in the result set. Thus, <code>0</code> refers to the first result in the set. Setting both <code>start</code> and <code>end</code> to <code>com.liferay.portal.kernel.dao.orm.QueryUtil#ALL_POS</code> will return the full result set. If <code>orderByComparator</code> is specified, then the query will include the given ORDER BY logic. If <code>orderByComparator</code> is absent, then the query will include the default ORDER BY logic from <code>com.liferay.layout.page.template.model.impl.LayoutPageTemplateEntryModelImpl</code>.
	 * </p>
	 *
	 * @param uuid the uuid
	 * @param companyId the company ID
	 * @param start the lower bound of the range of layout page template entries
	 * @param end the upper bound of the range of layout page template entries (not inclusive)
	 * @param orderByComparator the comparator to order the results by (optionally <code>null</code>)
	 * @param useFinderCache whether to use the finder cache
	 * @return the ordered range of matching layout page template entries
	 */
	public java.util.List<LayoutPageTemplateEntry> findByUuid_C(
		String uuid, long companyId, int start, int end,
		com.liferay.portal.kernel.util.OrderByComparator
			<LayoutPageTemplateEntry> orderByComparator,
		boolean useFinderCache);

	/**
	 * Returns the first layout page template entry in the ordered set where uuid = &#63; and companyId = &#63;.
	 *
	 * @param uuid the uuid
	 * @param companyId the company ID
	 * @param orderByComparator the comparator to order the set by (optionally <code>null</code>)
	 * @return the first matching layout page template entry
	 * @throws NoSuchPageTemplateEntryException if a matching layout page template entry could not be found
	 */
	public LayoutPageTemplateEntry findByUuid_C_First(
			String uuid, long companyId,
			com.liferay.portal.kernel.util.OrderByComparator
				<LayoutPageTemplateEntry> orderByComparator)
		throws NoSuchPageTemplateEntryException;

	/**
	 * Returns the first layout page template entry in the ordered set where uuid = &#63; and companyId = &#63;.
	 *
	 * @param uuid the uuid
	 * @param companyId the company ID
	 * @param orderByComparator the comparator to order the set by (optionally <code>null</code>)
	 * @return the first matching layout page template entry, or <code>null</code> if a matching layout page template entry could not be found
	 */
	public LayoutPageTemplateEntry fetchByUuid_C_First(
		String uuid, long companyId,
		com.liferay.portal.kernel.util.OrderByComparator
			<LayoutPageTemplateEntry> orderByComparator);

	/**
	 * Removes all the layout page template entries where uuid = &#63; and companyId = &#63; from the database.
	 *
	 * @param uuid the uuid
	 * @param companyId the company ID
	 */
	public void removeByUuid_C(String uuid, long companyId);

	/**
	 * Returns the number of layout page template entries where uuid = &#63; and companyId = &#63;.
	 *
	 * @param uuid the uuid
	 * @param companyId the company ID
	 * @return the number of matching layout page template entries
	 */
	public int countByUuid_C(String uuid, long companyId);

	/**
	 * Returns an ordered range of all the layout page template entries where groupId = &#63;.
	 *
	 * <p>
	 * Useful when paginating results. Returns a maximum of <code>end - start</code> instances. <code>start</code> and <code>end</code> are not primary keys, they are indexes in the result set. Thus, <code>0</code> refers to the first result in the set. Setting both <code>start</code> and <code>end</code> to <code>com.liferay.portal.kernel.dao.orm.QueryUtil#ALL_POS</code> will return the full result set. If <code>orderByComparator</code> is specified, then the query will include the given ORDER BY logic. If <code>orderByComparator</code> is absent, then the query will include the default ORDER BY logic from <code>com.liferay.layout.page.template.model.impl.LayoutPageTemplateEntryModelImpl</code>.
	 * </p>
	 *
	 * @param groupId the group ID
	 * @param start the lower bound of the range of layout page template entries
	 * @param end the upper bound of the range of layout page template entries (not inclusive)
	 * @param orderByComparator the comparator to order the results by (optionally <code>null</code>)
	 * @param useFinderCache whether to use the finder cache
	 * @return the ordered range of matching layout page template entries
	 */
	public java.util.List<LayoutPageTemplateEntry> findByGroupId(
		long groupId, int start, int end,
		com.liferay.portal.kernel.util.OrderByComparator
			<LayoutPageTemplateEntry> orderByComparator,
		boolean useFinderCache);

	/**
	 * Returns the first layout page template entry in the ordered set where groupId = &#63;.
	 *
	 * @param groupId the group ID
	 * @param orderByComparator the comparator to order the set by (optionally <code>null</code>)
	 * @return the first matching layout page template entry
	 * @throws NoSuchPageTemplateEntryException if a matching layout page template entry could not be found
	 */
	public LayoutPageTemplateEntry findByGroupId_First(
			long groupId,
			com.liferay.portal.kernel.util.OrderByComparator
				<LayoutPageTemplateEntry> orderByComparator)
		throws NoSuchPageTemplateEntryException;

	/**
	 * Returns the first layout page template entry in the ordered set where groupId = &#63;.
	 *
	 * @param groupId the group ID
	 * @param orderByComparator the comparator to order the set by (optionally <code>null</code>)
	 * @return the first matching layout page template entry, or <code>null</code> if a matching layout page template entry could not be found
	 */
	public LayoutPageTemplateEntry fetchByGroupId_First(
		long groupId,
		com.liferay.portal.kernel.util.OrderByComparator
			<LayoutPageTemplateEntry> orderByComparator);

	/**
	 * Returns an ordered range of all the layout page template entries that the user has permissions to view where groupId = &#63;.
	 *
	 * <p>
	 * Useful when paginating results. Returns a maximum of <code>end - start</code> instances. <code>start</code> and <code>end</code> are not primary keys, they are indexes in the result set. Thus, <code>0</code> refers to the first result in the set. Setting both <code>start</code> and <code>end</code> to <code>com.liferay.portal.kernel.dao.orm.QueryUtil#ALL_POS</code> will return the full result set. If <code>orderByComparator</code> is specified, then the query will include the given ORDER BY logic. If <code>orderByComparator</code> is absent, then the query will include the default ORDER BY logic from <code>com.liferay.layout.page.template.model.impl.LayoutPageTemplateEntryModelImpl</code>.
	 * </p>
	 *
	 * @param groupId the group ID
	 * @param start the lower bound of the range of layout page template entries
	 * @param end the upper bound of the range of layout page template entries (not inclusive)
	 * @param orderByComparator the comparator to order the results by (optionally <code>null</code>)
	 * @return the ordered range of matching layout page template entries that the user has permission to view
	 */
	public java.util.List<LayoutPageTemplateEntry> filterFindByGroupId(
		long groupId, int start, int end,
		com.liferay.portal.kernel.util.OrderByComparator
			<LayoutPageTemplateEntry> orderByComparator);

	/**
	 * Removes all the layout page template entries where groupId = &#63; from the database.
	 *
	 * @param groupId the group ID
	 */
	public void removeByGroupId(long groupId);

	/**
	 * Returns the number of layout page template entries where groupId = &#63;.
	 *
	 * @param groupId the group ID
	 * @return the number of matching layout page template entries
	 */
	public int countByGroupId(long groupId);

	/**
	 * Returns the number of layout page template entries that the user has permission to view where groupId = &#63;.
	 *
	 * @param groupId the group ID
	 * @return the number of matching layout page template entries that the user has permission to view
	 */
	public int filterCountByGroupId(long groupId);

	/**
	 * Returns an ordered range of all the layout page template entries where layoutPrototypeId = &#63;.
	 *
	 * <p>
	 * Useful when paginating results. Returns a maximum of <code>end - start</code> instances. <code>start</code> and <code>end</code> are not primary keys, they are indexes in the result set. Thus, <code>0</code> refers to the first result in the set. Setting both <code>start</code> and <code>end</code> to <code>com.liferay.portal.kernel.dao.orm.QueryUtil#ALL_POS</code> will return the full result set. If <code>orderByComparator</code> is specified, then the query will include the given ORDER BY logic. If <code>orderByComparator</code> is absent, then the query will include the default ORDER BY logic from <code>com.liferay.layout.page.template.model.impl.LayoutPageTemplateEntryModelImpl</code>.
	 * </p>
	 *
	 * @param layoutPrototypeId the layout prototype ID
	 * @param start the lower bound of the range of layout page template entries
	 * @param end the upper bound of the range of layout page template entries (not inclusive)
	 * @param orderByComparator the comparator to order the results by (optionally <code>null</code>)
	 * @param useFinderCache whether to use the finder cache
	 * @return the ordered range of matching layout page template entries
	 */
	public java.util.List<LayoutPageTemplateEntry> findByLayoutPrototypeId(
		long layoutPrototypeId, int start, int end,
		com.liferay.portal.kernel.util.OrderByComparator
			<LayoutPageTemplateEntry> orderByComparator,
		boolean useFinderCache);

	/**
	 * Returns the first layout page template entry in the ordered set where layoutPrototypeId = &#63;.
	 *
	 * @param layoutPrototypeId the layout prototype ID
	 * @param orderByComparator the comparator to order the set by (optionally <code>null</code>)
	 * @return the first matching layout page template entry
	 * @throws NoSuchPageTemplateEntryException if a matching layout page template entry could not be found
	 */
	public LayoutPageTemplateEntry findByLayoutPrototypeId_First(
			long layoutPrototypeId,
			com.liferay.portal.kernel.util.OrderByComparator
				<LayoutPageTemplateEntry> orderByComparator)
		throws NoSuchPageTemplateEntryException;

	/**
	 * Returns the first layout page template entry in the ordered set where layoutPrototypeId = &#63;.
	 *
	 * @param layoutPrototypeId the layout prototype ID
	 * @param orderByComparator the comparator to order the set by (optionally <code>null</code>)
	 * @return the first matching layout page template entry, or <code>null</code> if a matching layout page template entry could not be found
	 */
	public LayoutPageTemplateEntry fetchByLayoutPrototypeId_First(
		long layoutPrototypeId,
		com.liferay.portal.kernel.util.OrderByComparator
			<LayoutPageTemplateEntry> orderByComparator);

	/**
	 * Removes all the layout page template entries where layoutPrototypeId = &#63; from the database.
	 *
	 * @param layoutPrototypeId the layout prototype ID
	 */
	public void removeByLayoutPrototypeId(long layoutPrototypeId);

	/**
	 * Returns the number of layout page template entries where layoutPrototypeId = &#63;.
	 *
	 * @param layoutPrototypeId the layout prototype ID
	 * @return the number of matching layout page template entries
	 */
	public int countByLayoutPrototypeId(long layoutPrototypeId);

	/**
	 * Returns the layout page template entry where plid = &#63; or throws a <code>NoSuchPageTemplateEntryException</code> if it could not be found.
	 *
	 * @param plid the plid
	 * @return the matching layout page template entry
	 * @throws NoSuchPageTemplateEntryException if a matching layout page template entry could not be found
	 */
	public LayoutPageTemplateEntry findByPlid(long plid)
		throws NoSuchPageTemplateEntryException;

	/**
	 * Returns the layout page template entry where plid = &#63; or returns <code>null</code> if it could not be found, optionally using the finder cache.
	 *
	 * @param plid the plid
	 * @param useFinderCache whether to use the finder cache
	 * @return the matching layout page template entry, or <code>null</code> if a matching layout page template entry could not be found
	 */
	public LayoutPageTemplateEntry fetchByPlid(
		long plid, boolean useFinderCache);

	/**
	 * Removes the layout page template entry where plid = &#63; from the database.
	 *
	 * @param plid the plid
	 * @return the layout page template entry that was removed
	 */
	public LayoutPageTemplateEntry removeByPlid(long plid)
		throws NoSuchPageTemplateEntryException;

	/**
	 * Returns the number of layout page template entries where plid = &#63;.
	 *
	 * @param plid the plid
	 * @return the number of matching layout page template entries
	 */
	public int countByPlid(long plid);

	/**
	 * Returns an ordered range of all the layout page template entries where groupId = &#63; and layoutPageTemplateCollectionId = &#63;.
	 *
	 * <p>
	 * Useful when paginating results. Returns a maximum of <code>end - start</code> instances. <code>start</code> and <code>end</code> are not primary keys, they are indexes in the result set. Thus, <code>0</code> refers to the first result in the set. Setting both <code>start</code> and <code>end</code> to <code>com.liferay.portal.kernel.dao.orm.QueryUtil#ALL_POS</code> will return the full result set. If <code>orderByComparator</code> is specified, then the query will include the given ORDER BY logic. If <code>orderByComparator</code> is absent, then the query will include the default ORDER BY logic from <code>com.liferay.layout.page.template.model.impl.LayoutPageTemplateEntryModelImpl</code>.
	 * </p>
	 *
	 * @param groupId the group ID
	 * @param layoutPageTemplateCollectionId the layout page template collection ID
	 * @param start the lower bound of the range of layout page template entries
	 * @param end the upper bound of the range of layout page template entries (not inclusive)
	 * @param orderByComparator the comparator to order the results by (optionally <code>null</code>)
	 * @param useFinderCache whether to use the finder cache
	 * @return the ordered range of matching layout page template entries
	 */
	public java.util.List<LayoutPageTemplateEntry> findByG_L(
		long groupId, long layoutPageTemplateCollectionId, int start, int end,
		com.liferay.portal.kernel.util.OrderByComparator
			<LayoutPageTemplateEntry> orderByComparator,
		boolean useFinderCache);

	/**
	 * Returns the first layout page template entry in the ordered set where groupId = &#63; and layoutPageTemplateCollectionId = &#63;.
	 *
	 * @param groupId the group ID
	 * @param layoutPageTemplateCollectionId the layout page template collection ID
	 * @param orderByComparator the comparator to order the set by (optionally <code>null</code>)
	 * @return the first matching layout page template entry
	 * @throws NoSuchPageTemplateEntryException if a matching layout page template entry could not be found
	 */
	public LayoutPageTemplateEntry findByG_L_First(
			long groupId, long layoutPageTemplateCollectionId,
			com.liferay.portal.kernel.util.OrderByComparator
				<LayoutPageTemplateEntry> orderByComparator)
		throws NoSuchPageTemplateEntryException;

	/**
	 * Returns the first layout page template entry in the ordered set where groupId = &#63; and layoutPageTemplateCollectionId = &#63;.
	 *
	 * @param groupId the group ID
	 * @param layoutPageTemplateCollectionId the layout page template collection ID
	 * @param orderByComparator the comparator to order the set by (optionally <code>null</code>)
	 * @return the first matching layout page template entry, or <code>null</code> if a matching layout page template entry could not be found
	 */
	public LayoutPageTemplateEntry fetchByG_L_First(
		long groupId, long layoutPageTemplateCollectionId,
		com.liferay.portal.kernel.util.OrderByComparator
			<LayoutPageTemplateEntry> orderByComparator);

	/**
	 * Returns an ordered range of all the layout page template entries that the user has permissions to view where groupId = &#63; and layoutPageTemplateCollectionId = &#63;.
	 *
	 * <p>
	 * Useful when paginating results. Returns a maximum of <code>end - start</code> instances. <code>start</code> and <code>end</code> are not primary keys, they are indexes in the result set. Thus, <code>0</code> refers to the first result in the set. Setting both <code>start</code> and <code>end</code> to <code>com.liferay.portal.kernel.dao.orm.QueryUtil#ALL_POS</code> will return the full result set. If <code>orderByComparator</code> is specified, then the query will include the given ORDER BY logic. If <code>orderByComparator</code> is absent, then the query will include the default ORDER BY logic from <code>com.liferay.layout.page.template.model.impl.LayoutPageTemplateEntryModelImpl</code>.
	 * </p>
	 *
	 * @param groupId the group ID
	 * @param layoutPageTemplateCollectionId the layout page template collection ID
	 * @param start the lower bound of the range of layout page template entries
	 * @param end the upper bound of the range of layout page template entries (not inclusive)
	 * @param orderByComparator the comparator to order the results by (optionally <code>null</code>)
	 * @return the ordered range of matching layout page template entries that the user has permission to view
	 */
	public java.util.List<LayoutPageTemplateEntry> filterFindByG_L(
		long groupId, long layoutPageTemplateCollectionId, int start, int end,
		com.liferay.portal.kernel.util.OrderByComparator
			<LayoutPageTemplateEntry> orderByComparator);

	/**
	 * Removes all the layout page template entries where groupId = &#63; and layoutPageTemplateCollectionId = &#63; from the database.
	 *
	 * @param groupId the group ID
	 * @param layoutPageTemplateCollectionId the layout page template collection ID
	 */
	public void removeByG_L(long groupId, long layoutPageTemplateCollectionId);

	/**
	 * Returns the number of layout page template entries where groupId = &#63; and layoutPageTemplateCollectionId = &#63;.
	 *
	 * @param groupId the group ID
	 * @param layoutPageTemplateCollectionId the layout page template collection ID
	 * @return the number of matching layout page template entries
	 */
	public int countByG_L(long groupId, long layoutPageTemplateCollectionId);

	/**
	 * Returns the number of layout page template entries that the user has permission to view where groupId = &#63; and layoutPageTemplateCollectionId = &#63;.
	 *
	 * @param groupId the group ID
	 * @param layoutPageTemplateCollectionId the layout page template collection ID
	 * @return the number of matching layout page template entries that the user has permission to view
	 */
	public int filterCountByG_L(
		long groupId, long layoutPageTemplateCollectionId);

	/**
	 * Returns the layout page template entry where groupId = &#63; and layoutPageTemplateEntryKey = &#63; or throws a <code>NoSuchPageTemplateEntryException</code> if it could not be found.
	 *
	 * @param groupId the group ID
	 * @param layoutPageTemplateEntryKey the layout page template entry key
	 * @return the matching layout page template entry
	 * @throws NoSuchPageTemplateEntryException if a matching layout page template entry could not be found
	 */
	public LayoutPageTemplateEntry findByG_LPTEK(
			long groupId, String layoutPageTemplateEntryKey)
		throws NoSuchPageTemplateEntryException;

	/**
	 * Returns the layout page template entry where groupId = &#63; and layoutPageTemplateEntryKey = &#63; or returns <code>null</code> if it could not be found, optionally using the finder cache.
	 *
	 * @param groupId the group ID
	 * @param layoutPageTemplateEntryKey the layout page template entry key
	 * @param useFinderCache whether to use the finder cache
	 * @return the matching layout page template entry, or <code>null</code> if a matching layout page template entry could not be found
	 */
	public LayoutPageTemplateEntry fetchByG_LPTEK(
		long groupId, String layoutPageTemplateEntryKey,
		boolean useFinderCache);

	/**
	 * Removes the layout page template entry where groupId = &#63; and layoutPageTemplateEntryKey = &#63; from the database.
	 *
	 * @param groupId the group ID
	 * @param layoutPageTemplateEntryKey the layout page template entry key
	 * @return the layout page template entry that was removed
	 */
	public LayoutPageTemplateEntry removeByG_LPTEK(
			long groupId, String layoutPageTemplateEntryKey)
		throws NoSuchPageTemplateEntryException;

	/**
	 * Returns the number of layout page template entries where groupId = &#63; and layoutPageTemplateEntryKey = &#63;.
	 *
	 * @param groupId the group ID
	 * @param layoutPageTemplateEntryKey the layout page template entry key
	 * @return the number of matching layout page template entries
	 */
	public int countByG_LPTEK(long groupId, String layoutPageTemplateEntryKey);

	/**
	 * Returns an ordered range of all the layout page template entries where groupId = &#63; and name = &#63;.
	 *
	 * <p>
	 * Useful when paginating results. Returns a maximum of <code>end - start</code> instances. <code>start</code> and <code>end</code> are not primary keys, they are indexes in the result set. Thus, <code>0</code> refers to the first result in the set. Setting both <code>start</code> and <code>end</code> to <code>com.liferay.portal.kernel.dao.orm.QueryUtil#ALL_POS</code> will return the full result set. If <code>orderByComparator</code> is specified, then the query will include the given ORDER BY logic. If <code>orderByComparator</code> is absent, then the query will include the default ORDER BY logic from <code>com.liferay.layout.page.template.model.impl.LayoutPageTemplateEntryModelImpl</code>.
	 * </p>
	 *
	 * @param groupId the group ID
	 * @param name the name
	 * @param start the lower bound of the range of layout page template entries
	 * @param end the upper bound of the range of layout page template entries (not inclusive)
	 * @param orderByComparator the comparator to order the results by (optionally <code>null</code>)
	 * @param useFinderCache whether to use the finder cache
	 * @return the ordered range of matching layout page template entries
	 */
	public java.util.List<LayoutPageTemplateEntry> findByG_N(
		long groupId, String name, int start, int end,
		com.liferay.portal.kernel.util.OrderByComparator
			<LayoutPageTemplateEntry> orderByComparator,
		boolean useFinderCache);

	/**
	 * Returns the first layout page template entry in the ordered set where groupId = &#63; and name = &#63;.
	 *
	 * @param groupId the group ID
	 * @param name the name
	 * @param orderByComparator the comparator to order the set by (optionally <code>null</code>)
	 * @return the first matching layout page template entry
	 * @throws NoSuchPageTemplateEntryException if a matching layout page template entry could not be found
	 */
	public LayoutPageTemplateEntry findByG_N_First(
			long groupId, String name,
			com.liferay.portal.kernel.util.OrderByComparator
				<LayoutPageTemplateEntry> orderByComparator)
		throws NoSuchPageTemplateEntryException;

	/**
	 * Returns the first layout page template entry in the ordered set where groupId = &#63; and name = &#63;.
	 *
	 * @param groupId the group ID
	 * @param name the name
	 * @param orderByComparator the comparator to order the set by (optionally <code>null</code>)
	 * @return the first matching layout page template entry, or <code>null</code> if a matching layout page template entry could not be found
	 */
	public LayoutPageTemplateEntry fetchByG_N_First(
		long groupId, String name,
		com.liferay.portal.kernel.util.OrderByComparator
			<LayoutPageTemplateEntry> orderByComparator);

	/**
	 * Returns an ordered range of all the layout page template entries that the user has permissions to view where groupId = &#63; and name = &#63;.
	 *
	 * <p>
	 * Useful when paginating results. Returns a maximum of <code>end - start</code> instances. <code>start</code> and <code>end</code> are not primary keys, they are indexes in the result set. Thus, <code>0</code> refers to the first result in the set. Setting both <code>start</code> and <code>end</code> to <code>com.liferay.portal.kernel.dao.orm.QueryUtil#ALL_POS</code> will return the full result set. If <code>orderByComparator</code> is specified, then the query will include the given ORDER BY logic. If <code>orderByComparator</code> is absent, then the query will include the default ORDER BY logic from <code>com.liferay.layout.page.template.model.impl.LayoutPageTemplateEntryModelImpl</code>.
	 * </p>
	 *
	 * @param groupId the group ID
	 * @param name the name
	 * @param start the lower bound of the range of layout page template entries
	 * @param end the upper bound of the range of layout page template entries (not inclusive)
	 * @param orderByComparator the comparator to order the results by (optionally <code>null</code>)
	 * @return the ordered range of matching layout page template entries that the user has permission to view
	 */
	public java.util.List<LayoutPageTemplateEntry> filterFindByG_N(
		long groupId, String name, int start, int end,
		com.liferay.portal.kernel.util.OrderByComparator
			<LayoutPageTemplateEntry> orderByComparator);

	/**
	 * Removes all the layout page template entries where groupId = &#63; and name = &#63; from the database.
	 *
	 * @param groupId the group ID
	 * @param name the name
	 */
	public void removeByG_N(long groupId, String name);

	/**
	 * Returns the number of layout page template entries where groupId = &#63; and name = &#63;.
	 *
	 * @param groupId the group ID
	 * @param name the name
	 * @return the number of matching layout page template entries
	 */
	public int countByG_N(long groupId, String name);

	/**
	 * Returns the number of layout page template entries that the user has permission to view where groupId = &#63; and name = &#63;.
	 *
	 * @param groupId the group ID
	 * @param name the name
	 * @return the number of matching layout page template entries that the user has permission to view
	 */
	public int filterCountByG_N(long groupId, String name);

	/**
	 * Returns an ordered range of all the layout page template entries where groupId = &#63; and type = &#63;.
	 *
	 * <p>
	 * Useful when paginating results. Returns a maximum of <code>end - start</code> instances. <code>start</code> and <code>end</code> are not primary keys, they are indexes in the result set. Thus, <code>0</code> refers to the first result in the set. Setting both <code>start</code> and <code>end</code> to <code>com.liferay.portal.kernel.dao.orm.QueryUtil#ALL_POS</code> will return the full result set. If <code>orderByComparator</code> is specified, then the query will include the given ORDER BY logic. If <code>orderByComparator</code> is absent, then the query will include the default ORDER BY logic from <code>com.liferay.layout.page.template.model.impl.LayoutPageTemplateEntryModelImpl</code>.
	 * </p>
	 *
	 * @param groupId the group ID
	 * @param type the type
	 * @param start the lower bound of the range of layout page template entries
	 * @param end the upper bound of the range of layout page template entries (not inclusive)
	 * @param orderByComparator the comparator to order the results by (optionally <code>null</code>)
	 * @param useFinderCache whether to use the finder cache
	 * @return the ordered range of matching layout page template entries
	 */
	public java.util.List<LayoutPageTemplateEntry> findByG_T(
		long groupId, int type, int start, int end,
		com.liferay.portal.kernel.util.OrderByComparator
			<LayoutPageTemplateEntry> orderByComparator,
		boolean useFinderCache);

	/**
	 * Returns the first layout page template entry in the ordered set where groupId = &#63; and type = &#63;.
	 *
	 * @param groupId the group ID
	 * @param type the type
	 * @param orderByComparator the comparator to order the set by (optionally <code>null</code>)
	 * @return the first matching layout page template entry
	 * @throws NoSuchPageTemplateEntryException if a matching layout page template entry could not be found
	 */
	public LayoutPageTemplateEntry findByG_T_First(
			long groupId, int type,
			com.liferay.portal.kernel.util.OrderByComparator
				<LayoutPageTemplateEntry> orderByComparator)
		throws NoSuchPageTemplateEntryException;

	/**
	 * Returns the first layout page template entry in the ordered set where groupId = &#63; and type = &#63;.
	 *
	 * @param groupId the group ID
	 * @param type the type
	 * @param orderByComparator the comparator to order the set by (optionally <code>null</code>)
	 * @return the first matching layout page template entry, or <code>null</code> if a matching layout page template entry could not be found
	 */
	public LayoutPageTemplateEntry fetchByG_T_First(
		long groupId, int type,
		com.liferay.portal.kernel.util.OrderByComparator
			<LayoutPageTemplateEntry> orderByComparator);

	/**
	 * Returns an ordered range of all the layout page template entries that the user has permissions to view where groupId = &#63; and type = &#63;.
	 *
	 * <p>
	 * Useful when paginating results. Returns a maximum of <code>end - start</code> instances. <code>start</code> and <code>end</code> are not primary keys, they are indexes in the result set. Thus, <code>0</code> refers to the first result in the set. Setting both <code>start</code> and <code>end</code> to <code>com.liferay.portal.kernel.dao.orm.QueryUtil#ALL_POS</code> will return the full result set. If <code>orderByComparator</code> is specified, then the query will include the given ORDER BY logic. If <code>orderByComparator</code> is absent, then the query will include the default ORDER BY logic from <code>com.liferay.layout.page.template.model.impl.LayoutPageTemplateEntryModelImpl</code>.
	 * </p>
	 *
	 * @param groupId the group ID
	 * @param type the type
	 * @param start the lower bound of the range of layout page template entries
	 * @param end the upper bound of the range of layout page template entries (not inclusive)
	 * @param orderByComparator the comparator to order the results by (optionally <code>null</code>)
	 * @return the ordered range of matching layout page template entries that the user has permission to view
	 */
	public java.util.List<LayoutPageTemplateEntry> filterFindByG_T(
		long groupId, int type, int start, int end,
		com.liferay.portal.kernel.util.OrderByComparator
			<LayoutPageTemplateEntry> orderByComparator);

	/**
	 * Returns an ordered range of all the layout page template entries that the user has permission to view where groupId = &#63; and type = any &#63;.
	 *
	 * <p>
	 * Useful when paginating results. Returns a maximum of <code>end - start</code> instances. <code>start</code> and <code>end</code> are not primary keys, they are indexes in the result set. Thus, <code>0</code> refers to the first result in the set. Setting both <code>start</code> and <code>end</code> to <code>com.liferay.portal.kernel.dao.orm.QueryUtil#ALL_POS</code> will return the full result set. If <code>orderByComparator</code> is specified, then the query will include the given ORDER BY logic. If <code>orderByComparator</code> is absent, then the query will include the default ORDER BY logic from <code>com.liferay.layout.page.template.model.impl.LayoutPageTemplateEntryModelImpl</code>.
	 * </p>
	 *
	 * @param groupId the group ID
	 * @param types the types
	 * @param start the lower bound of the range of layout page template entries
	 * @param end the upper bound of the range of layout page template entries (not inclusive)
	 * @param orderByComparator the comparator to order the results by (optionally <code>null</code>)
	 * @return the ordered range of matching layout page template entries that the user has permission to view
	 */
	public java.util.List<LayoutPageTemplateEntry> filterFindByG_T(
		long groupId, int[] types, int start, int end,
		com.liferay.portal.kernel.util.OrderByComparator
			<LayoutPageTemplateEntry> orderByComparator);

	/**
	 * Returns an ordered range of all the layout page template entries where groupId = &#63; and type = &#63;, optionally using the finder cache.
	 *
	 * <p>
	 * Useful when paginating results. Returns a maximum of <code>end - start</code> instances. <code>start</code> and <code>end</code> are not primary keys, they are indexes in the result set. Thus, <code>0</code> refers to the first result in the set. Setting both <code>start</code> and <code>end</code> to <code>com.liferay.portal.kernel.dao.orm.QueryUtil#ALL_POS</code> will return the full result set. If <code>orderByComparator</code> is specified, then the query will include the given ORDER BY logic. If <code>orderByComparator</code> is absent, then the query will include the default ORDER BY logic from <code>com.liferay.layout.page.template.model.impl.LayoutPageTemplateEntryModelImpl</code>.
	 * </p>
	 *
	 * @param groupId the group ID
	 * @param types the types
	 * @param start the lower bound of the range of layout page template entries
	 * @param end the upper bound of the range of layout page template entries (not inclusive)
	 * @param orderByComparator the comparator to order the results by (optionally <code>null</code>)
	 * @param useFinderCache whether to use the finder cache
	 * @return the ordered range of matching layout page template entries
	 */
	public java.util.List<LayoutPageTemplateEntry> findByG_T(
		long groupId, int[] types, int start, int end,
		com.liferay.portal.kernel.util.OrderByComparator
			<LayoutPageTemplateEntry> orderByComparator,
		boolean useFinderCache);

	/**
	 * Removes all the layout page template entries where groupId = &#63; and type = &#63; from the database.
	 *
	 * @param groupId the group ID
	 * @param type the type
	 */
	public void removeByG_T(long groupId, int type);

	/**
	 * Returns the number of layout page template entries where groupId = &#63; and type = &#63;.
	 *
	 * @param groupId the group ID
	 * @param type the type
	 * @return the number of matching layout page template entries
	 */
	public int countByG_T(long groupId, int type);

	/**
	 * Returns the number of layout page template entries where groupId = &#63; and type = any &#63;.
	 *
	 * @param groupId the group ID
	 * @param types the types
	 * @return the number of matching layout page template entries
	 */
	public int countByG_T(long groupId, int[] types);

	/**
	 * Returns the number of layout page template entries that the user has permission to view where groupId = &#63; and type = &#63;.
	 *
	 * @param groupId the group ID
	 * @param type the type
	 * @return the number of matching layout page template entries that the user has permission to view
	 */
	public int filterCountByG_T(long groupId, int type);

	/**
	 * Returns the number of layout page template entries that the user has permission to view where groupId = &#63; and type = any &#63;.
	 *
	 * @param groupId the group ID
	 * @param types the types
	 * @return the number of matching layout page template entries that the user has permission to view
	 */
	public int filterCountByG_T(long groupId, int[] types);

	/**
	 * Returns an ordered range of all the layout page template entries where groupId = &#63; and status = &#63;.
	 *
	 * <p>
	 * Useful when paginating results. Returns a maximum of <code>end - start</code> instances. <code>start</code> and <code>end</code> are not primary keys, they are indexes in the result set. Thus, <code>0</code> refers to the first result in the set. Setting both <code>start</code> and <code>end</code> to <code>com.liferay.portal.kernel.dao.orm.QueryUtil#ALL_POS</code> will return the full result set. If <code>orderByComparator</code> is specified, then the query will include the given ORDER BY logic. If <code>orderByComparator</code> is absent, then the query will include the default ORDER BY logic from <code>com.liferay.layout.page.template.model.impl.LayoutPageTemplateEntryModelImpl</code>.
	 * </p>
	 *
	 * @param groupId the group ID
	 * @param status the status
	 * @param start the lower bound of the range of layout page template entries
	 * @param end the upper bound of the range of layout page template entries (not inclusive)
	 * @param orderByComparator the comparator to order the results by (optionally <code>null</code>)
	 * @param useFinderCache whether to use the finder cache
	 * @return the ordered range of matching layout page template entries
	 */
	public java.util.List<LayoutPageTemplateEntry> findByG_S(
		long groupId, int status, int start, int end,
		com.liferay.portal.kernel.util.OrderByComparator
			<LayoutPageTemplateEntry> orderByComparator,
		boolean useFinderCache);

	/**
	 * Returns the first layout page template entry in the ordered set where groupId = &#63; and status = &#63;.
	 *
	 * @param groupId the group ID
	 * @param status the status
	 * @param orderByComparator the comparator to order the set by (optionally <code>null</code>)
	 * @return the first matching layout page template entry
	 * @throws NoSuchPageTemplateEntryException if a matching layout page template entry could not be found
	 */
	public LayoutPageTemplateEntry findByG_S_First(
			long groupId, int status,
			com.liferay.portal.kernel.util.OrderByComparator
				<LayoutPageTemplateEntry> orderByComparator)
		throws NoSuchPageTemplateEntryException;

	/**
	 * Returns the first layout page template entry in the ordered set where groupId = &#63; and status = &#63;.
	 *
	 * @param groupId the group ID
	 * @param status the status
	 * @param orderByComparator the comparator to order the set by (optionally <code>null</code>)
	 * @return the first matching layout page template entry, or <code>null</code> if a matching layout page template entry could not be found
	 */
	public LayoutPageTemplateEntry fetchByG_S_First(
		long groupId, int status,
		com.liferay.portal.kernel.util.OrderByComparator
			<LayoutPageTemplateEntry> orderByComparator);

	/**
	 * Returns an ordered range of all the layout page template entries that the user has permissions to view where groupId = &#63; and status = &#63;.
	 *
	 * <p>
	 * Useful when paginating results. Returns a maximum of <code>end - start</code> instances. <code>start</code> and <code>end</code> are not primary keys, they are indexes in the result set. Thus, <code>0</code> refers to the first result in the set. Setting both <code>start</code> and <code>end</code> to <code>com.liferay.portal.kernel.dao.orm.QueryUtil#ALL_POS</code> will return the full result set. If <code>orderByComparator</code> is specified, then the query will include the given ORDER BY logic. If <code>orderByComparator</code> is absent, then the query will include the default ORDER BY logic from <code>com.liferay.layout.page.template.model.impl.LayoutPageTemplateEntryModelImpl</code>.
	 * </p>
	 *
	 * @param groupId the group ID
	 * @param status the status
	 * @param start the lower bound of the range of layout page template entries
	 * @param end the upper bound of the range of layout page template entries (not inclusive)
	 * @param orderByComparator the comparator to order the results by (optionally <code>null</code>)
	 * @return the ordered range of matching layout page template entries that the user has permission to view
	 */
	public java.util.List<LayoutPageTemplateEntry> filterFindByG_S(
		long groupId, int status, int start, int end,
		com.liferay.portal.kernel.util.OrderByComparator
			<LayoutPageTemplateEntry> orderByComparator);

	/**
	 * Removes all the layout page template entries where groupId = &#63; and status = &#63; from the database.
	 *
	 * @param groupId the group ID
	 * @param status the status
	 */
	public void removeByG_S(long groupId, int status);

	/**
	 * Returns the number of layout page template entries where groupId = &#63; and status = &#63;.
	 *
	 * @param groupId the group ID
	 * @param status the status
	 * @return the number of matching layout page template entries
	 */
	public int countByG_S(long groupId, int status);

	/**
	 * Returns the number of layout page template entries that the user has permission to view where groupId = &#63; and status = &#63;.
	 *
	 * @param groupId the group ID
	 * @param status the status
	 * @return the number of matching layout page template entries that the user has permission to view
	 */
	public int filterCountByG_S(long groupId, int status);

	/**
	 * Returns all the layout page template entries where groupId = &#63; and layoutPageTemplateCollectionId = &#63; and name LIKE &#63;.
	 *
	 * @param groupId the group ID
	 * @param layoutPageTemplateCollectionId the layout page template collection ID
	 * @param name the name
	 * @return the matching layout page template entries
	 */
	public java.util.List<LayoutPageTemplateEntry> findByG_L_LikeN(
		long groupId, long layoutPageTemplateCollectionId, String name);

	/**
	 * Returns a range of all the layout page template entries where groupId = &#63; and layoutPageTemplateCollectionId = &#63; and name LIKE &#63;.
	 *
	 * <p>
	 * Useful when paginating results. Returns a maximum of <code>end - start</code> instances. <code>start</code> and <code>end</code> are not primary keys, they are indexes in the result set. Thus, <code>0</code> refers to the first result in the set. Setting both <code>start</code> and <code>end</code> to <code>com.liferay.portal.kernel.dao.orm.QueryUtil#ALL_POS</code> will return the full result set. If <code>orderByComparator</code> is specified, then the query will include the given ORDER BY logic. If <code>orderByComparator</code> is absent, then the query will include the default ORDER BY logic from <code>com.liferay.layout.page.template.model.impl.LayoutPageTemplateEntryModelImpl</code>.
	 * </p>
	 *
	 * @param groupId the group ID
	 * @param layoutPageTemplateCollectionId the layout page template collection ID
	 * @param name the name
	 * @param start the lower bound of the range of layout page template entries
	 * @param end the upper bound of the range of layout page template entries (not inclusive)
	 * @return the range of matching layout page template entries
	 */
	public java.util.List<LayoutPageTemplateEntry> findByG_L_LikeN(
		long groupId, long layoutPageTemplateCollectionId, String name,
		int start, int end);

	/**
	 * Returns an ordered range of all the layout page template entries where groupId = &#63; and layoutPageTemplateCollectionId = &#63; and name LIKE &#63;.
	 *
	 * <p>
	 * Useful when paginating results. Returns a maximum of <code>end - start</code> instances. <code>start</code> and <code>end</code> are not primary keys, they are indexes in the result set. Thus, <code>0</code> refers to the first result in the set. Setting both <code>start</code> and <code>end</code> to <code>com.liferay.portal.kernel.dao.orm.QueryUtil#ALL_POS</code> will return the full result set. If <code>orderByComparator</code> is specified, then the query will include the given ORDER BY logic. If <code>orderByComparator</code> is absent, then the query will include the default ORDER BY logic from <code>com.liferay.layout.page.template.model.impl.LayoutPageTemplateEntryModelImpl</code>.
	 * </p>
	 *
	 * @param groupId the group ID
	 * @param layoutPageTemplateCollectionId the layout page template collection ID
	 * @param name the name
	 * @param start the lower bound of the range of layout page template entries
	 * @param end the upper bound of the range of layout page template entries (not inclusive)
	 * @param orderByComparator the comparator to order the results by (optionally <code>null</code>)
	 * @return the ordered range of matching layout page template entries
	 */
	public java.util.List<LayoutPageTemplateEntry> findByG_L_LikeN(
		long groupId, long layoutPageTemplateCollectionId, String name,
		int start, int end,
		com.liferay.portal.kernel.util.OrderByComparator
			<LayoutPageTemplateEntry> orderByComparator);

	/**
	 * Returns an ordered range of all the layout page template entries where groupId = &#63; and layoutPageTemplateCollectionId = &#63; and name LIKE &#63;.
	 *
	 * <p>
	 * Useful when paginating results. Returns a maximum of <code>end - start</code> instances. <code>start</code> and <code>end</code> are not primary keys, they are indexes in the result set. Thus, <code>0</code> refers to the first result in the set. Setting both <code>start</code> and <code>end</code> to <code>com.liferay.portal.kernel.dao.orm.QueryUtil#ALL_POS</code> will return the full result set. If <code>orderByComparator</code> is specified, then the query will include the given ORDER BY logic. If <code>orderByComparator</code> is absent, then the query will include the default ORDER BY logic from <code>com.liferay.layout.page.template.model.impl.LayoutPageTemplateEntryModelImpl</code>.
	 * </p>
	 *
	 * @param groupId the group ID
	 * @param layoutPageTemplateCollectionId the layout page template collection ID
	 * @param name the name
	 * @param start the lower bound of the range of layout page template entries
	 * @param end the upper bound of the range of layout page template entries (not inclusive)
	 * @param orderByComparator the comparator to order the results by (optionally <code>null</code>)
	 * @param useFinderCache whether to use the finder cache
	 * @return the ordered range of matching layout page template entries
	 */
	public java.util.List<LayoutPageTemplateEntry> findByG_L_LikeN(
		long groupId, long layoutPageTemplateCollectionId, String name,
		int start, int end,
		com.liferay.portal.kernel.util.OrderByComparator
			<LayoutPageTemplateEntry> orderByComparator,
		boolean useFinderCache);

	/**
	 * Returns the first layout page template entry in the ordered set where groupId = &#63; and layoutPageTemplateCollectionId = &#63; and name LIKE &#63;.
	 *
	 * @param groupId the group ID
	 * @param layoutPageTemplateCollectionId the layout page template collection ID
	 * @param name the name
	 * @param orderByComparator the comparator to order the set by (optionally <code>null</code>)
	 * @return the first matching layout page template entry
	 * @throws NoSuchPageTemplateEntryException if a matching layout page template entry could not be found
	 */
	public LayoutPageTemplateEntry findByG_L_LikeN_First(
			long groupId, long layoutPageTemplateCollectionId, String name,
			com.liferay.portal.kernel.util.OrderByComparator
				<LayoutPageTemplateEntry> orderByComparator)
		throws NoSuchPageTemplateEntryException;

	/**
	 * Returns the first layout page template entry in the ordered set where groupId = &#63; and layoutPageTemplateCollectionId = &#63; and name LIKE &#63;.
	 *
	 * @param groupId the group ID
	 * @param layoutPageTemplateCollectionId the layout page template collection ID
	 * @param name the name
	 * @param orderByComparator the comparator to order the set by (optionally <code>null</code>)
	 * @return the first matching layout page template entry, or <code>null</code> if a matching layout page template entry could not be found
	 */
	public LayoutPageTemplateEntry fetchByG_L_LikeN_First(
		long groupId, long layoutPageTemplateCollectionId, String name,
		com.liferay.portal.kernel.util.OrderByComparator
			<LayoutPageTemplateEntry> orderByComparator);

	/**
	 * Returns all the layout page template entries that the user has permission to view where groupId = &#63; and layoutPageTemplateCollectionId = &#63; and name LIKE &#63;.
	 *
	 * @param groupId the group ID
	 * @param layoutPageTemplateCollectionId the layout page template collection ID
	 * @param name the name
	 * @return the matching layout page template entries that the user has permission to view
	 */
	public java.util.List<LayoutPageTemplateEntry> filterFindByG_L_LikeN(
		long groupId, long layoutPageTemplateCollectionId, String name);

	/**
	 * Returns a range of all the layout page template entries that the user has permission to view where groupId = &#63; and layoutPageTemplateCollectionId = &#63; and name LIKE &#63;.
	 *
	 * <p>
	 * Useful when paginating results. Returns a maximum of <code>end - start</code> instances. <code>start</code> and <code>end</code> are not primary keys, they are indexes in the result set. Thus, <code>0</code> refers to the first result in the set. Setting both <code>start</code> and <code>end</code> to <code>com.liferay.portal.kernel.dao.orm.QueryUtil#ALL_POS</code> will return the full result set. If <code>orderByComparator</code> is specified, then the query will include the given ORDER BY logic. If <code>orderByComparator</code> is absent, then the query will include the default ORDER BY logic from <code>com.liferay.layout.page.template.model.impl.LayoutPageTemplateEntryModelImpl</code>.
	 * </p>
	 *
	 * @param groupId the group ID
	 * @param layoutPageTemplateCollectionId the layout page template collection ID
	 * @param name the name
	 * @param start the lower bound of the range of layout page template entries
	 * @param end the upper bound of the range of layout page template entries (not inclusive)
	 * @return the range of matching layout page template entries that the user has permission to view
	 */
	public java.util.List<LayoutPageTemplateEntry> filterFindByG_L_LikeN(
		long groupId, long layoutPageTemplateCollectionId, String name,
		int start, int end);

	/**
	 * Returns an ordered range of all the layout page template entries that the user has permissions to view where groupId = &#63; and layoutPageTemplateCollectionId = &#63; and name LIKE &#63;.
	 *
	 * <p>
	 * Useful when paginating results. Returns a maximum of <code>end - start</code> instances. <code>start</code> and <code>end</code> are not primary keys, they are indexes in the result set. Thus, <code>0</code> refers to the first result in the set. Setting both <code>start</code> and <code>end</code> to <code>com.liferay.portal.kernel.dao.orm.QueryUtil#ALL_POS</code> will return the full result set. If <code>orderByComparator</code> is specified, then the query will include the given ORDER BY logic. If <code>orderByComparator</code> is absent, then the query will include the default ORDER BY logic from <code>com.liferay.layout.page.template.model.impl.LayoutPageTemplateEntryModelImpl</code>.
	 * </p>
	 *
	 * @param groupId the group ID
	 * @param layoutPageTemplateCollectionId the layout page template collection ID
	 * @param name the name
	 * @param start the lower bound of the range of layout page template entries
	 * @param end the upper bound of the range of layout page template entries (not inclusive)
	 * @param orderByComparator the comparator to order the results by (optionally <code>null</code>)
	 * @return the ordered range of matching layout page template entries that the user has permission to view
	 */
	public java.util.List<LayoutPageTemplateEntry> filterFindByG_L_LikeN(
		long groupId, long layoutPageTemplateCollectionId, String name,
		int start, int end,
		com.liferay.portal.kernel.util.OrderByComparator
			<LayoutPageTemplateEntry> orderByComparator);

	/**
	 * Removes all the layout page template entries where groupId = &#63; and layoutPageTemplateCollectionId = &#63; and name LIKE &#63; from the database.
	 *
	 * @param groupId the group ID
	 * @param layoutPageTemplateCollectionId the layout page template collection ID
	 * @param name the name
	 */
	public void removeByG_L_LikeN(
		long groupId, long layoutPageTemplateCollectionId, String name);

	/**
	 * Returns the number of layout page template entries where groupId = &#63; and layoutPageTemplateCollectionId = &#63; and name LIKE &#63;.
	 *
	 * @param groupId the group ID
	 * @param layoutPageTemplateCollectionId the layout page template collection ID
	 * @param name the name
	 * @return the number of matching layout page template entries
	 */
	public int countByG_L_LikeN(
		long groupId, long layoutPageTemplateCollectionId, String name);

	/**
	 * Returns the number of layout page template entries that the user has permission to view where groupId = &#63; and layoutPageTemplateCollectionId = &#63; and name LIKE &#63;.
	 *
	 * @param groupId the group ID
	 * @param layoutPageTemplateCollectionId the layout page template collection ID
	 * @param name the name
	 * @return the number of matching layout page template entries that the user has permission to view
	 */
	public int filterCountByG_L_LikeN(
		long groupId, long layoutPageTemplateCollectionId, String name);

	/**
	 * Returns an ordered range of all the layout page template entries where groupId = &#63; and layoutPageTemplateCollectionId = &#63; and type = &#63;.
	 *
	 * <p>
	 * Useful when paginating results. Returns a maximum of <code>end - start</code> instances. <code>start</code> and <code>end</code> are not primary keys, they are indexes in the result set. Thus, <code>0</code> refers to the first result in the set. Setting both <code>start</code> and <code>end</code> to <code>com.liferay.portal.kernel.dao.orm.QueryUtil#ALL_POS</code> will return the full result set. If <code>orderByComparator</code> is specified, then the query will include the given ORDER BY logic. If <code>orderByComparator</code> is absent, then the query will include the default ORDER BY logic from <code>com.liferay.layout.page.template.model.impl.LayoutPageTemplateEntryModelImpl</code>.
	 * </p>
	 *
	 * @param groupId the group ID
	 * @param layoutPageTemplateCollectionId the layout page template collection ID
	 * @param type the type
	 * @param start the lower bound of the range of layout page template entries
	 * @param end the upper bound of the range of layout page template entries (not inclusive)
	 * @param orderByComparator the comparator to order the results by (optionally <code>null</code>)
	 * @param useFinderCache whether to use the finder cache
	 * @return the ordered range of matching layout page template entries
	 */
	public java.util.List<LayoutPageTemplateEntry> findByG_L_T(
		long groupId, long layoutPageTemplateCollectionId, int type, int start,
		int end,
		com.liferay.portal.kernel.util.OrderByComparator
			<LayoutPageTemplateEntry> orderByComparator,
		boolean useFinderCache);

	/**
	 * Returns the first layout page template entry in the ordered set where groupId = &#63; and layoutPageTemplateCollectionId = &#63; and type = &#63;.
	 *
	 * @param groupId the group ID
	 * @param layoutPageTemplateCollectionId the layout page template collection ID
	 * @param type the type
	 * @param orderByComparator the comparator to order the set by (optionally <code>null</code>)
	 * @return the first matching layout page template entry
	 * @throws NoSuchPageTemplateEntryException if a matching layout page template entry could not be found
	 */
	public LayoutPageTemplateEntry findByG_L_T_First(
			long groupId, long layoutPageTemplateCollectionId, int type,
			com.liferay.portal.kernel.util.OrderByComparator
				<LayoutPageTemplateEntry> orderByComparator)
		throws NoSuchPageTemplateEntryException;

	/**
	 * Returns the first layout page template entry in the ordered set where groupId = &#63; and layoutPageTemplateCollectionId = &#63; and type = &#63;.
	 *
	 * @param groupId the group ID
	 * @param layoutPageTemplateCollectionId the layout page template collection ID
	 * @param type the type
	 * @param orderByComparator the comparator to order the set by (optionally <code>null</code>)
	 * @return the first matching layout page template entry, or <code>null</code> if a matching layout page template entry could not be found
	 */
	public LayoutPageTemplateEntry fetchByG_L_T_First(
		long groupId, long layoutPageTemplateCollectionId, int type,
		com.liferay.portal.kernel.util.OrderByComparator
			<LayoutPageTemplateEntry> orderByComparator);

	/**
	 * Returns an ordered range of all the layout page template entries that the user has permissions to view where groupId = &#63; and layoutPageTemplateCollectionId = &#63; and type = &#63;.
	 *
	 * <p>
	 * Useful when paginating results. Returns a maximum of <code>end - start</code> instances. <code>start</code> and <code>end</code> are not primary keys, they are indexes in the result set. Thus, <code>0</code> refers to the first result in the set. Setting both <code>start</code> and <code>end</code> to <code>com.liferay.portal.kernel.dao.orm.QueryUtil#ALL_POS</code> will return the full result set. If <code>orderByComparator</code> is specified, then the query will include the given ORDER BY logic. If <code>orderByComparator</code> is absent, then the query will include the default ORDER BY logic from <code>com.liferay.layout.page.template.model.impl.LayoutPageTemplateEntryModelImpl</code>.
	 * </p>
	 *
	 * @param groupId the group ID
	 * @param layoutPageTemplateCollectionId the layout page template collection ID
	 * @param type the type
	 * @param start the lower bound of the range of layout page template entries
	 * @param end the upper bound of the range of layout page template entries (not inclusive)
	 * @param orderByComparator the comparator to order the results by (optionally <code>null</code>)
	 * @return the ordered range of matching layout page template entries that the user has permission to view
	 */
	public java.util.List<LayoutPageTemplateEntry> filterFindByG_L_T(
		long groupId, long layoutPageTemplateCollectionId, int type, int start,
		int end,
		com.liferay.portal.kernel.util.OrderByComparator
			<LayoutPageTemplateEntry> orderByComparator);

	/**
	 * Removes all the layout page template entries where groupId = &#63; and layoutPageTemplateCollectionId = &#63; and type = &#63; from the database.
	 *
	 * @param groupId the group ID
	 * @param layoutPageTemplateCollectionId the layout page template collection ID
	 * @param type the type
	 */
	public void removeByG_L_T(
		long groupId, long layoutPageTemplateCollectionId, int type);

	/**
	 * Returns the number of layout page template entries where groupId = &#63; and layoutPageTemplateCollectionId = &#63; and type = &#63;.
	 *
	 * @param groupId the group ID
	 * @param layoutPageTemplateCollectionId the layout page template collection ID
	 * @param type the type
	 * @return the number of matching layout page template entries
	 */
	public int countByG_L_T(
		long groupId, long layoutPageTemplateCollectionId, int type);

	/**
	 * Returns the number of layout page template entries that the user has permission to view where groupId = &#63; and layoutPageTemplateCollectionId = &#63; and type = &#63;.
	 *
	 * @param groupId the group ID
	 * @param layoutPageTemplateCollectionId the layout page template collection ID
	 * @param type the type
	 * @return the number of matching layout page template entries that the user has permission to view
	 */
	public int filterCountByG_L_T(
		long groupId, long layoutPageTemplateCollectionId, int type);

	/**
	 * Returns an ordered range of all the layout page template entries where groupId = &#63; and layoutPageTemplateCollectionId = &#63; and status = &#63;.
	 *
	 * <p>
	 * Useful when paginating results. Returns a maximum of <code>end - start</code> instances. <code>start</code> and <code>end</code> are not primary keys, they are indexes in the result set. Thus, <code>0</code> refers to the first result in the set. Setting both <code>start</code> and <code>end</code> to <code>com.liferay.portal.kernel.dao.orm.QueryUtil#ALL_POS</code> will return the full result set. If <code>orderByComparator</code> is specified, then the query will include the given ORDER BY logic. If <code>orderByComparator</code> is absent, then the query will include the default ORDER BY logic from <code>com.liferay.layout.page.template.model.impl.LayoutPageTemplateEntryModelImpl</code>.
	 * </p>
	 *
	 * @param groupId the group ID
	 * @param layoutPageTemplateCollectionId the layout page template collection ID
	 * @param status the status
	 * @param start the lower bound of the range of layout page template entries
	 * @param end the upper bound of the range of layout page template entries (not inclusive)
	 * @param orderByComparator the comparator to order the results by (optionally <code>null</code>)
	 * @param useFinderCache whether to use the finder cache
	 * @return the ordered range of matching layout page template entries
	 */
	public java.util.List<LayoutPageTemplateEntry> findByG_L_S(
		long groupId, long layoutPageTemplateCollectionId, int status,
		int start, int end,
		com.liferay.portal.kernel.util.OrderByComparator
			<LayoutPageTemplateEntry> orderByComparator,
		boolean useFinderCache);

	/**
	 * Returns the first layout page template entry in the ordered set where groupId = &#63; and layoutPageTemplateCollectionId = &#63; and status = &#63;.
	 *
	 * @param groupId the group ID
	 * @param layoutPageTemplateCollectionId the layout page template collection ID
	 * @param status the status
	 * @param orderByComparator the comparator to order the set by (optionally <code>null</code>)
	 * @return the first matching layout page template entry
	 * @throws NoSuchPageTemplateEntryException if a matching layout page template entry could not be found
	 */
	public LayoutPageTemplateEntry findByG_L_S_First(
			long groupId, long layoutPageTemplateCollectionId, int status,
			com.liferay.portal.kernel.util.OrderByComparator
				<LayoutPageTemplateEntry> orderByComparator)
		throws NoSuchPageTemplateEntryException;

	/**
	 * Returns the first layout page template entry in the ordered set where groupId = &#63; and layoutPageTemplateCollectionId = &#63; and status = &#63;.
	 *
	 * @param groupId the group ID
	 * @param layoutPageTemplateCollectionId the layout page template collection ID
	 * @param status the status
	 * @param orderByComparator the comparator to order the set by (optionally <code>null</code>)
	 * @return the first matching layout page template entry, or <code>null</code> if a matching layout page template entry could not be found
	 */
	public LayoutPageTemplateEntry fetchByG_L_S_First(
		long groupId, long layoutPageTemplateCollectionId, int status,
		com.liferay.portal.kernel.util.OrderByComparator
			<LayoutPageTemplateEntry> orderByComparator);

	/**
	 * Returns an ordered range of all the layout page template entries that the user has permissions to view where groupId = &#63; and layoutPageTemplateCollectionId = &#63; and status = &#63;.
	 *
	 * <p>
	 * Useful when paginating results. Returns a maximum of <code>end - start</code> instances. <code>start</code> and <code>end</code> are not primary keys, they are indexes in the result set. Thus, <code>0</code> refers to the first result in the set. Setting both <code>start</code> and <code>end</code> to <code>com.liferay.portal.kernel.dao.orm.QueryUtil#ALL_POS</code> will return the full result set. If <code>orderByComparator</code> is specified, then the query will include the given ORDER BY logic. If <code>orderByComparator</code> is absent, then the query will include the default ORDER BY logic from <code>com.liferay.layout.page.template.model.impl.LayoutPageTemplateEntryModelImpl</code>.
	 * </p>
	 *
	 * @param groupId the group ID
	 * @param layoutPageTemplateCollectionId the layout page template collection ID
	 * @param status the status
	 * @param start the lower bound of the range of layout page template entries
	 * @param end the upper bound of the range of layout page template entries (not inclusive)
	 * @param orderByComparator the comparator to order the results by (optionally <code>null</code>)
	 * @return the ordered range of matching layout page template entries that the user has permission to view
	 */
	public java.util.List<LayoutPageTemplateEntry> filterFindByG_L_S(
		long groupId, long layoutPageTemplateCollectionId, int status,
		int start, int end,
		com.liferay.portal.kernel.util.OrderByComparator
			<LayoutPageTemplateEntry> orderByComparator);

	/**
	 * Removes all the layout page template entries where groupId = &#63; and layoutPageTemplateCollectionId = &#63; and status = &#63; from the database.
	 *
	 * @param groupId the group ID
	 * @param layoutPageTemplateCollectionId the layout page template collection ID
	 * @param status the status
	 */
	public void removeByG_L_S(
		long groupId, long layoutPageTemplateCollectionId, int status);

	/**
	 * Returns the number of layout page template entries where groupId = &#63; and layoutPageTemplateCollectionId = &#63; and status = &#63;.
	 *
	 * @param groupId the group ID
	 * @param layoutPageTemplateCollectionId the layout page template collection ID
	 * @param status the status
	 * @return the number of matching layout page template entries
	 */
	public int countByG_L_S(
		long groupId, long layoutPageTemplateCollectionId, int status);

	/**
	 * Returns the number of layout page template entries that the user has permission to view where groupId = &#63; and layoutPageTemplateCollectionId = &#63; and status = &#63;.
	 *
	 * @param groupId the group ID
	 * @param layoutPageTemplateCollectionId the layout page template collection ID
	 * @param status the status
	 * @return the number of matching layout page template entries that the user has permission to view
	 */
	public int filterCountByG_L_S(
		long groupId, long layoutPageTemplateCollectionId, int status);

	/**
	 * Returns an ordered range of all the layout page template entries where groupId = &#63; and name = &#63; and type = &#63;.
	 *
	 * <p>
	 * Useful when paginating results. Returns a maximum of <code>end - start</code> instances. <code>start</code> and <code>end</code> are not primary keys, they are indexes in the result set. Thus, <code>0</code> refers to the first result in the set. Setting both <code>start</code> and <code>end</code> to <code>com.liferay.portal.kernel.dao.orm.QueryUtil#ALL_POS</code> will return the full result set. If <code>orderByComparator</code> is specified, then the query will include the given ORDER BY logic. If <code>orderByComparator</code> is absent, then the query will include the default ORDER BY logic from <code>com.liferay.layout.page.template.model.impl.LayoutPageTemplateEntryModelImpl</code>.
	 * </p>
	 *
	 * @param groupId the group ID
	 * @param name the name
	 * @param type the type
	 * @param start the lower bound of the range of layout page template entries
	 * @param end the upper bound of the range of layout page template entries (not inclusive)
	 * @param orderByComparator the comparator to order the results by (optionally <code>null</code>)
	 * @param useFinderCache whether to use the finder cache
	 * @return the ordered range of matching layout page template entries
	 */
	public java.util.List<LayoutPageTemplateEntry> findByG_N_T(
		long groupId, String name, int type, int start, int end,
		com.liferay.portal.kernel.util.OrderByComparator
			<LayoutPageTemplateEntry> orderByComparator,
		boolean useFinderCache);

	/**
	 * Returns the first layout page template entry in the ordered set where groupId = &#63; and name = &#63; and type = &#63;.
	 *
	 * @param groupId the group ID
	 * @param name the name
	 * @param type the type
	 * @param orderByComparator the comparator to order the set by (optionally <code>null</code>)
	 * @return the first matching layout page template entry
	 * @throws NoSuchPageTemplateEntryException if a matching layout page template entry could not be found
	 */
	public LayoutPageTemplateEntry findByG_N_T_First(
			long groupId, String name, int type,
			com.liferay.portal.kernel.util.OrderByComparator
				<LayoutPageTemplateEntry> orderByComparator)
		throws NoSuchPageTemplateEntryException;

	/**
	 * Returns the first layout page template entry in the ordered set where groupId = &#63; and name = &#63; and type = &#63;.
	 *
	 * @param groupId the group ID
	 * @param name the name
	 * @param type the type
	 * @param orderByComparator the comparator to order the set by (optionally <code>null</code>)
	 * @return the first matching layout page template entry, or <code>null</code> if a matching layout page template entry could not be found
	 */
	public LayoutPageTemplateEntry fetchByG_N_T_First(
		long groupId, String name, int type,
		com.liferay.portal.kernel.util.OrderByComparator
			<LayoutPageTemplateEntry> orderByComparator);

	/**
	 * Returns an ordered range of all the layout page template entries that the user has permissions to view where groupId = &#63; and name = &#63; and type = &#63;.
	 *
	 * <p>
	 * Useful when paginating results. Returns a maximum of <code>end - start</code> instances. <code>start</code> and <code>end</code> are not primary keys, they are indexes in the result set. Thus, <code>0</code> refers to the first result in the set. Setting both <code>start</code> and <code>end</code> to <code>com.liferay.portal.kernel.dao.orm.QueryUtil#ALL_POS</code> will return the full result set. If <code>orderByComparator</code> is specified, then the query will include the given ORDER BY logic. If <code>orderByComparator</code> is absent, then the query will include the default ORDER BY logic from <code>com.liferay.layout.page.template.model.impl.LayoutPageTemplateEntryModelImpl</code>.
	 * </p>
	 *
	 * @param groupId the group ID
	 * @param name the name
	 * @param type the type
	 * @param start the lower bound of the range of layout page template entries
	 * @param end the upper bound of the range of layout page template entries (not inclusive)
	 * @param orderByComparator the comparator to order the results by (optionally <code>null</code>)
	 * @return the ordered range of matching layout page template entries that the user has permission to view
	 */
	public java.util.List<LayoutPageTemplateEntry> filterFindByG_N_T(
		long groupId, String name, int type, int start, int end,
		com.liferay.portal.kernel.util.OrderByComparator
			<LayoutPageTemplateEntry> orderByComparator);

	/**
	 * Removes all the layout page template entries where groupId = &#63; and name = &#63; and type = &#63; from the database.
	 *
	 * @param groupId the group ID
	 * @param name the name
	 * @param type the type
	 */
	public void removeByG_N_T(long groupId, String name, int type);

	/**
	 * Returns the number of layout page template entries where groupId = &#63; and name = &#63; and type = &#63;.
	 *
	 * @param groupId the group ID
	 * @param name the name
	 * @param type the type
	 * @return the number of matching layout page template entries
	 */
	public int countByG_N_T(long groupId, String name, int type);

	/**
	 * Returns the number of layout page template entries that the user has permission to view where groupId = &#63; and name = &#63; and type = &#63;.
	 *
	 * @param groupId the group ID
	 * @param name the name
	 * @param type the type
	 * @return the number of matching layout page template entries that the user has permission to view
	 */
	public int filterCountByG_N_T(long groupId, String name, int type);

	/**
	 * Returns all the layout page template entries where groupId = &#63; and name LIKE &#63; and type = &#63;.
	 *
	 * @param groupId the group ID
	 * @param name the name
	 * @param type the type
	 * @return the matching layout page template entries
	 */
	public java.util.List<LayoutPageTemplateEntry> findByG_T_LikeN(
		long groupId, String name, int type);

	/**
	 * Returns a range of all the layout page template entries where groupId = &#63; and name LIKE &#63; and type = &#63;.
	 *
	 * <p>
	 * Useful when paginating results. Returns a maximum of <code>end - start</code> instances. <code>start</code> and <code>end</code> are not primary keys, they are indexes in the result set. Thus, <code>0</code> refers to the first result in the set. Setting both <code>start</code> and <code>end</code> to <code>com.liferay.portal.kernel.dao.orm.QueryUtil#ALL_POS</code> will return the full result set. If <code>orderByComparator</code> is specified, then the query will include the given ORDER BY logic. If <code>orderByComparator</code> is absent, then the query will include the default ORDER BY logic from <code>com.liferay.layout.page.template.model.impl.LayoutPageTemplateEntryModelImpl</code>.
	 * </p>
	 *
	 * @param groupId the group ID
	 * @param name the name
	 * @param type the type
	 * @param start the lower bound of the range of layout page template entries
	 * @param end the upper bound of the range of layout page template entries (not inclusive)
	 * @return the range of matching layout page template entries
	 */
	public java.util.List<LayoutPageTemplateEntry> findByG_T_LikeN(
		long groupId, String name, int type, int start, int end);

	/**
	 * Returns an ordered range of all the layout page template entries where groupId = &#63; and name LIKE &#63; and type = &#63;.
	 *
	 * <p>
	 * Useful when paginating results. Returns a maximum of <code>end - start</code> instances. <code>start</code> and <code>end</code> are not primary keys, they are indexes in the result set. Thus, <code>0</code> refers to the first result in the set. Setting both <code>start</code> and <code>end</code> to <code>com.liferay.portal.kernel.dao.orm.QueryUtil#ALL_POS</code> will return the full result set. If <code>orderByComparator</code> is specified, then the query will include the given ORDER BY logic. If <code>orderByComparator</code> is absent, then the query will include the default ORDER BY logic from <code>com.liferay.layout.page.template.model.impl.LayoutPageTemplateEntryModelImpl</code>.
	 * </p>
	 *
	 * @param groupId the group ID
	 * @param name the name
	 * @param type the type
	 * @param start the lower bound of the range of layout page template entries
	 * @param end the upper bound of the range of layout page template entries (not inclusive)
	 * @param orderByComparator the comparator to order the results by (optionally <code>null</code>)
	 * @return the ordered range of matching layout page template entries
	 */
	public java.util.List<LayoutPageTemplateEntry> findByG_T_LikeN(
		long groupId, String name, int type, int start, int end,
		com.liferay.portal.kernel.util.OrderByComparator
			<LayoutPageTemplateEntry> orderByComparator);

	/**
	 * Returns an ordered range of all the layout page template entries where groupId = &#63; and name LIKE &#63; and type = &#63;.
	 *
	 * <p>
	 * Useful when paginating results. Returns a maximum of <code>end - start</code> instances. <code>start</code> and <code>end</code> are not primary keys, they are indexes in the result set. Thus, <code>0</code> refers to the first result in the set. Setting both <code>start</code> and <code>end</code> to <code>com.liferay.portal.kernel.dao.orm.QueryUtil#ALL_POS</code> will return the full result set. If <code>orderByComparator</code> is specified, then the query will include the given ORDER BY logic. If <code>orderByComparator</code> is absent, then the query will include the default ORDER BY logic from <code>com.liferay.layout.page.template.model.impl.LayoutPageTemplateEntryModelImpl</code>.
	 * </p>
	 *
	 * @param groupId the group ID
	 * @param name the name
	 * @param type the type
	 * @param start the lower bound of the range of layout page template entries
	 * @param end the upper bound of the range of layout page template entries (not inclusive)
	 * @param orderByComparator the comparator to order the results by (optionally <code>null</code>)
	 * @param useFinderCache whether to use the finder cache
	 * @return the ordered range of matching layout page template entries
	 */
	public java.util.List<LayoutPageTemplateEntry> findByG_T_LikeN(
		long groupId, String name, int type, int start, int end,
		com.liferay.portal.kernel.util.OrderByComparator
			<LayoutPageTemplateEntry> orderByComparator,
		boolean useFinderCache);

	/**
	 * Returns the first layout page template entry in the ordered set where groupId = &#63; and name LIKE &#63; and type = &#63;.
	 *
	 * @param groupId the group ID
	 * @param name the name
	 * @param type the type
	 * @param orderByComparator the comparator to order the set by (optionally <code>null</code>)
	 * @return the first matching layout page template entry
	 * @throws NoSuchPageTemplateEntryException if a matching layout page template entry could not be found
	 */
	public LayoutPageTemplateEntry findByG_T_LikeN_First(
			long groupId, String name, int type,
			com.liferay.portal.kernel.util.OrderByComparator
				<LayoutPageTemplateEntry> orderByComparator)
		throws NoSuchPageTemplateEntryException;

	/**
	 * Returns the first layout page template entry in the ordered set where groupId = &#63; and name LIKE &#63; and type = &#63;.
	 *
	 * @param groupId the group ID
	 * @param name the name
	 * @param type the type
	 * @param orderByComparator the comparator to order the set by (optionally <code>null</code>)
	 * @return the first matching layout page template entry, or <code>null</code> if a matching layout page template entry could not be found
	 */
	public LayoutPageTemplateEntry fetchByG_T_LikeN_First(
		long groupId, String name, int type,
		com.liferay.portal.kernel.util.OrderByComparator
			<LayoutPageTemplateEntry> orderByComparator);

	/**
	 * Returns all the layout page template entries that the user has permission to view where groupId = &#63; and name LIKE &#63; and type = &#63;.
	 *
	 * @param groupId the group ID
	 * @param name the name
	 * @param type the type
	 * @return the matching layout page template entries that the user has permission to view
	 */
	public java.util.List<LayoutPageTemplateEntry> filterFindByG_T_LikeN(
		long groupId, String name, int type);

	/**
	 * Returns a range of all the layout page template entries that the user has permission to view where groupId = &#63; and name LIKE &#63; and type = &#63;.
	 *
	 * <p>
	 * Useful when paginating results. Returns a maximum of <code>end - start</code> instances. <code>start</code> and <code>end</code> are not primary keys, they are indexes in the result set. Thus, <code>0</code> refers to the first result in the set. Setting both <code>start</code> and <code>end</code> to <code>com.liferay.portal.kernel.dao.orm.QueryUtil#ALL_POS</code> will return the full result set. If <code>orderByComparator</code> is specified, then the query will include the given ORDER BY logic. If <code>orderByComparator</code> is absent, then the query will include the default ORDER BY logic from <code>com.liferay.layout.page.template.model.impl.LayoutPageTemplateEntryModelImpl</code>.
	 * </p>
	 *
	 * @param groupId the group ID
	 * @param name the name
	 * @param type the type
	 * @param start the lower bound of the range of layout page template entries
	 * @param end the upper bound of the range of layout page template entries (not inclusive)
	 * @return the range of matching layout page template entries that the user has permission to view
	 */
	public java.util.List<LayoutPageTemplateEntry> filterFindByG_T_LikeN(
		long groupId, String name, int type, int start, int end);

	/**
	 * Returns an ordered range of all the layout page template entries that the user has permissions to view where groupId = &#63; and name LIKE &#63; and type = &#63;.
	 *
	 * <p>
	 * Useful when paginating results. Returns a maximum of <code>end - start</code> instances. <code>start</code> and <code>end</code> are not primary keys, they are indexes in the result set. Thus, <code>0</code> refers to the first result in the set. Setting both <code>start</code> and <code>end</code> to <code>com.liferay.portal.kernel.dao.orm.QueryUtil#ALL_POS</code> will return the full result set. If <code>orderByComparator</code> is specified, then the query will include the given ORDER BY logic. If <code>orderByComparator</code> is absent, then the query will include the default ORDER BY logic from <code>com.liferay.layout.page.template.model.impl.LayoutPageTemplateEntryModelImpl</code>.
	 * </p>
	 *
	 * @param groupId the group ID
	 * @param name the name
	 * @param type the type
	 * @param start the lower bound of the range of layout page template entries
	 * @param end the upper bound of the range of layout page template entries (not inclusive)
	 * @param orderByComparator the comparator to order the results by (optionally <code>null</code>)
	 * @return the ordered range of matching layout page template entries that the user has permission to view
	 */
	public java.util.List<LayoutPageTemplateEntry> filterFindByG_T_LikeN(
		long groupId, String name, int type, int start, int end,
		com.liferay.portal.kernel.util.OrderByComparator
			<LayoutPageTemplateEntry> orderByComparator);

	/**
	 * Returns all the layout page template entries that the user has permission to view where groupId = &#63; and name LIKE &#63; and type = any &#63;.
	 *
	 * @param groupId the group ID
	 * @param name the name
	 * @param types the types
	 * @return the matching layout page template entries that the user has permission to view
	 */
	public java.util.List<LayoutPageTemplateEntry> filterFindByG_T_LikeN(
		long groupId, String name, int[] types);

	/**
	 * Returns a range of all the layout page template entries that the user has permission to view where groupId = &#63; and name LIKE &#63; and type = any &#63;.
	 *
	 * <p>
	 * Useful when paginating results. Returns a maximum of <code>end - start</code> instances. <code>start</code> and <code>end</code> are not primary keys, they are indexes in the result set. Thus, <code>0</code> refers to the first result in the set. Setting both <code>start</code> and <code>end</code> to <code>com.liferay.portal.kernel.dao.orm.QueryUtil#ALL_POS</code> will return the full result set. If <code>orderByComparator</code> is specified, then the query will include the given ORDER BY logic. If <code>orderByComparator</code> is absent, then the query will include the default ORDER BY logic from <code>com.liferay.layout.page.template.model.impl.LayoutPageTemplateEntryModelImpl</code>.
	 * </p>
	 *
	 * @param groupId the group ID
	 * @param name the name
	 * @param types the types
	 * @param start the lower bound of the range of layout page template entries
	 * @param end the upper bound of the range of layout page template entries (not inclusive)
	 * @return the range of matching layout page template entries that the user has permission to view
	 */
	public java.util.List<LayoutPageTemplateEntry> filterFindByG_T_LikeN(
		long groupId, String name, int[] types, int start, int end);

	/**
	 * Returns an ordered range of all the layout page template entries that the user has permission to view where groupId = &#63; and name LIKE &#63; and type = any &#63;.
	 *
	 * <p>
	 * Useful when paginating results. Returns a maximum of <code>end - start</code> instances. <code>start</code> and <code>end</code> are not primary keys, they are indexes in the result set. Thus, <code>0</code> refers to the first result in the set. Setting both <code>start</code> and <code>end</code> to <code>com.liferay.portal.kernel.dao.orm.QueryUtil#ALL_POS</code> will return the full result set. If <code>orderByComparator</code> is specified, then the query will include the given ORDER BY logic. If <code>orderByComparator</code> is absent, then the query will include the default ORDER BY logic from <code>com.liferay.layout.page.template.model.impl.LayoutPageTemplateEntryModelImpl</code>.
	 * </p>
	 *
	 * @param groupId the group ID
	 * @param name the name
	 * @param types the types
	 * @param start the lower bound of the range of layout page template entries
	 * @param end the upper bound of the range of layout page template entries (not inclusive)
	 * @param orderByComparator the comparator to order the results by (optionally <code>null</code>)
	 * @return the ordered range of matching layout page template entries that the user has permission to view
	 */
	public java.util.List<LayoutPageTemplateEntry> filterFindByG_T_LikeN(
		long groupId, String name, int[] types, int start, int end,
		com.liferay.portal.kernel.util.OrderByComparator
			<LayoutPageTemplateEntry> orderByComparator);

	/**
	 * Returns all the layout page template entries where groupId = &#63; and name LIKE &#63; and type = any &#63;.
	 *
	 * <p>
	 * Useful when paginating results. Returns a maximum of <code>end - start</code> instances. <code>start</code> and <code>end</code> are not primary keys, they are indexes in the result set. Thus, <code>0</code> refers to the first result in the set. Setting both <code>start</code> and <code>end</code> to <code>com.liferay.portal.kernel.dao.orm.QueryUtil#ALL_POS</code> will return the full result set. If <code>orderByComparator</code> is specified, then the query will include the given ORDER BY logic. If <code>orderByComparator</code> is absent, then the query will include the default ORDER BY logic from <code>com.liferay.layout.page.template.model.impl.LayoutPageTemplateEntryModelImpl</code>.
	 * </p>
	 *
	 * @param groupId the group ID
	 * @param name the name
	 * @param types the types
	 * @return the matching layout page template entries
	 */
	public java.util.List<LayoutPageTemplateEntry> findByG_T_LikeN(
		long groupId, String name, int[] types);

	/**
	 * Returns a range of all the layout page template entries where groupId = &#63; and name LIKE &#63; and type = any &#63;.
	 *
	 * <p>
	 * Useful when paginating results. Returns a maximum of <code>end - start</code> instances. <code>start</code> and <code>end</code> are not primary keys, they are indexes in the result set. Thus, <code>0</code> refers to the first result in the set. Setting both <code>start</code> and <code>end</code> to <code>com.liferay.portal.kernel.dao.orm.QueryUtil#ALL_POS</code> will return the full result set. If <code>orderByComparator</code> is specified, then the query will include the given ORDER BY logic. If <code>orderByComparator</code> is absent, then the query will include the default ORDER BY logic from <code>com.liferay.layout.page.template.model.impl.LayoutPageTemplateEntryModelImpl</code>.
	 * </p>
	 *
	 * @param groupId the group ID
	 * @param name the name
	 * @param types the types
	 * @param start the lower bound of the range of layout page template entries
	 * @param end the upper bound of the range of layout page template entries (not inclusive)
	 * @return the range of matching layout page template entries
	 */
	public java.util.List<LayoutPageTemplateEntry> findByG_T_LikeN(
		long groupId, String name, int[] types, int start, int end);

	/**
	 * Returns an ordered range of all the layout page template entries where groupId = &#63; and name LIKE &#63; and type = any &#63;.
	 *
	 * <p>
	 * Useful when paginating results. Returns a maximum of <code>end - start</code> instances. <code>start</code> and <code>end</code> are not primary keys, they are indexes in the result set. Thus, <code>0</code> refers to the first result in the set. Setting both <code>start</code> and <code>end</code> to <code>com.liferay.portal.kernel.dao.orm.QueryUtil#ALL_POS</code> will return the full result set. If <code>orderByComparator</code> is specified, then the query will include the given ORDER BY logic. If <code>orderByComparator</code> is absent, then the query will include the default ORDER BY logic from <code>com.liferay.layout.page.template.model.impl.LayoutPageTemplateEntryModelImpl</code>.
	 * </p>
	 *
	 * @param groupId the group ID
	 * @param name the name
	 * @param types the types
	 * @param start the lower bound of the range of layout page template entries
	 * @param end the upper bound of the range of layout page template entries (not inclusive)
	 * @param orderByComparator the comparator to order the results by (optionally <code>null</code>)
	 * @return the ordered range of matching layout page template entries
	 */
	public java.util.List<LayoutPageTemplateEntry> findByG_T_LikeN(
		long groupId, String name, int[] types, int start, int end,
		com.liferay.portal.kernel.util.OrderByComparator
			<LayoutPageTemplateEntry> orderByComparator);

	/**
	 * Returns an ordered range of all the layout page template entries where groupId = &#63; and name LIKE &#63; and type = &#63;, optionally using the finder cache.
	 *
	 * <p>
	 * Useful when paginating results. Returns a maximum of <code>end - start</code> instances. <code>start</code> and <code>end</code> are not primary keys, they are indexes in the result set. Thus, <code>0</code> refers to the first result in the set. Setting both <code>start</code> and <code>end</code> to <code>com.liferay.portal.kernel.dao.orm.QueryUtil#ALL_POS</code> will return the full result set. If <code>orderByComparator</code> is specified, then the query will include the given ORDER BY logic. If <code>orderByComparator</code> is absent, then the query will include the default ORDER BY logic from <code>com.liferay.layout.page.template.model.impl.LayoutPageTemplateEntryModelImpl</code>.
	 * </p>
	 *
	 * @param groupId the group ID
	 * @param name the name
	 * @param types the types
	 * @param start the lower bound of the range of layout page template entries
	 * @param end the upper bound of the range of layout page template entries (not inclusive)
	 * @param orderByComparator the comparator to order the results by (optionally <code>null</code>)
	 * @param useFinderCache whether to use the finder cache
	 * @return the ordered range of matching layout page template entries
	 */
	public java.util.List<LayoutPageTemplateEntry> findByG_T_LikeN(
		long groupId, String name, int[] types, int start, int end,
		com.liferay.portal.kernel.util.OrderByComparator
			<LayoutPageTemplateEntry> orderByComparator,
		boolean useFinderCache);

	/**
	 * Removes all the layout page template entries where groupId = &#63; and name LIKE &#63; and type = &#63; from the database.
	 *
	 * @param groupId the group ID
	 * @param name the name
	 * @param type the type
	 */
	public void removeByG_T_LikeN(long groupId, String name, int type);

	/**
	 * Returns the number of layout page template entries where groupId = &#63; and name LIKE &#63; and type = &#63;.
	 *
	 * @param groupId the group ID
	 * @param name the name
	 * @param type the type
	 * @return the number of matching layout page template entries
	 */
	public int countByG_T_LikeN(long groupId, String name, int type);

	/**
	 * Returns the number of layout page template entries where groupId = &#63; and name LIKE &#63; and type = any &#63;.
	 *
	 * @param groupId the group ID
	 * @param name the name
	 * @param types the types
	 * @return the number of matching layout page template entries
	 */
	public int countByG_T_LikeN(long groupId, String name, int[] types);

	/**
	 * Returns the number of layout page template entries that the user has permission to view where groupId = &#63; and name LIKE &#63; and type = &#63;.
	 *
	 * @param groupId the group ID
	 * @param name the name
	 * @param type the type
	 * @return the number of matching layout page template entries that the user has permission to view
	 */
	public int filterCountByG_T_LikeN(long groupId, String name, int type);

	/**
	 * Returns the number of layout page template entries that the user has permission to view where groupId = &#63; and name LIKE &#63; and type = any &#63;.
	 *
	 * @param groupId the group ID
	 * @param name the name
	 * @param types the types
	 * @return the number of matching layout page template entries that the user has permission to view
	 */
	public int filterCountByG_T_LikeN(long groupId, String name, int[] types);

	/**
	 * Returns an ordered range of all the layout page template entries where groupId = &#63; and type = &#63; and status = &#63;.
	 *
	 * <p>
	 * Useful when paginating results. Returns a maximum of <code>end - start</code> instances. <code>start</code> and <code>end</code> are not primary keys, they are indexes in the result set. Thus, <code>0</code> refers to the first result in the set. Setting both <code>start</code> and <code>end</code> to <code>com.liferay.portal.kernel.dao.orm.QueryUtil#ALL_POS</code> will return the full result set. If <code>orderByComparator</code> is specified, then the query will include the given ORDER BY logic. If <code>orderByComparator</code> is absent, then the query will include the default ORDER BY logic from <code>com.liferay.layout.page.template.model.impl.LayoutPageTemplateEntryModelImpl</code>.
	 * </p>
	 *
	 * @param groupId the group ID
	 * @param type the type
	 * @param status the status
	 * @param start the lower bound of the range of layout page template entries
	 * @param end the upper bound of the range of layout page template entries (not inclusive)
	 * @param orderByComparator the comparator to order the results by (optionally <code>null</code>)
	 * @param useFinderCache whether to use the finder cache
	 * @return the ordered range of matching layout page template entries
	 */
	public java.util.List<LayoutPageTemplateEntry> findByG_T_S(
		long groupId, int type, int status, int start, int end,
		com.liferay.portal.kernel.util.OrderByComparator
			<LayoutPageTemplateEntry> orderByComparator,
		boolean useFinderCache);

	/**
	 * Returns the first layout page template entry in the ordered set where groupId = &#63; and type = &#63; and status = &#63;.
	 *
	 * @param groupId the group ID
	 * @param type the type
	 * @param status the status
	 * @param orderByComparator the comparator to order the set by (optionally <code>null</code>)
	 * @return the first matching layout page template entry
	 * @throws NoSuchPageTemplateEntryException if a matching layout page template entry could not be found
	 */
	public LayoutPageTemplateEntry findByG_T_S_First(
			long groupId, int type, int status,
			com.liferay.portal.kernel.util.OrderByComparator
				<LayoutPageTemplateEntry> orderByComparator)
		throws NoSuchPageTemplateEntryException;

	/**
	 * Returns the first layout page template entry in the ordered set where groupId = &#63; and type = &#63; and status = &#63;.
	 *
	 * @param groupId the group ID
	 * @param type the type
	 * @param status the status
	 * @param orderByComparator the comparator to order the set by (optionally <code>null</code>)
	 * @return the first matching layout page template entry, or <code>null</code> if a matching layout page template entry could not be found
	 */
	public LayoutPageTemplateEntry fetchByG_T_S_First(
		long groupId, int type, int status,
		com.liferay.portal.kernel.util.OrderByComparator
			<LayoutPageTemplateEntry> orderByComparator);

	/**
	 * Returns an ordered range of all the layout page template entries that the user has permissions to view where groupId = &#63; and type = &#63; and status = &#63;.
	 *
	 * <p>
	 * Useful when paginating results. Returns a maximum of <code>end - start</code> instances. <code>start</code> and <code>end</code> are not primary keys, they are indexes in the result set. Thus, <code>0</code> refers to the first result in the set. Setting both <code>start</code> and <code>end</code> to <code>com.liferay.portal.kernel.dao.orm.QueryUtil#ALL_POS</code> will return the full result set. If <code>orderByComparator</code> is specified, then the query will include the given ORDER BY logic. If <code>orderByComparator</code> is absent, then the query will include the default ORDER BY logic from <code>com.liferay.layout.page.template.model.impl.LayoutPageTemplateEntryModelImpl</code>.
	 * </p>
	 *
	 * @param groupId the group ID
	 * @param type the type
	 * @param status the status
	 * @param start the lower bound of the range of layout page template entries
	 * @param end the upper bound of the range of layout page template entries (not inclusive)
	 * @param orderByComparator the comparator to order the results by (optionally <code>null</code>)
	 * @return the ordered range of matching layout page template entries that the user has permission to view
	 */
	public java.util.List<LayoutPageTemplateEntry> filterFindByG_T_S(
		long groupId, int type, int status, int start, int end,
		com.liferay.portal.kernel.util.OrderByComparator
			<LayoutPageTemplateEntry> orderByComparator);

	/**
	 * Returns an ordered range of all the layout page template entries that the user has permission to view where groupId = &#63; and type = any &#63; and status = &#63;.
	 *
	 * <p>
	 * Useful when paginating results. Returns a maximum of <code>end - start</code> instances. <code>start</code> and <code>end</code> are not primary keys, they are indexes in the result set. Thus, <code>0</code> refers to the first result in the set. Setting both <code>start</code> and <code>end</code> to <code>com.liferay.portal.kernel.dao.orm.QueryUtil#ALL_POS</code> will return the full result set. If <code>orderByComparator</code> is specified, then the query will include the given ORDER BY logic. If <code>orderByComparator</code> is absent, then the query will include the default ORDER BY logic from <code>com.liferay.layout.page.template.model.impl.LayoutPageTemplateEntryModelImpl</code>.
	 * </p>
	 *
	 * @param groupId the group ID
	 * @param types the types
	 * @param status the status
	 * @param start the lower bound of the range of layout page template entries
	 * @param end the upper bound of the range of layout page template entries (not inclusive)
	 * @param orderByComparator the comparator to order the results by (optionally <code>null</code>)
	 * @return the ordered range of matching layout page template entries that the user has permission to view
	 */
	public java.util.List<LayoutPageTemplateEntry> filterFindByG_T_S(
		long groupId, int[] types, int status, int start, int end,
		com.liferay.portal.kernel.util.OrderByComparator
			<LayoutPageTemplateEntry> orderByComparator);

	/**
	 * Returns an ordered range of all the layout page template entries where groupId = &#63; and type = &#63; and status = &#63;, optionally using the finder cache.
	 *
	 * <p>
	 * Useful when paginating results. Returns a maximum of <code>end - start</code> instances. <code>start</code> and <code>end</code> are not primary keys, they are indexes in the result set. Thus, <code>0</code> refers to the first result in the set. Setting both <code>start</code> and <code>end</code> to <code>com.liferay.portal.kernel.dao.orm.QueryUtil#ALL_POS</code> will return the full result set. If <code>orderByComparator</code> is specified, then the query will include the given ORDER BY logic. If <code>orderByComparator</code> is absent, then the query will include the default ORDER BY logic from <code>com.liferay.layout.page.template.model.impl.LayoutPageTemplateEntryModelImpl</code>.
	 * </p>
	 *
	 * @param groupId the group ID
	 * @param types the types
	 * @param status the status
	 * @param start the lower bound of the range of layout page template entries
	 * @param end the upper bound of the range of layout page template entries (not inclusive)
	 * @param orderByComparator the comparator to order the results by (optionally <code>null</code>)
	 * @param useFinderCache whether to use the finder cache
	 * @return the ordered range of matching layout page template entries
	 */
	public java.util.List<LayoutPageTemplateEntry> findByG_T_S(
		long groupId, int[] types, int status, int start, int end,
		com.liferay.portal.kernel.util.OrderByComparator
			<LayoutPageTemplateEntry> orderByComparator,
		boolean useFinderCache);

	/**
	 * Removes all the layout page template entries where groupId = &#63; and type = &#63; and status = &#63; from the database.
	 *
	 * @param groupId the group ID
	 * @param type the type
	 * @param status the status
	 */
	public void removeByG_T_S(long groupId, int type, int status);

	/**
	 * Returns the number of layout page template entries where groupId = &#63; and type = &#63; and status = &#63;.
	 *
	 * @param groupId the group ID
	 * @param type the type
	 * @param status the status
	 * @return the number of matching layout page template entries
	 */
	public int countByG_T_S(long groupId, int type, int status);

	/**
	 * Returns the number of layout page template entries where groupId = &#63; and type = any &#63; and status = &#63;.
	 *
	 * @param groupId the group ID
	 * @param types the types
	 * @param status the status
	 * @return the number of matching layout page template entries
	 */
	public int countByG_T_S(long groupId, int[] types, int status);

	/**
	 * Returns the number of layout page template entries that the user has permission to view where groupId = &#63; and type = &#63; and status = &#63;.
	 *
	 * @param groupId the group ID
	 * @param type the type
	 * @param status the status
	 * @return the number of matching layout page template entries that the user has permission to view
	 */
	public int filterCountByG_T_S(long groupId, int type, int status);

	/**
	 * Returns the number of layout page template entries that the user has permission to view where groupId = &#63; and type = any &#63; and status = &#63;.
	 *
	 * @param groupId the group ID
	 * @param types the types
	 * @param status the status
	 * @return the number of matching layout page template entries that the user has permission to view
	 */
	public int filterCountByG_T_S(long groupId, int[] types, int status);

	/**
	 * Returns the layout page template entry where groupId = &#63; and layoutPageTemplateCollectionId = &#63; and name = &#63; and type = &#63; or throws a <code>NoSuchPageTemplateEntryException</code> if it could not be found.
	 *
	 * @param groupId the group ID
	 * @param layoutPageTemplateCollectionId the layout page template collection ID
	 * @param name the name
	 * @param type the type
	 * @return the matching layout page template entry
	 * @throws NoSuchPageTemplateEntryException if a matching layout page template entry could not be found
	 */
	public LayoutPageTemplateEntry findByG_L_N_T(
			long groupId, long layoutPageTemplateCollectionId, String name,
			int type)
		throws NoSuchPageTemplateEntryException;

	/**
	 * Returns the layout page template entry where groupId = &#63; and layoutPageTemplateCollectionId = &#63; and name = &#63; and type = &#63; or returns <code>null</code> if it could not be found, optionally using the finder cache.
	 *
	 * @param groupId the group ID
	 * @param layoutPageTemplateCollectionId the layout page template collection ID
	 * @param name the name
	 * @param type the type
	 * @param useFinderCache whether to use the finder cache
	 * @return the matching layout page template entry, or <code>null</code> if a matching layout page template entry could not be found
	 */
	public LayoutPageTemplateEntry fetchByG_L_N_T(
		long groupId, long layoutPageTemplateCollectionId, String name,
		int type, boolean useFinderCache);

	/**
	 * Removes the layout page template entry where groupId = &#63; and layoutPageTemplateCollectionId = &#63; and name = &#63; and type = &#63; from the database.
	 *
	 * @param groupId the group ID
	 * @param layoutPageTemplateCollectionId the layout page template collection ID
	 * @param name the name
	 * @param type the type
	 * @return the layout page template entry that was removed
	 */
	public LayoutPageTemplateEntry removeByG_L_N_T(
			long groupId, long layoutPageTemplateCollectionId, String name,
			int type)
		throws NoSuchPageTemplateEntryException;

	/**
	 * Returns the number of layout page template entries where groupId = &#63; and layoutPageTemplateCollectionId = &#63; and name = &#63; and type = &#63;.
	 *
	 * @param groupId the group ID
	 * @param layoutPageTemplateCollectionId the layout page template collection ID
	 * @param name the name
	 * @param type the type
	 * @return the number of matching layout page template entries
	 */
	public int countByG_L_N_T(
		long groupId, long layoutPageTemplateCollectionId, String name,
		int type);

	/**
	 * Returns all the layout page template entries where groupId = &#63; and layoutPageTemplateCollectionId = &#63; and name LIKE &#63; and status = &#63;.
	 *
	 * @param groupId the group ID
	 * @param layoutPageTemplateCollectionId the layout page template collection ID
	 * @param name the name
	 * @param status the status
	 * @return the matching layout page template entries
	 */
	public java.util.List<LayoutPageTemplateEntry> findByG_L_LikeN_S(
		long groupId, long layoutPageTemplateCollectionId, String name,
		int status);

	/**
	 * Returns a range of all the layout page template entries where groupId = &#63; and layoutPageTemplateCollectionId = &#63; and name LIKE &#63; and status = &#63;.
	 *
	 * <p>
	 * Useful when paginating results. Returns a maximum of <code>end - start</code> instances. <code>start</code> and <code>end</code> are not primary keys, they are indexes in the result set. Thus, <code>0</code> refers to the first result in the set. Setting both <code>start</code> and <code>end</code> to <code>com.liferay.portal.kernel.dao.orm.QueryUtil#ALL_POS</code> will return the full result set. If <code>orderByComparator</code> is specified, then the query will include the given ORDER BY logic. If <code>orderByComparator</code> is absent, then the query will include the default ORDER BY logic from <code>com.liferay.layout.page.template.model.impl.LayoutPageTemplateEntryModelImpl</code>.
	 * </p>
	 *
	 * @param groupId the group ID
	 * @param layoutPageTemplateCollectionId the layout page template collection ID
	 * @param name the name
	 * @param status the status
	 * @param start the lower bound of the range of layout page template entries
	 * @param end the upper bound of the range of layout page template entries (not inclusive)
	 * @return the range of matching layout page template entries
	 */
	public java.util.List<LayoutPageTemplateEntry> findByG_L_LikeN_S(
		long groupId, long layoutPageTemplateCollectionId, String name,
		int status, int start, int end);

	/**
	 * Returns an ordered range of all the layout page template entries where groupId = &#63; and layoutPageTemplateCollectionId = &#63; and name LIKE &#63; and status = &#63;.
	 *
	 * <p>
	 * Useful when paginating results. Returns a maximum of <code>end - start</code> instances. <code>start</code> and <code>end</code> are not primary keys, they are indexes in the result set. Thus, <code>0</code> refers to the first result in the set. Setting both <code>start</code> and <code>end</code> to <code>com.liferay.portal.kernel.dao.orm.QueryUtil#ALL_POS</code> will return the full result set. If <code>orderByComparator</code> is specified, then the query will include the given ORDER BY logic. If <code>orderByComparator</code> is absent, then the query will include the default ORDER BY logic from <code>com.liferay.layout.page.template.model.impl.LayoutPageTemplateEntryModelImpl</code>.
	 * </p>
	 *
	 * @param groupId the group ID
	 * @param layoutPageTemplateCollectionId the layout page template collection ID
	 * @param name the name
	 * @param status the status
	 * @param start the lower bound of the range of layout page template entries
	 * @param end the upper bound of the range of layout page template entries (not inclusive)
	 * @param orderByComparator the comparator to order the results by (optionally <code>null</code>)
	 * @return the ordered range of matching layout page template entries
	 */
	public java.util.List<LayoutPageTemplateEntry> findByG_L_LikeN_S(
		long groupId, long layoutPageTemplateCollectionId, String name,
		int status, int start, int end,
		com.liferay.portal.kernel.util.OrderByComparator
			<LayoutPageTemplateEntry> orderByComparator);

	/**
	 * Returns an ordered range of all the layout page template entries where groupId = &#63; and layoutPageTemplateCollectionId = &#63; and name LIKE &#63; and status = &#63;.
	 *
	 * <p>
	 * Useful when paginating results. Returns a maximum of <code>end - start</code> instances. <code>start</code> and <code>end</code> are not primary keys, they are indexes in the result set. Thus, <code>0</code> refers to the first result in the set. Setting both <code>start</code> and <code>end</code> to <code>com.liferay.portal.kernel.dao.orm.QueryUtil#ALL_POS</code> will return the full result set. If <code>orderByComparator</code> is specified, then the query will include the given ORDER BY logic. If <code>orderByComparator</code> is absent, then the query will include the default ORDER BY logic from <code>com.liferay.layout.page.template.model.impl.LayoutPageTemplateEntryModelImpl</code>.
	 * </p>
	 *
	 * @param groupId the group ID
	 * @param layoutPageTemplateCollectionId the layout page template collection ID
	 * @param name the name
	 * @param status the status
	 * @param start the lower bound of the range of layout page template entries
	 * @param end the upper bound of the range of layout page template entries (not inclusive)
	 * @param orderByComparator the comparator to order the results by (optionally <code>null</code>)
	 * @param useFinderCache whether to use the finder cache
	 * @return the ordered range of matching layout page template entries
	 */
	public java.util.List<LayoutPageTemplateEntry> findByG_L_LikeN_S(
		long groupId, long layoutPageTemplateCollectionId, String name,
		int status, int start, int end,
		com.liferay.portal.kernel.util.OrderByComparator
			<LayoutPageTemplateEntry> orderByComparator,
		boolean useFinderCache);

	/**
	 * Returns the first layout page template entry in the ordered set where groupId = &#63; and layoutPageTemplateCollectionId = &#63; and name LIKE &#63; and status = &#63;.
	 *
	 * @param groupId the group ID
	 * @param layoutPageTemplateCollectionId the layout page template collection ID
	 * @param name the name
	 * @param status the status
	 * @param orderByComparator the comparator to order the set by (optionally <code>null</code>)
	 * @return the first matching layout page template entry
	 * @throws NoSuchPageTemplateEntryException if a matching layout page template entry could not be found
	 */
	public LayoutPageTemplateEntry findByG_L_LikeN_S_First(
			long groupId, long layoutPageTemplateCollectionId, String name,
			int status,
			com.liferay.portal.kernel.util.OrderByComparator
				<LayoutPageTemplateEntry> orderByComparator)
		throws NoSuchPageTemplateEntryException;

	/**
	 * Returns the first layout page template entry in the ordered set where groupId = &#63; and layoutPageTemplateCollectionId = &#63; and name LIKE &#63; and status = &#63;.
	 *
	 * @param groupId the group ID
	 * @param layoutPageTemplateCollectionId the layout page template collection ID
	 * @param name the name
	 * @param status the status
	 * @param orderByComparator the comparator to order the set by (optionally <code>null</code>)
	 * @return the first matching layout page template entry, or <code>null</code> if a matching layout page template entry could not be found
	 */
	public LayoutPageTemplateEntry fetchByG_L_LikeN_S_First(
		long groupId, long layoutPageTemplateCollectionId, String name,
		int status,
		com.liferay.portal.kernel.util.OrderByComparator
			<LayoutPageTemplateEntry> orderByComparator);

	/**
	 * Returns all the layout page template entries that the user has permission to view where groupId = &#63; and layoutPageTemplateCollectionId = &#63; and name LIKE &#63; and status = &#63;.
	 *
	 * @param groupId the group ID
	 * @param layoutPageTemplateCollectionId the layout page template collection ID
	 * @param name the name
	 * @param status the status
	 * @return the matching layout page template entries that the user has permission to view
	 */
	public java.util.List<LayoutPageTemplateEntry> filterFindByG_L_LikeN_S(
		long groupId, long layoutPageTemplateCollectionId, String name,
		int status);

	/**
	 * Returns a range of all the layout page template entries that the user has permission to view where groupId = &#63; and layoutPageTemplateCollectionId = &#63; and name LIKE &#63; and status = &#63;.
	 *
	 * <p>
	 * Useful when paginating results. Returns a maximum of <code>end - start</code> instances. <code>start</code> and <code>end</code> are not primary keys, they are indexes in the result set. Thus, <code>0</code> refers to the first result in the set. Setting both <code>start</code> and <code>end</code> to <code>com.liferay.portal.kernel.dao.orm.QueryUtil#ALL_POS</code> will return the full result set. If <code>orderByComparator</code> is specified, then the query will include the given ORDER BY logic. If <code>orderByComparator</code> is absent, then the query will include the default ORDER BY logic from <code>com.liferay.layout.page.template.model.impl.LayoutPageTemplateEntryModelImpl</code>.
	 * </p>
	 *
	 * @param groupId the group ID
	 * @param layoutPageTemplateCollectionId the layout page template collection ID
	 * @param name the name
	 * @param status the status
	 * @param start the lower bound of the range of layout page template entries
	 * @param end the upper bound of the range of layout page template entries (not inclusive)
	 * @return the range of matching layout page template entries that the user has permission to view
	 */
	public java.util.List<LayoutPageTemplateEntry> filterFindByG_L_LikeN_S(
		long groupId, long layoutPageTemplateCollectionId, String name,
		int status, int start, int end);

	/**
	 * Returns an ordered range of all the layout page template entries that the user has permissions to view where groupId = &#63; and layoutPageTemplateCollectionId = &#63; and name LIKE &#63; and status = &#63;.
	 *
	 * <p>
	 * Useful when paginating results. Returns a maximum of <code>end - start</code> instances. <code>start</code> and <code>end</code> are not primary keys, they are indexes in the result set. Thus, <code>0</code> refers to the first result in the set. Setting both <code>start</code> and <code>end</code> to <code>com.liferay.portal.kernel.dao.orm.QueryUtil#ALL_POS</code> will return the full result set. If <code>orderByComparator</code> is specified, then the query will include the given ORDER BY logic. If <code>orderByComparator</code> is absent, then the query will include the default ORDER BY logic from <code>com.liferay.layout.page.template.model.impl.LayoutPageTemplateEntryModelImpl</code>.
	 * </p>
	 *
	 * @param groupId the group ID
	 * @param layoutPageTemplateCollectionId the layout page template collection ID
	 * @param name the name
	 * @param status the status
	 * @param start the lower bound of the range of layout page template entries
	 * @param end the upper bound of the range of layout page template entries (not inclusive)
	 * @param orderByComparator the comparator to order the results by (optionally <code>null</code>)
	 * @return the ordered range of matching layout page template entries that the user has permission to view
	 */
	public java.util.List<LayoutPageTemplateEntry> filterFindByG_L_LikeN_S(
		long groupId, long layoutPageTemplateCollectionId, String name,
		int status, int start, int end,
		com.liferay.portal.kernel.util.OrderByComparator
			<LayoutPageTemplateEntry> orderByComparator);

	/**
	 * Removes all the layout page template entries where groupId = &#63; and layoutPageTemplateCollectionId = &#63; and name LIKE &#63; and status = &#63; from the database.
	 *
	 * @param groupId the group ID
	 * @param layoutPageTemplateCollectionId the layout page template collection ID
	 * @param name the name
	 * @param status the status
	 */
	public void removeByG_L_LikeN_S(
		long groupId, long layoutPageTemplateCollectionId, String name,
		int status);

	/**
	 * Returns the number of layout page template entries where groupId = &#63; and layoutPageTemplateCollectionId = &#63; and name LIKE &#63; and status = &#63;.
	 *
	 * @param groupId the group ID
	 * @param layoutPageTemplateCollectionId the layout page template collection ID
	 * @param name the name
	 * @param status the status
	 * @return the number of matching layout page template entries
	 */
	public int countByG_L_LikeN_S(
		long groupId, long layoutPageTemplateCollectionId, String name,
		int status);

	/**
	 * Returns the number of layout page template entries that the user has permission to view where groupId = &#63; and layoutPageTemplateCollectionId = &#63; and name LIKE &#63; and status = &#63;.
	 *
	 * @param groupId the group ID
	 * @param layoutPageTemplateCollectionId the layout page template collection ID
	 * @param name the name
	 * @param status the status
	 * @return the number of matching layout page template entries that the user has permission to view
	 */
	public int filterCountByG_L_LikeN_S(
		long groupId, long layoutPageTemplateCollectionId, String name,
		int status);

	/**
	 * Returns an ordered range of all the layout page template entries where groupId = &#63; and classNameId = &#63; and classTypeKey = &#63; and type = &#63;.
	 *
	 * <p>
	 * Useful when paginating results. Returns a maximum of <code>end - start</code> instances. <code>start</code> and <code>end</code> are not primary keys, they are indexes in the result set. Thus, <code>0</code> refers to the first result in the set. Setting both <code>start</code> and <code>end</code> to <code>com.liferay.portal.kernel.dao.orm.QueryUtil#ALL_POS</code> will return the full result set. If <code>orderByComparator</code> is specified, then the query will include the given ORDER BY logic. If <code>orderByComparator</code> is absent, then the query will include the default ORDER BY logic from <code>com.liferay.layout.page.template.model.impl.LayoutPageTemplateEntryModelImpl</code>.
	 * </p>
	 *
	 * @param groupId the group ID
	 * @param classNameId the class name ID
	 * @param classTypeKey the class type key
	 * @param type the type
	 * @param start the lower bound of the range of layout page template entries
	 * @param end the upper bound of the range of layout page template entries (not inclusive)
	 * @param orderByComparator the comparator to order the results by (optionally <code>null</code>)
	 * @param useFinderCache whether to use the finder cache
	 * @return the ordered range of matching layout page template entries
	 */
	public java.util.List<LayoutPageTemplateEntry> findByG_C_C_T(
		long groupId, long classNameId, String classTypeKey, int type,
		int start, int end,
		com.liferay.portal.kernel.util.OrderByComparator
			<LayoutPageTemplateEntry> orderByComparator,
		boolean useFinderCache);

	/**
	 * Returns the first layout page template entry in the ordered set where groupId = &#63; and classNameId = &#63; and classTypeKey = &#63; and type = &#63;.
	 *
	 * @param groupId the group ID
	 * @param classNameId the class name ID
	 * @param classTypeKey the class type key
	 * @param type the type
	 * @param orderByComparator the comparator to order the set by (optionally <code>null</code>)
	 * @return the first matching layout page template entry
	 * @throws NoSuchPageTemplateEntryException if a matching layout page template entry could not be found
	 */
	public LayoutPageTemplateEntry findByG_C_C_T_First(
			long groupId, long classNameId, String classTypeKey, int type,
			com.liferay.portal.kernel.util.OrderByComparator
				<LayoutPageTemplateEntry> orderByComparator)
		throws NoSuchPageTemplateEntryException;

	/**
	 * Returns the first layout page template entry in the ordered set where groupId = &#63; and classNameId = &#63; and classTypeKey = &#63; and type = &#63;.
	 *
	 * @param groupId the group ID
	 * @param classNameId the class name ID
	 * @param classTypeKey the class type key
	 * @param type the type
	 * @param orderByComparator the comparator to order the set by (optionally <code>null</code>)
	 * @return the first matching layout page template entry, or <code>null</code> if a matching layout page template entry could not be found
	 */
	public LayoutPageTemplateEntry fetchByG_C_C_T_First(
		long groupId, long classNameId, String classTypeKey, int type,
		com.liferay.portal.kernel.util.OrderByComparator
			<LayoutPageTemplateEntry> orderByComparator);

	/**
	 * Returns an ordered range of all the layout page template entries that the user has permissions to view where groupId = &#63; and classNameId = &#63; and classTypeKey = &#63; and type = &#63;.
	 *
	 * <p>
	 * Useful when paginating results. Returns a maximum of <code>end - start</code> instances. <code>start</code> and <code>end</code> are not primary keys, they are indexes in the result set. Thus, <code>0</code> refers to the first result in the set. Setting both <code>start</code> and <code>end</code> to <code>com.liferay.portal.kernel.dao.orm.QueryUtil#ALL_POS</code> will return the full result set. If <code>orderByComparator</code> is specified, then the query will include the given ORDER BY logic. If <code>orderByComparator</code> is absent, then the query will include the default ORDER BY logic from <code>com.liferay.layout.page.template.model.impl.LayoutPageTemplateEntryModelImpl</code>.
	 * </p>
	 *
	 * @param groupId the group ID
	 * @param classNameId the class name ID
	 * @param classTypeKey the class type key
	 * @param type the type
	 * @param start the lower bound of the range of layout page template entries
	 * @param end the upper bound of the range of layout page template entries (not inclusive)
	 * @param orderByComparator the comparator to order the results by (optionally <code>null</code>)
	 * @return the ordered range of matching layout page template entries that the user has permission to view
	 */
	public java.util.List<LayoutPageTemplateEntry> filterFindByG_C_C_T(
		long groupId, long classNameId, String classTypeKey, int type,
		int start, int end,
		com.liferay.portal.kernel.util.OrderByComparator
			<LayoutPageTemplateEntry> orderByComparator);

	/**
	 * Returns an ordered range of all the layout page template entries that the user has permission to view where groupId = any &#63; and classNameId = &#63; and classTypeKey = &#63; and type = &#63;.
	 *
	 * <p>
	 * Useful when paginating results. Returns a maximum of <code>end - start</code> instances. <code>start</code> and <code>end</code> are not primary keys, they are indexes in the result set. Thus, <code>0</code> refers to the first result in the set. Setting both <code>start</code> and <code>end</code> to <code>com.liferay.portal.kernel.dao.orm.QueryUtil#ALL_POS</code> will return the full result set. If <code>orderByComparator</code> is specified, then the query will include the given ORDER BY logic. If <code>orderByComparator</code> is absent, then the query will include the default ORDER BY logic from <code>com.liferay.layout.page.template.model.impl.LayoutPageTemplateEntryModelImpl</code>.
	 * </p>
	 *
	 * @param groupIds the group IDs
	 * @param classNameId the class name ID
	 * @param classTypeKey the class type key
	 * @param type the type
	 * @param start the lower bound of the range of layout page template entries
	 * @param end the upper bound of the range of layout page template entries (not inclusive)
	 * @param orderByComparator the comparator to order the results by (optionally <code>null</code>)
	 * @return the ordered range of matching layout page template entries that the user has permission to view
	 */
	public java.util.List<LayoutPageTemplateEntry> filterFindByG_C_C_T(
		long[] groupIds, long classNameId, String classTypeKey, int type,
		int start, int end,
		com.liferay.portal.kernel.util.OrderByComparator
			<LayoutPageTemplateEntry> orderByComparator);

	/**
	 * Returns an ordered range of all the layout page template entries where groupId = &#63; and classNameId = &#63; and classTypeKey = &#63; and type = &#63;, optionally using the finder cache.
	 *
	 * <p>
	 * Useful when paginating results. Returns a maximum of <code>end - start</code> instances. <code>start</code> and <code>end</code> are not primary keys, they are indexes in the result set. Thus, <code>0</code> refers to the first result in the set. Setting both <code>start</code> and <code>end</code> to <code>com.liferay.portal.kernel.dao.orm.QueryUtil#ALL_POS</code> will return the full result set. If <code>orderByComparator</code> is specified, then the query will include the given ORDER BY logic. If <code>orderByComparator</code> is absent, then the query will include the default ORDER BY logic from <code>com.liferay.layout.page.template.model.impl.LayoutPageTemplateEntryModelImpl</code>.
	 * </p>
	 *
	 * @param groupIds the group IDs
	 * @param classNameId the class name ID
	 * @param classTypeKey the class type key
	 * @param type the type
	 * @param start the lower bound of the range of layout page template entries
	 * @param end the upper bound of the range of layout page template entries (not inclusive)
	 * @param orderByComparator the comparator to order the results by (optionally <code>null</code>)
	 * @param useFinderCache whether to use the finder cache
	 * @return the ordered range of matching layout page template entries
	 */
	public java.util.List<LayoutPageTemplateEntry> findByG_C_C_T(
		long[] groupIds, long classNameId, String classTypeKey, int type,
		int start, int end,
		com.liferay.portal.kernel.util.OrderByComparator
			<LayoutPageTemplateEntry> orderByComparator,
		boolean useFinderCache);

	/**
	 * Removes all the layout page template entries where groupId = &#63; and classNameId = &#63; and classTypeKey = &#63; and type = &#63; from the database.
	 *
	 * @param groupId the group ID
	 * @param classNameId the class name ID
	 * @param classTypeKey the class type key
	 * @param type the type
	 */
	public void removeByG_C_C_T(
		long groupId, long classNameId, String classTypeKey, int type);

	/**
	 * Returns the number of layout page template entries where groupId = &#63; and classNameId = &#63; and classTypeKey = &#63; and type = &#63;.
	 *
	 * @param groupId the group ID
	 * @param classNameId the class name ID
	 * @param classTypeKey the class type key
	 * @param type the type
	 * @return the number of matching layout page template entries
	 */
	public int countByG_C_C_T(
		long groupId, long classNameId, String classTypeKey, int type);

	/**
	 * Returns the number of layout page template entries where groupId = any &#63; and classNameId = &#63; and classTypeKey = &#63; and type = &#63;.
	 *
	 * @param groupIds the group IDs
	 * @param classNameId the class name ID
	 * @param classTypeKey the class type key
	 * @param type the type
	 * @return the number of matching layout page template entries
	 */
	public int countByG_C_C_T(
		long[] groupIds, long classNameId, String classTypeKey, int type);

	/**
	 * Returns the number of layout page template entries that the user has permission to view where groupId = &#63; and classNameId = &#63; and classTypeKey = &#63; and type = &#63;.
	 *
	 * @param groupId the group ID
	 * @param classNameId the class name ID
	 * @param classTypeKey the class type key
	 * @param type the type
	 * @return the number of matching layout page template entries that the user has permission to view
	 */
	public int filterCountByG_C_C_T(
		long groupId, long classNameId, String classTypeKey, int type);

	/**
	 * Returns the number of layout page template entries that the user has permission to view where groupId = any &#63; and classNameId = &#63; and classTypeKey = &#63; and type = &#63;.
	 *
	 * @param groupIds the group IDs
	 * @param classNameId the class name ID
	 * @param classTypeKey the class type key
	 * @param type the type
	 * @return the number of matching layout page template entries that the user has permission to view
	 */
	public int filterCountByG_C_C_T(
		long[] groupIds, long classNameId, String classTypeKey, int type);

	/**
	 * Returns an ordered range of all the layout page template entries where groupId = &#63; and classNameId = &#63; and classTypeKey = &#63; and defaultTemplate = &#63;.
	 *
	 * <p>
	 * Useful when paginating results. Returns a maximum of <code>end - start</code> instances. <code>start</code> and <code>end</code> are not primary keys, they are indexes in the result set. Thus, <code>0</code> refers to the first result in the set. Setting both <code>start</code> and <code>end</code> to <code>com.liferay.portal.kernel.dao.orm.QueryUtil#ALL_POS</code> will return the full result set. If <code>orderByComparator</code> is specified, then the query will include the given ORDER BY logic. If <code>orderByComparator</code> is absent, then the query will include the default ORDER BY logic from <code>com.liferay.layout.page.template.model.impl.LayoutPageTemplateEntryModelImpl</code>.
	 * </p>
	 *
	 * @param groupId the group ID
	 * @param classNameId the class name ID
	 * @param classTypeKey the class type key
	 * @param defaultTemplate the default template
	 * @param start the lower bound of the range of layout page template entries
	 * @param end the upper bound of the range of layout page template entries (not inclusive)
	 * @param orderByComparator the comparator to order the results by (optionally <code>null</code>)
	 * @param useFinderCache whether to use the finder cache
	 * @return the ordered range of matching layout page template entries
	 */
	public java.util.List<LayoutPageTemplateEntry> findByG_C_C_D(
		long groupId, long classNameId, String classTypeKey,
		boolean defaultTemplate, int start, int end,
		com.liferay.portal.kernel.util.OrderByComparator
			<LayoutPageTemplateEntry> orderByComparator,
		boolean useFinderCache);

	/**
	 * Returns the first layout page template entry in the ordered set where groupId = &#63; and classNameId = &#63; and classTypeKey = &#63; and defaultTemplate = &#63;.
	 *
	 * @param groupId the group ID
	 * @param classNameId the class name ID
	 * @param classTypeKey the class type key
	 * @param defaultTemplate the default template
	 * @param orderByComparator the comparator to order the set by (optionally <code>null</code>)
	 * @return the first matching layout page template entry
	 * @throws NoSuchPageTemplateEntryException if a matching layout page template entry could not be found
	 */
	public LayoutPageTemplateEntry findByG_C_C_D_First(
			long groupId, long classNameId, String classTypeKey,
			boolean defaultTemplate,
			com.liferay.portal.kernel.util.OrderByComparator
				<LayoutPageTemplateEntry> orderByComparator)
		throws NoSuchPageTemplateEntryException;

	/**
	 * Returns the first layout page template entry in the ordered set where groupId = &#63; and classNameId = &#63; and classTypeKey = &#63; and defaultTemplate = &#63;.
	 *
	 * @param groupId the group ID
	 * @param classNameId the class name ID
	 * @param classTypeKey the class type key
	 * @param defaultTemplate the default template
	 * @param orderByComparator the comparator to order the set by (optionally <code>null</code>)
	 * @return the first matching layout page template entry, or <code>null</code> if a matching layout page template entry could not be found
	 */
	public LayoutPageTemplateEntry fetchByG_C_C_D_First(
		long groupId, long classNameId, String classTypeKey,
		boolean defaultTemplate,
		com.liferay.portal.kernel.util.OrderByComparator
			<LayoutPageTemplateEntry> orderByComparator);

	/**
	 * Returns an ordered range of all the layout page template entries that the user has permissions to view where groupId = &#63; and classNameId = &#63; and classTypeKey = &#63; and defaultTemplate = &#63;.
	 *
	 * <p>
	 * Useful when paginating results. Returns a maximum of <code>end - start</code> instances. <code>start</code> and <code>end</code> are not primary keys, they are indexes in the result set. Thus, <code>0</code> refers to the first result in the set. Setting both <code>start</code> and <code>end</code> to <code>com.liferay.portal.kernel.dao.orm.QueryUtil#ALL_POS</code> will return the full result set. If <code>orderByComparator</code> is specified, then the query will include the given ORDER BY logic. If <code>orderByComparator</code> is absent, then the query will include the default ORDER BY logic from <code>com.liferay.layout.page.template.model.impl.LayoutPageTemplateEntryModelImpl</code>.
	 * </p>
	 *
	 * @param groupId the group ID
	 * @param classNameId the class name ID
	 * @param classTypeKey the class type key
	 * @param defaultTemplate the default template
	 * @param start the lower bound of the range of layout page template entries
	 * @param end the upper bound of the range of layout page template entries (not inclusive)
	 * @param orderByComparator the comparator to order the results by (optionally <code>null</code>)
	 * @return the ordered range of matching layout page template entries that the user has permission to view
	 */
	public java.util.List<LayoutPageTemplateEntry> filterFindByG_C_C_D(
		long groupId, long classNameId, String classTypeKey,
		boolean defaultTemplate, int start, int end,
		com.liferay.portal.kernel.util.OrderByComparator
			<LayoutPageTemplateEntry> orderByComparator);

	/**
	 * Removes all the layout page template entries where groupId = &#63; and classNameId = &#63; and classTypeKey = &#63; and defaultTemplate = &#63; from the database.
	 *
	 * @param groupId the group ID
	 * @param classNameId the class name ID
	 * @param classTypeKey the class type key
	 * @param defaultTemplate the default template
	 */
	public void removeByG_C_C_D(
		long groupId, long classNameId, String classTypeKey,
		boolean defaultTemplate);

	/**
	 * Returns the number of layout page template entries where groupId = &#63; and classNameId = &#63; and classTypeKey = &#63; and defaultTemplate = &#63;.
	 *
	 * @param groupId the group ID
	 * @param classNameId the class name ID
	 * @param classTypeKey the class type key
	 * @param defaultTemplate the default template
	 * @return the number of matching layout page template entries
	 */
	public int countByG_C_C_D(
		long groupId, long classNameId, String classTypeKey,
		boolean defaultTemplate);

	/**
	 * Returns the number of layout page template entries that the user has permission to view where groupId = &#63; and classNameId = &#63; and classTypeKey = &#63; and defaultTemplate = &#63;.
	 *
	 * @param groupId the group ID
	 * @param classNameId the class name ID
	 * @param classTypeKey the class type key
	 * @param defaultTemplate the default template
	 * @return the number of matching layout page template entries that the user has permission to view
	 */
	public int filterCountByG_C_C_D(
		long groupId, long classNameId, String classTypeKey,
		boolean defaultTemplate);

	/**
	 * Returns an ordered range of all the layout page template entries where groupId = &#63; and classNameId = &#63; and type = &#63; and defaultTemplate = &#63;.
	 *
	 * <p>
	 * Useful when paginating results. Returns a maximum of <code>end - start</code> instances. <code>start</code> and <code>end</code> are not primary keys, they are indexes in the result set. Thus, <code>0</code> refers to the first result in the set. Setting both <code>start</code> and <code>end</code> to <code>com.liferay.portal.kernel.dao.orm.QueryUtil#ALL_POS</code> will return the full result set. If <code>orderByComparator</code> is specified, then the query will include the given ORDER BY logic. If <code>orderByComparator</code> is absent, then the query will include the default ORDER BY logic from <code>com.liferay.layout.page.template.model.impl.LayoutPageTemplateEntryModelImpl</code>.
	 * </p>
	 *
	 * @param groupId the group ID
	 * @param classNameId the class name ID
	 * @param type the type
	 * @param defaultTemplate the default template
	 * @param start the lower bound of the range of layout page template entries
	 * @param end the upper bound of the range of layout page template entries (not inclusive)
	 * @param orderByComparator the comparator to order the results by (optionally <code>null</code>)
	 * @param useFinderCache whether to use the finder cache
	 * @return the ordered range of matching layout page template entries
	 */
	public java.util.List<LayoutPageTemplateEntry> findByG_C_T_D(
		long groupId, long classNameId, int type, boolean defaultTemplate,
		int start, int end,
		com.liferay.portal.kernel.util.OrderByComparator
			<LayoutPageTemplateEntry> orderByComparator,
		boolean useFinderCache);

	/**
	 * Returns the first layout page template entry in the ordered set where groupId = &#63; and classNameId = &#63; and type = &#63; and defaultTemplate = &#63;.
	 *
	 * @param groupId the group ID
	 * @param classNameId the class name ID
	 * @param type the type
	 * @param defaultTemplate the default template
	 * @param orderByComparator the comparator to order the set by (optionally <code>null</code>)
	 * @return the first matching layout page template entry
	 * @throws NoSuchPageTemplateEntryException if a matching layout page template entry could not be found
	 */
	public LayoutPageTemplateEntry findByG_C_T_D_First(
			long groupId, long classNameId, int type, boolean defaultTemplate,
			com.liferay.portal.kernel.util.OrderByComparator
				<LayoutPageTemplateEntry> orderByComparator)
		throws NoSuchPageTemplateEntryException;

	/**
	 * Returns the first layout page template entry in the ordered set where groupId = &#63; and classNameId = &#63; and type = &#63; and defaultTemplate = &#63;.
	 *
	 * @param groupId the group ID
	 * @param classNameId the class name ID
	 * @param type the type
	 * @param defaultTemplate the default template
	 * @param orderByComparator the comparator to order the set by (optionally <code>null</code>)
	 * @return the first matching layout page template entry, or <code>null</code> if a matching layout page template entry could not be found
	 */
	public LayoutPageTemplateEntry fetchByG_C_T_D_First(
		long groupId, long classNameId, int type, boolean defaultTemplate,
		com.liferay.portal.kernel.util.OrderByComparator
			<LayoutPageTemplateEntry> orderByComparator);

	/**
	 * Returns an ordered range of all the layout page template entries that the user has permissions to view where groupId = &#63; and classNameId = &#63; and type = &#63; and defaultTemplate = &#63;.
	 *
	 * <p>
	 * Useful when paginating results. Returns a maximum of <code>end - start</code> instances. <code>start</code> and <code>end</code> are not primary keys, they are indexes in the result set. Thus, <code>0</code> refers to the first result in the set. Setting both <code>start</code> and <code>end</code> to <code>com.liferay.portal.kernel.dao.orm.QueryUtil#ALL_POS</code> will return the full result set. If <code>orderByComparator</code> is specified, then the query will include the given ORDER BY logic. If <code>orderByComparator</code> is absent, then the query will include the default ORDER BY logic from <code>com.liferay.layout.page.template.model.impl.LayoutPageTemplateEntryModelImpl</code>.
	 * </p>
	 *
	 * @param groupId the group ID
	 * @param classNameId the class name ID
	 * @param type the type
	 * @param defaultTemplate the default template
	 * @param start the lower bound of the range of layout page template entries
	 * @param end the upper bound of the range of layout page template entries (not inclusive)
	 * @param orderByComparator the comparator to order the results by (optionally <code>null</code>)
	 * @return the ordered range of matching layout page template entries that the user has permission to view
	 */
	public java.util.List<LayoutPageTemplateEntry> filterFindByG_C_T_D(
		long groupId, long classNameId, int type, boolean defaultTemplate,
		int start, int end,
		com.liferay.portal.kernel.util.OrderByComparator
			<LayoutPageTemplateEntry> orderByComparator);

	/**
	 * Removes all the layout page template entries where groupId = &#63; and classNameId = &#63; and type = &#63; and defaultTemplate = &#63; from the database.
	 *
	 * @param groupId the group ID
	 * @param classNameId the class name ID
	 * @param type the type
	 * @param defaultTemplate the default template
	 */
	public void removeByG_C_T_D(
		long groupId, long classNameId, int type, boolean defaultTemplate);

	/**
	 * Returns the number of layout page template entries where groupId = &#63; and classNameId = &#63; and type = &#63; and defaultTemplate = &#63;.
	 *
	 * @param groupId the group ID
	 * @param classNameId the class name ID
	 * @param type the type
	 * @param defaultTemplate the default template
	 * @return the number of matching layout page template entries
	 */
	public int countByG_C_T_D(
		long groupId, long classNameId, int type, boolean defaultTemplate);

	/**
	 * Returns the number of layout page template entries that the user has permission to view where groupId = &#63; and classNameId = &#63; and type = &#63; and defaultTemplate = &#63;.
	 *
	 * @param groupId the group ID
	 * @param classNameId the class name ID
	 * @param type the type
	 * @param defaultTemplate the default template
	 * @return the number of matching layout page template entries that the user has permission to view
	 */
	public int filterCountByG_C_T_D(
		long groupId, long classNameId, int type, boolean defaultTemplate);

	/**
	 * Returns all the layout page template entries where groupId = &#63; and name LIKE &#63; and type = &#63; and status = &#63;.
	 *
	 * @param groupId the group ID
	 * @param name the name
	 * @param type the type
	 * @param status the status
	 * @return the matching layout page template entries
	 */
	public java.util.List<LayoutPageTemplateEntry> findByG_T_LikeN_S(
		long groupId, String name, int type, int status);

	/**
	 * Returns a range of all the layout page template entries where groupId = &#63; and name LIKE &#63; and type = &#63; and status = &#63;.
	 *
	 * <p>
	 * Useful when paginating results. Returns a maximum of <code>end - start</code> instances. <code>start</code> and <code>end</code> are not primary keys, they are indexes in the result set. Thus, <code>0</code> refers to the first result in the set. Setting both <code>start</code> and <code>end</code> to <code>com.liferay.portal.kernel.dao.orm.QueryUtil#ALL_POS</code> will return the full result set. If <code>orderByComparator</code> is specified, then the query will include the given ORDER BY logic. If <code>orderByComparator</code> is absent, then the query will include the default ORDER BY logic from <code>com.liferay.layout.page.template.model.impl.LayoutPageTemplateEntryModelImpl</code>.
	 * </p>
	 *
	 * @param groupId the group ID
	 * @param name the name
	 * @param type the type
	 * @param status the status
	 * @param start the lower bound of the range of layout page template entries
	 * @param end the upper bound of the range of layout page template entries (not inclusive)
	 * @return the range of matching layout page template entries
	 */
	public java.util.List<LayoutPageTemplateEntry> findByG_T_LikeN_S(
		long groupId, String name, int type, int status, int start, int end);

	/**
	 * Returns an ordered range of all the layout page template entries where groupId = &#63; and name LIKE &#63; and type = &#63; and status = &#63;.
	 *
	 * <p>
	 * Useful when paginating results. Returns a maximum of <code>end - start</code> instances. <code>start</code> and <code>end</code> are not primary keys, they are indexes in the result set. Thus, <code>0</code> refers to the first result in the set. Setting both <code>start</code> and <code>end</code> to <code>com.liferay.portal.kernel.dao.orm.QueryUtil#ALL_POS</code> will return the full result set. If <code>orderByComparator</code> is specified, then the query will include the given ORDER BY logic. If <code>orderByComparator</code> is absent, then the query will include the default ORDER BY logic from <code>com.liferay.layout.page.template.model.impl.LayoutPageTemplateEntryModelImpl</code>.
	 * </p>
	 *
	 * @param groupId the group ID
	 * @param name the name
	 * @param type the type
	 * @param status the status
	 * @param start the lower bound of the range of layout page template entries
	 * @param end the upper bound of the range of layout page template entries (not inclusive)
	 * @param orderByComparator the comparator to order the results by (optionally <code>null</code>)
	 * @return the ordered range of matching layout page template entries
	 */
	public java.util.List<LayoutPageTemplateEntry> findByG_T_LikeN_S(
		long groupId, String name, int type, int status, int start, int end,
		com.liferay.portal.kernel.util.OrderByComparator
			<LayoutPageTemplateEntry> orderByComparator);

	/**
	 * Returns an ordered range of all the layout page template entries where groupId = &#63; and name LIKE &#63; and type = &#63; and status = &#63;.
	 *
	 * <p>
	 * Useful when paginating results. Returns a maximum of <code>end - start</code> instances. <code>start</code> and <code>end</code> are not primary keys, they are indexes in the result set. Thus, <code>0</code> refers to the first result in the set. Setting both <code>start</code> and <code>end</code> to <code>com.liferay.portal.kernel.dao.orm.QueryUtil#ALL_POS</code> will return the full result set. If <code>orderByComparator</code> is specified, then the query will include the given ORDER BY logic. If <code>orderByComparator</code> is absent, then the query will include the default ORDER BY logic from <code>com.liferay.layout.page.template.model.impl.LayoutPageTemplateEntryModelImpl</code>.
	 * </p>
	 *
	 * @param groupId the group ID
	 * @param name the name
	 * @param type the type
	 * @param status the status
	 * @param start the lower bound of the range of layout page template entries
	 * @param end the upper bound of the range of layout page template entries (not inclusive)
	 * @param orderByComparator the comparator to order the results by (optionally <code>null</code>)
	 * @param useFinderCache whether to use the finder cache
	 * @return the ordered range of matching layout page template entries
	 */
	public java.util.List<LayoutPageTemplateEntry> findByG_T_LikeN_S(
		long groupId, String name, int type, int status, int start, int end,
		com.liferay.portal.kernel.util.OrderByComparator
			<LayoutPageTemplateEntry> orderByComparator,
		boolean useFinderCache);

	/**
	 * Returns the first layout page template entry in the ordered set where groupId = &#63; and name LIKE &#63; and type = &#63; and status = &#63;.
	 *
	 * @param groupId the group ID
	 * @param name the name
	 * @param type the type
	 * @param status the status
	 * @param orderByComparator the comparator to order the set by (optionally <code>null</code>)
	 * @return the first matching layout page template entry
	 * @throws NoSuchPageTemplateEntryException if a matching layout page template entry could not be found
	 */
	public LayoutPageTemplateEntry findByG_T_LikeN_S_First(
			long groupId, String name, int type, int status,
			com.liferay.portal.kernel.util.OrderByComparator
				<LayoutPageTemplateEntry> orderByComparator)
		throws NoSuchPageTemplateEntryException;

	/**
	 * Returns the first layout page template entry in the ordered set where groupId = &#63; and name LIKE &#63; and type = &#63; and status = &#63;.
	 *
	 * @param groupId the group ID
	 * @param name the name
	 * @param type the type
	 * @param status the status
	 * @param orderByComparator the comparator to order the set by (optionally <code>null</code>)
	 * @return the first matching layout page template entry, or <code>null</code> if a matching layout page template entry could not be found
	 */
	public LayoutPageTemplateEntry fetchByG_T_LikeN_S_First(
		long groupId, String name, int type, int status,
		com.liferay.portal.kernel.util.OrderByComparator
			<LayoutPageTemplateEntry> orderByComparator);

	/**
	 * Returns all the layout page template entries that the user has permission to view where groupId = &#63; and name LIKE &#63; and type = &#63; and status = &#63;.
	 *
	 * @param groupId the group ID
	 * @param name the name
	 * @param type the type
	 * @param status the status
	 * @return the matching layout page template entries that the user has permission to view
	 */
	public java.util.List<LayoutPageTemplateEntry> filterFindByG_T_LikeN_S(
		long groupId, String name, int type, int status);

	/**
	 * Returns a range of all the layout page template entries that the user has permission to view where groupId = &#63; and name LIKE &#63; and type = &#63; and status = &#63;.
	 *
	 * <p>
	 * Useful when paginating results. Returns a maximum of <code>end - start</code> instances. <code>start</code> and <code>end</code> are not primary keys, they are indexes in the result set. Thus, <code>0</code> refers to the first result in the set. Setting both <code>start</code> and <code>end</code> to <code>com.liferay.portal.kernel.dao.orm.QueryUtil#ALL_POS</code> will return the full result set. If <code>orderByComparator</code> is specified, then the query will include the given ORDER BY logic. If <code>orderByComparator</code> is absent, then the query will include the default ORDER BY logic from <code>com.liferay.layout.page.template.model.impl.LayoutPageTemplateEntryModelImpl</code>.
	 * </p>
	 *
	 * @param groupId the group ID
	 * @param name the name
	 * @param type the type
	 * @param status the status
	 * @param start the lower bound of the range of layout page template entries
	 * @param end the upper bound of the range of layout page template entries (not inclusive)
	 * @return the range of matching layout page template entries that the user has permission to view
	 */
	public java.util.List<LayoutPageTemplateEntry> filterFindByG_T_LikeN_S(
		long groupId, String name, int type, int status, int start, int end);

	/**
	 * Returns an ordered range of all the layout page template entries that the user has permissions to view where groupId = &#63; and name LIKE &#63; and type = &#63; and status = &#63;.
	 *
	 * <p>
	 * Useful when paginating results. Returns a maximum of <code>end - start</code> instances. <code>start</code> and <code>end</code> are not primary keys, they are indexes in the result set. Thus, <code>0</code> refers to the first result in the set. Setting both <code>start</code> and <code>end</code> to <code>com.liferay.portal.kernel.dao.orm.QueryUtil#ALL_POS</code> will return the full result set. If <code>orderByComparator</code> is specified, then the query will include the given ORDER BY logic. If <code>orderByComparator</code> is absent, then the query will include the default ORDER BY logic from <code>com.liferay.layout.page.template.model.impl.LayoutPageTemplateEntryModelImpl</code>.
	 * </p>
	 *
	 * @param groupId the group ID
	 * @param name the name
	 * @param type the type
	 * @param status the status
	 * @param start the lower bound of the range of layout page template entries
	 * @param end the upper bound of the range of layout page template entries (not inclusive)
	 * @param orderByComparator the comparator to order the results by (optionally <code>null</code>)
	 * @return the ordered range of matching layout page template entries that the user has permission to view
	 */
	public java.util.List<LayoutPageTemplateEntry> filterFindByG_T_LikeN_S(
		long groupId, String name, int type, int status, int start, int end,
		com.liferay.portal.kernel.util.OrderByComparator
			<LayoutPageTemplateEntry> orderByComparator);

	/**
	 * Returns all the layout page template entries that the user has permission to view where groupId = &#63; and name LIKE &#63; and type = any &#63; and status = &#63;.
	 *
	 * @param groupId the group ID
	 * @param name the name
	 * @param types the types
	 * @param status the status
	 * @return the matching layout page template entries that the user has permission to view
	 */
	public java.util.List<LayoutPageTemplateEntry> filterFindByG_T_LikeN_S(
		long groupId, String name, int[] types, int status);

	/**
	 * Returns a range of all the layout page template entries that the user has permission to view where groupId = &#63; and name LIKE &#63; and type = any &#63; and status = &#63;.
	 *
	 * <p>
	 * Useful when paginating results. Returns a maximum of <code>end - start</code> instances. <code>start</code> and <code>end</code> are not primary keys, they are indexes in the result set. Thus, <code>0</code> refers to the first result in the set. Setting both <code>start</code> and <code>end</code> to <code>com.liferay.portal.kernel.dao.orm.QueryUtil#ALL_POS</code> will return the full result set. If <code>orderByComparator</code> is specified, then the query will include the given ORDER BY logic. If <code>orderByComparator</code> is absent, then the query will include the default ORDER BY logic from <code>com.liferay.layout.page.template.model.impl.LayoutPageTemplateEntryModelImpl</code>.
	 * </p>
	 *
	 * @param groupId the group ID
	 * @param name the name
	 * @param types the types
	 * @param status the status
	 * @param start the lower bound of the range of layout page template entries
	 * @param end the upper bound of the range of layout page template entries (not inclusive)
	 * @return the range of matching layout page template entries that the user has permission to view
	 */
	public java.util.List<LayoutPageTemplateEntry> filterFindByG_T_LikeN_S(
		long groupId, String name, int[] types, int status, int start, int end);

	/**
	 * Returns an ordered range of all the layout page template entries that the user has permission to view where groupId = &#63; and name LIKE &#63; and type = any &#63; and status = &#63;.
	 *
	 * <p>
	 * Useful when paginating results. Returns a maximum of <code>end - start</code> instances. <code>start</code> and <code>end</code> are not primary keys, they are indexes in the result set. Thus, <code>0</code> refers to the first result in the set. Setting both <code>start</code> and <code>end</code> to <code>com.liferay.portal.kernel.dao.orm.QueryUtil#ALL_POS</code> will return the full result set. If <code>orderByComparator</code> is specified, then the query will include the given ORDER BY logic. If <code>orderByComparator</code> is absent, then the query will include the default ORDER BY logic from <code>com.liferay.layout.page.template.model.impl.LayoutPageTemplateEntryModelImpl</code>.
	 * </p>
	 *
	 * @param groupId the group ID
	 * @param name the name
	 * @param types the types
	 * @param status the status
	 * @param start the lower bound of the range of layout page template entries
	 * @param end the upper bound of the range of layout page template entries (not inclusive)
	 * @param orderByComparator the comparator to order the results by (optionally <code>null</code>)
	 * @return the ordered range of matching layout page template entries that the user has permission to view
	 */
	public java.util.List<LayoutPageTemplateEntry> filterFindByG_T_LikeN_S(
		long groupId, String name, int[] types, int status, int start, int end,
		com.liferay.portal.kernel.util.OrderByComparator
			<LayoutPageTemplateEntry> orderByComparator);

	/**
	 * Returns all the layout page template entries where groupId = &#63; and name LIKE &#63; and type = any &#63; and status = &#63;.
	 *
	 * <p>
	 * Useful when paginating results. Returns a maximum of <code>end - start</code> instances. <code>start</code> and <code>end</code> are not primary keys, they are indexes in the result set. Thus, <code>0</code> refers to the first result in the set. Setting both <code>start</code> and <code>end</code> to <code>com.liferay.portal.kernel.dao.orm.QueryUtil#ALL_POS</code> will return the full result set. If <code>orderByComparator</code> is specified, then the query will include the given ORDER BY logic. If <code>orderByComparator</code> is absent, then the query will include the default ORDER BY logic from <code>com.liferay.layout.page.template.model.impl.LayoutPageTemplateEntryModelImpl</code>.
	 * </p>
	 *
	 * @param groupId the group ID
	 * @param name the name
	 * @param types the types
	 * @param status the status
	 * @return the matching layout page template entries
	 */
	public java.util.List<LayoutPageTemplateEntry> findByG_T_LikeN_S(
		long groupId, String name, int[] types, int status);

	/**
	 * Returns a range of all the layout page template entries where groupId = &#63; and name LIKE &#63; and type = any &#63; and status = &#63;.
	 *
	 * <p>
	 * Useful when paginating results. Returns a maximum of <code>end - start</code> instances. <code>start</code> and <code>end</code> are not primary keys, they are indexes in the result set. Thus, <code>0</code> refers to the first result in the set. Setting both <code>start</code> and <code>end</code> to <code>com.liferay.portal.kernel.dao.orm.QueryUtil#ALL_POS</code> will return the full result set. If <code>orderByComparator</code> is specified, then the query will include the given ORDER BY logic. If <code>orderByComparator</code> is absent, then the query will include the default ORDER BY logic from <code>com.liferay.layout.page.template.model.impl.LayoutPageTemplateEntryModelImpl</code>.
	 * </p>
	 *
	 * @param groupId the group ID
	 * @param name the name
	 * @param types the types
	 * @param status the status
	 * @param start the lower bound of the range of layout page template entries
	 * @param end the upper bound of the range of layout page template entries (not inclusive)
	 * @return the range of matching layout page template entries
	 */
	public java.util.List<LayoutPageTemplateEntry> findByG_T_LikeN_S(
		long groupId, String name, int[] types, int status, int start, int end);

	/**
	 * Returns an ordered range of all the layout page template entries where groupId = &#63; and name LIKE &#63; and type = any &#63; and status = &#63;.
	 *
	 * <p>
	 * Useful when paginating results. Returns a maximum of <code>end - start</code> instances. <code>start</code> and <code>end</code> are not primary keys, they are indexes in the result set. Thus, <code>0</code> refers to the first result in the set. Setting both <code>start</code> and <code>end</code> to <code>com.liferay.portal.kernel.dao.orm.QueryUtil#ALL_POS</code> will return the full result set. If <code>orderByComparator</code> is specified, then the query will include the given ORDER BY logic. If <code>orderByComparator</code> is absent, then the query will include the default ORDER BY logic from <code>com.liferay.layout.page.template.model.impl.LayoutPageTemplateEntryModelImpl</code>.
	 * </p>
	 *
	 * @param groupId the group ID
	 * @param name the name
	 * @param types the types
	 * @param status the status
	 * @param start the lower bound of the range of layout page template entries
	 * @param end the upper bound of the range of layout page template entries (not inclusive)
	 * @param orderByComparator the comparator to order the results by (optionally <code>null</code>)
	 * @return the ordered range of matching layout page template entries
	 */
	public java.util.List<LayoutPageTemplateEntry> findByG_T_LikeN_S(
		long groupId, String name, int[] types, int status, int start, int end,
		com.liferay.portal.kernel.util.OrderByComparator
			<LayoutPageTemplateEntry> orderByComparator);

	/**
	 * Returns an ordered range of all the layout page template entries where groupId = &#63; and name LIKE &#63; and type = &#63; and status = &#63;, optionally using the finder cache.
	 *
	 * <p>
	 * Useful when paginating results. Returns a maximum of <code>end - start</code> instances. <code>start</code> and <code>end</code> are not primary keys, they are indexes in the result set. Thus, <code>0</code> refers to the first result in the set. Setting both <code>start</code> and <code>end</code> to <code>com.liferay.portal.kernel.dao.orm.QueryUtil#ALL_POS</code> will return the full result set. If <code>orderByComparator</code> is specified, then the query will include the given ORDER BY logic. If <code>orderByComparator</code> is absent, then the query will include the default ORDER BY logic from <code>com.liferay.layout.page.template.model.impl.LayoutPageTemplateEntryModelImpl</code>.
	 * </p>
	 *
	 * @param groupId the group ID
	 * @param name the name
	 * @param types the types
	 * @param status the status
	 * @param start the lower bound of the range of layout page template entries
	 * @param end the upper bound of the range of layout page template entries (not inclusive)
	 * @param orderByComparator the comparator to order the results by (optionally <code>null</code>)
	 * @param useFinderCache whether to use the finder cache
	 * @return the ordered range of matching layout page template entries
	 */
	public java.util.List<LayoutPageTemplateEntry> findByG_T_LikeN_S(
		long groupId, String name, int[] types, int status, int start, int end,
		com.liferay.portal.kernel.util.OrderByComparator
			<LayoutPageTemplateEntry> orderByComparator,
		boolean useFinderCache);

	/**
	 * Removes all the layout page template entries where groupId = &#63; and name LIKE &#63; and type = &#63; and status = &#63; from the database.
	 *
	 * @param groupId the group ID
	 * @param name the name
	 * @param type the type
	 * @param status the status
	 */
	public void removeByG_T_LikeN_S(
		long groupId, String name, int type, int status);

	/**
	 * Returns the number of layout page template entries where groupId = &#63; and name LIKE &#63; and type = &#63; and status = &#63;.
	 *
	 * @param groupId the group ID
	 * @param name the name
	 * @param type the type
	 * @param status the status
	 * @return the number of matching layout page template entries
	 */
	public int countByG_T_LikeN_S(
		long groupId, String name, int type, int status);

	/**
	 * Returns the number of layout page template entries where groupId = &#63; and name LIKE &#63; and type = any &#63; and status = &#63;.
	 *
	 * @param groupId the group ID
	 * @param name the name
	 * @param types the types
	 * @param status the status
	 * @return the number of matching layout page template entries
	 */
	public int countByG_T_LikeN_S(
		long groupId, String name, int[] types, int status);

	/**
	 * Returns the number of layout page template entries that the user has permission to view where groupId = &#63; and name LIKE &#63; and type = &#63; and status = &#63;.
	 *
	 * @param groupId the group ID
	 * @param name the name
	 * @param type the type
	 * @param status the status
	 * @return the number of matching layout page template entries that the user has permission to view
	 */
	public int filterCountByG_T_LikeN_S(
		long groupId, String name, int type, int status);

	/**
	 * Returns the number of layout page template entries that the user has permission to view where groupId = &#63; and name LIKE &#63; and type = any &#63; and status = &#63;.
	 *
	 * @param groupId the group ID
	 * @param name the name
	 * @param types the types
	 * @param status the status
	 * @return the number of matching layout page template entries that the user has permission to view
	 */
	public int filterCountByG_T_LikeN_S(
		long groupId, String name, int[] types, int status);

	/**
	 * Returns an ordered range of all the layout page template entries where groupId = &#63; and type = &#63; and defaultTemplate = &#63; and status = &#63;.
	 *
	 * <p>
	 * Useful when paginating results. Returns a maximum of <code>end - start</code> instances. <code>start</code> and <code>end</code> are not primary keys, they are indexes in the result set. Thus, <code>0</code> refers to the first result in the set. Setting both <code>start</code> and <code>end</code> to <code>com.liferay.portal.kernel.dao.orm.QueryUtil#ALL_POS</code> will return the full result set. If <code>orderByComparator</code> is specified, then the query will include the given ORDER BY logic. If <code>orderByComparator</code> is absent, then the query will include the default ORDER BY logic from <code>com.liferay.layout.page.template.model.impl.LayoutPageTemplateEntryModelImpl</code>.
	 * </p>
	 *
	 * @param groupId the group ID
	 * @param type the type
	 * @param defaultTemplate the default template
	 * @param status the status
	 * @param start the lower bound of the range of layout page template entries
	 * @param end the upper bound of the range of layout page template entries (not inclusive)
	 * @param orderByComparator the comparator to order the results by (optionally <code>null</code>)
	 * @param useFinderCache whether to use the finder cache
	 * @return the ordered range of matching layout page template entries
	 */
	public java.util.List<LayoutPageTemplateEntry> findByG_T_D_S(
		long groupId, int type, boolean defaultTemplate, int status, int start,
		int end,
		com.liferay.portal.kernel.util.OrderByComparator
			<LayoutPageTemplateEntry> orderByComparator,
		boolean useFinderCache);

	/**
	 * Returns the first layout page template entry in the ordered set where groupId = &#63; and type = &#63; and defaultTemplate = &#63; and status = &#63;.
	 *
	 * @param groupId the group ID
	 * @param type the type
	 * @param defaultTemplate the default template
	 * @param status the status
	 * @param orderByComparator the comparator to order the set by (optionally <code>null</code>)
	 * @return the first matching layout page template entry
	 * @throws NoSuchPageTemplateEntryException if a matching layout page template entry could not be found
	 */
	public LayoutPageTemplateEntry findByG_T_D_S_First(
			long groupId, int type, boolean defaultTemplate, int status,
			com.liferay.portal.kernel.util.OrderByComparator
				<LayoutPageTemplateEntry> orderByComparator)
		throws NoSuchPageTemplateEntryException;

	/**
	 * Returns the first layout page template entry in the ordered set where groupId = &#63; and type = &#63; and defaultTemplate = &#63; and status = &#63;.
	 *
	 * @param groupId the group ID
	 * @param type the type
	 * @param defaultTemplate the default template
	 * @param status the status
	 * @param orderByComparator the comparator to order the set by (optionally <code>null</code>)
	 * @return the first matching layout page template entry, or <code>null</code> if a matching layout page template entry could not be found
	 */
	public LayoutPageTemplateEntry fetchByG_T_D_S_First(
		long groupId, int type, boolean defaultTemplate, int status,
		com.liferay.portal.kernel.util.OrderByComparator
			<LayoutPageTemplateEntry> orderByComparator);

	/**
	 * Returns an ordered range of all the layout page template entries that the user has permissions to view where groupId = &#63; and type = &#63; and defaultTemplate = &#63; and status = &#63;.
	 *
	 * <p>
	 * Useful when paginating results. Returns a maximum of <code>end - start</code> instances. <code>start</code> and <code>end</code> are not primary keys, they are indexes in the result set. Thus, <code>0</code> refers to the first result in the set. Setting both <code>start</code> and <code>end</code> to <code>com.liferay.portal.kernel.dao.orm.QueryUtil#ALL_POS</code> will return the full result set. If <code>orderByComparator</code> is specified, then the query will include the given ORDER BY logic. If <code>orderByComparator</code> is absent, then the query will include the default ORDER BY logic from <code>com.liferay.layout.page.template.model.impl.LayoutPageTemplateEntryModelImpl</code>.
	 * </p>
	 *
	 * @param groupId the group ID
	 * @param type the type
	 * @param defaultTemplate the default template
	 * @param status the status
	 * @param start the lower bound of the range of layout page template entries
	 * @param end the upper bound of the range of layout page template entries (not inclusive)
	 * @param orderByComparator the comparator to order the results by (optionally <code>null</code>)
	 * @return the ordered range of matching layout page template entries that the user has permission to view
	 */
	public java.util.List<LayoutPageTemplateEntry> filterFindByG_T_D_S(
		long groupId, int type, boolean defaultTemplate, int status, int start,
		int end,
		com.liferay.portal.kernel.util.OrderByComparator
			<LayoutPageTemplateEntry> orderByComparator);

	/**
	 * Removes all the layout page template entries where groupId = &#63; and type = &#63; and defaultTemplate = &#63; and status = &#63; from the database.
	 *
	 * @param groupId the group ID
	 * @param type the type
	 * @param defaultTemplate the default template
	 * @param status the status
	 */
	public void removeByG_T_D_S(
		long groupId, int type, boolean defaultTemplate, int status);

	/**
	 * Returns the number of layout page template entries where groupId = &#63; and type = &#63; and defaultTemplate = &#63; and status = &#63;.
	 *
	 * @param groupId the group ID
	 * @param type the type
	 * @param defaultTemplate the default template
	 * @param status the status
	 * @return the number of matching layout page template entries
	 */
	public int countByG_T_D_S(
		long groupId, int type, boolean defaultTemplate, int status);

	/**
	 * Returns the number of layout page template entries that the user has permission to view where groupId = &#63; and type = &#63; and defaultTemplate = &#63; and status = &#63;.
	 *
	 * @param groupId the group ID
	 * @param type the type
	 * @param defaultTemplate the default template
	 * @param status the status
	 * @return the number of matching layout page template entries that the user has permission to view
	 */
	public int filterCountByG_T_D_S(
		long groupId, int type, boolean defaultTemplate, int status);

	/**
	 * Returns all the layout page template entries where groupId = &#63; and classNameId = &#63; and classTypeKey = &#63; and name LIKE &#63; and type = &#63;.
	 *
	 * @param groupId the group ID
	 * @param classNameId the class name ID
	 * @param classTypeKey the class type key
	 * @param name the name
	 * @param type the type
	 * @return the matching layout page template entries
	 */
	public java.util.List<LayoutPageTemplateEntry> findByG_C_C_LikeN_T(
		long groupId, long classNameId, String classTypeKey, String name,
		int type);

	/**
	 * Returns a range of all the layout page template entries where groupId = &#63; and classNameId = &#63; and classTypeKey = &#63; and name LIKE &#63; and type = &#63;.
	 *
	 * <p>
	 * Useful when paginating results. Returns a maximum of <code>end - start</code> instances. <code>start</code> and <code>end</code> are not primary keys, they are indexes in the result set. Thus, <code>0</code> refers to the first result in the set. Setting both <code>start</code> and <code>end</code> to <code>com.liferay.portal.kernel.dao.orm.QueryUtil#ALL_POS</code> will return the full result set. If <code>orderByComparator</code> is specified, then the query will include the given ORDER BY logic. If <code>orderByComparator</code> is absent, then the query will include the default ORDER BY logic from <code>com.liferay.layout.page.template.model.impl.LayoutPageTemplateEntryModelImpl</code>.
	 * </p>
	 *
	 * @param groupId the group ID
	 * @param classNameId the class name ID
	 * @param classTypeKey the class type key
	 * @param name the name
	 * @param type the type
	 * @param start the lower bound of the range of layout page template entries
	 * @param end the upper bound of the range of layout page template entries (not inclusive)
	 * @return the range of matching layout page template entries
	 */
	public java.util.List<LayoutPageTemplateEntry> findByG_C_C_LikeN_T(
		long groupId, long classNameId, String classTypeKey, String name,
		int type, int start, int end);

	/**
	 * Returns an ordered range of all the layout page template entries where groupId = &#63; and classNameId = &#63; and classTypeKey = &#63; and name LIKE &#63; and type = &#63;.
	 *
	 * <p>
	 * Useful when paginating results. Returns a maximum of <code>end - start</code> instances. <code>start</code> and <code>end</code> are not primary keys, they are indexes in the result set. Thus, <code>0</code> refers to the first result in the set. Setting both <code>start</code> and <code>end</code> to <code>com.liferay.portal.kernel.dao.orm.QueryUtil#ALL_POS</code> will return the full result set. If <code>orderByComparator</code> is specified, then the query will include the given ORDER BY logic. If <code>orderByComparator</code> is absent, then the query will include the default ORDER BY logic from <code>com.liferay.layout.page.template.model.impl.LayoutPageTemplateEntryModelImpl</code>.
	 * </p>
	 *
	 * @param groupId the group ID
	 * @param classNameId the class name ID
	 * @param classTypeKey the class type key
	 * @param name the name
	 * @param type the type
	 * @param start the lower bound of the range of layout page template entries
	 * @param end the upper bound of the range of layout page template entries (not inclusive)
	 * @param orderByComparator the comparator to order the results by (optionally <code>null</code>)
	 * @return the ordered range of matching layout page template entries
	 */
	public java.util.List<LayoutPageTemplateEntry> findByG_C_C_LikeN_T(
		long groupId, long classNameId, String classTypeKey, String name,
		int type, int start, int end,
		com.liferay.portal.kernel.util.OrderByComparator
			<LayoutPageTemplateEntry> orderByComparator);

	/**
	 * Returns an ordered range of all the layout page template entries where groupId = &#63; and classNameId = &#63; and classTypeKey = &#63; and name LIKE &#63; and type = &#63;.
	 *
	 * <p>
	 * Useful when paginating results. Returns a maximum of <code>end - start</code> instances. <code>start</code> and <code>end</code> are not primary keys, they are indexes in the result set. Thus, <code>0</code> refers to the first result in the set. Setting both <code>start</code> and <code>end</code> to <code>com.liferay.portal.kernel.dao.orm.QueryUtil#ALL_POS</code> will return the full result set. If <code>orderByComparator</code> is specified, then the query will include the given ORDER BY logic. If <code>orderByComparator</code> is absent, then the query will include the default ORDER BY logic from <code>com.liferay.layout.page.template.model.impl.LayoutPageTemplateEntryModelImpl</code>.
	 * </p>
	 *
	 * @param groupId the group ID
	 * @param classNameId the class name ID
	 * @param classTypeKey the class type key
	 * @param name the name
	 * @param type the type
	 * @param start the lower bound of the range of layout page template entries
	 * @param end the upper bound of the range of layout page template entries (not inclusive)
	 * @param orderByComparator the comparator to order the results by (optionally <code>null</code>)
	 * @param useFinderCache whether to use the finder cache
	 * @return the ordered range of matching layout page template entries
	 */
	public java.util.List<LayoutPageTemplateEntry> findByG_C_C_LikeN_T(
		long groupId, long classNameId, String classTypeKey, String name,
		int type, int start, int end,
		com.liferay.portal.kernel.util.OrderByComparator
			<LayoutPageTemplateEntry> orderByComparator,
		boolean useFinderCache);

	/**
	 * Returns the first layout page template entry in the ordered set where groupId = &#63; and classNameId = &#63; and classTypeKey = &#63; and name LIKE &#63; and type = &#63;.
	 *
	 * @param groupId the group ID
	 * @param classNameId the class name ID
	 * @param classTypeKey the class type key
	 * @param name the name
	 * @param type the type
	 * @param orderByComparator the comparator to order the set by (optionally <code>null</code>)
	 * @return the first matching layout page template entry
	 * @throws NoSuchPageTemplateEntryException if a matching layout page template entry could not be found
	 */
	public LayoutPageTemplateEntry findByG_C_C_LikeN_T_First(
			long groupId, long classNameId, String classTypeKey, String name,
			int type,
			com.liferay.portal.kernel.util.OrderByComparator
				<LayoutPageTemplateEntry> orderByComparator)
		throws NoSuchPageTemplateEntryException;

	/**
	 * Returns the first layout page template entry in the ordered set where groupId = &#63; and classNameId = &#63; and classTypeKey = &#63; and name LIKE &#63; and type = &#63;.
	 *
	 * @param groupId the group ID
	 * @param classNameId the class name ID
	 * @param classTypeKey the class type key
	 * @param name the name
	 * @param type the type
	 * @param orderByComparator the comparator to order the set by (optionally <code>null</code>)
	 * @return the first matching layout page template entry, or <code>null</code> if a matching layout page template entry could not be found
	 */
	public LayoutPageTemplateEntry fetchByG_C_C_LikeN_T_First(
		long groupId, long classNameId, String classTypeKey, String name,
		int type,
		com.liferay.portal.kernel.util.OrderByComparator
			<LayoutPageTemplateEntry> orderByComparator);

	/**
	 * Returns all the layout page template entries that the user has permission to view where groupId = &#63; and classNameId = &#63; and classTypeKey = &#63; and name LIKE &#63; and type = &#63;.
	 *
	 * @param groupId the group ID
	 * @param classNameId the class name ID
	 * @param classTypeKey the class type key
	 * @param name the name
	 * @param type the type
	 * @return the matching layout page template entries that the user has permission to view
	 */
	public java.util.List<LayoutPageTemplateEntry> filterFindByG_C_C_LikeN_T(
		long groupId, long classNameId, String classTypeKey, String name,
		int type);

	/**
	 * Returns a range of all the layout page template entries that the user has permission to view where groupId = &#63; and classNameId = &#63; and classTypeKey = &#63; and name LIKE &#63; and type = &#63;.
	 *
	 * <p>
	 * Useful when paginating results. Returns a maximum of <code>end - start</code> instances. <code>start</code> and <code>end</code> are not primary keys, they are indexes in the result set. Thus, <code>0</code> refers to the first result in the set. Setting both <code>start</code> and <code>end</code> to <code>com.liferay.portal.kernel.dao.orm.QueryUtil#ALL_POS</code> will return the full result set. If <code>orderByComparator</code> is specified, then the query will include the given ORDER BY logic. If <code>orderByComparator</code> is absent, then the query will include the default ORDER BY logic from <code>com.liferay.layout.page.template.model.impl.LayoutPageTemplateEntryModelImpl</code>.
	 * </p>
	 *
	 * @param groupId the group ID
	 * @param classNameId the class name ID
	 * @param classTypeKey the class type key
	 * @param name the name
	 * @param type the type
	 * @param start the lower bound of the range of layout page template entries
	 * @param end the upper bound of the range of layout page template entries (not inclusive)
	 * @return the range of matching layout page template entries that the user has permission to view
	 */
	public java.util.List<LayoutPageTemplateEntry> filterFindByG_C_C_LikeN_T(
		long groupId, long classNameId, String classTypeKey, String name,
		int type, int start, int end);

	/**
	 * Returns an ordered range of all the layout page template entries that the user has permissions to view where groupId = &#63; and classNameId = &#63; and classTypeKey = &#63; and name LIKE &#63; and type = &#63;.
	 *
	 * <p>
	 * Useful when paginating results. Returns a maximum of <code>end - start</code> instances. <code>start</code> and <code>end</code> are not primary keys, they are indexes in the result set. Thus, <code>0</code> refers to the first result in the set. Setting both <code>start</code> and <code>end</code> to <code>com.liferay.portal.kernel.dao.orm.QueryUtil#ALL_POS</code> will return the full result set. If <code>orderByComparator</code> is specified, then the query will include the given ORDER BY logic. If <code>orderByComparator</code> is absent, then the query will include the default ORDER BY logic from <code>com.liferay.layout.page.template.model.impl.LayoutPageTemplateEntryModelImpl</code>.
	 * </p>
	 *
	 * @param groupId the group ID
	 * @param classNameId the class name ID
	 * @param classTypeKey the class type key
	 * @param name the name
	 * @param type the type
	 * @param start the lower bound of the range of layout page template entries
	 * @param end the upper bound of the range of layout page template entries (not inclusive)
	 * @param orderByComparator the comparator to order the results by (optionally <code>null</code>)
	 * @return the ordered range of matching layout page template entries that the user has permission to view
	 */
	public java.util.List<LayoutPageTemplateEntry> filterFindByG_C_C_LikeN_T(
		long groupId, long classNameId, String classTypeKey, String name,
		int type, int start, int end,
		com.liferay.portal.kernel.util.OrderByComparator
			<LayoutPageTemplateEntry> orderByComparator);

	/**
	 * Returns all the layout page template entries that the user has permission to view where groupId = any &#63; and classNameId = &#63; and classTypeKey = &#63; and name LIKE &#63; and type = &#63;.
	 *
	 * @param groupIds the group IDs
	 * @param classNameId the class name ID
	 * @param classTypeKey the class type key
	 * @param name the name
	 * @param type the type
	 * @return the matching layout page template entries that the user has permission to view
	 */
	public java.util.List<LayoutPageTemplateEntry> filterFindByG_C_C_LikeN_T(
		long[] groupIds, long classNameId, String classTypeKey, String name,
		int type);

	/**
	 * Returns a range of all the layout page template entries that the user has permission to view where groupId = any &#63; and classNameId = &#63; and classTypeKey = &#63; and name LIKE &#63; and type = &#63;.
	 *
	 * <p>
	 * Useful when paginating results. Returns a maximum of <code>end - start</code> instances. <code>start</code> and <code>end</code> are not primary keys, they are indexes in the result set. Thus, <code>0</code> refers to the first result in the set. Setting both <code>start</code> and <code>end</code> to <code>com.liferay.portal.kernel.dao.orm.QueryUtil#ALL_POS</code> will return the full result set. If <code>orderByComparator</code> is specified, then the query will include the given ORDER BY logic. If <code>orderByComparator</code> is absent, then the query will include the default ORDER BY logic from <code>com.liferay.layout.page.template.model.impl.LayoutPageTemplateEntryModelImpl</code>.
	 * </p>
	 *
	 * @param groupIds the group IDs
	 * @param classNameId the class name ID
	 * @param classTypeKey the class type key
	 * @param name the name
	 * @param type the type
	 * @param start the lower bound of the range of layout page template entries
	 * @param end the upper bound of the range of layout page template entries (not inclusive)
	 * @return the range of matching layout page template entries that the user has permission to view
	 */
	public java.util.List<LayoutPageTemplateEntry> filterFindByG_C_C_LikeN_T(
		long[] groupIds, long classNameId, String classTypeKey, String name,
		int type, int start, int end);

	/**
	 * Returns an ordered range of all the layout page template entries that the user has permission to view where groupId = any &#63; and classNameId = &#63; and classTypeKey = &#63; and name LIKE &#63; and type = &#63;.
	 *
	 * <p>
	 * Useful when paginating results. Returns a maximum of <code>end - start</code> instances. <code>start</code> and <code>end</code> are not primary keys, they are indexes in the result set. Thus, <code>0</code> refers to the first result in the set. Setting both <code>start</code> and <code>end</code> to <code>com.liferay.portal.kernel.dao.orm.QueryUtil#ALL_POS</code> will return the full result set. If <code>orderByComparator</code> is specified, then the query will include the given ORDER BY logic. If <code>orderByComparator</code> is absent, then the query will include the default ORDER BY logic from <code>com.liferay.layout.page.template.model.impl.LayoutPageTemplateEntryModelImpl</code>.
	 * </p>
	 *
	 * @param groupIds the group IDs
	 * @param classNameId the class name ID
	 * @param classTypeKey the class type key
	 * @param name the name
	 * @param type the type
	 * @param start the lower bound of the range of layout page template entries
	 * @param end the upper bound of the range of layout page template entries (not inclusive)
	 * @param orderByComparator the comparator to order the results by (optionally <code>null</code>)
	 * @return the ordered range of matching layout page template entries that the user has permission to view
	 */
	public java.util.List<LayoutPageTemplateEntry> filterFindByG_C_C_LikeN_T(
		long[] groupIds, long classNameId, String classTypeKey, String name,
		int type, int start, int end,
		com.liferay.portal.kernel.util.OrderByComparator
			<LayoutPageTemplateEntry> orderByComparator);

	/**
	 * Returns all the layout page template entries where groupId = any &#63; and classNameId = &#63; and classTypeKey = &#63; and name LIKE &#63; and type = &#63;.
	 *
	 * <p>
	 * Useful when paginating results. Returns a maximum of <code>end - start</code> instances. <code>start</code> and <code>end</code> are not primary keys, they are indexes in the result set. Thus, <code>0</code> refers to the first result in the set. Setting both <code>start</code> and <code>end</code> to <code>com.liferay.portal.kernel.dao.orm.QueryUtil#ALL_POS</code> will return the full result set. If <code>orderByComparator</code> is specified, then the query will include the given ORDER BY logic. If <code>orderByComparator</code> is absent, then the query will include the default ORDER BY logic from <code>com.liferay.layout.page.template.model.impl.LayoutPageTemplateEntryModelImpl</code>.
	 * </p>
	 *
	 * @param groupIds the group IDs
	 * @param classNameId the class name ID
	 * @param classTypeKey the class type key
	 * @param name the name
	 * @param type the type
	 * @return the matching layout page template entries
	 */
	public java.util.List<LayoutPageTemplateEntry> findByG_C_C_LikeN_T(
		long[] groupIds, long classNameId, String classTypeKey, String name,
		int type);

	/**
	 * Returns a range of all the layout page template entries where groupId = any &#63; and classNameId = &#63; and classTypeKey = &#63; and name LIKE &#63; and type = &#63;.
	 *
	 * <p>
	 * Useful when paginating results. Returns a maximum of <code>end - start</code> instances. <code>start</code> and <code>end</code> are not primary keys, they are indexes in the result set. Thus, <code>0</code> refers to the first result in the set. Setting both <code>start</code> and <code>end</code> to <code>com.liferay.portal.kernel.dao.orm.QueryUtil#ALL_POS</code> will return the full result set. If <code>orderByComparator</code> is specified, then the query will include the given ORDER BY logic. If <code>orderByComparator</code> is absent, then the query will include the default ORDER BY logic from <code>com.liferay.layout.page.template.model.impl.LayoutPageTemplateEntryModelImpl</code>.
	 * </p>
	 *
	 * @param groupIds the group IDs
	 * @param classNameId the class name ID
	 * @param classTypeKey the class type key
	 * @param name the name
	 * @param type the type
	 * @param start the lower bound of the range of layout page template entries
	 * @param end the upper bound of the range of layout page template entries (not inclusive)
	 * @return the range of matching layout page template entries
	 */
	public java.util.List<LayoutPageTemplateEntry> findByG_C_C_LikeN_T(
		long[] groupIds, long classNameId, String classTypeKey, String name,
		int type, int start, int end);

	/**
	 * Returns an ordered range of all the layout page template entries where groupId = any &#63; and classNameId = &#63; and classTypeKey = &#63; and name LIKE &#63; and type = &#63;.
	 *
	 * <p>
	 * Useful when paginating results. Returns a maximum of <code>end - start</code> instances. <code>start</code> and <code>end</code> are not primary keys, they are indexes in the result set. Thus, <code>0</code> refers to the first result in the set. Setting both <code>start</code> and <code>end</code> to <code>com.liferay.portal.kernel.dao.orm.QueryUtil#ALL_POS</code> will return the full result set. If <code>orderByComparator</code> is specified, then the query will include the given ORDER BY logic. If <code>orderByComparator</code> is absent, then the query will include the default ORDER BY logic from <code>com.liferay.layout.page.template.model.impl.LayoutPageTemplateEntryModelImpl</code>.
	 * </p>
	 *
	 * @param groupIds the group IDs
	 * @param classNameId the class name ID
	 * @param classTypeKey the class type key
	 * @param name the name
	 * @param type the type
	 * @param start the lower bound of the range of layout page template entries
	 * @param end the upper bound of the range of layout page template entries (not inclusive)
	 * @param orderByComparator the comparator to order the results by (optionally <code>null</code>)
	 * @return the ordered range of matching layout page template entries
	 */
	public java.util.List<LayoutPageTemplateEntry> findByG_C_C_LikeN_T(
		long[] groupIds, long classNameId, String classTypeKey, String name,
		int type, int start, int end,
		com.liferay.portal.kernel.util.OrderByComparator
			<LayoutPageTemplateEntry> orderByComparator);

	/**
	 * Returns an ordered range of all the layout page template entries where groupId = &#63; and classNameId = &#63; and classTypeKey = &#63; and name LIKE &#63; and type = &#63;, optionally using the finder cache.
	 *
	 * <p>
	 * Useful when paginating results. Returns a maximum of <code>end - start</code> instances. <code>start</code> and <code>end</code> are not primary keys, they are indexes in the result set. Thus, <code>0</code> refers to the first result in the set. Setting both <code>start</code> and <code>end</code> to <code>com.liferay.portal.kernel.dao.orm.QueryUtil#ALL_POS</code> will return the full result set. If <code>orderByComparator</code> is specified, then the query will include the given ORDER BY logic. If <code>orderByComparator</code> is absent, then the query will include the default ORDER BY logic from <code>com.liferay.layout.page.template.model.impl.LayoutPageTemplateEntryModelImpl</code>.
	 * </p>
	 *
	 * @param groupIds the group IDs
	 * @param classNameId the class name ID
	 * @param classTypeKey the class type key
	 * @param name the name
	 * @param type the type
	 * @param start the lower bound of the range of layout page template entries
	 * @param end the upper bound of the range of layout page template entries (not inclusive)
	 * @param orderByComparator the comparator to order the results by (optionally <code>null</code>)
	 * @param useFinderCache whether to use the finder cache
	 * @return the ordered range of matching layout page template entries
	 */
	public java.util.List<LayoutPageTemplateEntry> findByG_C_C_LikeN_T(
		long[] groupIds, long classNameId, String classTypeKey, String name,
		int type, int start, int end,
		com.liferay.portal.kernel.util.OrderByComparator
			<LayoutPageTemplateEntry> orderByComparator,
		boolean useFinderCache);

	/**
	 * Removes all the layout page template entries where groupId = &#63; and classNameId = &#63; and classTypeKey = &#63; and name LIKE &#63; and type = &#63; from the database.
	 *
	 * @param groupId the group ID
	 * @param classNameId the class name ID
	 * @param classTypeKey the class type key
	 * @param name the name
	 * @param type the type
	 */
	public void removeByG_C_C_LikeN_T(
		long groupId, long classNameId, String classTypeKey, String name,
		int type);

	/**
	 * Returns the number of layout page template entries where groupId = &#63; and classNameId = &#63; and classTypeKey = &#63; and name LIKE &#63; and type = &#63;.
	 *
	 * @param groupId the group ID
	 * @param classNameId the class name ID
	 * @param classTypeKey the class type key
	 * @param name the name
	 * @param type the type
	 * @return the number of matching layout page template entries
	 */
	public int countByG_C_C_LikeN_T(
		long groupId, long classNameId, String classTypeKey, String name,
		int type);

	/**
	 * Returns the number of layout page template entries where groupId = any &#63; and classNameId = &#63; and classTypeKey = &#63; and name LIKE &#63; and type = &#63;.
	 *
	 * @param groupIds the group IDs
	 * @param classNameId the class name ID
	 * @param classTypeKey the class type key
	 * @param name the name
	 * @param type the type
	 * @return the number of matching layout page template entries
	 */
	public int countByG_C_C_LikeN_T(
		long[] groupIds, long classNameId, String classTypeKey, String name,
		int type);

	/**
	 * Returns the number of layout page template entries that the user has permission to view where groupId = &#63; and classNameId = &#63; and classTypeKey = &#63; and name LIKE &#63; and type = &#63;.
	 *
	 * @param groupId the group ID
	 * @param classNameId the class name ID
	 * @param classTypeKey the class type key
	 * @param name the name
	 * @param type the type
	 * @return the number of matching layout page template entries that the user has permission to view
	 */
	public int filterCountByG_C_C_LikeN_T(
		long groupId, long classNameId, String classTypeKey, String name,
		int type);

	/**
	 * Returns the number of layout page template entries that the user has permission to view where groupId = any &#63; and classNameId = &#63; and classTypeKey = &#63; and name LIKE &#63; and type = &#63;.
	 *
	 * @param groupIds the group IDs
	 * @param classNameId the class name ID
	 * @param classTypeKey the class type key
	 * @param name the name
	 * @param type the type
	 * @return the number of matching layout page template entries that the user has permission to view
	 */
	public int filterCountByG_C_C_LikeN_T(
		long[] groupIds, long classNameId, String classTypeKey, String name,
		int type);

	/**
	 * Returns an ordered range of all the layout page template entries where groupId = &#63; and classNameId = &#63; and classTypeKey = &#63; and type = &#63; and status = &#63;.
	 *
	 * <p>
	 * Useful when paginating results. Returns a maximum of <code>end - start</code> instances. <code>start</code> and <code>end</code> are not primary keys, they are indexes in the result set. Thus, <code>0</code> refers to the first result in the set. Setting both <code>start</code> and <code>end</code> to <code>com.liferay.portal.kernel.dao.orm.QueryUtil#ALL_POS</code> will return the full result set. If <code>orderByComparator</code> is specified, then the query will include the given ORDER BY logic. If <code>orderByComparator</code> is absent, then the query will include the default ORDER BY logic from <code>com.liferay.layout.page.template.model.impl.LayoutPageTemplateEntryModelImpl</code>.
	 * </p>
	 *
	 * @param groupId the group ID
	 * @param classNameId the class name ID
	 * @param classTypeKey the class type key
	 * @param type the type
	 * @param status the status
	 * @param start the lower bound of the range of layout page template entries
	 * @param end the upper bound of the range of layout page template entries (not inclusive)
	 * @param orderByComparator the comparator to order the results by (optionally <code>null</code>)
	 * @param useFinderCache whether to use the finder cache
	 * @return the ordered range of matching layout page template entries
	 */
	public java.util.List<LayoutPageTemplateEntry> findByG_C_C_T_S(
		long groupId, long classNameId, String classTypeKey, int type,
		int status, int start, int end,
		com.liferay.portal.kernel.util.OrderByComparator
			<LayoutPageTemplateEntry> orderByComparator,
		boolean useFinderCache);

	/**
	 * Returns the first layout page template entry in the ordered set where groupId = &#63; and classNameId = &#63; and classTypeKey = &#63; and type = &#63; and status = &#63;.
	 *
	 * @param groupId the group ID
	 * @param classNameId the class name ID
	 * @param classTypeKey the class type key
	 * @param type the type
	 * @param status the status
	 * @param orderByComparator the comparator to order the set by (optionally <code>null</code>)
	 * @return the first matching layout page template entry
	 * @throws NoSuchPageTemplateEntryException if a matching layout page template entry could not be found
	 */
	public LayoutPageTemplateEntry findByG_C_C_T_S_First(
			long groupId, long classNameId, String classTypeKey, int type,
			int status,
			com.liferay.portal.kernel.util.OrderByComparator
				<LayoutPageTemplateEntry> orderByComparator)
		throws NoSuchPageTemplateEntryException;

	/**
	 * Returns the first layout page template entry in the ordered set where groupId = &#63; and classNameId = &#63; and classTypeKey = &#63; and type = &#63; and status = &#63;.
	 *
	 * @param groupId the group ID
	 * @param classNameId the class name ID
	 * @param classTypeKey the class type key
	 * @param type the type
	 * @param status the status
	 * @param orderByComparator the comparator to order the set by (optionally <code>null</code>)
	 * @return the first matching layout page template entry, or <code>null</code> if a matching layout page template entry could not be found
	 */
	public LayoutPageTemplateEntry fetchByG_C_C_T_S_First(
		long groupId, long classNameId, String classTypeKey, int type,
		int status,
		com.liferay.portal.kernel.util.OrderByComparator
			<LayoutPageTemplateEntry> orderByComparator);

	/**
	 * Returns an ordered range of all the layout page template entries that the user has permissions to view where groupId = &#63; and classNameId = &#63; and classTypeKey = &#63; and type = &#63; and status = &#63;.
	 *
	 * <p>
	 * Useful when paginating results. Returns a maximum of <code>end - start</code> instances. <code>start</code> and <code>end</code> are not primary keys, they are indexes in the result set. Thus, <code>0</code> refers to the first result in the set. Setting both <code>start</code> and <code>end</code> to <code>com.liferay.portal.kernel.dao.orm.QueryUtil#ALL_POS</code> will return the full result set. If <code>orderByComparator</code> is specified, then the query will include the given ORDER BY logic. If <code>orderByComparator</code> is absent, then the query will include the default ORDER BY logic from <code>com.liferay.layout.page.template.model.impl.LayoutPageTemplateEntryModelImpl</code>.
	 * </p>
	 *
	 * @param groupId the group ID
	 * @param classNameId the class name ID
	 * @param classTypeKey the class type key
	 * @param type the type
	 * @param status the status
	 * @param start the lower bound of the range of layout page template entries
	 * @param end the upper bound of the range of layout page template entries (not inclusive)
	 * @param orderByComparator the comparator to order the results by (optionally <code>null</code>)
	 * @return the ordered range of matching layout page template entries that the user has permission to view
	 */
	public java.util.List<LayoutPageTemplateEntry> filterFindByG_C_C_T_S(
		long groupId, long classNameId, String classTypeKey, int type,
		int status, int start, int end,
		com.liferay.portal.kernel.util.OrderByComparator
			<LayoutPageTemplateEntry> orderByComparator);

	/**
	 * Returns an ordered range of all the layout page template entries that the user has permission to view where groupId = any &#63; and classNameId = &#63; and classTypeKey = &#63; and type = &#63; and status = &#63;.
	 *
	 * <p>
	 * Useful when paginating results. Returns a maximum of <code>end - start</code> instances. <code>start</code> and <code>end</code> are not primary keys, they are indexes in the result set. Thus, <code>0</code> refers to the first result in the set. Setting both <code>start</code> and <code>end</code> to <code>com.liferay.portal.kernel.dao.orm.QueryUtil#ALL_POS</code> will return the full result set. If <code>orderByComparator</code> is specified, then the query will include the given ORDER BY logic. If <code>orderByComparator</code> is absent, then the query will include the default ORDER BY logic from <code>com.liferay.layout.page.template.model.impl.LayoutPageTemplateEntryModelImpl</code>.
	 * </p>
	 *
	 * @param groupIds the group IDs
	 * @param classNameId the class name ID
	 * @param classTypeKey the class type key
	 * @param type the type
	 * @param status the status
	 * @param start the lower bound of the range of layout page template entries
	 * @param end the upper bound of the range of layout page template entries (not inclusive)
	 * @param orderByComparator the comparator to order the results by (optionally <code>null</code>)
	 * @return the ordered range of matching layout page template entries that the user has permission to view
	 */
	public java.util.List<LayoutPageTemplateEntry> filterFindByG_C_C_T_S(
		long[] groupIds, long classNameId, String classTypeKey, int type,
		int status, int start, int end,
		com.liferay.portal.kernel.util.OrderByComparator
			<LayoutPageTemplateEntry> orderByComparator);

	/**
	 * Returns an ordered range of all the layout page template entries where groupId = &#63; and classNameId = &#63; and classTypeKey = &#63; and type = &#63; and status = &#63;, optionally using the finder cache.
	 *
	 * <p>
	 * Useful when paginating results. Returns a maximum of <code>end - start</code> instances. <code>start</code> and <code>end</code> are not primary keys, they are indexes in the result set. Thus, <code>0</code> refers to the first result in the set. Setting both <code>start</code> and <code>end</code> to <code>com.liferay.portal.kernel.dao.orm.QueryUtil#ALL_POS</code> will return the full result set. If <code>orderByComparator</code> is specified, then the query will include the given ORDER BY logic. If <code>orderByComparator</code> is absent, then the query will include the default ORDER BY logic from <code>com.liferay.layout.page.template.model.impl.LayoutPageTemplateEntryModelImpl</code>.
	 * </p>
	 *
	 * @param groupIds the group IDs
	 * @param classNameId the class name ID
	 * @param classTypeKey the class type key
	 * @param type the type
	 * @param status the status
	 * @param start the lower bound of the range of layout page template entries
	 * @param end the upper bound of the range of layout page template entries (not inclusive)
	 * @param orderByComparator the comparator to order the results by (optionally <code>null</code>)
	 * @param useFinderCache whether to use the finder cache
	 * @return the ordered range of matching layout page template entries
	 */
	public java.util.List<LayoutPageTemplateEntry> findByG_C_C_T_S(
		long[] groupIds, long classNameId, String classTypeKey, int type,
		int status, int start, int end,
		com.liferay.portal.kernel.util.OrderByComparator
			<LayoutPageTemplateEntry> orderByComparator,
		boolean useFinderCache);

	/**
	 * Removes all the layout page template entries where groupId = &#63; and classNameId = &#63; and classTypeKey = &#63; and type = &#63; and status = &#63; from the database.
	 *
	 * @param groupId the group ID
	 * @param classNameId the class name ID
	 * @param classTypeKey the class type key
	 * @param type the type
	 * @param status the status
	 */
	public void removeByG_C_C_T_S(
		long groupId, long classNameId, String classTypeKey, int type,
		int status);

	/**
	 * Returns the number of layout page template entries where groupId = &#63; and classNameId = &#63; and classTypeKey = &#63; and type = &#63; and status = &#63;.
	 *
	 * @param groupId the group ID
	 * @param classNameId the class name ID
	 * @param classTypeKey the class type key
	 * @param type the type
	 * @param status the status
	 * @return the number of matching layout page template entries
	 */
	public int countByG_C_C_T_S(
		long groupId, long classNameId, String classTypeKey, int type,
		int status);

	/**
	 * Returns the number of layout page template entries where groupId = any &#63; and classNameId = &#63; and classTypeKey = &#63; and type = &#63; and status = &#63;.
	 *
	 * @param groupIds the group IDs
	 * @param classNameId the class name ID
	 * @param classTypeKey the class type key
	 * @param type the type
	 * @param status the status
	 * @return the number of matching layout page template entries
	 */
	public int countByG_C_C_T_S(
		long[] groupIds, long classNameId, String classTypeKey, int type,
		int status);

	/**
	 * Returns the number of layout page template entries that the user has permission to view where groupId = &#63; and classNameId = &#63; and classTypeKey = &#63; and type = &#63; and status = &#63;.
	 *
	 * @param groupId the group ID
	 * @param classNameId the class name ID
	 * @param classTypeKey the class type key
	 * @param type the type
	 * @param status the status
	 * @return the number of matching layout page template entries that the user has permission to view
	 */
	public int filterCountByG_C_C_T_S(
		long groupId, long classNameId, String classTypeKey, int type,
		int status);

	/**
	 * Returns the number of layout page template entries that the user has permission to view where groupId = any &#63; and classNameId = &#63; and classTypeKey = &#63; and type = &#63; and status = &#63;.
	 *
	 * @param groupIds the group IDs
	 * @param classNameId the class name ID
	 * @param classTypeKey the class type key
	 * @param type the type
	 * @param status the status
	 * @return the number of matching layout page template entries that the user has permission to view
	 */
	public int filterCountByG_C_C_T_S(
		long[] groupIds, long classNameId, String classTypeKey, int type,
		int status);

	/**
	 * Returns an ordered range of all the layout page template entries where groupId = &#63; and classNameId = &#63; and classTypeKey = &#63; and defaultTemplate = &#63; and status = &#63;.
	 *
	 * <p>
	 * Useful when paginating results. Returns a maximum of <code>end - start</code> instances. <code>start</code> and <code>end</code> are not primary keys, they are indexes in the result set. Thus, <code>0</code> refers to the first result in the set. Setting both <code>start</code> and <code>end</code> to <code>com.liferay.portal.kernel.dao.orm.QueryUtil#ALL_POS</code> will return the full result set. If <code>orderByComparator</code> is specified, then the query will include the given ORDER BY logic. If <code>orderByComparator</code> is absent, then the query will include the default ORDER BY logic from <code>com.liferay.layout.page.template.model.impl.LayoutPageTemplateEntryModelImpl</code>.
	 * </p>
	 *
	 * @param groupId the group ID
	 * @param classNameId the class name ID
	 * @param classTypeKey the class type key
	 * @param defaultTemplate the default template
	 * @param status the status
	 * @param start the lower bound of the range of layout page template entries
	 * @param end the upper bound of the range of layout page template entries (not inclusive)
	 * @param orderByComparator the comparator to order the results by (optionally <code>null</code>)
	 * @param useFinderCache whether to use the finder cache
	 * @return the ordered range of matching layout page template entries
	 */
	public java.util.List<LayoutPageTemplateEntry> findByG_C_C_D_S(
		long groupId, long classNameId, String classTypeKey,
		boolean defaultTemplate, int status, int start, int end,
		com.liferay.portal.kernel.util.OrderByComparator
			<LayoutPageTemplateEntry> orderByComparator,
		boolean useFinderCache);

	/**
	 * Returns the first layout page template entry in the ordered set where groupId = &#63; and classNameId = &#63; and classTypeKey = &#63; and defaultTemplate = &#63; and status = &#63;.
	 *
	 * @param groupId the group ID
	 * @param classNameId the class name ID
	 * @param classTypeKey the class type key
	 * @param defaultTemplate the default template
	 * @param status the status
	 * @param orderByComparator the comparator to order the set by (optionally <code>null</code>)
	 * @return the first matching layout page template entry
	 * @throws NoSuchPageTemplateEntryException if a matching layout page template entry could not be found
	 */
	public LayoutPageTemplateEntry findByG_C_C_D_S_First(
			long groupId, long classNameId, String classTypeKey,
			boolean defaultTemplate, int status,
			com.liferay.portal.kernel.util.OrderByComparator
				<LayoutPageTemplateEntry> orderByComparator)
		throws NoSuchPageTemplateEntryException;

	/**
	 * Returns the first layout page template entry in the ordered set where groupId = &#63; and classNameId = &#63; and classTypeKey = &#63; and defaultTemplate = &#63; and status = &#63;.
	 *
	 * @param groupId the group ID
	 * @param classNameId the class name ID
	 * @param classTypeKey the class type key
	 * @param defaultTemplate the default template
	 * @param status the status
	 * @param orderByComparator the comparator to order the set by (optionally <code>null</code>)
	 * @return the first matching layout page template entry, or <code>null</code> if a matching layout page template entry could not be found
	 */
	public LayoutPageTemplateEntry fetchByG_C_C_D_S_First(
		long groupId, long classNameId, String classTypeKey,
		boolean defaultTemplate, int status,
		com.liferay.portal.kernel.util.OrderByComparator
			<LayoutPageTemplateEntry> orderByComparator);

	/**
	 * Returns an ordered range of all the layout page template entries that the user has permissions to view where groupId = &#63; and classNameId = &#63; and classTypeKey = &#63; and defaultTemplate = &#63; and status = &#63;.
	 *
	 * <p>
	 * Useful when paginating results. Returns a maximum of <code>end - start</code> instances. <code>start</code> and <code>end</code> are not primary keys, they are indexes in the result set. Thus, <code>0</code> refers to the first result in the set. Setting both <code>start</code> and <code>end</code> to <code>com.liferay.portal.kernel.dao.orm.QueryUtil#ALL_POS</code> will return the full result set. If <code>orderByComparator</code> is specified, then the query will include the given ORDER BY logic. If <code>orderByComparator</code> is absent, then the query will include the default ORDER BY logic from <code>com.liferay.layout.page.template.model.impl.LayoutPageTemplateEntryModelImpl</code>.
	 * </p>
	 *
	 * @param groupId the group ID
	 * @param classNameId the class name ID
	 * @param classTypeKey the class type key
	 * @param defaultTemplate the default template
	 * @param status the status
	 * @param start the lower bound of the range of layout page template entries
	 * @param end the upper bound of the range of layout page template entries (not inclusive)
	 * @param orderByComparator the comparator to order the results by (optionally <code>null</code>)
	 * @return the ordered range of matching layout page template entries that the user has permission to view
	 */
	public java.util.List<LayoutPageTemplateEntry> filterFindByG_C_C_D_S(
		long groupId, long classNameId, String classTypeKey,
		boolean defaultTemplate, int status, int start, int end,
		com.liferay.portal.kernel.util.OrderByComparator
			<LayoutPageTemplateEntry> orderByComparator);

	/**
	 * Removes all the layout page template entries where groupId = &#63; and classNameId = &#63; and classTypeKey = &#63; and defaultTemplate = &#63; and status = &#63; from the database.
	 *
	 * @param groupId the group ID
	 * @param classNameId the class name ID
	 * @param classTypeKey the class type key
	 * @param defaultTemplate the default template
	 * @param status the status
	 */
	public void removeByG_C_C_D_S(
		long groupId, long classNameId, String classTypeKey,
		boolean defaultTemplate, int status);

	/**
	 * Returns the number of layout page template entries where groupId = &#63; and classNameId = &#63; and classTypeKey = &#63; and defaultTemplate = &#63; and status = &#63;.
	 *
	 * @param groupId the group ID
	 * @param classNameId the class name ID
	 * @param classTypeKey the class type key
	 * @param defaultTemplate the default template
	 * @param status the status
	 * @return the number of matching layout page template entries
	 */
	public int countByG_C_C_D_S(
		long groupId, long classNameId, String classTypeKey,
		boolean defaultTemplate, int status);

	/**
	 * Returns the number of layout page template entries that the user has permission to view where groupId = &#63; and classNameId = &#63; and classTypeKey = &#63; and defaultTemplate = &#63; and status = &#63;.
	 *
	 * @param groupId the group ID
	 * @param classNameId the class name ID
	 * @param classTypeKey the class type key
	 * @param defaultTemplate the default template
	 * @param status the status
	 * @return the number of matching layout page template entries that the user has permission to view
	 */
	public int filterCountByG_C_C_D_S(
		long groupId, long classNameId, String classTypeKey,
		boolean defaultTemplate, int status);

	/**
	 * Returns all the layout page template entries where groupId = &#63; and classNameId = &#63; and classTypeKey = &#63; and name LIKE &#63; and type = &#63; and status = &#63;.
	 *
	 * @param groupId the group ID
	 * @param classNameId the class name ID
	 * @param classTypeKey the class type key
	 * @param name the name
	 * @param type the type
	 * @param status the status
	 * @return the matching layout page template entries
	 */
	public java.util.List<LayoutPageTemplateEntry> findByG_C_C_LikeN_T_S(
		long groupId, long classNameId, String classTypeKey, String name,
		int type, int status);

	/**
	 * Returns a range of all the layout page template entries where groupId = &#63; and classNameId = &#63; and classTypeKey = &#63; and name LIKE &#63; and type = &#63; and status = &#63;.
	 *
	 * <p>
	 * Useful when paginating results. Returns a maximum of <code>end - start</code> instances. <code>start</code> and <code>end</code> are not primary keys, they are indexes in the result set. Thus, <code>0</code> refers to the first result in the set. Setting both <code>start</code> and <code>end</code> to <code>com.liferay.portal.kernel.dao.orm.QueryUtil#ALL_POS</code> will return the full result set. If <code>orderByComparator</code> is specified, then the query will include the given ORDER BY logic. If <code>orderByComparator</code> is absent, then the query will include the default ORDER BY logic from <code>com.liferay.layout.page.template.model.impl.LayoutPageTemplateEntryModelImpl</code>.
	 * </p>
	 *
	 * @param groupId the group ID
	 * @param classNameId the class name ID
	 * @param classTypeKey the class type key
	 * @param name the name
	 * @param type the type
	 * @param status the status
	 * @param start the lower bound of the range of layout page template entries
	 * @param end the upper bound of the range of layout page template entries (not inclusive)
	 * @return the range of matching layout page template entries
	 */
	public java.util.List<LayoutPageTemplateEntry> findByG_C_C_LikeN_T_S(
		long groupId, long classNameId, String classTypeKey, String name,
		int type, int status, int start, int end);

	/**
	 * Returns an ordered range of all the layout page template entries where groupId = &#63; and classNameId = &#63; and classTypeKey = &#63; and name LIKE &#63; and type = &#63; and status = &#63;.
	 *
	 * <p>
	 * Useful when paginating results. Returns a maximum of <code>end - start</code> instances. <code>start</code> and <code>end</code> are not primary keys, they are indexes in the result set. Thus, <code>0</code> refers to the first result in the set. Setting both <code>start</code> and <code>end</code> to <code>com.liferay.portal.kernel.dao.orm.QueryUtil#ALL_POS</code> will return the full result set. If <code>orderByComparator</code> is specified, then the query will include the given ORDER BY logic. If <code>orderByComparator</code> is absent, then the query will include the default ORDER BY logic from <code>com.liferay.layout.page.template.model.impl.LayoutPageTemplateEntryModelImpl</code>.
	 * </p>
	 *
	 * @param groupId the group ID
	 * @param classNameId the class name ID
	 * @param classTypeKey the class type key
	 * @param name the name
	 * @param type the type
	 * @param status the status
	 * @param start the lower bound of the range of layout page template entries
	 * @param end the upper bound of the range of layout page template entries (not inclusive)
	 * @param orderByComparator the comparator to order the results by (optionally <code>null</code>)
	 * @return the ordered range of matching layout page template entries
	 */
	public java.util.List<LayoutPageTemplateEntry> findByG_C_C_LikeN_T_S(
		long groupId, long classNameId, String classTypeKey, String name,
		int type, int status, int start, int end,
		com.liferay.portal.kernel.util.OrderByComparator
			<LayoutPageTemplateEntry> orderByComparator);

	/**
	 * Returns an ordered range of all the layout page template entries where groupId = &#63; and classNameId = &#63; and classTypeKey = &#63; and name LIKE &#63; and type = &#63; and status = &#63;.
	 *
	 * <p>
	 * Useful when paginating results. Returns a maximum of <code>end - start</code> instances. <code>start</code> and <code>end</code> are not primary keys, they are indexes in the result set. Thus, <code>0</code> refers to the first result in the set. Setting both <code>start</code> and <code>end</code> to <code>com.liferay.portal.kernel.dao.orm.QueryUtil#ALL_POS</code> will return the full result set. If <code>orderByComparator</code> is specified, then the query will include the given ORDER BY logic. If <code>orderByComparator</code> is absent, then the query will include the default ORDER BY logic from <code>com.liferay.layout.page.template.model.impl.LayoutPageTemplateEntryModelImpl</code>.
	 * </p>
	 *
	 * @param groupId the group ID
	 * @param classNameId the class name ID
	 * @param classTypeKey the class type key
	 * @param name the name
	 * @param type the type
	 * @param status the status
	 * @param start the lower bound of the range of layout page template entries
	 * @param end the upper bound of the range of layout page template entries (not inclusive)
	 * @param orderByComparator the comparator to order the results by (optionally <code>null</code>)
	 * @param useFinderCache whether to use the finder cache
	 * @return the ordered range of matching layout page template entries
	 */
	public java.util.List<LayoutPageTemplateEntry> findByG_C_C_LikeN_T_S(
		long groupId, long classNameId, String classTypeKey, String name,
		int type, int status, int start, int end,
		com.liferay.portal.kernel.util.OrderByComparator
			<LayoutPageTemplateEntry> orderByComparator,
		boolean useFinderCache);

	/**
	 * Returns the first layout page template entry in the ordered set where groupId = &#63; and classNameId = &#63; and classTypeKey = &#63; and name LIKE &#63; and type = &#63; and status = &#63;.
	 *
	 * @param groupId the group ID
	 * @param classNameId the class name ID
	 * @param classTypeKey the class type key
	 * @param name the name
	 * @param type the type
	 * @param status the status
	 * @param orderByComparator the comparator to order the set by (optionally <code>null</code>)
	 * @return the first matching layout page template entry
	 * @throws NoSuchPageTemplateEntryException if a matching layout page template entry could not be found
	 */
	public LayoutPageTemplateEntry findByG_C_C_LikeN_T_S_First(
			long groupId, long classNameId, String classTypeKey, String name,
			int type, int status,
			com.liferay.portal.kernel.util.OrderByComparator
				<LayoutPageTemplateEntry> orderByComparator)
		throws NoSuchPageTemplateEntryException;

	/**
	 * Returns the first layout page template entry in the ordered set where groupId = &#63; and classNameId = &#63; and classTypeKey = &#63; and name LIKE &#63; and type = &#63; and status = &#63;.
	 *
	 * @param groupId the group ID
	 * @param classNameId the class name ID
	 * @param classTypeKey the class type key
	 * @param name the name
	 * @param type the type
	 * @param status the status
	 * @param orderByComparator the comparator to order the set by (optionally <code>null</code>)
	 * @return the first matching layout page template entry, or <code>null</code> if a matching layout page template entry could not be found
	 */
	public LayoutPageTemplateEntry fetchByG_C_C_LikeN_T_S_First(
		long groupId, long classNameId, String classTypeKey, String name,
		int type, int status,
		com.liferay.portal.kernel.util.OrderByComparator
			<LayoutPageTemplateEntry> orderByComparator);

	/**
	 * Returns all the layout page template entries that the user has permission to view where groupId = &#63; and classNameId = &#63; and classTypeKey = &#63; and name LIKE &#63; and type = &#63; and status = &#63;.
	 *
	 * @param groupId the group ID
	 * @param classNameId the class name ID
	 * @param classTypeKey the class type key
	 * @param name the name
	 * @param type the type
	 * @param status the status
	 * @return the matching layout page template entries that the user has permission to view
	 */
	public java.util.List<LayoutPageTemplateEntry> filterFindByG_C_C_LikeN_T_S(
		long groupId, long classNameId, String classTypeKey, String name,
		int type, int status);

	/**
	 * Returns a range of all the layout page template entries that the user has permission to view where groupId = &#63; and classNameId = &#63; and classTypeKey = &#63; and name LIKE &#63; and type = &#63; and status = &#63;.
	 *
	 * <p>
	 * Useful when paginating results. Returns a maximum of <code>end - start</code> instances. <code>start</code> and <code>end</code> are not primary keys, they are indexes in the result set. Thus, <code>0</code> refers to the first result in the set. Setting both <code>start</code> and <code>end</code> to <code>com.liferay.portal.kernel.dao.orm.QueryUtil#ALL_POS</code> will return the full result set. If <code>orderByComparator</code> is specified, then the query will include the given ORDER BY logic. If <code>orderByComparator</code> is absent, then the query will include the default ORDER BY logic from <code>com.liferay.layout.page.template.model.impl.LayoutPageTemplateEntryModelImpl</code>.
	 * </p>
	 *
	 * @param groupId the group ID
	 * @param classNameId the class name ID
	 * @param classTypeKey the class type key
	 * @param name the name
	 * @param type the type
	 * @param status the status
	 * @param start the lower bound of the range of layout page template entries
	 * @param end the upper bound of the range of layout page template entries (not inclusive)
	 * @return the range of matching layout page template entries that the user has permission to view
	 */
	public java.util.List<LayoutPageTemplateEntry> filterFindByG_C_C_LikeN_T_S(
		long groupId, long classNameId, String classTypeKey, String name,
		int type, int status, int start, int end);

	/**
	 * Returns an ordered range of all the layout page template entries that the user has permissions to view where groupId = &#63; and classNameId = &#63; and classTypeKey = &#63; and name LIKE &#63; and type = &#63; and status = &#63;.
	 *
	 * <p>
	 * Useful when paginating results. Returns a maximum of <code>end - start</code> instances. <code>start</code> and <code>end</code> are not primary keys, they are indexes in the result set. Thus, <code>0</code> refers to the first result in the set. Setting both <code>start</code> and <code>end</code> to <code>com.liferay.portal.kernel.dao.orm.QueryUtil#ALL_POS</code> will return the full result set. If <code>orderByComparator</code> is specified, then the query will include the given ORDER BY logic. If <code>orderByComparator</code> is absent, then the query will include the default ORDER BY logic from <code>com.liferay.layout.page.template.model.impl.LayoutPageTemplateEntryModelImpl</code>.
	 * </p>
	 *
	 * @param groupId the group ID
	 * @param classNameId the class name ID
	 * @param classTypeKey the class type key
	 * @param name the name
	 * @param type the type
	 * @param status the status
	 * @param start the lower bound of the range of layout page template entries
	 * @param end the upper bound of the range of layout page template entries (not inclusive)
	 * @param orderByComparator the comparator to order the results by (optionally <code>null</code>)
	 * @return the ordered range of matching layout page template entries that the user has permission to view
	 */
	public java.util.List<LayoutPageTemplateEntry> filterFindByG_C_C_LikeN_T_S(
		long groupId, long classNameId, String classTypeKey, String name,
		int type, int status, int start, int end,
		com.liferay.portal.kernel.util.OrderByComparator
			<LayoutPageTemplateEntry> orderByComparator);

	/**
	 * Returns all the layout page template entries that the user has permission to view where groupId = any &#63; and classNameId = &#63; and classTypeKey = &#63; and name LIKE &#63; and type = &#63; and status = &#63;.
	 *
	 * @param groupIds the group IDs
	 * @param classNameId the class name ID
	 * @param classTypeKey the class type key
	 * @param name the name
	 * @param type the type
	 * @param status the status
	 * @return the matching layout page template entries that the user has permission to view
	 */
	public java.util.List<LayoutPageTemplateEntry> filterFindByG_C_C_LikeN_T_S(
		long[] groupIds, long classNameId, String classTypeKey, String name,
		int type, int status);

	/**
	 * Returns a range of all the layout page template entries that the user has permission to view where groupId = any &#63; and classNameId = &#63; and classTypeKey = &#63; and name LIKE &#63; and type = &#63; and status = &#63;.
	 *
	 * <p>
	 * Useful when paginating results. Returns a maximum of <code>end - start</code> instances. <code>start</code> and <code>end</code> are not primary keys, they are indexes in the result set. Thus, <code>0</code> refers to the first result in the set. Setting both <code>start</code> and <code>end</code> to <code>com.liferay.portal.kernel.dao.orm.QueryUtil#ALL_POS</code> will return the full result set. If <code>orderByComparator</code> is specified, then the query will include the given ORDER BY logic. If <code>orderByComparator</code> is absent, then the query will include the default ORDER BY logic from <code>com.liferay.layout.page.template.model.impl.LayoutPageTemplateEntryModelImpl</code>.
	 * </p>
	 *
	 * @param groupIds the group IDs
	 * @param classNameId the class name ID
	 * @param classTypeKey the class type key
	 * @param name the name
	 * @param type the type
	 * @param status the status
	 * @param start the lower bound of the range of layout page template entries
	 * @param end the upper bound of the range of layout page template entries (not inclusive)
	 * @return the range of matching layout page template entries that the user has permission to view
	 */
	public java.util.List<LayoutPageTemplateEntry> filterFindByG_C_C_LikeN_T_S(
		long[] groupIds, long classNameId, String classTypeKey, String name,
		int type, int status, int start, int end);

	/**
	 * Returns an ordered range of all the layout page template entries that the user has permission to view where groupId = any &#63; and classNameId = &#63; and classTypeKey = &#63; and name LIKE &#63; and type = &#63; and status = &#63;.
	 *
	 * <p>
	 * Useful when paginating results. Returns a maximum of <code>end - start</code> instances. <code>start</code> and <code>end</code> are not primary keys, they are indexes in the result set. Thus, <code>0</code> refers to the first result in the set. Setting both <code>start</code> and <code>end</code> to <code>com.liferay.portal.kernel.dao.orm.QueryUtil#ALL_POS</code> will return the full result set. If <code>orderByComparator</code> is specified, then the query will include the given ORDER BY logic. If <code>orderByComparator</code> is absent, then the query will include the default ORDER BY logic from <code>com.liferay.layout.page.template.model.impl.LayoutPageTemplateEntryModelImpl</code>.
	 * </p>
	 *
	 * @param groupIds the group IDs
	 * @param classNameId the class name ID
	 * @param classTypeKey the class type key
	 * @param name the name
	 * @param type the type
	 * @param status the status
	 * @param start the lower bound of the range of layout page template entries
	 * @param end the upper bound of the range of layout page template entries (not inclusive)
	 * @param orderByComparator the comparator to order the results by (optionally <code>null</code>)
	 * @return the ordered range of matching layout page template entries that the user has permission to view
	 */
	public java.util.List<LayoutPageTemplateEntry> filterFindByG_C_C_LikeN_T_S(
		long[] groupIds, long classNameId, String classTypeKey, String name,
		int type, int status, int start, int end,
		com.liferay.portal.kernel.util.OrderByComparator
			<LayoutPageTemplateEntry> orderByComparator);

	/**
	 * Returns all the layout page template entries where groupId = any &#63; and classNameId = &#63; and classTypeKey = &#63; and name LIKE &#63; and type = &#63; and status = &#63;.
	 *
	 * <p>
	 * Useful when paginating results. Returns a maximum of <code>end - start</code> instances. <code>start</code> and <code>end</code> are not primary keys, they are indexes in the result set. Thus, <code>0</code> refers to the first result in the set. Setting both <code>start</code> and <code>end</code> to <code>com.liferay.portal.kernel.dao.orm.QueryUtil#ALL_POS</code> will return the full result set. If <code>orderByComparator</code> is specified, then the query will include the given ORDER BY logic. If <code>orderByComparator</code> is absent, then the query will include the default ORDER BY logic from <code>com.liferay.layout.page.template.model.impl.LayoutPageTemplateEntryModelImpl</code>.
	 * </p>
	 *
	 * @param groupIds the group IDs
	 * @param classNameId the class name ID
	 * @param classTypeKey the class type key
	 * @param name the name
	 * @param type the type
	 * @param status the status
	 * @return the matching layout page template entries
	 */
	public java.util.List<LayoutPageTemplateEntry> findByG_C_C_LikeN_T_S(
		long[] groupIds, long classNameId, String classTypeKey, String name,
		int type, int status);

	/**
	 * Returns a range of all the layout page template entries where groupId = any &#63; and classNameId = &#63; and classTypeKey = &#63; and name LIKE &#63; and type = &#63; and status = &#63;.
	 *
	 * <p>
	 * Useful when paginating results. Returns a maximum of <code>end - start</code> instances. <code>start</code> and <code>end</code> are not primary keys, they are indexes in the result set. Thus, <code>0</code> refers to the first result in the set. Setting both <code>start</code> and <code>end</code> to <code>com.liferay.portal.kernel.dao.orm.QueryUtil#ALL_POS</code> will return the full result set. If <code>orderByComparator</code> is specified, then the query will include the given ORDER BY logic. If <code>orderByComparator</code> is absent, then the query will include the default ORDER BY logic from <code>com.liferay.layout.page.template.model.impl.LayoutPageTemplateEntryModelImpl</code>.
	 * </p>
	 *
	 * @param groupIds the group IDs
	 * @param classNameId the class name ID
	 * @param classTypeKey the class type key
	 * @param name the name
	 * @param type the type
	 * @param status the status
	 * @param start the lower bound of the range of layout page template entries
	 * @param end the upper bound of the range of layout page template entries (not inclusive)
	 * @return the range of matching layout page template entries
	 */
	public java.util.List<LayoutPageTemplateEntry> findByG_C_C_LikeN_T_S(
		long[] groupIds, long classNameId, String classTypeKey, String name,
		int type, int status, int start, int end);

	/**
	 * Returns an ordered range of all the layout page template entries where groupId = any &#63; and classNameId = &#63; and classTypeKey = &#63; and name LIKE &#63; and type = &#63; and status = &#63;.
	 *
	 * <p>
	 * Useful when paginating results. Returns a maximum of <code>end - start</code> instances. <code>start</code> and <code>end</code> are not primary keys, they are indexes in the result set. Thus, <code>0</code> refers to the first result in the set. Setting both <code>start</code> and <code>end</code> to <code>com.liferay.portal.kernel.dao.orm.QueryUtil#ALL_POS</code> will return the full result set. If <code>orderByComparator</code> is specified, then the query will include the given ORDER BY logic. If <code>orderByComparator</code> is absent, then the query will include the default ORDER BY logic from <code>com.liferay.layout.page.template.model.impl.LayoutPageTemplateEntryModelImpl</code>.
	 * </p>
	 *
	 * @param groupIds the group IDs
	 * @param classNameId the class name ID
	 * @param classTypeKey the class type key
	 * @param name the name
	 * @param type the type
	 * @param status the status
	 * @param start the lower bound of the range of layout page template entries
	 * @param end the upper bound of the range of layout page template entries (not inclusive)
	 * @param orderByComparator the comparator to order the results by (optionally <code>null</code>)
	 * @return the ordered range of matching layout page template entries
	 */
	public java.util.List<LayoutPageTemplateEntry> findByG_C_C_LikeN_T_S(
		long[] groupIds, long classNameId, String classTypeKey, String name,
		int type, int status, int start, int end,
		com.liferay.portal.kernel.util.OrderByComparator
			<LayoutPageTemplateEntry> orderByComparator);

	/**
	 * Returns an ordered range of all the layout page template entries where groupId = &#63; and classNameId = &#63; and classTypeKey = &#63; and name LIKE &#63; and type = &#63; and status = &#63;, optionally using the finder cache.
	 *
	 * <p>
	 * Useful when paginating results. Returns a maximum of <code>end - start</code> instances. <code>start</code> and <code>end</code> are not primary keys, they are indexes in the result set. Thus, <code>0</code> refers to the first result in the set. Setting both <code>start</code> and <code>end</code> to <code>com.liferay.portal.kernel.dao.orm.QueryUtil#ALL_POS</code> will return the full result set. If <code>orderByComparator</code> is specified, then the query will include the given ORDER BY logic. If <code>orderByComparator</code> is absent, then the query will include the default ORDER BY logic from <code>com.liferay.layout.page.template.model.impl.LayoutPageTemplateEntryModelImpl</code>.
	 * </p>
	 *
	 * @param groupIds the group IDs
	 * @param classNameId the class name ID
	 * @param classTypeKey the class type key
	 * @param name the name
	 * @param type the type
	 * @param status the status
	 * @param start the lower bound of the range of layout page template entries
	 * @param end the upper bound of the range of layout page template entries (not inclusive)
	 * @param orderByComparator the comparator to order the results by (optionally <code>null</code>)
	 * @param useFinderCache whether to use the finder cache
	 * @return the ordered range of matching layout page template entries
	 */
	public java.util.List<LayoutPageTemplateEntry> findByG_C_C_LikeN_T_S(
		long[] groupIds, long classNameId, String classTypeKey, String name,
		int type, int status, int start, int end,
		com.liferay.portal.kernel.util.OrderByComparator
			<LayoutPageTemplateEntry> orderByComparator,
		boolean useFinderCache);

	/**
	 * Removes all the layout page template entries where groupId = &#63; and classNameId = &#63; and classTypeKey = &#63; and name LIKE &#63; and type = &#63; and status = &#63; from the database.
	 *
	 * @param groupId the group ID
	 * @param classNameId the class name ID
	 * @param classTypeKey the class type key
	 * @param name the name
	 * @param type the type
	 * @param status the status
	 */
	public void removeByG_C_C_LikeN_T_S(
		long groupId, long classNameId, String classTypeKey, String name,
		int type, int status);

	/**
	 * Returns the number of layout page template entries where groupId = &#63; and classNameId = &#63; and classTypeKey = &#63; and name LIKE &#63; and type = &#63; and status = &#63;.
	 *
	 * @param groupId the group ID
	 * @param classNameId the class name ID
	 * @param classTypeKey the class type key
	 * @param name the name
	 * @param type the type
	 * @param status the status
	 * @return the number of matching layout page template entries
	 */
	public int countByG_C_C_LikeN_T_S(
		long groupId, long classNameId, String classTypeKey, String name,
		int type, int status);

	/**
	 * Returns the number of layout page template entries where groupId = any &#63; and classNameId = &#63; and classTypeKey = &#63; and name LIKE &#63; and type = &#63; and status = &#63;.
	 *
	 * @param groupIds the group IDs
	 * @param classNameId the class name ID
	 * @param classTypeKey the class type key
	 * @param name the name
	 * @param type the type
	 * @param status the status
	 * @return the number of matching layout page template entries
	 */
	public int countByG_C_C_LikeN_T_S(
		long[] groupIds, long classNameId, String classTypeKey, String name,
		int type, int status);

	/**
	 * Returns the number of layout page template entries that the user has permission to view where groupId = &#63; and classNameId = &#63; and classTypeKey = &#63; and name LIKE &#63; and type = &#63; and status = &#63;.
	 *
	 * @param groupId the group ID
	 * @param classNameId the class name ID
	 * @param classTypeKey the class type key
	 * @param name the name
	 * @param type the type
	 * @param status the status
	 * @return the number of matching layout page template entries that the user has permission to view
	 */
	public int filterCountByG_C_C_LikeN_T_S(
		long groupId, long classNameId, String classTypeKey, String name,
		int type, int status);

	/**
	 * Returns the number of layout page template entries that the user has permission to view where groupId = any &#63; and classNameId = &#63; and classTypeKey = &#63; and name LIKE &#63; and type = &#63; and status = &#63;.
	 *
	 * @param groupIds the group IDs
	 * @param classNameId the class name ID
	 * @param classTypeKey the class type key
	 * @param name the name
	 * @param type the type
	 * @param status the status
	 * @return the number of matching layout page template entries that the user has permission to view
	 */
	public int filterCountByG_C_C_LikeN_T_S(
		long[] groupIds, long classNameId, String classTypeKey, String name,
		int type, int status);

	/**
	 * Returns the layout page template entry where externalReferenceCode = &#63; and groupId = &#63; or throws a <code>NoSuchPageTemplateEntryException</code> if it could not be found.
	 *
	 * @param externalReferenceCode the external reference code
	 * @param groupId the group ID
	 * @return the matching layout page template entry
	 * @throws NoSuchPageTemplateEntryException if a matching layout page template entry could not be found
	 */
	public LayoutPageTemplateEntry findByERC_G(
			String externalReferenceCode, long groupId)
		throws NoSuchPageTemplateEntryException;

	/**
	 * Returns the layout page template entry where externalReferenceCode = &#63; and groupId = &#63; or returns <code>null</code> if it could not be found, optionally using the finder cache.
	 *
	 * @param externalReferenceCode the external reference code
	 * @param groupId the group ID
	 * @param useFinderCache whether to use the finder cache
	 * @return the matching layout page template entry, or <code>null</code> if a matching layout page template entry could not be found
	 */
	public LayoutPageTemplateEntry fetchByERC_G(
		String externalReferenceCode, long groupId, boolean useFinderCache);

	/**
	 * Removes the layout page template entry where externalReferenceCode = &#63; and groupId = &#63; from the database.
	 *
	 * @param externalReferenceCode the external reference code
	 * @param groupId the group ID
	 * @return the layout page template entry that was removed
	 */
	public LayoutPageTemplateEntry removeByERC_G(
			String externalReferenceCode, long groupId)
		throws NoSuchPageTemplateEntryException;

	/**
	 * Returns the number of layout page template entries where externalReferenceCode = &#63; and groupId = &#63;.
	 *
	 * @param externalReferenceCode the external reference code
	 * @param groupId the group ID
	 * @return the number of matching layout page template entries
	 */
	public int countByERC_G(String externalReferenceCode, long groupId);

	/**
	 * Creates a new layout page template entry with the primary key. Does not add the layout page template entry to the database.
	 *
	 * @param layoutPageTemplateEntryId the primary key for the new layout page template entry
	 * @return the new layout page template entry
	 */
	public LayoutPageTemplateEntry create(long layoutPageTemplateEntryId);

	/**
	 * Removes the layout page template entry with the primary key from the database. Also notifies the appropriate model listeners.
	 *
	 * @param layoutPageTemplateEntryId the primary key of the layout page template entry
	 * @return the layout page template entry that was removed
	 * @throws NoSuchPageTemplateEntryException if a layout page template entry with the primary key could not be found
	 */
	public LayoutPageTemplateEntry remove(long layoutPageTemplateEntryId)
		throws NoSuchPageTemplateEntryException;

	public LayoutPageTemplateEntry updateImpl(
		LayoutPageTemplateEntry layoutPageTemplateEntry);

	/**
	 * Returns the layout page template entry with the primary key or throws a <code>NoSuchPageTemplateEntryException</code> if it could not be found.
	 *
	 * @param layoutPageTemplateEntryId the primary key of the layout page template entry
	 * @return the layout page template entry
	 * @throws NoSuchPageTemplateEntryException if a layout page template entry with the primary key could not be found
	 */
	public LayoutPageTemplateEntry findByPrimaryKey(
			long layoutPageTemplateEntryId)
		throws NoSuchPageTemplateEntryException;

	/**
	 * Returns the layout page template entry with the primary key or returns <code>null</code> if it could not be found.
	 *
	 * @param layoutPageTemplateEntryId the primary key of the layout page template entry
	 * @return the layout page template entry, or <code>null</code> if a layout page template entry with the primary key could not be found
	 */
	public LayoutPageTemplateEntry fetchByPrimaryKey(
		long layoutPageTemplateEntryId);

	/**
	 * Returns the layout page template entry where uuid = &#63; and groupId = &#63; or returns <code>null</code> if it could not be found. Uses the finder cache.
	 *
	 * @param uuid the uuid
	 * @param groupId the group ID
	 * @return the matching layout page template entry, or <code>null</code> if a matching layout page template entry could not be found
	 */
	public default LayoutPageTemplateEntry fetchByUUID_G(
		String uuid, long groupId) {

		return fetchByUUID_G(uuid, groupId, true);
	}

	/**
	 * Returns the layout page template entry where plid = &#63; or returns <code>null</code> if it could not be found. Uses the finder cache.
	 *
	 * @param plid the plid
	 * @return the matching layout page template entry, or <code>null</code> if a matching layout page template entry could not be found
	 */
	public default LayoutPageTemplateEntry fetchByPlid(long plid) {
		return fetchByPlid(plid, true);
	}

	/**
	 * Returns the layout page template entry where groupId = &#63; and layoutPageTemplateEntryKey = &#63; or returns <code>null</code> if it could not be found. Uses the finder cache.
	 *
	 * @param groupId the group ID
	 * @param layoutPageTemplateEntryKey the layout page template entry key
	 * @return the matching layout page template entry, or <code>null</code> if a matching layout page template entry could not be found
	 */
	public default LayoutPageTemplateEntry fetchByG_LPTEK(
		long groupId, String layoutPageTemplateEntryKey) {

		return fetchByG_LPTEK(groupId, layoutPageTemplateEntryKey, true);
	}

	/**
	 * Returns the layout page template entry where groupId = &#63; and layoutPageTemplateCollectionId = &#63; and name = &#63; and type = &#63; or returns <code>null</code> if it could not be found. Uses the finder cache.
	 *
	 * @param groupId the group ID
	 * @param layoutPageTemplateCollectionId the layout page template collection ID
	 * @param name the name
	 * @param type the type
	 * @return the matching layout page template entry, or <code>null</code> if a matching layout page template entry could not be found
	 */
	public default LayoutPageTemplateEntry fetchByG_L_N_T(
		long groupId, long layoutPageTemplateCollectionId, String name,
		int type) {

		return fetchByG_L_N_T(
			groupId, layoutPageTemplateCollectionId, name, type, true);
	}

	/**
	 * Returns the layout page template entry where externalReferenceCode = &#63; and groupId = &#63; or returns <code>null</code> if it could not be found. Uses the finder cache.
	 *
	 * @param externalReferenceCode the external reference code
	 * @param groupId the group ID
	 * @return the matching layout page template entry, or <code>null</code> if a matching layout page template entry could not be found
	 */
	public default LayoutPageTemplateEntry fetchByERC_G(
		String externalReferenceCode, long groupId) {

		return fetchByERC_G(externalReferenceCode, groupId, true);
	}

	/**
	 * Returns all the layout page template entries where uuid = &#63;.
	 *
	 * @param uuid the uuid
	 * @return the matching layout page template entries
	 */
	public default java.util.List<LayoutPageTemplateEntry> findByUuid(
		String uuid) {

		return findByUuid(
			uuid, com.liferay.portal.kernel.dao.orm.QueryUtil.ALL_POS,
			com.liferay.portal.kernel.dao.orm.QueryUtil.ALL_POS, null, true);
	}

	/**
	 * Returns a range of all the layout page template entries where uuid = &#63;.
	 *
	 * <p>
	 * Useful when paginating results. Returns a maximum of <code>end - start</code> instances. <code>start</code> and <code>end</code> are not primary keys, they are indexes in the result set. Thus, <code>0</code> refers to the first result in the set. Setting both <code>start</code> and <code>end</code> to <code>com.liferay.portal.kernel.dao.orm.QueryUtil#ALL_POS</code> will return the full result set. If <code>orderByComparator</code> is specified, then the query will include the given ORDER BY logic. If <code>orderByComparator</code> is absent, then the query will include the default ORDER BY logic from <code>com.liferay.layout.page.template.model.impl.LayoutPageTemplateEntryModelImpl</code>.
	 * </p>
	 *
	 * @param uuid the uuid
	 * @param start the lower bound of the range of layout page template entries
	 * @param end the upper bound of the range of layout page template entries (not inclusive)
	 * @return the range of matching layout page template entries
	 */
	public default java.util.List<LayoutPageTemplateEntry> findByUuid(
		String uuid, int start, int end) {

		return findByUuid(uuid, start, end, null, true);
	}

	/**
	 * Returns an ordered range of all the layout page template entries where uuid = &#63;.
	 *
	 * <p>
	 * Useful when paginating results. Returns a maximum of <code>end - start</code> instances. <code>start</code> and <code>end</code> are not primary keys, they are indexes in the result set. Thus, <code>0</code> refers to the first result in the set. Setting both <code>start</code> and <code>end</code> to <code>com.liferay.portal.kernel.dao.orm.QueryUtil#ALL_POS</code> will return the full result set. If <code>orderByComparator</code> is specified, then the query will include the given ORDER BY logic. If <code>orderByComparator</code> is absent, then the query will include the default ORDER BY logic from <code>com.liferay.layout.page.template.model.impl.LayoutPageTemplateEntryModelImpl</code>.
	 * </p>
	 *
	 * @param uuid the uuid
	 * @param start the lower bound of the range of layout page template entries
	 * @param end the upper bound of the range of layout page template entries (not inclusive)
	 * @param orderByComparator the comparator to order the results by (optionally <code>null</code>)
	 * @return the ordered range of matching layout page template entries
	 */
	public default java.util.List<LayoutPageTemplateEntry> findByUuid(
		String uuid, int start, int end,
		com.liferay.portal.kernel.util.OrderByComparator
			<LayoutPageTemplateEntry> orderByComparator) {

		return findByUuid(uuid, start, end, orderByComparator, true);
	}

	/**
	 * Returns all the layout page template entries where uuid = &#63; and companyId = &#63;.
	 *
	 * @param uuid the uuid
	 * @param companyId the company ID
	 * @return the matching layout page template entries
	 */
	public default java.util.List<LayoutPageTemplateEntry> findByUuid_C(
		String uuid, long companyId) {

		return findByUuid_C(
			uuid, companyId,
			com.liferay.portal.kernel.dao.orm.QueryUtil.ALL_POS,
			com.liferay.portal.kernel.dao.orm.QueryUtil.ALL_POS, null, true);
	}

	/**
	 * Returns a range of all the layout page template entries where uuid = &#63; and companyId = &#63;.
	 *
	 * <p>
	 * Useful when paginating results. Returns a maximum of <code>end - start</code> instances. <code>start</code> and <code>end</code> are not primary keys, they are indexes in the result set. Thus, <code>0</code> refers to the first result in the set. Setting both <code>start</code> and <code>end</code> to <code>com.liferay.portal.kernel.dao.orm.QueryUtil#ALL_POS</code> will return the full result set. If <code>orderByComparator</code> is specified, then the query will include the given ORDER BY logic. If <code>orderByComparator</code> is absent, then the query will include the default ORDER BY logic from <code>com.liferay.layout.page.template.model.impl.LayoutPageTemplateEntryModelImpl</code>.
	 * </p>
	 *
	 * @param uuid the uuid
	 * @param companyId the company ID
	 * @param start the lower bound of the range of layout page template entries
	 * @param end the upper bound of the range of layout page template entries (not inclusive)
	 * @return the range of matching layout page template entries
	 */
	public default java.util.List<LayoutPageTemplateEntry> findByUuid_C(
		String uuid, long companyId, int start, int end) {

		return findByUuid_C(uuid, companyId, start, end, null, true);
	}

	/**
	 * Returns an ordered range of all the layout page template entries where uuid = &#63; and companyId = &#63;.
	 *
	 * <p>
	 * Useful when paginating results. Returns a maximum of <code>end - start</code> instances. <code>start</code> and <code>end</code> are not primary keys, they are indexes in the result set. Thus, <code>0</code> refers to the first result in the set. Setting both <code>start</code> and <code>end</code> to <code>com.liferay.portal.kernel.dao.orm.QueryUtil#ALL_POS</code> will return the full result set. If <code>orderByComparator</code> is specified, then the query will include the given ORDER BY logic. If <code>orderByComparator</code> is absent, then the query will include the default ORDER BY logic from <code>com.liferay.layout.page.template.model.impl.LayoutPageTemplateEntryModelImpl</code>.
	 * </p>
	 *
	 * @param uuid the uuid
	 * @param companyId the company ID
	 * @param start the lower bound of the range of layout page template entries
	 * @param end the upper bound of the range of layout page template entries (not inclusive)
	 * @param orderByComparator the comparator to order the results by (optionally <code>null</code>)
	 * @return the ordered range of matching layout page template entries
	 */
	public default java.util.List<LayoutPageTemplateEntry> findByUuid_C(
		String uuid, long companyId, int start, int end,
		com.liferay.portal.kernel.util.OrderByComparator
			<LayoutPageTemplateEntry> orderByComparator) {

		return findByUuid_C(
			uuid, companyId, start, end, orderByComparator, true);
	}

	/**
	 * Returns all the layout page template entries where groupId = &#63;.
	 *
	 * @param groupId the group ID
	 * @return the matching layout page template entries
	 */
	public default java.util.List<LayoutPageTemplateEntry> findByGroupId(
		long groupId) {

		return findByGroupId(
			groupId, com.liferay.portal.kernel.dao.orm.QueryUtil.ALL_POS,
			com.liferay.portal.kernel.dao.orm.QueryUtil.ALL_POS, null, true);
	}

	/**
	 * Returns a range of all the layout page template entries where groupId = &#63;.
	 *
	 * <p>
	 * Useful when paginating results. Returns a maximum of <code>end - start</code> instances. <code>start</code> and <code>end</code> are not primary keys, they are indexes in the result set. Thus, <code>0</code> refers to the first result in the set. Setting both <code>start</code> and <code>end</code> to <code>com.liferay.portal.kernel.dao.orm.QueryUtil#ALL_POS</code> will return the full result set. If <code>orderByComparator</code> is specified, then the query will include the given ORDER BY logic. If <code>orderByComparator</code> is absent, then the query will include the default ORDER BY logic from <code>com.liferay.layout.page.template.model.impl.LayoutPageTemplateEntryModelImpl</code>.
	 * </p>
	 *
	 * @param groupId the group ID
	 * @param start the lower bound of the range of layout page template entries
	 * @param end the upper bound of the range of layout page template entries (not inclusive)
	 * @return the range of matching layout page template entries
	 */
	public default java.util.List<LayoutPageTemplateEntry> findByGroupId(
		long groupId, int start, int end) {

		return findByGroupId(groupId, start, end, null, true);
	}

	/**
	 * Returns an ordered range of all the layout page template entries where groupId = &#63;.
	 *
	 * <p>
	 * Useful when paginating results. Returns a maximum of <code>end - start</code> instances. <code>start</code> and <code>end</code> are not primary keys, they are indexes in the result set. Thus, <code>0</code> refers to the first result in the set. Setting both <code>start</code> and <code>end</code> to <code>com.liferay.portal.kernel.dao.orm.QueryUtil#ALL_POS</code> will return the full result set. If <code>orderByComparator</code> is specified, then the query will include the given ORDER BY logic. If <code>orderByComparator</code> is absent, then the query will include the default ORDER BY logic from <code>com.liferay.layout.page.template.model.impl.LayoutPageTemplateEntryModelImpl</code>.
	 * </p>
	 *
	 * @param groupId the group ID
	 * @param start the lower bound of the range of layout page template entries
	 * @param end the upper bound of the range of layout page template entries (not inclusive)
	 * @param orderByComparator the comparator to order the results by (optionally <code>null</code>)
	 * @return the ordered range of matching layout page template entries
	 */
	public default java.util.List<LayoutPageTemplateEntry> findByGroupId(
		long groupId, int start, int end,
		com.liferay.portal.kernel.util.OrderByComparator
			<LayoutPageTemplateEntry> orderByComparator) {

		return findByGroupId(groupId, start, end, orderByComparator, true);
	}

	/**
	 * Returns all the layout page template entries that the user has permission to view where groupId = &#63;.
	 *
	 * @param groupId the group ID
	 * @return the matching layout page template entries that the user has permission to view
	 */
	public default java.util.List<LayoutPageTemplateEntry> filterFindByGroupId(
		long groupId) {

		return filterFindByGroupId(
			groupId, com.liferay.portal.kernel.dao.orm.QueryUtil.ALL_POS,
			com.liferay.portal.kernel.dao.orm.QueryUtil.ALL_POS, null);
	}

	/**
	 * Returns a range of all the layout page template entries that the user has permission to view where groupId = &#63;.
	 *
	 * <p>
	 * Useful when paginating results. Returns a maximum of <code>end - start</code> instances. <code>start</code> and <code>end</code> are not primary keys, they are indexes in the result set. Thus, <code>0</code> refers to the first result in the set. Setting both <code>start</code> and <code>end</code> to <code>com.liferay.portal.kernel.dao.orm.QueryUtil#ALL_POS</code> will return the full result set. If <code>orderByComparator</code> is specified, then the query will include the given ORDER BY logic. If <code>orderByComparator</code> is absent, then the query will include the default ORDER BY logic from <code>com.liferay.layout.page.template.model.impl.LayoutPageTemplateEntryModelImpl</code>.
	 * </p>
	 *
	 * @param groupId the group ID
	 * @param start the lower bound of the range of layout page template entries
	 * @param end the upper bound of the range of layout page template entries (not inclusive)
	 * @return the range of matching layout page template entries that the user has permission to view
	 */
	public default java.util.List<LayoutPageTemplateEntry> filterFindByGroupId(
		long groupId, int start, int end) {

		return filterFindByGroupId(groupId, start, end, null);
	}

	/**
	 * Returns all the layout page template entries where layoutPrototypeId = &#63;.
	 *
	 * @param layoutPrototypeId the layout prototype ID
	 * @return the matching layout page template entries
	 */
	public default java.util.List<LayoutPageTemplateEntry>
		findByLayoutPrototypeId(long layoutPrototypeId) {

		return findByLayoutPrototypeId(
			layoutPrototypeId,
			com.liferay.portal.kernel.dao.orm.QueryUtil.ALL_POS,
			com.liferay.portal.kernel.dao.orm.QueryUtil.ALL_POS, null, true);
	}

	/**
	 * Returns a range of all the layout page template entries where layoutPrototypeId = &#63;.
	 *
	 * <p>
	 * Useful when paginating results. Returns a maximum of <code>end - start</code> instances. <code>start</code> and <code>end</code> are not primary keys, they are indexes in the result set. Thus, <code>0</code> refers to the first result in the set. Setting both <code>start</code> and <code>end</code> to <code>com.liferay.portal.kernel.dao.orm.QueryUtil#ALL_POS</code> will return the full result set. If <code>orderByComparator</code> is specified, then the query will include the given ORDER BY logic. If <code>orderByComparator</code> is absent, then the query will include the default ORDER BY logic from <code>com.liferay.layout.page.template.model.impl.LayoutPageTemplateEntryModelImpl</code>.
	 * </p>
	 *
	 * @param layoutPrototypeId the layout prototype ID
	 * @param start the lower bound of the range of layout page template entries
	 * @param end the upper bound of the range of layout page template entries (not inclusive)
	 * @return the range of matching layout page template entries
	 */
	public default java.util.List<LayoutPageTemplateEntry>
		findByLayoutPrototypeId(long layoutPrototypeId, int start, int end) {

		return findByLayoutPrototypeId(
			layoutPrototypeId, start, end, null, true);
	}

	/**
	 * Returns an ordered range of all the layout page template entries where layoutPrototypeId = &#63;.
	 *
	 * <p>
	 * Useful when paginating results. Returns a maximum of <code>end - start</code> instances. <code>start</code> and <code>end</code> are not primary keys, they are indexes in the result set. Thus, <code>0</code> refers to the first result in the set. Setting both <code>start</code> and <code>end</code> to <code>com.liferay.portal.kernel.dao.orm.QueryUtil#ALL_POS</code> will return the full result set. If <code>orderByComparator</code> is specified, then the query will include the given ORDER BY logic. If <code>orderByComparator</code> is absent, then the query will include the default ORDER BY logic from <code>com.liferay.layout.page.template.model.impl.LayoutPageTemplateEntryModelImpl</code>.
	 * </p>
	 *
	 * @param layoutPrototypeId the layout prototype ID
	 * @param start the lower bound of the range of layout page template entries
	 * @param end the upper bound of the range of layout page template entries (not inclusive)
	 * @param orderByComparator the comparator to order the results by (optionally <code>null</code>)
	 * @return the ordered range of matching layout page template entries
	 */
	public default java.util.List<LayoutPageTemplateEntry>
		findByLayoutPrototypeId(
			long layoutPrototypeId, int start, int end,
			com.liferay.portal.kernel.util.OrderByComparator
				<LayoutPageTemplateEntry> orderByComparator) {

		return findByLayoutPrototypeId(
			layoutPrototypeId, start, end, orderByComparator, true);
	}

	/**
	 * Returns all the layout page template entries where groupId = &#63; and layoutPageTemplateCollectionId = &#63;.
	 *
	 * @param groupId the group ID
	 * @param layoutPageTemplateCollectionId the layout page template collection ID
	 * @return the matching layout page template entries
	 */
	public default java.util.List<LayoutPageTemplateEntry> findByG_L(
		long groupId, long layoutPageTemplateCollectionId) {

		return findByG_L(
			groupId, layoutPageTemplateCollectionId,
			com.liferay.portal.kernel.dao.orm.QueryUtil.ALL_POS,
			com.liferay.portal.kernel.dao.orm.QueryUtil.ALL_POS, null, true);
	}

	/**
	 * Returns a range of all the layout page template entries where groupId = &#63; and layoutPageTemplateCollectionId = &#63;.
	 *
	 * <p>
	 * Useful when paginating results. Returns a maximum of <code>end - start</code> instances. <code>start</code> and <code>end</code> are not primary keys, they are indexes in the result set. Thus, <code>0</code> refers to the first result in the set. Setting both <code>start</code> and <code>end</code> to <code>com.liferay.portal.kernel.dao.orm.QueryUtil#ALL_POS</code> will return the full result set. If <code>orderByComparator</code> is specified, then the query will include the given ORDER BY logic. If <code>orderByComparator</code> is absent, then the query will include the default ORDER BY logic from <code>com.liferay.layout.page.template.model.impl.LayoutPageTemplateEntryModelImpl</code>.
	 * </p>
	 *
	 * @param groupId the group ID
	 * @param layoutPageTemplateCollectionId the layout page template collection ID
	 * @param start the lower bound of the range of layout page template entries
	 * @param end the upper bound of the range of layout page template entries (not inclusive)
	 * @return the range of matching layout page template entries
	 */
	public default java.util.List<LayoutPageTemplateEntry> findByG_L(
		long groupId, long layoutPageTemplateCollectionId, int start, int end) {

		return findByG_L(
			groupId, layoutPageTemplateCollectionId, start, end, null, true);
	}

	/**
	 * Returns an ordered range of all the layout page template entries where groupId = &#63; and layoutPageTemplateCollectionId = &#63;.
	 *
	 * <p>
	 * Useful when paginating results. Returns a maximum of <code>end - start</code> instances. <code>start</code> and <code>end</code> are not primary keys, they are indexes in the result set. Thus, <code>0</code> refers to the first result in the set. Setting both <code>start</code> and <code>end</code> to <code>com.liferay.portal.kernel.dao.orm.QueryUtil#ALL_POS</code> will return the full result set. If <code>orderByComparator</code> is specified, then the query will include the given ORDER BY logic. If <code>orderByComparator</code> is absent, then the query will include the default ORDER BY logic from <code>com.liferay.layout.page.template.model.impl.LayoutPageTemplateEntryModelImpl</code>.
	 * </p>
	 *
	 * @param groupId the group ID
	 * @param layoutPageTemplateCollectionId the layout page template collection ID
	 * @param start the lower bound of the range of layout page template entries
	 * @param end the upper bound of the range of layout page template entries (not inclusive)
	 * @param orderByComparator the comparator to order the results by (optionally <code>null</code>)
	 * @return the ordered range of matching layout page template entries
	 */
	public default java.util.List<LayoutPageTemplateEntry> findByG_L(
		long groupId, long layoutPageTemplateCollectionId, int start, int end,
		com.liferay.portal.kernel.util.OrderByComparator
			<LayoutPageTemplateEntry> orderByComparator) {

		return findByG_L(
			groupId, layoutPageTemplateCollectionId, start, end,
			orderByComparator, true);
	}

	/**
	 * Returns all the layout page template entries that the user has permission to view where groupId = &#63; and layoutPageTemplateCollectionId = &#63;.
	 *
	 * @param groupId the group ID
	 * @param layoutPageTemplateCollectionId the layout page template collection ID
	 * @return the matching layout page template entries that the user has permission to view
	 */
	public default java.util.List<LayoutPageTemplateEntry> filterFindByG_L(
		long groupId, long layoutPageTemplateCollectionId) {

		return filterFindByG_L(
			groupId, layoutPageTemplateCollectionId,
			com.liferay.portal.kernel.dao.orm.QueryUtil.ALL_POS,
			com.liferay.portal.kernel.dao.orm.QueryUtil.ALL_POS, null);
	}

	/**
	 * Returns a range of all the layout page template entries that the user has permission to view where groupId = &#63; and layoutPageTemplateCollectionId = &#63;.
	 *
	 * <p>
	 * Useful when paginating results. Returns a maximum of <code>end - start</code> instances. <code>start</code> and <code>end</code> are not primary keys, they are indexes in the result set. Thus, <code>0</code> refers to the first result in the set. Setting both <code>start</code> and <code>end</code> to <code>com.liferay.portal.kernel.dao.orm.QueryUtil#ALL_POS</code> will return the full result set. If <code>orderByComparator</code> is specified, then the query will include the given ORDER BY logic. If <code>orderByComparator</code> is absent, then the query will include the default ORDER BY logic from <code>com.liferay.layout.page.template.model.impl.LayoutPageTemplateEntryModelImpl</code>.
	 * </p>
	 *
	 * @param groupId the group ID
	 * @param layoutPageTemplateCollectionId the layout page template collection ID
	 * @param start the lower bound of the range of layout page template entries
	 * @param end the upper bound of the range of layout page template entries (not inclusive)
	 * @return the range of matching layout page template entries that the user has permission to view
	 */
	public default java.util.List<LayoutPageTemplateEntry> filterFindByG_L(
		long groupId, long layoutPageTemplateCollectionId, int start, int end) {

		return filterFindByG_L(
			groupId, layoutPageTemplateCollectionId, start, end, null);
	}

	/**
	 * Returns all the layout page template entries where groupId = &#63; and name = &#63;.
	 *
	 * @param groupId the group ID
	 * @param name the name
	 * @return the matching layout page template entries
	 */
	public default java.util.List<LayoutPageTemplateEntry> findByG_N(
		long groupId, String name) {

		return findByG_N(
			groupId, name, com.liferay.portal.kernel.dao.orm.QueryUtil.ALL_POS,
			com.liferay.portal.kernel.dao.orm.QueryUtil.ALL_POS, null, true);
	}

	/**
	 * Returns a range of all the layout page template entries where groupId = &#63; and name = &#63;.
	 *
	 * <p>
	 * Useful when paginating results. Returns a maximum of <code>end - start</code> instances. <code>start</code> and <code>end</code> are not primary keys, they are indexes in the result set. Thus, <code>0</code> refers to the first result in the set. Setting both <code>start</code> and <code>end</code> to <code>com.liferay.portal.kernel.dao.orm.QueryUtil#ALL_POS</code> will return the full result set. If <code>orderByComparator</code> is specified, then the query will include the given ORDER BY logic. If <code>orderByComparator</code> is absent, then the query will include the default ORDER BY logic from <code>com.liferay.layout.page.template.model.impl.LayoutPageTemplateEntryModelImpl</code>.
	 * </p>
	 *
	 * @param groupId the group ID
	 * @param name the name
	 * @param start the lower bound of the range of layout page template entries
	 * @param end the upper bound of the range of layout page template entries (not inclusive)
	 * @return the range of matching layout page template entries
	 */
	public default java.util.List<LayoutPageTemplateEntry> findByG_N(
		long groupId, String name, int start, int end) {

		return findByG_N(groupId, name, start, end, null, true);
	}

	/**
	 * Returns an ordered range of all the layout page template entries where groupId = &#63; and name = &#63;.
	 *
	 * <p>
	 * Useful when paginating results. Returns a maximum of <code>end - start</code> instances. <code>start</code> and <code>end</code> are not primary keys, they are indexes in the result set. Thus, <code>0</code> refers to the first result in the set. Setting both <code>start</code> and <code>end</code> to <code>com.liferay.portal.kernel.dao.orm.QueryUtil#ALL_POS</code> will return the full result set. If <code>orderByComparator</code> is specified, then the query will include the given ORDER BY logic. If <code>orderByComparator</code> is absent, then the query will include the default ORDER BY logic from <code>com.liferay.layout.page.template.model.impl.LayoutPageTemplateEntryModelImpl</code>.
	 * </p>
	 *
	 * @param groupId the group ID
	 * @param name the name
	 * @param start the lower bound of the range of layout page template entries
	 * @param end the upper bound of the range of layout page template entries (not inclusive)
	 * @param orderByComparator the comparator to order the results by (optionally <code>null</code>)
	 * @return the ordered range of matching layout page template entries
	 */
	public default java.util.List<LayoutPageTemplateEntry> findByG_N(
		long groupId, String name, int start, int end,
		com.liferay.portal.kernel.util.OrderByComparator
			<LayoutPageTemplateEntry> orderByComparator) {

		return findByG_N(groupId, name, start, end, orderByComparator, true);
	}

	/**
	 * Returns all the layout page template entries that the user has permission to view where groupId = &#63; and name = &#63;.
	 *
	 * @param groupId the group ID
	 * @param name the name
	 * @return the matching layout page template entries that the user has permission to view
	 */
	public default java.util.List<LayoutPageTemplateEntry> filterFindByG_N(
		long groupId, String name) {

		return filterFindByG_N(
			groupId, name, com.liferay.portal.kernel.dao.orm.QueryUtil.ALL_POS,
			com.liferay.portal.kernel.dao.orm.QueryUtil.ALL_POS, null);
	}

	/**
	 * Returns a range of all the layout page template entries that the user has permission to view where groupId = &#63; and name = &#63;.
	 *
	 * <p>
	 * Useful when paginating results. Returns a maximum of <code>end - start</code> instances. <code>start</code> and <code>end</code> are not primary keys, they are indexes in the result set. Thus, <code>0</code> refers to the first result in the set. Setting both <code>start</code> and <code>end</code> to <code>com.liferay.portal.kernel.dao.orm.QueryUtil#ALL_POS</code> will return the full result set. If <code>orderByComparator</code> is specified, then the query will include the given ORDER BY logic. If <code>orderByComparator</code> is absent, then the query will include the default ORDER BY logic from <code>com.liferay.layout.page.template.model.impl.LayoutPageTemplateEntryModelImpl</code>.
	 * </p>
	 *
	 * @param groupId the group ID
	 * @param name the name
	 * @param start the lower bound of the range of layout page template entries
	 * @param end the upper bound of the range of layout page template entries (not inclusive)
	 * @return the range of matching layout page template entries that the user has permission to view
	 */
	public default java.util.List<LayoutPageTemplateEntry> filterFindByG_N(
		long groupId, String name, int start, int end) {

		return filterFindByG_N(groupId, name, start, end, null);
	}

	/**
	 * Returns all the layout page template entries where groupId = &#63; and type = &#63;.
	 *
	 * @param groupId the group ID
	 * @param type the type
	 * @return the matching layout page template entries
	 */
	public default java.util.List<LayoutPageTemplateEntry> findByG_T(
		long groupId, int type) {

		return findByG_T(
			groupId, type, com.liferay.portal.kernel.dao.orm.QueryUtil.ALL_POS,
			com.liferay.portal.kernel.dao.orm.QueryUtil.ALL_POS, null, true);
	}

	/**
	 * Returns a range of all the layout page template entries where groupId = &#63; and type = &#63;.
	 *
	 * <p>
	 * Useful when paginating results. Returns a maximum of <code>end - start</code> instances. <code>start</code> and <code>end</code> are not primary keys, they are indexes in the result set. Thus, <code>0</code> refers to the first result in the set. Setting both <code>start</code> and <code>end</code> to <code>com.liferay.portal.kernel.dao.orm.QueryUtil#ALL_POS</code> will return the full result set. If <code>orderByComparator</code> is specified, then the query will include the given ORDER BY logic. If <code>orderByComparator</code> is absent, then the query will include the default ORDER BY logic from <code>com.liferay.layout.page.template.model.impl.LayoutPageTemplateEntryModelImpl</code>.
	 * </p>
	 *
	 * @param groupId the group ID
	 * @param type the type
	 * @param start the lower bound of the range of layout page template entries
	 * @param end the upper bound of the range of layout page template entries (not inclusive)
	 * @return the range of matching layout page template entries
	 */
	public default java.util.List<LayoutPageTemplateEntry> findByG_T(
		long groupId, int type, int start, int end) {

		return findByG_T(groupId, type, start, end, null, true);
	}

	/**
	 * Returns an ordered range of all the layout page template entries where groupId = &#63; and type = &#63;.
	 *
	 * <p>
	 * Useful when paginating results. Returns a maximum of <code>end - start</code> instances. <code>start</code> and <code>end</code> are not primary keys, they are indexes in the result set. Thus, <code>0</code> refers to the first result in the set. Setting both <code>start</code> and <code>end</code> to <code>com.liferay.portal.kernel.dao.orm.QueryUtil#ALL_POS</code> will return the full result set. If <code>orderByComparator</code> is specified, then the query will include the given ORDER BY logic. If <code>orderByComparator</code> is absent, then the query will include the default ORDER BY logic from <code>com.liferay.layout.page.template.model.impl.LayoutPageTemplateEntryModelImpl</code>.
	 * </p>
	 *
	 * @param groupId the group ID
	 * @param type the type
	 * @param start the lower bound of the range of layout page template entries
	 * @param end the upper bound of the range of layout page template entries (not inclusive)
	 * @param orderByComparator the comparator to order the results by (optionally <code>null</code>)
	 * @return the ordered range of matching layout page template entries
	 */
	public default java.util.List<LayoutPageTemplateEntry> findByG_T(
		long groupId, int type, int start, int end,
		com.liferay.portal.kernel.util.OrderByComparator
			<LayoutPageTemplateEntry> orderByComparator) {

		return findByG_T(groupId, type, start, end, orderByComparator, true);
	}

	/**
	 * Returns all the layout page template entries where groupId = &#63; and type = any &#63;.
	 *
	 * @param groupId the group ID
	 * @param types the types
	 * @return the matching layout page template entries
	 */
	public default java.util.List<LayoutPageTemplateEntry> findByG_T(
		long groupId, int[] types) {

		return findByG_T(
			groupId, types, com.liferay.portal.kernel.dao.orm.QueryUtil.ALL_POS,
			com.liferay.portal.kernel.dao.orm.QueryUtil.ALL_POS, null, true);
	}

	/**
	 * Returns a range of all the layout page template entries where groupId = &#63; and type = any &#63;.
	 *
	 * <p>
	 * Useful when paginating results. Returns a maximum of <code>end - start</code> instances. <code>start</code> and <code>end</code> are not primary keys, they are indexes in the result set. Thus, <code>0</code> refers to the first result in the set. Setting both <code>start</code> and <code>end</code> to <code>com.liferay.portal.kernel.dao.orm.QueryUtil#ALL_POS</code> will return the full result set. If <code>orderByComparator</code> is specified, then the query will include the given ORDER BY logic. If <code>orderByComparator</code> is absent, then the query will include the default ORDER BY logic from <code>com.liferay.layout.page.template.model.impl.LayoutPageTemplateEntryModelImpl</code>.
	 * </p>
	 *
	 * @param groupId the group ID
	 * @param types the types
	 * @param start the lower bound of the range of layout page template entries
	 * @param end the upper bound of the range of layout page template entries (not inclusive)
	 * @return the range of matching layout page template entries
	 */
	public default java.util.List<LayoutPageTemplateEntry> findByG_T(
		long groupId, int[] types, int start, int end) {

		return findByG_T(groupId, types, start, end, null, true);
	}

	/**
	 * Returns an ordered range of all the layout page template entries where groupId = &#63; and type = any &#63;.
	 *
	 * <p>
	 * Useful when paginating results. Returns a maximum of <code>end - start</code> instances. <code>start</code> and <code>end</code> are not primary keys, they are indexes in the result set. Thus, <code>0</code> refers to the first result in the set. Setting both <code>start</code> and <code>end</code> to <code>com.liferay.portal.kernel.dao.orm.QueryUtil#ALL_POS</code> will return the full result set. If <code>orderByComparator</code> is specified, then the query will include the given ORDER BY logic. If <code>orderByComparator</code> is absent, then the query will include the default ORDER BY logic from <code>com.liferay.layout.page.template.model.impl.LayoutPageTemplateEntryModelImpl</code>.
	 * </p>
	 *
	 * @param groupId the group ID
	 * @param types the types
	 * @param start the lower bound of the range of layout page template entries
	 * @param end the upper bound of the range of layout page template entries (not inclusive)
	 * @param orderByComparator the comparator to order the results by (optionally <code>null</code>)
	 * @return the ordered range of matching layout page template entries
	 */
	public default java.util.List<LayoutPageTemplateEntry> findByG_T(
		long groupId, int[] types, int start, int end,
		com.liferay.portal.kernel.util.OrderByComparator
			<LayoutPageTemplateEntry> orderByComparator) {

		return findByG_T(groupId, types, start, end, orderByComparator, true);
	}

	/**
	 * Returns all the layout page template entries that the user has permission to view where groupId = &#63; and type = &#63;.
	 *
	 * @param groupId the group ID
	 * @param type the type
	 * @return the matching layout page template entries that the user has permission to view
	 */
	public default java.util.List<LayoutPageTemplateEntry> filterFindByG_T(
		long groupId, int type) {

		return filterFindByG_T(
			groupId, type, com.liferay.portal.kernel.dao.orm.QueryUtil.ALL_POS,
			com.liferay.portal.kernel.dao.orm.QueryUtil.ALL_POS, null);
	}

	/**
	 * Returns a range of all the layout page template entries that the user has permission to view where groupId = &#63; and type = &#63;.
	 *
	 * <p>
	 * Useful when paginating results. Returns a maximum of <code>end - start</code> instances. <code>start</code> and <code>end</code> are not primary keys, they are indexes in the result set. Thus, <code>0</code> refers to the first result in the set. Setting both <code>start</code> and <code>end</code> to <code>com.liferay.portal.kernel.dao.orm.QueryUtil#ALL_POS</code> will return the full result set. If <code>orderByComparator</code> is specified, then the query will include the given ORDER BY logic. If <code>orderByComparator</code> is absent, then the query will include the default ORDER BY logic from <code>com.liferay.layout.page.template.model.impl.LayoutPageTemplateEntryModelImpl</code>.
	 * </p>
	 *
	 * @param groupId the group ID
	 * @param type the type
	 * @param start the lower bound of the range of layout page template entries
	 * @param end the upper bound of the range of layout page template entries (not inclusive)
	 * @return the range of matching layout page template entries that the user has permission to view
	 */
	public default java.util.List<LayoutPageTemplateEntry> filterFindByG_T(
		long groupId, int type, int start, int end) {

		return filterFindByG_T(groupId, type, start, end, null);
	}

	/**
	 * Returns all the layout page template entries that the user has permission to view where groupId = &#63; and type = any &#63;.
	 *
	 * @param groupId the group ID
	 * @param types the types
	 * @return the matching layout page template entries that the user has permission to view
	 */
	public default java.util.List<LayoutPageTemplateEntry> filterFindByG_T(
		long groupId, int[] types) {

		return filterFindByG_T(
			groupId, types, com.liferay.portal.kernel.dao.orm.QueryUtil.ALL_POS,
			com.liferay.portal.kernel.dao.orm.QueryUtil.ALL_POS, null);
	}

	/**
	 * Returns a range of all the layout page template entries that the user has permission to view where groupId = &#63; and type = any &#63;.
	 *
	 * <p>
	 * Useful when paginating results. Returns a maximum of <code>end - start</code> instances. <code>start</code> and <code>end</code> are not primary keys, they are indexes in the result set. Thus, <code>0</code> refers to the first result in the set. Setting both <code>start</code> and <code>end</code> to <code>com.liferay.portal.kernel.dao.orm.QueryUtil#ALL_POS</code> will return the full result set. If <code>orderByComparator</code> is specified, then the query will include the given ORDER BY logic. If <code>orderByComparator</code> is absent, then the query will include the default ORDER BY logic from <code>com.liferay.layout.page.template.model.impl.LayoutPageTemplateEntryModelImpl</code>.
	 * </p>
	 *
	 * @param groupId the group ID
	 * @param types the types
	 * @param start the lower bound of the range of layout page template entries
	 * @param end the upper bound of the range of layout page template entries (not inclusive)
	 * @return the range of matching layout page template entries that the user has permission to view
	 */
	public default java.util.List<LayoutPageTemplateEntry> filterFindByG_T(
		long groupId, int[] types, int start, int end) {

		return filterFindByG_T(groupId, types, start, end, null);
	}

	/**
	 * Returns all the layout page template entries where groupId = &#63; and status = &#63;.
	 *
	 * @param groupId the group ID
	 * @param status the status
	 * @return the matching layout page template entries
	 */
	public default java.util.List<LayoutPageTemplateEntry> findByG_S(
		long groupId, int status) {

		return findByG_S(
			groupId, status,
			com.liferay.portal.kernel.dao.orm.QueryUtil.ALL_POS,
			com.liferay.portal.kernel.dao.orm.QueryUtil.ALL_POS, null, true);
	}

	/**
	 * Returns a range of all the layout page template entries where groupId = &#63; and status = &#63;.
	 *
	 * <p>
	 * Useful when paginating results. Returns a maximum of <code>end - start</code> instances. <code>start</code> and <code>end</code> are not primary keys, they are indexes in the result set. Thus, <code>0</code> refers to the first result in the set. Setting both <code>start</code> and <code>end</code> to <code>com.liferay.portal.kernel.dao.orm.QueryUtil#ALL_POS</code> will return the full result set. If <code>orderByComparator</code> is specified, then the query will include the given ORDER BY logic. If <code>orderByComparator</code> is absent, then the query will include the default ORDER BY logic from <code>com.liferay.layout.page.template.model.impl.LayoutPageTemplateEntryModelImpl</code>.
	 * </p>
	 *
	 * @param groupId the group ID
	 * @param status the status
	 * @param start the lower bound of the range of layout page template entries
	 * @param end the upper bound of the range of layout page template entries (not inclusive)
	 * @return the range of matching layout page template entries
	 */
	public default java.util.List<LayoutPageTemplateEntry> findByG_S(
		long groupId, int status, int start, int end) {

		return findByG_S(groupId, status, start, end, null, true);
	}

	/**
	 * Returns an ordered range of all the layout page template entries where groupId = &#63; and status = &#63;.
	 *
	 * <p>
	 * Useful when paginating results. Returns a maximum of <code>end - start</code> instances. <code>start</code> and <code>end</code> are not primary keys, they are indexes in the result set. Thus, <code>0</code> refers to the first result in the set. Setting both <code>start</code> and <code>end</code> to <code>com.liferay.portal.kernel.dao.orm.QueryUtil#ALL_POS</code> will return the full result set. If <code>orderByComparator</code> is specified, then the query will include the given ORDER BY logic. If <code>orderByComparator</code> is absent, then the query will include the default ORDER BY logic from <code>com.liferay.layout.page.template.model.impl.LayoutPageTemplateEntryModelImpl</code>.
	 * </p>
	 *
	 * @param groupId the group ID
	 * @param status the status
	 * @param start the lower bound of the range of layout page template entries
	 * @param end the upper bound of the range of layout page template entries (not inclusive)
	 * @param orderByComparator the comparator to order the results by (optionally <code>null</code>)
	 * @return the ordered range of matching layout page template entries
	 */
	public default java.util.List<LayoutPageTemplateEntry> findByG_S(
		long groupId, int status, int start, int end,
		com.liferay.portal.kernel.util.OrderByComparator
			<LayoutPageTemplateEntry> orderByComparator) {

		return findByG_S(groupId, status, start, end, orderByComparator, true);
	}

	/**
	 * Returns all the layout page template entries that the user has permission to view where groupId = &#63; and status = &#63;.
	 *
	 * @param groupId the group ID
	 * @param status the status
	 * @return the matching layout page template entries that the user has permission to view
	 */
	public default java.util.List<LayoutPageTemplateEntry> filterFindByG_S(
		long groupId, int status) {

		return filterFindByG_S(
			groupId, status,
			com.liferay.portal.kernel.dao.orm.QueryUtil.ALL_POS,
			com.liferay.portal.kernel.dao.orm.QueryUtil.ALL_POS, null);
	}

	/**
	 * Returns a range of all the layout page template entries that the user has permission to view where groupId = &#63; and status = &#63;.
	 *
	 * <p>
	 * Useful when paginating results. Returns a maximum of <code>end - start</code> instances. <code>start</code> and <code>end</code> are not primary keys, they are indexes in the result set. Thus, <code>0</code> refers to the first result in the set. Setting both <code>start</code> and <code>end</code> to <code>com.liferay.portal.kernel.dao.orm.QueryUtil#ALL_POS</code> will return the full result set. If <code>orderByComparator</code> is specified, then the query will include the given ORDER BY logic. If <code>orderByComparator</code> is absent, then the query will include the default ORDER BY logic from <code>com.liferay.layout.page.template.model.impl.LayoutPageTemplateEntryModelImpl</code>.
	 * </p>
	 *
	 * @param groupId the group ID
	 * @param status the status
	 * @param start the lower bound of the range of layout page template entries
	 * @param end the upper bound of the range of layout page template entries (not inclusive)
	 * @return the range of matching layout page template entries that the user has permission to view
	 */
	public default java.util.List<LayoutPageTemplateEntry> filterFindByG_S(
		long groupId, int status, int start, int end) {

		return filterFindByG_S(groupId, status, start, end, null);
	}

	/**
	 * Returns all the layout page template entries where groupId = &#63; and layoutPageTemplateCollectionId = &#63; and type = &#63;.
	 *
	 * @param groupId the group ID
	 * @param layoutPageTemplateCollectionId the layout page template collection ID
	 * @param type the type
	 * @return the matching layout page template entries
	 */
	public default java.util.List<LayoutPageTemplateEntry> findByG_L_T(
		long groupId, long layoutPageTemplateCollectionId, int type) {

		return findByG_L_T(
			groupId, layoutPageTemplateCollectionId, type,
			com.liferay.portal.kernel.dao.orm.QueryUtil.ALL_POS,
			com.liferay.portal.kernel.dao.orm.QueryUtil.ALL_POS, null, true);
	}

	/**
	 * Returns a range of all the layout page template entries where groupId = &#63; and layoutPageTemplateCollectionId = &#63; and type = &#63;.
	 *
	 * <p>
	 * Useful when paginating results. Returns a maximum of <code>end - start</code> instances. <code>start</code> and <code>end</code> are not primary keys, they are indexes in the result set. Thus, <code>0</code> refers to the first result in the set. Setting both <code>start</code> and <code>end</code> to <code>com.liferay.portal.kernel.dao.orm.QueryUtil#ALL_POS</code> will return the full result set. If <code>orderByComparator</code> is specified, then the query will include the given ORDER BY logic. If <code>orderByComparator</code> is absent, then the query will include the default ORDER BY logic from <code>com.liferay.layout.page.template.model.impl.LayoutPageTemplateEntryModelImpl</code>.
	 * </p>
	 *
	 * @param groupId the group ID
	 * @param layoutPageTemplateCollectionId the layout page template collection ID
	 * @param type the type
	 * @param start the lower bound of the range of layout page template entries
	 * @param end the upper bound of the range of layout page template entries (not inclusive)
	 * @return the range of matching layout page template entries
	 */
	public default java.util.List<LayoutPageTemplateEntry> findByG_L_T(
		long groupId, long layoutPageTemplateCollectionId, int type, int start,
		int end) {

		return findByG_L_T(
			groupId, layoutPageTemplateCollectionId, type, start, end, null,
			true);
	}

	/**
	 * Returns an ordered range of all the layout page template entries where groupId = &#63; and layoutPageTemplateCollectionId = &#63; and type = &#63;.
	 *
	 * <p>
	 * Useful when paginating results. Returns a maximum of <code>end - start</code> instances. <code>start</code> and <code>end</code> are not primary keys, they are indexes in the result set. Thus, <code>0</code> refers to the first result in the set. Setting both <code>start</code> and <code>end</code> to <code>com.liferay.portal.kernel.dao.orm.QueryUtil#ALL_POS</code> will return the full result set. If <code>orderByComparator</code> is specified, then the query will include the given ORDER BY logic. If <code>orderByComparator</code> is absent, then the query will include the default ORDER BY logic from <code>com.liferay.layout.page.template.model.impl.LayoutPageTemplateEntryModelImpl</code>.
	 * </p>
	 *
	 * @param groupId the group ID
	 * @param layoutPageTemplateCollectionId the layout page template collection ID
	 * @param type the type
	 * @param start the lower bound of the range of layout page template entries
	 * @param end the upper bound of the range of layout page template entries (not inclusive)
	 * @param orderByComparator the comparator to order the results by (optionally <code>null</code>)
	 * @return the ordered range of matching layout page template entries
	 */
	public default java.util.List<LayoutPageTemplateEntry> findByG_L_T(
		long groupId, long layoutPageTemplateCollectionId, int type, int start,
		int end,
		com.liferay.portal.kernel.util.OrderByComparator
			<LayoutPageTemplateEntry> orderByComparator) {

		return findByG_L_T(
			groupId, layoutPageTemplateCollectionId, type, start, end,
			orderByComparator, true);
	}

	/**
	 * Returns all the layout page template entries that the user has permission to view where groupId = &#63; and layoutPageTemplateCollectionId = &#63; and type = &#63;.
	 *
	 * @param groupId the group ID
	 * @param layoutPageTemplateCollectionId the layout page template collection ID
	 * @param type the type
	 * @return the matching layout page template entries that the user has permission to view
	 */
	public default java.util.List<LayoutPageTemplateEntry> filterFindByG_L_T(
		long groupId, long layoutPageTemplateCollectionId, int type) {

		return filterFindByG_L_T(
			groupId, layoutPageTemplateCollectionId, type,
			com.liferay.portal.kernel.dao.orm.QueryUtil.ALL_POS,
			com.liferay.portal.kernel.dao.orm.QueryUtil.ALL_POS, null);
	}

	/**
	 * Returns a range of all the layout page template entries that the user has permission to view where groupId = &#63; and layoutPageTemplateCollectionId = &#63; and type = &#63;.
	 *
	 * <p>
	 * Useful when paginating results. Returns a maximum of <code>end - start</code> instances. <code>start</code> and <code>end</code> are not primary keys, they are indexes in the result set. Thus, <code>0</code> refers to the first result in the set. Setting both <code>start</code> and <code>end</code> to <code>com.liferay.portal.kernel.dao.orm.QueryUtil#ALL_POS</code> will return the full result set. If <code>orderByComparator</code> is specified, then the query will include the given ORDER BY logic. If <code>orderByComparator</code> is absent, then the query will include the default ORDER BY logic from <code>com.liferay.layout.page.template.model.impl.LayoutPageTemplateEntryModelImpl</code>.
	 * </p>
	 *
	 * @param groupId the group ID
	 * @param layoutPageTemplateCollectionId the layout page template collection ID
	 * @param type the type
	 * @param start the lower bound of the range of layout page template entries
	 * @param end the upper bound of the range of layout page template entries (not inclusive)
	 * @return the range of matching layout page template entries that the user has permission to view
	 */
	public default java.util.List<LayoutPageTemplateEntry> filterFindByG_L_T(
		long groupId, long layoutPageTemplateCollectionId, int type, int start,
		int end) {

		return filterFindByG_L_T(
			groupId, layoutPageTemplateCollectionId, type, start, end, null);
	}

	/**
	 * Returns all the layout page template entries where groupId = &#63; and layoutPageTemplateCollectionId = &#63; and status = &#63;.
	 *
	 * @param groupId the group ID
	 * @param layoutPageTemplateCollectionId the layout page template collection ID
	 * @param status the status
	 * @return the matching layout page template entries
	 */
	public default java.util.List<LayoutPageTemplateEntry> findByG_L_S(
		long groupId, long layoutPageTemplateCollectionId, int status) {

		return findByG_L_S(
			groupId, layoutPageTemplateCollectionId, status,
			com.liferay.portal.kernel.dao.orm.QueryUtil.ALL_POS,
			com.liferay.portal.kernel.dao.orm.QueryUtil.ALL_POS, null, true);
	}

	/**
	 * Returns a range of all the layout page template entries where groupId = &#63; and layoutPageTemplateCollectionId = &#63; and status = &#63;.
	 *
	 * <p>
	 * Useful when paginating results. Returns a maximum of <code>end - start</code> instances. <code>start</code> and <code>end</code> are not primary keys, they are indexes in the result set. Thus, <code>0</code> refers to the first result in the set. Setting both <code>start</code> and <code>end</code> to <code>com.liferay.portal.kernel.dao.orm.QueryUtil#ALL_POS</code> will return the full result set. If <code>orderByComparator</code> is specified, then the query will include the given ORDER BY logic. If <code>orderByComparator</code> is absent, then the query will include the default ORDER BY logic from <code>com.liferay.layout.page.template.model.impl.LayoutPageTemplateEntryModelImpl</code>.
	 * </p>
	 *
	 * @param groupId the group ID
	 * @param layoutPageTemplateCollectionId the layout page template collection ID
	 * @param status the status
	 * @param start the lower bound of the range of layout page template entries
	 * @param end the upper bound of the range of layout page template entries (not inclusive)
	 * @return the range of matching layout page template entries
	 */
	public default java.util.List<LayoutPageTemplateEntry> findByG_L_S(
		long groupId, long layoutPageTemplateCollectionId, int status,
		int start, int end) {

		return findByG_L_S(
			groupId, layoutPageTemplateCollectionId, status, start, end, null,
			true);
	}

	/**
	 * Returns an ordered range of all the layout page template entries where groupId = &#63; and layoutPageTemplateCollectionId = &#63; and status = &#63;.
	 *
	 * <p>
	 * Useful when paginating results. Returns a maximum of <code>end - start</code> instances. <code>start</code> and <code>end</code> are not primary keys, they are indexes in the result set. Thus, <code>0</code> refers to the first result in the set. Setting both <code>start</code> and <code>end</code> to <code>com.liferay.portal.kernel.dao.orm.QueryUtil#ALL_POS</code> will return the full result set. If <code>orderByComparator</code> is specified, then the query will include the given ORDER BY logic. If <code>orderByComparator</code> is absent, then the query will include the default ORDER BY logic from <code>com.liferay.layout.page.template.model.impl.LayoutPageTemplateEntryModelImpl</code>.
	 * </p>
	 *
	 * @param groupId the group ID
	 * @param layoutPageTemplateCollectionId the layout page template collection ID
	 * @param status the status
	 * @param start the lower bound of the range of layout page template entries
	 * @param end the upper bound of the range of layout page template entries (not inclusive)
	 * @param orderByComparator the comparator to order the results by (optionally <code>null</code>)
	 * @return the ordered range of matching layout page template entries
	 */
	public default java.util.List<LayoutPageTemplateEntry> findByG_L_S(
		long groupId, long layoutPageTemplateCollectionId, int status,
		int start, int end,
		com.liferay.portal.kernel.util.OrderByComparator
			<LayoutPageTemplateEntry> orderByComparator) {

		return findByG_L_S(
			groupId, layoutPageTemplateCollectionId, status, start, end,
			orderByComparator, true);
	}

	/**
	 * Returns all the layout page template entries that the user has permission to view where groupId = &#63; and layoutPageTemplateCollectionId = &#63; and status = &#63;.
	 *
	 * @param groupId the group ID
	 * @param layoutPageTemplateCollectionId the layout page template collection ID
	 * @param status the status
	 * @return the matching layout page template entries that the user has permission to view
	 */
	public default java.util.List<LayoutPageTemplateEntry> filterFindByG_L_S(
		long groupId, long layoutPageTemplateCollectionId, int status) {

		return filterFindByG_L_S(
			groupId, layoutPageTemplateCollectionId, status,
			com.liferay.portal.kernel.dao.orm.QueryUtil.ALL_POS,
			com.liferay.portal.kernel.dao.orm.QueryUtil.ALL_POS, null);
	}

	/**
	 * Returns a range of all the layout page template entries that the user has permission to view where groupId = &#63; and layoutPageTemplateCollectionId = &#63; and status = &#63;.
	 *
	 * <p>
	 * Useful when paginating results. Returns a maximum of <code>end - start</code> instances. <code>start</code> and <code>end</code> are not primary keys, they are indexes in the result set. Thus, <code>0</code> refers to the first result in the set. Setting both <code>start</code> and <code>end</code> to <code>com.liferay.portal.kernel.dao.orm.QueryUtil#ALL_POS</code> will return the full result set. If <code>orderByComparator</code> is specified, then the query will include the given ORDER BY logic. If <code>orderByComparator</code> is absent, then the query will include the default ORDER BY logic from <code>com.liferay.layout.page.template.model.impl.LayoutPageTemplateEntryModelImpl</code>.
	 * </p>
	 *
	 * @param groupId the group ID
	 * @param layoutPageTemplateCollectionId the layout page template collection ID
	 * @param status the status
	 * @param start the lower bound of the range of layout page template entries
	 * @param end the upper bound of the range of layout page template entries (not inclusive)
	 * @return the range of matching layout page template entries that the user has permission to view
	 */
	public default java.util.List<LayoutPageTemplateEntry> filterFindByG_L_S(
		long groupId, long layoutPageTemplateCollectionId, int status,
		int start, int end) {

		return filterFindByG_L_S(
			groupId, layoutPageTemplateCollectionId, status, start, end, null);
	}

	/**
	 * Returns all the layout page template entries where groupId = &#63; and name = &#63; and type = &#63;.
	 *
	 * @param groupId the group ID
	 * @param name the name
	 * @param type the type
	 * @return the matching layout page template entries
	 */
	public default java.util.List<LayoutPageTemplateEntry> findByG_N_T(
		long groupId, String name, int type) {

		return findByG_N_T(
			groupId, name, type,
			com.liferay.portal.kernel.dao.orm.QueryUtil.ALL_POS,
			com.liferay.portal.kernel.dao.orm.QueryUtil.ALL_POS, null, true);
	}

	/**
	 * Returns a range of all the layout page template entries where groupId = &#63; and name = &#63; and type = &#63;.
	 *
	 * <p>
	 * Useful when paginating results. Returns a maximum of <code>end - start</code> instances. <code>start</code> and <code>end</code> are not primary keys, they are indexes in the result set. Thus, <code>0</code> refers to the first result in the set. Setting both <code>start</code> and <code>end</code> to <code>com.liferay.portal.kernel.dao.orm.QueryUtil#ALL_POS</code> will return the full result set. If <code>orderByComparator</code> is specified, then the query will include the given ORDER BY logic. If <code>orderByComparator</code> is absent, then the query will include the default ORDER BY logic from <code>com.liferay.layout.page.template.model.impl.LayoutPageTemplateEntryModelImpl</code>.
	 * </p>
	 *
	 * @param groupId the group ID
	 * @param name the name
	 * @param type the type
	 * @param start the lower bound of the range of layout page template entries
	 * @param end the upper bound of the range of layout page template entries (not inclusive)
	 * @return the range of matching layout page template entries
	 */
	public default java.util.List<LayoutPageTemplateEntry> findByG_N_T(
		long groupId, String name, int type, int start, int end) {

		return findByG_N_T(groupId, name, type, start, end, null, true);
	}

	/**
	 * Returns an ordered range of all the layout page template entries where groupId = &#63; and name = &#63; and type = &#63;.
	 *
	 * <p>
	 * Useful when paginating results. Returns a maximum of <code>end - start</code> instances. <code>start</code> and <code>end</code> are not primary keys, they are indexes in the result set. Thus, <code>0</code> refers to the first result in the set. Setting both <code>start</code> and <code>end</code> to <code>com.liferay.portal.kernel.dao.orm.QueryUtil#ALL_POS</code> will return the full result set. If <code>orderByComparator</code> is specified, then the query will include the given ORDER BY logic. If <code>orderByComparator</code> is absent, then the query will include the default ORDER BY logic from <code>com.liferay.layout.page.template.model.impl.LayoutPageTemplateEntryModelImpl</code>.
	 * </p>
	 *
	 * @param groupId the group ID
	 * @param name the name
	 * @param type the type
	 * @param start the lower bound of the range of layout page template entries
	 * @param end the upper bound of the range of layout page template entries (not inclusive)
	 * @param orderByComparator the comparator to order the results by (optionally <code>null</code>)
	 * @return the ordered range of matching layout page template entries
	 */
	public default java.util.List<LayoutPageTemplateEntry> findByG_N_T(
		long groupId, String name, int type, int start, int end,
		com.liferay.portal.kernel.util.OrderByComparator
			<LayoutPageTemplateEntry> orderByComparator) {

		return findByG_N_T(
			groupId, name, type, start, end, orderByComparator, true);
	}

	/**
	 * Returns all the layout page template entries that the user has permission to view where groupId = &#63; and name = &#63; and type = &#63;.
	 *
	 * @param groupId the group ID
	 * @param name the name
	 * @param type the type
	 * @return the matching layout page template entries that the user has permission to view
	 */
	public default java.util.List<LayoutPageTemplateEntry> filterFindByG_N_T(
		long groupId, String name, int type) {

		return filterFindByG_N_T(
			groupId, name, type,
			com.liferay.portal.kernel.dao.orm.QueryUtil.ALL_POS,
			com.liferay.portal.kernel.dao.orm.QueryUtil.ALL_POS, null);
	}

	/**
	 * Returns a range of all the layout page template entries that the user has permission to view where groupId = &#63; and name = &#63; and type = &#63;.
	 *
	 * <p>
	 * Useful when paginating results. Returns a maximum of <code>end - start</code> instances. <code>start</code> and <code>end</code> are not primary keys, they are indexes in the result set. Thus, <code>0</code> refers to the first result in the set. Setting both <code>start</code> and <code>end</code> to <code>com.liferay.portal.kernel.dao.orm.QueryUtil#ALL_POS</code> will return the full result set. If <code>orderByComparator</code> is specified, then the query will include the given ORDER BY logic. If <code>orderByComparator</code> is absent, then the query will include the default ORDER BY logic from <code>com.liferay.layout.page.template.model.impl.LayoutPageTemplateEntryModelImpl</code>.
	 * </p>
	 *
	 * @param groupId the group ID
	 * @param name the name
	 * @param type the type
	 * @param start the lower bound of the range of layout page template entries
	 * @param end the upper bound of the range of layout page template entries (not inclusive)
	 * @return the range of matching layout page template entries that the user has permission to view
	 */
	public default java.util.List<LayoutPageTemplateEntry> filterFindByG_N_T(
		long groupId, String name, int type, int start, int end) {

		return filterFindByG_N_T(groupId, name, type, start, end, null);
	}

	/**
	 * Returns all the layout page template entries where groupId = &#63; and type = &#63; and status = &#63;.
	 *
	 * @param groupId the group ID
	 * @param type the type
	 * @param status the status
	 * @return the matching layout page template entries
	 */
	public default java.util.List<LayoutPageTemplateEntry> findByG_T_S(
		long groupId, int type, int status) {

		return findByG_T_S(
			groupId, type, status,
			com.liferay.portal.kernel.dao.orm.QueryUtil.ALL_POS,
			com.liferay.portal.kernel.dao.orm.QueryUtil.ALL_POS, null, true);
	}

	/**
	 * Returns a range of all the layout page template entries where groupId = &#63; and type = &#63; and status = &#63;.
	 *
	 * <p>
	 * Useful when paginating results. Returns a maximum of <code>end - start</code> instances. <code>start</code> and <code>end</code> are not primary keys, they are indexes in the result set. Thus, <code>0</code> refers to the first result in the set. Setting both <code>start</code> and <code>end</code> to <code>com.liferay.portal.kernel.dao.orm.QueryUtil#ALL_POS</code> will return the full result set. If <code>orderByComparator</code> is specified, then the query will include the given ORDER BY logic. If <code>orderByComparator</code> is absent, then the query will include the default ORDER BY logic from <code>com.liferay.layout.page.template.model.impl.LayoutPageTemplateEntryModelImpl</code>.
	 * </p>
	 *
	 * @param groupId the group ID
	 * @param type the type
	 * @param status the status
	 * @param start the lower bound of the range of layout page template entries
	 * @param end the upper bound of the range of layout page template entries (not inclusive)
	 * @return the range of matching layout page template entries
	 */
	public default java.util.List<LayoutPageTemplateEntry> findByG_T_S(
		long groupId, int type, int status, int start, int end) {

		return findByG_T_S(groupId, type, status, start, end, null, true);
	}

	/**
	 * Returns an ordered range of all the layout page template entries where groupId = &#63; and type = &#63; and status = &#63;.
	 *
	 * <p>
	 * Useful when paginating results. Returns a maximum of <code>end - start</code> instances. <code>start</code> and <code>end</code> are not primary keys, they are indexes in the result set. Thus, <code>0</code> refers to the first result in the set. Setting both <code>start</code> and <code>end</code> to <code>com.liferay.portal.kernel.dao.orm.QueryUtil#ALL_POS</code> will return the full result set. If <code>orderByComparator</code> is specified, then the query will include the given ORDER BY logic. If <code>orderByComparator</code> is absent, then the query will include the default ORDER BY logic from <code>com.liferay.layout.page.template.model.impl.LayoutPageTemplateEntryModelImpl</code>.
	 * </p>
	 *
	 * @param groupId the group ID
	 * @param type the type
	 * @param status the status
	 * @param start the lower bound of the range of layout page template entries
	 * @param end the upper bound of the range of layout page template entries (not inclusive)
	 * @param orderByComparator the comparator to order the results by (optionally <code>null</code>)
	 * @return the ordered range of matching layout page template entries
	 */
	public default java.util.List<LayoutPageTemplateEntry> findByG_T_S(
		long groupId, int type, int status, int start, int end,
		com.liferay.portal.kernel.util.OrderByComparator
			<LayoutPageTemplateEntry> orderByComparator) {

		return findByG_T_S(
			groupId, type, status, start, end, orderByComparator, true);
	}

	/**
	 * Returns all the layout page template entries where groupId = &#63; and type = any &#63; and status = &#63;.
	 *
	 * @param groupId the group ID
	 * @param types the types
	 * @param status the status
	 * @return the matching layout page template entries
	 */
	public default java.util.List<LayoutPageTemplateEntry> findByG_T_S(
		long groupId, int[] types, int status) {

		return findByG_T_S(
			groupId, types, status,
			com.liferay.portal.kernel.dao.orm.QueryUtil.ALL_POS,
			com.liferay.portal.kernel.dao.orm.QueryUtil.ALL_POS, null, true);
	}

	/**
	 * Returns a range of all the layout page template entries where groupId = &#63; and type = any &#63; and status = &#63;.
	 *
	 * <p>
	 * Useful when paginating results. Returns a maximum of <code>end - start</code> instances. <code>start</code> and <code>end</code> are not primary keys, they are indexes in the result set. Thus, <code>0</code> refers to the first result in the set. Setting both <code>start</code> and <code>end</code> to <code>com.liferay.portal.kernel.dao.orm.QueryUtil#ALL_POS</code> will return the full result set. If <code>orderByComparator</code> is specified, then the query will include the given ORDER BY logic. If <code>orderByComparator</code> is absent, then the query will include the default ORDER BY logic from <code>com.liferay.layout.page.template.model.impl.LayoutPageTemplateEntryModelImpl</code>.
	 * </p>
	 *
	 * @param groupId the group ID
	 * @param types the types
	 * @param status the status
	 * @param start the lower bound of the range of layout page template entries
	 * @param end the upper bound of the range of layout page template entries (not inclusive)
	 * @return the range of matching layout page template entries
	 */
	public default java.util.List<LayoutPageTemplateEntry> findByG_T_S(
		long groupId, int[] types, int status, int start, int end) {

		return findByG_T_S(groupId, types, status, start, end, null, true);
	}

	/**
	 * Returns an ordered range of all the layout page template entries where groupId = &#63; and type = any &#63; and status = &#63;.
	 *
	 * <p>
	 * Useful when paginating results. Returns a maximum of <code>end - start</code> instances. <code>start</code> and <code>end</code> are not primary keys, they are indexes in the result set. Thus, <code>0</code> refers to the first result in the set. Setting both <code>start</code> and <code>end</code> to <code>com.liferay.portal.kernel.dao.orm.QueryUtil#ALL_POS</code> will return the full result set. If <code>orderByComparator</code> is specified, then the query will include the given ORDER BY logic. If <code>orderByComparator</code> is absent, then the query will include the default ORDER BY logic from <code>com.liferay.layout.page.template.model.impl.LayoutPageTemplateEntryModelImpl</code>.
	 * </p>
	 *
	 * @param groupId the group ID
	 * @param types the types
	 * @param status the status
	 * @param start the lower bound of the range of layout page template entries
	 * @param end the upper bound of the range of layout page template entries (not inclusive)
	 * @param orderByComparator the comparator to order the results by (optionally <code>null</code>)
	 * @return the ordered range of matching layout page template entries
	 */
	public default java.util.List<LayoutPageTemplateEntry> findByG_T_S(
		long groupId, int[] types, int status, int start, int end,
		com.liferay.portal.kernel.util.OrderByComparator
			<LayoutPageTemplateEntry> orderByComparator) {

		return findByG_T_S(
			groupId, types, status, start, end, orderByComparator, true);
	}

	/**
	 * Returns all the layout page template entries that the user has permission to view where groupId = &#63; and type = &#63; and status = &#63;.
	 *
	 * @param groupId the group ID
	 * @param type the type
	 * @param status the status
	 * @return the matching layout page template entries that the user has permission to view
	 */
	public default java.util.List<LayoutPageTemplateEntry> filterFindByG_T_S(
		long groupId, int type, int status) {

		return filterFindByG_T_S(
			groupId, type, status,
			com.liferay.portal.kernel.dao.orm.QueryUtil.ALL_POS,
			com.liferay.portal.kernel.dao.orm.QueryUtil.ALL_POS, null);
	}

	/**
	 * Returns a range of all the layout page template entries that the user has permission to view where groupId = &#63; and type = &#63; and status = &#63;.
	 *
	 * <p>
	 * Useful when paginating results. Returns a maximum of <code>end - start</code> instances. <code>start</code> and <code>end</code> are not primary keys, they are indexes in the result set. Thus, <code>0</code> refers to the first result in the set. Setting both <code>start</code> and <code>end</code> to <code>com.liferay.portal.kernel.dao.orm.QueryUtil#ALL_POS</code> will return the full result set. If <code>orderByComparator</code> is specified, then the query will include the given ORDER BY logic. If <code>orderByComparator</code> is absent, then the query will include the default ORDER BY logic from <code>com.liferay.layout.page.template.model.impl.LayoutPageTemplateEntryModelImpl</code>.
	 * </p>
	 *
	 * @param groupId the group ID
	 * @param type the type
	 * @param status the status
	 * @param start the lower bound of the range of layout page template entries
	 * @param end the upper bound of the range of layout page template entries (not inclusive)
	 * @return the range of matching layout page template entries that the user has permission to view
	 */
	public default java.util.List<LayoutPageTemplateEntry> filterFindByG_T_S(
		long groupId, int type, int status, int start, int end) {

		return filterFindByG_T_S(groupId, type, status, start, end, null);
	}

	/**
	 * Returns all the layout page template entries that the user has permission to view where groupId = &#63; and type = any &#63; and status = &#63;.
	 *
	 * @param groupId the group ID
	 * @param types the types
	 * @param status the status
	 * @return the matching layout page template entries that the user has permission to view
	 */
	public default java.util.List<LayoutPageTemplateEntry> filterFindByG_T_S(
		long groupId, int[] types, int status) {

		return filterFindByG_T_S(
			groupId, types, status,
			com.liferay.portal.kernel.dao.orm.QueryUtil.ALL_POS,
			com.liferay.portal.kernel.dao.orm.QueryUtil.ALL_POS, null);
	}

	/**
	 * Returns a range of all the layout page template entries that the user has permission to view where groupId = &#63; and type = any &#63; and status = &#63;.
	 *
	 * <p>
	 * Useful when paginating results. Returns a maximum of <code>end - start</code> instances. <code>start</code> and <code>end</code> are not primary keys, they are indexes in the result set. Thus, <code>0</code> refers to the first result in the set. Setting both <code>start</code> and <code>end</code> to <code>com.liferay.portal.kernel.dao.orm.QueryUtil#ALL_POS</code> will return the full result set. If <code>orderByComparator</code> is specified, then the query will include the given ORDER BY logic. If <code>orderByComparator</code> is absent, then the query will include the default ORDER BY logic from <code>com.liferay.layout.page.template.model.impl.LayoutPageTemplateEntryModelImpl</code>.
	 * </p>
	 *
	 * @param groupId the group ID
	 * @param types the types
	 * @param status the status
	 * @param start the lower bound of the range of layout page template entries
	 * @param end the upper bound of the range of layout page template entries (not inclusive)
	 * @return the range of matching layout page template entries that the user has permission to view
	 */
	public default java.util.List<LayoutPageTemplateEntry> filterFindByG_T_S(
		long groupId, int[] types, int status, int start, int end) {

		return filterFindByG_T_S(groupId, types, status, start, end, null);
	}

	/**
	 * Returns all the layout page template entries where groupId = &#63; and classNameId = &#63; and classTypeKey = &#63; and type = &#63;.
	 *
	 * @param groupId the group ID
	 * @param classNameId the class name ID
	 * @param classTypeKey the class type key
	 * @param type the type
	 * @return the matching layout page template entries
	 */
	public default java.util.List<LayoutPageTemplateEntry> findByG_C_C_T(
		long groupId, long classNameId, String classTypeKey, int type) {

		return findByG_C_C_T(
			groupId, classNameId, classTypeKey, type,
			com.liferay.portal.kernel.dao.orm.QueryUtil.ALL_POS,
			com.liferay.portal.kernel.dao.orm.QueryUtil.ALL_POS, null, true);
	}

	/**
	 * Returns a range of all the layout page template entries where groupId = &#63; and classNameId = &#63; and classTypeKey = &#63; and type = &#63;.
	 *
	 * <p>
	 * Useful when paginating results. Returns a maximum of <code>end - start</code> instances. <code>start</code> and <code>end</code> are not primary keys, they are indexes in the result set. Thus, <code>0</code> refers to the first result in the set. Setting both <code>start</code> and <code>end</code> to <code>com.liferay.portal.kernel.dao.orm.QueryUtil#ALL_POS</code> will return the full result set. If <code>orderByComparator</code> is specified, then the query will include the given ORDER BY logic. If <code>orderByComparator</code> is absent, then the query will include the default ORDER BY logic from <code>com.liferay.layout.page.template.model.impl.LayoutPageTemplateEntryModelImpl</code>.
	 * </p>
	 *
	 * @param groupId the group ID
	 * @param classNameId the class name ID
	 * @param classTypeKey the class type key
	 * @param type the type
	 * @param start the lower bound of the range of layout page template entries
	 * @param end the upper bound of the range of layout page template entries (not inclusive)
	 * @return the range of matching layout page template entries
	 */
	public default java.util.List<LayoutPageTemplateEntry> findByG_C_C_T(
		long groupId, long classNameId, String classTypeKey, int type,
		int start, int end) {

		return findByG_C_C_T(
			groupId, classNameId, classTypeKey, type, start, end, null, true);
	}

	/**
	 * Returns an ordered range of all the layout page template entries where groupId = &#63; and classNameId = &#63; and classTypeKey = &#63; and type = &#63;.
	 *
	 * <p>
	 * Useful when paginating results. Returns a maximum of <code>end - start</code> instances. <code>start</code> and <code>end</code> are not primary keys, they are indexes in the result set. Thus, <code>0</code> refers to the first result in the set. Setting both <code>start</code> and <code>end</code> to <code>com.liferay.portal.kernel.dao.orm.QueryUtil#ALL_POS</code> will return the full result set. If <code>orderByComparator</code> is specified, then the query will include the given ORDER BY logic. If <code>orderByComparator</code> is absent, then the query will include the default ORDER BY logic from <code>com.liferay.layout.page.template.model.impl.LayoutPageTemplateEntryModelImpl</code>.
	 * </p>
	 *
	 * @param groupId the group ID
	 * @param classNameId the class name ID
	 * @param classTypeKey the class type key
	 * @param type the type
	 * @param start the lower bound of the range of layout page template entries
	 * @param end the upper bound of the range of layout page template entries (not inclusive)
	 * @param orderByComparator the comparator to order the results by (optionally <code>null</code>)
	 * @return the ordered range of matching layout page template entries
	 */
	public default java.util.List<LayoutPageTemplateEntry> findByG_C_C_T(
		long groupId, long classNameId, String classTypeKey, int type,
		int start, int end,
		com.liferay.portal.kernel.util.OrderByComparator
			<LayoutPageTemplateEntry> orderByComparator) {

		return findByG_C_C_T(
			groupId, classNameId, classTypeKey, type, start, end,
			orderByComparator, true);
	}

	/**
	 * Returns all the layout page template entries where groupId = any &#63; and classNameId = &#63; and classTypeKey = &#63; and type = &#63;.
	 *
	 * @param groupIds the group IDs
	 * @param classNameId the class name ID
	 * @param classTypeKey the class type key
	 * @param type the type
	 * @return the matching layout page template entries
	 */
	public default java.util.List<LayoutPageTemplateEntry> findByG_C_C_T(
		long[] groupIds, long classNameId, String classTypeKey, int type) {

		return findByG_C_C_T(
			groupIds, classNameId, classTypeKey, type,
			com.liferay.portal.kernel.dao.orm.QueryUtil.ALL_POS,
			com.liferay.portal.kernel.dao.orm.QueryUtil.ALL_POS, null, true);
	}

	/**
	 * Returns a range of all the layout page template entries where groupId = any &#63; and classNameId = &#63; and classTypeKey = &#63; and type = &#63;.
	 *
	 * <p>
	 * Useful when paginating results. Returns a maximum of <code>end - start</code> instances. <code>start</code> and <code>end</code> are not primary keys, they are indexes in the result set. Thus, <code>0</code> refers to the first result in the set. Setting both <code>start</code> and <code>end</code> to <code>com.liferay.portal.kernel.dao.orm.QueryUtil#ALL_POS</code> will return the full result set. If <code>orderByComparator</code> is specified, then the query will include the given ORDER BY logic. If <code>orderByComparator</code> is absent, then the query will include the default ORDER BY logic from <code>com.liferay.layout.page.template.model.impl.LayoutPageTemplateEntryModelImpl</code>.
	 * </p>
	 *
	 * @param groupIds the group IDs
	 * @param classNameId the class name ID
	 * @param classTypeKey the class type key
	 * @param type the type
	 * @param start the lower bound of the range of layout page template entries
	 * @param end the upper bound of the range of layout page template entries (not inclusive)
	 * @return the range of matching layout page template entries
	 */
	public default java.util.List<LayoutPageTemplateEntry> findByG_C_C_T(
		long[] groupIds, long classNameId, String classTypeKey, int type,
		int start, int end) {

		return findByG_C_C_T(
			groupIds, classNameId, classTypeKey, type, start, end, null, true);
	}

	/**
	 * Returns an ordered range of all the layout page template entries where groupId = any &#63; and classNameId = &#63; and classTypeKey = &#63; and type = &#63;.
	 *
	 * <p>
	 * Useful when paginating results. Returns a maximum of <code>end - start</code> instances. <code>start</code> and <code>end</code> are not primary keys, they are indexes in the result set. Thus, <code>0</code> refers to the first result in the set. Setting both <code>start</code> and <code>end</code> to <code>com.liferay.portal.kernel.dao.orm.QueryUtil#ALL_POS</code> will return the full result set. If <code>orderByComparator</code> is specified, then the query will include the given ORDER BY logic. If <code>orderByComparator</code> is absent, then the query will include the default ORDER BY logic from <code>com.liferay.layout.page.template.model.impl.LayoutPageTemplateEntryModelImpl</code>.
	 * </p>
	 *
	 * @param groupIds the group IDs
	 * @param classNameId the class name ID
	 * @param classTypeKey the class type key
	 * @param type the type
	 * @param start the lower bound of the range of layout page template entries
	 * @param end the upper bound of the range of layout page template entries (not inclusive)
	 * @param orderByComparator the comparator to order the results by (optionally <code>null</code>)
	 * @return the ordered range of matching layout page template entries
	 */
	public default java.util.List<LayoutPageTemplateEntry> findByG_C_C_T(
		long[] groupIds, long classNameId, String classTypeKey, int type,
		int start, int end,
		com.liferay.portal.kernel.util.OrderByComparator
			<LayoutPageTemplateEntry> orderByComparator) {

		return findByG_C_C_T(
			groupIds, classNameId, classTypeKey, type, start, end,
			orderByComparator, true);
	}

	/**
	 * Returns all the layout page template entries that the user has permission to view where groupId = &#63; and classNameId = &#63; and classTypeKey = &#63; and type = &#63;.
	 *
	 * @param groupId the group ID
	 * @param classNameId the class name ID
	 * @param classTypeKey the class type key
	 * @param type the type
	 * @return the matching layout page template entries that the user has permission to view
	 */
	public default java.util.List<LayoutPageTemplateEntry> filterFindByG_C_C_T(
		long groupId, long classNameId, String classTypeKey, int type) {

		return filterFindByG_C_C_T(
			groupId, classNameId, classTypeKey, type,
			com.liferay.portal.kernel.dao.orm.QueryUtil.ALL_POS,
			com.liferay.portal.kernel.dao.orm.QueryUtil.ALL_POS, null);
	}

	/**
	 * Returns a range of all the layout page template entries that the user has permission to view where groupId = &#63; and classNameId = &#63; and classTypeKey = &#63; and type = &#63;.
	 *
	 * <p>
	 * Useful when paginating results. Returns a maximum of <code>end - start</code> instances. <code>start</code> and <code>end</code> are not primary keys, they are indexes in the result set. Thus, <code>0</code> refers to the first result in the set. Setting both <code>start</code> and <code>end</code> to <code>com.liferay.portal.kernel.dao.orm.QueryUtil#ALL_POS</code> will return the full result set. If <code>orderByComparator</code> is specified, then the query will include the given ORDER BY logic. If <code>orderByComparator</code> is absent, then the query will include the default ORDER BY logic from <code>com.liferay.layout.page.template.model.impl.LayoutPageTemplateEntryModelImpl</code>.
	 * </p>
	 *
	 * @param groupId the group ID
	 * @param classNameId the class name ID
	 * @param classTypeKey the class type key
	 * @param type the type
	 * @param start the lower bound of the range of layout page template entries
	 * @param end the upper bound of the range of layout page template entries (not inclusive)
	 * @return the range of matching layout page template entries that the user has permission to view
	 */
	public default java.util.List<LayoutPageTemplateEntry> filterFindByG_C_C_T(
		long groupId, long classNameId, String classTypeKey, int type,
		int start, int end) {

		return filterFindByG_C_C_T(
			groupId, classNameId, classTypeKey, type, start, end, null);
	}

	/**
	 * Returns all the layout page template entries that the user has permission to view where groupId = any &#63; and classNameId = &#63; and classTypeKey = &#63; and type = &#63;.
	 *
	 * @param groupIds the group IDs
	 * @param classNameId the class name ID
	 * @param classTypeKey the class type key
	 * @param type the type
	 * @return the matching layout page template entries that the user has permission to view
	 */
	public default java.util.List<LayoutPageTemplateEntry> filterFindByG_C_C_T(
		long[] groupIds, long classNameId, String classTypeKey, int type) {

		return filterFindByG_C_C_T(
			groupIds, classNameId, classTypeKey, type,
			com.liferay.portal.kernel.dao.orm.QueryUtil.ALL_POS,
			com.liferay.portal.kernel.dao.orm.QueryUtil.ALL_POS, null);
	}

	/**
	 * Returns a range of all the layout page template entries that the user has permission to view where groupId = any &#63; and classNameId = &#63; and classTypeKey = &#63; and type = &#63;.
	 *
	 * <p>
	 * Useful when paginating results. Returns a maximum of <code>end - start</code> instances. <code>start</code> and <code>end</code> are not primary keys, they are indexes in the result set. Thus, <code>0</code> refers to the first result in the set. Setting both <code>start</code> and <code>end</code> to <code>com.liferay.portal.kernel.dao.orm.QueryUtil#ALL_POS</code> will return the full result set. If <code>orderByComparator</code> is specified, then the query will include the given ORDER BY logic. If <code>orderByComparator</code> is absent, then the query will include the default ORDER BY logic from <code>com.liferay.layout.page.template.model.impl.LayoutPageTemplateEntryModelImpl</code>.
	 * </p>
	 *
	 * @param groupIds the group IDs
	 * @param classNameId the class name ID
	 * @param classTypeKey the class type key
	 * @param type the type
	 * @param start the lower bound of the range of layout page template entries
	 * @param end the upper bound of the range of layout page template entries (not inclusive)
	 * @return the range of matching layout page template entries that the user has permission to view
	 */
	public default java.util.List<LayoutPageTemplateEntry> filterFindByG_C_C_T(
		long[] groupIds, long classNameId, String classTypeKey, int type,
		int start, int end) {

		return filterFindByG_C_C_T(
			groupIds, classNameId, classTypeKey, type, start, end, null);
	}

	/**
	 * Returns all the layout page template entries where groupId = &#63; and classNameId = &#63; and classTypeKey = &#63; and defaultTemplate = &#63;.
	 *
	 * @param groupId the group ID
	 * @param classNameId the class name ID
	 * @param classTypeKey the class type key
	 * @param defaultTemplate the default template
	 * @return the matching layout page template entries
	 */
	public default java.util.List<LayoutPageTemplateEntry> findByG_C_C_D(
		long groupId, long classNameId, String classTypeKey,
		boolean defaultTemplate) {

		return findByG_C_C_D(
			groupId, classNameId, classTypeKey, defaultTemplate,
			com.liferay.portal.kernel.dao.orm.QueryUtil.ALL_POS,
			com.liferay.portal.kernel.dao.orm.QueryUtil.ALL_POS, null, true);
	}

	/**
	 * Returns a range of all the layout page template entries where groupId = &#63; and classNameId = &#63; and classTypeKey = &#63; and defaultTemplate = &#63;.
	 *
	 * <p>
	 * Useful when paginating results. Returns a maximum of <code>end - start</code> instances. <code>start</code> and <code>end</code> are not primary keys, they are indexes in the result set. Thus, <code>0</code> refers to the first result in the set. Setting both <code>start</code> and <code>end</code> to <code>com.liferay.portal.kernel.dao.orm.QueryUtil#ALL_POS</code> will return the full result set. If <code>orderByComparator</code> is specified, then the query will include the given ORDER BY logic. If <code>orderByComparator</code> is absent, then the query will include the default ORDER BY logic from <code>com.liferay.layout.page.template.model.impl.LayoutPageTemplateEntryModelImpl</code>.
	 * </p>
	 *
	 * @param groupId the group ID
	 * @param classNameId the class name ID
	 * @param classTypeKey the class type key
	 * @param defaultTemplate the default template
	 * @param start the lower bound of the range of layout page template entries
	 * @param end the upper bound of the range of layout page template entries (not inclusive)
	 * @return the range of matching layout page template entries
	 */
	public default java.util.List<LayoutPageTemplateEntry> findByG_C_C_D(
		long groupId, long classNameId, String classTypeKey,
		boolean defaultTemplate, int start, int end) {

		return findByG_C_C_D(
			groupId, classNameId, classTypeKey, defaultTemplate, start, end,
			null, true);
	}

	/**
	 * Returns an ordered range of all the layout page template entries where groupId = &#63; and classNameId = &#63; and classTypeKey = &#63; and defaultTemplate = &#63;.
	 *
	 * <p>
	 * Useful when paginating results. Returns a maximum of <code>end - start</code> instances. <code>start</code> and <code>end</code> are not primary keys, they are indexes in the result set. Thus, <code>0</code> refers to the first result in the set. Setting both <code>start</code> and <code>end</code> to <code>com.liferay.portal.kernel.dao.orm.QueryUtil#ALL_POS</code> will return the full result set. If <code>orderByComparator</code> is specified, then the query will include the given ORDER BY logic. If <code>orderByComparator</code> is absent, then the query will include the default ORDER BY logic from <code>com.liferay.layout.page.template.model.impl.LayoutPageTemplateEntryModelImpl</code>.
	 * </p>
	 *
	 * @param groupId the group ID
	 * @param classNameId the class name ID
	 * @param classTypeKey the class type key
	 * @param defaultTemplate the default template
	 * @param start the lower bound of the range of layout page template entries
	 * @param end the upper bound of the range of layout page template entries (not inclusive)
	 * @param orderByComparator the comparator to order the results by (optionally <code>null</code>)
	 * @return the ordered range of matching layout page template entries
	 */
	public default java.util.List<LayoutPageTemplateEntry> findByG_C_C_D(
		long groupId, long classNameId, String classTypeKey,
		boolean defaultTemplate, int start, int end,
		com.liferay.portal.kernel.util.OrderByComparator
			<LayoutPageTemplateEntry> orderByComparator) {

		return findByG_C_C_D(
			groupId, classNameId, classTypeKey, defaultTemplate, start, end,
			orderByComparator, true);
	}

	/**
	 * Returns all the layout page template entries that the user has permission to view where groupId = &#63; and classNameId = &#63; and classTypeKey = &#63; and defaultTemplate = &#63;.
	 *
	 * @param groupId the group ID
	 * @param classNameId the class name ID
	 * @param classTypeKey the class type key
	 * @param defaultTemplate the default template
	 * @return the matching layout page template entries that the user has permission to view
	 */
	public default java.util.List<LayoutPageTemplateEntry> filterFindByG_C_C_D(
		long groupId, long classNameId, String classTypeKey,
		boolean defaultTemplate) {

		return filterFindByG_C_C_D(
			groupId, classNameId, classTypeKey, defaultTemplate,
			com.liferay.portal.kernel.dao.orm.QueryUtil.ALL_POS,
			com.liferay.portal.kernel.dao.orm.QueryUtil.ALL_POS, null);
	}

	/**
	 * Returns a range of all the layout page template entries that the user has permission to view where groupId = &#63; and classNameId = &#63; and classTypeKey = &#63; and defaultTemplate = &#63;.
	 *
	 * <p>
	 * Useful when paginating results. Returns a maximum of <code>end - start</code> instances. <code>start</code> and <code>end</code> are not primary keys, they are indexes in the result set. Thus, <code>0</code> refers to the first result in the set. Setting both <code>start</code> and <code>end</code> to <code>com.liferay.portal.kernel.dao.orm.QueryUtil#ALL_POS</code> will return the full result set. If <code>orderByComparator</code> is specified, then the query will include the given ORDER BY logic. If <code>orderByComparator</code> is absent, then the query will include the default ORDER BY logic from <code>com.liferay.layout.page.template.model.impl.LayoutPageTemplateEntryModelImpl</code>.
	 * </p>
	 *
	 * @param groupId the group ID
	 * @param classNameId the class name ID
	 * @param classTypeKey the class type key
	 * @param defaultTemplate the default template
	 * @param start the lower bound of the range of layout page template entries
	 * @param end the upper bound of the range of layout page template entries (not inclusive)
	 * @return the range of matching layout page template entries that the user has permission to view
	 */
	public default java.util.List<LayoutPageTemplateEntry> filterFindByG_C_C_D(
		long groupId, long classNameId, String classTypeKey,
		boolean defaultTemplate, int start, int end) {

		return filterFindByG_C_C_D(
			groupId, classNameId, classTypeKey, defaultTemplate, start, end,
			null);
	}

	/**
	 * Returns all the layout page template entries where groupId = &#63; and classNameId = &#63; and type = &#63; and defaultTemplate = &#63;.
	 *
	 * @param groupId the group ID
	 * @param classNameId the class name ID
	 * @param type the type
	 * @param defaultTemplate the default template
	 * @return the matching layout page template entries
	 */
	public default java.util.List<LayoutPageTemplateEntry> findByG_C_T_D(
		long groupId, long classNameId, int type, boolean defaultTemplate) {

		return findByG_C_T_D(
			groupId, classNameId, type, defaultTemplate,
			com.liferay.portal.kernel.dao.orm.QueryUtil.ALL_POS,
			com.liferay.portal.kernel.dao.orm.QueryUtil.ALL_POS, null, true);
	}

	/**
	 * Returns a range of all the layout page template entries where groupId = &#63; and classNameId = &#63; and type = &#63; and defaultTemplate = &#63;.
	 *
	 * <p>
	 * Useful when paginating results. Returns a maximum of <code>end - start</code> instances. <code>start</code> and <code>end</code> are not primary keys, they are indexes in the result set. Thus, <code>0</code> refers to the first result in the set. Setting both <code>start</code> and <code>end</code> to <code>com.liferay.portal.kernel.dao.orm.QueryUtil#ALL_POS</code> will return the full result set. If <code>orderByComparator</code> is specified, then the query will include the given ORDER BY logic. If <code>orderByComparator</code> is absent, then the query will include the default ORDER BY logic from <code>com.liferay.layout.page.template.model.impl.LayoutPageTemplateEntryModelImpl</code>.
	 * </p>
	 *
	 * @param groupId the group ID
	 * @param classNameId the class name ID
	 * @param type the type
	 * @param defaultTemplate the default template
	 * @param start the lower bound of the range of layout page template entries
	 * @param end the upper bound of the range of layout page template entries (not inclusive)
	 * @return the range of matching layout page template entries
	 */
	public default java.util.List<LayoutPageTemplateEntry> findByG_C_T_D(
		long groupId, long classNameId, int type, boolean defaultTemplate,
		int start, int end) {

		return findByG_C_T_D(
			groupId, classNameId, type, defaultTemplate, start, end, null,
			true);
	}

	/**
	 * Returns an ordered range of all the layout page template entries where groupId = &#63; and classNameId = &#63; and type = &#63; and defaultTemplate = &#63;.
	 *
	 * <p>
	 * Useful when paginating results. Returns a maximum of <code>end - start</code> instances. <code>start</code> and <code>end</code> are not primary keys, they are indexes in the result set. Thus, <code>0</code> refers to the first result in the set. Setting both <code>start</code> and <code>end</code> to <code>com.liferay.portal.kernel.dao.orm.QueryUtil#ALL_POS</code> will return the full result set. If <code>orderByComparator</code> is specified, then the query will include the given ORDER BY logic. If <code>orderByComparator</code> is absent, then the query will include the default ORDER BY logic from <code>com.liferay.layout.page.template.model.impl.LayoutPageTemplateEntryModelImpl</code>.
	 * </p>
	 *
	 * @param groupId the group ID
	 * @param classNameId the class name ID
	 * @param type the type
	 * @param defaultTemplate the default template
	 * @param start the lower bound of the range of layout page template entries
	 * @param end the upper bound of the range of layout page template entries (not inclusive)
	 * @param orderByComparator the comparator to order the results by (optionally <code>null</code>)
	 * @return the ordered range of matching layout page template entries
	 */
	public default java.util.List<LayoutPageTemplateEntry> findByG_C_T_D(
		long groupId, long classNameId, int type, boolean defaultTemplate,
		int start, int end,
		com.liferay.portal.kernel.util.OrderByComparator
			<LayoutPageTemplateEntry> orderByComparator) {

		return findByG_C_T_D(
			groupId, classNameId, type, defaultTemplate, start, end,
			orderByComparator, true);
	}

	/**
	 * Returns all the layout page template entries that the user has permission to view where groupId = &#63; and classNameId = &#63; and type = &#63; and defaultTemplate = &#63;.
	 *
	 * @param groupId the group ID
	 * @param classNameId the class name ID
	 * @param type the type
	 * @param defaultTemplate the default template
	 * @return the matching layout page template entries that the user has permission to view
	 */
	public default java.util.List<LayoutPageTemplateEntry> filterFindByG_C_T_D(
		long groupId, long classNameId, int type, boolean defaultTemplate) {

		return filterFindByG_C_T_D(
			groupId, classNameId, type, defaultTemplate,
			com.liferay.portal.kernel.dao.orm.QueryUtil.ALL_POS,
			com.liferay.portal.kernel.dao.orm.QueryUtil.ALL_POS, null);
	}

	/**
	 * Returns a range of all the layout page template entries that the user has permission to view where groupId = &#63; and classNameId = &#63; and type = &#63; and defaultTemplate = &#63;.
	 *
	 * <p>
	 * Useful when paginating results. Returns a maximum of <code>end - start</code> instances. <code>start</code> and <code>end</code> are not primary keys, they are indexes in the result set. Thus, <code>0</code> refers to the first result in the set. Setting both <code>start</code> and <code>end</code> to <code>com.liferay.portal.kernel.dao.orm.QueryUtil#ALL_POS</code> will return the full result set. If <code>orderByComparator</code> is specified, then the query will include the given ORDER BY logic. If <code>orderByComparator</code> is absent, then the query will include the default ORDER BY logic from <code>com.liferay.layout.page.template.model.impl.LayoutPageTemplateEntryModelImpl</code>.
	 * </p>
	 *
	 * @param groupId the group ID
	 * @param classNameId the class name ID
	 * @param type the type
	 * @param defaultTemplate the default template
	 * @param start the lower bound of the range of layout page template entries
	 * @param end the upper bound of the range of layout page template entries (not inclusive)
	 * @return the range of matching layout page template entries that the user has permission to view
	 */
	public default java.util.List<LayoutPageTemplateEntry> filterFindByG_C_T_D(
		long groupId, long classNameId, int type, boolean defaultTemplate,
		int start, int end) {

		return filterFindByG_C_T_D(
			groupId, classNameId, type, defaultTemplate, start, end, null);
	}

	/**
	 * Returns all the layout page template entries where groupId = &#63; and type = &#63; and defaultTemplate = &#63; and status = &#63;.
	 *
	 * @param groupId the group ID
	 * @param type the type
	 * @param defaultTemplate the default template
	 * @param status the status
	 * @return the matching layout page template entries
	 */
	public default java.util.List<LayoutPageTemplateEntry> findByG_T_D_S(
		long groupId, int type, boolean defaultTemplate, int status) {

		return findByG_T_D_S(
			groupId, type, defaultTemplate, status,
			com.liferay.portal.kernel.dao.orm.QueryUtil.ALL_POS,
			com.liferay.portal.kernel.dao.orm.QueryUtil.ALL_POS, null, true);
	}

	/**
	 * Returns a range of all the layout page template entries where groupId = &#63; and type = &#63; and defaultTemplate = &#63; and status = &#63;.
	 *
	 * <p>
	 * Useful when paginating results. Returns a maximum of <code>end - start</code> instances. <code>start</code> and <code>end</code> are not primary keys, they are indexes in the result set. Thus, <code>0</code> refers to the first result in the set. Setting both <code>start</code> and <code>end</code> to <code>com.liferay.portal.kernel.dao.orm.QueryUtil#ALL_POS</code> will return the full result set. If <code>orderByComparator</code> is specified, then the query will include the given ORDER BY logic. If <code>orderByComparator</code> is absent, then the query will include the default ORDER BY logic from <code>com.liferay.layout.page.template.model.impl.LayoutPageTemplateEntryModelImpl</code>.
	 * </p>
	 *
	 * @param groupId the group ID
	 * @param type the type
	 * @param defaultTemplate the default template
	 * @param status the status
	 * @param start the lower bound of the range of layout page template entries
	 * @param end the upper bound of the range of layout page template entries (not inclusive)
	 * @return the range of matching layout page template entries
	 */
	public default java.util.List<LayoutPageTemplateEntry> findByG_T_D_S(
		long groupId, int type, boolean defaultTemplate, int status, int start,
		int end) {

		return findByG_T_D_S(
			groupId, type, defaultTemplate, status, start, end, null, true);
	}

	/**
	 * Returns an ordered range of all the layout page template entries where groupId = &#63; and type = &#63; and defaultTemplate = &#63; and status = &#63;.
	 *
	 * <p>
	 * Useful when paginating results. Returns a maximum of <code>end - start</code> instances. <code>start</code> and <code>end</code> are not primary keys, they are indexes in the result set. Thus, <code>0</code> refers to the first result in the set. Setting both <code>start</code> and <code>end</code> to <code>com.liferay.portal.kernel.dao.orm.QueryUtil#ALL_POS</code> will return the full result set. If <code>orderByComparator</code> is specified, then the query will include the given ORDER BY logic. If <code>orderByComparator</code> is absent, then the query will include the default ORDER BY logic from <code>com.liferay.layout.page.template.model.impl.LayoutPageTemplateEntryModelImpl</code>.
	 * </p>
	 *
	 * @param groupId the group ID
	 * @param type the type
	 * @param defaultTemplate the default template
	 * @param status the status
	 * @param start the lower bound of the range of layout page template entries
	 * @param end the upper bound of the range of layout page template entries (not inclusive)
	 * @param orderByComparator the comparator to order the results by (optionally <code>null</code>)
	 * @return the ordered range of matching layout page template entries
	 */
	public default java.util.List<LayoutPageTemplateEntry> findByG_T_D_S(
		long groupId, int type, boolean defaultTemplate, int status, int start,
		int end,
		com.liferay.portal.kernel.util.OrderByComparator
			<LayoutPageTemplateEntry> orderByComparator) {

		return findByG_T_D_S(
			groupId, type, defaultTemplate, status, start, end,
			orderByComparator, true);
	}

	/**
	 * Returns all the layout page template entries that the user has permission to view where groupId = &#63; and type = &#63; and defaultTemplate = &#63; and status = &#63;.
	 *
	 * @param groupId the group ID
	 * @param type the type
	 * @param defaultTemplate the default template
	 * @param status the status
	 * @return the matching layout page template entries that the user has permission to view
	 */
	public default java.util.List<LayoutPageTemplateEntry> filterFindByG_T_D_S(
		long groupId, int type, boolean defaultTemplate, int status) {

		return filterFindByG_T_D_S(
			groupId, type, defaultTemplate, status,
			com.liferay.portal.kernel.dao.orm.QueryUtil.ALL_POS,
			com.liferay.portal.kernel.dao.orm.QueryUtil.ALL_POS, null);
	}

	/**
	 * Returns a range of all the layout page template entries that the user has permission to view where groupId = &#63; and type = &#63; and defaultTemplate = &#63; and status = &#63;.
	 *
	 * <p>
	 * Useful when paginating results. Returns a maximum of <code>end - start</code> instances. <code>start</code> and <code>end</code> are not primary keys, they are indexes in the result set. Thus, <code>0</code> refers to the first result in the set. Setting both <code>start</code> and <code>end</code> to <code>com.liferay.portal.kernel.dao.orm.QueryUtil#ALL_POS</code> will return the full result set. If <code>orderByComparator</code> is specified, then the query will include the given ORDER BY logic. If <code>orderByComparator</code> is absent, then the query will include the default ORDER BY logic from <code>com.liferay.layout.page.template.model.impl.LayoutPageTemplateEntryModelImpl</code>.
	 * </p>
	 *
	 * @param groupId the group ID
	 * @param type the type
	 * @param defaultTemplate the default template
	 * @param status the status
	 * @param start the lower bound of the range of layout page template entries
	 * @param end the upper bound of the range of layout page template entries (not inclusive)
	 * @return the range of matching layout page template entries that the user has permission to view
	 */
	public default java.util.List<LayoutPageTemplateEntry> filterFindByG_T_D_S(
		long groupId, int type, boolean defaultTemplate, int status, int start,
		int end) {

		return filterFindByG_T_D_S(
			groupId, type, defaultTemplate, status, start, end, null);
	}

	/**
	 * Returns all the layout page template entries where groupId = &#63; and classNameId = &#63; and classTypeKey = &#63; and type = &#63; and status = &#63;.
	 *
	 * @param groupId the group ID
	 * @param classNameId the class name ID
	 * @param classTypeKey the class type key
	 * @param type the type
	 * @param status the status
	 * @return the matching layout page template entries
	 */
	public default java.util.List<LayoutPageTemplateEntry> findByG_C_C_T_S(
		long groupId, long classNameId, String classTypeKey, int type,
		int status) {

		return findByG_C_C_T_S(
			groupId, classNameId, classTypeKey, type, status,
			com.liferay.portal.kernel.dao.orm.QueryUtil.ALL_POS,
			com.liferay.portal.kernel.dao.orm.QueryUtil.ALL_POS, null, true);
	}

	/**
	 * Returns a range of all the layout page template entries where groupId = &#63; and classNameId = &#63; and classTypeKey = &#63; and type = &#63; and status = &#63;.
	 *
	 * <p>
	 * Useful when paginating results. Returns a maximum of <code>end - start</code> instances. <code>start</code> and <code>end</code> are not primary keys, they are indexes in the result set. Thus, <code>0</code> refers to the first result in the set. Setting both <code>start</code> and <code>end</code> to <code>com.liferay.portal.kernel.dao.orm.QueryUtil#ALL_POS</code> will return the full result set. If <code>orderByComparator</code> is specified, then the query will include the given ORDER BY logic. If <code>orderByComparator</code> is absent, then the query will include the default ORDER BY logic from <code>com.liferay.layout.page.template.model.impl.LayoutPageTemplateEntryModelImpl</code>.
	 * </p>
	 *
	 * @param groupId the group ID
	 * @param classNameId the class name ID
	 * @param classTypeKey the class type key
	 * @param type the type
	 * @param status the status
	 * @param start the lower bound of the range of layout page template entries
	 * @param end the upper bound of the range of layout page template entries (not inclusive)
	 * @return the range of matching layout page template entries
	 */
	public default java.util.List<LayoutPageTemplateEntry> findByG_C_C_T_S(
		long groupId, long classNameId, String classTypeKey, int type,
		int status, int start, int end) {

		return findByG_C_C_T_S(
			groupId, classNameId, classTypeKey, type, status, start, end, null,
			true);
	}

	/**
	 * Returns an ordered range of all the layout page template entries where groupId = &#63; and classNameId = &#63; and classTypeKey = &#63; and type = &#63; and status = &#63;.
	 *
	 * <p>
	 * Useful when paginating results. Returns a maximum of <code>end - start</code> instances. <code>start</code> and <code>end</code> are not primary keys, they are indexes in the result set. Thus, <code>0</code> refers to the first result in the set. Setting both <code>start</code> and <code>end</code> to <code>com.liferay.portal.kernel.dao.orm.QueryUtil#ALL_POS</code> will return the full result set. If <code>orderByComparator</code> is specified, then the query will include the given ORDER BY logic. If <code>orderByComparator</code> is absent, then the query will include the default ORDER BY logic from <code>com.liferay.layout.page.template.model.impl.LayoutPageTemplateEntryModelImpl</code>.
	 * </p>
	 *
	 * @param groupId the group ID
	 * @param classNameId the class name ID
	 * @param classTypeKey the class type key
	 * @param type the type
	 * @param status the status
	 * @param start the lower bound of the range of layout page template entries
	 * @param end the upper bound of the range of layout page template entries (not inclusive)
	 * @param orderByComparator the comparator to order the results by (optionally <code>null</code>)
	 * @return the ordered range of matching layout page template entries
	 */
	public default java.util.List<LayoutPageTemplateEntry> findByG_C_C_T_S(
		long groupId, long classNameId, String classTypeKey, int type,
		int status, int start, int end,
		com.liferay.portal.kernel.util.OrderByComparator
			<LayoutPageTemplateEntry> orderByComparator) {

		return findByG_C_C_T_S(
			groupId, classNameId, classTypeKey, type, status, start, end,
			orderByComparator, true);
	}

	/**
	 * Returns all the layout page template entries where groupId = any &#63; and classNameId = &#63; and classTypeKey = &#63; and type = &#63; and status = &#63;.
	 *
	 * @param groupIds the group IDs
	 * @param classNameId the class name ID
	 * @param classTypeKey the class type key
	 * @param type the type
	 * @param status the status
	 * @return the matching layout page template entries
	 */
	public default java.util.List<LayoutPageTemplateEntry> findByG_C_C_T_S(
		long[] groupIds, long classNameId, String classTypeKey, int type,
		int status) {

		return findByG_C_C_T_S(
			groupIds, classNameId, classTypeKey, type, status,
			com.liferay.portal.kernel.dao.orm.QueryUtil.ALL_POS,
			com.liferay.portal.kernel.dao.orm.QueryUtil.ALL_POS, null, true);
	}

	/**
	 * Returns a range of all the layout page template entries where groupId = any &#63; and classNameId = &#63; and classTypeKey = &#63; and type = &#63; and status = &#63;.
	 *
	 * <p>
	 * Useful when paginating results. Returns a maximum of <code>end - start</code> instances. <code>start</code> and <code>end</code> are not primary keys, they are indexes in the result set. Thus, <code>0</code> refers to the first result in the set. Setting both <code>start</code> and <code>end</code> to <code>com.liferay.portal.kernel.dao.orm.QueryUtil#ALL_POS</code> will return the full result set. If <code>orderByComparator</code> is specified, then the query will include the given ORDER BY logic. If <code>orderByComparator</code> is absent, then the query will include the default ORDER BY logic from <code>com.liferay.layout.page.template.model.impl.LayoutPageTemplateEntryModelImpl</code>.
	 * </p>
	 *
	 * @param groupIds the group IDs
	 * @param classNameId the class name ID
	 * @param classTypeKey the class type key
	 * @param type the type
	 * @param status the status
	 * @param start the lower bound of the range of layout page template entries
	 * @param end the upper bound of the range of layout page template entries (not inclusive)
	 * @return the range of matching layout page template entries
	 */
	public default java.util.List<LayoutPageTemplateEntry> findByG_C_C_T_S(
		long[] groupIds, long classNameId, String classTypeKey, int type,
		int status, int start, int end) {

		return findByG_C_C_T_S(
			groupIds, classNameId, classTypeKey, type, status, start, end, null,
			true);
	}

	/**
	 * Returns an ordered range of all the layout page template entries where groupId = any &#63; and classNameId = &#63; and classTypeKey = &#63; and type = &#63; and status = &#63;.
	 *
	 * <p>
	 * Useful when paginating results. Returns a maximum of <code>end - start</code> instances. <code>start</code> and <code>end</code> are not primary keys, they are indexes in the result set. Thus, <code>0</code> refers to the first result in the set. Setting both <code>start</code> and <code>end</code> to <code>com.liferay.portal.kernel.dao.orm.QueryUtil#ALL_POS</code> will return the full result set. If <code>orderByComparator</code> is specified, then the query will include the given ORDER BY logic. If <code>orderByComparator</code> is absent, then the query will include the default ORDER BY logic from <code>com.liferay.layout.page.template.model.impl.LayoutPageTemplateEntryModelImpl</code>.
	 * </p>
	 *
	 * @param groupIds the group IDs
	 * @param classNameId the class name ID
	 * @param classTypeKey the class type key
	 * @param type the type
	 * @param status the status
	 * @param start the lower bound of the range of layout page template entries
	 * @param end the upper bound of the range of layout page template entries (not inclusive)
	 * @param orderByComparator the comparator to order the results by (optionally <code>null</code>)
	 * @return the ordered range of matching layout page template entries
	 */
	public default java.util.List<LayoutPageTemplateEntry> findByG_C_C_T_S(
		long[] groupIds, long classNameId, String classTypeKey, int type,
		int status, int start, int end,
		com.liferay.portal.kernel.util.OrderByComparator
			<LayoutPageTemplateEntry> orderByComparator) {

		return findByG_C_C_T_S(
			groupIds, classNameId, classTypeKey, type, status, start, end,
			orderByComparator, true);
	}

	/**
	 * Returns all the layout page template entries that the user has permission to view where groupId = &#63; and classNameId = &#63; and classTypeKey = &#63; and type = &#63; and status = &#63;.
	 *
	 * @param groupId the group ID
	 * @param classNameId the class name ID
	 * @param classTypeKey the class type key
	 * @param type the type
	 * @param status the status
	 * @return the matching layout page template entries that the user has permission to view
	 */
	public default java.util.List<LayoutPageTemplateEntry>
		filterFindByG_C_C_T_S(
			long groupId, long classNameId, String classTypeKey, int type,
			int status) {

		return filterFindByG_C_C_T_S(
			groupId, classNameId, classTypeKey, type, status,
			com.liferay.portal.kernel.dao.orm.QueryUtil.ALL_POS,
			com.liferay.portal.kernel.dao.orm.QueryUtil.ALL_POS, null);
	}

	/**
	 * Returns a range of all the layout page template entries that the user has permission to view where groupId = &#63; and classNameId = &#63; and classTypeKey = &#63; and type = &#63; and status = &#63;.
	 *
	 * <p>
	 * Useful when paginating results. Returns a maximum of <code>end - start</code> instances. <code>start</code> and <code>end</code> are not primary keys, they are indexes in the result set. Thus, <code>0</code> refers to the first result in the set. Setting both <code>start</code> and <code>end</code> to <code>com.liferay.portal.kernel.dao.orm.QueryUtil#ALL_POS</code> will return the full result set. If <code>orderByComparator</code> is specified, then the query will include the given ORDER BY logic. If <code>orderByComparator</code> is absent, then the query will include the default ORDER BY logic from <code>com.liferay.layout.page.template.model.impl.LayoutPageTemplateEntryModelImpl</code>.
	 * </p>
	 *
	 * @param groupId the group ID
	 * @param classNameId the class name ID
	 * @param classTypeKey the class type key
	 * @param type the type
	 * @param status the status
	 * @param start the lower bound of the range of layout page template entries
	 * @param end the upper bound of the range of layout page template entries (not inclusive)
	 * @return the range of matching layout page template entries that the user has permission to view
	 */
	public default java.util.List<LayoutPageTemplateEntry>
		filterFindByG_C_C_T_S(
			long groupId, long classNameId, String classTypeKey, int type,
			int status, int start, int end) {

		return filterFindByG_C_C_T_S(
			groupId, classNameId, classTypeKey, type, status, start, end, null);
	}

	/**
	 * Returns all the layout page template entries that the user has permission to view where groupId = any &#63; and classNameId = &#63; and classTypeKey = &#63; and type = &#63; and status = &#63;.
	 *
	 * @param groupIds the group IDs
	 * @param classNameId the class name ID
	 * @param classTypeKey the class type key
	 * @param type the type
	 * @param status the status
	 * @return the matching layout page template entries that the user has permission to view
	 */
	public default java.util.List<LayoutPageTemplateEntry>
		filterFindByG_C_C_T_S(
			long[] groupIds, long classNameId, String classTypeKey, int type,
			int status) {

		return filterFindByG_C_C_T_S(
			groupIds, classNameId, classTypeKey, type, status,
			com.liferay.portal.kernel.dao.orm.QueryUtil.ALL_POS,
			com.liferay.portal.kernel.dao.orm.QueryUtil.ALL_POS, null);
	}

	/**
	 * Returns a range of all the layout page template entries that the user has permission to view where groupId = any &#63; and classNameId = &#63; and classTypeKey = &#63; and type = &#63; and status = &#63;.
	 *
	 * <p>
	 * Useful when paginating results. Returns a maximum of <code>end - start</code> instances. <code>start</code> and <code>end</code> are not primary keys, they are indexes in the result set. Thus, <code>0</code> refers to the first result in the set. Setting both <code>start</code> and <code>end</code> to <code>com.liferay.portal.kernel.dao.orm.QueryUtil#ALL_POS</code> will return the full result set. If <code>orderByComparator</code> is specified, then the query will include the given ORDER BY logic. If <code>orderByComparator</code> is absent, then the query will include the default ORDER BY logic from <code>com.liferay.layout.page.template.model.impl.LayoutPageTemplateEntryModelImpl</code>.
	 * </p>
	 *
	 * @param groupIds the group IDs
	 * @param classNameId the class name ID
	 * @param classTypeKey the class type key
	 * @param type the type
	 * @param status the status
	 * @param start the lower bound of the range of layout page template entries
	 * @param end the upper bound of the range of layout page template entries (not inclusive)
	 * @return the range of matching layout page template entries that the user has permission to view
	 */
	public default java.util.List<LayoutPageTemplateEntry>
		filterFindByG_C_C_T_S(
			long[] groupIds, long classNameId, String classTypeKey, int type,
			int status, int start, int end) {

		return filterFindByG_C_C_T_S(
			groupIds, classNameId, classTypeKey, type, status, start, end,
			null);
	}

	/**
	 * Returns all the layout page template entries where groupId = &#63; and classNameId = &#63; and classTypeKey = &#63; and defaultTemplate = &#63; and status = &#63;.
	 *
	 * @param groupId the group ID
	 * @param classNameId the class name ID
	 * @param classTypeKey the class type key
	 * @param defaultTemplate the default template
	 * @param status the status
	 * @return the matching layout page template entries
	 */
	public default java.util.List<LayoutPageTemplateEntry> findByG_C_C_D_S(
		long groupId, long classNameId, String classTypeKey,
		boolean defaultTemplate, int status) {

		return findByG_C_C_D_S(
			groupId, classNameId, classTypeKey, defaultTemplate, status,
			com.liferay.portal.kernel.dao.orm.QueryUtil.ALL_POS,
			com.liferay.portal.kernel.dao.orm.QueryUtil.ALL_POS, null, true);
	}

	/**
	 * Returns a range of all the layout page template entries where groupId = &#63; and classNameId = &#63; and classTypeKey = &#63; and defaultTemplate = &#63; and status = &#63;.
	 *
	 * <p>
	 * Useful when paginating results. Returns a maximum of <code>end - start</code> instances. <code>start</code> and <code>end</code> are not primary keys, they are indexes in the result set. Thus, <code>0</code> refers to the first result in the set. Setting both <code>start</code> and <code>end</code> to <code>com.liferay.portal.kernel.dao.orm.QueryUtil#ALL_POS</code> will return the full result set. If <code>orderByComparator</code> is specified, then the query will include the given ORDER BY logic. If <code>orderByComparator</code> is absent, then the query will include the default ORDER BY logic from <code>com.liferay.layout.page.template.model.impl.LayoutPageTemplateEntryModelImpl</code>.
	 * </p>
	 *
	 * @param groupId the group ID
	 * @param classNameId the class name ID
	 * @param classTypeKey the class type key
	 * @param defaultTemplate the default template
	 * @param status the status
	 * @param start the lower bound of the range of layout page template entries
	 * @param end the upper bound of the range of layout page template entries (not inclusive)
	 * @return the range of matching layout page template entries
	 */
	public default java.util.List<LayoutPageTemplateEntry> findByG_C_C_D_S(
		long groupId, long classNameId, String classTypeKey,
		boolean defaultTemplate, int status, int start, int end) {

		return findByG_C_C_D_S(
			groupId, classNameId, classTypeKey, defaultTemplate, status, start,
			end, null, true);
	}

	/**
	 * Returns an ordered range of all the layout page template entries where groupId = &#63; and classNameId = &#63; and classTypeKey = &#63; and defaultTemplate = &#63; and status = &#63;.
	 *
	 * <p>
	 * Useful when paginating results. Returns a maximum of <code>end - start</code> instances. <code>start</code> and <code>end</code> are not primary keys, they are indexes in the result set. Thus, <code>0</code> refers to the first result in the set. Setting both <code>start</code> and <code>end</code> to <code>com.liferay.portal.kernel.dao.orm.QueryUtil#ALL_POS</code> will return the full result set. If <code>orderByComparator</code> is specified, then the query will include the given ORDER BY logic. If <code>orderByComparator</code> is absent, then the query will include the default ORDER BY logic from <code>com.liferay.layout.page.template.model.impl.LayoutPageTemplateEntryModelImpl</code>.
	 * </p>
	 *
	 * @param groupId the group ID
	 * @param classNameId the class name ID
	 * @param classTypeKey the class type key
	 * @param defaultTemplate the default template
	 * @param status the status
	 * @param start the lower bound of the range of layout page template entries
	 * @param end the upper bound of the range of layout page template entries (not inclusive)
	 * @param orderByComparator the comparator to order the results by (optionally <code>null</code>)
	 * @return the ordered range of matching layout page template entries
	 */
	public default java.util.List<LayoutPageTemplateEntry> findByG_C_C_D_S(
		long groupId, long classNameId, String classTypeKey,
		boolean defaultTemplate, int status, int start, int end,
		com.liferay.portal.kernel.util.OrderByComparator
			<LayoutPageTemplateEntry> orderByComparator) {

		return findByG_C_C_D_S(
			groupId, classNameId, classTypeKey, defaultTemplate, status, start,
			end, orderByComparator, true);
	}

	/**
	 * Returns all the layout page template entries that the user has permission to view where groupId = &#63; and classNameId = &#63; and classTypeKey = &#63; and defaultTemplate = &#63; and status = &#63;.
	 *
	 * @param groupId the group ID
	 * @param classNameId the class name ID
	 * @param classTypeKey the class type key
	 * @param defaultTemplate the default template
	 * @param status the status
	 * @return the matching layout page template entries that the user has permission to view
	 */
	public default java.util.List<LayoutPageTemplateEntry>
		filterFindByG_C_C_D_S(
			long groupId, long classNameId, String classTypeKey,
			boolean defaultTemplate, int status) {

		return filterFindByG_C_C_D_S(
			groupId, classNameId, classTypeKey, defaultTemplate, status,
			com.liferay.portal.kernel.dao.orm.QueryUtil.ALL_POS,
			com.liferay.portal.kernel.dao.orm.QueryUtil.ALL_POS, null);
	}

	/**
	 * Returns a range of all the layout page template entries that the user has permission to view where groupId = &#63; and classNameId = &#63; and classTypeKey = &#63; and defaultTemplate = &#63; and status = &#63;.
	 *
	 * <p>
	 * Useful when paginating results. Returns a maximum of <code>end - start</code> instances. <code>start</code> and <code>end</code> are not primary keys, they are indexes in the result set. Thus, <code>0</code> refers to the first result in the set. Setting both <code>start</code> and <code>end</code> to <code>com.liferay.portal.kernel.dao.orm.QueryUtil#ALL_POS</code> will return the full result set. If <code>orderByComparator</code> is specified, then the query will include the given ORDER BY logic. If <code>orderByComparator</code> is absent, then the query will include the default ORDER BY logic from <code>com.liferay.layout.page.template.model.impl.LayoutPageTemplateEntryModelImpl</code>.
	 * </p>
	 *
	 * @param groupId the group ID
	 * @param classNameId the class name ID
	 * @param classTypeKey the class type key
	 * @param defaultTemplate the default template
	 * @param status the status
	 * @param start the lower bound of the range of layout page template entries
	 * @param end the upper bound of the range of layout page template entries (not inclusive)
	 * @return the range of matching layout page template entries that the user has permission to view
	 */
	public default java.util.List<LayoutPageTemplateEntry>
		filterFindByG_C_C_D_S(
			long groupId, long classNameId, String classTypeKey,
			boolean defaultTemplate, int status, int start, int end) {

		return filterFindByG_C_C_D_S(
			groupId, classNameId, classTypeKey, defaultTemplate, status, start,
			end, null);
	}

}
// LIFERAY-SERVICE-BUILDER-HASH:1553252830