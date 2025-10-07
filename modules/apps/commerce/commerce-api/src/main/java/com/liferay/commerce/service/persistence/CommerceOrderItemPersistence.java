/**
 * SPDX-FileCopyrightText: (c) 2000 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

package com.liferay.commerce.service.persistence;

import com.liferay.commerce.exception.NoSuchOrderItemException;
import com.liferay.commerce.model.CommerceOrderItem;
import com.liferay.portal.kernel.service.persistence.BasePersistence;

import org.osgi.annotation.versioning.ProviderType;

/**
 * The persistence interface for the commerce order item service.
 *
 * <p>
 * Caching information and settings can be found in <code>portal.properties</code>
 * </p>
 *
 * @author Alessio Antonio Rendina
 * @see CommerceOrderItemUtil
 * @generated
 */
@ProviderType
public interface CommerceOrderItemPersistence
	extends BasePersistence<CommerceOrderItem> {

	/*
	 * NOTE FOR DEVELOPERS:
	 *
	 * Never modify or reference this interface directly. Always use {@link CommerceOrderItemUtil} to access the commerce order item persistence. Modify <code>service.xml</code> and rerun ServiceBuilder to regenerate this interface.
	 */

	/**
	 * Returns all the commerce order items where uuid = &#63;.
	 *
	 * @param uuid the uuid
	 * @return the matching commerce order items
	 */
	public java.util.List<CommerceOrderItem> findByUuid(String uuid);

	/**
	 * Returns a range of all the commerce order items where uuid = &#63;.
	 *
	 * <p>
	 * Useful when paginating results. Returns a maximum of <code>end - start</code> instances. <code>start</code> and <code>end</code> are not primary keys, they are indexes in the result set. Thus, <code>0</code> refers to the first result in the set. Setting both <code>start</code> and <code>end</code> to <code>QueryUtil#ALL_POS</code> will return the full result set. If <code>orderByComparator</code> is specified, then the query will include the given ORDER BY logic. If <code>orderByComparator</code> is absent, then the query will include the default ORDER BY logic from <code>CommerceOrderItemModelImpl</code>.
	 * </p>
	 *
	 * @param uuid the uuid
	 * @param start the lower bound of the range of commerce order items
	 * @param end the upper bound of the range of commerce order items (not inclusive)
	 * @return the range of matching commerce order items
	 */
	public java.util.List<CommerceOrderItem> findByUuid(
		String uuid, int start, int end);

	/**
	 * Returns an ordered range of all the commerce order items where uuid = &#63;.
	 *
	 * <p>
	 * Useful when paginating results. Returns a maximum of <code>end - start</code> instances. <code>start</code> and <code>end</code> are not primary keys, they are indexes in the result set. Thus, <code>0</code> refers to the first result in the set. Setting both <code>start</code> and <code>end</code> to <code>QueryUtil#ALL_POS</code> will return the full result set. If <code>orderByComparator</code> is specified, then the query will include the given ORDER BY logic. If <code>orderByComparator</code> is absent, then the query will include the default ORDER BY logic from <code>CommerceOrderItemModelImpl</code>.
	 * </p>
	 *
	 * @param uuid the uuid
	 * @param start the lower bound of the range of commerce order items
	 * @param end the upper bound of the range of commerce order items (not inclusive)
	 * @param orderByComparator the comparator to order the results by (optionally <code>null</code>)
	 * @return the ordered range of matching commerce order items
	 */
	public java.util.List<CommerceOrderItem> findByUuid(
		String uuid, int start, int end,
		com.liferay.portal.kernel.util.OrderByComparator<CommerceOrderItem>
			orderByComparator);

	/**
	 * Returns an ordered range of all the commerce order items where uuid = &#63;.
	 *
	 * <p>
	 * Useful when paginating results. Returns a maximum of <code>end - start</code> instances. <code>start</code> and <code>end</code> are not primary keys, they are indexes in the result set. Thus, <code>0</code> refers to the first result in the set. Setting both <code>start</code> and <code>end</code> to <code>QueryUtil#ALL_POS</code> will return the full result set. If <code>orderByComparator</code> is specified, then the query will include the given ORDER BY logic. If <code>orderByComparator</code> is absent, then the query will include the default ORDER BY logic from <code>CommerceOrderItemModelImpl</code>.
	 * </p>
	 *
	 * @param uuid the uuid
	 * @param start the lower bound of the range of commerce order items
	 * @param end the upper bound of the range of commerce order items (not inclusive)
	 * @param orderByComparator the comparator to order the results by (optionally <code>null</code>)
	 * @param useFinderCache whether to use the finder cache
	 * @return the ordered range of matching commerce order items
	 */
	public java.util.List<CommerceOrderItem> findByUuid(
		String uuid, int start, int end,
		com.liferay.portal.kernel.util.OrderByComparator<CommerceOrderItem>
			orderByComparator,
		boolean useFinderCache);

	/**
	 * Returns the first commerce order item in the ordered set where uuid = &#63;.
	 *
	 * @param uuid the uuid
	 * @param orderByComparator the comparator to order the set by (optionally <code>null</code>)
	 * @return the first matching commerce order item
	 * @throws NoSuchOrderItemException if a matching commerce order item could not be found
	 */
	public CommerceOrderItem findByUuid_First(
			String uuid,
			com.liferay.portal.kernel.util.OrderByComparator<CommerceOrderItem>
				orderByComparator)
		throws NoSuchOrderItemException;

	/**
	 * Returns the first commerce order item in the ordered set where uuid = &#63;.
	 *
	 * @param uuid the uuid
	 * @param orderByComparator the comparator to order the set by (optionally <code>null</code>)
	 * @return the first matching commerce order item, or <code>null</code> if a matching commerce order item could not be found
	 */
	public CommerceOrderItem fetchByUuid_First(
		String uuid,
		com.liferay.portal.kernel.util.OrderByComparator<CommerceOrderItem>
			orderByComparator);

	/**
	 * Returns the last commerce order item in the ordered set where uuid = &#63;.
	 *
	 * @param uuid the uuid
	 * @param orderByComparator the comparator to order the set by (optionally <code>null</code>)
	 * @return the last matching commerce order item
	 * @throws NoSuchOrderItemException if a matching commerce order item could not be found
	 */
	public CommerceOrderItem findByUuid_Last(
			String uuid,
			com.liferay.portal.kernel.util.OrderByComparator<CommerceOrderItem>
				orderByComparator)
		throws NoSuchOrderItemException;

	/**
	 * Returns the last commerce order item in the ordered set where uuid = &#63;.
	 *
	 * @param uuid the uuid
	 * @param orderByComparator the comparator to order the set by (optionally <code>null</code>)
	 * @return the last matching commerce order item, or <code>null</code> if a matching commerce order item could not be found
	 */
	public CommerceOrderItem fetchByUuid_Last(
		String uuid,
		com.liferay.portal.kernel.util.OrderByComparator<CommerceOrderItem>
			orderByComparator);

	/**
	 * Returns the commerce order items before and after the current commerce order item in the ordered set where uuid = &#63;.
	 *
	 * @param commerceOrderItemId the primary key of the current commerce order item
	 * @param uuid the uuid
	 * @param orderByComparator the comparator to order the set by (optionally <code>null</code>)
	 * @return the previous, current, and next commerce order item
	 * @throws NoSuchOrderItemException if a commerce order item with the primary key could not be found
	 */
	public CommerceOrderItem[] findByUuid_PrevAndNext(
			long commerceOrderItemId, String uuid,
			com.liferay.portal.kernel.util.OrderByComparator<CommerceOrderItem>
				orderByComparator)
		throws NoSuchOrderItemException;

	/**
	 * Removes all the commerce order items where uuid = &#63; from the database.
	 *
	 * @param uuid the uuid
	 */
	public void removeByUuid(String uuid);

	/**
	 * Returns the number of commerce order items where uuid = &#63;.
	 *
	 * @param uuid the uuid
	 * @return the number of matching commerce order items
	 */
	public int countByUuid(String uuid);

	/**
	 * Returns the commerce order item where uuid = &#63; and groupId = &#63; or throws a <code>NoSuchOrderItemException</code> if it could not be found.
	 *
	 * @param uuid the uuid
	 * @param groupId the group ID
	 * @return the matching commerce order item
	 * @throws NoSuchOrderItemException if a matching commerce order item could not be found
	 */
	public CommerceOrderItem findByUUID_G(String uuid, long groupId)
		throws NoSuchOrderItemException;

	/**
	 * Returns the commerce order item where uuid = &#63; and groupId = &#63; or returns <code>null</code> if it could not be found. Uses the finder cache.
	 *
	 * @param uuid the uuid
	 * @param groupId the group ID
	 * @return the matching commerce order item, or <code>null</code> if a matching commerce order item could not be found
	 */
	public CommerceOrderItem fetchByUUID_G(String uuid, long groupId);

	/**
	 * Returns the commerce order item where uuid = &#63; and groupId = &#63; or returns <code>null</code> if it could not be found, optionally using the finder cache.
	 *
	 * @param uuid the uuid
	 * @param groupId the group ID
	 * @param useFinderCache whether to use the finder cache
	 * @return the matching commerce order item, or <code>null</code> if a matching commerce order item could not be found
	 */
	public CommerceOrderItem fetchByUUID_G(
		String uuid, long groupId, boolean useFinderCache);

	/**
	 * Removes the commerce order item where uuid = &#63; and groupId = &#63; from the database.
	 *
	 * @param uuid the uuid
	 * @param groupId the group ID
	 * @return the commerce order item that was removed
	 */
	public CommerceOrderItem removeByUUID_G(String uuid, long groupId)
		throws NoSuchOrderItemException;

	/**
	 * Returns the number of commerce order items where uuid = &#63; and groupId = &#63;.
	 *
	 * @param uuid the uuid
	 * @param groupId the group ID
	 * @return the number of matching commerce order items
	 */
	public int countByUUID_G(String uuid, long groupId);

	/**
	 * Returns all the commerce order items where uuid = &#63; and companyId = &#63;.
	 *
	 * @param uuid the uuid
	 * @param companyId the company ID
	 * @return the matching commerce order items
	 */
	public java.util.List<CommerceOrderItem> findByUuid_C(
		String uuid, long companyId);

	/**
	 * Returns a range of all the commerce order items where uuid = &#63; and companyId = &#63;.
	 *
	 * <p>
	 * Useful when paginating results. Returns a maximum of <code>end - start</code> instances. <code>start</code> and <code>end</code> are not primary keys, they are indexes in the result set. Thus, <code>0</code> refers to the first result in the set. Setting both <code>start</code> and <code>end</code> to <code>QueryUtil#ALL_POS</code> will return the full result set. If <code>orderByComparator</code> is specified, then the query will include the given ORDER BY logic. If <code>orderByComparator</code> is absent, then the query will include the default ORDER BY logic from <code>CommerceOrderItemModelImpl</code>.
	 * </p>
	 *
	 * @param uuid the uuid
	 * @param companyId the company ID
	 * @param start the lower bound of the range of commerce order items
	 * @param end the upper bound of the range of commerce order items (not inclusive)
	 * @return the range of matching commerce order items
	 */
	public java.util.List<CommerceOrderItem> findByUuid_C(
		String uuid, long companyId, int start, int end);

	/**
	 * Returns an ordered range of all the commerce order items where uuid = &#63; and companyId = &#63;.
	 *
	 * <p>
	 * Useful when paginating results. Returns a maximum of <code>end - start</code> instances. <code>start</code> and <code>end</code> are not primary keys, they are indexes in the result set. Thus, <code>0</code> refers to the first result in the set. Setting both <code>start</code> and <code>end</code> to <code>QueryUtil#ALL_POS</code> will return the full result set. If <code>orderByComparator</code> is specified, then the query will include the given ORDER BY logic. If <code>orderByComparator</code> is absent, then the query will include the default ORDER BY logic from <code>CommerceOrderItemModelImpl</code>.
	 * </p>
	 *
	 * @param uuid the uuid
	 * @param companyId the company ID
	 * @param start the lower bound of the range of commerce order items
	 * @param end the upper bound of the range of commerce order items (not inclusive)
	 * @param orderByComparator the comparator to order the results by (optionally <code>null</code>)
	 * @return the ordered range of matching commerce order items
	 */
	public java.util.List<CommerceOrderItem> findByUuid_C(
		String uuid, long companyId, int start, int end,
		com.liferay.portal.kernel.util.OrderByComparator<CommerceOrderItem>
			orderByComparator);

	/**
	 * Returns an ordered range of all the commerce order items where uuid = &#63; and companyId = &#63;.
	 *
	 * <p>
	 * Useful when paginating results. Returns a maximum of <code>end - start</code> instances. <code>start</code> and <code>end</code> are not primary keys, they are indexes in the result set. Thus, <code>0</code> refers to the first result in the set. Setting both <code>start</code> and <code>end</code> to <code>QueryUtil#ALL_POS</code> will return the full result set. If <code>orderByComparator</code> is specified, then the query will include the given ORDER BY logic. If <code>orderByComparator</code> is absent, then the query will include the default ORDER BY logic from <code>CommerceOrderItemModelImpl</code>.
	 * </p>
	 *
	 * @param uuid the uuid
	 * @param companyId the company ID
	 * @param start the lower bound of the range of commerce order items
	 * @param end the upper bound of the range of commerce order items (not inclusive)
	 * @param orderByComparator the comparator to order the results by (optionally <code>null</code>)
	 * @param useFinderCache whether to use the finder cache
	 * @return the ordered range of matching commerce order items
	 */
	public java.util.List<CommerceOrderItem> findByUuid_C(
		String uuid, long companyId, int start, int end,
		com.liferay.portal.kernel.util.OrderByComparator<CommerceOrderItem>
			orderByComparator,
		boolean useFinderCache);

	/**
	 * Returns the first commerce order item in the ordered set where uuid = &#63; and companyId = &#63;.
	 *
	 * @param uuid the uuid
	 * @param companyId the company ID
	 * @param orderByComparator the comparator to order the set by (optionally <code>null</code>)
	 * @return the first matching commerce order item
	 * @throws NoSuchOrderItemException if a matching commerce order item could not be found
	 */
	public CommerceOrderItem findByUuid_C_First(
			String uuid, long companyId,
			com.liferay.portal.kernel.util.OrderByComparator<CommerceOrderItem>
				orderByComparator)
		throws NoSuchOrderItemException;

	/**
	 * Returns the first commerce order item in the ordered set where uuid = &#63; and companyId = &#63;.
	 *
	 * @param uuid the uuid
	 * @param companyId the company ID
	 * @param orderByComparator the comparator to order the set by (optionally <code>null</code>)
	 * @return the first matching commerce order item, or <code>null</code> if a matching commerce order item could not be found
	 */
	public CommerceOrderItem fetchByUuid_C_First(
		String uuid, long companyId,
		com.liferay.portal.kernel.util.OrderByComparator<CommerceOrderItem>
			orderByComparator);

	/**
	 * Returns the last commerce order item in the ordered set where uuid = &#63; and companyId = &#63;.
	 *
	 * @param uuid the uuid
	 * @param companyId the company ID
	 * @param orderByComparator the comparator to order the set by (optionally <code>null</code>)
	 * @return the last matching commerce order item
	 * @throws NoSuchOrderItemException if a matching commerce order item could not be found
	 */
	public CommerceOrderItem findByUuid_C_Last(
			String uuid, long companyId,
			com.liferay.portal.kernel.util.OrderByComparator<CommerceOrderItem>
				orderByComparator)
		throws NoSuchOrderItemException;

	/**
	 * Returns the last commerce order item in the ordered set where uuid = &#63; and companyId = &#63;.
	 *
	 * @param uuid the uuid
	 * @param companyId the company ID
	 * @param orderByComparator the comparator to order the set by (optionally <code>null</code>)
	 * @return the last matching commerce order item, or <code>null</code> if a matching commerce order item could not be found
	 */
	public CommerceOrderItem fetchByUuid_C_Last(
		String uuid, long companyId,
		com.liferay.portal.kernel.util.OrderByComparator<CommerceOrderItem>
			orderByComparator);

	/**
	 * Returns the commerce order items before and after the current commerce order item in the ordered set where uuid = &#63; and companyId = &#63;.
	 *
	 * @param commerceOrderItemId the primary key of the current commerce order item
	 * @param uuid the uuid
	 * @param companyId the company ID
	 * @param orderByComparator the comparator to order the set by (optionally <code>null</code>)
	 * @return the previous, current, and next commerce order item
	 * @throws NoSuchOrderItemException if a commerce order item with the primary key could not be found
	 */
	public CommerceOrderItem[] findByUuid_C_PrevAndNext(
			long commerceOrderItemId, String uuid, long companyId,
			com.liferay.portal.kernel.util.OrderByComparator<CommerceOrderItem>
				orderByComparator)
		throws NoSuchOrderItemException;

	/**
	 * Removes all the commerce order items where uuid = &#63; and companyId = &#63; from the database.
	 *
	 * @param uuid the uuid
	 * @param companyId the company ID
	 */
	public void removeByUuid_C(String uuid, long companyId);

	/**
	 * Returns the number of commerce order items where uuid = &#63; and companyId = &#63;.
	 *
	 * @param uuid the uuid
	 * @param companyId the company ID
	 * @return the number of matching commerce order items
	 */
	public int countByUuid_C(String uuid, long companyId);

	/**
	 * Returns the commerce order item where commerceInventoryBookedQuantityId = &#63; or throws a <code>NoSuchOrderItemException</code> if it could not be found.
	 *
	 * @param commerceInventoryBookedQuantityId the commerce inventory booked quantity ID
	 * @return the matching commerce order item
	 * @throws NoSuchOrderItemException if a matching commerce order item could not be found
	 */
	public CommerceOrderItem findByCommerceInventoryBookedQuantityId(
			long commerceInventoryBookedQuantityId)
		throws NoSuchOrderItemException;

	/**
	 * Returns the commerce order item where commerceInventoryBookedQuantityId = &#63; or returns <code>null</code> if it could not be found. Uses the finder cache.
	 *
	 * @param commerceInventoryBookedQuantityId the commerce inventory booked quantity ID
	 * @return the matching commerce order item, or <code>null</code> if a matching commerce order item could not be found
	 */
	public CommerceOrderItem fetchByCommerceInventoryBookedQuantityId(
		long commerceInventoryBookedQuantityId);

	/**
	 * Returns the commerce order item where commerceInventoryBookedQuantityId = &#63; or returns <code>null</code> if it could not be found, optionally using the finder cache.
	 *
	 * @param commerceInventoryBookedQuantityId the commerce inventory booked quantity ID
	 * @param useFinderCache whether to use the finder cache
	 * @return the matching commerce order item, or <code>null</code> if a matching commerce order item could not be found
	 */
	public CommerceOrderItem fetchByCommerceInventoryBookedQuantityId(
		long commerceInventoryBookedQuantityId, boolean useFinderCache);

	/**
	 * Removes the commerce order item where commerceInventoryBookedQuantityId = &#63; from the database.
	 *
	 * @param commerceInventoryBookedQuantityId the commerce inventory booked quantity ID
	 * @return the commerce order item that was removed
	 */
	public CommerceOrderItem removeByCommerceInventoryBookedQuantityId(
			long commerceInventoryBookedQuantityId)
		throws NoSuchOrderItemException;

	/**
	 * Returns the number of commerce order items where commerceInventoryBookedQuantityId = &#63;.
	 *
	 * @param commerceInventoryBookedQuantityId the commerce inventory booked quantity ID
	 * @return the number of matching commerce order items
	 */
	public int countByCommerceInventoryBookedQuantityId(
		long commerceInventoryBookedQuantityId);

	/**
	 * Returns all the commerce order items where commerceOrderId = &#63;.
	 *
	 * @param commerceOrderId the commerce order ID
	 * @return the matching commerce order items
	 */
	public java.util.List<CommerceOrderItem> findByCommerceOrderId(
		long commerceOrderId);

	/**
	 * Returns a range of all the commerce order items where commerceOrderId = &#63;.
	 *
	 * <p>
	 * Useful when paginating results. Returns a maximum of <code>end - start</code> instances. <code>start</code> and <code>end</code> are not primary keys, they are indexes in the result set. Thus, <code>0</code> refers to the first result in the set. Setting both <code>start</code> and <code>end</code> to <code>QueryUtil#ALL_POS</code> will return the full result set. If <code>orderByComparator</code> is specified, then the query will include the given ORDER BY logic. If <code>orderByComparator</code> is absent, then the query will include the default ORDER BY logic from <code>CommerceOrderItemModelImpl</code>.
	 * </p>
	 *
	 * @param commerceOrderId the commerce order ID
	 * @param start the lower bound of the range of commerce order items
	 * @param end the upper bound of the range of commerce order items (not inclusive)
	 * @return the range of matching commerce order items
	 */
	public java.util.List<CommerceOrderItem> findByCommerceOrderId(
		long commerceOrderId, int start, int end);

	/**
	 * Returns an ordered range of all the commerce order items where commerceOrderId = &#63;.
	 *
	 * <p>
	 * Useful when paginating results. Returns a maximum of <code>end - start</code> instances. <code>start</code> and <code>end</code> are not primary keys, they are indexes in the result set. Thus, <code>0</code> refers to the first result in the set. Setting both <code>start</code> and <code>end</code> to <code>QueryUtil#ALL_POS</code> will return the full result set. If <code>orderByComparator</code> is specified, then the query will include the given ORDER BY logic. If <code>orderByComparator</code> is absent, then the query will include the default ORDER BY logic from <code>CommerceOrderItemModelImpl</code>.
	 * </p>
	 *
	 * @param commerceOrderId the commerce order ID
	 * @param start the lower bound of the range of commerce order items
	 * @param end the upper bound of the range of commerce order items (not inclusive)
	 * @param orderByComparator the comparator to order the results by (optionally <code>null</code>)
	 * @return the ordered range of matching commerce order items
	 */
	public java.util.List<CommerceOrderItem> findByCommerceOrderId(
		long commerceOrderId, int start, int end,
		com.liferay.portal.kernel.util.OrderByComparator<CommerceOrderItem>
			orderByComparator);

	/**
	 * Returns an ordered range of all the commerce order items where commerceOrderId = &#63;.
	 *
	 * <p>
	 * Useful when paginating results. Returns a maximum of <code>end - start</code> instances. <code>start</code> and <code>end</code> are not primary keys, they are indexes in the result set. Thus, <code>0</code> refers to the first result in the set. Setting both <code>start</code> and <code>end</code> to <code>QueryUtil#ALL_POS</code> will return the full result set. If <code>orderByComparator</code> is specified, then the query will include the given ORDER BY logic. If <code>orderByComparator</code> is absent, then the query will include the default ORDER BY logic from <code>CommerceOrderItemModelImpl</code>.
	 * </p>
	 *
	 * @param commerceOrderId the commerce order ID
	 * @param start the lower bound of the range of commerce order items
	 * @param end the upper bound of the range of commerce order items (not inclusive)
	 * @param orderByComparator the comparator to order the results by (optionally <code>null</code>)
	 * @param useFinderCache whether to use the finder cache
	 * @return the ordered range of matching commerce order items
	 */
	public java.util.List<CommerceOrderItem> findByCommerceOrderId(
		long commerceOrderId, int start, int end,
		com.liferay.portal.kernel.util.OrderByComparator<CommerceOrderItem>
			orderByComparator,
		boolean useFinderCache);

	/**
	 * Returns the first commerce order item in the ordered set where commerceOrderId = &#63;.
	 *
	 * @param commerceOrderId the commerce order ID
	 * @param orderByComparator the comparator to order the set by (optionally <code>null</code>)
	 * @return the first matching commerce order item
	 * @throws NoSuchOrderItemException if a matching commerce order item could not be found
	 */
	public CommerceOrderItem findByCommerceOrderId_First(
			long commerceOrderId,
			com.liferay.portal.kernel.util.OrderByComparator<CommerceOrderItem>
				orderByComparator)
		throws NoSuchOrderItemException;

	/**
	 * Returns the first commerce order item in the ordered set where commerceOrderId = &#63;.
	 *
	 * @param commerceOrderId the commerce order ID
	 * @param orderByComparator the comparator to order the set by (optionally <code>null</code>)
	 * @return the first matching commerce order item, or <code>null</code> if a matching commerce order item could not be found
	 */
	public CommerceOrderItem fetchByCommerceOrderId_First(
		long commerceOrderId,
		com.liferay.portal.kernel.util.OrderByComparator<CommerceOrderItem>
			orderByComparator);

	/**
	 * Returns the last commerce order item in the ordered set where commerceOrderId = &#63;.
	 *
	 * @param commerceOrderId the commerce order ID
	 * @param orderByComparator the comparator to order the set by (optionally <code>null</code>)
	 * @return the last matching commerce order item
	 * @throws NoSuchOrderItemException if a matching commerce order item could not be found
	 */
	public CommerceOrderItem findByCommerceOrderId_Last(
			long commerceOrderId,
			com.liferay.portal.kernel.util.OrderByComparator<CommerceOrderItem>
				orderByComparator)
		throws NoSuchOrderItemException;

	/**
	 * Returns the last commerce order item in the ordered set where commerceOrderId = &#63;.
	 *
	 * @param commerceOrderId the commerce order ID
	 * @param orderByComparator the comparator to order the set by (optionally <code>null</code>)
	 * @return the last matching commerce order item, or <code>null</code> if a matching commerce order item could not be found
	 */
	public CommerceOrderItem fetchByCommerceOrderId_Last(
		long commerceOrderId,
		com.liferay.portal.kernel.util.OrderByComparator<CommerceOrderItem>
			orderByComparator);

	/**
	 * Returns the commerce order items before and after the current commerce order item in the ordered set where commerceOrderId = &#63;.
	 *
	 * @param commerceOrderItemId the primary key of the current commerce order item
	 * @param commerceOrderId the commerce order ID
	 * @param orderByComparator the comparator to order the set by (optionally <code>null</code>)
	 * @return the previous, current, and next commerce order item
	 * @throws NoSuchOrderItemException if a commerce order item with the primary key could not be found
	 */
	public CommerceOrderItem[] findByCommerceOrderId_PrevAndNext(
			long commerceOrderItemId, long commerceOrderId,
			com.liferay.portal.kernel.util.OrderByComparator<CommerceOrderItem>
				orderByComparator)
		throws NoSuchOrderItemException;

	/**
	 * Removes all the commerce order items where commerceOrderId = &#63; from the database.
	 *
	 * @param commerceOrderId the commerce order ID
	 */
	public void removeByCommerceOrderId(long commerceOrderId);

	/**
	 * Returns the number of commerce order items where commerceOrderId = &#63;.
	 *
	 * @param commerceOrderId the commerce order ID
	 * @return the number of matching commerce order items
	 */
	public int countByCommerceOrderId(long commerceOrderId);

	/**
	 * Returns all the commerce order items where CPInstanceId = &#63;.
	 *
	 * @param CPInstanceId the cp instance ID
	 * @return the matching commerce order items
	 */
	public java.util.List<CommerceOrderItem> findByCPInstanceId(
		long CPInstanceId);

	/**
	 * Returns a range of all the commerce order items where CPInstanceId = &#63;.
	 *
	 * <p>
	 * Useful when paginating results. Returns a maximum of <code>end - start</code> instances. <code>start</code> and <code>end</code> are not primary keys, they are indexes in the result set. Thus, <code>0</code> refers to the first result in the set. Setting both <code>start</code> and <code>end</code> to <code>QueryUtil#ALL_POS</code> will return the full result set. If <code>orderByComparator</code> is specified, then the query will include the given ORDER BY logic. If <code>orderByComparator</code> is absent, then the query will include the default ORDER BY logic from <code>CommerceOrderItemModelImpl</code>.
	 * </p>
	 *
	 * @param CPInstanceId the cp instance ID
	 * @param start the lower bound of the range of commerce order items
	 * @param end the upper bound of the range of commerce order items (not inclusive)
	 * @return the range of matching commerce order items
	 */
	public java.util.List<CommerceOrderItem> findByCPInstanceId(
		long CPInstanceId, int start, int end);

	/**
	 * Returns an ordered range of all the commerce order items where CPInstanceId = &#63;.
	 *
	 * <p>
	 * Useful when paginating results. Returns a maximum of <code>end - start</code> instances. <code>start</code> and <code>end</code> are not primary keys, they are indexes in the result set. Thus, <code>0</code> refers to the first result in the set. Setting both <code>start</code> and <code>end</code> to <code>QueryUtil#ALL_POS</code> will return the full result set. If <code>orderByComparator</code> is specified, then the query will include the given ORDER BY logic. If <code>orderByComparator</code> is absent, then the query will include the default ORDER BY logic from <code>CommerceOrderItemModelImpl</code>.
	 * </p>
	 *
	 * @param CPInstanceId the cp instance ID
	 * @param start the lower bound of the range of commerce order items
	 * @param end the upper bound of the range of commerce order items (not inclusive)
	 * @param orderByComparator the comparator to order the results by (optionally <code>null</code>)
	 * @return the ordered range of matching commerce order items
	 */
	public java.util.List<CommerceOrderItem> findByCPInstanceId(
		long CPInstanceId, int start, int end,
		com.liferay.portal.kernel.util.OrderByComparator<CommerceOrderItem>
			orderByComparator);

	/**
	 * Returns an ordered range of all the commerce order items where CPInstanceId = &#63;.
	 *
	 * <p>
	 * Useful when paginating results. Returns a maximum of <code>end - start</code> instances. <code>start</code> and <code>end</code> are not primary keys, they are indexes in the result set. Thus, <code>0</code> refers to the first result in the set. Setting both <code>start</code> and <code>end</code> to <code>QueryUtil#ALL_POS</code> will return the full result set. If <code>orderByComparator</code> is specified, then the query will include the given ORDER BY logic. If <code>orderByComparator</code> is absent, then the query will include the default ORDER BY logic from <code>CommerceOrderItemModelImpl</code>.
	 * </p>
	 *
	 * @param CPInstanceId the cp instance ID
	 * @param start the lower bound of the range of commerce order items
	 * @param end the upper bound of the range of commerce order items (not inclusive)
	 * @param orderByComparator the comparator to order the results by (optionally <code>null</code>)
	 * @param useFinderCache whether to use the finder cache
	 * @return the ordered range of matching commerce order items
	 */
	public java.util.List<CommerceOrderItem> findByCPInstanceId(
		long CPInstanceId, int start, int end,
		com.liferay.portal.kernel.util.OrderByComparator<CommerceOrderItem>
			orderByComparator,
		boolean useFinderCache);

	/**
	 * Returns the first commerce order item in the ordered set where CPInstanceId = &#63;.
	 *
	 * @param CPInstanceId the cp instance ID
	 * @param orderByComparator the comparator to order the set by (optionally <code>null</code>)
	 * @return the first matching commerce order item
	 * @throws NoSuchOrderItemException if a matching commerce order item could not be found
	 */
	public CommerceOrderItem findByCPInstanceId_First(
			long CPInstanceId,
			com.liferay.portal.kernel.util.OrderByComparator<CommerceOrderItem>
				orderByComparator)
		throws NoSuchOrderItemException;

	/**
	 * Returns the first commerce order item in the ordered set where CPInstanceId = &#63;.
	 *
	 * @param CPInstanceId the cp instance ID
	 * @param orderByComparator the comparator to order the set by (optionally <code>null</code>)
	 * @return the first matching commerce order item, or <code>null</code> if a matching commerce order item could not be found
	 */
	public CommerceOrderItem fetchByCPInstanceId_First(
		long CPInstanceId,
		com.liferay.portal.kernel.util.OrderByComparator<CommerceOrderItem>
			orderByComparator);

	/**
	 * Returns the last commerce order item in the ordered set where CPInstanceId = &#63;.
	 *
	 * @param CPInstanceId the cp instance ID
	 * @param orderByComparator the comparator to order the set by (optionally <code>null</code>)
	 * @return the last matching commerce order item
	 * @throws NoSuchOrderItemException if a matching commerce order item could not be found
	 */
	public CommerceOrderItem findByCPInstanceId_Last(
			long CPInstanceId,
			com.liferay.portal.kernel.util.OrderByComparator<CommerceOrderItem>
				orderByComparator)
		throws NoSuchOrderItemException;

	/**
	 * Returns the last commerce order item in the ordered set where CPInstanceId = &#63;.
	 *
	 * @param CPInstanceId the cp instance ID
	 * @param orderByComparator the comparator to order the set by (optionally <code>null</code>)
	 * @return the last matching commerce order item, or <code>null</code> if a matching commerce order item could not be found
	 */
	public CommerceOrderItem fetchByCPInstanceId_Last(
		long CPInstanceId,
		com.liferay.portal.kernel.util.OrderByComparator<CommerceOrderItem>
			orderByComparator);

	/**
	 * Returns the commerce order items before and after the current commerce order item in the ordered set where CPInstanceId = &#63;.
	 *
	 * @param commerceOrderItemId the primary key of the current commerce order item
	 * @param CPInstanceId the cp instance ID
	 * @param orderByComparator the comparator to order the set by (optionally <code>null</code>)
	 * @return the previous, current, and next commerce order item
	 * @throws NoSuchOrderItemException if a commerce order item with the primary key could not be found
	 */
	public CommerceOrderItem[] findByCPInstanceId_PrevAndNext(
			long commerceOrderItemId, long CPInstanceId,
			com.liferay.portal.kernel.util.OrderByComparator<CommerceOrderItem>
				orderByComparator)
		throws NoSuchOrderItemException;

	/**
	 * Removes all the commerce order items where CPInstanceId = &#63; from the database.
	 *
	 * @param CPInstanceId the cp instance ID
	 */
	public void removeByCPInstanceId(long CPInstanceId);

	/**
	 * Returns the number of commerce order items where CPInstanceId = &#63;.
	 *
	 * @param CPInstanceId the cp instance ID
	 * @return the number of matching commerce order items
	 */
	public int countByCPInstanceId(long CPInstanceId);

	/**
	 * Returns all the commerce order items where CProductId = &#63;.
	 *
	 * @param CProductId the c product ID
	 * @return the matching commerce order items
	 */
	public java.util.List<CommerceOrderItem> findByCProductId(long CProductId);

	/**
	 * Returns a range of all the commerce order items where CProductId = &#63;.
	 *
	 * <p>
	 * Useful when paginating results. Returns a maximum of <code>end - start</code> instances. <code>start</code> and <code>end</code> are not primary keys, they are indexes in the result set. Thus, <code>0</code> refers to the first result in the set. Setting both <code>start</code> and <code>end</code> to <code>QueryUtil#ALL_POS</code> will return the full result set. If <code>orderByComparator</code> is specified, then the query will include the given ORDER BY logic. If <code>orderByComparator</code> is absent, then the query will include the default ORDER BY logic from <code>CommerceOrderItemModelImpl</code>.
	 * </p>
	 *
	 * @param CProductId the c product ID
	 * @param start the lower bound of the range of commerce order items
	 * @param end the upper bound of the range of commerce order items (not inclusive)
	 * @return the range of matching commerce order items
	 */
	public java.util.List<CommerceOrderItem> findByCProductId(
		long CProductId, int start, int end);

	/**
	 * Returns an ordered range of all the commerce order items where CProductId = &#63;.
	 *
	 * <p>
	 * Useful when paginating results. Returns a maximum of <code>end - start</code> instances. <code>start</code> and <code>end</code> are not primary keys, they are indexes in the result set. Thus, <code>0</code> refers to the first result in the set. Setting both <code>start</code> and <code>end</code> to <code>QueryUtil#ALL_POS</code> will return the full result set. If <code>orderByComparator</code> is specified, then the query will include the given ORDER BY logic. If <code>orderByComparator</code> is absent, then the query will include the default ORDER BY logic from <code>CommerceOrderItemModelImpl</code>.
	 * </p>
	 *
	 * @param CProductId the c product ID
	 * @param start the lower bound of the range of commerce order items
	 * @param end the upper bound of the range of commerce order items (not inclusive)
	 * @param orderByComparator the comparator to order the results by (optionally <code>null</code>)
	 * @return the ordered range of matching commerce order items
	 */
	public java.util.List<CommerceOrderItem> findByCProductId(
		long CProductId, int start, int end,
		com.liferay.portal.kernel.util.OrderByComparator<CommerceOrderItem>
			orderByComparator);

	/**
	 * Returns an ordered range of all the commerce order items where CProductId = &#63;.
	 *
	 * <p>
	 * Useful when paginating results. Returns a maximum of <code>end - start</code> instances. <code>start</code> and <code>end</code> are not primary keys, they are indexes in the result set. Thus, <code>0</code> refers to the first result in the set. Setting both <code>start</code> and <code>end</code> to <code>QueryUtil#ALL_POS</code> will return the full result set. If <code>orderByComparator</code> is specified, then the query will include the given ORDER BY logic. If <code>orderByComparator</code> is absent, then the query will include the default ORDER BY logic from <code>CommerceOrderItemModelImpl</code>.
	 * </p>
	 *
	 * @param CProductId the c product ID
	 * @param start the lower bound of the range of commerce order items
	 * @param end the upper bound of the range of commerce order items (not inclusive)
	 * @param orderByComparator the comparator to order the results by (optionally <code>null</code>)
	 * @param useFinderCache whether to use the finder cache
	 * @return the ordered range of matching commerce order items
	 */
	public java.util.List<CommerceOrderItem> findByCProductId(
		long CProductId, int start, int end,
		com.liferay.portal.kernel.util.OrderByComparator<CommerceOrderItem>
			orderByComparator,
		boolean useFinderCache);

	/**
	 * Returns the first commerce order item in the ordered set where CProductId = &#63;.
	 *
	 * @param CProductId the c product ID
	 * @param orderByComparator the comparator to order the set by (optionally <code>null</code>)
	 * @return the first matching commerce order item
	 * @throws NoSuchOrderItemException if a matching commerce order item could not be found
	 */
	public CommerceOrderItem findByCProductId_First(
			long CProductId,
			com.liferay.portal.kernel.util.OrderByComparator<CommerceOrderItem>
				orderByComparator)
		throws NoSuchOrderItemException;

	/**
	 * Returns the first commerce order item in the ordered set where CProductId = &#63;.
	 *
	 * @param CProductId the c product ID
	 * @param orderByComparator the comparator to order the set by (optionally <code>null</code>)
	 * @return the first matching commerce order item, or <code>null</code> if a matching commerce order item could not be found
	 */
	public CommerceOrderItem fetchByCProductId_First(
		long CProductId,
		com.liferay.portal.kernel.util.OrderByComparator<CommerceOrderItem>
			orderByComparator);

	/**
	 * Returns the last commerce order item in the ordered set where CProductId = &#63;.
	 *
	 * @param CProductId the c product ID
	 * @param orderByComparator the comparator to order the set by (optionally <code>null</code>)
	 * @return the last matching commerce order item
	 * @throws NoSuchOrderItemException if a matching commerce order item could not be found
	 */
	public CommerceOrderItem findByCProductId_Last(
			long CProductId,
			com.liferay.portal.kernel.util.OrderByComparator<CommerceOrderItem>
				orderByComparator)
		throws NoSuchOrderItemException;

	/**
	 * Returns the last commerce order item in the ordered set where CProductId = &#63;.
	 *
	 * @param CProductId the c product ID
	 * @param orderByComparator the comparator to order the set by (optionally <code>null</code>)
	 * @return the last matching commerce order item, or <code>null</code> if a matching commerce order item could not be found
	 */
	public CommerceOrderItem fetchByCProductId_Last(
		long CProductId,
		com.liferay.portal.kernel.util.OrderByComparator<CommerceOrderItem>
			orderByComparator);

	/**
	 * Returns the commerce order items before and after the current commerce order item in the ordered set where CProductId = &#63;.
	 *
	 * @param commerceOrderItemId the primary key of the current commerce order item
	 * @param CProductId the c product ID
	 * @param orderByComparator the comparator to order the set by (optionally <code>null</code>)
	 * @return the previous, current, and next commerce order item
	 * @throws NoSuchOrderItemException if a commerce order item with the primary key could not be found
	 */
	public CommerceOrderItem[] findByCProductId_PrevAndNext(
			long commerceOrderItemId, long CProductId,
			com.liferay.portal.kernel.util.OrderByComparator<CommerceOrderItem>
				orderByComparator)
		throws NoSuchOrderItemException;

	/**
	 * Removes all the commerce order items where CProductId = &#63; from the database.
	 *
	 * @param CProductId the c product ID
	 */
	public void removeByCProductId(long CProductId);

	/**
	 * Returns the number of commerce order items where CProductId = &#63;.
	 *
	 * @param CProductId the c product ID
	 * @return the number of matching commerce order items
	 */
	public int countByCProductId(long CProductId);

	/**
	 * Returns all the commerce order items where customerCommerceOrderItemId = &#63;.
	 *
	 * @param customerCommerceOrderItemId the customer commerce order item ID
	 * @return the matching commerce order items
	 */
	public java.util.List<CommerceOrderItem> findByCustomerCommerceOrderItemId(
		long customerCommerceOrderItemId);

	/**
	 * Returns a range of all the commerce order items where customerCommerceOrderItemId = &#63;.
	 *
	 * <p>
	 * Useful when paginating results. Returns a maximum of <code>end - start</code> instances. <code>start</code> and <code>end</code> are not primary keys, they are indexes in the result set. Thus, <code>0</code> refers to the first result in the set. Setting both <code>start</code> and <code>end</code> to <code>QueryUtil#ALL_POS</code> will return the full result set. If <code>orderByComparator</code> is specified, then the query will include the given ORDER BY logic. If <code>orderByComparator</code> is absent, then the query will include the default ORDER BY logic from <code>CommerceOrderItemModelImpl</code>.
	 * </p>
	 *
	 * @param customerCommerceOrderItemId the customer commerce order item ID
	 * @param start the lower bound of the range of commerce order items
	 * @param end the upper bound of the range of commerce order items (not inclusive)
	 * @return the range of matching commerce order items
	 */
	public java.util.List<CommerceOrderItem> findByCustomerCommerceOrderItemId(
		long customerCommerceOrderItemId, int start, int end);

	/**
	 * Returns an ordered range of all the commerce order items where customerCommerceOrderItemId = &#63;.
	 *
	 * <p>
	 * Useful when paginating results. Returns a maximum of <code>end - start</code> instances. <code>start</code> and <code>end</code> are not primary keys, they are indexes in the result set. Thus, <code>0</code> refers to the first result in the set. Setting both <code>start</code> and <code>end</code> to <code>QueryUtil#ALL_POS</code> will return the full result set. If <code>orderByComparator</code> is specified, then the query will include the given ORDER BY logic. If <code>orderByComparator</code> is absent, then the query will include the default ORDER BY logic from <code>CommerceOrderItemModelImpl</code>.
	 * </p>
	 *
	 * @param customerCommerceOrderItemId the customer commerce order item ID
	 * @param start the lower bound of the range of commerce order items
	 * @param end the upper bound of the range of commerce order items (not inclusive)
	 * @param orderByComparator the comparator to order the results by (optionally <code>null</code>)
	 * @return the ordered range of matching commerce order items
	 */
	public java.util.List<CommerceOrderItem> findByCustomerCommerceOrderItemId(
		long customerCommerceOrderItemId, int start, int end,
		com.liferay.portal.kernel.util.OrderByComparator<CommerceOrderItem>
			orderByComparator);

	/**
	 * Returns an ordered range of all the commerce order items where customerCommerceOrderItemId = &#63;.
	 *
	 * <p>
	 * Useful when paginating results. Returns a maximum of <code>end - start</code> instances. <code>start</code> and <code>end</code> are not primary keys, they are indexes in the result set. Thus, <code>0</code> refers to the first result in the set. Setting both <code>start</code> and <code>end</code> to <code>QueryUtil#ALL_POS</code> will return the full result set. If <code>orderByComparator</code> is specified, then the query will include the given ORDER BY logic. If <code>orderByComparator</code> is absent, then the query will include the default ORDER BY logic from <code>CommerceOrderItemModelImpl</code>.
	 * </p>
	 *
	 * @param customerCommerceOrderItemId the customer commerce order item ID
	 * @param start the lower bound of the range of commerce order items
	 * @param end the upper bound of the range of commerce order items (not inclusive)
	 * @param orderByComparator the comparator to order the results by (optionally <code>null</code>)
	 * @param useFinderCache whether to use the finder cache
	 * @return the ordered range of matching commerce order items
	 */
	public java.util.List<CommerceOrderItem> findByCustomerCommerceOrderItemId(
		long customerCommerceOrderItemId, int start, int end,
		com.liferay.portal.kernel.util.OrderByComparator<CommerceOrderItem>
			orderByComparator,
		boolean useFinderCache);

	/**
	 * Returns the first commerce order item in the ordered set where customerCommerceOrderItemId = &#63;.
	 *
	 * @param customerCommerceOrderItemId the customer commerce order item ID
	 * @param orderByComparator the comparator to order the set by (optionally <code>null</code>)
	 * @return the first matching commerce order item
	 * @throws NoSuchOrderItemException if a matching commerce order item could not be found
	 */
	public CommerceOrderItem findByCustomerCommerceOrderItemId_First(
			long customerCommerceOrderItemId,
			com.liferay.portal.kernel.util.OrderByComparator<CommerceOrderItem>
				orderByComparator)
		throws NoSuchOrderItemException;

	/**
	 * Returns the first commerce order item in the ordered set where customerCommerceOrderItemId = &#63;.
	 *
	 * @param customerCommerceOrderItemId the customer commerce order item ID
	 * @param orderByComparator the comparator to order the set by (optionally <code>null</code>)
	 * @return the first matching commerce order item, or <code>null</code> if a matching commerce order item could not be found
	 */
	public CommerceOrderItem fetchByCustomerCommerceOrderItemId_First(
		long customerCommerceOrderItemId,
		com.liferay.portal.kernel.util.OrderByComparator<CommerceOrderItem>
			orderByComparator);

	/**
	 * Returns the last commerce order item in the ordered set where customerCommerceOrderItemId = &#63;.
	 *
	 * @param customerCommerceOrderItemId the customer commerce order item ID
	 * @param orderByComparator the comparator to order the set by (optionally <code>null</code>)
	 * @return the last matching commerce order item
	 * @throws NoSuchOrderItemException if a matching commerce order item could not be found
	 */
	public CommerceOrderItem findByCustomerCommerceOrderItemId_Last(
			long customerCommerceOrderItemId,
			com.liferay.portal.kernel.util.OrderByComparator<CommerceOrderItem>
				orderByComparator)
		throws NoSuchOrderItemException;

	/**
	 * Returns the last commerce order item in the ordered set where customerCommerceOrderItemId = &#63;.
	 *
	 * @param customerCommerceOrderItemId the customer commerce order item ID
	 * @param orderByComparator the comparator to order the set by (optionally <code>null</code>)
	 * @return the last matching commerce order item, or <code>null</code> if a matching commerce order item could not be found
	 */
	public CommerceOrderItem fetchByCustomerCommerceOrderItemId_Last(
		long customerCommerceOrderItemId,
		com.liferay.portal.kernel.util.OrderByComparator<CommerceOrderItem>
			orderByComparator);

	/**
	 * Returns the commerce order items before and after the current commerce order item in the ordered set where customerCommerceOrderItemId = &#63;.
	 *
	 * @param commerceOrderItemId the primary key of the current commerce order item
	 * @param customerCommerceOrderItemId the customer commerce order item ID
	 * @param orderByComparator the comparator to order the set by (optionally <code>null</code>)
	 * @return the previous, current, and next commerce order item
	 * @throws NoSuchOrderItemException if a commerce order item with the primary key could not be found
	 */
	public CommerceOrderItem[] findByCustomerCommerceOrderItemId_PrevAndNext(
			long commerceOrderItemId, long customerCommerceOrderItemId,
			com.liferay.portal.kernel.util.OrderByComparator<CommerceOrderItem>
				orderByComparator)
		throws NoSuchOrderItemException;

	/**
	 * Removes all the commerce order items where customerCommerceOrderItemId = &#63; from the database.
	 *
	 * @param customerCommerceOrderItemId the customer commerce order item ID
	 */
	public void removeByCustomerCommerceOrderItemId(
		long customerCommerceOrderItemId);

	/**
	 * Returns the number of commerce order items where customerCommerceOrderItemId = &#63;.
	 *
	 * @param customerCommerceOrderItemId the customer commerce order item ID
	 * @return the number of matching commerce order items
	 */
	public int countByCustomerCommerceOrderItemId(
		long customerCommerceOrderItemId);

	/**
	 * Returns all the commerce order items where parentCommerceOrderItemId = &#63;.
	 *
	 * @param parentCommerceOrderItemId the parent commerce order item ID
	 * @return the matching commerce order items
	 */
	public java.util.List<CommerceOrderItem> findByParentCommerceOrderItemId(
		long parentCommerceOrderItemId);

	/**
	 * Returns a range of all the commerce order items where parentCommerceOrderItemId = &#63;.
	 *
	 * <p>
	 * Useful when paginating results. Returns a maximum of <code>end - start</code> instances. <code>start</code> and <code>end</code> are not primary keys, they are indexes in the result set. Thus, <code>0</code> refers to the first result in the set. Setting both <code>start</code> and <code>end</code> to <code>QueryUtil#ALL_POS</code> will return the full result set. If <code>orderByComparator</code> is specified, then the query will include the given ORDER BY logic. If <code>orderByComparator</code> is absent, then the query will include the default ORDER BY logic from <code>CommerceOrderItemModelImpl</code>.
	 * </p>
	 *
	 * @param parentCommerceOrderItemId the parent commerce order item ID
	 * @param start the lower bound of the range of commerce order items
	 * @param end the upper bound of the range of commerce order items (not inclusive)
	 * @return the range of matching commerce order items
	 */
	public java.util.List<CommerceOrderItem> findByParentCommerceOrderItemId(
		long parentCommerceOrderItemId, int start, int end);

	/**
	 * Returns an ordered range of all the commerce order items where parentCommerceOrderItemId = &#63;.
	 *
	 * <p>
	 * Useful when paginating results. Returns a maximum of <code>end - start</code> instances. <code>start</code> and <code>end</code> are not primary keys, they are indexes in the result set. Thus, <code>0</code> refers to the first result in the set. Setting both <code>start</code> and <code>end</code> to <code>QueryUtil#ALL_POS</code> will return the full result set. If <code>orderByComparator</code> is specified, then the query will include the given ORDER BY logic. If <code>orderByComparator</code> is absent, then the query will include the default ORDER BY logic from <code>CommerceOrderItemModelImpl</code>.
	 * </p>
	 *
	 * @param parentCommerceOrderItemId the parent commerce order item ID
	 * @param start the lower bound of the range of commerce order items
	 * @param end the upper bound of the range of commerce order items (not inclusive)
	 * @param orderByComparator the comparator to order the results by (optionally <code>null</code>)
	 * @return the ordered range of matching commerce order items
	 */
	public java.util.List<CommerceOrderItem> findByParentCommerceOrderItemId(
		long parentCommerceOrderItemId, int start, int end,
		com.liferay.portal.kernel.util.OrderByComparator<CommerceOrderItem>
			orderByComparator);

	/**
	 * Returns an ordered range of all the commerce order items where parentCommerceOrderItemId = &#63;.
	 *
	 * <p>
	 * Useful when paginating results. Returns a maximum of <code>end - start</code> instances. <code>start</code> and <code>end</code> are not primary keys, they are indexes in the result set. Thus, <code>0</code> refers to the first result in the set. Setting both <code>start</code> and <code>end</code> to <code>QueryUtil#ALL_POS</code> will return the full result set. If <code>orderByComparator</code> is specified, then the query will include the given ORDER BY logic. If <code>orderByComparator</code> is absent, then the query will include the default ORDER BY logic from <code>CommerceOrderItemModelImpl</code>.
	 * </p>
	 *
	 * @param parentCommerceOrderItemId the parent commerce order item ID
	 * @param start the lower bound of the range of commerce order items
	 * @param end the upper bound of the range of commerce order items (not inclusive)
	 * @param orderByComparator the comparator to order the results by (optionally <code>null</code>)
	 * @param useFinderCache whether to use the finder cache
	 * @return the ordered range of matching commerce order items
	 */
	public java.util.List<CommerceOrderItem> findByParentCommerceOrderItemId(
		long parentCommerceOrderItemId, int start, int end,
		com.liferay.portal.kernel.util.OrderByComparator<CommerceOrderItem>
			orderByComparator,
		boolean useFinderCache);

	/**
	 * Returns the first commerce order item in the ordered set where parentCommerceOrderItemId = &#63;.
	 *
	 * @param parentCommerceOrderItemId the parent commerce order item ID
	 * @param orderByComparator the comparator to order the set by (optionally <code>null</code>)
	 * @return the first matching commerce order item
	 * @throws NoSuchOrderItemException if a matching commerce order item could not be found
	 */
	public CommerceOrderItem findByParentCommerceOrderItemId_First(
			long parentCommerceOrderItemId,
			com.liferay.portal.kernel.util.OrderByComparator<CommerceOrderItem>
				orderByComparator)
		throws NoSuchOrderItemException;

	/**
	 * Returns the first commerce order item in the ordered set where parentCommerceOrderItemId = &#63;.
	 *
	 * @param parentCommerceOrderItemId the parent commerce order item ID
	 * @param orderByComparator the comparator to order the set by (optionally <code>null</code>)
	 * @return the first matching commerce order item, or <code>null</code> if a matching commerce order item could not be found
	 */
	public CommerceOrderItem fetchByParentCommerceOrderItemId_First(
		long parentCommerceOrderItemId,
		com.liferay.portal.kernel.util.OrderByComparator<CommerceOrderItem>
			orderByComparator);

	/**
	 * Returns the last commerce order item in the ordered set where parentCommerceOrderItemId = &#63;.
	 *
	 * @param parentCommerceOrderItemId the parent commerce order item ID
	 * @param orderByComparator the comparator to order the set by (optionally <code>null</code>)
	 * @return the last matching commerce order item
	 * @throws NoSuchOrderItemException if a matching commerce order item could not be found
	 */
	public CommerceOrderItem findByParentCommerceOrderItemId_Last(
			long parentCommerceOrderItemId,
			com.liferay.portal.kernel.util.OrderByComparator<CommerceOrderItem>
				orderByComparator)
		throws NoSuchOrderItemException;

	/**
	 * Returns the last commerce order item in the ordered set where parentCommerceOrderItemId = &#63;.
	 *
	 * @param parentCommerceOrderItemId the parent commerce order item ID
	 * @param orderByComparator the comparator to order the set by (optionally <code>null</code>)
	 * @return the last matching commerce order item, or <code>null</code> if a matching commerce order item could not be found
	 */
	public CommerceOrderItem fetchByParentCommerceOrderItemId_Last(
		long parentCommerceOrderItemId,
		com.liferay.portal.kernel.util.OrderByComparator<CommerceOrderItem>
			orderByComparator);

	/**
	 * Returns the commerce order items before and after the current commerce order item in the ordered set where parentCommerceOrderItemId = &#63;.
	 *
	 * @param commerceOrderItemId the primary key of the current commerce order item
	 * @param parentCommerceOrderItemId the parent commerce order item ID
	 * @param orderByComparator the comparator to order the set by (optionally <code>null</code>)
	 * @return the previous, current, and next commerce order item
	 * @throws NoSuchOrderItemException if a commerce order item with the primary key could not be found
	 */
	public CommerceOrderItem[] findByParentCommerceOrderItemId_PrevAndNext(
			long commerceOrderItemId, long parentCommerceOrderItemId,
			com.liferay.portal.kernel.util.OrderByComparator<CommerceOrderItem>
				orderByComparator)
		throws NoSuchOrderItemException;

	/**
	 * Removes all the commerce order items where parentCommerceOrderItemId = &#63; from the database.
	 *
	 * @param parentCommerceOrderItemId the parent commerce order item ID
	 */
	public void removeByParentCommerceOrderItemId(
		long parentCommerceOrderItemId);

	/**
	 * Returns the number of commerce order items where parentCommerceOrderItemId = &#63;.
	 *
	 * @param parentCommerceOrderItemId the parent commerce order item ID
	 * @return the number of matching commerce order items
	 */
	public int countByParentCommerceOrderItemId(long parentCommerceOrderItemId);

	/**
	 * Returns all the commerce order items where commerceOrderId = &#63; and CPInstanceId = &#63;.
	 *
	 * @param commerceOrderId the commerce order ID
	 * @param CPInstanceId the cp instance ID
	 * @return the matching commerce order items
	 */
	public java.util.List<CommerceOrderItem> findByC_CPI(
		long commerceOrderId, long CPInstanceId);

	/**
	 * Returns a range of all the commerce order items where commerceOrderId = &#63; and CPInstanceId = &#63;.
	 *
	 * <p>
	 * Useful when paginating results. Returns a maximum of <code>end - start</code> instances. <code>start</code> and <code>end</code> are not primary keys, they are indexes in the result set. Thus, <code>0</code> refers to the first result in the set. Setting both <code>start</code> and <code>end</code> to <code>QueryUtil#ALL_POS</code> will return the full result set. If <code>orderByComparator</code> is specified, then the query will include the given ORDER BY logic. If <code>orderByComparator</code> is absent, then the query will include the default ORDER BY logic from <code>CommerceOrderItemModelImpl</code>.
	 * </p>
	 *
	 * @param commerceOrderId the commerce order ID
	 * @param CPInstanceId the cp instance ID
	 * @param start the lower bound of the range of commerce order items
	 * @param end the upper bound of the range of commerce order items (not inclusive)
	 * @return the range of matching commerce order items
	 */
	public java.util.List<CommerceOrderItem> findByC_CPI(
		long commerceOrderId, long CPInstanceId, int start, int end);

	/**
	 * Returns an ordered range of all the commerce order items where commerceOrderId = &#63; and CPInstanceId = &#63;.
	 *
	 * <p>
	 * Useful when paginating results. Returns a maximum of <code>end - start</code> instances. <code>start</code> and <code>end</code> are not primary keys, they are indexes in the result set. Thus, <code>0</code> refers to the first result in the set. Setting both <code>start</code> and <code>end</code> to <code>QueryUtil#ALL_POS</code> will return the full result set. If <code>orderByComparator</code> is specified, then the query will include the given ORDER BY logic. If <code>orderByComparator</code> is absent, then the query will include the default ORDER BY logic from <code>CommerceOrderItemModelImpl</code>.
	 * </p>
	 *
	 * @param commerceOrderId the commerce order ID
	 * @param CPInstanceId the cp instance ID
	 * @param start the lower bound of the range of commerce order items
	 * @param end the upper bound of the range of commerce order items (not inclusive)
	 * @param orderByComparator the comparator to order the results by (optionally <code>null</code>)
	 * @return the ordered range of matching commerce order items
	 */
	public java.util.List<CommerceOrderItem> findByC_CPI(
		long commerceOrderId, long CPInstanceId, int start, int end,
		com.liferay.portal.kernel.util.OrderByComparator<CommerceOrderItem>
			orderByComparator);

	/**
	 * Returns an ordered range of all the commerce order items where commerceOrderId = &#63; and CPInstanceId = &#63;.
	 *
	 * <p>
	 * Useful when paginating results. Returns a maximum of <code>end - start</code> instances. <code>start</code> and <code>end</code> are not primary keys, they are indexes in the result set. Thus, <code>0</code> refers to the first result in the set. Setting both <code>start</code> and <code>end</code> to <code>QueryUtil#ALL_POS</code> will return the full result set. If <code>orderByComparator</code> is specified, then the query will include the given ORDER BY logic. If <code>orderByComparator</code> is absent, then the query will include the default ORDER BY logic from <code>CommerceOrderItemModelImpl</code>.
	 * </p>
	 *
	 * @param commerceOrderId the commerce order ID
	 * @param CPInstanceId the cp instance ID
	 * @param start the lower bound of the range of commerce order items
	 * @param end the upper bound of the range of commerce order items (not inclusive)
	 * @param orderByComparator the comparator to order the results by (optionally <code>null</code>)
	 * @param useFinderCache whether to use the finder cache
	 * @return the ordered range of matching commerce order items
	 */
	public java.util.List<CommerceOrderItem> findByC_CPI(
		long commerceOrderId, long CPInstanceId, int start, int end,
		com.liferay.portal.kernel.util.OrderByComparator<CommerceOrderItem>
			orderByComparator,
		boolean useFinderCache);

	/**
	 * Returns the first commerce order item in the ordered set where commerceOrderId = &#63; and CPInstanceId = &#63;.
	 *
	 * @param commerceOrderId the commerce order ID
	 * @param CPInstanceId the cp instance ID
	 * @param orderByComparator the comparator to order the set by (optionally <code>null</code>)
	 * @return the first matching commerce order item
	 * @throws NoSuchOrderItemException if a matching commerce order item could not be found
	 */
	public CommerceOrderItem findByC_CPI_First(
			long commerceOrderId, long CPInstanceId,
			com.liferay.portal.kernel.util.OrderByComparator<CommerceOrderItem>
				orderByComparator)
		throws NoSuchOrderItemException;

	/**
	 * Returns the first commerce order item in the ordered set where commerceOrderId = &#63; and CPInstanceId = &#63;.
	 *
	 * @param commerceOrderId the commerce order ID
	 * @param CPInstanceId the cp instance ID
	 * @param orderByComparator the comparator to order the set by (optionally <code>null</code>)
	 * @return the first matching commerce order item, or <code>null</code> if a matching commerce order item could not be found
	 */
	public CommerceOrderItem fetchByC_CPI_First(
		long commerceOrderId, long CPInstanceId,
		com.liferay.portal.kernel.util.OrderByComparator<CommerceOrderItem>
			orderByComparator);

	/**
	 * Returns the last commerce order item in the ordered set where commerceOrderId = &#63; and CPInstanceId = &#63;.
	 *
	 * @param commerceOrderId the commerce order ID
	 * @param CPInstanceId the cp instance ID
	 * @param orderByComparator the comparator to order the set by (optionally <code>null</code>)
	 * @return the last matching commerce order item
	 * @throws NoSuchOrderItemException if a matching commerce order item could not be found
	 */
	public CommerceOrderItem findByC_CPI_Last(
			long commerceOrderId, long CPInstanceId,
			com.liferay.portal.kernel.util.OrderByComparator<CommerceOrderItem>
				orderByComparator)
		throws NoSuchOrderItemException;

	/**
	 * Returns the last commerce order item in the ordered set where commerceOrderId = &#63; and CPInstanceId = &#63;.
	 *
	 * @param commerceOrderId the commerce order ID
	 * @param CPInstanceId the cp instance ID
	 * @param orderByComparator the comparator to order the set by (optionally <code>null</code>)
	 * @return the last matching commerce order item, or <code>null</code> if a matching commerce order item could not be found
	 */
	public CommerceOrderItem fetchByC_CPI_Last(
		long commerceOrderId, long CPInstanceId,
		com.liferay.portal.kernel.util.OrderByComparator<CommerceOrderItem>
			orderByComparator);

	/**
	 * Returns the commerce order items before and after the current commerce order item in the ordered set where commerceOrderId = &#63; and CPInstanceId = &#63;.
	 *
	 * @param commerceOrderItemId the primary key of the current commerce order item
	 * @param commerceOrderId the commerce order ID
	 * @param CPInstanceId the cp instance ID
	 * @param orderByComparator the comparator to order the set by (optionally <code>null</code>)
	 * @return the previous, current, and next commerce order item
	 * @throws NoSuchOrderItemException if a commerce order item with the primary key could not be found
	 */
	public CommerceOrderItem[] findByC_CPI_PrevAndNext(
			long commerceOrderItemId, long commerceOrderId, long CPInstanceId,
			com.liferay.portal.kernel.util.OrderByComparator<CommerceOrderItem>
				orderByComparator)
		throws NoSuchOrderItemException;

	/**
	 * Removes all the commerce order items where commerceOrderId = &#63; and CPInstanceId = &#63; from the database.
	 *
	 * @param commerceOrderId the commerce order ID
	 * @param CPInstanceId the cp instance ID
	 */
	public void removeByC_CPI(long commerceOrderId, long CPInstanceId);

	/**
	 * Returns the number of commerce order items where commerceOrderId = &#63; and CPInstanceId = &#63;.
	 *
	 * @param commerceOrderId the commerce order ID
	 * @param CPInstanceId the cp instance ID
	 * @return the number of matching commerce order items
	 */
	public int countByC_CPI(long commerceOrderId, long CPInstanceId);

	/**
	 * Returns all the commerce order items where commerceOrderId = &#63; and parentCommerceOrderItemId = &#63;.
	 *
	 * @param commerceOrderId the commerce order ID
	 * @param parentCommerceOrderItemId the parent commerce order item ID
	 * @return the matching commerce order items
	 */
	public java.util.List<CommerceOrderItem> findByC_PCOI(
		long commerceOrderId, long parentCommerceOrderItemId);

	/**
	 * Returns a range of all the commerce order items where commerceOrderId = &#63; and parentCommerceOrderItemId = &#63;.
	 *
	 * <p>
	 * Useful when paginating results. Returns a maximum of <code>end - start</code> instances. <code>start</code> and <code>end</code> are not primary keys, they are indexes in the result set. Thus, <code>0</code> refers to the first result in the set. Setting both <code>start</code> and <code>end</code> to <code>QueryUtil#ALL_POS</code> will return the full result set. If <code>orderByComparator</code> is specified, then the query will include the given ORDER BY logic. If <code>orderByComparator</code> is absent, then the query will include the default ORDER BY logic from <code>CommerceOrderItemModelImpl</code>.
	 * </p>
	 *
	 * @param commerceOrderId the commerce order ID
	 * @param parentCommerceOrderItemId the parent commerce order item ID
	 * @param start the lower bound of the range of commerce order items
	 * @param end the upper bound of the range of commerce order items (not inclusive)
	 * @return the range of matching commerce order items
	 */
	public java.util.List<CommerceOrderItem> findByC_PCOI(
		long commerceOrderId, long parentCommerceOrderItemId, int start,
		int end);

	/**
	 * Returns an ordered range of all the commerce order items where commerceOrderId = &#63; and parentCommerceOrderItemId = &#63;.
	 *
	 * <p>
	 * Useful when paginating results. Returns a maximum of <code>end - start</code> instances. <code>start</code> and <code>end</code> are not primary keys, they are indexes in the result set. Thus, <code>0</code> refers to the first result in the set. Setting both <code>start</code> and <code>end</code> to <code>QueryUtil#ALL_POS</code> will return the full result set. If <code>orderByComparator</code> is specified, then the query will include the given ORDER BY logic. If <code>orderByComparator</code> is absent, then the query will include the default ORDER BY logic from <code>CommerceOrderItemModelImpl</code>.
	 * </p>
	 *
	 * @param commerceOrderId the commerce order ID
	 * @param parentCommerceOrderItemId the parent commerce order item ID
	 * @param start the lower bound of the range of commerce order items
	 * @param end the upper bound of the range of commerce order items (not inclusive)
	 * @param orderByComparator the comparator to order the results by (optionally <code>null</code>)
	 * @return the ordered range of matching commerce order items
	 */
	public java.util.List<CommerceOrderItem> findByC_PCOI(
		long commerceOrderId, long parentCommerceOrderItemId, int start,
		int end,
		com.liferay.portal.kernel.util.OrderByComparator<CommerceOrderItem>
			orderByComparator);

	/**
	 * Returns an ordered range of all the commerce order items where commerceOrderId = &#63; and parentCommerceOrderItemId = &#63;.
	 *
	 * <p>
	 * Useful when paginating results. Returns a maximum of <code>end - start</code> instances. <code>start</code> and <code>end</code> are not primary keys, they are indexes in the result set. Thus, <code>0</code> refers to the first result in the set. Setting both <code>start</code> and <code>end</code> to <code>QueryUtil#ALL_POS</code> will return the full result set. If <code>orderByComparator</code> is specified, then the query will include the given ORDER BY logic. If <code>orderByComparator</code> is absent, then the query will include the default ORDER BY logic from <code>CommerceOrderItemModelImpl</code>.
	 * </p>
	 *
	 * @param commerceOrderId the commerce order ID
	 * @param parentCommerceOrderItemId the parent commerce order item ID
	 * @param start the lower bound of the range of commerce order items
	 * @param end the upper bound of the range of commerce order items (not inclusive)
	 * @param orderByComparator the comparator to order the results by (optionally <code>null</code>)
	 * @param useFinderCache whether to use the finder cache
	 * @return the ordered range of matching commerce order items
	 */
	public java.util.List<CommerceOrderItem> findByC_PCOI(
		long commerceOrderId, long parentCommerceOrderItemId, int start,
		int end,
		com.liferay.portal.kernel.util.OrderByComparator<CommerceOrderItem>
			orderByComparator,
		boolean useFinderCache);

	/**
	 * Returns the first commerce order item in the ordered set where commerceOrderId = &#63; and parentCommerceOrderItemId = &#63;.
	 *
	 * @param commerceOrderId the commerce order ID
	 * @param parentCommerceOrderItemId the parent commerce order item ID
	 * @param orderByComparator the comparator to order the set by (optionally <code>null</code>)
	 * @return the first matching commerce order item
	 * @throws NoSuchOrderItemException if a matching commerce order item could not be found
	 */
	public CommerceOrderItem findByC_PCOI_First(
			long commerceOrderId, long parentCommerceOrderItemId,
			com.liferay.portal.kernel.util.OrderByComparator<CommerceOrderItem>
				orderByComparator)
		throws NoSuchOrderItemException;

	/**
	 * Returns the first commerce order item in the ordered set where commerceOrderId = &#63; and parentCommerceOrderItemId = &#63;.
	 *
	 * @param commerceOrderId the commerce order ID
	 * @param parentCommerceOrderItemId the parent commerce order item ID
	 * @param orderByComparator the comparator to order the set by (optionally <code>null</code>)
	 * @return the first matching commerce order item, or <code>null</code> if a matching commerce order item could not be found
	 */
	public CommerceOrderItem fetchByC_PCOI_First(
		long commerceOrderId, long parentCommerceOrderItemId,
		com.liferay.portal.kernel.util.OrderByComparator<CommerceOrderItem>
			orderByComparator);

	/**
	 * Returns the last commerce order item in the ordered set where commerceOrderId = &#63; and parentCommerceOrderItemId = &#63;.
	 *
	 * @param commerceOrderId the commerce order ID
	 * @param parentCommerceOrderItemId the parent commerce order item ID
	 * @param orderByComparator the comparator to order the set by (optionally <code>null</code>)
	 * @return the last matching commerce order item
	 * @throws NoSuchOrderItemException if a matching commerce order item could not be found
	 */
	public CommerceOrderItem findByC_PCOI_Last(
			long commerceOrderId, long parentCommerceOrderItemId,
			com.liferay.portal.kernel.util.OrderByComparator<CommerceOrderItem>
				orderByComparator)
		throws NoSuchOrderItemException;

	/**
	 * Returns the last commerce order item in the ordered set where commerceOrderId = &#63; and parentCommerceOrderItemId = &#63;.
	 *
	 * @param commerceOrderId the commerce order ID
	 * @param parentCommerceOrderItemId the parent commerce order item ID
	 * @param orderByComparator the comparator to order the set by (optionally <code>null</code>)
	 * @return the last matching commerce order item, or <code>null</code> if a matching commerce order item could not be found
	 */
	public CommerceOrderItem fetchByC_PCOI_Last(
		long commerceOrderId, long parentCommerceOrderItemId,
		com.liferay.portal.kernel.util.OrderByComparator<CommerceOrderItem>
			orderByComparator);

	/**
	 * Returns the commerce order items before and after the current commerce order item in the ordered set where commerceOrderId = &#63; and parentCommerceOrderItemId = &#63;.
	 *
	 * @param commerceOrderItemId the primary key of the current commerce order item
	 * @param commerceOrderId the commerce order ID
	 * @param parentCommerceOrderItemId the parent commerce order item ID
	 * @param orderByComparator the comparator to order the set by (optionally <code>null</code>)
	 * @return the previous, current, and next commerce order item
	 * @throws NoSuchOrderItemException if a commerce order item with the primary key could not be found
	 */
	public CommerceOrderItem[] findByC_PCOI_PrevAndNext(
			long commerceOrderItemId, long commerceOrderId,
			long parentCommerceOrderItemId,
			com.liferay.portal.kernel.util.OrderByComparator<CommerceOrderItem>
				orderByComparator)
		throws NoSuchOrderItemException;

	/**
	 * Removes all the commerce order items where commerceOrderId = &#63; and parentCommerceOrderItemId = &#63; from the database.
	 *
	 * @param commerceOrderId the commerce order ID
	 * @param parentCommerceOrderItemId the parent commerce order item ID
	 */
	public void removeByC_PCOI(
		long commerceOrderId, long parentCommerceOrderItemId);

	/**
	 * Returns the number of commerce order items where commerceOrderId = &#63; and parentCommerceOrderItemId = &#63;.
	 *
	 * @param commerceOrderId the commerce order ID
	 * @param parentCommerceOrderItemId the parent commerce order item ID
	 * @return the number of matching commerce order items
	 */
	public int countByC_PCOI(
		long commerceOrderId, long parentCommerceOrderItemId);

	/**
	 * Returns all the commerce order items where commerceOrderId = &#63; and subscription = &#63;.
	 *
	 * @param commerceOrderId the commerce order ID
	 * @param subscription the subscription
	 * @return the matching commerce order items
	 */
	public java.util.List<CommerceOrderItem> findByC_S(
		long commerceOrderId, boolean subscription);

	/**
	 * Returns a range of all the commerce order items where commerceOrderId = &#63; and subscription = &#63;.
	 *
	 * <p>
	 * Useful when paginating results. Returns a maximum of <code>end - start</code> instances. <code>start</code> and <code>end</code> are not primary keys, they are indexes in the result set. Thus, <code>0</code> refers to the first result in the set. Setting both <code>start</code> and <code>end</code> to <code>QueryUtil#ALL_POS</code> will return the full result set. If <code>orderByComparator</code> is specified, then the query will include the given ORDER BY logic. If <code>orderByComparator</code> is absent, then the query will include the default ORDER BY logic from <code>CommerceOrderItemModelImpl</code>.
	 * </p>
	 *
	 * @param commerceOrderId the commerce order ID
	 * @param subscription the subscription
	 * @param start the lower bound of the range of commerce order items
	 * @param end the upper bound of the range of commerce order items (not inclusive)
	 * @return the range of matching commerce order items
	 */
	public java.util.List<CommerceOrderItem> findByC_S(
		long commerceOrderId, boolean subscription, int start, int end);

	/**
	 * Returns an ordered range of all the commerce order items where commerceOrderId = &#63; and subscription = &#63;.
	 *
	 * <p>
	 * Useful when paginating results. Returns a maximum of <code>end - start</code> instances. <code>start</code> and <code>end</code> are not primary keys, they are indexes in the result set. Thus, <code>0</code> refers to the first result in the set. Setting both <code>start</code> and <code>end</code> to <code>QueryUtil#ALL_POS</code> will return the full result set. If <code>orderByComparator</code> is specified, then the query will include the given ORDER BY logic. If <code>orderByComparator</code> is absent, then the query will include the default ORDER BY logic from <code>CommerceOrderItemModelImpl</code>.
	 * </p>
	 *
	 * @param commerceOrderId the commerce order ID
	 * @param subscription the subscription
	 * @param start the lower bound of the range of commerce order items
	 * @param end the upper bound of the range of commerce order items (not inclusive)
	 * @param orderByComparator the comparator to order the results by (optionally <code>null</code>)
	 * @return the ordered range of matching commerce order items
	 */
	public java.util.List<CommerceOrderItem> findByC_S(
		long commerceOrderId, boolean subscription, int start, int end,
		com.liferay.portal.kernel.util.OrderByComparator<CommerceOrderItem>
			orderByComparator);

	/**
	 * Returns an ordered range of all the commerce order items where commerceOrderId = &#63; and subscription = &#63;.
	 *
	 * <p>
	 * Useful when paginating results. Returns a maximum of <code>end - start</code> instances. <code>start</code> and <code>end</code> are not primary keys, they are indexes in the result set. Thus, <code>0</code> refers to the first result in the set. Setting both <code>start</code> and <code>end</code> to <code>QueryUtil#ALL_POS</code> will return the full result set. If <code>orderByComparator</code> is specified, then the query will include the given ORDER BY logic. If <code>orderByComparator</code> is absent, then the query will include the default ORDER BY logic from <code>CommerceOrderItemModelImpl</code>.
	 * </p>
	 *
	 * @param commerceOrderId the commerce order ID
	 * @param subscription the subscription
	 * @param start the lower bound of the range of commerce order items
	 * @param end the upper bound of the range of commerce order items (not inclusive)
	 * @param orderByComparator the comparator to order the results by (optionally <code>null</code>)
	 * @param useFinderCache whether to use the finder cache
	 * @return the ordered range of matching commerce order items
	 */
	public java.util.List<CommerceOrderItem> findByC_S(
		long commerceOrderId, boolean subscription, int start, int end,
		com.liferay.portal.kernel.util.OrderByComparator<CommerceOrderItem>
			orderByComparator,
		boolean useFinderCache);

	/**
	 * Returns the first commerce order item in the ordered set where commerceOrderId = &#63; and subscription = &#63;.
	 *
	 * @param commerceOrderId the commerce order ID
	 * @param subscription the subscription
	 * @param orderByComparator the comparator to order the set by (optionally <code>null</code>)
	 * @return the first matching commerce order item
	 * @throws NoSuchOrderItemException if a matching commerce order item could not be found
	 */
	public CommerceOrderItem findByC_S_First(
			long commerceOrderId, boolean subscription,
			com.liferay.portal.kernel.util.OrderByComparator<CommerceOrderItem>
				orderByComparator)
		throws NoSuchOrderItemException;

	/**
	 * Returns the first commerce order item in the ordered set where commerceOrderId = &#63; and subscription = &#63;.
	 *
	 * @param commerceOrderId the commerce order ID
	 * @param subscription the subscription
	 * @param orderByComparator the comparator to order the set by (optionally <code>null</code>)
	 * @return the first matching commerce order item, or <code>null</code> if a matching commerce order item could not be found
	 */
	public CommerceOrderItem fetchByC_S_First(
		long commerceOrderId, boolean subscription,
		com.liferay.portal.kernel.util.OrderByComparator<CommerceOrderItem>
			orderByComparator);

	/**
	 * Returns the last commerce order item in the ordered set where commerceOrderId = &#63; and subscription = &#63;.
	 *
	 * @param commerceOrderId the commerce order ID
	 * @param subscription the subscription
	 * @param orderByComparator the comparator to order the set by (optionally <code>null</code>)
	 * @return the last matching commerce order item
	 * @throws NoSuchOrderItemException if a matching commerce order item could not be found
	 */
	public CommerceOrderItem findByC_S_Last(
			long commerceOrderId, boolean subscription,
			com.liferay.portal.kernel.util.OrderByComparator<CommerceOrderItem>
				orderByComparator)
		throws NoSuchOrderItemException;

	/**
	 * Returns the last commerce order item in the ordered set where commerceOrderId = &#63; and subscription = &#63;.
	 *
	 * @param commerceOrderId the commerce order ID
	 * @param subscription the subscription
	 * @param orderByComparator the comparator to order the set by (optionally <code>null</code>)
	 * @return the last matching commerce order item, or <code>null</code> if a matching commerce order item could not be found
	 */
	public CommerceOrderItem fetchByC_S_Last(
		long commerceOrderId, boolean subscription,
		com.liferay.portal.kernel.util.OrderByComparator<CommerceOrderItem>
			orderByComparator);

	/**
	 * Returns the commerce order items before and after the current commerce order item in the ordered set where commerceOrderId = &#63; and subscription = &#63;.
	 *
	 * @param commerceOrderItemId the primary key of the current commerce order item
	 * @param commerceOrderId the commerce order ID
	 * @param subscription the subscription
	 * @param orderByComparator the comparator to order the set by (optionally <code>null</code>)
	 * @return the previous, current, and next commerce order item
	 * @throws NoSuchOrderItemException if a commerce order item with the primary key could not be found
	 */
	public CommerceOrderItem[] findByC_S_PrevAndNext(
			long commerceOrderItemId, long commerceOrderId,
			boolean subscription,
			com.liferay.portal.kernel.util.OrderByComparator<CommerceOrderItem>
				orderByComparator)
		throws NoSuchOrderItemException;

	/**
	 * Removes all the commerce order items where commerceOrderId = &#63; and subscription = &#63; from the database.
	 *
	 * @param commerceOrderId the commerce order ID
	 * @param subscription the subscription
	 */
	public void removeByC_S(long commerceOrderId, boolean subscription);

	/**
	 * Returns the number of commerce order items where commerceOrderId = &#63; and subscription = &#63;.
	 *
	 * @param commerceOrderId the commerce order ID
	 * @param subscription the subscription
	 * @return the number of matching commerce order items
	 */
	public int countByC_S(long commerceOrderId, boolean subscription);

	/**
	 * Returns the commerce order item where externalReferenceCode = &#63; and companyId = &#63; or throws a <code>NoSuchOrderItemException</code> if it could not be found.
	 *
	 * @param externalReferenceCode the external reference code
	 * @param companyId the company ID
	 * @return the matching commerce order item
	 * @throws NoSuchOrderItemException if a matching commerce order item could not be found
	 */
	public CommerceOrderItem findByERC_C(
			String externalReferenceCode, long companyId)
		throws NoSuchOrderItemException;

	/**
	 * Returns the commerce order item where externalReferenceCode = &#63; and companyId = &#63; or returns <code>null</code> if it could not be found. Uses the finder cache.
	 *
	 * @param externalReferenceCode the external reference code
	 * @param companyId the company ID
	 * @return the matching commerce order item, or <code>null</code> if a matching commerce order item could not be found
	 */
	public CommerceOrderItem fetchByERC_C(
		String externalReferenceCode, long companyId);

	/**
	 * Returns the commerce order item where externalReferenceCode = &#63; and companyId = &#63; or returns <code>null</code> if it could not be found, optionally using the finder cache.
	 *
	 * @param externalReferenceCode the external reference code
	 * @param companyId the company ID
	 * @param useFinderCache whether to use the finder cache
	 * @return the matching commerce order item, or <code>null</code> if a matching commerce order item could not be found
	 */
	public CommerceOrderItem fetchByERC_C(
		String externalReferenceCode, long companyId, boolean useFinderCache);

	/**
	 * Removes the commerce order item where externalReferenceCode = &#63; and companyId = &#63; from the database.
	 *
	 * @param externalReferenceCode the external reference code
	 * @param companyId the company ID
	 * @return the commerce order item that was removed
	 */
	public CommerceOrderItem removeByERC_C(
			String externalReferenceCode, long companyId)
		throws NoSuchOrderItemException;

	/**
	 * Returns the number of commerce order items where externalReferenceCode = &#63; and companyId = &#63;.
	 *
	 * @param externalReferenceCode the external reference code
	 * @param companyId the company ID
	 * @return the number of matching commerce order items
	 */
	public int countByERC_C(String externalReferenceCode, long companyId);

	/**
	 * Caches the commerce order item in the entity cache if it is enabled.
	 *
	 * @param commerceOrderItem the commerce order item
	 */
	public void cacheResult(CommerceOrderItem commerceOrderItem);

	/**
	 * Caches the commerce order items in the entity cache if it is enabled.
	 *
	 * @param commerceOrderItems the commerce order items
	 */
	public void cacheResult(
		java.util.List<CommerceOrderItem> commerceOrderItems);

	/**
	 * Creates a new commerce order item with the primary key. Does not add the commerce order item to the database.
	 *
	 * @param commerceOrderItemId the primary key for the new commerce order item
	 * @return the new commerce order item
	 */
	public CommerceOrderItem create(long commerceOrderItemId);

	/**
	 * Removes the commerce order item with the primary key from the database. Also notifies the appropriate model listeners.
	 *
	 * @param commerceOrderItemId the primary key of the commerce order item
	 * @return the commerce order item that was removed
	 * @throws NoSuchOrderItemException if a commerce order item with the primary key could not be found
	 */
	public CommerceOrderItem remove(long commerceOrderItemId)
		throws NoSuchOrderItemException;

	public CommerceOrderItem updateImpl(CommerceOrderItem commerceOrderItem);

	/**
	 * Returns the commerce order item with the primary key or throws a <code>NoSuchOrderItemException</code> if it could not be found.
	 *
	 * @param commerceOrderItemId the primary key of the commerce order item
	 * @return the commerce order item
	 * @throws NoSuchOrderItemException if a commerce order item with the primary key could not be found
	 */
	public CommerceOrderItem findByPrimaryKey(long commerceOrderItemId)
		throws NoSuchOrderItemException;

	/**
	 * Returns the commerce order item with the primary key or returns <code>null</code> if it could not be found.
	 *
	 * @param commerceOrderItemId the primary key of the commerce order item
	 * @return the commerce order item, or <code>null</code> if a commerce order item with the primary key could not be found
	 */
	public CommerceOrderItem fetchByPrimaryKey(long commerceOrderItemId);

	/**
	 * Returns all the commerce order items.
	 *
	 * @return the commerce order items
	 */
	public java.util.List<CommerceOrderItem> findAll();

	/**
	 * Returns a range of all the commerce order items.
	 *
	 * <p>
	 * Useful when paginating results. Returns a maximum of <code>end - start</code> instances. <code>start</code> and <code>end</code> are not primary keys, they are indexes in the result set. Thus, <code>0</code> refers to the first result in the set. Setting both <code>start</code> and <code>end</code> to <code>QueryUtil#ALL_POS</code> will return the full result set. If <code>orderByComparator</code> is specified, then the query will include the given ORDER BY logic. If <code>orderByComparator</code> is absent, then the query will include the default ORDER BY logic from <code>CommerceOrderItemModelImpl</code>.
	 * </p>
	 *
	 * @param start the lower bound of the range of commerce order items
	 * @param end the upper bound of the range of commerce order items (not inclusive)
	 * @return the range of commerce order items
	 */
	public java.util.List<CommerceOrderItem> findAll(int start, int end);

	/**
	 * Returns an ordered range of all the commerce order items.
	 *
	 * <p>
	 * Useful when paginating results. Returns a maximum of <code>end - start</code> instances. <code>start</code> and <code>end</code> are not primary keys, they are indexes in the result set. Thus, <code>0</code> refers to the first result in the set. Setting both <code>start</code> and <code>end</code> to <code>QueryUtil#ALL_POS</code> will return the full result set. If <code>orderByComparator</code> is specified, then the query will include the given ORDER BY logic. If <code>orderByComparator</code> is absent, then the query will include the default ORDER BY logic from <code>CommerceOrderItemModelImpl</code>.
	 * </p>
	 *
	 * @param start the lower bound of the range of commerce order items
	 * @param end the upper bound of the range of commerce order items (not inclusive)
	 * @param orderByComparator the comparator to order the results by (optionally <code>null</code>)
	 * @return the ordered range of commerce order items
	 */
	public java.util.List<CommerceOrderItem> findAll(
		int start, int end,
		com.liferay.portal.kernel.util.OrderByComparator<CommerceOrderItem>
			orderByComparator);

	/**
	 * Returns an ordered range of all the commerce order items.
	 *
	 * <p>
	 * Useful when paginating results. Returns a maximum of <code>end - start</code> instances. <code>start</code> and <code>end</code> are not primary keys, they are indexes in the result set. Thus, <code>0</code> refers to the first result in the set. Setting both <code>start</code> and <code>end</code> to <code>QueryUtil#ALL_POS</code> will return the full result set. If <code>orderByComparator</code> is specified, then the query will include the given ORDER BY logic. If <code>orderByComparator</code> is absent, then the query will include the default ORDER BY logic from <code>CommerceOrderItemModelImpl</code>.
	 * </p>
	 *
	 * @param start the lower bound of the range of commerce order items
	 * @param end the upper bound of the range of commerce order items (not inclusive)
	 * @param orderByComparator the comparator to order the results by (optionally <code>null</code>)
	 * @param useFinderCache whether to use the finder cache
	 * @return the ordered range of commerce order items
	 */
	public java.util.List<CommerceOrderItem> findAll(
		int start, int end,
		com.liferay.portal.kernel.util.OrderByComparator<CommerceOrderItem>
			orderByComparator,
		boolean useFinderCache);

	/**
	 * Removes all the commerce order items from the database.
	 */
	public void removeAll();

	/**
	 * Returns the number of commerce order items.
	 *
	 * @return the number of commerce order items
	 */
	public int countAll();

}