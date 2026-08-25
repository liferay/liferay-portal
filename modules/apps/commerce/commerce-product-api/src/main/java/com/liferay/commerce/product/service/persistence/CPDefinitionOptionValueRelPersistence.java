/**
 * SPDX-FileCopyrightText: (c) 2000 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

package com.liferay.commerce.product.service.persistence;

import com.liferay.commerce.product.exception.NoSuchCPDefinitionOptionValueRelException;
import com.liferay.commerce.product.model.CPDefinitionOptionValueRel;
import com.liferay.portal.kernel.service.persistence.BasePersistence;
import com.liferay.portal.kernel.service.persistence.change.tracking.CTPersistence;

import org.osgi.annotation.versioning.ProviderType;

/**
 * The persistence interface for the cp definition option value rel service.
 *
 * <p>
 * Caching information and settings can be found in <code>portal.properties</code>
 * </p>
 *
 * @author Marco Leo
 * @see CPDefinitionOptionValueRelUtil
 * @generated
 */
@ProviderType
public interface CPDefinitionOptionValueRelPersistence
	extends BasePersistence<CPDefinitionOptionValueRel>,
			CTPersistence<CPDefinitionOptionValueRel> {

	/*
	 * NOTE FOR DEVELOPERS:
	 *
	 * Never modify or reference this interface directly. Always use {@link CPDefinitionOptionValueRelUtil} to access the cp definition option value rel persistence. Modify <code>service.xml</code> and rerun ServiceBuilder to regenerate this interface.
	 */

	/**
	 * Returns an ordered range of all the cp definition option value rels where uuid = &#63;.
	 *
	 * <p>
	 * Useful when paginating results. Returns a maximum of <code>end - start</code> instances. <code>start</code> and <code>end</code> are not primary keys, they are indexes in the result set. Thus, <code>0</code> refers to the first result in the set. Setting both <code>start</code> and <code>end</code> to <code>com.liferay.portal.kernel.dao.orm.QueryUtil#ALL_POS</code> will return the full result set. If <code>orderByComparator</code> is specified, then the query will include the given ORDER BY logic. If <code>orderByComparator</code> is absent, then the query will include the default ORDER BY logic from <code>com.liferay.commerce.product.model.impl.CPDefinitionOptionValueRelModelImpl</code>.
	 * </p>
	 *
	 * @param uuid the uuid
	 * @param start the lower bound of the range of cp definition option value rels
	 * @param end the upper bound of the range of cp definition option value rels (not inclusive)
	 * @param orderByComparator the comparator to order the results by (optionally <code>null</code>)
	 * @param useFinderCache whether to use the finder cache
	 * @return the ordered range of matching cp definition option value rels
	 */
	public java.util.List<CPDefinitionOptionValueRel> findByUuid(
		String uuid, int start, int end,
		com.liferay.portal.kernel.util.OrderByComparator
			<CPDefinitionOptionValueRel> orderByComparator,
		boolean useFinderCache);

	/**
	 * Returns the first cp definition option value rel in the ordered set where uuid = &#63;.
	 *
	 * @param uuid the uuid
	 * @param orderByComparator the comparator to order the set by (optionally <code>null</code>)
	 * @return the first matching cp definition option value rel
	 * @throws NoSuchCPDefinitionOptionValueRelException if a matching cp definition option value rel could not be found
	 */
	public CPDefinitionOptionValueRel findByUuid_First(
			String uuid,
			com.liferay.portal.kernel.util.OrderByComparator
				<CPDefinitionOptionValueRel> orderByComparator)
		throws NoSuchCPDefinitionOptionValueRelException;

	/**
	 * Returns the first cp definition option value rel in the ordered set where uuid = &#63;.
	 *
	 * @param uuid the uuid
	 * @param orderByComparator the comparator to order the set by (optionally <code>null</code>)
	 * @return the first matching cp definition option value rel, or <code>null</code> if a matching cp definition option value rel could not be found
	 */
	public CPDefinitionOptionValueRel fetchByUuid_First(
		String uuid,
		com.liferay.portal.kernel.util.OrderByComparator
			<CPDefinitionOptionValueRel> orderByComparator);

	/**
	 * Removes all the cp definition option value rels where uuid = &#63; from the database.
	 *
	 * @param uuid the uuid
	 */
	public void removeByUuid(String uuid);

	/**
	 * Returns the number of cp definition option value rels where uuid = &#63;.
	 *
	 * @param uuid the uuid
	 * @return the number of matching cp definition option value rels
	 */
	public int countByUuid(String uuid);

	/**
	 * Returns the cp definition option value rel where uuid = &#63; and groupId = &#63; or throws a <code>NoSuchCPDefinitionOptionValueRelException</code> if it could not be found.
	 *
	 * @param uuid the uuid
	 * @param groupId the group ID
	 * @return the matching cp definition option value rel
	 * @throws NoSuchCPDefinitionOptionValueRelException if a matching cp definition option value rel could not be found
	 */
	public CPDefinitionOptionValueRel findByUUID_G(String uuid, long groupId)
		throws NoSuchCPDefinitionOptionValueRelException;

	/**
	 * Returns the cp definition option value rel where uuid = &#63; and groupId = &#63; or returns <code>null</code> if it could not be found, optionally using the finder cache.
	 *
	 * @param uuid the uuid
	 * @param groupId the group ID
	 * @param useFinderCache whether to use the finder cache
	 * @return the matching cp definition option value rel, or <code>null</code> if a matching cp definition option value rel could not be found
	 */
	public CPDefinitionOptionValueRel fetchByUUID_G(
		String uuid, long groupId, boolean useFinderCache);

	/**
	 * Removes the cp definition option value rel where uuid = &#63; and groupId = &#63; from the database.
	 *
	 * @param uuid the uuid
	 * @param groupId the group ID
	 * @return the cp definition option value rel that was removed
	 */
	public CPDefinitionOptionValueRel removeByUUID_G(String uuid, long groupId)
		throws NoSuchCPDefinitionOptionValueRelException;

	/**
	 * Returns the number of cp definition option value rels where uuid = &#63; and groupId = &#63;.
	 *
	 * @param uuid the uuid
	 * @param groupId the group ID
	 * @return the number of matching cp definition option value rels
	 */
	public int countByUUID_G(String uuid, long groupId);

	/**
	 * Returns an ordered range of all the cp definition option value rels where uuid = &#63; and companyId = &#63;.
	 *
	 * <p>
	 * Useful when paginating results. Returns a maximum of <code>end - start</code> instances. <code>start</code> and <code>end</code> are not primary keys, they are indexes in the result set. Thus, <code>0</code> refers to the first result in the set. Setting both <code>start</code> and <code>end</code> to <code>com.liferay.portal.kernel.dao.orm.QueryUtil#ALL_POS</code> will return the full result set. If <code>orderByComparator</code> is specified, then the query will include the given ORDER BY logic. If <code>orderByComparator</code> is absent, then the query will include the default ORDER BY logic from <code>com.liferay.commerce.product.model.impl.CPDefinitionOptionValueRelModelImpl</code>.
	 * </p>
	 *
	 * @param uuid the uuid
	 * @param companyId the company ID
	 * @param start the lower bound of the range of cp definition option value rels
	 * @param end the upper bound of the range of cp definition option value rels (not inclusive)
	 * @param orderByComparator the comparator to order the results by (optionally <code>null</code>)
	 * @param useFinderCache whether to use the finder cache
	 * @return the ordered range of matching cp definition option value rels
	 */
	public java.util.List<CPDefinitionOptionValueRel> findByUuid_C(
		String uuid, long companyId, int start, int end,
		com.liferay.portal.kernel.util.OrderByComparator
			<CPDefinitionOptionValueRel> orderByComparator,
		boolean useFinderCache);

	/**
	 * Returns the first cp definition option value rel in the ordered set where uuid = &#63; and companyId = &#63;.
	 *
	 * @param uuid the uuid
	 * @param companyId the company ID
	 * @param orderByComparator the comparator to order the set by (optionally <code>null</code>)
	 * @return the first matching cp definition option value rel
	 * @throws NoSuchCPDefinitionOptionValueRelException if a matching cp definition option value rel could not be found
	 */
	public CPDefinitionOptionValueRel findByUuid_C_First(
			String uuid, long companyId,
			com.liferay.portal.kernel.util.OrderByComparator
				<CPDefinitionOptionValueRel> orderByComparator)
		throws NoSuchCPDefinitionOptionValueRelException;

	/**
	 * Returns the first cp definition option value rel in the ordered set where uuid = &#63; and companyId = &#63;.
	 *
	 * @param uuid the uuid
	 * @param companyId the company ID
	 * @param orderByComparator the comparator to order the set by (optionally <code>null</code>)
	 * @return the first matching cp definition option value rel, or <code>null</code> if a matching cp definition option value rel could not be found
	 */
	public CPDefinitionOptionValueRel fetchByUuid_C_First(
		String uuid, long companyId,
		com.liferay.portal.kernel.util.OrderByComparator
			<CPDefinitionOptionValueRel> orderByComparator);

	/**
	 * Removes all the cp definition option value rels where uuid = &#63; and companyId = &#63; from the database.
	 *
	 * @param uuid the uuid
	 * @param companyId the company ID
	 */
	public void removeByUuid_C(String uuid, long companyId);

	/**
	 * Returns the number of cp definition option value rels where uuid = &#63; and companyId = &#63;.
	 *
	 * @param uuid the uuid
	 * @param companyId the company ID
	 * @return the number of matching cp definition option value rels
	 */
	public int countByUuid_C(String uuid, long companyId);

	/**
	 * Returns an ordered range of all the cp definition option value rels where groupId = &#63;.
	 *
	 * <p>
	 * Useful when paginating results. Returns a maximum of <code>end - start</code> instances. <code>start</code> and <code>end</code> are not primary keys, they are indexes in the result set. Thus, <code>0</code> refers to the first result in the set. Setting both <code>start</code> and <code>end</code> to <code>com.liferay.portal.kernel.dao.orm.QueryUtil#ALL_POS</code> will return the full result set. If <code>orderByComparator</code> is specified, then the query will include the given ORDER BY logic. If <code>orderByComparator</code> is absent, then the query will include the default ORDER BY logic from <code>com.liferay.commerce.product.model.impl.CPDefinitionOptionValueRelModelImpl</code>.
	 * </p>
	 *
	 * @param groupId the group ID
	 * @param start the lower bound of the range of cp definition option value rels
	 * @param end the upper bound of the range of cp definition option value rels (not inclusive)
	 * @param orderByComparator the comparator to order the results by (optionally <code>null</code>)
	 * @param useFinderCache whether to use the finder cache
	 * @return the ordered range of matching cp definition option value rels
	 */
	public java.util.List<CPDefinitionOptionValueRel> findByGroupId(
		long groupId, int start, int end,
		com.liferay.portal.kernel.util.OrderByComparator
			<CPDefinitionOptionValueRel> orderByComparator,
		boolean useFinderCache);

	/**
	 * Returns the first cp definition option value rel in the ordered set where groupId = &#63;.
	 *
	 * @param groupId the group ID
	 * @param orderByComparator the comparator to order the set by (optionally <code>null</code>)
	 * @return the first matching cp definition option value rel
	 * @throws NoSuchCPDefinitionOptionValueRelException if a matching cp definition option value rel could not be found
	 */
	public CPDefinitionOptionValueRel findByGroupId_First(
			long groupId,
			com.liferay.portal.kernel.util.OrderByComparator
				<CPDefinitionOptionValueRel> orderByComparator)
		throws NoSuchCPDefinitionOptionValueRelException;

	/**
	 * Returns the first cp definition option value rel in the ordered set where groupId = &#63;.
	 *
	 * @param groupId the group ID
	 * @param orderByComparator the comparator to order the set by (optionally <code>null</code>)
	 * @return the first matching cp definition option value rel, or <code>null</code> if a matching cp definition option value rel could not be found
	 */
	public CPDefinitionOptionValueRel fetchByGroupId_First(
		long groupId,
		com.liferay.portal.kernel.util.OrderByComparator
			<CPDefinitionOptionValueRel> orderByComparator);

	/**
	 * Removes all the cp definition option value rels where groupId = &#63; from the database.
	 *
	 * @param groupId the group ID
	 */
	public void removeByGroupId(long groupId);

	/**
	 * Returns the number of cp definition option value rels where groupId = &#63;.
	 *
	 * @param groupId the group ID
	 * @return the number of matching cp definition option value rels
	 */
	public int countByGroupId(long groupId);

	/**
	 * Returns an ordered range of all the cp definition option value rels where companyId = &#63;.
	 *
	 * <p>
	 * Useful when paginating results. Returns a maximum of <code>end - start</code> instances. <code>start</code> and <code>end</code> are not primary keys, they are indexes in the result set. Thus, <code>0</code> refers to the first result in the set. Setting both <code>start</code> and <code>end</code> to <code>com.liferay.portal.kernel.dao.orm.QueryUtil#ALL_POS</code> will return the full result set. If <code>orderByComparator</code> is specified, then the query will include the given ORDER BY logic. If <code>orderByComparator</code> is absent, then the query will include the default ORDER BY logic from <code>com.liferay.commerce.product.model.impl.CPDefinitionOptionValueRelModelImpl</code>.
	 * </p>
	 *
	 * @param companyId the company ID
	 * @param start the lower bound of the range of cp definition option value rels
	 * @param end the upper bound of the range of cp definition option value rels (not inclusive)
	 * @param orderByComparator the comparator to order the results by (optionally <code>null</code>)
	 * @param useFinderCache whether to use the finder cache
	 * @return the ordered range of matching cp definition option value rels
	 */
	public java.util.List<CPDefinitionOptionValueRel> findByCompanyId(
		long companyId, int start, int end,
		com.liferay.portal.kernel.util.OrderByComparator
			<CPDefinitionOptionValueRel> orderByComparator,
		boolean useFinderCache);

	/**
	 * Returns the first cp definition option value rel in the ordered set where companyId = &#63;.
	 *
	 * @param companyId the company ID
	 * @param orderByComparator the comparator to order the set by (optionally <code>null</code>)
	 * @return the first matching cp definition option value rel
	 * @throws NoSuchCPDefinitionOptionValueRelException if a matching cp definition option value rel could not be found
	 */
	public CPDefinitionOptionValueRel findByCompanyId_First(
			long companyId,
			com.liferay.portal.kernel.util.OrderByComparator
				<CPDefinitionOptionValueRel> orderByComparator)
		throws NoSuchCPDefinitionOptionValueRelException;

	/**
	 * Returns the first cp definition option value rel in the ordered set where companyId = &#63;.
	 *
	 * @param companyId the company ID
	 * @param orderByComparator the comparator to order the set by (optionally <code>null</code>)
	 * @return the first matching cp definition option value rel, or <code>null</code> if a matching cp definition option value rel could not be found
	 */
	public CPDefinitionOptionValueRel fetchByCompanyId_First(
		long companyId,
		com.liferay.portal.kernel.util.OrderByComparator
			<CPDefinitionOptionValueRel> orderByComparator);

	/**
	 * Removes all the cp definition option value rels where companyId = &#63; from the database.
	 *
	 * @param companyId the company ID
	 */
	public void removeByCompanyId(long companyId);

	/**
	 * Returns the number of cp definition option value rels where companyId = &#63;.
	 *
	 * @param companyId the company ID
	 * @return the number of matching cp definition option value rels
	 */
	public int countByCompanyId(long companyId);

	/**
	 * Returns an ordered range of all the cp definition option value rels where CPDefinitionOptionRelId = &#63;.
	 *
	 * <p>
	 * Useful when paginating results. Returns a maximum of <code>end - start</code> instances. <code>start</code> and <code>end</code> are not primary keys, they are indexes in the result set. Thus, <code>0</code> refers to the first result in the set. Setting both <code>start</code> and <code>end</code> to <code>com.liferay.portal.kernel.dao.orm.QueryUtil#ALL_POS</code> will return the full result set. If <code>orderByComparator</code> is specified, then the query will include the given ORDER BY logic. If <code>orderByComparator</code> is absent, then the query will include the default ORDER BY logic from <code>com.liferay.commerce.product.model.impl.CPDefinitionOptionValueRelModelImpl</code>.
	 * </p>
	 *
	 * @param CPDefinitionOptionRelId the cp definition option rel ID
	 * @param start the lower bound of the range of cp definition option value rels
	 * @param end the upper bound of the range of cp definition option value rels (not inclusive)
	 * @param orderByComparator the comparator to order the results by (optionally <code>null</code>)
	 * @param useFinderCache whether to use the finder cache
	 * @return the ordered range of matching cp definition option value rels
	 */
	public java.util.List<CPDefinitionOptionValueRel>
		findByCPDefinitionOptionRelId(
			long CPDefinitionOptionRelId, int start, int end,
			com.liferay.portal.kernel.util.OrderByComparator
				<CPDefinitionOptionValueRel> orderByComparator,
			boolean useFinderCache);

	/**
	 * Returns the first cp definition option value rel in the ordered set where CPDefinitionOptionRelId = &#63;.
	 *
	 * @param CPDefinitionOptionRelId the cp definition option rel ID
	 * @param orderByComparator the comparator to order the set by (optionally <code>null</code>)
	 * @return the first matching cp definition option value rel
	 * @throws NoSuchCPDefinitionOptionValueRelException if a matching cp definition option value rel could not be found
	 */
	public CPDefinitionOptionValueRel findByCPDefinitionOptionRelId_First(
			long CPDefinitionOptionRelId,
			com.liferay.portal.kernel.util.OrderByComparator
				<CPDefinitionOptionValueRel> orderByComparator)
		throws NoSuchCPDefinitionOptionValueRelException;

	/**
	 * Returns the first cp definition option value rel in the ordered set where CPDefinitionOptionRelId = &#63;.
	 *
	 * @param CPDefinitionOptionRelId the cp definition option rel ID
	 * @param orderByComparator the comparator to order the set by (optionally <code>null</code>)
	 * @return the first matching cp definition option value rel, or <code>null</code> if a matching cp definition option value rel could not be found
	 */
	public CPDefinitionOptionValueRel fetchByCPDefinitionOptionRelId_First(
		long CPDefinitionOptionRelId,
		com.liferay.portal.kernel.util.OrderByComparator
			<CPDefinitionOptionValueRel> orderByComparator);

	/**
	 * Removes all the cp definition option value rels where CPDefinitionOptionRelId = &#63; from the database.
	 *
	 * @param CPDefinitionOptionRelId the cp definition option rel ID
	 */
	public void removeByCPDefinitionOptionRelId(long CPDefinitionOptionRelId);

	/**
	 * Returns the number of cp definition option value rels where CPDefinitionOptionRelId = &#63;.
	 *
	 * @param CPDefinitionOptionRelId the cp definition option rel ID
	 * @return the number of matching cp definition option value rels
	 */
	public int countByCPDefinitionOptionRelId(long CPDefinitionOptionRelId);

	/**
	 * Returns an ordered range of all the cp definition option value rels where CPInstanceUuid = &#63;.
	 *
	 * <p>
	 * Useful when paginating results. Returns a maximum of <code>end - start</code> instances. <code>start</code> and <code>end</code> are not primary keys, they are indexes in the result set. Thus, <code>0</code> refers to the first result in the set. Setting both <code>start</code> and <code>end</code> to <code>com.liferay.portal.kernel.dao.orm.QueryUtil#ALL_POS</code> will return the full result set. If <code>orderByComparator</code> is specified, then the query will include the given ORDER BY logic. If <code>orderByComparator</code> is absent, then the query will include the default ORDER BY logic from <code>com.liferay.commerce.product.model.impl.CPDefinitionOptionValueRelModelImpl</code>.
	 * </p>
	 *
	 * @param CPInstanceUuid the cp instance uuid
	 * @param start the lower bound of the range of cp definition option value rels
	 * @param end the upper bound of the range of cp definition option value rels (not inclusive)
	 * @param orderByComparator the comparator to order the results by (optionally <code>null</code>)
	 * @param useFinderCache whether to use the finder cache
	 * @return the ordered range of matching cp definition option value rels
	 */
	public java.util.List<CPDefinitionOptionValueRel> findByCPInstanceUuid(
		String CPInstanceUuid, int start, int end,
		com.liferay.portal.kernel.util.OrderByComparator
			<CPDefinitionOptionValueRel> orderByComparator,
		boolean useFinderCache);

	/**
	 * Returns the first cp definition option value rel in the ordered set where CPInstanceUuid = &#63;.
	 *
	 * @param CPInstanceUuid the cp instance uuid
	 * @param orderByComparator the comparator to order the set by (optionally <code>null</code>)
	 * @return the first matching cp definition option value rel
	 * @throws NoSuchCPDefinitionOptionValueRelException if a matching cp definition option value rel could not be found
	 */
	public CPDefinitionOptionValueRel findByCPInstanceUuid_First(
			String CPInstanceUuid,
			com.liferay.portal.kernel.util.OrderByComparator
				<CPDefinitionOptionValueRel> orderByComparator)
		throws NoSuchCPDefinitionOptionValueRelException;

	/**
	 * Returns the first cp definition option value rel in the ordered set where CPInstanceUuid = &#63;.
	 *
	 * @param CPInstanceUuid the cp instance uuid
	 * @param orderByComparator the comparator to order the set by (optionally <code>null</code>)
	 * @return the first matching cp definition option value rel, or <code>null</code> if a matching cp definition option value rel could not be found
	 */
	public CPDefinitionOptionValueRel fetchByCPInstanceUuid_First(
		String CPInstanceUuid,
		com.liferay.portal.kernel.util.OrderByComparator
			<CPDefinitionOptionValueRel> orderByComparator);

	/**
	 * Removes all the cp definition option value rels where CPInstanceUuid = &#63; from the database.
	 *
	 * @param CPInstanceUuid the cp instance uuid
	 */
	public void removeByCPInstanceUuid(String CPInstanceUuid);

	/**
	 * Returns the number of cp definition option value rels where CPInstanceUuid = &#63;.
	 *
	 * @param CPInstanceUuid the cp instance uuid
	 * @return the number of matching cp definition option value rels
	 */
	public int countByCPInstanceUuid(String CPInstanceUuid);

	/**
	 * Returns an ordered range of all the cp definition option value rels where key = &#63;.
	 *
	 * <p>
	 * Useful when paginating results. Returns a maximum of <code>end - start</code> instances. <code>start</code> and <code>end</code> are not primary keys, they are indexes in the result set. Thus, <code>0</code> refers to the first result in the set. Setting both <code>start</code> and <code>end</code> to <code>com.liferay.portal.kernel.dao.orm.QueryUtil#ALL_POS</code> will return the full result set. If <code>orderByComparator</code> is specified, then the query will include the given ORDER BY logic. If <code>orderByComparator</code> is absent, then the query will include the default ORDER BY logic from <code>com.liferay.commerce.product.model.impl.CPDefinitionOptionValueRelModelImpl</code>.
	 * </p>
	 *
	 * @param key the key
	 * @param start the lower bound of the range of cp definition option value rels
	 * @param end the upper bound of the range of cp definition option value rels (not inclusive)
	 * @param orderByComparator the comparator to order the results by (optionally <code>null</code>)
	 * @param useFinderCache whether to use the finder cache
	 * @return the ordered range of matching cp definition option value rels
	 */
	public java.util.List<CPDefinitionOptionValueRel> findByKey(
		String key, int start, int end,
		com.liferay.portal.kernel.util.OrderByComparator
			<CPDefinitionOptionValueRel> orderByComparator,
		boolean useFinderCache);

	/**
	 * Returns the first cp definition option value rel in the ordered set where key = &#63;.
	 *
	 * @param key the key
	 * @param orderByComparator the comparator to order the set by (optionally <code>null</code>)
	 * @return the first matching cp definition option value rel
	 * @throws NoSuchCPDefinitionOptionValueRelException if a matching cp definition option value rel could not be found
	 */
	public CPDefinitionOptionValueRel findByKey_First(
			String key,
			com.liferay.portal.kernel.util.OrderByComparator
				<CPDefinitionOptionValueRel> orderByComparator)
		throws NoSuchCPDefinitionOptionValueRelException;

	/**
	 * Returns the first cp definition option value rel in the ordered set where key = &#63;.
	 *
	 * @param key the key
	 * @param orderByComparator the comparator to order the set by (optionally <code>null</code>)
	 * @return the first matching cp definition option value rel, or <code>null</code> if a matching cp definition option value rel could not be found
	 */
	public CPDefinitionOptionValueRel fetchByKey_First(
		String key,
		com.liferay.portal.kernel.util.OrderByComparator
			<CPDefinitionOptionValueRel> orderByComparator);

	/**
	 * Removes all the cp definition option value rels where key = &#63; from the database.
	 *
	 * @param key the key
	 */
	public void removeByKey(String key);

	/**
	 * Returns the number of cp definition option value rels where key = &#63;.
	 *
	 * @param key the key
	 * @return the number of matching cp definition option value rels
	 */
	public int countByKey(String key);

	/**
	 * Returns the cp definition option value rel where CPDefinitionOptionRelId = &#63; and key = &#63; or throws a <code>NoSuchCPDefinitionOptionValueRelException</code> if it could not be found.
	 *
	 * @param CPDefinitionOptionRelId the cp definition option rel ID
	 * @param key the key
	 * @return the matching cp definition option value rel
	 * @throws NoSuchCPDefinitionOptionValueRelException if a matching cp definition option value rel could not be found
	 */
	public CPDefinitionOptionValueRel findByC_K(
			long CPDefinitionOptionRelId, String key)
		throws NoSuchCPDefinitionOptionValueRelException;

	/**
	 * Returns the cp definition option value rel where CPDefinitionOptionRelId = &#63; and key = &#63; or returns <code>null</code> if it could not be found, optionally using the finder cache.
	 *
	 * @param CPDefinitionOptionRelId the cp definition option rel ID
	 * @param key the key
	 * @param useFinderCache whether to use the finder cache
	 * @return the matching cp definition option value rel, or <code>null</code> if a matching cp definition option value rel could not be found
	 */
	public CPDefinitionOptionValueRel fetchByC_K(
		long CPDefinitionOptionRelId, String key, boolean useFinderCache);

	/**
	 * Removes the cp definition option value rel where CPDefinitionOptionRelId = &#63; and key = &#63; from the database.
	 *
	 * @param CPDefinitionOptionRelId the cp definition option rel ID
	 * @param key the key
	 * @return the cp definition option value rel that was removed
	 */
	public CPDefinitionOptionValueRel removeByC_K(
			long CPDefinitionOptionRelId, String key)
		throws NoSuchCPDefinitionOptionValueRelException;

	/**
	 * Returns the number of cp definition option value rels where CPDefinitionOptionRelId = &#63; and key = &#63;.
	 *
	 * @param CPDefinitionOptionRelId the cp definition option rel ID
	 * @param key the key
	 * @return the number of matching cp definition option value rels
	 */
	public int countByC_K(long CPDefinitionOptionRelId, String key);

	/**
	 * Returns an ordered range of all the cp definition option value rels where CPDefinitionOptionRelId = &#63; and preselected = &#63;.
	 *
	 * <p>
	 * Useful when paginating results. Returns a maximum of <code>end - start</code> instances. <code>start</code> and <code>end</code> are not primary keys, they are indexes in the result set. Thus, <code>0</code> refers to the first result in the set. Setting both <code>start</code> and <code>end</code> to <code>com.liferay.portal.kernel.dao.orm.QueryUtil#ALL_POS</code> will return the full result set. If <code>orderByComparator</code> is specified, then the query will include the given ORDER BY logic. If <code>orderByComparator</code> is absent, then the query will include the default ORDER BY logic from <code>com.liferay.commerce.product.model.impl.CPDefinitionOptionValueRelModelImpl</code>.
	 * </p>
	 *
	 * @param CPDefinitionOptionRelId the cp definition option rel ID
	 * @param preselected the preselected
	 * @param start the lower bound of the range of cp definition option value rels
	 * @param end the upper bound of the range of cp definition option value rels (not inclusive)
	 * @param orderByComparator the comparator to order the results by (optionally <code>null</code>)
	 * @param useFinderCache whether to use the finder cache
	 * @return the ordered range of matching cp definition option value rels
	 */
	public java.util.List<CPDefinitionOptionValueRel> findByCDORI_P(
		long CPDefinitionOptionRelId, boolean preselected, int start, int end,
		com.liferay.portal.kernel.util.OrderByComparator
			<CPDefinitionOptionValueRel> orderByComparator,
		boolean useFinderCache);

	/**
	 * Returns the first cp definition option value rel in the ordered set where CPDefinitionOptionRelId = &#63; and preselected = &#63;.
	 *
	 * @param CPDefinitionOptionRelId the cp definition option rel ID
	 * @param preselected the preselected
	 * @param orderByComparator the comparator to order the set by (optionally <code>null</code>)
	 * @return the first matching cp definition option value rel
	 * @throws NoSuchCPDefinitionOptionValueRelException if a matching cp definition option value rel could not be found
	 */
	public CPDefinitionOptionValueRel findByCDORI_P_First(
			long CPDefinitionOptionRelId, boolean preselected,
			com.liferay.portal.kernel.util.OrderByComparator
				<CPDefinitionOptionValueRel> orderByComparator)
		throws NoSuchCPDefinitionOptionValueRelException;

	/**
	 * Returns the first cp definition option value rel in the ordered set where CPDefinitionOptionRelId = &#63; and preselected = &#63;.
	 *
	 * @param CPDefinitionOptionRelId the cp definition option rel ID
	 * @param preselected the preselected
	 * @param orderByComparator the comparator to order the set by (optionally <code>null</code>)
	 * @return the first matching cp definition option value rel, or <code>null</code> if a matching cp definition option value rel could not be found
	 */
	public CPDefinitionOptionValueRel fetchByCDORI_P_First(
		long CPDefinitionOptionRelId, boolean preselected,
		com.liferay.portal.kernel.util.OrderByComparator
			<CPDefinitionOptionValueRel> orderByComparator);

	/**
	 * Removes all the cp definition option value rels where CPDefinitionOptionRelId = &#63; and preselected = &#63; from the database.
	 *
	 * @param CPDefinitionOptionRelId the cp definition option rel ID
	 * @param preselected the preselected
	 */
	public void removeByCDORI_P(
		long CPDefinitionOptionRelId, boolean preselected);

	/**
	 * Returns the number of cp definition option value rels where CPDefinitionOptionRelId = &#63; and preselected = &#63;.
	 *
	 * @param CPDefinitionOptionRelId the cp definition option rel ID
	 * @param preselected the preselected
	 * @return the number of matching cp definition option value rels
	 */
	public int countByCDORI_P(
		long CPDefinitionOptionRelId, boolean preselected);

	/**
	 * Returns the cp definition option value rel where externalReferenceCode = &#63; and companyId = &#63; or throws a <code>NoSuchCPDefinitionOptionValueRelException</code> if it could not be found.
	 *
	 * @param externalReferenceCode the external reference code
	 * @param companyId the company ID
	 * @return the matching cp definition option value rel
	 * @throws NoSuchCPDefinitionOptionValueRelException if a matching cp definition option value rel could not be found
	 */
	public CPDefinitionOptionValueRel findByERC_C(
			String externalReferenceCode, long companyId)
		throws NoSuchCPDefinitionOptionValueRelException;

	/**
	 * Returns the cp definition option value rel where externalReferenceCode = &#63; and companyId = &#63; or returns <code>null</code> if it could not be found, optionally using the finder cache.
	 *
	 * @param externalReferenceCode the external reference code
	 * @param companyId the company ID
	 * @param useFinderCache whether to use the finder cache
	 * @return the matching cp definition option value rel, or <code>null</code> if a matching cp definition option value rel could not be found
	 */
	public CPDefinitionOptionValueRel fetchByERC_C(
		String externalReferenceCode, long companyId, boolean useFinderCache);

	/**
	 * Removes the cp definition option value rel where externalReferenceCode = &#63; and companyId = &#63; from the database.
	 *
	 * @param externalReferenceCode the external reference code
	 * @param companyId the company ID
	 * @return the cp definition option value rel that was removed
	 */
	public CPDefinitionOptionValueRel removeByERC_C(
			String externalReferenceCode, long companyId)
		throws NoSuchCPDefinitionOptionValueRelException;

	/**
	 * Returns the number of cp definition option value rels where externalReferenceCode = &#63; and companyId = &#63;.
	 *
	 * @param externalReferenceCode the external reference code
	 * @param companyId the company ID
	 * @return the number of matching cp definition option value rels
	 */
	public int countByERC_C(String externalReferenceCode, long companyId);

	/**
	 * Creates a new cp definition option value rel with the primary key. Does not add the cp definition option value rel to the database.
	 *
	 * @param CPDefinitionOptionValueRelId the primary key for the new cp definition option value rel
	 * @return the new cp definition option value rel
	 */
	public CPDefinitionOptionValueRel create(long CPDefinitionOptionValueRelId);

	/**
	 * Removes the cp definition option value rel with the primary key from the database. Also notifies the appropriate model listeners.
	 *
	 * @param CPDefinitionOptionValueRelId the primary key of the cp definition option value rel
	 * @return the cp definition option value rel that was removed
	 * @throws NoSuchCPDefinitionOptionValueRelException if a cp definition option value rel with the primary key could not be found
	 */
	public CPDefinitionOptionValueRel remove(long CPDefinitionOptionValueRelId)
		throws NoSuchCPDefinitionOptionValueRelException;

	public CPDefinitionOptionValueRel updateImpl(
		CPDefinitionOptionValueRel cpDefinitionOptionValueRel);

	/**
	 * Returns the cp definition option value rel with the primary key or throws a <code>NoSuchCPDefinitionOptionValueRelException</code> if it could not be found.
	 *
	 * @param CPDefinitionOptionValueRelId the primary key of the cp definition option value rel
	 * @return the cp definition option value rel
	 * @throws NoSuchCPDefinitionOptionValueRelException if a cp definition option value rel with the primary key could not be found
	 */
	public CPDefinitionOptionValueRel findByPrimaryKey(
			long CPDefinitionOptionValueRelId)
		throws NoSuchCPDefinitionOptionValueRelException;

	/**
	 * Returns the cp definition option value rel with the primary key or returns <code>null</code> if it could not be found.
	 *
	 * @param CPDefinitionOptionValueRelId the primary key of the cp definition option value rel
	 * @return the cp definition option value rel, or <code>null</code> if a cp definition option value rel with the primary key could not be found
	 */
	public CPDefinitionOptionValueRel fetchByPrimaryKey(
		long CPDefinitionOptionValueRelId);

	/**
	 * Returns the cp definition option value rel where uuid = &#63; and groupId = &#63; or returns <code>null</code> if it could not be found. Uses the finder cache.
	 *
	 * @param uuid the uuid
	 * @param groupId the group ID
	 * @return the matching cp definition option value rel, or <code>null</code> if a matching cp definition option value rel could not be found
	 */
	public default CPDefinitionOptionValueRel fetchByUUID_G(
		String uuid, long groupId) {

		return fetchByUUID_G(uuid, groupId, true);
	}

	/**
	 * Returns the cp definition option value rel where CPDefinitionOptionRelId = &#63; and key = &#63; or returns <code>null</code> if it could not be found. Uses the finder cache.
	 *
	 * @param CPDefinitionOptionRelId the cp definition option rel ID
	 * @param key the key
	 * @return the matching cp definition option value rel, or <code>null</code> if a matching cp definition option value rel could not be found
	 */
	public default CPDefinitionOptionValueRel fetchByC_K(
		long CPDefinitionOptionRelId, String key) {

		return fetchByC_K(CPDefinitionOptionRelId, key, true);
	}

	/**
	 * Returns the cp definition option value rel where externalReferenceCode = &#63; and companyId = &#63; or returns <code>null</code> if it could not be found. Uses the finder cache.
	 *
	 * @param externalReferenceCode the external reference code
	 * @param companyId the company ID
	 * @return the matching cp definition option value rel, or <code>null</code> if a matching cp definition option value rel could not be found
	 */
	public default CPDefinitionOptionValueRel fetchByERC_C(
		String externalReferenceCode, long companyId) {

		return fetchByERC_C(externalReferenceCode, companyId, true);
	}

	/**
	 * Returns all the cp definition option value rels where uuid = &#63;.
	 *
	 * @param uuid the uuid
	 * @return the matching cp definition option value rels
	 */
	public default java.util.List<CPDefinitionOptionValueRel> findByUuid(
		String uuid) {

		return findByUuid(
			uuid, com.liferay.portal.kernel.dao.orm.QueryUtil.ALL_POS,
			com.liferay.portal.kernel.dao.orm.QueryUtil.ALL_POS, null, true);
	}

	/**
	 * Returns a range of all the cp definition option value rels where uuid = &#63;.
	 *
	 * <p>
	 * Useful when paginating results. Returns a maximum of <code>end - start</code> instances. <code>start</code> and <code>end</code> are not primary keys, they are indexes in the result set. Thus, <code>0</code> refers to the first result in the set. Setting both <code>start</code> and <code>end</code> to <code>com.liferay.portal.kernel.dao.orm.QueryUtil#ALL_POS</code> will return the full result set. If <code>orderByComparator</code> is specified, then the query will include the given ORDER BY logic. If <code>orderByComparator</code> is absent, then the query will include the default ORDER BY logic from <code>com.liferay.commerce.product.model.impl.CPDefinitionOptionValueRelModelImpl</code>.
	 * </p>
	 *
	 * @param uuid the uuid
	 * @param start the lower bound of the range of cp definition option value rels
	 * @param end the upper bound of the range of cp definition option value rels (not inclusive)
	 * @return the range of matching cp definition option value rels
	 */
	public default java.util.List<CPDefinitionOptionValueRel> findByUuid(
		String uuid, int start, int end) {

		return findByUuid(uuid, start, end, null, true);
	}

	/**
	 * Returns an ordered range of all the cp definition option value rels where uuid = &#63;.
	 *
	 * <p>
	 * Useful when paginating results. Returns a maximum of <code>end - start</code> instances. <code>start</code> and <code>end</code> are not primary keys, they are indexes in the result set. Thus, <code>0</code> refers to the first result in the set. Setting both <code>start</code> and <code>end</code> to <code>com.liferay.portal.kernel.dao.orm.QueryUtil#ALL_POS</code> will return the full result set. If <code>orderByComparator</code> is specified, then the query will include the given ORDER BY logic. If <code>orderByComparator</code> is absent, then the query will include the default ORDER BY logic from <code>com.liferay.commerce.product.model.impl.CPDefinitionOptionValueRelModelImpl</code>.
	 * </p>
	 *
	 * @param uuid the uuid
	 * @param start the lower bound of the range of cp definition option value rels
	 * @param end the upper bound of the range of cp definition option value rels (not inclusive)
	 * @param orderByComparator the comparator to order the results by (optionally <code>null</code>)
	 * @return the ordered range of matching cp definition option value rels
	 */
	public default java.util.List<CPDefinitionOptionValueRel> findByUuid(
		String uuid, int start, int end,
		com.liferay.portal.kernel.util.OrderByComparator
			<CPDefinitionOptionValueRel> orderByComparator) {

		return findByUuid(uuid, start, end, orderByComparator, true);
	}

	/**
	 * Returns all the cp definition option value rels where uuid = &#63; and companyId = &#63;.
	 *
	 * @param uuid the uuid
	 * @param companyId the company ID
	 * @return the matching cp definition option value rels
	 */
	public default java.util.List<CPDefinitionOptionValueRel> findByUuid_C(
		String uuid, long companyId) {

		return findByUuid_C(
			uuid, companyId,
			com.liferay.portal.kernel.dao.orm.QueryUtil.ALL_POS,
			com.liferay.portal.kernel.dao.orm.QueryUtil.ALL_POS, null, true);
	}

	/**
	 * Returns a range of all the cp definition option value rels where uuid = &#63; and companyId = &#63;.
	 *
	 * <p>
	 * Useful when paginating results. Returns a maximum of <code>end - start</code> instances. <code>start</code> and <code>end</code> are not primary keys, they are indexes in the result set. Thus, <code>0</code> refers to the first result in the set. Setting both <code>start</code> and <code>end</code> to <code>com.liferay.portal.kernel.dao.orm.QueryUtil#ALL_POS</code> will return the full result set. If <code>orderByComparator</code> is specified, then the query will include the given ORDER BY logic. If <code>orderByComparator</code> is absent, then the query will include the default ORDER BY logic from <code>com.liferay.commerce.product.model.impl.CPDefinitionOptionValueRelModelImpl</code>.
	 * </p>
	 *
	 * @param uuid the uuid
	 * @param companyId the company ID
	 * @param start the lower bound of the range of cp definition option value rels
	 * @param end the upper bound of the range of cp definition option value rels (not inclusive)
	 * @return the range of matching cp definition option value rels
	 */
	public default java.util.List<CPDefinitionOptionValueRel> findByUuid_C(
		String uuid, long companyId, int start, int end) {

		return findByUuid_C(uuid, companyId, start, end, null, true);
	}

	/**
	 * Returns an ordered range of all the cp definition option value rels where uuid = &#63; and companyId = &#63;.
	 *
	 * <p>
	 * Useful when paginating results. Returns a maximum of <code>end - start</code> instances. <code>start</code> and <code>end</code> are not primary keys, they are indexes in the result set. Thus, <code>0</code> refers to the first result in the set. Setting both <code>start</code> and <code>end</code> to <code>com.liferay.portal.kernel.dao.orm.QueryUtil#ALL_POS</code> will return the full result set. If <code>orderByComparator</code> is specified, then the query will include the given ORDER BY logic. If <code>orderByComparator</code> is absent, then the query will include the default ORDER BY logic from <code>com.liferay.commerce.product.model.impl.CPDefinitionOptionValueRelModelImpl</code>.
	 * </p>
	 *
	 * @param uuid the uuid
	 * @param companyId the company ID
	 * @param start the lower bound of the range of cp definition option value rels
	 * @param end the upper bound of the range of cp definition option value rels (not inclusive)
	 * @param orderByComparator the comparator to order the results by (optionally <code>null</code>)
	 * @return the ordered range of matching cp definition option value rels
	 */
	public default java.util.List<CPDefinitionOptionValueRel> findByUuid_C(
		String uuid, long companyId, int start, int end,
		com.liferay.portal.kernel.util.OrderByComparator
			<CPDefinitionOptionValueRel> orderByComparator) {

		return findByUuid_C(
			uuid, companyId, start, end, orderByComparator, true);
	}

	/**
	 * Returns all the cp definition option value rels where groupId = &#63;.
	 *
	 * @param groupId the group ID
	 * @return the matching cp definition option value rels
	 */
	public default java.util.List<CPDefinitionOptionValueRel> findByGroupId(
		long groupId) {

		return findByGroupId(
			groupId, com.liferay.portal.kernel.dao.orm.QueryUtil.ALL_POS,
			com.liferay.portal.kernel.dao.orm.QueryUtil.ALL_POS, null, true);
	}

	/**
	 * Returns a range of all the cp definition option value rels where groupId = &#63;.
	 *
	 * <p>
	 * Useful when paginating results. Returns a maximum of <code>end - start</code> instances. <code>start</code> and <code>end</code> are not primary keys, they are indexes in the result set. Thus, <code>0</code> refers to the first result in the set. Setting both <code>start</code> and <code>end</code> to <code>com.liferay.portal.kernel.dao.orm.QueryUtil#ALL_POS</code> will return the full result set. If <code>orderByComparator</code> is specified, then the query will include the given ORDER BY logic. If <code>orderByComparator</code> is absent, then the query will include the default ORDER BY logic from <code>com.liferay.commerce.product.model.impl.CPDefinitionOptionValueRelModelImpl</code>.
	 * </p>
	 *
	 * @param groupId the group ID
	 * @param start the lower bound of the range of cp definition option value rels
	 * @param end the upper bound of the range of cp definition option value rels (not inclusive)
	 * @return the range of matching cp definition option value rels
	 */
	public default java.util.List<CPDefinitionOptionValueRel> findByGroupId(
		long groupId, int start, int end) {

		return findByGroupId(groupId, start, end, null, true);
	}

	/**
	 * Returns an ordered range of all the cp definition option value rels where groupId = &#63;.
	 *
	 * <p>
	 * Useful when paginating results. Returns a maximum of <code>end - start</code> instances. <code>start</code> and <code>end</code> are not primary keys, they are indexes in the result set. Thus, <code>0</code> refers to the first result in the set. Setting both <code>start</code> and <code>end</code> to <code>com.liferay.portal.kernel.dao.orm.QueryUtil#ALL_POS</code> will return the full result set. If <code>orderByComparator</code> is specified, then the query will include the given ORDER BY logic. If <code>orderByComparator</code> is absent, then the query will include the default ORDER BY logic from <code>com.liferay.commerce.product.model.impl.CPDefinitionOptionValueRelModelImpl</code>.
	 * </p>
	 *
	 * @param groupId the group ID
	 * @param start the lower bound of the range of cp definition option value rels
	 * @param end the upper bound of the range of cp definition option value rels (not inclusive)
	 * @param orderByComparator the comparator to order the results by (optionally <code>null</code>)
	 * @return the ordered range of matching cp definition option value rels
	 */
	public default java.util.List<CPDefinitionOptionValueRel> findByGroupId(
		long groupId, int start, int end,
		com.liferay.portal.kernel.util.OrderByComparator
			<CPDefinitionOptionValueRel> orderByComparator) {

		return findByGroupId(groupId, start, end, orderByComparator, true);
	}

	/**
	 * Returns all the cp definition option value rels where companyId = &#63;.
	 *
	 * @param companyId the company ID
	 * @return the matching cp definition option value rels
	 */
	public default java.util.List<CPDefinitionOptionValueRel> findByCompanyId(
		long companyId) {

		return findByCompanyId(
			companyId, com.liferay.portal.kernel.dao.orm.QueryUtil.ALL_POS,
			com.liferay.portal.kernel.dao.orm.QueryUtil.ALL_POS, null, true);
	}

	/**
	 * Returns a range of all the cp definition option value rels where companyId = &#63;.
	 *
	 * <p>
	 * Useful when paginating results. Returns a maximum of <code>end - start</code> instances. <code>start</code> and <code>end</code> are not primary keys, they are indexes in the result set. Thus, <code>0</code> refers to the first result in the set. Setting both <code>start</code> and <code>end</code> to <code>com.liferay.portal.kernel.dao.orm.QueryUtil#ALL_POS</code> will return the full result set. If <code>orderByComparator</code> is specified, then the query will include the given ORDER BY logic. If <code>orderByComparator</code> is absent, then the query will include the default ORDER BY logic from <code>com.liferay.commerce.product.model.impl.CPDefinitionOptionValueRelModelImpl</code>.
	 * </p>
	 *
	 * @param companyId the company ID
	 * @param start the lower bound of the range of cp definition option value rels
	 * @param end the upper bound of the range of cp definition option value rels (not inclusive)
	 * @return the range of matching cp definition option value rels
	 */
	public default java.util.List<CPDefinitionOptionValueRel> findByCompanyId(
		long companyId, int start, int end) {

		return findByCompanyId(companyId, start, end, null, true);
	}

	/**
	 * Returns an ordered range of all the cp definition option value rels where companyId = &#63;.
	 *
	 * <p>
	 * Useful when paginating results. Returns a maximum of <code>end - start</code> instances. <code>start</code> and <code>end</code> are not primary keys, they are indexes in the result set. Thus, <code>0</code> refers to the first result in the set. Setting both <code>start</code> and <code>end</code> to <code>com.liferay.portal.kernel.dao.orm.QueryUtil#ALL_POS</code> will return the full result set. If <code>orderByComparator</code> is specified, then the query will include the given ORDER BY logic. If <code>orderByComparator</code> is absent, then the query will include the default ORDER BY logic from <code>com.liferay.commerce.product.model.impl.CPDefinitionOptionValueRelModelImpl</code>.
	 * </p>
	 *
	 * @param companyId the company ID
	 * @param start the lower bound of the range of cp definition option value rels
	 * @param end the upper bound of the range of cp definition option value rels (not inclusive)
	 * @param orderByComparator the comparator to order the results by (optionally <code>null</code>)
	 * @return the ordered range of matching cp definition option value rels
	 */
	public default java.util.List<CPDefinitionOptionValueRel> findByCompanyId(
		long companyId, int start, int end,
		com.liferay.portal.kernel.util.OrderByComparator
			<CPDefinitionOptionValueRel> orderByComparator) {

		return findByCompanyId(companyId, start, end, orderByComparator, true);
	}

	/**
	 * Returns all the cp definition option value rels where CPDefinitionOptionRelId = &#63;.
	 *
	 * @param CPDefinitionOptionRelId the cp definition option rel ID
	 * @return the matching cp definition option value rels
	 */
	public default java.util.List<CPDefinitionOptionValueRel>
		findByCPDefinitionOptionRelId(long CPDefinitionOptionRelId) {

		return findByCPDefinitionOptionRelId(
			CPDefinitionOptionRelId,
			com.liferay.portal.kernel.dao.orm.QueryUtil.ALL_POS,
			com.liferay.portal.kernel.dao.orm.QueryUtil.ALL_POS, null, true);
	}

	/**
	 * Returns a range of all the cp definition option value rels where CPDefinitionOptionRelId = &#63;.
	 *
	 * <p>
	 * Useful when paginating results. Returns a maximum of <code>end - start</code> instances. <code>start</code> and <code>end</code> are not primary keys, they are indexes in the result set. Thus, <code>0</code> refers to the first result in the set. Setting both <code>start</code> and <code>end</code> to <code>com.liferay.portal.kernel.dao.orm.QueryUtil#ALL_POS</code> will return the full result set. If <code>orderByComparator</code> is specified, then the query will include the given ORDER BY logic. If <code>orderByComparator</code> is absent, then the query will include the default ORDER BY logic from <code>com.liferay.commerce.product.model.impl.CPDefinitionOptionValueRelModelImpl</code>.
	 * </p>
	 *
	 * @param CPDefinitionOptionRelId the cp definition option rel ID
	 * @param start the lower bound of the range of cp definition option value rels
	 * @param end the upper bound of the range of cp definition option value rels (not inclusive)
	 * @return the range of matching cp definition option value rels
	 */
	public default java.util.List<CPDefinitionOptionValueRel>
		findByCPDefinitionOptionRelId(
			long CPDefinitionOptionRelId, int start, int end) {

		return findByCPDefinitionOptionRelId(
			CPDefinitionOptionRelId, start, end, null, true);
	}

	/**
	 * Returns an ordered range of all the cp definition option value rels where CPDefinitionOptionRelId = &#63;.
	 *
	 * <p>
	 * Useful when paginating results. Returns a maximum of <code>end - start</code> instances. <code>start</code> and <code>end</code> are not primary keys, they are indexes in the result set. Thus, <code>0</code> refers to the first result in the set. Setting both <code>start</code> and <code>end</code> to <code>com.liferay.portal.kernel.dao.orm.QueryUtil#ALL_POS</code> will return the full result set. If <code>orderByComparator</code> is specified, then the query will include the given ORDER BY logic. If <code>orderByComparator</code> is absent, then the query will include the default ORDER BY logic from <code>com.liferay.commerce.product.model.impl.CPDefinitionOptionValueRelModelImpl</code>.
	 * </p>
	 *
	 * @param CPDefinitionOptionRelId the cp definition option rel ID
	 * @param start the lower bound of the range of cp definition option value rels
	 * @param end the upper bound of the range of cp definition option value rels (not inclusive)
	 * @param orderByComparator the comparator to order the results by (optionally <code>null</code>)
	 * @return the ordered range of matching cp definition option value rels
	 */
	public default java.util.List<CPDefinitionOptionValueRel>
		findByCPDefinitionOptionRelId(
			long CPDefinitionOptionRelId, int start, int end,
			com.liferay.portal.kernel.util.OrderByComparator
				<CPDefinitionOptionValueRel> orderByComparator) {

		return findByCPDefinitionOptionRelId(
			CPDefinitionOptionRelId, start, end, orderByComparator, true);
	}

	/**
	 * Returns all the cp definition option value rels where CPInstanceUuid = &#63;.
	 *
	 * @param CPInstanceUuid the cp instance uuid
	 * @return the matching cp definition option value rels
	 */
	public default java.util.List<CPDefinitionOptionValueRel>
		findByCPInstanceUuid(String CPInstanceUuid) {

		return findByCPInstanceUuid(
			CPInstanceUuid, com.liferay.portal.kernel.dao.orm.QueryUtil.ALL_POS,
			com.liferay.portal.kernel.dao.orm.QueryUtil.ALL_POS, null, true);
	}

	/**
	 * Returns a range of all the cp definition option value rels where CPInstanceUuid = &#63;.
	 *
	 * <p>
	 * Useful when paginating results. Returns a maximum of <code>end - start</code> instances. <code>start</code> and <code>end</code> are not primary keys, they are indexes in the result set. Thus, <code>0</code> refers to the first result in the set. Setting both <code>start</code> and <code>end</code> to <code>com.liferay.portal.kernel.dao.orm.QueryUtil#ALL_POS</code> will return the full result set. If <code>orderByComparator</code> is specified, then the query will include the given ORDER BY logic. If <code>orderByComparator</code> is absent, then the query will include the default ORDER BY logic from <code>com.liferay.commerce.product.model.impl.CPDefinitionOptionValueRelModelImpl</code>.
	 * </p>
	 *
	 * @param CPInstanceUuid the cp instance uuid
	 * @param start the lower bound of the range of cp definition option value rels
	 * @param end the upper bound of the range of cp definition option value rels (not inclusive)
	 * @return the range of matching cp definition option value rels
	 */
	public default java.util.List<CPDefinitionOptionValueRel>
		findByCPInstanceUuid(String CPInstanceUuid, int start, int end) {

		return findByCPInstanceUuid(CPInstanceUuid, start, end, null, true);
	}

	/**
	 * Returns an ordered range of all the cp definition option value rels where CPInstanceUuid = &#63;.
	 *
	 * <p>
	 * Useful when paginating results. Returns a maximum of <code>end - start</code> instances. <code>start</code> and <code>end</code> are not primary keys, they are indexes in the result set. Thus, <code>0</code> refers to the first result in the set. Setting both <code>start</code> and <code>end</code> to <code>com.liferay.portal.kernel.dao.orm.QueryUtil#ALL_POS</code> will return the full result set. If <code>orderByComparator</code> is specified, then the query will include the given ORDER BY logic. If <code>orderByComparator</code> is absent, then the query will include the default ORDER BY logic from <code>com.liferay.commerce.product.model.impl.CPDefinitionOptionValueRelModelImpl</code>.
	 * </p>
	 *
	 * @param CPInstanceUuid the cp instance uuid
	 * @param start the lower bound of the range of cp definition option value rels
	 * @param end the upper bound of the range of cp definition option value rels (not inclusive)
	 * @param orderByComparator the comparator to order the results by (optionally <code>null</code>)
	 * @return the ordered range of matching cp definition option value rels
	 */
	public default java.util.List<CPDefinitionOptionValueRel>
		findByCPInstanceUuid(
			String CPInstanceUuid, int start, int end,
			com.liferay.portal.kernel.util.OrderByComparator
				<CPDefinitionOptionValueRel> orderByComparator) {

		return findByCPInstanceUuid(
			CPInstanceUuid, start, end, orderByComparator, true);
	}

	/**
	 * Returns all the cp definition option value rels where key = &#63;.
	 *
	 * @param key the key
	 * @return the matching cp definition option value rels
	 */
	public default java.util.List<CPDefinitionOptionValueRel> findByKey(
		String key) {

		return findByKey(
			key, com.liferay.portal.kernel.dao.orm.QueryUtil.ALL_POS,
			com.liferay.portal.kernel.dao.orm.QueryUtil.ALL_POS, null, true);
	}

	/**
	 * Returns a range of all the cp definition option value rels where key = &#63;.
	 *
	 * <p>
	 * Useful when paginating results. Returns a maximum of <code>end - start</code> instances. <code>start</code> and <code>end</code> are not primary keys, they are indexes in the result set. Thus, <code>0</code> refers to the first result in the set. Setting both <code>start</code> and <code>end</code> to <code>com.liferay.portal.kernel.dao.orm.QueryUtil#ALL_POS</code> will return the full result set. If <code>orderByComparator</code> is specified, then the query will include the given ORDER BY logic. If <code>orderByComparator</code> is absent, then the query will include the default ORDER BY logic from <code>com.liferay.commerce.product.model.impl.CPDefinitionOptionValueRelModelImpl</code>.
	 * </p>
	 *
	 * @param key the key
	 * @param start the lower bound of the range of cp definition option value rels
	 * @param end the upper bound of the range of cp definition option value rels (not inclusive)
	 * @return the range of matching cp definition option value rels
	 */
	public default java.util.List<CPDefinitionOptionValueRel> findByKey(
		String key, int start, int end) {

		return findByKey(key, start, end, null, true);
	}

	/**
	 * Returns an ordered range of all the cp definition option value rels where key = &#63;.
	 *
	 * <p>
	 * Useful when paginating results. Returns a maximum of <code>end - start</code> instances. <code>start</code> and <code>end</code> are not primary keys, they are indexes in the result set. Thus, <code>0</code> refers to the first result in the set. Setting both <code>start</code> and <code>end</code> to <code>com.liferay.portal.kernel.dao.orm.QueryUtil#ALL_POS</code> will return the full result set. If <code>orderByComparator</code> is specified, then the query will include the given ORDER BY logic. If <code>orderByComparator</code> is absent, then the query will include the default ORDER BY logic from <code>com.liferay.commerce.product.model.impl.CPDefinitionOptionValueRelModelImpl</code>.
	 * </p>
	 *
	 * @param key the key
	 * @param start the lower bound of the range of cp definition option value rels
	 * @param end the upper bound of the range of cp definition option value rels (not inclusive)
	 * @param orderByComparator the comparator to order the results by (optionally <code>null</code>)
	 * @return the ordered range of matching cp definition option value rels
	 */
	public default java.util.List<CPDefinitionOptionValueRel> findByKey(
		String key, int start, int end,
		com.liferay.portal.kernel.util.OrderByComparator
			<CPDefinitionOptionValueRel> orderByComparator) {

		return findByKey(key, start, end, orderByComparator, true);
	}

	/**
	 * Returns all the cp definition option value rels where CPDefinitionOptionRelId = &#63; and preselected = &#63;.
	 *
	 * @param CPDefinitionOptionRelId the cp definition option rel ID
	 * @param preselected the preselected
	 * @return the matching cp definition option value rels
	 */
	public default java.util.List<CPDefinitionOptionValueRel> findByCDORI_P(
		long CPDefinitionOptionRelId, boolean preselected) {

		return findByCDORI_P(
			CPDefinitionOptionRelId, preselected,
			com.liferay.portal.kernel.dao.orm.QueryUtil.ALL_POS,
			com.liferay.portal.kernel.dao.orm.QueryUtil.ALL_POS, null, true);
	}

	/**
	 * Returns a range of all the cp definition option value rels where CPDefinitionOptionRelId = &#63; and preselected = &#63;.
	 *
	 * <p>
	 * Useful when paginating results. Returns a maximum of <code>end - start</code> instances. <code>start</code> and <code>end</code> are not primary keys, they are indexes in the result set. Thus, <code>0</code> refers to the first result in the set. Setting both <code>start</code> and <code>end</code> to <code>com.liferay.portal.kernel.dao.orm.QueryUtil#ALL_POS</code> will return the full result set. If <code>orderByComparator</code> is specified, then the query will include the given ORDER BY logic. If <code>orderByComparator</code> is absent, then the query will include the default ORDER BY logic from <code>com.liferay.commerce.product.model.impl.CPDefinitionOptionValueRelModelImpl</code>.
	 * </p>
	 *
	 * @param CPDefinitionOptionRelId the cp definition option rel ID
	 * @param preselected the preselected
	 * @param start the lower bound of the range of cp definition option value rels
	 * @param end the upper bound of the range of cp definition option value rels (not inclusive)
	 * @return the range of matching cp definition option value rels
	 */
	public default java.util.List<CPDefinitionOptionValueRel> findByCDORI_P(
		long CPDefinitionOptionRelId, boolean preselected, int start, int end) {

		return findByCDORI_P(
			CPDefinitionOptionRelId, preselected, start, end, null, true);
	}

	/**
	 * Returns an ordered range of all the cp definition option value rels where CPDefinitionOptionRelId = &#63; and preselected = &#63;.
	 *
	 * <p>
	 * Useful when paginating results. Returns a maximum of <code>end - start</code> instances. <code>start</code> and <code>end</code> are not primary keys, they are indexes in the result set. Thus, <code>0</code> refers to the first result in the set. Setting both <code>start</code> and <code>end</code> to <code>com.liferay.portal.kernel.dao.orm.QueryUtil#ALL_POS</code> will return the full result set. If <code>orderByComparator</code> is specified, then the query will include the given ORDER BY logic. If <code>orderByComparator</code> is absent, then the query will include the default ORDER BY logic from <code>com.liferay.commerce.product.model.impl.CPDefinitionOptionValueRelModelImpl</code>.
	 * </p>
	 *
	 * @param CPDefinitionOptionRelId the cp definition option rel ID
	 * @param preselected the preselected
	 * @param start the lower bound of the range of cp definition option value rels
	 * @param end the upper bound of the range of cp definition option value rels (not inclusive)
	 * @param orderByComparator the comparator to order the results by (optionally <code>null</code>)
	 * @return the ordered range of matching cp definition option value rels
	 */
	public default java.util.List<CPDefinitionOptionValueRel> findByCDORI_P(
		long CPDefinitionOptionRelId, boolean preselected, int start, int end,
		com.liferay.portal.kernel.util.OrderByComparator
			<CPDefinitionOptionValueRel> orderByComparator) {

		return findByCDORI_P(
			CPDefinitionOptionRelId, preselected, start, end, orderByComparator,
			true);
	}

}
// LIFERAY-SERVICE-BUILDER-HASH:-4923223