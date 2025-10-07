/**
 * SPDX-FileCopyrightText: (c) 2000 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

package com.liferay.portal.kernel.service.persistence;

import com.liferay.portal.kernel.exception.NoSuchLayoutException;
import com.liferay.portal.kernel.model.Layout;
import com.liferay.portal.kernel.service.persistence.change.tracking.CTPersistence;

import org.osgi.annotation.versioning.ProviderType;

/**
 * The persistence interface for the layout service.
 *
 * <p>
 * Caching information and settings can be found in <code>portal.properties</code>
 * </p>
 *
 * @author Brian Wing Shun Chan
 * @see LayoutUtil
 * @generated
 */
@ProviderType
public interface LayoutPersistence
	extends BasePersistence<Layout>, CTPersistence<Layout> {

	/*
	 * NOTE FOR DEVELOPERS:
	 *
	 * Never modify or reference this interface directly. Always use {@link LayoutUtil} to access the layout persistence. Modify <code>service.xml</code> and rerun ServiceBuilder to regenerate this interface.
	 */

	/**
	 * Returns all the layouts where uuid = &#63;.
	 *
	 * @param uuid the uuid
	 * @return the matching layouts
	 */
	public java.util.List<Layout> findByUuid(String uuid);

	/**
	 * Returns a range of all the layouts where uuid = &#63;.
	 *
	 * <p>
	 * Useful when paginating results. Returns a maximum of <code>end - start</code> instances. <code>start</code> and <code>end</code> are not primary keys, they are indexes in the result set. Thus, <code>0</code> refers to the first result in the set. Setting both <code>start</code> and <code>end</code> to <code>QueryUtil#ALL_POS</code> will return the full result set. If <code>orderByComparator</code> is specified, then the query will include the given ORDER BY logic. If <code>orderByComparator</code> is absent, then the query will include the default ORDER BY logic from <code>LayoutModelImpl</code>.
	 * </p>
	 *
	 * @param uuid the uuid
	 * @param start the lower bound of the range of layouts
	 * @param end the upper bound of the range of layouts (not inclusive)
	 * @return the range of matching layouts
	 */
	public java.util.List<Layout> findByUuid(String uuid, int start, int end);

	/**
	 * Returns an ordered range of all the layouts where uuid = &#63;.
	 *
	 * <p>
	 * Useful when paginating results. Returns a maximum of <code>end - start</code> instances. <code>start</code> and <code>end</code> are not primary keys, they are indexes in the result set. Thus, <code>0</code> refers to the first result in the set. Setting both <code>start</code> and <code>end</code> to <code>QueryUtil#ALL_POS</code> will return the full result set. If <code>orderByComparator</code> is specified, then the query will include the given ORDER BY logic. If <code>orderByComparator</code> is absent, then the query will include the default ORDER BY logic from <code>LayoutModelImpl</code>.
	 * </p>
	 *
	 * @param uuid the uuid
	 * @param start the lower bound of the range of layouts
	 * @param end the upper bound of the range of layouts (not inclusive)
	 * @param orderByComparator the comparator to order the results by (optionally <code>null</code>)
	 * @return the ordered range of matching layouts
	 */
	public java.util.List<Layout> findByUuid(
		String uuid, int start, int end,
		com.liferay.portal.kernel.util.OrderByComparator<Layout>
			orderByComparator);

	/**
	 * Returns an ordered range of all the layouts where uuid = &#63;.
	 *
	 * <p>
	 * Useful when paginating results. Returns a maximum of <code>end - start</code> instances. <code>start</code> and <code>end</code> are not primary keys, they are indexes in the result set. Thus, <code>0</code> refers to the first result in the set. Setting both <code>start</code> and <code>end</code> to <code>QueryUtil#ALL_POS</code> will return the full result set. If <code>orderByComparator</code> is specified, then the query will include the given ORDER BY logic. If <code>orderByComparator</code> is absent, then the query will include the default ORDER BY logic from <code>LayoutModelImpl</code>.
	 * </p>
	 *
	 * @param uuid the uuid
	 * @param start the lower bound of the range of layouts
	 * @param end the upper bound of the range of layouts (not inclusive)
	 * @param orderByComparator the comparator to order the results by (optionally <code>null</code>)
	 * @param useFinderCache whether to use the finder cache
	 * @return the ordered range of matching layouts
	 */
	public java.util.List<Layout> findByUuid(
		String uuid, int start, int end,
		com.liferay.portal.kernel.util.OrderByComparator<Layout>
			orderByComparator,
		boolean useFinderCache);

	/**
	 * Returns the first layout in the ordered set where uuid = &#63;.
	 *
	 * @param uuid the uuid
	 * @param orderByComparator the comparator to order the set by (optionally <code>null</code>)
	 * @return the first matching layout
	 * @throws NoSuchLayoutException if a matching layout could not be found
	 */
	public Layout findByUuid_First(
			String uuid,
			com.liferay.portal.kernel.util.OrderByComparator<Layout>
				orderByComparator)
		throws NoSuchLayoutException;

	/**
	 * Returns the first layout in the ordered set where uuid = &#63;.
	 *
	 * @param uuid the uuid
	 * @param orderByComparator the comparator to order the set by (optionally <code>null</code>)
	 * @return the first matching layout, or <code>null</code> if a matching layout could not be found
	 */
	public Layout fetchByUuid_First(
		String uuid,
		com.liferay.portal.kernel.util.OrderByComparator<Layout>
			orderByComparator);

	/**
	 * Returns the last layout in the ordered set where uuid = &#63;.
	 *
	 * @param uuid the uuid
	 * @param orderByComparator the comparator to order the set by (optionally <code>null</code>)
	 * @return the last matching layout
	 * @throws NoSuchLayoutException if a matching layout could not be found
	 */
	public Layout findByUuid_Last(
			String uuid,
			com.liferay.portal.kernel.util.OrderByComparator<Layout>
				orderByComparator)
		throws NoSuchLayoutException;

	/**
	 * Returns the last layout in the ordered set where uuid = &#63;.
	 *
	 * @param uuid the uuid
	 * @param orderByComparator the comparator to order the set by (optionally <code>null</code>)
	 * @return the last matching layout, or <code>null</code> if a matching layout could not be found
	 */
	public Layout fetchByUuid_Last(
		String uuid,
		com.liferay.portal.kernel.util.OrderByComparator<Layout>
			orderByComparator);

	/**
	 * Returns the layouts before and after the current layout in the ordered set where uuid = &#63;.
	 *
	 * @param plid the primary key of the current layout
	 * @param uuid the uuid
	 * @param orderByComparator the comparator to order the set by (optionally <code>null</code>)
	 * @return the previous, current, and next layout
	 * @throws NoSuchLayoutException if a layout with the primary key could not be found
	 */
	public Layout[] findByUuid_PrevAndNext(
			long plid, String uuid,
			com.liferay.portal.kernel.util.OrderByComparator<Layout>
				orderByComparator)
		throws NoSuchLayoutException;

	/**
	 * Removes all the layouts where uuid = &#63; from the database.
	 *
	 * @param uuid the uuid
	 */
	public void removeByUuid(String uuid);

	/**
	 * Returns the number of layouts where uuid = &#63;.
	 *
	 * @param uuid the uuid
	 * @return the number of matching layouts
	 */
	public int countByUuid(String uuid);

	/**
	 * Returns the layout where uuid = &#63; and groupId = &#63; and privateLayout = &#63; or throws a <code>NoSuchLayoutException</code> if it could not be found.
	 *
	 * @param uuid the uuid
	 * @param groupId the group ID
	 * @param privateLayout the private layout
	 * @return the matching layout
	 * @throws NoSuchLayoutException if a matching layout could not be found
	 */
	public Layout findByUUID_G_P(
			String uuid, long groupId, boolean privateLayout)
		throws NoSuchLayoutException;

	/**
	 * Returns the layout where uuid = &#63; and groupId = &#63; and privateLayout = &#63; or returns <code>null</code> if it could not be found. Uses the finder cache.
	 *
	 * @param uuid the uuid
	 * @param groupId the group ID
	 * @param privateLayout the private layout
	 * @return the matching layout, or <code>null</code> if a matching layout could not be found
	 */
	public Layout fetchByUUID_G_P(
		String uuid, long groupId, boolean privateLayout);

	/**
	 * Returns the layout where uuid = &#63; and groupId = &#63; and privateLayout = &#63; or returns <code>null</code> if it could not be found, optionally using the finder cache.
	 *
	 * @param uuid the uuid
	 * @param groupId the group ID
	 * @param privateLayout the private layout
	 * @param useFinderCache whether to use the finder cache
	 * @return the matching layout, or <code>null</code> if a matching layout could not be found
	 */
	public Layout fetchByUUID_G_P(
		String uuid, long groupId, boolean privateLayout,
		boolean useFinderCache);

	/**
	 * Removes the layout where uuid = &#63; and groupId = &#63; and privateLayout = &#63; from the database.
	 *
	 * @param uuid the uuid
	 * @param groupId the group ID
	 * @param privateLayout the private layout
	 * @return the layout that was removed
	 */
	public Layout removeByUUID_G_P(
			String uuid, long groupId, boolean privateLayout)
		throws NoSuchLayoutException;

	/**
	 * Returns the number of layouts where uuid = &#63; and groupId = &#63; and privateLayout = &#63;.
	 *
	 * @param uuid the uuid
	 * @param groupId the group ID
	 * @param privateLayout the private layout
	 * @return the number of matching layouts
	 */
	public int countByUUID_G_P(
		String uuid, long groupId, boolean privateLayout);

	/**
	 * Returns all the layouts where uuid = &#63; and companyId = &#63;.
	 *
	 * @param uuid the uuid
	 * @param companyId the company ID
	 * @return the matching layouts
	 */
	public java.util.List<Layout> findByUuid_C(String uuid, long companyId);

	/**
	 * Returns a range of all the layouts where uuid = &#63; and companyId = &#63;.
	 *
	 * <p>
	 * Useful when paginating results. Returns a maximum of <code>end - start</code> instances. <code>start</code> and <code>end</code> are not primary keys, they are indexes in the result set. Thus, <code>0</code> refers to the first result in the set. Setting both <code>start</code> and <code>end</code> to <code>QueryUtil#ALL_POS</code> will return the full result set. If <code>orderByComparator</code> is specified, then the query will include the given ORDER BY logic. If <code>orderByComparator</code> is absent, then the query will include the default ORDER BY logic from <code>LayoutModelImpl</code>.
	 * </p>
	 *
	 * @param uuid the uuid
	 * @param companyId the company ID
	 * @param start the lower bound of the range of layouts
	 * @param end the upper bound of the range of layouts (not inclusive)
	 * @return the range of matching layouts
	 */
	public java.util.List<Layout> findByUuid_C(
		String uuid, long companyId, int start, int end);

	/**
	 * Returns an ordered range of all the layouts where uuid = &#63; and companyId = &#63;.
	 *
	 * <p>
	 * Useful when paginating results. Returns a maximum of <code>end - start</code> instances. <code>start</code> and <code>end</code> are not primary keys, they are indexes in the result set. Thus, <code>0</code> refers to the first result in the set. Setting both <code>start</code> and <code>end</code> to <code>QueryUtil#ALL_POS</code> will return the full result set. If <code>orderByComparator</code> is specified, then the query will include the given ORDER BY logic. If <code>orderByComparator</code> is absent, then the query will include the default ORDER BY logic from <code>LayoutModelImpl</code>.
	 * </p>
	 *
	 * @param uuid the uuid
	 * @param companyId the company ID
	 * @param start the lower bound of the range of layouts
	 * @param end the upper bound of the range of layouts (not inclusive)
	 * @param orderByComparator the comparator to order the results by (optionally <code>null</code>)
	 * @return the ordered range of matching layouts
	 */
	public java.util.List<Layout> findByUuid_C(
		String uuid, long companyId, int start, int end,
		com.liferay.portal.kernel.util.OrderByComparator<Layout>
			orderByComparator);

	/**
	 * Returns an ordered range of all the layouts where uuid = &#63; and companyId = &#63;.
	 *
	 * <p>
	 * Useful when paginating results. Returns a maximum of <code>end - start</code> instances. <code>start</code> and <code>end</code> are not primary keys, they are indexes in the result set. Thus, <code>0</code> refers to the first result in the set. Setting both <code>start</code> and <code>end</code> to <code>QueryUtil#ALL_POS</code> will return the full result set. If <code>orderByComparator</code> is specified, then the query will include the given ORDER BY logic. If <code>orderByComparator</code> is absent, then the query will include the default ORDER BY logic from <code>LayoutModelImpl</code>.
	 * </p>
	 *
	 * @param uuid the uuid
	 * @param companyId the company ID
	 * @param start the lower bound of the range of layouts
	 * @param end the upper bound of the range of layouts (not inclusive)
	 * @param orderByComparator the comparator to order the results by (optionally <code>null</code>)
	 * @param useFinderCache whether to use the finder cache
	 * @return the ordered range of matching layouts
	 */
	public java.util.List<Layout> findByUuid_C(
		String uuid, long companyId, int start, int end,
		com.liferay.portal.kernel.util.OrderByComparator<Layout>
			orderByComparator,
		boolean useFinderCache);

	/**
	 * Returns the first layout in the ordered set where uuid = &#63; and companyId = &#63;.
	 *
	 * @param uuid the uuid
	 * @param companyId the company ID
	 * @param orderByComparator the comparator to order the set by (optionally <code>null</code>)
	 * @return the first matching layout
	 * @throws NoSuchLayoutException if a matching layout could not be found
	 */
	public Layout findByUuid_C_First(
			String uuid, long companyId,
			com.liferay.portal.kernel.util.OrderByComparator<Layout>
				orderByComparator)
		throws NoSuchLayoutException;

	/**
	 * Returns the first layout in the ordered set where uuid = &#63; and companyId = &#63;.
	 *
	 * @param uuid the uuid
	 * @param companyId the company ID
	 * @param orderByComparator the comparator to order the set by (optionally <code>null</code>)
	 * @return the first matching layout, or <code>null</code> if a matching layout could not be found
	 */
	public Layout fetchByUuid_C_First(
		String uuid, long companyId,
		com.liferay.portal.kernel.util.OrderByComparator<Layout>
			orderByComparator);

	/**
	 * Returns the last layout in the ordered set where uuid = &#63; and companyId = &#63;.
	 *
	 * @param uuid the uuid
	 * @param companyId the company ID
	 * @param orderByComparator the comparator to order the set by (optionally <code>null</code>)
	 * @return the last matching layout
	 * @throws NoSuchLayoutException if a matching layout could not be found
	 */
	public Layout findByUuid_C_Last(
			String uuid, long companyId,
			com.liferay.portal.kernel.util.OrderByComparator<Layout>
				orderByComparator)
		throws NoSuchLayoutException;

	/**
	 * Returns the last layout in the ordered set where uuid = &#63; and companyId = &#63;.
	 *
	 * @param uuid the uuid
	 * @param companyId the company ID
	 * @param orderByComparator the comparator to order the set by (optionally <code>null</code>)
	 * @return the last matching layout, or <code>null</code> if a matching layout could not be found
	 */
	public Layout fetchByUuid_C_Last(
		String uuid, long companyId,
		com.liferay.portal.kernel.util.OrderByComparator<Layout>
			orderByComparator);

	/**
	 * Returns the layouts before and after the current layout in the ordered set where uuid = &#63; and companyId = &#63;.
	 *
	 * @param plid the primary key of the current layout
	 * @param uuid the uuid
	 * @param companyId the company ID
	 * @param orderByComparator the comparator to order the set by (optionally <code>null</code>)
	 * @return the previous, current, and next layout
	 * @throws NoSuchLayoutException if a layout with the primary key could not be found
	 */
	public Layout[] findByUuid_C_PrevAndNext(
			long plid, String uuid, long companyId,
			com.liferay.portal.kernel.util.OrderByComparator<Layout>
				orderByComparator)
		throws NoSuchLayoutException;

	/**
	 * Removes all the layouts where uuid = &#63; and companyId = &#63; from the database.
	 *
	 * @param uuid the uuid
	 * @param companyId the company ID
	 */
	public void removeByUuid_C(String uuid, long companyId);

	/**
	 * Returns the number of layouts where uuid = &#63; and companyId = &#63;.
	 *
	 * @param uuid the uuid
	 * @param companyId the company ID
	 * @return the number of matching layouts
	 */
	public int countByUuid_C(String uuid, long companyId);

	/**
	 * Returns all the layouts where groupId = &#63;.
	 *
	 * @param groupId the group ID
	 * @return the matching layouts
	 */
	public java.util.List<Layout> findByGroupId(long groupId);

	/**
	 * Returns a range of all the layouts where groupId = &#63;.
	 *
	 * <p>
	 * Useful when paginating results. Returns a maximum of <code>end - start</code> instances. <code>start</code> and <code>end</code> are not primary keys, they are indexes in the result set. Thus, <code>0</code> refers to the first result in the set. Setting both <code>start</code> and <code>end</code> to <code>QueryUtil#ALL_POS</code> will return the full result set. If <code>orderByComparator</code> is specified, then the query will include the given ORDER BY logic. If <code>orderByComparator</code> is absent, then the query will include the default ORDER BY logic from <code>LayoutModelImpl</code>.
	 * </p>
	 *
	 * @param groupId the group ID
	 * @param start the lower bound of the range of layouts
	 * @param end the upper bound of the range of layouts (not inclusive)
	 * @return the range of matching layouts
	 */
	public java.util.List<Layout> findByGroupId(
		long groupId, int start, int end);

	/**
	 * Returns an ordered range of all the layouts where groupId = &#63;.
	 *
	 * <p>
	 * Useful when paginating results. Returns a maximum of <code>end - start</code> instances. <code>start</code> and <code>end</code> are not primary keys, they are indexes in the result set. Thus, <code>0</code> refers to the first result in the set. Setting both <code>start</code> and <code>end</code> to <code>QueryUtil#ALL_POS</code> will return the full result set. If <code>orderByComparator</code> is specified, then the query will include the given ORDER BY logic. If <code>orderByComparator</code> is absent, then the query will include the default ORDER BY logic from <code>LayoutModelImpl</code>.
	 * </p>
	 *
	 * @param groupId the group ID
	 * @param start the lower bound of the range of layouts
	 * @param end the upper bound of the range of layouts (not inclusive)
	 * @param orderByComparator the comparator to order the results by (optionally <code>null</code>)
	 * @return the ordered range of matching layouts
	 */
	public java.util.List<Layout> findByGroupId(
		long groupId, int start, int end,
		com.liferay.portal.kernel.util.OrderByComparator<Layout>
			orderByComparator);

	/**
	 * Returns an ordered range of all the layouts where groupId = &#63;.
	 *
	 * <p>
	 * Useful when paginating results. Returns a maximum of <code>end - start</code> instances. <code>start</code> and <code>end</code> are not primary keys, they are indexes in the result set. Thus, <code>0</code> refers to the first result in the set. Setting both <code>start</code> and <code>end</code> to <code>QueryUtil#ALL_POS</code> will return the full result set. If <code>orderByComparator</code> is specified, then the query will include the given ORDER BY logic. If <code>orderByComparator</code> is absent, then the query will include the default ORDER BY logic from <code>LayoutModelImpl</code>.
	 * </p>
	 *
	 * @param groupId the group ID
	 * @param start the lower bound of the range of layouts
	 * @param end the upper bound of the range of layouts (not inclusive)
	 * @param orderByComparator the comparator to order the results by (optionally <code>null</code>)
	 * @param useFinderCache whether to use the finder cache
	 * @return the ordered range of matching layouts
	 */
	public java.util.List<Layout> findByGroupId(
		long groupId, int start, int end,
		com.liferay.portal.kernel.util.OrderByComparator<Layout>
			orderByComparator,
		boolean useFinderCache);

	/**
	 * Returns the first layout in the ordered set where groupId = &#63;.
	 *
	 * @param groupId the group ID
	 * @param orderByComparator the comparator to order the set by (optionally <code>null</code>)
	 * @return the first matching layout
	 * @throws NoSuchLayoutException if a matching layout could not be found
	 */
	public Layout findByGroupId_First(
			long groupId,
			com.liferay.portal.kernel.util.OrderByComparator<Layout>
				orderByComparator)
		throws NoSuchLayoutException;

	/**
	 * Returns the first layout in the ordered set where groupId = &#63;.
	 *
	 * @param groupId the group ID
	 * @param orderByComparator the comparator to order the set by (optionally <code>null</code>)
	 * @return the first matching layout, or <code>null</code> if a matching layout could not be found
	 */
	public Layout fetchByGroupId_First(
		long groupId,
		com.liferay.portal.kernel.util.OrderByComparator<Layout>
			orderByComparator);

	/**
	 * Returns the last layout in the ordered set where groupId = &#63;.
	 *
	 * @param groupId the group ID
	 * @param orderByComparator the comparator to order the set by (optionally <code>null</code>)
	 * @return the last matching layout
	 * @throws NoSuchLayoutException if a matching layout could not be found
	 */
	public Layout findByGroupId_Last(
			long groupId,
			com.liferay.portal.kernel.util.OrderByComparator<Layout>
				orderByComparator)
		throws NoSuchLayoutException;

	/**
	 * Returns the last layout in the ordered set where groupId = &#63;.
	 *
	 * @param groupId the group ID
	 * @param orderByComparator the comparator to order the set by (optionally <code>null</code>)
	 * @return the last matching layout, or <code>null</code> if a matching layout could not be found
	 */
	public Layout fetchByGroupId_Last(
		long groupId,
		com.liferay.portal.kernel.util.OrderByComparator<Layout>
			orderByComparator);

	/**
	 * Returns the layouts before and after the current layout in the ordered set where groupId = &#63;.
	 *
	 * @param plid the primary key of the current layout
	 * @param groupId the group ID
	 * @param orderByComparator the comparator to order the set by (optionally <code>null</code>)
	 * @return the previous, current, and next layout
	 * @throws NoSuchLayoutException if a layout with the primary key could not be found
	 */
	public Layout[] findByGroupId_PrevAndNext(
			long plid, long groupId,
			com.liferay.portal.kernel.util.OrderByComparator<Layout>
				orderByComparator)
		throws NoSuchLayoutException;

	/**
	 * Returns all the layouts that the user has permission to view where groupId = &#63;.
	 *
	 * @param groupId the group ID
	 * @return the matching layouts that the user has permission to view
	 */
	public java.util.List<Layout> filterFindByGroupId(long groupId);

	/**
	 * Returns a range of all the layouts that the user has permission to view where groupId = &#63;.
	 *
	 * <p>
	 * Useful when paginating results. Returns a maximum of <code>end - start</code> instances. <code>start</code> and <code>end</code> are not primary keys, they are indexes in the result set. Thus, <code>0</code> refers to the first result in the set. Setting both <code>start</code> and <code>end</code> to <code>QueryUtil#ALL_POS</code> will return the full result set. If <code>orderByComparator</code> is specified, then the query will include the given ORDER BY logic. If <code>orderByComparator</code> is absent, then the query will include the default ORDER BY logic from <code>LayoutModelImpl</code>.
	 * </p>
	 *
	 * @param groupId the group ID
	 * @param start the lower bound of the range of layouts
	 * @param end the upper bound of the range of layouts (not inclusive)
	 * @return the range of matching layouts that the user has permission to view
	 */
	public java.util.List<Layout> filterFindByGroupId(
		long groupId, int start, int end);

	/**
	 * Returns an ordered range of all the layouts that the user has permissions to view where groupId = &#63;.
	 *
	 * <p>
	 * Useful when paginating results. Returns a maximum of <code>end - start</code> instances. <code>start</code> and <code>end</code> are not primary keys, they are indexes in the result set. Thus, <code>0</code> refers to the first result in the set. Setting both <code>start</code> and <code>end</code> to <code>QueryUtil#ALL_POS</code> will return the full result set. If <code>orderByComparator</code> is specified, then the query will include the given ORDER BY logic. If <code>orderByComparator</code> is absent, then the query will include the default ORDER BY logic from <code>LayoutModelImpl</code>.
	 * </p>
	 *
	 * @param groupId the group ID
	 * @param start the lower bound of the range of layouts
	 * @param end the upper bound of the range of layouts (not inclusive)
	 * @param orderByComparator the comparator to order the results by (optionally <code>null</code>)
	 * @return the ordered range of matching layouts that the user has permission to view
	 */
	public java.util.List<Layout> filterFindByGroupId(
		long groupId, int start, int end,
		com.liferay.portal.kernel.util.OrderByComparator<Layout>
			orderByComparator);

	/**
	 * Returns the layouts before and after the current layout in the ordered set of layouts that the user has permission to view where groupId = &#63;.
	 *
	 * @param plid the primary key of the current layout
	 * @param groupId the group ID
	 * @param orderByComparator the comparator to order the set by (optionally <code>null</code>)
	 * @return the previous, current, and next layout
	 * @throws NoSuchLayoutException if a layout with the primary key could not be found
	 */
	public Layout[] filterFindByGroupId_PrevAndNext(
			long plid, long groupId,
			com.liferay.portal.kernel.util.OrderByComparator<Layout>
				orderByComparator)
		throws NoSuchLayoutException;

	/**
	 * Removes all the layouts where groupId = &#63; from the database.
	 *
	 * @param groupId the group ID
	 */
	public void removeByGroupId(long groupId);

	/**
	 * Returns the number of layouts where groupId = &#63;.
	 *
	 * @param groupId the group ID
	 * @return the number of matching layouts
	 */
	public int countByGroupId(long groupId);

	/**
	 * Returns the number of layouts that the user has permission to view where groupId = &#63;.
	 *
	 * @param groupId the group ID
	 * @return the number of matching layouts that the user has permission to view
	 */
	public int filterCountByGroupId(long groupId);

	/**
	 * Returns all the layouts where companyId = &#63;.
	 *
	 * @param companyId the company ID
	 * @return the matching layouts
	 */
	public java.util.List<Layout> findByCompanyId(long companyId);

	/**
	 * Returns a range of all the layouts where companyId = &#63;.
	 *
	 * <p>
	 * Useful when paginating results. Returns a maximum of <code>end - start</code> instances. <code>start</code> and <code>end</code> are not primary keys, they are indexes in the result set. Thus, <code>0</code> refers to the first result in the set. Setting both <code>start</code> and <code>end</code> to <code>QueryUtil#ALL_POS</code> will return the full result set. If <code>orderByComparator</code> is specified, then the query will include the given ORDER BY logic. If <code>orderByComparator</code> is absent, then the query will include the default ORDER BY logic from <code>LayoutModelImpl</code>.
	 * </p>
	 *
	 * @param companyId the company ID
	 * @param start the lower bound of the range of layouts
	 * @param end the upper bound of the range of layouts (not inclusive)
	 * @return the range of matching layouts
	 */
	public java.util.List<Layout> findByCompanyId(
		long companyId, int start, int end);

	/**
	 * Returns an ordered range of all the layouts where companyId = &#63;.
	 *
	 * <p>
	 * Useful when paginating results. Returns a maximum of <code>end - start</code> instances. <code>start</code> and <code>end</code> are not primary keys, they are indexes in the result set. Thus, <code>0</code> refers to the first result in the set. Setting both <code>start</code> and <code>end</code> to <code>QueryUtil#ALL_POS</code> will return the full result set. If <code>orderByComparator</code> is specified, then the query will include the given ORDER BY logic. If <code>orderByComparator</code> is absent, then the query will include the default ORDER BY logic from <code>LayoutModelImpl</code>.
	 * </p>
	 *
	 * @param companyId the company ID
	 * @param start the lower bound of the range of layouts
	 * @param end the upper bound of the range of layouts (not inclusive)
	 * @param orderByComparator the comparator to order the results by (optionally <code>null</code>)
	 * @return the ordered range of matching layouts
	 */
	public java.util.List<Layout> findByCompanyId(
		long companyId, int start, int end,
		com.liferay.portal.kernel.util.OrderByComparator<Layout>
			orderByComparator);

	/**
	 * Returns an ordered range of all the layouts where companyId = &#63;.
	 *
	 * <p>
	 * Useful when paginating results. Returns a maximum of <code>end - start</code> instances. <code>start</code> and <code>end</code> are not primary keys, they are indexes in the result set. Thus, <code>0</code> refers to the first result in the set. Setting both <code>start</code> and <code>end</code> to <code>QueryUtil#ALL_POS</code> will return the full result set. If <code>orderByComparator</code> is specified, then the query will include the given ORDER BY logic. If <code>orderByComparator</code> is absent, then the query will include the default ORDER BY logic from <code>LayoutModelImpl</code>.
	 * </p>
	 *
	 * @param companyId the company ID
	 * @param start the lower bound of the range of layouts
	 * @param end the upper bound of the range of layouts (not inclusive)
	 * @param orderByComparator the comparator to order the results by (optionally <code>null</code>)
	 * @param useFinderCache whether to use the finder cache
	 * @return the ordered range of matching layouts
	 */
	public java.util.List<Layout> findByCompanyId(
		long companyId, int start, int end,
		com.liferay.portal.kernel.util.OrderByComparator<Layout>
			orderByComparator,
		boolean useFinderCache);

	/**
	 * Returns the first layout in the ordered set where companyId = &#63;.
	 *
	 * @param companyId the company ID
	 * @param orderByComparator the comparator to order the set by (optionally <code>null</code>)
	 * @return the first matching layout
	 * @throws NoSuchLayoutException if a matching layout could not be found
	 */
	public Layout findByCompanyId_First(
			long companyId,
			com.liferay.portal.kernel.util.OrderByComparator<Layout>
				orderByComparator)
		throws NoSuchLayoutException;

	/**
	 * Returns the first layout in the ordered set where companyId = &#63;.
	 *
	 * @param companyId the company ID
	 * @param orderByComparator the comparator to order the set by (optionally <code>null</code>)
	 * @return the first matching layout, or <code>null</code> if a matching layout could not be found
	 */
	public Layout fetchByCompanyId_First(
		long companyId,
		com.liferay.portal.kernel.util.OrderByComparator<Layout>
			orderByComparator);

	/**
	 * Returns the last layout in the ordered set where companyId = &#63;.
	 *
	 * @param companyId the company ID
	 * @param orderByComparator the comparator to order the set by (optionally <code>null</code>)
	 * @return the last matching layout
	 * @throws NoSuchLayoutException if a matching layout could not be found
	 */
	public Layout findByCompanyId_Last(
			long companyId,
			com.liferay.portal.kernel.util.OrderByComparator<Layout>
				orderByComparator)
		throws NoSuchLayoutException;

	/**
	 * Returns the last layout in the ordered set where companyId = &#63;.
	 *
	 * @param companyId the company ID
	 * @param orderByComparator the comparator to order the set by (optionally <code>null</code>)
	 * @return the last matching layout, or <code>null</code> if a matching layout could not be found
	 */
	public Layout fetchByCompanyId_Last(
		long companyId,
		com.liferay.portal.kernel.util.OrderByComparator<Layout>
			orderByComparator);

	/**
	 * Returns the layouts before and after the current layout in the ordered set where companyId = &#63;.
	 *
	 * @param plid the primary key of the current layout
	 * @param companyId the company ID
	 * @param orderByComparator the comparator to order the set by (optionally <code>null</code>)
	 * @return the previous, current, and next layout
	 * @throws NoSuchLayoutException if a layout with the primary key could not be found
	 */
	public Layout[] findByCompanyId_PrevAndNext(
			long plid, long companyId,
			com.liferay.portal.kernel.util.OrderByComparator<Layout>
				orderByComparator)
		throws NoSuchLayoutException;

	/**
	 * Removes all the layouts where companyId = &#63; from the database.
	 *
	 * @param companyId the company ID
	 */
	public void removeByCompanyId(long companyId);

	/**
	 * Returns the number of layouts where companyId = &#63;.
	 *
	 * @param companyId the company ID
	 * @return the number of matching layouts
	 */
	public int countByCompanyId(long companyId);

	/**
	 * Returns all the layouts where parentPlid = &#63;.
	 *
	 * @param parentPlid the parent plid
	 * @return the matching layouts
	 */
	public java.util.List<Layout> findByParentPlid(long parentPlid);

	/**
	 * Returns a range of all the layouts where parentPlid = &#63;.
	 *
	 * <p>
	 * Useful when paginating results. Returns a maximum of <code>end - start</code> instances. <code>start</code> and <code>end</code> are not primary keys, they are indexes in the result set. Thus, <code>0</code> refers to the first result in the set. Setting both <code>start</code> and <code>end</code> to <code>QueryUtil#ALL_POS</code> will return the full result set. If <code>orderByComparator</code> is specified, then the query will include the given ORDER BY logic. If <code>orderByComparator</code> is absent, then the query will include the default ORDER BY logic from <code>LayoutModelImpl</code>.
	 * </p>
	 *
	 * @param parentPlid the parent plid
	 * @param start the lower bound of the range of layouts
	 * @param end the upper bound of the range of layouts (not inclusive)
	 * @return the range of matching layouts
	 */
	public java.util.List<Layout> findByParentPlid(
		long parentPlid, int start, int end);

	/**
	 * Returns an ordered range of all the layouts where parentPlid = &#63;.
	 *
	 * <p>
	 * Useful when paginating results. Returns a maximum of <code>end - start</code> instances. <code>start</code> and <code>end</code> are not primary keys, they are indexes in the result set. Thus, <code>0</code> refers to the first result in the set. Setting both <code>start</code> and <code>end</code> to <code>QueryUtil#ALL_POS</code> will return the full result set. If <code>orderByComparator</code> is specified, then the query will include the given ORDER BY logic. If <code>orderByComparator</code> is absent, then the query will include the default ORDER BY logic from <code>LayoutModelImpl</code>.
	 * </p>
	 *
	 * @param parentPlid the parent plid
	 * @param start the lower bound of the range of layouts
	 * @param end the upper bound of the range of layouts (not inclusive)
	 * @param orderByComparator the comparator to order the results by (optionally <code>null</code>)
	 * @return the ordered range of matching layouts
	 */
	public java.util.List<Layout> findByParentPlid(
		long parentPlid, int start, int end,
		com.liferay.portal.kernel.util.OrderByComparator<Layout>
			orderByComparator);

	/**
	 * Returns an ordered range of all the layouts where parentPlid = &#63;.
	 *
	 * <p>
	 * Useful when paginating results. Returns a maximum of <code>end - start</code> instances. <code>start</code> and <code>end</code> are not primary keys, they are indexes in the result set. Thus, <code>0</code> refers to the first result in the set. Setting both <code>start</code> and <code>end</code> to <code>QueryUtil#ALL_POS</code> will return the full result set. If <code>orderByComparator</code> is specified, then the query will include the given ORDER BY logic. If <code>orderByComparator</code> is absent, then the query will include the default ORDER BY logic from <code>LayoutModelImpl</code>.
	 * </p>
	 *
	 * @param parentPlid the parent plid
	 * @param start the lower bound of the range of layouts
	 * @param end the upper bound of the range of layouts (not inclusive)
	 * @param orderByComparator the comparator to order the results by (optionally <code>null</code>)
	 * @param useFinderCache whether to use the finder cache
	 * @return the ordered range of matching layouts
	 */
	public java.util.List<Layout> findByParentPlid(
		long parentPlid, int start, int end,
		com.liferay.portal.kernel.util.OrderByComparator<Layout>
			orderByComparator,
		boolean useFinderCache);

	/**
	 * Returns the first layout in the ordered set where parentPlid = &#63;.
	 *
	 * @param parentPlid the parent plid
	 * @param orderByComparator the comparator to order the set by (optionally <code>null</code>)
	 * @return the first matching layout
	 * @throws NoSuchLayoutException if a matching layout could not be found
	 */
	public Layout findByParentPlid_First(
			long parentPlid,
			com.liferay.portal.kernel.util.OrderByComparator<Layout>
				orderByComparator)
		throws NoSuchLayoutException;

	/**
	 * Returns the first layout in the ordered set where parentPlid = &#63;.
	 *
	 * @param parentPlid the parent plid
	 * @param orderByComparator the comparator to order the set by (optionally <code>null</code>)
	 * @return the first matching layout, or <code>null</code> if a matching layout could not be found
	 */
	public Layout fetchByParentPlid_First(
		long parentPlid,
		com.liferay.portal.kernel.util.OrderByComparator<Layout>
			orderByComparator);

	/**
	 * Returns the last layout in the ordered set where parentPlid = &#63;.
	 *
	 * @param parentPlid the parent plid
	 * @param orderByComparator the comparator to order the set by (optionally <code>null</code>)
	 * @return the last matching layout
	 * @throws NoSuchLayoutException if a matching layout could not be found
	 */
	public Layout findByParentPlid_Last(
			long parentPlid,
			com.liferay.portal.kernel.util.OrderByComparator<Layout>
				orderByComparator)
		throws NoSuchLayoutException;

	/**
	 * Returns the last layout in the ordered set where parentPlid = &#63;.
	 *
	 * @param parentPlid the parent plid
	 * @param orderByComparator the comparator to order the set by (optionally <code>null</code>)
	 * @return the last matching layout, or <code>null</code> if a matching layout could not be found
	 */
	public Layout fetchByParentPlid_Last(
		long parentPlid,
		com.liferay.portal.kernel.util.OrderByComparator<Layout>
			orderByComparator);

	/**
	 * Returns the layouts before and after the current layout in the ordered set where parentPlid = &#63;.
	 *
	 * @param plid the primary key of the current layout
	 * @param parentPlid the parent plid
	 * @param orderByComparator the comparator to order the set by (optionally <code>null</code>)
	 * @return the previous, current, and next layout
	 * @throws NoSuchLayoutException if a layout with the primary key could not be found
	 */
	public Layout[] findByParentPlid_PrevAndNext(
			long plid, long parentPlid,
			com.liferay.portal.kernel.util.OrderByComparator<Layout>
				orderByComparator)
		throws NoSuchLayoutException;

	/**
	 * Removes all the layouts where parentPlid = &#63; from the database.
	 *
	 * @param parentPlid the parent plid
	 */
	public void removeByParentPlid(long parentPlid);

	/**
	 * Returns the number of layouts where parentPlid = &#63;.
	 *
	 * @param parentPlid the parent plid
	 * @return the number of matching layouts
	 */
	public int countByParentPlid(long parentPlid);

	/**
	 * Returns all the layouts where iconImageId = &#63;.
	 *
	 * @param iconImageId the icon image ID
	 * @return the matching layouts
	 */
	public java.util.List<Layout> findByIconImageId(long iconImageId);

	/**
	 * Returns a range of all the layouts where iconImageId = &#63;.
	 *
	 * <p>
	 * Useful when paginating results. Returns a maximum of <code>end - start</code> instances. <code>start</code> and <code>end</code> are not primary keys, they are indexes in the result set. Thus, <code>0</code> refers to the first result in the set. Setting both <code>start</code> and <code>end</code> to <code>QueryUtil#ALL_POS</code> will return the full result set. If <code>orderByComparator</code> is specified, then the query will include the given ORDER BY logic. If <code>orderByComparator</code> is absent, then the query will include the default ORDER BY logic from <code>LayoutModelImpl</code>.
	 * </p>
	 *
	 * @param iconImageId the icon image ID
	 * @param start the lower bound of the range of layouts
	 * @param end the upper bound of the range of layouts (not inclusive)
	 * @return the range of matching layouts
	 */
	public java.util.List<Layout> findByIconImageId(
		long iconImageId, int start, int end);

	/**
	 * Returns an ordered range of all the layouts where iconImageId = &#63;.
	 *
	 * <p>
	 * Useful when paginating results. Returns a maximum of <code>end - start</code> instances. <code>start</code> and <code>end</code> are not primary keys, they are indexes in the result set. Thus, <code>0</code> refers to the first result in the set. Setting both <code>start</code> and <code>end</code> to <code>QueryUtil#ALL_POS</code> will return the full result set. If <code>orderByComparator</code> is specified, then the query will include the given ORDER BY logic. If <code>orderByComparator</code> is absent, then the query will include the default ORDER BY logic from <code>LayoutModelImpl</code>.
	 * </p>
	 *
	 * @param iconImageId the icon image ID
	 * @param start the lower bound of the range of layouts
	 * @param end the upper bound of the range of layouts (not inclusive)
	 * @param orderByComparator the comparator to order the results by (optionally <code>null</code>)
	 * @return the ordered range of matching layouts
	 */
	public java.util.List<Layout> findByIconImageId(
		long iconImageId, int start, int end,
		com.liferay.portal.kernel.util.OrderByComparator<Layout>
			orderByComparator);

	/**
	 * Returns an ordered range of all the layouts where iconImageId = &#63;.
	 *
	 * <p>
	 * Useful when paginating results. Returns a maximum of <code>end - start</code> instances. <code>start</code> and <code>end</code> are not primary keys, they are indexes in the result set. Thus, <code>0</code> refers to the first result in the set. Setting both <code>start</code> and <code>end</code> to <code>QueryUtil#ALL_POS</code> will return the full result set. If <code>orderByComparator</code> is specified, then the query will include the given ORDER BY logic. If <code>orderByComparator</code> is absent, then the query will include the default ORDER BY logic from <code>LayoutModelImpl</code>.
	 * </p>
	 *
	 * @param iconImageId the icon image ID
	 * @param start the lower bound of the range of layouts
	 * @param end the upper bound of the range of layouts (not inclusive)
	 * @param orderByComparator the comparator to order the results by (optionally <code>null</code>)
	 * @param useFinderCache whether to use the finder cache
	 * @return the ordered range of matching layouts
	 */
	public java.util.List<Layout> findByIconImageId(
		long iconImageId, int start, int end,
		com.liferay.portal.kernel.util.OrderByComparator<Layout>
			orderByComparator,
		boolean useFinderCache);

	/**
	 * Returns the first layout in the ordered set where iconImageId = &#63;.
	 *
	 * @param iconImageId the icon image ID
	 * @param orderByComparator the comparator to order the set by (optionally <code>null</code>)
	 * @return the first matching layout
	 * @throws NoSuchLayoutException if a matching layout could not be found
	 */
	public Layout findByIconImageId_First(
			long iconImageId,
			com.liferay.portal.kernel.util.OrderByComparator<Layout>
				orderByComparator)
		throws NoSuchLayoutException;

	/**
	 * Returns the first layout in the ordered set where iconImageId = &#63;.
	 *
	 * @param iconImageId the icon image ID
	 * @param orderByComparator the comparator to order the set by (optionally <code>null</code>)
	 * @return the first matching layout, or <code>null</code> if a matching layout could not be found
	 */
	public Layout fetchByIconImageId_First(
		long iconImageId,
		com.liferay.portal.kernel.util.OrderByComparator<Layout>
			orderByComparator);

	/**
	 * Returns the last layout in the ordered set where iconImageId = &#63;.
	 *
	 * @param iconImageId the icon image ID
	 * @param orderByComparator the comparator to order the set by (optionally <code>null</code>)
	 * @return the last matching layout
	 * @throws NoSuchLayoutException if a matching layout could not be found
	 */
	public Layout findByIconImageId_Last(
			long iconImageId,
			com.liferay.portal.kernel.util.OrderByComparator<Layout>
				orderByComparator)
		throws NoSuchLayoutException;

	/**
	 * Returns the last layout in the ordered set where iconImageId = &#63;.
	 *
	 * @param iconImageId the icon image ID
	 * @param orderByComparator the comparator to order the set by (optionally <code>null</code>)
	 * @return the last matching layout, or <code>null</code> if a matching layout could not be found
	 */
	public Layout fetchByIconImageId_Last(
		long iconImageId,
		com.liferay.portal.kernel.util.OrderByComparator<Layout>
			orderByComparator);

	/**
	 * Returns the layouts before and after the current layout in the ordered set where iconImageId = &#63;.
	 *
	 * @param plid the primary key of the current layout
	 * @param iconImageId the icon image ID
	 * @param orderByComparator the comparator to order the set by (optionally <code>null</code>)
	 * @return the previous, current, and next layout
	 * @throws NoSuchLayoutException if a layout with the primary key could not be found
	 */
	public Layout[] findByIconImageId_PrevAndNext(
			long plid, long iconImageId,
			com.liferay.portal.kernel.util.OrderByComparator<Layout>
				orderByComparator)
		throws NoSuchLayoutException;

	/**
	 * Removes all the layouts where iconImageId = &#63; from the database.
	 *
	 * @param iconImageId the icon image ID
	 */
	public void removeByIconImageId(long iconImageId);

	/**
	 * Returns the number of layouts where iconImageId = &#63;.
	 *
	 * @param iconImageId the icon image ID
	 * @return the number of matching layouts
	 */
	public int countByIconImageId(long iconImageId);

	/**
	 * Returns all the layouts where layoutPrototypeUuid = &#63;.
	 *
	 * @param layoutPrototypeUuid the layout prototype uuid
	 * @return the matching layouts
	 */
	public java.util.List<Layout> findByLayoutPrototypeUuid(
		String layoutPrototypeUuid);

	/**
	 * Returns a range of all the layouts where layoutPrototypeUuid = &#63;.
	 *
	 * <p>
	 * Useful when paginating results. Returns a maximum of <code>end - start</code> instances. <code>start</code> and <code>end</code> are not primary keys, they are indexes in the result set. Thus, <code>0</code> refers to the first result in the set. Setting both <code>start</code> and <code>end</code> to <code>QueryUtil#ALL_POS</code> will return the full result set. If <code>orderByComparator</code> is specified, then the query will include the given ORDER BY logic. If <code>orderByComparator</code> is absent, then the query will include the default ORDER BY logic from <code>LayoutModelImpl</code>.
	 * </p>
	 *
	 * @param layoutPrototypeUuid the layout prototype uuid
	 * @param start the lower bound of the range of layouts
	 * @param end the upper bound of the range of layouts (not inclusive)
	 * @return the range of matching layouts
	 */
	public java.util.List<Layout> findByLayoutPrototypeUuid(
		String layoutPrototypeUuid, int start, int end);

	/**
	 * Returns an ordered range of all the layouts where layoutPrototypeUuid = &#63;.
	 *
	 * <p>
	 * Useful when paginating results. Returns a maximum of <code>end - start</code> instances. <code>start</code> and <code>end</code> are not primary keys, they are indexes in the result set. Thus, <code>0</code> refers to the first result in the set. Setting both <code>start</code> and <code>end</code> to <code>QueryUtil#ALL_POS</code> will return the full result set. If <code>orderByComparator</code> is specified, then the query will include the given ORDER BY logic. If <code>orderByComparator</code> is absent, then the query will include the default ORDER BY logic from <code>LayoutModelImpl</code>.
	 * </p>
	 *
	 * @param layoutPrototypeUuid the layout prototype uuid
	 * @param start the lower bound of the range of layouts
	 * @param end the upper bound of the range of layouts (not inclusive)
	 * @param orderByComparator the comparator to order the results by (optionally <code>null</code>)
	 * @return the ordered range of matching layouts
	 */
	public java.util.List<Layout> findByLayoutPrototypeUuid(
		String layoutPrototypeUuid, int start, int end,
		com.liferay.portal.kernel.util.OrderByComparator<Layout>
			orderByComparator);

	/**
	 * Returns an ordered range of all the layouts where layoutPrototypeUuid = &#63;.
	 *
	 * <p>
	 * Useful when paginating results. Returns a maximum of <code>end - start</code> instances. <code>start</code> and <code>end</code> are not primary keys, they are indexes in the result set. Thus, <code>0</code> refers to the first result in the set. Setting both <code>start</code> and <code>end</code> to <code>QueryUtil#ALL_POS</code> will return the full result set. If <code>orderByComparator</code> is specified, then the query will include the given ORDER BY logic. If <code>orderByComparator</code> is absent, then the query will include the default ORDER BY logic from <code>LayoutModelImpl</code>.
	 * </p>
	 *
	 * @param layoutPrototypeUuid the layout prototype uuid
	 * @param start the lower bound of the range of layouts
	 * @param end the upper bound of the range of layouts (not inclusive)
	 * @param orderByComparator the comparator to order the results by (optionally <code>null</code>)
	 * @param useFinderCache whether to use the finder cache
	 * @return the ordered range of matching layouts
	 */
	public java.util.List<Layout> findByLayoutPrototypeUuid(
		String layoutPrototypeUuid, int start, int end,
		com.liferay.portal.kernel.util.OrderByComparator<Layout>
			orderByComparator,
		boolean useFinderCache);

	/**
	 * Returns the first layout in the ordered set where layoutPrototypeUuid = &#63;.
	 *
	 * @param layoutPrototypeUuid the layout prototype uuid
	 * @param orderByComparator the comparator to order the set by (optionally <code>null</code>)
	 * @return the first matching layout
	 * @throws NoSuchLayoutException if a matching layout could not be found
	 */
	public Layout findByLayoutPrototypeUuid_First(
			String layoutPrototypeUuid,
			com.liferay.portal.kernel.util.OrderByComparator<Layout>
				orderByComparator)
		throws NoSuchLayoutException;

	/**
	 * Returns the first layout in the ordered set where layoutPrototypeUuid = &#63;.
	 *
	 * @param layoutPrototypeUuid the layout prototype uuid
	 * @param orderByComparator the comparator to order the set by (optionally <code>null</code>)
	 * @return the first matching layout, or <code>null</code> if a matching layout could not be found
	 */
	public Layout fetchByLayoutPrototypeUuid_First(
		String layoutPrototypeUuid,
		com.liferay.portal.kernel.util.OrderByComparator<Layout>
			orderByComparator);

	/**
	 * Returns the last layout in the ordered set where layoutPrototypeUuid = &#63;.
	 *
	 * @param layoutPrototypeUuid the layout prototype uuid
	 * @param orderByComparator the comparator to order the set by (optionally <code>null</code>)
	 * @return the last matching layout
	 * @throws NoSuchLayoutException if a matching layout could not be found
	 */
	public Layout findByLayoutPrototypeUuid_Last(
			String layoutPrototypeUuid,
			com.liferay.portal.kernel.util.OrderByComparator<Layout>
				orderByComparator)
		throws NoSuchLayoutException;

	/**
	 * Returns the last layout in the ordered set where layoutPrototypeUuid = &#63;.
	 *
	 * @param layoutPrototypeUuid the layout prototype uuid
	 * @param orderByComparator the comparator to order the set by (optionally <code>null</code>)
	 * @return the last matching layout, or <code>null</code> if a matching layout could not be found
	 */
	public Layout fetchByLayoutPrototypeUuid_Last(
		String layoutPrototypeUuid,
		com.liferay.portal.kernel.util.OrderByComparator<Layout>
			orderByComparator);

	/**
	 * Returns the layouts before and after the current layout in the ordered set where layoutPrototypeUuid = &#63;.
	 *
	 * @param plid the primary key of the current layout
	 * @param layoutPrototypeUuid the layout prototype uuid
	 * @param orderByComparator the comparator to order the set by (optionally <code>null</code>)
	 * @return the previous, current, and next layout
	 * @throws NoSuchLayoutException if a layout with the primary key could not be found
	 */
	public Layout[] findByLayoutPrototypeUuid_PrevAndNext(
			long plid, String layoutPrototypeUuid,
			com.liferay.portal.kernel.util.OrderByComparator<Layout>
				orderByComparator)
		throws NoSuchLayoutException;

	/**
	 * Removes all the layouts where layoutPrototypeUuid = &#63; from the database.
	 *
	 * @param layoutPrototypeUuid the layout prototype uuid
	 */
	public void removeByLayoutPrototypeUuid(String layoutPrototypeUuid);

	/**
	 * Returns the number of layouts where layoutPrototypeUuid = &#63;.
	 *
	 * @param layoutPrototypeUuid the layout prototype uuid
	 * @return the number of matching layouts
	 */
	public int countByLayoutPrototypeUuid(String layoutPrototypeUuid);

	/**
	 * Returns all the layouts where layoutSetPrototypeLayoutERC = &#63;.
	 *
	 * @param layoutSetPrototypeLayoutERC the layout set prototype layout erc
	 * @return the matching layouts
	 */
	public java.util.List<Layout> findByLayoutSetPrototypeLayoutERC(
		String layoutSetPrototypeLayoutERC);

	/**
	 * Returns a range of all the layouts where layoutSetPrototypeLayoutERC = &#63;.
	 *
	 * <p>
	 * Useful when paginating results. Returns a maximum of <code>end - start</code> instances. <code>start</code> and <code>end</code> are not primary keys, they are indexes in the result set. Thus, <code>0</code> refers to the first result in the set. Setting both <code>start</code> and <code>end</code> to <code>QueryUtil#ALL_POS</code> will return the full result set. If <code>orderByComparator</code> is specified, then the query will include the given ORDER BY logic. If <code>orderByComparator</code> is absent, then the query will include the default ORDER BY logic from <code>LayoutModelImpl</code>.
	 * </p>
	 *
	 * @param layoutSetPrototypeLayoutERC the layout set prototype layout erc
	 * @param start the lower bound of the range of layouts
	 * @param end the upper bound of the range of layouts (not inclusive)
	 * @return the range of matching layouts
	 */
	public java.util.List<Layout> findByLayoutSetPrototypeLayoutERC(
		String layoutSetPrototypeLayoutERC, int start, int end);

	/**
	 * Returns an ordered range of all the layouts where layoutSetPrototypeLayoutERC = &#63;.
	 *
	 * <p>
	 * Useful when paginating results. Returns a maximum of <code>end - start</code> instances. <code>start</code> and <code>end</code> are not primary keys, they are indexes in the result set. Thus, <code>0</code> refers to the first result in the set. Setting both <code>start</code> and <code>end</code> to <code>QueryUtil#ALL_POS</code> will return the full result set. If <code>orderByComparator</code> is specified, then the query will include the given ORDER BY logic. If <code>orderByComparator</code> is absent, then the query will include the default ORDER BY logic from <code>LayoutModelImpl</code>.
	 * </p>
	 *
	 * @param layoutSetPrototypeLayoutERC the layout set prototype layout erc
	 * @param start the lower bound of the range of layouts
	 * @param end the upper bound of the range of layouts (not inclusive)
	 * @param orderByComparator the comparator to order the results by (optionally <code>null</code>)
	 * @return the ordered range of matching layouts
	 */
	public java.util.List<Layout> findByLayoutSetPrototypeLayoutERC(
		String layoutSetPrototypeLayoutERC, int start, int end,
		com.liferay.portal.kernel.util.OrderByComparator<Layout>
			orderByComparator);

	/**
	 * Returns an ordered range of all the layouts where layoutSetPrototypeLayoutERC = &#63;.
	 *
	 * <p>
	 * Useful when paginating results. Returns a maximum of <code>end - start</code> instances. <code>start</code> and <code>end</code> are not primary keys, they are indexes in the result set. Thus, <code>0</code> refers to the first result in the set. Setting both <code>start</code> and <code>end</code> to <code>QueryUtil#ALL_POS</code> will return the full result set. If <code>orderByComparator</code> is specified, then the query will include the given ORDER BY logic. If <code>orderByComparator</code> is absent, then the query will include the default ORDER BY logic from <code>LayoutModelImpl</code>.
	 * </p>
	 *
	 * @param layoutSetPrototypeLayoutERC the layout set prototype layout erc
	 * @param start the lower bound of the range of layouts
	 * @param end the upper bound of the range of layouts (not inclusive)
	 * @param orderByComparator the comparator to order the results by (optionally <code>null</code>)
	 * @param useFinderCache whether to use the finder cache
	 * @return the ordered range of matching layouts
	 */
	public java.util.List<Layout> findByLayoutSetPrototypeLayoutERC(
		String layoutSetPrototypeLayoutERC, int start, int end,
		com.liferay.portal.kernel.util.OrderByComparator<Layout>
			orderByComparator,
		boolean useFinderCache);

	/**
	 * Returns the first layout in the ordered set where layoutSetPrototypeLayoutERC = &#63;.
	 *
	 * @param layoutSetPrototypeLayoutERC the layout set prototype layout erc
	 * @param orderByComparator the comparator to order the set by (optionally <code>null</code>)
	 * @return the first matching layout
	 * @throws NoSuchLayoutException if a matching layout could not be found
	 */
	public Layout findByLayoutSetPrototypeLayoutERC_First(
			String layoutSetPrototypeLayoutERC,
			com.liferay.portal.kernel.util.OrderByComparator<Layout>
				orderByComparator)
		throws NoSuchLayoutException;

	/**
	 * Returns the first layout in the ordered set where layoutSetPrototypeLayoutERC = &#63;.
	 *
	 * @param layoutSetPrototypeLayoutERC the layout set prototype layout erc
	 * @param orderByComparator the comparator to order the set by (optionally <code>null</code>)
	 * @return the first matching layout, or <code>null</code> if a matching layout could not be found
	 */
	public Layout fetchByLayoutSetPrototypeLayoutERC_First(
		String layoutSetPrototypeLayoutERC,
		com.liferay.portal.kernel.util.OrderByComparator<Layout>
			orderByComparator);

	/**
	 * Returns the last layout in the ordered set where layoutSetPrototypeLayoutERC = &#63;.
	 *
	 * @param layoutSetPrototypeLayoutERC the layout set prototype layout erc
	 * @param orderByComparator the comparator to order the set by (optionally <code>null</code>)
	 * @return the last matching layout
	 * @throws NoSuchLayoutException if a matching layout could not be found
	 */
	public Layout findByLayoutSetPrototypeLayoutERC_Last(
			String layoutSetPrototypeLayoutERC,
			com.liferay.portal.kernel.util.OrderByComparator<Layout>
				orderByComparator)
		throws NoSuchLayoutException;

	/**
	 * Returns the last layout in the ordered set where layoutSetPrototypeLayoutERC = &#63;.
	 *
	 * @param layoutSetPrototypeLayoutERC the layout set prototype layout erc
	 * @param orderByComparator the comparator to order the set by (optionally <code>null</code>)
	 * @return the last matching layout, or <code>null</code> if a matching layout could not be found
	 */
	public Layout fetchByLayoutSetPrototypeLayoutERC_Last(
		String layoutSetPrototypeLayoutERC,
		com.liferay.portal.kernel.util.OrderByComparator<Layout>
			orderByComparator);

	/**
	 * Returns the layouts before and after the current layout in the ordered set where layoutSetPrototypeLayoutERC = &#63;.
	 *
	 * @param plid the primary key of the current layout
	 * @param layoutSetPrototypeLayoutERC the layout set prototype layout erc
	 * @param orderByComparator the comparator to order the set by (optionally <code>null</code>)
	 * @return the previous, current, and next layout
	 * @throws NoSuchLayoutException if a layout with the primary key could not be found
	 */
	public Layout[] findByLayoutSetPrototypeLayoutERC_PrevAndNext(
			long plid, String layoutSetPrototypeLayoutERC,
			com.liferay.portal.kernel.util.OrderByComparator<Layout>
				orderByComparator)
		throws NoSuchLayoutException;

	/**
	 * Removes all the layouts where layoutSetPrototypeLayoutERC = &#63; from the database.
	 *
	 * @param layoutSetPrototypeLayoutERC the layout set prototype layout erc
	 */
	public void removeByLayoutSetPrototypeLayoutERC(
		String layoutSetPrototypeLayoutERC);

	/**
	 * Returns the number of layouts where layoutSetPrototypeLayoutERC = &#63;.
	 *
	 * @param layoutSetPrototypeLayoutERC the layout set prototype layout erc
	 * @return the number of matching layouts
	 */
	public int countByLayoutSetPrototypeLayoutERC(
		String layoutSetPrototypeLayoutERC);

	/**
	 * Returns all the layouts where groupId = &#63; and privateLayout = &#63;.
	 *
	 * @param groupId the group ID
	 * @param privateLayout the private layout
	 * @return the matching layouts
	 */
	public java.util.List<Layout> findByG_P(
		long groupId, boolean privateLayout);

	/**
	 * Returns a range of all the layouts where groupId = &#63; and privateLayout = &#63;.
	 *
	 * <p>
	 * Useful when paginating results. Returns a maximum of <code>end - start</code> instances. <code>start</code> and <code>end</code> are not primary keys, they are indexes in the result set. Thus, <code>0</code> refers to the first result in the set. Setting both <code>start</code> and <code>end</code> to <code>QueryUtil#ALL_POS</code> will return the full result set. If <code>orderByComparator</code> is specified, then the query will include the given ORDER BY logic. If <code>orderByComparator</code> is absent, then the query will include the default ORDER BY logic from <code>LayoutModelImpl</code>.
	 * </p>
	 *
	 * @param groupId the group ID
	 * @param privateLayout the private layout
	 * @param start the lower bound of the range of layouts
	 * @param end the upper bound of the range of layouts (not inclusive)
	 * @return the range of matching layouts
	 */
	public java.util.List<Layout> findByG_P(
		long groupId, boolean privateLayout, int start, int end);

	/**
	 * Returns an ordered range of all the layouts where groupId = &#63; and privateLayout = &#63;.
	 *
	 * <p>
	 * Useful when paginating results. Returns a maximum of <code>end - start</code> instances. <code>start</code> and <code>end</code> are not primary keys, they are indexes in the result set. Thus, <code>0</code> refers to the first result in the set. Setting both <code>start</code> and <code>end</code> to <code>QueryUtil#ALL_POS</code> will return the full result set. If <code>orderByComparator</code> is specified, then the query will include the given ORDER BY logic. If <code>orderByComparator</code> is absent, then the query will include the default ORDER BY logic from <code>LayoutModelImpl</code>.
	 * </p>
	 *
	 * @param groupId the group ID
	 * @param privateLayout the private layout
	 * @param start the lower bound of the range of layouts
	 * @param end the upper bound of the range of layouts (not inclusive)
	 * @param orderByComparator the comparator to order the results by (optionally <code>null</code>)
	 * @return the ordered range of matching layouts
	 */
	public java.util.List<Layout> findByG_P(
		long groupId, boolean privateLayout, int start, int end,
		com.liferay.portal.kernel.util.OrderByComparator<Layout>
			orderByComparator);

	/**
	 * Returns an ordered range of all the layouts where groupId = &#63; and privateLayout = &#63;.
	 *
	 * <p>
	 * Useful when paginating results. Returns a maximum of <code>end - start</code> instances. <code>start</code> and <code>end</code> are not primary keys, they are indexes in the result set. Thus, <code>0</code> refers to the first result in the set. Setting both <code>start</code> and <code>end</code> to <code>QueryUtil#ALL_POS</code> will return the full result set. If <code>orderByComparator</code> is specified, then the query will include the given ORDER BY logic. If <code>orderByComparator</code> is absent, then the query will include the default ORDER BY logic from <code>LayoutModelImpl</code>.
	 * </p>
	 *
	 * @param groupId the group ID
	 * @param privateLayout the private layout
	 * @param start the lower bound of the range of layouts
	 * @param end the upper bound of the range of layouts (not inclusive)
	 * @param orderByComparator the comparator to order the results by (optionally <code>null</code>)
	 * @param useFinderCache whether to use the finder cache
	 * @return the ordered range of matching layouts
	 */
	public java.util.List<Layout> findByG_P(
		long groupId, boolean privateLayout, int start, int end,
		com.liferay.portal.kernel.util.OrderByComparator<Layout>
			orderByComparator,
		boolean useFinderCache);

	/**
	 * Returns the first layout in the ordered set where groupId = &#63; and privateLayout = &#63;.
	 *
	 * @param groupId the group ID
	 * @param privateLayout the private layout
	 * @param orderByComparator the comparator to order the set by (optionally <code>null</code>)
	 * @return the first matching layout
	 * @throws NoSuchLayoutException if a matching layout could not be found
	 */
	public Layout findByG_P_First(
			long groupId, boolean privateLayout,
			com.liferay.portal.kernel.util.OrderByComparator<Layout>
				orderByComparator)
		throws NoSuchLayoutException;

	/**
	 * Returns the first layout in the ordered set where groupId = &#63; and privateLayout = &#63;.
	 *
	 * @param groupId the group ID
	 * @param privateLayout the private layout
	 * @param orderByComparator the comparator to order the set by (optionally <code>null</code>)
	 * @return the first matching layout, or <code>null</code> if a matching layout could not be found
	 */
	public Layout fetchByG_P_First(
		long groupId, boolean privateLayout,
		com.liferay.portal.kernel.util.OrderByComparator<Layout>
			orderByComparator);

	/**
	 * Returns the last layout in the ordered set where groupId = &#63; and privateLayout = &#63;.
	 *
	 * @param groupId the group ID
	 * @param privateLayout the private layout
	 * @param orderByComparator the comparator to order the set by (optionally <code>null</code>)
	 * @return the last matching layout
	 * @throws NoSuchLayoutException if a matching layout could not be found
	 */
	public Layout findByG_P_Last(
			long groupId, boolean privateLayout,
			com.liferay.portal.kernel.util.OrderByComparator<Layout>
				orderByComparator)
		throws NoSuchLayoutException;

	/**
	 * Returns the last layout in the ordered set where groupId = &#63; and privateLayout = &#63;.
	 *
	 * @param groupId the group ID
	 * @param privateLayout the private layout
	 * @param orderByComparator the comparator to order the set by (optionally <code>null</code>)
	 * @return the last matching layout, or <code>null</code> if a matching layout could not be found
	 */
	public Layout fetchByG_P_Last(
		long groupId, boolean privateLayout,
		com.liferay.portal.kernel.util.OrderByComparator<Layout>
			orderByComparator);

	/**
	 * Returns the layouts before and after the current layout in the ordered set where groupId = &#63; and privateLayout = &#63;.
	 *
	 * @param plid the primary key of the current layout
	 * @param groupId the group ID
	 * @param privateLayout the private layout
	 * @param orderByComparator the comparator to order the set by (optionally <code>null</code>)
	 * @return the previous, current, and next layout
	 * @throws NoSuchLayoutException if a layout with the primary key could not be found
	 */
	public Layout[] findByG_P_PrevAndNext(
			long plid, long groupId, boolean privateLayout,
			com.liferay.portal.kernel.util.OrderByComparator<Layout>
				orderByComparator)
		throws NoSuchLayoutException;

	/**
	 * Returns all the layouts that the user has permission to view where groupId = &#63; and privateLayout = &#63;.
	 *
	 * @param groupId the group ID
	 * @param privateLayout the private layout
	 * @return the matching layouts that the user has permission to view
	 */
	public java.util.List<Layout> filterFindByG_P(
		long groupId, boolean privateLayout);

	/**
	 * Returns a range of all the layouts that the user has permission to view where groupId = &#63; and privateLayout = &#63;.
	 *
	 * <p>
	 * Useful when paginating results. Returns a maximum of <code>end - start</code> instances. <code>start</code> and <code>end</code> are not primary keys, they are indexes in the result set. Thus, <code>0</code> refers to the first result in the set. Setting both <code>start</code> and <code>end</code> to <code>QueryUtil#ALL_POS</code> will return the full result set. If <code>orderByComparator</code> is specified, then the query will include the given ORDER BY logic. If <code>orderByComparator</code> is absent, then the query will include the default ORDER BY logic from <code>LayoutModelImpl</code>.
	 * </p>
	 *
	 * @param groupId the group ID
	 * @param privateLayout the private layout
	 * @param start the lower bound of the range of layouts
	 * @param end the upper bound of the range of layouts (not inclusive)
	 * @return the range of matching layouts that the user has permission to view
	 */
	public java.util.List<Layout> filterFindByG_P(
		long groupId, boolean privateLayout, int start, int end);

	/**
	 * Returns an ordered range of all the layouts that the user has permissions to view where groupId = &#63; and privateLayout = &#63;.
	 *
	 * <p>
	 * Useful when paginating results. Returns a maximum of <code>end - start</code> instances. <code>start</code> and <code>end</code> are not primary keys, they are indexes in the result set. Thus, <code>0</code> refers to the first result in the set. Setting both <code>start</code> and <code>end</code> to <code>QueryUtil#ALL_POS</code> will return the full result set. If <code>orderByComparator</code> is specified, then the query will include the given ORDER BY logic. If <code>orderByComparator</code> is absent, then the query will include the default ORDER BY logic from <code>LayoutModelImpl</code>.
	 * </p>
	 *
	 * @param groupId the group ID
	 * @param privateLayout the private layout
	 * @param start the lower bound of the range of layouts
	 * @param end the upper bound of the range of layouts (not inclusive)
	 * @param orderByComparator the comparator to order the results by (optionally <code>null</code>)
	 * @return the ordered range of matching layouts that the user has permission to view
	 */
	public java.util.List<Layout> filterFindByG_P(
		long groupId, boolean privateLayout, int start, int end,
		com.liferay.portal.kernel.util.OrderByComparator<Layout>
			orderByComparator);

	/**
	 * Returns the layouts before and after the current layout in the ordered set of layouts that the user has permission to view where groupId = &#63; and privateLayout = &#63;.
	 *
	 * @param plid the primary key of the current layout
	 * @param groupId the group ID
	 * @param privateLayout the private layout
	 * @param orderByComparator the comparator to order the set by (optionally <code>null</code>)
	 * @return the previous, current, and next layout
	 * @throws NoSuchLayoutException if a layout with the primary key could not be found
	 */
	public Layout[] filterFindByG_P_PrevAndNext(
			long plid, long groupId, boolean privateLayout,
			com.liferay.portal.kernel.util.OrderByComparator<Layout>
				orderByComparator)
		throws NoSuchLayoutException;

	/**
	 * Removes all the layouts where groupId = &#63; and privateLayout = &#63; from the database.
	 *
	 * @param groupId the group ID
	 * @param privateLayout the private layout
	 */
	public void removeByG_P(long groupId, boolean privateLayout);

	/**
	 * Returns the number of layouts where groupId = &#63; and privateLayout = &#63;.
	 *
	 * @param groupId the group ID
	 * @param privateLayout the private layout
	 * @return the number of matching layouts
	 */
	public int countByG_P(long groupId, boolean privateLayout);

	/**
	 * Returns the number of layouts that the user has permission to view where groupId = &#63; and privateLayout = &#63;.
	 *
	 * @param groupId the group ID
	 * @param privateLayout the private layout
	 * @return the number of matching layouts that the user has permission to view
	 */
	public int filterCountByG_P(long groupId, boolean privateLayout);

	/**
	 * Returns all the layouts where groupId = &#63; and type = &#63;.
	 *
	 * @param groupId the group ID
	 * @param type the type
	 * @return the matching layouts
	 */
	public java.util.List<Layout> findByG_T(long groupId, String type);

	/**
	 * Returns a range of all the layouts where groupId = &#63; and type = &#63;.
	 *
	 * <p>
	 * Useful when paginating results. Returns a maximum of <code>end - start</code> instances. <code>start</code> and <code>end</code> are not primary keys, they are indexes in the result set. Thus, <code>0</code> refers to the first result in the set. Setting both <code>start</code> and <code>end</code> to <code>QueryUtil#ALL_POS</code> will return the full result set. If <code>orderByComparator</code> is specified, then the query will include the given ORDER BY logic. If <code>orderByComparator</code> is absent, then the query will include the default ORDER BY logic from <code>LayoutModelImpl</code>.
	 * </p>
	 *
	 * @param groupId the group ID
	 * @param type the type
	 * @param start the lower bound of the range of layouts
	 * @param end the upper bound of the range of layouts (not inclusive)
	 * @return the range of matching layouts
	 */
	public java.util.List<Layout> findByG_T(
		long groupId, String type, int start, int end);

	/**
	 * Returns an ordered range of all the layouts where groupId = &#63; and type = &#63;.
	 *
	 * <p>
	 * Useful when paginating results. Returns a maximum of <code>end - start</code> instances. <code>start</code> and <code>end</code> are not primary keys, they are indexes in the result set. Thus, <code>0</code> refers to the first result in the set. Setting both <code>start</code> and <code>end</code> to <code>QueryUtil#ALL_POS</code> will return the full result set. If <code>orderByComparator</code> is specified, then the query will include the given ORDER BY logic. If <code>orderByComparator</code> is absent, then the query will include the default ORDER BY logic from <code>LayoutModelImpl</code>.
	 * </p>
	 *
	 * @param groupId the group ID
	 * @param type the type
	 * @param start the lower bound of the range of layouts
	 * @param end the upper bound of the range of layouts (not inclusive)
	 * @param orderByComparator the comparator to order the results by (optionally <code>null</code>)
	 * @return the ordered range of matching layouts
	 */
	public java.util.List<Layout> findByG_T(
		long groupId, String type, int start, int end,
		com.liferay.portal.kernel.util.OrderByComparator<Layout>
			orderByComparator);

	/**
	 * Returns an ordered range of all the layouts where groupId = &#63; and type = &#63;.
	 *
	 * <p>
	 * Useful when paginating results. Returns a maximum of <code>end - start</code> instances. <code>start</code> and <code>end</code> are not primary keys, they are indexes in the result set. Thus, <code>0</code> refers to the first result in the set. Setting both <code>start</code> and <code>end</code> to <code>QueryUtil#ALL_POS</code> will return the full result set. If <code>orderByComparator</code> is specified, then the query will include the given ORDER BY logic. If <code>orderByComparator</code> is absent, then the query will include the default ORDER BY logic from <code>LayoutModelImpl</code>.
	 * </p>
	 *
	 * @param groupId the group ID
	 * @param type the type
	 * @param start the lower bound of the range of layouts
	 * @param end the upper bound of the range of layouts (not inclusive)
	 * @param orderByComparator the comparator to order the results by (optionally <code>null</code>)
	 * @param useFinderCache whether to use the finder cache
	 * @return the ordered range of matching layouts
	 */
	public java.util.List<Layout> findByG_T(
		long groupId, String type, int start, int end,
		com.liferay.portal.kernel.util.OrderByComparator<Layout>
			orderByComparator,
		boolean useFinderCache);

	/**
	 * Returns the first layout in the ordered set where groupId = &#63; and type = &#63;.
	 *
	 * @param groupId the group ID
	 * @param type the type
	 * @param orderByComparator the comparator to order the set by (optionally <code>null</code>)
	 * @return the first matching layout
	 * @throws NoSuchLayoutException if a matching layout could not be found
	 */
	public Layout findByG_T_First(
			long groupId, String type,
			com.liferay.portal.kernel.util.OrderByComparator<Layout>
				orderByComparator)
		throws NoSuchLayoutException;

	/**
	 * Returns the first layout in the ordered set where groupId = &#63; and type = &#63;.
	 *
	 * @param groupId the group ID
	 * @param type the type
	 * @param orderByComparator the comparator to order the set by (optionally <code>null</code>)
	 * @return the first matching layout, or <code>null</code> if a matching layout could not be found
	 */
	public Layout fetchByG_T_First(
		long groupId, String type,
		com.liferay.portal.kernel.util.OrderByComparator<Layout>
			orderByComparator);

	/**
	 * Returns the last layout in the ordered set where groupId = &#63; and type = &#63;.
	 *
	 * @param groupId the group ID
	 * @param type the type
	 * @param orderByComparator the comparator to order the set by (optionally <code>null</code>)
	 * @return the last matching layout
	 * @throws NoSuchLayoutException if a matching layout could not be found
	 */
	public Layout findByG_T_Last(
			long groupId, String type,
			com.liferay.portal.kernel.util.OrderByComparator<Layout>
				orderByComparator)
		throws NoSuchLayoutException;

	/**
	 * Returns the last layout in the ordered set where groupId = &#63; and type = &#63;.
	 *
	 * @param groupId the group ID
	 * @param type the type
	 * @param orderByComparator the comparator to order the set by (optionally <code>null</code>)
	 * @return the last matching layout, or <code>null</code> if a matching layout could not be found
	 */
	public Layout fetchByG_T_Last(
		long groupId, String type,
		com.liferay.portal.kernel.util.OrderByComparator<Layout>
			orderByComparator);

	/**
	 * Returns the layouts before and after the current layout in the ordered set where groupId = &#63; and type = &#63;.
	 *
	 * @param plid the primary key of the current layout
	 * @param groupId the group ID
	 * @param type the type
	 * @param orderByComparator the comparator to order the set by (optionally <code>null</code>)
	 * @return the previous, current, and next layout
	 * @throws NoSuchLayoutException if a layout with the primary key could not be found
	 */
	public Layout[] findByG_T_PrevAndNext(
			long plid, long groupId, String type,
			com.liferay.portal.kernel.util.OrderByComparator<Layout>
				orderByComparator)
		throws NoSuchLayoutException;

	/**
	 * Returns all the layouts that the user has permission to view where groupId = &#63; and type = &#63;.
	 *
	 * @param groupId the group ID
	 * @param type the type
	 * @return the matching layouts that the user has permission to view
	 */
	public java.util.List<Layout> filterFindByG_T(long groupId, String type);

	/**
	 * Returns a range of all the layouts that the user has permission to view where groupId = &#63; and type = &#63;.
	 *
	 * <p>
	 * Useful when paginating results. Returns a maximum of <code>end - start</code> instances. <code>start</code> and <code>end</code> are not primary keys, they are indexes in the result set. Thus, <code>0</code> refers to the first result in the set. Setting both <code>start</code> and <code>end</code> to <code>QueryUtil#ALL_POS</code> will return the full result set. If <code>orderByComparator</code> is specified, then the query will include the given ORDER BY logic. If <code>orderByComparator</code> is absent, then the query will include the default ORDER BY logic from <code>LayoutModelImpl</code>.
	 * </p>
	 *
	 * @param groupId the group ID
	 * @param type the type
	 * @param start the lower bound of the range of layouts
	 * @param end the upper bound of the range of layouts (not inclusive)
	 * @return the range of matching layouts that the user has permission to view
	 */
	public java.util.List<Layout> filterFindByG_T(
		long groupId, String type, int start, int end);

	/**
	 * Returns an ordered range of all the layouts that the user has permissions to view where groupId = &#63; and type = &#63;.
	 *
	 * <p>
	 * Useful when paginating results. Returns a maximum of <code>end - start</code> instances. <code>start</code> and <code>end</code> are not primary keys, they are indexes in the result set. Thus, <code>0</code> refers to the first result in the set. Setting both <code>start</code> and <code>end</code> to <code>QueryUtil#ALL_POS</code> will return the full result set. If <code>orderByComparator</code> is specified, then the query will include the given ORDER BY logic. If <code>orderByComparator</code> is absent, then the query will include the default ORDER BY logic from <code>LayoutModelImpl</code>.
	 * </p>
	 *
	 * @param groupId the group ID
	 * @param type the type
	 * @param start the lower bound of the range of layouts
	 * @param end the upper bound of the range of layouts (not inclusive)
	 * @param orderByComparator the comparator to order the results by (optionally <code>null</code>)
	 * @return the ordered range of matching layouts that the user has permission to view
	 */
	public java.util.List<Layout> filterFindByG_T(
		long groupId, String type, int start, int end,
		com.liferay.portal.kernel.util.OrderByComparator<Layout>
			orderByComparator);

	/**
	 * Returns the layouts before and after the current layout in the ordered set of layouts that the user has permission to view where groupId = &#63; and type = &#63;.
	 *
	 * @param plid the primary key of the current layout
	 * @param groupId the group ID
	 * @param type the type
	 * @param orderByComparator the comparator to order the set by (optionally <code>null</code>)
	 * @return the previous, current, and next layout
	 * @throws NoSuchLayoutException if a layout with the primary key could not be found
	 */
	public Layout[] filterFindByG_T_PrevAndNext(
			long plid, long groupId, String type,
			com.liferay.portal.kernel.util.OrderByComparator<Layout>
				orderByComparator)
		throws NoSuchLayoutException;

	/**
	 * Removes all the layouts where groupId = &#63; and type = &#63; from the database.
	 *
	 * @param groupId the group ID
	 * @param type the type
	 */
	public void removeByG_T(long groupId, String type);

	/**
	 * Returns the number of layouts where groupId = &#63; and type = &#63;.
	 *
	 * @param groupId the group ID
	 * @param type the type
	 * @return the number of matching layouts
	 */
	public int countByG_T(long groupId, String type);

	/**
	 * Returns the number of layouts that the user has permission to view where groupId = &#63; and type = &#63;.
	 *
	 * @param groupId the group ID
	 * @param type the type
	 * @return the number of matching layouts that the user has permission to view
	 */
	public int filterCountByG_T(long groupId, String type);

	/**
	 * Returns all the layouts where groupId = &#63; and masterLayoutPlid = &#63;.
	 *
	 * @param groupId the group ID
	 * @param masterLayoutPlid the master layout plid
	 * @return the matching layouts
	 */
	public java.util.List<Layout> findByG_MLP(
		long groupId, long masterLayoutPlid);

	/**
	 * Returns a range of all the layouts where groupId = &#63; and masterLayoutPlid = &#63;.
	 *
	 * <p>
	 * Useful when paginating results. Returns a maximum of <code>end - start</code> instances. <code>start</code> and <code>end</code> are not primary keys, they are indexes in the result set. Thus, <code>0</code> refers to the first result in the set. Setting both <code>start</code> and <code>end</code> to <code>QueryUtil#ALL_POS</code> will return the full result set. If <code>orderByComparator</code> is specified, then the query will include the given ORDER BY logic. If <code>orderByComparator</code> is absent, then the query will include the default ORDER BY logic from <code>LayoutModelImpl</code>.
	 * </p>
	 *
	 * @param groupId the group ID
	 * @param masterLayoutPlid the master layout plid
	 * @param start the lower bound of the range of layouts
	 * @param end the upper bound of the range of layouts (not inclusive)
	 * @return the range of matching layouts
	 */
	public java.util.List<Layout> findByG_MLP(
		long groupId, long masterLayoutPlid, int start, int end);

	/**
	 * Returns an ordered range of all the layouts where groupId = &#63; and masterLayoutPlid = &#63;.
	 *
	 * <p>
	 * Useful when paginating results. Returns a maximum of <code>end - start</code> instances. <code>start</code> and <code>end</code> are not primary keys, they are indexes in the result set. Thus, <code>0</code> refers to the first result in the set. Setting both <code>start</code> and <code>end</code> to <code>QueryUtil#ALL_POS</code> will return the full result set. If <code>orderByComparator</code> is specified, then the query will include the given ORDER BY logic. If <code>orderByComparator</code> is absent, then the query will include the default ORDER BY logic from <code>LayoutModelImpl</code>.
	 * </p>
	 *
	 * @param groupId the group ID
	 * @param masterLayoutPlid the master layout plid
	 * @param start the lower bound of the range of layouts
	 * @param end the upper bound of the range of layouts (not inclusive)
	 * @param orderByComparator the comparator to order the results by (optionally <code>null</code>)
	 * @return the ordered range of matching layouts
	 */
	public java.util.List<Layout> findByG_MLP(
		long groupId, long masterLayoutPlid, int start, int end,
		com.liferay.portal.kernel.util.OrderByComparator<Layout>
			orderByComparator);

	/**
	 * Returns an ordered range of all the layouts where groupId = &#63; and masterLayoutPlid = &#63;.
	 *
	 * <p>
	 * Useful when paginating results. Returns a maximum of <code>end - start</code> instances. <code>start</code> and <code>end</code> are not primary keys, they are indexes in the result set. Thus, <code>0</code> refers to the first result in the set. Setting both <code>start</code> and <code>end</code> to <code>QueryUtil#ALL_POS</code> will return the full result set. If <code>orderByComparator</code> is specified, then the query will include the given ORDER BY logic. If <code>orderByComparator</code> is absent, then the query will include the default ORDER BY logic from <code>LayoutModelImpl</code>.
	 * </p>
	 *
	 * @param groupId the group ID
	 * @param masterLayoutPlid the master layout plid
	 * @param start the lower bound of the range of layouts
	 * @param end the upper bound of the range of layouts (not inclusive)
	 * @param orderByComparator the comparator to order the results by (optionally <code>null</code>)
	 * @param useFinderCache whether to use the finder cache
	 * @return the ordered range of matching layouts
	 */
	public java.util.List<Layout> findByG_MLP(
		long groupId, long masterLayoutPlid, int start, int end,
		com.liferay.portal.kernel.util.OrderByComparator<Layout>
			orderByComparator,
		boolean useFinderCache);

	/**
	 * Returns the first layout in the ordered set where groupId = &#63; and masterLayoutPlid = &#63;.
	 *
	 * @param groupId the group ID
	 * @param masterLayoutPlid the master layout plid
	 * @param orderByComparator the comparator to order the set by (optionally <code>null</code>)
	 * @return the first matching layout
	 * @throws NoSuchLayoutException if a matching layout could not be found
	 */
	public Layout findByG_MLP_First(
			long groupId, long masterLayoutPlid,
			com.liferay.portal.kernel.util.OrderByComparator<Layout>
				orderByComparator)
		throws NoSuchLayoutException;

	/**
	 * Returns the first layout in the ordered set where groupId = &#63; and masterLayoutPlid = &#63;.
	 *
	 * @param groupId the group ID
	 * @param masterLayoutPlid the master layout plid
	 * @param orderByComparator the comparator to order the set by (optionally <code>null</code>)
	 * @return the first matching layout, or <code>null</code> if a matching layout could not be found
	 */
	public Layout fetchByG_MLP_First(
		long groupId, long masterLayoutPlid,
		com.liferay.portal.kernel.util.OrderByComparator<Layout>
			orderByComparator);

	/**
	 * Returns the last layout in the ordered set where groupId = &#63; and masterLayoutPlid = &#63;.
	 *
	 * @param groupId the group ID
	 * @param masterLayoutPlid the master layout plid
	 * @param orderByComparator the comparator to order the set by (optionally <code>null</code>)
	 * @return the last matching layout
	 * @throws NoSuchLayoutException if a matching layout could not be found
	 */
	public Layout findByG_MLP_Last(
			long groupId, long masterLayoutPlid,
			com.liferay.portal.kernel.util.OrderByComparator<Layout>
				orderByComparator)
		throws NoSuchLayoutException;

	/**
	 * Returns the last layout in the ordered set where groupId = &#63; and masterLayoutPlid = &#63;.
	 *
	 * @param groupId the group ID
	 * @param masterLayoutPlid the master layout plid
	 * @param orderByComparator the comparator to order the set by (optionally <code>null</code>)
	 * @return the last matching layout, or <code>null</code> if a matching layout could not be found
	 */
	public Layout fetchByG_MLP_Last(
		long groupId, long masterLayoutPlid,
		com.liferay.portal.kernel.util.OrderByComparator<Layout>
			orderByComparator);

	/**
	 * Returns the layouts before and after the current layout in the ordered set where groupId = &#63; and masterLayoutPlid = &#63;.
	 *
	 * @param plid the primary key of the current layout
	 * @param groupId the group ID
	 * @param masterLayoutPlid the master layout plid
	 * @param orderByComparator the comparator to order the set by (optionally <code>null</code>)
	 * @return the previous, current, and next layout
	 * @throws NoSuchLayoutException if a layout with the primary key could not be found
	 */
	public Layout[] findByG_MLP_PrevAndNext(
			long plid, long groupId, long masterLayoutPlid,
			com.liferay.portal.kernel.util.OrderByComparator<Layout>
				orderByComparator)
		throws NoSuchLayoutException;

	/**
	 * Returns all the layouts that the user has permission to view where groupId = &#63; and masterLayoutPlid = &#63;.
	 *
	 * @param groupId the group ID
	 * @param masterLayoutPlid the master layout plid
	 * @return the matching layouts that the user has permission to view
	 */
	public java.util.List<Layout> filterFindByG_MLP(
		long groupId, long masterLayoutPlid);

	/**
	 * Returns a range of all the layouts that the user has permission to view where groupId = &#63; and masterLayoutPlid = &#63;.
	 *
	 * <p>
	 * Useful when paginating results. Returns a maximum of <code>end - start</code> instances. <code>start</code> and <code>end</code> are not primary keys, they are indexes in the result set. Thus, <code>0</code> refers to the first result in the set. Setting both <code>start</code> and <code>end</code> to <code>QueryUtil#ALL_POS</code> will return the full result set. If <code>orderByComparator</code> is specified, then the query will include the given ORDER BY logic. If <code>orderByComparator</code> is absent, then the query will include the default ORDER BY logic from <code>LayoutModelImpl</code>.
	 * </p>
	 *
	 * @param groupId the group ID
	 * @param masterLayoutPlid the master layout plid
	 * @param start the lower bound of the range of layouts
	 * @param end the upper bound of the range of layouts (not inclusive)
	 * @return the range of matching layouts that the user has permission to view
	 */
	public java.util.List<Layout> filterFindByG_MLP(
		long groupId, long masterLayoutPlid, int start, int end);

	/**
	 * Returns an ordered range of all the layouts that the user has permissions to view where groupId = &#63; and masterLayoutPlid = &#63;.
	 *
	 * <p>
	 * Useful when paginating results. Returns a maximum of <code>end - start</code> instances. <code>start</code> and <code>end</code> are not primary keys, they are indexes in the result set. Thus, <code>0</code> refers to the first result in the set. Setting both <code>start</code> and <code>end</code> to <code>QueryUtil#ALL_POS</code> will return the full result set. If <code>orderByComparator</code> is specified, then the query will include the given ORDER BY logic. If <code>orderByComparator</code> is absent, then the query will include the default ORDER BY logic from <code>LayoutModelImpl</code>.
	 * </p>
	 *
	 * @param groupId the group ID
	 * @param masterLayoutPlid the master layout plid
	 * @param start the lower bound of the range of layouts
	 * @param end the upper bound of the range of layouts (not inclusive)
	 * @param orderByComparator the comparator to order the results by (optionally <code>null</code>)
	 * @return the ordered range of matching layouts that the user has permission to view
	 */
	public java.util.List<Layout> filterFindByG_MLP(
		long groupId, long masterLayoutPlid, int start, int end,
		com.liferay.portal.kernel.util.OrderByComparator<Layout>
			orderByComparator);

	/**
	 * Returns the layouts before and after the current layout in the ordered set of layouts that the user has permission to view where groupId = &#63; and masterLayoutPlid = &#63;.
	 *
	 * @param plid the primary key of the current layout
	 * @param groupId the group ID
	 * @param masterLayoutPlid the master layout plid
	 * @param orderByComparator the comparator to order the set by (optionally <code>null</code>)
	 * @return the previous, current, and next layout
	 * @throws NoSuchLayoutException if a layout with the primary key could not be found
	 */
	public Layout[] filterFindByG_MLP_PrevAndNext(
			long plid, long groupId, long masterLayoutPlid,
			com.liferay.portal.kernel.util.OrderByComparator<Layout>
				orderByComparator)
		throws NoSuchLayoutException;

	/**
	 * Removes all the layouts where groupId = &#63; and masterLayoutPlid = &#63; from the database.
	 *
	 * @param groupId the group ID
	 * @param masterLayoutPlid the master layout plid
	 */
	public void removeByG_MLP(long groupId, long masterLayoutPlid);

	/**
	 * Returns the number of layouts where groupId = &#63; and masterLayoutPlid = &#63;.
	 *
	 * @param groupId the group ID
	 * @param masterLayoutPlid the master layout plid
	 * @return the number of matching layouts
	 */
	public int countByG_MLP(long groupId, long masterLayoutPlid);

	/**
	 * Returns the number of layouts that the user has permission to view where groupId = &#63; and masterLayoutPlid = &#63;.
	 *
	 * @param groupId the group ID
	 * @param masterLayoutPlid the master layout plid
	 * @return the number of matching layouts that the user has permission to view
	 */
	public int filterCountByG_MLP(long groupId, long masterLayoutPlid);

	/**
	 * Returns all the layouts where companyId = &#63; and layoutPrototypeUuid = &#63;.
	 *
	 * @param companyId the company ID
	 * @param layoutPrototypeUuid the layout prototype uuid
	 * @return the matching layouts
	 */
	public java.util.List<Layout> findByC_L(
		long companyId, String layoutPrototypeUuid);

	/**
	 * Returns a range of all the layouts where companyId = &#63; and layoutPrototypeUuid = &#63;.
	 *
	 * <p>
	 * Useful when paginating results. Returns a maximum of <code>end - start</code> instances. <code>start</code> and <code>end</code> are not primary keys, they are indexes in the result set. Thus, <code>0</code> refers to the first result in the set. Setting both <code>start</code> and <code>end</code> to <code>QueryUtil#ALL_POS</code> will return the full result set. If <code>orderByComparator</code> is specified, then the query will include the given ORDER BY logic. If <code>orderByComparator</code> is absent, then the query will include the default ORDER BY logic from <code>LayoutModelImpl</code>.
	 * </p>
	 *
	 * @param companyId the company ID
	 * @param layoutPrototypeUuid the layout prototype uuid
	 * @param start the lower bound of the range of layouts
	 * @param end the upper bound of the range of layouts (not inclusive)
	 * @return the range of matching layouts
	 */
	public java.util.List<Layout> findByC_L(
		long companyId, String layoutPrototypeUuid, int start, int end);

	/**
	 * Returns an ordered range of all the layouts where companyId = &#63; and layoutPrototypeUuid = &#63;.
	 *
	 * <p>
	 * Useful when paginating results. Returns a maximum of <code>end - start</code> instances. <code>start</code> and <code>end</code> are not primary keys, they are indexes in the result set. Thus, <code>0</code> refers to the first result in the set. Setting both <code>start</code> and <code>end</code> to <code>QueryUtil#ALL_POS</code> will return the full result set. If <code>orderByComparator</code> is specified, then the query will include the given ORDER BY logic. If <code>orderByComparator</code> is absent, then the query will include the default ORDER BY logic from <code>LayoutModelImpl</code>.
	 * </p>
	 *
	 * @param companyId the company ID
	 * @param layoutPrototypeUuid the layout prototype uuid
	 * @param start the lower bound of the range of layouts
	 * @param end the upper bound of the range of layouts (not inclusive)
	 * @param orderByComparator the comparator to order the results by (optionally <code>null</code>)
	 * @return the ordered range of matching layouts
	 */
	public java.util.List<Layout> findByC_L(
		long companyId, String layoutPrototypeUuid, int start, int end,
		com.liferay.portal.kernel.util.OrderByComparator<Layout>
			orderByComparator);

	/**
	 * Returns an ordered range of all the layouts where companyId = &#63; and layoutPrototypeUuid = &#63;.
	 *
	 * <p>
	 * Useful when paginating results. Returns a maximum of <code>end - start</code> instances. <code>start</code> and <code>end</code> are not primary keys, they are indexes in the result set. Thus, <code>0</code> refers to the first result in the set. Setting both <code>start</code> and <code>end</code> to <code>QueryUtil#ALL_POS</code> will return the full result set. If <code>orderByComparator</code> is specified, then the query will include the given ORDER BY logic. If <code>orderByComparator</code> is absent, then the query will include the default ORDER BY logic from <code>LayoutModelImpl</code>.
	 * </p>
	 *
	 * @param companyId the company ID
	 * @param layoutPrototypeUuid the layout prototype uuid
	 * @param start the lower bound of the range of layouts
	 * @param end the upper bound of the range of layouts (not inclusive)
	 * @param orderByComparator the comparator to order the results by (optionally <code>null</code>)
	 * @param useFinderCache whether to use the finder cache
	 * @return the ordered range of matching layouts
	 */
	public java.util.List<Layout> findByC_L(
		long companyId, String layoutPrototypeUuid, int start, int end,
		com.liferay.portal.kernel.util.OrderByComparator<Layout>
			orderByComparator,
		boolean useFinderCache);

	/**
	 * Returns the first layout in the ordered set where companyId = &#63; and layoutPrototypeUuid = &#63;.
	 *
	 * @param companyId the company ID
	 * @param layoutPrototypeUuid the layout prototype uuid
	 * @param orderByComparator the comparator to order the set by (optionally <code>null</code>)
	 * @return the first matching layout
	 * @throws NoSuchLayoutException if a matching layout could not be found
	 */
	public Layout findByC_L_First(
			long companyId, String layoutPrototypeUuid,
			com.liferay.portal.kernel.util.OrderByComparator<Layout>
				orderByComparator)
		throws NoSuchLayoutException;

	/**
	 * Returns the first layout in the ordered set where companyId = &#63; and layoutPrototypeUuid = &#63;.
	 *
	 * @param companyId the company ID
	 * @param layoutPrototypeUuid the layout prototype uuid
	 * @param orderByComparator the comparator to order the set by (optionally <code>null</code>)
	 * @return the first matching layout, or <code>null</code> if a matching layout could not be found
	 */
	public Layout fetchByC_L_First(
		long companyId, String layoutPrototypeUuid,
		com.liferay.portal.kernel.util.OrderByComparator<Layout>
			orderByComparator);

	/**
	 * Returns the last layout in the ordered set where companyId = &#63; and layoutPrototypeUuid = &#63;.
	 *
	 * @param companyId the company ID
	 * @param layoutPrototypeUuid the layout prototype uuid
	 * @param orderByComparator the comparator to order the set by (optionally <code>null</code>)
	 * @return the last matching layout
	 * @throws NoSuchLayoutException if a matching layout could not be found
	 */
	public Layout findByC_L_Last(
			long companyId, String layoutPrototypeUuid,
			com.liferay.portal.kernel.util.OrderByComparator<Layout>
				orderByComparator)
		throws NoSuchLayoutException;

	/**
	 * Returns the last layout in the ordered set where companyId = &#63; and layoutPrototypeUuid = &#63;.
	 *
	 * @param companyId the company ID
	 * @param layoutPrototypeUuid the layout prototype uuid
	 * @param orderByComparator the comparator to order the set by (optionally <code>null</code>)
	 * @return the last matching layout, or <code>null</code> if a matching layout could not be found
	 */
	public Layout fetchByC_L_Last(
		long companyId, String layoutPrototypeUuid,
		com.liferay.portal.kernel.util.OrderByComparator<Layout>
			orderByComparator);

	/**
	 * Returns the layouts before and after the current layout in the ordered set where companyId = &#63; and layoutPrototypeUuid = &#63;.
	 *
	 * @param plid the primary key of the current layout
	 * @param companyId the company ID
	 * @param layoutPrototypeUuid the layout prototype uuid
	 * @param orderByComparator the comparator to order the set by (optionally <code>null</code>)
	 * @return the previous, current, and next layout
	 * @throws NoSuchLayoutException if a layout with the primary key could not be found
	 */
	public Layout[] findByC_L_PrevAndNext(
			long plid, long companyId, String layoutPrototypeUuid,
			com.liferay.portal.kernel.util.OrderByComparator<Layout>
				orderByComparator)
		throws NoSuchLayoutException;

	/**
	 * Removes all the layouts where companyId = &#63; and layoutPrototypeUuid = &#63; from the database.
	 *
	 * @param companyId the company ID
	 * @param layoutPrototypeUuid the layout prototype uuid
	 */
	public void removeByC_L(long companyId, String layoutPrototypeUuid);

	/**
	 * Returns the number of layouts where companyId = &#63; and layoutPrototypeUuid = &#63;.
	 *
	 * @param companyId the company ID
	 * @param layoutPrototypeUuid the layout prototype uuid
	 * @return the number of matching layouts
	 */
	public int countByC_L(long companyId, String layoutPrototypeUuid);

	/**
	 * Returns all the layouts where privateLayout = &#63; and iconImageId = &#63;.
	 *
	 * @param privateLayout the private layout
	 * @param iconImageId the icon image ID
	 * @return the matching layouts
	 */
	public java.util.List<Layout> findByP_I(
		boolean privateLayout, long iconImageId);

	/**
	 * Returns a range of all the layouts where privateLayout = &#63; and iconImageId = &#63;.
	 *
	 * <p>
	 * Useful when paginating results. Returns a maximum of <code>end - start</code> instances. <code>start</code> and <code>end</code> are not primary keys, they are indexes in the result set. Thus, <code>0</code> refers to the first result in the set. Setting both <code>start</code> and <code>end</code> to <code>QueryUtil#ALL_POS</code> will return the full result set. If <code>orderByComparator</code> is specified, then the query will include the given ORDER BY logic. If <code>orderByComparator</code> is absent, then the query will include the default ORDER BY logic from <code>LayoutModelImpl</code>.
	 * </p>
	 *
	 * @param privateLayout the private layout
	 * @param iconImageId the icon image ID
	 * @param start the lower bound of the range of layouts
	 * @param end the upper bound of the range of layouts (not inclusive)
	 * @return the range of matching layouts
	 */
	public java.util.List<Layout> findByP_I(
		boolean privateLayout, long iconImageId, int start, int end);

	/**
	 * Returns an ordered range of all the layouts where privateLayout = &#63; and iconImageId = &#63;.
	 *
	 * <p>
	 * Useful when paginating results. Returns a maximum of <code>end - start</code> instances. <code>start</code> and <code>end</code> are not primary keys, they are indexes in the result set. Thus, <code>0</code> refers to the first result in the set. Setting both <code>start</code> and <code>end</code> to <code>QueryUtil#ALL_POS</code> will return the full result set. If <code>orderByComparator</code> is specified, then the query will include the given ORDER BY logic. If <code>orderByComparator</code> is absent, then the query will include the default ORDER BY logic from <code>LayoutModelImpl</code>.
	 * </p>
	 *
	 * @param privateLayout the private layout
	 * @param iconImageId the icon image ID
	 * @param start the lower bound of the range of layouts
	 * @param end the upper bound of the range of layouts (not inclusive)
	 * @param orderByComparator the comparator to order the results by (optionally <code>null</code>)
	 * @return the ordered range of matching layouts
	 */
	public java.util.List<Layout> findByP_I(
		boolean privateLayout, long iconImageId, int start, int end,
		com.liferay.portal.kernel.util.OrderByComparator<Layout>
			orderByComparator);

	/**
	 * Returns an ordered range of all the layouts where privateLayout = &#63; and iconImageId = &#63;.
	 *
	 * <p>
	 * Useful when paginating results. Returns a maximum of <code>end - start</code> instances. <code>start</code> and <code>end</code> are not primary keys, they are indexes in the result set. Thus, <code>0</code> refers to the first result in the set. Setting both <code>start</code> and <code>end</code> to <code>QueryUtil#ALL_POS</code> will return the full result set. If <code>orderByComparator</code> is specified, then the query will include the given ORDER BY logic. If <code>orderByComparator</code> is absent, then the query will include the default ORDER BY logic from <code>LayoutModelImpl</code>.
	 * </p>
	 *
	 * @param privateLayout the private layout
	 * @param iconImageId the icon image ID
	 * @param start the lower bound of the range of layouts
	 * @param end the upper bound of the range of layouts (not inclusive)
	 * @param orderByComparator the comparator to order the results by (optionally <code>null</code>)
	 * @param useFinderCache whether to use the finder cache
	 * @return the ordered range of matching layouts
	 */
	public java.util.List<Layout> findByP_I(
		boolean privateLayout, long iconImageId, int start, int end,
		com.liferay.portal.kernel.util.OrderByComparator<Layout>
			orderByComparator,
		boolean useFinderCache);

	/**
	 * Returns the first layout in the ordered set where privateLayout = &#63; and iconImageId = &#63;.
	 *
	 * @param privateLayout the private layout
	 * @param iconImageId the icon image ID
	 * @param orderByComparator the comparator to order the set by (optionally <code>null</code>)
	 * @return the first matching layout
	 * @throws NoSuchLayoutException if a matching layout could not be found
	 */
	public Layout findByP_I_First(
			boolean privateLayout, long iconImageId,
			com.liferay.portal.kernel.util.OrderByComparator<Layout>
				orderByComparator)
		throws NoSuchLayoutException;

	/**
	 * Returns the first layout in the ordered set where privateLayout = &#63; and iconImageId = &#63;.
	 *
	 * @param privateLayout the private layout
	 * @param iconImageId the icon image ID
	 * @param orderByComparator the comparator to order the set by (optionally <code>null</code>)
	 * @return the first matching layout, or <code>null</code> if a matching layout could not be found
	 */
	public Layout fetchByP_I_First(
		boolean privateLayout, long iconImageId,
		com.liferay.portal.kernel.util.OrderByComparator<Layout>
			orderByComparator);

	/**
	 * Returns the last layout in the ordered set where privateLayout = &#63; and iconImageId = &#63;.
	 *
	 * @param privateLayout the private layout
	 * @param iconImageId the icon image ID
	 * @param orderByComparator the comparator to order the set by (optionally <code>null</code>)
	 * @return the last matching layout
	 * @throws NoSuchLayoutException if a matching layout could not be found
	 */
	public Layout findByP_I_Last(
			boolean privateLayout, long iconImageId,
			com.liferay.portal.kernel.util.OrderByComparator<Layout>
				orderByComparator)
		throws NoSuchLayoutException;

	/**
	 * Returns the last layout in the ordered set where privateLayout = &#63; and iconImageId = &#63;.
	 *
	 * @param privateLayout the private layout
	 * @param iconImageId the icon image ID
	 * @param orderByComparator the comparator to order the set by (optionally <code>null</code>)
	 * @return the last matching layout, or <code>null</code> if a matching layout could not be found
	 */
	public Layout fetchByP_I_Last(
		boolean privateLayout, long iconImageId,
		com.liferay.portal.kernel.util.OrderByComparator<Layout>
			orderByComparator);

	/**
	 * Returns the layouts before and after the current layout in the ordered set where privateLayout = &#63; and iconImageId = &#63;.
	 *
	 * @param plid the primary key of the current layout
	 * @param privateLayout the private layout
	 * @param iconImageId the icon image ID
	 * @param orderByComparator the comparator to order the set by (optionally <code>null</code>)
	 * @return the previous, current, and next layout
	 * @throws NoSuchLayoutException if a layout with the primary key could not be found
	 */
	public Layout[] findByP_I_PrevAndNext(
			long plid, boolean privateLayout, long iconImageId,
			com.liferay.portal.kernel.util.OrderByComparator<Layout>
				orderByComparator)
		throws NoSuchLayoutException;

	/**
	 * Removes all the layouts where privateLayout = &#63; and iconImageId = &#63; from the database.
	 *
	 * @param privateLayout the private layout
	 * @param iconImageId the icon image ID
	 */
	public void removeByP_I(boolean privateLayout, long iconImageId);

	/**
	 * Returns the number of layouts where privateLayout = &#63; and iconImageId = &#63;.
	 *
	 * @param privateLayout the private layout
	 * @param iconImageId the icon image ID
	 * @return the number of matching layouts
	 */
	public int countByP_I(boolean privateLayout, long iconImageId);

	/**
	 * Returns all the layouts where classNameId = &#63; and classPK = &#63;.
	 *
	 * @param classNameId the class name ID
	 * @param classPK the class pk
	 * @return the matching layouts
	 */
	public java.util.List<Layout> findByC_C(long classNameId, long classPK);

	/**
	 * Returns a range of all the layouts where classNameId = &#63; and classPK = &#63;.
	 *
	 * <p>
	 * Useful when paginating results. Returns a maximum of <code>end - start</code> instances. <code>start</code> and <code>end</code> are not primary keys, they are indexes in the result set. Thus, <code>0</code> refers to the first result in the set. Setting both <code>start</code> and <code>end</code> to <code>QueryUtil#ALL_POS</code> will return the full result set. If <code>orderByComparator</code> is specified, then the query will include the given ORDER BY logic. If <code>orderByComparator</code> is absent, then the query will include the default ORDER BY logic from <code>LayoutModelImpl</code>.
	 * </p>
	 *
	 * @param classNameId the class name ID
	 * @param classPK the class pk
	 * @param start the lower bound of the range of layouts
	 * @param end the upper bound of the range of layouts (not inclusive)
	 * @return the range of matching layouts
	 */
	public java.util.List<Layout> findByC_C(
		long classNameId, long classPK, int start, int end);

	/**
	 * Returns an ordered range of all the layouts where classNameId = &#63; and classPK = &#63;.
	 *
	 * <p>
	 * Useful when paginating results. Returns a maximum of <code>end - start</code> instances. <code>start</code> and <code>end</code> are not primary keys, they are indexes in the result set. Thus, <code>0</code> refers to the first result in the set. Setting both <code>start</code> and <code>end</code> to <code>QueryUtil#ALL_POS</code> will return the full result set. If <code>orderByComparator</code> is specified, then the query will include the given ORDER BY logic. If <code>orderByComparator</code> is absent, then the query will include the default ORDER BY logic from <code>LayoutModelImpl</code>.
	 * </p>
	 *
	 * @param classNameId the class name ID
	 * @param classPK the class pk
	 * @param start the lower bound of the range of layouts
	 * @param end the upper bound of the range of layouts (not inclusive)
	 * @param orderByComparator the comparator to order the results by (optionally <code>null</code>)
	 * @return the ordered range of matching layouts
	 */
	public java.util.List<Layout> findByC_C(
		long classNameId, long classPK, int start, int end,
		com.liferay.portal.kernel.util.OrderByComparator<Layout>
			orderByComparator);

	/**
	 * Returns an ordered range of all the layouts where classNameId = &#63; and classPK = &#63;.
	 *
	 * <p>
	 * Useful when paginating results. Returns a maximum of <code>end - start</code> instances. <code>start</code> and <code>end</code> are not primary keys, they are indexes in the result set. Thus, <code>0</code> refers to the first result in the set. Setting both <code>start</code> and <code>end</code> to <code>QueryUtil#ALL_POS</code> will return the full result set. If <code>orderByComparator</code> is specified, then the query will include the given ORDER BY logic. If <code>orderByComparator</code> is absent, then the query will include the default ORDER BY logic from <code>LayoutModelImpl</code>.
	 * </p>
	 *
	 * @param classNameId the class name ID
	 * @param classPK the class pk
	 * @param start the lower bound of the range of layouts
	 * @param end the upper bound of the range of layouts (not inclusive)
	 * @param orderByComparator the comparator to order the results by (optionally <code>null</code>)
	 * @param useFinderCache whether to use the finder cache
	 * @return the ordered range of matching layouts
	 */
	public java.util.List<Layout> findByC_C(
		long classNameId, long classPK, int start, int end,
		com.liferay.portal.kernel.util.OrderByComparator<Layout>
			orderByComparator,
		boolean useFinderCache);

	/**
	 * Returns the first layout in the ordered set where classNameId = &#63; and classPK = &#63;.
	 *
	 * @param classNameId the class name ID
	 * @param classPK the class pk
	 * @param orderByComparator the comparator to order the set by (optionally <code>null</code>)
	 * @return the first matching layout
	 * @throws NoSuchLayoutException if a matching layout could not be found
	 */
	public Layout findByC_C_First(
			long classNameId, long classPK,
			com.liferay.portal.kernel.util.OrderByComparator<Layout>
				orderByComparator)
		throws NoSuchLayoutException;

	/**
	 * Returns the first layout in the ordered set where classNameId = &#63; and classPK = &#63;.
	 *
	 * @param classNameId the class name ID
	 * @param classPK the class pk
	 * @param orderByComparator the comparator to order the set by (optionally <code>null</code>)
	 * @return the first matching layout, or <code>null</code> if a matching layout could not be found
	 */
	public Layout fetchByC_C_First(
		long classNameId, long classPK,
		com.liferay.portal.kernel.util.OrderByComparator<Layout>
			orderByComparator);

	/**
	 * Returns the last layout in the ordered set where classNameId = &#63; and classPK = &#63;.
	 *
	 * @param classNameId the class name ID
	 * @param classPK the class pk
	 * @param orderByComparator the comparator to order the set by (optionally <code>null</code>)
	 * @return the last matching layout
	 * @throws NoSuchLayoutException if a matching layout could not be found
	 */
	public Layout findByC_C_Last(
			long classNameId, long classPK,
			com.liferay.portal.kernel.util.OrderByComparator<Layout>
				orderByComparator)
		throws NoSuchLayoutException;

	/**
	 * Returns the last layout in the ordered set where classNameId = &#63; and classPK = &#63;.
	 *
	 * @param classNameId the class name ID
	 * @param classPK the class pk
	 * @param orderByComparator the comparator to order the set by (optionally <code>null</code>)
	 * @return the last matching layout, or <code>null</code> if a matching layout could not be found
	 */
	public Layout fetchByC_C_Last(
		long classNameId, long classPK,
		com.liferay.portal.kernel.util.OrderByComparator<Layout>
			orderByComparator);

	/**
	 * Returns the layouts before and after the current layout in the ordered set where classNameId = &#63; and classPK = &#63;.
	 *
	 * @param plid the primary key of the current layout
	 * @param classNameId the class name ID
	 * @param classPK the class pk
	 * @param orderByComparator the comparator to order the set by (optionally <code>null</code>)
	 * @return the previous, current, and next layout
	 * @throws NoSuchLayoutException if a layout with the primary key could not be found
	 */
	public Layout[] findByC_C_PrevAndNext(
			long plid, long classNameId, long classPK,
			com.liferay.portal.kernel.util.OrderByComparator<Layout>
				orderByComparator)
		throws NoSuchLayoutException;

	/**
	 * Returns all the layouts where classNameId = &#63; and classPK = any &#63;.
	 *
	 * <p>
	 * Useful when paginating results. Returns a maximum of <code>end - start</code> instances. <code>start</code> and <code>end</code> are not primary keys, they are indexes in the result set. Thus, <code>0</code> refers to the first result in the set. Setting both <code>start</code> and <code>end</code> to <code>QueryUtil#ALL_POS</code> will return the full result set. If <code>orderByComparator</code> is specified, then the query will include the given ORDER BY logic. If <code>orderByComparator</code> is absent, then the query will include the default ORDER BY logic from <code>LayoutModelImpl</code>.
	 * </p>
	 *
	 * @param classNameId the class name ID
	 * @param classPKs the class pks
	 * @return the matching layouts
	 */
	public java.util.List<Layout> findByC_C(long classNameId, long[] classPKs);

	/**
	 * Returns a range of all the layouts where classNameId = &#63; and classPK = any &#63;.
	 *
	 * <p>
	 * Useful when paginating results. Returns a maximum of <code>end - start</code> instances. <code>start</code> and <code>end</code> are not primary keys, they are indexes in the result set. Thus, <code>0</code> refers to the first result in the set. Setting both <code>start</code> and <code>end</code> to <code>QueryUtil#ALL_POS</code> will return the full result set. If <code>orderByComparator</code> is specified, then the query will include the given ORDER BY logic. If <code>orderByComparator</code> is absent, then the query will include the default ORDER BY logic from <code>LayoutModelImpl</code>.
	 * </p>
	 *
	 * @param classNameId the class name ID
	 * @param classPKs the class pks
	 * @param start the lower bound of the range of layouts
	 * @param end the upper bound of the range of layouts (not inclusive)
	 * @return the range of matching layouts
	 */
	public java.util.List<Layout> findByC_C(
		long classNameId, long[] classPKs, int start, int end);

	/**
	 * Returns an ordered range of all the layouts where classNameId = &#63; and classPK = any &#63;.
	 *
	 * <p>
	 * Useful when paginating results. Returns a maximum of <code>end - start</code> instances. <code>start</code> and <code>end</code> are not primary keys, they are indexes in the result set. Thus, <code>0</code> refers to the first result in the set. Setting both <code>start</code> and <code>end</code> to <code>QueryUtil#ALL_POS</code> will return the full result set. If <code>orderByComparator</code> is specified, then the query will include the given ORDER BY logic. If <code>orderByComparator</code> is absent, then the query will include the default ORDER BY logic from <code>LayoutModelImpl</code>.
	 * </p>
	 *
	 * @param classNameId the class name ID
	 * @param classPKs the class pks
	 * @param start the lower bound of the range of layouts
	 * @param end the upper bound of the range of layouts (not inclusive)
	 * @param orderByComparator the comparator to order the results by (optionally <code>null</code>)
	 * @return the ordered range of matching layouts
	 */
	public java.util.List<Layout> findByC_C(
		long classNameId, long[] classPKs, int start, int end,
		com.liferay.portal.kernel.util.OrderByComparator<Layout>
			orderByComparator);

	/**
	 * Returns an ordered range of all the layouts where classNameId = &#63; and classPK = &#63;, optionally using the finder cache.
	 *
	 * <p>
	 * Useful when paginating results. Returns a maximum of <code>end - start</code> instances. <code>start</code> and <code>end</code> are not primary keys, they are indexes in the result set. Thus, <code>0</code> refers to the first result in the set. Setting both <code>start</code> and <code>end</code> to <code>QueryUtil#ALL_POS</code> will return the full result set. If <code>orderByComparator</code> is specified, then the query will include the given ORDER BY logic. If <code>orderByComparator</code> is absent, then the query will include the default ORDER BY logic from <code>LayoutModelImpl</code>.
	 * </p>
	 *
	 * @param classNameId the class name ID
	 * @param classPKs the class pks
	 * @param start the lower bound of the range of layouts
	 * @param end the upper bound of the range of layouts (not inclusive)
	 * @param orderByComparator the comparator to order the results by (optionally <code>null</code>)
	 * @param useFinderCache whether to use the finder cache
	 * @return the ordered range of matching layouts
	 */
	public java.util.List<Layout> findByC_C(
		long classNameId, long[] classPKs, int start, int end,
		com.liferay.portal.kernel.util.OrderByComparator<Layout>
			orderByComparator,
		boolean useFinderCache);

	/**
	 * Removes all the layouts where classNameId = &#63; and classPK = &#63; from the database.
	 *
	 * @param classNameId the class name ID
	 * @param classPK the class pk
	 */
	public void removeByC_C(long classNameId, long classPK);

	/**
	 * Returns the number of layouts where classNameId = &#63; and classPK = &#63;.
	 *
	 * @param classNameId the class name ID
	 * @param classPK the class pk
	 * @return the number of matching layouts
	 */
	public int countByC_C(long classNameId, long classPK);

	/**
	 * Returns the number of layouts where classNameId = &#63; and classPK = any &#63;.
	 *
	 * @param classNameId the class name ID
	 * @param classPKs the class pks
	 * @return the number of matching layouts
	 */
	public int countByC_C(long classNameId, long[] classPKs);

	/**
	 * Returns the layout where groupId = &#63; and privateLayout = &#63; and layoutId = &#63; or throws a <code>NoSuchLayoutException</code> if it could not be found.
	 *
	 * @param groupId the group ID
	 * @param privateLayout the private layout
	 * @param layoutId the layout ID
	 * @return the matching layout
	 * @throws NoSuchLayoutException if a matching layout could not be found
	 */
	public Layout findByG_P_L(
			long groupId, boolean privateLayout, long layoutId)
		throws NoSuchLayoutException;

	/**
	 * Returns the layout where groupId = &#63; and privateLayout = &#63; and layoutId = &#63; or returns <code>null</code> if it could not be found. Uses the finder cache.
	 *
	 * @param groupId the group ID
	 * @param privateLayout the private layout
	 * @param layoutId the layout ID
	 * @return the matching layout, or <code>null</code> if a matching layout could not be found
	 */
	public Layout fetchByG_P_L(
		long groupId, boolean privateLayout, long layoutId);

	/**
	 * Returns the layout where groupId = &#63; and privateLayout = &#63; and layoutId = &#63; or returns <code>null</code> if it could not be found, optionally using the finder cache.
	 *
	 * @param groupId the group ID
	 * @param privateLayout the private layout
	 * @param layoutId the layout ID
	 * @param useFinderCache whether to use the finder cache
	 * @return the matching layout, or <code>null</code> if a matching layout could not be found
	 */
	public Layout fetchByG_P_L(
		long groupId, boolean privateLayout, long layoutId,
		boolean useFinderCache);

	/**
	 * Removes the layout where groupId = &#63; and privateLayout = &#63; and layoutId = &#63; from the database.
	 *
	 * @param groupId the group ID
	 * @param privateLayout the private layout
	 * @param layoutId the layout ID
	 * @return the layout that was removed
	 */
	public Layout removeByG_P_L(
			long groupId, boolean privateLayout, long layoutId)
		throws NoSuchLayoutException;

	/**
	 * Returns the number of layouts where groupId = &#63; and privateLayout = &#63; and layoutId = &#63;.
	 *
	 * @param groupId the group ID
	 * @param privateLayout the private layout
	 * @param layoutId the layout ID
	 * @return the number of matching layouts
	 */
	public int countByG_P_L(long groupId, boolean privateLayout, long layoutId);

	/**
	 * Returns all the layouts where groupId = &#63; and privateLayout = &#63; and parentLayoutId = &#63;.
	 *
	 * @param groupId the group ID
	 * @param privateLayout the private layout
	 * @param parentLayoutId the parent layout ID
	 * @return the matching layouts
	 */
	public java.util.List<Layout> findByG_P_P(
		long groupId, boolean privateLayout, long parentLayoutId);

	/**
	 * Returns a range of all the layouts where groupId = &#63; and privateLayout = &#63; and parentLayoutId = &#63;.
	 *
	 * <p>
	 * Useful when paginating results. Returns a maximum of <code>end - start</code> instances. <code>start</code> and <code>end</code> are not primary keys, they are indexes in the result set. Thus, <code>0</code> refers to the first result in the set. Setting both <code>start</code> and <code>end</code> to <code>QueryUtil#ALL_POS</code> will return the full result set. If <code>orderByComparator</code> is specified, then the query will include the given ORDER BY logic. If <code>orderByComparator</code> is absent, then the query will include the default ORDER BY logic from <code>LayoutModelImpl</code>.
	 * </p>
	 *
	 * @param groupId the group ID
	 * @param privateLayout the private layout
	 * @param parentLayoutId the parent layout ID
	 * @param start the lower bound of the range of layouts
	 * @param end the upper bound of the range of layouts (not inclusive)
	 * @return the range of matching layouts
	 */
	public java.util.List<Layout> findByG_P_P(
		long groupId, boolean privateLayout, long parentLayoutId, int start,
		int end);

	/**
	 * Returns an ordered range of all the layouts where groupId = &#63; and privateLayout = &#63; and parentLayoutId = &#63;.
	 *
	 * <p>
	 * Useful when paginating results. Returns a maximum of <code>end - start</code> instances. <code>start</code> and <code>end</code> are not primary keys, they are indexes in the result set. Thus, <code>0</code> refers to the first result in the set. Setting both <code>start</code> and <code>end</code> to <code>QueryUtil#ALL_POS</code> will return the full result set. If <code>orderByComparator</code> is specified, then the query will include the given ORDER BY logic. If <code>orderByComparator</code> is absent, then the query will include the default ORDER BY logic from <code>LayoutModelImpl</code>.
	 * </p>
	 *
	 * @param groupId the group ID
	 * @param privateLayout the private layout
	 * @param parentLayoutId the parent layout ID
	 * @param start the lower bound of the range of layouts
	 * @param end the upper bound of the range of layouts (not inclusive)
	 * @param orderByComparator the comparator to order the results by (optionally <code>null</code>)
	 * @return the ordered range of matching layouts
	 */
	public java.util.List<Layout> findByG_P_P(
		long groupId, boolean privateLayout, long parentLayoutId, int start,
		int end,
		com.liferay.portal.kernel.util.OrderByComparator<Layout>
			orderByComparator);

	/**
	 * Returns an ordered range of all the layouts where groupId = &#63; and privateLayout = &#63; and parentLayoutId = &#63;.
	 *
	 * <p>
	 * Useful when paginating results. Returns a maximum of <code>end - start</code> instances. <code>start</code> and <code>end</code> are not primary keys, they are indexes in the result set. Thus, <code>0</code> refers to the first result in the set. Setting both <code>start</code> and <code>end</code> to <code>QueryUtil#ALL_POS</code> will return the full result set. If <code>orderByComparator</code> is specified, then the query will include the given ORDER BY logic. If <code>orderByComparator</code> is absent, then the query will include the default ORDER BY logic from <code>LayoutModelImpl</code>.
	 * </p>
	 *
	 * @param groupId the group ID
	 * @param privateLayout the private layout
	 * @param parentLayoutId the parent layout ID
	 * @param start the lower bound of the range of layouts
	 * @param end the upper bound of the range of layouts (not inclusive)
	 * @param orderByComparator the comparator to order the results by (optionally <code>null</code>)
	 * @param useFinderCache whether to use the finder cache
	 * @return the ordered range of matching layouts
	 */
	public java.util.List<Layout> findByG_P_P(
		long groupId, boolean privateLayout, long parentLayoutId, int start,
		int end,
		com.liferay.portal.kernel.util.OrderByComparator<Layout>
			orderByComparator,
		boolean useFinderCache);

	/**
	 * Returns the first layout in the ordered set where groupId = &#63; and privateLayout = &#63; and parentLayoutId = &#63;.
	 *
	 * @param groupId the group ID
	 * @param privateLayout the private layout
	 * @param parentLayoutId the parent layout ID
	 * @param orderByComparator the comparator to order the set by (optionally <code>null</code>)
	 * @return the first matching layout
	 * @throws NoSuchLayoutException if a matching layout could not be found
	 */
	public Layout findByG_P_P_First(
			long groupId, boolean privateLayout, long parentLayoutId,
			com.liferay.portal.kernel.util.OrderByComparator<Layout>
				orderByComparator)
		throws NoSuchLayoutException;

	/**
	 * Returns the first layout in the ordered set where groupId = &#63; and privateLayout = &#63; and parentLayoutId = &#63;.
	 *
	 * @param groupId the group ID
	 * @param privateLayout the private layout
	 * @param parentLayoutId the parent layout ID
	 * @param orderByComparator the comparator to order the set by (optionally <code>null</code>)
	 * @return the first matching layout, or <code>null</code> if a matching layout could not be found
	 */
	public Layout fetchByG_P_P_First(
		long groupId, boolean privateLayout, long parentLayoutId,
		com.liferay.portal.kernel.util.OrderByComparator<Layout>
			orderByComparator);

	/**
	 * Returns the last layout in the ordered set where groupId = &#63; and privateLayout = &#63; and parentLayoutId = &#63;.
	 *
	 * @param groupId the group ID
	 * @param privateLayout the private layout
	 * @param parentLayoutId the parent layout ID
	 * @param orderByComparator the comparator to order the set by (optionally <code>null</code>)
	 * @return the last matching layout
	 * @throws NoSuchLayoutException if a matching layout could not be found
	 */
	public Layout findByG_P_P_Last(
			long groupId, boolean privateLayout, long parentLayoutId,
			com.liferay.portal.kernel.util.OrderByComparator<Layout>
				orderByComparator)
		throws NoSuchLayoutException;

	/**
	 * Returns the last layout in the ordered set where groupId = &#63; and privateLayout = &#63; and parentLayoutId = &#63;.
	 *
	 * @param groupId the group ID
	 * @param privateLayout the private layout
	 * @param parentLayoutId the parent layout ID
	 * @param orderByComparator the comparator to order the set by (optionally <code>null</code>)
	 * @return the last matching layout, or <code>null</code> if a matching layout could not be found
	 */
	public Layout fetchByG_P_P_Last(
		long groupId, boolean privateLayout, long parentLayoutId,
		com.liferay.portal.kernel.util.OrderByComparator<Layout>
			orderByComparator);

	/**
	 * Returns the layouts before and after the current layout in the ordered set where groupId = &#63; and privateLayout = &#63; and parentLayoutId = &#63;.
	 *
	 * @param plid the primary key of the current layout
	 * @param groupId the group ID
	 * @param privateLayout the private layout
	 * @param parentLayoutId the parent layout ID
	 * @param orderByComparator the comparator to order the set by (optionally <code>null</code>)
	 * @return the previous, current, and next layout
	 * @throws NoSuchLayoutException if a layout with the primary key could not be found
	 */
	public Layout[] findByG_P_P_PrevAndNext(
			long plid, long groupId, boolean privateLayout, long parentLayoutId,
			com.liferay.portal.kernel.util.OrderByComparator<Layout>
				orderByComparator)
		throws NoSuchLayoutException;

	/**
	 * Returns all the layouts that the user has permission to view where groupId = &#63; and privateLayout = &#63; and parentLayoutId = &#63;.
	 *
	 * @param groupId the group ID
	 * @param privateLayout the private layout
	 * @param parentLayoutId the parent layout ID
	 * @return the matching layouts that the user has permission to view
	 */
	public java.util.List<Layout> filterFindByG_P_P(
		long groupId, boolean privateLayout, long parentLayoutId);

	/**
	 * Returns a range of all the layouts that the user has permission to view where groupId = &#63; and privateLayout = &#63; and parentLayoutId = &#63;.
	 *
	 * <p>
	 * Useful when paginating results. Returns a maximum of <code>end - start</code> instances. <code>start</code> and <code>end</code> are not primary keys, they are indexes in the result set. Thus, <code>0</code> refers to the first result in the set. Setting both <code>start</code> and <code>end</code> to <code>QueryUtil#ALL_POS</code> will return the full result set. If <code>orderByComparator</code> is specified, then the query will include the given ORDER BY logic. If <code>orderByComparator</code> is absent, then the query will include the default ORDER BY logic from <code>LayoutModelImpl</code>.
	 * </p>
	 *
	 * @param groupId the group ID
	 * @param privateLayout the private layout
	 * @param parentLayoutId the parent layout ID
	 * @param start the lower bound of the range of layouts
	 * @param end the upper bound of the range of layouts (not inclusive)
	 * @return the range of matching layouts that the user has permission to view
	 */
	public java.util.List<Layout> filterFindByG_P_P(
		long groupId, boolean privateLayout, long parentLayoutId, int start,
		int end);

	/**
	 * Returns an ordered range of all the layouts that the user has permissions to view where groupId = &#63; and privateLayout = &#63; and parentLayoutId = &#63;.
	 *
	 * <p>
	 * Useful when paginating results. Returns a maximum of <code>end - start</code> instances. <code>start</code> and <code>end</code> are not primary keys, they are indexes in the result set. Thus, <code>0</code> refers to the first result in the set. Setting both <code>start</code> and <code>end</code> to <code>QueryUtil#ALL_POS</code> will return the full result set. If <code>orderByComparator</code> is specified, then the query will include the given ORDER BY logic. If <code>orderByComparator</code> is absent, then the query will include the default ORDER BY logic from <code>LayoutModelImpl</code>.
	 * </p>
	 *
	 * @param groupId the group ID
	 * @param privateLayout the private layout
	 * @param parentLayoutId the parent layout ID
	 * @param start the lower bound of the range of layouts
	 * @param end the upper bound of the range of layouts (not inclusive)
	 * @param orderByComparator the comparator to order the results by (optionally <code>null</code>)
	 * @return the ordered range of matching layouts that the user has permission to view
	 */
	public java.util.List<Layout> filterFindByG_P_P(
		long groupId, boolean privateLayout, long parentLayoutId, int start,
		int end,
		com.liferay.portal.kernel.util.OrderByComparator<Layout>
			orderByComparator);

	/**
	 * Returns the layouts before and after the current layout in the ordered set of layouts that the user has permission to view where groupId = &#63; and privateLayout = &#63; and parentLayoutId = &#63;.
	 *
	 * @param plid the primary key of the current layout
	 * @param groupId the group ID
	 * @param privateLayout the private layout
	 * @param parentLayoutId the parent layout ID
	 * @param orderByComparator the comparator to order the set by (optionally <code>null</code>)
	 * @return the previous, current, and next layout
	 * @throws NoSuchLayoutException if a layout with the primary key could not be found
	 */
	public Layout[] filterFindByG_P_P_PrevAndNext(
			long plid, long groupId, boolean privateLayout, long parentLayoutId,
			com.liferay.portal.kernel.util.OrderByComparator<Layout>
				orderByComparator)
		throws NoSuchLayoutException;

	/**
	 * Returns all the layouts that the user has permission to view where groupId = &#63; and privateLayout = &#63; and parentLayoutId = any &#63;.
	 *
	 * @param groupId the group ID
	 * @param privateLayout the private layout
	 * @param parentLayoutIds the parent layout IDs
	 * @return the matching layouts that the user has permission to view
	 */
	public java.util.List<Layout> filterFindByG_P_P(
		long groupId, boolean privateLayout, long[] parentLayoutIds);

	/**
	 * Returns a range of all the layouts that the user has permission to view where groupId = &#63; and privateLayout = &#63; and parentLayoutId = any &#63;.
	 *
	 * <p>
	 * Useful when paginating results. Returns a maximum of <code>end - start</code> instances. <code>start</code> and <code>end</code> are not primary keys, they are indexes in the result set. Thus, <code>0</code> refers to the first result in the set. Setting both <code>start</code> and <code>end</code> to <code>QueryUtil#ALL_POS</code> will return the full result set. If <code>orderByComparator</code> is specified, then the query will include the given ORDER BY logic. If <code>orderByComparator</code> is absent, then the query will include the default ORDER BY logic from <code>LayoutModelImpl</code>.
	 * </p>
	 *
	 * @param groupId the group ID
	 * @param privateLayout the private layout
	 * @param parentLayoutIds the parent layout IDs
	 * @param start the lower bound of the range of layouts
	 * @param end the upper bound of the range of layouts (not inclusive)
	 * @return the range of matching layouts that the user has permission to view
	 */
	public java.util.List<Layout> filterFindByG_P_P(
		long groupId, boolean privateLayout, long[] parentLayoutIds, int start,
		int end);

	/**
	 * Returns an ordered range of all the layouts that the user has permission to view where groupId = &#63; and privateLayout = &#63; and parentLayoutId = any &#63;.
	 *
	 * <p>
	 * Useful when paginating results. Returns a maximum of <code>end - start</code> instances. <code>start</code> and <code>end</code> are not primary keys, they are indexes in the result set. Thus, <code>0</code> refers to the first result in the set. Setting both <code>start</code> and <code>end</code> to <code>QueryUtil#ALL_POS</code> will return the full result set. If <code>orderByComparator</code> is specified, then the query will include the given ORDER BY logic. If <code>orderByComparator</code> is absent, then the query will include the default ORDER BY logic from <code>LayoutModelImpl</code>.
	 * </p>
	 *
	 * @param groupId the group ID
	 * @param privateLayout the private layout
	 * @param parentLayoutIds the parent layout IDs
	 * @param start the lower bound of the range of layouts
	 * @param end the upper bound of the range of layouts (not inclusive)
	 * @param orderByComparator the comparator to order the results by (optionally <code>null</code>)
	 * @return the ordered range of matching layouts that the user has permission to view
	 */
	public java.util.List<Layout> filterFindByG_P_P(
		long groupId, boolean privateLayout, long[] parentLayoutIds, int start,
		int end,
		com.liferay.portal.kernel.util.OrderByComparator<Layout>
			orderByComparator);

	/**
	 * Returns all the layouts where groupId = &#63; and privateLayout = &#63; and parentLayoutId = any &#63;.
	 *
	 * <p>
	 * Useful when paginating results. Returns a maximum of <code>end - start</code> instances. <code>start</code> and <code>end</code> are not primary keys, they are indexes in the result set. Thus, <code>0</code> refers to the first result in the set. Setting both <code>start</code> and <code>end</code> to <code>QueryUtil#ALL_POS</code> will return the full result set. If <code>orderByComparator</code> is specified, then the query will include the given ORDER BY logic. If <code>orderByComparator</code> is absent, then the query will include the default ORDER BY logic from <code>LayoutModelImpl</code>.
	 * </p>
	 *
	 * @param groupId the group ID
	 * @param privateLayout the private layout
	 * @param parentLayoutIds the parent layout IDs
	 * @return the matching layouts
	 */
	public java.util.List<Layout> findByG_P_P(
		long groupId, boolean privateLayout, long[] parentLayoutIds);

	/**
	 * Returns a range of all the layouts where groupId = &#63; and privateLayout = &#63; and parentLayoutId = any &#63;.
	 *
	 * <p>
	 * Useful when paginating results. Returns a maximum of <code>end - start</code> instances. <code>start</code> and <code>end</code> are not primary keys, they are indexes in the result set. Thus, <code>0</code> refers to the first result in the set. Setting both <code>start</code> and <code>end</code> to <code>QueryUtil#ALL_POS</code> will return the full result set. If <code>orderByComparator</code> is specified, then the query will include the given ORDER BY logic. If <code>orderByComparator</code> is absent, then the query will include the default ORDER BY logic from <code>LayoutModelImpl</code>.
	 * </p>
	 *
	 * @param groupId the group ID
	 * @param privateLayout the private layout
	 * @param parentLayoutIds the parent layout IDs
	 * @param start the lower bound of the range of layouts
	 * @param end the upper bound of the range of layouts (not inclusive)
	 * @return the range of matching layouts
	 */
	public java.util.List<Layout> findByG_P_P(
		long groupId, boolean privateLayout, long[] parentLayoutIds, int start,
		int end);

	/**
	 * Returns an ordered range of all the layouts where groupId = &#63; and privateLayout = &#63; and parentLayoutId = any &#63;.
	 *
	 * <p>
	 * Useful when paginating results. Returns a maximum of <code>end - start</code> instances. <code>start</code> and <code>end</code> are not primary keys, they are indexes in the result set. Thus, <code>0</code> refers to the first result in the set. Setting both <code>start</code> and <code>end</code> to <code>QueryUtil#ALL_POS</code> will return the full result set. If <code>orderByComparator</code> is specified, then the query will include the given ORDER BY logic. If <code>orderByComparator</code> is absent, then the query will include the default ORDER BY logic from <code>LayoutModelImpl</code>.
	 * </p>
	 *
	 * @param groupId the group ID
	 * @param privateLayout the private layout
	 * @param parentLayoutIds the parent layout IDs
	 * @param start the lower bound of the range of layouts
	 * @param end the upper bound of the range of layouts (not inclusive)
	 * @param orderByComparator the comparator to order the results by (optionally <code>null</code>)
	 * @return the ordered range of matching layouts
	 */
	public java.util.List<Layout> findByG_P_P(
		long groupId, boolean privateLayout, long[] parentLayoutIds, int start,
		int end,
		com.liferay.portal.kernel.util.OrderByComparator<Layout>
			orderByComparator);

	/**
	 * Returns an ordered range of all the layouts where groupId = &#63; and privateLayout = &#63; and parentLayoutId = &#63;, optionally using the finder cache.
	 *
	 * <p>
	 * Useful when paginating results. Returns a maximum of <code>end - start</code> instances. <code>start</code> and <code>end</code> are not primary keys, they are indexes in the result set. Thus, <code>0</code> refers to the first result in the set. Setting both <code>start</code> and <code>end</code> to <code>QueryUtil#ALL_POS</code> will return the full result set. If <code>orderByComparator</code> is specified, then the query will include the given ORDER BY logic. If <code>orderByComparator</code> is absent, then the query will include the default ORDER BY logic from <code>LayoutModelImpl</code>.
	 * </p>
	 *
	 * @param groupId the group ID
	 * @param privateLayout the private layout
	 * @param parentLayoutIds the parent layout IDs
	 * @param start the lower bound of the range of layouts
	 * @param end the upper bound of the range of layouts (not inclusive)
	 * @param orderByComparator the comparator to order the results by (optionally <code>null</code>)
	 * @param useFinderCache whether to use the finder cache
	 * @return the ordered range of matching layouts
	 */
	public java.util.List<Layout> findByG_P_P(
		long groupId, boolean privateLayout, long[] parentLayoutIds, int start,
		int end,
		com.liferay.portal.kernel.util.OrderByComparator<Layout>
			orderByComparator,
		boolean useFinderCache);

	/**
	 * Removes all the layouts where groupId = &#63; and privateLayout = &#63; and parentLayoutId = &#63; from the database.
	 *
	 * @param groupId the group ID
	 * @param privateLayout the private layout
	 * @param parentLayoutId the parent layout ID
	 */
	public void removeByG_P_P(
		long groupId, boolean privateLayout, long parentLayoutId);

	/**
	 * Returns the number of layouts where groupId = &#63; and privateLayout = &#63; and parentLayoutId = &#63;.
	 *
	 * @param groupId the group ID
	 * @param privateLayout the private layout
	 * @param parentLayoutId the parent layout ID
	 * @return the number of matching layouts
	 */
	public int countByG_P_P(
		long groupId, boolean privateLayout, long parentLayoutId);

	/**
	 * Returns the number of layouts where groupId = &#63; and privateLayout = &#63; and parentLayoutId = any &#63;.
	 *
	 * @param groupId the group ID
	 * @param privateLayout the private layout
	 * @param parentLayoutIds the parent layout IDs
	 * @return the number of matching layouts
	 */
	public int countByG_P_P(
		long groupId, boolean privateLayout, long[] parentLayoutIds);

	/**
	 * Returns the number of layouts that the user has permission to view where groupId = &#63; and privateLayout = &#63; and parentLayoutId = &#63;.
	 *
	 * @param groupId the group ID
	 * @param privateLayout the private layout
	 * @param parentLayoutId the parent layout ID
	 * @return the number of matching layouts that the user has permission to view
	 */
	public int filterCountByG_P_P(
		long groupId, boolean privateLayout, long parentLayoutId);

	/**
	 * Returns the number of layouts that the user has permission to view where groupId = &#63; and privateLayout = &#63; and parentLayoutId = any &#63;.
	 *
	 * @param groupId the group ID
	 * @param privateLayout the private layout
	 * @param parentLayoutIds the parent layout IDs
	 * @return the number of matching layouts that the user has permission to view
	 */
	public int filterCountByG_P_P(
		long groupId, boolean privateLayout, long[] parentLayoutIds);

	/**
	 * Returns all the layouts where groupId = &#63; and privateLayout = &#63; and type = &#63;.
	 *
	 * @param groupId the group ID
	 * @param privateLayout the private layout
	 * @param type the type
	 * @return the matching layouts
	 */
	public java.util.List<Layout> findByG_P_T(
		long groupId, boolean privateLayout, String type);

	/**
	 * Returns a range of all the layouts where groupId = &#63; and privateLayout = &#63; and type = &#63;.
	 *
	 * <p>
	 * Useful when paginating results. Returns a maximum of <code>end - start</code> instances. <code>start</code> and <code>end</code> are not primary keys, they are indexes in the result set. Thus, <code>0</code> refers to the first result in the set. Setting both <code>start</code> and <code>end</code> to <code>QueryUtil#ALL_POS</code> will return the full result set. If <code>orderByComparator</code> is specified, then the query will include the given ORDER BY logic. If <code>orderByComparator</code> is absent, then the query will include the default ORDER BY logic from <code>LayoutModelImpl</code>.
	 * </p>
	 *
	 * @param groupId the group ID
	 * @param privateLayout the private layout
	 * @param type the type
	 * @param start the lower bound of the range of layouts
	 * @param end the upper bound of the range of layouts (not inclusive)
	 * @return the range of matching layouts
	 */
	public java.util.List<Layout> findByG_P_T(
		long groupId, boolean privateLayout, String type, int start, int end);

	/**
	 * Returns an ordered range of all the layouts where groupId = &#63; and privateLayout = &#63; and type = &#63;.
	 *
	 * <p>
	 * Useful when paginating results. Returns a maximum of <code>end - start</code> instances. <code>start</code> and <code>end</code> are not primary keys, they are indexes in the result set. Thus, <code>0</code> refers to the first result in the set. Setting both <code>start</code> and <code>end</code> to <code>QueryUtil#ALL_POS</code> will return the full result set. If <code>orderByComparator</code> is specified, then the query will include the given ORDER BY logic. If <code>orderByComparator</code> is absent, then the query will include the default ORDER BY logic from <code>LayoutModelImpl</code>.
	 * </p>
	 *
	 * @param groupId the group ID
	 * @param privateLayout the private layout
	 * @param type the type
	 * @param start the lower bound of the range of layouts
	 * @param end the upper bound of the range of layouts (not inclusive)
	 * @param orderByComparator the comparator to order the results by (optionally <code>null</code>)
	 * @return the ordered range of matching layouts
	 */
	public java.util.List<Layout> findByG_P_T(
		long groupId, boolean privateLayout, String type, int start, int end,
		com.liferay.portal.kernel.util.OrderByComparator<Layout>
			orderByComparator);

	/**
	 * Returns an ordered range of all the layouts where groupId = &#63; and privateLayout = &#63; and type = &#63;.
	 *
	 * <p>
	 * Useful when paginating results. Returns a maximum of <code>end - start</code> instances. <code>start</code> and <code>end</code> are not primary keys, they are indexes in the result set. Thus, <code>0</code> refers to the first result in the set. Setting both <code>start</code> and <code>end</code> to <code>QueryUtil#ALL_POS</code> will return the full result set. If <code>orderByComparator</code> is specified, then the query will include the given ORDER BY logic. If <code>orderByComparator</code> is absent, then the query will include the default ORDER BY logic from <code>LayoutModelImpl</code>.
	 * </p>
	 *
	 * @param groupId the group ID
	 * @param privateLayout the private layout
	 * @param type the type
	 * @param start the lower bound of the range of layouts
	 * @param end the upper bound of the range of layouts (not inclusive)
	 * @param orderByComparator the comparator to order the results by (optionally <code>null</code>)
	 * @param useFinderCache whether to use the finder cache
	 * @return the ordered range of matching layouts
	 */
	public java.util.List<Layout> findByG_P_T(
		long groupId, boolean privateLayout, String type, int start, int end,
		com.liferay.portal.kernel.util.OrderByComparator<Layout>
			orderByComparator,
		boolean useFinderCache);

	/**
	 * Returns the first layout in the ordered set where groupId = &#63; and privateLayout = &#63; and type = &#63;.
	 *
	 * @param groupId the group ID
	 * @param privateLayout the private layout
	 * @param type the type
	 * @param orderByComparator the comparator to order the set by (optionally <code>null</code>)
	 * @return the first matching layout
	 * @throws NoSuchLayoutException if a matching layout could not be found
	 */
	public Layout findByG_P_T_First(
			long groupId, boolean privateLayout, String type,
			com.liferay.portal.kernel.util.OrderByComparator<Layout>
				orderByComparator)
		throws NoSuchLayoutException;

	/**
	 * Returns the first layout in the ordered set where groupId = &#63; and privateLayout = &#63; and type = &#63;.
	 *
	 * @param groupId the group ID
	 * @param privateLayout the private layout
	 * @param type the type
	 * @param orderByComparator the comparator to order the set by (optionally <code>null</code>)
	 * @return the first matching layout, or <code>null</code> if a matching layout could not be found
	 */
	public Layout fetchByG_P_T_First(
		long groupId, boolean privateLayout, String type,
		com.liferay.portal.kernel.util.OrderByComparator<Layout>
			orderByComparator);

	/**
	 * Returns the last layout in the ordered set where groupId = &#63; and privateLayout = &#63; and type = &#63;.
	 *
	 * @param groupId the group ID
	 * @param privateLayout the private layout
	 * @param type the type
	 * @param orderByComparator the comparator to order the set by (optionally <code>null</code>)
	 * @return the last matching layout
	 * @throws NoSuchLayoutException if a matching layout could not be found
	 */
	public Layout findByG_P_T_Last(
			long groupId, boolean privateLayout, String type,
			com.liferay.portal.kernel.util.OrderByComparator<Layout>
				orderByComparator)
		throws NoSuchLayoutException;

	/**
	 * Returns the last layout in the ordered set where groupId = &#63; and privateLayout = &#63; and type = &#63;.
	 *
	 * @param groupId the group ID
	 * @param privateLayout the private layout
	 * @param type the type
	 * @param orderByComparator the comparator to order the set by (optionally <code>null</code>)
	 * @return the last matching layout, or <code>null</code> if a matching layout could not be found
	 */
	public Layout fetchByG_P_T_Last(
		long groupId, boolean privateLayout, String type,
		com.liferay.portal.kernel.util.OrderByComparator<Layout>
			orderByComparator);

	/**
	 * Returns the layouts before and after the current layout in the ordered set where groupId = &#63; and privateLayout = &#63; and type = &#63;.
	 *
	 * @param plid the primary key of the current layout
	 * @param groupId the group ID
	 * @param privateLayout the private layout
	 * @param type the type
	 * @param orderByComparator the comparator to order the set by (optionally <code>null</code>)
	 * @return the previous, current, and next layout
	 * @throws NoSuchLayoutException if a layout with the primary key could not be found
	 */
	public Layout[] findByG_P_T_PrevAndNext(
			long plid, long groupId, boolean privateLayout, String type,
			com.liferay.portal.kernel.util.OrderByComparator<Layout>
				orderByComparator)
		throws NoSuchLayoutException;

	/**
	 * Returns all the layouts that the user has permission to view where groupId = &#63; and privateLayout = &#63; and type = &#63;.
	 *
	 * @param groupId the group ID
	 * @param privateLayout the private layout
	 * @param type the type
	 * @return the matching layouts that the user has permission to view
	 */
	public java.util.List<Layout> filterFindByG_P_T(
		long groupId, boolean privateLayout, String type);

	/**
	 * Returns a range of all the layouts that the user has permission to view where groupId = &#63; and privateLayout = &#63; and type = &#63;.
	 *
	 * <p>
	 * Useful when paginating results. Returns a maximum of <code>end - start</code> instances. <code>start</code> and <code>end</code> are not primary keys, they are indexes in the result set. Thus, <code>0</code> refers to the first result in the set. Setting both <code>start</code> and <code>end</code> to <code>QueryUtil#ALL_POS</code> will return the full result set. If <code>orderByComparator</code> is specified, then the query will include the given ORDER BY logic. If <code>orderByComparator</code> is absent, then the query will include the default ORDER BY logic from <code>LayoutModelImpl</code>.
	 * </p>
	 *
	 * @param groupId the group ID
	 * @param privateLayout the private layout
	 * @param type the type
	 * @param start the lower bound of the range of layouts
	 * @param end the upper bound of the range of layouts (not inclusive)
	 * @return the range of matching layouts that the user has permission to view
	 */
	public java.util.List<Layout> filterFindByG_P_T(
		long groupId, boolean privateLayout, String type, int start, int end);

	/**
	 * Returns an ordered range of all the layouts that the user has permissions to view where groupId = &#63; and privateLayout = &#63; and type = &#63;.
	 *
	 * <p>
	 * Useful when paginating results. Returns a maximum of <code>end - start</code> instances. <code>start</code> and <code>end</code> are not primary keys, they are indexes in the result set. Thus, <code>0</code> refers to the first result in the set. Setting both <code>start</code> and <code>end</code> to <code>QueryUtil#ALL_POS</code> will return the full result set. If <code>orderByComparator</code> is specified, then the query will include the given ORDER BY logic. If <code>orderByComparator</code> is absent, then the query will include the default ORDER BY logic from <code>LayoutModelImpl</code>.
	 * </p>
	 *
	 * @param groupId the group ID
	 * @param privateLayout the private layout
	 * @param type the type
	 * @param start the lower bound of the range of layouts
	 * @param end the upper bound of the range of layouts (not inclusive)
	 * @param orderByComparator the comparator to order the results by (optionally <code>null</code>)
	 * @return the ordered range of matching layouts that the user has permission to view
	 */
	public java.util.List<Layout> filterFindByG_P_T(
		long groupId, boolean privateLayout, String type, int start, int end,
		com.liferay.portal.kernel.util.OrderByComparator<Layout>
			orderByComparator);

	/**
	 * Returns the layouts before and after the current layout in the ordered set of layouts that the user has permission to view where groupId = &#63; and privateLayout = &#63; and type = &#63;.
	 *
	 * @param plid the primary key of the current layout
	 * @param groupId the group ID
	 * @param privateLayout the private layout
	 * @param type the type
	 * @param orderByComparator the comparator to order the set by (optionally <code>null</code>)
	 * @return the previous, current, and next layout
	 * @throws NoSuchLayoutException if a layout with the primary key could not be found
	 */
	public Layout[] filterFindByG_P_T_PrevAndNext(
			long plid, long groupId, boolean privateLayout, String type,
			com.liferay.portal.kernel.util.OrderByComparator<Layout>
				orderByComparator)
		throws NoSuchLayoutException;

	/**
	 * Returns all the layouts that the user has permission to view where groupId = &#63; and privateLayout = &#63; and type = any &#63;.
	 *
	 * @param groupId the group ID
	 * @param privateLayout the private layout
	 * @param types the types
	 * @return the matching layouts that the user has permission to view
	 */
	public java.util.List<Layout> filterFindByG_P_T(
		long groupId, boolean privateLayout, String[] types);

	/**
	 * Returns a range of all the layouts that the user has permission to view where groupId = &#63; and privateLayout = &#63; and type = any &#63;.
	 *
	 * <p>
	 * Useful when paginating results. Returns a maximum of <code>end - start</code> instances. <code>start</code> and <code>end</code> are not primary keys, they are indexes in the result set. Thus, <code>0</code> refers to the first result in the set. Setting both <code>start</code> and <code>end</code> to <code>QueryUtil#ALL_POS</code> will return the full result set. If <code>orderByComparator</code> is specified, then the query will include the given ORDER BY logic. If <code>orderByComparator</code> is absent, then the query will include the default ORDER BY logic from <code>LayoutModelImpl</code>.
	 * </p>
	 *
	 * @param groupId the group ID
	 * @param privateLayout the private layout
	 * @param types the types
	 * @param start the lower bound of the range of layouts
	 * @param end the upper bound of the range of layouts (not inclusive)
	 * @return the range of matching layouts that the user has permission to view
	 */
	public java.util.List<Layout> filterFindByG_P_T(
		long groupId, boolean privateLayout, String[] types, int start,
		int end);

	/**
	 * Returns an ordered range of all the layouts that the user has permission to view where groupId = &#63; and privateLayout = &#63; and type = any &#63;.
	 *
	 * <p>
	 * Useful when paginating results. Returns a maximum of <code>end - start</code> instances. <code>start</code> and <code>end</code> are not primary keys, they are indexes in the result set. Thus, <code>0</code> refers to the first result in the set. Setting both <code>start</code> and <code>end</code> to <code>QueryUtil#ALL_POS</code> will return the full result set. If <code>orderByComparator</code> is specified, then the query will include the given ORDER BY logic. If <code>orderByComparator</code> is absent, then the query will include the default ORDER BY logic from <code>LayoutModelImpl</code>.
	 * </p>
	 *
	 * @param groupId the group ID
	 * @param privateLayout the private layout
	 * @param types the types
	 * @param start the lower bound of the range of layouts
	 * @param end the upper bound of the range of layouts (not inclusive)
	 * @param orderByComparator the comparator to order the results by (optionally <code>null</code>)
	 * @return the ordered range of matching layouts that the user has permission to view
	 */
	public java.util.List<Layout> filterFindByG_P_T(
		long groupId, boolean privateLayout, String[] types, int start, int end,
		com.liferay.portal.kernel.util.OrderByComparator<Layout>
			orderByComparator);

	/**
	 * Returns all the layouts where groupId = &#63; and privateLayout = &#63; and type = any &#63;.
	 *
	 * <p>
	 * Useful when paginating results. Returns a maximum of <code>end - start</code> instances. <code>start</code> and <code>end</code> are not primary keys, they are indexes in the result set. Thus, <code>0</code> refers to the first result in the set. Setting both <code>start</code> and <code>end</code> to <code>QueryUtil#ALL_POS</code> will return the full result set. If <code>orderByComparator</code> is specified, then the query will include the given ORDER BY logic. If <code>orderByComparator</code> is absent, then the query will include the default ORDER BY logic from <code>LayoutModelImpl</code>.
	 * </p>
	 *
	 * @param groupId the group ID
	 * @param privateLayout the private layout
	 * @param types the types
	 * @return the matching layouts
	 */
	public java.util.List<Layout> findByG_P_T(
		long groupId, boolean privateLayout, String[] types);

	/**
	 * Returns a range of all the layouts where groupId = &#63; and privateLayout = &#63; and type = any &#63;.
	 *
	 * <p>
	 * Useful when paginating results. Returns a maximum of <code>end - start</code> instances. <code>start</code> and <code>end</code> are not primary keys, they are indexes in the result set. Thus, <code>0</code> refers to the first result in the set. Setting both <code>start</code> and <code>end</code> to <code>QueryUtil#ALL_POS</code> will return the full result set. If <code>orderByComparator</code> is specified, then the query will include the given ORDER BY logic. If <code>orderByComparator</code> is absent, then the query will include the default ORDER BY logic from <code>LayoutModelImpl</code>.
	 * </p>
	 *
	 * @param groupId the group ID
	 * @param privateLayout the private layout
	 * @param types the types
	 * @param start the lower bound of the range of layouts
	 * @param end the upper bound of the range of layouts (not inclusive)
	 * @return the range of matching layouts
	 */
	public java.util.List<Layout> findByG_P_T(
		long groupId, boolean privateLayout, String[] types, int start,
		int end);

	/**
	 * Returns an ordered range of all the layouts where groupId = &#63; and privateLayout = &#63; and type = any &#63;.
	 *
	 * <p>
	 * Useful when paginating results. Returns a maximum of <code>end - start</code> instances. <code>start</code> and <code>end</code> are not primary keys, they are indexes in the result set. Thus, <code>0</code> refers to the first result in the set. Setting both <code>start</code> and <code>end</code> to <code>QueryUtil#ALL_POS</code> will return the full result set. If <code>orderByComparator</code> is specified, then the query will include the given ORDER BY logic. If <code>orderByComparator</code> is absent, then the query will include the default ORDER BY logic from <code>LayoutModelImpl</code>.
	 * </p>
	 *
	 * @param groupId the group ID
	 * @param privateLayout the private layout
	 * @param types the types
	 * @param start the lower bound of the range of layouts
	 * @param end the upper bound of the range of layouts (not inclusive)
	 * @param orderByComparator the comparator to order the results by (optionally <code>null</code>)
	 * @return the ordered range of matching layouts
	 */
	public java.util.List<Layout> findByG_P_T(
		long groupId, boolean privateLayout, String[] types, int start, int end,
		com.liferay.portal.kernel.util.OrderByComparator<Layout>
			orderByComparator);

	/**
	 * Returns an ordered range of all the layouts where groupId = &#63; and privateLayout = &#63; and type = &#63;, optionally using the finder cache.
	 *
	 * <p>
	 * Useful when paginating results. Returns a maximum of <code>end - start</code> instances. <code>start</code> and <code>end</code> are not primary keys, they are indexes in the result set. Thus, <code>0</code> refers to the first result in the set. Setting both <code>start</code> and <code>end</code> to <code>QueryUtil#ALL_POS</code> will return the full result set. If <code>orderByComparator</code> is specified, then the query will include the given ORDER BY logic. If <code>orderByComparator</code> is absent, then the query will include the default ORDER BY logic from <code>LayoutModelImpl</code>.
	 * </p>
	 *
	 * @param groupId the group ID
	 * @param privateLayout the private layout
	 * @param types the types
	 * @param start the lower bound of the range of layouts
	 * @param end the upper bound of the range of layouts (not inclusive)
	 * @param orderByComparator the comparator to order the results by (optionally <code>null</code>)
	 * @param useFinderCache whether to use the finder cache
	 * @return the ordered range of matching layouts
	 */
	public java.util.List<Layout> findByG_P_T(
		long groupId, boolean privateLayout, String[] types, int start, int end,
		com.liferay.portal.kernel.util.OrderByComparator<Layout>
			orderByComparator,
		boolean useFinderCache);

	/**
	 * Removes all the layouts where groupId = &#63; and privateLayout = &#63; and type = &#63; from the database.
	 *
	 * @param groupId the group ID
	 * @param privateLayout the private layout
	 * @param type the type
	 */
	public void removeByG_P_T(long groupId, boolean privateLayout, String type);

	/**
	 * Returns the number of layouts where groupId = &#63; and privateLayout = &#63; and type = &#63;.
	 *
	 * @param groupId the group ID
	 * @param privateLayout the private layout
	 * @param type the type
	 * @return the number of matching layouts
	 */
	public int countByG_P_T(long groupId, boolean privateLayout, String type);

	/**
	 * Returns the number of layouts where groupId = &#63; and privateLayout = &#63; and type = any &#63;.
	 *
	 * @param groupId the group ID
	 * @param privateLayout the private layout
	 * @param types the types
	 * @return the number of matching layouts
	 */
	public int countByG_P_T(
		long groupId, boolean privateLayout, String[] types);

	/**
	 * Returns the number of layouts that the user has permission to view where groupId = &#63; and privateLayout = &#63; and type = &#63;.
	 *
	 * @param groupId the group ID
	 * @param privateLayout the private layout
	 * @param type the type
	 * @return the number of matching layouts that the user has permission to view
	 */
	public int filterCountByG_P_T(
		long groupId, boolean privateLayout, String type);

	/**
	 * Returns the number of layouts that the user has permission to view where groupId = &#63; and privateLayout = &#63; and type = any &#63;.
	 *
	 * @param groupId the group ID
	 * @param privateLayout the private layout
	 * @param types the types
	 * @return the number of matching layouts that the user has permission to view
	 */
	public int filterCountByG_P_T(
		long groupId, boolean privateLayout, String[] types);

	/**
	 * Returns the layout where groupId = &#63; and privateLayout = &#63; and friendlyURL = &#63; or throws a <code>NoSuchLayoutException</code> if it could not be found.
	 *
	 * @param groupId the group ID
	 * @param privateLayout the private layout
	 * @param friendlyURL the friendly url
	 * @return the matching layout
	 * @throws NoSuchLayoutException if a matching layout could not be found
	 */
	public Layout findByG_P_F(
			long groupId, boolean privateLayout, String friendlyURL)
		throws NoSuchLayoutException;

	/**
	 * Returns the layout where groupId = &#63; and privateLayout = &#63; and friendlyURL = &#63; or returns <code>null</code> if it could not be found. Uses the finder cache.
	 *
	 * @param groupId the group ID
	 * @param privateLayout the private layout
	 * @param friendlyURL the friendly url
	 * @return the matching layout, or <code>null</code> if a matching layout could not be found
	 */
	public Layout fetchByG_P_F(
		long groupId, boolean privateLayout, String friendlyURL);

	/**
	 * Returns the layout where groupId = &#63; and privateLayout = &#63; and friendlyURL = &#63; or returns <code>null</code> if it could not be found, optionally using the finder cache.
	 *
	 * @param groupId the group ID
	 * @param privateLayout the private layout
	 * @param friendlyURL the friendly url
	 * @param useFinderCache whether to use the finder cache
	 * @return the matching layout, or <code>null</code> if a matching layout could not be found
	 */
	public Layout fetchByG_P_F(
		long groupId, boolean privateLayout, String friendlyURL,
		boolean useFinderCache);

	/**
	 * Removes the layout where groupId = &#63; and privateLayout = &#63; and friendlyURL = &#63; from the database.
	 *
	 * @param groupId the group ID
	 * @param privateLayout the private layout
	 * @param friendlyURL the friendly url
	 * @return the layout that was removed
	 */
	public Layout removeByG_P_F(
			long groupId, boolean privateLayout, String friendlyURL)
		throws NoSuchLayoutException;

	/**
	 * Returns the number of layouts where groupId = &#63; and privateLayout = &#63; and friendlyURL = &#63;.
	 *
	 * @param groupId the group ID
	 * @param privateLayout the private layout
	 * @param friendlyURL the friendly url
	 * @return the number of matching layouts
	 */
	public int countByG_P_F(
		long groupId, boolean privateLayout, String friendlyURL);

	/**
	 * Returns all the layouts where groupId = &#63; and privateLayout = &#63; and layoutSetPrototypeLayoutERC = &#63;.
	 *
	 * @param groupId the group ID
	 * @param privateLayout the private layout
	 * @param layoutSetPrototypeLayoutERC the layout set prototype layout erc
	 * @return the matching layouts
	 */
	public java.util.List<Layout> findByG_P_LSPLE(
		long groupId, boolean privateLayout,
		String layoutSetPrototypeLayoutERC);

	/**
	 * Returns a range of all the layouts where groupId = &#63; and privateLayout = &#63; and layoutSetPrototypeLayoutERC = &#63;.
	 *
	 * <p>
	 * Useful when paginating results. Returns a maximum of <code>end - start</code> instances. <code>start</code> and <code>end</code> are not primary keys, they are indexes in the result set. Thus, <code>0</code> refers to the first result in the set. Setting both <code>start</code> and <code>end</code> to <code>QueryUtil#ALL_POS</code> will return the full result set. If <code>orderByComparator</code> is specified, then the query will include the given ORDER BY logic. If <code>orderByComparator</code> is absent, then the query will include the default ORDER BY logic from <code>LayoutModelImpl</code>.
	 * </p>
	 *
	 * @param groupId the group ID
	 * @param privateLayout the private layout
	 * @param layoutSetPrototypeLayoutERC the layout set prototype layout erc
	 * @param start the lower bound of the range of layouts
	 * @param end the upper bound of the range of layouts (not inclusive)
	 * @return the range of matching layouts
	 */
	public java.util.List<Layout> findByG_P_LSPLE(
		long groupId, boolean privateLayout, String layoutSetPrototypeLayoutERC,
		int start, int end);

	/**
	 * Returns an ordered range of all the layouts where groupId = &#63; and privateLayout = &#63; and layoutSetPrototypeLayoutERC = &#63;.
	 *
	 * <p>
	 * Useful when paginating results. Returns a maximum of <code>end - start</code> instances. <code>start</code> and <code>end</code> are not primary keys, they are indexes in the result set. Thus, <code>0</code> refers to the first result in the set. Setting both <code>start</code> and <code>end</code> to <code>QueryUtil#ALL_POS</code> will return the full result set. If <code>orderByComparator</code> is specified, then the query will include the given ORDER BY logic. If <code>orderByComparator</code> is absent, then the query will include the default ORDER BY logic from <code>LayoutModelImpl</code>.
	 * </p>
	 *
	 * @param groupId the group ID
	 * @param privateLayout the private layout
	 * @param layoutSetPrototypeLayoutERC the layout set prototype layout erc
	 * @param start the lower bound of the range of layouts
	 * @param end the upper bound of the range of layouts (not inclusive)
	 * @param orderByComparator the comparator to order the results by (optionally <code>null</code>)
	 * @return the ordered range of matching layouts
	 */
	public java.util.List<Layout> findByG_P_LSPLE(
		long groupId, boolean privateLayout, String layoutSetPrototypeLayoutERC,
		int start, int end,
		com.liferay.portal.kernel.util.OrderByComparator<Layout>
			orderByComparator);

	/**
	 * Returns an ordered range of all the layouts where groupId = &#63; and privateLayout = &#63; and layoutSetPrototypeLayoutERC = &#63;.
	 *
	 * <p>
	 * Useful when paginating results. Returns a maximum of <code>end - start</code> instances. <code>start</code> and <code>end</code> are not primary keys, they are indexes in the result set. Thus, <code>0</code> refers to the first result in the set. Setting both <code>start</code> and <code>end</code> to <code>QueryUtil#ALL_POS</code> will return the full result set. If <code>orderByComparator</code> is specified, then the query will include the given ORDER BY logic. If <code>orderByComparator</code> is absent, then the query will include the default ORDER BY logic from <code>LayoutModelImpl</code>.
	 * </p>
	 *
	 * @param groupId the group ID
	 * @param privateLayout the private layout
	 * @param layoutSetPrototypeLayoutERC the layout set prototype layout erc
	 * @param start the lower bound of the range of layouts
	 * @param end the upper bound of the range of layouts (not inclusive)
	 * @param orderByComparator the comparator to order the results by (optionally <code>null</code>)
	 * @param useFinderCache whether to use the finder cache
	 * @return the ordered range of matching layouts
	 */
	public java.util.List<Layout> findByG_P_LSPLE(
		long groupId, boolean privateLayout, String layoutSetPrototypeLayoutERC,
		int start, int end,
		com.liferay.portal.kernel.util.OrderByComparator<Layout>
			orderByComparator,
		boolean useFinderCache);

	/**
	 * Returns the first layout in the ordered set where groupId = &#63; and privateLayout = &#63; and layoutSetPrototypeLayoutERC = &#63;.
	 *
	 * @param groupId the group ID
	 * @param privateLayout the private layout
	 * @param layoutSetPrototypeLayoutERC the layout set prototype layout erc
	 * @param orderByComparator the comparator to order the set by (optionally <code>null</code>)
	 * @return the first matching layout
	 * @throws NoSuchLayoutException if a matching layout could not be found
	 */
	public Layout findByG_P_LSPLE_First(
			long groupId, boolean privateLayout,
			String layoutSetPrototypeLayoutERC,
			com.liferay.portal.kernel.util.OrderByComparator<Layout>
				orderByComparator)
		throws NoSuchLayoutException;

	/**
	 * Returns the first layout in the ordered set where groupId = &#63; and privateLayout = &#63; and layoutSetPrototypeLayoutERC = &#63;.
	 *
	 * @param groupId the group ID
	 * @param privateLayout the private layout
	 * @param layoutSetPrototypeLayoutERC the layout set prototype layout erc
	 * @param orderByComparator the comparator to order the set by (optionally <code>null</code>)
	 * @return the first matching layout, or <code>null</code> if a matching layout could not be found
	 */
	public Layout fetchByG_P_LSPLE_First(
		long groupId, boolean privateLayout, String layoutSetPrototypeLayoutERC,
		com.liferay.portal.kernel.util.OrderByComparator<Layout>
			orderByComparator);

	/**
	 * Returns the last layout in the ordered set where groupId = &#63; and privateLayout = &#63; and layoutSetPrototypeLayoutERC = &#63;.
	 *
	 * @param groupId the group ID
	 * @param privateLayout the private layout
	 * @param layoutSetPrototypeLayoutERC the layout set prototype layout erc
	 * @param orderByComparator the comparator to order the set by (optionally <code>null</code>)
	 * @return the last matching layout
	 * @throws NoSuchLayoutException if a matching layout could not be found
	 */
	public Layout findByG_P_LSPLE_Last(
			long groupId, boolean privateLayout,
			String layoutSetPrototypeLayoutERC,
			com.liferay.portal.kernel.util.OrderByComparator<Layout>
				orderByComparator)
		throws NoSuchLayoutException;

	/**
	 * Returns the last layout in the ordered set where groupId = &#63; and privateLayout = &#63; and layoutSetPrototypeLayoutERC = &#63;.
	 *
	 * @param groupId the group ID
	 * @param privateLayout the private layout
	 * @param layoutSetPrototypeLayoutERC the layout set prototype layout erc
	 * @param orderByComparator the comparator to order the set by (optionally <code>null</code>)
	 * @return the last matching layout, or <code>null</code> if a matching layout could not be found
	 */
	public Layout fetchByG_P_LSPLE_Last(
		long groupId, boolean privateLayout, String layoutSetPrototypeLayoutERC,
		com.liferay.portal.kernel.util.OrderByComparator<Layout>
			orderByComparator);

	/**
	 * Returns the layouts before and after the current layout in the ordered set where groupId = &#63; and privateLayout = &#63; and layoutSetPrototypeLayoutERC = &#63;.
	 *
	 * @param plid the primary key of the current layout
	 * @param groupId the group ID
	 * @param privateLayout the private layout
	 * @param layoutSetPrototypeLayoutERC the layout set prototype layout erc
	 * @param orderByComparator the comparator to order the set by (optionally <code>null</code>)
	 * @return the previous, current, and next layout
	 * @throws NoSuchLayoutException if a layout with the primary key could not be found
	 */
	public Layout[] findByG_P_LSPLE_PrevAndNext(
			long plid, long groupId, boolean privateLayout,
			String layoutSetPrototypeLayoutERC,
			com.liferay.portal.kernel.util.OrderByComparator<Layout>
				orderByComparator)
		throws NoSuchLayoutException;

	/**
	 * Returns all the layouts that the user has permission to view where groupId = &#63; and privateLayout = &#63; and layoutSetPrototypeLayoutERC = &#63;.
	 *
	 * @param groupId the group ID
	 * @param privateLayout the private layout
	 * @param layoutSetPrototypeLayoutERC the layout set prototype layout erc
	 * @return the matching layouts that the user has permission to view
	 */
	public java.util.List<Layout> filterFindByG_P_LSPLE(
		long groupId, boolean privateLayout,
		String layoutSetPrototypeLayoutERC);

	/**
	 * Returns a range of all the layouts that the user has permission to view where groupId = &#63; and privateLayout = &#63; and layoutSetPrototypeLayoutERC = &#63;.
	 *
	 * <p>
	 * Useful when paginating results. Returns a maximum of <code>end - start</code> instances. <code>start</code> and <code>end</code> are not primary keys, they are indexes in the result set. Thus, <code>0</code> refers to the first result in the set. Setting both <code>start</code> and <code>end</code> to <code>QueryUtil#ALL_POS</code> will return the full result set. If <code>orderByComparator</code> is specified, then the query will include the given ORDER BY logic. If <code>orderByComparator</code> is absent, then the query will include the default ORDER BY logic from <code>LayoutModelImpl</code>.
	 * </p>
	 *
	 * @param groupId the group ID
	 * @param privateLayout the private layout
	 * @param layoutSetPrototypeLayoutERC the layout set prototype layout erc
	 * @param start the lower bound of the range of layouts
	 * @param end the upper bound of the range of layouts (not inclusive)
	 * @return the range of matching layouts that the user has permission to view
	 */
	public java.util.List<Layout> filterFindByG_P_LSPLE(
		long groupId, boolean privateLayout, String layoutSetPrototypeLayoutERC,
		int start, int end);

	/**
	 * Returns an ordered range of all the layouts that the user has permissions to view where groupId = &#63; and privateLayout = &#63; and layoutSetPrototypeLayoutERC = &#63;.
	 *
	 * <p>
	 * Useful when paginating results. Returns a maximum of <code>end - start</code> instances. <code>start</code> and <code>end</code> are not primary keys, they are indexes in the result set. Thus, <code>0</code> refers to the first result in the set. Setting both <code>start</code> and <code>end</code> to <code>QueryUtil#ALL_POS</code> will return the full result set. If <code>orderByComparator</code> is specified, then the query will include the given ORDER BY logic. If <code>orderByComparator</code> is absent, then the query will include the default ORDER BY logic from <code>LayoutModelImpl</code>.
	 * </p>
	 *
	 * @param groupId the group ID
	 * @param privateLayout the private layout
	 * @param layoutSetPrototypeLayoutERC the layout set prototype layout erc
	 * @param start the lower bound of the range of layouts
	 * @param end the upper bound of the range of layouts (not inclusive)
	 * @param orderByComparator the comparator to order the results by (optionally <code>null</code>)
	 * @return the ordered range of matching layouts that the user has permission to view
	 */
	public java.util.List<Layout> filterFindByG_P_LSPLE(
		long groupId, boolean privateLayout, String layoutSetPrototypeLayoutERC,
		int start, int end,
		com.liferay.portal.kernel.util.OrderByComparator<Layout>
			orderByComparator);

	/**
	 * Returns the layouts before and after the current layout in the ordered set of layouts that the user has permission to view where groupId = &#63; and privateLayout = &#63; and layoutSetPrototypeLayoutERC = &#63;.
	 *
	 * @param plid the primary key of the current layout
	 * @param groupId the group ID
	 * @param privateLayout the private layout
	 * @param layoutSetPrototypeLayoutERC the layout set prototype layout erc
	 * @param orderByComparator the comparator to order the set by (optionally <code>null</code>)
	 * @return the previous, current, and next layout
	 * @throws NoSuchLayoutException if a layout with the primary key could not be found
	 */
	public Layout[] filterFindByG_P_LSPLE_PrevAndNext(
			long plid, long groupId, boolean privateLayout,
			String layoutSetPrototypeLayoutERC,
			com.liferay.portal.kernel.util.OrderByComparator<Layout>
				orderByComparator)
		throws NoSuchLayoutException;

	/**
	 * Removes all the layouts where groupId = &#63; and privateLayout = &#63; and layoutSetPrototypeLayoutERC = &#63; from the database.
	 *
	 * @param groupId the group ID
	 * @param privateLayout the private layout
	 * @param layoutSetPrototypeLayoutERC the layout set prototype layout erc
	 */
	public void removeByG_P_LSPLE(
		long groupId, boolean privateLayout,
		String layoutSetPrototypeLayoutERC);

	/**
	 * Returns the number of layouts where groupId = &#63; and privateLayout = &#63; and layoutSetPrototypeLayoutERC = &#63;.
	 *
	 * @param groupId the group ID
	 * @param privateLayout the private layout
	 * @param layoutSetPrototypeLayoutERC the layout set prototype layout erc
	 * @return the number of matching layouts
	 */
	public int countByG_P_LSPLE(
		long groupId, boolean privateLayout,
		String layoutSetPrototypeLayoutERC);

	/**
	 * Returns the number of layouts that the user has permission to view where groupId = &#63; and privateLayout = &#63; and layoutSetPrototypeLayoutERC = &#63;.
	 *
	 * @param groupId the group ID
	 * @param privateLayout the private layout
	 * @param layoutSetPrototypeLayoutERC the layout set prototype layout erc
	 * @return the number of matching layouts that the user has permission to view
	 */
	public int filterCountByG_P_LSPLE(
		long groupId, boolean privateLayout,
		String layoutSetPrototypeLayoutERC);

	/**
	 * Returns all the layouts where groupId = &#63; and privateLayout = &#63; and status = &#63;.
	 *
	 * @param groupId the group ID
	 * @param privateLayout the private layout
	 * @param status the status
	 * @return the matching layouts
	 */
	public java.util.List<Layout> findByG_P_ST(
		long groupId, boolean privateLayout, int status);

	/**
	 * Returns a range of all the layouts where groupId = &#63; and privateLayout = &#63; and status = &#63;.
	 *
	 * <p>
	 * Useful when paginating results. Returns a maximum of <code>end - start</code> instances. <code>start</code> and <code>end</code> are not primary keys, they are indexes in the result set. Thus, <code>0</code> refers to the first result in the set. Setting both <code>start</code> and <code>end</code> to <code>QueryUtil#ALL_POS</code> will return the full result set. If <code>orderByComparator</code> is specified, then the query will include the given ORDER BY logic. If <code>orderByComparator</code> is absent, then the query will include the default ORDER BY logic from <code>LayoutModelImpl</code>.
	 * </p>
	 *
	 * @param groupId the group ID
	 * @param privateLayout the private layout
	 * @param status the status
	 * @param start the lower bound of the range of layouts
	 * @param end the upper bound of the range of layouts (not inclusive)
	 * @return the range of matching layouts
	 */
	public java.util.List<Layout> findByG_P_ST(
		long groupId, boolean privateLayout, int status, int start, int end);

	/**
	 * Returns an ordered range of all the layouts where groupId = &#63; and privateLayout = &#63; and status = &#63;.
	 *
	 * <p>
	 * Useful when paginating results. Returns a maximum of <code>end - start</code> instances. <code>start</code> and <code>end</code> are not primary keys, they are indexes in the result set. Thus, <code>0</code> refers to the first result in the set. Setting both <code>start</code> and <code>end</code> to <code>QueryUtil#ALL_POS</code> will return the full result set. If <code>orderByComparator</code> is specified, then the query will include the given ORDER BY logic. If <code>orderByComparator</code> is absent, then the query will include the default ORDER BY logic from <code>LayoutModelImpl</code>.
	 * </p>
	 *
	 * @param groupId the group ID
	 * @param privateLayout the private layout
	 * @param status the status
	 * @param start the lower bound of the range of layouts
	 * @param end the upper bound of the range of layouts (not inclusive)
	 * @param orderByComparator the comparator to order the results by (optionally <code>null</code>)
	 * @return the ordered range of matching layouts
	 */
	public java.util.List<Layout> findByG_P_ST(
		long groupId, boolean privateLayout, int status, int start, int end,
		com.liferay.portal.kernel.util.OrderByComparator<Layout>
			orderByComparator);

	/**
	 * Returns an ordered range of all the layouts where groupId = &#63; and privateLayout = &#63; and status = &#63;.
	 *
	 * <p>
	 * Useful when paginating results. Returns a maximum of <code>end - start</code> instances. <code>start</code> and <code>end</code> are not primary keys, they are indexes in the result set. Thus, <code>0</code> refers to the first result in the set. Setting both <code>start</code> and <code>end</code> to <code>QueryUtil#ALL_POS</code> will return the full result set. If <code>orderByComparator</code> is specified, then the query will include the given ORDER BY logic. If <code>orderByComparator</code> is absent, then the query will include the default ORDER BY logic from <code>LayoutModelImpl</code>.
	 * </p>
	 *
	 * @param groupId the group ID
	 * @param privateLayout the private layout
	 * @param status the status
	 * @param start the lower bound of the range of layouts
	 * @param end the upper bound of the range of layouts (not inclusive)
	 * @param orderByComparator the comparator to order the results by (optionally <code>null</code>)
	 * @param useFinderCache whether to use the finder cache
	 * @return the ordered range of matching layouts
	 */
	public java.util.List<Layout> findByG_P_ST(
		long groupId, boolean privateLayout, int status, int start, int end,
		com.liferay.portal.kernel.util.OrderByComparator<Layout>
			orderByComparator,
		boolean useFinderCache);

	/**
	 * Returns the first layout in the ordered set where groupId = &#63; and privateLayout = &#63; and status = &#63;.
	 *
	 * @param groupId the group ID
	 * @param privateLayout the private layout
	 * @param status the status
	 * @param orderByComparator the comparator to order the set by (optionally <code>null</code>)
	 * @return the first matching layout
	 * @throws NoSuchLayoutException if a matching layout could not be found
	 */
	public Layout findByG_P_ST_First(
			long groupId, boolean privateLayout, int status,
			com.liferay.portal.kernel.util.OrderByComparator<Layout>
				orderByComparator)
		throws NoSuchLayoutException;

	/**
	 * Returns the first layout in the ordered set where groupId = &#63; and privateLayout = &#63; and status = &#63;.
	 *
	 * @param groupId the group ID
	 * @param privateLayout the private layout
	 * @param status the status
	 * @param orderByComparator the comparator to order the set by (optionally <code>null</code>)
	 * @return the first matching layout, or <code>null</code> if a matching layout could not be found
	 */
	public Layout fetchByG_P_ST_First(
		long groupId, boolean privateLayout, int status,
		com.liferay.portal.kernel.util.OrderByComparator<Layout>
			orderByComparator);

	/**
	 * Returns the last layout in the ordered set where groupId = &#63; and privateLayout = &#63; and status = &#63;.
	 *
	 * @param groupId the group ID
	 * @param privateLayout the private layout
	 * @param status the status
	 * @param orderByComparator the comparator to order the set by (optionally <code>null</code>)
	 * @return the last matching layout
	 * @throws NoSuchLayoutException if a matching layout could not be found
	 */
	public Layout findByG_P_ST_Last(
			long groupId, boolean privateLayout, int status,
			com.liferay.portal.kernel.util.OrderByComparator<Layout>
				orderByComparator)
		throws NoSuchLayoutException;

	/**
	 * Returns the last layout in the ordered set where groupId = &#63; and privateLayout = &#63; and status = &#63;.
	 *
	 * @param groupId the group ID
	 * @param privateLayout the private layout
	 * @param status the status
	 * @param orderByComparator the comparator to order the set by (optionally <code>null</code>)
	 * @return the last matching layout, or <code>null</code> if a matching layout could not be found
	 */
	public Layout fetchByG_P_ST_Last(
		long groupId, boolean privateLayout, int status,
		com.liferay.portal.kernel.util.OrderByComparator<Layout>
			orderByComparator);

	/**
	 * Returns the layouts before and after the current layout in the ordered set where groupId = &#63; and privateLayout = &#63; and status = &#63;.
	 *
	 * @param plid the primary key of the current layout
	 * @param groupId the group ID
	 * @param privateLayout the private layout
	 * @param status the status
	 * @param orderByComparator the comparator to order the set by (optionally <code>null</code>)
	 * @return the previous, current, and next layout
	 * @throws NoSuchLayoutException if a layout with the primary key could not be found
	 */
	public Layout[] findByG_P_ST_PrevAndNext(
			long plid, long groupId, boolean privateLayout, int status,
			com.liferay.portal.kernel.util.OrderByComparator<Layout>
				orderByComparator)
		throws NoSuchLayoutException;

	/**
	 * Returns all the layouts that the user has permission to view where groupId = &#63; and privateLayout = &#63; and status = &#63;.
	 *
	 * @param groupId the group ID
	 * @param privateLayout the private layout
	 * @param status the status
	 * @return the matching layouts that the user has permission to view
	 */
	public java.util.List<Layout> filterFindByG_P_ST(
		long groupId, boolean privateLayout, int status);

	/**
	 * Returns a range of all the layouts that the user has permission to view where groupId = &#63; and privateLayout = &#63; and status = &#63;.
	 *
	 * <p>
	 * Useful when paginating results. Returns a maximum of <code>end - start</code> instances. <code>start</code> and <code>end</code> are not primary keys, they are indexes in the result set. Thus, <code>0</code> refers to the first result in the set. Setting both <code>start</code> and <code>end</code> to <code>QueryUtil#ALL_POS</code> will return the full result set. If <code>orderByComparator</code> is specified, then the query will include the given ORDER BY logic. If <code>orderByComparator</code> is absent, then the query will include the default ORDER BY logic from <code>LayoutModelImpl</code>.
	 * </p>
	 *
	 * @param groupId the group ID
	 * @param privateLayout the private layout
	 * @param status the status
	 * @param start the lower bound of the range of layouts
	 * @param end the upper bound of the range of layouts (not inclusive)
	 * @return the range of matching layouts that the user has permission to view
	 */
	public java.util.List<Layout> filterFindByG_P_ST(
		long groupId, boolean privateLayout, int status, int start, int end);

	/**
	 * Returns an ordered range of all the layouts that the user has permissions to view where groupId = &#63; and privateLayout = &#63; and status = &#63;.
	 *
	 * <p>
	 * Useful when paginating results. Returns a maximum of <code>end - start</code> instances. <code>start</code> and <code>end</code> are not primary keys, they are indexes in the result set. Thus, <code>0</code> refers to the first result in the set. Setting both <code>start</code> and <code>end</code> to <code>QueryUtil#ALL_POS</code> will return the full result set. If <code>orderByComparator</code> is specified, then the query will include the given ORDER BY logic. If <code>orderByComparator</code> is absent, then the query will include the default ORDER BY logic from <code>LayoutModelImpl</code>.
	 * </p>
	 *
	 * @param groupId the group ID
	 * @param privateLayout the private layout
	 * @param status the status
	 * @param start the lower bound of the range of layouts
	 * @param end the upper bound of the range of layouts (not inclusive)
	 * @param orderByComparator the comparator to order the results by (optionally <code>null</code>)
	 * @return the ordered range of matching layouts that the user has permission to view
	 */
	public java.util.List<Layout> filterFindByG_P_ST(
		long groupId, boolean privateLayout, int status, int start, int end,
		com.liferay.portal.kernel.util.OrderByComparator<Layout>
			orderByComparator);

	/**
	 * Returns the layouts before and after the current layout in the ordered set of layouts that the user has permission to view where groupId = &#63; and privateLayout = &#63; and status = &#63;.
	 *
	 * @param plid the primary key of the current layout
	 * @param groupId the group ID
	 * @param privateLayout the private layout
	 * @param status the status
	 * @param orderByComparator the comparator to order the set by (optionally <code>null</code>)
	 * @return the previous, current, and next layout
	 * @throws NoSuchLayoutException if a layout with the primary key could not be found
	 */
	public Layout[] filterFindByG_P_ST_PrevAndNext(
			long plid, long groupId, boolean privateLayout, int status,
			com.liferay.portal.kernel.util.OrderByComparator<Layout>
				orderByComparator)
		throws NoSuchLayoutException;

	/**
	 * Returns all the layouts that the user has permission to view where groupId = &#63; and privateLayout = &#63; and status = any &#63;.
	 *
	 * @param groupId the group ID
	 * @param privateLayout the private layout
	 * @param statuses the statuses
	 * @return the matching layouts that the user has permission to view
	 */
	public java.util.List<Layout> filterFindByG_P_ST(
		long groupId, boolean privateLayout, int[] statuses);

	/**
	 * Returns a range of all the layouts that the user has permission to view where groupId = &#63; and privateLayout = &#63; and status = any &#63;.
	 *
	 * <p>
	 * Useful when paginating results. Returns a maximum of <code>end - start</code> instances. <code>start</code> and <code>end</code> are not primary keys, they are indexes in the result set. Thus, <code>0</code> refers to the first result in the set. Setting both <code>start</code> and <code>end</code> to <code>QueryUtil#ALL_POS</code> will return the full result set. If <code>orderByComparator</code> is specified, then the query will include the given ORDER BY logic. If <code>orderByComparator</code> is absent, then the query will include the default ORDER BY logic from <code>LayoutModelImpl</code>.
	 * </p>
	 *
	 * @param groupId the group ID
	 * @param privateLayout the private layout
	 * @param statuses the statuses
	 * @param start the lower bound of the range of layouts
	 * @param end the upper bound of the range of layouts (not inclusive)
	 * @return the range of matching layouts that the user has permission to view
	 */
	public java.util.List<Layout> filterFindByG_P_ST(
		long groupId, boolean privateLayout, int[] statuses, int start,
		int end);

	/**
	 * Returns an ordered range of all the layouts that the user has permission to view where groupId = &#63; and privateLayout = &#63; and status = any &#63;.
	 *
	 * <p>
	 * Useful when paginating results. Returns a maximum of <code>end - start</code> instances. <code>start</code> and <code>end</code> are not primary keys, they are indexes in the result set. Thus, <code>0</code> refers to the first result in the set. Setting both <code>start</code> and <code>end</code> to <code>QueryUtil#ALL_POS</code> will return the full result set. If <code>orderByComparator</code> is specified, then the query will include the given ORDER BY logic. If <code>orderByComparator</code> is absent, then the query will include the default ORDER BY logic from <code>LayoutModelImpl</code>.
	 * </p>
	 *
	 * @param groupId the group ID
	 * @param privateLayout the private layout
	 * @param statuses the statuses
	 * @param start the lower bound of the range of layouts
	 * @param end the upper bound of the range of layouts (not inclusive)
	 * @param orderByComparator the comparator to order the results by (optionally <code>null</code>)
	 * @return the ordered range of matching layouts that the user has permission to view
	 */
	public java.util.List<Layout> filterFindByG_P_ST(
		long groupId, boolean privateLayout, int[] statuses, int start, int end,
		com.liferay.portal.kernel.util.OrderByComparator<Layout>
			orderByComparator);

	/**
	 * Returns all the layouts where groupId = &#63; and privateLayout = &#63; and status = any &#63;.
	 *
	 * <p>
	 * Useful when paginating results. Returns a maximum of <code>end - start</code> instances. <code>start</code> and <code>end</code> are not primary keys, they are indexes in the result set. Thus, <code>0</code> refers to the first result in the set. Setting both <code>start</code> and <code>end</code> to <code>QueryUtil#ALL_POS</code> will return the full result set. If <code>orderByComparator</code> is specified, then the query will include the given ORDER BY logic. If <code>orderByComparator</code> is absent, then the query will include the default ORDER BY logic from <code>LayoutModelImpl</code>.
	 * </p>
	 *
	 * @param groupId the group ID
	 * @param privateLayout the private layout
	 * @param statuses the statuses
	 * @return the matching layouts
	 */
	public java.util.List<Layout> findByG_P_ST(
		long groupId, boolean privateLayout, int[] statuses);

	/**
	 * Returns a range of all the layouts where groupId = &#63; and privateLayout = &#63; and status = any &#63;.
	 *
	 * <p>
	 * Useful when paginating results. Returns a maximum of <code>end - start</code> instances. <code>start</code> and <code>end</code> are not primary keys, they are indexes in the result set. Thus, <code>0</code> refers to the first result in the set. Setting both <code>start</code> and <code>end</code> to <code>QueryUtil#ALL_POS</code> will return the full result set. If <code>orderByComparator</code> is specified, then the query will include the given ORDER BY logic. If <code>orderByComparator</code> is absent, then the query will include the default ORDER BY logic from <code>LayoutModelImpl</code>.
	 * </p>
	 *
	 * @param groupId the group ID
	 * @param privateLayout the private layout
	 * @param statuses the statuses
	 * @param start the lower bound of the range of layouts
	 * @param end the upper bound of the range of layouts (not inclusive)
	 * @return the range of matching layouts
	 */
	public java.util.List<Layout> findByG_P_ST(
		long groupId, boolean privateLayout, int[] statuses, int start,
		int end);

	/**
	 * Returns an ordered range of all the layouts where groupId = &#63; and privateLayout = &#63; and status = any &#63;.
	 *
	 * <p>
	 * Useful when paginating results. Returns a maximum of <code>end - start</code> instances. <code>start</code> and <code>end</code> are not primary keys, they are indexes in the result set. Thus, <code>0</code> refers to the first result in the set. Setting both <code>start</code> and <code>end</code> to <code>QueryUtil#ALL_POS</code> will return the full result set. If <code>orderByComparator</code> is specified, then the query will include the given ORDER BY logic. If <code>orderByComparator</code> is absent, then the query will include the default ORDER BY logic from <code>LayoutModelImpl</code>.
	 * </p>
	 *
	 * @param groupId the group ID
	 * @param privateLayout the private layout
	 * @param statuses the statuses
	 * @param start the lower bound of the range of layouts
	 * @param end the upper bound of the range of layouts (not inclusive)
	 * @param orderByComparator the comparator to order the results by (optionally <code>null</code>)
	 * @return the ordered range of matching layouts
	 */
	public java.util.List<Layout> findByG_P_ST(
		long groupId, boolean privateLayout, int[] statuses, int start, int end,
		com.liferay.portal.kernel.util.OrderByComparator<Layout>
			orderByComparator);

	/**
	 * Returns an ordered range of all the layouts where groupId = &#63; and privateLayout = &#63; and status = &#63;, optionally using the finder cache.
	 *
	 * <p>
	 * Useful when paginating results. Returns a maximum of <code>end - start</code> instances. <code>start</code> and <code>end</code> are not primary keys, they are indexes in the result set. Thus, <code>0</code> refers to the first result in the set. Setting both <code>start</code> and <code>end</code> to <code>QueryUtil#ALL_POS</code> will return the full result set. If <code>orderByComparator</code> is specified, then the query will include the given ORDER BY logic. If <code>orderByComparator</code> is absent, then the query will include the default ORDER BY logic from <code>LayoutModelImpl</code>.
	 * </p>
	 *
	 * @param groupId the group ID
	 * @param privateLayout the private layout
	 * @param statuses the statuses
	 * @param start the lower bound of the range of layouts
	 * @param end the upper bound of the range of layouts (not inclusive)
	 * @param orderByComparator the comparator to order the results by (optionally <code>null</code>)
	 * @param useFinderCache whether to use the finder cache
	 * @return the ordered range of matching layouts
	 */
	public java.util.List<Layout> findByG_P_ST(
		long groupId, boolean privateLayout, int[] statuses, int start, int end,
		com.liferay.portal.kernel.util.OrderByComparator<Layout>
			orderByComparator,
		boolean useFinderCache);

	/**
	 * Removes all the layouts where groupId = &#63; and privateLayout = &#63; and status = &#63; from the database.
	 *
	 * @param groupId the group ID
	 * @param privateLayout the private layout
	 * @param status the status
	 */
	public void removeByG_P_ST(long groupId, boolean privateLayout, int status);

	/**
	 * Returns the number of layouts where groupId = &#63; and privateLayout = &#63; and status = &#63;.
	 *
	 * @param groupId the group ID
	 * @param privateLayout the private layout
	 * @param status the status
	 * @return the number of matching layouts
	 */
	public int countByG_P_ST(long groupId, boolean privateLayout, int status);

	/**
	 * Returns the number of layouts where groupId = &#63; and privateLayout = &#63; and status = any &#63;.
	 *
	 * @param groupId the group ID
	 * @param privateLayout the private layout
	 * @param statuses the statuses
	 * @return the number of matching layouts
	 */
	public int countByG_P_ST(
		long groupId, boolean privateLayout, int[] statuses);

	/**
	 * Returns the number of layouts that the user has permission to view where groupId = &#63; and privateLayout = &#63; and status = &#63;.
	 *
	 * @param groupId the group ID
	 * @param privateLayout the private layout
	 * @param status the status
	 * @return the number of matching layouts that the user has permission to view
	 */
	public int filterCountByG_P_ST(
		long groupId, boolean privateLayout, int status);

	/**
	 * Returns the number of layouts that the user has permission to view where groupId = &#63; and privateLayout = &#63; and status = any &#63;.
	 *
	 * @param groupId the group ID
	 * @param privateLayout the private layout
	 * @param statuses the statuses
	 * @return the number of matching layouts that the user has permission to view
	 */
	public int filterCountByG_P_ST(
		long groupId, boolean privateLayout, int[] statuses);

	/**
	 * Returns all the layouts where groupId = &#63; and privateLayout = &#63; and parentLayoutId = &#63; and hidden = &#63;.
	 *
	 * @param groupId the group ID
	 * @param privateLayout the private layout
	 * @param parentLayoutId the parent layout ID
	 * @param hidden the hidden
	 * @return the matching layouts
	 */
	public java.util.List<Layout> findByG_P_P_H(
		long groupId, boolean privateLayout, long parentLayoutId,
		boolean hidden);

	/**
	 * Returns a range of all the layouts where groupId = &#63; and privateLayout = &#63; and parentLayoutId = &#63; and hidden = &#63;.
	 *
	 * <p>
	 * Useful when paginating results. Returns a maximum of <code>end - start</code> instances. <code>start</code> and <code>end</code> are not primary keys, they are indexes in the result set. Thus, <code>0</code> refers to the first result in the set. Setting both <code>start</code> and <code>end</code> to <code>QueryUtil#ALL_POS</code> will return the full result set. If <code>orderByComparator</code> is specified, then the query will include the given ORDER BY logic. If <code>orderByComparator</code> is absent, then the query will include the default ORDER BY logic from <code>LayoutModelImpl</code>.
	 * </p>
	 *
	 * @param groupId the group ID
	 * @param privateLayout the private layout
	 * @param parentLayoutId the parent layout ID
	 * @param hidden the hidden
	 * @param start the lower bound of the range of layouts
	 * @param end the upper bound of the range of layouts (not inclusive)
	 * @return the range of matching layouts
	 */
	public java.util.List<Layout> findByG_P_P_H(
		long groupId, boolean privateLayout, long parentLayoutId,
		boolean hidden, int start, int end);

	/**
	 * Returns an ordered range of all the layouts where groupId = &#63; and privateLayout = &#63; and parentLayoutId = &#63; and hidden = &#63;.
	 *
	 * <p>
	 * Useful when paginating results. Returns a maximum of <code>end - start</code> instances. <code>start</code> and <code>end</code> are not primary keys, they are indexes in the result set. Thus, <code>0</code> refers to the first result in the set. Setting both <code>start</code> and <code>end</code> to <code>QueryUtil#ALL_POS</code> will return the full result set. If <code>orderByComparator</code> is specified, then the query will include the given ORDER BY logic. If <code>orderByComparator</code> is absent, then the query will include the default ORDER BY logic from <code>LayoutModelImpl</code>.
	 * </p>
	 *
	 * @param groupId the group ID
	 * @param privateLayout the private layout
	 * @param parentLayoutId the parent layout ID
	 * @param hidden the hidden
	 * @param start the lower bound of the range of layouts
	 * @param end the upper bound of the range of layouts (not inclusive)
	 * @param orderByComparator the comparator to order the results by (optionally <code>null</code>)
	 * @return the ordered range of matching layouts
	 */
	public java.util.List<Layout> findByG_P_P_H(
		long groupId, boolean privateLayout, long parentLayoutId,
		boolean hidden, int start, int end,
		com.liferay.portal.kernel.util.OrderByComparator<Layout>
			orderByComparator);

	/**
	 * Returns an ordered range of all the layouts where groupId = &#63; and privateLayout = &#63; and parentLayoutId = &#63; and hidden = &#63;.
	 *
	 * <p>
	 * Useful when paginating results. Returns a maximum of <code>end - start</code> instances. <code>start</code> and <code>end</code> are not primary keys, they are indexes in the result set. Thus, <code>0</code> refers to the first result in the set. Setting both <code>start</code> and <code>end</code> to <code>QueryUtil#ALL_POS</code> will return the full result set. If <code>orderByComparator</code> is specified, then the query will include the given ORDER BY logic. If <code>orderByComparator</code> is absent, then the query will include the default ORDER BY logic from <code>LayoutModelImpl</code>.
	 * </p>
	 *
	 * @param groupId the group ID
	 * @param privateLayout the private layout
	 * @param parentLayoutId the parent layout ID
	 * @param hidden the hidden
	 * @param start the lower bound of the range of layouts
	 * @param end the upper bound of the range of layouts (not inclusive)
	 * @param orderByComparator the comparator to order the results by (optionally <code>null</code>)
	 * @param useFinderCache whether to use the finder cache
	 * @return the ordered range of matching layouts
	 */
	public java.util.List<Layout> findByG_P_P_H(
		long groupId, boolean privateLayout, long parentLayoutId,
		boolean hidden, int start, int end,
		com.liferay.portal.kernel.util.OrderByComparator<Layout>
			orderByComparator,
		boolean useFinderCache);

	/**
	 * Returns the first layout in the ordered set where groupId = &#63; and privateLayout = &#63; and parentLayoutId = &#63; and hidden = &#63;.
	 *
	 * @param groupId the group ID
	 * @param privateLayout the private layout
	 * @param parentLayoutId the parent layout ID
	 * @param hidden the hidden
	 * @param orderByComparator the comparator to order the set by (optionally <code>null</code>)
	 * @return the first matching layout
	 * @throws NoSuchLayoutException if a matching layout could not be found
	 */
	public Layout findByG_P_P_H_First(
			long groupId, boolean privateLayout, long parentLayoutId,
			boolean hidden,
			com.liferay.portal.kernel.util.OrderByComparator<Layout>
				orderByComparator)
		throws NoSuchLayoutException;

	/**
	 * Returns the first layout in the ordered set where groupId = &#63; and privateLayout = &#63; and parentLayoutId = &#63; and hidden = &#63;.
	 *
	 * @param groupId the group ID
	 * @param privateLayout the private layout
	 * @param parentLayoutId the parent layout ID
	 * @param hidden the hidden
	 * @param orderByComparator the comparator to order the set by (optionally <code>null</code>)
	 * @return the first matching layout, or <code>null</code> if a matching layout could not be found
	 */
	public Layout fetchByG_P_P_H_First(
		long groupId, boolean privateLayout, long parentLayoutId,
		boolean hidden,
		com.liferay.portal.kernel.util.OrderByComparator<Layout>
			orderByComparator);

	/**
	 * Returns the last layout in the ordered set where groupId = &#63; and privateLayout = &#63; and parentLayoutId = &#63; and hidden = &#63;.
	 *
	 * @param groupId the group ID
	 * @param privateLayout the private layout
	 * @param parentLayoutId the parent layout ID
	 * @param hidden the hidden
	 * @param orderByComparator the comparator to order the set by (optionally <code>null</code>)
	 * @return the last matching layout
	 * @throws NoSuchLayoutException if a matching layout could not be found
	 */
	public Layout findByG_P_P_H_Last(
			long groupId, boolean privateLayout, long parentLayoutId,
			boolean hidden,
			com.liferay.portal.kernel.util.OrderByComparator<Layout>
				orderByComparator)
		throws NoSuchLayoutException;

	/**
	 * Returns the last layout in the ordered set where groupId = &#63; and privateLayout = &#63; and parentLayoutId = &#63; and hidden = &#63;.
	 *
	 * @param groupId the group ID
	 * @param privateLayout the private layout
	 * @param parentLayoutId the parent layout ID
	 * @param hidden the hidden
	 * @param orderByComparator the comparator to order the set by (optionally <code>null</code>)
	 * @return the last matching layout, or <code>null</code> if a matching layout could not be found
	 */
	public Layout fetchByG_P_P_H_Last(
		long groupId, boolean privateLayout, long parentLayoutId,
		boolean hidden,
		com.liferay.portal.kernel.util.OrderByComparator<Layout>
			orderByComparator);

	/**
	 * Returns the layouts before and after the current layout in the ordered set where groupId = &#63; and privateLayout = &#63; and parentLayoutId = &#63; and hidden = &#63;.
	 *
	 * @param plid the primary key of the current layout
	 * @param groupId the group ID
	 * @param privateLayout the private layout
	 * @param parentLayoutId the parent layout ID
	 * @param hidden the hidden
	 * @param orderByComparator the comparator to order the set by (optionally <code>null</code>)
	 * @return the previous, current, and next layout
	 * @throws NoSuchLayoutException if a layout with the primary key could not be found
	 */
	public Layout[] findByG_P_P_H_PrevAndNext(
			long plid, long groupId, boolean privateLayout, long parentLayoutId,
			boolean hidden,
			com.liferay.portal.kernel.util.OrderByComparator<Layout>
				orderByComparator)
		throws NoSuchLayoutException;

	/**
	 * Returns all the layouts that the user has permission to view where groupId = &#63; and privateLayout = &#63; and parentLayoutId = &#63; and hidden = &#63;.
	 *
	 * @param groupId the group ID
	 * @param privateLayout the private layout
	 * @param parentLayoutId the parent layout ID
	 * @param hidden the hidden
	 * @return the matching layouts that the user has permission to view
	 */
	public java.util.List<Layout> filterFindByG_P_P_H(
		long groupId, boolean privateLayout, long parentLayoutId,
		boolean hidden);

	/**
	 * Returns a range of all the layouts that the user has permission to view where groupId = &#63; and privateLayout = &#63; and parentLayoutId = &#63; and hidden = &#63;.
	 *
	 * <p>
	 * Useful when paginating results. Returns a maximum of <code>end - start</code> instances. <code>start</code> and <code>end</code> are not primary keys, they are indexes in the result set. Thus, <code>0</code> refers to the first result in the set. Setting both <code>start</code> and <code>end</code> to <code>QueryUtil#ALL_POS</code> will return the full result set. If <code>orderByComparator</code> is specified, then the query will include the given ORDER BY logic. If <code>orderByComparator</code> is absent, then the query will include the default ORDER BY logic from <code>LayoutModelImpl</code>.
	 * </p>
	 *
	 * @param groupId the group ID
	 * @param privateLayout the private layout
	 * @param parentLayoutId the parent layout ID
	 * @param hidden the hidden
	 * @param start the lower bound of the range of layouts
	 * @param end the upper bound of the range of layouts (not inclusive)
	 * @return the range of matching layouts that the user has permission to view
	 */
	public java.util.List<Layout> filterFindByG_P_P_H(
		long groupId, boolean privateLayout, long parentLayoutId,
		boolean hidden, int start, int end);

	/**
	 * Returns an ordered range of all the layouts that the user has permissions to view where groupId = &#63; and privateLayout = &#63; and parentLayoutId = &#63; and hidden = &#63;.
	 *
	 * <p>
	 * Useful when paginating results. Returns a maximum of <code>end - start</code> instances. <code>start</code> and <code>end</code> are not primary keys, they are indexes in the result set. Thus, <code>0</code> refers to the first result in the set. Setting both <code>start</code> and <code>end</code> to <code>QueryUtil#ALL_POS</code> will return the full result set. If <code>orderByComparator</code> is specified, then the query will include the given ORDER BY logic. If <code>orderByComparator</code> is absent, then the query will include the default ORDER BY logic from <code>LayoutModelImpl</code>.
	 * </p>
	 *
	 * @param groupId the group ID
	 * @param privateLayout the private layout
	 * @param parentLayoutId the parent layout ID
	 * @param hidden the hidden
	 * @param start the lower bound of the range of layouts
	 * @param end the upper bound of the range of layouts (not inclusive)
	 * @param orderByComparator the comparator to order the results by (optionally <code>null</code>)
	 * @return the ordered range of matching layouts that the user has permission to view
	 */
	public java.util.List<Layout> filterFindByG_P_P_H(
		long groupId, boolean privateLayout, long parentLayoutId,
		boolean hidden, int start, int end,
		com.liferay.portal.kernel.util.OrderByComparator<Layout>
			orderByComparator);

	/**
	 * Returns the layouts before and after the current layout in the ordered set of layouts that the user has permission to view where groupId = &#63; and privateLayout = &#63; and parentLayoutId = &#63; and hidden = &#63;.
	 *
	 * @param plid the primary key of the current layout
	 * @param groupId the group ID
	 * @param privateLayout the private layout
	 * @param parentLayoutId the parent layout ID
	 * @param hidden the hidden
	 * @param orderByComparator the comparator to order the set by (optionally <code>null</code>)
	 * @return the previous, current, and next layout
	 * @throws NoSuchLayoutException if a layout with the primary key could not be found
	 */
	public Layout[] filterFindByG_P_P_H_PrevAndNext(
			long plid, long groupId, boolean privateLayout, long parentLayoutId,
			boolean hidden,
			com.liferay.portal.kernel.util.OrderByComparator<Layout>
				orderByComparator)
		throws NoSuchLayoutException;

	/**
	 * Returns all the layouts that the user has permission to view where groupId = &#63; and privateLayout = &#63; and parentLayoutId = any &#63; and hidden = &#63;.
	 *
	 * @param groupId the group ID
	 * @param privateLayout the private layout
	 * @param parentLayoutIds the parent layout IDs
	 * @param hidden the hidden
	 * @return the matching layouts that the user has permission to view
	 */
	public java.util.List<Layout> filterFindByG_P_P_H(
		long groupId, boolean privateLayout, long[] parentLayoutIds,
		boolean hidden);

	/**
	 * Returns a range of all the layouts that the user has permission to view where groupId = &#63; and privateLayout = &#63; and parentLayoutId = any &#63; and hidden = &#63;.
	 *
	 * <p>
	 * Useful when paginating results. Returns a maximum of <code>end - start</code> instances. <code>start</code> and <code>end</code> are not primary keys, they are indexes in the result set. Thus, <code>0</code> refers to the first result in the set. Setting both <code>start</code> and <code>end</code> to <code>QueryUtil#ALL_POS</code> will return the full result set. If <code>orderByComparator</code> is specified, then the query will include the given ORDER BY logic. If <code>orderByComparator</code> is absent, then the query will include the default ORDER BY logic from <code>LayoutModelImpl</code>.
	 * </p>
	 *
	 * @param groupId the group ID
	 * @param privateLayout the private layout
	 * @param parentLayoutIds the parent layout IDs
	 * @param hidden the hidden
	 * @param start the lower bound of the range of layouts
	 * @param end the upper bound of the range of layouts (not inclusive)
	 * @return the range of matching layouts that the user has permission to view
	 */
	public java.util.List<Layout> filterFindByG_P_P_H(
		long groupId, boolean privateLayout, long[] parentLayoutIds,
		boolean hidden, int start, int end);

	/**
	 * Returns an ordered range of all the layouts that the user has permission to view where groupId = &#63; and privateLayout = &#63; and parentLayoutId = any &#63; and hidden = &#63;.
	 *
	 * <p>
	 * Useful when paginating results. Returns a maximum of <code>end - start</code> instances. <code>start</code> and <code>end</code> are not primary keys, they are indexes in the result set. Thus, <code>0</code> refers to the first result in the set. Setting both <code>start</code> and <code>end</code> to <code>QueryUtil#ALL_POS</code> will return the full result set. If <code>orderByComparator</code> is specified, then the query will include the given ORDER BY logic. If <code>orderByComparator</code> is absent, then the query will include the default ORDER BY logic from <code>LayoutModelImpl</code>.
	 * </p>
	 *
	 * @param groupId the group ID
	 * @param privateLayout the private layout
	 * @param parentLayoutIds the parent layout IDs
	 * @param hidden the hidden
	 * @param start the lower bound of the range of layouts
	 * @param end the upper bound of the range of layouts (not inclusive)
	 * @param orderByComparator the comparator to order the results by (optionally <code>null</code>)
	 * @return the ordered range of matching layouts that the user has permission to view
	 */
	public java.util.List<Layout> filterFindByG_P_P_H(
		long groupId, boolean privateLayout, long[] parentLayoutIds,
		boolean hidden, int start, int end,
		com.liferay.portal.kernel.util.OrderByComparator<Layout>
			orderByComparator);

	/**
	 * Returns all the layouts where groupId = &#63; and privateLayout = &#63; and parentLayoutId = any &#63; and hidden = &#63;.
	 *
	 * <p>
	 * Useful when paginating results. Returns a maximum of <code>end - start</code> instances. <code>start</code> and <code>end</code> are not primary keys, they are indexes in the result set. Thus, <code>0</code> refers to the first result in the set. Setting both <code>start</code> and <code>end</code> to <code>QueryUtil#ALL_POS</code> will return the full result set. If <code>orderByComparator</code> is specified, then the query will include the given ORDER BY logic. If <code>orderByComparator</code> is absent, then the query will include the default ORDER BY logic from <code>LayoutModelImpl</code>.
	 * </p>
	 *
	 * @param groupId the group ID
	 * @param privateLayout the private layout
	 * @param parentLayoutIds the parent layout IDs
	 * @param hidden the hidden
	 * @return the matching layouts
	 */
	public java.util.List<Layout> findByG_P_P_H(
		long groupId, boolean privateLayout, long[] parentLayoutIds,
		boolean hidden);

	/**
	 * Returns a range of all the layouts where groupId = &#63; and privateLayout = &#63; and parentLayoutId = any &#63; and hidden = &#63;.
	 *
	 * <p>
	 * Useful when paginating results. Returns a maximum of <code>end - start</code> instances. <code>start</code> and <code>end</code> are not primary keys, they are indexes in the result set. Thus, <code>0</code> refers to the first result in the set. Setting both <code>start</code> and <code>end</code> to <code>QueryUtil#ALL_POS</code> will return the full result set. If <code>orderByComparator</code> is specified, then the query will include the given ORDER BY logic. If <code>orderByComparator</code> is absent, then the query will include the default ORDER BY logic from <code>LayoutModelImpl</code>.
	 * </p>
	 *
	 * @param groupId the group ID
	 * @param privateLayout the private layout
	 * @param parentLayoutIds the parent layout IDs
	 * @param hidden the hidden
	 * @param start the lower bound of the range of layouts
	 * @param end the upper bound of the range of layouts (not inclusive)
	 * @return the range of matching layouts
	 */
	public java.util.List<Layout> findByG_P_P_H(
		long groupId, boolean privateLayout, long[] parentLayoutIds,
		boolean hidden, int start, int end);

	/**
	 * Returns an ordered range of all the layouts where groupId = &#63; and privateLayout = &#63; and parentLayoutId = any &#63; and hidden = &#63;.
	 *
	 * <p>
	 * Useful when paginating results. Returns a maximum of <code>end - start</code> instances. <code>start</code> and <code>end</code> are not primary keys, they are indexes in the result set. Thus, <code>0</code> refers to the first result in the set. Setting both <code>start</code> and <code>end</code> to <code>QueryUtil#ALL_POS</code> will return the full result set. If <code>orderByComparator</code> is specified, then the query will include the given ORDER BY logic. If <code>orderByComparator</code> is absent, then the query will include the default ORDER BY logic from <code>LayoutModelImpl</code>.
	 * </p>
	 *
	 * @param groupId the group ID
	 * @param privateLayout the private layout
	 * @param parentLayoutIds the parent layout IDs
	 * @param hidden the hidden
	 * @param start the lower bound of the range of layouts
	 * @param end the upper bound of the range of layouts (not inclusive)
	 * @param orderByComparator the comparator to order the results by (optionally <code>null</code>)
	 * @return the ordered range of matching layouts
	 */
	public java.util.List<Layout> findByG_P_P_H(
		long groupId, boolean privateLayout, long[] parentLayoutIds,
		boolean hidden, int start, int end,
		com.liferay.portal.kernel.util.OrderByComparator<Layout>
			orderByComparator);

	/**
	 * Returns an ordered range of all the layouts where groupId = &#63; and privateLayout = &#63; and parentLayoutId = &#63; and hidden = &#63;, optionally using the finder cache.
	 *
	 * <p>
	 * Useful when paginating results. Returns a maximum of <code>end - start</code> instances. <code>start</code> and <code>end</code> are not primary keys, they are indexes in the result set. Thus, <code>0</code> refers to the first result in the set. Setting both <code>start</code> and <code>end</code> to <code>QueryUtil#ALL_POS</code> will return the full result set. If <code>orderByComparator</code> is specified, then the query will include the given ORDER BY logic. If <code>orderByComparator</code> is absent, then the query will include the default ORDER BY logic from <code>LayoutModelImpl</code>.
	 * </p>
	 *
	 * @param groupId the group ID
	 * @param privateLayout the private layout
	 * @param parentLayoutIds the parent layout IDs
	 * @param hidden the hidden
	 * @param start the lower bound of the range of layouts
	 * @param end the upper bound of the range of layouts (not inclusive)
	 * @param orderByComparator the comparator to order the results by (optionally <code>null</code>)
	 * @param useFinderCache whether to use the finder cache
	 * @return the ordered range of matching layouts
	 */
	public java.util.List<Layout> findByG_P_P_H(
		long groupId, boolean privateLayout, long[] parentLayoutIds,
		boolean hidden, int start, int end,
		com.liferay.portal.kernel.util.OrderByComparator<Layout>
			orderByComparator,
		boolean useFinderCache);

	/**
	 * Removes all the layouts where groupId = &#63; and privateLayout = &#63; and parentLayoutId = &#63; and hidden = &#63; from the database.
	 *
	 * @param groupId the group ID
	 * @param privateLayout the private layout
	 * @param parentLayoutId the parent layout ID
	 * @param hidden the hidden
	 */
	public void removeByG_P_P_H(
		long groupId, boolean privateLayout, long parentLayoutId,
		boolean hidden);

	/**
	 * Returns the number of layouts where groupId = &#63; and privateLayout = &#63; and parentLayoutId = &#63; and hidden = &#63;.
	 *
	 * @param groupId the group ID
	 * @param privateLayout the private layout
	 * @param parentLayoutId the parent layout ID
	 * @param hidden the hidden
	 * @return the number of matching layouts
	 */
	public int countByG_P_P_H(
		long groupId, boolean privateLayout, long parentLayoutId,
		boolean hidden);

	/**
	 * Returns the number of layouts where groupId = &#63; and privateLayout = &#63; and parentLayoutId = any &#63; and hidden = &#63;.
	 *
	 * @param groupId the group ID
	 * @param privateLayout the private layout
	 * @param parentLayoutIds the parent layout IDs
	 * @param hidden the hidden
	 * @return the number of matching layouts
	 */
	public int countByG_P_P_H(
		long groupId, boolean privateLayout, long[] parentLayoutIds,
		boolean hidden);

	/**
	 * Returns the number of layouts that the user has permission to view where groupId = &#63; and privateLayout = &#63; and parentLayoutId = &#63; and hidden = &#63;.
	 *
	 * @param groupId the group ID
	 * @param privateLayout the private layout
	 * @param parentLayoutId the parent layout ID
	 * @param hidden the hidden
	 * @return the number of matching layouts that the user has permission to view
	 */
	public int filterCountByG_P_P_H(
		long groupId, boolean privateLayout, long parentLayoutId,
		boolean hidden);

	/**
	 * Returns the number of layouts that the user has permission to view where groupId = &#63; and privateLayout = &#63; and parentLayoutId = any &#63; and hidden = &#63;.
	 *
	 * @param groupId the group ID
	 * @param privateLayout the private layout
	 * @param parentLayoutIds the parent layout IDs
	 * @param hidden the hidden
	 * @return the number of matching layouts that the user has permission to view
	 */
	public int filterCountByG_P_P_H(
		long groupId, boolean privateLayout, long[] parentLayoutIds,
		boolean hidden);

	/**
	 * Returns all the layouts where groupId = &#63; and privateLayout = &#63; and parentLayoutId = &#63; and system = &#63;.
	 *
	 * @param groupId the group ID
	 * @param privateLayout the private layout
	 * @param parentLayoutId the parent layout ID
	 * @param system the system
	 * @return the matching layouts
	 */
	public java.util.List<Layout> findByG_P_P_S(
		long groupId, boolean privateLayout, long parentLayoutId,
		boolean system);

	/**
	 * Returns a range of all the layouts where groupId = &#63; and privateLayout = &#63; and parentLayoutId = &#63; and system = &#63;.
	 *
	 * <p>
	 * Useful when paginating results. Returns a maximum of <code>end - start</code> instances. <code>start</code> and <code>end</code> are not primary keys, they are indexes in the result set. Thus, <code>0</code> refers to the first result in the set. Setting both <code>start</code> and <code>end</code> to <code>QueryUtil#ALL_POS</code> will return the full result set. If <code>orderByComparator</code> is specified, then the query will include the given ORDER BY logic. If <code>orderByComparator</code> is absent, then the query will include the default ORDER BY logic from <code>LayoutModelImpl</code>.
	 * </p>
	 *
	 * @param groupId the group ID
	 * @param privateLayout the private layout
	 * @param parentLayoutId the parent layout ID
	 * @param system the system
	 * @param start the lower bound of the range of layouts
	 * @param end the upper bound of the range of layouts (not inclusive)
	 * @return the range of matching layouts
	 */
	public java.util.List<Layout> findByG_P_P_S(
		long groupId, boolean privateLayout, long parentLayoutId,
		boolean system, int start, int end);

	/**
	 * Returns an ordered range of all the layouts where groupId = &#63; and privateLayout = &#63; and parentLayoutId = &#63; and system = &#63;.
	 *
	 * <p>
	 * Useful when paginating results. Returns a maximum of <code>end - start</code> instances. <code>start</code> and <code>end</code> are not primary keys, they are indexes in the result set. Thus, <code>0</code> refers to the first result in the set. Setting both <code>start</code> and <code>end</code> to <code>QueryUtil#ALL_POS</code> will return the full result set. If <code>orderByComparator</code> is specified, then the query will include the given ORDER BY logic. If <code>orderByComparator</code> is absent, then the query will include the default ORDER BY logic from <code>LayoutModelImpl</code>.
	 * </p>
	 *
	 * @param groupId the group ID
	 * @param privateLayout the private layout
	 * @param parentLayoutId the parent layout ID
	 * @param system the system
	 * @param start the lower bound of the range of layouts
	 * @param end the upper bound of the range of layouts (not inclusive)
	 * @param orderByComparator the comparator to order the results by (optionally <code>null</code>)
	 * @return the ordered range of matching layouts
	 */
	public java.util.List<Layout> findByG_P_P_S(
		long groupId, boolean privateLayout, long parentLayoutId,
		boolean system, int start, int end,
		com.liferay.portal.kernel.util.OrderByComparator<Layout>
			orderByComparator);

	/**
	 * Returns an ordered range of all the layouts where groupId = &#63; and privateLayout = &#63; and parentLayoutId = &#63; and system = &#63;.
	 *
	 * <p>
	 * Useful when paginating results. Returns a maximum of <code>end - start</code> instances. <code>start</code> and <code>end</code> are not primary keys, they are indexes in the result set. Thus, <code>0</code> refers to the first result in the set. Setting both <code>start</code> and <code>end</code> to <code>QueryUtil#ALL_POS</code> will return the full result set. If <code>orderByComparator</code> is specified, then the query will include the given ORDER BY logic. If <code>orderByComparator</code> is absent, then the query will include the default ORDER BY logic from <code>LayoutModelImpl</code>.
	 * </p>
	 *
	 * @param groupId the group ID
	 * @param privateLayout the private layout
	 * @param parentLayoutId the parent layout ID
	 * @param system the system
	 * @param start the lower bound of the range of layouts
	 * @param end the upper bound of the range of layouts (not inclusive)
	 * @param orderByComparator the comparator to order the results by (optionally <code>null</code>)
	 * @param useFinderCache whether to use the finder cache
	 * @return the ordered range of matching layouts
	 */
	public java.util.List<Layout> findByG_P_P_S(
		long groupId, boolean privateLayout, long parentLayoutId,
		boolean system, int start, int end,
		com.liferay.portal.kernel.util.OrderByComparator<Layout>
			orderByComparator,
		boolean useFinderCache);

	/**
	 * Returns the first layout in the ordered set where groupId = &#63; and privateLayout = &#63; and parentLayoutId = &#63; and system = &#63;.
	 *
	 * @param groupId the group ID
	 * @param privateLayout the private layout
	 * @param parentLayoutId the parent layout ID
	 * @param system the system
	 * @param orderByComparator the comparator to order the set by (optionally <code>null</code>)
	 * @return the first matching layout
	 * @throws NoSuchLayoutException if a matching layout could not be found
	 */
	public Layout findByG_P_P_S_First(
			long groupId, boolean privateLayout, long parentLayoutId,
			boolean system,
			com.liferay.portal.kernel.util.OrderByComparator<Layout>
				orderByComparator)
		throws NoSuchLayoutException;

	/**
	 * Returns the first layout in the ordered set where groupId = &#63; and privateLayout = &#63; and parentLayoutId = &#63; and system = &#63;.
	 *
	 * @param groupId the group ID
	 * @param privateLayout the private layout
	 * @param parentLayoutId the parent layout ID
	 * @param system the system
	 * @param orderByComparator the comparator to order the set by (optionally <code>null</code>)
	 * @return the first matching layout, or <code>null</code> if a matching layout could not be found
	 */
	public Layout fetchByG_P_P_S_First(
		long groupId, boolean privateLayout, long parentLayoutId,
		boolean system,
		com.liferay.portal.kernel.util.OrderByComparator<Layout>
			orderByComparator);

	/**
	 * Returns the last layout in the ordered set where groupId = &#63; and privateLayout = &#63; and parentLayoutId = &#63; and system = &#63;.
	 *
	 * @param groupId the group ID
	 * @param privateLayout the private layout
	 * @param parentLayoutId the parent layout ID
	 * @param system the system
	 * @param orderByComparator the comparator to order the set by (optionally <code>null</code>)
	 * @return the last matching layout
	 * @throws NoSuchLayoutException if a matching layout could not be found
	 */
	public Layout findByG_P_P_S_Last(
			long groupId, boolean privateLayout, long parentLayoutId,
			boolean system,
			com.liferay.portal.kernel.util.OrderByComparator<Layout>
				orderByComparator)
		throws NoSuchLayoutException;

	/**
	 * Returns the last layout in the ordered set where groupId = &#63; and privateLayout = &#63; and parentLayoutId = &#63; and system = &#63;.
	 *
	 * @param groupId the group ID
	 * @param privateLayout the private layout
	 * @param parentLayoutId the parent layout ID
	 * @param system the system
	 * @param orderByComparator the comparator to order the set by (optionally <code>null</code>)
	 * @return the last matching layout, or <code>null</code> if a matching layout could not be found
	 */
	public Layout fetchByG_P_P_S_Last(
		long groupId, boolean privateLayout, long parentLayoutId,
		boolean system,
		com.liferay.portal.kernel.util.OrderByComparator<Layout>
			orderByComparator);

	/**
	 * Returns the layouts before and after the current layout in the ordered set where groupId = &#63; and privateLayout = &#63; and parentLayoutId = &#63; and system = &#63;.
	 *
	 * @param plid the primary key of the current layout
	 * @param groupId the group ID
	 * @param privateLayout the private layout
	 * @param parentLayoutId the parent layout ID
	 * @param system the system
	 * @param orderByComparator the comparator to order the set by (optionally <code>null</code>)
	 * @return the previous, current, and next layout
	 * @throws NoSuchLayoutException if a layout with the primary key could not be found
	 */
	public Layout[] findByG_P_P_S_PrevAndNext(
			long plid, long groupId, boolean privateLayout, long parentLayoutId,
			boolean system,
			com.liferay.portal.kernel.util.OrderByComparator<Layout>
				orderByComparator)
		throws NoSuchLayoutException;

	/**
	 * Returns all the layouts that the user has permission to view where groupId = &#63; and privateLayout = &#63; and parentLayoutId = &#63; and system = &#63;.
	 *
	 * @param groupId the group ID
	 * @param privateLayout the private layout
	 * @param parentLayoutId the parent layout ID
	 * @param system the system
	 * @return the matching layouts that the user has permission to view
	 */
	public java.util.List<Layout> filterFindByG_P_P_S(
		long groupId, boolean privateLayout, long parentLayoutId,
		boolean system);

	/**
	 * Returns a range of all the layouts that the user has permission to view where groupId = &#63; and privateLayout = &#63; and parentLayoutId = &#63; and system = &#63;.
	 *
	 * <p>
	 * Useful when paginating results. Returns a maximum of <code>end - start</code> instances. <code>start</code> and <code>end</code> are not primary keys, they are indexes in the result set. Thus, <code>0</code> refers to the first result in the set. Setting both <code>start</code> and <code>end</code> to <code>QueryUtil#ALL_POS</code> will return the full result set. If <code>orderByComparator</code> is specified, then the query will include the given ORDER BY logic. If <code>orderByComparator</code> is absent, then the query will include the default ORDER BY logic from <code>LayoutModelImpl</code>.
	 * </p>
	 *
	 * @param groupId the group ID
	 * @param privateLayout the private layout
	 * @param parentLayoutId the parent layout ID
	 * @param system the system
	 * @param start the lower bound of the range of layouts
	 * @param end the upper bound of the range of layouts (not inclusive)
	 * @return the range of matching layouts that the user has permission to view
	 */
	public java.util.List<Layout> filterFindByG_P_P_S(
		long groupId, boolean privateLayout, long parentLayoutId,
		boolean system, int start, int end);

	/**
	 * Returns an ordered range of all the layouts that the user has permissions to view where groupId = &#63; and privateLayout = &#63; and parentLayoutId = &#63; and system = &#63;.
	 *
	 * <p>
	 * Useful when paginating results. Returns a maximum of <code>end - start</code> instances. <code>start</code> and <code>end</code> are not primary keys, they are indexes in the result set. Thus, <code>0</code> refers to the first result in the set. Setting both <code>start</code> and <code>end</code> to <code>QueryUtil#ALL_POS</code> will return the full result set. If <code>orderByComparator</code> is specified, then the query will include the given ORDER BY logic. If <code>orderByComparator</code> is absent, then the query will include the default ORDER BY logic from <code>LayoutModelImpl</code>.
	 * </p>
	 *
	 * @param groupId the group ID
	 * @param privateLayout the private layout
	 * @param parentLayoutId the parent layout ID
	 * @param system the system
	 * @param start the lower bound of the range of layouts
	 * @param end the upper bound of the range of layouts (not inclusive)
	 * @param orderByComparator the comparator to order the results by (optionally <code>null</code>)
	 * @return the ordered range of matching layouts that the user has permission to view
	 */
	public java.util.List<Layout> filterFindByG_P_P_S(
		long groupId, boolean privateLayout, long parentLayoutId,
		boolean system, int start, int end,
		com.liferay.portal.kernel.util.OrderByComparator<Layout>
			orderByComparator);

	/**
	 * Returns the layouts before and after the current layout in the ordered set of layouts that the user has permission to view where groupId = &#63; and privateLayout = &#63; and parentLayoutId = &#63; and system = &#63;.
	 *
	 * @param plid the primary key of the current layout
	 * @param groupId the group ID
	 * @param privateLayout the private layout
	 * @param parentLayoutId the parent layout ID
	 * @param system the system
	 * @param orderByComparator the comparator to order the set by (optionally <code>null</code>)
	 * @return the previous, current, and next layout
	 * @throws NoSuchLayoutException if a layout with the primary key could not be found
	 */
	public Layout[] filterFindByG_P_P_S_PrevAndNext(
			long plid, long groupId, boolean privateLayout, long parentLayoutId,
			boolean system,
			com.liferay.portal.kernel.util.OrderByComparator<Layout>
				orderByComparator)
		throws NoSuchLayoutException;

	/**
	 * Returns all the layouts that the user has permission to view where groupId = &#63; and privateLayout = &#63; and parentLayoutId = any &#63; and system = &#63;.
	 *
	 * @param groupId the group ID
	 * @param privateLayout the private layout
	 * @param parentLayoutIds the parent layout IDs
	 * @param system the system
	 * @return the matching layouts that the user has permission to view
	 */
	public java.util.List<Layout> filterFindByG_P_P_S(
		long groupId, boolean privateLayout, long[] parentLayoutIds,
		boolean system);

	/**
	 * Returns a range of all the layouts that the user has permission to view where groupId = &#63; and privateLayout = &#63; and parentLayoutId = any &#63; and system = &#63;.
	 *
	 * <p>
	 * Useful when paginating results. Returns a maximum of <code>end - start</code> instances. <code>start</code> and <code>end</code> are not primary keys, they are indexes in the result set. Thus, <code>0</code> refers to the first result in the set. Setting both <code>start</code> and <code>end</code> to <code>QueryUtil#ALL_POS</code> will return the full result set. If <code>orderByComparator</code> is specified, then the query will include the given ORDER BY logic. If <code>orderByComparator</code> is absent, then the query will include the default ORDER BY logic from <code>LayoutModelImpl</code>.
	 * </p>
	 *
	 * @param groupId the group ID
	 * @param privateLayout the private layout
	 * @param parentLayoutIds the parent layout IDs
	 * @param system the system
	 * @param start the lower bound of the range of layouts
	 * @param end the upper bound of the range of layouts (not inclusive)
	 * @return the range of matching layouts that the user has permission to view
	 */
	public java.util.List<Layout> filterFindByG_P_P_S(
		long groupId, boolean privateLayout, long[] parentLayoutIds,
		boolean system, int start, int end);

	/**
	 * Returns an ordered range of all the layouts that the user has permission to view where groupId = &#63; and privateLayout = &#63; and parentLayoutId = any &#63; and system = &#63;.
	 *
	 * <p>
	 * Useful when paginating results. Returns a maximum of <code>end - start</code> instances. <code>start</code> and <code>end</code> are not primary keys, they are indexes in the result set. Thus, <code>0</code> refers to the first result in the set. Setting both <code>start</code> and <code>end</code> to <code>QueryUtil#ALL_POS</code> will return the full result set. If <code>orderByComparator</code> is specified, then the query will include the given ORDER BY logic. If <code>orderByComparator</code> is absent, then the query will include the default ORDER BY logic from <code>LayoutModelImpl</code>.
	 * </p>
	 *
	 * @param groupId the group ID
	 * @param privateLayout the private layout
	 * @param parentLayoutIds the parent layout IDs
	 * @param system the system
	 * @param start the lower bound of the range of layouts
	 * @param end the upper bound of the range of layouts (not inclusive)
	 * @param orderByComparator the comparator to order the results by (optionally <code>null</code>)
	 * @return the ordered range of matching layouts that the user has permission to view
	 */
	public java.util.List<Layout> filterFindByG_P_P_S(
		long groupId, boolean privateLayout, long[] parentLayoutIds,
		boolean system, int start, int end,
		com.liferay.portal.kernel.util.OrderByComparator<Layout>
			orderByComparator);

	/**
	 * Returns all the layouts where groupId = &#63; and privateLayout = &#63; and parentLayoutId = any &#63; and system = &#63;.
	 *
	 * <p>
	 * Useful when paginating results. Returns a maximum of <code>end - start</code> instances. <code>start</code> and <code>end</code> are not primary keys, they are indexes in the result set. Thus, <code>0</code> refers to the first result in the set. Setting both <code>start</code> and <code>end</code> to <code>QueryUtil#ALL_POS</code> will return the full result set. If <code>orderByComparator</code> is specified, then the query will include the given ORDER BY logic. If <code>orderByComparator</code> is absent, then the query will include the default ORDER BY logic from <code>LayoutModelImpl</code>.
	 * </p>
	 *
	 * @param groupId the group ID
	 * @param privateLayout the private layout
	 * @param parentLayoutIds the parent layout IDs
	 * @param system the system
	 * @return the matching layouts
	 */
	public java.util.List<Layout> findByG_P_P_S(
		long groupId, boolean privateLayout, long[] parentLayoutIds,
		boolean system);

	/**
	 * Returns a range of all the layouts where groupId = &#63; and privateLayout = &#63; and parentLayoutId = any &#63; and system = &#63;.
	 *
	 * <p>
	 * Useful when paginating results. Returns a maximum of <code>end - start</code> instances. <code>start</code> and <code>end</code> are not primary keys, they are indexes in the result set. Thus, <code>0</code> refers to the first result in the set. Setting both <code>start</code> and <code>end</code> to <code>QueryUtil#ALL_POS</code> will return the full result set. If <code>orderByComparator</code> is specified, then the query will include the given ORDER BY logic. If <code>orderByComparator</code> is absent, then the query will include the default ORDER BY logic from <code>LayoutModelImpl</code>.
	 * </p>
	 *
	 * @param groupId the group ID
	 * @param privateLayout the private layout
	 * @param parentLayoutIds the parent layout IDs
	 * @param system the system
	 * @param start the lower bound of the range of layouts
	 * @param end the upper bound of the range of layouts (not inclusive)
	 * @return the range of matching layouts
	 */
	public java.util.List<Layout> findByG_P_P_S(
		long groupId, boolean privateLayout, long[] parentLayoutIds,
		boolean system, int start, int end);

	/**
	 * Returns an ordered range of all the layouts where groupId = &#63; and privateLayout = &#63; and parentLayoutId = any &#63; and system = &#63;.
	 *
	 * <p>
	 * Useful when paginating results. Returns a maximum of <code>end - start</code> instances. <code>start</code> and <code>end</code> are not primary keys, they are indexes in the result set. Thus, <code>0</code> refers to the first result in the set. Setting both <code>start</code> and <code>end</code> to <code>QueryUtil#ALL_POS</code> will return the full result set. If <code>orderByComparator</code> is specified, then the query will include the given ORDER BY logic. If <code>orderByComparator</code> is absent, then the query will include the default ORDER BY logic from <code>LayoutModelImpl</code>.
	 * </p>
	 *
	 * @param groupId the group ID
	 * @param privateLayout the private layout
	 * @param parentLayoutIds the parent layout IDs
	 * @param system the system
	 * @param start the lower bound of the range of layouts
	 * @param end the upper bound of the range of layouts (not inclusive)
	 * @param orderByComparator the comparator to order the results by (optionally <code>null</code>)
	 * @return the ordered range of matching layouts
	 */
	public java.util.List<Layout> findByG_P_P_S(
		long groupId, boolean privateLayout, long[] parentLayoutIds,
		boolean system, int start, int end,
		com.liferay.portal.kernel.util.OrderByComparator<Layout>
			orderByComparator);

	/**
	 * Returns an ordered range of all the layouts where groupId = &#63; and privateLayout = &#63; and parentLayoutId = &#63; and system = &#63;, optionally using the finder cache.
	 *
	 * <p>
	 * Useful when paginating results. Returns a maximum of <code>end - start</code> instances. <code>start</code> and <code>end</code> are not primary keys, they are indexes in the result set. Thus, <code>0</code> refers to the first result in the set. Setting both <code>start</code> and <code>end</code> to <code>QueryUtil#ALL_POS</code> will return the full result set. If <code>orderByComparator</code> is specified, then the query will include the given ORDER BY logic. If <code>orderByComparator</code> is absent, then the query will include the default ORDER BY logic from <code>LayoutModelImpl</code>.
	 * </p>
	 *
	 * @param groupId the group ID
	 * @param privateLayout the private layout
	 * @param parentLayoutIds the parent layout IDs
	 * @param system the system
	 * @param start the lower bound of the range of layouts
	 * @param end the upper bound of the range of layouts (not inclusive)
	 * @param orderByComparator the comparator to order the results by (optionally <code>null</code>)
	 * @param useFinderCache whether to use the finder cache
	 * @return the ordered range of matching layouts
	 */
	public java.util.List<Layout> findByG_P_P_S(
		long groupId, boolean privateLayout, long[] parentLayoutIds,
		boolean system, int start, int end,
		com.liferay.portal.kernel.util.OrderByComparator<Layout>
			orderByComparator,
		boolean useFinderCache);

	/**
	 * Removes all the layouts where groupId = &#63; and privateLayout = &#63; and parentLayoutId = &#63; and system = &#63; from the database.
	 *
	 * @param groupId the group ID
	 * @param privateLayout the private layout
	 * @param parentLayoutId the parent layout ID
	 * @param system the system
	 */
	public void removeByG_P_P_S(
		long groupId, boolean privateLayout, long parentLayoutId,
		boolean system);

	/**
	 * Returns the number of layouts where groupId = &#63; and privateLayout = &#63; and parentLayoutId = &#63; and system = &#63;.
	 *
	 * @param groupId the group ID
	 * @param privateLayout the private layout
	 * @param parentLayoutId the parent layout ID
	 * @param system the system
	 * @return the number of matching layouts
	 */
	public int countByG_P_P_S(
		long groupId, boolean privateLayout, long parentLayoutId,
		boolean system);

	/**
	 * Returns the number of layouts where groupId = &#63; and privateLayout = &#63; and parentLayoutId = any &#63; and system = &#63;.
	 *
	 * @param groupId the group ID
	 * @param privateLayout the private layout
	 * @param parentLayoutIds the parent layout IDs
	 * @param system the system
	 * @return the number of matching layouts
	 */
	public int countByG_P_P_S(
		long groupId, boolean privateLayout, long[] parentLayoutIds,
		boolean system);

	/**
	 * Returns the number of layouts that the user has permission to view where groupId = &#63; and privateLayout = &#63; and parentLayoutId = &#63; and system = &#63;.
	 *
	 * @param groupId the group ID
	 * @param privateLayout the private layout
	 * @param parentLayoutId the parent layout ID
	 * @param system the system
	 * @return the number of matching layouts that the user has permission to view
	 */
	public int filterCountByG_P_P_S(
		long groupId, boolean privateLayout, long parentLayoutId,
		boolean system);

	/**
	 * Returns the number of layouts that the user has permission to view where groupId = &#63; and privateLayout = &#63; and parentLayoutId = any &#63; and system = &#63;.
	 *
	 * @param groupId the group ID
	 * @param privateLayout the private layout
	 * @param parentLayoutIds the parent layout IDs
	 * @param system the system
	 * @return the number of matching layouts that the user has permission to view
	 */
	public int filterCountByG_P_P_S(
		long groupId, boolean privateLayout, long[] parentLayoutIds,
		boolean system);

	/**
	 * Returns all the layouts where groupId = &#63; and privateLayout = &#63; and parentLayoutId = &#63; and priority &le; &#63;.
	 *
	 * @param groupId the group ID
	 * @param privateLayout the private layout
	 * @param parentLayoutId the parent layout ID
	 * @param priority the priority
	 * @return the matching layouts
	 */
	public java.util.List<Layout> findByG_P_P_LteP(
		long groupId, boolean privateLayout, long parentLayoutId, int priority);

	/**
	 * Returns a range of all the layouts where groupId = &#63; and privateLayout = &#63; and parentLayoutId = &#63; and priority &le; &#63;.
	 *
	 * <p>
	 * Useful when paginating results. Returns a maximum of <code>end - start</code> instances. <code>start</code> and <code>end</code> are not primary keys, they are indexes in the result set. Thus, <code>0</code> refers to the first result in the set. Setting both <code>start</code> and <code>end</code> to <code>QueryUtil#ALL_POS</code> will return the full result set. If <code>orderByComparator</code> is specified, then the query will include the given ORDER BY logic. If <code>orderByComparator</code> is absent, then the query will include the default ORDER BY logic from <code>LayoutModelImpl</code>.
	 * </p>
	 *
	 * @param groupId the group ID
	 * @param privateLayout the private layout
	 * @param parentLayoutId the parent layout ID
	 * @param priority the priority
	 * @param start the lower bound of the range of layouts
	 * @param end the upper bound of the range of layouts (not inclusive)
	 * @return the range of matching layouts
	 */
	public java.util.List<Layout> findByG_P_P_LteP(
		long groupId, boolean privateLayout, long parentLayoutId, int priority,
		int start, int end);

	/**
	 * Returns an ordered range of all the layouts where groupId = &#63; and privateLayout = &#63; and parentLayoutId = &#63; and priority &le; &#63;.
	 *
	 * <p>
	 * Useful when paginating results. Returns a maximum of <code>end - start</code> instances. <code>start</code> and <code>end</code> are not primary keys, they are indexes in the result set. Thus, <code>0</code> refers to the first result in the set. Setting both <code>start</code> and <code>end</code> to <code>QueryUtil#ALL_POS</code> will return the full result set. If <code>orderByComparator</code> is specified, then the query will include the given ORDER BY logic. If <code>orderByComparator</code> is absent, then the query will include the default ORDER BY logic from <code>LayoutModelImpl</code>.
	 * </p>
	 *
	 * @param groupId the group ID
	 * @param privateLayout the private layout
	 * @param parentLayoutId the parent layout ID
	 * @param priority the priority
	 * @param start the lower bound of the range of layouts
	 * @param end the upper bound of the range of layouts (not inclusive)
	 * @param orderByComparator the comparator to order the results by (optionally <code>null</code>)
	 * @return the ordered range of matching layouts
	 */
	public java.util.List<Layout> findByG_P_P_LteP(
		long groupId, boolean privateLayout, long parentLayoutId, int priority,
		int start, int end,
		com.liferay.portal.kernel.util.OrderByComparator<Layout>
			orderByComparator);

	/**
	 * Returns an ordered range of all the layouts where groupId = &#63; and privateLayout = &#63; and parentLayoutId = &#63; and priority &le; &#63;.
	 *
	 * <p>
	 * Useful when paginating results. Returns a maximum of <code>end - start</code> instances. <code>start</code> and <code>end</code> are not primary keys, they are indexes in the result set. Thus, <code>0</code> refers to the first result in the set. Setting both <code>start</code> and <code>end</code> to <code>QueryUtil#ALL_POS</code> will return the full result set. If <code>orderByComparator</code> is specified, then the query will include the given ORDER BY logic. If <code>orderByComparator</code> is absent, then the query will include the default ORDER BY logic from <code>LayoutModelImpl</code>.
	 * </p>
	 *
	 * @param groupId the group ID
	 * @param privateLayout the private layout
	 * @param parentLayoutId the parent layout ID
	 * @param priority the priority
	 * @param start the lower bound of the range of layouts
	 * @param end the upper bound of the range of layouts (not inclusive)
	 * @param orderByComparator the comparator to order the results by (optionally <code>null</code>)
	 * @param useFinderCache whether to use the finder cache
	 * @return the ordered range of matching layouts
	 */
	public java.util.List<Layout> findByG_P_P_LteP(
		long groupId, boolean privateLayout, long parentLayoutId, int priority,
		int start, int end,
		com.liferay.portal.kernel.util.OrderByComparator<Layout>
			orderByComparator,
		boolean useFinderCache);

	/**
	 * Returns the first layout in the ordered set where groupId = &#63; and privateLayout = &#63; and parentLayoutId = &#63; and priority &le; &#63;.
	 *
	 * @param groupId the group ID
	 * @param privateLayout the private layout
	 * @param parentLayoutId the parent layout ID
	 * @param priority the priority
	 * @param orderByComparator the comparator to order the set by (optionally <code>null</code>)
	 * @return the first matching layout
	 * @throws NoSuchLayoutException if a matching layout could not be found
	 */
	public Layout findByG_P_P_LteP_First(
			long groupId, boolean privateLayout, long parentLayoutId,
			int priority,
			com.liferay.portal.kernel.util.OrderByComparator<Layout>
				orderByComparator)
		throws NoSuchLayoutException;

	/**
	 * Returns the first layout in the ordered set where groupId = &#63; and privateLayout = &#63; and parentLayoutId = &#63; and priority &le; &#63;.
	 *
	 * @param groupId the group ID
	 * @param privateLayout the private layout
	 * @param parentLayoutId the parent layout ID
	 * @param priority the priority
	 * @param orderByComparator the comparator to order the set by (optionally <code>null</code>)
	 * @return the first matching layout, or <code>null</code> if a matching layout could not be found
	 */
	public Layout fetchByG_P_P_LteP_First(
		long groupId, boolean privateLayout, long parentLayoutId, int priority,
		com.liferay.portal.kernel.util.OrderByComparator<Layout>
			orderByComparator);

	/**
	 * Returns the last layout in the ordered set where groupId = &#63; and privateLayout = &#63; and parentLayoutId = &#63; and priority &le; &#63;.
	 *
	 * @param groupId the group ID
	 * @param privateLayout the private layout
	 * @param parentLayoutId the parent layout ID
	 * @param priority the priority
	 * @param orderByComparator the comparator to order the set by (optionally <code>null</code>)
	 * @return the last matching layout
	 * @throws NoSuchLayoutException if a matching layout could not be found
	 */
	public Layout findByG_P_P_LteP_Last(
			long groupId, boolean privateLayout, long parentLayoutId,
			int priority,
			com.liferay.portal.kernel.util.OrderByComparator<Layout>
				orderByComparator)
		throws NoSuchLayoutException;

	/**
	 * Returns the last layout in the ordered set where groupId = &#63; and privateLayout = &#63; and parentLayoutId = &#63; and priority &le; &#63;.
	 *
	 * @param groupId the group ID
	 * @param privateLayout the private layout
	 * @param parentLayoutId the parent layout ID
	 * @param priority the priority
	 * @param orderByComparator the comparator to order the set by (optionally <code>null</code>)
	 * @return the last matching layout, or <code>null</code> if a matching layout could not be found
	 */
	public Layout fetchByG_P_P_LteP_Last(
		long groupId, boolean privateLayout, long parentLayoutId, int priority,
		com.liferay.portal.kernel.util.OrderByComparator<Layout>
			orderByComparator);

	/**
	 * Returns the layouts before and after the current layout in the ordered set where groupId = &#63; and privateLayout = &#63; and parentLayoutId = &#63; and priority &le; &#63;.
	 *
	 * @param plid the primary key of the current layout
	 * @param groupId the group ID
	 * @param privateLayout the private layout
	 * @param parentLayoutId the parent layout ID
	 * @param priority the priority
	 * @param orderByComparator the comparator to order the set by (optionally <code>null</code>)
	 * @return the previous, current, and next layout
	 * @throws NoSuchLayoutException if a layout with the primary key could not be found
	 */
	public Layout[] findByG_P_P_LteP_PrevAndNext(
			long plid, long groupId, boolean privateLayout, long parentLayoutId,
			int priority,
			com.liferay.portal.kernel.util.OrderByComparator<Layout>
				orderByComparator)
		throws NoSuchLayoutException;

	/**
	 * Returns all the layouts that the user has permission to view where groupId = &#63; and privateLayout = &#63; and parentLayoutId = &#63; and priority &le; &#63;.
	 *
	 * @param groupId the group ID
	 * @param privateLayout the private layout
	 * @param parentLayoutId the parent layout ID
	 * @param priority the priority
	 * @return the matching layouts that the user has permission to view
	 */
	public java.util.List<Layout> filterFindByG_P_P_LteP(
		long groupId, boolean privateLayout, long parentLayoutId, int priority);

	/**
	 * Returns a range of all the layouts that the user has permission to view where groupId = &#63; and privateLayout = &#63; and parentLayoutId = &#63; and priority &le; &#63;.
	 *
	 * <p>
	 * Useful when paginating results. Returns a maximum of <code>end - start</code> instances. <code>start</code> and <code>end</code> are not primary keys, they are indexes in the result set. Thus, <code>0</code> refers to the first result in the set. Setting both <code>start</code> and <code>end</code> to <code>QueryUtil#ALL_POS</code> will return the full result set. If <code>orderByComparator</code> is specified, then the query will include the given ORDER BY logic. If <code>orderByComparator</code> is absent, then the query will include the default ORDER BY logic from <code>LayoutModelImpl</code>.
	 * </p>
	 *
	 * @param groupId the group ID
	 * @param privateLayout the private layout
	 * @param parentLayoutId the parent layout ID
	 * @param priority the priority
	 * @param start the lower bound of the range of layouts
	 * @param end the upper bound of the range of layouts (not inclusive)
	 * @return the range of matching layouts that the user has permission to view
	 */
	public java.util.List<Layout> filterFindByG_P_P_LteP(
		long groupId, boolean privateLayout, long parentLayoutId, int priority,
		int start, int end);

	/**
	 * Returns an ordered range of all the layouts that the user has permissions to view where groupId = &#63; and privateLayout = &#63; and parentLayoutId = &#63; and priority &le; &#63;.
	 *
	 * <p>
	 * Useful when paginating results. Returns a maximum of <code>end - start</code> instances. <code>start</code> and <code>end</code> are not primary keys, they are indexes in the result set. Thus, <code>0</code> refers to the first result in the set. Setting both <code>start</code> and <code>end</code> to <code>QueryUtil#ALL_POS</code> will return the full result set. If <code>orderByComparator</code> is specified, then the query will include the given ORDER BY logic. If <code>orderByComparator</code> is absent, then the query will include the default ORDER BY logic from <code>LayoutModelImpl</code>.
	 * </p>
	 *
	 * @param groupId the group ID
	 * @param privateLayout the private layout
	 * @param parentLayoutId the parent layout ID
	 * @param priority the priority
	 * @param start the lower bound of the range of layouts
	 * @param end the upper bound of the range of layouts (not inclusive)
	 * @param orderByComparator the comparator to order the results by (optionally <code>null</code>)
	 * @return the ordered range of matching layouts that the user has permission to view
	 */
	public java.util.List<Layout> filterFindByG_P_P_LteP(
		long groupId, boolean privateLayout, long parentLayoutId, int priority,
		int start, int end,
		com.liferay.portal.kernel.util.OrderByComparator<Layout>
			orderByComparator);

	/**
	 * Returns the layouts before and after the current layout in the ordered set of layouts that the user has permission to view where groupId = &#63; and privateLayout = &#63; and parentLayoutId = &#63; and priority &le; &#63;.
	 *
	 * @param plid the primary key of the current layout
	 * @param groupId the group ID
	 * @param privateLayout the private layout
	 * @param parentLayoutId the parent layout ID
	 * @param priority the priority
	 * @param orderByComparator the comparator to order the set by (optionally <code>null</code>)
	 * @return the previous, current, and next layout
	 * @throws NoSuchLayoutException if a layout with the primary key could not be found
	 */
	public Layout[] filterFindByG_P_P_LteP_PrevAndNext(
			long plid, long groupId, boolean privateLayout, long parentLayoutId,
			int priority,
			com.liferay.portal.kernel.util.OrderByComparator<Layout>
				orderByComparator)
		throws NoSuchLayoutException;

	/**
	 * Removes all the layouts where groupId = &#63; and privateLayout = &#63; and parentLayoutId = &#63; and priority &le; &#63; from the database.
	 *
	 * @param groupId the group ID
	 * @param privateLayout the private layout
	 * @param parentLayoutId the parent layout ID
	 * @param priority the priority
	 */
	public void removeByG_P_P_LteP(
		long groupId, boolean privateLayout, long parentLayoutId, int priority);

	/**
	 * Returns the number of layouts where groupId = &#63; and privateLayout = &#63; and parentLayoutId = &#63; and priority &le; &#63;.
	 *
	 * @param groupId the group ID
	 * @param privateLayout the private layout
	 * @param parentLayoutId the parent layout ID
	 * @param priority the priority
	 * @return the number of matching layouts
	 */
	public int countByG_P_P_LteP(
		long groupId, boolean privateLayout, long parentLayoutId, int priority);

	/**
	 * Returns the number of layouts that the user has permission to view where groupId = &#63; and privateLayout = &#63; and parentLayoutId = &#63; and priority &le; &#63;.
	 *
	 * @param groupId the group ID
	 * @param privateLayout the private layout
	 * @param parentLayoutId the parent layout ID
	 * @param priority the priority
	 * @return the number of matching layouts that the user has permission to view
	 */
	public int filterCountByG_P_P_LteP(
		long groupId, boolean privateLayout, long parentLayoutId, int priority);

	/**
	 * Returns the layout where externalReferenceCode = &#63; and groupId = &#63; or throws a <code>NoSuchLayoutException</code> if it could not be found.
	 *
	 * @param externalReferenceCode the external reference code
	 * @param groupId the group ID
	 * @return the matching layout
	 * @throws NoSuchLayoutException if a matching layout could not be found
	 */
	public Layout findByERC_G(String externalReferenceCode, long groupId)
		throws NoSuchLayoutException;

	/**
	 * Returns the layout where externalReferenceCode = &#63; and groupId = &#63; or returns <code>null</code> if it could not be found. Uses the finder cache.
	 *
	 * @param externalReferenceCode the external reference code
	 * @param groupId the group ID
	 * @return the matching layout, or <code>null</code> if a matching layout could not be found
	 */
	public Layout fetchByERC_G(String externalReferenceCode, long groupId);

	/**
	 * Returns the layout where externalReferenceCode = &#63; and groupId = &#63; or returns <code>null</code> if it could not be found, optionally using the finder cache.
	 *
	 * @param externalReferenceCode the external reference code
	 * @param groupId the group ID
	 * @param useFinderCache whether to use the finder cache
	 * @return the matching layout, or <code>null</code> if a matching layout could not be found
	 */
	public Layout fetchByERC_G(
		String externalReferenceCode, long groupId, boolean useFinderCache);

	/**
	 * Removes the layout where externalReferenceCode = &#63; and groupId = &#63; from the database.
	 *
	 * @param externalReferenceCode the external reference code
	 * @param groupId the group ID
	 * @return the layout that was removed
	 */
	public Layout removeByERC_G(String externalReferenceCode, long groupId)
		throws NoSuchLayoutException;

	/**
	 * Returns the number of layouts where externalReferenceCode = &#63; and groupId = &#63;.
	 *
	 * @param externalReferenceCode the external reference code
	 * @param groupId the group ID
	 * @return the number of matching layouts
	 */
	public int countByERC_G(String externalReferenceCode, long groupId);

	/**
	 * Caches the layout in the entity cache if it is enabled.
	 *
	 * @param layout the layout
	 */
	public void cacheResult(Layout layout);

	/**
	 * Caches the layouts in the entity cache if it is enabled.
	 *
	 * @param layouts the layouts
	 */
	public void cacheResult(java.util.List<Layout> layouts);

	/**
	 * Creates a new layout with the primary key. Does not add the layout to the database.
	 *
	 * @param plid the primary key for the new layout
	 * @return the new layout
	 */
	public Layout create(long plid);

	/**
	 * Removes the layout with the primary key from the database. Also notifies the appropriate model listeners.
	 *
	 * @param plid the primary key of the layout
	 * @return the layout that was removed
	 * @throws NoSuchLayoutException if a layout with the primary key could not be found
	 */
	public Layout remove(long plid) throws NoSuchLayoutException;

	public Layout updateImpl(Layout layout);

	/**
	 * Returns the layout with the primary key or throws a <code>NoSuchLayoutException</code> if it could not be found.
	 *
	 * @param plid the primary key of the layout
	 * @return the layout
	 * @throws NoSuchLayoutException if a layout with the primary key could not be found
	 */
	public Layout findByPrimaryKey(long plid) throws NoSuchLayoutException;

	/**
	 * Returns the layout with the primary key or returns <code>null</code> if it could not be found.
	 *
	 * @param plid the primary key of the layout
	 * @return the layout, or <code>null</code> if a layout with the primary key could not be found
	 */
	public Layout fetchByPrimaryKey(long plid);

	/**
	 * Returns all the layouts.
	 *
	 * @return the layouts
	 */
	public java.util.List<Layout> findAll();

	/**
	 * Returns a range of all the layouts.
	 *
	 * <p>
	 * Useful when paginating results. Returns a maximum of <code>end - start</code> instances. <code>start</code> and <code>end</code> are not primary keys, they are indexes in the result set. Thus, <code>0</code> refers to the first result in the set. Setting both <code>start</code> and <code>end</code> to <code>QueryUtil#ALL_POS</code> will return the full result set. If <code>orderByComparator</code> is specified, then the query will include the given ORDER BY logic. If <code>orderByComparator</code> is absent, then the query will include the default ORDER BY logic from <code>LayoutModelImpl</code>.
	 * </p>
	 *
	 * @param start the lower bound of the range of layouts
	 * @param end the upper bound of the range of layouts (not inclusive)
	 * @return the range of layouts
	 */
	public java.util.List<Layout> findAll(int start, int end);

	/**
	 * Returns an ordered range of all the layouts.
	 *
	 * <p>
	 * Useful when paginating results. Returns a maximum of <code>end - start</code> instances. <code>start</code> and <code>end</code> are not primary keys, they are indexes in the result set. Thus, <code>0</code> refers to the first result in the set. Setting both <code>start</code> and <code>end</code> to <code>QueryUtil#ALL_POS</code> will return the full result set. If <code>orderByComparator</code> is specified, then the query will include the given ORDER BY logic. If <code>orderByComparator</code> is absent, then the query will include the default ORDER BY logic from <code>LayoutModelImpl</code>.
	 * </p>
	 *
	 * @param start the lower bound of the range of layouts
	 * @param end the upper bound of the range of layouts (not inclusive)
	 * @param orderByComparator the comparator to order the results by (optionally <code>null</code>)
	 * @return the ordered range of layouts
	 */
	public java.util.List<Layout> findAll(
		int start, int end,
		com.liferay.portal.kernel.util.OrderByComparator<Layout>
			orderByComparator);

	/**
	 * Returns an ordered range of all the layouts.
	 *
	 * <p>
	 * Useful when paginating results. Returns a maximum of <code>end - start</code> instances. <code>start</code> and <code>end</code> are not primary keys, they are indexes in the result set. Thus, <code>0</code> refers to the first result in the set. Setting both <code>start</code> and <code>end</code> to <code>QueryUtil#ALL_POS</code> will return the full result set. If <code>orderByComparator</code> is specified, then the query will include the given ORDER BY logic. If <code>orderByComparator</code> is absent, then the query will include the default ORDER BY logic from <code>LayoutModelImpl</code>.
	 * </p>
	 *
	 * @param start the lower bound of the range of layouts
	 * @param end the upper bound of the range of layouts (not inclusive)
	 * @param orderByComparator the comparator to order the results by (optionally <code>null</code>)
	 * @param useFinderCache whether to use the finder cache
	 * @return the ordered range of layouts
	 */
	public java.util.List<Layout> findAll(
		int start, int end,
		com.liferay.portal.kernel.util.OrderByComparator<Layout>
			orderByComparator,
		boolean useFinderCache);

	/**
	 * Removes all the layouts from the database.
	 */
	public void removeAll();

	/**
	 * Returns the number of layouts.
	 *
	 * @return the number of layouts
	 */
	public int countAll();

}