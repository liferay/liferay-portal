/**
 * SPDX-FileCopyrightText: (c) 2000 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

package com.liferay.fragment.service.persistence;

import com.liferay.fragment.exception.NoSuchEntryLinkException;
import com.liferay.fragment.model.FragmentEntryLink;
import com.liferay.portal.kernel.service.persistence.BasePersistence;
import com.liferay.portal.kernel.service.persistence.change.tracking.CTPersistence;

import org.osgi.annotation.versioning.ProviderType;

/**
 * The persistence interface for the fragment entry link service.
 *
 * <p>
 * Caching information and settings can be found in <code>portal.properties</code>
 * </p>
 *
 * @author Brian Wing Shun Chan
 * @see FragmentEntryLinkUtil
 * @generated
 */
@ProviderType
public interface FragmentEntryLinkPersistence
	extends BasePersistence<FragmentEntryLink>,
			CTPersistence<FragmentEntryLink> {

	/*
	 * NOTE FOR DEVELOPERS:
	 *
	 * Never modify or reference this interface directly. Always use {@link FragmentEntryLinkUtil} to access the fragment entry link persistence. Modify <code>service.xml</code> and rerun ServiceBuilder to regenerate this interface.
	 */

	/**
	 * Returns all the fragment entry links where uuid = &#63;.
	 *
	 * @param uuid the uuid
	 * @return the matching fragment entry links
	 */
	public java.util.List<FragmentEntryLink> findByUuid(String uuid);

	/**
	 * Returns a range of all the fragment entry links where uuid = &#63;.
	 *
	 * <p>
	 * Useful when paginating results. Returns a maximum of <code>end - start</code> instances. <code>start</code> and <code>end</code> are not primary keys, they are indexes in the result set. Thus, <code>0</code> refers to the first result in the set. Setting both <code>start</code> and <code>end</code> to <code>QueryUtil#ALL_POS</code> will return the full result set. If <code>orderByComparator</code> is specified, then the query will include the given ORDER BY logic. If <code>orderByComparator</code> is absent, then the query will include the default ORDER BY logic from <code>FragmentEntryLinkModelImpl</code>.
	 * </p>
	 *
	 * @param uuid the uuid
	 * @param start the lower bound of the range of fragment entry links
	 * @param end the upper bound of the range of fragment entry links (not inclusive)
	 * @return the range of matching fragment entry links
	 */
	public java.util.List<FragmentEntryLink> findByUuid(
		String uuid, int start, int end);

	/**
	 * Returns an ordered range of all the fragment entry links where uuid = &#63;.
	 *
	 * <p>
	 * Useful when paginating results. Returns a maximum of <code>end - start</code> instances. <code>start</code> and <code>end</code> are not primary keys, they are indexes in the result set. Thus, <code>0</code> refers to the first result in the set. Setting both <code>start</code> and <code>end</code> to <code>QueryUtil#ALL_POS</code> will return the full result set. If <code>orderByComparator</code> is specified, then the query will include the given ORDER BY logic. If <code>orderByComparator</code> is absent, then the query will include the default ORDER BY logic from <code>FragmentEntryLinkModelImpl</code>.
	 * </p>
	 *
	 * @param uuid the uuid
	 * @param start the lower bound of the range of fragment entry links
	 * @param end the upper bound of the range of fragment entry links (not inclusive)
	 * @param orderByComparator the comparator to order the results by (optionally <code>null</code>)
	 * @return the ordered range of matching fragment entry links
	 */
	public java.util.List<FragmentEntryLink> findByUuid(
		String uuid, int start, int end,
		com.liferay.portal.kernel.util.OrderByComparator<FragmentEntryLink>
			orderByComparator);

	/**
	 * Returns an ordered range of all the fragment entry links where uuid = &#63;.
	 *
	 * <p>
	 * Useful when paginating results. Returns a maximum of <code>end - start</code> instances. <code>start</code> and <code>end</code> are not primary keys, they are indexes in the result set. Thus, <code>0</code> refers to the first result in the set. Setting both <code>start</code> and <code>end</code> to <code>QueryUtil#ALL_POS</code> will return the full result set. If <code>orderByComparator</code> is specified, then the query will include the given ORDER BY logic. If <code>orderByComparator</code> is absent, then the query will include the default ORDER BY logic from <code>FragmentEntryLinkModelImpl</code>.
	 * </p>
	 *
	 * @param uuid the uuid
	 * @param start the lower bound of the range of fragment entry links
	 * @param end the upper bound of the range of fragment entry links (not inclusive)
	 * @param orderByComparator the comparator to order the results by (optionally <code>null</code>)
	 * @param useFinderCache whether to use the finder cache
	 * @return the ordered range of matching fragment entry links
	 */
	public java.util.List<FragmentEntryLink> findByUuid(
		String uuid, int start, int end,
		com.liferay.portal.kernel.util.OrderByComparator<FragmentEntryLink>
			orderByComparator,
		boolean useFinderCache);

	/**
	 * Returns the first fragment entry link in the ordered set where uuid = &#63;.
	 *
	 * @param uuid the uuid
	 * @param orderByComparator the comparator to order the set by (optionally <code>null</code>)
	 * @return the first matching fragment entry link
	 * @throws NoSuchEntryLinkException if a matching fragment entry link could not be found
	 */
	public FragmentEntryLink findByUuid_First(
			String uuid,
			com.liferay.portal.kernel.util.OrderByComparator<FragmentEntryLink>
				orderByComparator)
		throws NoSuchEntryLinkException;

	/**
	 * Returns the first fragment entry link in the ordered set where uuid = &#63;.
	 *
	 * @param uuid the uuid
	 * @param orderByComparator the comparator to order the set by (optionally <code>null</code>)
	 * @return the first matching fragment entry link, or <code>null</code> if a matching fragment entry link could not be found
	 */
	public FragmentEntryLink fetchByUuid_First(
		String uuid,
		com.liferay.portal.kernel.util.OrderByComparator<FragmentEntryLink>
			orderByComparator);

	/**
	 * Returns the last fragment entry link in the ordered set where uuid = &#63;.
	 *
	 * @param uuid the uuid
	 * @param orderByComparator the comparator to order the set by (optionally <code>null</code>)
	 * @return the last matching fragment entry link
	 * @throws NoSuchEntryLinkException if a matching fragment entry link could not be found
	 */
	public FragmentEntryLink findByUuid_Last(
			String uuid,
			com.liferay.portal.kernel.util.OrderByComparator<FragmentEntryLink>
				orderByComparator)
		throws NoSuchEntryLinkException;

	/**
	 * Returns the last fragment entry link in the ordered set where uuid = &#63;.
	 *
	 * @param uuid the uuid
	 * @param orderByComparator the comparator to order the set by (optionally <code>null</code>)
	 * @return the last matching fragment entry link, or <code>null</code> if a matching fragment entry link could not be found
	 */
	public FragmentEntryLink fetchByUuid_Last(
		String uuid,
		com.liferay.portal.kernel.util.OrderByComparator<FragmentEntryLink>
			orderByComparator);

	/**
	 * Returns the fragment entry links before and after the current fragment entry link in the ordered set where uuid = &#63;.
	 *
	 * @param fragmentEntryLinkId the primary key of the current fragment entry link
	 * @param uuid the uuid
	 * @param orderByComparator the comparator to order the set by (optionally <code>null</code>)
	 * @return the previous, current, and next fragment entry link
	 * @throws NoSuchEntryLinkException if a fragment entry link with the primary key could not be found
	 */
	public FragmentEntryLink[] findByUuid_PrevAndNext(
			long fragmentEntryLinkId, String uuid,
			com.liferay.portal.kernel.util.OrderByComparator<FragmentEntryLink>
				orderByComparator)
		throws NoSuchEntryLinkException;

	/**
	 * Removes all the fragment entry links where uuid = &#63; from the database.
	 *
	 * @param uuid the uuid
	 */
	public void removeByUuid(String uuid);

	/**
	 * Returns the number of fragment entry links where uuid = &#63;.
	 *
	 * @param uuid the uuid
	 * @return the number of matching fragment entry links
	 */
	public int countByUuid(String uuid);

	/**
	 * Returns the fragment entry link where uuid = &#63; and groupId = &#63; or throws a <code>NoSuchEntryLinkException</code> if it could not be found.
	 *
	 * @param uuid the uuid
	 * @param groupId the group ID
	 * @return the matching fragment entry link
	 * @throws NoSuchEntryLinkException if a matching fragment entry link could not be found
	 */
	public FragmentEntryLink findByUUID_G(String uuid, long groupId)
		throws NoSuchEntryLinkException;

	/**
	 * Returns the fragment entry link where uuid = &#63; and groupId = &#63; or returns <code>null</code> if it could not be found. Uses the finder cache.
	 *
	 * @param uuid the uuid
	 * @param groupId the group ID
	 * @return the matching fragment entry link, or <code>null</code> if a matching fragment entry link could not be found
	 */
	public FragmentEntryLink fetchByUUID_G(String uuid, long groupId);

	/**
	 * Returns the fragment entry link where uuid = &#63; and groupId = &#63; or returns <code>null</code> if it could not be found, optionally using the finder cache.
	 *
	 * @param uuid the uuid
	 * @param groupId the group ID
	 * @param useFinderCache whether to use the finder cache
	 * @return the matching fragment entry link, or <code>null</code> if a matching fragment entry link could not be found
	 */
	public FragmentEntryLink fetchByUUID_G(
		String uuid, long groupId, boolean useFinderCache);

	/**
	 * Removes the fragment entry link where uuid = &#63; and groupId = &#63; from the database.
	 *
	 * @param uuid the uuid
	 * @param groupId the group ID
	 * @return the fragment entry link that was removed
	 */
	public FragmentEntryLink removeByUUID_G(String uuid, long groupId)
		throws NoSuchEntryLinkException;

	/**
	 * Returns the number of fragment entry links where uuid = &#63; and groupId = &#63;.
	 *
	 * @param uuid the uuid
	 * @param groupId the group ID
	 * @return the number of matching fragment entry links
	 */
	public int countByUUID_G(String uuid, long groupId);

	/**
	 * Returns all the fragment entry links where uuid = &#63; and companyId = &#63;.
	 *
	 * @param uuid the uuid
	 * @param companyId the company ID
	 * @return the matching fragment entry links
	 */
	public java.util.List<FragmentEntryLink> findByUuid_C(
		String uuid, long companyId);

	/**
	 * Returns a range of all the fragment entry links where uuid = &#63; and companyId = &#63;.
	 *
	 * <p>
	 * Useful when paginating results. Returns a maximum of <code>end - start</code> instances. <code>start</code> and <code>end</code> are not primary keys, they are indexes in the result set. Thus, <code>0</code> refers to the first result in the set. Setting both <code>start</code> and <code>end</code> to <code>QueryUtil#ALL_POS</code> will return the full result set. If <code>orderByComparator</code> is specified, then the query will include the given ORDER BY logic. If <code>orderByComparator</code> is absent, then the query will include the default ORDER BY logic from <code>FragmentEntryLinkModelImpl</code>.
	 * </p>
	 *
	 * @param uuid the uuid
	 * @param companyId the company ID
	 * @param start the lower bound of the range of fragment entry links
	 * @param end the upper bound of the range of fragment entry links (not inclusive)
	 * @return the range of matching fragment entry links
	 */
	public java.util.List<FragmentEntryLink> findByUuid_C(
		String uuid, long companyId, int start, int end);

	/**
	 * Returns an ordered range of all the fragment entry links where uuid = &#63; and companyId = &#63;.
	 *
	 * <p>
	 * Useful when paginating results. Returns a maximum of <code>end - start</code> instances. <code>start</code> and <code>end</code> are not primary keys, they are indexes in the result set. Thus, <code>0</code> refers to the first result in the set. Setting both <code>start</code> and <code>end</code> to <code>QueryUtil#ALL_POS</code> will return the full result set. If <code>orderByComparator</code> is specified, then the query will include the given ORDER BY logic. If <code>orderByComparator</code> is absent, then the query will include the default ORDER BY logic from <code>FragmentEntryLinkModelImpl</code>.
	 * </p>
	 *
	 * @param uuid the uuid
	 * @param companyId the company ID
	 * @param start the lower bound of the range of fragment entry links
	 * @param end the upper bound of the range of fragment entry links (not inclusive)
	 * @param orderByComparator the comparator to order the results by (optionally <code>null</code>)
	 * @return the ordered range of matching fragment entry links
	 */
	public java.util.List<FragmentEntryLink> findByUuid_C(
		String uuid, long companyId, int start, int end,
		com.liferay.portal.kernel.util.OrderByComparator<FragmentEntryLink>
			orderByComparator);

	/**
	 * Returns an ordered range of all the fragment entry links where uuid = &#63; and companyId = &#63;.
	 *
	 * <p>
	 * Useful when paginating results. Returns a maximum of <code>end - start</code> instances. <code>start</code> and <code>end</code> are not primary keys, they are indexes in the result set. Thus, <code>0</code> refers to the first result in the set. Setting both <code>start</code> and <code>end</code> to <code>QueryUtil#ALL_POS</code> will return the full result set. If <code>orderByComparator</code> is specified, then the query will include the given ORDER BY logic. If <code>orderByComparator</code> is absent, then the query will include the default ORDER BY logic from <code>FragmentEntryLinkModelImpl</code>.
	 * </p>
	 *
	 * @param uuid the uuid
	 * @param companyId the company ID
	 * @param start the lower bound of the range of fragment entry links
	 * @param end the upper bound of the range of fragment entry links (not inclusive)
	 * @param orderByComparator the comparator to order the results by (optionally <code>null</code>)
	 * @param useFinderCache whether to use the finder cache
	 * @return the ordered range of matching fragment entry links
	 */
	public java.util.List<FragmentEntryLink> findByUuid_C(
		String uuid, long companyId, int start, int end,
		com.liferay.portal.kernel.util.OrderByComparator<FragmentEntryLink>
			orderByComparator,
		boolean useFinderCache);

	/**
	 * Returns the first fragment entry link in the ordered set where uuid = &#63; and companyId = &#63;.
	 *
	 * @param uuid the uuid
	 * @param companyId the company ID
	 * @param orderByComparator the comparator to order the set by (optionally <code>null</code>)
	 * @return the first matching fragment entry link
	 * @throws NoSuchEntryLinkException if a matching fragment entry link could not be found
	 */
	public FragmentEntryLink findByUuid_C_First(
			String uuid, long companyId,
			com.liferay.portal.kernel.util.OrderByComparator<FragmentEntryLink>
				orderByComparator)
		throws NoSuchEntryLinkException;

	/**
	 * Returns the first fragment entry link in the ordered set where uuid = &#63; and companyId = &#63;.
	 *
	 * @param uuid the uuid
	 * @param companyId the company ID
	 * @param orderByComparator the comparator to order the set by (optionally <code>null</code>)
	 * @return the first matching fragment entry link, or <code>null</code> if a matching fragment entry link could not be found
	 */
	public FragmentEntryLink fetchByUuid_C_First(
		String uuid, long companyId,
		com.liferay.portal.kernel.util.OrderByComparator<FragmentEntryLink>
			orderByComparator);

	/**
	 * Returns the last fragment entry link in the ordered set where uuid = &#63; and companyId = &#63;.
	 *
	 * @param uuid the uuid
	 * @param companyId the company ID
	 * @param orderByComparator the comparator to order the set by (optionally <code>null</code>)
	 * @return the last matching fragment entry link
	 * @throws NoSuchEntryLinkException if a matching fragment entry link could not be found
	 */
	public FragmentEntryLink findByUuid_C_Last(
			String uuid, long companyId,
			com.liferay.portal.kernel.util.OrderByComparator<FragmentEntryLink>
				orderByComparator)
		throws NoSuchEntryLinkException;

	/**
	 * Returns the last fragment entry link in the ordered set where uuid = &#63; and companyId = &#63;.
	 *
	 * @param uuid the uuid
	 * @param companyId the company ID
	 * @param orderByComparator the comparator to order the set by (optionally <code>null</code>)
	 * @return the last matching fragment entry link, or <code>null</code> if a matching fragment entry link could not be found
	 */
	public FragmentEntryLink fetchByUuid_C_Last(
		String uuid, long companyId,
		com.liferay.portal.kernel.util.OrderByComparator<FragmentEntryLink>
			orderByComparator);

	/**
	 * Returns the fragment entry links before and after the current fragment entry link in the ordered set where uuid = &#63; and companyId = &#63;.
	 *
	 * @param fragmentEntryLinkId the primary key of the current fragment entry link
	 * @param uuid the uuid
	 * @param companyId the company ID
	 * @param orderByComparator the comparator to order the set by (optionally <code>null</code>)
	 * @return the previous, current, and next fragment entry link
	 * @throws NoSuchEntryLinkException if a fragment entry link with the primary key could not be found
	 */
	public FragmentEntryLink[] findByUuid_C_PrevAndNext(
			long fragmentEntryLinkId, String uuid, long companyId,
			com.liferay.portal.kernel.util.OrderByComparator<FragmentEntryLink>
				orderByComparator)
		throws NoSuchEntryLinkException;

	/**
	 * Removes all the fragment entry links where uuid = &#63; and companyId = &#63; from the database.
	 *
	 * @param uuid the uuid
	 * @param companyId the company ID
	 */
	public void removeByUuid_C(String uuid, long companyId);

	/**
	 * Returns the number of fragment entry links where uuid = &#63; and companyId = &#63;.
	 *
	 * @param uuid the uuid
	 * @param companyId the company ID
	 * @return the number of matching fragment entry links
	 */
	public int countByUuid_C(String uuid, long companyId);

	/**
	 * Returns all the fragment entry links where groupId = &#63;.
	 *
	 * @param groupId the group ID
	 * @return the matching fragment entry links
	 */
	public java.util.List<FragmentEntryLink> findByGroupId(long groupId);

	/**
	 * Returns a range of all the fragment entry links where groupId = &#63;.
	 *
	 * <p>
	 * Useful when paginating results. Returns a maximum of <code>end - start</code> instances. <code>start</code> and <code>end</code> are not primary keys, they are indexes in the result set. Thus, <code>0</code> refers to the first result in the set. Setting both <code>start</code> and <code>end</code> to <code>QueryUtil#ALL_POS</code> will return the full result set. If <code>orderByComparator</code> is specified, then the query will include the given ORDER BY logic. If <code>orderByComparator</code> is absent, then the query will include the default ORDER BY logic from <code>FragmentEntryLinkModelImpl</code>.
	 * </p>
	 *
	 * @param groupId the group ID
	 * @param start the lower bound of the range of fragment entry links
	 * @param end the upper bound of the range of fragment entry links (not inclusive)
	 * @return the range of matching fragment entry links
	 */
	public java.util.List<FragmentEntryLink> findByGroupId(
		long groupId, int start, int end);

	/**
	 * Returns an ordered range of all the fragment entry links where groupId = &#63;.
	 *
	 * <p>
	 * Useful when paginating results. Returns a maximum of <code>end - start</code> instances. <code>start</code> and <code>end</code> are not primary keys, they are indexes in the result set. Thus, <code>0</code> refers to the first result in the set. Setting both <code>start</code> and <code>end</code> to <code>QueryUtil#ALL_POS</code> will return the full result set. If <code>orderByComparator</code> is specified, then the query will include the given ORDER BY logic. If <code>orderByComparator</code> is absent, then the query will include the default ORDER BY logic from <code>FragmentEntryLinkModelImpl</code>.
	 * </p>
	 *
	 * @param groupId the group ID
	 * @param start the lower bound of the range of fragment entry links
	 * @param end the upper bound of the range of fragment entry links (not inclusive)
	 * @param orderByComparator the comparator to order the results by (optionally <code>null</code>)
	 * @return the ordered range of matching fragment entry links
	 */
	public java.util.List<FragmentEntryLink> findByGroupId(
		long groupId, int start, int end,
		com.liferay.portal.kernel.util.OrderByComparator<FragmentEntryLink>
			orderByComparator);

	/**
	 * Returns an ordered range of all the fragment entry links where groupId = &#63;.
	 *
	 * <p>
	 * Useful when paginating results. Returns a maximum of <code>end - start</code> instances. <code>start</code> and <code>end</code> are not primary keys, they are indexes in the result set. Thus, <code>0</code> refers to the first result in the set. Setting both <code>start</code> and <code>end</code> to <code>QueryUtil#ALL_POS</code> will return the full result set. If <code>orderByComparator</code> is specified, then the query will include the given ORDER BY logic. If <code>orderByComparator</code> is absent, then the query will include the default ORDER BY logic from <code>FragmentEntryLinkModelImpl</code>.
	 * </p>
	 *
	 * @param groupId the group ID
	 * @param start the lower bound of the range of fragment entry links
	 * @param end the upper bound of the range of fragment entry links (not inclusive)
	 * @param orderByComparator the comparator to order the results by (optionally <code>null</code>)
	 * @param useFinderCache whether to use the finder cache
	 * @return the ordered range of matching fragment entry links
	 */
	public java.util.List<FragmentEntryLink> findByGroupId(
		long groupId, int start, int end,
		com.liferay.portal.kernel.util.OrderByComparator<FragmentEntryLink>
			orderByComparator,
		boolean useFinderCache);

	/**
	 * Returns the first fragment entry link in the ordered set where groupId = &#63;.
	 *
	 * @param groupId the group ID
	 * @param orderByComparator the comparator to order the set by (optionally <code>null</code>)
	 * @return the first matching fragment entry link
	 * @throws NoSuchEntryLinkException if a matching fragment entry link could not be found
	 */
	public FragmentEntryLink findByGroupId_First(
			long groupId,
			com.liferay.portal.kernel.util.OrderByComparator<FragmentEntryLink>
				orderByComparator)
		throws NoSuchEntryLinkException;

	/**
	 * Returns the first fragment entry link in the ordered set where groupId = &#63;.
	 *
	 * @param groupId the group ID
	 * @param orderByComparator the comparator to order the set by (optionally <code>null</code>)
	 * @return the first matching fragment entry link, or <code>null</code> if a matching fragment entry link could not be found
	 */
	public FragmentEntryLink fetchByGroupId_First(
		long groupId,
		com.liferay.portal.kernel.util.OrderByComparator<FragmentEntryLink>
			orderByComparator);

	/**
	 * Returns the last fragment entry link in the ordered set where groupId = &#63;.
	 *
	 * @param groupId the group ID
	 * @param orderByComparator the comparator to order the set by (optionally <code>null</code>)
	 * @return the last matching fragment entry link
	 * @throws NoSuchEntryLinkException if a matching fragment entry link could not be found
	 */
	public FragmentEntryLink findByGroupId_Last(
			long groupId,
			com.liferay.portal.kernel.util.OrderByComparator<FragmentEntryLink>
				orderByComparator)
		throws NoSuchEntryLinkException;

	/**
	 * Returns the last fragment entry link in the ordered set where groupId = &#63;.
	 *
	 * @param groupId the group ID
	 * @param orderByComparator the comparator to order the set by (optionally <code>null</code>)
	 * @return the last matching fragment entry link, or <code>null</code> if a matching fragment entry link could not be found
	 */
	public FragmentEntryLink fetchByGroupId_Last(
		long groupId,
		com.liferay.portal.kernel.util.OrderByComparator<FragmentEntryLink>
			orderByComparator);

	/**
	 * Returns the fragment entry links before and after the current fragment entry link in the ordered set where groupId = &#63;.
	 *
	 * @param fragmentEntryLinkId the primary key of the current fragment entry link
	 * @param groupId the group ID
	 * @param orderByComparator the comparator to order the set by (optionally <code>null</code>)
	 * @return the previous, current, and next fragment entry link
	 * @throws NoSuchEntryLinkException if a fragment entry link with the primary key could not be found
	 */
	public FragmentEntryLink[] findByGroupId_PrevAndNext(
			long fragmentEntryLinkId, long groupId,
			com.liferay.portal.kernel.util.OrderByComparator<FragmentEntryLink>
				orderByComparator)
		throws NoSuchEntryLinkException;

	/**
	 * Removes all the fragment entry links where groupId = &#63; from the database.
	 *
	 * @param groupId the group ID
	 */
	public void removeByGroupId(long groupId);

	/**
	 * Returns the number of fragment entry links where groupId = &#63;.
	 *
	 * @param groupId the group ID
	 * @return the number of matching fragment entry links
	 */
	public int countByGroupId(long groupId);

	/**
	 * Returns all the fragment entry links where rendererKey = &#63;.
	 *
	 * @param rendererKey the renderer key
	 * @return the matching fragment entry links
	 */
	public java.util.List<FragmentEntryLink> findByRendererKey(
		String rendererKey);

	/**
	 * Returns a range of all the fragment entry links where rendererKey = &#63;.
	 *
	 * <p>
	 * Useful when paginating results. Returns a maximum of <code>end - start</code> instances. <code>start</code> and <code>end</code> are not primary keys, they are indexes in the result set. Thus, <code>0</code> refers to the first result in the set. Setting both <code>start</code> and <code>end</code> to <code>QueryUtil#ALL_POS</code> will return the full result set. If <code>orderByComparator</code> is specified, then the query will include the given ORDER BY logic. If <code>orderByComparator</code> is absent, then the query will include the default ORDER BY logic from <code>FragmentEntryLinkModelImpl</code>.
	 * </p>
	 *
	 * @param rendererKey the renderer key
	 * @param start the lower bound of the range of fragment entry links
	 * @param end the upper bound of the range of fragment entry links (not inclusive)
	 * @return the range of matching fragment entry links
	 */
	public java.util.List<FragmentEntryLink> findByRendererKey(
		String rendererKey, int start, int end);

	/**
	 * Returns an ordered range of all the fragment entry links where rendererKey = &#63;.
	 *
	 * <p>
	 * Useful when paginating results. Returns a maximum of <code>end - start</code> instances. <code>start</code> and <code>end</code> are not primary keys, they are indexes in the result set. Thus, <code>0</code> refers to the first result in the set. Setting both <code>start</code> and <code>end</code> to <code>QueryUtil#ALL_POS</code> will return the full result set. If <code>orderByComparator</code> is specified, then the query will include the given ORDER BY logic. If <code>orderByComparator</code> is absent, then the query will include the default ORDER BY logic from <code>FragmentEntryLinkModelImpl</code>.
	 * </p>
	 *
	 * @param rendererKey the renderer key
	 * @param start the lower bound of the range of fragment entry links
	 * @param end the upper bound of the range of fragment entry links (not inclusive)
	 * @param orderByComparator the comparator to order the results by (optionally <code>null</code>)
	 * @return the ordered range of matching fragment entry links
	 */
	public java.util.List<FragmentEntryLink> findByRendererKey(
		String rendererKey, int start, int end,
		com.liferay.portal.kernel.util.OrderByComparator<FragmentEntryLink>
			orderByComparator);

	/**
	 * Returns an ordered range of all the fragment entry links where rendererKey = &#63;.
	 *
	 * <p>
	 * Useful when paginating results. Returns a maximum of <code>end - start</code> instances. <code>start</code> and <code>end</code> are not primary keys, they are indexes in the result set. Thus, <code>0</code> refers to the first result in the set. Setting both <code>start</code> and <code>end</code> to <code>QueryUtil#ALL_POS</code> will return the full result set. If <code>orderByComparator</code> is specified, then the query will include the given ORDER BY logic. If <code>orderByComparator</code> is absent, then the query will include the default ORDER BY logic from <code>FragmentEntryLinkModelImpl</code>.
	 * </p>
	 *
	 * @param rendererKey the renderer key
	 * @param start the lower bound of the range of fragment entry links
	 * @param end the upper bound of the range of fragment entry links (not inclusive)
	 * @param orderByComparator the comparator to order the results by (optionally <code>null</code>)
	 * @param useFinderCache whether to use the finder cache
	 * @return the ordered range of matching fragment entry links
	 */
	public java.util.List<FragmentEntryLink> findByRendererKey(
		String rendererKey, int start, int end,
		com.liferay.portal.kernel.util.OrderByComparator<FragmentEntryLink>
			orderByComparator,
		boolean useFinderCache);

	/**
	 * Returns the first fragment entry link in the ordered set where rendererKey = &#63;.
	 *
	 * @param rendererKey the renderer key
	 * @param orderByComparator the comparator to order the set by (optionally <code>null</code>)
	 * @return the first matching fragment entry link
	 * @throws NoSuchEntryLinkException if a matching fragment entry link could not be found
	 */
	public FragmentEntryLink findByRendererKey_First(
			String rendererKey,
			com.liferay.portal.kernel.util.OrderByComparator<FragmentEntryLink>
				orderByComparator)
		throws NoSuchEntryLinkException;

	/**
	 * Returns the first fragment entry link in the ordered set where rendererKey = &#63;.
	 *
	 * @param rendererKey the renderer key
	 * @param orderByComparator the comparator to order the set by (optionally <code>null</code>)
	 * @return the first matching fragment entry link, or <code>null</code> if a matching fragment entry link could not be found
	 */
	public FragmentEntryLink fetchByRendererKey_First(
		String rendererKey,
		com.liferay.portal.kernel.util.OrderByComparator<FragmentEntryLink>
			orderByComparator);

	/**
	 * Returns the last fragment entry link in the ordered set where rendererKey = &#63;.
	 *
	 * @param rendererKey the renderer key
	 * @param orderByComparator the comparator to order the set by (optionally <code>null</code>)
	 * @return the last matching fragment entry link
	 * @throws NoSuchEntryLinkException if a matching fragment entry link could not be found
	 */
	public FragmentEntryLink findByRendererKey_Last(
			String rendererKey,
			com.liferay.portal.kernel.util.OrderByComparator<FragmentEntryLink>
				orderByComparator)
		throws NoSuchEntryLinkException;

	/**
	 * Returns the last fragment entry link in the ordered set where rendererKey = &#63;.
	 *
	 * @param rendererKey the renderer key
	 * @param orderByComparator the comparator to order the set by (optionally <code>null</code>)
	 * @return the last matching fragment entry link, or <code>null</code> if a matching fragment entry link could not be found
	 */
	public FragmentEntryLink fetchByRendererKey_Last(
		String rendererKey,
		com.liferay.portal.kernel.util.OrderByComparator<FragmentEntryLink>
			orderByComparator);

	/**
	 * Returns the fragment entry links before and after the current fragment entry link in the ordered set where rendererKey = &#63;.
	 *
	 * @param fragmentEntryLinkId the primary key of the current fragment entry link
	 * @param rendererKey the renderer key
	 * @param orderByComparator the comparator to order the set by (optionally <code>null</code>)
	 * @return the previous, current, and next fragment entry link
	 * @throws NoSuchEntryLinkException if a fragment entry link with the primary key could not be found
	 */
	public FragmentEntryLink[] findByRendererKey_PrevAndNext(
			long fragmentEntryLinkId, String rendererKey,
			com.liferay.portal.kernel.util.OrderByComparator<FragmentEntryLink>
				orderByComparator)
		throws NoSuchEntryLinkException;

	/**
	 * Removes all the fragment entry links where rendererKey = &#63; from the database.
	 *
	 * @param rendererKey the renderer key
	 */
	public void removeByRendererKey(String rendererKey);

	/**
	 * Returns the number of fragment entry links where rendererKey = &#63;.
	 *
	 * @param rendererKey the renderer key
	 * @return the number of matching fragment entry links
	 */
	public int countByRendererKey(String rendererKey);

	/**
	 * Returns all the fragment entry links where type = &#63;.
	 *
	 * @param type the type
	 * @return the matching fragment entry links
	 */
	public java.util.List<FragmentEntryLink> findByType(int type);

	/**
	 * Returns a range of all the fragment entry links where type = &#63;.
	 *
	 * <p>
	 * Useful when paginating results. Returns a maximum of <code>end - start</code> instances. <code>start</code> and <code>end</code> are not primary keys, they are indexes in the result set. Thus, <code>0</code> refers to the first result in the set. Setting both <code>start</code> and <code>end</code> to <code>QueryUtil#ALL_POS</code> will return the full result set. If <code>orderByComparator</code> is specified, then the query will include the given ORDER BY logic. If <code>orderByComparator</code> is absent, then the query will include the default ORDER BY logic from <code>FragmentEntryLinkModelImpl</code>.
	 * </p>
	 *
	 * @param type the type
	 * @param start the lower bound of the range of fragment entry links
	 * @param end the upper bound of the range of fragment entry links (not inclusive)
	 * @return the range of matching fragment entry links
	 */
	public java.util.List<FragmentEntryLink> findByType(
		int type, int start, int end);

	/**
	 * Returns an ordered range of all the fragment entry links where type = &#63;.
	 *
	 * <p>
	 * Useful when paginating results. Returns a maximum of <code>end - start</code> instances. <code>start</code> and <code>end</code> are not primary keys, they are indexes in the result set. Thus, <code>0</code> refers to the first result in the set. Setting both <code>start</code> and <code>end</code> to <code>QueryUtil#ALL_POS</code> will return the full result set. If <code>orderByComparator</code> is specified, then the query will include the given ORDER BY logic. If <code>orderByComparator</code> is absent, then the query will include the default ORDER BY logic from <code>FragmentEntryLinkModelImpl</code>.
	 * </p>
	 *
	 * @param type the type
	 * @param start the lower bound of the range of fragment entry links
	 * @param end the upper bound of the range of fragment entry links (not inclusive)
	 * @param orderByComparator the comparator to order the results by (optionally <code>null</code>)
	 * @return the ordered range of matching fragment entry links
	 */
	public java.util.List<FragmentEntryLink> findByType(
		int type, int start, int end,
		com.liferay.portal.kernel.util.OrderByComparator<FragmentEntryLink>
			orderByComparator);

	/**
	 * Returns an ordered range of all the fragment entry links where type = &#63;.
	 *
	 * <p>
	 * Useful when paginating results. Returns a maximum of <code>end - start</code> instances. <code>start</code> and <code>end</code> are not primary keys, they are indexes in the result set. Thus, <code>0</code> refers to the first result in the set. Setting both <code>start</code> and <code>end</code> to <code>QueryUtil#ALL_POS</code> will return the full result set. If <code>orderByComparator</code> is specified, then the query will include the given ORDER BY logic. If <code>orderByComparator</code> is absent, then the query will include the default ORDER BY logic from <code>FragmentEntryLinkModelImpl</code>.
	 * </p>
	 *
	 * @param type the type
	 * @param start the lower bound of the range of fragment entry links
	 * @param end the upper bound of the range of fragment entry links (not inclusive)
	 * @param orderByComparator the comparator to order the results by (optionally <code>null</code>)
	 * @param useFinderCache whether to use the finder cache
	 * @return the ordered range of matching fragment entry links
	 */
	public java.util.List<FragmentEntryLink> findByType(
		int type, int start, int end,
		com.liferay.portal.kernel.util.OrderByComparator<FragmentEntryLink>
			orderByComparator,
		boolean useFinderCache);

	/**
	 * Returns the first fragment entry link in the ordered set where type = &#63;.
	 *
	 * @param type the type
	 * @param orderByComparator the comparator to order the set by (optionally <code>null</code>)
	 * @return the first matching fragment entry link
	 * @throws NoSuchEntryLinkException if a matching fragment entry link could not be found
	 */
	public FragmentEntryLink findByType_First(
			int type,
			com.liferay.portal.kernel.util.OrderByComparator<FragmentEntryLink>
				orderByComparator)
		throws NoSuchEntryLinkException;

	/**
	 * Returns the first fragment entry link in the ordered set where type = &#63;.
	 *
	 * @param type the type
	 * @param orderByComparator the comparator to order the set by (optionally <code>null</code>)
	 * @return the first matching fragment entry link, or <code>null</code> if a matching fragment entry link could not be found
	 */
	public FragmentEntryLink fetchByType_First(
		int type,
		com.liferay.portal.kernel.util.OrderByComparator<FragmentEntryLink>
			orderByComparator);

	/**
	 * Returns the last fragment entry link in the ordered set where type = &#63;.
	 *
	 * @param type the type
	 * @param orderByComparator the comparator to order the set by (optionally <code>null</code>)
	 * @return the last matching fragment entry link
	 * @throws NoSuchEntryLinkException if a matching fragment entry link could not be found
	 */
	public FragmentEntryLink findByType_Last(
			int type,
			com.liferay.portal.kernel.util.OrderByComparator<FragmentEntryLink>
				orderByComparator)
		throws NoSuchEntryLinkException;

	/**
	 * Returns the last fragment entry link in the ordered set where type = &#63;.
	 *
	 * @param type the type
	 * @param orderByComparator the comparator to order the set by (optionally <code>null</code>)
	 * @return the last matching fragment entry link, or <code>null</code> if a matching fragment entry link could not be found
	 */
	public FragmentEntryLink fetchByType_Last(
		int type,
		com.liferay.portal.kernel.util.OrderByComparator<FragmentEntryLink>
			orderByComparator);

	/**
	 * Returns the fragment entry links before and after the current fragment entry link in the ordered set where type = &#63;.
	 *
	 * @param fragmentEntryLinkId the primary key of the current fragment entry link
	 * @param type the type
	 * @param orderByComparator the comparator to order the set by (optionally <code>null</code>)
	 * @return the previous, current, and next fragment entry link
	 * @throws NoSuchEntryLinkException if a fragment entry link with the primary key could not be found
	 */
	public FragmentEntryLink[] findByType_PrevAndNext(
			long fragmentEntryLinkId, int type,
			com.liferay.portal.kernel.util.OrderByComparator<FragmentEntryLink>
				orderByComparator)
		throws NoSuchEntryLinkException;

	/**
	 * Removes all the fragment entry links where type = &#63; from the database.
	 *
	 * @param type the type
	 */
	public void removeByType(int type);

	/**
	 * Returns the number of fragment entry links where type = &#63;.
	 *
	 * @param type the type
	 * @return the number of matching fragment entry links
	 */
	public int countByType(int type);

	/**
	 * Returns all the fragment entry links where groupId = &#63; and plid = &#63;.
	 *
	 * @param groupId the group ID
	 * @param plid the plid
	 * @return the matching fragment entry links
	 */
	public java.util.List<FragmentEntryLink> findByG_P(long groupId, long plid);

	/**
	 * Returns a range of all the fragment entry links where groupId = &#63; and plid = &#63;.
	 *
	 * <p>
	 * Useful when paginating results. Returns a maximum of <code>end - start</code> instances. <code>start</code> and <code>end</code> are not primary keys, they are indexes in the result set. Thus, <code>0</code> refers to the first result in the set. Setting both <code>start</code> and <code>end</code> to <code>QueryUtil#ALL_POS</code> will return the full result set. If <code>orderByComparator</code> is specified, then the query will include the given ORDER BY logic. If <code>orderByComparator</code> is absent, then the query will include the default ORDER BY logic from <code>FragmentEntryLinkModelImpl</code>.
	 * </p>
	 *
	 * @param groupId the group ID
	 * @param plid the plid
	 * @param start the lower bound of the range of fragment entry links
	 * @param end the upper bound of the range of fragment entry links (not inclusive)
	 * @return the range of matching fragment entry links
	 */
	public java.util.List<FragmentEntryLink> findByG_P(
		long groupId, long plid, int start, int end);

	/**
	 * Returns an ordered range of all the fragment entry links where groupId = &#63; and plid = &#63;.
	 *
	 * <p>
	 * Useful when paginating results. Returns a maximum of <code>end - start</code> instances. <code>start</code> and <code>end</code> are not primary keys, they are indexes in the result set. Thus, <code>0</code> refers to the first result in the set. Setting both <code>start</code> and <code>end</code> to <code>QueryUtil#ALL_POS</code> will return the full result set. If <code>orderByComparator</code> is specified, then the query will include the given ORDER BY logic. If <code>orderByComparator</code> is absent, then the query will include the default ORDER BY logic from <code>FragmentEntryLinkModelImpl</code>.
	 * </p>
	 *
	 * @param groupId the group ID
	 * @param plid the plid
	 * @param start the lower bound of the range of fragment entry links
	 * @param end the upper bound of the range of fragment entry links (not inclusive)
	 * @param orderByComparator the comparator to order the results by (optionally <code>null</code>)
	 * @return the ordered range of matching fragment entry links
	 */
	public java.util.List<FragmentEntryLink> findByG_P(
		long groupId, long plid, int start, int end,
		com.liferay.portal.kernel.util.OrderByComparator<FragmentEntryLink>
			orderByComparator);

	/**
	 * Returns an ordered range of all the fragment entry links where groupId = &#63; and plid = &#63;.
	 *
	 * <p>
	 * Useful when paginating results. Returns a maximum of <code>end - start</code> instances. <code>start</code> and <code>end</code> are not primary keys, they are indexes in the result set. Thus, <code>0</code> refers to the first result in the set. Setting both <code>start</code> and <code>end</code> to <code>QueryUtil#ALL_POS</code> will return the full result set. If <code>orderByComparator</code> is specified, then the query will include the given ORDER BY logic. If <code>orderByComparator</code> is absent, then the query will include the default ORDER BY logic from <code>FragmentEntryLinkModelImpl</code>.
	 * </p>
	 *
	 * @param groupId the group ID
	 * @param plid the plid
	 * @param start the lower bound of the range of fragment entry links
	 * @param end the upper bound of the range of fragment entry links (not inclusive)
	 * @param orderByComparator the comparator to order the results by (optionally <code>null</code>)
	 * @param useFinderCache whether to use the finder cache
	 * @return the ordered range of matching fragment entry links
	 */
	public java.util.List<FragmentEntryLink> findByG_P(
		long groupId, long plid, int start, int end,
		com.liferay.portal.kernel.util.OrderByComparator<FragmentEntryLink>
			orderByComparator,
		boolean useFinderCache);

	/**
	 * Returns the first fragment entry link in the ordered set where groupId = &#63; and plid = &#63;.
	 *
	 * @param groupId the group ID
	 * @param plid the plid
	 * @param orderByComparator the comparator to order the set by (optionally <code>null</code>)
	 * @return the first matching fragment entry link
	 * @throws NoSuchEntryLinkException if a matching fragment entry link could not be found
	 */
	public FragmentEntryLink findByG_P_First(
			long groupId, long plid,
			com.liferay.portal.kernel.util.OrderByComparator<FragmentEntryLink>
				orderByComparator)
		throws NoSuchEntryLinkException;

	/**
	 * Returns the first fragment entry link in the ordered set where groupId = &#63; and plid = &#63;.
	 *
	 * @param groupId the group ID
	 * @param plid the plid
	 * @param orderByComparator the comparator to order the set by (optionally <code>null</code>)
	 * @return the first matching fragment entry link, or <code>null</code> if a matching fragment entry link could not be found
	 */
	public FragmentEntryLink fetchByG_P_First(
		long groupId, long plid,
		com.liferay.portal.kernel.util.OrderByComparator<FragmentEntryLink>
			orderByComparator);

	/**
	 * Returns the last fragment entry link in the ordered set where groupId = &#63; and plid = &#63;.
	 *
	 * @param groupId the group ID
	 * @param plid the plid
	 * @param orderByComparator the comparator to order the set by (optionally <code>null</code>)
	 * @return the last matching fragment entry link
	 * @throws NoSuchEntryLinkException if a matching fragment entry link could not be found
	 */
	public FragmentEntryLink findByG_P_Last(
			long groupId, long plid,
			com.liferay.portal.kernel.util.OrderByComparator<FragmentEntryLink>
				orderByComparator)
		throws NoSuchEntryLinkException;

	/**
	 * Returns the last fragment entry link in the ordered set where groupId = &#63; and plid = &#63;.
	 *
	 * @param groupId the group ID
	 * @param plid the plid
	 * @param orderByComparator the comparator to order the set by (optionally <code>null</code>)
	 * @return the last matching fragment entry link, or <code>null</code> if a matching fragment entry link could not be found
	 */
	public FragmentEntryLink fetchByG_P_Last(
		long groupId, long plid,
		com.liferay.portal.kernel.util.OrderByComparator<FragmentEntryLink>
			orderByComparator);

	/**
	 * Returns the fragment entry links before and after the current fragment entry link in the ordered set where groupId = &#63; and plid = &#63;.
	 *
	 * @param fragmentEntryLinkId the primary key of the current fragment entry link
	 * @param groupId the group ID
	 * @param plid the plid
	 * @param orderByComparator the comparator to order the set by (optionally <code>null</code>)
	 * @return the previous, current, and next fragment entry link
	 * @throws NoSuchEntryLinkException if a fragment entry link with the primary key could not be found
	 */
	public FragmentEntryLink[] findByG_P_PrevAndNext(
			long fragmentEntryLinkId, long groupId, long plid,
			com.liferay.portal.kernel.util.OrderByComparator<FragmentEntryLink>
				orderByComparator)
		throws NoSuchEntryLinkException;

	/**
	 * Removes all the fragment entry links where groupId = &#63; and plid = &#63; from the database.
	 *
	 * @param groupId the group ID
	 * @param plid the plid
	 */
	public void removeByG_P(long groupId, long plid);

	/**
	 * Returns the number of fragment entry links where groupId = &#63; and plid = &#63;.
	 *
	 * @param groupId the group ID
	 * @param plid the plid
	 * @return the number of matching fragment entry links
	 */
	public int countByG_P(long groupId, long plid);

	/**
	 * Returns all the fragment entry links where companyId = &#63; and rendererKey = &#63;.
	 *
	 * @param companyId the company ID
	 * @param rendererKey the renderer key
	 * @return the matching fragment entry links
	 */
	public java.util.List<FragmentEntryLink> findByC_R(
		long companyId, String rendererKey);

	/**
	 * Returns a range of all the fragment entry links where companyId = &#63; and rendererKey = &#63;.
	 *
	 * <p>
	 * Useful when paginating results. Returns a maximum of <code>end - start</code> instances. <code>start</code> and <code>end</code> are not primary keys, they are indexes in the result set. Thus, <code>0</code> refers to the first result in the set. Setting both <code>start</code> and <code>end</code> to <code>QueryUtil#ALL_POS</code> will return the full result set. If <code>orderByComparator</code> is specified, then the query will include the given ORDER BY logic. If <code>orderByComparator</code> is absent, then the query will include the default ORDER BY logic from <code>FragmentEntryLinkModelImpl</code>.
	 * </p>
	 *
	 * @param companyId the company ID
	 * @param rendererKey the renderer key
	 * @param start the lower bound of the range of fragment entry links
	 * @param end the upper bound of the range of fragment entry links (not inclusive)
	 * @return the range of matching fragment entry links
	 */
	public java.util.List<FragmentEntryLink> findByC_R(
		long companyId, String rendererKey, int start, int end);

	/**
	 * Returns an ordered range of all the fragment entry links where companyId = &#63; and rendererKey = &#63;.
	 *
	 * <p>
	 * Useful when paginating results. Returns a maximum of <code>end - start</code> instances. <code>start</code> and <code>end</code> are not primary keys, they are indexes in the result set. Thus, <code>0</code> refers to the first result in the set. Setting both <code>start</code> and <code>end</code> to <code>QueryUtil#ALL_POS</code> will return the full result set. If <code>orderByComparator</code> is specified, then the query will include the given ORDER BY logic. If <code>orderByComparator</code> is absent, then the query will include the default ORDER BY logic from <code>FragmentEntryLinkModelImpl</code>.
	 * </p>
	 *
	 * @param companyId the company ID
	 * @param rendererKey the renderer key
	 * @param start the lower bound of the range of fragment entry links
	 * @param end the upper bound of the range of fragment entry links (not inclusive)
	 * @param orderByComparator the comparator to order the results by (optionally <code>null</code>)
	 * @return the ordered range of matching fragment entry links
	 */
	public java.util.List<FragmentEntryLink> findByC_R(
		long companyId, String rendererKey, int start, int end,
		com.liferay.portal.kernel.util.OrderByComparator<FragmentEntryLink>
			orderByComparator);

	/**
	 * Returns an ordered range of all the fragment entry links where companyId = &#63; and rendererKey = &#63;.
	 *
	 * <p>
	 * Useful when paginating results. Returns a maximum of <code>end - start</code> instances. <code>start</code> and <code>end</code> are not primary keys, they are indexes in the result set. Thus, <code>0</code> refers to the first result in the set. Setting both <code>start</code> and <code>end</code> to <code>QueryUtil#ALL_POS</code> will return the full result set. If <code>orderByComparator</code> is specified, then the query will include the given ORDER BY logic. If <code>orderByComparator</code> is absent, then the query will include the default ORDER BY logic from <code>FragmentEntryLinkModelImpl</code>.
	 * </p>
	 *
	 * @param companyId the company ID
	 * @param rendererKey the renderer key
	 * @param start the lower bound of the range of fragment entry links
	 * @param end the upper bound of the range of fragment entry links (not inclusive)
	 * @param orderByComparator the comparator to order the results by (optionally <code>null</code>)
	 * @param useFinderCache whether to use the finder cache
	 * @return the ordered range of matching fragment entry links
	 */
	public java.util.List<FragmentEntryLink> findByC_R(
		long companyId, String rendererKey, int start, int end,
		com.liferay.portal.kernel.util.OrderByComparator<FragmentEntryLink>
			orderByComparator,
		boolean useFinderCache);

	/**
	 * Returns the first fragment entry link in the ordered set where companyId = &#63; and rendererKey = &#63;.
	 *
	 * @param companyId the company ID
	 * @param rendererKey the renderer key
	 * @param orderByComparator the comparator to order the set by (optionally <code>null</code>)
	 * @return the first matching fragment entry link
	 * @throws NoSuchEntryLinkException if a matching fragment entry link could not be found
	 */
	public FragmentEntryLink findByC_R_First(
			long companyId, String rendererKey,
			com.liferay.portal.kernel.util.OrderByComparator<FragmentEntryLink>
				orderByComparator)
		throws NoSuchEntryLinkException;

	/**
	 * Returns the first fragment entry link in the ordered set where companyId = &#63; and rendererKey = &#63;.
	 *
	 * @param companyId the company ID
	 * @param rendererKey the renderer key
	 * @param orderByComparator the comparator to order the set by (optionally <code>null</code>)
	 * @return the first matching fragment entry link, or <code>null</code> if a matching fragment entry link could not be found
	 */
	public FragmentEntryLink fetchByC_R_First(
		long companyId, String rendererKey,
		com.liferay.portal.kernel.util.OrderByComparator<FragmentEntryLink>
			orderByComparator);

	/**
	 * Returns the last fragment entry link in the ordered set where companyId = &#63; and rendererKey = &#63;.
	 *
	 * @param companyId the company ID
	 * @param rendererKey the renderer key
	 * @param orderByComparator the comparator to order the set by (optionally <code>null</code>)
	 * @return the last matching fragment entry link
	 * @throws NoSuchEntryLinkException if a matching fragment entry link could not be found
	 */
	public FragmentEntryLink findByC_R_Last(
			long companyId, String rendererKey,
			com.liferay.portal.kernel.util.OrderByComparator<FragmentEntryLink>
				orderByComparator)
		throws NoSuchEntryLinkException;

	/**
	 * Returns the last fragment entry link in the ordered set where companyId = &#63; and rendererKey = &#63;.
	 *
	 * @param companyId the company ID
	 * @param rendererKey the renderer key
	 * @param orderByComparator the comparator to order the set by (optionally <code>null</code>)
	 * @return the last matching fragment entry link, or <code>null</code> if a matching fragment entry link could not be found
	 */
	public FragmentEntryLink fetchByC_R_Last(
		long companyId, String rendererKey,
		com.liferay.portal.kernel.util.OrderByComparator<FragmentEntryLink>
			orderByComparator);

	/**
	 * Returns the fragment entry links before and after the current fragment entry link in the ordered set where companyId = &#63; and rendererKey = &#63;.
	 *
	 * @param fragmentEntryLinkId the primary key of the current fragment entry link
	 * @param companyId the company ID
	 * @param rendererKey the renderer key
	 * @param orderByComparator the comparator to order the set by (optionally <code>null</code>)
	 * @return the previous, current, and next fragment entry link
	 * @throws NoSuchEntryLinkException if a fragment entry link with the primary key could not be found
	 */
	public FragmentEntryLink[] findByC_R_PrevAndNext(
			long fragmentEntryLinkId, long companyId, String rendererKey,
			com.liferay.portal.kernel.util.OrderByComparator<FragmentEntryLink>
				orderByComparator)
		throws NoSuchEntryLinkException;

	/**
	 * Returns all the fragment entry links where companyId = &#63; and rendererKey = any &#63;.
	 *
	 * <p>
	 * Useful when paginating results. Returns a maximum of <code>end - start</code> instances. <code>start</code> and <code>end</code> are not primary keys, they are indexes in the result set. Thus, <code>0</code> refers to the first result in the set. Setting both <code>start</code> and <code>end</code> to <code>QueryUtil#ALL_POS</code> will return the full result set. If <code>orderByComparator</code> is specified, then the query will include the given ORDER BY logic. If <code>orderByComparator</code> is absent, then the query will include the default ORDER BY logic from <code>FragmentEntryLinkModelImpl</code>.
	 * </p>
	 *
	 * @param companyId the company ID
	 * @param rendererKeys the renderer keys
	 * @return the matching fragment entry links
	 */
	public java.util.List<FragmentEntryLink> findByC_R(
		long companyId, String[] rendererKeys);

	/**
	 * Returns a range of all the fragment entry links where companyId = &#63; and rendererKey = any &#63;.
	 *
	 * <p>
	 * Useful when paginating results. Returns a maximum of <code>end - start</code> instances. <code>start</code> and <code>end</code> are not primary keys, they are indexes in the result set. Thus, <code>0</code> refers to the first result in the set. Setting both <code>start</code> and <code>end</code> to <code>QueryUtil#ALL_POS</code> will return the full result set. If <code>orderByComparator</code> is specified, then the query will include the given ORDER BY logic. If <code>orderByComparator</code> is absent, then the query will include the default ORDER BY logic from <code>FragmentEntryLinkModelImpl</code>.
	 * </p>
	 *
	 * @param companyId the company ID
	 * @param rendererKeys the renderer keys
	 * @param start the lower bound of the range of fragment entry links
	 * @param end the upper bound of the range of fragment entry links (not inclusive)
	 * @return the range of matching fragment entry links
	 */
	public java.util.List<FragmentEntryLink> findByC_R(
		long companyId, String[] rendererKeys, int start, int end);

	/**
	 * Returns an ordered range of all the fragment entry links where companyId = &#63; and rendererKey = any &#63;.
	 *
	 * <p>
	 * Useful when paginating results. Returns a maximum of <code>end - start</code> instances. <code>start</code> and <code>end</code> are not primary keys, they are indexes in the result set. Thus, <code>0</code> refers to the first result in the set. Setting both <code>start</code> and <code>end</code> to <code>QueryUtil#ALL_POS</code> will return the full result set. If <code>orderByComparator</code> is specified, then the query will include the given ORDER BY logic. If <code>orderByComparator</code> is absent, then the query will include the default ORDER BY logic from <code>FragmentEntryLinkModelImpl</code>.
	 * </p>
	 *
	 * @param companyId the company ID
	 * @param rendererKeys the renderer keys
	 * @param start the lower bound of the range of fragment entry links
	 * @param end the upper bound of the range of fragment entry links (not inclusive)
	 * @param orderByComparator the comparator to order the results by (optionally <code>null</code>)
	 * @return the ordered range of matching fragment entry links
	 */
	public java.util.List<FragmentEntryLink> findByC_R(
		long companyId, String[] rendererKeys, int start, int end,
		com.liferay.portal.kernel.util.OrderByComparator<FragmentEntryLink>
			orderByComparator);

	/**
	 * Returns an ordered range of all the fragment entry links where companyId = &#63; and rendererKey = &#63;, optionally using the finder cache.
	 *
	 * <p>
	 * Useful when paginating results. Returns a maximum of <code>end - start</code> instances. <code>start</code> and <code>end</code> are not primary keys, they are indexes in the result set. Thus, <code>0</code> refers to the first result in the set. Setting both <code>start</code> and <code>end</code> to <code>QueryUtil#ALL_POS</code> will return the full result set. If <code>orderByComparator</code> is specified, then the query will include the given ORDER BY logic. If <code>orderByComparator</code> is absent, then the query will include the default ORDER BY logic from <code>FragmentEntryLinkModelImpl</code>.
	 * </p>
	 *
	 * @param companyId the company ID
	 * @param rendererKeys the renderer keys
	 * @param start the lower bound of the range of fragment entry links
	 * @param end the upper bound of the range of fragment entry links (not inclusive)
	 * @param orderByComparator the comparator to order the results by (optionally <code>null</code>)
	 * @param useFinderCache whether to use the finder cache
	 * @return the ordered range of matching fragment entry links
	 */
	public java.util.List<FragmentEntryLink> findByC_R(
		long companyId, String[] rendererKeys, int start, int end,
		com.liferay.portal.kernel.util.OrderByComparator<FragmentEntryLink>
			orderByComparator,
		boolean useFinderCache);

	/**
	 * Removes all the fragment entry links where companyId = &#63; and rendererKey = &#63; from the database.
	 *
	 * @param companyId the company ID
	 * @param rendererKey the renderer key
	 */
	public void removeByC_R(long companyId, String rendererKey);

	/**
	 * Returns the number of fragment entry links where companyId = &#63; and rendererKey = &#63;.
	 *
	 * @param companyId the company ID
	 * @param rendererKey the renderer key
	 * @return the number of matching fragment entry links
	 */
	public int countByC_R(long companyId, String rendererKey);

	/**
	 * Returns the number of fragment entry links where companyId = &#63; and rendererKey = any &#63;.
	 *
	 * @param companyId the company ID
	 * @param rendererKeys the renderer keys
	 * @return the number of matching fragment entry links
	 */
	public int countByC_R(long companyId, String[] rendererKeys);

	/**
	 * Returns all the fragment entry links where fragmentEntryERC = &#63; and fragmentEntryScopeERC = &#63;.
	 *
	 * @param fragmentEntryERC the fragment entry erc
	 * @param fragmentEntryScopeERC the fragment entry scope erc
	 * @return the matching fragment entry links
	 */
	public java.util.List<FragmentEntryLink> findByFEERC_FESERC(
		String fragmentEntryERC, String fragmentEntryScopeERC);

	/**
	 * Returns a range of all the fragment entry links where fragmentEntryERC = &#63; and fragmentEntryScopeERC = &#63;.
	 *
	 * <p>
	 * Useful when paginating results. Returns a maximum of <code>end - start</code> instances. <code>start</code> and <code>end</code> are not primary keys, they are indexes in the result set. Thus, <code>0</code> refers to the first result in the set. Setting both <code>start</code> and <code>end</code> to <code>QueryUtil#ALL_POS</code> will return the full result set. If <code>orderByComparator</code> is specified, then the query will include the given ORDER BY logic. If <code>orderByComparator</code> is absent, then the query will include the default ORDER BY logic from <code>FragmentEntryLinkModelImpl</code>.
	 * </p>
	 *
	 * @param fragmentEntryERC the fragment entry erc
	 * @param fragmentEntryScopeERC the fragment entry scope erc
	 * @param start the lower bound of the range of fragment entry links
	 * @param end the upper bound of the range of fragment entry links (not inclusive)
	 * @return the range of matching fragment entry links
	 */
	public java.util.List<FragmentEntryLink> findByFEERC_FESERC(
		String fragmentEntryERC, String fragmentEntryScopeERC, int start,
		int end);

	/**
	 * Returns an ordered range of all the fragment entry links where fragmentEntryERC = &#63; and fragmentEntryScopeERC = &#63;.
	 *
	 * <p>
	 * Useful when paginating results. Returns a maximum of <code>end - start</code> instances. <code>start</code> and <code>end</code> are not primary keys, they are indexes in the result set. Thus, <code>0</code> refers to the first result in the set. Setting both <code>start</code> and <code>end</code> to <code>QueryUtil#ALL_POS</code> will return the full result set. If <code>orderByComparator</code> is specified, then the query will include the given ORDER BY logic. If <code>orderByComparator</code> is absent, then the query will include the default ORDER BY logic from <code>FragmentEntryLinkModelImpl</code>.
	 * </p>
	 *
	 * @param fragmentEntryERC the fragment entry erc
	 * @param fragmentEntryScopeERC the fragment entry scope erc
	 * @param start the lower bound of the range of fragment entry links
	 * @param end the upper bound of the range of fragment entry links (not inclusive)
	 * @param orderByComparator the comparator to order the results by (optionally <code>null</code>)
	 * @return the ordered range of matching fragment entry links
	 */
	public java.util.List<FragmentEntryLink> findByFEERC_FESERC(
		String fragmentEntryERC, String fragmentEntryScopeERC, int start,
		int end,
		com.liferay.portal.kernel.util.OrderByComparator<FragmentEntryLink>
			orderByComparator);

	/**
	 * Returns an ordered range of all the fragment entry links where fragmentEntryERC = &#63; and fragmentEntryScopeERC = &#63;.
	 *
	 * <p>
	 * Useful when paginating results. Returns a maximum of <code>end - start</code> instances. <code>start</code> and <code>end</code> are not primary keys, they are indexes in the result set. Thus, <code>0</code> refers to the first result in the set. Setting both <code>start</code> and <code>end</code> to <code>QueryUtil#ALL_POS</code> will return the full result set. If <code>orderByComparator</code> is specified, then the query will include the given ORDER BY logic. If <code>orderByComparator</code> is absent, then the query will include the default ORDER BY logic from <code>FragmentEntryLinkModelImpl</code>.
	 * </p>
	 *
	 * @param fragmentEntryERC the fragment entry erc
	 * @param fragmentEntryScopeERC the fragment entry scope erc
	 * @param start the lower bound of the range of fragment entry links
	 * @param end the upper bound of the range of fragment entry links (not inclusive)
	 * @param orderByComparator the comparator to order the results by (optionally <code>null</code>)
	 * @param useFinderCache whether to use the finder cache
	 * @return the ordered range of matching fragment entry links
	 */
	public java.util.List<FragmentEntryLink> findByFEERC_FESERC(
		String fragmentEntryERC, String fragmentEntryScopeERC, int start,
		int end,
		com.liferay.portal.kernel.util.OrderByComparator<FragmentEntryLink>
			orderByComparator,
		boolean useFinderCache);

	/**
	 * Returns the first fragment entry link in the ordered set where fragmentEntryERC = &#63; and fragmentEntryScopeERC = &#63;.
	 *
	 * @param fragmentEntryERC the fragment entry erc
	 * @param fragmentEntryScopeERC the fragment entry scope erc
	 * @param orderByComparator the comparator to order the set by (optionally <code>null</code>)
	 * @return the first matching fragment entry link
	 * @throws NoSuchEntryLinkException if a matching fragment entry link could not be found
	 */
	public FragmentEntryLink findByFEERC_FESERC_First(
			String fragmentEntryERC, String fragmentEntryScopeERC,
			com.liferay.portal.kernel.util.OrderByComparator<FragmentEntryLink>
				orderByComparator)
		throws NoSuchEntryLinkException;

	/**
	 * Returns the first fragment entry link in the ordered set where fragmentEntryERC = &#63; and fragmentEntryScopeERC = &#63;.
	 *
	 * @param fragmentEntryERC the fragment entry erc
	 * @param fragmentEntryScopeERC the fragment entry scope erc
	 * @param orderByComparator the comparator to order the set by (optionally <code>null</code>)
	 * @return the first matching fragment entry link, or <code>null</code> if a matching fragment entry link could not be found
	 */
	public FragmentEntryLink fetchByFEERC_FESERC_First(
		String fragmentEntryERC, String fragmentEntryScopeERC,
		com.liferay.portal.kernel.util.OrderByComparator<FragmentEntryLink>
			orderByComparator);

	/**
	 * Returns the last fragment entry link in the ordered set where fragmentEntryERC = &#63; and fragmentEntryScopeERC = &#63;.
	 *
	 * @param fragmentEntryERC the fragment entry erc
	 * @param fragmentEntryScopeERC the fragment entry scope erc
	 * @param orderByComparator the comparator to order the set by (optionally <code>null</code>)
	 * @return the last matching fragment entry link
	 * @throws NoSuchEntryLinkException if a matching fragment entry link could not be found
	 */
	public FragmentEntryLink findByFEERC_FESERC_Last(
			String fragmentEntryERC, String fragmentEntryScopeERC,
			com.liferay.portal.kernel.util.OrderByComparator<FragmentEntryLink>
				orderByComparator)
		throws NoSuchEntryLinkException;

	/**
	 * Returns the last fragment entry link in the ordered set where fragmentEntryERC = &#63; and fragmentEntryScopeERC = &#63;.
	 *
	 * @param fragmentEntryERC the fragment entry erc
	 * @param fragmentEntryScopeERC the fragment entry scope erc
	 * @param orderByComparator the comparator to order the set by (optionally <code>null</code>)
	 * @return the last matching fragment entry link, or <code>null</code> if a matching fragment entry link could not be found
	 */
	public FragmentEntryLink fetchByFEERC_FESERC_Last(
		String fragmentEntryERC, String fragmentEntryScopeERC,
		com.liferay.portal.kernel.util.OrderByComparator<FragmentEntryLink>
			orderByComparator);

	/**
	 * Returns the fragment entry links before and after the current fragment entry link in the ordered set where fragmentEntryERC = &#63; and fragmentEntryScopeERC = &#63;.
	 *
	 * @param fragmentEntryLinkId the primary key of the current fragment entry link
	 * @param fragmentEntryERC the fragment entry erc
	 * @param fragmentEntryScopeERC the fragment entry scope erc
	 * @param orderByComparator the comparator to order the set by (optionally <code>null</code>)
	 * @return the previous, current, and next fragment entry link
	 * @throws NoSuchEntryLinkException if a fragment entry link with the primary key could not be found
	 */
	public FragmentEntryLink[] findByFEERC_FESERC_PrevAndNext(
			long fragmentEntryLinkId, String fragmentEntryERC,
			String fragmentEntryScopeERC,
			com.liferay.portal.kernel.util.OrderByComparator<FragmentEntryLink>
				orderByComparator)
		throws NoSuchEntryLinkException;

	/**
	 * Removes all the fragment entry links where fragmentEntryERC = &#63; and fragmentEntryScopeERC = &#63; from the database.
	 *
	 * @param fragmentEntryERC the fragment entry erc
	 * @param fragmentEntryScopeERC the fragment entry scope erc
	 */
	public void removeByFEERC_FESERC(
		String fragmentEntryERC, String fragmentEntryScopeERC);

	/**
	 * Returns the number of fragment entry links where fragmentEntryERC = &#63; and fragmentEntryScopeERC = &#63;.
	 *
	 * @param fragmentEntryERC the fragment entry erc
	 * @param fragmentEntryScopeERC the fragment entry scope erc
	 * @return the number of matching fragment entry links
	 */
	public int countByFEERC_FESERC(
		String fragmentEntryERC, String fragmentEntryScopeERC);

	/**
	 * Returns all the fragment entry links where groupId = &#63; and originalFragmentEntryLinkERC = &#63; and plid = &#63;.
	 *
	 * @param groupId the group ID
	 * @param originalFragmentEntryLinkERC the original fragment entry link erc
	 * @param plid the plid
	 * @return the matching fragment entry links
	 */
	public java.util.List<FragmentEntryLink> findByG_OFELERC_P(
		long groupId, String originalFragmentEntryLinkERC, long plid);

	/**
	 * Returns a range of all the fragment entry links where groupId = &#63; and originalFragmentEntryLinkERC = &#63; and plid = &#63;.
	 *
	 * <p>
	 * Useful when paginating results. Returns a maximum of <code>end - start</code> instances. <code>start</code> and <code>end</code> are not primary keys, they are indexes in the result set. Thus, <code>0</code> refers to the first result in the set. Setting both <code>start</code> and <code>end</code> to <code>QueryUtil#ALL_POS</code> will return the full result set. If <code>orderByComparator</code> is specified, then the query will include the given ORDER BY logic. If <code>orderByComparator</code> is absent, then the query will include the default ORDER BY logic from <code>FragmentEntryLinkModelImpl</code>.
	 * </p>
	 *
	 * @param groupId the group ID
	 * @param originalFragmentEntryLinkERC the original fragment entry link erc
	 * @param plid the plid
	 * @param start the lower bound of the range of fragment entry links
	 * @param end the upper bound of the range of fragment entry links (not inclusive)
	 * @return the range of matching fragment entry links
	 */
	public java.util.List<FragmentEntryLink> findByG_OFELERC_P(
		long groupId, String originalFragmentEntryLinkERC, long plid, int start,
		int end);

	/**
	 * Returns an ordered range of all the fragment entry links where groupId = &#63; and originalFragmentEntryLinkERC = &#63; and plid = &#63;.
	 *
	 * <p>
	 * Useful when paginating results. Returns a maximum of <code>end - start</code> instances. <code>start</code> and <code>end</code> are not primary keys, they are indexes in the result set. Thus, <code>0</code> refers to the first result in the set. Setting both <code>start</code> and <code>end</code> to <code>QueryUtil#ALL_POS</code> will return the full result set. If <code>orderByComparator</code> is specified, then the query will include the given ORDER BY logic. If <code>orderByComparator</code> is absent, then the query will include the default ORDER BY logic from <code>FragmentEntryLinkModelImpl</code>.
	 * </p>
	 *
	 * @param groupId the group ID
	 * @param originalFragmentEntryLinkERC the original fragment entry link erc
	 * @param plid the plid
	 * @param start the lower bound of the range of fragment entry links
	 * @param end the upper bound of the range of fragment entry links (not inclusive)
	 * @param orderByComparator the comparator to order the results by (optionally <code>null</code>)
	 * @return the ordered range of matching fragment entry links
	 */
	public java.util.List<FragmentEntryLink> findByG_OFELERC_P(
		long groupId, String originalFragmentEntryLinkERC, long plid, int start,
		int end,
		com.liferay.portal.kernel.util.OrderByComparator<FragmentEntryLink>
			orderByComparator);

	/**
	 * Returns an ordered range of all the fragment entry links where groupId = &#63; and originalFragmentEntryLinkERC = &#63; and plid = &#63;.
	 *
	 * <p>
	 * Useful when paginating results. Returns a maximum of <code>end - start</code> instances. <code>start</code> and <code>end</code> are not primary keys, they are indexes in the result set. Thus, <code>0</code> refers to the first result in the set. Setting both <code>start</code> and <code>end</code> to <code>QueryUtil#ALL_POS</code> will return the full result set. If <code>orderByComparator</code> is specified, then the query will include the given ORDER BY logic. If <code>orderByComparator</code> is absent, then the query will include the default ORDER BY logic from <code>FragmentEntryLinkModelImpl</code>.
	 * </p>
	 *
	 * @param groupId the group ID
	 * @param originalFragmentEntryLinkERC the original fragment entry link erc
	 * @param plid the plid
	 * @param start the lower bound of the range of fragment entry links
	 * @param end the upper bound of the range of fragment entry links (not inclusive)
	 * @param orderByComparator the comparator to order the results by (optionally <code>null</code>)
	 * @param useFinderCache whether to use the finder cache
	 * @return the ordered range of matching fragment entry links
	 */
	public java.util.List<FragmentEntryLink> findByG_OFELERC_P(
		long groupId, String originalFragmentEntryLinkERC, long plid, int start,
		int end,
		com.liferay.portal.kernel.util.OrderByComparator<FragmentEntryLink>
			orderByComparator,
		boolean useFinderCache);

	/**
	 * Returns the first fragment entry link in the ordered set where groupId = &#63; and originalFragmentEntryLinkERC = &#63; and plid = &#63;.
	 *
	 * @param groupId the group ID
	 * @param originalFragmentEntryLinkERC the original fragment entry link erc
	 * @param plid the plid
	 * @param orderByComparator the comparator to order the set by (optionally <code>null</code>)
	 * @return the first matching fragment entry link
	 * @throws NoSuchEntryLinkException if a matching fragment entry link could not be found
	 */
	public FragmentEntryLink findByG_OFELERC_P_First(
			long groupId, String originalFragmentEntryLinkERC, long plid,
			com.liferay.portal.kernel.util.OrderByComparator<FragmentEntryLink>
				orderByComparator)
		throws NoSuchEntryLinkException;

	/**
	 * Returns the first fragment entry link in the ordered set where groupId = &#63; and originalFragmentEntryLinkERC = &#63; and plid = &#63;.
	 *
	 * @param groupId the group ID
	 * @param originalFragmentEntryLinkERC the original fragment entry link erc
	 * @param plid the plid
	 * @param orderByComparator the comparator to order the set by (optionally <code>null</code>)
	 * @return the first matching fragment entry link, or <code>null</code> if a matching fragment entry link could not be found
	 */
	public FragmentEntryLink fetchByG_OFELERC_P_First(
		long groupId, String originalFragmentEntryLinkERC, long plid,
		com.liferay.portal.kernel.util.OrderByComparator<FragmentEntryLink>
			orderByComparator);

	/**
	 * Returns the last fragment entry link in the ordered set where groupId = &#63; and originalFragmentEntryLinkERC = &#63; and plid = &#63;.
	 *
	 * @param groupId the group ID
	 * @param originalFragmentEntryLinkERC the original fragment entry link erc
	 * @param plid the plid
	 * @param orderByComparator the comparator to order the set by (optionally <code>null</code>)
	 * @return the last matching fragment entry link
	 * @throws NoSuchEntryLinkException if a matching fragment entry link could not be found
	 */
	public FragmentEntryLink findByG_OFELERC_P_Last(
			long groupId, String originalFragmentEntryLinkERC, long plid,
			com.liferay.portal.kernel.util.OrderByComparator<FragmentEntryLink>
				orderByComparator)
		throws NoSuchEntryLinkException;

	/**
	 * Returns the last fragment entry link in the ordered set where groupId = &#63; and originalFragmentEntryLinkERC = &#63; and plid = &#63;.
	 *
	 * @param groupId the group ID
	 * @param originalFragmentEntryLinkERC the original fragment entry link erc
	 * @param plid the plid
	 * @param orderByComparator the comparator to order the set by (optionally <code>null</code>)
	 * @return the last matching fragment entry link, or <code>null</code> if a matching fragment entry link could not be found
	 */
	public FragmentEntryLink fetchByG_OFELERC_P_Last(
		long groupId, String originalFragmentEntryLinkERC, long plid,
		com.liferay.portal.kernel.util.OrderByComparator<FragmentEntryLink>
			orderByComparator);

	/**
	 * Returns the fragment entry links before and after the current fragment entry link in the ordered set where groupId = &#63; and originalFragmentEntryLinkERC = &#63; and plid = &#63;.
	 *
	 * @param fragmentEntryLinkId the primary key of the current fragment entry link
	 * @param groupId the group ID
	 * @param originalFragmentEntryLinkERC the original fragment entry link erc
	 * @param plid the plid
	 * @param orderByComparator the comparator to order the set by (optionally <code>null</code>)
	 * @return the previous, current, and next fragment entry link
	 * @throws NoSuchEntryLinkException if a fragment entry link with the primary key could not be found
	 */
	public FragmentEntryLink[] findByG_OFELERC_P_PrevAndNext(
			long fragmentEntryLinkId, long groupId,
			String originalFragmentEntryLinkERC, long plid,
			com.liferay.portal.kernel.util.OrderByComparator<FragmentEntryLink>
				orderByComparator)
		throws NoSuchEntryLinkException;

	/**
	 * Removes all the fragment entry links where groupId = &#63; and originalFragmentEntryLinkERC = &#63; and plid = &#63; from the database.
	 *
	 * @param groupId the group ID
	 * @param originalFragmentEntryLinkERC the original fragment entry link erc
	 * @param plid the plid
	 */
	public void removeByG_OFELERC_P(
		long groupId, String originalFragmentEntryLinkERC, long plid);

	/**
	 * Returns the number of fragment entry links where groupId = &#63; and originalFragmentEntryLinkERC = &#63; and plid = &#63;.
	 *
	 * @param groupId the group ID
	 * @param originalFragmentEntryLinkERC the original fragment entry link erc
	 * @param plid the plid
	 * @return the number of matching fragment entry links
	 */
	public int countByG_OFELERC_P(
		long groupId, String originalFragmentEntryLinkERC, long plid);

	/**
	 * Returns all the fragment entry links where groupId = &#63; and fragmentEntryERC = &#63; and fragmentEntryScopeERC = &#63;.
	 *
	 * @param groupId the group ID
	 * @param fragmentEntryERC the fragment entry erc
	 * @param fragmentEntryScopeERC the fragment entry scope erc
	 * @return the matching fragment entry links
	 */
	public java.util.List<FragmentEntryLink> findByG_FEERC_FESERC(
		long groupId, String fragmentEntryERC, String fragmentEntryScopeERC);

	/**
	 * Returns a range of all the fragment entry links where groupId = &#63; and fragmentEntryERC = &#63; and fragmentEntryScopeERC = &#63;.
	 *
	 * <p>
	 * Useful when paginating results. Returns a maximum of <code>end - start</code> instances. <code>start</code> and <code>end</code> are not primary keys, they are indexes in the result set. Thus, <code>0</code> refers to the first result in the set. Setting both <code>start</code> and <code>end</code> to <code>QueryUtil#ALL_POS</code> will return the full result set. If <code>orderByComparator</code> is specified, then the query will include the given ORDER BY logic. If <code>orderByComparator</code> is absent, then the query will include the default ORDER BY logic from <code>FragmentEntryLinkModelImpl</code>.
	 * </p>
	 *
	 * @param groupId the group ID
	 * @param fragmentEntryERC the fragment entry erc
	 * @param fragmentEntryScopeERC the fragment entry scope erc
	 * @param start the lower bound of the range of fragment entry links
	 * @param end the upper bound of the range of fragment entry links (not inclusive)
	 * @return the range of matching fragment entry links
	 */
	public java.util.List<FragmentEntryLink> findByG_FEERC_FESERC(
		long groupId, String fragmentEntryERC, String fragmentEntryScopeERC,
		int start, int end);

	/**
	 * Returns an ordered range of all the fragment entry links where groupId = &#63; and fragmentEntryERC = &#63; and fragmentEntryScopeERC = &#63;.
	 *
	 * <p>
	 * Useful when paginating results. Returns a maximum of <code>end - start</code> instances. <code>start</code> and <code>end</code> are not primary keys, they are indexes in the result set. Thus, <code>0</code> refers to the first result in the set. Setting both <code>start</code> and <code>end</code> to <code>QueryUtil#ALL_POS</code> will return the full result set. If <code>orderByComparator</code> is specified, then the query will include the given ORDER BY logic. If <code>orderByComparator</code> is absent, then the query will include the default ORDER BY logic from <code>FragmentEntryLinkModelImpl</code>.
	 * </p>
	 *
	 * @param groupId the group ID
	 * @param fragmentEntryERC the fragment entry erc
	 * @param fragmentEntryScopeERC the fragment entry scope erc
	 * @param start the lower bound of the range of fragment entry links
	 * @param end the upper bound of the range of fragment entry links (not inclusive)
	 * @param orderByComparator the comparator to order the results by (optionally <code>null</code>)
	 * @return the ordered range of matching fragment entry links
	 */
	public java.util.List<FragmentEntryLink> findByG_FEERC_FESERC(
		long groupId, String fragmentEntryERC, String fragmentEntryScopeERC,
		int start, int end,
		com.liferay.portal.kernel.util.OrderByComparator<FragmentEntryLink>
			orderByComparator);

	/**
	 * Returns an ordered range of all the fragment entry links where groupId = &#63; and fragmentEntryERC = &#63; and fragmentEntryScopeERC = &#63;.
	 *
	 * <p>
	 * Useful when paginating results. Returns a maximum of <code>end - start</code> instances. <code>start</code> and <code>end</code> are not primary keys, they are indexes in the result set. Thus, <code>0</code> refers to the first result in the set. Setting both <code>start</code> and <code>end</code> to <code>QueryUtil#ALL_POS</code> will return the full result set. If <code>orderByComparator</code> is specified, then the query will include the given ORDER BY logic. If <code>orderByComparator</code> is absent, then the query will include the default ORDER BY logic from <code>FragmentEntryLinkModelImpl</code>.
	 * </p>
	 *
	 * @param groupId the group ID
	 * @param fragmentEntryERC the fragment entry erc
	 * @param fragmentEntryScopeERC the fragment entry scope erc
	 * @param start the lower bound of the range of fragment entry links
	 * @param end the upper bound of the range of fragment entry links (not inclusive)
	 * @param orderByComparator the comparator to order the results by (optionally <code>null</code>)
	 * @param useFinderCache whether to use the finder cache
	 * @return the ordered range of matching fragment entry links
	 */
	public java.util.List<FragmentEntryLink> findByG_FEERC_FESERC(
		long groupId, String fragmentEntryERC, String fragmentEntryScopeERC,
		int start, int end,
		com.liferay.portal.kernel.util.OrderByComparator<FragmentEntryLink>
			orderByComparator,
		boolean useFinderCache);

	/**
	 * Returns the first fragment entry link in the ordered set where groupId = &#63; and fragmentEntryERC = &#63; and fragmentEntryScopeERC = &#63;.
	 *
	 * @param groupId the group ID
	 * @param fragmentEntryERC the fragment entry erc
	 * @param fragmentEntryScopeERC the fragment entry scope erc
	 * @param orderByComparator the comparator to order the set by (optionally <code>null</code>)
	 * @return the first matching fragment entry link
	 * @throws NoSuchEntryLinkException if a matching fragment entry link could not be found
	 */
	public FragmentEntryLink findByG_FEERC_FESERC_First(
			long groupId, String fragmentEntryERC, String fragmentEntryScopeERC,
			com.liferay.portal.kernel.util.OrderByComparator<FragmentEntryLink>
				orderByComparator)
		throws NoSuchEntryLinkException;

	/**
	 * Returns the first fragment entry link in the ordered set where groupId = &#63; and fragmentEntryERC = &#63; and fragmentEntryScopeERC = &#63;.
	 *
	 * @param groupId the group ID
	 * @param fragmentEntryERC the fragment entry erc
	 * @param fragmentEntryScopeERC the fragment entry scope erc
	 * @param orderByComparator the comparator to order the set by (optionally <code>null</code>)
	 * @return the first matching fragment entry link, or <code>null</code> if a matching fragment entry link could not be found
	 */
	public FragmentEntryLink fetchByG_FEERC_FESERC_First(
		long groupId, String fragmentEntryERC, String fragmentEntryScopeERC,
		com.liferay.portal.kernel.util.OrderByComparator<FragmentEntryLink>
			orderByComparator);

	/**
	 * Returns the last fragment entry link in the ordered set where groupId = &#63; and fragmentEntryERC = &#63; and fragmentEntryScopeERC = &#63;.
	 *
	 * @param groupId the group ID
	 * @param fragmentEntryERC the fragment entry erc
	 * @param fragmentEntryScopeERC the fragment entry scope erc
	 * @param orderByComparator the comparator to order the set by (optionally <code>null</code>)
	 * @return the last matching fragment entry link
	 * @throws NoSuchEntryLinkException if a matching fragment entry link could not be found
	 */
	public FragmentEntryLink findByG_FEERC_FESERC_Last(
			long groupId, String fragmentEntryERC, String fragmentEntryScopeERC,
			com.liferay.portal.kernel.util.OrderByComparator<FragmentEntryLink>
				orderByComparator)
		throws NoSuchEntryLinkException;

	/**
	 * Returns the last fragment entry link in the ordered set where groupId = &#63; and fragmentEntryERC = &#63; and fragmentEntryScopeERC = &#63;.
	 *
	 * @param groupId the group ID
	 * @param fragmentEntryERC the fragment entry erc
	 * @param fragmentEntryScopeERC the fragment entry scope erc
	 * @param orderByComparator the comparator to order the set by (optionally <code>null</code>)
	 * @return the last matching fragment entry link, or <code>null</code> if a matching fragment entry link could not be found
	 */
	public FragmentEntryLink fetchByG_FEERC_FESERC_Last(
		long groupId, String fragmentEntryERC, String fragmentEntryScopeERC,
		com.liferay.portal.kernel.util.OrderByComparator<FragmentEntryLink>
			orderByComparator);

	/**
	 * Returns the fragment entry links before and after the current fragment entry link in the ordered set where groupId = &#63; and fragmentEntryERC = &#63; and fragmentEntryScopeERC = &#63;.
	 *
	 * @param fragmentEntryLinkId the primary key of the current fragment entry link
	 * @param groupId the group ID
	 * @param fragmentEntryERC the fragment entry erc
	 * @param fragmentEntryScopeERC the fragment entry scope erc
	 * @param orderByComparator the comparator to order the set by (optionally <code>null</code>)
	 * @return the previous, current, and next fragment entry link
	 * @throws NoSuchEntryLinkException if a fragment entry link with the primary key could not be found
	 */
	public FragmentEntryLink[] findByG_FEERC_FESERC_PrevAndNext(
			long fragmentEntryLinkId, long groupId, String fragmentEntryERC,
			String fragmentEntryScopeERC,
			com.liferay.portal.kernel.util.OrderByComparator<FragmentEntryLink>
				orderByComparator)
		throws NoSuchEntryLinkException;

	/**
	 * Removes all the fragment entry links where groupId = &#63; and fragmentEntryERC = &#63; and fragmentEntryScopeERC = &#63; from the database.
	 *
	 * @param groupId the group ID
	 * @param fragmentEntryERC the fragment entry erc
	 * @param fragmentEntryScopeERC the fragment entry scope erc
	 */
	public void removeByG_FEERC_FESERC(
		long groupId, String fragmentEntryERC, String fragmentEntryScopeERC);

	/**
	 * Returns the number of fragment entry links where groupId = &#63; and fragmentEntryERC = &#63; and fragmentEntryScopeERC = &#63;.
	 *
	 * @param groupId the group ID
	 * @param fragmentEntryERC the fragment entry erc
	 * @param fragmentEntryScopeERC the fragment entry scope erc
	 * @return the number of matching fragment entry links
	 */
	public int countByG_FEERC_FESERC(
		long groupId, String fragmentEntryERC, String fragmentEntryScopeERC);

	/**
	 * Returns all the fragment entry links where groupId = &#63; and segmentsExperienceId = &#63; and plid = &#63;.
	 *
	 * @param groupId the group ID
	 * @param segmentsExperienceId the segments experience ID
	 * @param plid the plid
	 * @return the matching fragment entry links
	 */
	public java.util.List<FragmentEntryLink> findByG_S_P(
		long groupId, long segmentsExperienceId, long plid);

	/**
	 * Returns a range of all the fragment entry links where groupId = &#63; and segmentsExperienceId = &#63; and plid = &#63;.
	 *
	 * <p>
	 * Useful when paginating results. Returns a maximum of <code>end - start</code> instances. <code>start</code> and <code>end</code> are not primary keys, they are indexes in the result set. Thus, <code>0</code> refers to the first result in the set. Setting both <code>start</code> and <code>end</code> to <code>QueryUtil#ALL_POS</code> will return the full result set. If <code>orderByComparator</code> is specified, then the query will include the given ORDER BY logic. If <code>orderByComparator</code> is absent, then the query will include the default ORDER BY logic from <code>FragmentEntryLinkModelImpl</code>.
	 * </p>
	 *
	 * @param groupId the group ID
	 * @param segmentsExperienceId the segments experience ID
	 * @param plid the plid
	 * @param start the lower bound of the range of fragment entry links
	 * @param end the upper bound of the range of fragment entry links (not inclusive)
	 * @return the range of matching fragment entry links
	 */
	public java.util.List<FragmentEntryLink> findByG_S_P(
		long groupId, long segmentsExperienceId, long plid, int start, int end);

	/**
	 * Returns an ordered range of all the fragment entry links where groupId = &#63; and segmentsExperienceId = &#63; and plid = &#63;.
	 *
	 * <p>
	 * Useful when paginating results. Returns a maximum of <code>end - start</code> instances. <code>start</code> and <code>end</code> are not primary keys, they are indexes in the result set. Thus, <code>0</code> refers to the first result in the set. Setting both <code>start</code> and <code>end</code> to <code>QueryUtil#ALL_POS</code> will return the full result set. If <code>orderByComparator</code> is specified, then the query will include the given ORDER BY logic. If <code>orderByComparator</code> is absent, then the query will include the default ORDER BY logic from <code>FragmentEntryLinkModelImpl</code>.
	 * </p>
	 *
	 * @param groupId the group ID
	 * @param segmentsExperienceId the segments experience ID
	 * @param plid the plid
	 * @param start the lower bound of the range of fragment entry links
	 * @param end the upper bound of the range of fragment entry links (not inclusive)
	 * @param orderByComparator the comparator to order the results by (optionally <code>null</code>)
	 * @return the ordered range of matching fragment entry links
	 */
	public java.util.List<FragmentEntryLink> findByG_S_P(
		long groupId, long segmentsExperienceId, long plid, int start, int end,
		com.liferay.portal.kernel.util.OrderByComparator<FragmentEntryLink>
			orderByComparator);

	/**
	 * Returns an ordered range of all the fragment entry links where groupId = &#63; and segmentsExperienceId = &#63; and plid = &#63;.
	 *
	 * <p>
	 * Useful when paginating results. Returns a maximum of <code>end - start</code> instances. <code>start</code> and <code>end</code> are not primary keys, they are indexes in the result set. Thus, <code>0</code> refers to the first result in the set. Setting both <code>start</code> and <code>end</code> to <code>QueryUtil#ALL_POS</code> will return the full result set. If <code>orderByComparator</code> is specified, then the query will include the given ORDER BY logic. If <code>orderByComparator</code> is absent, then the query will include the default ORDER BY logic from <code>FragmentEntryLinkModelImpl</code>.
	 * </p>
	 *
	 * @param groupId the group ID
	 * @param segmentsExperienceId the segments experience ID
	 * @param plid the plid
	 * @param start the lower bound of the range of fragment entry links
	 * @param end the upper bound of the range of fragment entry links (not inclusive)
	 * @param orderByComparator the comparator to order the results by (optionally <code>null</code>)
	 * @param useFinderCache whether to use the finder cache
	 * @return the ordered range of matching fragment entry links
	 */
	public java.util.List<FragmentEntryLink> findByG_S_P(
		long groupId, long segmentsExperienceId, long plid, int start, int end,
		com.liferay.portal.kernel.util.OrderByComparator<FragmentEntryLink>
			orderByComparator,
		boolean useFinderCache);

	/**
	 * Returns the first fragment entry link in the ordered set where groupId = &#63; and segmentsExperienceId = &#63; and plid = &#63;.
	 *
	 * @param groupId the group ID
	 * @param segmentsExperienceId the segments experience ID
	 * @param plid the plid
	 * @param orderByComparator the comparator to order the set by (optionally <code>null</code>)
	 * @return the first matching fragment entry link
	 * @throws NoSuchEntryLinkException if a matching fragment entry link could not be found
	 */
	public FragmentEntryLink findByG_S_P_First(
			long groupId, long segmentsExperienceId, long plid,
			com.liferay.portal.kernel.util.OrderByComparator<FragmentEntryLink>
				orderByComparator)
		throws NoSuchEntryLinkException;

	/**
	 * Returns the first fragment entry link in the ordered set where groupId = &#63; and segmentsExperienceId = &#63; and plid = &#63;.
	 *
	 * @param groupId the group ID
	 * @param segmentsExperienceId the segments experience ID
	 * @param plid the plid
	 * @param orderByComparator the comparator to order the set by (optionally <code>null</code>)
	 * @return the first matching fragment entry link, or <code>null</code> if a matching fragment entry link could not be found
	 */
	public FragmentEntryLink fetchByG_S_P_First(
		long groupId, long segmentsExperienceId, long plid,
		com.liferay.portal.kernel.util.OrderByComparator<FragmentEntryLink>
			orderByComparator);

	/**
	 * Returns the last fragment entry link in the ordered set where groupId = &#63; and segmentsExperienceId = &#63; and plid = &#63;.
	 *
	 * @param groupId the group ID
	 * @param segmentsExperienceId the segments experience ID
	 * @param plid the plid
	 * @param orderByComparator the comparator to order the set by (optionally <code>null</code>)
	 * @return the last matching fragment entry link
	 * @throws NoSuchEntryLinkException if a matching fragment entry link could not be found
	 */
	public FragmentEntryLink findByG_S_P_Last(
			long groupId, long segmentsExperienceId, long plid,
			com.liferay.portal.kernel.util.OrderByComparator<FragmentEntryLink>
				orderByComparator)
		throws NoSuchEntryLinkException;

	/**
	 * Returns the last fragment entry link in the ordered set where groupId = &#63; and segmentsExperienceId = &#63; and plid = &#63;.
	 *
	 * @param groupId the group ID
	 * @param segmentsExperienceId the segments experience ID
	 * @param plid the plid
	 * @param orderByComparator the comparator to order the set by (optionally <code>null</code>)
	 * @return the last matching fragment entry link, or <code>null</code> if a matching fragment entry link could not be found
	 */
	public FragmentEntryLink fetchByG_S_P_Last(
		long groupId, long segmentsExperienceId, long plid,
		com.liferay.portal.kernel.util.OrderByComparator<FragmentEntryLink>
			orderByComparator);

	/**
	 * Returns the fragment entry links before and after the current fragment entry link in the ordered set where groupId = &#63; and segmentsExperienceId = &#63; and plid = &#63;.
	 *
	 * @param fragmentEntryLinkId the primary key of the current fragment entry link
	 * @param groupId the group ID
	 * @param segmentsExperienceId the segments experience ID
	 * @param plid the plid
	 * @param orderByComparator the comparator to order the set by (optionally <code>null</code>)
	 * @return the previous, current, and next fragment entry link
	 * @throws NoSuchEntryLinkException if a fragment entry link with the primary key could not be found
	 */
	public FragmentEntryLink[] findByG_S_P_PrevAndNext(
			long fragmentEntryLinkId, long groupId, long segmentsExperienceId,
			long plid,
			com.liferay.portal.kernel.util.OrderByComparator<FragmentEntryLink>
				orderByComparator)
		throws NoSuchEntryLinkException;

	/**
	 * Returns all the fragment entry links where groupId = &#63; and segmentsExperienceId = any &#63; and plid = &#63;.
	 *
	 * <p>
	 * Useful when paginating results. Returns a maximum of <code>end - start</code> instances. <code>start</code> and <code>end</code> are not primary keys, they are indexes in the result set. Thus, <code>0</code> refers to the first result in the set. Setting both <code>start</code> and <code>end</code> to <code>QueryUtil#ALL_POS</code> will return the full result set. If <code>orderByComparator</code> is specified, then the query will include the given ORDER BY logic. If <code>orderByComparator</code> is absent, then the query will include the default ORDER BY logic from <code>FragmentEntryLinkModelImpl</code>.
	 * </p>
	 *
	 * @param groupId the group ID
	 * @param segmentsExperienceIds the segments experience IDs
	 * @param plid the plid
	 * @return the matching fragment entry links
	 */
	public java.util.List<FragmentEntryLink> findByG_S_P(
		long groupId, long[] segmentsExperienceIds, long plid);

	/**
	 * Returns a range of all the fragment entry links where groupId = &#63; and segmentsExperienceId = any &#63; and plid = &#63;.
	 *
	 * <p>
	 * Useful when paginating results. Returns a maximum of <code>end - start</code> instances. <code>start</code> and <code>end</code> are not primary keys, they are indexes in the result set. Thus, <code>0</code> refers to the first result in the set. Setting both <code>start</code> and <code>end</code> to <code>QueryUtil#ALL_POS</code> will return the full result set. If <code>orderByComparator</code> is specified, then the query will include the given ORDER BY logic. If <code>orderByComparator</code> is absent, then the query will include the default ORDER BY logic from <code>FragmentEntryLinkModelImpl</code>.
	 * </p>
	 *
	 * @param groupId the group ID
	 * @param segmentsExperienceIds the segments experience IDs
	 * @param plid the plid
	 * @param start the lower bound of the range of fragment entry links
	 * @param end the upper bound of the range of fragment entry links (not inclusive)
	 * @return the range of matching fragment entry links
	 */
	public java.util.List<FragmentEntryLink> findByG_S_P(
		long groupId, long[] segmentsExperienceIds, long plid, int start,
		int end);

	/**
	 * Returns an ordered range of all the fragment entry links where groupId = &#63; and segmentsExperienceId = any &#63; and plid = &#63;.
	 *
	 * <p>
	 * Useful when paginating results. Returns a maximum of <code>end - start</code> instances. <code>start</code> and <code>end</code> are not primary keys, they are indexes in the result set. Thus, <code>0</code> refers to the first result in the set. Setting both <code>start</code> and <code>end</code> to <code>QueryUtil#ALL_POS</code> will return the full result set. If <code>orderByComparator</code> is specified, then the query will include the given ORDER BY logic. If <code>orderByComparator</code> is absent, then the query will include the default ORDER BY logic from <code>FragmentEntryLinkModelImpl</code>.
	 * </p>
	 *
	 * @param groupId the group ID
	 * @param segmentsExperienceIds the segments experience IDs
	 * @param plid the plid
	 * @param start the lower bound of the range of fragment entry links
	 * @param end the upper bound of the range of fragment entry links (not inclusive)
	 * @param orderByComparator the comparator to order the results by (optionally <code>null</code>)
	 * @return the ordered range of matching fragment entry links
	 */
	public java.util.List<FragmentEntryLink> findByG_S_P(
		long groupId, long[] segmentsExperienceIds, long plid, int start,
		int end,
		com.liferay.portal.kernel.util.OrderByComparator<FragmentEntryLink>
			orderByComparator);

	/**
	 * Returns an ordered range of all the fragment entry links where groupId = &#63; and segmentsExperienceId = &#63; and plid = &#63;, optionally using the finder cache.
	 *
	 * <p>
	 * Useful when paginating results. Returns a maximum of <code>end - start</code> instances. <code>start</code> and <code>end</code> are not primary keys, they are indexes in the result set. Thus, <code>0</code> refers to the first result in the set. Setting both <code>start</code> and <code>end</code> to <code>QueryUtil#ALL_POS</code> will return the full result set. If <code>orderByComparator</code> is specified, then the query will include the given ORDER BY logic. If <code>orderByComparator</code> is absent, then the query will include the default ORDER BY logic from <code>FragmentEntryLinkModelImpl</code>.
	 * </p>
	 *
	 * @param groupId the group ID
	 * @param segmentsExperienceIds the segments experience IDs
	 * @param plid the plid
	 * @param start the lower bound of the range of fragment entry links
	 * @param end the upper bound of the range of fragment entry links (not inclusive)
	 * @param orderByComparator the comparator to order the results by (optionally <code>null</code>)
	 * @param useFinderCache whether to use the finder cache
	 * @return the ordered range of matching fragment entry links
	 */
	public java.util.List<FragmentEntryLink> findByG_S_P(
		long groupId, long[] segmentsExperienceIds, long plid, int start,
		int end,
		com.liferay.portal.kernel.util.OrderByComparator<FragmentEntryLink>
			orderByComparator,
		boolean useFinderCache);

	/**
	 * Removes all the fragment entry links where groupId = &#63; and segmentsExperienceId = &#63; and plid = &#63; from the database.
	 *
	 * @param groupId the group ID
	 * @param segmentsExperienceId the segments experience ID
	 * @param plid the plid
	 */
	public void removeByG_S_P(
		long groupId, long segmentsExperienceId, long plid);

	/**
	 * Returns the number of fragment entry links where groupId = &#63; and segmentsExperienceId = &#63; and plid = &#63;.
	 *
	 * @param groupId the group ID
	 * @param segmentsExperienceId the segments experience ID
	 * @param plid the plid
	 * @return the number of matching fragment entry links
	 */
	public int countByG_S_P(long groupId, long segmentsExperienceId, long plid);

	/**
	 * Returns the number of fragment entry links where groupId = &#63; and segmentsExperienceId = any &#63; and plid = &#63;.
	 *
	 * @param groupId the group ID
	 * @param segmentsExperienceIds the segments experience IDs
	 * @param plid the plid
	 * @return the number of matching fragment entry links
	 */
	public int countByG_S_P(
		long groupId, long[] segmentsExperienceIds, long plid);

	/**
	 * Returns all the fragment entry links where groupId = &#63; and classNameId = &#63; and classPK = &#63;.
	 *
	 * @param groupId the group ID
	 * @param classNameId the class name ID
	 * @param classPK the class pk
	 * @return the matching fragment entry links
	 */
	public java.util.List<FragmentEntryLink> findByG_C_C(
		long groupId, long classNameId, long classPK);

	/**
	 * Returns a range of all the fragment entry links where groupId = &#63; and classNameId = &#63; and classPK = &#63;.
	 *
	 * <p>
	 * Useful when paginating results. Returns a maximum of <code>end - start</code> instances. <code>start</code> and <code>end</code> are not primary keys, they are indexes in the result set. Thus, <code>0</code> refers to the first result in the set. Setting both <code>start</code> and <code>end</code> to <code>QueryUtil#ALL_POS</code> will return the full result set. If <code>orderByComparator</code> is specified, then the query will include the given ORDER BY logic. If <code>orderByComparator</code> is absent, then the query will include the default ORDER BY logic from <code>FragmentEntryLinkModelImpl</code>.
	 * </p>
	 *
	 * @param groupId the group ID
	 * @param classNameId the class name ID
	 * @param classPK the class pk
	 * @param start the lower bound of the range of fragment entry links
	 * @param end the upper bound of the range of fragment entry links (not inclusive)
	 * @return the range of matching fragment entry links
	 */
	public java.util.List<FragmentEntryLink> findByG_C_C(
		long groupId, long classNameId, long classPK, int start, int end);

	/**
	 * Returns an ordered range of all the fragment entry links where groupId = &#63; and classNameId = &#63; and classPK = &#63;.
	 *
	 * <p>
	 * Useful when paginating results. Returns a maximum of <code>end - start</code> instances. <code>start</code> and <code>end</code> are not primary keys, they are indexes in the result set. Thus, <code>0</code> refers to the first result in the set. Setting both <code>start</code> and <code>end</code> to <code>QueryUtil#ALL_POS</code> will return the full result set. If <code>orderByComparator</code> is specified, then the query will include the given ORDER BY logic. If <code>orderByComparator</code> is absent, then the query will include the default ORDER BY logic from <code>FragmentEntryLinkModelImpl</code>.
	 * </p>
	 *
	 * @param groupId the group ID
	 * @param classNameId the class name ID
	 * @param classPK the class pk
	 * @param start the lower bound of the range of fragment entry links
	 * @param end the upper bound of the range of fragment entry links (not inclusive)
	 * @param orderByComparator the comparator to order the results by (optionally <code>null</code>)
	 * @return the ordered range of matching fragment entry links
	 */
	public java.util.List<FragmentEntryLink> findByG_C_C(
		long groupId, long classNameId, long classPK, int start, int end,
		com.liferay.portal.kernel.util.OrderByComparator<FragmentEntryLink>
			orderByComparator);

	/**
	 * Returns an ordered range of all the fragment entry links where groupId = &#63; and classNameId = &#63; and classPK = &#63;.
	 *
	 * <p>
	 * Useful when paginating results. Returns a maximum of <code>end - start</code> instances. <code>start</code> and <code>end</code> are not primary keys, they are indexes in the result set. Thus, <code>0</code> refers to the first result in the set. Setting both <code>start</code> and <code>end</code> to <code>QueryUtil#ALL_POS</code> will return the full result set. If <code>orderByComparator</code> is specified, then the query will include the given ORDER BY logic. If <code>orderByComparator</code> is absent, then the query will include the default ORDER BY logic from <code>FragmentEntryLinkModelImpl</code>.
	 * </p>
	 *
	 * @param groupId the group ID
	 * @param classNameId the class name ID
	 * @param classPK the class pk
	 * @param start the lower bound of the range of fragment entry links
	 * @param end the upper bound of the range of fragment entry links (not inclusive)
	 * @param orderByComparator the comparator to order the results by (optionally <code>null</code>)
	 * @param useFinderCache whether to use the finder cache
	 * @return the ordered range of matching fragment entry links
	 */
	public java.util.List<FragmentEntryLink> findByG_C_C(
		long groupId, long classNameId, long classPK, int start, int end,
		com.liferay.portal.kernel.util.OrderByComparator<FragmentEntryLink>
			orderByComparator,
		boolean useFinderCache);

	/**
	 * Returns the first fragment entry link in the ordered set where groupId = &#63; and classNameId = &#63; and classPK = &#63;.
	 *
	 * @param groupId the group ID
	 * @param classNameId the class name ID
	 * @param classPK the class pk
	 * @param orderByComparator the comparator to order the set by (optionally <code>null</code>)
	 * @return the first matching fragment entry link
	 * @throws NoSuchEntryLinkException if a matching fragment entry link could not be found
	 */
	public FragmentEntryLink findByG_C_C_First(
			long groupId, long classNameId, long classPK,
			com.liferay.portal.kernel.util.OrderByComparator<FragmentEntryLink>
				orderByComparator)
		throws NoSuchEntryLinkException;

	/**
	 * Returns the first fragment entry link in the ordered set where groupId = &#63; and classNameId = &#63; and classPK = &#63;.
	 *
	 * @param groupId the group ID
	 * @param classNameId the class name ID
	 * @param classPK the class pk
	 * @param orderByComparator the comparator to order the set by (optionally <code>null</code>)
	 * @return the first matching fragment entry link, or <code>null</code> if a matching fragment entry link could not be found
	 */
	public FragmentEntryLink fetchByG_C_C_First(
		long groupId, long classNameId, long classPK,
		com.liferay.portal.kernel.util.OrderByComparator<FragmentEntryLink>
			orderByComparator);

	/**
	 * Returns the last fragment entry link in the ordered set where groupId = &#63; and classNameId = &#63; and classPK = &#63;.
	 *
	 * @param groupId the group ID
	 * @param classNameId the class name ID
	 * @param classPK the class pk
	 * @param orderByComparator the comparator to order the set by (optionally <code>null</code>)
	 * @return the last matching fragment entry link
	 * @throws NoSuchEntryLinkException if a matching fragment entry link could not be found
	 */
	public FragmentEntryLink findByG_C_C_Last(
			long groupId, long classNameId, long classPK,
			com.liferay.portal.kernel.util.OrderByComparator<FragmentEntryLink>
				orderByComparator)
		throws NoSuchEntryLinkException;

	/**
	 * Returns the last fragment entry link in the ordered set where groupId = &#63; and classNameId = &#63; and classPK = &#63;.
	 *
	 * @param groupId the group ID
	 * @param classNameId the class name ID
	 * @param classPK the class pk
	 * @param orderByComparator the comparator to order the set by (optionally <code>null</code>)
	 * @return the last matching fragment entry link, or <code>null</code> if a matching fragment entry link could not be found
	 */
	public FragmentEntryLink fetchByG_C_C_Last(
		long groupId, long classNameId, long classPK,
		com.liferay.portal.kernel.util.OrderByComparator<FragmentEntryLink>
			orderByComparator);

	/**
	 * Returns the fragment entry links before and after the current fragment entry link in the ordered set where groupId = &#63; and classNameId = &#63; and classPK = &#63;.
	 *
	 * @param fragmentEntryLinkId the primary key of the current fragment entry link
	 * @param groupId the group ID
	 * @param classNameId the class name ID
	 * @param classPK the class pk
	 * @param orderByComparator the comparator to order the set by (optionally <code>null</code>)
	 * @return the previous, current, and next fragment entry link
	 * @throws NoSuchEntryLinkException if a fragment entry link with the primary key could not be found
	 */
	public FragmentEntryLink[] findByG_C_C_PrevAndNext(
			long fragmentEntryLinkId, long groupId, long classNameId,
			long classPK,
			com.liferay.portal.kernel.util.OrderByComparator<FragmentEntryLink>
				orderByComparator)
		throws NoSuchEntryLinkException;

	/**
	 * Removes all the fragment entry links where groupId = &#63; and classNameId = &#63; and classPK = &#63; from the database.
	 *
	 * @param groupId the group ID
	 * @param classNameId the class name ID
	 * @param classPK the class pk
	 */
	public void removeByG_C_C(long groupId, long classNameId, long classPK);

	/**
	 * Returns the number of fragment entry links where groupId = &#63; and classNameId = &#63; and classPK = &#63;.
	 *
	 * @param groupId the group ID
	 * @param classNameId the class name ID
	 * @param classPK the class pk
	 * @return the number of matching fragment entry links
	 */
	public int countByG_C_C(long groupId, long classNameId, long classPK);

	/**
	 * Returns all the fragment entry links where groupId = &#63; and plid = &#63; and deleted = &#63;.
	 *
	 * @param groupId the group ID
	 * @param plid the plid
	 * @param deleted the deleted
	 * @return the matching fragment entry links
	 */
	public java.util.List<FragmentEntryLink> findByG_P_D(
		long groupId, long plid, boolean deleted);

	/**
	 * Returns a range of all the fragment entry links where groupId = &#63; and plid = &#63; and deleted = &#63;.
	 *
	 * <p>
	 * Useful when paginating results. Returns a maximum of <code>end - start</code> instances. <code>start</code> and <code>end</code> are not primary keys, they are indexes in the result set. Thus, <code>0</code> refers to the first result in the set. Setting both <code>start</code> and <code>end</code> to <code>QueryUtil#ALL_POS</code> will return the full result set. If <code>orderByComparator</code> is specified, then the query will include the given ORDER BY logic. If <code>orderByComparator</code> is absent, then the query will include the default ORDER BY logic from <code>FragmentEntryLinkModelImpl</code>.
	 * </p>
	 *
	 * @param groupId the group ID
	 * @param plid the plid
	 * @param deleted the deleted
	 * @param start the lower bound of the range of fragment entry links
	 * @param end the upper bound of the range of fragment entry links (not inclusive)
	 * @return the range of matching fragment entry links
	 */
	public java.util.List<FragmentEntryLink> findByG_P_D(
		long groupId, long plid, boolean deleted, int start, int end);

	/**
	 * Returns an ordered range of all the fragment entry links where groupId = &#63; and plid = &#63; and deleted = &#63;.
	 *
	 * <p>
	 * Useful when paginating results. Returns a maximum of <code>end - start</code> instances. <code>start</code> and <code>end</code> are not primary keys, they are indexes in the result set. Thus, <code>0</code> refers to the first result in the set. Setting both <code>start</code> and <code>end</code> to <code>QueryUtil#ALL_POS</code> will return the full result set. If <code>orderByComparator</code> is specified, then the query will include the given ORDER BY logic. If <code>orderByComparator</code> is absent, then the query will include the default ORDER BY logic from <code>FragmentEntryLinkModelImpl</code>.
	 * </p>
	 *
	 * @param groupId the group ID
	 * @param plid the plid
	 * @param deleted the deleted
	 * @param start the lower bound of the range of fragment entry links
	 * @param end the upper bound of the range of fragment entry links (not inclusive)
	 * @param orderByComparator the comparator to order the results by (optionally <code>null</code>)
	 * @return the ordered range of matching fragment entry links
	 */
	public java.util.List<FragmentEntryLink> findByG_P_D(
		long groupId, long plid, boolean deleted, int start, int end,
		com.liferay.portal.kernel.util.OrderByComparator<FragmentEntryLink>
			orderByComparator);

	/**
	 * Returns an ordered range of all the fragment entry links where groupId = &#63; and plid = &#63; and deleted = &#63;.
	 *
	 * <p>
	 * Useful when paginating results. Returns a maximum of <code>end - start</code> instances. <code>start</code> and <code>end</code> are not primary keys, they are indexes in the result set. Thus, <code>0</code> refers to the first result in the set. Setting both <code>start</code> and <code>end</code> to <code>QueryUtil#ALL_POS</code> will return the full result set. If <code>orderByComparator</code> is specified, then the query will include the given ORDER BY logic. If <code>orderByComparator</code> is absent, then the query will include the default ORDER BY logic from <code>FragmentEntryLinkModelImpl</code>.
	 * </p>
	 *
	 * @param groupId the group ID
	 * @param plid the plid
	 * @param deleted the deleted
	 * @param start the lower bound of the range of fragment entry links
	 * @param end the upper bound of the range of fragment entry links (not inclusive)
	 * @param orderByComparator the comparator to order the results by (optionally <code>null</code>)
	 * @param useFinderCache whether to use the finder cache
	 * @return the ordered range of matching fragment entry links
	 */
	public java.util.List<FragmentEntryLink> findByG_P_D(
		long groupId, long plid, boolean deleted, int start, int end,
		com.liferay.portal.kernel.util.OrderByComparator<FragmentEntryLink>
			orderByComparator,
		boolean useFinderCache);

	/**
	 * Returns the first fragment entry link in the ordered set where groupId = &#63; and plid = &#63; and deleted = &#63;.
	 *
	 * @param groupId the group ID
	 * @param plid the plid
	 * @param deleted the deleted
	 * @param orderByComparator the comparator to order the set by (optionally <code>null</code>)
	 * @return the first matching fragment entry link
	 * @throws NoSuchEntryLinkException if a matching fragment entry link could not be found
	 */
	public FragmentEntryLink findByG_P_D_First(
			long groupId, long plid, boolean deleted,
			com.liferay.portal.kernel.util.OrderByComparator<FragmentEntryLink>
				orderByComparator)
		throws NoSuchEntryLinkException;

	/**
	 * Returns the first fragment entry link in the ordered set where groupId = &#63; and plid = &#63; and deleted = &#63;.
	 *
	 * @param groupId the group ID
	 * @param plid the plid
	 * @param deleted the deleted
	 * @param orderByComparator the comparator to order the set by (optionally <code>null</code>)
	 * @return the first matching fragment entry link, or <code>null</code> if a matching fragment entry link could not be found
	 */
	public FragmentEntryLink fetchByG_P_D_First(
		long groupId, long plid, boolean deleted,
		com.liferay.portal.kernel.util.OrderByComparator<FragmentEntryLink>
			orderByComparator);

	/**
	 * Returns the last fragment entry link in the ordered set where groupId = &#63; and plid = &#63; and deleted = &#63;.
	 *
	 * @param groupId the group ID
	 * @param plid the plid
	 * @param deleted the deleted
	 * @param orderByComparator the comparator to order the set by (optionally <code>null</code>)
	 * @return the last matching fragment entry link
	 * @throws NoSuchEntryLinkException if a matching fragment entry link could not be found
	 */
	public FragmentEntryLink findByG_P_D_Last(
			long groupId, long plid, boolean deleted,
			com.liferay.portal.kernel.util.OrderByComparator<FragmentEntryLink>
				orderByComparator)
		throws NoSuchEntryLinkException;

	/**
	 * Returns the last fragment entry link in the ordered set where groupId = &#63; and plid = &#63; and deleted = &#63;.
	 *
	 * @param groupId the group ID
	 * @param plid the plid
	 * @param deleted the deleted
	 * @param orderByComparator the comparator to order the set by (optionally <code>null</code>)
	 * @return the last matching fragment entry link, or <code>null</code> if a matching fragment entry link could not be found
	 */
	public FragmentEntryLink fetchByG_P_D_Last(
		long groupId, long plid, boolean deleted,
		com.liferay.portal.kernel.util.OrderByComparator<FragmentEntryLink>
			orderByComparator);

	/**
	 * Returns the fragment entry links before and after the current fragment entry link in the ordered set where groupId = &#63; and plid = &#63; and deleted = &#63;.
	 *
	 * @param fragmentEntryLinkId the primary key of the current fragment entry link
	 * @param groupId the group ID
	 * @param plid the plid
	 * @param deleted the deleted
	 * @param orderByComparator the comparator to order the set by (optionally <code>null</code>)
	 * @return the previous, current, and next fragment entry link
	 * @throws NoSuchEntryLinkException if a fragment entry link with the primary key could not be found
	 */
	public FragmentEntryLink[] findByG_P_D_PrevAndNext(
			long fragmentEntryLinkId, long groupId, long plid, boolean deleted,
			com.liferay.portal.kernel.util.OrderByComparator<FragmentEntryLink>
				orderByComparator)
		throws NoSuchEntryLinkException;

	/**
	 * Removes all the fragment entry links where groupId = &#63; and plid = &#63; and deleted = &#63; from the database.
	 *
	 * @param groupId the group ID
	 * @param plid the plid
	 * @param deleted the deleted
	 */
	public void removeByG_P_D(long groupId, long plid, boolean deleted);

	/**
	 * Returns the number of fragment entry links where groupId = &#63; and plid = &#63; and deleted = &#63;.
	 *
	 * @param groupId the group ID
	 * @param plid the plid
	 * @param deleted the deleted
	 * @return the number of matching fragment entry links
	 */
	public int countByG_P_D(long groupId, long plid, boolean deleted);

	/**
	 * Returns all the fragment entry links where fragmentEntryERC = &#63; and fragmentEntryScopeERC = &#63; and deleted = &#63;.
	 *
	 * @param fragmentEntryERC the fragment entry erc
	 * @param fragmentEntryScopeERC the fragment entry scope erc
	 * @param deleted the deleted
	 * @return the matching fragment entry links
	 */
	public java.util.List<FragmentEntryLink> findByFEERC_FESERC_D(
		String fragmentEntryERC, String fragmentEntryScopeERC, boolean deleted);

	/**
	 * Returns a range of all the fragment entry links where fragmentEntryERC = &#63; and fragmentEntryScopeERC = &#63; and deleted = &#63;.
	 *
	 * <p>
	 * Useful when paginating results. Returns a maximum of <code>end - start</code> instances. <code>start</code> and <code>end</code> are not primary keys, they are indexes in the result set. Thus, <code>0</code> refers to the first result in the set. Setting both <code>start</code> and <code>end</code> to <code>QueryUtil#ALL_POS</code> will return the full result set. If <code>orderByComparator</code> is specified, then the query will include the given ORDER BY logic. If <code>orderByComparator</code> is absent, then the query will include the default ORDER BY logic from <code>FragmentEntryLinkModelImpl</code>.
	 * </p>
	 *
	 * @param fragmentEntryERC the fragment entry erc
	 * @param fragmentEntryScopeERC the fragment entry scope erc
	 * @param deleted the deleted
	 * @param start the lower bound of the range of fragment entry links
	 * @param end the upper bound of the range of fragment entry links (not inclusive)
	 * @return the range of matching fragment entry links
	 */
	public java.util.List<FragmentEntryLink> findByFEERC_FESERC_D(
		String fragmentEntryERC, String fragmentEntryScopeERC, boolean deleted,
		int start, int end);

	/**
	 * Returns an ordered range of all the fragment entry links where fragmentEntryERC = &#63; and fragmentEntryScopeERC = &#63; and deleted = &#63;.
	 *
	 * <p>
	 * Useful when paginating results. Returns a maximum of <code>end - start</code> instances. <code>start</code> and <code>end</code> are not primary keys, they are indexes in the result set. Thus, <code>0</code> refers to the first result in the set. Setting both <code>start</code> and <code>end</code> to <code>QueryUtil#ALL_POS</code> will return the full result set. If <code>orderByComparator</code> is specified, then the query will include the given ORDER BY logic. If <code>orderByComparator</code> is absent, then the query will include the default ORDER BY logic from <code>FragmentEntryLinkModelImpl</code>.
	 * </p>
	 *
	 * @param fragmentEntryERC the fragment entry erc
	 * @param fragmentEntryScopeERC the fragment entry scope erc
	 * @param deleted the deleted
	 * @param start the lower bound of the range of fragment entry links
	 * @param end the upper bound of the range of fragment entry links (not inclusive)
	 * @param orderByComparator the comparator to order the results by (optionally <code>null</code>)
	 * @return the ordered range of matching fragment entry links
	 */
	public java.util.List<FragmentEntryLink> findByFEERC_FESERC_D(
		String fragmentEntryERC, String fragmentEntryScopeERC, boolean deleted,
		int start, int end,
		com.liferay.portal.kernel.util.OrderByComparator<FragmentEntryLink>
			orderByComparator);

	/**
	 * Returns an ordered range of all the fragment entry links where fragmentEntryERC = &#63; and fragmentEntryScopeERC = &#63; and deleted = &#63;.
	 *
	 * <p>
	 * Useful when paginating results. Returns a maximum of <code>end - start</code> instances. <code>start</code> and <code>end</code> are not primary keys, they are indexes in the result set. Thus, <code>0</code> refers to the first result in the set. Setting both <code>start</code> and <code>end</code> to <code>QueryUtil#ALL_POS</code> will return the full result set. If <code>orderByComparator</code> is specified, then the query will include the given ORDER BY logic. If <code>orderByComparator</code> is absent, then the query will include the default ORDER BY logic from <code>FragmentEntryLinkModelImpl</code>.
	 * </p>
	 *
	 * @param fragmentEntryERC the fragment entry erc
	 * @param fragmentEntryScopeERC the fragment entry scope erc
	 * @param deleted the deleted
	 * @param start the lower bound of the range of fragment entry links
	 * @param end the upper bound of the range of fragment entry links (not inclusive)
	 * @param orderByComparator the comparator to order the results by (optionally <code>null</code>)
	 * @param useFinderCache whether to use the finder cache
	 * @return the ordered range of matching fragment entry links
	 */
	public java.util.List<FragmentEntryLink> findByFEERC_FESERC_D(
		String fragmentEntryERC, String fragmentEntryScopeERC, boolean deleted,
		int start, int end,
		com.liferay.portal.kernel.util.OrderByComparator<FragmentEntryLink>
			orderByComparator,
		boolean useFinderCache);

	/**
	 * Returns the first fragment entry link in the ordered set where fragmentEntryERC = &#63; and fragmentEntryScopeERC = &#63; and deleted = &#63;.
	 *
	 * @param fragmentEntryERC the fragment entry erc
	 * @param fragmentEntryScopeERC the fragment entry scope erc
	 * @param deleted the deleted
	 * @param orderByComparator the comparator to order the set by (optionally <code>null</code>)
	 * @return the first matching fragment entry link
	 * @throws NoSuchEntryLinkException if a matching fragment entry link could not be found
	 */
	public FragmentEntryLink findByFEERC_FESERC_D_First(
			String fragmentEntryERC, String fragmentEntryScopeERC,
			boolean deleted,
			com.liferay.portal.kernel.util.OrderByComparator<FragmentEntryLink>
				orderByComparator)
		throws NoSuchEntryLinkException;

	/**
	 * Returns the first fragment entry link in the ordered set where fragmentEntryERC = &#63; and fragmentEntryScopeERC = &#63; and deleted = &#63;.
	 *
	 * @param fragmentEntryERC the fragment entry erc
	 * @param fragmentEntryScopeERC the fragment entry scope erc
	 * @param deleted the deleted
	 * @param orderByComparator the comparator to order the set by (optionally <code>null</code>)
	 * @return the first matching fragment entry link, or <code>null</code> if a matching fragment entry link could not be found
	 */
	public FragmentEntryLink fetchByFEERC_FESERC_D_First(
		String fragmentEntryERC, String fragmentEntryScopeERC, boolean deleted,
		com.liferay.portal.kernel.util.OrderByComparator<FragmentEntryLink>
			orderByComparator);

	/**
	 * Returns the last fragment entry link in the ordered set where fragmentEntryERC = &#63; and fragmentEntryScopeERC = &#63; and deleted = &#63;.
	 *
	 * @param fragmentEntryERC the fragment entry erc
	 * @param fragmentEntryScopeERC the fragment entry scope erc
	 * @param deleted the deleted
	 * @param orderByComparator the comparator to order the set by (optionally <code>null</code>)
	 * @return the last matching fragment entry link
	 * @throws NoSuchEntryLinkException if a matching fragment entry link could not be found
	 */
	public FragmentEntryLink findByFEERC_FESERC_D_Last(
			String fragmentEntryERC, String fragmentEntryScopeERC,
			boolean deleted,
			com.liferay.portal.kernel.util.OrderByComparator<FragmentEntryLink>
				orderByComparator)
		throws NoSuchEntryLinkException;

	/**
	 * Returns the last fragment entry link in the ordered set where fragmentEntryERC = &#63; and fragmentEntryScopeERC = &#63; and deleted = &#63;.
	 *
	 * @param fragmentEntryERC the fragment entry erc
	 * @param fragmentEntryScopeERC the fragment entry scope erc
	 * @param deleted the deleted
	 * @param orderByComparator the comparator to order the set by (optionally <code>null</code>)
	 * @return the last matching fragment entry link, or <code>null</code> if a matching fragment entry link could not be found
	 */
	public FragmentEntryLink fetchByFEERC_FESERC_D_Last(
		String fragmentEntryERC, String fragmentEntryScopeERC, boolean deleted,
		com.liferay.portal.kernel.util.OrderByComparator<FragmentEntryLink>
			orderByComparator);

	/**
	 * Returns the fragment entry links before and after the current fragment entry link in the ordered set where fragmentEntryERC = &#63; and fragmentEntryScopeERC = &#63; and deleted = &#63;.
	 *
	 * @param fragmentEntryLinkId the primary key of the current fragment entry link
	 * @param fragmentEntryERC the fragment entry erc
	 * @param fragmentEntryScopeERC the fragment entry scope erc
	 * @param deleted the deleted
	 * @param orderByComparator the comparator to order the set by (optionally <code>null</code>)
	 * @return the previous, current, and next fragment entry link
	 * @throws NoSuchEntryLinkException if a fragment entry link with the primary key could not be found
	 */
	public FragmentEntryLink[] findByFEERC_FESERC_D_PrevAndNext(
			long fragmentEntryLinkId, String fragmentEntryERC,
			String fragmentEntryScopeERC, boolean deleted,
			com.liferay.portal.kernel.util.OrderByComparator<FragmentEntryLink>
				orderByComparator)
		throws NoSuchEntryLinkException;

	/**
	 * Removes all the fragment entry links where fragmentEntryERC = &#63; and fragmentEntryScopeERC = &#63; and deleted = &#63; from the database.
	 *
	 * @param fragmentEntryERC the fragment entry erc
	 * @param fragmentEntryScopeERC the fragment entry scope erc
	 * @param deleted the deleted
	 */
	public void removeByFEERC_FESERC_D(
		String fragmentEntryERC, String fragmentEntryScopeERC, boolean deleted);

	/**
	 * Returns the number of fragment entry links where fragmentEntryERC = &#63; and fragmentEntryScopeERC = &#63; and deleted = &#63;.
	 *
	 * @param fragmentEntryERC the fragment entry erc
	 * @param fragmentEntryScopeERC the fragment entry scope erc
	 * @param deleted the deleted
	 * @return the number of matching fragment entry links
	 */
	public int countByFEERC_FESERC_D(
		String fragmentEntryERC, String fragmentEntryScopeERC, boolean deleted);

	/**
	 * Returns all the fragment entry links where groupId = &#63; and fragmentEntryERC = &#63; and fragmentEntryScopeERC = &#63; and classNameId = &#63;.
	 *
	 * @param groupId the group ID
	 * @param fragmentEntryERC the fragment entry erc
	 * @param fragmentEntryScopeERC the fragment entry scope erc
	 * @param classNameId the class name ID
	 * @return the matching fragment entry links
	 */
	public java.util.List<FragmentEntryLink> findByG_FEERC_FESERC_C(
		long groupId, String fragmentEntryERC, String fragmentEntryScopeERC,
		long classNameId);

	/**
	 * Returns a range of all the fragment entry links where groupId = &#63; and fragmentEntryERC = &#63; and fragmentEntryScopeERC = &#63; and classNameId = &#63;.
	 *
	 * <p>
	 * Useful when paginating results. Returns a maximum of <code>end - start</code> instances. <code>start</code> and <code>end</code> are not primary keys, they are indexes in the result set. Thus, <code>0</code> refers to the first result in the set. Setting both <code>start</code> and <code>end</code> to <code>QueryUtil#ALL_POS</code> will return the full result set. If <code>orderByComparator</code> is specified, then the query will include the given ORDER BY logic. If <code>orderByComparator</code> is absent, then the query will include the default ORDER BY logic from <code>FragmentEntryLinkModelImpl</code>.
	 * </p>
	 *
	 * @param groupId the group ID
	 * @param fragmentEntryERC the fragment entry erc
	 * @param fragmentEntryScopeERC the fragment entry scope erc
	 * @param classNameId the class name ID
	 * @param start the lower bound of the range of fragment entry links
	 * @param end the upper bound of the range of fragment entry links (not inclusive)
	 * @return the range of matching fragment entry links
	 */
	public java.util.List<FragmentEntryLink> findByG_FEERC_FESERC_C(
		long groupId, String fragmentEntryERC, String fragmentEntryScopeERC,
		long classNameId, int start, int end);

	/**
	 * Returns an ordered range of all the fragment entry links where groupId = &#63; and fragmentEntryERC = &#63; and fragmentEntryScopeERC = &#63; and classNameId = &#63;.
	 *
	 * <p>
	 * Useful when paginating results. Returns a maximum of <code>end - start</code> instances. <code>start</code> and <code>end</code> are not primary keys, they are indexes in the result set. Thus, <code>0</code> refers to the first result in the set. Setting both <code>start</code> and <code>end</code> to <code>QueryUtil#ALL_POS</code> will return the full result set. If <code>orderByComparator</code> is specified, then the query will include the given ORDER BY logic. If <code>orderByComparator</code> is absent, then the query will include the default ORDER BY logic from <code>FragmentEntryLinkModelImpl</code>.
	 * </p>
	 *
	 * @param groupId the group ID
	 * @param fragmentEntryERC the fragment entry erc
	 * @param fragmentEntryScopeERC the fragment entry scope erc
	 * @param classNameId the class name ID
	 * @param start the lower bound of the range of fragment entry links
	 * @param end the upper bound of the range of fragment entry links (not inclusive)
	 * @param orderByComparator the comparator to order the results by (optionally <code>null</code>)
	 * @return the ordered range of matching fragment entry links
	 */
	public java.util.List<FragmentEntryLink> findByG_FEERC_FESERC_C(
		long groupId, String fragmentEntryERC, String fragmentEntryScopeERC,
		long classNameId, int start, int end,
		com.liferay.portal.kernel.util.OrderByComparator<FragmentEntryLink>
			orderByComparator);

	/**
	 * Returns an ordered range of all the fragment entry links where groupId = &#63; and fragmentEntryERC = &#63; and fragmentEntryScopeERC = &#63; and classNameId = &#63;.
	 *
	 * <p>
	 * Useful when paginating results. Returns a maximum of <code>end - start</code> instances. <code>start</code> and <code>end</code> are not primary keys, they are indexes in the result set. Thus, <code>0</code> refers to the first result in the set. Setting both <code>start</code> and <code>end</code> to <code>QueryUtil#ALL_POS</code> will return the full result set. If <code>orderByComparator</code> is specified, then the query will include the given ORDER BY logic. If <code>orderByComparator</code> is absent, then the query will include the default ORDER BY logic from <code>FragmentEntryLinkModelImpl</code>.
	 * </p>
	 *
	 * @param groupId the group ID
	 * @param fragmentEntryERC the fragment entry erc
	 * @param fragmentEntryScopeERC the fragment entry scope erc
	 * @param classNameId the class name ID
	 * @param start the lower bound of the range of fragment entry links
	 * @param end the upper bound of the range of fragment entry links (not inclusive)
	 * @param orderByComparator the comparator to order the results by (optionally <code>null</code>)
	 * @param useFinderCache whether to use the finder cache
	 * @return the ordered range of matching fragment entry links
	 */
	public java.util.List<FragmentEntryLink> findByG_FEERC_FESERC_C(
		long groupId, String fragmentEntryERC, String fragmentEntryScopeERC,
		long classNameId, int start, int end,
		com.liferay.portal.kernel.util.OrderByComparator<FragmentEntryLink>
			orderByComparator,
		boolean useFinderCache);

	/**
	 * Returns the first fragment entry link in the ordered set where groupId = &#63; and fragmentEntryERC = &#63; and fragmentEntryScopeERC = &#63; and classNameId = &#63;.
	 *
	 * @param groupId the group ID
	 * @param fragmentEntryERC the fragment entry erc
	 * @param fragmentEntryScopeERC the fragment entry scope erc
	 * @param classNameId the class name ID
	 * @param orderByComparator the comparator to order the set by (optionally <code>null</code>)
	 * @return the first matching fragment entry link
	 * @throws NoSuchEntryLinkException if a matching fragment entry link could not be found
	 */
	public FragmentEntryLink findByG_FEERC_FESERC_C_First(
			long groupId, String fragmentEntryERC, String fragmentEntryScopeERC,
			long classNameId,
			com.liferay.portal.kernel.util.OrderByComparator<FragmentEntryLink>
				orderByComparator)
		throws NoSuchEntryLinkException;

	/**
	 * Returns the first fragment entry link in the ordered set where groupId = &#63; and fragmentEntryERC = &#63; and fragmentEntryScopeERC = &#63; and classNameId = &#63;.
	 *
	 * @param groupId the group ID
	 * @param fragmentEntryERC the fragment entry erc
	 * @param fragmentEntryScopeERC the fragment entry scope erc
	 * @param classNameId the class name ID
	 * @param orderByComparator the comparator to order the set by (optionally <code>null</code>)
	 * @return the first matching fragment entry link, or <code>null</code> if a matching fragment entry link could not be found
	 */
	public FragmentEntryLink fetchByG_FEERC_FESERC_C_First(
		long groupId, String fragmentEntryERC, String fragmentEntryScopeERC,
		long classNameId,
		com.liferay.portal.kernel.util.OrderByComparator<FragmentEntryLink>
			orderByComparator);

	/**
	 * Returns the last fragment entry link in the ordered set where groupId = &#63; and fragmentEntryERC = &#63; and fragmentEntryScopeERC = &#63; and classNameId = &#63;.
	 *
	 * @param groupId the group ID
	 * @param fragmentEntryERC the fragment entry erc
	 * @param fragmentEntryScopeERC the fragment entry scope erc
	 * @param classNameId the class name ID
	 * @param orderByComparator the comparator to order the set by (optionally <code>null</code>)
	 * @return the last matching fragment entry link
	 * @throws NoSuchEntryLinkException if a matching fragment entry link could not be found
	 */
	public FragmentEntryLink findByG_FEERC_FESERC_C_Last(
			long groupId, String fragmentEntryERC, String fragmentEntryScopeERC,
			long classNameId,
			com.liferay.portal.kernel.util.OrderByComparator<FragmentEntryLink>
				orderByComparator)
		throws NoSuchEntryLinkException;

	/**
	 * Returns the last fragment entry link in the ordered set where groupId = &#63; and fragmentEntryERC = &#63; and fragmentEntryScopeERC = &#63; and classNameId = &#63;.
	 *
	 * @param groupId the group ID
	 * @param fragmentEntryERC the fragment entry erc
	 * @param fragmentEntryScopeERC the fragment entry scope erc
	 * @param classNameId the class name ID
	 * @param orderByComparator the comparator to order the set by (optionally <code>null</code>)
	 * @return the last matching fragment entry link, or <code>null</code> if a matching fragment entry link could not be found
	 */
	public FragmentEntryLink fetchByG_FEERC_FESERC_C_Last(
		long groupId, String fragmentEntryERC, String fragmentEntryScopeERC,
		long classNameId,
		com.liferay.portal.kernel.util.OrderByComparator<FragmentEntryLink>
			orderByComparator);

	/**
	 * Returns the fragment entry links before and after the current fragment entry link in the ordered set where groupId = &#63; and fragmentEntryERC = &#63; and fragmentEntryScopeERC = &#63; and classNameId = &#63;.
	 *
	 * @param fragmentEntryLinkId the primary key of the current fragment entry link
	 * @param groupId the group ID
	 * @param fragmentEntryERC the fragment entry erc
	 * @param fragmentEntryScopeERC the fragment entry scope erc
	 * @param classNameId the class name ID
	 * @param orderByComparator the comparator to order the set by (optionally <code>null</code>)
	 * @return the previous, current, and next fragment entry link
	 * @throws NoSuchEntryLinkException if a fragment entry link with the primary key could not be found
	 */
	public FragmentEntryLink[] findByG_FEERC_FESERC_C_PrevAndNext(
			long fragmentEntryLinkId, long groupId, String fragmentEntryERC,
			String fragmentEntryScopeERC, long classNameId,
			com.liferay.portal.kernel.util.OrderByComparator<FragmentEntryLink>
				orderByComparator)
		throws NoSuchEntryLinkException;

	/**
	 * Removes all the fragment entry links where groupId = &#63; and fragmentEntryERC = &#63; and fragmentEntryScopeERC = &#63; and classNameId = &#63; from the database.
	 *
	 * @param groupId the group ID
	 * @param fragmentEntryERC the fragment entry erc
	 * @param fragmentEntryScopeERC the fragment entry scope erc
	 * @param classNameId the class name ID
	 */
	public void removeByG_FEERC_FESERC_C(
		long groupId, String fragmentEntryERC, String fragmentEntryScopeERC,
		long classNameId);

	/**
	 * Returns the number of fragment entry links where groupId = &#63; and fragmentEntryERC = &#63; and fragmentEntryScopeERC = &#63; and classNameId = &#63;.
	 *
	 * @param groupId the group ID
	 * @param fragmentEntryERC the fragment entry erc
	 * @param fragmentEntryScopeERC the fragment entry scope erc
	 * @param classNameId the class name ID
	 * @return the number of matching fragment entry links
	 */
	public int countByG_FEERC_FESERC_C(
		long groupId, String fragmentEntryERC, String fragmentEntryScopeERC,
		long classNameId);

	/**
	 * Returns all the fragment entry links where groupId = &#63; and fragmentEntryERC = &#63; and fragmentEntryScopeERC = &#63; and plid = &#63;.
	 *
	 * @param groupId the group ID
	 * @param fragmentEntryERC the fragment entry erc
	 * @param fragmentEntryScopeERC the fragment entry scope erc
	 * @param plid the plid
	 * @return the matching fragment entry links
	 */
	public java.util.List<FragmentEntryLink> findByG_FEERC_FESERC_P(
		long groupId, String fragmentEntryERC, String fragmentEntryScopeERC,
		long plid);

	/**
	 * Returns a range of all the fragment entry links where groupId = &#63; and fragmentEntryERC = &#63; and fragmentEntryScopeERC = &#63; and plid = &#63;.
	 *
	 * <p>
	 * Useful when paginating results. Returns a maximum of <code>end - start</code> instances. <code>start</code> and <code>end</code> are not primary keys, they are indexes in the result set. Thus, <code>0</code> refers to the first result in the set. Setting both <code>start</code> and <code>end</code> to <code>QueryUtil#ALL_POS</code> will return the full result set. If <code>orderByComparator</code> is specified, then the query will include the given ORDER BY logic. If <code>orderByComparator</code> is absent, then the query will include the default ORDER BY logic from <code>FragmentEntryLinkModelImpl</code>.
	 * </p>
	 *
	 * @param groupId the group ID
	 * @param fragmentEntryERC the fragment entry erc
	 * @param fragmentEntryScopeERC the fragment entry scope erc
	 * @param plid the plid
	 * @param start the lower bound of the range of fragment entry links
	 * @param end the upper bound of the range of fragment entry links (not inclusive)
	 * @return the range of matching fragment entry links
	 */
	public java.util.List<FragmentEntryLink> findByG_FEERC_FESERC_P(
		long groupId, String fragmentEntryERC, String fragmentEntryScopeERC,
		long plid, int start, int end);

	/**
	 * Returns an ordered range of all the fragment entry links where groupId = &#63; and fragmentEntryERC = &#63; and fragmentEntryScopeERC = &#63; and plid = &#63;.
	 *
	 * <p>
	 * Useful when paginating results. Returns a maximum of <code>end - start</code> instances. <code>start</code> and <code>end</code> are not primary keys, they are indexes in the result set. Thus, <code>0</code> refers to the first result in the set. Setting both <code>start</code> and <code>end</code> to <code>QueryUtil#ALL_POS</code> will return the full result set. If <code>orderByComparator</code> is specified, then the query will include the given ORDER BY logic. If <code>orderByComparator</code> is absent, then the query will include the default ORDER BY logic from <code>FragmentEntryLinkModelImpl</code>.
	 * </p>
	 *
	 * @param groupId the group ID
	 * @param fragmentEntryERC the fragment entry erc
	 * @param fragmentEntryScopeERC the fragment entry scope erc
	 * @param plid the plid
	 * @param start the lower bound of the range of fragment entry links
	 * @param end the upper bound of the range of fragment entry links (not inclusive)
	 * @param orderByComparator the comparator to order the results by (optionally <code>null</code>)
	 * @return the ordered range of matching fragment entry links
	 */
	public java.util.List<FragmentEntryLink> findByG_FEERC_FESERC_P(
		long groupId, String fragmentEntryERC, String fragmentEntryScopeERC,
		long plid, int start, int end,
		com.liferay.portal.kernel.util.OrderByComparator<FragmentEntryLink>
			orderByComparator);

	/**
	 * Returns an ordered range of all the fragment entry links where groupId = &#63; and fragmentEntryERC = &#63; and fragmentEntryScopeERC = &#63; and plid = &#63;.
	 *
	 * <p>
	 * Useful when paginating results. Returns a maximum of <code>end - start</code> instances. <code>start</code> and <code>end</code> are not primary keys, they are indexes in the result set. Thus, <code>0</code> refers to the first result in the set. Setting both <code>start</code> and <code>end</code> to <code>QueryUtil#ALL_POS</code> will return the full result set. If <code>orderByComparator</code> is specified, then the query will include the given ORDER BY logic. If <code>orderByComparator</code> is absent, then the query will include the default ORDER BY logic from <code>FragmentEntryLinkModelImpl</code>.
	 * </p>
	 *
	 * @param groupId the group ID
	 * @param fragmentEntryERC the fragment entry erc
	 * @param fragmentEntryScopeERC the fragment entry scope erc
	 * @param plid the plid
	 * @param start the lower bound of the range of fragment entry links
	 * @param end the upper bound of the range of fragment entry links (not inclusive)
	 * @param orderByComparator the comparator to order the results by (optionally <code>null</code>)
	 * @param useFinderCache whether to use the finder cache
	 * @return the ordered range of matching fragment entry links
	 */
	public java.util.List<FragmentEntryLink> findByG_FEERC_FESERC_P(
		long groupId, String fragmentEntryERC, String fragmentEntryScopeERC,
		long plid, int start, int end,
		com.liferay.portal.kernel.util.OrderByComparator<FragmentEntryLink>
			orderByComparator,
		boolean useFinderCache);

	/**
	 * Returns the first fragment entry link in the ordered set where groupId = &#63; and fragmentEntryERC = &#63; and fragmentEntryScopeERC = &#63; and plid = &#63;.
	 *
	 * @param groupId the group ID
	 * @param fragmentEntryERC the fragment entry erc
	 * @param fragmentEntryScopeERC the fragment entry scope erc
	 * @param plid the plid
	 * @param orderByComparator the comparator to order the set by (optionally <code>null</code>)
	 * @return the first matching fragment entry link
	 * @throws NoSuchEntryLinkException if a matching fragment entry link could not be found
	 */
	public FragmentEntryLink findByG_FEERC_FESERC_P_First(
			long groupId, String fragmentEntryERC, String fragmentEntryScopeERC,
			long plid,
			com.liferay.portal.kernel.util.OrderByComparator<FragmentEntryLink>
				orderByComparator)
		throws NoSuchEntryLinkException;

	/**
	 * Returns the first fragment entry link in the ordered set where groupId = &#63; and fragmentEntryERC = &#63; and fragmentEntryScopeERC = &#63; and plid = &#63;.
	 *
	 * @param groupId the group ID
	 * @param fragmentEntryERC the fragment entry erc
	 * @param fragmentEntryScopeERC the fragment entry scope erc
	 * @param plid the plid
	 * @param orderByComparator the comparator to order the set by (optionally <code>null</code>)
	 * @return the first matching fragment entry link, or <code>null</code> if a matching fragment entry link could not be found
	 */
	public FragmentEntryLink fetchByG_FEERC_FESERC_P_First(
		long groupId, String fragmentEntryERC, String fragmentEntryScopeERC,
		long plid,
		com.liferay.portal.kernel.util.OrderByComparator<FragmentEntryLink>
			orderByComparator);

	/**
	 * Returns the last fragment entry link in the ordered set where groupId = &#63; and fragmentEntryERC = &#63; and fragmentEntryScopeERC = &#63; and plid = &#63;.
	 *
	 * @param groupId the group ID
	 * @param fragmentEntryERC the fragment entry erc
	 * @param fragmentEntryScopeERC the fragment entry scope erc
	 * @param plid the plid
	 * @param orderByComparator the comparator to order the set by (optionally <code>null</code>)
	 * @return the last matching fragment entry link
	 * @throws NoSuchEntryLinkException if a matching fragment entry link could not be found
	 */
	public FragmentEntryLink findByG_FEERC_FESERC_P_Last(
			long groupId, String fragmentEntryERC, String fragmentEntryScopeERC,
			long plid,
			com.liferay.portal.kernel.util.OrderByComparator<FragmentEntryLink>
				orderByComparator)
		throws NoSuchEntryLinkException;

	/**
	 * Returns the last fragment entry link in the ordered set where groupId = &#63; and fragmentEntryERC = &#63; and fragmentEntryScopeERC = &#63; and plid = &#63;.
	 *
	 * @param groupId the group ID
	 * @param fragmentEntryERC the fragment entry erc
	 * @param fragmentEntryScopeERC the fragment entry scope erc
	 * @param plid the plid
	 * @param orderByComparator the comparator to order the set by (optionally <code>null</code>)
	 * @return the last matching fragment entry link, or <code>null</code> if a matching fragment entry link could not be found
	 */
	public FragmentEntryLink fetchByG_FEERC_FESERC_P_Last(
		long groupId, String fragmentEntryERC, String fragmentEntryScopeERC,
		long plid,
		com.liferay.portal.kernel.util.OrderByComparator<FragmentEntryLink>
			orderByComparator);

	/**
	 * Returns the fragment entry links before and after the current fragment entry link in the ordered set where groupId = &#63; and fragmentEntryERC = &#63; and fragmentEntryScopeERC = &#63; and plid = &#63;.
	 *
	 * @param fragmentEntryLinkId the primary key of the current fragment entry link
	 * @param groupId the group ID
	 * @param fragmentEntryERC the fragment entry erc
	 * @param fragmentEntryScopeERC the fragment entry scope erc
	 * @param plid the plid
	 * @param orderByComparator the comparator to order the set by (optionally <code>null</code>)
	 * @return the previous, current, and next fragment entry link
	 * @throws NoSuchEntryLinkException if a fragment entry link with the primary key could not be found
	 */
	public FragmentEntryLink[] findByG_FEERC_FESERC_P_PrevAndNext(
			long fragmentEntryLinkId, long groupId, String fragmentEntryERC,
			String fragmentEntryScopeERC, long plid,
			com.liferay.portal.kernel.util.OrderByComparator<FragmentEntryLink>
				orderByComparator)
		throws NoSuchEntryLinkException;

	/**
	 * Removes all the fragment entry links where groupId = &#63; and fragmentEntryERC = &#63; and fragmentEntryScopeERC = &#63; and plid = &#63; from the database.
	 *
	 * @param groupId the group ID
	 * @param fragmentEntryERC the fragment entry erc
	 * @param fragmentEntryScopeERC the fragment entry scope erc
	 * @param plid the plid
	 */
	public void removeByG_FEERC_FESERC_P(
		long groupId, String fragmentEntryERC, String fragmentEntryScopeERC,
		long plid);

	/**
	 * Returns the number of fragment entry links where groupId = &#63; and fragmentEntryERC = &#63; and fragmentEntryScopeERC = &#63; and plid = &#63;.
	 *
	 * @param groupId the group ID
	 * @param fragmentEntryERC the fragment entry erc
	 * @param fragmentEntryScopeERC the fragment entry scope erc
	 * @param plid the plid
	 * @return the number of matching fragment entry links
	 */
	public int countByG_FEERC_FESERC_P(
		long groupId, String fragmentEntryERC, String fragmentEntryScopeERC,
		long plid);

	/**
	 * Returns all the fragment entry links where groupId = &#63; and fragmentEntryERC = &#63; and fragmentEntryScopeERC = &#63; and deleted = &#63;.
	 *
	 * @param groupId the group ID
	 * @param fragmentEntryERC the fragment entry erc
	 * @param fragmentEntryScopeERC the fragment entry scope erc
	 * @param deleted the deleted
	 * @return the matching fragment entry links
	 */
	public java.util.List<FragmentEntryLink> findByG_FEERC_FESERC_D(
		long groupId, String fragmentEntryERC, String fragmentEntryScopeERC,
		boolean deleted);

	/**
	 * Returns a range of all the fragment entry links where groupId = &#63; and fragmentEntryERC = &#63; and fragmentEntryScopeERC = &#63; and deleted = &#63;.
	 *
	 * <p>
	 * Useful when paginating results. Returns a maximum of <code>end - start</code> instances. <code>start</code> and <code>end</code> are not primary keys, they are indexes in the result set. Thus, <code>0</code> refers to the first result in the set. Setting both <code>start</code> and <code>end</code> to <code>QueryUtil#ALL_POS</code> will return the full result set. If <code>orderByComparator</code> is specified, then the query will include the given ORDER BY logic. If <code>orderByComparator</code> is absent, then the query will include the default ORDER BY logic from <code>FragmentEntryLinkModelImpl</code>.
	 * </p>
	 *
	 * @param groupId the group ID
	 * @param fragmentEntryERC the fragment entry erc
	 * @param fragmentEntryScopeERC the fragment entry scope erc
	 * @param deleted the deleted
	 * @param start the lower bound of the range of fragment entry links
	 * @param end the upper bound of the range of fragment entry links (not inclusive)
	 * @return the range of matching fragment entry links
	 */
	public java.util.List<FragmentEntryLink> findByG_FEERC_FESERC_D(
		long groupId, String fragmentEntryERC, String fragmentEntryScopeERC,
		boolean deleted, int start, int end);

	/**
	 * Returns an ordered range of all the fragment entry links where groupId = &#63; and fragmentEntryERC = &#63; and fragmentEntryScopeERC = &#63; and deleted = &#63;.
	 *
	 * <p>
	 * Useful when paginating results. Returns a maximum of <code>end - start</code> instances. <code>start</code> and <code>end</code> are not primary keys, they are indexes in the result set. Thus, <code>0</code> refers to the first result in the set. Setting both <code>start</code> and <code>end</code> to <code>QueryUtil#ALL_POS</code> will return the full result set. If <code>orderByComparator</code> is specified, then the query will include the given ORDER BY logic. If <code>orderByComparator</code> is absent, then the query will include the default ORDER BY logic from <code>FragmentEntryLinkModelImpl</code>.
	 * </p>
	 *
	 * @param groupId the group ID
	 * @param fragmentEntryERC the fragment entry erc
	 * @param fragmentEntryScopeERC the fragment entry scope erc
	 * @param deleted the deleted
	 * @param start the lower bound of the range of fragment entry links
	 * @param end the upper bound of the range of fragment entry links (not inclusive)
	 * @param orderByComparator the comparator to order the results by (optionally <code>null</code>)
	 * @return the ordered range of matching fragment entry links
	 */
	public java.util.List<FragmentEntryLink> findByG_FEERC_FESERC_D(
		long groupId, String fragmentEntryERC, String fragmentEntryScopeERC,
		boolean deleted, int start, int end,
		com.liferay.portal.kernel.util.OrderByComparator<FragmentEntryLink>
			orderByComparator);

	/**
	 * Returns an ordered range of all the fragment entry links where groupId = &#63; and fragmentEntryERC = &#63; and fragmentEntryScopeERC = &#63; and deleted = &#63;.
	 *
	 * <p>
	 * Useful when paginating results. Returns a maximum of <code>end - start</code> instances. <code>start</code> and <code>end</code> are not primary keys, they are indexes in the result set. Thus, <code>0</code> refers to the first result in the set. Setting both <code>start</code> and <code>end</code> to <code>QueryUtil#ALL_POS</code> will return the full result set. If <code>orderByComparator</code> is specified, then the query will include the given ORDER BY logic. If <code>orderByComparator</code> is absent, then the query will include the default ORDER BY logic from <code>FragmentEntryLinkModelImpl</code>.
	 * </p>
	 *
	 * @param groupId the group ID
	 * @param fragmentEntryERC the fragment entry erc
	 * @param fragmentEntryScopeERC the fragment entry scope erc
	 * @param deleted the deleted
	 * @param start the lower bound of the range of fragment entry links
	 * @param end the upper bound of the range of fragment entry links (not inclusive)
	 * @param orderByComparator the comparator to order the results by (optionally <code>null</code>)
	 * @param useFinderCache whether to use the finder cache
	 * @return the ordered range of matching fragment entry links
	 */
	public java.util.List<FragmentEntryLink> findByG_FEERC_FESERC_D(
		long groupId, String fragmentEntryERC, String fragmentEntryScopeERC,
		boolean deleted, int start, int end,
		com.liferay.portal.kernel.util.OrderByComparator<FragmentEntryLink>
			orderByComparator,
		boolean useFinderCache);

	/**
	 * Returns the first fragment entry link in the ordered set where groupId = &#63; and fragmentEntryERC = &#63; and fragmentEntryScopeERC = &#63; and deleted = &#63;.
	 *
	 * @param groupId the group ID
	 * @param fragmentEntryERC the fragment entry erc
	 * @param fragmentEntryScopeERC the fragment entry scope erc
	 * @param deleted the deleted
	 * @param orderByComparator the comparator to order the set by (optionally <code>null</code>)
	 * @return the first matching fragment entry link
	 * @throws NoSuchEntryLinkException if a matching fragment entry link could not be found
	 */
	public FragmentEntryLink findByG_FEERC_FESERC_D_First(
			long groupId, String fragmentEntryERC, String fragmentEntryScopeERC,
			boolean deleted,
			com.liferay.portal.kernel.util.OrderByComparator<FragmentEntryLink>
				orderByComparator)
		throws NoSuchEntryLinkException;

	/**
	 * Returns the first fragment entry link in the ordered set where groupId = &#63; and fragmentEntryERC = &#63; and fragmentEntryScopeERC = &#63; and deleted = &#63;.
	 *
	 * @param groupId the group ID
	 * @param fragmentEntryERC the fragment entry erc
	 * @param fragmentEntryScopeERC the fragment entry scope erc
	 * @param deleted the deleted
	 * @param orderByComparator the comparator to order the set by (optionally <code>null</code>)
	 * @return the first matching fragment entry link, or <code>null</code> if a matching fragment entry link could not be found
	 */
	public FragmentEntryLink fetchByG_FEERC_FESERC_D_First(
		long groupId, String fragmentEntryERC, String fragmentEntryScopeERC,
		boolean deleted,
		com.liferay.portal.kernel.util.OrderByComparator<FragmentEntryLink>
			orderByComparator);

	/**
	 * Returns the last fragment entry link in the ordered set where groupId = &#63; and fragmentEntryERC = &#63; and fragmentEntryScopeERC = &#63; and deleted = &#63;.
	 *
	 * @param groupId the group ID
	 * @param fragmentEntryERC the fragment entry erc
	 * @param fragmentEntryScopeERC the fragment entry scope erc
	 * @param deleted the deleted
	 * @param orderByComparator the comparator to order the set by (optionally <code>null</code>)
	 * @return the last matching fragment entry link
	 * @throws NoSuchEntryLinkException if a matching fragment entry link could not be found
	 */
	public FragmentEntryLink findByG_FEERC_FESERC_D_Last(
			long groupId, String fragmentEntryERC, String fragmentEntryScopeERC,
			boolean deleted,
			com.liferay.portal.kernel.util.OrderByComparator<FragmentEntryLink>
				orderByComparator)
		throws NoSuchEntryLinkException;

	/**
	 * Returns the last fragment entry link in the ordered set where groupId = &#63; and fragmentEntryERC = &#63; and fragmentEntryScopeERC = &#63; and deleted = &#63;.
	 *
	 * @param groupId the group ID
	 * @param fragmentEntryERC the fragment entry erc
	 * @param fragmentEntryScopeERC the fragment entry scope erc
	 * @param deleted the deleted
	 * @param orderByComparator the comparator to order the set by (optionally <code>null</code>)
	 * @return the last matching fragment entry link, or <code>null</code> if a matching fragment entry link could not be found
	 */
	public FragmentEntryLink fetchByG_FEERC_FESERC_D_Last(
		long groupId, String fragmentEntryERC, String fragmentEntryScopeERC,
		boolean deleted,
		com.liferay.portal.kernel.util.OrderByComparator<FragmentEntryLink>
			orderByComparator);

	/**
	 * Returns the fragment entry links before and after the current fragment entry link in the ordered set where groupId = &#63; and fragmentEntryERC = &#63; and fragmentEntryScopeERC = &#63; and deleted = &#63;.
	 *
	 * @param fragmentEntryLinkId the primary key of the current fragment entry link
	 * @param groupId the group ID
	 * @param fragmentEntryERC the fragment entry erc
	 * @param fragmentEntryScopeERC the fragment entry scope erc
	 * @param deleted the deleted
	 * @param orderByComparator the comparator to order the set by (optionally <code>null</code>)
	 * @return the previous, current, and next fragment entry link
	 * @throws NoSuchEntryLinkException if a fragment entry link with the primary key could not be found
	 */
	public FragmentEntryLink[] findByG_FEERC_FESERC_D_PrevAndNext(
			long fragmentEntryLinkId, long groupId, String fragmentEntryERC,
			String fragmentEntryScopeERC, boolean deleted,
			com.liferay.portal.kernel.util.OrderByComparator<FragmentEntryLink>
				orderByComparator)
		throws NoSuchEntryLinkException;

	/**
	 * Removes all the fragment entry links where groupId = &#63; and fragmentEntryERC = &#63; and fragmentEntryScopeERC = &#63; and deleted = &#63; from the database.
	 *
	 * @param groupId the group ID
	 * @param fragmentEntryERC the fragment entry erc
	 * @param fragmentEntryScopeERC the fragment entry scope erc
	 * @param deleted the deleted
	 */
	public void removeByG_FEERC_FESERC_D(
		long groupId, String fragmentEntryERC, String fragmentEntryScopeERC,
		boolean deleted);

	/**
	 * Returns the number of fragment entry links where groupId = &#63; and fragmentEntryERC = &#63; and fragmentEntryScopeERC = &#63; and deleted = &#63;.
	 *
	 * @param groupId the group ID
	 * @param fragmentEntryERC the fragment entry erc
	 * @param fragmentEntryScopeERC the fragment entry scope erc
	 * @param deleted the deleted
	 * @return the number of matching fragment entry links
	 */
	public int countByG_FEERC_FESERC_D(
		long groupId, String fragmentEntryERC, String fragmentEntryScopeERC,
		boolean deleted);

	/**
	 * Returns all the fragment entry links where groupId = &#63; and segmentsExperienceId = &#63; and classNameId = &#63; and classPK = &#63;.
	 *
	 * @param groupId the group ID
	 * @param segmentsExperienceId the segments experience ID
	 * @param classNameId the class name ID
	 * @param classPK the class pk
	 * @return the matching fragment entry links
	 */
	public java.util.List<FragmentEntryLink> findByG_S_C_C(
		long groupId, long segmentsExperienceId, long classNameId,
		long classPK);

	/**
	 * Returns a range of all the fragment entry links where groupId = &#63; and segmentsExperienceId = &#63; and classNameId = &#63; and classPK = &#63;.
	 *
	 * <p>
	 * Useful when paginating results. Returns a maximum of <code>end - start</code> instances. <code>start</code> and <code>end</code> are not primary keys, they are indexes in the result set. Thus, <code>0</code> refers to the first result in the set. Setting both <code>start</code> and <code>end</code> to <code>QueryUtil#ALL_POS</code> will return the full result set. If <code>orderByComparator</code> is specified, then the query will include the given ORDER BY logic. If <code>orderByComparator</code> is absent, then the query will include the default ORDER BY logic from <code>FragmentEntryLinkModelImpl</code>.
	 * </p>
	 *
	 * @param groupId the group ID
	 * @param segmentsExperienceId the segments experience ID
	 * @param classNameId the class name ID
	 * @param classPK the class pk
	 * @param start the lower bound of the range of fragment entry links
	 * @param end the upper bound of the range of fragment entry links (not inclusive)
	 * @return the range of matching fragment entry links
	 */
	public java.util.List<FragmentEntryLink> findByG_S_C_C(
		long groupId, long segmentsExperienceId, long classNameId, long classPK,
		int start, int end);

	/**
	 * Returns an ordered range of all the fragment entry links where groupId = &#63; and segmentsExperienceId = &#63; and classNameId = &#63; and classPK = &#63;.
	 *
	 * <p>
	 * Useful when paginating results. Returns a maximum of <code>end - start</code> instances. <code>start</code> and <code>end</code> are not primary keys, they are indexes in the result set. Thus, <code>0</code> refers to the first result in the set. Setting both <code>start</code> and <code>end</code> to <code>QueryUtil#ALL_POS</code> will return the full result set. If <code>orderByComparator</code> is specified, then the query will include the given ORDER BY logic. If <code>orderByComparator</code> is absent, then the query will include the default ORDER BY logic from <code>FragmentEntryLinkModelImpl</code>.
	 * </p>
	 *
	 * @param groupId the group ID
	 * @param segmentsExperienceId the segments experience ID
	 * @param classNameId the class name ID
	 * @param classPK the class pk
	 * @param start the lower bound of the range of fragment entry links
	 * @param end the upper bound of the range of fragment entry links (not inclusive)
	 * @param orderByComparator the comparator to order the results by (optionally <code>null</code>)
	 * @return the ordered range of matching fragment entry links
	 */
	public java.util.List<FragmentEntryLink> findByG_S_C_C(
		long groupId, long segmentsExperienceId, long classNameId, long classPK,
		int start, int end,
		com.liferay.portal.kernel.util.OrderByComparator<FragmentEntryLink>
			orderByComparator);

	/**
	 * Returns an ordered range of all the fragment entry links where groupId = &#63; and segmentsExperienceId = &#63; and classNameId = &#63; and classPK = &#63;.
	 *
	 * <p>
	 * Useful when paginating results. Returns a maximum of <code>end - start</code> instances. <code>start</code> and <code>end</code> are not primary keys, they are indexes in the result set. Thus, <code>0</code> refers to the first result in the set. Setting both <code>start</code> and <code>end</code> to <code>QueryUtil#ALL_POS</code> will return the full result set. If <code>orderByComparator</code> is specified, then the query will include the given ORDER BY logic. If <code>orderByComparator</code> is absent, then the query will include the default ORDER BY logic from <code>FragmentEntryLinkModelImpl</code>.
	 * </p>
	 *
	 * @param groupId the group ID
	 * @param segmentsExperienceId the segments experience ID
	 * @param classNameId the class name ID
	 * @param classPK the class pk
	 * @param start the lower bound of the range of fragment entry links
	 * @param end the upper bound of the range of fragment entry links (not inclusive)
	 * @param orderByComparator the comparator to order the results by (optionally <code>null</code>)
	 * @param useFinderCache whether to use the finder cache
	 * @return the ordered range of matching fragment entry links
	 */
	public java.util.List<FragmentEntryLink> findByG_S_C_C(
		long groupId, long segmentsExperienceId, long classNameId, long classPK,
		int start, int end,
		com.liferay.portal.kernel.util.OrderByComparator<FragmentEntryLink>
			orderByComparator,
		boolean useFinderCache);

	/**
	 * Returns the first fragment entry link in the ordered set where groupId = &#63; and segmentsExperienceId = &#63; and classNameId = &#63; and classPK = &#63;.
	 *
	 * @param groupId the group ID
	 * @param segmentsExperienceId the segments experience ID
	 * @param classNameId the class name ID
	 * @param classPK the class pk
	 * @param orderByComparator the comparator to order the set by (optionally <code>null</code>)
	 * @return the first matching fragment entry link
	 * @throws NoSuchEntryLinkException if a matching fragment entry link could not be found
	 */
	public FragmentEntryLink findByG_S_C_C_First(
			long groupId, long segmentsExperienceId, long classNameId,
			long classPK,
			com.liferay.portal.kernel.util.OrderByComparator<FragmentEntryLink>
				orderByComparator)
		throws NoSuchEntryLinkException;

	/**
	 * Returns the first fragment entry link in the ordered set where groupId = &#63; and segmentsExperienceId = &#63; and classNameId = &#63; and classPK = &#63;.
	 *
	 * @param groupId the group ID
	 * @param segmentsExperienceId the segments experience ID
	 * @param classNameId the class name ID
	 * @param classPK the class pk
	 * @param orderByComparator the comparator to order the set by (optionally <code>null</code>)
	 * @return the first matching fragment entry link, or <code>null</code> if a matching fragment entry link could not be found
	 */
	public FragmentEntryLink fetchByG_S_C_C_First(
		long groupId, long segmentsExperienceId, long classNameId, long classPK,
		com.liferay.portal.kernel.util.OrderByComparator<FragmentEntryLink>
			orderByComparator);

	/**
	 * Returns the last fragment entry link in the ordered set where groupId = &#63; and segmentsExperienceId = &#63; and classNameId = &#63; and classPK = &#63;.
	 *
	 * @param groupId the group ID
	 * @param segmentsExperienceId the segments experience ID
	 * @param classNameId the class name ID
	 * @param classPK the class pk
	 * @param orderByComparator the comparator to order the set by (optionally <code>null</code>)
	 * @return the last matching fragment entry link
	 * @throws NoSuchEntryLinkException if a matching fragment entry link could not be found
	 */
	public FragmentEntryLink findByG_S_C_C_Last(
			long groupId, long segmentsExperienceId, long classNameId,
			long classPK,
			com.liferay.portal.kernel.util.OrderByComparator<FragmentEntryLink>
				orderByComparator)
		throws NoSuchEntryLinkException;

	/**
	 * Returns the last fragment entry link in the ordered set where groupId = &#63; and segmentsExperienceId = &#63; and classNameId = &#63; and classPK = &#63;.
	 *
	 * @param groupId the group ID
	 * @param segmentsExperienceId the segments experience ID
	 * @param classNameId the class name ID
	 * @param classPK the class pk
	 * @param orderByComparator the comparator to order the set by (optionally <code>null</code>)
	 * @return the last matching fragment entry link, or <code>null</code> if a matching fragment entry link could not be found
	 */
	public FragmentEntryLink fetchByG_S_C_C_Last(
		long groupId, long segmentsExperienceId, long classNameId, long classPK,
		com.liferay.portal.kernel.util.OrderByComparator<FragmentEntryLink>
			orderByComparator);

	/**
	 * Returns the fragment entry links before and after the current fragment entry link in the ordered set where groupId = &#63; and segmentsExperienceId = &#63; and classNameId = &#63; and classPK = &#63;.
	 *
	 * @param fragmentEntryLinkId the primary key of the current fragment entry link
	 * @param groupId the group ID
	 * @param segmentsExperienceId the segments experience ID
	 * @param classNameId the class name ID
	 * @param classPK the class pk
	 * @param orderByComparator the comparator to order the set by (optionally <code>null</code>)
	 * @return the previous, current, and next fragment entry link
	 * @throws NoSuchEntryLinkException if a fragment entry link with the primary key could not be found
	 */
	public FragmentEntryLink[] findByG_S_C_C_PrevAndNext(
			long fragmentEntryLinkId, long groupId, long segmentsExperienceId,
			long classNameId, long classPK,
			com.liferay.portal.kernel.util.OrderByComparator<FragmentEntryLink>
				orderByComparator)
		throws NoSuchEntryLinkException;

	/**
	 * Removes all the fragment entry links where groupId = &#63; and segmentsExperienceId = &#63; and classNameId = &#63; and classPK = &#63; from the database.
	 *
	 * @param groupId the group ID
	 * @param segmentsExperienceId the segments experience ID
	 * @param classNameId the class name ID
	 * @param classPK the class pk
	 */
	public void removeByG_S_C_C(
		long groupId, long segmentsExperienceId, long classNameId,
		long classPK);

	/**
	 * Returns the number of fragment entry links where groupId = &#63; and segmentsExperienceId = &#63; and classNameId = &#63; and classPK = &#63;.
	 *
	 * @param groupId the group ID
	 * @param segmentsExperienceId the segments experience ID
	 * @param classNameId the class name ID
	 * @param classPK the class pk
	 * @return the number of matching fragment entry links
	 */
	public int countByG_S_C_C(
		long groupId, long segmentsExperienceId, long classNameId,
		long classPK);

	/**
	 * Returns all the fragment entry links where groupId = &#63; and segmentsExperienceId = &#63; and plid = &#63; and deleted = &#63;.
	 *
	 * @param groupId the group ID
	 * @param segmentsExperienceId the segments experience ID
	 * @param plid the plid
	 * @param deleted the deleted
	 * @return the matching fragment entry links
	 */
	public java.util.List<FragmentEntryLink> findByG_S_P_D(
		long groupId, long segmentsExperienceId, long plid, boolean deleted);

	/**
	 * Returns a range of all the fragment entry links where groupId = &#63; and segmentsExperienceId = &#63; and plid = &#63; and deleted = &#63;.
	 *
	 * <p>
	 * Useful when paginating results. Returns a maximum of <code>end - start</code> instances. <code>start</code> and <code>end</code> are not primary keys, they are indexes in the result set. Thus, <code>0</code> refers to the first result in the set. Setting both <code>start</code> and <code>end</code> to <code>QueryUtil#ALL_POS</code> will return the full result set. If <code>orderByComparator</code> is specified, then the query will include the given ORDER BY logic. If <code>orderByComparator</code> is absent, then the query will include the default ORDER BY logic from <code>FragmentEntryLinkModelImpl</code>.
	 * </p>
	 *
	 * @param groupId the group ID
	 * @param segmentsExperienceId the segments experience ID
	 * @param plid the plid
	 * @param deleted the deleted
	 * @param start the lower bound of the range of fragment entry links
	 * @param end the upper bound of the range of fragment entry links (not inclusive)
	 * @return the range of matching fragment entry links
	 */
	public java.util.List<FragmentEntryLink> findByG_S_P_D(
		long groupId, long segmentsExperienceId, long plid, boolean deleted,
		int start, int end);

	/**
	 * Returns an ordered range of all the fragment entry links where groupId = &#63; and segmentsExperienceId = &#63; and plid = &#63; and deleted = &#63;.
	 *
	 * <p>
	 * Useful when paginating results. Returns a maximum of <code>end - start</code> instances. <code>start</code> and <code>end</code> are not primary keys, they are indexes in the result set. Thus, <code>0</code> refers to the first result in the set. Setting both <code>start</code> and <code>end</code> to <code>QueryUtil#ALL_POS</code> will return the full result set. If <code>orderByComparator</code> is specified, then the query will include the given ORDER BY logic. If <code>orderByComparator</code> is absent, then the query will include the default ORDER BY logic from <code>FragmentEntryLinkModelImpl</code>.
	 * </p>
	 *
	 * @param groupId the group ID
	 * @param segmentsExperienceId the segments experience ID
	 * @param plid the plid
	 * @param deleted the deleted
	 * @param start the lower bound of the range of fragment entry links
	 * @param end the upper bound of the range of fragment entry links (not inclusive)
	 * @param orderByComparator the comparator to order the results by (optionally <code>null</code>)
	 * @return the ordered range of matching fragment entry links
	 */
	public java.util.List<FragmentEntryLink> findByG_S_P_D(
		long groupId, long segmentsExperienceId, long plid, boolean deleted,
		int start, int end,
		com.liferay.portal.kernel.util.OrderByComparator<FragmentEntryLink>
			orderByComparator);

	/**
	 * Returns an ordered range of all the fragment entry links where groupId = &#63; and segmentsExperienceId = &#63; and plid = &#63; and deleted = &#63;.
	 *
	 * <p>
	 * Useful when paginating results. Returns a maximum of <code>end - start</code> instances. <code>start</code> and <code>end</code> are not primary keys, they are indexes in the result set. Thus, <code>0</code> refers to the first result in the set. Setting both <code>start</code> and <code>end</code> to <code>QueryUtil#ALL_POS</code> will return the full result set. If <code>orderByComparator</code> is specified, then the query will include the given ORDER BY logic. If <code>orderByComparator</code> is absent, then the query will include the default ORDER BY logic from <code>FragmentEntryLinkModelImpl</code>.
	 * </p>
	 *
	 * @param groupId the group ID
	 * @param segmentsExperienceId the segments experience ID
	 * @param plid the plid
	 * @param deleted the deleted
	 * @param start the lower bound of the range of fragment entry links
	 * @param end the upper bound of the range of fragment entry links (not inclusive)
	 * @param orderByComparator the comparator to order the results by (optionally <code>null</code>)
	 * @param useFinderCache whether to use the finder cache
	 * @return the ordered range of matching fragment entry links
	 */
	public java.util.List<FragmentEntryLink> findByG_S_P_D(
		long groupId, long segmentsExperienceId, long plid, boolean deleted,
		int start, int end,
		com.liferay.portal.kernel.util.OrderByComparator<FragmentEntryLink>
			orderByComparator,
		boolean useFinderCache);

	/**
	 * Returns the first fragment entry link in the ordered set where groupId = &#63; and segmentsExperienceId = &#63; and plid = &#63; and deleted = &#63;.
	 *
	 * @param groupId the group ID
	 * @param segmentsExperienceId the segments experience ID
	 * @param plid the plid
	 * @param deleted the deleted
	 * @param orderByComparator the comparator to order the set by (optionally <code>null</code>)
	 * @return the first matching fragment entry link
	 * @throws NoSuchEntryLinkException if a matching fragment entry link could not be found
	 */
	public FragmentEntryLink findByG_S_P_D_First(
			long groupId, long segmentsExperienceId, long plid, boolean deleted,
			com.liferay.portal.kernel.util.OrderByComparator<FragmentEntryLink>
				orderByComparator)
		throws NoSuchEntryLinkException;

	/**
	 * Returns the first fragment entry link in the ordered set where groupId = &#63; and segmentsExperienceId = &#63; and plid = &#63; and deleted = &#63;.
	 *
	 * @param groupId the group ID
	 * @param segmentsExperienceId the segments experience ID
	 * @param plid the plid
	 * @param deleted the deleted
	 * @param orderByComparator the comparator to order the set by (optionally <code>null</code>)
	 * @return the first matching fragment entry link, or <code>null</code> if a matching fragment entry link could not be found
	 */
	public FragmentEntryLink fetchByG_S_P_D_First(
		long groupId, long segmentsExperienceId, long plid, boolean deleted,
		com.liferay.portal.kernel.util.OrderByComparator<FragmentEntryLink>
			orderByComparator);

	/**
	 * Returns the last fragment entry link in the ordered set where groupId = &#63; and segmentsExperienceId = &#63; and plid = &#63; and deleted = &#63;.
	 *
	 * @param groupId the group ID
	 * @param segmentsExperienceId the segments experience ID
	 * @param plid the plid
	 * @param deleted the deleted
	 * @param orderByComparator the comparator to order the set by (optionally <code>null</code>)
	 * @return the last matching fragment entry link
	 * @throws NoSuchEntryLinkException if a matching fragment entry link could not be found
	 */
	public FragmentEntryLink findByG_S_P_D_Last(
			long groupId, long segmentsExperienceId, long plid, boolean deleted,
			com.liferay.portal.kernel.util.OrderByComparator<FragmentEntryLink>
				orderByComparator)
		throws NoSuchEntryLinkException;

	/**
	 * Returns the last fragment entry link in the ordered set where groupId = &#63; and segmentsExperienceId = &#63; and plid = &#63; and deleted = &#63;.
	 *
	 * @param groupId the group ID
	 * @param segmentsExperienceId the segments experience ID
	 * @param plid the plid
	 * @param deleted the deleted
	 * @param orderByComparator the comparator to order the set by (optionally <code>null</code>)
	 * @return the last matching fragment entry link, or <code>null</code> if a matching fragment entry link could not be found
	 */
	public FragmentEntryLink fetchByG_S_P_D_Last(
		long groupId, long segmentsExperienceId, long plid, boolean deleted,
		com.liferay.portal.kernel.util.OrderByComparator<FragmentEntryLink>
			orderByComparator);

	/**
	 * Returns the fragment entry links before and after the current fragment entry link in the ordered set where groupId = &#63; and segmentsExperienceId = &#63; and plid = &#63; and deleted = &#63;.
	 *
	 * @param fragmentEntryLinkId the primary key of the current fragment entry link
	 * @param groupId the group ID
	 * @param segmentsExperienceId the segments experience ID
	 * @param plid the plid
	 * @param deleted the deleted
	 * @param orderByComparator the comparator to order the set by (optionally <code>null</code>)
	 * @return the previous, current, and next fragment entry link
	 * @throws NoSuchEntryLinkException if a fragment entry link with the primary key could not be found
	 */
	public FragmentEntryLink[] findByG_S_P_D_PrevAndNext(
			long fragmentEntryLinkId, long groupId, long segmentsExperienceId,
			long plid, boolean deleted,
			com.liferay.portal.kernel.util.OrderByComparator<FragmentEntryLink>
				orderByComparator)
		throws NoSuchEntryLinkException;

	/**
	 * Returns all the fragment entry links where groupId = &#63; and segmentsExperienceId = any &#63; and plid = &#63; and deleted = &#63;.
	 *
	 * <p>
	 * Useful when paginating results. Returns a maximum of <code>end - start</code> instances. <code>start</code> and <code>end</code> are not primary keys, they are indexes in the result set. Thus, <code>0</code> refers to the first result in the set. Setting both <code>start</code> and <code>end</code> to <code>QueryUtil#ALL_POS</code> will return the full result set. If <code>orderByComparator</code> is specified, then the query will include the given ORDER BY logic. If <code>orderByComparator</code> is absent, then the query will include the default ORDER BY logic from <code>FragmentEntryLinkModelImpl</code>.
	 * </p>
	 *
	 * @param groupId the group ID
	 * @param segmentsExperienceIds the segments experience IDs
	 * @param plid the plid
	 * @param deleted the deleted
	 * @return the matching fragment entry links
	 */
	public java.util.List<FragmentEntryLink> findByG_S_P_D(
		long groupId, long[] segmentsExperienceIds, long plid, boolean deleted);

	/**
	 * Returns a range of all the fragment entry links where groupId = &#63; and segmentsExperienceId = any &#63; and plid = &#63; and deleted = &#63;.
	 *
	 * <p>
	 * Useful when paginating results. Returns a maximum of <code>end - start</code> instances. <code>start</code> and <code>end</code> are not primary keys, they are indexes in the result set. Thus, <code>0</code> refers to the first result in the set. Setting both <code>start</code> and <code>end</code> to <code>QueryUtil#ALL_POS</code> will return the full result set. If <code>orderByComparator</code> is specified, then the query will include the given ORDER BY logic. If <code>orderByComparator</code> is absent, then the query will include the default ORDER BY logic from <code>FragmentEntryLinkModelImpl</code>.
	 * </p>
	 *
	 * @param groupId the group ID
	 * @param segmentsExperienceIds the segments experience IDs
	 * @param plid the plid
	 * @param deleted the deleted
	 * @param start the lower bound of the range of fragment entry links
	 * @param end the upper bound of the range of fragment entry links (not inclusive)
	 * @return the range of matching fragment entry links
	 */
	public java.util.List<FragmentEntryLink> findByG_S_P_D(
		long groupId, long[] segmentsExperienceIds, long plid, boolean deleted,
		int start, int end);

	/**
	 * Returns an ordered range of all the fragment entry links where groupId = &#63; and segmentsExperienceId = any &#63; and plid = &#63; and deleted = &#63;.
	 *
	 * <p>
	 * Useful when paginating results. Returns a maximum of <code>end - start</code> instances. <code>start</code> and <code>end</code> are not primary keys, they are indexes in the result set. Thus, <code>0</code> refers to the first result in the set. Setting both <code>start</code> and <code>end</code> to <code>QueryUtil#ALL_POS</code> will return the full result set. If <code>orderByComparator</code> is specified, then the query will include the given ORDER BY logic. If <code>orderByComparator</code> is absent, then the query will include the default ORDER BY logic from <code>FragmentEntryLinkModelImpl</code>.
	 * </p>
	 *
	 * @param groupId the group ID
	 * @param segmentsExperienceIds the segments experience IDs
	 * @param plid the plid
	 * @param deleted the deleted
	 * @param start the lower bound of the range of fragment entry links
	 * @param end the upper bound of the range of fragment entry links (not inclusive)
	 * @param orderByComparator the comparator to order the results by (optionally <code>null</code>)
	 * @return the ordered range of matching fragment entry links
	 */
	public java.util.List<FragmentEntryLink> findByG_S_P_D(
		long groupId, long[] segmentsExperienceIds, long plid, boolean deleted,
		int start, int end,
		com.liferay.portal.kernel.util.OrderByComparator<FragmentEntryLink>
			orderByComparator);

	/**
	 * Returns an ordered range of all the fragment entry links where groupId = &#63; and segmentsExperienceId = &#63; and plid = &#63; and deleted = &#63;, optionally using the finder cache.
	 *
	 * <p>
	 * Useful when paginating results. Returns a maximum of <code>end - start</code> instances. <code>start</code> and <code>end</code> are not primary keys, they are indexes in the result set. Thus, <code>0</code> refers to the first result in the set. Setting both <code>start</code> and <code>end</code> to <code>QueryUtil#ALL_POS</code> will return the full result set. If <code>orderByComparator</code> is specified, then the query will include the given ORDER BY logic. If <code>orderByComparator</code> is absent, then the query will include the default ORDER BY logic from <code>FragmentEntryLinkModelImpl</code>.
	 * </p>
	 *
	 * @param groupId the group ID
	 * @param segmentsExperienceIds the segments experience IDs
	 * @param plid the plid
	 * @param deleted the deleted
	 * @param start the lower bound of the range of fragment entry links
	 * @param end the upper bound of the range of fragment entry links (not inclusive)
	 * @param orderByComparator the comparator to order the results by (optionally <code>null</code>)
	 * @param useFinderCache whether to use the finder cache
	 * @return the ordered range of matching fragment entry links
	 */
	public java.util.List<FragmentEntryLink> findByG_S_P_D(
		long groupId, long[] segmentsExperienceIds, long plid, boolean deleted,
		int start, int end,
		com.liferay.portal.kernel.util.OrderByComparator<FragmentEntryLink>
			orderByComparator,
		boolean useFinderCache);

	/**
	 * Removes all the fragment entry links where groupId = &#63; and segmentsExperienceId = &#63; and plid = &#63; and deleted = &#63; from the database.
	 *
	 * @param groupId the group ID
	 * @param segmentsExperienceId the segments experience ID
	 * @param plid the plid
	 * @param deleted the deleted
	 */
	public void removeByG_S_P_D(
		long groupId, long segmentsExperienceId, long plid, boolean deleted);

	/**
	 * Returns the number of fragment entry links where groupId = &#63; and segmentsExperienceId = &#63; and plid = &#63; and deleted = &#63;.
	 *
	 * @param groupId the group ID
	 * @param segmentsExperienceId the segments experience ID
	 * @param plid the plid
	 * @param deleted the deleted
	 * @return the number of matching fragment entry links
	 */
	public int countByG_S_P_D(
		long groupId, long segmentsExperienceId, long plid, boolean deleted);

	/**
	 * Returns the number of fragment entry links where groupId = &#63; and segmentsExperienceId = any &#63; and plid = &#63; and deleted = &#63;.
	 *
	 * @param groupId the group ID
	 * @param segmentsExperienceIds the segments experience IDs
	 * @param plid the plid
	 * @param deleted the deleted
	 * @return the number of matching fragment entry links
	 */
	public int countByG_S_P_D(
		long groupId, long[] segmentsExperienceIds, long plid, boolean deleted);

	/**
	 * Returns all the fragment entry links where groupId = &#63; and segmentsExperienceId = &#63; and plid = &#63; and rendererKey = &#63;.
	 *
	 * @param groupId the group ID
	 * @param segmentsExperienceId the segments experience ID
	 * @param plid the plid
	 * @param rendererKey the renderer key
	 * @return the matching fragment entry links
	 */
	public java.util.List<FragmentEntryLink> findByG_S_P_R(
		long groupId, long segmentsExperienceId, long plid, String rendererKey);

	/**
	 * Returns a range of all the fragment entry links where groupId = &#63; and segmentsExperienceId = &#63; and plid = &#63; and rendererKey = &#63;.
	 *
	 * <p>
	 * Useful when paginating results. Returns a maximum of <code>end - start</code> instances. <code>start</code> and <code>end</code> are not primary keys, they are indexes in the result set. Thus, <code>0</code> refers to the first result in the set. Setting both <code>start</code> and <code>end</code> to <code>QueryUtil#ALL_POS</code> will return the full result set. If <code>orderByComparator</code> is specified, then the query will include the given ORDER BY logic. If <code>orderByComparator</code> is absent, then the query will include the default ORDER BY logic from <code>FragmentEntryLinkModelImpl</code>.
	 * </p>
	 *
	 * @param groupId the group ID
	 * @param segmentsExperienceId the segments experience ID
	 * @param plid the plid
	 * @param rendererKey the renderer key
	 * @param start the lower bound of the range of fragment entry links
	 * @param end the upper bound of the range of fragment entry links (not inclusive)
	 * @return the range of matching fragment entry links
	 */
	public java.util.List<FragmentEntryLink> findByG_S_P_R(
		long groupId, long segmentsExperienceId, long plid, String rendererKey,
		int start, int end);

	/**
	 * Returns an ordered range of all the fragment entry links where groupId = &#63; and segmentsExperienceId = &#63; and plid = &#63; and rendererKey = &#63;.
	 *
	 * <p>
	 * Useful when paginating results. Returns a maximum of <code>end - start</code> instances. <code>start</code> and <code>end</code> are not primary keys, they are indexes in the result set. Thus, <code>0</code> refers to the first result in the set. Setting both <code>start</code> and <code>end</code> to <code>QueryUtil#ALL_POS</code> will return the full result set. If <code>orderByComparator</code> is specified, then the query will include the given ORDER BY logic. If <code>orderByComparator</code> is absent, then the query will include the default ORDER BY logic from <code>FragmentEntryLinkModelImpl</code>.
	 * </p>
	 *
	 * @param groupId the group ID
	 * @param segmentsExperienceId the segments experience ID
	 * @param plid the plid
	 * @param rendererKey the renderer key
	 * @param start the lower bound of the range of fragment entry links
	 * @param end the upper bound of the range of fragment entry links (not inclusive)
	 * @param orderByComparator the comparator to order the results by (optionally <code>null</code>)
	 * @return the ordered range of matching fragment entry links
	 */
	public java.util.List<FragmentEntryLink> findByG_S_P_R(
		long groupId, long segmentsExperienceId, long plid, String rendererKey,
		int start, int end,
		com.liferay.portal.kernel.util.OrderByComparator<FragmentEntryLink>
			orderByComparator);

	/**
	 * Returns an ordered range of all the fragment entry links where groupId = &#63; and segmentsExperienceId = &#63; and plid = &#63; and rendererKey = &#63;.
	 *
	 * <p>
	 * Useful when paginating results. Returns a maximum of <code>end - start</code> instances. <code>start</code> and <code>end</code> are not primary keys, they are indexes in the result set. Thus, <code>0</code> refers to the first result in the set. Setting both <code>start</code> and <code>end</code> to <code>QueryUtil#ALL_POS</code> will return the full result set. If <code>orderByComparator</code> is specified, then the query will include the given ORDER BY logic. If <code>orderByComparator</code> is absent, then the query will include the default ORDER BY logic from <code>FragmentEntryLinkModelImpl</code>.
	 * </p>
	 *
	 * @param groupId the group ID
	 * @param segmentsExperienceId the segments experience ID
	 * @param plid the plid
	 * @param rendererKey the renderer key
	 * @param start the lower bound of the range of fragment entry links
	 * @param end the upper bound of the range of fragment entry links (not inclusive)
	 * @param orderByComparator the comparator to order the results by (optionally <code>null</code>)
	 * @param useFinderCache whether to use the finder cache
	 * @return the ordered range of matching fragment entry links
	 */
	public java.util.List<FragmentEntryLink> findByG_S_P_R(
		long groupId, long segmentsExperienceId, long plid, String rendererKey,
		int start, int end,
		com.liferay.portal.kernel.util.OrderByComparator<FragmentEntryLink>
			orderByComparator,
		boolean useFinderCache);

	/**
	 * Returns the first fragment entry link in the ordered set where groupId = &#63; and segmentsExperienceId = &#63; and plid = &#63; and rendererKey = &#63;.
	 *
	 * @param groupId the group ID
	 * @param segmentsExperienceId the segments experience ID
	 * @param plid the plid
	 * @param rendererKey the renderer key
	 * @param orderByComparator the comparator to order the set by (optionally <code>null</code>)
	 * @return the first matching fragment entry link
	 * @throws NoSuchEntryLinkException if a matching fragment entry link could not be found
	 */
	public FragmentEntryLink findByG_S_P_R_First(
			long groupId, long segmentsExperienceId, long plid,
			String rendererKey,
			com.liferay.portal.kernel.util.OrderByComparator<FragmentEntryLink>
				orderByComparator)
		throws NoSuchEntryLinkException;

	/**
	 * Returns the first fragment entry link in the ordered set where groupId = &#63; and segmentsExperienceId = &#63; and plid = &#63; and rendererKey = &#63;.
	 *
	 * @param groupId the group ID
	 * @param segmentsExperienceId the segments experience ID
	 * @param plid the plid
	 * @param rendererKey the renderer key
	 * @param orderByComparator the comparator to order the set by (optionally <code>null</code>)
	 * @return the first matching fragment entry link, or <code>null</code> if a matching fragment entry link could not be found
	 */
	public FragmentEntryLink fetchByG_S_P_R_First(
		long groupId, long segmentsExperienceId, long plid, String rendererKey,
		com.liferay.portal.kernel.util.OrderByComparator<FragmentEntryLink>
			orderByComparator);

	/**
	 * Returns the last fragment entry link in the ordered set where groupId = &#63; and segmentsExperienceId = &#63; and plid = &#63; and rendererKey = &#63;.
	 *
	 * @param groupId the group ID
	 * @param segmentsExperienceId the segments experience ID
	 * @param plid the plid
	 * @param rendererKey the renderer key
	 * @param orderByComparator the comparator to order the set by (optionally <code>null</code>)
	 * @return the last matching fragment entry link
	 * @throws NoSuchEntryLinkException if a matching fragment entry link could not be found
	 */
	public FragmentEntryLink findByG_S_P_R_Last(
			long groupId, long segmentsExperienceId, long plid,
			String rendererKey,
			com.liferay.portal.kernel.util.OrderByComparator<FragmentEntryLink>
				orderByComparator)
		throws NoSuchEntryLinkException;

	/**
	 * Returns the last fragment entry link in the ordered set where groupId = &#63; and segmentsExperienceId = &#63; and plid = &#63; and rendererKey = &#63;.
	 *
	 * @param groupId the group ID
	 * @param segmentsExperienceId the segments experience ID
	 * @param plid the plid
	 * @param rendererKey the renderer key
	 * @param orderByComparator the comparator to order the set by (optionally <code>null</code>)
	 * @return the last matching fragment entry link, or <code>null</code> if a matching fragment entry link could not be found
	 */
	public FragmentEntryLink fetchByG_S_P_R_Last(
		long groupId, long segmentsExperienceId, long plid, String rendererKey,
		com.liferay.portal.kernel.util.OrderByComparator<FragmentEntryLink>
			orderByComparator);

	/**
	 * Returns the fragment entry links before and after the current fragment entry link in the ordered set where groupId = &#63; and segmentsExperienceId = &#63; and plid = &#63; and rendererKey = &#63;.
	 *
	 * @param fragmentEntryLinkId the primary key of the current fragment entry link
	 * @param groupId the group ID
	 * @param segmentsExperienceId the segments experience ID
	 * @param plid the plid
	 * @param rendererKey the renderer key
	 * @param orderByComparator the comparator to order the set by (optionally <code>null</code>)
	 * @return the previous, current, and next fragment entry link
	 * @throws NoSuchEntryLinkException if a fragment entry link with the primary key could not be found
	 */
	public FragmentEntryLink[] findByG_S_P_R_PrevAndNext(
			long fragmentEntryLinkId, long groupId, long segmentsExperienceId,
			long plid, String rendererKey,
			com.liferay.portal.kernel.util.OrderByComparator<FragmentEntryLink>
				orderByComparator)
		throws NoSuchEntryLinkException;

	/**
	 * Removes all the fragment entry links where groupId = &#63; and segmentsExperienceId = &#63; and plid = &#63; and rendererKey = &#63; from the database.
	 *
	 * @param groupId the group ID
	 * @param segmentsExperienceId the segments experience ID
	 * @param plid the plid
	 * @param rendererKey the renderer key
	 */
	public void removeByG_S_P_R(
		long groupId, long segmentsExperienceId, long plid, String rendererKey);

	/**
	 * Returns the number of fragment entry links where groupId = &#63; and segmentsExperienceId = &#63; and plid = &#63; and rendererKey = &#63;.
	 *
	 * @param groupId the group ID
	 * @param segmentsExperienceId the segments experience ID
	 * @param plid the plid
	 * @param rendererKey the renderer key
	 * @return the number of matching fragment entry links
	 */
	public int countByG_S_P_R(
		long groupId, long segmentsExperienceId, long plid, String rendererKey);

	/**
	 * Returns all the fragment entry links where groupId = &#63; and fragmentEntryERC = &#63; and fragmentEntryScopeERC = &#63; and classNameId = &#63; and classPK = &#63;.
	 *
	 * @param groupId the group ID
	 * @param fragmentEntryERC the fragment entry erc
	 * @param fragmentEntryScopeERC the fragment entry scope erc
	 * @param classNameId the class name ID
	 * @param classPK the class pk
	 * @return the matching fragment entry links
	 */
	public java.util.List<FragmentEntryLink> findByG_FEERC_FESERC_C_C(
		long groupId, String fragmentEntryERC, String fragmentEntryScopeERC,
		long classNameId, long classPK);

	/**
	 * Returns a range of all the fragment entry links where groupId = &#63; and fragmentEntryERC = &#63; and fragmentEntryScopeERC = &#63; and classNameId = &#63; and classPK = &#63;.
	 *
	 * <p>
	 * Useful when paginating results. Returns a maximum of <code>end - start</code> instances. <code>start</code> and <code>end</code> are not primary keys, they are indexes in the result set. Thus, <code>0</code> refers to the first result in the set. Setting both <code>start</code> and <code>end</code> to <code>QueryUtil#ALL_POS</code> will return the full result set. If <code>orderByComparator</code> is specified, then the query will include the given ORDER BY logic. If <code>orderByComparator</code> is absent, then the query will include the default ORDER BY logic from <code>FragmentEntryLinkModelImpl</code>.
	 * </p>
	 *
	 * @param groupId the group ID
	 * @param fragmentEntryERC the fragment entry erc
	 * @param fragmentEntryScopeERC the fragment entry scope erc
	 * @param classNameId the class name ID
	 * @param classPK the class pk
	 * @param start the lower bound of the range of fragment entry links
	 * @param end the upper bound of the range of fragment entry links (not inclusive)
	 * @return the range of matching fragment entry links
	 */
	public java.util.List<FragmentEntryLink> findByG_FEERC_FESERC_C_C(
		long groupId, String fragmentEntryERC, String fragmentEntryScopeERC,
		long classNameId, long classPK, int start, int end);

	/**
	 * Returns an ordered range of all the fragment entry links where groupId = &#63; and fragmentEntryERC = &#63; and fragmentEntryScopeERC = &#63; and classNameId = &#63; and classPK = &#63;.
	 *
	 * <p>
	 * Useful when paginating results. Returns a maximum of <code>end - start</code> instances. <code>start</code> and <code>end</code> are not primary keys, they are indexes in the result set. Thus, <code>0</code> refers to the first result in the set. Setting both <code>start</code> and <code>end</code> to <code>QueryUtil#ALL_POS</code> will return the full result set. If <code>orderByComparator</code> is specified, then the query will include the given ORDER BY logic. If <code>orderByComparator</code> is absent, then the query will include the default ORDER BY logic from <code>FragmentEntryLinkModelImpl</code>.
	 * </p>
	 *
	 * @param groupId the group ID
	 * @param fragmentEntryERC the fragment entry erc
	 * @param fragmentEntryScopeERC the fragment entry scope erc
	 * @param classNameId the class name ID
	 * @param classPK the class pk
	 * @param start the lower bound of the range of fragment entry links
	 * @param end the upper bound of the range of fragment entry links (not inclusive)
	 * @param orderByComparator the comparator to order the results by (optionally <code>null</code>)
	 * @return the ordered range of matching fragment entry links
	 */
	public java.util.List<FragmentEntryLink> findByG_FEERC_FESERC_C_C(
		long groupId, String fragmentEntryERC, String fragmentEntryScopeERC,
		long classNameId, long classPK, int start, int end,
		com.liferay.portal.kernel.util.OrderByComparator<FragmentEntryLink>
			orderByComparator);

	/**
	 * Returns an ordered range of all the fragment entry links where groupId = &#63; and fragmentEntryERC = &#63; and fragmentEntryScopeERC = &#63; and classNameId = &#63; and classPK = &#63;.
	 *
	 * <p>
	 * Useful when paginating results. Returns a maximum of <code>end - start</code> instances. <code>start</code> and <code>end</code> are not primary keys, they are indexes in the result set. Thus, <code>0</code> refers to the first result in the set. Setting both <code>start</code> and <code>end</code> to <code>QueryUtil#ALL_POS</code> will return the full result set. If <code>orderByComparator</code> is specified, then the query will include the given ORDER BY logic. If <code>orderByComparator</code> is absent, then the query will include the default ORDER BY logic from <code>FragmentEntryLinkModelImpl</code>.
	 * </p>
	 *
	 * @param groupId the group ID
	 * @param fragmentEntryERC the fragment entry erc
	 * @param fragmentEntryScopeERC the fragment entry scope erc
	 * @param classNameId the class name ID
	 * @param classPK the class pk
	 * @param start the lower bound of the range of fragment entry links
	 * @param end the upper bound of the range of fragment entry links (not inclusive)
	 * @param orderByComparator the comparator to order the results by (optionally <code>null</code>)
	 * @param useFinderCache whether to use the finder cache
	 * @return the ordered range of matching fragment entry links
	 */
	public java.util.List<FragmentEntryLink> findByG_FEERC_FESERC_C_C(
		long groupId, String fragmentEntryERC, String fragmentEntryScopeERC,
		long classNameId, long classPK, int start, int end,
		com.liferay.portal.kernel.util.OrderByComparator<FragmentEntryLink>
			orderByComparator,
		boolean useFinderCache);

	/**
	 * Returns the first fragment entry link in the ordered set where groupId = &#63; and fragmentEntryERC = &#63; and fragmentEntryScopeERC = &#63; and classNameId = &#63; and classPK = &#63;.
	 *
	 * @param groupId the group ID
	 * @param fragmentEntryERC the fragment entry erc
	 * @param fragmentEntryScopeERC the fragment entry scope erc
	 * @param classNameId the class name ID
	 * @param classPK the class pk
	 * @param orderByComparator the comparator to order the set by (optionally <code>null</code>)
	 * @return the first matching fragment entry link
	 * @throws NoSuchEntryLinkException if a matching fragment entry link could not be found
	 */
	public FragmentEntryLink findByG_FEERC_FESERC_C_C_First(
			long groupId, String fragmentEntryERC, String fragmentEntryScopeERC,
			long classNameId, long classPK,
			com.liferay.portal.kernel.util.OrderByComparator<FragmentEntryLink>
				orderByComparator)
		throws NoSuchEntryLinkException;

	/**
	 * Returns the first fragment entry link in the ordered set where groupId = &#63; and fragmentEntryERC = &#63; and fragmentEntryScopeERC = &#63; and classNameId = &#63; and classPK = &#63;.
	 *
	 * @param groupId the group ID
	 * @param fragmentEntryERC the fragment entry erc
	 * @param fragmentEntryScopeERC the fragment entry scope erc
	 * @param classNameId the class name ID
	 * @param classPK the class pk
	 * @param orderByComparator the comparator to order the set by (optionally <code>null</code>)
	 * @return the first matching fragment entry link, or <code>null</code> if a matching fragment entry link could not be found
	 */
	public FragmentEntryLink fetchByG_FEERC_FESERC_C_C_First(
		long groupId, String fragmentEntryERC, String fragmentEntryScopeERC,
		long classNameId, long classPK,
		com.liferay.portal.kernel.util.OrderByComparator<FragmentEntryLink>
			orderByComparator);

	/**
	 * Returns the last fragment entry link in the ordered set where groupId = &#63; and fragmentEntryERC = &#63; and fragmentEntryScopeERC = &#63; and classNameId = &#63; and classPK = &#63;.
	 *
	 * @param groupId the group ID
	 * @param fragmentEntryERC the fragment entry erc
	 * @param fragmentEntryScopeERC the fragment entry scope erc
	 * @param classNameId the class name ID
	 * @param classPK the class pk
	 * @param orderByComparator the comparator to order the set by (optionally <code>null</code>)
	 * @return the last matching fragment entry link
	 * @throws NoSuchEntryLinkException if a matching fragment entry link could not be found
	 */
	public FragmentEntryLink findByG_FEERC_FESERC_C_C_Last(
			long groupId, String fragmentEntryERC, String fragmentEntryScopeERC,
			long classNameId, long classPK,
			com.liferay.portal.kernel.util.OrderByComparator<FragmentEntryLink>
				orderByComparator)
		throws NoSuchEntryLinkException;

	/**
	 * Returns the last fragment entry link in the ordered set where groupId = &#63; and fragmentEntryERC = &#63; and fragmentEntryScopeERC = &#63; and classNameId = &#63; and classPK = &#63;.
	 *
	 * @param groupId the group ID
	 * @param fragmentEntryERC the fragment entry erc
	 * @param fragmentEntryScopeERC the fragment entry scope erc
	 * @param classNameId the class name ID
	 * @param classPK the class pk
	 * @param orderByComparator the comparator to order the set by (optionally <code>null</code>)
	 * @return the last matching fragment entry link, or <code>null</code> if a matching fragment entry link could not be found
	 */
	public FragmentEntryLink fetchByG_FEERC_FESERC_C_C_Last(
		long groupId, String fragmentEntryERC, String fragmentEntryScopeERC,
		long classNameId, long classPK,
		com.liferay.portal.kernel.util.OrderByComparator<FragmentEntryLink>
			orderByComparator);

	/**
	 * Returns the fragment entry links before and after the current fragment entry link in the ordered set where groupId = &#63; and fragmentEntryERC = &#63; and fragmentEntryScopeERC = &#63; and classNameId = &#63; and classPK = &#63;.
	 *
	 * @param fragmentEntryLinkId the primary key of the current fragment entry link
	 * @param groupId the group ID
	 * @param fragmentEntryERC the fragment entry erc
	 * @param fragmentEntryScopeERC the fragment entry scope erc
	 * @param classNameId the class name ID
	 * @param classPK the class pk
	 * @param orderByComparator the comparator to order the set by (optionally <code>null</code>)
	 * @return the previous, current, and next fragment entry link
	 * @throws NoSuchEntryLinkException if a fragment entry link with the primary key could not be found
	 */
	public FragmentEntryLink[] findByG_FEERC_FESERC_C_C_PrevAndNext(
			long fragmentEntryLinkId, long groupId, String fragmentEntryERC,
			String fragmentEntryScopeERC, long classNameId, long classPK,
			com.liferay.portal.kernel.util.OrderByComparator<FragmentEntryLink>
				orderByComparator)
		throws NoSuchEntryLinkException;

	/**
	 * Removes all the fragment entry links where groupId = &#63; and fragmentEntryERC = &#63; and fragmentEntryScopeERC = &#63; and classNameId = &#63; and classPK = &#63; from the database.
	 *
	 * @param groupId the group ID
	 * @param fragmentEntryERC the fragment entry erc
	 * @param fragmentEntryScopeERC the fragment entry scope erc
	 * @param classNameId the class name ID
	 * @param classPK the class pk
	 */
	public void removeByG_FEERC_FESERC_C_C(
		long groupId, String fragmentEntryERC, String fragmentEntryScopeERC,
		long classNameId, long classPK);

	/**
	 * Returns the number of fragment entry links where groupId = &#63; and fragmentEntryERC = &#63; and fragmentEntryScopeERC = &#63; and classNameId = &#63; and classPK = &#63;.
	 *
	 * @param groupId the group ID
	 * @param fragmentEntryERC the fragment entry erc
	 * @param fragmentEntryScopeERC the fragment entry scope erc
	 * @param classNameId the class name ID
	 * @param classPK the class pk
	 * @return the number of matching fragment entry links
	 */
	public int countByG_FEERC_FESERC_C_C(
		long groupId, String fragmentEntryERC, String fragmentEntryScopeERC,
		long classNameId, long classPK);

	/**
	 * Returns the fragment entry link where externalReferenceCode = &#63; and groupId = &#63; or throws a <code>NoSuchEntryLinkException</code> if it could not be found.
	 *
	 * @param externalReferenceCode the external reference code
	 * @param groupId the group ID
	 * @return the matching fragment entry link
	 * @throws NoSuchEntryLinkException if a matching fragment entry link could not be found
	 */
	public FragmentEntryLink findByERC_G(
			String externalReferenceCode, long groupId)
		throws NoSuchEntryLinkException;

	/**
	 * Returns the fragment entry link where externalReferenceCode = &#63; and groupId = &#63; or returns <code>null</code> if it could not be found. Uses the finder cache.
	 *
	 * @param externalReferenceCode the external reference code
	 * @param groupId the group ID
	 * @return the matching fragment entry link, or <code>null</code> if a matching fragment entry link could not be found
	 */
	public FragmentEntryLink fetchByERC_G(
		String externalReferenceCode, long groupId);

	/**
	 * Returns the fragment entry link where externalReferenceCode = &#63; and groupId = &#63; or returns <code>null</code> if it could not be found, optionally using the finder cache.
	 *
	 * @param externalReferenceCode the external reference code
	 * @param groupId the group ID
	 * @param useFinderCache whether to use the finder cache
	 * @return the matching fragment entry link, or <code>null</code> if a matching fragment entry link could not be found
	 */
	public FragmentEntryLink fetchByERC_G(
		String externalReferenceCode, long groupId, boolean useFinderCache);

	/**
	 * Removes the fragment entry link where externalReferenceCode = &#63; and groupId = &#63; from the database.
	 *
	 * @param externalReferenceCode the external reference code
	 * @param groupId the group ID
	 * @return the fragment entry link that was removed
	 */
	public FragmentEntryLink removeByERC_G(
			String externalReferenceCode, long groupId)
		throws NoSuchEntryLinkException;

	/**
	 * Returns the number of fragment entry links where externalReferenceCode = &#63; and groupId = &#63;.
	 *
	 * @param externalReferenceCode the external reference code
	 * @param groupId the group ID
	 * @return the number of matching fragment entry links
	 */
	public int countByERC_G(String externalReferenceCode, long groupId);

	/**
	 * Caches the fragment entry link in the entity cache if it is enabled.
	 *
	 * @param fragmentEntryLink the fragment entry link
	 */
	public void cacheResult(FragmentEntryLink fragmentEntryLink);

	/**
	 * Caches the fragment entry links in the entity cache if it is enabled.
	 *
	 * @param fragmentEntryLinks the fragment entry links
	 */
	public void cacheResult(
		java.util.List<FragmentEntryLink> fragmentEntryLinks);

	/**
	 * Creates a new fragment entry link with the primary key. Does not add the fragment entry link to the database.
	 *
	 * @param fragmentEntryLinkId the primary key for the new fragment entry link
	 * @return the new fragment entry link
	 */
	public FragmentEntryLink create(long fragmentEntryLinkId);

	/**
	 * Removes the fragment entry link with the primary key from the database. Also notifies the appropriate model listeners.
	 *
	 * @param fragmentEntryLinkId the primary key of the fragment entry link
	 * @return the fragment entry link that was removed
	 * @throws NoSuchEntryLinkException if a fragment entry link with the primary key could not be found
	 */
	public FragmentEntryLink remove(long fragmentEntryLinkId)
		throws NoSuchEntryLinkException;

	public FragmentEntryLink updateImpl(FragmentEntryLink fragmentEntryLink);

	/**
	 * Returns the fragment entry link with the primary key or throws a <code>NoSuchEntryLinkException</code> if it could not be found.
	 *
	 * @param fragmentEntryLinkId the primary key of the fragment entry link
	 * @return the fragment entry link
	 * @throws NoSuchEntryLinkException if a fragment entry link with the primary key could not be found
	 */
	public FragmentEntryLink findByPrimaryKey(long fragmentEntryLinkId)
		throws NoSuchEntryLinkException;

	/**
	 * Returns the fragment entry link with the primary key or returns <code>null</code> if it could not be found.
	 *
	 * @param fragmentEntryLinkId the primary key of the fragment entry link
	 * @return the fragment entry link, or <code>null</code> if a fragment entry link with the primary key could not be found
	 */
	public FragmentEntryLink fetchByPrimaryKey(long fragmentEntryLinkId);

	/**
	 * Returns all the fragment entry links.
	 *
	 * @return the fragment entry links
	 */
	public java.util.List<FragmentEntryLink> findAll();

	/**
	 * Returns a range of all the fragment entry links.
	 *
	 * <p>
	 * Useful when paginating results. Returns a maximum of <code>end - start</code> instances. <code>start</code> and <code>end</code> are not primary keys, they are indexes in the result set. Thus, <code>0</code> refers to the first result in the set. Setting both <code>start</code> and <code>end</code> to <code>QueryUtil#ALL_POS</code> will return the full result set. If <code>orderByComparator</code> is specified, then the query will include the given ORDER BY logic. If <code>orderByComparator</code> is absent, then the query will include the default ORDER BY logic from <code>FragmentEntryLinkModelImpl</code>.
	 * </p>
	 *
	 * @param start the lower bound of the range of fragment entry links
	 * @param end the upper bound of the range of fragment entry links (not inclusive)
	 * @return the range of fragment entry links
	 */
	public java.util.List<FragmentEntryLink> findAll(int start, int end);

	/**
	 * Returns an ordered range of all the fragment entry links.
	 *
	 * <p>
	 * Useful when paginating results. Returns a maximum of <code>end - start</code> instances. <code>start</code> and <code>end</code> are not primary keys, they are indexes in the result set. Thus, <code>0</code> refers to the first result in the set. Setting both <code>start</code> and <code>end</code> to <code>QueryUtil#ALL_POS</code> will return the full result set. If <code>orderByComparator</code> is specified, then the query will include the given ORDER BY logic. If <code>orderByComparator</code> is absent, then the query will include the default ORDER BY logic from <code>FragmentEntryLinkModelImpl</code>.
	 * </p>
	 *
	 * @param start the lower bound of the range of fragment entry links
	 * @param end the upper bound of the range of fragment entry links (not inclusive)
	 * @param orderByComparator the comparator to order the results by (optionally <code>null</code>)
	 * @return the ordered range of fragment entry links
	 */
	public java.util.List<FragmentEntryLink> findAll(
		int start, int end,
		com.liferay.portal.kernel.util.OrderByComparator<FragmentEntryLink>
			orderByComparator);

	/**
	 * Returns an ordered range of all the fragment entry links.
	 *
	 * <p>
	 * Useful when paginating results. Returns a maximum of <code>end - start</code> instances. <code>start</code> and <code>end</code> are not primary keys, they are indexes in the result set. Thus, <code>0</code> refers to the first result in the set. Setting both <code>start</code> and <code>end</code> to <code>QueryUtil#ALL_POS</code> will return the full result set. If <code>orderByComparator</code> is specified, then the query will include the given ORDER BY logic. If <code>orderByComparator</code> is absent, then the query will include the default ORDER BY logic from <code>FragmentEntryLinkModelImpl</code>.
	 * </p>
	 *
	 * @param start the lower bound of the range of fragment entry links
	 * @param end the upper bound of the range of fragment entry links (not inclusive)
	 * @param orderByComparator the comparator to order the results by (optionally <code>null</code>)
	 * @param useFinderCache whether to use the finder cache
	 * @return the ordered range of fragment entry links
	 */
	public java.util.List<FragmentEntryLink> findAll(
		int start, int end,
		com.liferay.portal.kernel.util.OrderByComparator<FragmentEntryLink>
			orderByComparator,
		boolean useFinderCache);

	/**
	 * Removes all the fragment entry links from the database.
	 */
	public void removeAll();

	/**
	 * Returns the number of fragment entry links.
	 *
	 * @return the number of fragment entry links
	 */
	public int countAll();

}