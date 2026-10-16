/**
 * SPDX-FileCopyrightText: (c) 2000 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

package com.liferay.commerce.inventory.service.persistence;

import com.liferay.commerce.inventory.exception.NoSuchInventoryReplenishmentItemException;
import com.liferay.commerce.inventory.model.CommerceInventoryReplenishmentItem;
import com.liferay.portal.kernel.service.persistence.BasePersistence;

import java.util.Date;

import org.osgi.annotation.versioning.ProviderType;

/**
 * The persistence interface for the commerce inventory replenishment item service.
 *
 * <p>
 * Caching information and settings can be found in <code>portal.properties</code>
 * </p>
 *
 * @author Luca Pellizzon
 * @see CommerceInventoryReplenishmentItemUtil
 * @generated
 */
@ProviderType
public interface CommerceInventoryReplenishmentItemPersistence
	extends BasePersistence<CommerceInventoryReplenishmentItem> {

	/*
	 * NOTE FOR DEVELOPERS:
	 *
	 * Never modify or reference this interface directly. Always use {@link CommerceInventoryReplenishmentItemUtil} to access the commerce inventory replenishment item persistence. Modify <code>service.xml</code> and rerun ServiceBuilder to regenerate this interface.
	 */

	/**
	 * Returns an ordered range of all the commerce inventory replenishment items where uuid = &#63;.
	 *
	 * <p>
	 * Useful when paginating results. Returns a maximum of <code>end - start</code> instances. <code>start</code> and <code>end</code> are not primary keys, they are indexes in the result set. Thus, <code>0</code> refers to the first result in the set. Setting both <code>start</code> and <code>end</code> to <code>com.liferay.portal.kernel.dao.orm.QueryUtil#ALL_POS</code> will return the full result set. If <code>orderByComparator</code> is specified, then the query will include the given ORDER BY logic. If <code>orderByComparator</code> is absent, then the query will include the default ORDER BY logic from <code>com.liferay.commerce.inventory.model.impl.CommerceInventoryReplenishmentItemModelImpl</code>.
	 * </p>
	 *
	 * @param uuid the uuid
	 * @param start the lower bound of the range of commerce inventory replenishment items
	 * @param end the upper bound of the range of commerce inventory replenishment items (not inclusive)
	 * @param orderByComparator the comparator to order the results by (optionally <code>null</code>)
	 * @param useFinderCache whether to use the finder cache
	 * @return the ordered range of matching commerce inventory replenishment items
	 */
	public java.util.List<CommerceInventoryReplenishmentItem> findByUuid(
		String uuid, int start, int end,
		com.liferay.portal.kernel.util.OrderByComparator
			<CommerceInventoryReplenishmentItem> orderByComparator,
		boolean useFinderCache);

	/**
	 * Returns the first commerce inventory replenishment item in the ordered set where uuid = &#63;.
	 *
	 * @param uuid the uuid
	 * @param orderByComparator the comparator to order the set by (optionally <code>null</code>)
	 * @return the first matching commerce inventory replenishment item
	 * @throws NoSuchInventoryReplenishmentItemException if a matching commerce inventory replenishment item could not be found
	 */
	public CommerceInventoryReplenishmentItem findByUuid_First(
			String uuid,
			com.liferay.portal.kernel.util.OrderByComparator
				<CommerceInventoryReplenishmentItem> orderByComparator)
		throws NoSuchInventoryReplenishmentItemException;

	/**
	 * Returns the first commerce inventory replenishment item in the ordered set where uuid = &#63;.
	 *
	 * @param uuid the uuid
	 * @param orderByComparator the comparator to order the set by (optionally <code>null</code>)
	 * @return the first matching commerce inventory replenishment item, or <code>null</code> if a matching commerce inventory replenishment item could not be found
	 */
	public CommerceInventoryReplenishmentItem fetchByUuid_First(
		String uuid,
		com.liferay.portal.kernel.util.OrderByComparator
			<CommerceInventoryReplenishmentItem> orderByComparator);

	/**
	 * Removes all the commerce inventory replenishment items where uuid = &#63; from the database.
	 *
	 * @param uuid the uuid
	 */
	public void removeByUuid(String uuid);

	/**
	 * Returns the number of commerce inventory replenishment items where uuid = &#63;.
	 *
	 * @param uuid the uuid
	 * @return the number of matching commerce inventory replenishment items
	 */
	public int countByUuid(String uuid);

	/**
	 * Returns an ordered range of all the commerce inventory replenishment items where uuid = &#63; and companyId = &#63;.
	 *
	 * <p>
	 * Useful when paginating results. Returns a maximum of <code>end - start</code> instances. <code>start</code> and <code>end</code> are not primary keys, they are indexes in the result set. Thus, <code>0</code> refers to the first result in the set. Setting both <code>start</code> and <code>end</code> to <code>com.liferay.portal.kernel.dao.orm.QueryUtil#ALL_POS</code> will return the full result set. If <code>orderByComparator</code> is specified, then the query will include the given ORDER BY logic. If <code>orderByComparator</code> is absent, then the query will include the default ORDER BY logic from <code>com.liferay.commerce.inventory.model.impl.CommerceInventoryReplenishmentItemModelImpl</code>.
	 * </p>
	 *
	 * @param uuid the uuid
	 * @param companyId the company ID
	 * @param start the lower bound of the range of commerce inventory replenishment items
	 * @param end the upper bound of the range of commerce inventory replenishment items (not inclusive)
	 * @param orderByComparator the comparator to order the results by (optionally <code>null</code>)
	 * @param useFinderCache whether to use the finder cache
	 * @return the ordered range of matching commerce inventory replenishment items
	 */
	public java.util.List<CommerceInventoryReplenishmentItem> findByUuid_C(
		String uuid, long companyId, int start, int end,
		com.liferay.portal.kernel.util.OrderByComparator
			<CommerceInventoryReplenishmentItem> orderByComparator,
		boolean useFinderCache);

	/**
	 * Returns the first commerce inventory replenishment item in the ordered set where uuid = &#63; and companyId = &#63;.
	 *
	 * @param uuid the uuid
	 * @param companyId the company ID
	 * @param orderByComparator the comparator to order the set by (optionally <code>null</code>)
	 * @return the first matching commerce inventory replenishment item
	 * @throws NoSuchInventoryReplenishmentItemException if a matching commerce inventory replenishment item could not be found
	 */
	public CommerceInventoryReplenishmentItem findByUuid_C_First(
			String uuid, long companyId,
			com.liferay.portal.kernel.util.OrderByComparator
				<CommerceInventoryReplenishmentItem> orderByComparator)
		throws NoSuchInventoryReplenishmentItemException;

	/**
	 * Returns the first commerce inventory replenishment item in the ordered set where uuid = &#63; and companyId = &#63;.
	 *
	 * @param uuid the uuid
	 * @param companyId the company ID
	 * @param orderByComparator the comparator to order the set by (optionally <code>null</code>)
	 * @return the first matching commerce inventory replenishment item, or <code>null</code> if a matching commerce inventory replenishment item could not be found
	 */
	public CommerceInventoryReplenishmentItem fetchByUuid_C_First(
		String uuid, long companyId,
		com.liferay.portal.kernel.util.OrderByComparator
			<CommerceInventoryReplenishmentItem> orderByComparator);

	/**
	 * Removes all the commerce inventory replenishment items where uuid = &#63; and companyId = &#63; from the database.
	 *
	 * @param uuid the uuid
	 * @param companyId the company ID
	 */
	public void removeByUuid_C(String uuid, long companyId);

	/**
	 * Returns the number of commerce inventory replenishment items where uuid = &#63; and companyId = &#63;.
	 *
	 * @param uuid the uuid
	 * @param companyId the company ID
	 * @return the number of matching commerce inventory replenishment items
	 */
	public int countByUuid_C(String uuid, long companyId);

	/**
	 * Returns an ordered range of all the commerce inventory replenishment items where commerceInventoryWarehouseId = &#63;.
	 *
	 * <p>
	 * Useful when paginating results. Returns a maximum of <code>end - start</code> instances. <code>start</code> and <code>end</code> are not primary keys, they are indexes in the result set. Thus, <code>0</code> refers to the first result in the set. Setting both <code>start</code> and <code>end</code> to <code>com.liferay.portal.kernel.dao.orm.QueryUtil#ALL_POS</code> will return the full result set. If <code>orderByComparator</code> is specified, then the query will include the given ORDER BY logic. If <code>orderByComparator</code> is absent, then the query will include the default ORDER BY logic from <code>com.liferay.commerce.inventory.model.impl.CommerceInventoryReplenishmentItemModelImpl</code>.
	 * </p>
	 *
	 * @param commerceInventoryWarehouseId the commerce inventory warehouse ID
	 * @param start the lower bound of the range of commerce inventory replenishment items
	 * @param end the upper bound of the range of commerce inventory replenishment items (not inclusive)
	 * @param orderByComparator the comparator to order the results by (optionally <code>null</code>)
	 * @param useFinderCache whether to use the finder cache
	 * @return the ordered range of matching commerce inventory replenishment items
	 */
	public java.util.List<CommerceInventoryReplenishmentItem>
		findByCommerceInventoryWarehouseId(
			long commerceInventoryWarehouseId, int start, int end,
			com.liferay.portal.kernel.util.OrderByComparator
				<CommerceInventoryReplenishmentItem> orderByComparator,
			boolean useFinderCache);

	/**
	 * Returns the first commerce inventory replenishment item in the ordered set where commerceInventoryWarehouseId = &#63;.
	 *
	 * @param commerceInventoryWarehouseId the commerce inventory warehouse ID
	 * @param orderByComparator the comparator to order the set by (optionally <code>null</code>)
	 * @return the first matching commerce inventory replenishment item
	 * @throws NoSuchInventoryReplenishmentItemException if a matching commerce inventory replenishment item could not be found
	 */
	public CommerceInventoryReplenishmentItem
			findByCommerceInventoryWarehouseId_First(
				long commerceInventoryWarehouseId,
				com.liferay.portal.kernel.util.OrderByComparator
					<CommerceInventoryReplenishmentItem> orderByComparator)
		throws NoSuchInventoryReplenishmentItemException;

	/**
	 * Returns the first commerce inventory replenishment item in the ordered set where commerceInventoryWarehouseId = &#63;.
	 *
	 * @param commerceInventoryWarehouseId the commerce inventory warehouse ID
	 * @param orderByComparator the comparator to order the set by (optionally <code>null</code>)
	 * @return the first matching commerce inventory replenishment item, or <code>null</code> if a matching commerce inventory replenishment item could not be found
	 */
	public CommerceInventoryReplenishmentItem
		fetchByCommerceInventoryWarehouseId_First(
			long commerceInventoryWarehouseId,
			com.liferay.portal.kernel.util.OrderByComparator
				<CommerceInventoryReplenishmentItem> orderByComparator);

	/**
	 * Removes all the commerce inventory replenishment items where commerceInventoryWarehouseId = &#63; from the database.
	 *
	 * @param commerceInventoryWarehouseId the commerce inventory warehouse ID
	 */
	public void removeByCommerceInventoryWarehouseId(
		long commerceInventoryWarehouseId);

	/**
	 * Returns the number of commerce inventory replenishment items where commerceInventoryWarehouseId = &#63;.
	 *
	 * @param commerceInventoryWarehouseId the commerce inventory warehouse ID
	 * @return the number of matching commerce inventory replenishment items
	 */
	public int countByCommerceInventoryWarehouseId(
		long commerceInventoryWarehouseId);

	/**
	 * Returns an ordered range of all the commerce inventory replenishment items where sku = &#63;.
	 *
	 * <p>
	 * Useful when paginating results. Returns a maximum of <code>end - start</code> instances. <code>start</code> and <code>end</code> are not primary keys, they are indexes in the result set. Thus, <code>0</code> refers to the first result in the set. Setting both <code>start</code> and <code>end</code> to <code>com.liferay.portal.kernel.dao.orm.QueryUtil#ALL_POS</code> will return the full result set. If <code>orderByComparator</code> is specified, then the query will include the given ORDER BY logic. If <code>orderByComparator</code> is absent, then the query will include the default ORDER BY logic from <code>com.liferay.commerce.inventory.model.impl.CommerceInventoryReplenishmentItemModelImpl</code>.
	 * </p>
	 *
	 * @param sku the sku
	 * @param start the lower bound of the range of commerce inventory replenishment items
	 * @param end the upper bound of the range of commerce inventory replenishment items (not inclusive)
	 * @param orderByComparator the comparator to order the results by (optionally <code>null</code>)
	 * @param useFinderCache whether to use the finder cache
	 * @return the ordered range of matching commerce inventory replenishment items
	 */
	public java.util.List<CommerceInventoryReplenishmentItem> findBySku(
		String sku, int start, int end,
		com.liferay.portal.kernel.util.OrderByComparator
			<CommerceInventoryReplenishmentItem> orderByComparator,
		boolean useFinderCache);

	/**
	 * Returns the first commerce inventory replenishment item in the ordered set where sku = &#63;.
	 *
	 * @param sku the sku
	 * @param orderByComparator the comparator to order the set by (optionally <code>null</code>)
	 * @return the first matching commerce inventory replenishment item
	 * @throws NoSuchInventoryReplenishmentItemException if a matching commerce inventory replenishment item could not be found
	 */
	public CommerceInventoryReplenishmentItem findBySku_First(
			String sku,
			com.liferay.portal.kernel.util.OrderByComparator
				<CommerceInventoryReplenishmentItem> orderByComparator)
		throws NoSuchInventoryReplenishmentItemException;

	/**
	 * Returns the first commerce inventory replenishment item in the ordered set where sku = &#63;.
	 *
	 * @param sku the sku
	 * @param orderByComparator the comparator to order the set by (optionally <code>null</code>)
	 * @return the first matching commerce inventory replenishment item, or <code>null</code> if a matching commerce inventory replenishment item could not be found
	 */
	public CommerceInventoryReplenishmentItem fetchBySku_First(
		String sku,
		com.liferay.portal.kernel.util.OrderByComparator
			<CommerceInventoryReplenishmentItem> orderByComparator);

	/**
	 * Removes all the commerce inventory replenishment items where sku = &#63; from the database.
	 *
	 * @param sku the sku
	 */
	public void removeBySku(String sku);

	/**
	 * Returns the number of commerce inventory replenishment items where sku = &#63;.
	 *
	 * @param sku the sku
	 * @return the number of matching commerce inventory replenishment items
	 */
	public int countBySku(String sku);

	/**
	 * Returns an ordered range of all the commerce inventory replenishment items where companyId = &#63; and sku = &#63; and unitOfMeasureKey = &#63;.
	 *
	 * <p>
	 * Useful when paginating results. Returns a maximum of <code>end - start</code> instances. <code>start</code> and <code>end</code> are not primary keys, they are indexes in the result set. Thus, <code>0</code> refers to the first result in the set. Setting both <code>start</code> and <code>end</code> to <code>com.liferay.portal.kernel.dao.orm.QueryUtil#ALL_POS</code> will return the full result set. If <code>orderByComparator</code> is specified, then the query will include the given ORDER BY logic. If <code>orderByComparator</code> is absent, then the query will include the default ORDER BY logic from <code>com.liferay.commerce.inventory.model.impl.CommerceInventoryReplenishmentItemModelImpl</code>.
	 * </p>
	 *
	 * @param companyId the company ID
	 * @param sku the sku
	 * @param unitOfMeasureKey the unit of measure key
	 * @param start the lower bound of the range of commerce inventory replenishment items
	 * @param end the upper bound of the range of commerce inventory replenishment items (not inclusive)
	 * @param orderByComparator the comparator to order the results by (optionally <code>null</code>)
	 * @param useFinderCache whether to use the finder cache
	 * @return the ordered range of matching commerce inventory replenishment items
	 */
	public java.util.List<CommerceInventoryReplenishmentItem> findByC_S_U(
		long companyId, String sku, String unitOfMeasureKey, int start, int end,
		com.liferay.portal.kernel.util.OrderByComparator
			<CommerceInventoryReplenishmentItem> orderByComparator,
		boolean useFinderCache);

	/**
	 * Returns the first commerce inventory replenishment item in the ordered set where companyId = &#63; and sku = &#63; and unitOfMeasureKey = &#63;.
	 *
	 * @param companyId the company ID
	 * @param sku the sku
	 * @param unitOfMeasureKey the unit of measure key
	 * @param orderByComparator the comparator to order the set by (optionally <code>null</code>)
	 * @return the first matching commerce inventory replenishment item
	 * @throws NoSuchInventoryReplenishmentItemException if a matching commerce inventory replenishment item could not be found
	 */
	public CommerceInventoryReplenishmentItem findByC_S_U_First(
			long companyId, String sku, String unitOfMeasureKey,
			com.liferay.portal.kernel.util.OrderByComparator
				<CommerceInventoryReplenishmentItem> orderByComparator)
		throws NoSuchInventoryReplenishmentItemException;

	/**
	 * Returns the first commerce inventory replenishment item in the ordered set where companyId = &#63; and sku = &#63; and unitOfMeasureKey = &#63;.
	 *
	 * @param companyId the company ID
	 * @param sku the sku
	 * @param unitOfMeasureKey the unit of measure key
	 * @param orderByComparator the comparator to order the set by (optionally <code>null</code>)
	 * @return the first matching commerce inventory replenishment item, or <code>null</code> if a matching commerce inventory replenishment item could not be found
	 */
	public CommerceInventoryReplenishmentItem fetchByC_S_U_First(
		long companyId, String sku, String unitOfMeasureKey,
		com.liferay.portal.kernel.util.OrderByComparator
			<CommerceInventoryReplenishmentItem> orderByComparator);

	/**
	 * Removes all the commerce inventory replenishment items where companyId = &#63; and sku = &#63; and unitOfMeasureKey = &#63; from the database.
	 *
	 * @param companyId the company ID
	 * @param sku the sku
	 * @param unitOfMeasureKey the unit of measure key
	 */
	public void removeByC_S_U(
		long companyId, String sku, String unitOfMeasureKey);

	/**
	 * Returns the number of commerce inventory replenishment items where companyId = &#63; and sku = &#63; and unitOfMeasureKey = &#63;.
	 *
	 * @param companyId the company ID
	 * @param sku the sku
	 * @param unitOfMeasureKey the unit of measure key
	 * @return the number of matching commerce inventory replenishment items
	 */
	public int countByC_S_U(
		long companyId, String sku, String unitOfMeasureKey);

	/**
	 * Returns an ordered range of all the commerce inventory replenishment items where availabilityDate = &#63; and sku = &#63; and unitOfMeasureKey = &#63;.
	 *
	 * <p>
	 * Useful when paginating results. Returns a maximum of <code>end - start</code> instances. <code>start</code> and <code>end</code> are not primary keys, they are indexes in the result set. Thus, <code>0</code> refers to the first result in the set. Setting both <code>start</code> and <code>end</code> to <code>com.liferay.portal.kernel.dao.orm.QueryUtil#ALL_POS</code> will return the full result set. If <code>orderByComparator</code> is specified, then the query will include the given ORDER BY logic. If <code>orderByComparator</code> is absent, then the query will include the default ORDER BY logic from <code>com.liferay.commerce.inventory.model.impl.CommerceInventoryReplenishmentItemModelImpl</code>.
	 * </p>
	 *
	 * @param availabilityDate the availability date
	 * @param sku the sku
	 * @param unitOfMeasureKey the unit of measure key
	 * @param start the lower bound of the range of commerce inventory replenishment items
	 * @param end the upper bound of the range of commerce inventory replenishment items (not inclusive)
	 * @param orderByComparator the comparator to order the results by (optionally <code>null</code>)
	 * @param useFinderCache whether to use the finder cache
	 * @return the ordered range of matching commerce inventory replenishment items
	 */
	public java.util.List<CommerceInventoryReplenishmentItem> findByAD_S_U(
		Date availabilityDate, String sku, String unitOfMeasureKey, int start,
		int end,
		com.liferay.portal.kernel.util.OrderByComparator
			<CommerceInventoryReplenishmentItem> orderByComparator,
		boolean useFinderCache);

	/**
	 * Returns the first commerce inventory replenishment item in the ordered set where availabilityDate = &#63; and sku = &#63; and unitOfMeasureKey = &#63;.
	 *
	 * @param availabilityDate the availability date
	 * @param sku the sku
	 * @param unitOfMeasureKey the unit of measure key
	 * @param orderByComparator the comparator to order the set by (optionally <code>null</code>)
	 * @return the first matching commerce inventory replenishment item
	 * @throws NoSuchInventoryReplenishmentItemException if a matching commerce inventory replenishment item could not be found
	 */
	public CommerceInventoryReplenishmentItem findByAD_S_U_First(
			Date availabilityDate, String sku, String unitOfMeasureKey,
			com.liferay.portal.kernel.util.OrderByComparator
				<CommerceInventoryReplenishmentItem> orderByComparator)
		throws NoSuchInventoryReplenishmentItemException;

	/**
	 * Returns the first commerce inventory replenishment item in the ordered set where availabilityDate = &#63; and sku = &#63; and unitOfMeasureKey = &#63;.
	 *
	 * @param availabilityDate the availability date
	 * @param sku the sku
	 * @param unitOfMeasureKey the unit of measure key
	 * @param orderByComparator the comparator to order the set by (optionally <code>null</code>)
	 * @return the first matching commerce inventory replenishment item, or <code>null</code> if a matching commerce inventory replenishment item could not be found
	 */
	public CommerceInventoryReplenishmentItem fetchByAD_S_U_First(
		Date availabilityDate, String sku, String unitOfMeasureKey,
		com.liferay.portal.kernel.util.OrderByComparator
			<CommerceInventoryReplenishmentItem> orderByComparator);

	/**
	 * Removes all the commerce inventory replenishment items where availabilityDate = &#63; and sku = &#63; and unitOfMeasureKey = &#63; from the database.
	 *
	 * @param availabilityDate the availability date
	 * @param sku the sku
	 * @param unitOfMeasureKey the unit of measure key
	 */
	public void removeByAD_S_U(
		Date availabilityDate, String sku, String unitOfMeasureKey);

	/**
	 * Returns the number of commerce inventory replenishment items where availabilityDate = &#63; and sku = &#63; and unitOfMeasureKey = &#63;.
	 *
	 * @param availabilityDate the availability date
	 * @param sku the sku
	 * @param unitOfMeasureKey the unit of measure key
	 * @return the number of matching commerce inventory replenishment items
	 */
	public int countByAD_S_U(
		Date availabilityDate, String sku, String unitOfMeasureKey);

	/**
	 * Returns the commerce inventory replenishment item where externalReferenceCode = &#63; and companyId = &#63; or throws a <code>NoSuchInventoryReplenishmentItemException</code> if it could not be found.
	 *
	 * @param externalReferenceCode the external reference code
	 * @param companyId the company ID
	 * @return the matching commerce inventory replenishment item
	 * @throws NoSuchInventoryReplenishmentItemException if a matching commerce inventory replenishment item could not be found
	 */
	public CommerceInventoryReplenishmentItem findByERC_C(
			String externalReferenceCode, long companyId)
		throws NoSuchInventoryReplenishmentItemException;

	/**
	 * Returns the commerce inventory replenishment item where externalReferenceCode = &#63; and companyId = &#63; or returns <code>null</code> if it could not be found, optionally using the finder cache.
	 *
	 * @param externalReferenceCode the external reference code
	 * @param companyId the company ID
	 * @param useFinderCache whether to use the finder cache
	 * @return the matching commerce inventory replenishment item, or <code>null</code> if a matching commerce inventory replenishment item could not be found
	 */
	public CommerceInventoryReplenishmentItem fetchByERC_C(
		String externalReferenceCode, long companyId, boolean useFinderCache);

	/**
	 * Removes the commerce inventory replenishment item where externalReferenceCode = &#63; and companyId = &#63; from the database.
	 *
	 * @param externalReferenceCode the external reference code
	 * @param companyId the company ID
	 * @return the commerce inventory replenishment item that was removed
	 */
	public CommerceInventoryReplenishmentItem removeByERC_C(
			String externalReferenceCode, long companyId)
		throws NoSuchInventoryReplenishmentItemException;

	/**
	 * Returns the number of commerce inventory replenishment items where externalReferenceCode = &#63; and companyId = &#63;.
	 *
	 * @param externalReferenceCode the external reference code
	 * @param companyId the company ID
	 * @return the number of matching commerce inventory replenishment items
	 */
	public int countByERC_C(String externalReferenceCode, long companyId);

	/**
	 * Creates a new commerce inventory replenishment item with the primary key. Does not add the commerce inventory replenishment item to the database.
	 *
	 * @param commerceInventoryReplenishmentItemId the primary key for the new commerce inventory replenishment item
	 * @return the new commerce inventory replenishment item
	 */
	public CommerceInventoryReplenishmentItem create(
		long commerceInventoryReplenishmentItemId);

	/**
	 * Removes the commerce inventory replenishment item with the primary key from the database. Also notifies the appropriate model listeners.
	 *
	 * @param commerceInventoryReplenishmentItemId the primary key of the commerce inventory replenishment item
	 * @return the commerce inventory replenishment item that was removed
	 * @throws NoSuchInventoryReplenishmentItemException if a commerce inventory replenishment item with the primary key could not be found
	 */
	public CommerceInventoryReplenishmentItem remove(
			long commerceInventoryReplenishmentItemId)
		throws NoSuchInventoryReplenishmentItemException;

	public CommerceInventoryReplenishmentItem updateImpl(
		CommerceInventoryReplenishmentItem commerceInventoryReplenishmentItem);

	/**
	 * Returns the commerce inventory replenishment item with the primary key or throws a <code>NoSuchInventoryReplenishmentItemException</code> if it could not be found.
	 *
	 * @param commerceInventoryReplenishmentItemId the primary key of the commerce inventory replenishment item
	 * @return the commerce inventory replenishment item
	 * @throws NoSuchInventoryReplenishmentItemException if a commerce inventory replenishment item with the primary key could not be found
	 */
	public CommerceInventoryReplenishmentItem findByPrimaryKey(
			long commerceInventoryReplenishmentItemId)
		throws NoSuchInventoryReplenishmentItemException;

	/**
	 * Returns the commerce inventory replenishment item with the primary key or returns <code>null</code> if it could not be found.
	 *
	 * @param commerceInventoryReplenishmentItemId the primary key of the commerce inventory replenishment item
	 * @return the commerce inventory replenishment item, or <code>null</code> if a commerce inventory replenishment item with the primary key could not be found
	 */
	public CommerceInventoryReplenishmentItem fetchByPrimaryKey(
		long commerceInventoryReplenishmentItemId);

	/**
	 * Returns the commerce inventory replenishment item where externalReferenceCode = &#63; and companyId = &#63; or returns <code>null</code> if it could not be found. Uses the finder cache.
	 *
	 * @param externalReferenceCode the external reference code
	 * @param companyId the company ID
	 * @return the matching commerce inventory replenishment item, or <code>null</code> if a matching commerce inventory replenishment item could not be found
	 */
	public default CommerceInventoryReplenishmentItem fetchByERC_C(
		String externalReferenceCode, long companyId) {

		return fetchByERC_C(externalReferenceCode, companyId, true);
	}

	/**
	 * Returns all the commerce inventory replenishment items where uuid = &#63;.
	 *
	 * @param uuid the uuid
	 * @return the matching commerce inventory replenishment items
	 */
	public default java.util.List<CommerceInventoryReplenishmentItem>
		findByUuid(String uuid) {

		return findByUuid(
			uuid, com.liferay.portal.kernel.dao.orm.QueryUtil.ALL_POS,
			com.liferay.portal.kernel.dao.orm.QueryUtil.ALL_POS, null, true);
	}

	/**
	 * Returns a range of all the commerce inventory replenishment items where uuid = &#63;.
	 *
	 * <p>
	 * Useful when paginating results. Returns a maximum of <code>end - start</code> instances. <code>start</code> and <code>end</code> are not primary keys, they are indexes in the result set. Thus, <code>0</code> refers to the first result in the set. Setting both <code>start</code> and <code>end</code> to <code>com.liferay.portal.kernel.dao.orm.QueryUtil#ALL_POS</code> will return the full result set. If <code>orderByComparator</code> is specified, then the query will include the given ORDER BY logic. If <code>orderByComparator</code> is absent, then the query will include the default ORDER BY logic from <code>com.liferay.commerce.inventory.model.impl.CommerceInventoryReplenishmentItemModelImpl</code>.
	 * </p>
	 *
	 * @param uuid the uuid
	 * @param start the lower bound of the range of commerce inventory replenishment items
	 * @param end the upper bound of the range of commerce inventory replenishment items (not inclusive)
	 * @return the range of matching commerce inventory replenishment items
	 */
	public default java.util.List<CommerceInventoryReplenishmentItem>
		findByUuid(String uuid, int start, int end) {

		return findByUuid(uuid, start, end, null, true);
	}

	/**
	 * Returns an ordered range of all the commerce inventory replenishment items where uuid = &#63;.
	 *
	 * <p>
	 * Useful when paginating results. Returns a maximum of <code>end - start</code> instances. <code>start</code> and <code>end</code> are not primary keys, they are indexes in the result set. Thus, <code>0</code> refers to the first result in the set. Setting both <code>start</code> and <code>end</code> to <code>com.liferay.portal.kernel.dao.orm.QueryUtil#ALL_POS</code> will return the full result set. If <code>orderByComparator</code> is specified, then the query will include the given ORDER BY logic. If <code>orderByComparator</code> is absent, then the query will include the default ORDER BY logic from <code>com.liferay.commerce.inventory.model.impl.CommerceInventoryReplenishmentItemModelImpl</code>.
	 * </p>
	 *
	 * @param uuid the uuid
	 * @param start the lower bound of the range of commerce inventory replenishment items
	 * @param end the upper bound of the range of commerce inventory replenishment items (not inclusive)
	 * @param orderByComparator the comparator to order the results by (optionally <code>null</code>)
	 * @return the ordered range of matching commerce inventory replenishment items
	 */
	public default java.util.List<CommerceInventoryReplenishmentItem>
		findByUuid(
			String uuid, int start, int end,
			com.liferay.portal.kernel.util.OrderByComparator
				<CommerceInventoryReplenishmentItem> orderByComparator) {

		return findByUuid(uuid, start, end, orderByComparator, true);
	}

	/**
	 * Returns all the commerce inventory replenishment items where uuid = &#63; and companyId = &#63;.
	 *
	 * @param uuid the uuid
	 * @param companyId the company ID
	 * @return the matching commerce inventory replenishment items
	 */
	public default java.util.List<CommerceInventoryReplenishmentItem>
		findByUuid_C(String uuid, long companyId) {

		return findByUuid_C(
			uuid, companyId,
			com.liferay.portal.kernel.dao.orm.QueryUtil.ALL_POS,
			com.liferay.portal.kernel.dao.orm.QueryUtil.ALL_POS, null, true);
	}

	/**
	 * Returns a range of all the commerce inventory replenishment items where uuid = &#63; and companyId = &#63;.
	 *
	 * <p>
	 * Useful when paginating results. Returns a maximum of <code>end - start</code> instances. <code>start</code> and <code>end</code> are not primary keys, they are indexes in the result set. Thus, <code>0</code> refers to the first result in the set. Setting both <code>start</code> and <code>end</code> to <code>com.liferay.portal.kernel.dao.orm.QueryUtil#ALL_POS</code> will return the full result set. If <code>orderByComparator</code> is specified, then the query will include the given ORDER BY logic. If <code>orderByComparator</code> is absent, then the query will include the default ORDER BY logic from <code>com.liferay.commerce.inventory.model.impl.CommerceInventoryReplenishmentItemModelImpl</code>.
	 * </p>
	 *
	 * @param uuid the uuid
	 * @param companyId the company ID
	 * @param start the lower bound of the range of commerce inventory replenishment items
	 * @param end the upper bound of the range of commerce inventory replenishment items (not inclusive)
	 * @return the range of matching commerce inventory replenishment items
	 */
	public default java.util.List<CommerceInventoryReplenishmentItem>
		findByUuid_C(String uuid, long companyId, int start, int end) {

		return findByUuid_C(uuid, companyId, start, end, null, true);
	}

	/**
	 * Returns an ordered range of all the commerce inventory replenishment items where uuid = &#63; and companyId = &#63;.
	 *
	 * <p>
	 * Useful when paginating results. Returns a maximum of <code>end - start</code> instances. <code>start</code> and <code>end</code> are not primary keys, they are indexes in the result set. Thus, <code>0</code> refers to the first result in the set. Setting both <code>start</code> and <code>end</code> to <code>com.liferay.portal.kernel.dao.orm.QueryUtil#ALL_POS</code> will return the full result set. If <code>orderByComparator</code> is specified, then the query will include the given ORDER BY logic. If <code>orderByComparator</code> is absent, then the query will include the default ORDER BY logic from <code>com.liferay.commerce.inventory.model.impl.CommerceInventoryReplenishmentItemModelImpl</code>.
	 * </p>
	 *
	 * @param uuid the uuid
	 * @param companyId the company ID
	 * @param start the lower bound of the range of commerce inventory replenishment items
	 * @param end the upper bound of the range of commerce inventory replenishment items (not inclusive)
	 * @param orderByComparator the comparator to order the results by (optionally <code>null</code>)
	 * @return the ordered range of matching commerce inventory replenishment items
	 */
	public default java.util.List<CommerceInventoryReplenishmentItem>
		findByUuid_C(
			String uuid, long companyId, int start, int end,
			com.liferay.portal.kernel.util.OrderByComparator
				<CommerceInventoryReplenishmentItem> orderByComparator) {

		return findByUuid_C(
			uuid, companyId, start, end, orderByComparator, true);
	}

	/**
	 * Returns all the commerce inventory replenishment items where commerceInventoryWarehouseId = &#63;.
	 *
	 * @param commerceInventoryWarehouseId the commerce inventory warehouse ID
	 * @return the matching commerce inventory replenishment items
	 */
	public default java.util.List<CommerceInventoryReplenishmentItem>
		findByCommerceInventoryWarehouseId(long commerceInventoryWarehouseId) {

		return findByCommerceInventoryWarehouseId(
			commerceInventoryWarehouseId,
			com.liferay.portal.kernel.dao.orm.QueryUtil.ALL_POS,
			com.liferay.portal.kernel.dao.orm.QueryUtil.ALL_POS, null, true);
	}

	/**
	 * Returns a range of all the commerce inventory replenishment items where commerceInventoryWarehouseId = &#63;.
	 *
	 * <p>
	 * Useful when paginating results. Returns a maximum of <code>end - start</code> instances. <code>start</code> and <code>end</code> are not primary keys, they are indexes in the result set. Thus, <code>0</code> refers to the first result in the set. Setting both <code>start</code> and <code>end</code> to <code>com.liferay.portal.kernel.dao.orm.QueryUtil#ALL_POS</code> will return the full result set. If <code>orderByComparator</code> is specified, then the query will include the given ORDER BY logic. If <code>orderByComparator</code> is absent, then the query will include the default ORDER BY logic from <code>com.liferay.commerce.inventory.model.impl.CommerceInventoryReplenishmentItemModelImpl</code>.
	 * </p>
	 *
	 * @param commerceInventoryWarehouseId the commerce inventory warehouse ID
	 * @param start the lower bound of the range of commerce inventory replenishment items
	 * @param end the upper bound of the range of commerce inventory replenishment items (not inclusive)
	 * @return the range of matching commerce inventory replenishment items
	 */
	public default java.util.List<CommerceInventoryReplenishmentItem>
		findByCommerceInventoryWarehouseId(
			long commerceInventoryWarehouseId, int start, int end) {

		return findByCommerceInventoryWarehouseId(
			commerceInventoryWarehouseId, start, end, null, true);
	}

	/**
	 * Returns an ordered range of all the commerce inventory replenishment items where commerceInventoryWarehouseId = &#63;.
	 *
	 * <p>
	 * Useful when paginating results. Returns a maximum of <code>end - start</code> instances. <code>start</code> and <code>end</code> are not primary keys, they are indexes in the result set. Thus, <code>0</code> refers to the first result in the set. Setting both <code>start</code> and <code>end</code> to <code>com.liferay.portal.kernel.dao.orm.QueryUtil#ALL_POS</code> will return the full result set. If <code>orderByComparator</code> is specified, then the query will include the given ORDER BY logic. If <code>orderByComparator</code> is absent, then the query will include the default ORDER BY logic from <code>com.liferay.commerce.inventory.model.impl.CommerceInventoryReplenishmentItemModelImpl</code>.
	 * </p>
	 *
	 * @param commerceInventoryWarehouseId the commerce inventory warehouse ID
	 * @param start the lower bound of the range of commerce inventory replenishment items
	 * @param end the upper bound of the range of commerce inventory replenishment items (not inclusive)
	 * @param orderByComparator the comparator to order the results by (optionally <code>null</code>)
	 * @return the ordered range of matching commerce inventory replenishment items
	 */
	public default java.util.List<CommerceInventoryReplenishmentItem>
		findByCommerceInventoryWarehouseId(
			long commerceInventoryWarehouseId, int start, int end,
			com.liferay.portal.kernel.util.OrderByComparator
				<CommerceInventoryReplenishmentItem> orderByComparator) {

		return findByCommerceInventoryWarehouseId(
			commerceInventoryWarehouseId, start, end, orderByComparator, true);
	}

	/**
	 * Returns all the commerce inventory replenishment items where sku = &#63;.
	 *
	 * @param sku the sku
	 * @return the matching commerce inventory replenishment items
	 */
	public default java.util.List<CommerceInventoryReplenishmentItem> findBySku(
		String sku) {

		return findBySku(
			sku, com.liferay.portal.kernel.dao.orm.QueryUtil.ALL_POS,
			com.liferay.portal.kernel.dao.orm.QueryUtil.ALL_POS, null, true);
	}

	/**
	 * Returns a range of all the commerce inventory replenishment items where sku = &#63;.
	 *
	 * <p>
	 * Useful when paginating results. Returns a maximum of <code>end - start</code> instances. <code>start</code> and <code>end</code> are not primary keys, they are indexes in the result set. Thus, <code>0</code> refers to the first result in the set. Setting both <code>start</code> and <code>end</code> to <code>com.liferay.portal.kernel.dao.orm.QueryUtil#ALL_POS</code> will return the full result set. If <code>orderByComparator</code> is specified, then the query will include the given ORDER BY logic. If <code>orderByComparator</code> is absent, then the query will include the default ORDER BY logic from <code>com.liferay.commerce.inventory.model.impl.CommerceInventoryReplenishmentItemModelImpl</code>.
	 * </p>
	 *
	 * @param sku the sku
	 * @param start the lower bound of the range of commerce inventory replenishment items
	 * @param end the upper bound of the range of commerce inventory replenishment items (not inclusive)
	 * @return the range of matching commerce inventory replenishment items
	 */
	public default java.util.List<CommerceInventoryReplenishmentItem> findBySku(
		String sku, int start, int end) {

		return findBySku(sku, start, end, null, true);
	}

	/**
	 * Returns an ordered range of all the commerce inventory replenishment items where sku = &#63;.
	 *
	 * <p>
	 * Useful when paginating results. Returns a maximum of <code>end - start</code> instances. <code>start</code> and <code>end</code> are not primary keys, they are indexes in the result set. Thus, <code>0</code> refers to the first result in the set. Setting both <code>start</code> and <code>end</code> to <code>com.liferay.portal.kernel.dao.orm.QueryUtil#ALL_POS</code> will return the full result set. If <code>orderByComparator</code> is specified, then the query will include the given ORDER BY logic. If <code>orderByComparator</code> is absent, then the query will include the default ORDER BY logic from <code>com.liferay.commerce.inventory.model.impl.CommerceInventoryReplenishmentItemModelImpl</code>.
	 * </p>
	 *
	 * @param sku the sku
	 * @param start the lower bound of the range of commerce inventory replenishment items
	 * @param end the upper bound of the range of commerce inventory replenishment items (not inclusive)
	 * @param orderByComparator the comparator to order the results by (optionally <code>null</code>)
	 * @return the ordered range of matching commerce inventory replenishment items
	 */
	public default java.util.List<CommerceInventoryReplenishmentItem> findBySku(
		String sku, int start, int end,
		com.liferay.portal.kernel.util.OrderByComparator
			<CommerceInventoryReplenishmentItem> orderByComparator) {

		return findBySku(sku, start, end, orderByComparator, true);
	}

	/**
	 * Returns all the commerce inventory replenishment items where companyId = &#63; and sku = &#63; and unitOfMeasureKey = &#63;.
	 *
	 * @param companyId the company ID
	 * @param sku the sku
	 * @param unitOfMeasureKey the unit of measure key
	 * @return the matching commerce inventory replenishment items
	 */
	public default java.util.List<CommerceInventoryReplenishmentItem>
		findByC_S_U(long companyId, String sku, String unitOfMeasureKey) {

		return findByC_S_U(
			companyId, sku, unitOfMeasureKey,
			com.liferay.portal.kernel.dao.orm.QueryUtil.ALL_POS,
			com.liferay.portal.kernel.dao.orm.QueryUtil.ALL_POS, null, true);
	}

	/**
	 * Returns a range of all the commerce inventory replenishment items where companyId = &#63; and sku = &#63; and unitOfMeasureKey = &#63;.
	 *
	 * <p>
	 * Useful when paginating results. Returns a maximum of <code>end - start</code> instances. <code>start</code> and <code>end</code> are not primary keys, they are indexes in the result set. Thus, <code>0</code> refers to the first result in the set. Setting both <code>start</code> and <code>end</code> to <code>com.liferay.portal.kernel.dao.orm.QueryUtil#ALL_POS</code> will return the full result set. If <code>orderByComparator</code> is specified, then the query will include the given ORDER BY logic. If <code>orderByComparator</code> is absent, then the query will include the default ORDER BY logic from <code>com.liferay.commerce.inventory.model.impl.CommerceInventoryReplenishmentItemModelImpl</code>.
	 * </p>
	 *
	 * @param companyId the company ID
	 * @param sku the sku
	 * @param unitOfMeasureKey the unit of measure key
	 * @param start the lower bound of the range of commerce inventory replenishment items
	 * @param end the upper bound of the range of commerce inventory replenishment items (not inclusive)
	 * @return the range of matching commerce inventory replenishment items
	 */
	public default java.util.List<CommerceInventoryReplenishmentItem>
		findByC_S_U(
			long companyId, String sku, String unitOfMeasureKey, int start,
			int end) {

		return findByC_S_U(
			companyId, sku, unitOfMeasureKey, start, end, null, true);
	}

	/**
	 * Returns an ordered range of all the commerce inventory replenishment items where companyId = &#63; and sku = &#63; and unitOfMeasureKey = &#63;.
	 *
	 * <p>
	 * Useful when paginating results. Returns a maximum of <code>end - start</code> instances. <code>start</code> and <code>end</code> are not primary keys, they are indexes in the result set. Thus, <code>0</code> refers to the first result in the set. Setting both <code>start</code> and <code>end</code> to <code>com.liferay.portal.kernel.dao.orm.QueryUtil#ALL_POS</code> will return the full result set. If <code>orderByComparator</code> is specified, then the query will include the given ORDER BY logic. If <code>orderByComparator</code> is absent, then the query will include the default ORDER BY logic from <code>com.liferay.commerce.inventory.model.impl.CommerceInventoryReplenishmentItemModelImpl</code>.
	 * </p>
	 *
	 * @param companyId the company ID
	 * @param sku the sku
	 * @param unitOfMeasureKey the unit of measure key
	 * @param start the lower bound of the range of commerce inventory replenishment items
	 * @param end the upper bound of the range of commerce inventory replenishment items (not inclusive)
	 * @param orderByComparator the comparator to order the results by (optionally <code>null</code>)
	 * @return the ordered range of matching commerce inventory replenishment items
	 */
	public default java.util.List<CommerceInventoryReplenishmentItem>
		findByC_S_U(
			long companyId, String sku, String unitOfMeasureKey, int start,
			int end,
			com.liferay.portal.kernel.util.OrderByComparator
				<CommerceInventoryReplenishmentItem> orderByComparator) {

		return findByC_S_U(
			companyId, sku, unitOfMeasureKey, start, end, orderByComparator,
			true);
	}

	/**
	 * Returns all the commerce inventory replenishment items where availabilityDate = &#63; and sku = &#63; and unitOfMeasureKey = &#63;.
	 *
	 * @param availabilityDate the availability date
	 * @param sku the sku
	 * @param unitOfMeasureKey the unit of measure key
	 * @return the matching commerce inventory replenishment items
	 */
	public default java.util.List<CommerceInventoryReplenishmentItem>
		findByAD_S_U(
			Date availabilityDate, String sku, String unitOfMeasureKey) {

		return findByAD_S_U(
			availabilityDate, sku, unitOfMeasureKey,
			com.liferay.portal.kernel.dao.orm.QueryUtil.ALL_POS,
			com.liferay.portal.kernel.dao.orm.QueryUtil.ALL_POS, null, true);
	}

	/**
	 * Returns a range of all the commerce inventory replenishment items where availabilityDate = &#63; and sku = &#63; and unitOfMeasureKey = &#63;.
	 *
	 * <p>
	 * Useful when paginating results. Returns a maximum of <code>end - start</code> instances. <code>start</code> and <code>end</code> are not primary keys, they are indexes in the result set. Thus, <code>0</code> refers to the first result in the set. Setting both <code>start</code> and <code>end</code> to <code>com.liferay.portal.kernel.dao.orm.QueryUtil#ALL_POS</code> will return the full result set. If <code>orderByComparator</code> is specified, then the query will include the given ORDER BY logic. If <code>orderByComparator</code> is absent, then the query will include the default ORDER BY logic from <code>com.liferay.commerce.inventory.model.impl.CommerceInventoryReplenishmentItemModelImpl</code>.
	 * </p>
	 *
	 * @param availabilityDate the availability date
	 * @param sku the sku
	 * @param unitOfMeasureKey the unit of measure key
	 * @param start the lower bound of the range of commerce inventory replenishment items
	 * @param end the upper bound of the range of commerce inventory replenishment items (not inclusive)
	 * @return the range of matching commerce inventory replenishment items
	 */
	public default java.util.List<CommerceInventoryReplenishmentItem>
		findByAD_S_U(
			Date availabilityDate, String sku, String unitOfMeasureKey,
			int start, int end) {

		return findByAD_S_U(
			availabilityDate, sku, unitOfMeasureKey, start, end, null, true);
	}

	/**
	 * Returns an ordered range of all the commerce inventory replenishment items where availabilityDate = &#63; and sku = &#63; and unitOfMeasureKey = &#63;.
	 *
	 * <p>
	 * Useful when paginating results. Returns a maximum of <code>end - start</code> instances. <code>start</code> and <code>end</code> are not primary keys, they are indexes in the result set. Thus, <code>0</code> refers to the first result in the set. Setting both <code>start</code> and <code>end</code> to <code>com.liferay.portal.kernel.dao.orm.QueryUtil#ALL_POS</code> will return the full result set. If <code>orderByComparator</code> is specified, then the query will include the given ORDER BY logic. If <code>orderByComparator</code> is absent, then the query will include the default ORDER BY logic from <code>com.liferay.commerce.inventory.model.impl.CommerceInventoryReplenishmentItemModelImpl</code>.
	 * </p>
	 *
	 * @param availabilityDate the availability date
	 * @param sku the sku
	 * @param unitOfMeasureKey the unit of measure key
	 * @param start the lower bound of the range of commerce inventory replenishment items
	 * @param end the upper bound of the range of commerce inventory replenishment items (not inclusive)
	 * @param orderByComparator the comparator to order the results by (optionally <code>null</code>)
	 * @return the ordered range of matching commerce inventory replenishment items
	 */
	public default java.util.List<CommerceInventoryReplenishmentItem>
		findByAD_S_U(
			Date availabilityDate, String sku, String unitOfMeasureKey,
			int start, int end,
			com.liferay.portal.kernel.util.OrderByComparator
				<CommerceInventoryReplenishmentItem> orderByComparator) {

		return findByAD_S_U(
			availabilityDate, sku, unitOfMeasureKey, start, end,
			orderByComparator, true);
	}

}
// LIFERAY-SERVICE-BUILDER-HASH:1508281372