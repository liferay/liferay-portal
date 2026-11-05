/**
 * SPDX-FileCopyrightText: (c) 2000 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

package com.liferay.commerce.service.persistence;

import com.liferay.commerce.exception.NoSuchShippingMethodException;
import com.liferay.commerce.model.CommerceShippingMethod;
import com.liferay.portal.kernel.service.persistence.BasePersistence;

import org.osgi.annotation.versioning.ProviderType;

/**
 * The persistence interface for the commerce shipping method service.
 *
 * <p>
 * Caching information and settings can be found in <code>portal.properties</code>
 * </p>
 *
 * @author Alessio Antonio Rendina
 * @see CommerceShippingMethodUtil
 * @generated
 */
@ProviderType
public interface CommerceShippingMethodPersistence
	extends BasePersistence<CommerceShippingMethod> {

	/*
	 * NOTE FOR DEVELOPERS:
	 *
	 * Never modify or reference this interface directly. Always use {@link CommerceShippingMethodUtil} to access the commerce shipping method persistence. Modify <code>service.xml</code> and rerun ServiceBuilder to regenerate this interface.
	 */

	/**
	 * Returns an ordered range of all the commerce shipping methods where groupId = &#63;.
	 *
	 * <p>
	 * Useful when paginating results. Returns a maximum of <code>end - start</code> instances. <code>start</code> and <code>end</code> are not primary keys, they are indexes in the result set. Thus, <code>0</code> refers to the first result in the set. Setting both <code>start</code> and <code>end</code> to <code>com.liferay.portal.kernel.dao.orm.QueryUtil#ALL_POS</code> will return the full result set. If <code>orderByComparator</code> is specified, then the query will include the given ORDER BY logic. If <code>orderByComparator</code> is absent, then the query will include the default ORDER BY logic from <code>com.liferay.commerce.model.impl.CommerceShippingMethodModelImpl</code>.
	 * </p>
	 *
	 * @param groupId the group ID
	 * @param start the lower bound of the range of commerce shipping methods
	 * @param end the upper bound of the range of commerce shipping methods (not inclusive)
	 * @param orderByComparator the comparator to order the results by (optionally <code>null</code>)
	 * @param useFinderCache whether to use the finder cache
	 * @return the ordered range of matching commerce shipping methods
	 */
	public java.util.List<CommerceShippingMethod> findByGroupId(
		long groupId, int start, int end,
		com.liferay.portal.kernel.util.OrderByComparator<CommerceShippingMethod>
			orderByComparator,
		boolean useFinderCache);

	/**
	 * Returns the first commerce shipping method in the ordered set where groupId = &#63;.
	 *
	 * @param groupId the group ID
	 * @param orderByComparator the comparator to order the set by (optionally <code>null</code>)
	 * @return the first matching commerce shipping method
	 * @throws NoSuchShippingMethodException if a matching commerce shipping method could not be found
	 */
	public CommerceShippingMethod findByGroupId_First(
			long groupId,
			com.liferay.portal.kernel.util.OrderByComparator
				<CommerceShippingMethod> orderByComparator)
		throws NoSuchShippingMethodException;

	/**
	 * Returns the first commerce shipping method in the ordered set where groupId = &#63;.
	 *
	 * @param groupId the group ID
	 * @param orderByComparator the comparator to order the set by (optionally <code>null</code>)
	 * @return the first matching commerce shipping method, or <code>null</code> if a matching commerce shipping method could not be found
	 */
	public CommerceShippingMethod fetchByGroupId_First(
		long groupId,
		com.liferay.portal.kernel.util.OrderByComparator<CommerceShippingMethod>
			orderByComparator);

	/**
	 * Removes all the commerce shipping methods where groupId = &#63; from the database.
	 *
	 * @param groupId the group ID
	 */
	public void removeByGroupId(long groupId);

	/**
	 * Returns the number of commerce shipping methods where groupId = &#63;.
	 *
	 * @param groupId the group ID
	 * @return the number of matching commerce shipping methods
	 */
	public int countByGroupId(long groupId);

	/**
	 * Returns an ordered range of all the commerce shipping methods where groupId = &#63; and active = &#63;.
	 *
	 * <p>
	 * Useful when paginating results. Returns a maximum of <code>end - start</code> instances. <code>start</code> and <code>end</code> are not primary keys, they are indexes in the result set. Thus, <code>0</code> refers to the first result in the set. Setting both <code>start</code> and <code>end</code> to <code>com.liferay.portal.kernel.dao.orm.QueryUtil#ALL_POS</code> will return the full result set. If <code>orderByComparator</code> is specified, then the query will include the given ORDER BY logic. If <code>orderByComparator</code> is absent, then the query will include the default ORDER BY logic from <code>com.liferay.commerce.model.impl.CommerceShippingMethodModelImpl</code>.
	 * </p>
	 *
	 * @param groupId the group ID
	 * @param active the active
	 * @param start the lower bound of the range of commerce shipping methods
	 * @param end the upper bound of the range of commerce shipping methods (not inclusive)
	 * @param orderByComparator the comparator to order the results by (optionally <code>null</code>)
	 * @param useFinderCache whether to use the finder cache
	 * @return the ordered range of matching commerce shipping methods
	 */
	public java.util.List<CommerceShippingMethod> findByG_A(
		long groupId, boolean active, int start, int end,
		com.liferay.portal.kernel.util.OrderByComparator<CommerceShippingMethod>
			orderByComparator,
		boolean useFinderCache);

	/**
	 * Returns the first commerce shipping method in the ordered set where groupId = &#63; and active = &#63;.
	 *
	 * @param groupId the group ID
	 * @param active the active
	 * @param orderByComparator the comparator to order the set by (optionally <code>null</code>)
	 * @return the first matching commerce shipping method
	 * @throws NoSuchShippingMethodException if a matching commerce shipping method could not be found
	 */
	public CommerceShippingMethod findByG_A_First(
			long groupId, boolean active,
			com.liferay.portal.kernel.util.OrderByComparator
				<CommerceShippingMethod> orderByComparator)
		throws NoSuchShippingMethodException;

	/**
	 * Returns the first commerce shipping method in the ordered set where groupId = &#63; and active = &#63;.
	 *
	 * @param groupId the group ID
	 * @param active the active
	 * @param orderByComparator the comparator to order the set by (optionally <code>null</code>)
	 * @return the first matching commerce shipping method, or <code>null</code> if a matching commerce shipping method could not be found
	 */
	public CommerceShippingMethod fetchByG_A_First(
		long groupId, boolean active,
		com.liferay.portal.kernel.util.OrderByComparator<CommerceShippingMethod>
			orderByComparator);

	/**
	 * Removes all the commerce shipping methods where groupId = &#63; and active = &#63; from the database.
	 *
	 * @param groupId the group ID
	 * @param active the active
	 */
	public void removeByG_A(long groupId, boolean active);

	/**
	 * Returns the number of commerce shipping methods where groupId = &#63; and active = &#63;.
	 *
	 * @param groupId the group ID
	 * @param active the active
	 * @return the number of matching commerce shipping methods
	 */
	public int countByG_A(long groupId, boolean active);

	/**
	 * Returns the commerce shipping method where groupId = &#63; and engineKey = &#63; or throws a <code>NoSuchShippingMethodException</code> if it could not be found.
	 *
	 * @param groupId the group ID
	 * @param engineKey the engine key
	 * @return the matching commerce shipping method
	 * @throws NoSuchShippingMethodException if a matching commerce shipping method could not be found
	 */
	public CommerceShippingMethod findByG_E(long groupId, String engineKey)
		throws NoSuchShippingMethodException;

	/**
	 * Returns the commerce shipping method where groupId = &#63; and engineKey = &#63; or returns <code>null</code> if it could not be found, optionally using the finder cache.
	 *
	 * @param groupId the group ID
	 * @param engineKey the engine key
	 * @param useFinderCache whether to use the finder cache
	 * @return the matching commerce shipping method, or <code>null</code> if a matching commerce shipping method could not be found
	 */
	public CommerceShippingMethod fetchByG_E(
		long groupId, String engineKey, boolean useFinderCache);

	/**
	 * Removes the commerce shipping method where groupId = &#63; and engineKey = &#63; from the database.
	 *
	 * @param groupId the group ID
	 * @param engineKey the engine key
	 * @return the commerce shipping method that was removed
	 */
	public CommerceShippingMethod removeByG_E(long groupId, String engineKey)
		throws NoSuchShippingMethodException;

	/**
	 * Returns the number of commerce shipping methods where groupId = &#63; and engineKey = &#63;.
	 *
	 * @param groupId the group ID
	 * @param engineKey the engine key
	 * @return the number of matching commerce shipping methods
	 */
	public int countByG_E(long groupId, String engineKey);

	/**
	 * Returns the commerce shipping method where externalReferenceCode = &#63; and companyId = &#63; or throws a <code>NoSuchShippingMethodException</code> if it could not be found.
	 *
	 * @param externalReferenceCode the external reference code
	 * @param companyId the company ID
	 * @return the matching commerce shipping method
	 * @throws NoSuchShippingMethodException if a matching commerce shipping method could not be found
	 */
	public CommerceShippingMethod findByERC_C(
			String externalReferenceCode, long companyId)
		throws NoSuchShippingMethodException;

	/**
	 * Returns the commerce shipping method where externalReferenceCode = &#63; and companyId = &#63; or returns <code>null</code> if it could not be found, optionally using the finder cache.
	 *
	 * @param externalReferenceCode the external reference code
	 * @param companyId the company ID
	 * @param useFinderCache whether to use the finder cache
	 * @return the matching commerce shipping method, or <code>null</code> if a matching commerce shipping method could not be found
	 */
	public CommerceShippingMethod fetchByERC_C(
		String externalReferenceCode, long companyId, boolean useFinderCache);

	/**
	 * Removes the commerce shipping method where externalReferenceCode = &#63; and companyId = &#63; from the database.
	 *
	 * @param externalReferenceCode the external reference code
	 * @param companyId the company ID
	 * @return the commerce shipping method that was removed
	 */
	public CommerceShippingMethod removeByERC_C(
			String externalReferenceCode, long companyId)
		throws NoSuchShippingMethodException;

	/**
	 * Returns the number of commerce shipping methods where externalReferenceCode = &#63; and companyId = &#63;.
	 *
	 * @param externalReferenceCode the external reference code
	 * @param companyId the company ID
	 * @return the number of matching commerce shipping methods
	 */
	public int countByERC_C(String externalReferenceCode, long companyId);

	/**
	 * Creates a new commerce shipping method with the primary key. Does not add the commerce shipping method to the database.
	 *
	 * @param commerceShippingMethodId the primary key for the new commerce shipping method
	 * @return the new commerce shipping method
	 */
	public CommerceShippingMethod create(long commerceShippingMethodId);

	/**
	 * Removes the commerce shipping method with the primary key from the database. Also notifies the appropriate model listeners.
	 *
	 * @param commerceShippingMethodId the primary key of the commerce shipping method
	 * @return the commerce shipping method that was removed
	 * @throws NoSuchShippingMethodException if a commerce shipping method with the primary key could not be found
	 */
	public CommerceShippingMethod remove(long commerceShippingMethodId)
		throws NoSuchShippingMethodException;

	public CommerceShippingMethod updateImpl(
		CommerceShippingMethod commerceShippingMethod);

	/**
	 * Returns the commerce shipping method with the primary key or throws a <code>NoSuchShippingMethodException</code> if it could not be found.
	 *
	 * @param commerceShippingMethodId the primary key of the commerce shipping method
	 * @return the commerce shipping method
	 * @throws NoSuchShippingMethodException if a commerce shipping method with the primary key could not be found
	 */
	public CommerceShippingMethod findByPrimaryKey(
			long commerceShippingMethodId)
		throws NoSuchShippingMethodException;

	/**
	 * Returns the commerce shipping method with the primary key or returns <code>null</code> if it could not be found.
	 *
	 * @param commerceShippingMethodId the primary key of the commerce shipping method
	 * @return the commerce shipping method, or <code>null</code> if a commerce shipping method with the primary key could not be found
	 */
	public CommerceShippingMethod fetchByPrimaryKey(
		long commerceShippingMethodId);

	/**
	 * Returns the commerce shipping method where groupId = &#63; and engineKey = &#63; or returns <code>null</code> if it could not be found. Uses the finder cache.
	 *
	 * @param groupId the group ID
	 * @param engineKey the engine key
	 * @return the matching commerce shipping method, or <code>null</code> if a matching commerce shipping method could not be found
	 */
	public default CommerceShippingMethod fetchByG_E(
		long groupId, String engineKey) {

		return fetchByG_E(groupId, engineKey, true);
	}

	/**
	 * Returns the commerce shipping method where externalReferenceCode = &#63; and companyId = &#63; or returns <code>null</code> if it could not be found. Uses the finder cache.
	 *
	 * @param externalReferenceCode the external reference code
	 * @param companyId the company ID
	 * @return the matching commerce shipping method, or <code>null</code> if a matching commerce shipping method could not be found
	 */
	public default CommerceShippingMethod fetchByERC_C(
		String externalReferenceCode, long companyId) {

		return fetchByERC_C(externalReferenceCode, companyId, true);
	}

	/**
	 * Returns all the commerce shipping methods where groupId = &#63;.
	 *
	 * @param groupId the group ID
	 * @return the matching commerce shipping methods
	 */
	public default java.util.List<CommerceShippingMethod> findByGroupId(
		long groupId) {

		return findByGroupId(
			groupId, com.liferay.portal.kernel.dao.orm.QueryUtil.ALL_POS,
			com.liferay.portal.kernel.dao.orm.QueryUtil.ALL_POS, null, true);
	}

	/**
	 * Returns a range of all the commerce shipping methods where groupId = &#63;.
	 *
	 * <p>
	 * Useful when paginating results. Returns a maximum of <code>end - start</code> instances. <code>start</code> and <code>end</code> are not primary keys, they are indexes in the result set. Thus, <code>0</code> refers to the first result in the set. Setting both <code>start</code> and <code>end</code> to <code>com.liferay.portal.kernel.dao.orm.QueryUtil#ALL_POS</code> will return the full result set. If <code>orderByComparator</code> is specified, then the query will include the given ORDER BY logic. If <code>orderByComparator</code> is absent, then the query will include the default ORDER BY logic from <code>com.liferay.commerce.model.impl.CommerceShippingMethodModelImpl</code>.
	 * </p>
	 *
	 * @param groupId the group ID
	 * @param start the lower bound of the range of commerce shipping methods
	 * @param end the upper bound of the range of commerce shipping methods (not inclusive)
	 * @return the range of matching commerce shipping methods
	 */
	public default java.util.List<CommerceShippingMethod> findByGroupId(
		long groupId, int start, int end) {

		return findByGroupId(groupId, start, end, null, true);
	}

	/**
	 * Returns an ordered range of all the commerce shipping methods where groupId = &#63;.
	 *
	 * <p>
	 * Useful when paginating results. Returns a maximum of <code>end - start</code> instances. <code>start</code> and <code>end</code> are not primary keys, they are indexes in the result set. Thus, <code>0</code> refers to the first result in the set. Setting both <code>start</code> and <code>end</code> to <code>com.liferay.portal.kernel.dao.orm.QueryUtil#ALL_POS</code> will return the full result set. If <code>orderByComparator</code> is specified, then the query will include the given ORDER BY logic. If <code>orderByComparator</code> is absent, then the query will include the default ORDER BY logic from <code>com.liferay.commerce.model.impl.CommerceShippingMethodModelImpl</code>.
	 * </p>
	 *
	 * @param groupId the group ID
	 * @param start the lower bound of the range of commerce shipping methods
	 * @param end the upper bound of the range of commerce shipping methods (not inclusive)
	 * @param orderByComparator the comparator to order the results by (optionally <code>null</code>)
	 * @return the ordered range of matching commerce shipping methods
	 */
	public default java.util.List<CommerceShippingMethod> findByGroupId(
		long groupId, int start, int end,
		com.liferay.portal.kernel.util.OrderByComparator<CommerceShippingMethod>
			orderByComparator) {

		return findByGroupId(groupId, start, end, orderByComparator, true);
	}

	/**
	 * Returns all the commerce shipping methods where groupId = &#63; and active = &#63;.
	 *
	 * @param groupId the group ID
	 * @param active the active
	 * @return the matching commerce shipping methods
	 */
	public default java.util.List<CommerceShippingMethod> findByG_A(
		long groupId, boolean active) {

		return findByG_A(
			groupId, active,
			com.liferay.portal.kernel.dao.orm.QueryUtil.ALL_POS,
			com.liferay.portal.kernel.dao.orm.QueryUtil.ALL_POS, null, true);
	}

	/**
	 * Returns a range of all the commerce shipping methods where groupId = &#63; and active = &#63;.
	 *
	 * <p>
	 * Useful when paginating results. Returns a maximum of <code>end - start</code> instances. <code>start</code> and <code>end</code> are not primary keys, they are indexes in the result set. Thus, <code>0</code> refers to the first result in the set. Setting both <code>start</code> and <code>end</code> to <code>com.liferay.portal.kernel.dao.orm.QueryUtil#ALL_POS</code> will return the full result set. If <code>orderByComparator</code> is specified, then the query will include the given ORDER BY logic. If <code>orderByComparator</code> is absent, then the query will include the default ORDER BY logic from <code>com.liferay.commerce.model.impl.CommerceShippingMethodModelImpl</code>.
	 * </p>
	 *
	 * @param groupId the group ID
	 * @param active the active
	 * @param start the lower bound of the range of commerce shipping methods
	 * @param end the upper bound of the range of commerce shipping methods (not inclusive)
	 * @return the range of matching commerce shipping methods
	 */
	public default java.util.List<CommerceShippingMethod> findByG_A(
		long groupId, boolean active, int start, int end) {

		return findByG_A(groupId, active, start, end, null, true);
	}

	/**
	 * Returns an ordered range of all the commerce shipping methods where groupId = &#63; and active = &#63;.
	 *
	 * <p>
	 * Useful when paginating results. Returns a maximum of <code>end - start</code> instances. <code>start</code> and <code>end</code> are not primary keys, they are indexes in the result set. Thus, <code>0</code> refers to the first result in the set. Setting both <code>start</code> and <code>end</code> to <code>com.liferay.portal.kernel.dao.orm.QueryUtil#ALL_POS</code> will return the full result set. If <code>orderByComparator</code> is specified, then the query will include the given ORDER BY logic. If <code>orderByComparator</code> is absent, then the query will include the default ORDER BY logic from <code>com.liferay.commerce.model.impl.CommerceShippingMethodModelImpl</code>.
	 * </p>
	 *
	 * @param groupId the group ID
	 * @param active the active
	 * @param start the lower bound of the range of commerce shipping methods
	 * @param end the upper bound of the range of commerce shipping methods (not inclusive)
	 * @param orderByComparator the comparator to order the results by (optionally <code>null</code>)
	 * @return the ordered range of matching commerce shipping methods
	 */
	public default java.util.List<CommerceShippingMethod> findByG_A(
		long groupId, boolean active, int start, int end,
		com.liferay.portal.kernel.util.OrderByComparator<CommerceShippingMethod>
			orderByComparator) {

		return findByG_A(groupId, active, start, end, orderByComparator, true);
	}

}
// LIFERAY-SERVICE-BUILDER-HASH:-1631153429