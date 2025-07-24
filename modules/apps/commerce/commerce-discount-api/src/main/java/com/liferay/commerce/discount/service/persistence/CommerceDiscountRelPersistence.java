/**
 * SPDX-FileCopyrightText: (c) 2000 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

package com.liferay.commerce.discount.service.persistence;

import com.liferay.commerce.discount.exception.NoSuchDiscountRelException;
import com.liferay.commerce.discount.model.CommerceDiscountRel;
import com.liferay.portal.kernel.service.persistence.BasePersistence;

import org.osgi.annotation.versioning.ProviderType;

/**
 * The persistence interface for the commerce discount rel service.
 *
 * <p>
 * Caching information and settings can be found in <code>portal.properties</code>
 * </p>
 *
 * @author Marco Leo
 * @see CommerceDiscountRelUtil
 * @generated
 */
@ProviderType
public interface CommerceDiscountRelPersistence
	extends BasePersistence<CommerceDiscountRel> {

	/*
	 * NOTE FOR DEVELOPERS:
	 *
	 * Never modify or reference this interface directly. Always use {@link CommerceDiscountRelUtil} to access the commerce discount rel persistence. Modify <code>service.xml</code> and rerun ServiceBuilder to regenerate this interface.
	 */

	/**
	 * Returns all the commerce discount rels where commerceDiscountId = &#63;.
	 *
	 * @param commerceDiscountId the commerce discount ID
	 * @return the matching commerce discount rels
	 */
	public java.util.List<CommerceDiscountRel> findByCommerceDiscountId(
		long commerceDiscountId);

	/**
	 * Returns a range of all the commerce discount rels where commerceDiscountId = &#63;.
	 *
	 * <p>
	 * Useful when paginating results. Returns a maximum of <code>end - start</code> instances. <code>start</code> and <code>end</code> are not primary keys, they are indexes in the result set. Thus, <code>0</code> refers to the first result in the set. Setting both <code>start</code> and <code>end</code> to <code>QueryUtil#ALL_POS</code> will return the full result set. If <code>orderByComparator</code> is specified, then the query will include the given ORDER BY logic. If <code>orderByComparator</code> is absent, then the query will include the default ORDER BY logic from <code>CommerceDiscountRelModelImpl</code>.
	 * </p>
	 *
	 * @param commerceDiscountId the commerce discount ID
	 * @param start the lower bound of the range of commerce discount rels
	 * @param end the upper bound of the range of commerce discount rels (not inclusive)
	 * @return the range of matching commerce discount rels
	 */
	public java.util.List<CommerceDiscountRel> findByCommerceDiscountId(
		long commerceDiscountId, int start, int end);

	/**
	 * Returns an ordered range of all the commerce discount rels where commerceDiscountId = &#63;.
	 *
	 * <p>
	 * Useful when paginating results. Returns a maximum of <code>end - start</code> instances. <code>start</code> and <code>end</code> are not primary keys, they are indexes in the result set. Thus, <code>0</code> refers to the first result in the set. Setting both <code>start</code> and <code>end</code> to <code>QueryUtil#ALL_POS</code> will return the full result set. If <code>orderByComparator</code> is specified, then the query will include the given ORDER BY logic. If <code>orderByComparator</code> is absent, then the query will include the default ORDER BY logic from <code>CommerceDiscountRelModelImpl</code>.
	 * </p>
	 *
	 * @param commerceDiscountId the commerce discount ID
	 * @param start the lower bound of the range of commerce discount rels
	 * @param end the upper bound of the range of commerce discount rels (not inclusive)
	 * @param orderByComparator the comparator to order the results by (optionally <code>null</code>)
	 * @return the ordered range of matching commerce discount rels
	 */
	public java.util.List<CommerceDiscountRel> findByCommerceDiscountId(
		long commerceDiscountId, int start, int end,
		com.liferay.portal.kernel.util.OrderByComparator<CommerceDiscountRel>
			orderByComparator);

	/**
	 * Returns an ordered range of all the commerce discount rels where commerceDiscountId = &#63;.
	 *
	 * <p>
	 * Useful when paginating results. Returns a maximum of <code>end - start</code> instances. <code>start</code> and <code>end</code> are not primary keys, they are indexes in the result set. Thus, <code>0</code> refers to the first result in the set. Setting both <code>start</code> and <code>end</code> to <code>QueryUtil#ALL_POS</code> will return the full result set. If <code>orderByComparator</code> is specified, then the query will include the given ORDER BY logic. If <code>orderByComparator</code> is absent, then the query will include the default ORDER BY logic from <code>CommerceDiscountRelModelImpl</code>.
	 * </p>
	 *
	 * @param commerceDiscountId the commerce discount ID
	 * @param start the lower bound of the range of commerce discount rels
	 * @param end the upper bound of the range of commerce discount rels (not inclusive)
	 * @param orderByComparator the comparator to order the results by (optionally <code>null</code>)
	 * @param useFinderCache whether to use the finder cache
	 * @return the ordered range of matching commerce discount rels
	 */
	public java.util.List<CommerceDiscountRel> findByCommerceDiscountId(
		long commerceDiscountId, int start, int end,
		com.liferay.portal.kernel.util.OrderByComparator<CommerceDiscountRel>
			orderByComparator,
		boolean useFinderCache);

	/**
	 * Returns the first commerce discount rel in the ordered set where commerceDiscountId = &#63;.
	 *
	 * @param commerceDiscountId the commerce discount ID
	 * @param orderByComparator the comparator to order the set by (optionally <code>null</code>)
	 * @return the first matching commerce discount rel
	 * @throws NoSuchDiscountRelException if a matching commerce discount rel could not be found
	 */
	public CommerceDiscountRel findByCommerceDiscountId_First(
			long commerceDiscountId,
			com.liferay.portal.kernel.util.OrderByComparator
				<CommerceDiscountRel> orderByComparator)
		throws NoSuchDiscountRelException;

	/**
	 * Returns the first commerce discount rel in the ordered set where commerceDiscountId = &#63;.
	 *
	 * @param commerceDiscountId the commerce discount ID
	 * @param orderByComparator the comparator to order the set by (optionally <code>null</code>)
	 * @return the first matching commerce discount rel, or <code>null</code> if a matching commerce discount rel could not be found
	 */
	public CommerceDiscountRel fetchByCommerceDiscountId_First(
		long commerceDiscountId,
		com.liferay.portal.kernel.util.OrderByComparator<CommerceDiscountRel>
			orderByComparator);

	/**
	 * Returns the last commerce discount rel in the ordered set where commerceDiscountId = &#63;.
	 *
	 * @param commerceDiscountId the commerce discount ID
	 * @param orderByComparator the comparator to order the set by (optionally <code>null</code>)
	 * @return the last matching commerce discount rel
	 * @throws NoSuchDiscountRelException if a matching commerce discount rel could not be found
	 */
	public CommerceDiscountRel findByCommerceDiscountId_Last(
			long commerceDiscountId,
			com.liferay.portal.kernel.util.OrderByComparator
				<CommerceDiscountRel> orderByComparator)
		throws NoSuchDiscountRelException;

	/**
	 * Returns the last commerce discount rel in the ordered set where commerceDiscountId = &#63;.
	 *
	 * @param commerceDiscountId the commerce discount ID
	 * @param orderByComparator the comparator to order the set by (optionally <code>null</code>)
	 * @return the last matching commerce discount rel, or <code>null</code> if a matching commerce discount rel could not be found
	 */
	public CommerceDiscountRel fetchByCommerceDiscountId_Last(
		long commerceDiscountId,
		com.liferay.portal.kernel.util.OrderByComparator<CommerceDiscountRel>
			orderByComparator);

	/**
	 * Returns the commerce discount rels before and after the current commerce discount rel in the ordered set where commerceDiscountId = &#63;.
	 *
	 * @param commerceDiscountRelId the primary key of the current commerce discount rel
	 * @param commerceDiscountId the commerce discount ID
	 * @param orderByComparator the comparator to order the set by (optionally <code>null</code>)
	 * @return the previous, current, and next commerce discount rel
	 * @throws NoSuchDiscountRelException if a commerce discount rel with the primary key could not be found
	 */
	public CommerceDiscountRel[] findByCommerceDiscountId_PrevAndNext(
			long commerceDiscountRelId, long commerceDiscountId,
			com.liferay.portal.kernel.util.OrderByComparator
				<CommerceDiscountRel> orderByComparator)
		throws NoSuchDiscountRelException;

	/**
	 * Removes all the commerce discount rels where commerceDiscountId = &#63; from the database.
	 *
	 * @param commerceDiscountId the commerce discount ID
	 */
	public void removeByCommerceDiscountId(long commerceDiscountId);

	/**
	 * Returns the number of commerce discount rels where commerceDiscountId = &#63;.
	 *
	 * @param commerceDiscountId the commerce discount ID
	 * @return the number of matching commerce discount rels
	 */
	public int countByCommerceDiscountId(long commerceDiscountId);

	/**
	 * Returns all the commerce discount rels where commerceDiscountId = &#63; and classNameId = &#63;.
	 *
	 * @param commerceDiscountId the commerce discount ID
	 * @param classNameId the class name ID
	 * @return the matching commerce discount rels
	 */
	public java.util.List<CommerceDiscountRel> findByCD_CN(
		long commerceDiscountId, long classNameId);

	/**
	 * Returns a range of all the commerce discount rels where commerceDiscountId = &#63; and classNameId = &#63;.
	 *
	 * <p>
	 * Useful when paginating results. Returns a maximum of <code>end - start</code> instances. <code>start</code> and <code>end</code> are not primary keys, they are indexes in the result set. Thus, <code>0</code> refers to the first result in the set. Setting both <code>start</code> and <code>end</code> to <code>QueryUtil#ALL_POS</code> will return the full result set. If <code>orderByComparator</code> is specified, then the query will include the given ORDER BY logic. If <code>orderByComparator</code> is absent, then the query will include the default ORDER BY logic from <code>CommerceDiscountRelModelImpl</code>.
	 * </p>
	 *
	 * @param commerceDiscountId the commerce discount ID
	 * @param classNameId the class name ID
	 * @param start the lower bound of the range of commerce discount rels
	 * @param end the upper bound of the range of commerce discount rels (not inclusive)
	 * @return the range of matching commerce discount rels
	 */
	public java.util.List<CommerceDiscountRel> findByCD_CN(
		long commerceDiscountId, long classNameId, int start, int end);

	/**
	 * Returns an ordered range of all the commerce discount rels where commerceDiscountId = &#63; and classNameId = &#63;.
	 *
	 * <p>
	 * Useful when paginating results. Returns a maximum of <code>end - start</code> instances. <code>start</code> and <code>end</code> are not primary keys, they are indexes in the result set. Thus, <code>0</code> refers to the first result in the set. Setting both <code>start</code> and <code>end</code> to <code>QueryUtil#ALL_POS</code> will return the full result set. If <code>orderByComparator</code> is specified, then the query will include the given ORDER BY logic. If <code>orderByComparator</code> is absent, then the query will include the default ORDER BY logic from <code>CommerceDiscountRelModelImpl</code>.
	 * </p>
	 *
	 * @param commerceDiscountId the commerce discount ID
	 * @param classNameId the class name ID
	 * @param start the lower bound of the range of commerce discount rels
	 * @param end the upper bound of the range of commerce discount rels (not inclusive)
	 * @param orderByComparator the comparator to order the results by (optionally <code>null</code>)
	 * @return the ordered range of matching commerce discount rels
	 */
	public java.util.List<CommerceDiscountRel> findByCD_CN(
		long commerceDiscountId, long classNameId, int start, int end,
		com.liferay.portal.kernel.util.OrderByComparator<CommerceDiscountRel>
			orderByComparator);

	/**
	 * Returns an ordered range of all the commerce discount rels where commerceDiscountId = &#63; and classNameId = &#63;.
	 *
	 * <p>
	 * Useful when paginating results. Returns a maximum of <code>end - start</code> instances. <code>start</code> and <code>end</code> are not primary keys, they are indexes in the result set. Thus, <code>0</code> refers to the first result in the set. Setting both <code>start</code> and <code>end</code> to <code>QueryUtil#ALL_POS</code> will return the full result set. If <code>orderByComparator</code> is specified, then the query will include the given ORDER BY logic. If <code>orderByComparator</code> is absent, then the query will include the default ORDER BY logic from <code>CommerceDiscountRelModelImpl</code>.
	 * </p>
	 *
	 * @param commerceDiscountId the commerce discount ID
	 * @param classNameId the class name ID
	 * @param start the lower bound of the range of commerce discount rels
	 * @param end the upper bound of the range of commerce discount rels (not inclusive)
	 * @param orderByComparator the comparator to order the results by (optionally <code>null</code>)
	 * @param useFinderCache whether to use the finder cache
	 * @return the ordered range of matching commerce discount rels
	 */
	public java.util.List<CommerceDiscountRel> findByCD_CN(
		long commerceDiscountId, long classNameId, int start, int end,
		com.liferay.portal.kernel.util.OrderByComparator<CommerceDiscountRel>
			orderByComparator,
		boolean useFinderCache);

	/**
	 * Returns the first commerce discount rel in the ordered set where commerceDiscountId = &#63; and classNameId = &#63;.
	 *
	 * @param commerceDiscountId the commerce discount ID
	 * @param classNameId the class name ID
	 * @param orderByComparator the comparator to order the set by (optionally <code>null</code>)
	 * @return the first matching commerce discount rel
	 * @throws NoSuchDiscountRelException if a matching commerce discount rel could not be found
	 */
	public CommerceDiscountRel findByCD_CN_First(
			long commerceDiscountId, long classNameId,
			com.liferay.portal.kernel.util.OrderByComparator
				<CommerceDiscountRel> orderByComparator)
		throws NoSuchDiscountRelException;

	/**
	 * Returns the first commerce discount rel in the ordered set where commerceDiscountId = &#63; and classNameId = &#63;.
	 *
	 * @param commerceDiscountId the commerce discount ID
	 * @param classNameId the class name ID
	 * @param orderByComparator the comparator to order the set by (optionally <code>null</code>)
	 * @return the first matching commerce discount rel, or <code>null</code> if a matching commerce discount rel could not be found
	 */
	public CommerceDiscountRel fetchByCD_CN_First(
		long commerceDiscountId, long classNameId,
		com.liferay.portal.kernel.util.OrderByComparator<CommerceDiscountRel>
			orderByComparator);

	/**
	 * Returns the last commerce discount rel in the ordered set where commerceDiscountId = &#63; and classNameId = &#63;.
	 *
	 * @param commerceDiscountId the commerce discount ID
	 * @param classNameId the class name ID
	 * @param orderByComparator the comparator to order the set by (optionally <code>null</code>)
	 * @return the last matching commerce discount rel
	 * @throws NoSuchDiscountRelException if a matching commerce discount rel could not be found
	 */
	public CommerceDiscountRel findByCD_CN_Last(
			long commerceDiscountId, long classNameId,
			com.liferay.portal.kernel.util.OrderByComparator
				<CommerceDiscountRel> orderByComparator)
		throws NoSuchDiscountRelException;

	/**
	 * Returns the last commerce discount rel in the ordered set where commerceDiscountId = &#63; and classNameId = &#63;.
	 *
	 * @param commerceDiscountId the commerce discount ID
	 * @param classNameId the class name ID
	 * @param orderByComparator the comparator to order the set by (optionally <code>null</code>)
	 * @return the last matching commerce discount rel, or <code>null</code> if a matching commerce discount rel could not be found
	 */
	public CommerceDiscountRel fetchByCD_CN_Last(
		long commerceDiscountId, long classNameId,
		com.liferay.portal.kernel.util.OrderByComparator<CommerceDiscountRel>
			orderByComparator);

	/**
	 * Returns the commerce discount rels before and after the current commerce discount rel in the ordered set where commerceDiscountId = &#63; and classNameId = &#63;.
	 *
	 * @param commerceDiscountRelId the primary key of the current commerce discount rel
	 * @param commerceDiscountId the commerce discount ID
	 * @param classNameId the class name ID
	 * @param orderByComparator the comparator to order the set by (optionally <code>null</code>)
	 * @return the previous, current, and next commerce discount rel
	 * @throws NoSuchDiscountRelException if a commerce discount rel with the primary key could not be found
	 */
	public CommerceDiscountRel[] findByCD_CN_PrevAndNext(
			long commerceDiscountRelId, long commerceDiscountId,
			long classNameId,
			com.liferay.portal.kernel.util.OrderByComparator
				<CommerceDiscountRel> orderByComparator)
		throws NoSuchDiscountRelException;

	/**
	 * Removes all the commerce discount rels where commerceDiscountId = &#63; and classNameId = &#63; from the database.
	 *
	 * @param commerceDiscountId the commerce discount ID
	 * @param classNameId the class name ID
	 */
	public void removeByCD_CN(long commerceDiscountId, long classNameId);

	/**
	 * Returns the number of commerce discount rels where commerceDiscountId = &#63; and classNameId = &#63;.
	 *
	 * @param commerceDiscountId the commerce discount ID
	 * @param classNameId the class name ID
	 * @return the number of matching commerce discount rels
	 */
	public int countByCD_CN(long commerceDiscountId, long classNameId);

	/**
	 * Returns all the commerce discount rels where classNameId = &#63; and classPK = &#63;.
	 *
	 * @param classNameId the class name ID
	 * @param classPK the class pk
	 * @return the matching commerce discount rels
	 */
	public java.util.List<CommerceDiscountRel> findByCN_CPK(
		long classNameId, long classPK);

	/**
	 * Returns a range of all the commerce discount rels where classNameId = &#63; and classPK = &#63;.
	 *
	 * <p>
	 * Useful when paginating results. Returns a maximum of <code>end - start</code> instances. <code>start</code> and <code>end</code> are not primary keys, they are indexes in the result set. Thus, <code>0</code> refers to the first result in the set. Setting both <code>start</code> and <code>end</code> to <code>QueryUtil#ALL_POS</code> will return the full result set. If <code>orderByComparator</code> is specified, then the query will include the given ORDER BY logic. If <code>orderByComparator</code> is absent, then the query will include the default ORDER BY logic from <code>CommerceDiscountRelModelImpl</code>.
	 * </p>
	 *
	 * @param classNameId the class name ID
	 * @param classPK the class pk
	 * @param start the lower bound of the range of commerce discount rels
	 * @param end the upper bound of the range of commerce discount rels (not inclusive)
	 * @return the range of matching commerce discount rels
	 */
	public java.util.List<CommerceDiscountRel> findByCN_CPK(
		long classNameId, long classPK, int start, int end);

	/**
	 * Returns an ordered range of all the commerce discount rels where classNameId = &#63; and classPK = &#63;.
	 *
	 * <p>
	 * Useful when paginating results. Returns a maximum of <code>end - start</code> instances. <code>start</code> and <code>end</code> are not primary keys, they are indexes in the result set. Thus, <code>0</code> refers to the first result in the set. Setting both <code>start</code> and <code>end</code> to <code>QueryUtil#ALL_POS</code> will return the full result set. If <code>orderByComparator</code> is specified, then the query will include the given ORDER BY logic. If <code>orderByComparator</code> is absent, then the query will include the default ORDER BY logic from <code>CommerceDiscountRelModelImpl</code>.
	 * </p>
	 *
	 * @param classNameId the class name ID
	 * @param classPK the class pk
	 * @param start the lower bound of the range of commerce discount rels
	 * @param end the upper bound of the range of commerce discount rels (not inclusive)
	 * @param orderByComparator the comparator to order the results by (optionally <code>null</code>)
	 * @return the ordered range of matching commerce discount rels
	 */
	public java.util.List<CommerceDiscountRel> findByCN_CPK(
		long classNameId, long classPK, int start, int end,
		com.liferay.portal.kernel.util.OrderByComparator<CommerceDiscountRel>
			orderByComparator);

	/**
	 * Returns an ordered range of all the commerce discount rels where classNameId = &#63; and classPK = &#63;.
	 *
	 * <p>
	 * Useful when paginating results. Returns a maximum of <code>end - start</code> instances. <code>start</code> and <code>end</code> are not primary keys, they are indexes in the result set. Thus, <code>0</code> refers to the first result in the set. Setting both <code>start</code> and <code>end</code> to <code>QueryUtil#ALL_POS</code> will return the full result set. If <code>orderByComparator</code> is specified, then the query will include the given ORDER BY logic. If <code>orderByComparator</code> is absent, then the query will include the default ORDER BY logic from <code>CommerceDiscountRelModelImpl</code>.
	 * </p>
	 *
	 * @param classNameId the class name ID
	 * @param classPK the class pk
	 * @param start the lower bound of the range of commerce discount rels
	 * @param end the upper bound of the range of commerce discount rels (not inclusive)
	 * @param orderByComparator the comparator to order the results by (optionally <code>null</code>)
	 * @param useFinderCache whether to use the finder cache
	 * @return the ordered range of matching commerce discount rels
	 */
	public java.util.List<CommerceDiscountRel> findByCN_CPK(
		long classNameId, long classPK, int start, int end,
		com.liferay.portal.kernel.util.OrderByComparator<CommerceDiscountRel>
			orderByComparator,
		boolean useFinderCache);

	/**
	 * Returns the first commerce discount rel in the ordered set where classNameId = &#63; and classPK = &#63;.
	 *
	 * @param classNameId the class name ID
	 * @param classPK the class pk
	 * @param orderByComparator the comparator to order the set by (optionally <code>null</code>)
	 * @return the first matching commerce discount rel
	 * @throws NoSuchDiscountRelException if a matching commerce discount rel could not be found
	 */
	public CommerceDiscountRel findByCN_CPK_First(
			long classNameId, long classPK,
			com.liferay.portal.kernel.util.OrderByComparator
				<CommerceDiscountRel> orderByComparator)
		throws NoSuchDiscountRelException;

	/**
	 * Returns the first commerce discount rel in the ordered set where classNameId = &#63; and classPK = &#63;.
	 *
	 * @param classNameId the class name ID
	 * @param classPK the class pk
	 * @param orderByComparator the comparator to order the set by (optionally <code>null</code>)
	 * @return the first matching commerce discount rel, or <code>null</code> if a matching commerce discount rel could not be found
	 */
	public CommerceDiscountRel fetchByCN_CPK_First(
		long classNameId, long classPK,
		com.liferay.portal.kernel.util.OrderByComparator<CommerceDiscountRel>
			orderByComparator);

	/**
	 * Returns the last commerce discount rel in the ordered set where classNameId = &#63; and classPK = &#63;.
	 *
	 * @param classNameId the class name ID
	 * @param classPK the class pk
	 * @param orderByComparator the comparator to order the set by (optionally <code>null</code>)
	 * @return the last matching commerce discount rel
	 * @throws NoSuchDiscountRelException if a matching commerce discount rel could not be found
	 */
	public CommerceDiscountRel findByCN_CPK_Last(
			long classNameId, long classPK,
			com.liferay.portal.kernel.util.OrderByComparator
				<CommerceDiscountRel> orderByComparator)
		throws NoSuchDiscountRelException;

	/**
	 * Returns the last commerce discount rel in the ordered set where classNameId = &#63; and classPK = &#63;.
	 *
	 * @param classNameId the class name ID
	 * @param classPK the class pk
	 * @param orderByComparator the comparator to order the set by (optionally <code>null</code>)
	 * @return the last matching commerce discount rel, or <code>null</code> if a matching commerce discount rel could not be found
	 */
	public CommerceDiscountRel fetchByCN_CPK_Last(
		long classNameId, long classPK,
		com.liferay.portal.kernel.util.OrderByComparator<CommerceDiscountRel>
			orderByComparator);

	/**
	 * Returns the commerce discount rels before and after the current commerce discount rel in the ordered set where classNameId = &#63; and classPK = &#63;.
	 *
	 * @param commerceDiscountRelId the primary key of the current commerce discount rel
	 * @param classNameId the class name ID
	 * @param classPK the class pk
	 * @param orderByComparator the comparator to order the set by (optionally <code>null</code>)
	 * @return the previous, current, and next commerce discount rel
	 * @throws NoSuchDiscountRelException if a commerce discount rel with the primary key could not be found
	 */
	public CommerceDiscountRel[] findByCN_CPK_PrevAndNext(
			long commerceDiscountRelId, long classNameId, long classPK,
			com.liferay.portal.kernel.util.OrderByComparator
				<CommerceDiscountRel> orderByComparator)
		throws NoSuchDiscountRelException;

	/**
	 * Removes all the commerce discount rels where classNameId = &#63; and classPK = &#63; from the database.
	 *
	 * @param classNameId the class name ID
	 * @param classPK the class pk
	 */
	public void removeByCN_CPK(long classNameId, long classPK);

	/**
	 * Returns the number of commerce discount rels where classNameId = &#63; and classPK = &#63;.
	 *
	 * @param classNameId the class name ID
	 * @param classPK the class pk
	 * @return the number of matching commerce discount rels
	 */
	public int countByCN_CPK(long classNameId, long classPK);

	/**
	 * Returns all the commerce discount rels where commerceDiscountId = &#63; and classNameId = &#63; and classPK = &#63;.
	 *
	 * @param commerceDiscountId the commerce discount ID
	 * @param classNameId the class name ID
	 * @param classPK the class pk
	 * @return the matching commerce discount rels
	 */
	public java.util.List<CommerceDiscountRel> findByCD_CN_CPK(
		long commerceDiscountId, long classNameId, long classPK);

	/**
	 * Returns a range of all the commerce discount rels where commerceDiscountId = &#63; and classNameId = &#63; and classPK = &#63;.
	 *
	 * <p>
	 * Useful when paginating results. Returns a maximum of <code>end - start</code> instances. <code>start</code> and <code>end</code> are not primary keys, they are indexes in the result set. Thus, <code>0</code> refers to the first result in the set. Setting both <code>start</code> and <code>end</code> to <code>QueryUtil#ALL_POS</code> will return the full result set. If <code>orderByComparator</code> is specified, then the query will include the given ORDER BY logic. If <code>orderByComparator</code> is absent, then the query will include the default ORDER BY logic from <code>CommerceDiscountRelModelImpl</code>.
	 * </p>
	 *
	 * @param commerceDiscountId the commerce discount ID
	 * @param classNameId the class name ID
	 * @param classPK the class pk
	 * @param start the lower bound of the range of commerce discount rels
	 * @param end the upper bound of the range of commerce discount rels (not inclusive)
	 * @return the range of matching commerce discount rels
	 */
	public java.util.List<CommerceDiscountRel> findByCD_CN_CPK(
		long commerceDiscountId, long classNameId, long classPK, int start,
		int end);

	/**
	 * Returns an ordered range of all the commerce discount rels where commerceDiscountId = &#63; and classNameId = &#63; and classPK = &#63;.
	 *
	 * <p>
	 * Useful when paginating results. Returns a maximum of <code>end - start</code> instances. <code>start</code> and <code>end</code> are not primary keys, they are indexes in the result set. Thus, <code>0</code> refers to the first result in the set. Setting both <code>start</code> and <code>end</code> to <code>QueryUtil#ALL_POS</code> will return the full result set. If <code>orderByComparator</code> is specified, then the query will include the given ORDER BY logic. If <code>orderByComparator</code> is absent, then the query will include the default ORDER BY logic from <code>CommerceDiscountRelModelImpl</code>.
	 * </p>
	 *
	 * @param commerceDiscountId the commerce discount ID
	 * @param classNameId the class name ID
	 * @param classPK the class pk
	 * @param start the lower bound of the range of commerce discount rels
	 * @param end the upper bound of the range of commerce discount rels (not inclusive)
	 * @param orderByComparator the comparator to order the results by (optionally <code>null</code>)
	 * @return the ordered range of matching commerce discount rels
	 */
	public java.util.List<CommerceDiscountRel> findByCD_CN_CPK(
		long commerceDiscountId, long classNameId, long classPK, int start,
		int end,
		com.liferay.portal.kernel.util.OrderByComparator<CommerceDiscountRel>
			orderByComparator);

	/**
	 * Returns an ordered range of all the commerce discount rels where commerceDiscountId = &#63; and classNameId = &#63; and classPK = &#63;.
	 *
	 * <p>
	 * Useful when paginating results. Returns a maximum of <code>end - start</code> instances. <code>start</code> and <code>end</code> are not primary keys, they are indexes in the result set. Thus, <code>0</code> refers to the first result in the set. Setting both <code>start</code> and <code>end</code> to <code>QueryUtil#ALL_POS</code> will return the full result set. If <code>orderByComparator</code> is specified, then the query will include the given ORDER BY logic. If <code>orderByComparator</code> is absent, then the query will include the default ORDER BY logic from <code>CommerceDiscountRelModelImpl</code>.
	 * </p>
	 *
	 * @param commerceDiscountId the commerce discount ID
	 * @param classNameId the class name ID
	 * @param classPK the class pk
	 * @param start the lower bound of the range of commerce discount rels
	 * @param end the upper bound of the range of commerce discount rels (not inclusive)
	 * @param orderByComparator the comparator to order the results by (optionally <code>null</code>)
	 * @param useFinderCache whether to use the finder cache
	 * @return the ordered range of matching commerce discount rels
	 */
	public java.util.List<CommerceDiscountRel> findByCD_CN_CPK(
		long commerceDiscountId, long classNameId, long classPK, int start,
		int end,
		com.liferay.portal.kernel.util.OrderByComparator<CommerceDiscountRel>
			orderByComparator,
		boolean useFinderCache);

	/**
	 * Returns the first commerce discount rel in the ordered set where commerceDiscountId = &#63; and classNameId = &#63; and classPK = &#63;.
	 *
	 * @param commerceDiscountId the commerce discount ID
	 * @param classNameId the class name ID
	 * @param classPK the class pk
	 * @param orderByComparator the comparator to order the set by (optionally <code>null</code>)
	 * @return the first matching commerce discount rel
	 * @throws NoSuchDiscountRelException if a matching commerce discount rel could not be found
	 */
	public CommerceDiscountRel findByCD_CN_CPK_First(
			long commerceDiscountId, long classNameId, long classPK,
			com.liferay.portal.kernel.util.OrderByComparator
				<CommerceDiscountRel> orderByComparator)
		throws NoSuchDiscountRelException;

	/**
	 * Returns the first commerce discount rel in the ordered set where commerceDiscountId = &#63; and classNameId = &#63; and classPK = &#63;.
	 *
	 * @param commerceDiscountId the commerce discount ID
	 * @param classNameId the class name ID
	 * @param classPK the class pk
	 * @param orderByComparator the comparator to order the set by (optionally <code>null</code>)
	 * @return the first matching commerce discount rel, or <code>null</code> if a matching commerce discount rel could not be found
	 */
	public CommerceDiscountRel fetchByCD_CN_CPK_First(
		long commerceDiscountId, long classNameId, long classPK,
		com.liferay.portal.kernel.util.OrderByComparator<CommerceDiscountRel>
			orderByComparator);

	/**
	 * Returns the last commerce discount rel in the ordered set where commerceDiscountId = &#63; and classNameId = &#63; and classPK = &#63;.
	 *
	 * @param commerceDiscountId the commerce discount ID
	 * @param classNameId the class name ID
	 * @param classPK the class pk
	 * @param orderByComparator the comparator to order the set by (optionally <code>null</code>)
	 * @return the last matching commerce discount rel
	 * @throws NoSuchDiscountRelException if a matching commerce discount rel could not be found
	 */
	public CommerceDiscountRel findByCD_CN_CPK_Last(
			long commerceDiscountId, long classNameId, long classPK,
			com.liferay.portal.kernel.util.OrderByComparator
				<CommerceDiscountRel> orderByComparator)
		throws NoSuchDiscountRelException;

	/**
	 * Returns the last commerce discount rel in the ordered set where commerceDiscountId = &#63; and classNameId = &#63; and classPK = &#63;.
	 *
	 * @param commerceDiscountId the commerce discount ID
	 * @param classNameId the class name ID
	 * @param classPK the class pk
	 * @param orderByComparator the comparator to order the set by (optionally <code>null</code>)
	 * @return the last matching commerce discount rel, or <code>null</code> if a matching commerce discount rel could not be found
	 */
	public CommerceDiscountRel fetchByCD_CN_CPK_Last(
		long commerceDiscountId, long classNameId, long classPK,
		com.liferay.portal.kernel.util.OrderByComparator<CommerceDiscountRel>
			orderByComparator);

	/**
	 * Returns the commerce discount rels before and after the current commerce discount rel in the ordered set where commerceDiscountId = &#63; and classNameId = &#63; and classPK = &#63;.
	 *
	 * @param commerceDiscountRelId the primary key of the current commerce discount rel
	 * @param commerceDiscountId the commerce discount ID
	 * @param classNameId the class name ID
	 * @param classPK the class pk
	 * @param orderByComparator the comparator to order the set by (optionally <code>null</code>)
	 * @return the previous, current, and next commerce discount rel
	 * @throws NoSuchDiscountRelException if a commerce discount rel with the primary key could not be found
	 */
	public CommerceDiscountRel[] findByCD_CN_CPK_PrevAndNext(
			long commerceDiscountRelId, long commerceDiscountId,
			long classNameId, long classPK,
			com.liferay.portal.kernel.util.OrderByComparator
				<CommerceDiscountRel> orderByComparator)
		throws NoSuchDiscountRelException;

	/**
	 * Removes all the commerce discount rels where commerceDiscountId = &#63; and classNameId = &#63; and classPK = &#63; from the database.
	 *
	 * @param commerceDiscountId the commerce discount ID
	 * @param classNameId the class name ID
	 * @param classPK the class pk
	 */
	public void removeByCD_CN_CPK(
		long commerceDiscountId, long classNameId, long classPK);

	/**
	 * Returns the number of commerce discount rels where commerceDiscountId = &#63; and classNameId = &#63; and classPK = &#63;.
	 *
	 * @param commerceDiscountId the commerce discount ID
	 * @param classNameId the class name ID
	 * @param classPK the class pk
	 * @return the number of matching commerce discount rels
	 */
	public int countByCD_CN_CPK(
		long commerceDiscountId, long classNameId, long classPK);

	/**
	 * Caches the commerce discount rel in the entity cache if it is enabled.
	 *
	 * @param commerceDiscountRel the commerce discount rel
	 */
	public void cacheResult(CommerceDiscountRel commerceDiscountRel);

	/**
	 * Caches the commerce discount rels in the entity cache if it is enabled.
	 *
	 * @param commerceDiscountRels the commerce discount rels
	 */
	public void cacheResult(
		java.util.List<CommerceDiscountRel> commerceDiscountRels);

	/**
	 * Creates a new commerce discount rel with the primary key. Does not add the commerce discount rel to the database.
	 *
	 * @param commerceDiscountRelId the primary key for the new commerce discount rel
	 * @return the new commerce discount rel
	 */
	public CommerceDiscountRel create(long commerceDiscountRelId);

	/**
	 * Removes the commerce discount rel with the primary key from the database. Also notifies the appropriate model listeners.
	 *
	 * @param commerceDiscountRelId the primary key of the commerce discount rel
	 * @return the commerce discount rel that was removed
	 * @throws NoSuchDiscountRelException if a commerce discount rel with the primary key could not be found
	 */
	public CommerceDiscountRel remove(long commerceDiscountRelId)
		throws NoSuchDiscountRelException;

	public CommerceDiscountRel updateImpl(
		CommerceDiscountRel commerceDiscountRel);

	/**
	 * Returns the commerce discount rel with the primary key or throws a <code>NoSuchDiscountRelException</code> if it could not be found.
	 *
	 * @param commerceDiscountRelId the primary key of the commerce discount rel
	 * @return the commerce discount rel
	 * @throws NoSuchDiscountRelException if a commerce discount rel with the primary key could not be found
	 */
	public CommerceDiscountRel findByPrimaryKey(long commerceDiscountRelId)
		throws NoSuchDiscountRelException;

	/**
	 * Returns the commerce discount rel with the primary key or returns <code>null</code> if it could not be found.
	 *
	 * @param commerceDiscountRelId the primary key of the commerce discount rel
	 * @return the commerce discount rel, or <code>null</code> if a commerce discount rel with the primary key could not be found
	 */
	public CommerceDiscountRel fetchByPrimaryKey(long commerceDiscountRelId);

	/**
	 * Returns all the commerce discount rels.
	 *
	 * @return the commerce discount rels
	 */
	public java.util.List<CommerceDiscountRel> findAll();

	/**
	 * Returns a range of all the commerce discount rels.
	 *
	 * <p>
	 * Useful when paginating results. Returns a maximum of <code>end - start</code> instances. <code>start</code> and <code>end</code> are not primary keys, they are indexes in the result set. Thus, <code>0</code> refers to the first result in the set. Setting both <code>start</code> and <code>end</code> to <code>QueryUtil#ALL_POS</code> will return the full result set. If <code>orderByComparator</code> is specified, then the query will include the given ORDER BY logic. If <code>orderByComparator</code> is absent, then the query will include the default ORDER BY logic from <code>CommerceDiscountRelModelImpl</code>.
	 * </p>
	 *
	 * @param start the lower bound of the range of commerce discount rels
	 * @param end the upper bound of the range of commerce discount rels (not inclusive)
	 * @return the range of commerce discount rels
	 */
	public java.util.List<CommerceDiscountRel> findAll(int start, int end);

	/**
	 * Returns an ordered range of all the commerce discount rels.
	 *
	 * <p>
	 * Useful when paginating results. Returns a maximum of <code>end - start</code> instances. <code>start</code> and <code>end</code> are not primary keys, they are indexes in the result set. Thus, <code>0</code> refers to the first result in the set. Setting both <code>start</code> and <code>end</code> to <code>QueryUtil#ALL_POS</code> will return the full result set. If <code>orderByComparator</code> is specified, then the query will include the given ORDER BY logic. If <code>orderByComparator</code> is absent, then the query will include the default ORDER BY logic from <code>CommerceDiscountRelModelImpl</code>.
	 * </p>
	 *
	 * @param start the lower bound of the range of commerce discount rels
	 * @param end the upper bound of the range of commerce discount rels (not inclusive)
	 * @param orderByComparator the comparator to order the results by (optionally <code>null</code>)
	 * @return the ordered range of commerce discount rels
	 */
	public java.util.List<CommerceDiscountRel> findAll(
		int start, int end,
		com.liferay.portal.kernel.util.OrderByComparator<CommerceDiscountRel>
			orderByComparator);

	/**
	 * Returns an ordered range of all the commerce discount rels.
	 *
	 * <p>
	 * Useful when paginating results. Returns a maximum of <code>end - start</code> instances. <code>start</code> and <code>end</code> are not primary keys, they are indexes in the result set. Thus, <code>0</code> refers to the first result in the set. Setting both <code>start</code> and <code>end</code> to <code>QueryUtil#ALL_POS</code> will return the full result set. If <code>orderByComparator</code> is specified, then the query will include the given ORDER BY logic. If <code>orderByComparator</code> is absent, then the query will include the default ORDER BY logic from <code>CommerceDiscountRelModelImpl</code>.
	 * </p>
	 *
	 * @param start the lower bound of the range of commerce discount rels
	 * @param end the upper bound of the range of commerce discount rels (not inclusive)
	 * @param orderByComparator the comparator to order the results by (optionally <code>null</code>)
	 * @param useFinderCache whether to use the finder cache
	 * @return the ordered range of commerce discount rels
	 */
	public java.util.List<CommerceDiscountRel> findAll(
		int start, int end,
		com.liferay.portal.kernel.util.OrderByComparator<CommerceDiscountRel>
			orderByComparator,
		boolean useFinderCache);

	/**
	 * Removes all the commerce discount rels from the database.
	 */
	public void removeAll();

	/**
	 * Returns the number of commerce discount rels.
	 *
	 * @return the number of commerce discount rels
	 */
	public int countAll();

}