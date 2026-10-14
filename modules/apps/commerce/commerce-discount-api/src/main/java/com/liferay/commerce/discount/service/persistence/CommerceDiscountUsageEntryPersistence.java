/**
 * SPDX-FileCopyrightText: (c) 2000 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

package com.liferay.commerce.discount.service.persistence;

import com.liferay.commerce.discount.exception.NoSuchDiscountUsageEntryException;
import com.liferay.commerce.discount.model.CommerceDiscountUsageEntry;
import com.liferay.portal.kernel.service.persistence.BasePersistence;

import org.osgi.annotation.versioning.ProviderType;

/**
 * The persistence interface for the commerce discount usage entry service.
 *
 * <p>
 * Caching information and settings can be found in <code>portal.properties</code>
 * </p>
 *
 * @author Marco Leo
 * @see CommerceDiscountUsageEntryUtil
 * @generated
 */
@ProviderType
public interface CommerceDiscountUsageEntryPersistence
	extends BasePersistence<CommerceDiscountUsageEntry> {

	/*
	 * NOTE FOR DEVELOPERS:
	 *
	 * Never modify or reference this interface directly. Always use {@link CommerceDiscountUsageEntryUtil} to access the commerce discount usage entry persistence. Modify <code>service.xml</code> and rerun ServiceBuilder to regenerate this interface.
	 */

	/**
	 * Returns an ordered range of all the commerce discount usage entries where commerceDiscountId = &#63;.
	 *
	 * <p>
	 * Useful when paginating results. Returns a maximum of <code>end - start</code> instances. <code>start</code> and <code>end</code> are not primary keys, they are indexes in the result set. Thus, <code>0</code> refers to the first result in the set. Setting both <code>start</code> and <code>end</code> to <code>com.liferay.portal.kernel.dao.orm.QueryUtil#ALL_POS</code> will return the full result set. If <code>orderByComparator</code> is specified, then the query will include the given ORDER BY logic. If <code>orderByComparator</code> is absent, then the query will include the default ORDER BY logic from <code>com.liferay.commerce.discount.model.impl.CommerceDiscountUsageEntryModelImpl</code>.
	 * </p>
	 *
	 * @param commerceDiscountId the commerce discount ID
	 * @param start the lower bound of the range of commerce discount usage entries
	 * @param end the upper bound of the range of commerce discount usage entries (not inclusive)
	 * @param orderByComparator the comparator to order the results by (optionally <code>null</code>)
	 * @param useFinderCache whether to use the finder cache
	 * @return the ordered range of matching commerce discount usage entries
	 */
	public java.util.List<CommerceDiscountUsageEntry> findByCommerceDiscountId(
		long commerceDiscountId, int start, int end,
		com.liferay.portal.kernel.util.OrderByComparator
			<CommerceDiscountUsageEntry> orderByComparator,
		boolean useFinderCache);

	/**
	 * Returns the first commerce discount usage entry in the ordered set where commerceDiscountId = &#63;.
	 *
	 * @param commerceDiscountId the commerce discount ID
	 * @param orderByComparator the comparator to order the set by (optionally <code>null</code>)
	 * @return the first matching commerce discount usage entry
	 * @throws NoSuchDiscountUsageEntryException if a matching commerce discount usage entry could not be found
	 */
	public CommerceDiscountUsageEntry findByCommerceDiscountId_First(
			long commerceDiscountId,
			com.liferay.portal.kernel.util.OrderByComparator
				<CommerceDiscountUsageEntry> orderByComparator)
		throws NoSuchDiscountUsageEntryException;

	/**
	 * Returns the first commerce discount usage entry in the ordered set where commerceDiscountId = &#63;.
	 *
	 * @param commerceDiscountId the commerce discount ID
	 * @param orderByComparator the comparator to order the set by (optionally <code>null</code>)
	 * @return the first matching commerce discount usage entry, or <code>null</code> if a matching commerce discount usage entry could not be found
	 */
	public CommerceDiscountUsageEntry fetchByCommerceDiscountId_First(
		long commerceDiscountId,
		com.liferay.portal.kernel.util.OrderByComparator
			<CommerceDiscountUsageEntry> orderByComparator);

	/**
	 * Removes all the commerce discount usage entries where commerceDiscountId = &#63; from the database.
	 *
	 * @param commerceDiscountId the commerce discount ID
	 */
	public void removeByCommerceDiscountId(long commerceDiscountId);

	/**
	 * Returns the number of commerce discount usage entries where commerceDiscountId = &#63;.
	 *
	 * @param commerceDiscountId the commerce discount ID
	 * @return the number of matching commerce discount usage entries
	 */
	public int countByCommerceDiscountId(long commerceDiscountId);

	/**
	 * Returns an ordered range of all the commerce discount usage entries where commerceAccountId = &#63; and commerceDiscountId = &#63;.
	 *
	 * <p>
	 * Useful when paginating results. Returns a maximum of <code>end - start</code> instances. <code>start</code> and <code>end</code> are not primary keys, they are indexes in the result set. Thus, <code>0</code> refers to the first result in the set. Setting both <code>start</code> and <code>end</code> to <code>com.liferay.portal.kernel.dao.orm.QueryUtil#ALL_POS</code> will return the full result set. If <code>orderByComparator</code> is specified, then the query will include the given ORDER BY logic. If <code>orderByComparator</code> is absent, then the query will include the default ORDER BY logic from <code>com.liferay.commerce.discount.model.impl.CommerceDiscountUsageEntryModelImpl</code>.
	 * </p>
	 *
	 * @param commerceAccountId the commerce account ID
	 * @param commerceDiscountId the commerce discount ID
	 * @param start the lower bound of the range of commerce discount usage entries
	 * @param end the upper bound of the range of commerce discount usage entries (not inclusive)
	 * @param orderByComparator the comparator to order the results by (optionally <code>null</code>)
	 * @param useFinderCache whether to use the finder cache
	 * @return the ordered range of matching commerce discount usage entries
	 */
	public java.util.List<CommerceDiscountUsageEntry> findByCAI_CDI(
		long commerceAccountId, long commerceDiscountId, int start, int end,
		com.liferay.portal.kernel.util.OrderByComparator
			<CommerceDiscountUsageEntry> orderByComparator,
		boolean useFinderCache);

	/**
	 * Returns the first commerce discount usage entry in the ordered set where commerceAccountId = &#63; and commerceDiscountId = &#63;.
	 *
	 * @param commerceAccountId the commerce account ID
	 * @param commerceDiscountId the commerce discount ID
	 * @param orderByComparator the comparator to order the set by (optionally <code>null</code>)
	 * @return the first matching commerce discount usage entry
	 * @throws NoSuchDiscountUsageEntryException if a matching commerce discount usage entry could not be found
	 */
	public CommerceDiscountUsageEntry findByCAI_CDI_First(
			long commerceAccountId, long commerceDiscountId,
			com.liferay.portal.kernel.util.OrderByComparator
				<CommerceDiscountUsageEntry> orderByComparator)
		throws NoSuchDiscountUsageEntryException;

	/**
	 * Returns the first commerce discount usage entry in the ordered set where commerceAccountId = &#63; and commerceDiscountId = &#63;.
	 *
	 * @param commerceAccountId the commerce account ID
	 * @param commerceDiscountId the commerce discount ID
	 * @param orderByComparator the comparator to order the set by (optionally <code>null</code>)
	 * @return the first matching commerce discount usage entry, or <code>null</code> if a matching commerce discount usage entry could not be found
	 */
	public CommerceDiscountUsageEntry fetchByCAI_CDI_First(
		long commerceAccountId, long commerceDiscountId,
		com.liferay.portal.kernel.util.OrderByComparator
			<CommerceDiscountUsageEntry> orderByComparator);

	/**
	 * Removes all the commerce discount usage entries where commerceAccountId = &#63; and commerceDiscountId = &#63; from the database.
	 *
	 * @param commerceAccountId the commerce account ID
	 * @param commerceDiscountId the commerce discount ID
	 */
	public void removeByCAI_CDI(
		long commerceAccountId, long commerceDiscountId);

	/**
	 * Returns the number of commerce discount usage entries where commerceAccountId = &#63; and commerceDiscountId = &#63;.
	 *
	 * @param commerceAccountId the commerce account ID
	 * @param commerceDiscountId the commerce discount ID
	 * @return the number of matching commerce discount usage entries
	 */
	public int countByCAI_CDI(long commerceAccountId, long commerceDiscountId);

	/**
	 * Returns an ordered range of all the commerce discount usage entries where commerceAccountId = &#63; and commerceOrderId = &#63; and commerceDiscountId = &#63;.
	 *
	 * <p>
	 * Useful when paginating results. Returns a maximum of <code>end - start</code> instances. <code>start</code> and <code>end</code> are not primary keys, they are indexes in the result set. Thus, <code>0</code> refers to the first result in the set. Setting both <code>start</code> and <code>end</code> to <code>com.liferay.portal.kernel.dao.orm.QueryUtil#ALL_POS</code> will return the full result set. If <code>orderByComparator</code> is specified, then the query will include the given ORDER BY logic. If <code>orderByComparator</code> is absent, then the query will include the default ORDER BY logic from <code>com.liferay.commerce.discount.model.impl.CommerceDiscountUsageEntryModelImpl</code>.
	 * </p>
	 *
	 * @param commerceAccountId the commerce account ID
	 * @param commerceOrderId the commerce order ID
	 * @param commerceDiscountId the commerce discount ID
	 * @param start the lower bound of the range of commerce discount usage entries
	 * @param end the upper bound of the range of commerce discount usage entries (not inclusive)
	 * @param orderByComparator the comparator to order the results by (optionally <code>null</code>)
	 * @param useFinderCache whether to use the finder cache
	 * @return the ordered range of matching commerce discount usage entries
	 */
	public java.util.List<CommerceDiscountUsageEntry> findByCAI_COI_CDI(
		long commerceAccountId, long commerceOrderId, long commerceDiscountId,
		int start, int end,
		com.liferay.portal.kernel.util.OrderByComparator
			<CommerceDiscountUsageEntry> orderByComparator,
		boolean useFinderCache);

	/**
	 * Returns the first commerce discount usage entry in the ordered set where commerceAccountId = &#63; and commerceOrderId = &#63; and commerceDiscountId = &#63;.
	 *
	 * @param commerceAccountId the commerce account ID
	 * @param commerceOrderId the commerce order ID
	 * @param commerceDiscountId the commerce discount ID
	 * @param orderByComparator the comparator to order the set by (optionally <code>null</code>)
	 * @return the first matching commerce discount usage entry
	 * @throws NoSuchDiscountUsageEntryException if a matching commerce discount usage entry could not be found
	 */
	public CommerceDiscountUsageEntry findByCAI_COI_CDI_First(
			long commerceAccountId, long commerceOrderId,
			long commerceDiscountId,
			com.liferay.portal.kernel.util.OrderByComparator
				<CommerceDiscountUsageEntry> orderByComparator)
		throws NoSuchDiscountUsageEntryException;

	/**
	 * Returns the first commerce discount usage entry in the ordered set where commerceAccountId = &#63; and commerceOrderId = &#63; and commerceDiscountId = &#63;.
	 *
	 * @param commerceAccountId the commerce account ID
	 * @param commerceOrderId the commerce order ID
	 * @param commerceDiscountId the commerce discount ID
	 * @param orderByComparator the comparator to order the set by (optionally <code>null</code>)
	 * @return the first matching commerce discount usage entry, or <code>null</code> if a matching commerce discount usage entry could not be found
	 */
	public CommerceDiscountUsageEntry fetchByCAI_COI_CDI_First(
		long commerceAccountId, long commerceOrderId, long commerceDiscountId,
		com.liferay.portal.kernel.util.OrderByComparator
			<CommerceDiscountUsageEntry> orderByComparator);

	/**
	 * Removes all the commerce discount usage entries where commerceAccountId = &#63; and commerceOrderId = &#63; and commerceDiscountId = &#63; from the database.
	 *
	 * @param commerceAccountId the commerce account ID
	 * @param commerceOrderId the commerce order ID
	 * @param commerceDiscountId the commerce discount ID
	 */
	public void removeByCAI_COI_CDI(
		long commerceAccountId, long commerceOrderId, long commerceDiscountId);

	/**
	 * Returns the number of commerce discount usage entries where commerceAccountId = &#63; and commerceOrderId = &#63; and commerceDiscountId = &#63;.
	 *
	 * @param commerceAccountId the commerce account ID
	 * @param commerceOrderId the commerce order ID
	 * @param commerceDiscountId the commerce discount ID
	 * @return the number of matching commerce discount usage entries
	 */
	public int countByCAI_COI_CDI(
		long commerceAccountId, long commerceOrderId, long commerceDiscountId);

	/**
	 * Creates a new commerce discount usage entry with the primary key. Does not add the commerce discount usage entry to the database.
	 *
	 * @param commerceDiscountUsageEntryId the primary key for the new commerce discount usage entry
	 * @return the new commerce discount usage entry
	 */
	public CommerceDiscountUsageEntry create(long commerceDiscountUsageEntryId);

	/**
	 * Removes the commerce discount usage entry with the primary key from the database. Also notifies the appropriate model listeners.
	 *
	 * @param commerceDiscountUsageEntryId the primary key of the commerce discount usage entry
	 * @return the commerce discount usage entry that was removed
	 * @throws NoSuchDiscountUsageEntryException if a commerce discount usage entry with the primary key could not be found
	 */
	public CommerceDiscountUsageEntry remove(long commerceDiscountUsageEntryId)
		throws NoSuchDiscountUsageEntryException;

	public CommerceDiscountUsageEntry updateImpl(
		CommerceDiscountUsageEntry commerceDiscountUsageEntry);

	/**
	 * Returns the commerce discount usage entry with the primary key or throws a <code>NoSuchDiscountUsageEntryException</code> if it could not be found.
	 *
	 * @param commerceDiscountUsageEntryId the primary key of the commerce discount usage entry
	 * @return the commerce discount usage entry
	 * @throws NoSuchDiscountUsageEntryException if a commerce discount usage entry with the primary key could not be found
	 */
	public CommerceDiscountUsageEntry findByPrimaryKey(
			long commerceDiscountUsageEntryId)
		throws NoSuchDiscountUsageEntryException;

	/**
	 * Returns the commerce discount usage entry with the primary key or returns <code>null</code> if it could not be found.
	 *
	 * @param commerceDiscountUsageEntryId the primary key of the commerce discount usage entry
	 * @return the commerce discount usage entry, or <code>null</code> if a commerce discount usage entry with the primary key could not be found
	 */
	public CommerceDiscountUsageEntry fetchByPrimaryKey(
		long commerceDiscountUsageEntryId);

	/**
	 * Returns all the commerce discount usage entries where commerceDiscountId = &#63;.
	 *
	 * @param commerceDiscountId the commerce discount ID
	 * @return the matching commerce discount usage entries
	 */
	public default java.util.List<CommerceDiscountUsageEntry>
		findByCommerceDiscountId(long commerceDiscountId) {

		return findByCommerceDiscountId(
			commerceDiscountId,
			com.liferay.portal.kernel.dao.orm.QueryUtil.ALL_POS,
			com.liferay.portal.kernel.dao.orm.QueryUtil.ALL_POS, null, true);
	}

	/**
	 * Returns a range of all the commerce discount usage entries where commerceDiscountId = &#63;.
	 *
	 * <p>
	 * Useful when paginating results. Returns a maximum of <code>end - start</code> instances. <code>start</code> and <code>end</code> are not primary keys, they are indexes in the result set. Thus, <code>0</code> refers to the first result in the set. Setting both <code>start</code> and <code>end</code> to <code>com.liferay.portal.kernel.dao.orm.QueryUtil#ALL_POS</code> will return the full result set. If <code>orderByComparator</code> is specified, then the query will include the given ORDER BY logic. If <code>orderByComparator</code> is absent, then the query will include the default ORDER BY logic from <code>com.liferay.commerce.discount.model.impl.CommerceDiscountUsageEntryModelImpl</code>.
	 * </p>
	 *
	 * @param commerceDiscountId the commerce discount ID
	 * @param start the lower bound of the range of commerce discount usage entries
	 * @param end the upper bound of the range of commerce discount usage entries (not inclusive)
	 * @return the range of matching commerce discount usage entries
	 */
	public default java.util.List<CommerceDiscountUsageEntry>
		findByCommerceDiscountId(long commerceDiscountId, int start, int end) {

		return findByCommerceDiscountId(
			commerceDiscountId, start, end, null, true);
	}

	/**
	 * Returns an ordered range of all the commerce discount usage entries where commerceDiscountId = &#63;.
	 *
	 * <p>
	 * Useful when paginating results. Returns a maximum of <code>end - start</code> instances. <code>start</code> and <code>end</code> are not primary keys, they are indexes in the result set. Thus, <code>0</code> refers to the first result in the set. Setting both <code>start</code> and <code>end</code> to <code>com.liferay.portal.kernel.dao.orm.QueryUtil#ALL_POS</code> will return the full result set. If <code>orderByComparator</code> is specified, then the query will include the given ORDER BY logic. If <code>orderByComparator</code> is absent, then the query will include the default ORDER BY logic from <code>com.liferay.commerce.discount.model.impl.CommerceDiscountUsageEntryModelImpl</code>.
	 * </p>
	 *
	 * @param commerceDiscountId the commerce discount ID
	 * @param start the lower bound of the range of commerce discount usage entries
	 * @param end the upper bound of the range of commerce discount usage entries (not inclusive)
	 * @param orderByComparator the comparator to order the results by (optionally <code>null</code>)
	 * @return the ordered range of matching commerce discount usage entries
	 */
	public default java.util.List<CommerceDiscountUsageEntry>
		findByCommerceDiscountId(
			long commerceDiscountId, int start, int end,
			com.liferay.portal.kernel.util.OrderByComparator
				<CommerceDiscountUsageEntry> orderByComparator) {

		return findByCommerceDiscountId(
			commerceDiscountId, start, end, orderByComparator, true);
	}

	/**
	 * Returns all the commerce discount usage entries where commerceAccountId = &#63; and commerceDiscountId = &#63;.
	 *
	 * @param commerceAccountId the commerce account ID
	 * @param commerceDiscountId the commerce discount ID
	 * @return the matching commerce discount usage entries
	 */
	public default java.util.List<CommerceDiscountUsageEntry> findByCAI_CDI(
		long commerceAccountId, long commerceDiscountId) {

		return findByCAI_CDI(
			commerceAccountId, commerceDiscountId,
			com.liferay.portal.kernel.dao.orm.QueryUtil.ALL_POS,
			com.liferay.portal.kernel.dao.orm.QueryUtil.ALL_POS, null, true);
	}

	/**
	 * Returns a range of all the commerce discount usage entries where commerceAccountId = &#63; and commerceDiscountId = &#63;.
	 *
	 * <p>
	 * Useful when paginating results. Returns a maximum of <code>end - start</code> instances. <code>start</code> and <code>end</code> are not primary keys, they are indexes in the result set. Thus, <code>0</code> refers to the first result in the set. Setting both <code>start</code> and <code>end</code> to <code>com.liferay.portal.kernel.dao.orm.QueryUtil#ALL_POS</code> will return the full result set. If <code>orderByComparator</code> is specified, then the query will include the given ORDER BY logic. If <code>orderByComparator</code> is absent, then the query will include the default ORDER BY logic from <code>com.liferay.commerce.discount.model.impl.CommerceDiscountUsageEntryModelImpl</code>.
	 * </p>
	 *
	 * @param commerceAccountId the commerce account ID
	 * @param commerceDiscountId the commerce discount ID
	 * @param start the lower bound of the range of commerce discount usage entries
	 * @param end the upper bound of the range of commerce discount usage entries (not inclusive)
	 * @return the range of matching commerce discount usage entries
	 */
	public default java.util.List<CommerceDiscountUsageEntry> findByCAI_CDI(
		long commerceAccountId, long commerceDiscountId, int start, int end) {

		return findByCAI_CDI(
			commerceAccountId, commerceDiscountId, start, end, null, true);
	}

	/**
	 * Returns an ordered range of all the commerce discount usage entries where commerceAccountId = &#63; and commerceDiscountId = &#63;.
	 *
	 * <p>
	 * Useful when paginating results. Returns a maximum of <code>end - start</code> instances. <code>start</code> and <code>end</code> are not primary keys, they are indexes in the result set. Thus, <code>0</code> refers to the first result in the set. Setting both <code>start</code> and <code>end</code> to <code>com.liferay.portal.kernel.dao.orm.QueryUtil#ALL_POS</code> will return the full result set. If <code>orderByComparator</code> is specified, then the query will include the given ORDER BY logic. If <code>orderByComparator</code> is absent, then the query will include the default ORDER BY logic from <code>com.liferay.commerce.discount.model.impl.CommerceDiscountUsageEntryModelImpl</code>.
	 * </p>
	 *
	 * @param commerceAccountId the commerce account ID
	 * @param commerceDiscountId the commerce discount ID
	 * @param start the lower bound of the range of commerce discount usage entries
	 * @param end the upper bound of the range of commerce discount usage entries (not inclusive)
	 * @param orderByComparator the comparator to order the results by (optionally <code>null</code>)
	 * @return the ordered range of matching commerce discount usage entries
	 */
	public default java.util.List<CommerceDiscountUsageEntry> findByCAI_CDI(
		long commerceAccountId, long commerceDiscountId, int start, int end,
		com.liferay.portal.kernel.util.OrderByComparator
			<CommerceDiscountUsageEntry> orderByComparator) {

		return findByCAI_CDI(
			commerceAccountId, commerceDiscountId, start, end,
			orderByComparator, true);
	}

	/**
	 * Returns all the commerce discount usage entries where commerceAccountId = &#63; and commerceOrderId = &#63; and commerceDiscountId = &#63;.
	 *
	 * @param commerceAccountId the commerce account ID
	 * @param commerceOrderId the commerce order ID
	 * @param commerceDiscountId the commerce discount ID
	 * @return the matching commerce discount usage entries
	 */
	public default java.util.List<CommerceDiscountUsageEntry> findByCAI_COI_CDI(
		long commerceAccountId, long commerceOrderId, long commerceDiscountId) {

		return findByCAI_COI_CDI(
			commerceAccountId, commerceOrderId, commerceDiscountId,
			com.liferay.portal.kernel.dao.orm.QueryUtil.ALL_POS,
			com.liferay.portal.kernel.dao.orm.QueryUtil.ALL_POS, null, true);
	}

	/**
	 * Returns a range of all the commerce discount usage entries where commerceAccountId = &#63; and commerceOrderId = &#63; and commerceDiscountId = &#63;.
	 *
	 * <p>
	 * Useful when paginating results. Returns a maximum of <code>end - start</code> instances. <code>start</code> and <code>end</code> are not primary keys, they are indexes in the result set. Thus, <code>0</code> refers to the first result in the set. Setting both <code>start</code> and <code>end</code> to <code>com.liferay.portal.kernel.dao.orm.QueryUtil#ALL_POS</code> will return the full result set. If <code>orderByComparator</code> is specified, then the query will include the given ORDER BY logic. If <code>orderByComparator</code> is absent, then the query will include the default ORDER BY logic from <code>com.liferay.commerce.discount.model.impl.CommerceDiscountUsageEntryModelImpl</code>.
	 * </p>
	 *
	 * @param commerceAccountId the commerce account ID
	 * @param commerceOrderId the commerce order ID
	 * @param commerceDiscountId the commerce discount ID
	 * @param start the lower bound of the range of commerce discount usage entries
	 * @param end the upper bound of the range of commerce discount usage entries (not inclusive)
	 * @return the range of matching commerce discount usage entries
	 */
	public default java.util.List<CommerceDiscountUsageEntry> findByCAI_COI_CDI(
		long commerceAccountId, long commerceOrderId, long commerceDiscountId,
		int start, int end) {

		return findByCAI_COI_CDI(
			commerceAccountId, commerceOrderId, commerceDiscountId, start, end,
			null, true);
	}

	/**
	 * Returns an ordered range of all the commerce discount usage entries where commerceAccountId = &#63; and commerceOrderId = &#63; and commerceDiscountId = &#63;.
	 *
	 * <p>
	 * Useful when paginating results. Returns a maximum of <code>end - start</code> instances. <code>start</code> and <code>end</code> are not primary keys, they are indexes in the result set. Thus, <code>0</code> refers to the first result in the set. Setting both <code>start</code> and <code>end</code> to <code>com.liferay.portal.kernel.dao.orm.QueryUtil#ALL_POS</code> will return the full result set. If <code>orderByComparator</code> is specified, then the query will include the given ORDER BY logic. If <code>orderByComparator</code> is absent, then the query will include the default ORDER BY logic from <code>com.liferay.commerce.discount.model.impl.CommerceDiscountUsageEntryModelImpl</code>.
	 * </p>
	 *
	 * @param commerceAccountId the commerce account ID
	 * @param commerceOrderId the commerce order ID
	 * @param commerceDiscountId the commerce discount ID
	 * @param start the lower bound of the range of commerce discount usage entries
	 * @param end the upper bound of the range of commerce discount usage entries (not inclusive)
	 * @param orderByComparator the comparator to order the results by (optionally <code>null</code>)
	 * @return the ordered range of matching commerce discount usage entries
	 */
	public default java.util.List<CommerceDiscountUsageEntry> findByCAI_COI_CDI(
		long commerceAccountId, long commerceOrderId, long commerceDiscountId,
		int start, int end,
		com.liferay.portal.kernel.util.OrderByComparator
			<CommerceDiscountUsageEntry> orderByComparator) {

		return findByCAI_COI_CDI(
			commerceAccountId, commerceOrderId, commerceDiscountId, start, end,
			orderByComparator, true);
	}

}
// LIFERAY-SERVICE-BUILDER-HASH:203470934