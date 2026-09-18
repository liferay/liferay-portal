/**
 * SPDX-FileCopyrightText: (c) 2000 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

package com.liferay.commerce.discount.service.persistence;

import com.liferay.commerce.discount.exception.NoSuchDiscountRuleException;
import com.liferay.commerce.discount.model.CommerceDiscountRule;
import com.liferay.portal.kernel.service.persistence.BasePersistence;

import org.osgi.annotation.versioning.ProviderType;

/**
 * The persistence interface for the commerce discount rule service.
 *
 * <p>
 * Caching information and settings can be found in <code>portal.properties</code>
 * </p>
 *
 * @author Marco Leo
 * @see CommerceDiscountRuleUtil
 * @generated
 */
@ProviderType
public interface CommerceDiscountRulePersistence
	extends BasePersistence<CommerceDiscountRule> {

	/*
	 * NOTE FOR DEVELOPERS:
	 *
	 * Never modify or reference this interface directly. Always use {@link CommerceDiscountRuleUtil} to access the commerce discount rule persistence. Modify <code>service.xml</code> and rerun ServiceBuilder to regenerate this interface.
	 */

	/**
	 * Returns an ordered range of all the commerce discount rules where commerceDiscountId = &#63;.
	 *
	 * <p>
	 * Useful when paginating results. Returns a maximum of <code>end - start</code> instances. <code>start</code> and <code>end</code> are not primary keys, they are indexes in the result set. Thus, <code>0</code> refers to the first result in the set. Setting both <code>start</code> and <code>end</code> to <code>com.liferay.portal.kernel.dao.orm.QueryUtil#ALL_POS</code> will return the full result set. If <code>orderByComparator</code> is specified, then the query will include the given ORDER BY logic. If <code>orderByComparator</code> is absent, then the query will include the default ORDER BY logic from <code>com.liferay.commerce.discount.model.impl.CommerceDiscountRuleModelImpl</code>.
	 * </p>
	 *
	 * @param commerceDiscountId the commerce discount ID
	 * @param start the lower bound of the range of commerce discount rules
	 * @param end the upper bound of the range of commerce discount rules (not inclusive)
	 * @param orderByComparator the comparator to order the results by (optionally <code>null</code>)
	 * @param useFinderCache whether to use the finder cache
	 * @return the ordered range of matching commerce discount rules
	 */
	public java.util.List<CommerceDiscountRule> findByCommerceDiscountId(
		long commerceDiscountId, int start, int end,
		com.liferay.portal.kernel.util.OrderByComparator<CommerceDiscountRule>
			orderByComparator,
		boolean useFinderCache);

	/**
	 * Returns the first commerce discount rule in the ordered set where commerceDiscountId = &#63;.
	 *
	 * @param commerceDiscountId the commerce discount ID
	 * @param orderByComparator the comparator to order the set by (optionally <code>null</code>)
	 * @return the first matching commerce discount rule
	 * @throws NoSuchDiscountRuleException if a matching commerce discount rule could not be found
	 */
	public CommerceDiscountRule findByCommerceDiscountId_First(
			long commerceDiscountId,
			com.liferay.portal.kernel.util.OrderByComparator
				<CommerceDiscountRule> orderByComparator)
		throws NoSuchDiscountRuleException;

	/**
	 * Returns the first commerce discount rule in the ordered set where commerceDiscountId = &#63;.
	 *
	 * @param commerceDiscountId the commerce discount ID
	 * @param orderByComparator the comparator to order the set by (optionally <code>null</code>)
	 * @return the first matching commerce discount rule, or <code>null</code> if a matching commerce discount rule could not be found
	 */
	public CommerceDiscountRule fetchByCommerceDiscountId_First(
		long commerceDiscountId,
		com.liferay.portal.kernel.util.OrderByComparator<CommerceDiscountRule>
			orderByComparator);

	/**
	 * Removes all the commerce discount rules where commerceDiscountId = &#63; from the database.
	 *
	 * @param commerceDiscountId the commerce discount ID
	 */
	public void removeByCommerceDiscountId(long commerceDiscountId);

	/**
	 * Returns the number of commerce discount rules where commerceDiscountId = &#63;.
	 *
	 * @param commerceDiscountId the commerce discount ID
	 * @return the number of matching commerce discount rules
	 */
	public int countByCommerceDiscountId(long commerceDiscountId);

	/**
	 * Returns the commerce discount rule where externalReferenceCode = &#63; and companyId = &#63; or throws a <code>NoSuchDiscountRuleException</code> if it could not be found.
	 *
	 * @param externalReferenceCode the external reference code
	 * @param companyId the company ID
	 * @return the matching commerce discount rule
	 * @throws NoSuchDiscountRuleException if a matching commerce discount rule could not be found
	 */
	public CommerceDiscountRule findByERC_C(
			String externalReferenceCode, long companyId)
		throws NoSuchDiscountRuleException;

	/**
	 * Returns the commerce discount rule where externalReferenceCode = &#63; and companyId = &#63; or returns <code>null</code> if it could not be found, optionally using the finder cache.
	 *
	 * @param externalReferenceCode the external reference code
	 * @param companyId the company ID
	 * @param useFinderCache whether to use the finder cache
	 * @return the matching commerce discount rule, or <code>null</code> if a matching commerce discount rule could not be found
	 */
	public CommerceDiscountRule fetchByERC_C(
		String externalReferenceCode, long companyId, boolean useFinderCache);

	/**
	 * Removes the commerce discount rule where externalReferenceCode = &#63; and companyId = &#63; from the database.
	 *
	 * @param externalReferenceCode the external reference code
	 * @param companyId the company ID
	 * @return the commerce discount rule that was removed
	 */
	public CommerceDiscountRule removeByERC_C(
			String externalReferenceCode, long companyId)
		throws NoSuchDiscountRuleException;

	/**
	 * Returns the number of commerce discount rules where externalReferenceCode = &#63; and companyId = &#63;.
	 *
	 * @param externalReferenceCode the external reference code
	 * @param companyId the company ID
	 * @return the number of matching commerce discount rules
	 */
	public int countByERC_C(String externalReferenceCode, long companyId);

	/**
	 * Creates a new commerce discount rule with the primary key. Does not add the commerce discount rule to the database.
	 *
	 * @param commerceDiscountRuleId the primary key for the new commerce discount rule
	 * @return the new commerce discount rule
	 */
	public CommerceDiscountRule create(long commerceDiscountRuleId);

	/**
	 * Removes the commerce discount rule with the primary key from the database. Also notifies the appropriate model listeners.
	 *
	 * @param commerceDiscountRuleId the primary key of the commerce discount rule
	 * @return the commerce discount rule that was removed
	 * @throws NoSuchDiscountRuleException if a commerce discount rule with the primary key could not be found
	 */
	public CommerceDiscountRule remove(long commerceDiscountRuleId)
		throws NoSuchDiscountRuleException;

	public CommerceDiscountRule updateImpl(
		CommerceDiscountRule commerceDiscountRule);

	/**
	 * Returns the commerce discount rule with the primary key or throws a <code>NoSuchDiscountRuleException</code> if it could not be found.
	 *
	 * @param commerceDiscountRuleId the primary key of the commerce discount rule
	 * @return the commerce discount rule
	 * @throws NoSuchDiscountRuleException if a commerce discount rule with the primary key could not be found
	 */
	public CommerceDiscountRule findByPrimaryKey(long commerceDiscountRuleId)
		throws NoSuchDiscountRuleException;

	/**
	 * Returns the commerce discount rule with the primary key or returns <code>null</code> if it could not be found.
	 *
	 * @param commerceDiscountRuleId the primary key of the commerce discount rule
	 * @return the commerce discount rule, or <code>null</code> if a commerce discount rule with the primary key could not be found
	 */
	public CommerceDiscountRule fetchByPrimaryKey(long commerceDiscountRuleId);

	/**
	 * Returns the commerce discount rule where externalReferenceCode = &#63; and companyId = &#63; or returns <code>null</code> if it could not be found. Uses the finder cache.
	 *
	 * @param externalReferenceCode the external reference code
	 * @param companyId the company ID
	 * @return the matching commerce discount rule, or <code>null</code> if a matching commerce discount rule could not be found
	 */
	public default CommerceDiscountRule fetchByERC_C(
		String externalReferenceCode, long companyId) {

		return fetchByERC_C(externalReferenceCode, companyId, true);
	}

	/**
	 * Returns all the commerce discount rules where commerceDiscountId = &#63;.
	 *
	 * @param commerceDiscountId the commerce discount ID
	 * @return the matching commerce discount rules
	 */
	public default java.util.List<CommerceDiscountRule>
		findByCommerceDiscountId(long commerceDiscountId) {

		return findByCommerceDiscountId(
			commerceDiscountId,
			com.liferay.portal.kernel.dao.orm.QueryUtil.ALL_POS,
			com.liferay.portal.kernel.dao.orm.QueryUtil.ALL_POS, null, true);
	}

	/**
	 * Returns a range of all the commerce discount rules where commerceDiscountId = &#63;.
	 *
	 * <p>
	 * Useful when paginating results. Returns a maximum of <code>end - start</code> instances. <code>start</code> and <code>end</code> are not primary keys, they are indexes in the result set. Thus, <code>0</code> refers to the first result in the set. Setting both <code>start</code> and <code>end</code> to <code>com.liferay.portal.kernel.dao.orm.QueryUtil#ALL_POS</code> will return the full result set. If <code>orderByComparator</code> is specified, then the query will include the given ORDER BY logic. If <code>orderByComparator</code> is absent, then the query will include the default ORDER BY logic from <code>com.liferay.commerce.discount.model.impl.CommerceDiscountRuleModelImpl</code>.
	 * </p>
	 *
	 * @param commerceDiscountId the commerce discount ID
	 * @param start the lower bound of the range of commerce discount rules
	 * @param end the upper bound of the range of commerce discount rules (not inclusive)
	 * @return the range of matching commerce discount rules
	 */
	public default java.util.List<CommerceDiscountRule>
		findByCommerceDiscountId(long commerceDiscountId, int start, int end) {

		return findByCommerceDiscountId(
			commerceDiscountId, start, end, null, true);
	}

	/**
	 * Returns an ordered range of all the commerce discount rules where commerceDiscountId = &#63;.
	 *
	 * <p>
	 * Useful when paginating results. Returns a maximum of <code>end - start</code> instances. <code>start</code> and <code>end</code> are not primary keys, they are indexes in the result set. Thus, <code>0</code> refers to the first result in the set. Setting both <code>start</code> and <code>end</code> to <code>com.liferay.portal.kernel.dao.orm.QueryUtil#ALL_POS</code> will return the full result set. If <code>orderByComparator</code> is specified, then the query will include the given ORDER BY logic. If <code>orderByComparator</code> is absent, then the query will include the default ORDER BY logic from <code>com.liferay.commerce.discount.model.impl.CommerceDiscountRuleModelImpl</code>.
	 * </p>
	 *
	 * @param commerceDiscountId the commerce discount ID
	 * @param start the lower bound of the range of commerce discount rules
	 * @param end the upper bound of the range of commerce discount rules (not inclusive)
	 * @param orderByComparator the comparator to order the results by (optionally <code>null</code>)
	 * @return the ordered range of matching commerce discount rules
	 */
	public default java.util.List<CommerceDiscountRule>
		findByCommerceDiscountId(
			long commerceDiscountId, int start, int end,
			com.liferay.portal.kernel.util.OrderByComparator
				<CommerceDiscountRule> orderByComparator) {

		return findByCommerceDiscountId(
			commerceDiscountId, start, end, orderByComparator, true);
	}

}
// LIFERAY-SERVICE-BUILDER-HASH:251197054