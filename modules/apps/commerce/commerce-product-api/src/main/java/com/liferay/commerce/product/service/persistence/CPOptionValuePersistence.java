/**
 * SPDX-FileCopyrightText: (c) 2000 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

package com.liferay.commerce.product.service.persistence;

import com.liferay.commerce.product.exception.NoSuchCPOptionValueException;
import com.liferay.commerce.product.model.CPOptionValue;
import com.liferay.portal.kernel.service.persistence.BasePersistence;
import com.liferay.portal.kernel.service.persistence.change.tracking.CTPersistence;

import org.osgi.annotation.versioning.ProviderType;

/**
 * The persistence interface for the cp option value service.
 *
 * <p>
 * Caching information and settings can be found in <code>portal.properties</code>
 * </p>
 *
 * @author Marco Leo
 * @see CPOptionValueUtil
 * @generated
 */
@ProviderType
public interface CPOptionValuePersistence
	extends BasePersistence<CPOptionValue>, CTPersistence<CPOptionValue> {

	/*
	 * NOTE FOR DEVELOPERS:
	 *
	 * Never modify or reference this interface directly. Always use {@link CPOptionValueUtil} to access the cp option value persistence. Modify <code>service.xml</code> and rerun ServiceBuilder to regenerate this interface.
	 */

	/**
	 * Returns an ordered range of all the cp option values where uuid = &#63;.
	 *
	 * <p>
	 * Useful when paginating results. Returns a maximum of <code>end - start</code> instances. <code>start</code> and <code>end</code> are not primary keys, they are indexes in the result set. Thus, <code>0</code> refers to the first result in the set. Setting both <code>start</code> and <code>end</code> to <code>com.liferay.portal.kernel.dao.orm.QueryUtil#ALL_POS</code> will return the full result set. If <code>orderByComparator</code> is specified, then the query will include the given ORDER BY logic. If <code>orderByComparator</code> is absent, then the query will include the default ORDER BY logic from <code>com.liferay.commerce.product.model.impl.CPOptionValueModelImpl</code>.
	 * </p>
	 *
	 * @param uuid the uuid
	 * @param start the lower bound of the range of cp option values
	 * @param end the upper bound of the range of cp option values (not inclusive)
	 * @param orderByComparator the comparator to order the results by (optionally <code>null</code>)
	 * @param useFinderCache whether to use the finder cache
	 * @return the ordered range of matching cp option values
	 */
	public java.util.List<CPOptionValue> findByUuid(
		String uuid, int start, int end,
		com.liferay.portal.kernel.util.OrderByComparator<CPOptionValue>
			orderByComparator,
		boolean useFinderCache);

	/**
	 * Returns the first cp option value in the ordered set where uuid = &#63;.
	 *
	 * @param uuid the uuid
	 * @param orderByComparator the comparator to order the set by (optionally <code>null</code>)
	 * @return the first matching cp option value
	 * @throws NoSuchCPOptionValueException if a matching cp option value could not be found
	 */
	public CPOptionValue findByUuid_First(
			String uuid,
			com.liferay.portal.kernel.util.OrderByComparator<CPOptionValue>
				orderByComparator)
		throws NoSuchCPOptionValueException;

	/**
	 * Returns the first cp option value in the ordered set where uuid = &#63;.
	 *
	 * @param uuid the uuid
	 * @param orderByComparator the comparator to order the set by (optionally <code>null</code>)
	 * @return the first matching cp option value, or <code>null</code> if a matching cp option value could not be found
	 */
	public CPOptionValue fetchByUuid_First(
		String uuid,
		com.liferay.portal.kernel.util.OrderByComparator<CPOptionValue>
			orderByComparator);

	/**
	 * Removes all the cp option values where uuid = &#63; from the database.
	 *
	 * @param uuid the uuid
	 */
	public void removeByUuid(String uuid);

	/**
	 * Returns the number of cp option values where uuid = &#63;.
	 *
	 * @param uuid the uuid
	 * @return the number of matching cp option values
	 */
	public int countByUuid(String uuid);

	/**
	 * Returns an ordered range of all the cp option values where uuid = &#63; and companyId = &#63;.
	 *
	 * <p>
	 * Useful when paginating results. Returns a maximum of <code>end - start</code> instances. <code>start</code> and <code>end</code> are not primary keys, they are indexes in the result set. Thus, <code>0</code> refers to the first result in the set. Setting both <code>start</code> and <code>end</code> to <code>com.liferay.portal.kernel.dao.orm.QueryUtil#ALL_POS</code> will return the full result set. If <code>orderByComparator</code> is specified, then the query will include the given ORDER BY logic. If <code>orderByComparator</code> is absent, then the query will include the default ORDER BY logic from <code>com.liferay.commerce.product.model.impl.CPOptionValueModelImpl</code>.
	 * </p>
	 *
	 * @param uuid the uuid
	 * @param companyId the company ID
	 * @param start the lower bound of the range of cp option values
	 * @param end the upper bound of the range of cp option values (not inclusive)
	 * @param orderByComparator the comparator to order the results by (optionally <code>null</code>)
	 * @param useFinderCache whether to use the finder cache
	 * @return the ordered range of matching cp option values
	 */
	public java.util.List<CPOptionValue> findByUuid_C(
		String uuid, long companyId, int start, int end,
		com.liferay.portal.kernel.util.OrderByComparator<CPOptionValue>
			orderByComparator,
		boolean useFinderCache);

	/**
	 * Returns the first cp option value in the ordered set where uuid = &#63; and companyId = &#63;.
	 *
	 * @param uuid the uuid
	 * @param companyId the company ID
	 * @param orderByComparator the comparator to order the set by (optionally <code>null</code>)
	 * @return the first matching cp option value
	 * @throws NoSuchCPOptionValueException if a matching cp option value could not be found
	 */
	public CPOptionValue findByUuid_C_First(
			String uuid, long companyId,
			com.liferay.portal.kernel.util.OrderByComparator<CPOptionValue>
				orderByComparator)
		throws NoSuchCPOptionValueException;

	/**
	 * Returns the first cp option value in the ordered set where uuid = &#63; and companyId = &#63;.
	 *
	 * @param uuid the uuid
	 * @param companyId the company ID
	 * @param orderByComparator the comparator to order the set by (optionally <code>null</code>)
	 * @return the first matching cp option value, or <code>null</code> if a matching cp option value could not be found
	 */
	public CPOptionValue fetchByUuid_C_First(
		String uuid, long companyId,
		com.liferay.portal.kernel.util.OrderByComparator<CPOptionValue>
			orderByComparator);

	/**
	 * Removes all the cp option values where uuid = &#63; and companyId = &#63; from the database.
	 *
	 * @param uuid the uuid
	 * @param companyId the company ID
	 */
	public void removeByUuid_C(String uuid, long companyId);

	/**
	 * Returns the number of cp option values where uuid = &#63; and companyId = &#63;.
	 *
	 * @param uuid the uuid
	 * @param companyId the company ID
	 * @return the number of matching cp option values
	 */
	public int countByUuid_C(String uuid, long companyId);

	/**
	 * Returns an ordered range of all the cp option values where CPOptionId = &#63;.
	 *
	 * <p>
	 * Useful when paginating results. Returns a maximum of <code>end - start</code> instances. <code>start</code> and <code>end</code> are not primary keys, they are indexes in the result set. Thus, <code>0</code> refers to the first result in the set. Setting both <code>start</code> and <code>end</code> to <code>com.liferay.portal.kernel.dao.orm.QueryUtil#ALL_POS</code> will return the full result set. If <code>orderByComparator</code> is specified, then the query will include the given ORDER BY logic. If <code>orderByComparator</code> is absent, then the query will include the default ORDER BY logic from <code>com.liferay.commerce.product.model.impl.CPOptionValueModelImpl</code>.
	 * </p>
	 *
	 * @param CPOptionId the cp option ID
	 * @param start the lower bound of the range of cp option values
	 * @param end the upper bound of the range of cp option values (not inclusive)
	 * @param orderByComparator the comparator to order the results by (optionally <code>null</code>)
	 * @param useFinderCache whether to use the finder cache
	 * @return the ordered range of matching cp option values
	 */
	public java.util.List<CPOptionValue> findByCPOptionId(
		long CPOptionId, int start, int end,
		com.liferay.portal.kernel.util.OrderByComparator<CPOptionValue>
			orderByComparator,
		boolean useFinderCache);

	/**
	 * Returns the first cp option value in the ordered set where CPOptionId = &#63;.
	 *
	 * @param CPOptionId the cp option ID
	 * @param orderByComparator the comparator to order the set by (optionally <code>null</code>)
	 * @return the first matching cp option value
	 * @throws NoSuchCPOptionValueException if a matching cp option value could not be found
	 */
	public CPOptionValue findByCPOptionId_First(
			long CPOptionId,
			com.liferay.portal.kernel.util.OrderByComparator<CPOptionValue>
				orderByComparator)
		throws NoSuchCPOptionValueException;

	/**
	 * Returns the first cp option value in the ordered set where CPOptionId = &#63;.
	 *
	 * @param CPOptionId the cp option ID
	 * @param orderByComparator the comparator to order the set by (optionally <code>null</code>)
	 * @return the first matching cp option value, or <code>null</code> if a matching cp option value could not be found
	 */
	public CPOptionValue fetchByCPOptionId_First(
		long CPOptionId,
		com.liferay.portal.kernel.util.OrderByComparator<CPOptionValue>
			orderByComparator);

	/**
	 * Removes all the cp option values where CPOptionId = &#63; from the database.
	 *
	 * @param CPOptionId the cp option ID
	 */
	public void removeByCPOptionId(long CPOptionId);

	/**
	 * Returns the number of cp option values where CPOptionId = &#63;.
	 *
	 * @param CPOptionId the cp option ID
	 * @return the number of matching cp option values
	 */
	public int countByCPOptionId(long CPOptionId);

	/**
	 * Returns the cp option value where CPOptionId = &#63; and key = &#63; or throws a <code>NoSuchCPOptionValueException</code> if it could not be found.
	 *
	 * @param CPOptionId the cp option ID
	 * @param key the key
	 * @return the matching cp option value
	 * @throws NoSuchCPOptionValueException if a matching cp option value could not be found
	 */
	public CPOptionValue findByC_K(long CPOptionId, String key)
		throws NoSuchCPOptionValueException;

	/**
	 * Returns the cp option value where CPOptionId = &#63; and key = &#63; or returns <code>null</code> if it could not be found, optionally using the finder cache.
	 *
	 * @param CPOptionId the cp option ID
	 * @param key the key
	 * @param useFinderCache whether to use the finder cache
	 * @return the matching cp option value, or <code>null</code> if a matching cp option value could not be found
	 */
	public CPOptionValue fetchByC_K(
		long CPOptionId, String key, boolean useFinderCache);

	/**
	 * Removes the cp option value where CPOptionId = &#63; and key = &#63; from the database.
	 *
	 * @param CPOptionId the cp option ID
	 * @param key the key
	 * @return the cp option value that was removed
	 */
	public CPOptionValue removeByC_K(long CPOptionId, String key)
		throws NoSuchCPOptionValueException;

	/**
	 * Returns the number of cp option values where CPOptionId = &#63; and key = &#63;.
	 *
	 * @param CPOptionId the cp option ID
	 * @param key the key
	 * @return the number of matching cp option values
	 */
	public int countByC_K(long CPOptionId, String key);

	/**
	 * Returns the cp option value where externalReferenceCode = &#63; and companyId = &#63; or throws a <code>NoSuchCPOptionValueException</code> if it could not be found.
	 *
	 * @param externalReferenceCode the external reference code
	 * @param companyId the company ID
	 * @return the matching cp option value
	 * @throws NoSuchCPOptionValueException if a matching cp option value could not be found
	 */
	public CPOptionValue findByERC_C(
			String externalReferenceCode, long companyId)
		throws NoSuchCPOptionValueException;

	/**
	 * Returns the cp option value where externalReferenceCode = &#63; and companyId = &#63; or returns <code>null</code> if it could not be found, optionally using the finder cache.
	 *
	 * @param externalReferenceCode the external reference code
	 * @param companyId the company ID
	 * @param useFinderCache whether to use the finder cache
	 * @return the matching cp option value, or <code>null</code> if a matching cp option value could not be found
	 */
	public CPOptionValue fetchByERC_C(
		String externalReferenceCode, long companyId, boolean useFinderCache);

	/**
	 * Removes the cp option value where externalReferenceCode = &#63; and companyId = &#63; from the database.
	 *
	 * @param externalReferenceCode the external reference code
	 * @param companyId the company ID
	 * @return the cp option value that was removed
	 */
	public CPOptionValue removeByERC_C(
			String externalReferenceCode, long companyId)
		throws NoSuchCPOptionValueException;

	/**
	 * Returns the number of cp option values where externalReferenceCode = &#63; and companyId = &#63;.
	 *
	 * @param externalReferenceCode the external reference code
	 * @param companyId the company ID
	 * @return the number of matching cp option values
	 */
	public int countByERC_C(String externalReferenceCode, long companyId);

	/**
	 * Creates a new cp option value with the primary key. Does not add the cp option value to the database.
	 *
	 * @param CPOptionValueId the primary key for the new cp option value
	 * @return the new cp option value
	 */
	public CPOptionValue create(long CPOptionValueId);

	/**
	 * Removes the cp option value with the primary key from the database. Also notifies the appropriate model listeners.
	 *
	 * @param CPOptionValueId the primary key of the cp option value
	 * @return the cp option value that was removed
	 * @throws NoSuchCPOptionValueException if a cp option value with the primary key could not be found
	 */
	public CPOptionValue remove(long CPOptionValueId)
		throws NoSuchCPOptionValueException;

	public CPOptionValue updateImpl(CPOptionValue cpOptionValue);

	/**
	 * Returns the cp option value with the primary key or throws a <code>NoSuchCPOptionValueException</code> if it could not be found.
	 *
	 * @param CPOptionValueId the primary key of the cp option value
	 * @return the cp option value
	 * @throws NoSuchCPOptionValueException if a cp option value with the primary key could not be found
	 */
	public CPOptionValue findByPrimaryKey(long CPOptionValueId)
		throws NoSuchCPOptionValueException;

	/**
	 * Returns the cp option value with the primary key or returns <code>null</code> if it could not be found.
	 *
	 * @param CPOptionValueId the primary key of the cp option value
	 * @return the cp option value, or <code>null</code> if a cp option value with the primary key could not be found
	 */
	public CPOptionValue fetchByPrimaryKey(long CPOptionValueId);

	/**
	 * Returns the cp option value where CPOptionId = &#63; and key = &#63; or returns <code>null</code> if it could not be found. Uses the finder cache.
	 *
	 * @param CPOptionId the cp option ID
	 * @param key the key
	 * @return the matching cp option value, or <code>null</code> if a matching cp option value could not be found
	 */
	public default CPOptionValue fetchByC_K(long CPOptionId, String key) {
		return fetchByC_K(CPOptionId, key, true);
	}

	/**
	 * Returns the cp option value where externalReferenceCode = &#63; and companyId = &#63; or returns <code>null</code> if it could not be found. Uses the finder cache.
	 *
	 * @param externalReferenceCode the external reference code
	 * @param companyId the company ID
	 * @return the matching cp option value, or <code>null</code> if a matching cp option value could not be found
	 */
	public default CPOptionValue fetchByERC_C(
		String externalReferenceCode, long companyId) {

		return fetchByERC_C(externalReferenceCode, companyId, true);
	}

	/**
	 * Returns all the cp option values where uuid = &#63;.
	 *
	 * @param uuid the uuid
	 * @return the matching cp option values
	 */
	public default java.util.List<CPOptionValue> findByUuid(String uuid) {
		return findByUuid(
			uuid, com.liferay.portal.kernel.dao.orm.QueryUtil.ALL_POS,
			com.liferay.portal.kernel.dao.orm.QueryUtil.ALL_POS, null, true);
	}

	/**
	 * Returns a range of all the cp option values where uuid = &#63;.
	 *
	 * <p>
	 * Useful when paginating results. Returns a maximum of <code>end - start</code> instances. <code>start</code> and <code>end</code> are not primary keys, they are indexes in the result set. Thus, <code>0</code> refers to the first result in the set. Setting both <code>start</code> and <code>end</code> to <code>com.liferay.portal.kernel.dao.orm.QueryUtil#ALL_POS</code> will return the full result set. If <code>orderByComparator</code> is specified, then the query will include the given ORDER BY logic. If <code>orderByComparator</code> is absent, then the query will include the default ORDER BY logic from <code>com.liferay.commerce.product.model.impl.CPOptionValueModelImpl</code>.
	 * </p>
	 *
	 * @param uuid the uuid
	 * @param start the lower bound of the range of cp option values
	 * @param end the upper bound of the range of cp option values (not inclusive)
	 * @return the range of matching cp option values
	 */
	public default java.util.List<CPOptionValue> findByUuid(
		String uuid, int start, int end) {

		return findByUuid(uuid, start, end, null, true);
	}

	/**
	 * Returns an ordered range of all the cp option values where uuid = &#63;.
	 *
	 * <p>
	 * Useful when paginating results. Returns a maximum of <code>end - start</code> instances. <code>start</code> and <code>end</code> are not primary keys, they are indexes in the result set. Thus, <code>0</code> refers to the first result in the set. Setting both <code>start</code> and <code>end</code> to <code>com.liferay.portal.kernel.dao.orm.QueryUtil#ALL_POS</code> will return the full result set. If <code>orderByComparator</code> is specified, then the query will include the given ORDER BY logic. If <code>orderByComparator</code> is absent, then the query will include the default ORDER BY logic from <code>com.liferay.commerce.product.model.impl.CPOptionValueModelImpl</code>.
	 * </p>
	 *
	 * @param uuid the uuid
	 * @param start the lower bound of the range of cp option values
	 * @param end the upper bound of the range of cp option values (not inclusive)
	 * @param orderByComparator the comparator to order the results by (optionally <code>null</code>)
	 * @return the ordered range of matching cp option values
	 */
	public default java.util.List<CPOptionValue> findByUuid(
		String uuid, int start, int end,
		com.liferay.portal.kernel.util.OrderByComparator<CPOptionValue>
			orderByComparator) {

		return findByUuid(uuid, start, end, orderByComparator, true);
	}

	/**
	 * Returns all the cp option values where uuid = &#63; and companyId = &#63;.
	 *
	 * @param uuid the uuid
	 * @param companyId the company ID
	 * @return the matching cp option values
	 */
	public default java.util.List<CPOptionValue> findByUuid_C(
		String uuid, long companyId) {

		return findByUuid_C(
			uuid, companyId,
			com.liferay.portal.kernel.dao.orm.QueryUtil.ALL_POS,
			com.liferay.portal.kernel.dao.orm.QueryUtil.ALL_POS, null, true);
	}

	/**
	 * Returns a range of all the cp option values where uuid = &#63; and companyId = &#63;.
	 *
	 * <p>
	 * Useful when paginating results. Returns a maximum of <code>end - start</code> instances. <code>start</code> and <code>end</code> are not primary keys, they are indexes in the result set. Thus, <code>0</code> refers to the first result in the set. Setting both <code>start</code> and <code>end</code> to <code>com.liferay.portal.kernel.dao.orm.QueryUtil#ALL_POS</code> will return the full result set. If <code>orderByComparator</code> is specified, then the query will include the given ORDER BY logic. If <code>orderByComparator</code> is absent, then the query will include the default ORDER BY logic from <code>com.liferay.commerce.product.model.impl.CPOptionValueModelImpl</code>.
	 * </p>
	 *
	 * @param uuid the uuid
	 * @param companyId the company ID
	 * @param start the lower bound of the range of cp option values
	 * @param end the upper bound of the range of cp option values (not inclusive)
	 * @return the range of matching cp option values
	 */
	public default java.util.List<CPOptionValue> findByUuid_C(
		String uuid, long companyId, int start, int end) {

		return findByUuid_C(uuid, companyId, start, end, null, true);
	}

	/**
	 * Returns an ordered range of all the cp option values where uuid = &#63; and companyId = &#63;.
	 *
	 * <p>
	 * Useful when paginating results. Returns a maximum of <code>end - start</code> instances. <code>start</code> and <code>end</code> are not primary keys, they are indexes in the result set. Thus, <code>0</code> refers to the first result in the set. Setting both <code>start</code> and <code>end</code> to <code>com.liferay.portal.kernel.dao.orm.QueryUtil#ALL_POS</code> will return the full result set. If <code>orderByComparator</code> is specified, then the query will include the given ORDER BY logic. If <code>orderByComparator</code> is absent, then the query will include the default ORDER BY logic from <code>com.liferay.commerce.product.model.impl.CPOptionValueModelImpl</code>.
	 * </p>
	 *
	 * @param uuid the uuid
	 * @param companyId the company ID
	 * @param start the lower bound of the range of cp option values
	 * @param end the upper bound of the range of cp option values (not inclusive)
	 * @param orderByComparator the comparator to order the results by (optionally <code>null</code>)
	 * @return the ordered range of matching cp option values
	 */
	public default java.util.List<CPOptionValue> findByUuid_C(
		String uuid, long companyId, int start, int end,
		com.liferay.portal.kernel.util.OrderByComparator<CPOptionValue>
			orderByComparator) {

		return findByUuid_C(
			uuid, companyId, start, end, orderByComparator, true);
	}

	/**
	 * Returns all the cp option values where CPOptionId = &#63;.
	 *
	 * @param CPOptionId the cp option ID
	 * @return the matching cp option values
	 */
	public default java.util.List<CPOptionValue> findByCPOptionId(
		long CPOptionId) {

		return findByCPOptionId(
			CPOptionId, com.liferay.portal.kernel.dao.orm.QueryUtil.ALL_POS,
			com.liferay.portal.kernel.dao.orm.QueryUtil.ALL_POS, null, true);
	}

	/**
	 * Returns a range of all the cp option values where CPOptionId = &#63;.
	 *
	 * <p>
	 * Useful when paginating results. Returns a maximum of <code>end - start</code> instances. <code>start</code> and <code>end</code> are not primary keys, they are indexes in the result set. Thus, <code>0</code> refers to the first result in the set. Setting both <code>start</code> and <code>end</code> to <code>com.liferay.portal.kernel.dao.orm.QueryUtil#ALL_POS</code> will return the full result set. If <code>orderByComparator</code> is specified, then the query will include the given ORDER BY logic. If <code>orderByComparator</code> is absent, then the query will include the default ORDER BY logic from <code>com.liferay.commerce.product.model.impl.CPOptionValueModelImpl</code>.
	 * </p>
	 *
	 * @param CPOptionId the cp option ID
	 * @param start the lower bound of the range of cp option values
	 * @param end the upper bound of the range of cp option values (not inclusive)
	 * @return the range of matching cp option values
	 */
	public default java.util.List<CPOptionValue> findByCPOptionId(
		long CPOptionId, int start, int end) {

		return findByCPOptionId(CPOptionId, start, end, null, true);
	}

	/**
	 * Returns an ordered range of all the cp option values where CPOptionId = &#63;.
	 *
	 * <p>
	 * Useful when paginating results. Returns a maximum of <code>end - start</code> instances. <code>start</code> and <code>end</code> are not primary keys, they are indexes in the result set. Thus, <code>0</code> refers to the first result in the set. Setting both <code>start</code> and <code>end</code> to <code>com.liferay.portal.kernel.dao.orm.QueryUtil#ALL_POS</code> will return the full result set. If <code>orderByComparator</code> is specified, then the query will include the given ORDER BY logic. If <code>orderByComparator</code> is absent, then the query will include the default ORDER BY logic from <code>com.liferay.commerce.product.model.impl.CPOptionValueModelImpl</code>.
	 * </p>
	 *
	 * @param CPOptionId the cp option ID
	 * @param start the lower bound of the range of cp option values
	 * @param end the upper bound of the range of cp option values (not inclusive)
	 * @param orderByComparator the comparator to order the results by (optionally <code>null</code>)
	 * @return the ordered range of matching cp option values
	 */
	public default java.util.List<CPOptionValue> findByCPOptionId(
		long CPOptionId, int start, int end,
		com.liferay.portal.kernel.util.OrderByComparator<CPOptionValue>
			orderByComparator) {

		return findByCPOptionId(
			CPOptionId, start, end, orderByComparator, true);
	}

}
// LIFERAY-SERVICE-BUILDER-HASH:-321810793