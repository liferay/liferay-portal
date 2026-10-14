/**
 * SPDX-FileCopyrightText: (c) 2000 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

package com.liferay.commerce.service.persistence;

import com.liferay.commerce.exception.NoSuchOrderException;
import com.liferay.commerce.model.CommerceOrder;
import com.liferay.portal.kernel.service.persistence.BasePersistence;

import java.util.Date;

import org.osgi.annotation.versioning.ProviderType;

/**
 * The persistence interface for the commerce order service.
 *
 * <p>
 * Caching information and settings can be found in <code>portal.properties</code>
 * </p>
 *
 * @author Alessio Antonio Rendina
 * @see CommerceOrderUtil
 * @generated
 */
@ProviderType
public interface CommerceOrderPersistence
	extends BasePersistence<CommerceOrder> {

	/*
	 * NOTE FOR DEVELOPERS:
	 *
	 * Never modify or reference this interface directly. Always use {@link CommerceOrderUtil} to access the commerce order persistence. Modify <code>service.xml</code> and rerun ServiceBuilder to regenerate this interface.
	 */

	/**
	 * Returns an ordered range of all the commerce orders where uuid = &#63;.
	 *
	 * <p>
	 * Useful when paginating results. Returns a maximum of <code>end - start</code> instances. <code>start</code> and <code>end</code> are not primary keys, they are indexes in the result set. Thus, <code>0</code> refers to the first result in the set. Setting both <code>start</code> and <code>end</code> to <code>com.liferay.portal.kernel.dao.orm.QueryUtil#ALL_POS</code> will return the full result set. If <code>orderByComparator</code> is specified, then the query will include the given ORDER BY logic. If <code>orderByComparator</code> is absent, then the query will include the default ORDER BY logic from <code>com.liferay.commerce.model.impl.CommerceOrderModelImpl</code>.
	 * </p>
	 *
	 * @param uuid the uuid
	 * @param start the lower bound of the range of commerce orders
	 * @param end the upper bound of the range of commerce orders (not inclusive)
	 * @param orderByComparator the comparator to order the results by (optionally <code>null</code>)
	 * @param useFinderCache whether to use the finder cache
	 * @return the ordered range of matching commerce orders
	 */
	public java.util.List<CommerceOrder> findByUuid(
		String uuid, int start, int end,
		com.liferay.portal.kernel.util.OrderByComparator<CommerceOrder>
			orderByComparator,
		boolean useFinderCache);

	/**
	 * Returns the first commerce order in the ordered set where uuid = &#63;.
	 *
	 * @param uuid the uuid
	 * @param orderByComparator the comparator to order the set by (optionally <code>null</code>)
	 * @return the first matching commerce order
	 * @throws NoSuchOrderException if a matching commerce order could not be found
	 */
	public CommerceOrder findByUuid_First(
			String uuid,
			com.liferay.portal.kernel.util.OrderByComparator<CommerceOrder>
				orderByComparator)
		throws NoSuchOrderException;

	/**
	 * Returns the first commerce order in the ordered set where uuid = &#63;.
	 *
	 * @param uuid the uuid
	 * @param orderByComparator the comparator to order the set by (optionally <code>null</code>)
	 * @return the first matching commerce order, or <code>null</code> if a matching commerce order could not be found
	 */
	public CommerceOrder fetchByUuid_First(
		String uuid,
		com.liferay.portal.kernel.util.OrderByComparator<CommerceOrder>
			orderByComparator);

	/**
	 * Removes all the commerce orders where uuid = &#63; from the database.
	 *
	 * @param uuid the uuid
	 */
	public void removeByUuid(String uuid);

	/**
	 * Returns the number of commerce orders where uuid = &#63;.
	 *
	 * @param uuid the uuid
	 * @return the number of matching commerce orders
	 */
	public int countByUuid(String uuid);

	/**
	 * Returns the commerce order where uuid = &#63; and groupId = &#63; or throws a <code>NoSuchOrderException</code> if it could not be found.
	 *
	 * @param uuid the uuid
	 * @param groupId the group ID
	 * @return the matching commerce order
	 * @throws NoSuchOrderException if a matching commerce order could not be found
	 */
	public CommerceOrder findByUUID_G(String uuid, long groupId)
		throws NoSuchOrderException;

	/**
	 * Returns the commerce order where uuid = &#63; and groupId = &#63; or returns <code>null</code> if it could not be found, optionally using the finder cache.
	 *
	 * @param uuid the uuid
	 * @param groupId the group ID
	 * @param useFinderCache whether to use the finder cache
	 * @return the matching commerce order, or <code>null</code> if a matching commerce order could not be found
	 */
	public CommerceOrder fetchByUUID_G(
		String uuid, long groupId, boolean useFinderCache);

	/**
	 * Removes the commerce order where uuid = &#63; and groupId = &#63; from the database.
	 *
	 * @param uuid the uuid
	 * @param groupId the group ID
	 * @return the commerce order that was removed
	 */
	public CommerceOrder removeByUUID_G(String uuid, long groupId)
		throws NoSuchOrderException;

	/**
	 * Returns the number of commerce orders where uuid = &#63; and groupId = &#63;.
	 *
	 * @param uuid the uuid
	 * @param groupId the group ID
	 * @return the number of matching commerce orders
	 */
	public int countByUUID_G(String uuid, long groupId);

	/**
	 * Returns an ordered range of all the commerce orders where uuid = &#63; and companyId = &#63;.
	 *
	 * <p>
	 * Useful when paginating results. Returns a maximum of <code>end - start</code> instances. <code>start</code> and <code>end</code> are not primary keys, they are indexes in the result set. Thus, <code>0</code> refers to the first result in the set. Setting both <code>start</code> and <code>end</code> to <code>com.liferay.portal.kernel.dao.orm.QueryUtil#ALL_POS</code> will return the full result set. If <code>orderByComparator</code> is specified, then the query will include the given ORDER BY logic. If <code>orderByComparator</code> is absent, then the query will include the default ORDER BY logic from <code>com.liferay.commerce.model.impl.CommerceOrderModelImpl</code>.
	 * </p>
	 *
	 * @param uuid the uuid
	 * @param companyId the company ID
	 * @param start the lower bound of the range of commerce orders
	 * @param end the upper bound of the range of commerce orders (not inclusive)
	 * @param orderByComparator the comparator to order the results by (optionally <code>null</code>)
	 * @param useFinderCache whether to use the finder cache
	 * @return the ordered range of matching commerce orders
	 */
	public java.util.List<CommerceOrder> findByUuid_C(
		String uuid, long companyId, int start, int end,
		com.liferay.portal.kernel.util.OrderByComparator<CommerceOrder>
			orderByComparator,
		boolean useFinderCache);

	/**
	 * Returns the first commerce order in the ordered set where uuid = &#63; and companyId = &#63;.
	 *
	 * @param uuid the uuid
	 * @param companyId the company ID
	 * @param orderByComparator the comparator to order the set by (optionally <code>null</code>)
	 * @return the first matching commerce order
	 * @throws NoSuchOrderException if a matching commerce order could not be found
	 */
	public CommerceOrder findByUuid_C_First(
			String uuid, long companyId,
			com.liferay.portal.kernel.util.OrderByComparator<CommerceOrder>
				orderByComparator)
		throws NoSuchOrderException;

	/**
	 * Returns the first commerce order in the ordered set where uuid = &#63; and companyId = &#63;.
	 *
	 * @param uuid the uuid
	 * @param companyId the company ID
	 * @param orderByComparator the comparator to order the set by (optionally <code>null</code>)
	 * @return the first matching commerce order, or <code>null</code> if a matching commerce order could not be found
	 */
	public CommerceOrder fetchByUuid_C_First(
		String uuid, long companyId,
		com.liferay.portal.kernel.util.OrderByComparator<CommerceOrder>
			orderByComparator);

	/**
	 * Removes all the commerce orders where uuid = &#63; and companyId = &#63; from the database.
	 *
	 * @param uuid the uuid
	 * @param companyId the company ID
	 */
	public void removeByUuid_C(String uuid, long companyId);

	/**
	 * Returns the number of commerce orders where uuid = &#63; and companyId = &#63;.
	 *
	 * @param uuid the uuid
	 * @param companyId the company ID
	 * @return the number of matching commerce orders
	 */
	public int countByUuid_C(String uuid, long companyId);

	/**
	 * Returns an ordered range of all the commerce orders where groupId = &#63;.
	 *
	 * <p>
	 * Useful when paginating results. Returns a maximum of <code>end - start</code> instances. <code>start</code> and <code>end</code> are not primary keys, they are indexes in the result set. Thus, <code>0</code> refers to the first result in the set. Setting both <code>start</code> and <code>end</code> to <code>com.liferay.portal.kernel.dao.orm.QueryUtil#ALL_POS</code> will return the full result set. If <code>orderByComparator</code> is specified, then the query will include the given ORDER BY logic. If <code>orderByComparator</code> is absent, then the query will include the default ORDER BY logic from <code>com.liferay.commerce.model.impl.CommerceOrderModelImpl</code>.
	 * </p>
	 *
	 * @param groupId the group ID
	 * @param start the lower bound of the range of commerce orders
	 * @param end the upper bound of the range of commerce orders (not inclusive)
	 * @param orderByComparator the comparator to order the results by (optionally <code>null</code>)
	 * @param useFinderCache whether to use the finder cache
	 * @return the ordered range of matching commerce orders
	 */
	public java.util.List<CommerceOrder> findByGroupId(
		long groupId, int start, int end,
		com.liferay.portal.kernel.util.OrderByComparator<CommerceOrder>
			orderByComparator,
		boolean useFinderCache);

	/**
	 * Returns the first commerce order in the ordered set where groupId = &#63;.
	 *
	 * @param groupId the group ID
	 * @param orderByComparator the comparator to order the set by (optionally <code>null</code>)
	 * @return the first matching commerce order
	 * @throws NoSuchOrderException if a matching commerce order could not be found
	 */
	public CommerceOrder findByGroupId_First(
			long groupId,
			com.liferay.portal.kernel.util.OrderByComparator<CommerceOrder>
				orderByComparator)
		throws NoSuchOrderException;

	/**
	 * Returns the first commerce order in the ordered set where groupId = &#63;.
	 *
	 * @param groupId the group ID
	 * @param orderByComparator the comparator to order the set by (optionally <code>null</code>)
	 * @return the first matching commerce order, or <code>null</code> if a matching commerce order could not be found
	 */
	public CommerceOrder fetchByGroupId_First(
		long groupId,
		com.liferay.portal.kernel.util.OrderByComparator<CommerceOrder>
			orderByComparator);

	/**
	 * Returns an ordered range of all the commerce orders that the user has permissions to view where groupId = &#63;.
	 *
	 * <p>
	 * Useful when paginating results. Returns a maximum of <code>end - start</code> instances. <code>start</code> and <code>end</code> are not primary keys, they are indexes in the result set. Thus, <code>0</code> refers to the first result in the set. Setting both <code>start</code> and <code>end</code> to <code>com.liferay.portal.kernel.dao.orm.QueryUtil#ALL_POS</code> will return the full result set. If <code>orderByComparator</code> is specified, then the query will include the given ORDER BY logic. If <code>orderByComparator</code> is absent, then the query will include the default ORDER BY logic from <code>com.liferay.commerce.model.impl.CommerceOrderModelImpl</code>.
	 * </p>
	 *
	 * @param groupId the group ID
	 * @param start the lower bound of the range of commerce orders
	 * @param end the upper bound of the range of commerce orders (not inclusive)
	 * @param orderByComparator the comparator to order the results by (optionally <code>null</code>)
	 * @return the ordered range of matching commerce orders that the user has permission to view
	 */
	public java.util.List<CommerceOrder> filterFindByGroupId(
		long groupId, int start, int end,
		com.liferay.portal.kernel.util.OrderByComparator<CommerceOrder>
			orderByComparator);

	/**
	 * Removes all the commerce orders where groupId = &#63; from the database.
	 *
	 * @param groupId the group ID
	 */
	public void removeByGroupId(long groupId);

	/**
	 * Returns the number of commerce orders where groupId = &#63;.
	 *
	 * @param groupId the group ID
	 * @return the number of matching commerce orders
	 */
	public int countByGroupId(long groupId);

	/**
	 * Returns the number of commerce orders that the user has permission to view where groupId = &#63;.
	 *
	 * @param groupId the group ID
	 * @return the number of matching commerce orders that the user has permission to view
	 */
	public int filterCountByGroupId(long groupId);

	/**
	 * Returns an ordered range of all the commerce orders where userId = &#63;.
	 *
	 * <p>
	 * Useful when paginating results. Returns a maximum of <code>end - start</code> instances. <code>start</code> and <code>end</code> are not primary keys, they are indexes in the result set. Thus, <code>0</code> refers to the first result in the set. Setting both <code>start</code> and <code>end</code> to <code>com.liferay.portal.kernel.dao.orm.QueryUtil#ALL_POS</code> will return the full result set. If <code>orderByComparator</code> is specified, then the query will include the given ORDER BY logic. If <code>orderByComparator</code> is absent, then the query will include the default ORDER BY logic from <code>com.liferay.commerce.model.impl.CommerceOrderModelImpl</code>.
	 * </p>
	 *
	 * @param userId the user ID
	 * @param start the lower bound of the range of commerce orders
	 * @param end the upper bound of the range of commerce orders (not inclusive)
	 * @param orderByComparator the comparator to order the results by (optionally <code>null</code>)
	 * @param useFinderCache whether to use the finder cache
	 * @return the ordered range of matching commerce orders
	 */
	public java.util.List<CommerceOrder> findByUserId(
		long userId, int start, int end,
		com.liferay.portal.kernel.util.OrderByComparator<CommerceOrder>
			orderByComparator,
		boolean useFinderCache);

	/**
	 * Returns the first commerce order in the ordered set where userId = &#63;.
	 *
	 * @param userId the user ID
	 * @param orderByComparator the comparator to order the set by (optionally <code>null</code>)
	 * @return the first matching commerce order
	 * @throws NoSuchOrderException if a matching commerce order could not be found
	 */
	public CommerceOrder findByUserId_First(
			long userId,
			com.liferay.portal.kernel.util.OrderByComparator<CommerceOrder>
				orderByComparator)
		throws NoSuchOrderException;

	/**
	 * Returns the first commerce order in the ordered set where userId = &#63;.
	 *
	 * @param userId the user ID
	 * @param orderByComparator the comparator to order the set by (optionally <code>null</code>)
	 * @return the first matching commerce order, or <code>null</code> if a matching commerce order could not be found
	 */
	public CommerceOrder fetchByUserId_First(
		long userId,
		com.liferay.portal.kernel.util.OrderByComparator<CommerceOrder>
			orderByComparator);

	/**
	 * Removes all the commerce orders where userId = &#63; from the database.
	 *
	 * @param userId the user ID
	 */
	public void removeByUserId(long userId);

	/**
	 * Returns the number of commerce orders where userId = &#63;.
	 *
	 * @param userId the user ID
	 * @return the number of matching commerce orders
	 */
	public int countByUserId(long userId);

	/**
	 * Returns an ordered range of all the commerce orders where billingAddressId = &#63;.
	 *
	 * <p>
	 * Useful when paginating results. Returns a maximum of <code>end - start</code> instances. <code>start</code> and <code>end</code> are not primary keys, they are indexes in the result set. Thus, <code>0</code> refers to the first result in the set. Setting both <code>start</code> and <code>end</code> to <code>com.liferay.portal.kernel.dao.orm.QueryUtil#ALL_POS</code> will return the full result set. If <code>orderByComparator</code> is specified, then the query will include the given ORDER BY logic. If <code>orderByComparator</code> is absent, then the query will include the default ORDER BY logic from <code>com.liferay.commerce.model.impl.CommerceOrderModelImpl</code>.
	 * </p>
	 *
	 * @param billingAddressId the billing address ID
	 * @param start the lower bound of the range of commerce orders
	 * @param end the upper bound of the range of commerce orders (not inclusive)
	 * @param orderByComparator the comparator to order the results by (optionally <code>null</code>)
	 * @param useFinderCache whether to use the finder cache
	 * @return the ordered range of matching commerce orders
	 */
	public java.util.List<CommerceOrder> findByBillingAddressId(
		long billingAddressId, int start, int end,
		com.liferay.portal.kernel.util.OrderByComparator<CommerceOrder>
			orderByComparator,
		boolean useFinderCache);

	/**
	 * Returns the first commerce order in the ordered set where billingAddressId = &#63;.
	 *
	 * @param billingAddressId the billing address ID
	 * @param orderByComparator the comparator to order the set by (optionally <code>null</code>)
	 * @return the first matching commerce order
	 * @throws NoSuchOrderException if a matching commerce order could not be found
	 */
	public CommerceOrder findByBillingAddressId_First(
			long billingAddressId,
			com.liferay.portal.kernel.util.OrderByComparator<CommerceOrder>
				orderByComparator)
		throws NoSuchOrderException;

	/**
	 * Returns the first commerce order in the ordered set where billingAddressId = &#63;.
	 *
	 * @param billingAddressId the billing address ID
	 * @param orderByComparator the comparator to order the set by (optionally <code>null</code>)
	 * @return the first matching commerce order, or <code>null</code> if a matching commerce order could not be found
	 */
	public CommerceOrder fetchByBillingAddressId_First(
		long billingAddressId,
		com.liferay.portal.kernel.util.OrderByComparator<CommerceOrder>
			orderByComparator);

	/**
	 * Removes all the commerce orders where billingAddressId = &#63; from the database.
	 *
	 * @param billingAddressId the billing address ID
	 */
	public void removeByBillingAddressId(long billingAddressId);

	/**
	 * Returns the number of commerce orders where billingAddressId = &#63;.
	 *
	 * @param billingAddressId the billing address ID
	 * @return the number of matching commerce orders
	 */
	public int countByBillingAddressId(long billingAddressId);

	/**
	 * Returns an ordered range of all the commerce orders where commerceAccountId = &#63;.
	 *
	 * <p>
	 * Useful when paginating results. Returns a maximum of <code>end - start</code> instances. <code>start</code> and <code>end</code> are not primary keys, they are indexes in the result set. Thus, <code>0</code> refers to the first result in the set. Setting both <code>start</code> and <code>end</code> to <code>com.liferay.portal.kernel.dao.orm.QueryUtil#ALL_POS</code> will return the full result set. If <code>orderByComparator</code> is specified, then the query will include the given ORDER BY logic. If <code>orderByComparator</code> is absent, then the query will include the default ORDER BY logic from <code>com.liferay.commerce.model.impl.CommerceOrderModelImpl</code>.
	 * </p>
	 *
	 * @param commerceAccountId the commerce account ID
	 * @param start the lower bound of the range of commerce orders
	 * @param end the upper bound of the range of commerce orders (not inclusive)
	 * @param orderByComparator the comparator to order the results by (optionally <code>null</code>)
	 * @param useFinderCache whether to use the finder cache
	 * @return the ordered range of matching commerce orders
	 */
	public java.util.List<CommerceOrder> findByCommerceAccountId(
		long commerceAccountId, int start, int end,
		com.liferay.portal.kernel.util.OrderByComparator<CommerceOrder>
			orderByComparator,
		boolean useFinderCache);

	/**
	 * Returns the first commerce order in the ordered set where commerceAccountId = &#63;.
	 *
	 * @param commerceAccountId the commerce account ID
	 * @param orderByComparator the comparator to order the set by (optionally <code>null</code>)
	 * @return the first matching commerce order
	 * @throws NoSuchOrderException if a matching commerce order could not be found
	 */
	public CommerceOrder findByCommerceAccountId_First(
			long commerceAccountId,
			com.liferay.portal.kernel.util.OrderByComparator<CommerceOrder>
				orderByComparator)
		throws NoSuchOrderException;

	/**
	 * Returns the first commerce order in the ordered set where commerceAccountId = &#63;.
	 *
	 * @param commerceAccountId the commerce account ID
	 * @param orderByComparator the comparator to order the set by (optionally <code>null</code>)
	 * @return the first matching commerce order, or <code>null</code> if a matching commerce order could not be found
	 */
	public CommerceOrder fetchByCommerceAccountId_First(
		long commerceAccountId,
		com.liferay.portal.kernel.util.OrderByComparator<CommerceOrder>
			orderByComparator);

	/**
	 * Removes all the commerce orders where commerceAccountId = &#63; from the database.
	 *
	 * @param commerceAccountId the commerce account ID
	 */
	public void removeByCommerceAccountId(long commerceAccountId);

	/**
	 * Returns the number of commerce orders where commerceAccountId = &#63;.
	 *
	 * @param commerceAccountId the commerce account ID
	 * @return the number of matching commerce orders
	 */
	public int countByCommerceAccountId(long commerceAccountId);

	/**
	 * Returns an ordered range of all the commerce orders where shippingAddressId = &#63;.
	 *
	 * <p>
	 * Useful when paginating results. Returns a maximum of <code>end - start</code> instances. <code>start</code> and <code>end</code> are not primary keys, they are indexes in the result set. Thus, <code>0</code> refers to the first result in the set. Setting both <code>start</code> and <code>end</code> to <code>com.liferay.portal.kernel.dao.orm.QueryUtil#ALL_POS</code> will return the full result set. If <code>orderByComparator</code> is specified, then the query will include the given ORDER BY logic. If <code>orderByComparator</code> is absent, then the query will include the default ORDER BY logic from <code>com.liferay.commerce.model.impl.CommerceOrderModelImpl</code>.
	 * </p>
	 *
	 * @param shippingAddressId the shipping address ID
	 * @param start the lower bound of the range of commerce orders
	 * @param end the upper bound of the range of commerce orders (not inclusive)
	 * @param orderByComparator the comparator to order the results by (optionally <code>null</code>)
	 * @param useFinderCache whether to use the finder cache
	 * @return the ordered range of matching commerce orders
	 */
	public java.util.List<CommerceOrder> findByShippingAddressId(
		long shippingAddressId, int start, int end,
		com.liferay.portal.kernel.util.OrderByComparator<CommerceOrder>
			orderByComparator,
		boolean useFinderCache);

	/**
	 * Returns the first commerce order in the ordered set where shippingAddressId = &#63;.
	 *
	 * @param shippingAddressId the shipping address ID
	 * @param orderByComparator the comparator to order the set by (optionally <code>null</code>)
	 * @return the first matching commerce order
	 * @throws NoSuchOrderException if a matching commerce order could not be found
	 */
	public CommerceOrder findByShippingAddressId_First(
			long shippingAddressId,
			com.liferay.portal.kernel.util.OrderByComparator<CommerceOrder>
				orderByComparator)
		throws NoSuchOrderException;

	/**
	 * Returns the first commerce order in the ordered set where shippingAddressId = &#63;.
	 *
	 * @param shippingAddressId the shipping address ID
	 * @param orderByComparator the comparator to order the set by (optionally <code>null</code>)
	 * @return the first matching commerce order, or <code>null</code> if a matching commerce order could not be found
	 */
	public CommerceOrder fetchByShippingAddressId_First(
		long shippingAddressId,
		com.liferay.portal.kernel.util.OrderByComparator<CommerceOrder>
			orderByComparator);

	/**
	 * Removes all the commerce orders where shippingAddressId = &#63; from the database.
	 *
	 * @param shippingAddressId the shipping address ID
	 */
	public void removeByShippingAddressId(long shippingAddressId);

	/**
	 * Returns the number of commerce orders where shippingAddressId = &#63;.
	 *
	 * @param shippingAddressId the shipping address ID
	 * @return the number of matching commerce orders
	 */
	public int countByShippingAddressId(long shippingAddressId);

	/**
	 * Returns an ordered range of all the commerce orders where groupId = &#63; and commerceAccountId = &#63;.
	 *
	 * <p>
	 * Useful when paginating results. Returns a maximum of <code>end - start</code> instances. <code>start</code> and <code>end</code> are not primary keys, they are indexes in the result set. Thus, <code>0</code> refers to the first result in the set. Setting both <code>start</code> and <code>end</code> to <code>com.liferay.portal.kernel.dao.orm.QueryUtil#ALL_POS</code> will return the full result set. If <code>orderByComparator</code> is specified, then the query will include the given ORDER BY logic. If <code>orderByComparator</code> is absent, then the query will include the default ORDER BY logic from <code>com.liferay.commerce.model.impl.CommerceOrderModelImpl</code>.
	 * </p>
	 *
	 * @param groupId the group ID
	 * @param commerceAccountId the commerce account ID
	 * @param start the lower bound of the range of commerce orders
	 * @param end the upper bound of the range of commerce orders (not inclusive)
	 * @param orderByComparator the comparator to order the results by (optionally <code>null</code>)
	 * @param useFinderCache whether to use the finder cache
	 * @return the ordered range of matching commerce orders
	 */
	public java.util.List<CommerceOrder> findByG_C(
		long groupId, long commerceAccountId, int start, int end,
		com.liferay.portal.kernel.util.OrderByComparator<CommerceOrder>
			orderByComparator,
		boolean useFinderCache);

	/**
	 * Returns the first commerce order in the ordered set where groupId = &#63; and commerceAccountId = &#63;.
	 *
	 * @param groupId the group ID
	 * @param commerceAccountId the commerce account ID
	 * @param orderByComparator the comparator to order the set by (optionally <code>null</code>)
	 * @return the first matching commerce order
	 * @throws NoSuchOrderException if a matching commerce order could not be found
	 */
	public CommerceOrder findByG_C_First(
			long groupId, long commerceAccountId,
			com.liferay.portal.kernel.util.OrderByComparator<CommerceOrder>
				orderByComparator)
		throws NoSuchOrderException;

	/**
	 * Returns the first commerce order in the ordered set where groupId = &#63; and commerceAccountId = &#63;.
	 *
	 * @param groupId the group ID
	 * @param commerceAccountId the commerce account ID
	 * @param orderByComparator the comparator to order the set by (optionally <code>null</code>)
	 * @return the first matching commerce order, or <code>null</code> if a matching commerce order could not be found
	 */
	public CommerceOrder fetchByG_C_First(
		long groupId, long commerceAccountId,
		com.liferay.portal.kernel.util.OrderByComparator<CommerceOrder>
			orderByComparator);

	/**
	 * Returns an ordered range of all the commerce orders that the user has permissions to view where groupId = &#63; and commerceAccountId = &#63;.
	 *
	 * <p>
	 * Useful when paginating results. Returns a maximum of <code>end - start</code> instances. <code>start</code> and <code>end</code> are not primary keys, they are indexes in the result set. Thus, <code>0</code> refers to the first result in the set. Setting both <code>start</code> and <code>end</code> to <code>com.liferay.portal.kernel.dao.orm.QueryUtil#ALL_POS</code> will return the full result set. If <code>orderByComparator</code> is specified, then the query will include the given ORDER BY logic. If <code>orderByComparator</code> is absent, then the query will include the default ORDER BY logic from <code>com.liferay.commerce.model.impl.CommerceOrderModelImpl</code>.
	 * </p>
	 *
	 * @param groupId the group ID
	 * @param commerceAccountId the commerce account ID
	 * @param start the lower bound of the range of commerce orders
	 * @param end the upper bound of the range of commerce orders (not inclusive)
	 * @param orderByComparator the comparator to order the results by (optionally <code>null</code>)
	 * @return the ordered range of matching commerce orders that the user has permission to view
	 */
	public java.util.List<CommerceOrder> filterFindByG_C(
		long groupId, long commerceAccountId, int start, int end,
		com.liferay.portal.kernel.util.OrderByComparator<CommerceOrder>
			orderByComparator);

	/**
	 * Removes all the commerce orders where groupId = &#63; and commerceAccountId = &#63; from the database.
	 *
	 * @param groupId the group ID
	 * @param commerceAccountId the commerce account ID
	 */
	public void removeByG_C(long groupId, long commerceAccountId);

	/**
	 * Returns the number of commerce orders where groupId = &#63; and commerceAccountId = &#63;.
	 *
	 * @param groupId the group ID
	 * @param commerceAccountId the commerce account ID
	 * @return the number of matching commerce orders
	 */
	public int countByG_C(long groupId, long commerceAccountId);

	/**
	 * Returns the number of commerce orders that the user has permission to view where groupId = &#63; and commerceAccountId = &#63;.
	 *
	 * @param groupId the group ID
	 * @param commerceAccountId the commerce account ID
	 * @return the number of matching commerce orders that the user has permission to view
	 */
	public int filterCountByG_C(long groupId, long commerceAccountId);

	/**
	 * Returns an ordered range of all the commerce orders where groupId = &#63; and userId = &#63; and orderStatus = &#63;.
	 *
	 * <p>
	 * Useful when paginating results. Returns a maximum of <code>end - start</code> instances. <code>start</code> and <code>end</code> are not primary keys, they are indexes in the result set. Thus, <code>0</code> refers to the first result in the set. Setting both <code>start</code> and <code>end</code> to <code>com.liferay.portal.kernel.dao.orm.QueryUtil#ALL_POS</code> will return the full result set. If <code>orderByComparator</code> is specified, then the query will include the given ORDER BY logic. If <code>orderByComparator</code> is absent, then the query will include the default ORDER BY logic from <code>com.liferay.commerce.model.impl.CommerceOrderModelImpl</code>.
	 * </p>
	 *
	 * @param groupId the group ID
	 * @param userId the user ID
	 * @param orderStatus the order status
	 * @param start the lower bound of the range of commerce orders
	 * @param end the upper bound of the range of commerce orders (not inclusive)
	 * @param orderByComparator the comparator to order the results by (optionally <code>null</code>)
	 * @param useFinderCache whether to use the finder cache
	 * @return the ordered range of matching commerce orders
	 */
	public java.util.List<CommerceOrder> findByG_U_O(
		long groupId, long userId, int orderStatus, int start, int end,
		com.liferay.portal.kernel.util.OrderByComparator<CommerceOrder>
			orderByComparator,
		boolean useFinderCache);

	/**
	 * Returns the first commerce order in the ordered set where groupId = &#63; and userId = &#63; and orderStatus = &#63;.
	 *
	 * @param groupId the group ID
	 * @param userId the user ID
	 * @param orderStatus the order status
	 * @param orderByComparator the comparator to order the set by (optionally <code>null</code>)
	 * @return the first matching commerce order
	 * @throws NoSuchOrderException if a matching commerce order could not be found
	 */
	public CommerceOrder findByG_U_O_First(
			long groupId, long userId, int orderStatus,
			com.liferay.portal.kernel.util.OrderByComparator<CommerceOrder>
				orderByComparator)
		throws NoSuchOrderException;

	/**
	 * Returns the first commerce order in the ordered set where groupId = &#63; and userId = &#63; and orderStatus = &#63;.
	 *
	 * @param groupId the group ID
	 * @param userId the user ID
	 * @param orderStatus the order status
	 * @param orderByComparator the comparator to order the set by (optionally <code>null</code>)
	 * @return the first matching commerce order, or <code>null</code> if a matching commerce order could not be found
	 */
	public CommerceOrder fetchByG_U_O_First(
		long groupId, long userId, int orderStatus,
		com.liferay.portal.kernel.util.OrderByComparator<CommerceOrder>
			orderByComparator);

	/**
	 * Returns an ordered range of all the commerce orders that the user has permissions to view where groupId = &#63; and userId = &#63; and orderStatus = &#63;.
	 *
	 * <p>
	 * Useful when paginating results. Returns a maximum of <code>end - start</code> instances. <code>start</code> and <code>end</code> are not primary keys, they are indexes in the result set. Thus, <code>0</code> refers to the first result in the set. Setting both <code>start</code> and <code>end</code> to <code>com.liferay.portal.kernel.dao.orm.QueryUtil#ALL_POS</code> will return the full result set. If <code>orderByComparator</code> is specified, then the query will include the given ORDER BY logic. If <code>orderByComparator</code> is absent, then the query will include the default ORDER BY logic from <code>com.liferay.commerce.model.impl.CommerceOrderModelImpl</code>.
	 * </p>
	 *
	 * @param groupId the group ID
	 * @param userId the user ID
	 * @param orderStatus the order status
	 * @param start the lower bound of the range of commerce orders
	 * @param end the upper bound of the range of commerce orders (not inclusive)
	 * @param orderByComparator the comparator to order the results by (optionally <code>null</code>)
	 * @return the ordered range of matching commerce orders that the user has permission to view
	 */
	public java.util.List<CommerceOrder> filterFindByG_U_O(
		long groupId, long userId, int orderStatus, int start, int end,
		com.liferay.portal.kernel.util.OrderByComparator<CommerceOrder>
			orderByComparator);

	/**
	 * Removes all the commerce orders where groupId = &#63; and userId = &#63; and orderStatus = &#63; from the database.
	 *
	 * @param groupId the group ID
	 * @param userId the user ID
	 * @param orderStatus the order status
	 */
	public void removeByG_U_O(long groupId, long userId, int orderStatus);

	/**
	 * Returns the number of commerce orders where groupId = &#63; and userId = &#63; and orderStatus = &#63;.
	 *
	 * @param groupId the group ID
	 * @param userId the user ID
	 * @param orderStatus the order status
	 * @return the number of matching commerce orders
	 */
	public int countByG_U_O(long groupId, long userId, int orderStatus);

	/**
	 * Returns the number of commerce orders that the user has permission to view where groupId = &#63; and userId = &#63; and orderStatus = &#63;.
	 *
	 * @param groupId the group ID
	 * @param userId the user ID
	 * @param orderStatus the order status
	 * @return the number of matching commerce orders that the user has permission to view
	 */
	public int filterCountByG_U_O(long groupId, long userId, int orderStatus);

	/**
	 * Returns an ordered range of all the commerce orders where groupId = &#63; and commerceAccountId = &#63; and orderStatus = &#63;.
	 *
	 * <p>
	 * Useful when paginating results. Returns a maximum of <code>end - start</code> instances. <code>start</code> and <code>end</code> are not primary keys, they are indexes in the result set. Thus, <code>0</code> refers to the first result in the set. Setting both <code>start</code> and <code>end</code> to <code>com.liferay.portal.kernel.dao.orm.QueryUtil#ALL_POS</code> will return the full result set. If <code>orderByComparator</code> is specified, then the query will include the given ORDER BY logic. If <code>orderByComparator</code> is absent, then the query will include the default ORDER BY logic from <code>com.liferay.commerce.model.impl.CommerceOrderModelImpl</code>.
	 * </p>
	 *
	 * @param groupId the group ID
	 * @param commerceAccountId the commerce account ID
	 * @param orderStatus the order status
	 * @param start the lower bound of the range of commerce orders
	 * @param end the upper bound of the range of commerce orders (not inclusive)
	 * @param orderByComparator the comparator to order the results by (optionally <code>null</code>)
	 * @param useFinderCache whether to use the finder cache
	 * @return the ordered range of matching commerce orders
	 */
	public java.util.List<CommerceOrder> findByG_C_O(
		long groupId, long commerceAccountId, int orderStatus, int start,
		int end,
		com.liferay.portal.kernel.util.OrderByComparator<CommerceOrder>
			orderByComparator,
		boolean useFinderCache);

	/**
	 * Returns the first commerce order in the ordered set where groupId = &#63; and commerceAccountId = &#63; and orderStatus = &#63;.
	 *
	 * @param groupId the group ID
	 * @param commerceAccountId the commerce account ID
	 * @param orderStatus the order status
	 * @param orderByComparator the comparator to order the set by (optionally <code>null</code>)
	 * @return the first matching commerce order
	 * @throws NoSuchOrderException if a matching commerce order could not be found
	 */
	public CommerceOrder findByG_C_O_First(
			long groupId, long commerceAccountId, int orderStatus,
			com.liferay.portal.kernel.util.OrderByComparator<CommerceOrder>
				orderByComparator)
		throws NoSuchOrderException;

	/**
	 * Returns the first commerce order in the ordered set where groupId = &#63; and commerceAccountId = &#63; and orderStatus = &#63;.
	 *
	 * @param groupId the group ID
	 * @param commerceAccountId the commerce account ID
	 * @param orderStatus the order status
	 * @param orderByComparator the comparator to order the set by (optionally <code>null</code>)
	 * @return the first matching commerce order, or <code>null</code> if a matching commerce order could not be found
	 */
	public CommerceOrder fetchByG_C_O_First(
		long groupId, long commerceAccountId, int orderStatus,
		com.liferay.portal.kernel.util.OrderByComparator<CommerceOrder>
			orderByComparator);

	/**
	 * Returns an ordered range of all the commerce orders that the user has permissions to view where groupId = &#63; and commerceAccountId = &#63; and orderStatus = &#63;.
	 *
	 * <p>
	 * Useful when paginating results. Returns a maximum of <code>end - start</code> instances. <code>start</code> and <code>end</code> are not primary keys, they are indexes in the result set. Thus, <code>0</code> refers to the first result in the set. Setting both <code>start</code> and <code>end</code> to <code>com.liferay.portal.kernel.dao.orm.QueryUtil#ALL_POS</code> will return the full result set. If <code>orderByComparator</code> is specified, then the query will include the given ORDER BY logic. If <code>orderByComparator</code> is absent, then the query will include the default ORDER BY logic from <code>com.liferay.commerce.model.impl.CommerceOrderModelImpl</code>.
	 * </p>
	 *
	 * @param groupId the group ID
	 * @param commerceAccountId the commerce account ID
	 * @param orderStatus the order status
	 * @param start the lower bound of the range of commerce orders
	 * @param end the upper bound of the range of commerce orders (not inclusive)
	 * @param orderByComparator the comparator to order the results by (optionally <code>null</code>)
	 * @return the ordered range of matching commerce orders that the user has permission to view
	 */
	public java.util.List<CommerceOrder> filterFindByG_C_O(
		long groupId, long commerceAccountId, int orderStatus, int start,
		int end,
		com.liferay.portal.kernel.util.OrderByComparator<CommerceOrder>
			orderByComparator);

	/**
	 * Removes all the commerce orders where groupId = &#63; and commerceAccountId = &#63; and orderStatus = &#63; from the database.
	 *
	 * @param groupId the group ID
	 * @param commerceAccountId the commerce account ID
	 * @param orderStatus the order status
	 */
	public void removeByG_C_O(
		long groupId, long commerceAccountId, int orderStatus);

	/**
	 * Returns the number of commerce orders where groupId = &#63; and commerceAccountId = &#63; and orderStatus = &#63;.
	 *
	 * @param groupId the group ID
	 * @param commerceAccountId the commerce account ID
	 * @param orderStatus the order status
	 * @return the number of matching commerce orders
	 */
	public int countByG_C_O(
		long groupId, long commerceAccountId, int orderStatus);

	/**
	 * Returns the number of commerce orders that the user has permission to view where groupId = &#63; and commerceAccountId = &#63; and orderStatus = &#63;.
	 *
	 * @param groupId the group ID
	 * @param commerceAccountId the commerce account ID
	 * @param orderStatus the order status
	 * @return the number of matching commerce orders that the user has permission to view
	 */
	public int filterCountByG_C_O(
		long groupId, long commerceAccountId, int orderStatus);

	/**
	 * Returns all the commerce orders where createDate &lt; &#63; and commerceAccountId = &#63; and orderStatus = &#63;.
	 *
	 * @param createDate the create date
	 * @param commerceAccountId the commerce account ID
	 * @param orderStatus the order status
	 * @return the matching commerce orders
	 */
	public java.util.List<CommerceOrder> findByC_LtC_O(
		Date createDate, long commerceAccountId, int orderStatus);

	/**
	 * Returns a range of all the commerce orders where createDate &lt; &#63; and commerceAccountId = &#63; and orderStatus = &#63;.
	 *
	 * <p>
	 * Useful when paginating results. Returns a maximum of <code>end - start</code> instances. <code>start</code> and <code>end</code> are not primary keys, they are indexes in the result set. Thus, <code>0</code> refers to the first result in the set. Setting both <code>start</code> and <code>end</code> to <code>com.liferay.portal.kernel.dao.orm.QueryUtil#ALL_POS</code> will return the full result set. If <code>orderByComparator</code> is specified, then the query will include the given ORDER BY logic. If <code>orderByComparator</code> is absent, then the query will include the default ORDER BY logic from <code>com.liferay.commerce.model.impl.CommerceOrderModelImpl</code>.
	 * </p>
	 *
	 * @param createDate the create date
	 * @param commerceAccountId the commerce account ID
	 * @param orderStatus the order status
	 * @param start the lower bound of the range of commerce orders
	 * @param end the upper bound of the range of commerce orders (not inclusive)
	 * @return the range of matching commerce orders
	 */
	public java.util.List<CommerceOrder> findByC_LtC_O(
		Date createDate, long commerceAccountId, int orderStatus, int start,
		int end);

	/**
	 * Returns an ordered range of all the commerce orders where createDate &lt; &#63; and commerceAccountId = &#63; and orderStatus = &#63;.
	 *
	 * <p>
	 * Useful when paginating results. Returns a maximum of <code>end - start</code> instances. <code>start</code> and <code>end</code> are not primary keys, they are indexes in the result set. Thus, <code>0</code> refers to the first result in the set. Setting both <code>start</code> and <code>end</code> to <code>com.liferay.portal.kernel.dao.orm.QueryUtil#ALL_POS</code> will return the full result set. If <code>orderByComparator</code> is specified, then the query will include the given ORDER BY logic. If <code>orderByComparator</code> is absent, then the query will include the default ORDER BY logic from <code>com.liferay.commerce.model.impl.CommerceOrderModelImpl</code>.
	 * </p>
	 *
	 * @param createDate the create date
	 * @param commerceAccountId the commerce account ID
	 * @param orderStatus the order status
	 * @param start the lower bound of the range of commerce orders
	 * @param end the upper bound of the range of commerce orders (not inclusive)
	 * @param orderByComparator the comparator to order the results by (optionally <code>null</code>)
	 * @return the ordered range of matching commerce orders
	 */
	public java.util.List<CommerceOrder> findByC_LtC_O(
		Date createDate, long commerceAccountId, int orderStatus, int start,
		int end,
		com.liferay.portal.kernel.util.OrderByComparator<CommerceOrder>
			orderByComparator);

	/**
	 * Returns an ordered range of all the commerce orders where createDate &lt; &#63; and commerceAccountId = &#63; and orderStatus = &#63;.
	 *
	 * <p>
	 * Useful when paginating results. Returns a maximum of <code>end - start</code> instances. <code>start</code> and <code>end</code> are not primary keys, they are indexes in the result set. Thus, <code>0</code> refers to the first result in the set. Setting both <code>start</code> and <code>end</code> to <code>com.liferay.portal.kernel.dao.orm.QueryUtil#ALL_POS</code> will return the full result set. If <code>orderByComparator</code> is specified, then the query will include the given ORDER BY logic. If <code>orderByComparator</code> is absent, then the query will include the default ORDER BY logic from <code>com.liferay.commerce.model.impl.CommerceOrderModelImpl</code>.
	 * </p>
	 *
	 * @param createDate the create date
	 * @param commerceAccountId the commerce account ID
	 * @param orderStatus the order status
	 * @param start the lower bound of the range of commerce orders
	 * @param end the upper bound of the range of commerce orders (not inclusive)
	 * @param orderByComparator the comparator to order the results by (optionally <code>null</code>)
	 * @param useFinderCache whether to use the finder cache
	 * @return the ordered range of matching commerce orders
	 */
	public java.util.List<CommerceOrder> findByC_LtC_O(
		Date createDate, long commerceAccountId, int orderStatus, int start,
		int end,
		com.liferay.portal.kernel.util.OrderByComparator<CommerceOrder>
			orderByComparator,
		boolean useFinderCache);

	/**
	 * Returns the first commerce order in the ordered set where createDate &lt; &#63; and commerceAccountId = &#63; and orderStatus = &#63;.
	 *
	 * @param createDate the create date
	 * @param commerceAccountId the commerce account ID
	 * @param orderStatus the order status
	 * @param orderByComparator the comparator to order the set by (optionally <code>null</code>)
	 * @return the first matching commerce order
	 * @throws NoSuchOrderException if a matching commerce order could not be found
	 */
	public CommerceOrder findByC_LtC_O_First(
			Date createDate, long commerceAccountId, int orderStatus,
			com.liferay.portal.kernel.util.OrderByComparator<CommerceOrder>
				orderByComparator)
		throws NoSuchOrderException;

	/**
	 * Returns the first commerce order in the ordered set where createDate &lt; &#63; and commerceAccountId = &#63; and orderStatus = &#63;.
	 *
	 * @param createDate the create date
	 * @param commerceAccountId the commerce account ID
	 * @param orderStatus the order status
	 * @param orderByComparator the comparator to order the set by (optionally <code>null</code>)
	 * @return the first matching commerce order, or <code>null</code> if a matching commerce order could not be found
	 */
	public CommerceOrder fetchByC_LtC_O_First(
		Date createDate, long commerceAccountId, int orderStatus,
		com.liferay.portal.kernel.util.OrderByComparator<CommerceOrder>
			orderByComparator);

	/**
	 * Removes all the commerce orders where createDate &lt; &#63; and commerceAccountId = &#63; and orderStatus = &#63; from the database.
	 *
	 * @param createDate the create date
	 * @param commerceAccountId the commerce account ID
	 * @param orderStatus the order status
	 */
	public void removeByC_LtC_O(
		Date createDate, long commerceAccountId, int orderStatus);

	/**
	 * Returns the number of commerce orders where createDate &lt; &#63; and commerceAccountId = &#63; and orderStatus = &#63;.
	 *
	 * @param createDate the create date
	 * @param commerceAccountId the commerce account ID
	 * @param orderStatus the order status
	 * @return the number of matching commerce orders
	 */
	public int countByC_LtC_O(
		Date createDate, long commerceAccountId, int orderStatus);

	/**
	 * Returns the commerce order where externalReferenceCode = &#63; and companyId = &#63; or throws a <code>NoSuchOrderException</code> if it could not be found.
	 *
	 * @param externalReferenceCode the external reference code
	 * @param companyId the company ID
	 * @return the matching commerce order
	 * @throws NoSuchOrderException if a matching commerce order could not be found
	 */
	public CommerceOrder findByERC_C(
			String externalReferenceCode, long companyId)
		throws NoSuchOrderException;

	/**
	 * Returns the commerce order where externalReferenceCode = &#63; and companyId = &#63; or returns <code>null</code> if it could not be found, optionally using the finder cache.
	 *
	 * @param externalReferenceCode the external reference code
	 * @param companyId the company ID
	 * @param useFinderCache whether to use the finder cache
	 * @return the matching commerce order, or <code>null</code> if a matching commerce order could not be found
	 */
	public CommerceOrder fetchByERC_C(
		String externalReferenceCode, long companyId, boolean useFinderCache);

	/**
	 * Removes the commerce order where externalReferenceCode = &#63; and companyId = &#63; from the database.
	 *
	 * @param externalReferenceCode the external reference code
	 * @param companyId the company ID
	 * @return the commerce order that was removed
	 */
	public CommerceOrder removeByERC_C(
			String externalReferenceCode, long companyId)
		throws NoSuchOrderException;

	/**
	 * Returns the number of commerce orders where externalReferenceCode = &#63; and companyId = &#63;.
	 *
	 * @param externalReferenceCode the external reference code
	 * @param companyId the company ID
	 * @return the number of matching commerce orders
	 */
	public int countByERC_C(String externalReferenceCode, long companyId);

	/**
	 * Creates a new commerce order with the primary key. Does not add the commerce order to the database.
	 *
	 * @param commerceOrderId the primary key for the new commerce order
	 * @return the new commerce order
	 */
	public CommerceOrder create(long commerceOrderId);

	/**
	 * Removes the commerce order with the primary key from the database. Also notifies the appropriate model listeners.
	 *
	 * @param commerceOrderId the primary key of the commerce order
	 * @return the commerce order that was removed
	 * @throws NoSuchOrderException if a commerce order with the primary key could not be found
	 */
	public CommerceOrder remove(long commerceOrderId)
		throws NoSuchOrderException;

	public CommerceOrder updateImpl(CommerceOrder commerceOrder);

	/**
	 * Returns the commerce order with the primary key or throws a <code>NoSuchOrderException</code> if it could not be found.
	 *
	 * @param commerceOrderId the primary key of the commerce order
	 * @return the commerce order
	 * @throws NoSuchOrderException if a commerce order with the primary key could not be found
	 */
	public CommerceOrder findByPrimaryKey(long commerceOrderId)
		throws NoSuchOrderException;

	/**
	 * Returns the commerce order with the primary key or returns <code>null</code> if it could not be found.
	 *
	 * @param commerceOrderId the primary key of the commerce order
	 * @return the commerce order, or <code>null</code> if a commerce order with the primary key could not be found
	 */
	public CommerceOrder fetchByPrimaryKey(long commerceOrderId);

	/**
	 * Returns the commerce order where uuid = &#63; and groupId = &#63; or returns <code>null</code> if it could not be found. Uses the finder cache.
	 *
	 * @param uuid the uuid
	 * @param groupId the group ID
	 * @return the matching commerce order, or <code>null</code> if a matching commerce order could not be found
	 */
	public default CommerceOrder fetchByUUID_G(String uuid, long groupId) {
		return fetchByUUID_G(uuid, groupId, true);
	}

	/**
	 * Returns the commerce order where externalReferenceCode = &#63; and companyId = &#63; or returns <code>null</code> if it could not be found. Uses the finder cache.
	 *
	 * @param externalReferenceCode the external reference code
	 * @param companyId the company ID
	 * @return the matching commerce order, or <code>null</code> if a matching commerce order could not be found
	 */
	public default CommerceOrder fetchByERC_C(
		String externalReferenceCode, long companyId) {

		return fetchByERC_C(externalReferenceCode, companyId, true);
	}

	/**
	 * Returns all the commerce orders where uuid = &#63;.
	 *
	 * @param uuid the uuid
	 * @return the matching commerce orders
	 */
	public default java.util.List<CommerceOrder> findByUuid(String uuid) {
		return findByUuid(
			uuid, com.liferay.portal.kernel.dao.orm.QueryUtil.ALL_POS,
			com.liferay.portal.kernel.dao.orm.QueryUtil.ALL_POS, null, true);
	}

	/**
	 * Returns a range of all the commerce orders where uuid = &#63;.
	 *
	 * <p>
	 * Useful when paginating results. Returns a maximum of <code>end - start</code> instances. <code>start</code> and <code>end</code> are not primary keys, they are indexes in the result set. Thus, <code>0</code> refers to the first result in the set. Setting both <code>start</code> and <code>end</code> to <code>com.liferay.portal.kernel.dao.orm.QueryUtil#ALL_POS</code> will return the full result set. If <code>orderByComparator</code> is specified, then the query will include the given ORDER BY logic. If <code>orderByComparator</code> is absent, then the query will include the default ORDER BY logic from <code>com.liferay.commerce.model.impl.CommerceOrderModelImpl</code>.
	 * </p>
	 *
	 * @param uuid the uuid
	 * @param start the lower bound of the range of commerce orders
	 * @param end the upper bound of the range of commerce orders (not inclusive)
	 * @return the range of matching commerce orders
	 */
	public default java.util.List<CommerceOrder> findByUuid(
		String uuid, int start, int end) {

		return findByUuid(uuid, start, end, null, true);
	}

	/**
	 * Returns an ordered range of all the commerce orders where uuid = &#63;.
	 *
	 * <p>
	 * Useful when paginating results. Returns a maximum of <code>end - start</code> instances. <code>start</code> and <code>end</code> are not primary keys, they are indexes in the result set. Thus, <code>0</code> refers to the first result in the set. Setting both <code>start</code> and <code>end</code> to <code>com.liferay.portal.kernel.dao.orm.QueryUtil#ALL_POS</code> will return the full result set. If <code>orderByComparator</code> is specified, then the query will include the given ORDER BY logic. If <code>orderByComparator</code> is absent, then the query will include the default ORDER BY logic from <code>com.liferay.commerce.model.impl.CommerceOrderModelImpl</code>.
	 * </p>
	 *
	 * @param uuid the uuid
	 * @param start the lower bound of the range of commerce orders
	 * @param end the upper bound of the range of commerce orders (not inclusive)
	 * @param orderByComparator the comparator to order the results by (optionally <code>null</code>)
	 * @return the ordered range of matching commerce orders
	 */
	public default java.util.List<CommerceOrder> findByUuid(
		String uuid, int start, int end,
		com.liferay.portal.kernel.util.OrderByComparator<CommerceOrder>
			orderByComparator) {

		return findByUuid(uuid, start, end, orderByComparator, true);
	}

	/**
	 * Returns all the commerce orders where uuid = &#63; and companyId = &#63;.
	 *
	 * @param uuid the uuid
	 * @param companyId the company ID
	 * @return the matching commerce orders
	 */
	public default java.util.List<CommerceOrder> findByUuid_C(
		String uuid, long companyId) {

		return findByUuid_C(
			uuid, companyId,
			com.liferay.portal.kernel.dao.orm.QueryUtil.ALL_POS,
			com.liferay.portal.kernel.dao.orm.QueryUtil.ALL_POS, null, true);
	}

	/**
	 * Returns a range of all the commerce orders where uuid = &#63; and companyId = &#63;.
	 *
	 * <p>
	 * Useful when paginating results. Returns a maximum of <code>end - start</code> instances. <code>start</code> and <code>end</code> are not primary keys, they are indexes in the result set. Thus, <code>0</code> refers to the first result in the set. Setting both <code>start</code> and <code>end</code> to <code>com.liferay.portal.kernel.dao.orm.QueryUtil#ALL_POS</code> will return the full result set. If <code>orderByComparator</code> is specified, then the query will include the given ORDER BY logic. If <code>orderByComparator</code> is absent, then the query will include the default ORDER BY logic from <code>com.liferay.commerce.model.impl.CommerceOrderModelImpl</code>.
	 * </p>
	 *
	 * @param uuid the uuid
	 * @param companyId the company ID
	 * @param start the lower bound of the range of commerce orders
	 * @param end the upper bound of the range of commerce orders (not inclusive)
	 * @return the range of matching commerce orders
	 */
	public default java.util.List<CommerceOrder> findByUuid_C(
		String uuid, long companyId, int start, int end) {

		return findByUuid_C(uuid, companyId, start, end, null, true);
	}

	/**
	 * Returns an ordered range of all the commerce orders where uuid = &#63; and companyId = &#63;.
	 *
	 * <p>
	 * Useful when paginating results. Returns a maximum of <code>end - start</code> instances. <code>start</code> and <code>end</code> are not primary keys, they are indexes in the result set. Thus, <code>0</code> refers to the first result in the set. Setting both <code>start</code> and <code>end</code> to <code>com.liferay.portal.kernel.dao.orm.QueryUtil#ALL_POS</code> will return the full result set. If <code>orderByComparator</code> is specified, then the query will include the given ORDER BY logic. If <code>orderByComparator</code> is absent, then the query will include the default ORDER BY logic from <code>com.liferay.commerce.model.impl.CommerceOrderModelImpl</code>.
	 * </p>
	 *
	 * @param uuid the uuid
	 * @param companyId the company ID
	 * @param start the lower bound of the range of commerce orders
	 * @param end the upper bound of the range of commerce orders (not inclusive)
	 * @param orderByComparator the comparator to order the results by (optionally <code>null</code>)
	 * @return the ordered range of matching commerce orders
	 */
	public default java.util.List<CommerceOrder> findByUuid_C(
		String uuid, long companyId, int start, int end,
		com.liferay.portal.kernel.util.OrderByComparator<CommerceOrder>
			orderByComparator) {

		return findByUuid_C(
			uuid, companyId, start, end, orderByComparator, true);
	}

	/**
	 * Returns all the commerce orders where groupId = &#63;.
	 *
	 * @param groupId the group ID
	 * @return the matching commerce orders
	 */
	public default java.util.List<CommerceOrder> findByGroupId(long groupId) {
		return findByGroupId(
			groupId, com.liferay.portal.kernel.dao.orm.QueryUtil.ALL_POS,
			com.liferay.portal.kernel.dao.orm.QueryUtil.ALL_POS, null, true);
	}

	/**
	 * Returns a range of all the commerce orders where groupId = &#63;.
	 *
	 * <p>
	 * Useful when paginating results. Returns a maximum of <code>end - start</code> instances. <code>start</code> and <code>end</code> are not primary keys, they are indexes in the result set. Thus, <code>0</code> refers to the first result in the set. Setting both <code>start</code> and <code>end</code> to <code>com.liferay.portal.kernel.dao.orm.QueryUtil#ALL_POS</code> will return the full result set. If <code>orderByComparator</code> is specified, then the query will include the given ORDER BY logic. If <code>orderByComparator</code> is absent, then the query will include the default ORDER BY logic from <code>com.liferay.commerce.model.impl.CommerceOrderModelImpl</code>.
	 * </p>
	 *
	 * @param groupId the group ID
	 * @param start the lower bound of the range of commerce orders
	 * @param end the upper bound of the range of commerce orders (not inclusive)
	 * @return the range of matching commerce orders
	 */
	public default java.util.List<CommerceOrder> findByGroupId(
		long groupId, int start, int end) {

		return findByGroupId(groupId, start, end, null, true);
	}

	/**
	 * Returns an ordered range of all the commerce orders where groupId = &#63;.
	 *
	 * <p>
	 * Useful when paginating results. Returns a maximum of <code>end - start</code> instances. <code>start</code> and <code>end</code> are not primary keys, they are indexes in the result set. Thus, <code>0</code> refers to the first result in the set. Setting both <code>start</code> and <code>end</code> to <code>com.liferay.portal.kernel.dao.orm.QueryUtil#ALL_POS</code> will return the full result set. If <code>orderByComparator</code> is specified, then the query will include the given ORDER BY logic. If <code>orderByComparator</code> is absent, then the query will include the default ORDER BY logic from <code>com.liferay.commerce.model.impl.CommerceOrderModelImpl</code>.
	 * </p>
	 *
	 * @param groupId the group ID
	 * @param start the lower bound of the range of commerce orders
	 * @param end the upper bound of the range of commerce orders (not inclusive)
	 * @param orderByComparator the comparator to order the results by (optionally <code>null</code>)
	 * @return the ordered range of matching commerce orders
	 */
	public default java.util.List<CommerceOrder> findByGroupId(
		long groupId, int start, int end,
		com.liferay.portal.kernel.util.OrderByComparator<CommerceOrder>
			orderByComparator) {

		return findByGroupId(groupId, start, end, orderByComparator, true);
	}

	/**
	 * Returns all the commerce orders that the user has permission to view where groupId = &#63;.
	 *
	 * @param groupId the group ID
	 * @return the matching commerce orders that the user has permission to view
	 */
	public default java.util.List<CommerceOrder> filterFindByGroupId(
		long groupId) {

		return filterFindByGroupId(
			groupId, com.liferay.portal.kernel.dao.orm.QueryUtil.ALL_POS,
			com.liferay.portal.kernel.dao.orm.QueryUtil.ALL_POS, null);
	}

	/**
	 * Returns a range of all the commerce orders that the user has permission to view where groupId = &#63;.
	 *
	 * <p>
	 * Useful when paginating results. Returns a maximum of <code>end - start</code> instances. <code>start</code> and <code>end</code> are not primary keys, they are indexes in the result set. Thus, <code>0</code> refers to the first result in the set. Setting both <code>start</code> and <code>end</code> to <code>com.liferay.portal.kernel.dao.orm.QueryUtil#ALL_POS</code> will return the full result set. If <code>orderByComparator</code> is specified, then the query will include the given ORDER BY logic. If <code>orderByComparator</code> is absent, then the query will include the default ORDER BY logic from <code>com.liferay.commerce.model.impl.CommerceOrderModelImpl</code>.
	 * </p>
	 *
	 * @param groupId the group ID
	 * @param start the lower bound of the range of commerce orders
	 * @param end the upper bound of the range of commerce orders (not inclusive)
	 * @return the range of matching commerce orders that the user has permission to view
	 */
	public default java.util.List<CommerceOrder> filterFindByGroupId(
		long groupId, int start, int end) {

		return filterFindByGroupId(groupId, start, end, null);
	}

	/**
	 * Returns all the commerce orders where userId = &#63;.
	 *
	 * @param userId the user ID
	 * @return the matching commerce orders
	 */
	public default java.util.List<CommerceOrder> findByUserId(long userId) {
		return findByUserId(
			userId, com.liferay.portal.kernel.dao.orm.QueryUtil.ALL_POS,
			com.liferay.portal.kernel.dao.orm.QueryUtil.ALL_POS, null, true);
	}

	/**
	 * Returns a range of all the commerce orders where userId = &#63;.
	 *
	 * <p>
	 * Useful when paginating results. Returns a maximum of <code>end - start</code> instances. <code>start</code> and <code>end</code> are not primary keys, they are indexes in the result set. Thus, <code>0</code> refers to the first result in the set. Setting both <code>start</code> and <code>end</code> to <code>com.liferay.portal.kernel.dao.orm.QueryUtil#ALL_POS</code> will return the full result set. If <code>orderByComparator</code> is specified, then the query will include the given ORDER BY logic. If <code>orderByComparator</code> is absent, then the query will include the default ORDER BY logic from <code>com.liferay.commerce.model.impl.CommerceOrderModelImpl</code>.
	 * </p>
	 *
	 * @param userId the user ID
	 * @param start the lower bound of the range of commerce orders
	 * @param end the upper bound of the range of commerce orders (not inclusive)
	 * @return the range of matching commerce orders
	 */
	public default java.util.List<CommerceOrder> findByUserId(
		long userId, int start, int end) {

		return findByUserId(userId, start, end, null, true);
	}

	/**
	 * Returns an ordered range of all the commerce orders where userId = &#63;.
	 *
	 * <p>
	 * Useful when paginating results. Returns a maximum of <code>end - start</code> instances. <code>start</code> and <code>end</code> are not primary keys, they are indexes in the result set. Thus, <code>0</code> refers to the first result in the set. Setting both <code>start</code> and <code>end</code> to <code>com.liferay.portal.kernel.dao.orm.QueryUtil#ALL_POS</code> will return the full result set. If <code>orderByComparator</code> is specified, then the query will include the given ORDER BY logic. If <code>orderByComparator</code> is absent, then the query will include the default ORDER BY logic from <code>com.liferay.commerce.model.impl.CommerceOrderModelImpl</code>.
	 * </p>
	 *
	 * @param userId the user ID
	 * @param start the lower bound of the range of commerce orders
	 * @param end the upper bound of the range of commerce orders (not inclusive)
	 * @param orderByComparator the comparator to order the results by (optionally <code>null</code>)
	 * @return the ordered range of matching commerce orders
	 */
	public default java.util.List<CommerceOrder> findByUserId(
		long userId, int start, int end,
		com.liferay.portal.kernel.util.OrderByComparator<CommerceOrder>
			orderByComparator) {

		return findByUserId(userId, start, end, orderByComparator, true);
	}

	/**
	 * Returns all the commerce orders where billingAddressId = &#63;.
	 *
	 * @param billingAddressId the billing address ID
	 * @return the matching commerce orders
	 */
	public default java.util.List<CommerceOrder> findByBillingAddressId(
		long billingAddressId) {

		return findByBillingAddressId(
			billingAddressId,
			com.liferay.portal.kernel.dao.orm.QueryUtil.ALL_POS,
			com.liferay.portal.kernel.dao.orm.QueryUtil.ALL_POS, null, true);
	}

	/**
	 * Returns a range of all the commerce orders where billingAddressId = &#63;.
	 *
	 * <p>
	 * Useful when paginating results. Returns a maximum of <code>end - start</code> instances. <code>start</code> and <code>end</code> are not primary keys, they are indexes in the result set. Thus, <code>0</code> refers to the first result in the set. Setting both <code>start</code> and <code>end</code> to <code>com.liferay.portal.kernel.dao.orm.QueryUtil#ALL_POS</code> will return the full result set. If <code>orderByComparator</code> is specified, then the query will include the given ORDER BY logic. If <code>orderByComparator</code> is absent, then the query will include the default ORDER BY logic from <code>com.liferay.commerce.model.impl.CommerceOrderModelImpl</code>.
	 * </p>
	 *
	 * @param billingAddressId the billing address ID
	 * @param start the lower bound of the range of commerce orders
	 * @param end the upper bound of the range of commerce orders (not inclusive)
	 * @return the range of matching commerce orders
	 */
	public default java.util.List<CommerceOrder> findByBillingAddressId(
		long billingAddressId, int start, int end) {

		return findByBillingAddressId(billingAddressId, start, end, null, true);
	}

	/**
	 * Returns an ordered range of all the commerce orders where billingAddressId = &#63;.
	 *
	 * <p>
	 * Useful when paginating results. Returns a maximum of <code>end - start</code> instances. <code>start</code> and <code>end</code> are not primary keys, they are indexes in the result set. Thus, <code>0</code> refers to the first result in the set. Setting both <code>start</code> and <code>end</code> to <code>com.liferay.portal.kernel.dao.orm.QueryUtil#ALL_POS</code> will return the full result set. If <code>orderByComparator</code> is specified, then the query will include the given ORDER BY logic. If <code>orderByComparator</code> is absent, then the query will include the default ORDER BY logic from <code>com.liferay.commerce.model.impl.CommerceOrderModelImpl</code>.
	 * </p>
	 *
	 * @param billingAddressId the billing address ID
	 * @param start the lower bound of the range of commerce orders
	 * @param end the upper bound of the range of commerce orders (not inclusive)
	 * @param orderByComparator the comparator to order the results by (optionally <code>null</code>)
	 * @return the ordered range of matching commerce orders
	 */
	public default java.util.List<CommerceOrder> findByBillingAddressId(
		long billingAddressId, int start, int end,
		com.liferay.portal.kernel.util.OrderByComparator<CommerceOrder>
			orderByComparator) {

		return findByBillingAddressId(
			billingAddressId, start, end, orderByComparator, true);
	}

	/**
	 * Returns all the commerce orders where commerceAccountId = &#63;.
	 *
	 * @param commerceAccountId the commerce account ID
	 * @return the matching commerce orders
	 */
	public default java.util.List<CommerceOrder> findByCommerceAccountId(
		long commerceAccountId) {

		return findByCommerceAccountId(
			commerceAccountId,
			com.liferay.portal.kernel.dao.orm.QueryUtil.ALL_POS,
			com.liferay.portal.kernel.dao.orm.QueryUtil.ALL_POS, null, true);
	}

	/**
	 * Returns a range of all the commerce orders where commerceAccountId = &#63;.
	 *
	 * <p>
	 * Useful when paginating results. Returns a maximum of <code>end - start</code> instances. <code>start</code> and <code>end</code> are not primary keys, they are indexes in the result set. Thus, <code>0</code> refers to the first result in the set. Setting both <code>start</code> and <code>end</code> to <code>com.liferay.portal.kernel.dao.orm.QueryUtil#ALL_POS</code> will return the full result set. If <code>orderByComparator</code> is specified, then the query will include the given ORDER BY logic. If <code>orderByComparator</code> is absent, then the query will include the default ORDER BY logic from <code>com.liferay.commerce.model.impl.CommerceOrderModelImpl</code>.
	 * </p>
	 *
	 * @param commerceAccountId the commerce account ID
	 * @param start the lower bound of the range of commerce orders
	 * @param end the upper bound of the range of commerce orders (not inclusive)
	 * @return the range of matching commerce orders
	 */
	public default java.util.List<CommerceOrder> findByCommerceAccountId(
		long commerceAccountId, int start, int end) {

		return findByCommerceAccountId(
			commerceAccountId, start, end, null, true);
	}

	/**
	 * Returns an ordered range of all the commerce orders where commerceAccountId = &#63;.
	 *
	 * <p>
	 * Useful when paginating results. Returns a maximum of <code>end - start</code> instances. <code>start</code> and <code>end</code> are not primary keys, they are indexes in the result set. Thus, <code>0</code> refers to the first result in the set. Setting both <code>start</code> and <code>end</code> to <code>com.liferay.portal.kernel.dao.orm.QueryUtil#ALL_POS</code> will return the full result set. If <code>orderByComparator</code> is specified, then the query will include the given ORDER BY logic. If <code>orderByComparator</code> is absent, then the query will include the default ORDER BY logic from <code>com.liferay.commerce.model.impl.CommerceOrderModelImpl</code>.
	 * </p>
	 *
	 * @param commerceAccountId the commerce account ID
	 * @param start the lower bound of the range of commerce orders
	 * @param end the upper bound of the range of commerce orders (not inclusive)
	 * @param orderByComparator the comparator to order the results by (optionally <code>null</code>)
	 * @return the ordered range of matching commerce orders
	 */
	public default java.util.List<CommerceOrder> findByCommerceAccountId(
		long commerceAccountId, int start, int end,
		com.liferay.portal.kernel.util.OrderByComparator<CommerceOrder>
			orderByComparator) {

		return findByCommerceAccountId(
			commerceAccountId, start, end, orderByComparator, true);
	}

	/**
	 * Returns all the commerce orders where shippingAddressId = &#63;.
	 *
	 * @param shippingAddressId the shipping address ID
	 * @return the matching commerce orders
	 */
	public default java.util.List<CommerceOrder> findByShippingAddressId(
		long shippingAddressId) {

		return findByShippingAddressId(
			shippingAddressId,
			com.liferay.portal.kernel.dao.orm.QueryUtil.ALL_POS,
			com.liferay.portal.kernel.dao.orm.QueryUtil.ALL_POS, null, true);
	}

	/**
	 * Returns a range of all the commerce orders where shippingAddressId = &#63;.
	 *
	 * <p>
	 * Useful when paginating results. Returns a maximum of <code>end - start</code> instances. <code>start</code> and <code>end</code> are not primary keys, they are indexes in the result set. Thus, <code>0</code> refers to the first result in the set. Setting both <code>start</code> and <code>end</code> to <code>com.liferay.portal.kernel.dao.orm.QueryUtil#ALL_POS</code> will return the full result set. If <code>orderByComparator</code> is specified, then the query will include the given ORDER BY logic. If <code>orderByComparator</code> is absent, then the query will include the default ORDER BY logic from <code>com.liferay.commerce.model.impl.CommerceOrderModelImpl</code>.
	 * </p>
	 *
	 * @param shippingAddressId the shipping address ID
	 * @param start the lower bound of the range of commerce orders
	 * @param end the upper bound of the range of commerce orders (not inclusive)
	 * @return the range of matching commerce orders
	 */
	public default java.util.List<CommerceOrder> findByShippingAddressId(
		long shippingAddressId, int start, int end) {

		return findByShippingAddressId(
			shippingAddressId, start, end, null, true);
	}

	/**
	 * Returns an ordered range of all the commerce orders where shippingAddressId = &#63;.
	 *
	 * <p>
	 * Useful when paginating results. Returns a maximum of <code>end - start</code> instances. <code>start</code> and <code>end</code> are not primary keys, they are indexes in the result set. Thus, <code>0</code> refers to the first result in the set. Setting both <code>start</code> and <code>end</code> to <code>com.liferay.portal.kernel.dao.orm.QueryUtil#ALL_POS</code> will return the full result set. If <code>orderByComparator</code> is specified, then the query will include the given ORDER BY logic. If <code>orderByComparator</code> is absent, then the query will include the default ORDER BY logic from <code>com.liferay.commerce.model.impl.CommerceOrderModelImpl</code>.
	 * </p>
	 *
	 * @param shippingAddressId the shipping address ID
	 * @param start the lower bound of the range of commerce orders
	 * @param end the upper bound of the range of commerce orders (not inclusive)
	 * @param orderByComparator the comparator to order the results by (optionally <code>null</code>)
	 * @return the ordered range of matching commerce orders
	 */
	public default java.util.List<CommerceOrder> findByShippingAddressId(
		long shippingAddressId, int start, int end,
		com.liferay.portal.kernel.util.OrderByComparator<CommerceOrder>
			orderByComparator) {

		return findByShippingAddressId(
			shippingAddressId, start, end, orderByComparator, true);
	}

	/**
	 * Returns all the commerce orders where groupId = &#63; and commerceAccountId = &#63;.
	 *
	 * @param groupId the group ID
	 * @param commerceAccountId the commerce account ID
	 * @return the matching commerce orders
	 */
	public default java.util.List<CommerceOrder> findByG_C(
		long groupId, long commerceAccountId) {

		return findByG_C(
			groupId, commerceAccountId,
			com.liferay.portal.kernel.dao.orm.QueryUtil.ALL_POS,
			com.liferay.portal.kernel.dao.orm.QueryUtil.ALL_POS, null, true);
	}

	/**
	 * Returns a range of all the commerce orders where groupId = &#63; and commerceAccountId = &#63;.
	 *
	 * <p>
	 * Useful when paginating results. Returns a maximum of <code>end - start</code> instances. <code>start</code> and <code>end</code> are not primary keys, they are indexes in the result set. Thus, <code>0</code> refers to the first result in the set. Setting both <code>start</code> and <code>end</code> to <code>com.liferay.portal.kernel.dao.orm.QueryUtil#ALL_POS</code> will return the full result set. If <code>orderByComparator</code> is specified, then the query will include the given ORDER BY logic. If <code>orderByComparator</code> is absent, then the query will include the default ORDER BY logic from <code>com.liferay.commerce.model.impl.CommerceOrderModelImpl</code>.
	 * </p>
	 *
	 * @param groupId the group ID
	 * @param commerceAccountId the commerce account ID
	 * @param start the lower bound of the range of commerce orders
	 * @param end the upper bound of the range of commerce orders (not inclusive)
	 * @return the range of matching commerce orders
	 */
	public default java.util.List<CommerceOrder> findByG_C(
		long groupId, long commerceAccountId, int start, int end) {

		return findByG_C(groupId, commerceAccountId, start, end, null, true);
	}

	/**
	 * Returns an ordered range of all the commerce orders where groupId = &#63; and commerceAccountId = &#63;.
	 *
	 * <p>
	 * Useful when paginating results. Returns a maximum of <code>end - start</code> instances. <code>start</code> and <code>end</code> are not primary keys, they are indexes in the result set. Thus, <code>0</code> refers to the first result in the set. Setting both <code>start</code> and <code>end</code> to <code>com.liferay.portal.kernel.dao.orm.QueryUtil#ALL_POS</code> will return the full result set. If <code>orderByComparator</code> is specified, then the query will include the given ORDER BY logic. If <code>orderByComparator</code> is absent, then the query will include the default ORDER BY logic from <code>com.liferay.commerce.model.impl.CommerceOrderModelImpl</code>.
	 * </p>
	 *
	 * @param groupId the group ID
	 * @param commerceAccountId the commerce account ID
	 * @param start the lower bound of the range of commerce orders
	 * @param end the upper bound of the range of commerce orders (not inclusive)
	 * @param orderByComparator the comparator to order the results by (optionally <code>null</code>)
	 * @return the ordered range of matching commerce orders
	 */
	public default java.util.List<CommerceOrder> findByG_C(
		long groupId, long commerceAccountId, int start, int end,
		com.liferay.portal.kernel.util.OrderByComparator<CommerceOrder>
			orderByComparator) {

		return findByG_C(
			groupId, commerceAccountId, start, end, orderByComparator, true);
	}

	/**
	 * Returns all the commerce orders that the user has permission to view where groupId = &#63; and commerceAccountId = &#63;.
	 *
	 * @param groupId the group ID
	 * @param commerceAccountId the commerce account ID
	 * @return the matching commerce orders that the user has permission to view
	 */
	public default java.util.List<CommerceOrder> filterFindByG_C(
		long groupId, long commerceAccountId) {

		return filterFindByG_C(
			groupId, commerceAccountId,
			com.liferay.portal.kernel.dao.orm.QueryUtil.ALL_POS,
			com.liferay.portal.kernel.dao.orm.QueryUtil.ALL_POS, null);
	}

	/**
	 * Returns a range of all the commerce orders that the user has permission to view where groupId = &#63; and commerceAccountId = &#63;.
	 *
	 * <p>
	 * Useful when paginating results. Returns a maximum of <code>end - start</code> instances. <code>start</code> and <code>end</code> are not primary keys, they are indexes in the result set. Thus, <code>0</code> refers to the first result in the set. Setting both <code>start</code> and <code>end</code> to <code>com.liferay.portal.kernel.dao.orm.QueryUtil#ALL_POS</code> will return the full result set. If <code>orderByComparator</code> is specified, then the query will include the given ORDER BY logic. If <code>orderByComparator</code> is absent, then the query will include the default ORDER BY logic from <code>com.liferay.commerce.model.impl.CommerceOrderModelImpl</code>.
	 * </p>
	 *
	 * @param groupId the group ID
	 * @param commerceAccountId the commerce account ID
	 * @param start the lower bound of the range of commerce orders
	 * @param end the upper bound of the range of commerce orders (not inclusive)
	 * @return the range of matching commerce orders that the user has permission to view
	 */
	public default java.util.List<CommerceOrder> filterFindByG_C(
		long groupId, long commerceAccountId, int start, int end) {

		return filterFindByG_C(groupId, commerceAccountId, start, end, null);
	}

	/**
	 * Returns all the commerce orders where groupId = &#63; and userId = &#63; and orderStatus = &#63;.
	 *
	 * @param groupId the group ID
	 * @param userId the user ID
	 * @param orderStatus the order status
	 * @return the matching commerce orders
	 */
	public default java.util.List<CommerceOrder> findByG_U_O(
		long groupId, long userId, int orderStatus) {

		return findByG_U_O(
			groupId, userId, orderStatus,
			com.liferay.portal.kernel.dao.orm.QueryUtil.ALL_POS,
			com.liferay.portal.kernel.dao.orm.QueryUtil.ALL_POS, null, true);
	}

	/**
	 * Returns a range of all the commerce orders where groupId = &#63; and userId = &#63; and orderStatus = &#63;.
	 *
	 * <p>
	 * Useful when paginating results. Returns a maximum of <code>end - start</code> instances. <code>start</code> and <code>end</code> are not primary keys, they are indexes in the result set. Thus, <code>0</code> refers to the first result in the set. Setting both <code>start</code> and <code>end</code> to <code>com.liferay.portal.kernel.dao.orm.QueryUtil#ALL_POS</code> will return the full result set. If <code>orderByComparator</code> is specified, then the query will include the given ORDER BY logic. If <code>orderByComparator</code> is absent, then the query will include the default ORDER BY logic from <code>com.liferay.commerce.model.impl.CommerceOrderModelImpl</code>.
	 * </p>
	 *
	 * @param groupId the group ID
	 * @param userId the user ID
	 * @param orderStatus the order status
	 * @param start the lower bound of the range of commerce orders
	 * @param end the upper bound of the range of commerce orders (not inclusive)
	 * @return the range of matching commerce orders
	 */
	public default java.util.List<CommerceOrder> findByG_U_O(
		long groupId, long userId, int orderStatus, int start, int end) {

		return findByG_U_O(
			groupId, userId, orderStatus, start, end, null, true);
	}

	/**
	 * Returns an ordered range of all the commerce orders where groupId = &#63; and userId = &#63; and orderStatus = &#63;.
	 *
	 * <p>
	 * Useful when paginating results. Returns a maximum of <code>end - start</code> instances. <code>start</code> and <code>end</code> are not primary keys, they are indexes in the result set. Thus, <code>0</code> refers to the first result in the set. Setting both <code>start</code> and <code>end</code> to <code>com.liferay.portal.kernel.dao.orm.QueryUtil#ALL_POS</code> will return the full result set. If <code>orderByComparator</code> is specified, then the query will include the given ORDER BY logic. If <code>orderByComparator</code> is absent, then the query will include the default ORDER BY logic from <code>com.liferay.commerce.model.impl.CommerceOrderModelImpl</code>.
	 * </p>
	 *
	 * @param groupId the group ID
	 * @param userId the user ID
	 * @param orderStatus the order status
	 * @param start the lower bound of the range of commerce orders
	 * @param end the upper bound of the range of commerce orders (not inclusive)
	 * @param orderByComparator the comparator to order the results by (optionally <code>null</code>)
	 * @return the ordered range of matching commerce orders
	 */
	public default java.util.List<CommerceOrder> findByG_U_O(
		long groupId, long userId, int orderStatus, int start, int end,
		com.liferay.portal.kernel.util.OrderByComparator<CommerceOrder>
			orderByComparator) {

		return findByG_U_O(
			groupId, userId, orderStatus, start, end, orderByComparator, true);
	}

	/**
	 * Returns all the commerce orders that the user has permission to view where groupId = &#63; and userId = &#63; and orderStatus = &#63;.
	 *
	 * @param groupId the group ID
	 * @param userId the user ID
	 * @param orderStatus the order status
	 * @return the matching commerce orders that the user has permission to view
	 */
	public default java.util.List<CommerceOrder> filterFindByG_U_O(
		long groupId, long userId, int orderStatus) {

		return filterFindByG_U_O(
			groupId, userId, orderStatus,
			com.liferay.portal.kernel.dao.orm.QueryUtil.ALL_POS,
			com.liferay.portal.kernel.dao.orm.QueryUtil.ALL_POS, null);
	}

	/**
	 * Returns a range of all the commerce orders that the user has permission to view where groupId = &#63; and userId = &#63; and orderStatus = &#63;.
	 *
	 * <p>
	 * Useful when paginating results. Returns a maximum of <code>end - start</code> instances. <code>start</code> and <code>end</code> are not primary keys, they are indexes in the result set. Thus, <code>0</code> refers to the first result in the set. Setting both <code>start</code> and <code>end</code> to <code>com.liferay.portal.kernel.dao.orm.QueryUtil#ALL_POS</code> will return the full result set. If <code>orderByComparator</code> is specified, then the query will include the given ORDER BY logic. If <code>orderByComparator</code> is absent, then the query will include the default ORDER BY logic from <code>com.liferay.commerce.model.impl.CommerceOrderModelImpl</code>.
	 * </p>
	 *
	 * @param groupId the group ID
	 * @param userId the user ID
	 * @param orderStatus the order status
	 * @param start the lower bound of the range of commerce orders
	 * @param end the upper bound of the range of commerce orders (not inclusive)
	 * @return the range of matching commerce orders that the user has permission to view
	 */
	public default java.util.List<CommerceOrder> filterFindByG_U_O(
		long groupId, long userId, int orderStatus, int start, int end) {

		return filterFindByG_U_O(
			groupId, userId, orderStatus, start, end, null);
	}

	/**
	 * Returns all the commerce orders where groupId = &#63; and commerceAccountId = &#63; and orderStatus = &#63;.
	 *
	 * @param groupId the group ID
	 * @param commerceAccountId the commerce account ID
	 * @param orderStatus the order status
	 * @return the matching commerce orders
	 */
	public default java.util.List<CommerceOrder> findByG_C_O(
		long groupId, long commerceAccountId, int orderStatus) {

		return findByG_C_O(
			groupId, commerceAccountId, orderStatus,
			com.liferay.portal.kernel.dao.orm.QueryUtil.ALL_POS,
			com.liferay.portal.kernel.dao.orm.QueryUtil.ALL_POS, null, true);
	}

	/**
	 * Returns a range of all the commerce orders where groupId = &#63; and commerceAccountId = &#63; and orderStatus = &#63;.
	 *
	 * <p>
	 * Useful when paginating results. Returns a maximum of <code>end - start</code> instances. <code>start</code> and <code>end</code> are not primary keys, they are indexes in the result set. Thus, <code>0</code> refers to the first result in the set. Setting both <code>start</code> and <code>end</code> to <code>com.liferay.portal.kernel.dao.orm.QueryUtil#ALL_POS</code> will return the full result set. If <code>orderByComparator</code> is specified, then the query will include the given ORDER BY logic. If <code>orderByComparator</code> is absent, then the query will include the default ORDER BY logic from <code>com.liferay.commerce.model.impl.CommerceOrderModelImpl</code>.
	 * </p>
	 *
	 * @param groupId the group ID
	 * @param commerceAccountId the commerce account ID
	 * @param orderStatus the order status
	 * @param start the lower bound of the range of commerce orders
	 * @param end the upper bound of the range of commerce orders (not inclusive)
	 * @return the range of matching commerce orders
	 */
	public default java.util.List<CommerceOrder> findByG_C_O(
		long groupId, long commerceAccountId, int orderStatus, int start,
		int end) {

		return findByG_C_O(
			groupId, commerceAccountId, orderStatus, start, end, null, true);
	}

	/**
	 * Returns an ordered range of all the commerce orders where groupId = &#63; and commerceAccountId = &#63; and orderStatus = &#63;.
	 *
	 * <p>
	 * Useful when paginating results. Returns a maximum of <code>end - start</code> instances. <code>start</code> and <code>end</code> are not primary keys, they are indexes in the result set. Thus, <code>0</code> refers to the first result in the set. Setting both <code>start</code> and <code>end</code> to <code>com.liferay.portal.kernel.dao.orm.QueryUtil#ALL_POS</code> will return the full result set. If <code>orderByComparator</code> is specified, then the query will include the given ORDER BY logic. If <code>orderByComparator</code> is absent, then the query will include the default ORDER BY logic from <code>com.liferay.commerce.model.impl.CommerceOrderModelImpl</code>.
	 * </p>
	 *
	 * @param groupId the group ID
	 * @param commerceAccountId the commerce account ID
	 * @param orderStatus the order status
	 * @param start the lower bound of the range of commerce orders
	 * @param end the upper bound of the range of commerce orders (not inclusive)
	 * @param orderByComparator the comparator to order the results by (optionally <code>null</code>)
	 * @return the ordered range of matching commerce orders
	 */
	public default java.util.List<CommerceOrder> findByG_C_O(
		long groupId, long commerceAccountId, int orderStatus, int start,
		int end,
		com.liferay.portal.kernel.util.OrderByComparator<CommerceOrder>
			orderByComparator) {

		return findByG_C_O(
			groupId, commerceAccountId, orderStatus, start, end,
			orderByComparator, true);
	}

	/**
	 * Returns all the commerce orders that the user has permission to view where groupId = &#63; and commerceAccountId = &#63; and orderStatus = &#63;.
	 *
	 * @param groupId the group ID
	 * @param commerceAccountId the commerce account ID
	 * @param orderStatus the order status
	 * @return the matching commerce orders that the user has permission to view
	 */
	public default java.util.List<CommerceOrder> filterFindByG_C_O(
		long groupId, long commerceAccountId, int orderStatus) {

		return filterFindByG_C_O(
			groupId, commerceAccountId, orderStatus,
			com.liferay.portal.kernel.dao.orm.QueryUtil.ALL_POS,
			com.liferay.portal.kernel.dao.orm.QueryUtil.ALL_POS, null);
	}

	/**
	 * Returns a range of all the commerce orders that the user has permission to view where groupId = &#63; and commerceAccountId = &#63; and orderStatus = &#63;.
	 *
	 * <p>
	 * Useful when paginating results. Returns a maximum of <code>end - start</code> instances. <code>start</code> and <code>end</code> are not primary keys, they are indexes in the result set. Thus, <code>0</code> refers to the first result in the set. Setting both <code>start</code> and <code>end</code> to <code>com.liferay.portal.kernel.dao.orm.QueryUtil#ALL_POS</code> will return the full result set. If <code>orderByComparator</code> is specified, then the query will include the given ORDER BY logic. If <code>orderByComparator</code> is absent, then the query will include the default ORDER BY logic from <code>com.liferay.commerce.model.impl.CommerceOrderModelImpl</code>.
	 * </p>
	 *
	 * @param groupId the group ID
	 * @param commerceAccountId the commerce account ID
	 * @param orderStatus the order status
	 * @param start the lower bound of the range of commerce orders
	 * @param end the upper bound of the range of commerce orders (not inclusive)
	 * @return the range of matching commerce orders that the user has permission to view
	 */
	public default java.util.List<CommerceOrder> filterFindByG_C_O(
		long groupId, long commerceAccountId, int orderStatus, int start,
		int end) {

		return filterFindByG_C_O(
			groupId, commerceAccountId, orderStatus, start, end, null);
	}

}
// LIFERAY-SERVICE-BUILDER-HASH:-1993732953