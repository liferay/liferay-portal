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
	 * Returns all the layout page template entries where uuid = &#63;.
	 *
	 * @param uuid the uuid
	 * @return the matching layout page template entries
	 */
	public java.util.List<LayoutPageTemplateEntry> findByUuid(String uuid);

	/**
	 * Returns a range of all the layout page template entries where uuid = &#63;.
	 *
	 * <p>
	 * Useful when paginating results. Returns a maximum of <code>end - start</code> instances. <code>start</code> and <code>end</code> are not primary keys, they are indexes in the result set. Thus, <code>0</code> refers to the first result in the set. Setting both <code>start</code> and <code>end</code> to <code>QueryUtil#ALL_POS</code> will return the full result set. If <code>orderByComparator</code> is specified, then the query will include the given ORDER BY logic. If <code>orderByComparator</code> is absent, then the query will include the default ORDER BY logic from <code>LayoutPageTemplateEntryModelImpl</code>.
	 * </p>
	 *
	 * @param uuid the uuid
	 * @param start the lower bound of the range of layout page template entries
	 * @param end the upper bound of the range of layout page template entries (not inclusive)
	 * @return the range of matching layout page template entries
	 */
	public java.util.List<LayoutPageTemplateEntry> findByUuid(
		String uuid, int start, int end);

	/**
	 * Returns an ordered range of all the layout page template entries where uuid = &#63;.
	 *
	 * <p>
	 * Useful when paginating results. Returns a maximum of <code>end - start</code> instances. <code>start</code> and <code>end</code> are not primary keys, they are indexes in the result set. Thus, <code>0</code> refers to the first result in the set. Setting both <code>start</code> and <code>end</code> to <code>QueryUtil#ALL_POS</code> will return the full result set. If <code>orderByComparator</code> is specified, then the query will include the given ORDER BY logic. If <code>orderByComparator</code> is absent, then the query will include the default ORDER BY logic from <code>LayoutPageTemplateEntryModelImpl</code>.
	 * </p>
	 *
	 * @param uuid the uuid
	 * @param start the lower bound of the range of layout page template entries
	 * @param end the upper bound of the range of layout page template entries (not inclusive)
	 * @param orderByComparator the comparator to order the results by (optionally <code>null</code>)
	 * @return the ordered range of matching layout page template entries
	 */
	public java.util.List<LayoutPageTemplateEntry> findByUuid(
		String uuid, int start, int end,
		com.liferay.portal.kernel.util.OrderByComparator
			<LayoutPageTemplateEntry> orderByComparator);

	/**
	 * Returns an ordered range of all the layout page template entries where uuid = &#63;.
	 *
	 * <p>
	 * Useful when paginating results. Returns a maximum of <code>end - start</code> instances. <code>start</code> and <code>end</code> are not primary keys, they are indexes in the result set. Thus, <code>0</code> refers to the first result in the set. Setting both <code>start</code> and <code>end</code> to <code>QueryUtil#ALL_POS</code> will return the full result set. If <code>orderByComparator</code> is specified, then the query will include the given ORDER BY logic. If <code>orderByComparator</code> is absent, then the query will include the default ORDER BY logic from <code>LayoutPageTemplateEntryModelImpl</code>.
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
	 * Returns the last layout page template entry in the ordered set where uuid = &#63;.
	 *
	 * @param uuid the uuid
	 * @param orderByComparator the comparator to order the set by (optionally <code>null</code>)
	 * @return the last matching layout page template entry
	 * @throws NoSuchPageTemplateEntryException if a matching layout page template entry could not be found
	 */
	public LayoutPageTemplateEntry findByUuid_Last(
			String uuid,
			com.liferay.portal.kernel.util.OrderByComparator
				<LayoutPageTemplateEntry> orderByComparator)
		throws NoSuchPageTemplateEntryException;

	/**
	 * Returns the last layout page template entry in the ordered set where uuid = &#63;.
	 *
	 * @param uuid the uuid
	 * @param orderByComparator the comparator to order the set by (optionally <code>null</code>)
	 * @return the last matching layout page template entry, or <code>null</code> if a matching layout page template entry could not be found
	 */
	public LayoutPageTemplateEntry fetchByUuid_Last(
		String uuid,
		com.liferay.portal.kernel.util.OrderByComparator
			<LayoutPageTemplateEntry> orderByComparator);

	/**
	 * Returns the layout page template entries before and after the current layout page template entry in the ordered set where uuid = &#63;.
	 *
	 * @param layoutPageTemplateEntryId the primary key of the current layout page template entry
	 * @param uuid the uuid
	 * @param orderByComparator the comparator to order the set by (optionally <code>null</code>)
	 * @return the previous, current, and next layout page template entry
	 * @throws NoSuchPageTemplateEntryException if a layout page template entry with the primary key could not be found
	 */
	public LayoutPageTemplateEntry[] findByUuid_PrevAndNext(
			long layoutPageTemplateEntryId, String uuid,
			com.liferay.portal.kernel.util.OrderByComparator
				<LayoutPageTemplateEntry> orderByComparator)
		throws NoSuchPageTemplateEntryException;

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
	 * Returns the layout page template entry where uuid = &#63; and groupId = &#63; or returns <code>null</code> if it could not be found. Uses the finder cache.
	 *
	 * @param uuid the uuid
	 * @param groupId the group ID
	 * @return the matching layout page template entry, or <code>null</code> if a matching layout page template entry could not be found
	 */
	public LayoutPageTemplateEntry fetchByUUID_G(String uuid, long groupId);

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
	 * Returns all the layout page template entries where uuid = &#63; and companyId = &#63;.
	 *
	 * @param uuid the uuid
	 * @param companyId the company ID
	 * @return the matching layout page template entries
	 */
	public java.util.List<LayoutPageTemplateEntry> findByUuid_C(
		String uuid, long companyId);

	/**
	 * Returns a range of all the layout page template entries where uuid = &#63; and companyId = &#63;.
	 *
	 * <p>
	 * Useful when paginating results. Returns a maximum of <code>end - start</code> instances. <code>start</code> and <code>end</code> are not primary keys, they are indexes in the result set. Thus, <code>0</code> refers to the first result in the set. Setting both <code>start</code> and <code>end</code> to <code>QueryUtil#ALL_POS</code> will return the full result set. If <code>orderByComparator</code> is specified, then the query will include the given ORDER BY logic. If <code>orderByComparator</code> is absent, then the query will include the default ORDER BY logic from <code>LayoutPageTemplateEntryModelImpl</code>.
	 * </p>
	 *
	 * @param uuid the uuid
	 * @param companyId the company ID
	 * @param start the lower bound of the range of layout page template entries
	 * @param end the upper bound of the range of layout page template entries (not inclusive)
	 * @return the range of matching layout page template entries
	 */
	public java.util.List<LayoutPageTemplateEntry> findByUuid_C(
		String uuid, long companyId, int start, int end);

	/**
	 * Returns an ordered range of all the layout page template entries where uuid = &#63; and companyId = &#63;.
	 *
	 * <p>
	 * Useful when paginating results. Returns a maximum of <code>end - start</code> instances. <code>start</code> and <code>end</code> are not primary keys, they are indexes in the result set. Thus, <code>0</code> refers to the first result in the set. Setting both <code>start</code> and <code>end</code> to <code>QueryUtil#ALL_POS</code> will return the full result set. If <code>orderByComparator</code> is specified, then the query will include the given ORDER BY logic. If <code>orderByComparator</code> is absent, then the query will include the default ORDER BY logic from <code>LayoutPageTemplateEntryModelImpl</code>.
	 * </p>
	 *
	 * @param uuid the uuid
	 * @param companyId the company ID
	 * @param start the lower bound of the range of layout page template entries
	 * @param end the upper bound of the range of layout page template entries (not inclusive)
	 * @param orderByComparator the comparator to order the results by (optionally <code>null</code>)
	 * @return the ordered range of matching layout page template entries
	 */
	public java.util.List<LayoutPageTemplateEntry> findByUuid_C(
		String uuid, long companyId, int start, int end,
		com.liferay.portal.kernel.util.OrderByComparator
			<LayoutPageTemplateEntry> orderByComparator);

	/**
	 * Returns an ordered range of all the layout page template entries where uuid = &#63; and companyId = &#63;.
	 *
	 * <p>
	 * Useful when paginating results. Returns a maximum of <code>end - start</code> instances. <code>start</code> and <code>end</code> are not primary keys, they are indexes in the result set. Thus, <code>0</code> refers to the first result in the set. Setting both <code>start</code> and <code>end</code> to <code>QueryUtil#ALL_POS</code> will return the full result set. If <code>orderByComparator</code> is specified, then the query will include the given ORDER BY logic. If <code>orderByComparator</code> is absent, then the query will include the default ORDER BY logic from <code>LayoutPageTemplateEntryModelImpl</code>.
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
	 * Returns the last layout page template entry in the ordered set where uuid = &#63; and companyId = &#63;.
	 *
	 * @param uuid the uuid
	 * @param companyId the company ID
	 * @param orderByComparator the comparator to order the set by (optionally <code>null</code>)
	 * @return the last matching layout page template entry
	 * @throws NoSuchPageTemplateEntryException if a matching layout page template entry could not be found
	 */
	public LayoutPageTemplateEntry findByUuid_C_Last(
			String uuid, long companyId,
			com.liferay.portal.kernel.util.OrderByComparator
				<LayoutPageTemplateEntry> orderByComparator)
		throws NoSuchPageTemplateEntryException;

	/**
	 * Returns the last layout page template entry in the ordered set where uuid = &#63; and companyId = &#63;.
	 *
	 * @param uuid the uuid
	 * @param companyId the company ID
	 * @param orderByComparator the comparator to order the set by (optionally <code>null</code>)
	 * @return the last matching layout page template entry, or <code>null</code> if a matching layout page template entry could not be found
	 */
	public LayoutPageTemplateEntry fetchByUuid_C_Last(
		String uuid, long companyId,
		com.liferay.portal.kernel.util.OrderByComparator
			<LayoutPageTemplateEntry> orderByComparator);

	/**
	 * Returns the layout page template entries before and after the current layout page template entry in the ordered set where uuid = &#63; and companyId = &#63;.
	 *
	 * @param layoutPageTemplateEntryId the primary key of the current layout page template entry
	 * @param uuid the uuid
	 * @param companyId the company ID
	 * @param orderByComparator the comparator to order the set by (optionally <code>null</code>)
	 * @return the previous, current, and next layout page template entry
	 * @throws NoSuchPageTemplateEntryException if a layout page template entry with the primary key could not be found
	 */
	public LayoutPageTemplateEntry[] findByUuid_C_PrevAndNext(
			long layoutPageTemplateEntryId, String uuid, long companyId,
			com.liferay.portal.kernel.util.OrderByComparator
				<LayoutPageTemplateEntry> orderByComparator)
		throws NoSuchPageTemplateEntryException;

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
	 * Returns all the layout page template entries where groupId = &#63;.
	 *
	 * @param groupId the group ID
	 * @return the matching layout page template entries
	 */
	public java.util.List<LayoutPageTemplateEntry> findByGroupId(long groupId);

	/**
	 * Returns a range of all the layout page template entries where groupId = &#63;.
	 *
	 * <p>
	 * Useful when paginating results. Returns a maximum of <code>end - start</code> instances. <code>start</code> and <code>end</code> are not primary keys, they are indexes in the result set. Thus, <code>0</code> refers to the first result in the set. Setting both <code>start</code> and <code>end</code> to <code>QueryUtil#ALL_POS</code> will return the full result set. If <code>orderByComparator</code> is specified, then the query will include the given ORDER BY logic. If <code>orderByComparator</code> is absent, then the query will include the default ORDER BY logic from <code>LayoutPageTemplateEntryModelImpl</code>.
	 * </p>
	 *
	 * @param groupId the group ID
	 * @param start the lower bound of the range of layout page template entries
	 * @param end the upper bound of the range of layout page template entries (not inclusive)
	 * @return the range of matching layout page template entries
	 */
	public java.util.List<LayoutPageTemplateEntry> findByGroupId(
		long groupId, int start, int end);

	/**
	 * Returns an ordered range of all the layout page template entries where groupId = &#63;.
	 *
	 * <p>
	 * Useful when paginating results. Returns a maximum of <code>end - start</code> instances. <code>start</code> and <code>end</code> are not primary keys, they are indexes in the result set. Thus, <code>0</code> refers to the first result in the set. Setting both <code>start</code> and <code>end</code> to <code>QueryUtil#ALL_POS</code> will return the full result set. If <code>orderByComparator</code> is specified, then the query will include the given ORDER BY logic. If <code>orderByComparator</code> is absent, then the query will include the default ORDER BY logic from <code>LayoutPageTemplateEntryModelImpl</code>.
	 * </p>
	 *
	 * @param groupId the group ID
	 * @param start the lower bound of the range of layout page template entries
	 * @param end the upper bound of the range of layout page template entries (not inclusive)
	 * @param orderByComparator the comparator to order the results by (optionally <code>null</code>)
	 * @return the ordered range of matching layout page template entries
	 */
	public java.util.List<LayoutPageTemplateEntry> findByGroupId(
		long groupId, int start, int end,
		com.liferay.portal.kernel.util.OrderByComparator
			<LayoutPageTemplateEntry> orderByComparator);

	/**
	 * Returns an ordered range of all the layout page template entries where groupId = &#63;.
	 *
	 * <p>
	 * Useful when paginating results. Returns a maximum of <code>end - start</code> instances. <code>start</code> and <code>end</code> are not primary keys, they are indexes in the result set. Thus, <code>0</code> refers to the first result in the set. Setting both <code>start</code> and <code>end</code> to <code>QueryUtil#ALL_POS</code> will return the full result set. If <code>orderByComparator</code> is specified, then the query will include the given ORDER BY logic. If <code>orderByComparator</code> is absent, then the query will include the default ORDER BY logic from <code>LayoutPageTemplateEntryModelImpl</code>.
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
	 * Returns the last layout page template entry in the ordered set where groupId = &#63;.
	 *
	 * @param groupId the group ID
	 * @param orderByComparator the comparator to order the set by (optionally <code>null</code>)
	 * @return the last matching layout page template entry
	 * @throws NoSuchPageTemplateEntryException if a matching layout page template entry could not be found
	 */
	public LayoutPageTemplateEntry findByGroupId_Last(
			long groupId,
			com.liferay.portal.kernel.util.OrderByComparator
				<LayoutPageTemplateEntry> orderByComparator)
		throws NoSuchPageTemplateEntryException;

	/**
	 * Returns the last layout page template entry in the ordered set where groupId = &#63;.
	 *
	 * @param groupId the group ID
	 * @param orderByComparator the comparator to order the set by (optionally <code>null</code>)
	 * @return the last matching layout page template entry, or <code>null</code> if a matching layout page template entry could not be found
	 */
	public LayoutPageTemplateEntry fetchByGroupId_Last(
		long groupId,
		com.liferay.portal.kernel.util.OrderByComparator
			<LayoutPageTemplateEntry> orderByComparator);

	/**
	 * Returns the layout page template entries before and after the current layout page template entry in the ordered set where groupId = &#63;.
	 *
	 * @param layoutPageTemplateEntryId the primary key of the current layout page template entry
	 * @param groupId the group ID
	 * @param orderByComparator the comparator to order the set by (optionally <code>null</code>)
	 * @return the previous, current, and next layout page template entry
	 * @throws NoSuchPageTemplateEntryException if a layout page template entry with the primary key could not be found
	 */
	public LayoutPageTemplateEntry[] findByGroupId_PrevAndNext(
			long layoutPageTemplateEntryId, long groupId,
			com.liferay.portal.kernel.util.OrderByComparator
				<LayoutPageTemplateEntry> orderByComparator)
		throws NoSuchPageTemplateEntryException;

	/**
	 * Returns all the layout page template entries that the user has permission to view where groupId = &#63;.
	 *
	 * @param groupId the group ID
	 * @return the matching layout page template entries that the user has permission to view
	 */
	public java.util.List<LayoutPageTemplateEntry> filterFindByGroupId(
		long groupId);

	/**
	 * Returns a range of all the layout page template entries that the user has permission to view where groupId = &#63;.
	 *
	 * <p>
	 * Useful when paginating results. Returns a maximum of <code>end - start</code> instances. <code>start</code> and <code>end</code> are not primary keys, they are indexes in the result set. Thus, <code>0</code> refers to the first result in the set. Setting both <code>start</code> and <code>end</code> to <code>QueryUtil#ALL_POS</code> will return the full result set. If <code>orderByComparator</code> is specified, then the query will include the given ORDER BY logic. If <code>orderByComparator</code> is absent, then the query will include the default ORDER BY logic from <code>LayoutPageTemplateEntryModelImpl</code>.
	 * </p>
	 *
	 * @param groupId the group ID
	 * @param start the lower bound of the range of layout page template entries
	 * @param end the upper bound of the range of layout page template entries (not inclusive)
	 * @return the range of matching layout page template entries that the user has permission to view
	 */
	public java.util.List<LayoutPageTemplateEntry> filterFindByGroupId(
		long groupId, int start, int end);

	/**
	 * Returns an ordered range of all the layout page template entries that the user has permissions to view where groupId = &#63;.
	 *
	 * <p>
	 * Useful when paginating results. Returns a maximum of <code>end - start</code> instances. <code>start</code> and <code>end</code> are not primary keys, they are indexes in the result set. Thus, <code>0</code> refers to the first result in the set. Setting both <code>start</code> and <code>end</code> to <code>QueryUtil#ALL_POS</code> will return the full result set. If <code>orderByComparator</code> is specified, then the query will include the given ORDER BY logic. If <code>orderByComparator</code> is absent, then the query will include the default ORDER BY logic from <code>LayoutPageTemplateEntryModelImpl</code>.
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
	 * Returns the layout page template entries before and after the current layout page template entry in the ordered set of layout page template entries that the user has permission to view where groupId = &#63;.
	 *
	 * @param layoutPageTemplateEntryId the primary key of the current layout page template entry
	 * @param groupId the group ID
	 * @param orderByComparator the comparator to order the set by (optionally <code>null</code>)
	 * @return the previous, current, and next layout page template entry
	 * @throws NoSuchPageTemplateEntryException if a layout page template entry with the primary key could not be found
	 */
	public LayoutPageTemplateEntry[] filterFindByGroupId_PrevAndNext(
			long layoutPageTemplateEntryId, long groupId,
			com.liferay.portal.kernel.util.OrderByComparator
				<LayoutPageTemplateEntry> orderByComparator)
		throws NoSuchPageTemplateEntryException;

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
	 * Returns all the layout page template entries where layoutPrototypeId = &#63;.
	 *
	 * @param layoutPrototypeId the layout prototype ID
	 * @return the matching layout page template entries
	 */
	public java.util.List<LayoutPageTemplateEntry> findByLayoutPrototypeId(
		long layoutPrototypeId);

	/**
	 * Returns a range of all the layout page template entries where layoutPrototypeId = &#63;.
	 *
	 * <p>
	 * Useful when paginating results. Returns a maximum of <code>end - start</code> instances. <code>start</code> and <code>end</code> are not primary keys, they are indexes in the result set. Thus, <code>0</code> refers to the first result in the set. Setting both <code>start</code> and <code>end</code> to <code>QueryUtil#ALL_POS</code> will return the full result set. If <code>orderByComparator</code> is specified, then the query will include the given ORDER BY logic. If <code>orderByComparator</code> is absent, then the query will include the default ORDER BY logic from <code>LayoutPageTemplateEntryModelImpl</code>.
	 * </p>
	 *
	 * @param layoutPrototypeId the layout prototype ID
	 * @param start the lower bound of the range of layout page template entries
	 * @param end the upper bound of the range of layout page template entries (not inclusive)
	 * @return the range of matching layout page template entries
	 */
	public java.util.List<LayoutPageTemplateEntry> findByLayoutPrototypeId(
		long layoutPrototypeId, int start, int end);

	/**
	 * Returns an ordered range of all the layout page template entries where layoutPrototypeId = &#63;.
	 *
	 * <p>
	 * Useful when paginating results. Returns a maximum of <code>end - start</code> instances. <code>start</code> and <code>end</code> are not primary keys, they are indexes in the result set. Thus, <code>0</code> refers to the first result in the set. Setting both <code>start</code> and <code>end</code> to <code>QueryUtil#ALL_POS</code> will return the full result set. If <code>orderByComparator</code> is specified, then the query will include the given ORDER BY logic. If <code>orderByComparator</code> is absent, then the query will include the default ORDER BY logic from <code>LayoutPageTemplateEntryModelImpl</code>.
	 * </p>
	 *
	 * @param layoutPrototypeId the layout prototype ID
	 * @param start the lower bound of the range of layout page template entries
	 * @param end the upper bound of the range of layout page template entries (not inclusive)
	 * @param orderByComparator the comparator to order the results by (optionally <code>null</code>)
	 * @return the ordered range of matching layout page template entries
	 */
	public java.util.List<LayoutPageTemplateEntry> findByLayoutPrototypeId(
		long layoutPrototypeId, int start, int end,
		com.liferay.portal.kernel.util.OrderByComparator
			<LayoutPageTemplateEntry> orderByComparator);

	/**
	 * Returns an ordered range of all the layout page template entries where layoutPrototypeId = &#63;.
	 *
	 * <p>
	 * Useful when paginating results. Returns a maximum of <code>end - start</code> instances. <code>start</code> and <code>end</code> are not primary keys, they are indexes in the result set. Thus, <code>0</code> refers to the first result in the set. Setting both <code>start</code> and <code>end</code> to <code>QueryUtil#ALL_POS</code> will return the full result set. If <code>orderByComparator</code> is specified, then the query will include the given ORDER BY logic. If <code>orderByComparator</code> is absent, then the query will include the default ORDER BY logic from <code>LayoutPageTemplateEntryModelImpl</code>.
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
	 * Returns the last layout page template entry in the ordered set where layoutPrototypeId = &#63;.
	 *
	 * @param layoutPrototypeId the layout prototype ID
	 * @param orderByComparator the comparator to order the set by (optionally <code>null</code>)
	 * @return the last matching layout page template entry
	 * @throws NoSuchPageTemplateEntryException if a matching layout page template entry could not be found
	 */
	public LayoutPageTemplateEntry findByLayoutPrototypeId_Last(
			long layoutPrototypeId,
			com.liferay.portal.kernel.util.OrderByComparator
				<LayoutPageTemplateEntry> orderByComparator)
		throws NoSuchPageTemplateEntryException;

	/**
	 * Returns the last layout page template entry in the ordered set where layoutPrototypeId = &#63;.
	 *
	 * @param layoutPrototypeId the layout prototype ID
	 * @param orderByComparator the comparator to order the set by (optionally <code>null</code>)
	 * @return the last matching layout page template entry, or <code>null</code> if a matching layout page template entry could not be found
	 */
	public LayoutPageTemplateEntry fetchByLayoutPrototypeId_Last(
		long layoutPrototypeId,
		com.liferay.portal.kernel.util.OrderByComparator
			<LayoutPageTemplateEntry> orderByComparator);

	/**
	 * Returns the layout page template entries before and after the current layout page template entry in the ordered set where layoutPrototypeId = &#63;.
	 *
	 * @param layoutPageTemplateEntryId the primary key of the current layout page template entry
	 * @param layoutPrototypeId the layout prototype ID
	 * @param orderByComparator the comparator to order the set by (optionally <code>null</code>)
	 * @return the previous, current, and next layout page template entry
	 * @throws NoSuchPageTemplateEntryException if a layout page template entry with the primary key could not be found
	 */
	public LayoutPageTemplateEntry[] findByLayoutPrototypeId_PrevAndNext(
			long layoutPageTemplateEntryId, long layoutPrototypeId,
			com.liferay.portal.kernel.util.OrderByComparator
				<LayoutPageTemplateEntry> orderByComparator)
		throws NoSuchPageTemplateEntryException;

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
	 * Returns the layout page template entry where plid = &#63; or returns <code>null</code> if it could not be found. Uses the finder cache.
	 *
	 * @param plid the plid
	 * @return the matching layout page template entry, or <code>null</code> if a matching layout page template entry could not be found
	 */
	public LayoutPageTemplateEntry fetchByPlid(long plid);

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
	 * Returns all the layout page template entries where groupId = &#63; and layoutPageTemplateCollectionId = &#63;.
	 *
	 * @param groupId the group ID
	 * @param layoutPageTemplateCollectionId the layout page template collection ID
	 * @return the matching layout page template entries
	 */
	public java.util.List<LayoutPageTemplateEntry> findByG_L(
		long groupId, long layoutPageTemplateCollectionId);

	/**
	 * Returns a range of all the layout page template entries where groupId = &#63; and layoutPageTemplateCollectionId = &#63;.
	 *
	 * <p>
	 * Useful when paginating results. Returns a maximum of <code>end - start</code> instances. <code>start</code> and <code>end</code> are not primary keys, they are indexes in the result set. Thus, <code>0</code> refers to the first result in the set. Setting both <code>start</code> and <code>end</code> to <code>QueryUtil#ALL_POS</code> will return the full result set. If <code>orderByComparator</code> is specified, then the query will include the given ORDER BY logic. If <code>orderByComparator</code> is absent, then the query will include the default ORDER BY logic from <code>LayoutPageTemplateEntryModelImpl</code>.
	 * </p>
	 *
	 * @param groupId the group ID
	 * @param layoutPageTemplateCollectionId the layout page template collection ID
	 * @param start the lower bound of the range of layout page template entries
	 * @param end the upper bound of the range of layout page template entries (not inclusive)
	 * @return the range of matching layout page template entries
	 */
	public java.util.List<LayoutPageTemplateEntry> findByG_L(
		long groupId, long layoutPageTemplateCollectionId, int start, int end);

	/**
	 * Returns an ordered range of all the layout page template entries where groupId = &#63; and layoutPageTemplateCollectionId = &#63;.
	 *
	 * <p>
	 * Useful when paginating results. Returns a maximum of <code>end - start</code> instances. <code>start</code> and <code>end</code> are not primary keys, they are indexes in the result set. Thus, <code>0</code> refers to the first result in the set. Setting both <code>start</code> and <code>end</code> to <code>QueryUtil#ALL_POS</code> will return the full result set. If <code>orderByComparator</code> is specified, then the query will include the given ORDER BY logic. If <code>orderByComparator</code> is absent, then the query will include the default ORDER BY logic from <code>LayoutPageTemplateEntryModelImpl</code>.
	 * </p>
	 *
	 * @param groupId the group ID
	 * @param layoutPageTemplateCollectionId the layout page template collection ID
	 * @param start the lower bound of the range of layout page template entries
	 * @param end the upper bound of the range of layout page template entries (not inclusive)
	 * @param orderByComparator the comparator to order the results by (optionally <code>null</code>)
	 * @return the ordered range of matching layout page template entries
	 */
	public java.util.List<LayoutPageTemplateEntry> findByG_L(
		long groupId, long layoutPageTemplateCollectionId, int start, int end,
		com.liferay.portal.kernel.util.OrderByComparator
			<LayoutPageTemplateEntry> orderByComparator);

	/**
	 * Returns an ordered range of all the layout page template entries where groupId = &#63; and layoutPageTemplateCollectionId = &#63;.
	 *
	 * <p>
	 * Useful when paginating results. Returns a maximum of <code>end - start</code> instances. <code>start</code> and <code>end</code> are not primary keys, they are indexes in the result set. Thus, <code>0</code> refers to the first result in the set. Setting both <code>start</code> and <code>end</code> to <code>QueryUtil#ALL_POS</code> will return the full result set. If <code>orderByComparator</code> is specified, then the query will include the given ORDER BY logic. If <code>orderByComparator</code> is absent, then the query will include the default ORDER BY logic from <code>LayoutPageTemplateEntryModelImpl</code>.
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
	 * Returns the last layout page template entry in the ordered set where groupId = &#63; and layoutPageTemplateCollectionId = &#63;.
	 *
	 * @param groupId the group ID
	 * @param layoutPageTemplateCollectionId the layout page template collection ID
	 * @param orderByComparator the comparator to order the set by (optionally <code>null</code>)
	 * @return the last matching layout page template entry
	 * @throws NoSuchPageTemplateEntryException if a matching layout page template entry could not be found
	 */
	public LayoutPageTemplateEntry findByG_L_Last(
			long groupId, long layoutPageTemplateCollectionId,
			com.liferay.portal.kernel.util.OrderByComparator
				<LayoutPageTemplateEntry> orderByComparator)
		throws NoSuchPageTemplateEntryException;

	/**
	 * Returns the last layout page template entry in the ordered set where groupId = &#63; and layoutPageTemplateCollectionId = &#63;.
	 *
	 * @param groupId the group ID
	 * @param layoutPageTemplateCollectionId the layout page template collection ID
	 * @param orderByComparator the comparator to order the set by (optionally <code>null</code>)
	 * @return the last matching layout page template entry, or <code>null</code> if a matching layout page template entry could not be found
	 */
	public LayoutPageTemplateEntry fetchByG_L_Last(
		long groupId, long layoutPageTemplateCollectionId,
		com.liferay.portal.kernel.util.OrderByComparator
			<LayoutPageTemplateEntry> orderByComparator);

	/**
	 * Returns the layout page template entries before and after the current layout page template entry in the ordered set where groupId = &#63; and layoutPageTemplateCollectionId = &#63;.
	 *
	 * @param layoutPageTemplateEntryId the primary key of the current layout page template entry
	 * @param groupId the group ID
	 * @param layoutPageTemplateCollectionId the layout page template collection ID
	 * @param orderByComparator the comparator to order the set by (optionally <code>null</code>)
	 * @return the previous, current, and next layout page template entry
	 * @throws NoSuchPageTemplateEntryException if a layout page template entry with the primary key could not be found
	 */
	public LayoutPageTemplateEntry[] findByG_L_PrevAndNext(
			long layoutPageTemplateEntryId, long groupId,
			long layoutPageTemplateCollectionId,
			com.liferay.portal.kernel.util.OrderByComparator
				<LayoutPageTemplateEntry> orderByComparator)
		throws NoSuchPageTemplateEntryException;

	/**
	 * Returns all the layout page template entries that the user has permission to view where groupId = &#63; and layoutPageTemplateCollectionId = &#63;.
	 *
	 * @param groupId the group ID
	 * @param layoutPageTemplateCollectionId the layout page template collection ID
	 * @return the matching layout page template entries that the user has permission to view
	 */
	public java.util.List<LayoutPageTemplateEntry> filterFindByG_L(
		long groupId, long layoutPageTemplateCollectionId);

	/**
	 * Returns a range of all the layout page template entries that the user has permission to view where groupId = &#63; and layoutPageTemplateCollectionId = &#63;.
	 *
	 * <p>
	 * Useful when paginating results. Returns a maximum of <code>end - start</code> instances. <code>start</code> and <code>end</code> are not primary keys, they are indexes in the result set. Thus, <code>0</code> refers to the first result in the set. Setting both <code>start</code> and <code>end</code> to <code>QueryUtil#ALL_POS</code> will return the full result set. If <code>orderByComparator</code> is specified, then the query will include the given ORDER BY logic. If <code>orderByComparator</code> is absent, then the query will include the default ORDER BY logic from <code>LayoutPageTemplateEntryModelImpl</code>.
	 * </p>
	 *
	 * @param groupId the group ID
	 * @param layoutPageTemplateCollectionId the layout page template collection ID
	 * @param start the lower bound of the range of layout page template entries
	 * @param end the upper bound of the range of layout page template entries (not inclusive)
	 * @return the range of matching layout page template entries that the user has permission to view
	 */
	public java.util.List<LayoutPageTemplateEntry> filterFindByG_L(
		long groupId, long layoutPageTemplateCollectionId, int start, int end);

	/**
	 * Returns an ordered range of all the layout page template entries that the user has permissions to view where groupId = &#63; and layoutPageTemplateCollectionId = &#63;.
	 *
	 * <p>
	 * Useful when paginating results. Returns a maximum of <code>end - start</code> instances. <code>start</code> and <code>end</code> are not primary keys, they are indexes in the result set. Thus, <code>0</code> refers to the first result in the set. Setting both <code>start</code> and <code>end</code> to <code>QueryUtil#ALL_POS</code> will return the full result set. If <code>orderByComparator</code> is specified, then the query will include the given ORDER BY logic. If <code>orderByComparator</code> is absent, then the query will include the default ORDER BY logic from <code>LayoutPageTemplateEntryModelImpl</code>.
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
	 * Returns the layout page template entries before and after the current layout page template entry in the ordered set of layout page template entries that the user has permission to view where groupId = &#63; and layoutPageTemplateCollectionId = &#63;.
	 *
	 * @param layoutPageTemplateEntryId the primary key of the current layout page template entry
	 * @param groupId the group ID
	 * @param layoutPageTemplateCollectionId the layout page template collection ID
	 * @param orderByComparator the comparator to order the set by (optionally <code>null</code>)
	 * @return the previous, current, and next layout page template entry
	 * @throws NoSuchPageTemplateEntryException if a layout page template entry with the primary key could not be found
	 */
	public LayoutPageTemplateEntry[] filterFindByG_L_PrevAndNext(
			long layoutPageTemplateEntryId, long groupId,
			long layoutPageTemplateCollectionId,
			com.liferay.portal.kernel.util.OrderByComparator
				<LayoutPageTemplateEntry> orderByComparator)
		throws NoSuchPageTemplateEntryException;

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
	 * Returns the layout page template entry where groupId = &#63; and layoutPageTemplateEntryKey = &#63; or returns <code>null</code> if it could not be found. Uses the finder cache.
	 *
	 * @param groupId the group ID
	 * @param layoutPageTemplateEntryKey the layout page template entry key
	 * @return the matching layout page template entry, or <code>null</code> if a matching layout page template entry could not be found
	 */
	public LayoutPageTemplateEntry fetchByG_LPTEK(
		long groupId, String layoutPageTemplateEntryKey);

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
	 * Returns all the layout page template entries where groupId = &#63; and name = &#63;.
	 *
	 * @param groupId the group ID
	 * @param name the name
	 * @return the matching layout page template entries
	 */
	public java.util.List<LayoutPageTemplateEntry> findByG_N(
		long groupId, String name);

	/**
	 * Returns a range of all the layout page template entries where groupId = &#63; and name = &#63;.
	 *
	 * <p>
	 * Useful when paginating results. Returns a maximum of <code>end - start</code> instances. <code>start</code> and <code>end</code> are not primary keys, they are indexes in the result set. Thus, <code>0</code> refers to the first result in the set. Setting both <code>start</code> and <code>end</code> to <code>QueryUtil#ALL_POS</code> will return the full result set. If <code>orderByComparator</code> is specified, then the query will include the given ORDER BY logic. If <code>orderByComparator</code> is absent, then the query will include the default ORDER BY logic from <code>LayoutPageTemplateEntryModelImpl</code>.
	 * </p>
	 *
	 * @param groupId the group ID
	 * @param name the name
	 * @param start the lower bound of the range of layout page template entries
	 * @param end the upper bound of the range of layout page template entries (not inclusive)
	 * @return the range of matching layout page template entries
	 */
	public java.util.List<LayoutPageTemplateEntry> findByG_N(
		long groupId, String name, int start, int end);

	/**
	 * Returns an ordered range of all the layout page template entries where groupId = &#63; and name = &#63;.
	 *
	 * <p>
	 * Useful when paginating results. Returns a maximum of <code>end - start</code> instances. <code>start</code> and <code>end</code> are not primary keys, they are indexes in the result set. Thus, <code>0</code> refers to the first result in the set. Setting both <code>start</code> and <code>end</code> to <code>QueryUtil#ALL_POS</code> will return the full result set. If <code>orderByComparator</code> is specified, then the query will include the given ORDER BY logic. If <code>orderByComparator</code> is absent, then the query will include the default ORDER BY logic from <code>LayoutPageTemplateEntryModelImpl</code>.
	 * </p>
	 *
	 * @param groupId the group ID
	 * @param name the name
	 * @param start the lower bound of the range of layout page template entries
	 * @param end the upper bound of the range of layout page template entries (not inclusive)
	 * @param orderByComparator the comparator to order the results by (optionally <code>null</code>)
	 * @return the ordered range of matching layout page template entries
	 */
	public java.util.List<LayoutPageTemplateEntry> findByG_N(
		long groupId, String name, int start, int end,
		com.liferay.portal.kernel.util.OrderByComparator
			<LayoutPageTemplateEntry> orderByComparator);

	/**
	 * Returns an ordered range of all the layout page template entries where groupId = &#63; and name = &#63;.
	 *
	 * <p>
	 * Useful when paginating results. Returns a maximum of <code>end - start</code> instances. <code>start</code> and <code>end</code> are not primary keys, they are indexes in the result set. Thus, <code>0</code> refers to the first result in the set. Setting both <code>start</code> and <code>end</code> to <code>QueryUtil#ALL_POS</code> will return the full result set. If <code>orderByComparator</code> is specified, then the query will include the given ORDER BY logic. If <code>orderByComparator</code> is absent, then the query will include the default ORDER BY logic from <code>LayoutPageTemplateEntryModelImpl</code>.
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
	 * Returns the last layout page template entry in the ordered set where groupId = &#63; and name = &#63;.
	 *
	 * @param groupId the group ID
	 * @param name the name
	 * @param orderByComparator the comparator to order the set by (optionally <code>null</code>)
	 * @return the last matching layout page template entry
	 * @throws NoSuchPageTemplateEntryException if a matching layout page template entry could not be found
	 */
	public LayoutPageTemplateEntry findByG_N_Last(
			long groupId, String name,
			com.liferay.portal.kernel.util.OrderByComparator
				<LayoutPageTemplateEntry> orderByComparator)
		throws NoSuchPageTemplateEntryException;

	/**
	 * Returns the last layout page template entry in the ordered set where groupId = &#63; and name = &#63;.
	 *
	 * @param groupId the group ID
	 * @param name the name
	 * @param orderByComparator the comparator to order the set by (optionally <code>null</code>)
	 * @return the last matching layout page template entry, or <code>null</code> if a matching layout page template entry could not be found
	 */
	public LayoutPageTemplateEntry fetchByG_N_Last(
		long groupId, String name,
		com.liferay.portal.kernel.util.OrderByComparator
			<LayoutPageTemplateEntry> orderByComparator);

	/**
	 * Returns the layout page template entries before and after the current layout page template entry in the ordered set where groupId = &#63; and name = &#63;.
	 *
	 * @param layoutPageTemplateEntryId the primary key of the current layout page template entry
	 * @param groupId the group ID
	 * @param name the name
	 * @param orderByComparator the comparator to order the set by (optionally <code>null</code>)
	 * @return the previous, current, and next layout page template entry
	 * @throws NoSuchPageTemplateEntryException if a layout page template entry with the primary key could not be found
	 */
	public LayoutPageTemplateEntry[] findByG_N_PrevAndNext(
			long layoutPageTemplateEntryId, long groupId, String name,
			com.liferay.portal.kernel.util.OrderByComparator
				<LayoutPageTemplateEntry> orderByComparator)
		throws NoSuchPageTemplateEntryException;

	/**
	 * Returns all the layout page template entries that the user has permission to view where groupId = &#63; and name = &#63;.
	 *
	 * @param groupId the group ID
	 * @param name the name
	 * @return the matching layout page template entries that the user has permission to view
	 */
	public java.util.List<LayoutPageTemplateEntry> filterFindByG_N(
		long groupId, String name);

	/**
	 * Returns a range of all the layout page template entries that the user has permission to view where groupId = &#63; and name = &#63;.
	 *
	 * <p>
	 * Useful when paginating results. Returns a maximum of <code>end - start</code> instances. <code>start</code> and <code>end</code> are not primary keys, they are indexes in the result set. Thus, <code>0</code> refers to the first result in the set. Setting both <code>start</code> and <code>end</code> to <code>QueryUtil#ALL_POS</code> will return the full result set. If <code>orderByComparator</code> is specified, then the query will include the given ORDER BY logic. If <code>orderByComparator</code> is absent, then the query will include the default ORDER BY logic from <code>LayoutPageTemplateEntryModelImpl</code>.
	 * </p>
	 *
	 * @param groupId the group ID
	 * @param name the name
	 * @param start the lower bound of the range of layout page template entries
	 * @param end the upper bound of the range of layout page template entries (not inclusive)
	 * @return the range of matching layout page template entries that the user has permission to view
	 */
	public java.util.List<LayoutPageTemplateEntry> filterFindByG_N(
		long groupId, String name, int start, int end);

	/**
	 * Returns an ordered range of all the layout page template entries that the user has permissions to view where groupId = &#63; and name = &#63;.
	 *
	 * <p>
	 * Useful when paginating results. Returns a maximum of <code>end - start</code> instances. <code>start</code> and <code>end</code> are not primary keys, they are indexes in the result set. Thus, <code>0</code> refers to the first result in the set. Setting both <code>start</code> and <code>end</code> to <code>QueryUtil#ALL_POS</code> will return the full result set. If <code>orderByComparator</code> is specified, then the query will include the given ORDER BY logic. If <code>orderByComparator</code> is absent, then the query will include the default ORDER BY logic from <code>LayoutPageTemplateEntryModelImpl</code>.
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
	 * Returns the layout page template entries before and after the current layout page template entry in the ordered set of layout page template entries that the user has permission to view where groupId = &#63; and name = &#63;.
	 *
	 * @param layoutPageTemplateEntryId the primary key of the current layout page template entry
	 * @param groupId the group ID
	 * @param name the name
	 * @param orderByComparator the comparator to order the set by (optionally <code>null</code>)
	 * @return the previous, current, and next layout page template entry
	 * @throws NoSuchPageTemplateEntryException if a layout page template entry with the primary key could not be found
	 */
	public LayoutPageTemplateEntry[] filterFindByG_N_PrevAndNext(
			long layoutPageTemplateEntryId, long groupId, String name,
			com.liferay.portal.kernel.util.OrderByComparator
				<LayoutPageTemplateEntry> orderByComparator)
		throws NoSuchPageTemplateEntryException;

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
	 * Returns all the layout page template entries where groupId = &#63; and type = &#63;.
	 *
	 * @param groupId the group ID
	 * @param type the type
	 * @return the matching layout page template entries
	 */
	public java.util.List<LayoutPageTemplateEntry> findByG_T(
		long groupId, int type);

	/**
	 * Returns a range of all the layout page template entries where groupId = &#63; and type = &#63;.
	 *
	 * <p>
	 * Useful when paginating results. Returns a maximum of <code>end - start</code> instances. <code>start</code> and <code>end</code> are not primary keys, they are indexes in the result set. Thus, <code>0</code> refers to the first result in the set. Setting both <code>start</code> and <code>end</code> to <code>QueryUtil#ALL_POS</code> will return the full result set. If <code>orderByComparator</code> is specified, then the query will include the given ORDER BY logic. If <code>orderByComparator</code> is absent, then the query will include the default ORDER BY logic from <code>LayoutPageTemplateEntryModelImpl</code>.
	 * </p>
	 *
	 * @param groupId the group ID
	 * @param type the type
	 * @param start the lower bound of the range of layout page template entries
	 * @param end the upper bound of the range of layout page template entries (not inclusive)
	 * @return the range of matching layout page template entries
	 */
	public java.util.List<LayoutPageTemplateEntry> findByG_T(
		long groupId, int type, int start, int end);

	/**
	 * Returns an ordered range of all the layout page template entries where groupId = &#63; and type = &#63;.
	 *
	 * <p>
	 * Useful when paginating results. Returns a maximum of <code>end - start</code> instances. <code>start</code> and <code>end</code> are not primary keys, they are indexes in the result set. Thus, <code>0</code> refers to the first result in the set. Setting both <code>start</code> and <code>end</code> to <code>QueryUtil#ALL_POS</code> will return the full result set. If <code>orderByComparator</code> is specified, then the query will include the given ORDER BY logic. If <code>orderByComparator</code> is absent, then the query will include the default ORDER BY logic from <code>LayoutPageTemplateEntryModelImpl</code>.
	 * </p>
	 *
	 * @param groupId the group ID
	 * @param type the type
	 * @param start the lower bound of the range of layout page template entries
	 * @param end the upper bound of the range of layout page template entries (not inclusive)
	 * @param orderByComparator the comparator to order the results by (optionally <code>null</code>)
	 * @return the ordered range of matching layout page template entries
	 */
	public java.util.List<LayoutPageTemplateEntry> findByG_T(
		long groupId, int type, int start, int end,
		com.liferay.portal.kernel.util.OrderByComparator
			<LayoutPageTemplateEntry> orderByComparator);

	/**
	 * Returns an ordered range of all the layout page template entries where groupId = &#63; and type = &#63;.
	 *
	 * <p>
	 * Useful when paginating results. Returns a maximum of <code>end - start</code> instances. <code>start</code> and <code>end</code> are not primary keys, they are indexes in the result set. Thus, <code>0</code> refers to the first result in the set. Setting both <code>start</code> and <code>end</code> to <code>QueryUtil#ALL_POS</code> will return the full result set. If <code>orderByComparator</code> is specified, then the query will include the given ORDER BY logic. If <code>orderByComparator</code> is absent, then the query will include the default ORDER BY logic from <code>LayoutPageTemplateEntryModelImpl</code>.
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
	 * Returns the last layout page template entry in the ordered set where groupId = &#63; and type = &#63;.
	 *
	 * @param groupId the group ID
	 * @param type the type
	 * @param orderByComparator the comparator to order the set by (optionally <code>null</code>)
	 * @return the last matching layout page template entry
	 * @throws NoSuchPageTemplateEntryException if a matching layout page template entry could not be found
	 */
	public LayoutPageTemplateEntry findByG_T_Last(
			long groupId, int type,
			com.liferay.portal.kernel.util.OrderByComparator
				<LayoutPageTemplateEntry> orderByComparator)
		throws NoSuchPageTemplateEntryException;

	/**
	 * Returns the last layout page template entry in the ordered set where groupId = &#63; and type = &#63;.
	 *
	 * @param groupId the group ID
	 * @param type the type
	 * @param orderByComparator the comparator to order the set by (optionally <code>null</code>)
	 * @return the last matching layout page template entry, or <code>null</code> if a matching layout page template entry could not be found
	 */
	public LayoutPageTemplateEntry fetchByG_T_Last(
		long groupId, int type,
		com.liferay.portal.kernel.util.OrderByComparator
			<LayoutPageTemplateEntry> orderByComparator);

	/**
	 * Returns the layout page template entries before and after the current layout page template entry in the ordered set where groupId = &#63; and type = &#63;.
	 *
	 * @param layoutPageTemplateEntryId the primary key of the current layout page template entry
	 * @param groupId the group ID
	 * @param type the type
	 * @param orderByComparator the comparator to order the set by (optionally <code>null</code>)
	 * @return the previous, current, and next layout page template entry
	 * @throws NoSuchPageTemplateEntryException if a layout page template entry with the primary key could not be found
	 */
	public LayoutPageTemplateEntry[] findByG_T_PrevAndNext(
			long layoutPageTemplateEntryId, long groupId, int type,
			com.liferay.portal.kernel.util.OrderByComparator
				<LayoutPageTemplateEntry> orderByComparator)
		throws NoSuchPageTemplateEntryException;

	/**
	 * Returns all the layout page template entries that the user has permission to view where groupId = &#63; and type = &#63;.
	 *
	 * @param groupId the group ID
	 * @param type the type
	 * @return the matching layout page template entries that the user has permission to view
	 */
	public java.util.List<LayoutPageTemplateEntry> filterFindByG_T(
		long groupId, int type);

	/**
	 * Returns a range of all the layout page template entries that the user has permission to view where groupId = &#63; and type = &#63;.
	 *
	 * <p>
	 * Useful when paginating results. Returns a maximum of <code>end - start</code> instances. <code>start</code> and <code>end</code> are not primary keys, they are indexes in the result set. Thus, <code>0</code> refers to the first result in the set. Setting both <code>start</code> and <code>end</code> to <code>QueryUtil#ALL_POS</code> will return the full result set. If <code>orderByComparator</code> is specified, then the query will include the given ORDER BY logic. If <code>orderByComparator</code> is absent, then the query will include the default ORDER BY logic from <code>LayoutPageTemplateEntryModelImpl</code>.
	 * </p>
	 *
	 * @param groupId the group ID
	 * @param type the type
	 * @param start the lower bound of the range of layout page template entries
	 * @param end the upper bound of the range of layout page template entries (not inclusive)
	 * @return the range of matching layout page template entries that the user has permission to view
	 */
	public java.util.List<LayoutPageTemplateEntry> filterFindByG_T(
		long groupId, int type, int start, int end);

	/**
	 * Returns an ordered range of all the layout page template entries that the user has permissions to view where groupId = &#63; and type = &#63;.
	 *
	 * <p>
	 * Useful when paginating results. Returns a maximum of <code>end - start</code> instances. <code>start</code> and <code>end</code> are not primary keys, they are indexes in the result set. Thus, <code>0</code> refers to the first result in the set. Setting both <code>start</code> and <code>end</code> to <code>QueryUtil#ALL_POS</code> will return the full result set. If <code>orderByComparator</code> is specified, then the query will include the given ORDER BY logic. If <code>orderByComparator</code> is absent, then the query will include the default ORDER BY logic from <code>LayoutPageTemplateEntryModelImpl</code>.
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
	 * Returns the layout page template entries before and after the current layout page template entry in the ordered set of layout page template entries that the user has permission to view where groupId = &#63; and type = &#63;.
	 *
	 * @param layoutPageTemplateEntryId the primary key of the current layout page template entry
	 * @param groupId the group ID
	 * @param type the type
	 * @param orderByComparator the comparator to order the set by (optionally <code>null</code>)
	 * @return the previous, current, and next layout page template entry
	 * @throws NoSuchPageTemplateEntryException if a layout page template entry with the primary key could not be found
	 */
	public LayoutPageTemplateEntry[] filterFindByG_T_PrevAndNext(
			long layoutPageTemplateEntryId, long groupId, int type,
			com.liferay.portal.kernel.util.OrderByComparator
				<LayoutPageTemplateEntry> orderByComparator)
		throws NoSuchPageTemplateEntryException;

	/**
	 * Returns all the layout page template entries that the user has permission to view where groupId = &#63; and type = any &#63;.
	 *
	 * @param groupId the group ID
	 * @param types the types
	 * @return the matching layout page template entries that the user has permission to view
	 */
	public java.util.List<LayoutPageTemplateEntry> filterFindByG_T(
		long groupId, int[] types);

	/**
	 * Returns a range of all the layout page template entries that the user has permission to view where groupId = &#63; and type = any &#63;.
	 *
	 * <p>
	 * Useful when paginating results. Returns a maximum of <code>end - start</code> instances. <code>start</code> and <code>end</code> are not primary keys, they are indexes in the result set. Thus, <code>0</code> refers to the first result in the set. Setting both <code>start</code> and <code>end</code> to <code>QueryUtil#ALL_POS</code> will return the full result set. If <code>orderByComparator</code> is specified, then the query will include the given ORDER BY logic. If <code>orderByComparator</code> is absent, then the query will include the default ORDER BY logic from <code>LayoutPageTemplateEntryModelImpl</code>.
	 * </p>
	 *
	 * @param groupId the group ID
	 * @param types the types
	 * @param start the lower bound of the range of layout page template entries
	 * @param end the upper bound of the range of layout page template entries (not inclusive)
	 * @return the range of matching layout page template entries that the user has permission to view
	 */
	public java.util.List<LayoutPageTemplateEntry> filterFindByG_T(
		long groupId, int[] types, int start, int end);

	/**
	 * Returns an ordered range of all the layout page template entries that the user has permission to view where groupId = &#63; and type = any &#63;.
	 *
	 * <p>
	 * Useful when paginating results. Returns a maximum of <code>end - start</code> instances. <code>start</code> and <code>end</code> are not primary keys, they are indexes in the result set. Thus, <code>0</code> refers to the first result in the set. Setting both <code>start</code> and <code>end</code> to <code>QueryUtil#ALL_POS</code> will return the full result set. If <code>orderByComparator</code> is specified, then the query will include the given ORDER BY logic. If <code>orderByComparator</code> is absent, then the query will include the default ORDER BY logic from <code>LayoutPageTemplateEntryModelImpl</code>.
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
	 * Returns all the layout page template entries where groupId = &#63; and type = any &#63;.
	 *
	 * <p>
	 * Useful when paginating results. Returns a maximum of <code>end - start</code> instances. <code>start</code> and <code>end</code> are not primary keys, they are indexes in the result set. Thus, <code>0</code> refers to the first result in the set. Setting both <code>start</code> and <code>end</code> to <code>QueryUtil#ALL_POS</code> will return the full result set. If <code>orderByComparator</code> is specified, then the query will include the given ORDER BY logic. If <code>orderByComparator</code> is absent, then the query will include the default ORDER BY logic from <code>LayoutPageTemplateEntryModelImpl</code>.
	 * </p>
	 *
	 * @param groupId the group ID
	 * @param types the types
	 * @return the matching layout page template entries
	 */
	public java.util.List<LayoutPageTemplateEntry> findByG_T(
		long groupId, int[] types);

	/**
	 * Returns a range of all the layout page template entries where groupId = &#63; and type = any &#63;.
	 *
	 * <p>
	 * Useful when paginating results. Returns a maximum of <code>end - start</code> instances. <code>start</code> and <code>end</code> are not primary keys, they are indexes in the result set. Thus, <code>0</code> refers to the first result in the set. Setting both <code>start</code> and <code>end</code> to <code>QueryUtil#ALL_POS</code> will return the full result set. If <code>orderByComparator</code> is specified, then the query will include the given ORDER BY logic. If <code>orderByComparator</code> is absent, then the query will include the default ORDER BY logic from <code>LayoutPageTemplateEntryModelImpl</code>.
	 * </p>
	 *
	 * @param groupId the group ID
	 * @param types the types
	 * @param start the lower bound of the range of layout page template entries
	 * @param end the upper bound of the range of layout page template entries (not inclusive)
	 * @return the range of matching layout page template entries
	 */
	public java.util.List<LayoutPageTemplateEntry> findByG_T(
		long groupId, int[] types, int start, int end);

	/**
	 * Returns an ordered range of all the layout page template entries where groupId = &#63; and type = any &#63;.
	 *
	 * <p>
	 * Useful when paginating results. Returns a maximum of <code>end - start</code> instances. <code>start</code> and <code>end</code> are not primary keys, they are indexes in the result set. Thus, <code>0</code> refers to the first result in the set. Setting both <code>start</code> and <code>end</code> to <code>QueryUtil#ALL_POS</code> will return the full result set. If <code>orderByComparator</code> is specified, then the query will include the given ORDER BY logic. If <code>orderByComparator</code> is absent, then the query will include the default ORDER BY logic from <code>LayoutPageTemplateEntryModelImpl</code>.
	 * </p>
	 *
	 * @param groupId the group ID
	 * @param types the types
	 * @param start the lower bound of the range of layout page template entries
	 * @param end the upper bound of the range of layout page template entries (not inclusive)
	 * @param orderByComparator the comparator to order the results by (optionally <code>null</code>)
	 * @return the ordered range of matching layout page template entries
	 */
	public java.util.List<LayoutPageTemplateEntry> findByG_T(
		long groupId, int[] types, int start, int end,
		com.liferay.portal.kernel.util.OrderByComparator
			<LayoutPageTemplateEntry> orderByComparator);

	/**
	 * Returns an ordered range of all the layout page template entries where groupId = &#63; and type = &#63;, optionally using the finder cache.
	 *
	 * <p>
	 * Useful when paginating results. Returns a maximum of <code>end - start</code> instances. <code>start</code> and <code>end</code> are not primary keys, they are indexes in the result set. Thus, <code>0</code> refers to the first result in the set. Setting both <code>start</code> and <code>end</code> to <code>QueryUtil#ALL_POS</code> will return the full result set. If <code>orderByComparator</code> is specified, then the query will include the given ORDER BY logic. If <code>orderByComparator</code> is absent, then the query will include the default ORDER BY logic from <code>LayoutPageTemplateEntryModelImpl</code>.
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
	 * Returns all the layout page template entries where groupId = &#63; and status = &#63;.
	 *
	 * @param groupId the group ID
	 * @param status the status
	 * @return the matching layout page template entries
	 */
	public java.util.List<LayoutPageTemplateEntry> findByG_S(
		long groupId, int status);

	/**
	 * Returns a range of all the layout page template entries where groupId = &#63; and status = &#63;.
	 *
	 * <p>
	 * Useful when paginating results. Returns a maximum of <code>end - start</code> instances. <code>start</code> and <code>end</code> are not primary keys, they are indexes in the result set. Thus, <code>0</code> refers to the first result in the set. Setting both <code>start</code> and <code>end</code> to <code>QueryUtil#ALL_POS</code> will return the full result set. If <code>orderByComparator</code> is specified, then the query will include the given ORDER BY logic. If <code>orderByComparator</code> is absent, then the query will include the default ORDER BY logic from <code>LayoutPageTemplateEntryModelImpl</code>.
	 * </p>
	 *
	 * @param groupId the group ID
	 * @param status the status
	 * @param start the lower bound of the range of layout page template entries
	 * @param end the upper bound of the range of layout page template entries (not inclusive)
	 * @return the range of matching layout page template entries
	 */
	public java.util.List<LayoutPageTemplateEntry> findByG_S(
		long groupId, int status, int start, int end);

	/**
	 * Returns an ordered range of all the layout page template entries where groupId = &#63; and status = &#63;.
	 *
	 * <p>
	 * Useful when paginating results. Returns a maximum of <code>end - start</code> instances. <code>start</code> and <code>end</code> are not primary keys, they are indexes in the result set. Thus, <code>0</code> refers to the first result in the set. Setting both <code>start</code> and <code>end</code> to <code>QueryUtil#ALL_POS</code> will return the full result set. If <code>orderByComparator</code> is specified, then the query will include the given ORDER BY logic. If <code>orderByComparator</code> is absent, then the query will include the default ORDER BY logic from <code>LayoutPageTemplateEntryModelImpl</code>.
	 * </p>
	 *
	 * @param groupId the group ID
	 * @param status the status
	 * @param start the lower bound of the range of layout page template entries
	 * @param end the upper bound of the range of layout page template entries (not inclusive)
	 * @param orderByComparator the comparator to order the results by (optionally <code>null</code>)
	 * @return the ordered range of matching layout page template entries
	 */
	public java.util.List<LayoutPageTemplateEntry> findByG_S(
		long groupId, int status, int start, int end,
		com.liferay.portal.kernel.util.OrderByComparator
			<LayoutPageTemplateEntry> orderByComparator);

	/**
	 * Returns an ordered range of all the layout page template entries where groupId = &#63; and status = &#63;.
	 *
	 * <p>
	 * Useful when paginating results. Returns a maximum of <code>end - start</code> instances. <code>start</code> and <code>end</code> are not primary keys, they are indexes in the result set. Thus, <code>0</code> refers to the first result in the set. Setting both <code>start</code> and <code>end</code> to <code>QueryUtil#ALL_POS</code> will return the full result set. If <code>orderByComparator</code> is specified, then the query will include the given ORDER BY logic. If <code>orderByComparator</code> is absent, then the query will include the default ORDER BY logic from <code>LayoutPageTemplateEntryModelImpl</code>.
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
	 * Returns the last layout page template entry in the ordered set where groupId = &#63; and status = &#63;.
	 *
	 * @param groupId the group ID
	 * @param status the status
	 * @param orderByComparator the comparator to order the set by (optionally <code>null</code>)
	 * @return the last matching layout page template entry
	 * @throws NoSuchPageTemplateEntryException if a matching layout page template entry could not be found
	 */
	public LayoutPageTemplateEntry findByG_S_Last(
			long groupId, int status,
			com.liferay.portal.kernel.util.OrderByComparator
				<LayoutPageTemplateEntry> orderByComparator)
		throws NoSuchPageTemplateEntryException;

	/**
	 * Returns the last layout page template entry in the ordered set where groupId = &#63; and status = &#63;.
	 *
	 * @param groupId the group ID
	 * @param status the status
	 * @param orderByComparator the comparator to order the set by (optionally <code>null</code>)
	 * @return the last matching layout page template entry, or <code>null</code> if a matching layout page template entry could not be found
	 */
	public LayoutPageTemplateEntry fetchByG_S_Last(
		long groupId, int status,
		com.liferay.portal.kernel.util.OrderByComparator
			<LayoutPageTemplateEntry> orderByComparator);

	/**
	 * Returns the layout page template entries before and after the current layout page template entry in the ordered set where groupId = &#63; and status = &#63;.
	 *
	 * @param layoutPageTemplateEntryId the primary key of the current layout page template entry
	 * @param groupId the group ID
	 * @param status the status
	 * @param orderByComparator the comparator to order the set by (optionally <code>null</code>)
	 * @return the previous, current, and next layout page template entry
	 * @throws NoSuchPageTemplateEntryException if a layout page template entry with the primary key could not be found
	 */
	public LayoutPageTemplateEntry[] findByG_S_PrevAndNext(
			long layoutPageTemplateEntryId, long groupId, int status,
			com.liferay.portal.kernel.util.OrderByComparator
				<LayoutPageTemplateEntry> orderByComparator)
		throws NoSuchPageTemplateEntryException;

	/**
	 * Returns all the layout page template entries that the user has permission to view where groupId = &#63; and status = &#63;.
	 *
	 * @param groupId the group ID
	 * @param status the status
	 * @return the matching layout page template entries that the user has permission to view
	 */
	public java.util.List<LayoutPageTemplateEntry> filterFindByG_S(
		long groupId, int status);

	/**
	 * Returns a range of all the layout page template entries that the user has permission to view where groupId = &#63; and status = &#63;.
	 *
	 * <p>
	 * Useful when paginating results. Returns a maximum of <code>end - start</code> instances. <code>start</code> and <code>end</code> are not primary keys, they are indexes in the result set. Thus, <code>0</code> refers to the first result in the set. Setting both <code>start</code> and <code>end</code> to <code>QueryUtil#ALL_POS</code> will return the full result set. If <code>orderByComparator</code> is specified, then the query will include the given ORDER BY logic. If <code>orderByComparator</code> is absent, then the query will include the default ORDER BY logic from <code>LayoutPageTemplateEntryModelImpl</code>.
	 * </p>
	 *
	 * @param groupId the group ID
	 * @param status the status
	 * @param start the lower bound of the range of layout page template entries
	 * @param end the upper bound of the range of layout page template entries (not inclusive)
	 * @return the range of matching layout page template entries that the user has permission to view
	 */
	public java.util.List<LayoutPageTemplateEntry> filterFindByG_S(
		long groupId, int status, int start, int end);

	/**
	 * Returns an ordered range of all the layout page template entries that the user has permissions to view where groupId = &#63; and status = &#63;.
	 *
	 * <p>
	 * Useful when paginating results. Returns a maximum of <code>end - start</code> instances. <code>start</code> and <code>end</code> are not primary keys, they are indexes in the result set. Thus, <code>0</code> refers to the first result in the set. Setting both <code>start</code> and <code>end</code> to <code>QueryUtil#ALL_POS</code> will return the full result set. If <code>orderByComparator</code> is specified, then the query will include the given ORDER BY logic. If <code>orderByComparator</code> is absent, then the query will include the default ORDER BY logic from <code>LayoutPageTemplateEntryModelImpl</code>.
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
	 * Returns the layout page template entries before and after the current layout page template entry in the ordered set of layout page template entries that the user has permission to view where groupId = &#63; and status = &#63;.
	 *
	 * @param layoutPageTemplateEntryId the primary key of the current layout page template entry
	 * @param groupId the group ID
	 * @param status the status
	 * @param orderByComparator the comparator to order the set by (optionally <code>null</code>)
	 * @return the previous, current, and next layout page template entry
	 * @throws NoSuchPageTemplateEntryException if a layout page template entry with the primary key could not be found
	 */
	public LayoutPageTemplateEntry[] filterFindByG_S_PrevAndNext(
			long layoutPageTemplateEntryId, long groupId, int status,
			com.liferay.portal.kernel.util.OrderByComparator
				<LayoutPageTemplateEntry> orderByComparator)
		throws NoSuchPageTemplateEntryException;

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
	 * Useful when paginating results. Returns a maximum of <code>end - start</code> instances. <code>start</code> and <code>end</code> are not primary keys, they are indexes in the result set. Thus, <code>0</code> refers to the first result in the set. Setting both <code>start</code> and <code>end</code> to <code>QueryUtil#ALL_POS</code> will return the full result set. If <code>orderByComparator</code> is specified, then the query will include the given ORDER BY logic. If <code>orderByComparator</code> is absent, then the query will include the default ORDER BY logic from <code>LayoutPageTemplateEntryModelImpl</code>.
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
	 * Useful when paginating results. Returns a maximum of <code>end - start</code> instances. <code>start</code> and <code>end</code> are not primary keys, they are indexes in the result set. Thus, <code>0</code> refers to the first result in the set. Setting both <code>start</code> and <code>end</code> to <code>QueryUtil#ALL_POS</code> will return the full result set. If <code>orderByComparator</code> is specified, then the query will include the given ORDER BY logic. If <code>orderByComparator</code> is absent, then the query will include the default ORDER BY logic from <code>LayoutPageTemplateEntryModelImpl</code>.
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
	 * Useful when paginating results. Returns a maximum of <code>end - start</code> instances. <code>start</code> and <code>end</code> are not primary keys, they are indexes in the result set. Thus, <code>0</code> refers to the first result in the set. Setting both <code>start</code> and <code>end</code> to <code>QueryUtil#ALL_POS</code> will return the full result set. If <code>orderByComparator</code> is specified, then the query will include the given ORDER BY logic. If <code>orderByComparator</code> is absent, then the query will include the default ORDER BY logic from <code>LayoutPageTemplateEntryModelImpl</code>.
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
	 * Returns the last layout page template entry in the ordered set where groupId = &#63; and layoutPageTemplateCollectionId = &#63; and name LIKE &#63;.
	 *
	 * @param groupId the group ID
	 * @param layoutPageTemplateCollectionId the layout page template collection ID
	 * @param name the name
	 * @param orderByComparator the comparator to order the set by (optionally <code>null</code>)
	 * @return the last matching layout page template entry
	 * @throws NoSuchPageTemplateEntryException if a matching layout page template entry could not be found
	 */
	public LayoutPageTemplateEntry findByG_L_LikeN_Last(
			long groupId, long layoutPageTemplateCollectionId, String name,
			com.liferay.portal.kernel.util.OrderByComparator
				<LayoutPageTemplateEntry> orderByComparator)
		throws NoSuchPageTemplateEntryException;

	/**
	 * Returns the last layout page template entry in the ordered set where groupId = &#63; and layoutPageTemplateCollectionId = &#63; and name LIKE &#63;.
	 *
	 * @param groupId the group ID
	 * @param layoutPageTemplateCollectionId the layout page template collection ID
	 * @param name the name
	 * @param orderByComparator the comparator to order the set by (optionally <code>null</code>)
	 * @return the last matching layout page template entry, or <code>null</code> if a matching layout page template entry could not be found
	 */
	public LayoutPageTemplateEntry fetchByG_L_LikeN_Last(
		long groupId, long layoutPageTemplateCollectionId, String name,
		com.liferay.portal.kernel.util.OrderByComparator
			<LayoutPageTemplateEntry> orderByComparator);

	/**
	 * Returns the layout page template entries before and after the current layout page template entry in the ordered set where groupId = &#63; and layoutPageTemplateCollectionId = &#63; and name LIKE &#63;.
	 *
	 * @param layoutPageTemplateEntryId the primary key of the current layout page template entry
	 * @param groupId the group ID
	 * @param layoutPageTemplateCollectionId the layout page template collection ID
	 * @param name the name
	 * @param orderByComparator the comparator to order the set by (optionally <code>null</code>)
	 * @return the previous, current, and next layout page template entry
	 * @throws NoSuchPageTemplateEntryException if a layout page template entry with the primary key could not be found
	 */
	public LayoutPageTemplateEntry[] findByG_L_LikeN_PrevAndNext(
			long layoutPageTemplateEntryId, long groupId,
			long layoutPageTemplateCollectionId, String name,
			com.liferay.portal.kernel.util.OrderByComparator
				<LayoutPageTemplateEntry> orderByComparator)
		throws NoSuchPageTemplateEntryException;

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
	 * Useful when paginating results. Returns a maximum of <code>end - start</code> instances. <code>start</code> and <code>end</code> are not primary keys, they are indexes in the result set. Thus, <code>0</code> refers to the first result in the set. Setting both <code>start</code> and <code>end</code> to <code>QueryUtil#ALL_POS</code> will return the full result set. If <code>orderByComparator</code> is specified, then the query will include the given ORDER BY logic. If <code>orderByComparator</code> is absent, then the query will include the default ORDER BY logic from <code>LayoutPageTemplateEntryModelImpl</code>.
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
	 * Useful when paginating results. Returns a maximum of <code>end - start</code> instances. <code>start</code> and <code>end</code> are not primary keys, they are indexes in the result set. Thus, <code>0</code> refers to the first result in the set. Setting both <code>start</code> and <code>end</code> to <code>QueryUtil#ALL_POS</code> will return the full result set. If <code>orderByComparator</code> is specified, then the query will include the given ORDER BY logic. If <code>orderByComparator</code> is absent, then the query will include the default ORDER BY logic from <code>LayoutPageTemplateEntryModelImpl</code>.
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
	 * Returns the layout page template entries before and after the current layout page template entry in the ordered set of layout page template entries that the user has permission to view where groupId = &#63; and layoutPageTemplateCollectionId = &#63; and name LIKE &#63;.
	 *
	 * @param layoutPageTemplateEntryId the primary key of the current layout page template entry
	 * @param groupId the group ID
	 * @param layoutPageTemplateCollectionId the layout page template collection ID
	 * @param name the name
	 * @param orderByComparator the comparator to order the set by (optionally <code>null</code>)
	 * @return the previous, current, and next layout page template entry
	 * @throws NoSuchPageTemplateEntryException if a layout page template entry with the primary key could not be found
	 */
	public LayoutPageTemplateEntry[] filterFindByG_L_LikeN_PrevAndNext(
			long layoutPageTemplateEntryId, long groupId,
			long layoutPageTemplateCollectionId, String name,
			com.liferay.portal.kernel.util.OrderByComparator
				<LayoutPageTemplateEntry> orderByComparator)
		throws NoSuchPageTemplateEntryException;

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
	 * Returns all the layout page template entries where groupId = &#63; and layoutPageTemplateCollectionId = &#63; and type = &#63;.
	 *
	 * @param groupId the group ID
	 * @param layoutPageTemplateCollectionId the layout page template collection ID
	 * @param type the type
	 * @return the matching layout page template entries
	 */
	public java.util.List<LayoutPageTemplateEntry> findByG_L_T(
		long groupId, long layoutPageTemplateCollectionId, int type);

	/**
	 * Returns a range of all the layout page template entries where groupId = &#63; and layoutPageTemplateCollectionId = &#63; and type = &#63;.
	 *
	 * <p>
	 * Useful when paginating results. Returns a maximum of <code>end - start</code> instances. <code>start</code> and <code>end</code> are not primary keys, they are indexes in the result set. Thus, <code>0</code> refers to the first result in the set. Setting both <code>start</code> and <code>end</code> to <code>QueryUtil#ALL_POS</code> will return the full result set. If <code>orderByComparator</code> is specified, then the query will include the given ORDER BY logic. If <code>orderByComparator</code> is absent, then the query will include the default ORDER BY logic from <code>LayoutPageTemplateEntryModelImpl</code>.
	 * </p>
	 *
	 * @param groupId the group ID
	 * @param layoutPageTemplateCollectionId the layout page template collection ID
	 * @param type the type
	 * @param start the lower bound of the range of layout page template entries
	 * @param end the upper bound of the range of layout page template entries (not inclusive)
	 * @return the range of matching layout page template entries
	 */
	public java.util.List<LayoutPageTemplateEntry> findByG_L_T(
		long groupId, long layoutPageTemplateCollectionId, int type, int start,
		int end);

	/**
	 * Returns an ordered range of all the layout page template entries where groupId = &#63; and layoutPageTemplateCollectionId = &#63; and type = &#63;.
	 *
	 * <p>
	 * Useful when paginating results. Returns a maximum of <code>end - start</code> instances. <code>start</code> and <code>end</code> are not primary keys, they are indexes in the result set. Thus, <code>0</code> refers to the first result in the set. Setting both <code>start</code> and <code>end</code> to <code>QueryUtil#ALL_POS</code> will return the full result set. If <code>orderByComparator</code> is specified, then the query will include the given ORDER BY logic. If <code>orderByComparator</code> is absent, then the query will include the default ORDER BY logic from <code>LayoutPageTemplateEntryModelImpl</code>.
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
	public java.util.List<LayoutPageTemplateEntry> findByG_L_T(
		long groupId, long layoutPageTemplateCollectionId, int type, int start,
		int end,
		com.liferay.portal.kernel.util.OrderByComparator
			<LayoutPageTemplateEntry> orderByComparator);

	/**
	 * Returns an ordered range of all the layout page template entries where groupId = &#63; and layoutPageTemplateCollectionId = &#63; and type = &#63;.
	 *
	 * <p>
	 * Useful when paginating results. Returns a maximum of <code>end - start</code> instances. <code>start</code> and <code>end</code> are not primary keys, they are indexes in the result set. Thus, <code>0</code> refers to the first result in the set. Setting both <code>start</code> and <code>end</code> to <code>QueryUtil#ALL_POS</code> will return the full result set. If <code>orderByComparator</code> is specified, then the query will include the given ORDER BY logic. If <code>orderByComparator</code> is absent, then the query will include the default ORDER BY logic from <code>LayoutPageTemplateEntryModelImpl</code>.
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
	 * Returns the last layout page template entry in the ordered set where groupId = &#63; and layoutPageTemplateCollectionId = &#63; and type = &#63;.
	 *
	 * @param groupId the group ID
	 * @param layoutPageTemplateCollectionId the layout page template collection ID
	 * @param type the type
	 * @param orderByComparator the comparator to order the set by (optionally <code>null</code>)
	 * @return the last matching layout page template entry
	 * @throws NoSuchPageTemplateEntryException if a matching layout page template entry could not be found
	 */
	public LayoutPageTemplateEntry findByG_L_T_Last(
			long groupId, long layoutPageTemplateCollectionId, int type,
			com.liferay.portal.kernel.util.OrderByComparator
				<LayoutPageTemplateEntry> orderByComparator)
		throws NoSuchPageTemplateEntryException;

	/**
	 * Returns the last layout page template entry in the ordered set where groupId = &#63; and layoutPageTemplateCollectionId = &#63; and type = &#63;.
	 *
	 * @param groupId the group ID
	 * @param layoutPageTemplateCollectionId the layout page template collection ID
	 * @param type the type
	 * @param orderByComparator the comparator to order the set by (optionally <code>null</code>)
	 * @return the last matching layout page template entry, or <code>null</code> if a matching layout page template entry could not be found
	 */
	public LayoutPageTemplateEntry fetchByG_L_T_Last(
		long groupId, long layoutPageTemplateCollectionId, int type,
		com.liferay.portal.kernel.util.OrderByComparator
			<LayoutPageTemplateEntry> orderByComparator);

	/**
	 * Returns the layout page template entries before and after the current layout page template entry in the ordered set where groupId = &#63; and layoutPageTemplateCollectionId = &#63; and type = &#63;.
	 *
	 * @param layoutPageTemplateEntryId the primary key of the current layout page template entry
	 * @param groupId the group ID
	 * @param layoutPageTemplateCollectionId the layout page template collection ID
	 * @param type the type
	 * @param orderByComparator the comparator to order the set by (optionally <code>null</code>)
	 * @return the previous, current, and next layout page template entry
	 * @throws NoSuchPageTemplateEntryException if a layout page template entry with the primary key could not be found
	 */
	public LayoutPageTemplateEntry[] findByG_L_T_PrevAndNext(
			long layoutPageTemplateEntryId, long groupId,
			long layoutPageTemplateCollectionId, int type,
			com.liferay.portal.kernel.util.OrderByComparator
				<LayoutPageTemplateEntry> orderByComparator)
		throws NoSuchPageTemplateEntryException;

	/**
	 * Returns all the layout page template entries that the user has permission to view where groupId = &#63; and layoutPageTemplateCollectionId = &#63; and type = &#63;.
	 *
	 * @param groupId the group ID
	 * @param layoutPageTemplateCollectionId the layout page template collection ID
	 * @param type the type
	 * @return the matching layout page template entries that the user has permission to view
	 */
	public java.util.List<LayoutPageTemplateEntry> filterFindByG_L_T(
		long groupId, long layoutPageTemplateCollectionId, int type);

	/**
	 * Returns a range of all the layout page template entries that the user has permission to view where groupId = &#63; and layoutPageTemplateCollectionId = &#63; and type = &#63;.
	 *
	 * <p>
	 * Useful when paginating results. Returns a maximum of <code>end - start</code> instances. <code>start</code> and <code>end</code> are not primary keys, they are indexes in the result set. Thus, <code>0</code> refers to the first result in the set. Setting both <code>start</code> and <code>end</code> to <code>QueryUtil#ALL_POS</code> will return the full result set. If <code>orderByComparator</code> is specified, then the query will include the given ORDER BY logic. If <code>orderByComparator</code> is absent, then the query will include the default ORDER BY logic from <code>LayoutPageTemplateEntryModelImpl</code>.
	 * </p>
	 *
	 * @param groupId the group ID
	 * @param layoutPageTemplateCollectionId the layout page template collection ID
	 * @param type the type
	 * @param start the lower bound of the range of layout page template entries
	 * @param end the upper bound of the range of layout page template entries (not inclusive)
	 * @return the range of matching layout page template entries that the user has permission to view
	 */
	public java.util.List<LayoutPageTemplateEntry> filterFindByG_L_T(
		long groupId, long layoutPageTemplateCollectionId, int type, int start,
		int end);

	/**
	 * Returns an ordered range of all the layout page template entries that the user has permissions to view where groupId = &#63; and layoutPageTemplateCollectionId = &#63; and type = &#63;.
	 *
	 * <p>
	 * Useful when paginating results. Returns a maximum of <code>end - start</code> instances. <code>start</code> and <code>end</code> are not primary keys, they are indexes in the result set. Thus, <code>0</code> refers to the first result in the set. Setting both <code>start</code> and <code>end</code> to <code>QueryUtil#ALL_POS</code> will return the full result set. If <code>orderByComparator</code> is specified, then the query will include the given ORDER BY logic. If <code>orderByComparator</code> is absent, then the query will include the default ORDER BY logic from <code>LayoutPageTemplateEntryModelImpl</code>.
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
	 * Returns the layout page template entries before and after the current layout page template entry in the ordered set of layout page template entries that the user has permission to view where groupId = &#63; and layoutPageTemplateCollectionId = &#63; and type = &#63;.
	 *
	 * @param layoutPageTemplateEntryId the primary key of the current layout page template entry
	 * @param groupId the group ID
	 * @param layoutPageTemplateCollectionId the layout page template collection ID
	 * @param type the type
	 * @param orderByComparator the comparator to order the set by (optionally <code>null</code>)
	 * @return the previous, current, and next layout page template entry
	 * @throws NoSuchPageTemplateEntryException if a layout page template entry with the primary key could not be found
	 */
	public LayoutPageTemplateEntry[] filterFindByG_L_T_PrevAndNext(
			long layoutPageTemplateEntryId, long groupId,
			long layoutPageTemplateCollectionId, int type,
			com.liferay.portal.kernel.util.OrderByComparator
				<LayoutPageTemplateEntry> orderByComparator)
		throws NoSuchPageTemplateEntryException;

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
	 * Returns all the layout page template entries where groupId = &#63; and layoutPageTemplateCollectionId = &#63; and status = &#63;.
	 *
	 * @param groupId the group ID
	 * @param layoutPageTemplateCollectionId the layout page template collection ID
	 * @param status the status
	 * @return the matching layout page template entries
	 */
	public java.util.List<LayoutPageTemplateEntry> findByG_L_S(
		long groupId, long layoutPageTemplateCollectionId, int status);

	/**
	 * Returns a range of all the layout page template entries where groupId = &#63; and layoutPageTemplateCollectionId = &#63; and status = &#63;.
	 *
	 * <p>
	 * Useful when paginating results. Returns a maximum of <code>end - start</code> instances. <code>start</code> and <code>end</code> are not primary keys, they are indexes in the result set. Thus, <code>0</code> refers to the first result in the set. Setting both <code>start</code> and <code>end</code> to <code>QueryUtil#ALL_POS</code> will return the full result set. If <code>orderByComparator</code> is specified, then the query will include the given ORDER BY logic. If <code>orderByComparator</code> is absent, then the query will include the default ORDER BY logic from <code>LayoutPageTemplateEntryModelImpl</code>.
	 * </p>
	 *
	 * @param groupId the group ID
	 * @param layoutPageTemplateCollectionId the layout page template collection ID
	 * @param status the status
	 * @param start the lower bound of the range of layout page template entries
	 * @param end the upper bound of the range of layout page template entries (not inclusive)
	 * @return the range of matching layout page template entries
	 */
	public java.util.List<LayoutPageTemplateEntry> findByG_L_S(
		long groupId, long layoutPageTemplateCollectionId, int status,
		int start, int end);

	/**
	 * Returns an ordered range of all the layout page template entries where groupId = &#63; and layoutPageTemplateCollectionId = &#63; and status = &#63;.
	 *
	 * <p>
	 * Useful when paginating results. Returns a maximum of <code>end - start</code> instances. <code>start</code> and <code>end</code> are not primary keys, they are indexes in the result set. Thus, <code>0</code> refers to the first result in the set. Setting both <code>start</code> and <code>end</code> to <code>QueryUtil#ALL_POS</code> will return the full result set. If <code>orderByComparator</code> is specified, then the query will include the given ORDER BY logic. If <code>orderByComparator</code> is absent, then the query will include the default ORDER BY logic from <code>LayoutPageTemplateEntryModelImpl</code>.
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
	public java.util.List<LayoutPageTemplateEntry> findByG_L_S(
		long groupId, long layoutPageTemplateCollectionId, int status,
		int start, int end,
		com.liferay.portal.kernel.util.OrderByComparator
			<LayoutPageTemplateEntry> orderByComparator);

	/**
	 * Returns an ordered range of all the layout page template entries where groupId = &#63; and layoutPageTemplateCollectionId = &#63; and status = &#63;.
	 *
	 * <p>
	 * Useful when paginating results. Returns a maximum of <code>end - start</code> instances. <code>start</code> and <code>end</code> are not primary keys, they are indexes in the result set. Thus, <code>0</code> refers to the first result in the set. Setting both <code>start</code> and <code>end</code> to <code>QueryUtil#ALL_POS</code> will return the full result set. If <code>orderByComparator</code> is specified, then the query will include the given ORDER BY logic. If <code>orderByComparator</code> is absent, then the query will include the default ORDER BY logic from <code>LayoutPageTemplateEntryModelImpl</code>.
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
	 * Returns the last layout page template entry in the ordered set where groupId = &#63; and layoutPageTemplateCollectionId = &#63; and status = &#63;.
	 *
	 * @param groupId the group ID
	 * @param layoutPageTemplateCollectionId the layout page template collection ID
	 * @param status the status
	 * @param orderByComparator the comparator to order the set by (optionally <code>null</code>)
	 * @return the last matching layout page template entry
	 * @throws NoSuchPageTemplateEntryException if a matching layout page template entry could not be found
	 */
	public LayoutPageTemplateEntry findByG_L_S_Last(
			long groupId, long layoutPageTemplateCollectionId, int status,
			com.liferay.portal.kernel.util.OrderByComparator
				<LayoutPageTemplateEntry> orderByComparator)
		throws NoSuchPageTemplateEntryException;

	/**
	 * Returns the last layout page template entry in the ordered set where groupId = &#63; and layoutPageTemplateCollectionId = &#63; and status = &#63;.
	 *
	 * @param groupId the group ID
	 * @param layoutPageTemplateCollectionId the layout page template collection ID
	 * @param status the status
	 * @param orderByComparator the comparator to order the set by (optionally <code>null</code>)
	 * @return the last matching layout page template entry, or <code>null</code> if a matching layout page template entry could not be found
	 */
	public LayoutPageTemplateEntry fetchByG_L_S_Last(
		long groupId, long layoutPageTemplateCollectionId, int status,
		com.liferay.portal.kernel.util.OrderByComparator
			<LayoutPageTemplateEntry> orderByComparator);

	/**
	 * Returns the layout page template entries before and after the current layout page template entry in the ordered set where groupId = &#63; and layoutPageTemplateCollectionId = &#63; and status = &#63;.
	 *
	 * @param layoutPageTemplateEntryId the primary key of the current layout page template entry
	 * @param groupId the group ID
	 * @param layoutPageTemplateCollectionId the layout page template collection ID
	 * @param status the status
	 * @param orderByComparator the comparator to order the set by (optionally <code>null</code>)
	 * @return the previous, current, and next layout page template entry
	 * @throws NoSuchPageTemplateEntryException if a layout page template entry with the primary key could not be found
	 */
	public LayoutPageTemplateEntry[] findByG_L_S_PrevAndNext(
			long layoutPageTemplateEntryId, long groupId,
			long layoutPageTemplateCollectionId, int status,
			com.liferay.portal.kernel.util.OrderByComparator
				<LayoutPageTemplateEntry> orderByComparator)
		throws NoSuchPageTemplateEntryException;

	/**
	 * Returns all the layout page template entries that the user has permission to view where groupId = &#63; and layoutPageTemplateCollectionId = &#63; and status = &#63;.
	 *
	 * @param groupId the group ID
	 * @param layoutPageTemplateCollectionId the layout page template collection ID
	 * @param status the status
	 * @return the matching layout page template entries that the user has permission to view
	 */
	public java.util.List<LayoutPageTemplateEntry> filterFindByG_L_S(
		long groupId, long layoutPageTemplateCollectionId, int status);

	/**
	 * Returns a range of all the layout page template entries that the user has permission to view where groupId = &#63; and layoutPageTemplateCollectionId = &#63; and status = &#63;.
	 *
	 * <p>
	 * Useful when paginating results. Returns a maximum of <code>end - start</code> instances. <code>start</code> and <code>end</code> are not primary keys, they are indexes in the result set. Thus, <code>0</code> refers to the first result in the set. Setting both <code>start</code> and <code>end</code> to <code>QueryUtil#ALL_POS</code> will return the full result set. If <code>orderByComparator</code> is specified, then the query will include the given ORDER BY logic. If <code>orderByComparator</code> is absent, then the query will include the default ORDER BY logic from <code>LayoutPageTemplateEntryModelImpl</code>.
	 * </p>
	 *
	 * @param groupId the group ID
	 * @param layoutPageTemplateCollectionId the layout page template collection ID
	 * @param status the status
	 * @param start the lower bound of the range of layout page template entries
	 * @param end the upper bound of the range of layout page template entries (not inclusive)
	 * @return the range of matching layout page template entries that the user has permission to view
	 */
	public java.util.List<LayoutPageTemplateEntry> filterFindByG_L_S(
		long groupId, long layoutPageTemplateCollectionId, int status,
		int start, int end);

	/**
	 * Returns an ordered range of all the layout page template entries that the user has permissions to view where groupId = &#63; and layoutPageTemplateCollectionId = &#63; and status = &#63;.
	 *
	 * <p>
	 * Useful when paginating results. Returns a maximum of <code>end - start</code> instances. <code>start</code> and <code>end</code> are not primary keys, they are indexes in the result set. Thus, <code>0</code> refers to the first result in the set. Setting both <code>start</code> and <code>end</code> to <code>QueryUtil#ALL_POS</code> will return the full result set. If <code>orderByComparator</code> is specified, then the query will include the given ORDER BY logic. If <code>orderByComparator</code> is absent, then the query will include the default ORDER BY logic from <code>LayoutPageTemplateEntryModelImpl</code>.
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
	 * Returns the layout page template entries before and after the current layout page template entry in the ordered set of layout page template entries that the user has permission to view where groupId = &#63; and layoutPageTemplateCollectionId = &#63; and status = &#63;.
	 *
	 * @param layoutPageTemplateEntryId the primary key of the current layout page template entry
	 * @param groupId the group ID
	 * @param layoutPageTemplateCollectionId the layout page template collection ID
	 * @param status the status
	 * @param orderByComparator the comparator to order the set by (optionally <code>null</code>)
	 * @return the previous, current, and next layout page template entry
	 * @throws NoSuchPageTemplateEntryException if a layout page template entry with the primary key could not be found
	 */
	public LayoutPageTemplateEntry[] filterFindByG_L_S_PrevAndNext(
			long layoutPageTemplateEntryId, long groupId,
			long layoutPageTemplateCollectionId, int status,
			com.liferay.portal.kernel.util.OrderByComparator
				<LayoutPageTemplateEntry> orderByComparator)
		throws NoSuchPageTemplateEntryException;

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
	 * Returns all the layout page template entries where groupId = &#63; and name = &#63; and type = &#63;.
	 *
	 * @param groupId the group ID
	 * @param name the name
	 * @param type the type
	 * @return the matching layout page template entries
	 */
	public java.util.List<LayoutPageTemplateEntry> findByG_N_T(
		long groupId, String name, int type);

	/**
	 * Returns a range of all the layout page template entries where groupId = &#63; and name = &#63; and type = &#63;.
	 *
	 * <p>
	 * Useful when paginating results. Returns a maximum of <code>end - start</code> instances. <code>start</code> and <code>end</code> are not primary keys, they are indexes in the result set. Thus, <code>0</code> refers to the first result in the set. Setting both <code>start</code> and <code>end</code> to <code>QueryUtil#ALL_POS</code> will return the full result set. If <code>orderByComparator</code> is specified, then the query will include the given ORDER BY logic. If <code>orderByComparator</code> is absent, then the query will include the default ORDER BY logic from <code>LayoutPageTemplateEntryModelImpl</code>.
	 * </p>
	 *
	 * @param groupId the group ID
	 * @param name the name
	 * @param type the type
	 * @param start the lower bound of the range of layout page template entries
	 * @param end the upper bound of the range of layout page template entries (not inclusive)
	 * @return the range of matching layout page template entries
	 */
	public java.util.List<LayoutPageTemplateEntry> findByG_N_T(
		long groupId, String name, int type, int start, int end);

	/**
	 * Returns an ordered range of all the layout page template entries where groupId = &#63; and name = &#63; and type = &#63;.
	 *
	 * <p>
	 * Useful when paginating results. Returns a maximum of <code>end - start</code> instances. <code>start</code> and <code>end</code> are not primary keys, they are indexes in the result set. Thus, <code>0</code> refers to the first result in the set. Setting both <code>start</code> and <code>end</code> to <code>QueryUtil#ALL_POS</code> will return the full result set. If <code>orderByComparator</code> is specified, then the query will include the given ORDER BY logic. If <code>orderByComparator</code> is absent, then the query will include the default ORDER BY logic from <code>LayoutPageTemplateEntryModelImpl</code>.
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
	public java.util.List<LayoutPageTemplateEntry> findByG_N_T(
		long groupId, String name, int type, int start, int end,
		com.liferay.portal.kernel.util.OrderByComparator
			<LayoutPageTemplateEntry> orderByComparator);

	/**
	 * Returns an ordered range of all the layout page template entries where groupId = &#63; and name = &#63; and type = &#63;.
	 *
	 * <p>
	 * Useful when paginating results. Returns a maximum of <code>end - start</code> instances. <code>start</code> and <code>end</code> are not primary keys, they are indexes in the result set. Thus, <code>0</code> refers to the first result in the set. Setting both <code>start</code> and <code>end</code> to <code>QueryUtil#ALL_POS</code> will return the full result set. If <code>orderByComparator</code> is specified, then the query will include the given ORDER BY logic. If <code>orderByComparator</code> is absent, then the query will include the default ORDER BY logic from <code>LayoutPageTemplateEntryModelImpl</code>.
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
	 * Returns the last layout page template entry in the ordered set where groupId = &#63; and name = &#63; and type = &#63;.
	 *
	 * @param groupId the group ID
	 * @param name the name
	 * @param type the type
	 * @param orderByComparator the comparator to order the set by (optionally <code>null</code>)
	 * @return the last matching layout page template entry
	 * @throws NoSuchPageTemplateEntryException if a matching layout page template entry could not be found
	 */
	public LayoutPageTemplateEntry findByG_N_T_Last(
			long groupId, String name, int type,
			com.liferay.portal.kernel.util.OrderByComparator
				<LayoutPageTemplateEntry> orderByComparator)
		throws NoSuchPageTemplateEntryException;

	/**
	 * Returns the last layout page template entry in the ordered set where groupId = &#63; and name = &#63; and type = &#63;.
	 *
	 * @param groupId the group ID
	 * @param name the name
	 * @param type the type
	 * @param orderByComparator the comparator to order the set by (optionally <code>null</code>)
	 * @return the last matching layout page template entry, or <code>null</code> if a matching layout page template entry could not be found
	 */
	public LayoutPageTemplateEntry fetchByG_N_T_Last(
		long groupId, String name, int type,
		com.liferay.portal.kernel.util.OrderByComparator
			<LayoutPageTemplateEntry> orderByComparator);

	/**
	 * Returns the layout page template entries before and after the current layout page template entry in the ordered set where groupId = &#63; and name = &#63; and type = &#63;.
	 *
	 * @param layoutPageTemplateEntryId the primary key of the current layout page template entry
	 * @param groupId the group ID
	 * @param name the name
	 * @param type the type
	 * @param orderByComparator the comparator to order the set by (optionally <code>null</code>)
	 * @return the previous, current, and next layout page template entry
	 * @throws NoSuchPageTemplateEntryException if a layout page template entry with the primary key could not be found
	 */
	public LayoutPageTemplateEntry[] findByG_N_T_PrevAndNext(
			long layoutPageTemplateEntryId, long groupId, String name, int type,
			com.liferay.portal.kernel.util.OrderByComparator
				<LayoutPageTemplateEntry> orderByComparator)
		throws NoSuchPageTemplateEntryException;

	/**
	 * Returns all the layout page template entries that the user has permission to view where groupId = &#63; and name = &#63; and type = &#63;.
	 *
	 * @param groupId the group ID
	 * @param name the name
	 * @param type the type
	 * @return the matching layout page template entries that the user has permission to view
	 */
	public java.util.List<LayoutPageTemplateEntry> filterFindByG_N_T(
		long groupId, String name, int type);

	/**
	 * Returns a range of all the layout page template entries that the user has permission to view where groupId = &#63; and name = &#63; and type = &#63;.
	 *
	 * <p>
	 * Useful when paginating results. Returns a maximum of <code>end - start</code> instances. <code>start</code> and <code>end</code> are not primary keys, they are indexes in the result set. Thus, <code>0</code> refers to the first result in the set. Setting both <code>start</code> and <code>end</code> to <code>QueryUtil#ALL_POS</code> will return the full result set. If <code>orderByComparator</code> is specified, then the query will include the given ORDER BY logic. If <code>orderByComparator</code> is absent, then the query will include the default ORDER BY logic from <code>LayoutPageTemplateEntryModelImpl</code>.
	 * </p>
	 *
	 * @param groupId the group ID
	 * @param name the name
	 * @param type the type
	 * @param start the lower bound of the range of layout page template entries
	 * @param end the upper bound of the range of layout page template entries (not inclusive)
	 * @return the range of matching layout page template entries that the user has permission to view
	 */
	public java.util.List<LayoutPageTemplateEntry> filterFindByG_N_T(
		long groupId, String name, int type, int start, int end);

	/**
	 * Returns an ordered range of all the layout page template entries that the user has permissions to view where groupId = &#63; and name = &#63; and type = &#63;.
	 *
	 * <p>
	 * Useful when paginating results. Returns a maximum of <code>end - start</code> instances. <code>start</code> and <code>end</code> are not primary keys, they are indexes in the result set. Thus, <code>0</code> refers to the first result in the set. Setting both <code>start</code> and <code>end</code> to <code>QueryUtil#ALL_POS</code> will return the full result set. If <code>orderByComparator</code> is specified, then the query will include the given ORDER BY logic. If <code>orderByComparator</code> is absent, then the query will include the default ORDER BY logic from <code>LayoutPageTemplateEntryModelImpl</code>.
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
	 * Returns the layout page template entries before and after the current layout page template entry in the ordered set of layout page template entries that the user has permission to view where groupId = &#63; and name = &#63; and type = &#63;.
	 *
	 * @param layoutPageTemplateEntryId the primary key of the current layout page template entry
	 * @param groupId the group ID
	 * @param name the name
	 * @param type the type
	 * @param orderByComparator the comparator to order the set by (optionally <code>null</code>)
	 * @return the previous, current, and next layout page template entry
	 * @throws NoSuchPageTemplateEntryException if a layout page template entry with the primary key could not be found
	 */
	public LayoutPageTemplateEntry[] filterFindByG_N_T_PrevAndNext(
			long layoutPageTemplateEntryId, long groupId, String name, int type,
			com.liferay.portal.kernel.util.OrderByComparator
				<LayoutPageTemplateEntry> orderByComparator)
		throws NoSuchPageTemplateEntryException;

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
	 * Useful when paginating results. Returns a maximum of <code>end - start</code> instances. <code>start</code> and <code>end</code> are not primary keys, they are indexes in the result set. Thus, <code>0</code> refers to the first result in the set. Setting both <code>start</code> and <code>end</code> to <code>QueryUtil#ALL_POS</code> will return the full result set. If <code>orderByComparator</code> is specified, then the query will include the given ORDER BY logic. If <code>orderByComparator</code> is absent, then the query will include the default ORDER BY logic from <code>LayoutPageTemplateEntryModelImpl</code>.
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
	 * Useful when paginating results. Returns a maximum of <code>end - start</code> instances. <code>start</code> and <code>end</code> are not primary keys, they are indexes in the result set. Thus, <code>0</code> refers to the first result in the set. Setting both <code>start</code> and <code>end</code> to <code>QueryUtil#ALL_POS</code> will return the full result set. If <code>orderByComparator</code> is specified, then the query will include the given ORDER BY logic. If <code>orderByComparator</code> is absent, then the query will include the default ORDER BY logic from <code>LayoutPageTemplateEntryModelImpl</code>.
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
	 * Useful when paginating results. Returns a maximum of <code>end - start</code> instances. <code>start</code> and <code>end</code> are not primary keys, they are indexes in the result set. Thus, <code>0</code> refers to the first result in the set. Setting both <code>start</code> and <code>end</code> to <code>QueryUtil#ALL_POS</code> will return the full result set. If <code>orderByComparator</code> is specified, then the query will include the given ORDER BY logic. If <code>orderByComparator</code> is absent, then the query will include the default ORDER BY logic from <code>LayoutPageTemplateEntryModelImpl</code>.
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
	 * Returns the last layout page template entry in the ordered set where groupId = &#63; and name LIKE &#63; and type = &#63;.
	 *
	 * @param groupId the group ID
	 * @param name the name
	 * @param type the type
	 * @param orderByComparator the comparator to order the set by (optionally <code>null</code>)
	 * @return the last matching layout page template entry
	 * @throws NoSuchPageTemplateEntryException if a matching layout page template entry could not be found
	 */
	public LayoutPageTemplateEntry findByG_T_LikeN_Last(
			long groupId, String name, int type,
			com.liferay.portal.kernel.util.OrderByComparator
				<LayoutPageTemplateEntry> orderByComparator)
		throws NoSuchPageTemplateEntryException;

	/**
	 * Returns the last layout page template entry in the ordered set where groupId = &#63; and name LIKE &#63; and type = &#63;.
	 *
	 * @param groupId the group ID
	 * @param name the name
	 * @param type the type
	 * @param orderByComparator the comparator to order the set by (optionally <code>null</code>)
	 * @return the last matching layout page template entry, or <code>null</code> if a matching layout page template entry could not be found
	 */
	public LayoutPageTemplateEntry fetchByG_T_LikeN_Last(
		long groupId, String name, int type,
		com.liferay.portal.kernel.util.OrderByComparator
			<LayoutPageTemplateEntry> orderByComparator);

	/**
	 * Returns the layout page template entries before and after the current layout page template entry in the ordered set where groupId = &#63; and name LIKE &#63; and type = &#63;.
	 *
	 * @param layoutPageTemplateEntryId the primary key of the current layout page template entry
	 * @param groupId the group ID
	 * @param name the name
	 * @param type the type
	 * @param orderByComparator the comparator to order the set by (optionally <code>null</code>)
	 * @return the previous, current, and next layout page template entry
	 * @throws NoSuchPageTemplateEntryException if a layout page template entry with the primary key could not be found
	 */
	public LayoutPageTemplateEntry[] findByG_T_LikeN_PrevAndNext(
			long layoutPageTemplateEntryId, long groupId, String name, int type,
			com.liferay.portal.kernel.util.OrderByComparator
				<LayoutPageTemplateEntry> orderByComparator)
		throws NoSuchPageTemplateEntryException;

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
	 * Useful when paginating results. Returns a maximum of <code>end - start</code> instances. <code>start</code> and <code>end</code> are not primary keys, they are indexes in the result set. Thus, <code>0</code> refers to the first result in the set. Setting both <code>start</code> and <code>end</code> to <code>QueryUtil#ALL_POS</code> will return the full result set. If <code>orderByComparator</code> is specified, then the query will include the given ORDER BY logic. If <code>orderByComparator</code> is absent, then the query will include the default ORDER BY logic from <code>LayoutPageTemplateEntryModelImpl</code>.
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
	 * Useful when paginating results. Returns a maximum of <code>end - start</code> instances. <code>start</code> and <code>end</code> are not primary keys, they are indexes in the result set. Thus, <code>0</code> refers to the first result in the set. Setting both <code>start</code> and <code>end</code> to <code>QueryUtil#ALL_POS</code> will return the full result set. If <code>orderByComparator</code> is specified, then the query will include the given ORDER BY logic. If <code>orderByComparator</code> is absent, then the query will include the default ORDER BY logic from <code>LayoutPageTemplateEntryModelImpl</code>.
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
	 * Returns the layout page template entries before and after the current layout page template entry in the ordered set of layout page template entries that the user has permission to view where groupId = &#63; and name LIKE &#63; and type = &#63;.
	 *
	 * @param layoutPageTemplateEntryId the primary key of the current layout page template entry
	 * @param groupId the group ID
	 * @param name the name
	 * @param type the type
	 * @param orderByComparator the comparator to order the set by (optionally <code>null</code>)
	 * @return the previous, current, and next layout page template entry
	 * @throws NoSuchPageTemplateEntryException if a layout page template entry with the primary key could not be found
	 */
	public LayoutPageTemplateEntry[] filterFindByG_T_LikeN_PrevAndNext(
			long layoutPageTemplateEntryId, long groupId, String name, int type,
			com.liferay.portal.kernel.util.OrderByComparator
				<LayoutPageTemplateEntry> orderByComparator)
		throws NoSuchPageTemplateEntryException;

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
	 * Useful when paginating results. Returns a maximum of <code>end - start</code> instances. <code>start</code> and <code>end</code> are not primary keys, they are indexes in the result set. Thus, <code>0</code> refers to the first result in the set. Setting both <code>start</code> and <code>end</code> to <code>QueryUtil#ALL_POS</code> will return the full result set. If <code>orderByComparator</code> is specified, then the query will include the given ORDER BY logic. If <code>orderByComparator</code> is absent, then the query will include the default ORDER BY logic from <code>LayoutPageTemplateEntryModelImpl</code>.
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
	 * Useful when paginating results. Returns a maximum of <code>end - start</code> instances. <code>start</code> and <code>end</code> are not primary keys, they are indexes in the result set. Thus, <code>0</code> refers to the first result in the set. Setting both <code>start</code> and <code>end</code> to <code>QueryUtil#ALL_POS</code> will return the full result set. If <code>orderByComparator</code> is specified, then the query will include the given ORDER BY logic. If <code>orderByComparator</code> is absent, then the query will include the default ORDER BY logic from <code>LayoutPageTemplateEntryModelImpl</code>.
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
	 * Useful when paginating results. Returns a maximum of <code>end - start</code> instances. <code>start</code> and <code>end</code> are not primary keys, they are indexes in the result set. Thus, <code>0</code> refers to the first result in the set. Setting both <code>start</code> and <code>end</code> to <code>QueryUtil#ALL_POS</code> will return the full result set. If <code>orderByComparator</code> is specified, then the query will include the given ORDER BY logic. If <code>orderByComparator</code> is absent, then the query will include the default ORDER BY logic from <code>LayoutPageTemplateEntryModelImpl</code>.
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
	 * Useful when paginating results. Returns a maximum of <code>end - start</code> instances. <code>start</code> and <code>end</code> are not primary keys, they are indexes in the result set. Thus, <code>0</code> refers to the first result in the set. Setting both <code>start</code> and <code>end</code> to <code>QueryUtil#ALL_POS</code> will return the full result set. If <code>orderByComparator</code> is specified, then the query will include the given ORDER BY logic. If <code>orderByComparator</code> is absent, then the query will include the default ORDER BY logic from <code>LayoutPageTemplateEntryModelImpl</code>.
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
	 * Useful when paginating results. Returns a maximum of <code>end - start</code> instances. <code>start</code> and <code>end</code> are not primary keys, they are indexes in the result set. Thus, <code>0</code> refers to the first result in the set. Setting both <code>start</code> and <code>end</code> to <code>QueryUtil#ALL_POS</code> will return the full result set. If <code>orderByComparator</code> is specified, then the query will include the given ORDER BY logic. If <code>orderByComparator</code> is absent, then the query will include the default ORDER BY logic from <code>LayoutPageTemplateEntryModelImpl</code>.
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
	 * Useful when paginating results. Returns a maximum of <code>end - start</code> instances. <code>start</code> and <code>end</code> are not primary keys, they are indexes in the result set. Thus, <code>0</code> refers to the first result in the set. Setting both <code>start</code> and <code>end</code> to <code>QueryUtil#ALL_POS</code> will return the full result set. If <code>orderByComparator</code> is specified, then the query will include the given ORDER BY logic. If <code>orderByComparator</code> is absent, then the query will include the default ORDER BY logic from <code>LayoutPageTemplateEntryModelImpl</code>.
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
	 * Returns all the layout page template entries where groupId = &#63; and type = &#63; and status = &#63;.
	 *
	 * @param groupId the group ID
	 * @param type the type
	 * @param status the status
	 * @return the matching layout page template entries
	 */
	public java.util.List<LayoutPageTemplateEntry> findByG_T_S(
		long groupId, int type, int status);

	/**
	 * Returns a range of all the layout page template entries where groupId = &#63; and type = &#63; and status = &#63;.
	 *
	 * <p>
	 * Useful when paginating results. Returns a maximum of <code>end - start</code> instances. <code>start</code> and <code>end</code> are not primary keys, they are indexes in the result set. Thus, <code>0</code> refers to the first result in the set. Setting both <code>start</code> and <code>end</code> to <code>QueryUtil#ALL_POS</code> will return the full result set. If <code>orderByComparator</code> is specified, then the query will include the given ORDER BY logic. If <code>orderByComparator</code> is absent, then the query will include the default ORDER BY logic from <code>LayoutPageTemplateEntryModelImpl</code>.
	 * </p>
	 *
	 * @param groupId the group ID
	 * @param type the type
	 * @param status the status
	 * @param start the lower bound of the range of layout page template entries
	 * @param end the upper bound of the range of layout page template entries (not inclusive)
	 * @return the range of matching layout page template entries
	 */
	public java.util.List<LayoutPageTemplateEntry> findByG_T_S(
		long groupId, int type, int status, int start, int end);

	/**
	 * Returns an ordered range of all the layout page template entries where groupId = &#63; and type = &#63; and status = &#63;.
	 *
	 * <p>
	 * Useful when paginating results. Returns a maximum of <code>end - start</code> instances. <code>start</code> and <code>end</code> are not primary keys, they are indexes in the result set. Thus, <code>0</code> refers to the first result in the set. Setting both <code>start</code> and <code>end</code> to <code>QueryUtil#ALL_POS</code> will return the full result set. If <code>orderByComparator</code> is specified, then the query will include the given ORDER BY logic. If <code>orderByComparator</code> is absent, then the query will include the default ORDER BY logic from <code>LayoutPageTemplateEntryModelImpl</code>.
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
	public java.util.List<LayoutPageTemplateEntry> findByG_T_S(
		long groupId, int type, int status, int start, int end,
		com.liferay.portal.kernel.util.OrderByComparator
			<LayoutPageTemplateEntry> orderByComparator);

	/**
	 * Returns an ordered range of all the layout page template entries where groupId = &#63; and type = &#63; and status = &#63;.
	 *
	 * <p>
	 * Useful when paginating results. Returns a maximum of <code>end - start</code> instances. <code>start</code> and <code>end</code> are not primary keys, they are indexes in the result set. Thus, <code>0</code> refers to the first result in the set. Setting both <code>start</code> and <code>end</code> to <code>QueryUtil#ALL_POS</code> will return the full result set. If <code>orderByComparator</code> is specified, then the query will include the given ORDER BY logic. If <code>orderByComparator</code> is absent, then the query will include the default ORDER BY logic from <code>LayoutPageTemplateEntryModelImpl</code>.
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
	 * Returns the last layout page template entry in the ordered set where groupId = &#63; and type = &#63; and status = &#63;.
	 *
	 * @param groupId the group ID
	 * @param type the type
	 * @param status the status
	 * @param orderByComparator the comparator to order the set by (optionally <code>null</code>)
	 * @return the last matching layout page template entry
	 * @throws NoSuchPageTemplateEntryException if a matching layout page template entry could not be found
	 */
	public LayoutPageTemplateEntry findByG_T_S_Last(
			long groupId, int type, int status,
			com.liferay.portal.kernel.util.OrderByComparator
				<LayoutPageTemplateEntry> orderByComparator)
		throws NoSuchPageTemplateEntryException;

	/**
	 * Returns the last layout page template entry in the ordered set where groupId = &#63; and type = &#63; and status = &#63;.
	 *
	 * @param groupId the group ID
	 * @param type the type
	 * @param status the status
	 * @param orderByComparator the comparator to order the set by (optionally <code>null</code>)
	 * @return the last matching layout page template entry, or <code>null</code> if a matching layout page template entry could not be found
	 */
	public LayoutPageTemplateEntry fetchByG_T_S_Last(
		long groupId, int type, int status,
		com.liferay.portal.kernel.util.OrderByComparator
			<LayoutPageTemplateEntry> orderByComparator);

	/**
	 * Returns the layout page template entries before and after the current layout page template entry in the ordered set where groupId = &#63; and type = &#63; and status = &#63;.
	 *
	 * @param layoutPageTemplateEntryId the primary key of the current layout page template entry
	 * @param groupId the group ID
	 * @param type the type
	 * @param status the status
	 * @param orderByComparator the comparator to order the set by (optionally <code>null</code>)
	 * @return the previous, current, and next layout page template entry
	 * @throws NoSuchPageTemplateEntryException if a layout page template entry with the primary key could not be found
	 */
	public LayoutPageTemplateEntry[] findByG_T_S_PrevAndNext(
			long layoutPageTemplateEntryId, long groupId, int type, int status,
			com.liferay.portal.kernel.util.OrderByComparator
				<LayoutPageTemplateEntry> orderByComparator)
		throws NoSuchPageTemplateEntryException;

	/**
	 * Returns all the layout page template entries that the user has permission to view where groupId = &#63; and type = &#63; and status = &#63;.
	 *
	 * @param groupId the group ID
	 * @param type the type
	 * @param status the status
	 * @return the matching layout page template entries that the user has permission to view
	 */
	public java.util.List<LayoutPageTemplateEntry> filterFindByG_T_S(
		long groupId, int type, int status);

	/**
	 * Returns a range of all the layout page template entries that the user has permission to view where groupId = &#63; and type = &#63; and status = &#63;.
	 *
	 * <p>
	 * Useful when paginating results. Returns a maximum of <code>end - start</code> instances. <code>start</code> and <code>end</code> are not primary keys, they are indexes in the result set. Thus, <code>0</code> refers to the first result in the set. Setting both <code>start</code> and <code>end</code> to <code>QueryUtil#ALL_POS</code> will return the full result set. If <code>orderByComparator</code> is specified, then the query will include the given ORDER BY logic. If <code>orderByComparator</code> is absent, then the query will include the default ORDER BY logic from <code>LayoutPageTemplateEntryModelImpl</code>.
	 * </p>
	 *
	 * @param groupId the group ID
	 * @param type the type
	 * @param status the status
	 * @param start the lower bound of the range of layout page template entries
	 * @param end the upper bound of the range of layout page template entries (not inclusive)
	 * @return the range of matching layout page template entries that the user has permission to view
	 */
	public java.util.List<LayoutPageTemplateEntry> filterFindByG_T_S(
		long groupId, int type, int status, int start, int end);

	/**
	 * Returns an ordered range of all the layout page template entries that the user has permissions to view where groupId = &#63; and type = &#63; and status = &#63;.
	 *
	 * <p>
	 * Useful when paginating results. Returns a maximum of <code>end - start</code> instances. <code>start</code> and <code>end</code> are not primary keys, they are indexes in the result set. Thus, <code>0</code> refers to the first result in the set. Setting both <code>start</code> and <code>end</code> to <code>QueryUtil#ALL_POS</code> will return the full result set. If <code>orderByComparator</code> is specified, then the query will include the given ORDER BY logic. If <code>orderByComparator</code> is absent, then the query will include the default ORDER BY logic from <code>LayoutPageTemplateEntryModelImpl</code>.
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
	 * Returns the layout page template entries before and after the current layout page template entry in the ordered set of layout page template entries that the user has permission to view where groupId = &#63; and type = &#63; and status = &#63;.
	 *
	 * @param layoutPageTemplateEntryId the primary key of the current layout page template entry
	 * @param groupId the group ID
	 * @param type the type
	 * @param status the status
	 * @param orderByComparator the comparator to order the set by (optionally <code>null</code>)
	 * @return the previous, current, and next layout page template entry
	 * @throws NoSuchPageTemplateEntryException if a layout page template entry with the primary key could not be found
	 */
	public LayoutPageTemplateEntry[] filterFindByG_T_S_PrevAndNext(
			long layoutPageTemplateEntryId, long groupId, int type, int status,
			com.liferay.portal.kernel.util.OrderByComparator
				<LayoutPageTemplateEntry> orderByComparator)
		throws NoSuchPageTemplateEntryException;

	/**
	 * Returns all the layout page template entries that the user has permission to view where groupId = &#63; and type = any &#63; and status = &#63;.
	 *
	 * @param groupId the group ID
	 * @param types the types
	 * @param status the status
	 * @return the matching layout page template entries that the user has permission to view
	 */
	public java.util.List<LayoutPageTemplateEntry> filterFindByG_T_S(
		long groupId, int[] types, int status);

	/**
	 * Returns a range of all the layout page template entries that the user has permission to view where groupId = &#63; and type = any &#63; and status = &#63;.
	 *
	 * <p>
	 * Useful when paginating results. Returns a maximum of <code>end - start</code> instances. <code>start</code> and <code>end</code> are not primary keys, they are indexes in the result set. Thus, <code>0</code> refers to the first result in the set. Setting both <code>start</code> and <code>end</code> to <code>QueryUtil#ALL_POS</code> will return the full result set. If <code>orderByComparator</code> is specified, then the query will include the given ORDER BY logic. If <code>orderByComparator</code> is absent, then the query will include the default ORDER BY logic from <code>LayoutPageTemplateEntryModelImpl</code>.
	 * </p>
	 *
	 * @param groupId the group ID
	 * @param types the types
	 * @param status the status
	 * @param start the lower bound of the range of layout page template entries
	 * @param end the upper bound of the range of layout page template entries (not inclusive)
	 * @return the range of matching layout page template entries that the user has permission to view
	 */
	public java.util.List<LayoutPageTemplateEntry> filterFindByG_T_S(
		long groupId, int[] types, int status, int start, int end);

	/**
	 * Returns an ordered range of all the layout page template entries that the user has permission to view where groupId = &#63; and type = any &#63; and status = &#63;.
	 *
	 * <p>
	 * Useful when paginating results. Returns a maximum of <code>end - start</code> instances. <code>start</code> and <code>end</code> are not primary keys, they are indexes in the result set. Thus, <code>0</code> refers to the first result in the set. Setting both <code>start</code> and <code>end</code> to <code>QueryUtil#ALL_POS</code> will return the full result set. If <code>orderByComparator</code> is specified, then the query will include the given ORDER BY logic. If <code>orderByComparator</code> is absent, then the query will include the default ORDER BY logic from <code>LayoutPageTemplateEntryModelImpl</code>.
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
	 * Returns all the layout page template entries where groupId = &#63; and type = any &#63; and status = &#63;.
	 *
	 * <p>
	 * Useful when paginating results. Returns a maximum of <code>end - start</code> instances. <code>start</code> and <code>end</code> are not primary keys, they are indexes in the result set. Thus, <code>0</code> refers to the first result in the set. Setting both <code>start</code> and <code>end</code> to <code>QueryUtil#ALL_POS</code> will return the full result set. If <code>orderByComparator</code> is specified, then the query will include the given ORDER BY logic. If <code>orderByComparator</code> is absent, then the query will include the default ORDER BY logic from <code>LayoutPageTemplateEntryModelImpl</code>.
	 * </p>
	 *
	 * @param groupId the group ID
	 * @param types the types
	 * @param status the status
	 * @return the matching layout page template entries
	 */
	public java.util.List<LayoutPageTemplateEntry> findByG_T_S(
		long groupId, int[] types, int status);

	/**
	 * Returns a range of all the layout page template entries where groupId = &#63; and type = any &#63; and status = &#63;.
	 *
	 * <p>
	 * Useful when paginating results. Returns a maximum of <code>end - start</code> instances. <code>start</code> and <code>end</code> are not primary keys, they are indexes in the result set. Thus, <code>0</code> refers to the first result in the set. Setting both <code>start</code> and <code>end</code> to <code>QueryUtil#ALL_POS</code> will return the full result set. If <code>orderByComparator</code> is specified, then the query will include the given ORDER BY logic. If <code>orderByComparator</code> is absent, then the query will include the default ORDER BY logic from <code>LayoutPageTemplateEntryModelImpl</code>.
	 * </p>
	 *
	 * @param groupId the group ID
	 * @param types the types
	 * @param status the status
	 * @param start the lower bound of the range of layout page template entries
	 * @param end the upper bound of the range of layout page template entries (not inclusive)
	 * @return the range of matching layout page template entries
	 */
	public java.util.List<LayoutPageTemplateEntry> findByG_T_S(
		long groupId, int[] types, int status, int start, int end);

	/**
	 * Returns an ordered range of all the layout page template entries where groupId = &#63; and type = any &#63; and status = &#63;.
	 *
	 * <p>
	 * Useful when paginating results. Returns a maximum of <code>end - start</code> instances. <code>start</code> and <code>end</code> are not primary keys, they are indexes in the result set. Thus, <code>0</code> refers to the first result in the set. Setting both <code>start</code> and <code>end</code> to <code>QueryUtil#ALL_POS</code> will return the full result set. If <code>orderByComparator</code> is specified, then the query will include the given ORDER BY logic. If <code>orderByComparator</code> is absent, then the query will include the default ORDER BY logic from <code>LayoutPageTemplateEntryModelImpl</code>.
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
	public java.util.List<LayoutPageTemplateEntry> findByG_T_S(
		long groupId, int[] types, int status, int start, int end,
		com.liferay.portal.kernel.util.OrderByComparator
			<LayoutPageTemplateEntry> orderByComparator);

	/**
	 * Returns an ordered range of all the layout page template entries where groupId = &#63; and type = &#63; and status = &#63;, optionally using the finder cache.
	 *
	 * <p>
	 * Useful when paginating results. Returns a maximum of <code>end - start</code> instances. <code>start</code> and <code>end</code> are not primary keys, they are indexes in the result set. Thus, <code>0</code> refers to the first result in the set. Setting both <code>start</code> and <code>end</code> to <code>QueryUtil#ALL_POS</code> will return the full result set. If <code>orderByComparator</code> is specified, then the query will include the given ORDER BY logic. If <code>orderByComparator</code> is absent, then the query will include the default ORDER BY logic from <code>LayoutPageTemplateEntryModelImpl</code>.
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
	 * Returns the layout page template entry where groupId = &#63; and layoutPageTemplateCollectionId = &#63; and name = &#63; and type = &#63; or returns <code>null</code> if it could not be found. Uses the finder cache.
	 *
	 * @param groupId the group ID
	 * @param layoutPageTemplateCollectionId the layout page template collection ID
	 * @param name the name
	 * @param type the type
	 * @return the matching layout page template entry, or <code>null</code> if a matching layout page template entry could not be found
	 */
	public LayoutPageTemplateEntry fetchByG_L_N_T(
		long groupId, long layoutPageTemplateCollectionId, String name,
		int type);

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
	 * Useful when paginating results. Returns a maximum of <code>end - start</code> instances. <code>start</code> and <code>end</code> are not primary keys, they are indexes in the result set. Thus, <code>0</code> refers to the first result in the set. Setting both <code>start</code> and <code>end</code> to <code>QueryUtil#ALL_POS</code> will return the full result set. If <code>orderByComparator</code> is specified, then the query will include the given ORDER BY logic. If <code>orderByComparator</code> is absent, then the query will include the default ORDER BY logic from <code>LayoutPageTemplateEntryModelImpl</code>.
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
	 * Useful when paginating results. Returns a maximum of <code>end - start</code> instances. <code>start</code> and <code>end</code> are not primary keys, they are indexes in the result set. Thus, <code>0</code> refers to the first result in the set. Setting both <code>start</code> and <code>end</code> to <code>QueryUtil#ALL_POS</code> will return the full result set. If <code>orderByComparator</code> is specified, then the query will include the given ORDER BY logic. If <code>orderByComparator</code> is absent, then the query will include the default ORDER BY logic from <code>LayoutPageTemplateEntryModelImpl</code>.
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
	 * Useful when paginating results. Returns a maximum of <code>end - start</code> instances. <code>start</code> and <code>end</code> are not primary keys, they are indexes in the result set. Thus, <code>0</code> refers to the first result in the set. Setting both <code>start</code> and <code>end</code> to <code>QueryUtil#ALL_POS</code> will return the full result set. If <code>orderByComparator</code> is specified, then the query will include the given ORDER BY logic. If <code>orderByComparator</code> is absent, then the query will include the default ORDER BY logic from <code>LayoutPageTemplateEntryModelImpl</code>.
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
	 * Returns the last layout page template entry in the ordered set where groupId = &#63; and layoutPageTemplateCollectionId = &#63; and name LIKE &#63; and status = &#63;.
	 *
	 * @param groupId the group ID
	 * @param layoutPageTemplateCollectionId the layout page template collection ID
	 * @param name the name
	 * @param status the status
	 * @param orderByComparator the comparator to order the set by (optionally <code>null</code>)
	 * @return the last matching layout page template entry
	 * @throws NoSuchPageTemplateEntryException if a matching layout page template entry could not be found
	 */
	public LayoutPageTemplateEntry findByG_L_LikeN_S_Last(
			long groupId, long layoutPageTemplateCollectionId, String name,
			int status,
			com.liferay.portal.kernel.util.OrderByComparator
				<LayoutPageTemplateEntry> orderByComparator)
		throws NoSuchPageTemplateEntryException;

	/**
	 * Returns the last layout page template entry in the ordered set where groupId = &#63; and layoutPageTemplateCollectionId = &#63; and name LIKE &#63; and status = &#63;.
	 *
	 * @param groupId the group ID
	 * @param layoutPageTemplateCollectionId the layout page template collection ID
	 * @param name the name
	 * @param status the status
	 * @param orderByComparator the comparator to order the set by (optionally <code>null</code>)
	 * @return the last matching layout page template entry, or <code>null</code> if a matching layout page template entry could not be found
	 */
	public LayoutPageTemplateEntry fetchByG_L_LikeN_S_Last(
		long groupId, long layoutPageTemplateCollectionId, String name,
		int status,
		com.liferay.portal.kernel.util.OrderByComparator
			<LayoutPageTemplateEntry> orderByComparator);

	/**
	 * Returns the layout page template entries before and after the current layout page template entry in the ordered set where groupId = &#63; and layoutPageTemplateCollectionId = &#63; and name LIKE &#63; and status = &#63;.
	 *
	 * @param layoutPageTemplateEntryId the primary key of the current layout page template entry
	 * @param groupId the group ID
	 * @param layoutPageTemplateCollectionId the layout page template collection ID
	 * @param name the name
	 * @param status the status
	 * @param orderByComparator the comparator to order the set by (optionally <code>null</code>)
	 * @return the previous, current, and next layout page template entry
	 * @throws NoSuchPageTemplateEntryException if a layout page template entry with the primary key could not be found
	 */
	public LayoutPageTemplateEntry[] findByG_L_LikeN_S_PrevAndNext(
			long layoutPageTemplateEntryId, long groupId,
			long layoutPageTemplateCollectionId, String name, int status,
			com.liferay.portal.kernel.util.OrderByComparator
				<LayoutPageTemplateEntry> orderByComparator)
		throws NoSuchPageTemplateEntryException;

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
	 * Useful when paginating results. Returns a maximum of <code>end - start</code> instances. <code>start</code> and <code>end</code> are not primary keys, they are indexes in the result set. Thus, <code>0</code> refers to the first result in the set. Setting both <code>start</code> and <code>end</code> to <code>QueryUtil#ALL_POS</code> will return the full result set. If <code>orderByComparator</code> is specified, then the query will include the given ORDER BY logic. If <code>orderByComparator</code> is absent, then the query will include the default ORDER BY logic from <code>LayoutPageTemplateEntryModelImpl</code>.
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
	 * Useful when paginating results. Returns a maximum of <code>end - start</code> instances. <code>start</code> and <code>end</code> are not primary keys, they are indexes in the result set. Thus, <code>0</code> refers to the first result in the set. Setting both <code>start</code> and <code>end</code> to <code>QueryUtil#ALL_POS</code> will return the full result set. If <code>orderByComparator</code> is specified, then the query will include the given ORDER BY logic. If <code>orderByComparator</code> is absent, then the query will include the default ORDER BY logic from <code>LayoutPageTemplateEntryModelImpl</code>.
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
	 * Returns the layout page template entries before and after the current layout page template entry in the ordered set of layout page template entries that the user has permission to view where groupId = &#63; and layoutPageTemplateCollectionId = &#63; and name LIKE &#63; and status = &#63;.
	 *
	 * @param layoutPageTemplateEntryId the primary key of the current layout page template entry
	 * @param groupId the group ID
	 * @param layoutPageTemplateCollectionId the layout page template collection ID
	 * @param name the name
	 * @param status the status
	 * @param orderByComparator the comparator to order the set by (optionally <code>null</code>)
	 * @return the previous, current, and next layout page template entry
	 * @throws NoSuchPageTemplateEntryException if a layout page template entry with the primary key could not be found
	 */
	public LayoutPageTemplateEntry[] filterFindByG_L_LikeN_S_PrevAndNext(
			long layoutPageTemplateEntryId, long groupId,
			long layoutPageTemplateCollectionId, String name, int status,
			com.liferay.portal.kernel.util.OrderByComparator
				<LayoutPageTemplateEntry> orderByComparator)
		throws NoSuchPageTemplateEntryException;

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
	 * Returns all the layout page template entries where groupId = &#63; and classNameId = &#63; and classTypeKey = &#63; and type = &#63;.
	 *
	 * @param groupId the group ID
	 * @param classNameId the class name ID
	 * @param classTypeKey the class type key
	 * @param type the type
	 * @return the matching layout page template entries
	 */
	public java.util.List<LayoutPageTemplateEntry> findByG_C_C_T(
		long groupId, long classNameId, String classTypeKey, int type);

	/**
	 * Returns a range of all the layout page template entries where groupId = &#63; and classNameId = &#63; and classTypeKey = &#63; and type = &#63;.
	 *
	 * <p>
	 * Useful when paginating results. Returns a maximum of <code>end - start</code> instances. <code>start</code> and <code>end</code> are not primary keys, they are indexes in the result set. Thus, <code>0</code> refers to the first result in the set. Setting both <code>start</code> and <code>end</code> to <code>QueryUtil#ALL_POS</code> will return the full result set. If <code>orderByComparator</code> is specified, then the query will include the given ORDER BY logic. If <code>orderByComparator</code> is absent, then the query will include the default ORDER BY logic from <code>LayoutPageTemplateEntryModelImpl</code>.
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
	public java.util.List<LayoutPageTemplateEntry> findByG_C_C_T(
		long groupId, long classNameId, String classTypeKey, int type,
		int start, int end);

	/**
	 * Returns an ordered range of all the layout page template entries where groupId = &#63; and classNameId = &#63; and classTypeKey = &#63; and type = &#63;.
	 *
	 * <p>
	 * Useful when paginating results. Returns a maximum of <code>end - start</code> instances. <code>start</code> and <code>end</code> are not primary keys, they are indexes in the result set. Thus, <code>0</code> refers to the first result in the set. Setting both <code>start</code> and <code>end</code> to <code>QueryUtil#ALL_POS</code> will return the full result set. If <code>orderByComparator</code> is specified, then the query will include the given ORDER BY logic. If <code>orderByComparator</code> is absent, then the query will include the default ORDER BY logic from <code>LayoutPageTemplateEntryModelImpl</code>.
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
	public java.util.List<LayoutPageTemplateEntry> findByG_C_C_T(
		long groupId, long classNameId, String classTypeKey, int type,
		int start, int end,
		com.liferay.portal.kernel.util.OrderByComparator
			<LayoutPageTemplateEntry> orderByComparator);

	/**
	 * Returns an ordered range of all the layout page template entries where groupId = &#63; and classNameId = &#63; and classTypeKey = &#63; and type = &#63;.
	 *
	 * <p>
	 * Useful when paginating results. Returns a maximum of <code>end - start</code> instances. <code>start</code> and <code>end</code> are not primary keys, they are indexes in the result set. Thus, <code>0</code> refers to the first result in the set. Setting both <code>start</code> and <code>end</code> to <code>QueryUtil#ALL_POS</code> will return the full result set. If <code>orderByComparator</code> is specified, then the query will include the given ORDER BY logic. If <code>orderByComparator</code> is absent, then the query will include the default ORDER BY logic from <code>LayoutPageTemplateEntryModelImpl</code>.
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
	 * Returns the last layout page template entry in the ordered set where groupId = &#63; and classNameId = &#63; and classTypeKey = &#63; and type = &#63;.
	 *
	 * @param groupId the group ID
	 * @param classNameId the class name ID
	 * @param classTypeKey the class type key
	 * @param type the type
	 * @param orderByComparator the comparator to order the set by (optionally <code>null</code>)
	 * @return the last matching layout page template entry
	 * @throws NoSuchPageTemplateEntryException if a matching layout page template entry could not be found
	 */
	public LayoutPageTemplateEntry findByG_C_C_T_Last(
			long groupId, long classNameId, String classTypeKey, int type,
			com.liferay.portal.kernel.util.OrderByComparator
				<LayoutPageTemplateEntry> orderByComparator)
		throws NoSuchPageTemplateEntryException;

	/**
	 * Returns the last layout page template entry in the ordered set where groupId = &#63; and classNameId = &#63; and classTypeKey = &#63; and type = &#63;.
	 *
	 * @param groupId the group ID
	 * @param classNameId the class name ID
	 * @param classTypeKey the class type key
	 * @param type the type
	 * @param orderByComparator the comparator to order the set by (optionally <code>null</code>)
	 * @return the last matching layout page template entry, or <code>null</code> if a matching layout page template entry could not be found
	 */
	public LayoutPageTemplateEntry fetchByG_C_C_T_Last(
		long groupId, long classNameId, String classTypeKey, int type,
		com.liferay.portal.kernel.util.OrderByComparator
			<LayoutPageTemplateEntry> orderByComparator);

	/**
	 * Returns the layout page template entries before and after the current layout page template entry in the ordered set where groupId = &#63; and classNameId = &#63; and classTypeKey = &#63; and type = &#63;.
	 *
	 * @param layoutPageTemplateEntryId the primary key of the current layout page template entry
	 * @param groupId the group ID
	 * @param classNameId the class name ID
	 * @param classTypeKey the class type key
	 * @param type the type
	 * @param orderByComparator the comparator to order the set by (optionally <code>null</code>)
	 * @return the previous, current, and next layout page template entry
	 * @throws NoSuchPageTemplateEntryException if a layout page template entry with the primary key could not be found
	 */
	public LayoutPageTemplateEntry[] findByG_C_C_T_PrevAndNext(
			long layoutPageTemplateEntryId, long groupId, long classNameId,
			String classTypeKey, int type,
			com.liferay.portal.kernel.util.OrderByComparator
				<LayoutPageTemplateEntry> orderByComparator)
		throws NoSuchPageTemplateEntryException;

	/**
	 * Returns all the layout page template entries that the user has permission to view where groupId = &#63; and classNameId = &#63; and classTypeKey = &#63; and type = &#63;.
	 *
	 * @param groupId the group ID
	 * @param classNameId the class name ID
	 * @param classTypeKey the class type key
	 * @param type the type
	 * @return the matching layout page template entries that the user has permission to view
	 */
	public java.util.List<LayoutPageTemplateEntry> filterFindByG_C_C_T(
		long groupId, long classNameId, String classTypeKey, int type);

	/**
	 * Returns a range of all the layout page template entries that the user has permission to view where groupId = &#63; and classNameId = &#63; and classTypeKey = &#63; and type = &#63;.
	 *
	 * <p>
	 * Useful when paginating results. Returns a maximum of <code>end - start</code> instances. <code>start</code> and <code>end</code> are not primary keys, they are indexes in the result set. Thus, <code>0</code> refers to the first result in the set. Setting both <code>start</code> and <code>end</code> to <code>QueryUtil#ALL_POS</code> will return the full result set. If <code>orderByComparator</code> is specified, then the query will include the given ORDER BY logic. If <code>orderByComparator</code> is absent, then the query will include the default ORDER BY logic from <code>LayoutPageTemplateEntryModelImpl</code>.
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
	public java.util.List<LayoutPageTemplateEntry> filterFindByG_C_C_T(
		long groupId, long classNameId, String classTypeKey, int type,
		int start, int end);

	/**
	 * Returns an ordered range of all the layout page template entries that the user has permissions to view where groupId = &#63; and classNameId = &#63; and classTypeKey = &#63; and type = &#63;.
	 *
	 * <p>
	 * Useful when paginating results. Returns a maximum of <code>end - start</code> instances. <code>start</code> and <code>end</code> are not primary keys, they are indexes in the result set. Thus, <code>0</code> refers to the first result in the set. Setting both <code>start</code> and <code>end</code> to <code>QueryUtil#ALL_POS</code> will return the full result set. If <code>orderByComparator</code> is specified, then the query will include the given ORDER BY logic. If <code>orderByComparator</code> is absent, then the query will include the default ORDER BY logic from <code>LayoutPageTemplateEntryModelImpl</code>.
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
	 * Returns the layout page template entries before and after the current layout page template entry in the ordered set of layout page template entries that the user has permission to view where groupId = &#63; and classNameId = &#63; and classTypeKey = &#63; and type = &#63;.
	 *
	 * @param layoutPageTemplateEntryId the primary key of the current layout page template entry
	 * @param groupId the group ID
	 * @param classNameId the class name ID
	 * @param classTypeKey the class type key
	 * @param type the type
	 * @param orderByComparator the comparator to order the set by (optionally <code>null</code>)
	 * @return the previous, current, and next layout page template entry
	 * @throws NoSuchPageTemplateEntryException if a layout page template entry with the primary key could not be found
	 */
	public LayoutPageTemplateEntry[] filterFindByG_C_C_T_PrevAndNext(
			long layoutPageTemplateEntryId, long groupId, long classNameId,
			String classTypeKey, int type,
			com.liferay.portal.kernel.util.OrderByComparator
				<LayoutPageTemplateEntry> orderByComparator)
		throws NoSuchPageTemplateEntryException;

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
	 * Returns all the layout page template entries where groupId = &#63; and classNameId = &#63; and classTypeKey = &#63; and defaultTemplate = &#63;.
	 *
	 * @param groupId the group ID
	 * @param classNameId the class name ID
	 * @param classTypeKey the class type key
	 * @param defaultTemplate the default template
	 * @return the matching layout page template entries
	 */
	public java.util.List<LayoutPageTemplateEntry> findByG_C_C_D(
		long groupId, long classNameId, String classTypeKey,
		boolean defaultTemplate);

	/**
	 * Returns a range of all the layout page template entries where groupId = &#63; and classNameId = &#63; and classTypeKey = &#63; and defaultTemplate = &#63;.
	 *
	 * <p>
	 * Useful when paginating results. Returns a maximum of <code>end - start</code> instances. <code>start</code> and <code>end</code> are not primary keys, they are indexes in the result set. Thus, <code>0</code> refers to the first result in the set. Setting both <code>start</code> and <code>end</code> to <code>QueryUtil#ALL_POS</code> will return the full result set. If <code>orderByComparator</code> is specified, then the query will include the given ORDER BY logic. If <code>orderByComparator</code> is absent, then the query will include the default ORDER BY logic from <code>LayoutPageTemplateEntryModelImpl</code>.
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
	public java.util.List<LayoutPageTemplateEntry> findByG_C_C_D(
		long groupId, long classNameId, String classTypeKey,
		boolean defaultTemplate, int start, int end);

	/**
	 * Returns an ordered range of all the layout page template entries where groupId = &#63; and classNameId = &#63; and classTypeKey = &#63; and defaultTemplate = &#63;.
	 *
	 * <p>
	 * Useful when paginating results. Returns a maximum of <code>end - start</code> instances. <code>start</code> and <code>end</code> are not primary keys, they are indexes in the result set. Thus, <code>0</code> refers to the first result in the set. Setting both <code>start</code> and <code>end</code> to <code>QueryUtil#ALL_POS</code> will return the full result set. If <code>orderByComparator</code> is specified, then the query will include the given ORDER BY logic. If <code>orderByComparator</code> is absent, then the query will include the default ORDER BY logic from <code>LayoutPageTemplateEntryModelImpl</code>.
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
	public java.util.List<LayoutPageTemplateEntry> findByG_C_C_D(
		long groupId, long classNameId, String classTypeKey,
		boolean defaultTemplate, int start, int end,
		com.liferay.portal.kernel.util.OrderByComparator
			<LayoutPageTemplateEntry> orderByComparator);

	/**
	 * Returns an ordered range of all the layout page template entries where groupId = &#63; and classNameId = &#63; and classTypeKey = &#63; and defaultTemplate = &#63;.
	 *
	 * <p>
	 * Useful when paginating results. Returns a maximum of <code>end - start</code> instances. <code>start</code> and <code>end</code> are not primary keys, they are indexes in the result set. Thus, <code>0</code> refers to the first result in the set. Setting both <code>start</code> and <code>end</code> to <code>QueryUtil#ALL_POS</code> will return the full result set. If <code>orderByComparator</code> is specified, then the query will include the given ORDER BY logic. If <code>orderByComparator</code> is absent, then the query will include the default ORDER BY logic from <code>LayoutPageTemplateEntryModelImpl</code>.
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
	 * Returns the last layout page template entry in the ordered set where groupId = &#63; and classNameId = &#63; and classTypeKey = &#63; and defaultTemplate = &#63;.
	 *
	 * @param groupId the group ID
	 * @param classNameId the class name ID
	 * @param classTypeKey the class type key
	 * @param defaultTemplate the default template
	 * @param orderByComparator the comparator to order the set by (optionally <code>null</code>)
	 * @return the last matching layout page template entry
	 * @throws NoSuchPageTemplateEntryException if a matching layout page template entry could not be found
	 */
	public LayoutPageTemplateEntry findByG_C_C_D_Last(
			long groupId, long classNameId, String classTypeKey,
			boolean defaultTemplate,
			com.liferay.portal.kernel.util.OrderByComparator
				<LayoutPageTemplateEntry> orderByComparator)
		throws NoSuchPageTemplateEntryException;

	/**
	 * Returns the last layout page template entry in the ordered set where groupId = &#63; and classNameId = &#63; and classTypeKey = &#63; and defaultTemplate = &#63;.
	 *
	 * @param groupId the group ID
	 * @param classNameId the class name ID
	 * @param classTypeKey the class type key
	 * @param defaultTemplate the default template
	 * @param orderByComparator the comparator to order the set by (optionally <code>null</code>)
	 * @return the last matching layout page template entry, or <code>null</code> if a matching layout page template entry could not be found
	 */
	public LayoutPageTemplateEntry fetchByG_C_C_D_Last(
		long groupId, long classNameId, String classTypeKey,
		boolean defaultTemplate,
		com.liferay.portal.kernel.util.OrderByComparator
			<LayoutPageTemplateEntry> orderByComparator);

	/**
	 * Returns the layout page template entries before and after the current layout page template entry in the ordered set where groupId = &#63; and classNameId = &#63; and classTypeKey = &#63; and defaultTemplate = &#63;.
	 *
	 * @param layoutPageTemplateEntryId the primary key of the current layout page template entry
	 * @param groupId the group ID
	 * @param classNameId the class name ID
	 * @param classTypeKey the class type key
	 * @param defaultTemplate the default template
	 * @param orderByComparator the comparator to order the set by (optionally <code>null</code>)
	 * @return the previous, current, and next layout page template entry
	 * @throws NoSuchPageTemplateEntryException if a layout page template entry with the primary key could not be found
	 */
	public LayoutPageTemplateEntry[] findByG_C_C_D_PrevAndNext(
			long layoutPageTemplateEntryId, long groupId, long classNameId,
			String classTypeKey, boolean defaultTemplate,
			com.liferay.portal.kernel.util.OrderByComparator
				<LayoutPageTemplateEntry> orderByComparator)
		throws NoSuchPageTemplateEntryException;

	/**
	 * Returns all the layout page template entries that the user has permission to view where groupId = &#63; and classNameId = &#63; and classTypeKey = &#63; and defaultTemplate = &#63;.
	 *
	 * @param groupId the group ID
	 * @param classNameId the class name ID
	 * @param classTypeKey the class type key
	 * @param defaultTemplate the default template
	 * @return the matching layout page template entries that the user has permission to view
	 */
	public java.util.List<LayoutPageTemplateEntry> filterFindByG_C_C_D(
		long groupId, long classNameId, String classTypeKey,
		boolean defaultTemplate);

	/**
	 * Returns a range of all the layout page template entries that the user has permission to view where groupId = &#63; and classNameId = &#63; and classTypeKey = &#63; and defaultTemplate = &#63;.
	 *
	 * <p>
	 * Useful when paginating results. Returns a maximum of <code>end - start</code> instances. <code>start</code> and <code>end</code> are not primary keys, they are indexes in the result set. Thus, <code>0</code> refers to the first result in the set. Setting both <code>start</code> and <code>end</code> to <code>QueryUtil#ALL_POS</code> will return the full result set. If <code>orderByComparator</code> is specified, then the query will include the given ORDER BY logic. If <code>orderByComparator</code> is absent, then the query will include the default ORDER BY logic from <code>LayoutPageTemplateEntryModelImpl</code>.
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
	public java.util.List<LayoutPageTemplateEntry> filterFindByG_C_C_D(
		long groupId, long classNameId, String classTypeKey,
		boolean defaultTemplate, int start, int end);

	/**
	 * Returns an ordered range of all the layout page template entries that the user has permissions to view where groupId = &#63; and classNameId = &#63; and classTypeKey = &#63; and defaultTemplate = &#63;.
	 *
	 * <p>
	 * Useful when paginating results. Returns a maximum of <code>end - start</code> instances. <code>start</code> and <code>end</code> are not primary keys, they are indexes in the result set. Thus, <code>0</code> refers to the first result in the set. Setting both <code>start</code> and <code>end</code> to <code>QueryUtil#ALL_POS</code> will return the full result set. If <code>orderByComparator</code> is specified, then the query will include the given ORDER BY logic. If <code>orderByComparator</code> is absent, then the query will include the default ORDER BY logic from <code>LayoutPageTemplateEntryModelImpl</code>.
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
	 * Returns the layout page template entries before and after the current layout page template entry in the ordered set of layout page template entries that the user has permission to view where groupId = &#63; and classNameId = &#63; and classTypeKey = &#63; and defaultTemplate = &#63;.
	 *
	 * @param layoutPageTemplateEntryId the primary key of the current layout page template entry
	 * @param groupId the group ID
	 * @param classNameId the class name ID
	 * @param classTypeKey the class type key
	 * @param defaultTemplate the default template
	 * @param orderByComparator the comparator to order the set by (optionally <code>null</code>)
	 * @return the previous, current, and next layout page template entry
	 * @throws NoSuchPageTemplateEntryException if a layout page template entry with the primary key could not be found
	 */
	public LayoutPageTemplateEntry[] filterFindByG_C_C_D_PrevAndNext(
			long layoutPageTemplateEntryId, long groupId, long classNameId,
			String classTypeKey, boolean defaultTemplate,
			com.liferay.portal.kernel.util.OrderByComparator
				<LayoutPageTemplateEntry> orderByComparator)
		throws NoSuchPageTemplateEntryException;

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
	 * Returns all the layout page template entries where groupId = &#63; and classNameId = &#63; and type = &#63; and defaultTemplate = &#63;.
	 *
	 * @param groupId the group ID
	 * @param classNameId the class name ID
	 * @param type the type
	 * @param defaultTemplate the default template
	 * @return the matching layout page template entries
	 */
	public java.util.List<LayoutPageTemplateEntry> findByG_C_T_D(
		long groupId, long classNameId, int type, boolean defaultTemplate);

	/**
	 * Returns a range of all the layout page template entries where groupId = &#63; and classNameId = &#63; and type = &#63; and defaultTemplate = &#63;.
	 *
	 * <p>
	 * Useful when paginating results. Returns a maximum of <code>end - start</code> instances. <code>start</code> and <code>end</code> are not primary keys, they are indexes in the result set. Thus, <code>0</code> refers to the first result in the set. Setting both <code>start</code> and <code>end</code> to <code>QueryUtil#ALL_POS</code> will return the full result set. If <code>orderByComparator</code> is specified, then the query will include the given ORDER BY logic. If <code>orderByComparator</code> is absent, then the query will include the default ORDER BY logic from <code>LayoutPageTemplateEntryModelImpl</code>.
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
	public java.util.List<LayoutPageTemplateEntry> findByG_C_T_D(
		long groupId, long classNameId, int type, boolean defaultTemplate,
		int start, int end);

	/**
	 * Returns an ordered range of all the layout page template entries where groupId = &#63; and classNameId = &#63; and type = &#63; and defaultTemplate = &#63;.
	 *
	 * <p>
	 * Useful when paginating results. Returns a maximum of <code>end - start</code> instances. <code>start</code> and <code>end</code> are not primary keys, they are indexes in the result set. Thus, <code>0</code> refers to the first result in the set. Setting both <code>start</code> and <code>end</code> to <code>QueryUtil#ALL_POS</code> will return the full result set. If <code>orderByComparator</code> is specified, then the query will include the given ORDER BY logic. If <code>orderByComparator</code> is absent, then the query will include the default ORDER BY logic from <code>LayoutPageTemplateEntryModelImpl</code>.
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
	public java.util.List<LayoutPageTemplateEntry> findByG_C_T_D(
		long groupId, long classNameId, int type, boolean defaultTemplate,
		int start, int end,
		com.liferay.portal.kernel.util.OrderByComparator
			<LayoutPageTemplateEntry> orderByComparator);

	/**
	 * Returns an ordered range of all the layout page template entries where groupId = &#63; and classNameId = &#63; and type = &#63; and defaultTemplate = &#63;.
	 *
	 * <p>
	 * Useful when paginating results. Returns a maximum of <code>end - start</code> instances. <code>start</code> and <code>end</code> are not primary keys, they are indexes in the result set. Thus, <code>0</code> refers to the first result in the set. Setting both <code>start</code> and <code>end</code> to <code>QueryUtil#ALL_POS</code> will return the full result set. If <code>orderByComparator</code> is specified, then the query will include the given ORDER BY logic. If <code>orderByComparator</code> is absent, then the query will include the default ORDER BY logic from <code>LayoutPageTemplateEntryModelImpl</code>.
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
	 * Returns the last layout page template entry in the ordered set where groupId = &#63; and classNameId = &#63; and type = &#63; and defaultTemplate = &#63;.
	 *
	 * @param groupId the group ID
	 * @param classNameId the class name ID
	 * @param type the type
	 * @param defaultTemplate the default template
	 * @param orderByComparator the comparator to order the set by (optionally <code>null</code>)
	 * @return the last matching layout page template entry
	 * @throws NoSuchPageTemplateEntryException if a matching layout page template entry could not be found
	 */
	public LayoutPageTemplateEntry findByG_C_T_D_Last(
			long groupId, long classNameId, int type, boolean defaultTemplate,
			com.liferay.portal.kernel.util.OrderByComparator
				<LayoutPageTemplateEntry> orderByComparator)
		throws NoSuchPageTemplateEntryException;

	/**
	 * Returns the last layout page template entry in the ordered set where groupId = &#63; and classNameId = &#63; and type = &#63; and defaultTemplate = &#63;.
	 *
	 * @param groupId the group ID
	 * @param classNameId the class name ID
	 * @param type the type
	 * @param defaultTemplate the default template
	 * @param orderByComparator the comparator to order the set by (optionally <code>null</code>)
	 * @return the last matching layout page template entry, or <code>null</code> if a matching layout page template entry could not be found
	 */
	public LayoutPageTemplateEntry fetchByG_C_T_D_Last(
		long groupId, long classNameId, int type, boolean defaultTemplate,
		com.liferay.portal.kernel.util.OrderByComparator
			<LayoutPageTemplateEntry> orderByComparator);

	/**
	 * Returns the layout page template entries before and after the current layout page template entry in the ordered set where groupId = &#63; and classNameId = &#63; and type = &#63; and defaultTemplate = &#63;.
	 *
	 * @param layoutPageTemplateEntryId the primary key of the current layout page template entry
	 * @param groupId the group ID
	 * @param classNameId the class name ID
	 * @param type the type
	 * @param defaultTemplate the default template
	 * @param orderByComparator the comparator to order the set by (optionally <code>null</code>)
	 * @return the previous, current, and next layout page template entry
	 * @throws NoSuchPageTemplateEntryException if a layout page template entry with the primary key could not be found
	 */
	public LayoutPageTemplateEntry[] findByG_C_T_D_PrevAndNext(
			long layoutPageTemplateEntryId, long groupId, long classNameId,
			int type, boolean defaultTemplate,
			com.liferay.portal.kernel.util.OrderByComparator
				<LayoutPageTemplateEntry> orderByComparator)
		throws NoSuchPageTemplateEntryException;

	/**
	 * Returns all the layout page template entries that the user has permission to view where groupId = &#63; and classNameId = &#63; and type = &#63; and defaultTemplate = &#63;.
	 *
	 * @param groupId the group ID
	 * @param classNameId the class name ID
	 * @param type the type
	 * @param defaultTemplate the default template
	 * @return the matching layout page template entries that the user has permission to view
	 */
	public java.util.List<LayoutPageTemplateEntry> filterFindByG_C_T_D(
		long groupId, long classNameId, int type, boolean defaultTemplate);

	/**
	 * Returns a range of all the layout page template entries that the user has permission to view where groupId = &#63; and classNameId = &#63; and type = &#63; and defaultTemplate = &#63;.
	 *
	 * <p>
	 * Useful when paginating results. Returns a maximum of <code>end - start</code> instances. <code>start</code> and <code>end</code> are not primary keys, they are indexes in the result set. Thus, <code>0</code> refers to the first result in the set. Setting both <code>start</code> and <code>end</code> to <code>QueryUtil#ALL_POS</code> will return the full result set. If <code>orderByComparator</code> is specified, then the query will include the given ORDER BY logic. If <code>orderByComparator</code> is absent, then the query will include the default ORDER BY logic from <code>LayoutPageTemplateEntryModelImpl</code>.
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
	public java.util.List<LayoutPageTemplateEntry> filterFindByG_C_T_D(
		long groupId, long classNameId, int type, boolean defaultTemplate,
		int start, int end);

	/**
	 * Returns an ordered range of all the layout page template entries that the user has permissions to view where groupId = &#63; and classNameId = &#63; and type = &#63; and defaultTemplate = &#63;.
	 *
	 * <p>
	 * Useful when paginating results. Returns a maximum of <code>end - start</code> instances. <code>start</code> and <code>end</code> are not primary keys, they are indexes in the result set. Thus, <code>0</code> refers to the first result in the set. Setting both <code>start</code> and <code>end</code> to <code>QueryUtil#ALL_POS</code> will return the full result set. If <code>orderByComparator</code> is specified, then the query will include the given ORDER BY logic. If <code>orderByComparator</code> is absent, then the query will include the default ORDER BY logic from <code>LayoutPageTemplateEntryModelImpl</code>.
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
	 * Returns the layout page template entries before and after the current layout page template entry in the ordered set of layout page template entries that the user has permission to view where groupId = &#63; and classNameId = &#63; and type = &#63; and defaultTemplate = &#63;.
	 *
	 * @param layoutPageTemplateEntryId the primary key of the current layout page template entry
	 * @param groupId the group ID
	 * @param classNameId the class name ID
	 * @param type the type
	 * @param defaultTemplate the default template
	 * @param orderByComparator the comparator to order the set by (optionally <code>null</code>)
	 * @return the previous, current, and next layout page template entry
	 * @throws NoSuchPageTemplateEntryException if a layout page template entry with the primary key could not be found
	 */
	public LayoutPageTemplateEntry[] filterFindByG_C_T_D_PrevAndNext(
			long layoutPageTemplateEntryId, long groupId, long classNameId,
			int type, boolean defaultTemplate,
			com.liferay.portal.kernel.util.OrderByComparator
				<LayoutPageTemplateEntry> orderByComparator)
		throws NoSuchPageTemplateEntryException;

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
	 * Useful when paginating results. Returns a maximum of <code>end - start</code> instances. <code>start</code> and <code>end</code> are not primary keys, they are indexes in the result set. Thus, <code>0</code> refers to the first result in the set. Setting both <code>start</code> and <code>end</code> to <code>QueryUtil#ALL_POS</code> will return the full result set. If <code>orderByComparator</code> is specified, then the query will include the given ORDER BY logic. If <code>orderByComparator</code> is absent, then the query will include the default ORDER BY logic from <code>LayoutPageTemplateEntryModelImpl</code>.
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
	 * Useful when paginating results. Returns a maximum of <code>end - start</code> instances. <code>start</code> and <code>end</code> are not primary keys, they are indexes in the result set. Thus, <code>0</code> refers to the first result in the set. Setting both <code>start</code> and <code>end</code> to <code>QueryUtil#ALL_POS</code> will return the full result set. If <code>orderByComparator</code> is specified, then the query will include the given ORDER BY logic. If <code>orderByComparator</code> is absent, then the query will include the default ORDER BY logic from <code>LayoutPageTemplateEntryModelImpl</code>.
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
	 * Useful when paginating results. Returns a maximum of <code>end - start</code> instances. <code>start</code> and <code>end</code> are not primary keys, they are indexes in the result set. Thus, <code>0</code> refers to the first result in the set. Setting both <code>start</code> and <code>end</code> to <code>QueryUtil#ALL_POS</code> will return the full result set. If <code>orderByComparator</code> is specified, then the query will include the given ORDER BY logic. If <code>orderByComparator</code> is absent, then the query will include the default ORDER BY logic from <code>LayoutPageTemplateEntryModelImpl</code>.
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
	 * Returns the last layout page template entry in the ordered set where groupId = &#63; and name LIKE &#63; and type = &#63; and status = &#63;.
	 *
	 * @param groupId the group ID
	 * @param name the name
	 * @param type the type
	 * @param status the status
	 * @param orderByComparator the comparator to order the set by (optionally <code>null</code>)
	 * @return the last matching layout page template entry
	 * @throws NoSuchPageTemplateEntryException if a matching layout page template entry could not be found
	 */
	public LayoutPageTemplateEntry findByG_T_LikeN_S_Last(
			long groupId, String name, int type, int status,
			com.liferay.portal.kernel.util.OrderByComparator
				<LayoutPageTemplateEntry> orderByComparator)
		throws NoSuchPageTemplateEntryException;

	/**
	 * Returns the last layout page template entry in the ordered set where groupId = &#63; and name LIKE &#63; and type = &#63; and status = &#63;.
	 *
	 * @param groupId the group ID
	 * @param name the name
	 * @param type the type
	 * @param status the status
	 * @param orderByComparator the comparator to order the set by (optionally <code>null</code>)
	 * @return the last matching layout page template entry, or <code>null</code> if a matching layout page template entry could not be found
	 */
	public LayoutPageTemplateEntry fetchByG_T_LikeN_S_Last(
		long groupId, String name, int type, int status,
		com.liferay.portal.kernel.util.OrderByComparator
			<LayoutPageTemplateEntry> orderByComparator);

	/**
	 * Returns the layout page template entries before and after the current layout page template entry in the ordered set where groupId = &#63; and name LIKE &#63; and type = &#63; and status = &#63;.
	 *
	 * @param layoutPageTemplateEntryId the primary key of the current layout page template entry
	 * @param groupId the group ID
	 * @param name the name
	 * @param type the type
	 * @param status the status
	 * @param orderByComparator the comparator to order the set by (optionally <code>null</code>)
	 * @return the previous, current, and next layout page template entry
	 * @throws NoSuchPageTemplateEntryException if a layout page template entry with the primary key could not be found
	 */
	public LayoutPageTemplateEntry[] findByG_T_LikeN_S_PrevAndNext(
			long layoutPageTemplateEntryId, long groupId, String name, int type,
			int status,
			com.liferay.portal.kernel.util.OrderByComparator
				<LayoutPageTemplateEntry> orderByComparator)
		throws NoSuchPageTemplateEntryException;

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
	 * Useful when paginating results. Returns a maximum of <code>end - start</code> instances. <code>start</code> and <code>end</code> are not primary keys, they are indexes in the result set. Thus, <code>0</code> refers to the first result in the set. Setting both <code>start</code> and <code>end</code> to <code>QueryUtil#ALL_POS</code> will return the full result set. If <code>orderByComparator</code> is specified, then the query will include the given ORDER BY logic. If <code>orderByComparator</code> is absent, then the query will include the default ORDER BY logic from <code>LayoutPageTemplateEntryModelImpl</code>.
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
	 * Useful when paginating results. Returns a maximum of <code>end - start</code> instances. <code>start</code> and <code>end</code> are not primary keys, they are indexes in the result set. Thus, <code>0</code> refers to the first result in the set. Setting both <code>start</code> and <code>end</code> to <code>QueryUtil#ALL_POS</code> will return the full result set. If <code>orderByComparator</code> is specified, then the query will include the given ORDER BY logic. If <code>orderByComparator</code> is absent, then the query will include the default ORDER BY logic from <code>LayoutPageTemplateEntryModelImpl</code>.
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
	 * Returns the layout page template entries before and after the current layout page template entry in the ordered set of layout page template entries that the user has permission to view where groupId = &#63; and name LIKE &#63; and type = &#63; and status = &#63;.
	 *
	 * @param layoutPageTemplateEntryId the primary key of the current layout page template entry
	 * @param groupId the group ID
	 * @param name the name
	 * @param type the type
	 * @param status the status
	 * @param orderByComparator the comparator to order the set by (optionally <code>null</code>)
	 * @return the previous, current, and next layout page template entry
	 * @throws NoSuchPageTemplateEntryException if a layout page template entry with the primary key could not be found
	 */
	public LayoutPageTemplateEntry[] filterFindByG_T_LikeN_S_PrevAndNext(
			long layoutPageTemplateEntryId, long groupId, String name, int type,
			int status,
			com.liferay.portal.kernel.util.OrderByComparator
				<LayoutPageTemplateEntry> orderByComparator)
		throws NoSuchPageTemplateEntryException;

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
	 * Useful when paginating results. Returns a maximum of <code>end - start</code> instances. <code>start</code> and <code>end</code> are not primary keys, they are indexes in the result set. Thus, <code>0</code> refers to the first result in the set. Setting both <code>start</code> and <code>end</code> to <code>QueryUtil#ALL_POS</code> will return the full result set. If <code>orderByComparator</code> is specified, then the query will include the given ORDER BY logic. If <code>orderByComparator</code> is absent, then the query will include the default ORDER BY logic from <code>LayoutPageTemplateEntryModelImpl</code>.
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
	 * Useful when paginating results. Returns a maximum of <code>end - start</code> instances. <code>start</code> and <code>end</code> are not primary keys, they are indexes in the result set. Thus, <code>0</code> refers to the first result in the set. Setting both <code>start</code> and <code>end</code> to <code>QueryUtil#ALL_POS</code> will return the full result set. If <code>orderByComparator</code> is specified, then the query will include the given ORDER BY logic. If <code>orderByComparator</code> is absent, then the query will include the default ORDER BY logic from <code>LayoutPageTemplateEntryModelImpl</code>.
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
	 * Useful when paginating results. Returns a maximum of <code>end - start</code> instances. <code>start</code> and <code>end</code> are not primary keys, they are indexes in the result set. Thus, <code>0</code> refers to the first result in the set. Setting both <code>start</code> and <code>end</code> to <code>QueryUtil#ALL_POS</code> will return the full result set. If <code>orderByComparator</code> is specified, then the query will include the given ORDER BY logic. If <code>orderByComparator</code> is absent, then the query will include the default ORDER BY logic from <code>LayoutPageTemplateEntryModelImpl</code>.
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
	 * Useful when paginating results. Returns a maximum of <code>end - start</code> instances. <code>start</code> and <code>end</code> are not primary keys, they are indexes in the result set. Thus, <code>0</code> refers to the first result in the set. Setting both <code>start</code> and <code>end</code> to <code>QueryUtil#ALL_POS</code> will return the full result set. If <code>orderByComparator</code> is specified, then the query will include the given ORDER BY logic. If <code>orderByComparator</code> is absent, then the query will include the default ORDER BY logic from <code>LayoutPageTemplateEntryModelImpl</code>.
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
	 * Useful when paginating results. Returns a maximum of <code>end - start</code> instances. <code>start</code> and <code>end</code> are not primary keys, they are indexes in the result set. Thus, <code>0</code> refers to the first result in the set. Setting both <code>start</code> and <code>end</code> to <code>QueryUtil#ALL_POS</code> will return the full result set. If <code>orderByComparator</code> is specified, then the query will include the given ORDER BY logic. If <code>orderByComparator</code> is absent, then the query will include the default ORDER BY logic from <code>LayoutPageTemplateEntryModelImpl</code>.
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
	 * Useful when paginating results. Returns a maximum of <code>end - start</code> instances. <code>start</code> and <code>end</code> are not primary keys, they are indexes in the result set. Thus, <code>0</code> refers to the first result in the set. Setting both <code>start</code> and <code>end</code> to <code>QueryUtil#ALL_POS</code> will return the full result set. If <code>orderByComparator</code> is specified, then the query will include the given ORDER BY logic. If <code>orderByComparator</code> is absent, then the query will include the default ORDER BY logic from <code>LayoutPageTemplateEntryModelImpl</code>.
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
	 * Returns all the layout page template entries where groupId = &#63; and type = &#63; and defaultTemplate = &#63; and status = &#63;.
	 *
	 * @param groupId the group ID
	 * @param type the type
	 * @param defaultTemplate the default template
	 * @param status the status
	 * @return the matching layout page template entries
	 */
	public java.util.List<LayoutPageTemplateEntry> findByG_T_D_S(
		long groupId, int type, boolean defaultTemplate, int status);

	/**
	 * Returns a range of all the layout page template entries where groupId = &#63; and type = &#63; and defaultTemplate = &#63; and status = &#63;.
	 *
	 * <p>
	 * Useful when paginating results. Returns a maximum of <code>end - start</code> instances. <code>start</code> and <code>end</code> are not primary keys, they are indexes in the result set. Thus, <code>0</code> refers to the first result in the set. Setting both <code>start</code> and <code>end</code> to <code>QueryUtil#ALL_POS</code> will return the full result set. If <code>orderByComparator</code> is specified, then the query will include the given ORDER BY logic. If <code>orderByComparator</code> is absent, then the query will include the default ORDER BY logic from <code>LayoutPageTemplateEntryModelImpl</code>.
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
	public java.util.List<LayoutPageTemplateEntry> findByG_T_D_S(
		long groupId, int type, boolean defaultTemplate, int status, int start,
		int end);

	/**
	 * Returns an ordered range of all the layout page template entries where groupId = &#63; and type = &#63; and defaultTemplate = &#63; and status = &#63;.
	 *
	 * <p>
	 * Useful when paginating results. Returns a maximum of <code>end - start</code> instances. <code>start</code> and <code>end</code> are not primary keys, they are indexes in the result set. Thus, <code>0</code> refers to the first result in the set. Setting both <code>start</code> and <code>end</code> to <code>QueryUtil#ALL_POS</code> will return the full result set. If <code>orderByComparator</code> is specified, then the query will include the given ORDER BY logic. If <code>orderByComparator</code> is absent, then the query will include the default ORDER BY logic from <code>LayoutPageTemplateEntryModelImpl</code>.
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
	public java.util.List<LayoutPageTemplateEntry> findByG_T_D_S(
		long groupId, int type, boolean defaultTemplate, int status, int start,
		int end,
		com.liferay.portal.kernel.util.OrderByComparator
			<LayoutPageTemplateEntry> orderByComparator);

	/**
	 * Returns an ordered range of all the layout page template entries where groupId = &#63; and type = &#63; and defaultTemplate = &#63; and status = &#63;.
	 *
	 * <p>
	 * Useful when paginating results. Returns a maximum of <code>end - start</code> instances. <code>start</code> and <code>end</code> are not primary keys, they are indexes in the result set. Thus, <code>0</code> refers to the first result in the set. Setting both <code>start</code> and <code>end</code> to <code>QueryUtil#ALL_POS</code> will return the full result set. If <code>orderByComparator</code> is specified, then the query will include the given ORDER BY logic. If <code>orderByComparator</code> is absent, then the query will include the default ORDER BY logic from <code>LayoutPageTemplateEntryModelImpl</code>.
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
	 * Returns the last layout page template entry in the ordered set where groupId = &#63; and type = &#63; and defaultTemplate = &#63; and status = &#63;.
	 *
	 * @param groupId the group ID
	 * @param type the type
	 * @param defaultTemplate the default template
	 * @param status the status
	 * @param orderByComparator the comparator to order the set by (optionally <code>null</code>)
	 * @return the last matching layout page template entry
	 * @throws NoSuchPageTemplateEntryException if a matching layout page template entry could not be found
	 */
	public LayoutPageTemplateEntry findByG_T_D_S_Last(
			long groupId, int type, boolean defaultTemplate, int status,
			com.liferay.portal.kernel.util.OrderByComparator
				<LayoutPageTemplateEntry> orderByComparator)
		throws NoSuchPageTemplateEntryException;

	/**
	 * Returns the last layout page template entry in the ordered set where groupId = &#63; and type = &#63; and defaultTemplate = &#63; and status = &#63;.
	 *
	 * @param groupId the group ID
	 * @param type the type
	 * @param defaultTemplate the default template
	 * @param status the status
	 * @param orderByComparator the comparator to order the set by (optionally <code>null</code>)
	 * @return the last matching layout page template entry, or <code>null</code> if a matching layout page template entry could not be found
	 */
	public LayoutPageTemplateEntry fetchByG_T_D_S_Last(
		long groupId, int type, boolean defaultTemplate, int status,
		com.liferay.portal.kernel.util.OrderByComparator
			<LayoutPageTemplateEntry> orderByComparator);

	/**
	 * Returns the layout page template entries before and after the current layout page template entry in the ordered set where groupId = &#63; and type = &#63; and defaultTemplate = &#63; and status = &#63;.
	 *
	 * @param layoutPageTemplateEntryId the primary key of the current layout page template entry
	 * @param groupId the group ID
	 * @param type the type
	 * @param defaultTemplate the default template
	 * @param status the status
	 * @param orderByComparator the comparator to order the set by (optionally <code>null</code>)
	 * @return the previous, current, and next layout page template entry
	 * @throws NoSuchPageTemplateEntryException if a layout page template entry with the primary key could not be found
	 */
	public LayoutPageTemplateEntry[] findByG_T_D_S_PrevAndNext(
			long layoutPageTemplateEntryId, long groupId, int type,
			boolean defaultTemplate, int status,
			com.liferay.portal.kernel.util.OrderByComparator
				<LayoutPageTemplateEntry> orderByComparator)
		throws NoSuchPageTemplateEntryException;

	/**
	 * Returns all the layout page template entries that the user has permission to view where groupId = &#63; and type = &#63; and defaultTemplate = &#63; and status = &#63;.
	 *
	 * @param groupId the group ID
	 * @param type the type
	 * @param defaultTemplate the default template
	 * @param status the status
	 * @return the matching layout page template entries that the user has permission to view
	 */
	public java.util.List<LayoutPageTemplateEntry> filterFindByG_T_D_S(
		long groupId, int type, boolean defaultTemplate, int status);

	/**
	 * Returns a range of all the layout page template entries that the user has permission to view where groupId = &#63; and type = &#63; and defaultTemplate = &#63; and status = &#63;.
	 *
	 * <p>
	 * Useful when paginating results. Returns a maximum of <code>end - start</code> instances. <code>start</code> and <code>end</code> are not primary keys, they are indexes in the result set. Thus, <code>0</code> refers to the first result in the set. Setting both <code>start</code> and <code>end</code> to <code>QueryUtil#ALL_POS</code> will return the full result set. If <code>orderByComparator</code> is specified, then the query will include the given ORDER BY logic. If <code>orderByComparator</code> is absent, then the query will include the default ORDER BY logic from <code>LayoutPageTemplateEntryModelImpl</code>.
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
	public java.util.List<LayoutPageTemplateEntry> filterFindByG_T_D_S(
		long groupId, int type, boolean defaultTemplate, int status, int start,
		int end);

	/**
	 * Returns an ordered range of all the layout page template entries that the user has permissions to view where groupId = &#63; and type = &#63; and defaultTemplate = &#63; and status = &#63;.
	 *
	 * <p>
	 * Useful when paginating results. Returns a maximum of <code>end - start</code> instances. <code>start</code> and <code>end</code> are not primary keys, they are indexes in the result set. Thus, <code>0</code> refers to the first result in the set. Setting both <code>start</code> and <code>end</code> to <code>QueryUtil#ALL_POS</code> will return the full result set. If <code>orderByComparator</code> is specified, then the query will include the given ORDER BY logic. If <code>orderByComparator</code> is absent, then the query will include the default ORDER BY logic from <code>LayoutPageTemplateEntryModelImpl</code>.
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
	 * Returns the layout page template entries before and after the current layout page template entry in the ordered set of layout page template entries that the user has permission to view where groupId = &#63; and type = &#63; and defaultTemplate = &#63; and status = &#63;.
	 *
	 * @param layoutPageTemplateEntryId the primary key of the current layout page template entry
	 * @param groupId the group ID
	 * @param type the type
	 * @param defaultTemplate the default template
	 * @param status the status
	 * @param orderByComparator the comparator to order the set by (optionally <code>null</code>)
	 * @return the previous, current, and next layout page template entry
	 * @throws NoSuchPageTemplateEntryException if a layout page template entry with the primary key could not be found
	 */
	public LayoutPageTemplateEntry[] filterFindByG_T_D_S_PrevAndNext(
			long layoutPageTemplateEntryId, long groupId, int type,
			boolean defaultTemplate, int status,
			com.liferay.portal.kernel.util.OrderByComparator
				<LayoutPageTemplateEntry> orderByComparator)
		throws NoSuchPageTemplateEntryException;

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
	 * Useful when paginating results. Returns a maximum of <code>end - start</code> instances. <code>start</code> and <code>end</code> are not primary keys, they are indexes in the result set. Thus, <code>0</code> refers to the first result in the set. Setting both <code>start</code> and <code>end</code> to <code>QueryUtil#ALL_POS</code> will return the full result set. If <code>orderByComparator</code> is specified, then the query will include the given ORDER BY logic. If <code>orderByComparator</code> is absent, then the query will include the default ORDER BY logic from <code>LayoutPageTemplateEntryModelImpl</code>.
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
	 * Useful when paginating results. Returns a maximum of <code>end - start</code> instances. <code>start</code> and <code>end</code> are not primary keys, they are indexes in the result set. Thus, <code>0</code> refers to the first result in the set. Setting both <code>start</code> and <code>end</code> to <code>QueryUtil#ALL_POS</code> will return the full result set. If <code>orderByComparator</code> is specified, then the query will include the given ORDER BY logic. If <code>orderByComparator</code> is absent, then the query will include the default ORDER BY logic from <code>LayoutPageTemplateEntryModelImpl</code>.
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
	 * Useful when paginating results. Returns a maximum of <code>end - start</code> instances. <code>start</code> and <code>end</code> are not primary keys, they are indexes in the result set. Thus, <code>0</code> refers to the first result in the set. Setting both <code>start</code> and <code>end</code> to <code>QueryUtil#ALL_POS</code> will return the full result set. If <code>orderByComparator</code> is specified, then the query will include the given ORDER BY logic. If <code>orderByComparator</code> is absent, then the query will include the default ORDER BY logic from <code>LayoutPageTemplateEntryModelImpl</code>.
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
	 * Returns the last layout page template entry in the ordered set where groupId = &#63; and classNameId = &#63; and classTypeKey = &#63; and name LIKE &#63; and type = &#63;.
	 *
	 * @param groupId the group ID
	 * @param classNameId the class name ID
	 * @param classTypeKey the class type key
	 * @param name the name
	 * @param type the type
	 * @param orderByComparator the comparator to order the set by (optionally <code>null</code>)
	 * @return the last matching layout page template entry
	 * @throws NoSuchPageTemplateEntryException if a matching layout page template entry could not be found
	 */
	public LayoutPageTemplateEntry findByG_C_C_LikeN_T_Last(
			long groupId, long classNameId, String classTypeKey, String name,
			int type,
			com.liferay.portal.kernel.util.OrderByComparator
				<LayoutPageTemplateEntry> orderByComparator)
		throws NoSuchPageTemplateEntryException;

	/**
	 * Returns the last layout page template entry in the ordered set where groupId = &#63; and classNameId = &#63; and classTypeKey = &#63; and name LIKE &#63; and type = &#63;.
	 *
	 * @param groupId the group ID
	 * @param classNameId the class name ID
	 * @param classTypeKey the class type key
	 * @param name the name
	 * @param type the type
	 * @param orderByComparator the comparator to order the set by (optionally <code>null</code>)
	 * @return the last matching layout page template entry, or <code>null</code> if a matching layout page template entry could not be found
	 */
	public LayoutPageTemplateEntry fetchByG_C_C_LikeN_T_Last(
		long groupId, long classNameId, String classTypeKey, String name,
		int type,
		com.liferay.portal.kernel.util.OrderByComparator
			<LayoutPageTemplateEntry> orderByComparator);

	/**
	 * Returns the layout page template entries before and after the current layout page template entry in the ordered set where groupId = &#63; and classNameId = &#63; and classTypeKey = &#63; and name LIKE &#63; and type = &#63;.
	 *
	 * @param layoutPageTemplateEntryId the primary key of the current layout page template entry
	 * @param groupId the group ID
	 * @param classNameId the class name ID
	 * @param classTypeKey the class type key
	 * @param name the name
	 * @param type the type
	 * @param orderByComparator the comparator to order the set by (optionally <code>null</code>)
	 * @return the previous, current, and next layout page template entry
	 * @throws NoSuchPageTemplateEntryException if a layout page template entry with the primary key could not be found
	 */
	public LayoutPageTemplateEntry[] findByG_C_C_LikeN_T_PrevAndNext(
			long layoutPageTemplateEntryId, long groupId, long classNameId,
			String classTypeKey, String name, int type,
			com.liferay.portal.kernel.util.OrderByComparator
				<LayoutPageTemplateEntry> orderByComparator)
		throws NoSuchPageTemplateEntryException;

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
	 * Useful when paginating results. Returns a maximum of <code>end - start</code> instances. <code>start</code> and <code>end</code> are not primary keys, they are indexes in the result set. Thus, <code>0</code> refers to the first result in the set. Setting both <code>start</code> and <code>end</code> to <code>QueryUtil#ALL_POS</code> will return the full result set. If <code>orderByComparator</code> is specified, then the query will include the given ORDER BY logic. If <code>orderByComparator</code> is absent, then the query will include the default ORDER BY logic from <code>LayoutPageTemplateEntryModelImpl</code>.
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
	 * Useful when paginating results. Returns a maximum of <code>end - start</code> instances. <code>start</code> and <code>end</code> are not primary keys, they are indexes in the result set. Thus, <code>0</code> refers to the first result in the set. Setting both <code>start</code> and <code>end</code> to <code>QueryUtil#ALL_POS</code> will return the full result set. If <code>orderByComparator</code> is specified, then the query will include the given ORDER BY logic. If <code>orderByComparator</code> is absent, then the query will include the default ORDER BY logic from <code>LayoutPageTemplateEntryModelImpl</code>.
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
	 * Returns the layout page template entries before and after the current layout page template entry in the ordered set of layout page template entries that the user has permission to view where groupId = &#63; and classNameId = &#63; and classTypeKey = &#63; and name LIKE &#63; and type = &#63;.
	 *
	 * @param layoutPageTemplateEntryId the primary key of the current layout page template entry
	 * @param groupId the group ID
	 * @param classNameId the class name ID
	 * @param classTypeKey the class type key
	 * @param name the name
	 * @param type the type
	 * @param orderByComparator the comparator to order the set by (optionally <code>null</code>)
	 * @return the previous, current, and next layout page template entry
	 * @throws NoSuchPageTemplateEntryException if a layout page template entry with the primary key could not be found
	 */
	public LayoutPageTemplateEntry[] filterFindByG_C_C_LikeN_T_PrevAndNext(
			long layoutPageTemplateEntryId, long groupId, long classNameId,
			String classTypeKey, String name, int type,
			com.liferay.portal.kernel.util.OrderByComparator
				<LayoutPageTemplateEntry> orderByComparator)
		throws NoSuchPageTemplateEntryException;

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
	 * Returns all the layout page template entries where groupId = &#63; and classNameId = &#63; and classTypeKey = &#63; and type = &#63; and status = &#63;.
	 *
	 * @param groupId the group ID
	 * @param classNameId the class name ID
	 * @param classTypeKey the class type key
	 * @param type the type
	 * @param status the status
	 * @return the matching layout page template entries
	 */
	public java.util.List<LayoutPageTemplateEntry> findByG_C_C_T_S(
		long groupId, long classNameId, String classTypeKey, int type,
		int status);

	/**
	 * Returns a range of all the layout page template entries where groupId = &#63; and classNameId = &#63; and classTypeKey = &#63; and type = &#63; and status = &#63;.
	 *
	 * <p>
	 * Useful when paginating results. Returns a maximum of <code>end - start</code> instances. <code>start</code> and <code>end</code> are not primary keys, they are indexes in the result set. Thus, <code>0</code> refers to the first result in the set. Setting both <code>start</code> and <code>end</code> to <code>QueryUtil#ALL_POS</code> will return the full result set. If <code>orderByComparator</code> is specified, then the query will include the given ORDER BY logic. If <code>orderByComparator</code> is absent, then the query will include the default ORDER BY logic from <code>LayoutPageTemplateEntryModelImpl</code>.
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
	public java.util.List<LayoutPageTemplateEntry> findByG_C_C_T_S(
		long groupId, long classNameId, String classTypeKey, int type,
		int status, int start, int end);

	/**
	 * Returns an ordered range of all the layout page template entries where groupId = &#63; and classNameId = &#63; and classTypeKey = &#63; and type = &#63; and status = &#63;.
	 *
	 * <p>
	 * Useful when paginating results. Returns a maximum of <code>end - start</code> instances. <code>start</code> and <code>end</code> are not primary keys, they are indexes in the result set. Thus, <code>0</code> refers to the first result in the set. Setting both <code>start</code> and <code>end</code> to <code>QueryUtil#ALL_POS</code> will return the full result set. If <code>orderByComparator</code> is specified, then the query will include the given ORDER BY logic. If <code>orderByComparator</code> is absent, then the query will include the default ORDER BY logic from <code>LayoutPageTemplateEntryModelImpl</code>.
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
	public java.util.List<LayoutPageTemplateEntry> findByG_C_C_T_S(
		long groupId, long classNameId, String classTypeKey, int type,
		int status, int start, int end,
		com.liferay.portal.kernel.util.OrderByComparator
			<LayoutPageTemplateEntry> orderByComparator);

	/**
	 * Returns an ordered range of all the layout page template entries where groupId = &#63; and classNameId = &#63; and classTypeKey = &#63; and type = &#63; and status = &#63;.
	 *
	 * <p>
	 * Useful when paginating results. Returns a maximum of <code>end - start</code> instances. <code>start</code> and <code>end</code> are not primary keys, they are indexes in the result set. Thus, <code>0</code> refers to the first result in the set. Setting both <code>start</code> and <code>end</code> to <code>QueryUtil#ALL_POS</code> will return the full result set. If <code>orderByComparator</code> is specified, then the query will include the given ORDER BY logic. If <code>orderByComparator</code> is absent, then the query will include the default ORDER BY logic from <code>LayoutPageTemplateEntryModelImpl</code>.
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
	 * Returns the last layout page template entry in the ordered set where groupId = &#63; and classNameId = &#63; and classTypeKey = &#63; and type = &#63; and status = &#63;.
	 *
	 * @param groupId the group ID
	 * @param classNameId the class name ID
	 * @param classTypeKey the class type key
	 * @param type the type
	 * @param status the status
	 * @param orderByComparator the comparator to order the set by (optionally <code>null</code>)
	 * @return the last matching layout page template entry
	 * @throws NoSuchPageTemplateEntryException if a matching layout page template entry could not be found
	 */
	public LayoutPageTemplateEntry findByG_C_C_T_S_Last(
			long groupId, long classNameId, String classTypeKey, int type,
			int status,
			com.liferay.portal.kernel.util.OrderByComparator
				<LayoutPageTemplateEntry> orderByComparator)
		throws NoSuchPageTemplateEntryException;

	/**
	 * Returns the last layout page template entry in the ordered set where groupId = &#63; and classNameId = &#63; and classTypeKey = &#63; and type = &#63; and status = &#63;.
	 *
	 * @param groupId the group ID
	 * @param classNameId the class name ID
	 * @param classTypeKey the class type key
	 * @param type the type
	 * @param status the status
	 * @param orderByComparator the comparator to order the set by (optionally <code>null</code>)
	 * @return the last matching layout page template entry, or <code>null</code> if a matching layout page template entry could not be found
	 */
	public LayoutPageTemplateEntry fetchByG_C_C_T_S_Last(
		long groupId, long classNameId, String classTypeKey, int type,
		int status,
		com.liferay.portal.kernel.util.OrderByComparator
			<LayoutPageTemplateEntry> orderByComparator);

	/**
	 * Returns the layout page template entries before and after the current layout page template entry in the ordered set where groupId = &#63; and classNameId = &#63; and classTypeKey = &#63; and type = &#63; and status = &#63;.
	 *
	 * @param layoutPageTemplateEntryId the primary key of the current layout page template entry
	 * @param groupId the group ID
	 * @param classNameId the class name ID
	 * @param classTypeKey the class type key
	 * @param type the type
	 * @param status the status
	 * @param orderByComparator the comparator to order the set by (optionally <code>null</code>)
	 * @return the previous, current, and next layout page template entry
	 * @throws NoSuchPageTemplateEntryException if a layout page template entry with the primary key could not be found
	 */
	public LayoutPageTemplateEntry[] findByG_C_C_T_S_PrevAndNext(
			long layoutPageTemplateEntryId, long groupId, long classNameId,
			String classTypeKey, int type, int status,
			com.liferay.portal.kernel.util.OrderByComparator
				<LayoutPageTemplateEntry> orderByComparator)
		throws NoSuchPageTemplateEntryException;

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
	public java.util.List<LayoutPageTemplateEntry> filterFindByG_C_C_T_S(
		long groupId, long classNameId, String classTypeKey, int type,
		int status);

	/**
	 * Returns a range of all the layout page template entries that the user has permission to view where groupId = &#63; and classNameId = &#63; and classTypeKey = &#63; and type = &#63; and status = &#63;.
	 *
	 * <p>
	 * Useful when paginating results. Returns a maximum of <code>end - start</code> instances. <code>start</code> and <code>end</code> are not primary keys, they are indexes in the result set. Thus, <code>0</code> refers to the first result in the set. Setting both <code>start</code> and <code>end</code> to <code>QueryUtil#ALL_POS</code> will return the full result set. If <code>orderByComparator</code> is specified, then the query will include the given ORDER BY logic. If <code>orderByComparator</code> is absent, then the query will include the default ORDER BY logic from <code>LayoutPageTemplateEntryModelImpl</code>.
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
	public java.util.List<LayoutPageTemplateEntry> filterFindByG_C_C_T_S(
		long groupId, long classNameId, String classTypeKey, int type,
		int status, int start, int end);

	/**
	 * Returns an ordered range of all the layout page template entries that the user has permissions to view where groupId = &#63; and classNameId = &#63; and classTypeKey = &#63; and type = &#63; and status = &#63;.
	 *
	 * <p>
	 * Useful when paginating results. Returns a maximum of <code>end - start</code> instances. <code>start</code> and <code>end</code> are not primary keys, they are indexes in the result set. Thus, <code>0</code> refers to the first result in the set. Setting both <code>start</code> and <code>end</code> to <code>QueryUtil#ALL_POS</code> will return the full result set. If <code>orderByComparator</code> is specified, then the query will include the given ORDER BY logic. If <code>orderByComparator</code> is absent, then the query will include the default ORDER BY logic from <code>LayoutPageTemplateEntryModelImpl</code>.
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
	 * Returns the layout page template entries before and after the current layout page template entry in the ordered set of layout page template entries that the user has permission to view where groupId = &#63; and classNameId = &#63; and classTypeKey = &#63; and type = &#63; and status = &#63;.
	 *
	 * @param layoutPageTemplateEntryId the primary key of the current layout page template entry
	 * @param groupId the group ID
	 * @param classNameId the class name ID
	 * @param classTypeKey the class type key
	 * @param type the type
	 * @param status the status
	 * @param orderByComparator the comparator to order the set by (optionally <code>null</code>)
	 * @return the previous, current, and next layout page template entry
	 * @throws NoSuchPageTemplateEntryException if a layout page template entry with the primary key could not be found
	 */
	public LayoutPageTemplateEntry[] filterFindByG_C_C_T_S_PrevAndNext(
			long layoutPageTemplateEntryId, long groupId, long classNameId,
			String classTypeKey, int type, int status,
			com.liferay.portal.kernel.util.OrderByComparator
				<LayoutPageTemplateEntry> orderByComparator)
		throws NoSuchPageTemplateEntryException;

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
	 * Returns all the layout page template entries where groupId = &#63; and classNameId = &#63; and classTypeKey = &#63; and defaultTemplate = &#63; and status = &#63;.
	 *
	 * @param groupId the group ID
	 * @param classNameId the class name ID
	 * @param classTypeKey the class type key
	 * @param defaultTemplate the default template
	 * @param status the status
	 * @return the matching layout page template entries
	 */
	public java.util.List<LayoutPageTemplateEntry> findByG_C_C_D_S(
		long groupId, long classNameId, String classTypeKey,
		boolean defaultTemplate, int status);

	/**
	 * Returns a range of all the layout page template entries where groupId = &#63; and classNameId = &#63; and classTypeKey = &#63; and defaultTemplate = &#63; and status = &#63;.
	 *
	 * <p>
	 * Useful when paginating results. Returns a maximum of <code>end - start</code> instances. <code>start</code> and <code>end</code> are not primary keys, they are indexes in the result set. Thus, <code>0</code> refers to the first result in the set. Setting both <code>start</code> and <code>end</code> to <code>QueryUtil#ALL_POS</code> will return the full result set. If <code>orderByComparator</code> is specified, then the query will include the given ORDER BY logic. If <code>orderByComparator</code> is absent, then the query will include the default ORDER BY logic from <code>LayoutPageTemplateEntryModelImpl</code>.
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
	public java.util.List<LayoutPageTemplateEntry> findByG_C_C_D_S(
		long groupId, long classNameId, String classTypeKey,
		boolean defaultTemplate, int status, int start, int end);

	/**
	 * Returns an ordered range of all the layout page template entries where groupId = &#63; and classNameId = &#63; and classTypeKey = &#63; and defaultTemplate = &#63; and status = &#63;.
	 *
	 * <p>
	 * Useful when paginating results. Returns a maximum of <code>end - start</code> instances. <code>start</code> and <code>end</code> are not primary keys, they are indexes in the result set. Thus, <code>0</code> refers to the first result in the set. Setting both <code>start</code> and <code>end</code> to <code>QueryUtil#ALL_POS</code> will return the full result set. If <code>orderByComparator</code> is specified, then the query will include the given ORDER BY logic. If <code>orderByComparator</code> is absent, then the query will include the default ORDER BY logic from <code>LayoutPageTemplateEntryModelImpl</code>.
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
	public java.util.List<LayoutPageTemplateEntry> findByG_C_C_D_S(
		long groupId, long classNameId, String classTypeKey,
		boolean defaultTemplate, int status, int start, int end,
		com.liferay.portal.kernel.util.OrderByComparator
			<LayoutPageTemplateEntry> orderByComparator);

	/**
	 * Returns an ordered range of all the layout page template entries where groupId = &#63; and classNameId = &#63; and classTypeKey = &#63; and defaultTemplate = &#63; and status = &#63;.
	 *
	 * <p>
	 * Useful when paginating results. Returns a maximum of <code>end - start</code> instances. <code>start</code> and <code>end</code> are not primary keys, they are indexes in the result set. Thus, <code>0</code> refers to the first result in the set. Setting both <code>start</code> and <code>end</code> to <code>QueryUtil#ALL_POS</code> will return the full result set. If <code>orderByComparator</code> is specified, then the query will include the given ORDER BY logic. If <code>orderByComparator</code> is absent, then the query will include the default ORDER BY logic from <code>LayoutPageTemplateEntryModelImpl</code>.
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
	 * Returns the last layout page template entry in the ordered set where groupId = &#63; and classNameId = &#63; and classTypeKey = &#63; and defaultTemplate = &#63; and status = &#63;.
	 *
	 * @param groupId the group ID
	 * @param classNameId the class name ID
	 * @param classTypeKey the class type key
	 * @param defaultTemplate the default template
	 * @param status the status
	 * @param orderByComparator the comparator to order the set by (optionally <code>null</code>)
	 * @return the last matching layout page template entry
	 * @throws NoSuchPageTemplateEntryException if a matching layout page template entry could not be found
	 */
	public LayoutPageTemplateEntry findByG_C_C_D_S_Last(
			long groupId, long classNameId, String classTypeKey,
			boolean defaultTemplate, int status,
			com.liferay.portal.kernel.util.OrderByComparator
				<LayoutPageTemplateEntry> orderByComparator)
		throws NoSuchPageTemplateEntryException;

	/**
	 * Returns the last layout page template entry in the ordered set where groupId = &#63; and classNameId = &#63; and classTypeKey = &#63; and defaultTemplate = &#63; and status = &#63;.
	 *
	 * @param groupId the group ID
	 * @param classNameId the class name ID
	 * @param classTypeKey the class type key
	 * @param defaultTemplate the default template
	 * @param status the status
	 * @param orderByComparator the comparator to order the set by (optionally <code>null</code>)
	 * @return the last matching layout page template entry, or <code>null</code> if a matching layout page template entry could not be found
	 */
	public LayoutPageTemplateEntry fetchByG_C_C_D_S_Last(
		long groupId, long classNameId, String classTypeKey,
		boolean defaultTemplate, int status,
		com.liferay.portal.kernel.util.OrderByComparator
			<LayoutPageTemplateEntry> orderByComparator);

	/**
	 * Returns the layout page template entries before and after the current layout page template entry in the ordered set where groupId = &#63; and classNameId = &#63; and classTypeKey = &#63; and defaultTemplate = &#63; and status = &#63;.
	 *
	 * @param layoutPageTemplateEntryId the primary key of the current layout page template entry
	 * @param groupId the group ID
	 * @param classNameId the class name ID
	 * @param classTypeKey the class type key
	 * @param defaultTemplate the default template
	 * @param status the status
	 * @param orderByComparator the comparator to order the set by (optionally <code>null</code>)
	 * @return the previous, current, and next layout page template entry
	 * @throws NoSuchPageTemplateEntryException if a layout page template entry with the primary key could not be found
	 */
	public LayoutPageTemplateEntry[] findByG_C_C_D_S_PrevAndNext(
			long layoutPageTemplateEntryId, long groupId, long classNameId,
			String classTypeKey, boolean defaultTemplate, int status,
			com.liferay.portal.kernel.util.OrderByComparator
				<LayoutPageTemplateEntry> orderByComparator)
		throws NoSuchPageTemplateEntryException;

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
	public java.util.List<LayoutPageTemplateEntry> filterFindByG_C_C_D_S(
		long groupId, long classNameId, String classTypeKey,
		boolean defaultTemplate, int status);

	/**
	 * Returns a range of all the layout page template entries that the user has permission to view where groupId = &#63; and classNameId = &#63; and classTypeKey = &#63; and defaultTemplate = &#63; and status = &#63;.
	 *
	 * <p>
	 * Useful when paginating results. Returns a maximum of <code>end - start</code> instances. <code>start</code> and <code>end</code> are not primary keys, they are indexes in the result set. Thus, <code>0</code> refers to the first result in the set. Setting both <code>start</code> and <code>end</code> to <code>QueryUtil#ALL_POS</code> will return the full result set. If <code>orderByComparator</code> is specified, then the query will include the given ORDER BY logic. If <code>orderByComparator</code> is absent, then the query will include the default ORDER BY logic from <code>LayoutPageTemplateEntryModelImpl</code>.
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
	public java.util.List<LayoutPageTemplateEntry> filterFindByG_C_C_D_S(
		long groupId, long classNameId, String classTypeKey,
		boolean defaultTemplate, int status, int start, int end);

	/**
	 * Returns an ordered range of all the layout page template entries that the user has permissions to view where groupId = &#63; and classNameId = &#63; and classTypeKey = &#63; and defaultTemplate = &#63; and status = &#63;.
	 *
	 * <p>
	 * Useful when paginating results. Returns a maximum of <code>end - start</code> instances. <code>start</code> and <code>end</code> are not primary keys, they are indexes in the result set. Thus, <code>0</code> refers to the first result in the set. Setting both <code>start</code> and <code>end</code> to <code>QueryUtil#ALL_POS</code> will return the full result set. If <code>orderByComparator</code> is specified, then the query will include the given ORDER BY logic. If <code>orderByComparator</code> is absent, then the query will include the default ORDER BY logic from <code>LayoutPageTemplateEntryModelImpl</code>.
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
	 * Returns the layout page template entries before and after the current layout page template entry in the ordered set of layout page template entries that the user has permission to view where groupId = &#63; and classNameId = &#63; and classTypeKey = &#63; and defaultTemplate = &#63; and status = &#63;.
	 *
	 * @param layoutPageTemplateEntryId the primary key of the current layout page template entry
	 * @param groupId the group ID
	 * @param classNameId the class name ID
	 * @param classTypeKey the class type key
	 * @param defaultTemplate the default template
	 * @param status the status
	 * @param orderByComparator the comparator to order the set by (optionally <code>null</code>)
	 * @return the previous, current, and next layout page template entry
	 * @throws NoSuchPageTemplateEntryException if a layout page template entry with the primary key could not be found
	 */
	public LayoutPageTemplateEntry[] filterFindByG_C_C_D_S_PrevAndNext(
			long layoutPageTemplateEntryId, long groupId, long classNameId,
			String classTypeKey, boolean defaultTemplate, int status,
			com.liferay.portal.kernel.util.OrderByComparator
				<LayoutPageTemplateEntry> orderByComparator)
		throws NoSuchPageTemplateEntryException;

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
	 * Useful when paginating results. Returns a maximum of <code>end - start</code> instances. <code>start</code> and <code>end</code> are not primary keys, they are indexes in the result set. Thus, <code>0</code> refers to the first result in the set. Setting both <code>start</code> and <code>end</code> to <code>QueryUtil#ALL_POS</code> will return the full result set. If <code>orderByComparator</code> is specified, then the query will include the given ORDER BY logic. If <code>orderByComparator</code> is absent, then the query will include the default ORDER BY logic from <code>LayoutPageTemplateEntryModelImpl</code>.
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
	 * Useful when paginating results. Returns a maximum of <code>end - start</code> instances. <code>start</code> and <code>end</code> are not primary keys, they are indexes in the result set. Thus, <code>0</code> refers to the first result in the set. Setting both <code>start</code> and <code>end</code> to <code>QueryUtil#ALL_POS</code> will return the full result set. If <code>orderByComparator</code> is specified, then the query will include the given ORDER BY logic. If <code>orderByComparator</code> is absent, then the query will include the default ORDER BY logic from <code>LayoutPageTemplateEntryModelImpl</code>.
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
	 * Useful when paginating results. Returns a maximum of <code>end - start</code> instances. <code>start</code> and <code>end</code> are not primary keys, they are indexes in the result set. Thus, <code>0</code> refers to the first result in the set. Setting both <code>start</code> and <code>end</code> to <code>QueryUtil#ALL_POS</code> will return the full result set. If <code>orderByComparator</code> is specified, then the query will include the given ORDER BY logic. If <code>orderByComparator</code> is absent, then the query will include the default ORDER BY logic from <code>LayoutPageTemplateEntryModelImpl</code>.
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
	 * Returns the last layout page template entry in the ordered set where groupId = &#63; and classNameId = &#63; and classTypeKey = &#63; and name LIKE &#63; and type = &#63; and status = &#63;.
	 *
	 * @param groupId the group ID
	 * @param classNameId the class name ID
	 * @param classTypeKey the class type key
	 * @param name the name
	 * @param type the type
	 * @param status the status
	 * @param orderByComparator the comparator to order the set by (optionally <code>null</code>)
	 * @return the last matching layout page template entry
	 * @throws NoSuchPageTemplateEntryException if a matching layout page template entry could not be found
	 */
	public LayoutPageTemplateEntry findByG_C_C_LikeN_T_S_Last(
			long groupId, long classNameId, String classTypeKey, String name,
			int type, int status,
			com.liferay.portal.kernel.util.OrderByComparator
				<LayoutPageTemplateEntry> orderByComparator)
		throws NoSuchPageTemplateEntryException;

	/**
	 * Returns the last layout page template entry in the ordered set where groupId = &#63; and classNameId = &#63; and classTypeKey = &#63; and name LIKE &#63; and type = &#63; and status = &#63;.
	 *
	 * @param groupId the group ID
	 * @param classNameId the class name ID
	 * @param classTypeKey the class type key
	 * @param name the name
	 * @param type the type
	 * @param status the status
	 * @param orderByComparator the comparator to order the set by (optionally <code>null</code>)
	 * @return the last matching layout page template entry, or <code>null</code> if a matching layout page template entry could not be found
	 */
	public LayoutPageTemplateEntry fetchByG_C_C_LikeN_T_S_Last(
		long groupId, long classNameId, String classTypeKey, String name,
		int type, int status,
		com.liferay.portal.kernel.util.OrderByComparator
			<LayoutPageTemplateEntry> orderByComparator);

	/**
	 * Returns the layout page template entries before and after the current layout page template entry in the ordered set where groupId = &#63; and classNameId = &#63; and classTypeKey = &#63; and name LIKE &#63; and type = &#63; and status = &#63;.
	 *
	 * @param layoutPageTemplateEntryId the primary key of the current layout page template entry
	 * @param groupId the group ID
	 * @param classNameId the class name ID
	 * @param classTypeKey the class type key
	 * @param name the name
	 * @param type the type
	 * @param status the status
	 * @param orderByComparator the comparator to order the set by (optionally <code>null</code>)
	 * @return the previous, current, and next layout page template entry
	 * @throws NoSuchPageTemplateEntryException if a layout page template entry with the primary key could not be found
	 */
	public LayoutPageTemplateEntry[] findByG_C_C_LikeN_T_S_PrevAndNext(
			long layoutPageTemplateEntryId, long groupId, long classNameId,
			String classTypeKey, String name, int type, int status,
			com.liferay.portal.kernel.util.OrderByComparator
				<LayoutPageTemplateEntry> orderByComparator)
		throws NoSuchPageTemplateEntryException;

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
	 * Useful when paginating results. Returns a maximum of <code>end - start</code> instances. <code>start</code> and <code>end</code> are not primary keys, they are indexes in the result set. Thus, <code>0</code> refers to the first result in the set. Setting both <code>start</code> and <code>end</code> to <code>QueryUtil#ALL_POS</code> will return the full result set. If <code>orderByComparator</code> is specified, then the query will include the given ORDER BY logic. If <code>orderByComparator</code> is absent, then the query will include the default ORDER BY logic from <code>LayoutPageTemplateEntryModelImpl</code>.
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
	 * Useful when paginating results. Returns a maximum of <code>end - start</code> instances. <code>start</code> and <code>end</code> are not primary keys, they are indexes in the result set. Thus, <code>0</code> refers to the first result in the set. Setting both <code>start</code> and <code>end</code> to <code>QueryUtil#ALL_POS</code> will return the full result set. If <code>orderByComparator</code> is specified, then the query will include the given ORDER BY logic. If <code>orderByComparator</code> is absent, then the query will include the default ORDER BY logic from <code>LayoutPageTemplateEntryModelImpl</code>.
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
	 * Returns the layout page template entries before and after the current layout page template entry in the ordered set of layout page template entries that the user has permission to view where groupId = &#63; and classNameId = &#63; and classTypeKey = &#63; and name LIKE &#63; and type = &#63; and status = &#63;.
	 *
	 * @param layoutPageTemplateEntryId the primary key of the current layout page template entry
	 * @param groupId the group ID
	 * @param classNameId the class name ID
	 * @param classTypeKey the class type key
	 * @param name the name
	 * @param type the type
	 * @param status the status
	 * @param orderByComparator the comparator to order the set by (optionally <code>null</code>)
	 * @return the previous, current, and next layout page template entry
	 * @throws NoSuchPageTemplateEntryException if a layout page template entry with the primary key could not be found
	 */
	public LayoutPageTemplateEntry[] filterFindByG_C_C_LikeN_T_S_PrevAndNext(
			long layoutPageTemplateEntryId, long groupId, long classNameId,
			String classTypeKey, String name, int type, int status,
			com.liferay.portal.kernel.util.OrderByComparator
				<LayoutPageTemplateEntry> orderByComparator)
		throws NoSuchPageTemplateEntryException;

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
	 * Returns the layout page template entry where externalReferenceCode = &#63; and groupId = &#63; or returns <code>null</code> if it could not be found. Uses the finder cache.
	 *
	 * @param externalReferenceCode the external reference code
	 * @param groupId the group ID
	 * @return the matching layout page template entry, or <code>null</code> if a matching layout page template entry could not be found
	 */
	public LayoutPageTemplateEntry fetchByERC_G(
		String externalReferenceCode, long groupId);

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
	 * Caches the layout page template entry in the entity cache if it is enabled.
	 *
	 * @param layoutPageTemplateEntry the layout page template entry
	 */
	public void cacheResult(LayoutPageTemplateEntry layoutPageTemplateEntry);

	/**
	 * Caches the layout page template entries in the entity cache if it is enabled.
	 *
	 * @param layoutPageTemplateEntries the layout page template entries
	 */
	public void cacheResult(
		java.util.List<LayoutPageTemplateEntry> layoutPageTemplateEntries);

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
	 * Returns all the layout page template entries.
	 *
	 * @return the layout page template entries
	 */
	public java.util.List<LayoutPageTemplateEntry> findAll();

	/**
	 * Returns a range of all the layout page template entries.
	 *
	 * <p>
	 * Useful when paginating results. Returns a maximum of <code>end - start</code> instances. <code>start</code> and <code>end</code> are not primary keys, they are indexes in the result set. Thus, <code>0</code> refers to the first result in the set. Setting both <code>start</code> and <code>end</code> to <code>QueryUtil#ALL_POS</code> will return the full result set. If <code>orderByComparator</code> is specified, then the query will include the given ORDER BY logic. If <code>orderByComparator</code> is absent, then the query will include the default ORDER BY logic from <code>LayoutPageTemplateEntryModelImpl</code>.
	 * </p>
	 *
	 * @param start the lower bound of the range of layout page template entries
	 * @param end the upper bound of the range of layout page template entries (not inclusive)
	 * @return the range of layout page template entries
	 */
	public java.util.List<LayoutPageTemplateEntry> findAll(int start, int end);

	/**
	 * Returns an ordered range of all the layout page template entries.
	 *
	 * <p>
	 * Useful when paginating results. Returns a maximum of <code>end - start</code> instances. <code>start</code> and <code>end</code> are not primary keys, they are indexes in the result set. Thus, <code>0</code> refers to the first result in the set. Setting both <code>start</code> and <code>end</code> to <code>QueryUtil#ALL_POS</code> will return the full result set. If <code>orderByComparator</code> is specified, then the query will include the given ORDER BY logic. If <code>orderByComparator</code> is absent, then the query will include the default ORDER BY logic from <code>LayoutPageTemplateEntryModelImpl</code>.
	 * </p>
	 *
	 * @param start the lower bound of the range of layout page template entries
	 * @param end the upper bound of the range of layout page template entries (not inclusive)
	 * @param orderByComparator the comparator to order the results by (optionally <code>null</code>)
	 * @return the ordered range of layout page template entries
	 */
	public java.util.List<LayoutPageTemplateEntry> findAll(
		int start, int end,
		com.liferay.portal.kernel.util.OrderByComparator
			<LayoutPageTemplateEntry> orderByComparator);

	/**
	 * Returns an ordered range of all the layout page template entries.
	 *
	 * <p>
	 * Useful when paginating results. Returns a maximum of <code>end - start</code> instances. <code>start</code> and <code>end</code> are not primary keys, they are indexes in the result set. Thus, <code>0</code> refers to the first result in the set. Setting both <code>start</code> and <code>end</code> to <code>QueryUtil#ALL_POS</code> will return the full result set. If <code>orderByComparator</code> is specified, then the query will include the given ORDER BY logic. If <code>orderByComparator</code> is absent, then the query will include the default ORDER BY logic from <code>LayoutPageTemplateEntryModelImpl</code>.
	 * </p>
	 *
	 * @param start the lower bound of the range of layout page template entries
	 * @param end the upper bound of the range of layout page template entries (not inclusive)
	 * @param orderByComparator the comparator to order the results by (optionally <code>null</code>)
	 * @param useFinderCache whether to use the finder cache
	 * @return the ordered range of layout page template entries
	 */
	public java.util.List<LayoutPageTemplateEntry> findAll(
		int start, int end,
		com.liferay.portal.kernel.util.OrderByComparator
			<LayoutPageTemplateEntry> orderByComparator,
		boolean useFinderCache);

	/**
	 * Removes all the layout page template entries from the database.
	 */
	public void removeAll();

	/**
	 * Returns the number of layout page template entries.
	 *
	 * @return the number of layout page template entries
	 */
	public int countAll();

}