/**
 * SPDX-FileCopyrightText: (c) 2024 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

package com.liferay.commerce.product.service.persistence;

import com.liferay.commerce.product.exception.NoSuchCPConfigurationListException;
import com.liferay.commerce.product.model.CPConfigurationList;
import com.liferay.portal.kernel.service.persistence.BasePersistence;
import com.liferay.portal.kernel.service.persistence.change.tracking.CTPersistence;

import org.osgi.annotation.versioning.ProviderType;

/**
 * The persistence interface for the cp configuration list service.
 *
 * <p>
 * Caching information and settings can be found in <code>portal.properties</code>
 * </p>
 *
 * @author Marco Leo
 * @see CPConfigurationListUtil
 * @generated
 */
@ProviderType
public interface CPConfigurationListPersistence
	extends BasePersistence<CPConfigurationList>,
			CTPersistence<CPConfigurationList> {

	/*
	 * NOTE FOR DEVELOPERS:
	 *
	 * Never modify or reference this interface directly. Always use {@link CPConfigurationListUtil} to access the cp configuration list persistence. Modify <code>service.xml</code> and rerun ServiceBuilder to regenerate this interface.
	 */

	/**
	 * Returns an ordered range of all the cp configuration lists where uuid = &#63;.
	 *
	 * <p>
	 * Useful when paginating results. Returns a maximum of <code>end - start</code> instances. <code>start</code> and <code>end</code> are not primary keys, they are indexes in the result set. Thus, <code>0</code> refers to the first result in the set. Setting both <code>start</code> and <code>end</code> to <code>com.liferay.portal.kernel.dao.orm.QueryUtil#ALL_POS</code> will return the full result set. If <code>orderByComparator</code> is specified, then the query will include the given ORDER BY logic. If <code>orderByComparator</code> is absent, then the query will include the default ORDER BY logic from <code>com.liferay.commerce.product.model.impl.CPConfigurationListModelImpl</code>.
	 * </p>
	 *
	 * @param uuid the uuid
	 * @param start the lower bound of the range of cp configuration lists
	 * @param end the upper bound of the range of cp configuration lists (not inclusive)
	 * @param orderByComparator the comparator to order the results by (optionally <code>null</code>)
	 * @param useFinderCache whether to use the finder cache
	 * @return the ordered range of matching cp configuration lists
	 */
	public java.util.List<CPConfigurationList> findByUuid(
		String uuid, int start, int end,
		com.liferay.portal.kernel.util.OrderByComparator<CPConfigurationList>
			orderByComparator,
		boolean useFinderCache);

	/**
	 * Returns the first cp configuration list in the ordered set where uuid = &#63;.
	 *
	 * @param uuid the uuid
	 * @param orderByComparator the comparator to order the set by (optionally <code>null</code>)
	 * @return the first matching cp configuration list
	 * @throws NoSuchCPConfigurationListException if a matching cp configuration list could not be found
	 */
	public CPConfigurationList findByUuid_First(
			String uuid,
			com.liferay.portal.kernel.util.OrderByComparator
				<CPConfigurationList> orderByComparator)
		throws NoSuchCPConfigurationListException;

	/**
	 * Returns the first cp configuration list in the ordered set where uuid = &#63;.
	 *
	 * @param uuid the uuid
	 * @param orderByComparator the comparator to order the set by (optionally <code>null</code>)
	 * @return the first matching cp configuration list, or <code>null</code> if a matching cp configuration list could not be found
	 */
	public CPConfigurationList fetchByUuid_First(
		String uuid,
		com.liferay.portal.kernel.util.OrderByComparator<CPConfigurationList>
			orderByComparator);

	/**
	 * Removes all the cp configuration lists where uuid = &#63; from the database.
	 *
	 * @param uuid the uuid
	 */
	public void removeByUuid(String uuid);

	/**
	 * Returns the number of cp configuration lists where uuid = &#63;.
	 *
	 * @param uuid the uuid
	 * @return the number of matching cp configuration lists
	 */
	public int countByUuid(String uuid);

	/**
	 * Returns the cp configuration list where uuid = &#63; and groupId = &#63; or throws a <code>NoSuchCPConfigurationListException</code> if it could not be found.
	 *
	 * @param uuid the uuid
	 * @param groupId the group ID
	 * @return the matching cp configuration list
	 * @throws NoSuchCPConfigurationListException if a matching cp configuration list could not be found
	 */
	public CPConfigurationList findByUUID_G(String uuid, long groupId)
		throws NoSuchCPConfigurationListException;

	/**
	 * Returns the cp configuration list where uuid = &#63; and groupId = &#63; or returns <code>null</code> if it could not be found, optionally using the finder cache.
	 *
	 * @param uuid the uuid
	 * @param groupId the group ID
	 * @param useFinderCache whether to use the finder cache
	 * @return the matching cp configuration list, or <code>null</code> if a matching cp configuration list could not be found
	 */
	public CPConfigurationList fetchByUUID_G(
		String uuid, long groupId, boolean useFinderCache);

	/**
	 * Removes the cp configuration list where uuid = &#63; and groupId = &#63; from the database.
	 *
	 * @param uuid the uuid
	 * @param groupId the group ID
	 * @return the cp configuration list that was removed
	 */
	public CPConfigurationList removeByUUID_G(String uuid, long groupId)
		throws NoSuchCPConfigurationListException;

	/**
	 * Returns the number of cp configuration lists where uuid = &#63; and groupId = &#63;.
	 *
	 * @param uuid the uuid
	 * @param groupId the group ID
	 * @return the number of matching cp configuration lists
	 */
	public int countByUUID_G(String uuid, long groupId);

	/**
	 * Returns an ordered range of all the cp configuration lists where uuid = &#63; and companyId = &#63;.
	 *
	 * <p>
	 * Useful when paginating results. Returns a maximum of <code>end - start</code> instances. <code>start</code> and <code>end</code> are not primary keys, they are indexes in the result set. Thus, <code>0</code> refers to the first result in the set. Setting both <code>start</code> and <code>end</code> to <code>com.liferay.portal.kernel.dao.orm.QueryUtil#ALL_POS</code> will return the full result set. If <code>orderByComparator</code> is specified, then the query will include the given ORDER BY logic. If <code>orderByComparator</code> is absent, then the query will include the default ORDER BY logic from <code>com.liferay.commerce.product.model.impl.CPConfigurationListModelImpl</code>.
	 * </p>
	 *
	 * @param uuid the uuid
	 * @param companyId the company ID
	 * @param start the lower bound of the range of cp configuration lists
	 * @param end the upper bound of the range of cp configuration lists (not inclusive)
	 * @param orderByComparator the comparator to order the results by (optionally <code>null</code>)
	 * @param useFinderCache whether to use the finder cache
	 * @return the ordered range of matching cp configuration lists
	 */
	public java.util.List<CPConfigurationList> findByUuid_C(
		String uuid, long companyId, int start, int end,
		com.liferay.portal.kernel.util.OrderByComparator<CPConfigurationList>
			orderByComparator,
		boolean useFinderCache);

	/**
	 * Returns the first cp configuration list in the ordered set where uuid = &#63; and companyId = &#63;.
	 *
	 * @param uuid the uuid
	 * @param companyId the company ID
	 * @param orderByComparator the comparator to order the set by (optionally <code>null</code>)
	 * @return the first matching cp configuration list
	 * @throws NoSuchCPConfigurationListException if a matching cp configuration list could not be found
	 */
	public CPConfigurationList findByUuid_C_First(
			String uuid, long companyId,
			com.liferay.portal.kernel.util.OrderByComparator
				<CPConfigurationList> orderByComparator)
		throws NoSuchCPConfigurationListException;

	/**
	 * Returns the first cp configuration list in the ordered set where uuid = &#63; and companyId = &#63;.
	 *
	 * @param uuid the uuid
	 * @param companyId the company ID
	 * @param orderByComparator the comparator to order the set by (optionally <code>null</code>)
	 * @return the first matching cp configuration list, or <code>null</code> if a matching cp configuration list could not be found
	 */
	public CPConfigurationList fetchByUuid_C_First(
		String uuid, long companyId,
		com.liferay.portal.kernel.util.OrderByComparator<CPConfigurationList>
			orderByComparator);

	/**
	 * Removes all the cp configuration lists where uuid = &#63; and companyId = &#63; from the database.
	 *
	 * @param uuid the uuid
	 * @param companyId the company ID
	 */
	public void removeByUuid_C(String uuid, long companyId);

	/**
	 * Returns the number of cp configuration lists where uuid = &#63; and companyId = &#63;.
	 *
	 * @param uuid the uuid
	 * @param companyId the company ID
	 * @return the number of matching cp configuration lists
	 */
	public int countByUuid_C(String uuid, long companyId);

	/**
	 * Returns an ordered range of all the cp configuration lists where groupId = &#63; and companyId = &#63;.
	 *
	 * <p>
	 * Useful when paginating results. Returns a maximum of <code>end - start</code> instances. <code>start</code> and <code>end</code> are not primary keys, they are indexes in the result set. Thus, <code>0</code> refers to the first result in the set. Setting both <code>start</code> and <code>end</code> to <code>com.liferay.portal.kernel.dao.orm.QueryUtil#ALL_POS</code> will return the full result set. If <code>orderByComparator</code> is specified, then the query will include the given ORDER BY logic. If <code>orderByComparator</code> is absent, then the query will include the default ORDER BY logic from <code>com.liferay.commerce.product.model.impl.CPConfigurationListModelImpl</code>.
	 * </p>
	 *
	 * @param groupId the group ID
	 * @param companyId the company ID
	 * @param start the lower bound of the range of cp configuration lists
	 * @param end the upper bound of the range of cp configuration lists (not inclusive)
	 * @param orderByComparator the comparator to order the results by (optionally <code>null</code>)
	 * @param useFinderCache whether to use the finder cache
	 * @return the ordered range of matching cp configuration lists
	 */
	public java.util.List<CPConfigurationList> findByG_C(
		long groupId, long companyId, int start, int end,
		com.liferay.portal.kernel.util.OrderByComparator<CPConfigurationList>
			orderByComparator,
		boolean useFinderCache);

	/**
	 * Returns the first cp configuration list in the ordered set where groupId = &#63; and companyId = &#63;.
	 *
	 * @param groupId the group ID
	 * @param companyId the company ID
	 * @param orderByComparator the comparator to order the set by (optionally <code>null</code>)
	 * @return the first matching cp configuration list
	 * @throws NoSuchCPConfigurationListException if a matching cp configuration list could not be found
	 */
	public CPConfigurationList findByG_C_First(
			long groupId, long companyId,
			com.liferay.portal.kernel.util.OrderByComparator
				<CPConfigurationList> orderByComparator)
		throws NoSuchCPConfigurationListException;

	/**
	 * Returns the first cp configuration list in the ordered set where groupId = &#63; and companyId = &#63;.
	 *
	 * @param groupId the group ID
	 * @param companyId the company ID
	 * @param orderByComparator the comparator to order the set by (optionally <code>null</code>)
	 * @return the first matching cp configuration list, or <code>null</code> if a matching cp configuration list could not be found
	 */
	public CPConfigurationList fetchByG_C_First(
		long groupId, long companyId,
		com.liferay.portal.kernel.util.OrderByComparator<CPConfigurationList>
			orderByComparator);

	/**
	 * Returns an ordered range of all the cp configuration lists where groupId = &#63; and companyId = &#63;, optionally using the finder cache.
	 *
	 * <p>
	 * Useful when paginating results. Returns a maximum of <code>end - start</code> instances. <code>start</code> and <code>end</code> are not primary keys, they are indexes in the result set. Thus, <code>0</code> refers to the first result in the set. Setting both <code>start</code> and <code>end</code> to <code>com.liferay.portal.kernel.dao.orm.QueryUtil#ALL_POS</code> will return the full result set. If <code>orderByComparator</code> is specified, then the query will include the given ORDER BY logic. If <code>orderByComparator</code> is absent, then the query will include the default ORDER BY logic from <code>com.liferay.commerce.product.model.impl.CPConfigurationListModelImpl</code>.
	 * </p>
	 *
	 * @param groupIds the group IDs
	 * @param companyId the company ID
	 * @param start the lower bound of the range of cp configuration lists
	 * @param end the upper bound of the range of cp configuration lists (not inclusive)
	 * @param orderByComparator the comparator to order the results by (optionally <code>null</code>)
	 * @param useFinderCache whether to use the finder cache
	 * @return the ordered range of matching cp configuration lists
	 */
	public java.util.List<CPConfigurationList> findByG_C(
		long[] groupIds, long companyId, int start, int end,
		com.liferay.portal.kernel.util.OrderByComparator<CPConfigurationList>
			orderByComparator,
		boolean useFinderCache);

	/**
	 * Removes all the cp configuration lists where groupId = &#63; and companyId = &#63; from the database.
	 *
	 * @param groupId the group ID
	 * @param companyId the company ID
	 */
	public void removeByG_C(long groupId, long companyId);

	/**
	 * Returns the number of cp configuration lists where groupId = &#63; and companyId = &#63;.
	 *
	 * @param groupId the group ID
	 * @param companyId the company ID
	 * @return the number of matching cp configuration lists
	 */
	public int countByG_C(long groupId, long companyId);

	/**
	 * Returns the number of cp configuration lists where groupId = any &#63; and companyId = &#63;.
	 *
	 * @param groupIds the group IDs
	 * @param companyId the company ID
	 * @return the number of matching cp configuration lists
	 */
	public int countByG_C(long[] groupIds, long companyId);

	/**
	 * Returns an ordered range of all the cp configuration lists where groupId = &#63; and master = &#63;.
	 *
	 * <p>
	 * Useful when paginating results. Returns a maximum of <code>end - start</code> instances. <code>start</code> and <code>end</code> are not primary keys, they are indexes in the result set. Thus, <code>0</code> refers to the first result in the set. Setting both <code>start</code> and <code>end</code> to <code>com.liferay.portal.kernel.dao.orm.QueryUtil#ALL_POS</code> will return the full result set. If <code>orderByComparator</code> is specified, then the query will include the given ORDER BY logic. If <code>orderByComparator</code> is absent, then the query will include the default ORDER BY logic from <code>com.liferay.commerce.product.model.impl.CPConfigurationListModelImpl</code>.
	 * </p>
	 *
	 * @param groupId the group ID
	 * @param master the master
	 * @param start the lower bound of the range of cp configuration lists
	 * @param end the upper bound of the range of cp configuration lists (not inclusive)
	 * @param orderByComparator the comparator to order the results by (optionally <code>null</code>)
	 * @param useFinderCache whether to use the finder cache
	 * @return the ordered range of matching cp configuration lists
	 */
	public java.util.List<CPConfigurationList> findByG_M(
		long groupId, boolean master, int start, int end,
		com.liferay.portal.kernel.util.OrderByComparator<CPConfigurationList>
			orderByComparator,
		boolean useFinderCache);

	/**
	 * Returns the first cp configuration list in the ordered set where groupId = &#63; and master = &#63;.
	 *
	 * @param groupId the group ID
	 * @param master the master
	 * @param orderByComparator the comparator to order the set by (optionally <code>null</code>)
	 * @return the first matching cp configuration list
	 * @throws NoSuchCPConfigurationListException if a matching cp configuration list could not be found
	 */
	public CPConfigurationList findByG_M_First(
			long groupId, boolean master,
			com.liferay.portal.kernel.util.OrderByComparator
				<CPConfigurationList> orderByComparator)
		throws NoSuchCPConfigurationListException;

	/**
	 * Returns the first cp configuration list in the ordered set where groupId = &#63; and master = &#63;.
	 *
	 * @param groupId the group ID
	 * @param master the master
	 * @param orderByComparator the comparator to order the set by (optionally <code>null</code>)
	 * @return the first matching cp configuration list, or <code>null</code> if a matching cp configuration list could not be found
	 */
	public CPConfigurationList fetchByG_M_First(
		long groupId, boolean master,
		com.liferay.portal.kernel.util.OrderByComparator<CPConfigurationList>
			orderByComparator);

	/**
	 * Removes all the cp configuration lists where groupId = &#63; and master = &#63; from the database.
	 *
	 * @param groupId the group ID
	 * @param master the master
	 */
	public void removeByG_M(long groupId, boolean master);

	/**
	 * Returns the number of cp configuration lists where groupId = &#63; and master = &#63;.
	 *
	 * @param groupId the group ID
	 * @param master the master
	 * @return the number of matching cp configuration lists
	 */
	public int countByG_M(long groupId, boolean master);

	/**
	 * Returns an ordered range of all the cp configuration lists where groupId = &#63; and companyId = &#63; and status = &#63;.
	 *
	 * <p>
	 * Useful when paginating results. Returns a maximum of <code>end - start</code> instances. <code>start</code> and <code>end</code> are not primary keys, they are indexes in the result set. Thus, <code>0</code> refers to the first result in the set. Setting both <code>start</code> and <code>end</code> to <code>com.liferay.portal.kernel.dao.orm.QueryUtil#ALL_POS</code> will return the full result set. If <code>orderByComparator</code> is specified, then the query will include the given ORDER BY logic. If <code>orderByComparator</code> is absent, then the query will include the default ORDER BY logic from <code>com.liferay.commerce.product.model.impl.CPConfigurationListModelImpl</code>.
	 * </p>
	 *
	 * @param groupId the group ID
	 * @param companyId the company ID
	 * @param status the status
	 * @param start the lower bound of the range of cp configuration lists
	 * @param end the upper bound of the range of cp configuration lists (not inclusive)
	 * @param orderByComparator the comparator to order the results by (optionally <code>null</code>)
	 * @param useFinderCache whether to use the finder cache
	 * @return the ordered range of matching cp configuration lists
	 */
	public java.util.List<CPConfigurationList> findByG_C_S(
		long groupId, long companyId, int status, int start, int end,
		com.liferay.portal.kernel.util.OrderByComparator<CPConfigurationList>
			orderByComparator,
		boolean useFinderCache);

	/**
	 * Returns the first cp configuration list in the ordered set where groupId = &#63; and companyId = &#63; and status = &#63;.
	 *
	 * @param groupId the group ID
	 * @param companyId the company ID
	 * @param status the status
	 * @param orderByComparator the comparator to order the set by (optionally <code>null</code>)
	 * @return the first matching cp configuration list
	 * @throws NoSuchCPConfigurationListException if a matching cp configuration list could not be found
	 */
	public CPConfigurationList findByG_C_S_First(
			long groupId, long companyId, int status,
			com.liferay.portal.kernel.util.OrderByComparator
				<CPConfigurationList> orderByComparator)
		throws NoSuchCPConfigurationListException;

	/**
	 * Returns the first cp configuration list in the ordered set where groupId = &#63; and companyId = &#63; and status = &#63;.
	 *
	 * @param groupId the group ID
	 * @param companyId the company ID
	 * @param status the status
	 * @param orderByComparator the comparator to order the set by (optionally <code>null</code>)
	 * @return the first matching cp configuration list, or <code>null</code> if a matching cp configuration list could not be found
	 */
	public CPConfigurationList fetchByG_C_S_First(
		long groupId, long companyId, int status,
		com.liferay.portal.kernel.util.OrderByComparator<CPConfigurationList>
			orderByComparator);

	/**
	 * Returns an ordered range of all the cp configuration lists where groupId = &#63; and companyId = &#63; and status = &#63;, optionally using the finder cache.
	 *
	 * <p>
	 * Useful when paginating results. Returns a maximum of <code>end - start</code> instances. <code>start</code> and <code>end</code> are not primary keys, they are indexes in the result set. Thus, <code>0</code> refers to the first result in the set. Setting both <code>start</code> and <code>end</code> to <code>com.liferay.portal.kernel.dao.orm.QueryUtil#ALL_POS</code> will return the full result set. If <code>orderByComparator</code> is specified, then the query will include the given ORDER BY logic. If <code>orderByComparator</code> is absent, then the query will include the default ORDER BY logic from <code>com.liferay.commerce.product.model.impl.CPConfigurationListModelImpl</code>.
	 * </p>
	 *
	 * @param groupIds the group IDs
	 * @param companyId the company ID
	 * @param status the status
	 * @param start the lower bound of the range of cp configuration lists
	 * @param end the upper bound of the range of cp configuration lists (not inclusive)
	 * @param orderByComparator the comparator to order the results by (optionally <code>null</code>)
	 * @param useFinderCache whether to use the finder cache
	 * @return the ordered range of matching cp configuration lists
	 */
	public java.util.List<CPConfigurationList> findByG_C_S(
		long[] groupIds, long companyId, int status, int start, int end,
		com.liferay.portal.kernel.util.OrderByComparator<CPConfigurationList>
			orderByComparator,
		boolean useFinderCache);

	/**
	 * Removes all the cp configuration lists where groupId = &#63; and companyId = &#63; and status = &#63; from the database.
	 *
	 * @param groupId the group ID
	 * @param companyId the company ID
	 * @param status the status
	 */
	public void removeByG_C_S(long groupId, long companyId, int status);

	/**
	 * Returns the number of cp configuration lists where groupId = &#63; and companyId = &#63; and status = &#63;.
	 *
	 * @param groupId the group ID
	 * @param companyId the company ID
	 * @param status the status
	 * @return the number of matching cp configuration lists
	 */
	public int countByG_C_S(long groupId, long companyId, int status);

	/**
	 * Returns the number of cp configuration lists where groupId = any &#63; and companyId = &#63; and status = &#63;.
	 *
	 * @param groupIds the group IDs
	 * @param companyId the company ID
	 * @param status the status
	 * @return the number of matching cp configuration lists
	 */
	public int countByG_C_S(long[] groupIds, long companyId, int status);

	/**
	 * Returns the cp configuration list where externalReferenceCode = &#63; and companyId = &#63; or throws a <code>NoSuchCPConfigurationListException</code> if it could not be found.
	 *
	 * @param externalReferenceCode the external reference code
	 * @param companyId the company ID
	 * @return the matching cp configuration list
	 * @throws NoSuchCPConfigurationListException if a matching cp configuration list could not be found
	 */
	public CPConfigurationList findByERC_C(
			String externalReferenceCode, long companyId)
		throws NoSuchCPConfigurationListException;

	/**
	 * Returns the cp configuration list where externalReferenceCode = &#63; and companyId = &#63; or returns <code>null</code> if it could not be found, optionally using the finder cache.
	 *
	 * @param externalReferenceCode the external reference code
	 * @param companyId the company ID
	 * @param useFinderCache whether to use the finder cache
	 * @return the matching cp configuration list, or <code>null</code> if a matching cp configuration list could not be found
	 */
	public CPConfigurationList fetchByERC_C(
		String externalReferenceCode, long companyId, boolean useFinderCache);

	/**
	 * Removes the cp configuration list where externalReferenceCode = &#63; and companyId = &#63; from the database.
	 *
	 * @param externalReferenceCode the external reference code
	 * @param companyId the company ID
	 * @return the cp configuration list that was removed
	 */
	public CPConfigurationList removeByERC_C(
			String externalReferenceCode, long companyId)
		throws NoSuchCPConfigurationListException;

	/**
	 * Returns the number of cp configuration lists where externalReferenceCode = &#63; and companyId = &#63;.
	 *
	 * @param externalReferenceCode the external reference code
	 * @param companyId the company ID
	 * @return the number of matching cp configuration lists
	 */
	public int countByERC_C(String externalReferenceCode, long companyId);

	/**
	 * Creates a new cp configuration list with the primary key. Does not add the cp configuration list to the database.
	 *
	 * @param CPConfigurationListId the primary key for the new cp configuration list
	 * @return the new cp configuration list
	 */
	public CPConfigurationList create(long CPConfigurationListId);

	/**
	 * Removes the cp configuration list with the primary key from the database. Also notifies the appropriate model listeners.
	 *
	 * @param CPConfigurationListId the primary key of the cp configuration list
	 * @return the cp configuration list that was removed
	 * @throws NoSuchCPConfigurationListException if a cp configuration list with the primary key could not be found
	 */
	public CPConfigurationList remove(long CPConfigurationListId)
		throws NoSuchCPConfigurationListException;

	public CPConfigurationList updateImpl(
		CPConfigurationList cpConfigurationList);

	/**
	 * Returns the cp configuration list with the primary key or throws a <code>NoSuchCPConfigurationListException</code> if it could not be found.
	 *
	 * @param CPConfigurationListId the primary key of the cp configuration list
	 * @return the cp configuration list
	 * @throws NoSuchCPConfigurationListException if a cp configuration list with the primary key could not be found
	 */
	public CPConfigurationList findByPrimaryKey(long CPConfigurationListId)
		throws NoSuchCPConfigurationListException;

	/**
	 * Returns the cp configuration list with the primary key or returns <code>null</code> if it could not be found.
	 *
	 * @param CPConfigurationListId the primary key of the cp configuration list
	 * @return the cp configuration list, or <code>null</code> if a cp configuration list with the primary key could not be found
	 */
	public CPConfigurationList fetchByPrimaryKey(long CPConfigurationListId);

	/**
	 * Returns the cp configuration list where uuid = &#63; and groupId = &#63; or returns <code>null</code> if it could not be found. Uses the finder cache.
	 *
	 * @param uuid the uuid
	 * @param groupId the group ID
	 * @return the matching cp configuration list, or <code>null</code> if a matching cp configuration list could not be found
	 */
	public default CPConfigurationList fetchByUUID_G(
		String uuid, long groupId) {

		return fetchByUUID_G(uuid, groupId, true);
	}

	/**
	 * Returns the cp configuration list where externalReferenceCode = &#63; and companyId = &#63; or returns <code>null</code> if it could not be found. Uses the finder cache.
	 *
	 * @param externalReferenceCode the external reference code
	 * @param companyId the company ID
	 * @return the matching cp configuration list, or <code>null</code> if a matching cp configuration list could not be found
	 */
	public default CPConfigurationList fetchByERC_C(
		String externalReferenceCode, long companyId) {

		return fetchByERC_C(externalReferenceCode, companyId, true);
	}

	/**
	 * Returns all the cp configuration lists where uuid = &#63;.
	 *
	 * @param uuid the uuid
	 * @return the matching cp configuration lists
	 */
	public default java.util.List<CPConfigurationList> findByUuid(String uuid) {
		return findByUuid(
			uuid, com.liferay.portal.kernel.dao.orm.QueryUtil.ALL_POS,
			com.liferay.portal.kernel.dao.orm.QueryUtil.ALL_POS, null, true);
	}

	/**
	 * Returns a range of all the cp configuration lists where uuid = &#63;.
	 *
	 * <p>
	 * Useful when paginating results. Returns a maximum of <code>end - start</code> instances. <code>start</code> and <code>end</code> are not primary keys, they are indexes in the result set. Thus, <code>0</code> refers to the first result in the set. Setting both <code>start</code> and <code>end</code> to <code>com.liferay.portal.kernel.dao.orm.QueryUtil#ALL_POS</code> will return the full result set. If <code>orderByComparator</code> is specified, then the query will include the given ORDER BY logic. If <code>orderByComparator</code> is absent, then the query will include the default ORDER BY logic from <code>com.liferay.commerce.product.model.impl.CPConfigurationListModelImpl</code>.
	 * </p>
	 *
	 * @param uuid the uuid
	 * @param start the lower bound of the range of cp configuration lists
	 * @param end the upper bound of the range of cp configuration lists (not inclusive)
	 * @return the range of matching cp configuration lists
	 */
	public default java.util.List<CPConfigurationList> findByUuid(
		String uuid, int start, int end) {

		return findByUuid(uuid, start, end, null, true);
	}

	/**
	 * Returns an ordered range of all the cp configuration lists where uuid = &#63;.
	 *
	 * <p>
	 * Useful when paginating results. Returns a maximum of <code>end - start</code> instances. <code>start</code> and <code>end</code> are not primary keys, they are indexes in the result set. Thus, <code>0</code> refers to the first result in the set. Setting both <code>start</code> and <code>end</code> to <code>com.liferay.portal.kernel.dao.orm.QueryUtil#ALL_POS</code> will return the full result set. If <code>orderByComparator</code> is specified, then the query will include the given ORDER BY logic. If <code>orderByComparator</code> is absent, then the query will include the default ORDER BY logic from <code>com.liferay.commerce.product.model.impl.CPConfigurationListModelImpl</code>.
	 * </p>
	 *
	 * @param uuid the uuid
	 * @param start the lower bound of the range of cp configuration lists
	 * @param end the upper bound of the range of cp configuration lists (not inclusive)
	 * @param orderByComparator the comparator to order the results by (optionally <code>null</code>)
	 * @return the ordered range of matching cp configuration lists
	 */
	public default java.util.List<CPConfigurationList> findByUuid(
		String uuid, int start, int end,
		com.liferay.portal.kernel.util.OrderByComparator<CPConfigurationList>
			orderByComparator) {

		return findByUuid(uuid, start, end, orderByComparator, true);
	}

	/**
	 * Returns all the cp configuration lists where uuid = &#63; and companyId = &#63;.
	 *
	 * @param uuid the uuid
	 * @param companyId the company ID
	 * @return the matching cp configuration lists
	 */
	public default java.util.List<CPConfigurationList> findByUuid_C(
		String uuid, long companyId) {

		return findByUuid_C(
			uuid, companyId,
			com.liferay.portal.kernel.dao.orm.QueryUtil.ALL_POS,
			com.liferay.portal.kernel.dao.orm.QueryUtil.ALL_POS, null, true);
	}

	/**
	 * Returns a range of all the cp configuration lists where uuid = &#63; and companyId = &#63;.
	 *
	 * <p>
	 * Useful when paginating results. Returns a maximum of <code>end - start</code> instances. <code>start</code> and <code>end</code> are not primary keys, they are indexes in the result set. Thus, <code>0</code> refers to the first result in the set. Setting both <code>start</code> and <code>end</code> to <code>com.liferay.portal.kernel.dao.orm.QueryUtil#ALL_POS</code> will return the full result set. If <code>orderByComparator</code> is specified, then the query will include the given ORDER BY logic. If <code>orderByComparator</code> is absent, then the query will include the default ORDER BY logic from <code>com.liferay.commerce.product.model.impl.CPConfigurationListModelImpl</code>.
	 * </p>
	 *
	 * @param uuid the uuid
	 * @param companyId the company ID
	 * @param start the lower bound of the range of cp configuration lists
	 * @param end the upper bound of the range of cp configuration lists (not inclusive)
	 * @return the range of matching cp configuration lists
	 */
	public default java.util.List<CPConfigurationList> findByUuid_C(
		String uuid, long companyId, int start, int end) {

		return findByUuid_C(uuid, companyId, start, end, null, true);
	}

	/**
	 * Returns an ordered range of all the cp configuration lists where uuid = &#63; and companyId = &#63;.
	 *
	 * <p>
	 * Useful when paginating results. Returns a maximum of <code>end - start</code> instances. <code>start</code> and <code>end</code> are not primary keys, they are indexes in the result set. Thus, <code>0</code> refers to the first result in the set. Setting both <code>start</code> and <code>end</code> to <code>com.liferay.portal.kernel.dao.orm.QueryUtil#ALL_POS</code> will return the full result set. If <code>orderByComparator</code> is specified, then the query will include the given ORDER BY logic. If <code>orderByComparator</code> is absent, then the query will include the default ORDER BY logic from <code>com.liferay.commerce.product.model.impl.CPConfigurationListModelImpl</code>.
	 * </p>
	 *
	 * @param uuid the uuid
	 * @param companyId the company ID
	 * @param start the lower bound of the range of cp configuration lists
	 * @param end the upper bound of the range of cp configuration lists (not inclusive)
	 * @param orderByComparator the comparator to order the results by (optionally <code>null</code>)
	 * @return the ordered range of matching cp configuration lists
	 */
	public default java.util.List<CPConfigurationList> findByUuid_C(
		String uuid, long companyId, int start, int end,
		com.liferay.portal.kernel.util.OrderByComparator<CPConfigurationList>
			orderByComparator) {

		return findByUuid_C(
			uuid, companyId, start, end, orderByComparator, true);
	}

	/**
	 * Returns all the cp configuration lists where groupId = &#63; and companyId = &#63;.
	 *
	 * @param groupId the group ID
	 * @param companyId the company ID
	 * @return the matching cp configuration lists
	 */
	public default java.util.List<CPConfigurationList> findByG_C(
		long groupId, long companyId) {

		return findByG_C(
			groupId, companyId,
			com.liferay.portal.kernel.dao.orm.QueryUtil.ALL_POS,
			com.liferay.portal.kernel.dao.orm.QueryUtil.ALL_POS, null, true);
	}

	/**
	 * Returns a range of all the cp configuration lists where groupId = &#63; and companyId = &#63;.
	 *
	 * <p>
	 * Useful when paginating results. Returns a maximum of <code>end - start</code> instances. <code>start</code> and <code>end</code> are not primary keys, they are indexes in the result set. Thus, <code>0</code> refers to the first result in the set. Setting both <code>start</code> and <code>end</code> to <code>com.liferay.portal.kernel.dao.orm.QueryUtil#ALL_POS</code> will return the full result set. If <code>orderByComparator</code> is specified, then the query will include the given ORDER BY logic. If <code>orderByComparator</code> is absent, then the query will include the default ORDER BY logic from <code>com.liferay.commerce.product.model.impl.CPConfigurationListModelImpl</code>.
	 * </p>
	 *
	 * @param groupId the group ID
	 * @param companyId the company ID
	 * @param start the lower bound of the range of cp configuration lists
	 * @param end the upper bound of the range of cp configuration lists (not inclusive)
	 * @return the range of matching cp configuration lists
	 */
	public default java.util.List<CPConfigurationList> findByG_C(
		long groupId, long companyId, int start, int end) {

		return findByG_C(groupId, companyId, start, end, null, true);
	}

	/**
	 * Returns an ordered range of all the cp configuration lists where groupId = &#63; and companyId = &#63;.
	 *
	 * <p>
	 * Useful when paginating results. Returns a maximum of <code>end - start</code> instances. <code>start</code> and <code>end</code> are not primary keys, they are indexes in the result set. Thus, <code>0</code> refers to the first result in the set. Setting both <code>start</code> and <code>end</code> to <code>com.liferay.portal.kernel.dao.orm.QueryUtil#ALL_POS</code> will return the full result set. If <code>orderByComparator</code> is specified, then the query will include the given ORDER BY logic. If <code>orderByComparator</code> is absent, then the query will include the default ORDER BY logic from <code>com.liferay.commerce.product.model.impl.CPConfigurationListModelImpl</code>.
	 * </p>
	 *
	 * @param groupId the group ID
	 * @param companyId the company ID
	 * @param start the lower bound of the range of cp configuration lists
	 * @param end the upper bound of the range of cp configuration lists (not inclusive)
	 * @param orderByComparator the comparator to order the results by (optionally <code>null</code>)
	 * @return the ordered range of matching cp configuration lists
	 */
	public default java.util.List<CPConfigurationList> findByG_C(
		long groupId, long companyId, int start, int end,
		com.liferay.portal.kernel.util.OrderByComparator<CPConfigurationList>
			orderByComparator) {

		return findByG_C(
			groupId, companyId, start, end, orderByComparator, true);
	}

	/**
	 * Returns all the cp configuration lists where groupId = any &#63; and companyId = &#63;.
	 *
	 * @param groupIds the group IDs
	 * @param companyId the company ID
	 * @return the matching cp configuration lists
	 */
	public default java.util.List<CPConfigurationList> findByG_C(
		long[] groupIds, long companyId) {

		return findByG_C(
			groupIds, companyId,
			com.liferay.portal.kernel.dao.orm.QueryUtil.ALL_POS,
			com.liferay.portal.kernel.dao.orm.QueryUtil.ALL_POS, null, true);
	}

	/**
	 * Returns a range of all the cp configuration lists where groupId = any &#63; and companyId = &#63;.
	 *
	 * <p>
	 * Useful when paginating results. Returns a maximum of <code>end - start</code> instances. <code>start</code> and <code>end</code> are not primary keys, they are indexes in the result set. Thus, <code>0</code> refers to the first result in the set. Setting both <code>start</code> and <code>end</code> to <code>com.liferay.portal.kernel.dao.orm.QueryUtil#ALL_POS</code> will return the full result set. If <code>orderByComparator</code> is specified, then the query will include the given ORDER BY logic. If <code>orderByComparator</code> is absent, then the query will include the default ORDER BY logic from <code>com.liferay.commerce.product.model.impl.CPConfigurationListModelImpl</code>.
	 * </p>
	 *
	 * @param groupIds the group IDs
	 * @param companyId the company ID
	 * @param start the lower bound of the range of cp configuration lists
	 * @param end the upper bound of the range of cp configuration lists (not inclusive)
	 * @return the range of matching cp configuration lists
	 */
	public default java.util.List<CPConfigurationList> findByG_C(
		long[] groupIds, long companyId, int start, int end) {

		return findByG_C(groupIds, companyId, start, end, null, true);
	}

	/**
	 * Returns an ordered range of all the cp configuration lists where groupId = any &#63; and companyId = &#63;.
	 *
	 * <p>
	 * Useful when paginating results. Returns a maximum of <code>end - start</code> instances. <code>start</code> and <code>end</code> are not primary keys, they are indexes in the result set. Thus, <code>0</code> refers to the first result in the set. Setting both <code>start</code> and <code>end</code> to <code>com.liferay.portal.kernel.dao.orm.QueryUtil#ALL_POS</code> will return the full result set. If <code>orderByComparator</code> is specified, then the query will include the given ORDER BY logic. If <code>orderByComparator</code> is absent, then the query will include the default ORDER BY logic from <code>com.liferay.commerce.product.model.impl.CPConfigurationListModelImpl</code>.
	 * </p>
	 *
	 * @param groupIds the group IDs
	 * @param companyId the company ID
	 * @param start the lower bound of the range of cp configuration lists
	 * @param end the upper bound of the range of cp configuration lists (not inclusive)
	 * @param orderByComparator the comparator to order the results by (optionally <code>null</code>)
	 * @return the ordered range of matching cp configuration lists
	 */
	public default java.util.List<CPConfigurationList> findByG_C(
		long[] groupIds, long companyId, int start, int end,
		com.liferay.portal.kernel.util.OrderByComparator<CPConfigurationList>
			orderByComparator) {

		return findByG_C(
			groupIds, companyId, start, end, orderByComparator, true);
	}

	/**
	 * Returns all the cp configuration lists where groupId = &#63; and master = &#63;.
	 *
	 * @param groupId the group ID
	 * @param master the master
	 * @return the matching cp configuration lists
	 */
	public default java.util.List<CPConfigurationList> findByG_M(
		long groupId, boolean master) {

		return findByG_M(
			groupId, master,
			com.liferay.portal.kernel.dao.orm.QueryUtil.ALL_POS,
			com.liferay.portal.kernel.dao.orm.QueryUtil.ALL_POS, null, true);
	}

	/**
	 * Returns a range of all the cp configuration lists where groupId = &#63; and master = &#63;.
	 *
	 * <p>
	 * Useful when paginating results. Returns a maximum of <code>end - start</code> instances. <code>start</code> and <code>end</code> are not primary keys, they are indexes in the result set. Thus, <code>0</code> refers to the first result in the set. Setting both <code>start</code> and <code>end</code> to <code>com.liferay.portal.kernel.dao.orm.QueryUtil#ALL_POS</code> will return the full result set. If <code>orderByComparator</code> is specified, then the query will include the given ORDER BY logic. If <code>orderByComparator</code> is absent, then the query will include the default ORDER BY logic from <code>com.liferay.commerce.product.model.impl.CPConfigurationListModelImpl</code>.
	 * </p>
	 *
	 * @param groupId the group ID
	 * @param master the master
	 * @param start the lower bound of the range of cp configuration lists
	 * @param end the upper bound of the range of cp configuration lists (not inclusive)
	 * @return the range of matching cp configuration lists
	 */
	public default java.util.List<CPConfigurationList> findByG_M(
		long groupId, boolean master, int start, int end) {

		return findByG_M(groupId, master, start, end, null, true);
	}

	/**
	 * Returns an ordered range of all the cp configuration lists where groupId = &#63; and master = &#63;.
	 *
	 * <p>
	 * Useful when paginating results. Returns a maximum of <code>end - start</code> instances. <code>start</code> and <code>end</code> are not primary keys, they are indexes in the result set. Thus, <code>0</code> refers to the first result in the set. Setting both <code>start</code> and <code>end</code> to <code>com.liferay.portal.kernel.dao.orm.QueryUtil#ALL_POS</code> will return the full result set. If <code>orderByComparator</code> is specified, then the query will include the given ORDER BY logic. If <code>orderByComparator</code> is absent, then the query will include the default ORDER BY logic from <code>com.liferay.commerce.product.model.impl.CPConfigurationListModelImpl</code>.
	 * </p>
	 *
	 * @param groupId the group ID
	 * @param master the master
	 * @param start the lower bound of the range of cp configuration lists
	 * @param end the upper bound of the range of cp configuration lists (not inclusive)
	 * @param orderByComparator the comparator to order the results by (optionally <code>null</code>)
	 * @return the ordered range of matching cp configuration lists
	 */
	public default java.util.List<CPConfigurationList> findByG_M(
		long groupId, boolean master, int start, int end,
		com.liferay.portal.kernel.util.OrderByComparator<CPConfigurationList>
			orderByComparator) {

		return findByG_M(groupId, master, start, end, orderByComparator, true);
	}

	/**
	 * Returns all the cp configuration lists where groupId = &#63; and companyId = &#63; and status = &#63;.
	 *
	 * @param groupId the group ID
	 * @param companyId the company ID
	 * @param status the status
	 * @return the matching cp configuration lists
	 */
	public default java.util.List<CPConfigurationList> findByG_C_S(
		long groupId, long companyId, int status) {

		return findByG_C_S(
			groupId, companyId, status,
			com.liferay.portal.kernel.dao.orm.QueryUtil.ALL_POS,
			com.liferay.portal.kernel.dao.orm.QueryUtil.ALL_POS, null, true);
	}

	/**
	 * Returns a range of all the cp configuration lists where groupId = &#63; and companyId = &#63; and status = &#63;.
	 *
	 * <p>
	 * Useful when paginating results. Returns a maximum of <code>end - start</code> instances. <code>start</code> and <code>end</code> are not primary keys, they are indexes in the result set. Thus, <code>0</code> refers to the first result in the set. Setting both <code>start</code> and <code>end</code> to <code>com.liferay.portal.kernel.dao.orm.QueryUtil#ALL_POS</code> will return the full result set. If <code>orderByComparator</code> is specified, then the query will include the given ORDER BY logic. If <code>orderByComparator</code> is absent, then the query will include the default ORDER BY logic from <code>com.liferay.commerce.product.model.impl.CPConfigurationListModelImpl</code>.
	 * </p>
	 *
	 * @param groupId the group ID
	 * @param companyId the company ID
	 * @param status the status
	 * @param start the lower bound of the range of cp configuration lists
	 * @param end the upper bound of the range of cp configuration lists (not inclusive)
	 * @return the range of matching cp configuration lists
	 */
	public default java.util.List<CPConfigurationList> findByG_C_S(
		long groupId, long companyId, int status, int start, int end) {

		return findByG_C_S(groupId, companyId, status, start, end, null, true);
	}

	/**
	 * Returns an ordered range of all the cp configuration lists where groupId = &#63; and companyId = &#63; and status = &#63;.
	 *
	 * <p>
	 * Useful when paginating results. Returns a maximum of <code>end - start</code> instances. <code>start</code> and <code>end</code> are not primary keys, they are indexes in the result set. Thus, <code>0</code> refers to the first result in the set. Setting both <code>start</code> and <code>end</code> to <code>com.liferay.portal.kernel.dao.orm.QueryUtil#ALL_POS</code> will return the full result set. If <code>orderByComparator</code> is specified, then the query will include the given ORDER BY logic. If <code>orderByComparator</code> is absent, then the query will include the default ORDER BY logic from <code>com.liferay.commerce.product.model.impl.CPConfigurationListModelImpl</code>.
	 * </p>
	 *
	 * @param groupId the group ID
	 * @param companyId the company ID
	 * @param status the status
	 * @param start the lower bound of the range of cp configuration lists
	 * @param end the upper bound of the range of cp configuration lists (not inclusive)
	 * @param orderByComparator the comparator to order the results by (optionally <code>null</code>)
	 * @return the ordered range of matching cp configuration lists
	 */
	public default java.util.List<CPConfigurationList> findByG_C_S(
		long groupId, long companyId, int status, int start, int end,
		com.liferay.portal.kernel.util.OrderByComparator<CPConfigurationList>
			orderByComparator) {

		return findByG_C_S(
			groupId, companyId, status, start, end, orderByComparator, true);
	}

	/**
	 * Returns all the cp configuration lists where groupId = any &#63; and companyId = &#63; and status = &#63;.
	 *
	 * @param groupIds the group IDs
	 * @param companyId the company ID
	 * @param status the status
	 * @return the matching cp configuration lists
	 */
	public default java.util.List<CPConfigurationList> findByG_C_S(
		long[] groupIds, long companyId, int status) {

		return findByG_C_S(
			groupIds, companyId, status,
			com.liferay.portal.kernel.dao.orm.QueryUtil.ALL_POS,
			com.liferay.portal.kernel.dao.orm.QueryUtil.ALL_POS, null, true);
	}

	/**
	 * Returns a range of all the cp configuration lists where groupId = any &#63; and companyId = &#63; and status = &#63;.
	 *
	 * <p>
	 * Useful when paginating results. Returns a maximum of <code>end - start</code> instances. <code>start</code> and <code>end</code> are not primary keys, they are indexes in the result set. Thus, <code>0</code> refers to the first result in the set. Setting both <code>start</code> and <code>end</code> to <code>com.liferay.portal.kernel.dao.orm.QueryUtil#ALL_POS</code> will return the full result set. If <code>orderByComparator</code> is specified, then the query will include the given ORDER BY logic. If <code>orderByComparator</code> is absent, then the query will include the default ORDER BY logic from <code>com.liferay.commerce.product.model.impl.CPConfigurationListModelImpl</code>.
	 * </p>
	 *
	 * @param groupIds the group IDs
	 * @param companyId the company ID
	 * @param status the status
	 * @param start the lower bound of the range of cp configuration lists
	 * @param end the upper bound of the range of cp configuration lists (not inclusive)
	 * @return the range of matching cp configuration lists
	 */
	public default java.util.List<CPConfigurationList> findByG_C_S(
		long[] groupIds, long companyId, int status, int start, int end) {

		return findByG_C_S(groupIds, companyId, status, start, end, null, true);
	}

	/**
	 * Returns an ordered range of all the cp configuration lists where groupId = any &#63; and companyId = &#63; and status = &#63;.
	 *
	 * <p>
	 * Useful when paginating results. Returns a maximum of <code>end - start</code> instances. <code>start</code> and <code>end</code> are not primary keys, they are indexes in the result set. Thus, <code>0</code> refers to the first result in the set. Setting both <code>start</code> and <code>end</code> to <code>com.liferay.portal.kernel.dao.orm.QueryUtil#ALL_POS</code> will return the full result set. If <code>orderByComparator</code> is specified, then the query will include the given ORDER BY logic. If <code>orderByComparator</code> is absent, then the query will include the default ORDER BY logic from <code>com.liferay.commerce.product.model.impl.CPConfigurationListModelImpl</code>.
	 * </p>
	 *
	 * @param groupIds the group IDs
	 * @param companyId the company ID
	 * @param status the status
	 * @param start the lower bound of the range of cp configuration lists
	 * @param end the upper bound of the range of cp configuration lists (not inclusive)
	 * @param orderByComparator the comparator to order the results by (optionally <code>null</code>)
	 * @return the ordered range of matching cp configuration lists
	 */
	public default java.util.List<CPConfigurationList> findByG_C_S(
		long[] groupIds, long companyId, int status, int start, int end,
		com.liferay.portal.kernel.util.OrderByComparator<CPConfigurationList>
			orderByComparator) {

		return findByG_C_S(
			groupIds, companyId, status, start, end, orderByComparator, true);
	}

}
// LIFERAY-SERVICE-BUILDER-HASH:362974326