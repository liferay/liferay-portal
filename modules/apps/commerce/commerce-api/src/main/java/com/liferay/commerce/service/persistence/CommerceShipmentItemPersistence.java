/**
 * SPDX-FileCopyrightText: (c) 2000 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

package com.liferay.commerce.service.persistence;

import com.liferay.commerce.exception.NoSuchShipmentItemException;
import com.liferay.commerce.model.CommerceShipmentItem;
import com.liferay.portal.kernel.service.persistence.BasePersistence;

import java.math.BigDecimal;

import org.osgi.annotation.versioning.ProviderType;

/**
 * The persistence interface for the commerce shipment item service.
 *
 * <p>
 * Caching information and settings can be found in <code>portal.properties</code>
 * </p>
 *
 * @author Alessio Antonio Rendina
 * @see CommerceShipmentItemUtil
 * @generated
 */
@ProviderType
public interface CommerceShipmentItemPersistence
	extends BasePersistence<CommerceShipmentItem> {

	/*
	 * NOTE FOR DEVELOPERS:
	 *
	 * Never modify or reference this interface directly. Always use {@link CommerceShipmentItemUtil} to access the commerce shipment item persistence. Modify <code>service.xml</code> and rerun ServiceBuilder to regenerate this interface.
	 */

	/**
	 * Returns all the commerce shipment items where uuid = &#63;.
	 *
	 * @param uuid the uuid
	 * @return the matching commerce shipment items
	 */
	public java.util.List<CommerceShipmentItem> findByUuid(String uuid);

	/**
	 * Returns a range of all the commerce shipment items where uuid = &#63;.
	 *
	 * <p>
	 * Useful when paginating results. Returns a maximum of <code>end - start</code> instances. <code>start</code> and <code>end</code> are not primary keys, they are indexes in the result set. Thus, <code>0</code> refers to the first result in the set. Setting both <code>start</code> and <code>end</code> to <code>QueryUtil#ALL_POS</code> will return the full result set. If <code>orderByComparator</code> is specified, then the query will include the given ORDER BY logic. If <code>orderByComparator</code> is absent, then the query will include the default ORDER BY logic from <code>CommerceShipmentItemModelImpl</code>.
	 * </p>
	 *
	 * @param uuid the uuid
	 * @param start the lower bound of the range of commerce shipment items
	 * @param end the upper bound of the range of commerce shipment items (not inclusive)
	 * @return the range of matching commerce shipment items
	 */
	public java.util.List<CommerceShipmentItem> findByUuid(
		String uuid, int start, int end);

	/**
	 * Returns an ordered range of all the commerce shipment items where uuid = &#63;.
	 *
	 * <p>
	 * Useful when paginating results. Returns a maximum of <code>end - start</code> instances. <code>start</code> and <code>end</code> are not primary keys, they are indexes in the result set. Thus, <code>0</code> refers to the first result in the set. Setting both <code>start</code> and <code>end</code> to <code>QueryUtil#ALL_POS</code> will return the full result set. If <code>orderByComparator</code> is specified, then the query will include the given ORDER BY logic. If <code>orderByComparator</code> is absent, then the query will include the default ORDER BY logic from <code>CommerceShipmentItemModelImpl</code>.
	 * </p>
	 *
	 * @param uuid the uuid
	 * @param start the lower bound of the range of commerce shipment items
	 * @param end the upper bound of the range of commerce shipment items (not inclusive)
	 * @param orderByComparator the comparator to order the results by (optionally <code>null</code>)
	 * @return the ordered range of matching commerce shipment items
	 */
	public java.util.List<CommerceShipmentItem> findByUuid(
		String uuid, int start, int end,
		com.liferay.portal.kernel.util.OrderByComparator<CommerceShipmentItem>
			orderByComparator);

	/**
	 * Returns an ordered range of all the commerce shipment items where uuid = &#63;.
	 *
	 * <p>
	 * Useful when paginating results. Returns a maximum of <code>end - start</code> instances. <code>start</code> and <code>end</code> are not primary keys, they are indexes in the result set. Thus, <code>0</code> refers to the first result in the set. Setting both <code>start</code> and <code>end</code> to <code>QueryUtil#ALL_POS</code> will return the full result set. If <code>orderByComparator</code> is specified, then the query will include the given ORDER BY logic. If <code>orderByComparator</code> is absent, then the query will include the default ORDER BY logic from <code>CommerceShipmentItemModelImpl</code>.
	 * </p>
	 *
	 * @param uuid the uuid
	 * @param start the lower bound of the range of commerce shipment items
	 * @param end the upper bound of the range of commerce shipment items (not inclusive)
	 * @param orderByComparator the comparator to order the results by (optionally <code>null</code>)
	 * @param useFinderCache whether to use the finder cache
	 * @return the ordered range of matching commerce shipment items
	 */
	public java.util.List<CommerceShipmentItem> findByUuid(
		String uuid, int start, int end,
		com.liferay.portal.kernel.util.OrderByComparator<CommerceShipmentItem>
			orderByComparator,
		boolean useFinderCache);

	/**
	 * Returns the first commerce shipment item in the ordered set where uuid = &#63;.
	 *
	 * @param uuid the uuid
	 * @param orderByComparator the comparator to order the set by (optionally <code>null</code>)
	 * @return the first matching commerce shipment item
	 * @throws NoSuchShipmentItemException if a matching commerce shipment item could not be found
	 */
	public CommerceShipmentItem findByUuid_First(
			String uuid,
			com.liferay.portal.kernel.util.OrderByComparator
				<CommerceShipmentItem> orderByComparator)
		throws NoSuchShipmentItemException;

	/**
	 * Returns the first commerce shipment item in the ordered set where uuid = &#63;.
	 *
	 * @param uuid the uuid
	 * @param orderByComparator the comparator to order the set by (optionally <code>null</code>)
	 * @return the first matching commerce shipment item, or <code>null</code> if a matching commerce shipment item could not be found
	 */
	public CommerceShipmentItem fetchByUuid_First(
		String uuid,
		com.liferay.portal.kernel.util.OrderByComparator<CommerceShipmentItem>
			orderByComparator);

	/**
	 * Returns the last commerce shipment item in the ordered set where uuid = &#63;.
	 *
	 * @param uuid the uuid
	 * @param orderByComparator the comparator to order the set by (optionally <code>null</code>)
	 * @return the last matching commerce shipment item
	 * @throws NoSuchShipmentItemException if a matching commerce shipment item could not be found
	 */
	public CommerceShipmentItem findByUuid_Last(
			String uuid,
			com.liferay.portal.kernel.util.OrderByComparator
				<CommerceShipmentItem> orderByComparator)
		throws NoSuchShipmentItemException;

	/**
	 * Returns the last commerce shipment item in the ordered set where uuid = &#63;.
	 *
	 * @param uuid the uuid
	 * @param orderByComparator the comparator to order the set by (optionally <code>null</code>)
	 * @return the last matching commerce shipment item, or <code>null</code> if a matching commerce shipment item could not be found
	 */
	public CommerceShipmentItem fetchByUuid_Last(
		String uuid,
		com.liferay.portal.kernel.util.OrderByComparator<CommerceShipmentItem>
			orderByComparator);

	/**
	 * Returns the commerce shipment items before and after the current commerce shipment item in the ordered set where uuid = &#63;.
	 *
	 * @param commerceShipmentItemId the primary key of the current commerce shipment item
	 * @param uuid the uuid
	 * @param orderByComparator the comparator to order the set by (optionally <code>null</code>)
	 * @return the previous, current, and next commerce shipment item
	 * @throws NoSuchShipmentItemException if a commerce shipment item with the primary key could not be found
	 */
	public CommerceShipmentItem[] findByUuid_PrevAndNext(
			long commerceShipmentItemId, String uuid,
			com.liferay.portal.kernel.util.OrderByComparator
				<CommerceShipmentItem> orderByComparator)
		throws NoSuchShipmentItemException;

	/**
	 * Removes all the commerce shipment items where uuid = &#63; from the database.
	 *
	 * @param uuid the uuid
	 */
	public void removeByUuid(String uuid);

	/**
	 * Returns the number of commerce shipment items where uuid = &#63;.
	 *
	 * @param uuid the uuid
	 * @return the number of matching commerce shipment items
	 */
	public int countByUuid(String uuid);

	/**
	 * Returns the commerce shipment item where uuid = &#63; and groupId = &#63; or throws a <code>NoSuchShipmentItemException</code> if it could not be found.
	 *
	 * @param uuid the uuid
	 * @param groupId the group ID
	 * @return the matching commerce shipment item
	 * @throws NoSuchShipmentItemException if a matching commerce shipment item could not be found
	 */
	public CommerceShipmentItem findByUUID_G(String uuid, long groupId)
		throws NoSuchShipmentItemException;

	/**
	 * Returns the commerce shipment item where uuid = &#63; and groupId = &#63; or returns <code>null</code> if it could not be found. Uses the finder cache.
	 *
	 * @param uuid the uuid
	 * @param groupId the group ID
	 * @return the matching commerce shipment item, or <code>null</code> if a matching commerce shipment item could not be found
	 */
	public CommerceShipmentItem fetchByUUID_G(String uuid, long groupId);

	/**
	 * Returns the commerce shipment item where uuid = &#63; and groupId = &#63; or returns <code>null</code> if it could not be found, optionally using the finder cache.
	 *
	 * @param uuid the uuid
	 * @param groupId the group ID
	 * @param useFinderCache whether to use the finder cache
	 * @return the matching commerce shipment item, or <code>null</code> if a matching commerce shipment item could not be found
	 */
	public CommerceShipmentItem fetchByUUID_G(
		String uuid, long groupId, boolean useFinderCache);

	/**
	 * Removes the commerce shipment item where uuid = &#63; and groupId = &#63; from the database.
	 *
	 * @param uuid the uuid
	 * @param groupId the group ID
	 * @return the commerce shipment item that was removed
	 */
	public CommerceShipmentItem removeByUUID_G(String uuid, long groupId)
		throws NoSuchShipmentItemException;

	/**
	 * Returns the number of commerce shipment items where uuid = &#63; and groupId = &#63;.
	 *
	 * @param uuid the uuid
	 * @param groupId the group ID
	 * @return the number of matching commerce shipment items
	 */
	public int countByUUID_G(String uuid, long groupId);

	/**
	 * Returns all the commerce shipment items where uuid = &#63; and companyId = &#63;.
	 *
	 * @param uuid the uuid
	 * @param companyId the company ID
	 * @return the matching commerce shipment items
	 */
	public java.util.List<CommerceShipmentItem> findByUuid_C(
		String uuid, long companyId);

	/**
	 * Returns a range of all the commerce shipment items where uuid = &#63; and companyId = &#63;.
	 *
	 * <p>
	 * Useful when paginating results. Returns a maximum of <code>end - start</code> instances. <code>start</code> and <code>end</code> are not primary keys, they are indexes in the result set. Thus, <code>0</code> refers to the first result in the set. Setting both <code>start</code> and <code>end</code> to <code>QueryUtil#ALL_POS</code> will return the full result set. If <code>orderByComparator</code> is specified, then the query will include the given ORDER BY logic. If <code>orderByComparator</code> is absent, then the query will include the default ORDER BY logic from <code>CommerceShipmentItemModelImpl</code>.
	 * </p>
	 *
	 * @param uuid the uuid
	 * @param companyId the company ID
	 * @param start the lower bound of the range of commerce shipment items
	 * @param end the upper bound of the range of commerce shipment items (not inclusive)
	 * @return the range of matching commerce shipment items
	 */
	public java.util.List<CommerceShipmentItem> findByUuid_C(
		String uuid, long companyId, int start, int end);

	/**
	 * Returns an ordered range of all the commerce shipment items where uuid = &#63; and companyId = &#63;.
	 *
	 * <p>
	 * Useful when paginating results. Returns a maximum of <code>end - start</code> instances. <code>start</code> and <code>end</code> are not primary keys, they are indexes in the result set. Thus, <code>0</code> refers to the first result in the set. Setting both <code>start</code> and <code>end</code> to <code>QueryUtil#ALL_POS</code> will return the full result set. If <code>orderByComparator</code> is specified, then the query will include the given ORDER BY logic. If <code>orderByComparator</code> is absent, then the query will include the default ORDER BY logic from <code>CommerceShipmentItemModelImpl</code>.
	 * </p>
	 *
	 * @param uuid the uuid
	 * @param companyId the company ID
	 * @param start the lower bound of the range of commerce shipment items
	 * @param end the upper bound of the range of commerce shipment items (not inclusive)
	 * @param orderByComparator the comparator to order the results by (optionally <code>null</code>)
	 * @return the ordered range of matching commerce shipment items
	 */
	public java.util.List<CommerceShipmentItem> findByUuid_C(
		String uuid, long companyId, int start, int end,
		com.liferay.portal.kernel.util.OrderByComparator<CommerceShipmentItem>
			orderByComparator);

	/**
	 * Returns an ordered range of all the commerce shipment items where uuid = &#63; and companyId = &#63;.
	 *
	 * <p>
	 * Useful when paginating results. Returns a maximum of <code>end - start</code> instances. <code>start</code> and <code>end</code> are not primary keys, they are indexes in the result set. Thus, <code>0</code> refers to the first result in the set. Setting both <code>start</code> and <code>end</code> to <code>QueryUtil#ALL_POS</code> will return the full result set. If <code>orderByComparator</code> is specified, then the query will include the given ORDER BY logic. If <code>orderByComparator</code> is absent, then the query will include the default ORDER BY logic from <code>CommerceShipmentItemModelImpl</code>.
	 * </p>
	 *
	 * @param uuid the uuid
	 * @param companyId the company ID
	 * @param start the lower bound of the range of commerce shipment items
	 * @param end the upper bound of the range of commerce shipment items (not inclusive)
	 * @param orderByComparator the comparator to order the results by (optionally <code>null</code>)
	 * @param useFinderCache whether to use the finder cache
	 * @return the ordered range of matching commerce shipment items
	 */
	public java.util.List<CommerceShipmentItem> findByUuid_C(
		String uuid, long companyId, int start, int end,
		com.liferay.portal.kernel.util.OrderByComparator<CommerceShipmentItem>
			orderByComparator,
		boolean useFinderCache);

	/**
	 * Returns the first commerce shipment item in the ordered set where uuid = &#63; and companyId = &#63;.
	 *
	 * @param uuid the uuid
	 * @param companyId the company ID
	 * @param orderByComparator the comparator to order the set by (optionally <code>null</code>)
	 * @return the first matching commerce shipment item
	 * @throws NoSuchShipmentItemException if a matching commerce shipment item could not be found
	 */
	public CommerceShipmentItem findByUuid_C_First(
			String uuid, long companyId,
			com.liferay.portal.kernel.util.OrderByComparator
				<CommerceShipmentItem> orderByComparator)
		throws NoSuchShipmentItemException;

	/**
	 * Returns the first commerce shipment item in the ordered set where uuid = &#63; and companyId = &#63;.
	 *
	 * @param uuid the uuid
	 * @param companyId the company ID
	 * @param orderByComparator the comparator to order the set by (optionally <code>null</code>)
	 * @return the first matching commerce shipment item, or <code>null</code> if a matching commerce shipment item could not be found
	 */
	public CommerceShipmentItem fetchByUuid_C_First(
		String uuid, long companyId,
		com.liferay.portal.kernel.util.OrderByComparator<CommerceShipmentItem>
			orderByComparator);

	/**
	 * Returns the last commerce shipment item in the ordered set where uuid = &#63; and companyId = &#63;.
	 *
	 * @param uuid the uuid
	 * @param companyId the company ID
	 * @param orderByComparator the comparator to order the set by (optionally <code>null</code>)
	 * @return the last matching commerce shipment item
	 * @throws NoSuchShipmentItemException if a matching commerce shipment item could not be found
	 */
	public CommerceShipmentItem findByUuid_C_Last(
			String uuid, long companyId,
			com.liferay.portal.kernel.util.OrderByComparator
				<CommerceShipmentItem> orderByComparator)
		throws NoSuchShipmentItemException;

	/**
	 * Returns the last commerce shipment item in the ordered set where uuid = &#63; and companyId = &#63;.
	 *
	 * @param uuid the uuid
	 * @param companyId the company ID
	 * @param orderByComparator the comparator to order the set by (optionally <code>null</code>)
	 * @return the last matching commerce shipment item, or <code>null</code> if a matching commerce shipment item could not be found
	 */
	public CommerceShipmentItem fetchByUuid_C_Last(
		String uuid, long companyId,
		com.liferay.portal.kernel.util.OrderByComparator<CommerceShipmentItem>
			orderByComparator);

	/**
	 * Returns the commerce shipment items before and after the current commerce shipment item in the ordered set where uuid = &#63; and companyId = &#63;.
	 *
	 * @param commerceShipmentItemId the primary key of the current commerce shipment item
	 * @param uuid the uuid
	 * @param companyId the company ID
	 * @param orderByComparator the comparator to order the set by (optionally <code>null</code>)
	 * @return the previous, current, and next commerce shipment item
	 * @throws NoSuchShipmentItemException if a commerce shipment item with the primary key could not be found
	 */
	public CommerceShipmentItem[] findByUuid_C_PrevAndNext(
			long commerceShipmentItemId, String uuid, long companyId,
			com.liferay.portal.kernel.util.OrderByComparator
				<CommerceShipmentItem> orderByComparator)
		throws NoSuchShipmentItemException;

	/**
	 * Removes all the commerce shipment items where uuid = &#63; and companyId = &#63; from the database.
	 *
	 * @param uuid the uuid
	 * @param companyId the company ID
	 */
	public void removeByUuid_C(String uuid, long companyId);

	/**
	 * Returns the number of commerce shipment items where uuid = &#63; and companyId = &#63;.
	 *
	 * @param uuid the uuid
	 * @param companyId the company ID
	 * @return the number of matching commerce shipment items
	 */
	public int countByUuid_C(String uuid, long companyId);

	/**
	 * Returns all the commerce shipment items where groupId = &#63;.
	 *
	 * @param groupId the group ID
	 * @return the matching commerce shipment items
	 */
	public java.util.List<CommerceShipmentItem> findByGroupId(long groupId);

	/**
	 * Returns a range of all the commerce shipment items where groupId = &#63;.
	 *
	 * <p>
	 * Useful when paginating results. Returns a maximum of <code>end - start</code> instances. <code>start</code> and <code>end</code> are not primary keys, they are indexes in the result set. Thus, <code>0</code> refers to the first result in the set. Setting both <code>start</code> and <code>end</code> to <code>QueryUtil#ALL_POS</code> will return the full result set. If <code>orderByComparator</code> is specified, then the query will include the given ORDER BY logic. If <code>orderByComparator</code> is absent, then the query will include the default ORDER BY logic from <code>CommerceShipmentItemModelImpl</code>.
	 * </p>
	 *
	 * @param groupId the group ID
	 * @param start the lower bound of the range of commerce shipment items
	 * @param end the upper bound of the range of commerce shipment items (not inclusive)
	 * @return the range of matching commerce shipment items
	 */
	public java.util.List<CommerceShipmentItem> findByGroupId(
		long groupId, int start, int end);

	/**
	 * Returns an ordered range of all the commerce shipment items where groupId = &#63;.
	 *
	 * <p>
	 * Useful when paginating results. Returns a maximum of <code>end - start</code> instances. <code>start</code> and <code>end</code> are not primary keys, they are indexes in the result set. Thus, <code>0</code> refers to the first result in the set. Setting both <code>start</code> and <code>end</code> to <code>QueryUtil#ALL_POS</code> will return the full result set. If <code>orderByComparator</code> is specified, then the query will include the given ORDER BY logic. If <code>orderByComparator</code> is absent, then the query will include the default ORDER BY logic from <code>CommerceShipmentItemModelImpl</code>.
	 * </p>
	 *
	 * @param groupId the group ID
	 * @param start the lower bound of the range of commerce shipment items
	 * @param end the upper bound of the range of commerce shipment items (not inclusive)
	 * @param orderByComparator the comparator to order the results by (optionally <code>null</code>)
	 * @return the ordered range of matching commerce shipment items
	 */
	public java.util.List<CommerceShipmentItem> findByGroupId(
		long groupId, int start, int end,
		com.liferay.portal.kernel.util.OrderByComparator<CommerceShipmentItem>
			orderByComparator);

	/**
	 * Returns an ordered range of all the commerce shipment items where groupId = &#63;.
	 *
	 * <p>
	 * Useful when paginating results. Returns a maximum of <code>end - start</code> instances. <code>start</code> and <code>end</code> are not primary keys, they are indexes in the result set. Thus, <code>0</code> refers to the first result in the set. Setting both <code>start</code> and <code>end</code> to <code>QueryUtil#ALL_POS</code> will return the full result set. If <code>orderByComparator</code> is specified, then the query will include the given ORDER BY logic. If <code>orderByComparator</code> is absent, then the query will include the default ORDER BY logic from <code>CommerceShipmentItemModelImpl</code>.
	 * </p>
	 *
	 * @param groupId the group ID
	 * @param start the lower bound of the range of commerce shipment items
	 * @param end the upper bound of the range of commerce shipment items (not inclusive)
	 * @param orderByComparator the comparator to order the results by (optionally <code>null</code>)
	 * @param useFinderCache whether to use the finder cache
	 * @return the ordered range of matching commerce shipment items
	 */
	public java.util.List<CommerceShipmentItem> findByGroupId(
		long groupId, int start, int end,
		com.liferay.portal.kernel.util.OrderByComparator<CommerceShipmentItem>
			orderByComparator,
		boolean useFinderCache);

	/**
	 * Returns the first commerce shipment item in the ordered set where groupId = &#63;.
	 *
	 * @param groupId the group ID
	 * @param orderByComparator the comparator to order the set by (optionally <code>null</code>)
	 * @return the first matching commerce shipment item
	 * @throws NoSuchShipmentItemException if a matching commerce shipment item could not be found
	 */
	public CommerceShipmentItem findByGroupId_First(
			long groupId,
			com.liferay.portal.kernel.util.OrderByComparator
				<CommerceShipmentItem> orderByComparator)
		throws NoSuchShipmentItemException;

	/**
	 * Returns the first commerce shipment item in the ordered set where groupId = &#63;.
	 *
	 * @param groupId the group ID
	 * @param orderByComparator the comparator to order the set by (optionally <code>null</code>)
	 * @return the first matching commerce shipment item, or <code>null</code> if a matching commerce shipment item could not be found
	 */
	public CommerceShipmentItem fetchByGroupId_First(
		long groupId,
		com.liferay.portal.kernel.util.OrderByComparator<CommerceShipmentItem>
			orderByComparator);

	/**
	 * Returns the last commerce shipment item in the ordered set where groupId = &#63;.
	 *
	 * @param groupId the group ID
	 * @param orderByComparator the comparator to order the set by (optionally <code>null</code>)
	 * @return the last matching commerce shipment item
	 * @throws NoSuchShipmentItemException if a matching commerce shipment item could not be found
	 */
	public CommerceShipmentItem findByGroupId_Last(
			long groupId,
			com.liferay.portal.kernel.util.OrderByComparator
				<CommerceShipmentItem> orderByComparator)
		throws NoSuchShipmentItemException;

	/**
	 * Returns the last commerce shipment item in the ordered set where groupId = &#63;.
	 *
	 * @param groupId the group ID
	 * @param orderByComparator the comparator to order the set by (optionally <code>null</code>)
	 * @return the last matching commerce shipment item, or <code>null</code> if a matching commerce shipment item could not be found
	 */
	public CommerceShipmentItem fetchByGroupId_Last(
		long groupId,
		com.liferay.portal.kernel.util.OrderByComparator<CommerceShipmentItem>
			orderByComparator);

	/**
	 * Returns the commerce shipment items before and after the current commerce shipment item in the ordered set where groupId = &#63;.
	 *
	 * @param commerceShipmentItemId the primary key of the current commerce shipment item
	 * @param groupId the group ID
	 * @param orderByComparator the comparator to order the set by (optionally <code>null</code>)
	 * @return the previous, current, and next commerce shipment item
	 * @throws NoSuchShipmentItemException if a commerce shipment item with the primary key could not be found
	 */
	public CommerceShipmentItem[] findByGroupId_PrevAndNext(
			long commerceShipmentItemId, long groupId,
			com.liferay.portal.kernel.util.OrderByComparator
				<CommerceShipmentItem> orderByComparator)
		throws NoSuchShipmentItemException;

	/**
	 * Removes all the commerce shipment items where groupId = &#63; from the database.
	 *
	 * @param groupId the group ID
	 */
	public void removeByGroupId(long groupId);

	/**
	 * Returns the number of commerce shipment items where groupId = &#63;.
	 *
	 * @param groupId the group ID
	 * @return the number of matching commerce shipment items
	 */
	public int countByGroupId(long groupId);

	/**
	 * Returns all the commerce shipment items where commerceShipmentId = &#63;.
	 *
	 * @param commerceShipmentId the commerce shipment ID
	 * @return the matching commerce shipment items
	 */
	public java.util.List<CommerceShipmentItem> findByCommerceShipmentId(
		long commerceShipmentId);

	/**
	 * Returns a range of all the commerce shipment items where commerceShipmentId = &#63;.
	 *
	 * <p>
	 * Useful when paginating results. Returns a maximum of <code>end - start</code> instances. <code>start</code> and <code>end</code> are not primary keys, they are indexes in the result set. Thus, <code>0</code> refers to the first result in the set. Setting both <code>start</code> and <code>end</code> to <code>QueryUtil#ALL_POS</code> will return the full result set. If <code>orderByComparator</code> is specified, then the query will include the given ORDER BY logic. If <code>orderByComparator</code> is absent, then the query will include the default ORDER BY logic from <code>CommerceShipmentItemModelImpl</code>.
	 * </p>
	 *
	 * @param commerceShipmentId the commerce shipment ID
	 * @param start the lower bound of the range of commerce shipment items
	 * @param end the upper bound of the range of commerce shipment items (not inclusive)
	 * @return the range of matching commerce shipment items
	 */
	public java.util.List<CommerceShipmentItem> findByCommerceShipmentId(
		long commerceShipmentId, int start, int end);

	/**
	 * Returns an ordered range of all the commerce shipment items where commerceShipmentId = &#63;.
	 *
	 * <p>
	 * Useful when paginating results. Returns a maximum of <code>end - start</code> instances. <code>start</code> and <code>end</code> are not primary keys, they are indexes in the result set. Thus, <code>0</code> refers to the first result in the set. Setting both <code>start</code> and <code>end</code> to <code>QueryUtil#ALL_POS</code> will return the full result set. If <code>orderByComparator</code> is specified, then the query will include the given ORDER BY logic. If <code>orderByComparator</code> is absent, then the query will include the default ORDER BY logic from <code>CommerceShipmentItemModelImpl</code>.
	 * </p>
	 *
	 * @param commerceShipmentId the commerce shipment ID
	 * @param start the lower bound of the range of commerce shipment items
	 * @param end the upper bound of the range of commerce shipment items (not inclusive)
	 * @param orderByComparator the comparator to order the results by (optionally <code>null</code>)
	 * @return the ordered range of matching commerce shipment items
	 */
	public java.util.List<CommerceShipmentItem> findByCommerceShipmentId(
		long commerceShipmentId, int start, int end,
		com.liferay.portal.kernel.util.OrderByComparator<CommerceShipmentItem>
			orderByComparator);

	/**
	 * Returns an ordered range of all the commerce shipment items where commerceShipmentId = &#63;.
	 *
	 * <p>
	 * Useful when paginating results. Returns a maximum of <code>end - start</code> instances. <code>start</code> and <code>end</code> are not primary keys, they are indexes in the result set. Thus, <code>0</code> refers to the first result in the set. Setting both <code>start</code> and <code>end</code> to <code>QueryUtil#ALL_POS</code> will return the full result set. If <code>orderByComparator</code> is specified, then the query will include the given ORDER BY logic. If <code>orderByComparator</code> is absent, then the query will include the default ORDER BY logic from <code>CommerceShipmentItemModelImpl</code>.
	 * </p>
	 *
	 * @param commerceShipmentId the commerce shipment ID
	 * @param start the lower bound of the range of commerce shipment items
	 * @param end the upper bound of the range of commerce shipment items (not inclusive)
	 * @param orderByComparator the comparator to order the results by (optionally <code>null</code>)
	 * @param useFinderCache whether to use the finder cache
	 * @return the ordered range of matching commerce shipment items
	 */
	public java.util.List<CommerceShipmentItem> findByCommerceShipmentId(
		long commerceShipmentId, int start, int end,
		com.liferay.portal.kernel.util.OrderByComparator<CommerceShipmentItem>
			orderByComparator,
		boolean useFinderCache);

	/**
	 * Returns the first commerce shipment item in the ordered set where commerceShipmentId = &#63;.
	 *
	 * @param commerceShipmentId the commerce shipment ID
	 * @param orderByComparator the comparator to order the set by (optionally <code>null</code>)
	 * @return the first matching commerce shipment item
	 * @throws NoSuchShipmentItemException if a matching commerce shipment item could not be found
	 */
	public CommerceShipmentItem findByCommerceShipmentId_First(
			long commerceShipmentId,
			com.liferay.portal.kernel.util.OrderByComparator
				<CommerceShipmentItem> orderByComparator)
		throws NoSuchShipmentItemException;

	/**
	 * Returns the first commerce shipment item in the ordered set where commerceShipmentId = &#63;.
	 *
	 * @param commerceShipmentId the commerce shipment ID
	 * @param orderByComparator the comparator to order the set by (optionally <code>null</code>)
	 * @return the first matching commerce shipment item, or <code>null</code> if a matching commerce shipment item could not be found
	 */
	public CommerceShipmentItem fetchByCommerceShipmentId_First(
		long commerceShipmentId,
		com.liferay.portal.kernel.util.OrderByComparator<CommerceShipmentItem>
			orderByComparator);

	/**
	 * Returns the last commerce shipment item in the ordered set where commerceShipmentId = &#63;.
	 *
	 * @param commerceShipmentId the commerce shipment ID
	 * @param orderByComparator the comparator to order the set by (optionally <code>null</code>)
	 * @return the last matching commerce shipment item
	 * @throws NoSuchShipmentItemException if a matching commerce shipment item could not be found
	 */
	public CommerceShipmentItem findByCommerceShipmentId_Last(
			long commerceShipmentId,
			com.liferay.portal.kernel.util.OrderByComparator
				<CommerceShipmentItem> orderByComparator)
		throws NoSuchShipmentItemException;

	/**
	 * Returns the last commerce shipment item in the ordered set where commerceShipmentId = &#63;.
	 *
	 * @param commerceShipmentId the commerce shipment ID
	 * @param orderByComparator the comparator to order the set by (optionally <code>null</code>)
	 * @return the last matching commerce shipment item, or <code>null</code> if a matching commerce shipment item could not be found
	 */
	public CommerceShipmentItem fetchByCommerceShipmentId_Last(
		long commerceShipmentId,
		com.liferay.portal.kernel.util.OrderByComparator<CommerceShipmentItem>
			orderByComparator);

	/**
	 * Returns the commerce shipment items before and after the current commerce shipment item in the ordered set where commerceShipmentId = &#63;.
	 *
	 * @param commerceShipmentItemId the primary key of the current commerce shipment item
	 * @param commerceShipmentId the commerce shipment ID
	 * @param orderByComparator the comparator to order the set by (optionally <code>null</code>)
	 * @return the previous, current, and next commerce shipment item
	 * @throws NoSuchShipmentItemException if a commerce shipment item with the primary key could not be found
	 */
	public CommerceShipmentItem[] findByCommerceShipmentId_PrevAndNext(
			long commerceShipmentItemId, long commerceShipmentId,
			com.liferay.portal.kernel.util.OrderByComparator
				<CommerceShipmentItem> orderByComparator)
		throws NoSuchShipmentItemException;

	/**
	 * Removes all the commerce shipment items where commerceShipmentId = &#63; from the database.
	 *
	 * @param commerceShipmentId the commerce shipment ID
	 */
	public void removeByCommerceShipmentId(long commerceShipmentId);

	/**
	 * Returns the number of commerce shipment items where commerceShipmentId = &#63;.
	 *
	 * @param commerceShipmentId the commerce shipment ID
	 * @return the number of matching commerce shipment items
	 */
	public int countByCommerceShipmentId(long commerceShipmentId);

	/**
	 * Returns all the commerce shipment items where commerceOrderItemId = &#63;.
	 *
	 * @param commerceOrderItemId the commerce order item ID
	 * @return the matching commerce shipment items
	 */
	public java.util.List<CommerceShipmentItem> findByCommerceOrderItemId(
		long commerceOrderItemId);

	/**
	 * Returns a range of all the commerce shipment items where commerceOrderItemId = &#63;.
	 *
	 * <p>
	 * Useful when paginating results. Returns a maximum of <code>end - start</code> instances. <code>start</code> and <code>end</code> are not primary keys, they are indexes in the result set. Thus, <code>0</code> refers to the first result in the set. Setting both <code>start</code> and <code>end</code> to <code>QueryUtil#ALL_POS</code> will return the full result set. If <code>orderByComparator</code> is specified, then the query will include the given ORDER BY logic. If <code>orderByComparator</code> is absent, then the query will include the default ORDER BY logic from <code>CommerceShipmentItemModelImpl</code>.
	 * </p>
	 *
	 * @param commerceOrderItemId the commerce order item ID
	 * @param start the lower bound of the range of commerce shipment items
	 * @param end the upper bound of the range of commerce shipment items (not inclusive)
	 * @return the range of matching commerce shipment items
	 */
	public java.util.List<CommerceShipmentItem> findByCommerceOrderItemId(
		long commerceOrderItemId, int start, int end);

	/**
	 * Returns an ordered range of all the commerce shipment items where commerceOrderItemId = &#63;.
	 *
	 * <p>
	 * Useful when paginating results. Returns a maximum of <code>end - start</code> instances. <code>start</code> and <code>end</code> are not primary keys, they are indexes in the result set. Thus, <code>0</code> refers to the first result in the set. Setting both <code>start</code> and <code>end</code> to <code>QueryUtil#ALL_POS</code> will return the full result set. If <code>orderByComparator</code> is specified, then the query will include the given ORDER BY logic. If <code>orderByComparator</code> is absent, then the query will include the default ORDER BY logic from <code>CommerceShipmentItemModelImpl</code>.
	 * </p>
	 *
	 * @param commerceOrderItemId the commerce order item ID
	 * @param start the lower bound of the range of commerce shipment items
	 * @param end the upper bound of the range of commerce shipment items (not inclusive)
	 * @param orderByComparator the comparator to order the results by (optionally <code>null</code>)
	 * @return the ordered range of matching commerce shipment items
	 */
	public java.util.List<CommerceShipmentItem> findByCommerceOrderItemId(
		long commerceOrderItemId, int start, int end,
		com.liferay.portal.kernel.util.OrderByComparator<CommerceShipmentItem>
			orderByComparator);

	/**
	 * Returns an ordered range of all the commerce shipment items where commerceOrderItemId = &#63;.
	 *
	 * <p>
	 * Useful when paginating results. Returns a maximum of <code>end - start</code> instances. <code>start</code> and <code>end</code> are not primary keys, they are indexes in the result set. Thus, <code>0</code> refers to the first result in the set. Setting both <code>start</code> and <code>end</code> to <code>QueryUtil#ALL_POS</code> will return the full result set. If <code>orderByComparator</code> is specified, then the query will include the given ORDER BY logic. If <code>orderByComparator</code> is absent, then the query will include the default ORDER BY logic from <code>CommerceShipmentItemModelImpl</code>.
	 * </p>
	 *
	 * @param commerceOrderItemId the commerce order item ID
	 * @param start the lower bound of the range of commerce shipment items
	 * @param end the upper bound of the range of commerce shipment items (not inclusive)
	 * @param orderByComparator the comparator to order the results by (optionally <code>null</code>)
	 * @param useFinderCache whether to use the finder cache
	 * @return the ordered range of matching commerce shipment items
	 */
	public java.util.List<CommerceShipmentItem> findByCommerceOrderItemId(
		long commerceOrderItemId, int start, int end,
		com.liferay.portal.kernel.util.OrderByComparator<CommerceShipmentItem>
			orderByComparator,
		boolean useFinderCache);

	/**
	 * Returns the first commerce shipment item in the ordered set where commerceOrderItemId = &#63;.
	 *
	 * @param commerceOrderItemId the commerce order item ID
	 * @param orderByComparator the comparator to order the set by (optionally <code>null</code>)
	 * @return the first matching commerce shipment item
	 * @throws NoSuchShipmentItemException if a matching commerce shipment item could not be found
	 */
	public CommerceShipmentItem findByCommerceOrderItemId_First(
			long commerceOrderItemId,
			com.liferay.portal.kernel.util.OrderByComparator
				<CommerceShipmentItem> orderByComparator)
		throws NoSuchShipmentItemException;

	/**
	 * Returns the first commerce shipment item in the ordered set where commerceOrderItemId = &#63;.
	 *
	 * @param commerceOrderItemId the commerce order item ID
	 * @param orderByComparator the comparator to order the set by (optionally <code>null</code>)
	 * @return the first matching commerce shipment item, or <code>null</code> if a matching commerce shipment item could not be found
	 */
	public CommerceShipmentItem fetchByCommerceOrderItemId_First(
		long commerceOrderItemId,
		com.liferay.portal.kernel.util.OrderByComparator<CommerceShipmentItem>
			orderByComparator);

	/**
	 * Returns the last commerce shipment item in the ordered set where commerceOrderItemId = &#63;.
	 *
	 * @param commerceOrderItemId the commerce order item ID
	 * @param orderByComparator the comparator to order the set by (optionally <code>null</code>)
	 * @return the last matching commerce shipment item
	 * @throws NoSuchShipmentItemException if a matching commerce shipment item could not be found
	 */
	public CommerceShipmentItem findByCommerceOrderItemId_Last(
			long commerceOrderItemId,
			com.liferay.portal.kernel.util.OrderByComparator
				<CommerceShipmentItem> orderByComparator)
		throws NoSuchShipmentItemException;

	/**
	 * Returns the last commerce shipment item in the ordered set where commerceOrderItemId = &#63;.
	 *
	 * @param commerceOrderItemId the commerce order item ID
	 * @param orderByComparator the comparator to order the set by (optionally <code>null</code>)
	 * @return the last matching commerce shipment item, or <code>null</code> if a matching commerce shipment item could not be found
	 */
	public CommerceShipmentItem fetchByCommerceOrderItemId_Last(
		long commerceOrderItemId,
		com.liferay.portal.kernel.util.OrderByComparator<CommerceShipmentItem>
			orderByComparator);

	/**
	 * Returns the commerce shipment items before and after the current commerce shipment item in the ordered set where commerceOrderItemId = &#63;.
	 *
	 * @param commerceShipmentItemId the primary key of the current commerce shipment item
	 * @param commerceOrderItemId the commerce order item ID
	 * @param orderByComparator the comparator to order the set by (optionally <code>null</code>)
	 * @return the previous, current, and next commerce shipment item
	 * @throws NoSuchShipmentItemException if a commerce shipment item with the primary key could not be found
	 */
	public CommerceShipmentItem[] findByCommerceOrderItemId_PrevAndNext(
			long commerceShipmentItemId, long commerceOrderItemId,
			com.liferay.portal.kernel.util.OrderByComparator
				<CommerceShipmentItem> orderByComparator)
		throws NoSuchShipmentItemException;

	/**
	 * Removes all the commerce shipment items where commerceOrderItemId = &#63; from the database.
	 *
	 * @param commerceOrderItemId the commerce order item ID
	 */
	public void removeByCommerceOrderItemId(long commerceOrderItemId);

	/**
	 * Returns the number of commerce shipment items where commerceOrderItemId = &#63;.
	 *
	 * @param commerceOrderItemId the commerce order item ID
	 * @return the number of matching commerce shipment items
	 */
	public int countByCommerceOrderItemId(long commerceOrderItemId);

	/**
	 * Returns all the commerce shipment items where commerceShipmentId = &#63; and commerceOrderItemId = &#63;.
	 *
	 * @param commerceShipmentId the commerce shipment ID
	 * @param commerceOrderItemId the commerce order item ID
	 * @return the matching commerce shipment items
	 */
	public java.util.List<CommerceShipmentItem> findByC_C(
		long commerceShipmentId, long commerceOrderItemId);

	/**
	 * Returns a range of all the commerce shipment items where commerceShipmentId = &#63; and commerceOrderItemId = &#63;.
	 *
	 * <p>
	 * Useful when paginating results. Returns a maximum of <code>end - start</code> instances. <code>start</code> and <code>end</code> are not primary keys, they are indexes in the result set. Thus, <code>0</code> refers to the first result in the set. Setting both <code>start</code> and <code>end</code> to <code>QueryUtil#ALL_POS</code> will return the full result set. If <code>orderByComparator</code> is specified, then the query will include the given ORDER BY logic. If <code>orderByComparator</code> is absent, then the query will include the default ORDER BY logic from <code>CommerceShipmentItemModelImpl</code>.
	 * </p>
	 *
	 * @param commerceShipmentId the commerce shipment ID
	 * @param commerceOrderItemId the commerce order item ID
	 * @param start the lower bound of the range of commerce shipment items
	 * @param end the upper bound of the range of commerce shipment items (not inclusive)
	 * @return the range of matching commerce shipment items
	 */
	public java.util.List<CommerceShipmentItem> findByC_C(
		long commerceShipmentId, long commerceOrderItemId, int start, int end);

	/**
	 * Returns an ordered range of all the commerce shipment items where commerceShipmentId = &#63; and commerceOrderItemId = &#63;.
	 *
	 * <p>
	 * Useful when paginating results. Returns a maximum of <code>end - start</code> instances. <code>start</code> and <code>end</code> are not primary keys, they are indexes in the result set. Thus, <code>0</code> refers to the first result in the set. Setting both <code>start</code> and <code>end</code> to <code>QueryUtil#ALL_POS</code> will return the full result set. If <code>orderByComparator</code> is specified, then the query will include the given ORDER BY logic. If <code>orderByComparator</code> is absent, then the query will include the default ORDER BY logic from <code>CommerceShipmentItemModelImpl</code>.
	 * </p>
	 *
	 * @param commerceShipmentId the commerce shipment ID
	 * @param commerceOrderItemId the commerce order item ID
	 * @param start the lower bound of the range of commerce shipment items
	 * @param end the upper bound of the range of commerce shipment items (not inclusive)
	 * @param orderByComparator the comparator to order the results by (optionally <code>null</code>)
	 * @return the ordered range of matching commerce shipment items
	 */
	public java.util.List<CommerceShipmentItem> findByC_C(
		long commerceShipmentId, long commerceOrderItemId, int start, int end,
		com.liferay.portal.kernel.util.OrderByComparator<CommerceShipmentItem>
			orderByComparator);

	/**
	 * Returns an ordered range of all the commerce shipment items where commerceShipmentId = &#63; and commerceOrderItemId = &#63;.
	 *
	 * <p>
	 * Useful when paginating results. Returns a maximum of <code>end - start</code> instances. <code>start</code> and <code>end</code> are not primary keys, they are indexes in the result set. Thus, <code>0</code> refers to the first result in the set. Setting both <code>start</code> and <code>end</code> to <code>QueryUtil#ALL_POS</code> will return the full result set. If <code>orderByComparator</code> is specified, then the query will include the given ORDER BY logic. If <code>orderByComparator</code> is absent, then the query will include the default ORDER BY logic from <code>CommerceShipmentItemModelImpl</code>.
	 * </p>
	 *
	 * @param commerceShipmentId the commerce shipment ID
	 * @param commerceOrderItemId the commerce order item ID
	 * @param start the lower bound of the range of commerce shipment items
	 * @param end the upper bound of the range of commerce shipment items (not inclusive)
	 * @param orderByComparator the comparator to order the results by (optionally <code>null</code>)
	 * @param useFinderCache whether to use the finder cache
	 * @return the ordered range of matching commerce shipment items
	 */
	public java.util.List<CommerceShipmentItem> findByC_C(
		long commerceShipmentId, long commerceOrderItemId, int start, int end,
		com.liferay.portal.kernel.util.OrderByComparator<CommerceShipmentItem>
			orderByComparator,
		boolean useFinderCache);

	/**
	 * Returns the first commerce shipment item in the ordered set where commerceShipmentId = &#63; and commerceOrderItemId = &#63;.
	 *
	 * @param commerceShipmentId the commerce shipment ID
	 * @param commerceOrderItemId the commerce order item ID
	 * @param orderByComparator the comparator to order the set by (optionally <code>null</code>)
	 * @return the first matching commerce shipment item
	 * @throws NoSuchShipmentItemException if a matching commerce shipment item could not be found
	 */
	public CommerceShipmentItem findByC_C_First(
			long commerceShipmentId, long commerceOrderItemId,
			com.liferay.portal.kernel.util.OrderByComparator
				<CommerceShipmentItem> orderByComparator)
		throws NoSuchShipmentItemException;

	/**
	 * Returns the first commerce shipment item in the ordered set where commerceShipmentId = &#63; and commerceOrderItemId = &#63;.
	 *
	 * @param commerceShipmentId the commerce shipment ID
	 * @param commerceOrderItemId the commerce order item ID
	 * @param orderByComparator the comparator to order the set by (optionally <code>null</code>)
	 * @return the first matching commerce shipment item, or <code>null</code> if a matching commerce shipment item could not be found
	 */
	public CommerceShipmentItem fetchByC_C_First(
		long commerceShipmentId, long commerceOrderItemId,
		com.liferay.portal.kernel.util.OrderByComparator<CommerceShipmentItem>
			orderByComparator);

	/**
	 * Returns the last commerce shipment item in the ordered set where commerceShipmentId = &#63; and commerceOrderItemId = &#63;.
	 *
	 * @param commerceShipmentId the commerce shipment ID
	 * @param commerceOrderItemId the commerce order item ID
	 * @param orderByComparator the comparator to order the set by (optionally <code>null</code>)
	 * @return the last matching commerce shipment item
	 * @throws NoSuchShipmentItemException if a matching commerce shipment item could not be found
	 */
	public CommerceShipmentItem findByC_C_Last(
			long commerceShipmentId, long commerceOrderItemId,
			com.liferay.portal.kernel.util.OrderByComparator
				<CommerceShipmentItem> orderByComparator)
		throws NoSuchShipmentItemException;

	/**
	 * Returns the last commerce shipment item in the ordered set where commerceShipmentId = &#63; and commerceOrderItemId = &#63;.
	 *
	 * @param commerceShipmentId the commerce shipment ID
	 * @param commerceOrderItemId the commerce order item ID
	 * @param orderByComparator the comparator to order the set by (optionally <code>null</code>)
	 * @return the last matching commerce shipment item, or <code>null</code> if a matching commerce shipment item could not be found
	 */
	public CommerceShipmentItem fetchByC_C_Last(
		long commerceShipmentId, long commerceOrderItemId,
		com.liferay.portal.kernel.util.OrderByComparator<CommerceShipmentItem>
			orderByComparator);

	/**
	 * Returns the commerce shipment items before and after the current commerce shipment item in the ordered set where commerceShipmentId = &#63; and commerceOrderItemId = &#63;.
	 *
	 * @param commerceShipmentItemId the primary key of the current commerce shipment item
	 * @param commerceShipmentId the commerce shipment ID
	 * @param commerceOrderItemId the commerce order item ID
	 * @param orderByComparator the comparator to order the set by (optionally <code>null</code>)
	 * @return the previous, current, and next commerce shipment item
	 * @throws NoSuchShipmentItemException if a commerce shipment item with the primary key could not be found
	 */
	public CommerceShipmentItem[] findByC_C_PrevAndNext(
			long commerceShipmentItemId, long commerceShipmentId,
			long commerceOrderItemId,
			com.liferay.portal.kernel.util.OrderByComparator
				<CommerceShipmentItem> orderByComparator)
		throws NoSuchShipmentItemException;

	/**
	 * Removes all the commerce shipment items where commerceShipmentId = &#63; and commerceOrderItemId = &#63; from the database.
	 *
	 * @param commerceShipmentId the commerce shipment ID
	 * @param commerceOrderItemId the commerce order item ID
	 */
	public void removeByC_C(long commerceShipmentId, long commerceOrderItemId);

	/**
	 * Returns the number of commerce shipment items where commerceShipmentId = &#63; and commerceOrderItemId = &#63;.
	 *
	 * @param commerceShipmentId the commerce shipment ID
	 * @param commerceOrderItemId the commerce order item ID
	 * @return the number of matching commerce shipment items
	 */
	public int countByC_C(long commerceShipmentId, long commerceOrderItemId);

	/**
	 * Returns the commerce shipment item where commerceShipmentId = &#63; and commerceOrderItemId = &#63; and commerceInventoryWarehouseId = &#63; or throws a <code>NoSuchShipmentItemException</code> if it could not be found.
	 *
	 * @param commerceShipmentId the commerce shipment ID
	 * @param commerceOrderItemId the commerce order item ID
	 * @param commerceInventoryWarehouseId the commerce inventory warehouse ID
	 * @return the matching commerce shipment item
	 * @throws NoSuchShipmentItemException if a matching commerce shipment item could not be found
	 */
	public CommerceShipmentItem findByC_C_C(
			long commerceShipmentId, long commerceOrderItemId,
			long commerceInventoryWarehouseId)
		throws NoSuchShipmentItemException;

	/**
	 * Returns the commerce shipment item where commerceShipmentId = &#63; and commerceOrderItemId = &#63; and commerceInventoryWarehouseId = &#63; or returns <code>null</code> if it could not be found. Uses the finder cache.
	 *
	 * @param commerceShipmentId the commerce shipment ID
	 * @param commerceOrderItemId the commerce order item ID
	 * @param commerceInventoryWarehouseId the commerce inventory warehouse ID
	 * @return the matching commerce shipment item, or <code>null</code> if a matching commerce shipment item could not be found
	 */
	public CommerceShipmentItem fetchByC_C_C(
		long commerceShipmentId, long commerceOrderItemId,
		long commerceInventoryWarehouseId);

	/**
	 * Returns the commerce shipment item where commerceShipmentId = &#63; and commerceOrderItemId = &#63; and commerceInventoryWarehouseId = &#63; or returns <code>null</code> if it could not be found, optionally using the finder cache.
	 *
	 * @param commerceShipmentId the commerce shipment ID
	 * @param commerceOrderItemId the commerce order item ID
	 * @param commerceInventoryWarehouseId the commerce inventory warehouse ID
	 * @param useFinderCache whether to use the finder cache
	 * @return the matching commerce shipment item, or <code>null</code> if a matching commerce shipment item could not be found
	 */
	public CommerceShipmentItem fetchByC_C_C(
		long commerceShipmentId, long commerceOrderItemId,
		long commerceInventoryWarehouseId, boolean useFinderCache);

	/**
	 * Removes the commerce shipment item where commerceShipmentId = &#63; and commerceOrderItemId = &#63; and commerceInventoryWarehouseId = &#63; from the database.
	 *
	 * @param commerceShipmentId the commerce shipment ID
	 * @param commerceOrderItemId the commerce order item ID
	 * @param commerceInventoryWarehouseId the commerce inventory warehouse ID
	 * @return the commerce shipment item that was removed
	 */
	public CommerceShipmentItem removeByC_C_C(
			long commerceShipmentId, long commerceOrderItemId,
			long commerceInventoryWarehouseId)
		throws NoSuchShipmentItemException;

	/**
	 * Returns the number of commerce shipment items where commerceShipmentId = &#63; and commerceOrderItemId = &#63; and commerceInventoryWarehouseId = &#63;.
	 *
	 * @param commerceShipmentId the commerce shipment ID
	 * @param commerceOrderItemId the commerce order item ID
	 * @param commerceInventoryWarehouseId the commerce inventory warehouse ID
	 * @return the number of matching commerce shipment items
	 */
	public int countByC_C_C(
		long commerceShipmentId, long commerceOrderItemId,
		long commerceInventoryWarehouseId);

	/**
	 * Returns all the commerce shipment items where commerceShipmentId = &#63; and commerceInventoryWarehouseId &ne; &#63; and quantity &ge; &#63;.
	 *
	 * @param commerceShipmentId the commerce shipment ID
	 * @param commerceInventoryWarehouseId the commerce inventory warehouse ID
	 * @param quantity the quantity
	 * @return the matching commerce shipment items
	 */
	public java.util.List<CommerceShipmentItem> findByC_NotC_GteQ(
		long commerceShipmentId, long commerceInventoryWarehouseId,
		BigDecimal quantity);

	/**
	 * Returns a range of all the commerce shipment items where commerceShipmentId = &#63; and commerceInventoryWarehouseId &ne; &#63; and quantity &ge; &#63;.
	 *
	 * <p>
	 * Useful when paginating results. Returns a maximum of <code>end - start</code> instances. <code>start</code> and <code>end</code> are not primary keys, they are indexes in the result set. Thus, <code>0</code> refers to the first result in the set. Setting both <code>start</code> and <code>end</code> to <code>QueryUtil#ALL_POS</code> will return the full result set. If <code>orderByComparator</code> is specified, then the query will include the given ORDER BY logic. If <code>orderByComparator</code> is absent, then the query will include the default ORDER BY logic from <code>CommerceShipmentItemModelImpl</code>.
	 * </p>
	 *
	 * @param commerceShipmentId the commerce shipment ID
	 * @param commerceInventoryWarehouseId the commerce inventory warehouse ID
	 * @param quantity the quantity
	 * @param start the lower bound of the range of commerce shipment items
	 * @param end the upper bound of the range of commerce shipment items (not inclusive)
	 * @return the range of matching commerce shipment items
	 */
	public java.util.List<CommerceShipmentItem> findByC_NotC_GteQ(
		long commerceShipmentId, long commerceInventoryWarehouseId,
		BigDecimal quantity, int start, int end);

	/**
	 * Returns an ordered range of all the commerce shipment items where commerceShipmentId = &#63; and commerceInventoryWarehouseId &ne; &#63; and quantity &ge; &#63;.
	 *
	 * <p>
	 * Useful when paginating results. Returns a maximum of <code>end - start</code> instances. <code>start</code> and <code>end</code> are not primary keys, they are indexes in the result set. Thus, <code>0</code> refers to the first result in the set. Setting both <code>start</code> and <code>end</code> to <code>QueryUtil#ALL_POS</code> will return the full result set. If <code>orderByComparator</code> is specified, then the query will include the given ORDER BY logic. If <code>orderByComparator</code> is absent, then the query will include the default ORDER BY logic from <code>CommerceShipmentItemModelImpl</code>.
	 * </p>
	 *
	 * @param commerceShipmentId the commerce shipment ID
	 * @param commerceInventoryWarehouseId the commerce inventory warehouse ID
	 * @param quantity the quantity
	 * @param start the lower bound of the range of commerce shipment items
	 * @param end the upper bound of the range of commerce shipment items (not inclusive)
	 * @param orderByComparator the comparator to order the results by (optionally <code>null</code>)
	 * @return the ordered range of matching commerce shipment items
	 */
	public java.util.List<CommerceShipmentItem> findByC_NotC_GteQ(
		long commerceShipmentId, long commerceInventoryWarehouseId,
		BigDecimal quantity, int start, int end,
		com.liferay.portal.kernel.util.OrderByComparator<CommerceShipmentItem>
			orderByComparator);

	/**
	 * Returns an ordered range of all the commerce shipment items where commerceShipmentId = &#63; and commerceInventoryWarehouseId &ne; &#63; and quantity &ge; &#63;.
	 *
	 * <p>
	 * Useful when paginating results. Returns a maximum of <code>end - start</code> instances. <code>start</code> and <code>end</code> are not primary keys, they are indexes in the result set. Thus, <code>0</code> refers to the first result in the set. Setting both <code>start</code> and <code>end</code> to <code>QueryUtil#ALL_POS</code> will return the full result set. If <code>orderByComparator</code> is specified, then the query will include the given ORDER BY logic. If <code>orderByComparator</code> is absent, then the query will include the default ORDER BY logic from <code>CommerceShipmentItemModelImpl</code>.
	 * </p>
	 *
	 * @param commerceShipmentId the commerce shipment ID
	 * @param commerceInventoryWarehouseId the commerce inventory warehouse ID
	 * @param quantity the quantity
	 * @param start the lower bound of the range of commerce shipment items
	 * @param end the upper bound of the range of commerce shipment items (not inclusive)
	 * @param orderByComparator the comparator to order the results by (optionally <code>null</code>)
	 * @param useFinderCache whether to use the finder cache
	 * @return the ordered range of matching commerce shipment items
	 */
	public java.util.List<CommerceShipmentItem> findByC_NotC_GteQ(
		long commerceShipmentId, long commerceInventoryWarehouseId,
		BigDecimal quantity, int start, int end,
		com.liferay.portal.kernel.util.OrderByComparator<CommerceShipmentItem>
			orderByComparator,
		boolean useFinderCache);

	/**
	 * Returns the first commerce shipment item in the ordered set where commerceShipmentId = &#63; and commerceInventoryWarehouseId &ne; &#63; and quantity &ge; &#63;.
	 *
	 * @param commerceShipmentId the commerce shipment ID
	 * @param commerceInventoryWarehouseId the commerce inventory warehouse ID
	 * @param quantity the quantity
	 * @param orderByComparator the comparator to order the set by (optionally <code>null</code>)
	 * @return the first matching commerce shipment item
	 * @throws NoSuchShipmentItemException if a matching commerce shipment item could not be found
	 */
	public CommerceShipmentItem findByC_NotC_GteQ_First(
			long commerceShipmentId, long commerceInventoryWarehouseId,
			BigDecimal quantity,
			com.liferay.portal.kernel.util.OrderByComparator
				<CommerceShipmentItem> orderByComparator)
		throws NoSuchShipmentItemException;

	/**
	 * Returns the first commerce shipment item in the ordered set where commerceShipmentId = &#63; and commerceInventoryWarehouseId &ne; &#63; and quantity &ge; &#63;.
	 *
	 * @param commerceShipmentId the commerce shipment ID
	 * @param commerceInventoryWarehouseId the commerce inventory warehouse ID
	 * @param quantity the quantity
	 * @param orderByComparator the comparator to order the set by (optionally <code>null</code>)
	 * @return the first matching commerce shipment item, or <code>null</code> if a matching commerce shipment item could not be found
	 */
	public CommerceShipmentItem fetchByC_NotC_GteQ_First(
		long commerceShipmentId, long commerceInventoryWarehouseId,
		BigDecimal quantity,
		com.liferay.portal.kernel.util.OrderByComparator<CommerceShipmentItem>
			orderByComparator);

	/**
	 * Returns the last commerce shipment item in the ordered set where commerceShipmentId = &#63; and commerceInventoryWarehouseId &ne; &#63; and quantity &ge; &#63;.
	 *
	 * @param commerceShipmentId the commerce shipment ID
	 * @param commerceInventoryWarehouseId the commerce inventory warehouse ID
	 * @param quantity the quantity
	 * @param orderByComparator the comparator to order the set by (optionally <code>null</code>)
	 * @return the last matching commerce shipment item
	 * @throws NoSuchShipmentItemException if a matching commerce shipment item could not be found
	 */
	public CommerceShipmentItem findByC_NotC_GteQ_Last(
			long commerceShipmentId, long commerceInventoryWarehouseId,
			BigDecimal quantity,
			com.liferay.portal.kernel.util.OrderByComparator
				<CommerceShipmentItem> orderByComparator)
		throws NoSuchShipmentItemException;

	/**
	 * Returns the last commerce shipment item in the ordered set where commerceShipmentId = &#63; and commerceInventoryWarehouseId &ne; &#63; and quantity &ge; &#63;.
	 *
	 * @param commerceShipmentId the commerce shipment ID
	 * @param commerceInventoryWarehouseId the commerce inventory warehouse ID
	 * @param quantity the quantity
	 * @param orderByComparator the comparator to order the set by (optionally <code>null</code>)
	 * @return the last matching commerce shipment item, or <code>null</code> if a matching commerce shipment item could not be found
	 */
	public CommerceShipmentItem fetchByC_NotC_GteQ_Last(
		long commerceShipmentId, long commerceInventoryWarehouseId,
		BigDecimal quantity,
		com.liferay.portal.kernel.util.OrderByComparator<CommerceShipmentItem>
			orderByComparator);

	/**
	 * Returns the commerce shipment items before and after the current commerce shipment item in the ordered set where commerceShipmentId = &#63; and commerceInventoryWarehouseId &ne; &#63; and quantity &ge; &#63;.
	 *
	 * @param commerceShipmentItemId the primary key of the current commerce shipment item
	 * @param commerceShipmentId the commerce shipment ID
	 * @param commerceInventoryWarehouseId the commerce inventory warehouse ID
	 * @param quantity the quantity
	 * @param orderByComparator the comparator to order the set by (optionally <code>null</code>)
	 * @return the previous, current, and next commerce shipment item
	 * @throws NoSuchShipmentItemException if a commerce shipment item with the primary key could not be found
	 */
	public CommerceShipmentItem[] findByC_NotC_GteQ_PrevAndNext(
			long commerceShipmentItemId, long commerceShipmentId,
			long commerceInventoryWarehouseId, BigDecimal quantity,
			com.liferay.portal.kernel.util.OrderByComparator
				<CommerceShipmentItem> orderByComparator)
		throws NoSuchShipmentItemException;

	/**
	 * Removes all the commerce shipment items where commerceShipmentId = &#63; and commerceInventoryWarehouseId &ne; &#63; and quantity &ge; &#63; from the database.
	 *
	 * @param commerceShipmentId the commerce shipment ID
	 * @param commerceInventoryWarehouseId the commerce inventory warehouse ID
	 * @param quantity the quantity
	 */
	public void removeByC_NotC_GteQ(
		long commerceShipmentId, long commerceInventoryWarehouseId,
		BigDecimal quantity);

	/**
	 * Returns the number of commerce shipment items where commerceShipmentId = &#63; and commerceInventoryWarehouseId &ne; &#63; and quantity &ge; &#63;.
	 *
	 * @param commerceShipmentId the commerce shipment ID
	 * @param commerceInventoryWarehouseId the commerce inventory warehouse ID
	 * @param quantity the quantity
	 * @return the number of matching commerce shipment items
	 */
	public int countByC_NotC_GteQ(
		long commerceShipmentId, long commerceInventoryWarehouseId,
		BigDecimal quantity);

	/**
	 * Returns the commerce shipment item where externalReferenceCode = &#63; and companyId = &#63; or throws a <code>NoSuchShipmentItemException</code> if it could not be found.
	 *
	 * @param externalReferenceCode the external reference code
	 * @param companyId the company ID
	 * @return the matching commerce shipment item
	 * @throws NoSuchShipmentItemException if a matching commerce shipment item could not be found
	 */
	public CommerceShipmentItem findByERC_C(
			String externalReferenceCode, long companyId)
		throws NoSuchShipmentItemException;

	/**
	 * Returns the commerce shipment item where externalReferenceCode = &#63; and companyId = &#63; or returns <code>null</code> if it could not be found. Uses the finder cache.
	 *
	 * @param externalReferenceCode the external reference code
	 * @param companyId the company ID
	 * @return the matching commerce shipment item, or <code>null</code> if a matching commerce shipment item could not be found
	 */
	public CommerceShipmentItem fetchByERC_C(
		String externalReferenceCode, long companyId);

	/**
	 * Returns the commerce shipment item where externalReferenceCode = &#63; and companyId = &#63; or returns <code>null</code> if it could not be found, optionally using the finder cache.
	 *
	 * @param externalReferenceCode the external reference code
	 * @param companyId the company ID
	 * @param useFinderCache whether to use the finder cache
	 * @return the matching commerce shipment item, or <code>null</code> if a matching commerce shipment item could not be found
	 */
	public CommerceShipmentItem fetchByERC_C(
		String externalReferenceCode, long companyId, boolean useFinderCache);

	/**
	 * Removes the commerce shipment item where externalReferenceCode = &#63; and companyId = &#63; from the database.
	 *
	 * @param externalReferenceCode the external reference code
	 * @param companyId the company ID
	 * @return the commerce shipment item that was removed
	 */
	public CommerceShipmentItem removeByERC_C(
			String externalReferenceCode, long companyId)
		throws NoSuchShipmentItemException;

	/**
	 * Returns the number of commerce shipment items where externalReferenceCode = &#63; and companyId = &#63;.
	 *
	 * @param externalReferenceCode the external reference code
	 * @param companyId the company ID
	 * @return the number of matching commerce shipment items
	 */
	public int countByERC_C(String externalReferenceCode, long companyId);

	/**
	 * Caches the commerce shipment item in the entity cache if it is enabled.
	 *
	 * @param commerceShipmentItem the commerce shipment item
	 */
	public void cacheResult(CommerceShipmentItem commerceShipmentItem);

	/**
	 * Caches the commerce shipment items in the entity cache if it is enabled.
	 *
	 * @param commerceShipmentItems the commerce shipment items
	 */
	public void cacheResult(
		java.util.List<CommerceShipmentItem> commerceShipmentItems);

	/**
	 * Creates a new commerce shipment item with the primary key. Does not add the commerce shipment item to the database.
	 *
	 * @param commerceShipmentItemId the primary key for the new commerce shipment item
	 * @return the new commerce shipment item
	 */
	public CommerceShipmentItem create(long commerceShipmentItemId);

	/**
	 * Removes the commerce shipment item with the primary key from the database. Also notifies the appropriate model listeners.
	 *
	 * @param commerceShipmentItemId the primary key of the commerce shipment item
	 * @return the commerce shipment item that was removed
	 * @throws NoSuchShipmentItemException if a commerce shipment item with the primary key could not be found
	 */
	public CommerceShipmentItem remove(long commerceShipmentItemId)
		throws NoSuchShipmentItemException;

	public CommerceShipmentItem updateImpl(
		CommerceShipmentItem commerceShipmentItem);

	/**
	 * Returns the commerce shipment item with the primary key or throws a <code>NoSuchShipmentItemException</code> if it could not be found.
	 *
	 * @param commerceShipmentItemId the primary key of the commerce shipment item
	 * @return the commerce shipment item
	 * @throws NoSuchShipmentItemException if a commerce shipment item with the primary key could not be found
	 */
	public CommerceShipmentItem findByPrimaryKey(long commerceShipmentItemId)
		throws NoSuchShipmentItemException;

	/**
	 * Returns the commerce shipment item with the primary key or returns <code>null</code> if it could not be found.
	 *
	 * @param commerceShipmentItemId the primary key of the commerce shipment item
	 * @return the commerce shipment item, or <code>null</code> if a commerce shipment item with the primary key could not be found
	 */
	public CommerceShipmentItem fetchByPrimaryKey(long commerceShipmentItemId);

	/**
	 * Returns all the commerce shipment items.
	 *
	 * @return the commerce shipment items
	 */
	public java.util.List<CommerceShipmentItem> findAll();

	/**
	 * Returns a range of all the commerce shipment items.
	 *
	 * <p>
	 * Useful when paginating results. Returns a maximum of <code>end - start</code> instances. <code>start</code> and <code>end</code> are not primary keys, they are indexes in the result set. Thus, <code>0</code> refers to the first result in the set. Setting both <code>start</code> and <code>end</code> to <code>QueryUtil#ALL_POS</code> will return the full result set. If <code>orderByComparator</code> is specified, then the query will include the given ORDER BY logic. If <code>orderByComparator</code> is absent, then the query will include the default ORDER BY logic from <code>CommerceShipmentItemModelImpl</code>.
	 * </p>
	 *
	 * @param start the lower bound of the range of commerce shipment items
	 * @param end the upper bound of the range of commerce shipment items (not inclusive)
	 * @return the range of commerce shipment items
	 */
	public java.util.List<CommerceShipmentItem> findAll(int start, int end);

	/**
	 * Returns an ordered range of all the commerce shipment items.
	 *
	 * <p>
	 * Useful when paginating results. Returns a maximum of <code>end - start</code> instances. <code>start</code> and <code>end</code> are not primary keys, they are indexes in the result set. Thus, <code>0</code> refers to the first result in the set. Setting both <code>start</code> and <code>end</code> to <code>QueryUtil#ALL_POS</code> will return the full result set. If <code>orderByComparator</code> is specified, then the query will include the given ORDER BY logic. If <code>orderByComparator</code> is absent, then the query will include the default ORDER BY logic from <code>CommerceShipmentItemModelImpl</code>.
	 * </p>
	 *
	 * @param start the lower bound of the range of commerce shipment items
	 * @param end the upper bound of the range of commerce shipment items (not inclusive)
	 * @param orderByComparator the comparator to order the results by (optionally <code>null</code>)
	 * @return the ordered range of commerce shipment items
	 */
	public java.util.List<CommerceShipmentItem> findAll(
		int start, int end,
		com.liferay.portal.kernel.util.OrderByComparator<CommerceShipmentItem>
			orderByComparator);

	/**
	 * Returns an ordered range of all the commerce shipment items.
	 *
	 * <p>
	 * Useful when paginating results. Returns a maximum of <code>end - start</code> instances. <code>start</code> and <code>end</code> are not primary keys, they are indexes in the result set. Thus, <code>0</code> refers to the first result in the set. Setting both <code>start</code> and <code>end</code> to <code>QueryUtil#ALL_POS</code> will return the full result set. If <code>orderByComparator</code> is specified, then the query will include the given ORDER BY logic. If <code>orderByComparator</code> is absent, then the query will include the default ORDER BY logic from <code>CommerceShipmentItemModelImpl</code>.
	 * </p>
	 *
	 * @param start the lower bound of the range of commerce shipment items
	 * @param end the upper bound of the range of commerce shipment items (not inclusive)
	 * @param orderByComparator the comparator to order the results by (optionally <code>null</code>)
	 * @param useFinderCache whether to use the finder cache
	 * @return the ordered range of commerce shipment items
	 */
	public java.util.List<CommerceShipmentItem> findAll(
		int start, int end,
		com.liferay.portal.kernel.util.OrderByComparator<CommerceShipmentItem>
			orderByComparator,
		boolean useFinderCache);

	/**
	 * Removes all the commerce shipment items from the database.
	 */
	public void removeAll();

	/**
	 * Returns the number of commerce shipment items.
	 *
	 * @return the number of commerce shipment items
	 */
	public int countAll();

}