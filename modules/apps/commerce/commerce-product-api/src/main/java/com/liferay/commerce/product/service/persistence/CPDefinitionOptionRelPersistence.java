/**
 * SPDX-FileCopyrightText: (c) 2000 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

package com.liferay.commerce.product.service.persistence;

import com.liferay.commerce.product.exception.NoSuchCPDefinitionOptionRelException;
import com.liferay.commerce.product.model.CPDefinitionOptionRel;
import com.liferay.portal.kernel.service.persistence.BasePersistence;
import com.liferay.portal.kernel.service.persistence.change.tracking.CTPersistence;

import org.osgi.annotation.versioning.ProviderType;

/**
 * The persistence interface for the cp definition option rel service.
 *
 * <p>
 * Caching information and settings can be found in <code>portal.properties</code>
 * </p>
 *
 * @author Marco Leo
 * @see CPDefinitionOptionRelUtil
 * @generated
 */
@ProviderType
public interface CPDefinitionOptionRelPersistence
	extends BasePersistence<CPDefinitionOptionRel>,
			CTPersistence<CPDefinitionOptionRel> {

	/*
	 * NOTE FOR DEVELOPERS:
	 *
	 * Never modify or reference this interface directly. Always use {@link CPDefinitionOptionRelUtil} to access the cp definition option rel persistence. Modify <code>service.xml</code> and rerun ServiceBuilder to regenerate this interface.
	 */

	/**
	 * Returns an ordered range of all the cp definition option rels where uuid = &#63;.
	 *
	 * <p>
	 * Useful when paginating results. Returns a maximum of <code>end - start</code> instances. <code>start</code> and <code>end</code> are not primary keys, they are indexes in the result set. Thus, <code>0</code> refers to the first result in the set. Setting both <code>start</code> and <code>end</code> to <code>com.liferay.portal.kernel.dao.orm.QueryUtil#ALL_POS</code> will return the full result set. If <code>orderByComparator</code> is specified, then the query will include the given ORDER BY logic. If <code>orderByComparator</code> is absent, then the query will include the default ORDER BY logic from <code>com.liferay.commerce.product.model.impl.CPDefinitionOptionRelModelImpl</code>.
	 * </p>
	 *
	 * @param uuid the uuid
	 * @param start the lower bound of the range of cp definition option rels
	 * @param end the upper bound of the range of cp definition option rels (not inclusive)
	 * @param orderByComparator the comparator to order the results by (optionally <code>null</code>)
	 * @param useFinderCache whether to use the finder cache
	 * @return the ordered range of matching cp definition option rels
	 */
	public java.util.List<CPDefinitionOptionRel> findByUuid(
		String uuid, int start, int end,
		com.liferay.portal.kernel.util.OrderByComparator<CPDefinitionOptionRel>
			orderByComparator,
		boolean useFinderCache);

	/**
	 * Returns the first cp definition option rel in the ordered set where uuid = &#63;.
	 *
	 * @param uuid the uuid
	 * @param orderByComparator the comparator to order the set by (optionally <code>null</code>)
	 * @return the first matching cp definition option rel
	 * @throws NoSuchCPDefinitionOptionRelException if a matching cp definition option rel could not be found
	 */
	public CPDefinitionOptionRel findByUuid_First(
			String uuid,
			com.liferay.portal.kernel.util.OrderByComparator
				<CPDefinitionOptionRel> orderByComparator)
		throws NoSuchCPDefinitionOptionRelException;

	/**
	 * Returns the first cp definition option rel in the ordered set where uuid = &#63;.
	 *
	 * @param uuid the uuid
	 * @param orderByComparator the comparator to order the set by (optionally <code>null</code>)
	 * @return the first matching cp definition option rel, or <code>null</code> if a matching cp definition option rel could not be found
	 */
	public CPDefinitionOptionRel fetchByUuid_First(
		String uuid,
		com.liferay.portal.kernel.util.OrderByComparator<CPDefinitionOptionRel>
			orderByComparator);

	/**
	 * Removes all the cp definition option rels where uuid = &#63; from the database.
	 *
	 * @param uuid the uuid
	 */
	public void removeByUuid(String uuid);

	/**
	 * Returns the number of cp definition option rels where uuid = &#63;.
	 *
	 * @param uuid the uuid
	 * @return the number of matching cp definition option rels
	 */
	public int countByUuid(String uuid);

	/**
	 * Returns the cp definition option rel where uuid = &#63; and groupId = &#63; or throws a <code>NoSuchCPDefinitionOptionRelException</code> if it could not be found.
	 *
	 * @param uuid the uuid
	 * @param groupId the group ID
	 * @return the matching cp definition option rel
	 * @throws NoSuchCPDefinitionOptionRelException if a matching cp definition option rel could not be found
	 */
	public CPDefinitionOptionRel findByUUID_G(String uuid, long groupId)
		throws NoSuchCPDefinitionOptionRelException;

	/**
	 * Returns the cp definition option rel where uuid = &#63; and groupId = &#63; or returns <code>null</code> if it could not be found, optionally using the finder cache.
	 *
	 * @param uuid the uuid
	 * @param groupId the group ID
	 * @param useFinderCache whether to use the finder cache
	 * @return the matching cp definition option rel, or <code>null</code> if a matching cp definition option rel could not be found
	 */
	public CPDefinitionOptionRel fetchByUUID_G(
		String uuid, long groupId, boolean useFinderCache);

	/**
	 * Removes the cp definition option rel where uuid = &#63; and groupId = &#63; from the database.
	 *
	 * @param uuid the uuid
	 * @param groupId the group ID
	 * @return the cp definition option rel that was removed
	 */
	public CPDefinitionOptionRel removeByUUID_G(String uuid, long groupId)
		throws NoSuchCPDefinitionOptionRelException;

	/**
	 * Returns the number of cp definition option rels where uuid = &#63; and groupId = &#63;.
	 *
	 * @param uuid the uuid
	 * @param groupId the group ID
	 * @return the number of matching cp definition option rels
	 */
	public int countByUUID_G(String uuid, long groupId);

	/**
	 * Returns an ordered range of all the cp definition option rels where uuid = &#63; and companyId = &#63;.
	 *
	 * <p>
	 * Useful when paginating results. Returns a maximum of <code>end - start</code> instances. <code>start</code> and <code>end</code> are not primary keys, they are indexes in the result set. Thus, <code>0</code> refers to the first result in the set. Setting both <code>start</code> and <code>end</code> to <code>com.liferay.portal.kernel.dao.orm.QueryUtil#ALL_POS</code> will return the full result set. If <code>orderByComparator</code> is specified, then the query will include the given ORDER BY logic. If <code>orderByComparator</code> is absent, then the query will include the default ORDER BY logic from <code>com.liferay.commerce.product.model.impl.CPDefinitionOptionRelModelImpl</code>.
	 * </p>
	 *
	 * @param uuid the uuid
	 * @param companyId the company ID
	 * @param start the lower bound of the range of cp definition option rels
	 * @param end the upper bound of the range of cp definition option rels (not inclusive)
	 * @param orderByComparator the comparator to order the results by (optionally <code>null</code>)
	 * @param useFinderCache whether to use the finder cache
	 * @return the ordered range of matching cp definition option rels
	 */
	public java.util.List<CPDefinitionOptionRel> findByUuid_C(
		String uuid, long companyId, int start, int end,
		com.liferay.portal.kernel.util.OrderByComparator<CPDefinitionOptionRel>
			orderByComparator,
		boolean useFinderCache);

	/**
	 * Returns the first cp definition option rel in the ordered set where uuid = &#63; and companyId = &#63;.
	 *
	 * @param uuid the uuid
	 * @param companyId the company ID
	 * @param orderByComparator the comparator to order the set by (optionally <code>null</code>)
	 * @return the first matching cp definition option rel
	 * @throws NoSuchCPDefinitionOptionRelException if a matching cp definition option rel could not be found
	 */
	public CPDefinitionOptionRel findByUuid_C_First(
			String uuid, long companyId,
			com.liferay.portal.kernel.util.OrderByComparator
				<CPDefinitionOptionRel> orderByComparator)
		throws NoSuchCPDefinitionOptionRelException;

	/**
	 * Returns the first cp definition option rel in the ordered set where uuid = &#63; and companyId = &#63;.
	 *
	 * @param uuid the uuid
	 * @param companyId the company ID
	 * @param orderByComparator the comparator to order the set by (optionally <code>null</code>)
	 * @return the first matching cp definition option rel, or <code>null</code> if a matching cp definition option rel could not be found
	 */
	public CPDefinitionOptionRel fetchByUuid_C_First(
		String uuid, long companyId,
		com.liferay.portal.kernel.util.OrderByComparator<CPDefinitionOptionRel>
			orderByComparator);

	/**
	 * Removes all the cp definition option rels where uuid = &#63; and companyId = &#63; from the database.
	 *
	 * @param uuid the uuid
	 * @param companyId the company ID
	 */
	public void removeByUuid_C(String uuid, long companyId);

	/**
	 * Returns the number of cp definition option rels where uuid = &#63; and companyId = &#63;.
	 *
	 * @param uuid the uuid
	 * @param companyId the company ID
	 * @return the number of matching cp definition option rels
	 */
	public int countByUuid_C(String uuid, long companyId);

	/**
	 * Returns an ordered range of all the cp definition option rels where groupId = &#63;.
	 *
	 * <p>
	 * Useful when paginating results. Returns a maximum of <code>end - start</code> instances. <code>start</code> and <code>end</code> are not primary keys, they are indexes in the result set. Thus, <code>0</code> refers to the first result in the set. Setting both <code>start</code> and <code>end</code> to <code>com.liferay.portal.kernel.dao.orm.QueryUtil#ALL_POS</code> will return the full result set. If <code>orderByComparator</code> is specified, then the query will include the given ORDER BY logic. If <code>orderByComparator</code> is absent, then the query will include the default ORDER BY logic from <code>com.liferay.commerce.product.model.impl.CPDefinitionOptionRelModelImpl</code>.
	 * </p>
	 *
	 * @param groupId the group ID
	 * @param start the lower bound of the range of cp definition option rels
	 * @param end the upper bound of the range of cp definition option rels (not inclusive)
	 * @param orderByComparator the comparator to order the results by (optionally <code>null</code>)
	 * @param useFinderCache whether to use the finder cache
	 * @return the ordered range of matching cp definition option rels
	 */
	public java.util.List<CPDefinitionOptionRel> findByGroupId(
		long groupId, int start, int end,
		com.liferay.portal.kernel.util.OrderByComparator<CPDefinitionOptionRel>
			orderByComparator,
		boolean useFinderCache);

	/**
	 * Returns the first cp definition option rel in the ordered set where groupId = &#63;.
	 *
	 * @param groupId the group ID
	 * @param orderByComparator the comparator to order the set by (optionally <code>null</code>)
	 * @return the first matching cp definition option rel
	 * @throws NoSuchCPDefinitionOptionRelException if a matching cp definition option rel could not be found
	 */
	public CPDefinitionOptionRel findByGroupId_First(
			long groupId,
			com.liferay.portal.kernel.util.OrderByComparator
				<CPDefinitionOptionRel> orderByComparator)
		throws NoSuchCPDefinitionOptionRelException;

	/**
	 * Returns the first cp definition option rel in the ordered set where groupId = &#63;.
	 *
	 * @param groupId the group ID
	 * @param orderByComparator the comparator to order the set by (optionally <code>null</code>)
	 * @return the first matching cp definition option rel, or <code>null</code> if a matching cp definition option rel could not be found
	 */
	public CPDefinitionOptionRel fetchByGroupId_First(
		long groupId,
		com.liferay.portal.kernel.util.OrderByComparator<CPDefinitionOptionRel>
			orderByComparator);

	/**
	 * Removes all the cp definition option rels where groupId = &#63; from the database.
	 *
	 * @param groupId the group ID
	 */
	public void removeByGroupId(long groupId);

	/**
	 * Returns the number of cp definition option rels where groupId = &#63;.
	 *
	 * @param groupId the group ID
	 * @return the number of matching cp definition option rels
	 */
	public int countByGroupId(long groupId);

	/**
	 * Returns an ordered range of all the cp definition option rels where companyId = &#63;.
	 *
	 * <p>
	 * Useful when paginating results. Returns a maximum of <code>end - start</code> instances. <code>start</code> and <code>end</code> are not primary keys, they are indexes in the result set. Thus, <code>0</code> refers to the first result in the set. Setting both <code>start</code> and <code>end</code> to <code>com.liferay.portal.kernel.dao.orm.QueryUtil#ALL_POS</code> will return the full result set. If <code>orderByComparator</code> is specified, then the query will include the given ORDER BY logic. If <code>orderByComparator</code> is absent, then the query will include the default ORDER BY logic from <code>com.liferay.commerce.product.model.impl.CPDefinitionOptionRelModelImpl</code>.
	 * </p>
	 *
	 * @param companyId the company ID
	 * @param start the lower bound of the range of cp definition option rels
	 * @param end the upper bound of the range of cp definition option rels (not inclusive)
	 * @param orderByComparator the comparator to order the results by (optionally <code>null</code>)
	 * @param useFinderCache whether to use the finder cache
	 * @return the ordered range of matching cp definition option rels
	 */
	public java.util.List<CPDefinitionOptionRel> findByCompanyId(
		long companyId, int start, int end,
		com.liferay.portal.kernel.util.OrderByComparator<CPDefinitionOptionRel>
			orderByComparator,
		boolean useFinderCache);

	/**
	 * Returns the first cp definition option rel in the ordered set where companyId = &#63;.
	 *
	 * @param companyId the company ID
	 * @param orderByComparator the comparator to order the set by (optionally <code>null</code>)
	 * @return the first matching cp definition option rel
	 * @throws NoSuchCPDefinitionOptionRelException if a matching cp definition option rel could not be found
	 */
	public CPDefinitionOptionRel findByCompanyId_First(
			long companyId,
			com.liferay.portal.kernel.util.OrderByComparator
				<CPDefinitionOptionRel> orderByComparator)
		throws NoSuchCPDefinitionOptionRelException;

	/**
	 * Returns the first cp definition option rel in the ordered set where companyId = &#63;.
	 *
	 * @param companyId the company ID
	 * @param orderByComparator the comparator to order the set by (optionally <code>null</code>)
	 * @return the first matching cp definition option rel, or <code>null</code> if a matching cp definition option rel could not be found
	 */
	public CPDefinitionOptionRel fetchByCompanyId_First(
		long companyId,
		com.liferay.portal.kernel.util.OrderByComparator<CPDefinitionOptionRel>
			orderByComparator);

	/**
	 * Removes all the cp definition option rels where companyId = &#63; from the database.
	 *
	 * @param companyId the company ID
	 */
	public void removeByCompanyId(long companyId);

	/**
	 * Returns the number of cp definition option rels where companyId = &#63;.
	 *
	 * @param companyId the company ID
	 * @return the number of matching cp definition option rels
	 */
	public int countByCompanyId(long companyId);

	/**
	 * Returns an ordered range of all the cp definition option rels where CPDefinitionId = &#63;.
	 *
	 * <p>
	 * Useful when paginating results. Returns a maximum of <code>end - start</code> instances. <code>start</code> and <code>end</code> are not primary keys, they are indexes in the result set. Thus, <code>0</code> refers to the first result in the set. Setting both <code>start</code> and <code>end</code> to <code>com.liferay.portal.kernel.dao.orm.QueryUtil#ALL_POS</code> will return the full result set. If <code>orderByComparator</code> is specified, then the query will include the given ORDER BY logic. If <code>orderByComparator</code> is absent, then the query will include the default ORDER BY logic from <code>com.liferay.commerce.product.model.impl.CPDefinitionOptionRelModelImpl</code>.
	 * </p>
	 *
	 * @param CPDefinitionId the cp definition ID
	 * @param start the lower bound of the range of cp definition option rels
	 * @param end the upper bound of the range of cp definition option rels (not inclusive)
	 * @param orderByComparator the comparator to order the results by (optionally <code>null</code>)
	 * @param useFinderCache whether to use the finder cache
	 * @return the ordered range of matching cp definition option rels
	 */
	public java.util.List<CPDefinitionOptionRel> findByCPDefinitionId(
		long CPDefinitionId, int start, int end,
		com.liferay.portal.kernel.util.OrderByComparator<CPDefinitionOptionRel>
			orderByComparator,
		boolean useFinderCache);

	/**
	 * Returns the first cp definition option rel in the ordered set where CPDefinitionId = &#63;.
	 *
	 * @param CPDefinitionId the cp definition ID
	 * @param orderByComparator the comparator to order the set by (optionally <code>null</code>)
	 * @return the first matching cp definition option rel
	 * @throws NoSuchCPDefinitionOptionRelException if a matching cp definition option rel could not be found
	 */
	public CPDefinitionOptionRel findByCPDefinitionId_First(
			long CPDefinitionId,
			com.liferay.portal.kernel.util.OrderByComparator
				<CPDefinitionOptionRel> orderByComparator)
		throws NoSuchCPDefinitionOptionRelException;

	/**
	 * Returns the first cp definition option rel in the ordered set where CPDefinitionId = &#63;.
	 *
	 * @param CPDefinitionId the cp definition ID
	 * @param orderByComparator the comparator to order the set by (optionally <code>null</code>)
	 * @return the first matching cp definition option rel, or <code>null</code> if a matching cp definition option rel could not be found
	 */
	public CPDefinitionOptionRel fetchByCPDefinitionId_First(
		long CPDefinitionId,
		com.liferay.portal.kernel.util.OrderByComparator<CPDefinitionOptionRel>
			orderByComparator);

	/**
	 * Removes all the cp definition option rels where CPDefinitionId = &#63; from the database.
	 *
	 * @param CPDefinitionId the cp definition ID
	 */
	public void removeByCPDefinitionId(long CPDefinitionId);

	/**
	 * Returns the number of cp definition option rels where CPDefinitionId = &#63;.
	 *
	 * @param CPDefinitionId the cp definition ID
	 * @return the number of matching cp definition option rels
	 */
	public int countByCPDefinitionId(long CPDefinitionId);

	/**
	 * Returns an ordered range of all the cp definition option rels where CPOptionId = &#63;.
	 *
	 * <p>
	 * Useful when paginating results. Returns a maximum of <code>end - start</code> instances. <code>start</code> and <code>end</code> are not primary keys, they are indexes in the result set. Thus, <code>0</code> refers to the first result in the set. Setting both <code>start</code> and <code>end</code> to <code>com.liferay.portal.kernel.dao.orm.QueryUtil#ALL_POS</code> will return the full result set. If <code>orderByComparator</code> is specified, then the query will include the given ORDER BY logic. If <code>orderByComparator</code> is absent, then the query will include the default ORDER BY logic from <code>com.liferay.commerce.product.model.impl.CPDefinitionOptionRelModelImpl</code>.
	 * </p>
	 *
	 * @param CPOptionId the cp option ID
	 * @param start the lower bound of the range of cp definition option rels
	 * @param end the upper bound of the range of cp definition option rels (not inclusive)
	 * @param orderByComparator the comparator to order the results by (optionally <code>null</code>)
	 * @param useFinderCache whether to use the finder cache
	 * @return the ordered range of matching cp definition option rels
	 */
	public java.util.List<CPDefinitionOptionRel> findByCPOptionId(
		long CPOptionId, int start, int end,
		com.liferay.portal.kernel.util.OrderByComparator<CPDefinitionOptionRel>
			orderByComparator,
		boolean useFinderCache);

	/**
	 * Returns the first cp definition option rel in the ordered set where CPOptionId = &#63;.
	 *
	 * @param CPOptionId the cp option ID
	 * @param orderByComparator the comparator to order the set by (optionally <code>null</code>)
	 * @return the first matching cp definition option rel
	 * @throws NoSuchCPDefinitionOptionRelException if a matching cp definition option rel could not be found
	 */
	public CPDefinitionOptionRel findByCPOptionId_First(
			long CPOptionId,
			com.liferay.portal.kernel.util.OrderByComparator
				<CPDefinitionOptionRel> orderByComparator)
		throws NoSuchCPDefinitionOptionRelException;

	/**
	 * Returns the first cp definition option rel in the ordered set where CPOptionId = &#63;.
	 *
	 * @param CPOptionId the cp option ID
	 * @param orderByComparator the comparator to order the set by (optionally <code>null</code>)
	 * @return the first matching cp definition option rel, or <code>null</code> if a matching cp definition option rel could not be found
	 */
	public CPDefinitionOptionRel fetchByCPOptionId_First(
		long CPOptionId,
		com.liferay.portal.kernel.util.OrderByComparator<CPDefinitionOptionRel>
			orderByComparator);

	/**
	 * Removes all the cp definition option rels where CPOptionId = &#63; from the database.
	 *
	 * @param CPOptionId the cp option ID
	 */
	public void removeByCPOptionId(long CPOptionId);

	/**
	 * Returns the number of cp definition option rels where CPOptionId = &#63;.
	 *
	 * @param CPOptionId the cp option ID
	 * @return the number of matching cp definition option rels
	 */
	public int countByCPOptionId(long CPOptionId);

	/**
	 * Returns the cp definition option rel where CPDefinitionId = &#63; and CPOptionId = &#63; or throws a <code>NoSuchCPDefinitionOptionRelException</code> if it could not be found.
	 *
	 * @param CPDefinitionId the cp definition ID
	 * @param CPOptionId the cp option ID
	 * @return the matching cp definition option rel
	 * @throws NoSuchCPDefinitionOptionRelException if a matching cp definition option rel could not be found
	 */
	public CPDefinitionOptionRel findByC_C(long CPDefinitionId, long CPOptionId)
		throws NoSuchCPDefinitionOptionRelException;

	/**
	 * Returns the cp definition option rel where CPDefinitionId = &#63; and CPOptionId = &#63; or returns <code>null</code> if it could not be found, optionally using the finder cache.
	 *
	 * @param CPDefinitionId the cp definition ID
	 * @param CPOptionId the cp option ID
	 * @param useFinderCache whether to use the finder cache
	 * @return the matching cp definition option rel, or <code>null</code> if a matching cp definition option rel could not be found
	 */
	public CPDefinitionOptionRel fetchByC_C(
		long CPDefinitionId, long CPOptionId, boolean useFinderCache);

	/**
	 * Removes the cp definition option rel where CPDefinitionId = &#63; and CPOptionId = &#63; from the database.
	 *
	 * @param CPDefinitionId the cp definition ID
	 * @param CPOptionId the cp option ID
	 * @return the cp definition option rel that was removed
	 */
	public CPDefinitionOptionRel removeByC_C(
			long CPDefinitionId, long CPOptionId)
		throws NoSuchCPDefinitionOptionRelException;

	/**
	 * Returns the number of cp definition option rels where CPDefinitionId = &#63; and CPOptionId = &#63;.
	 *
	 * @param CPDefinitionId the cp definition ID
	 * @param CPOptionId the cp option ID
	 * @return the number of matching cp definition option rels
	 */
	public int countByC_C(long CPDefinitionId, long CPOptionId);

	/**
	 * Returns an ordered range of all the cp definition option rels where CPDefinitionId = &#63; and required = &#63;.
	 *
	 * <p>
	 * Useful when paginating results. Returns a maximum of <code>end - start</code> instances. <code>start</code> and <code>end</code> are not primary keys, they are indexes in the result set. Thus, <code>0</code> refers to the first result in the set. Setting both <code>start</code> and <code>end</code> to <code>com.liferay.portal.kernel.dao.orm.QueryUtil#ALL_POS</code> will return the full result set. If <code>orderByComparator</code> is specified, then the query will include the given ORDER BY logic. If <code>orderByComparator</code> is absent, then the query will include the default ORDER BY logic from <code>com.liferay.commerce.product.model.impl.CPDefinitionOptionRelModelImpl</code>.
	 * </p>
	 *
	 * @param CPDefinitionId the cp definition ID
	 * @param required the required
	 * @param start the lower bound of the range of cp definition option rels
	 * @param end the upper bound of the range of cp definition option rels (not inclusive)
	 * @param orderByComparator the comparator to order the results by (optionally <code>null</code>)
	 * @param useFinderCache whether to use the finder cache
	 * @return the ordered range of matching cp definition option rels
	 */
	public java.util.List<CPDefinitionOptionRel> findByCPDI_R(
		long CPDefinitionId, boolean required, int start, int end,
		com.liferay.portal.kernel.util.OrderByComparator<CPDefinitionOptionRel>
			orderByComparator,
		boolean useFinderCache);

	/**
	 * Returns the first cp definition option rel in the ordered set where CPDefinitionId = &#63; and required = &#63;.
	 *
	 * @param CPDefinitionId the cp definition ID
	 * @param required the required
	 * @param orderByComparator the comparator to order the set by (optionally <code>null</code>)
	 * @return the first matching cp definition option rel
	 * @throws NoSuchCPDefinitionOptionRelException if a matching cp definition option rel could not be found
	 */
	public CPDefinitionOptionRel findByCPDI_R_First(
			long CPDefinitionId, boolean required,
			com.liferay.portal.kernel.util.OrderByComparator
				<CPDefinitionOptionRel> orderByComparator)
		throws NoSuchCPDefinitionOptionRelException;

	/**
	 * Returns the first cp definition option rel in the ordered set where CPDefinitionId = &#63; and required = &#63;.
	 *
	 * @param CPDefinitionId the cp definition ID
	 * @param required the required
	 * @param orderByComparator the comparator to order the set by (optionally <code>null</code>)
	 * @return the first matching cp definition option rel, or <code>null</code> if a matching cp definition option rel could not be found
	 */
	public CPDefinitionOptionRel fetchByCPDI_R_First(
		long CPDefinitionId, boolean required,
		com.liferay.portal.kernel.util.OrderByComparator<CPDefinitionOptionRel>
			orderByComparator);

	/**
	 * Removes all the cp definition option rels where CPDefinitionId = &#63; and required = &#63; from the database.
	 *
	 * @param CPDefinitionId the cp definition ID
	 * @param required the required
	 */
	public void removeByCPDI_R(long CPDefinitionId, boolean required);

	/**
	 * Returns the number of cp definition option rels where CPDefinitionId = &#63; and required = &#63;.
	 *
	 * @param CPDefinitionId the cp definition ID
	 * @param required the required
	 * @return the number of matching cp definition option rels
	 */
	public int countByCPDI_R(long CPDefinitionId, boolean required);

	/**
	 * Returns an ordered range of all the cp definition option rels where CPDefinitionId = &#63; and skuContributor = &#63;.
	 *
	 * <p>
	 * Useful when paginating results. Returns a maximum of <code>end - start</code> instances. <code>start</code> and <code>end</code> are not primary keys, they are indexes in the result set. Thus, <code>0</code> refers to the first result in the set. Setting both <code>start</code> and <code>end</code> to <code>com.liferay.portal.kernel.dao.orm.QueryUtil#ALL_POS</code> will return the full result set. If <code>orderByComparator</code> is specified, then the query will include the given ORDER BY logic. If <code>orderByComparator</code> is absent, then the query will include the default ORDER BY logic from <code>com.liferay.commerce.product.model.impl.CPDefinitionOptionRelModelImpl</code>.
	 * </p>
	 *
	 * @param CPDefinitionId the cp definition ID
	 * @param skuContributor the sku contributor
	 * @param start the lower bound of the range of cp definition option rels
	 * @param end the upper bound of the range of cp definition option rels (not inclusive)
	 * @param orderByComparator the comparator to order the results by (optionally <code>null</code>)
	 * @param useFinderCache whether to use the finder cache
	 * @return the ordered range of matching cp definition option rels
	 */
	public java.util.List<CPDefinitionOptionRel> findByC_SC(
		long CPDefinitionId, boolean skuContributor, int start, int end,
		com.liferay.portal.kernel.util.OrderByComparator<CPDefinitionOptionRel>
			orderByComparator,
		boolean useFinderCache);

	/**
	 * Returns the first cp definition option rel in the ordered set where CPDefinitionId = &#63; and skuContributor = &#63;.
	 *
	 * @param CPDefinitionId the cp definition ID
	 * @param skuContributor the sku contributor
	 * @param orderByComparator the comparator to order the set by (optionally <code>null</code>)
	 * @return the first matching cp definition option rel
	 * @throws NoSuchCPDefinitionOptionRelException if a matching cp definition option rel could not be found
	 */
	public CPDefinitionOptionRel findByC_SC_First(
			long CPDefinitionId, boolean skuContributor,
			com.liferay.portal.kernel.util.OrderByComparator
				<CPDefinitionOptionRel> orderByComparator)
		throws NoSuchCPDefinitionOptionRelException;

	/**
	 * Returns the first cp definition option rel in the ordered set where CPDefinitionId = &#63; and skuContributor = &#63;.
	 *
	 * @param CPDefinitionId the cp definition ID
	 * @param skuContributor the sku contributor
	 * @param orderByComparator the comparator to order the set by (optionally <code>null</code>)
	 * @return the first matching cp definition option rel, or <code>null</code> if a matching cp definition option rel could not be found
	 */
	public CPDefinitionOptionRel fetchByC_SC_First(
		long CPDefinitionId, boolean skuContributor,
		com.liferay.portal.kernel.util.OrderByComparator<CPDefinitionOptionRel>
			orderByComparator);

	/**
	 * Removes all the cp definition option rels where CPDefinitionId = &#63; and skuContributor = &#63; from the database.
	 *
	 * @param CPDefinitionId the cp definition ID
	 * @param skuContributor the sku contributor
	 */
	public void removeByC_SC(long CPDefinitionId, boolean skuContributor);

	/**
	 * Returns the number of cp definition option rels where CPDefinitionId = &#63; and skuContributor = &#63;.
	 *
	 * @param CPDefinitionId the cp definition ID
	 * @param skuContributor the sku contributor
	 * @return the number of matching cp definition option rels
	 */
	public int countByC_SC(long CPDefinitionId, boolean skuContributor);

	/**
	 * Returns the cp definition option rel where CPDefinitionId = &#63; and key = &#63; or throws a <code>NoSuchCPDefinitionOptionRelException</code> if it could not be found.
	 *
	 * @param CPDefinitionId the cp definition ID
	 * @param key the key
	 * @return the matching cp definition option rel
	 * @throws NoSuchCPDefinitionOptionRelException if a matching cp definition option rel could not be found
	 */
	public CPDefinitionOptionRel findByC_K(long CPDefinitionId, String key)
		throws NoSuchCPDefinitionOptionRelException;

	/**
	 * Returns the cp definition option rel where CPDefinitionId = &#63; and key = &#63; or returns <code>null</code> if it could not be found, optionally using the finder cache.
	 *
	 * @param CPDefinitionId the cp definition ID
	 * @param key the key
	 * @param useFinderCache whether to use the finder cache
	 * @return the matching cp definition option rel, or <code>null</code> if a matching cp definition option rel could not be found
	 */
	public CPDefinitionOptionRel fetchByC_K(
		long CPDefinitionId, String key, boolean useFinderCache);

	/**
	 * Removes the cp definition option rel where CPDefinitionId = &#63; and key = &#63; from the database.
	 *
	 * @param CPDefinitionId the cp definition ID
	 * @param key the key
	 * @return the cp definition option rel that was removed
	 */
	public CPDefinitionOptionRel removeByC_K(long CPDefinitionId, String key)
		throws NoSuchCPDefinitionOptionRelException;

	/**
	 * Returns the number of cp definition option rels where CPDefinitionId = &#63; and key = &#63;.
	 *
	 * @param CPDefinitionId the cp definition ID
	 * @param key the key
	 * @return the number of matching cp definition option rels
	 */
	public int countByC_K(long CPDefinitionId, String key);

	/**
	 * Returns the cp definition option rel where externalReferenceCode = &#63; and companyId = &#63; or throws a <code>NoSuchCPDefinitionOptionRelException</code> if it could not be found.
	 *
	 * @param externalReferenceCode the external reference code
	 * @param companyId the company ID
	 * @return the matching cp definition option rel
	 * @throws NoSuchCPDefinitionOptionRelException if a matching cp definition option rel could not be found
	 */
	public CPDefinitionOptionRel findByERC_C(
			String externalReferenceCode, long companyId)
		throws NoSuchCPDefinitionOptionRelException;

	/**
	 * Returns the cp definition option rel where externalReferenceCode = &#63; and companyId = &#63; or returns <code>null</code> if it could not be found, optionally using the finder cache.
	 *
	 * @param externalReferenceCode the external reference code
	 * @param companyId the company ID
	 * @param useFinderCache whether to use the finder cache
	 * @return the matching cp definition option rel, or <code>null</code> if a matching cp definition option rel could not be found
	 */
	public CPDefinitionOptionRel fetchByERC_C(
		String externalReferenceCode, long companyId, boolean useFinderCache);

	/**
	 * Removes the cp definition option rel where externalReferenceCode = &#63; and companyId = &#63; from the database.
	 *
	 * @param externalReferenceCode the external reference code
	 * @param companyId the company ID
	 * @return the cp definition option rel that was removed
	 */
	public CPDefinitionOptionRel removeByERC_C(
			String externalReferenceCode, long companyId)
		throws NoSuchCPDefinitionOptionRelException;

	/**
	 * Returns the number of cp definition option rels where externalReferenceCode = &#63; and companyId = &#63;.
	 *
	 * @param externalReferenceCode the external reference code
	 * @param companyId the company ID
	 * @return the number of matching cp definition option rels
	 */
	public int countByERC_C(String externalReferenceCode, long companyId);

	/**
	 * Creates a new cp definition option rel with the primary key. Does not add the cp definition option rel to the database.
	 *
	 * @param CPDefinitionOptionRelId the primary key for the new cp definition option rel
	 * @return the new cp definition option rel
	 */
	public CPDefinitionOptionRel create(long CPDefinitionOptionRelId);

	/**
	 * Removes the cp definition option rel with the primary key from the database. Also notifies the appropriate model listeners.
	 *
	 * @param CPDefinitionOptionRelId the primary key of the cp definition option rel
	 * @return the cp definition option rel that was removed
	 * @throws NoSuchCPDefinitionOptionRelException if a cp definition option rel with the primary key could not be found
	 */
	public CPDefinitionOptionRel remove(long CPDefinitionOptionRelId)
		throws NoSuchCPDefinitionOptionRelException;

	public CPDefinitionOptionRel updateImpl(
		CPDefinitionOptionRel cpDefinitionOptionRel);

	/**
	 * Returns the cp definition option rel with the primary key or throws a <code>NoSuchCPDefinitionOptionRelException</code> if it could not be found.
	 *
	 * @param CPDefinitionOptionRelId the primary key of the cp definition option rel
	 * @return the cp definition option rel
	 * @throws NoSuchCPDefinitionOptionRelException if a cp definition option rel with the primary key could not be found
	 */
	public CPDefinitionOptionRel findByPrimaryKey(long CPDefinitionOptionRelId)
		throws NoSuchCPDefinitionOptionRelException;

	/**
	 * Returns the cp definition option rel with the primary key or returns <code>null</code> if it could not be found.
	 *
	 * @param CPDefinitionOptionRelId the primary key of the cp definition option rel
	 * @return the cp definition option rel, or <code>null</code> if a cp definition option rel with the primary key could not be found
	 */
	public CPDefinitionOptionRel fetchByPrimaryKey(
		long CPDefinitionOptionRelId);

	/**
	 * Returns the cp definition option rel where uuid = &#63; and groupId = &#63; or returns <code>null</code> if it could not be found. Uses the finder cache.
	 *
	 * @param uuid the uuid
	 * @param groupId the group ID
	 * @return the matching cp definition option rel, or <code>null</code> if a matching cp definition option rel could not be found
	 */
	public default CPDefinitionOptionRel fetchByUUID_G(
		String uuid, long groupId) {

		return fetchByUUID_G(uuid, groupId, true);
	}

	/**
	 * Returns the cp definition option rel where CPDefinitionId = &#63; and CPOptionId = &#63; or returns <code>null</code> if it could not be found. Uses the finder cache.
	 *
	 * @param CPDefinitionId the cp definition ID
	 * @param CPOptionId the cp option ID
	 * @return the matching cp definition option rel, or <code>null</code> if a matching cp definition option rel could not be found
	 */
	public default CPDefinitionOptionRel fetchByC_C(
		long CPDefinitionId, long CPOptionId) {

		return fetchByC_C(CPDefinitionId, CPOptionId, true);
	}

	/**
	 * Returns the cp definition option rel where CPDefinitionId = &#63; and key = &#63; or returns <code>null</code> if it could not be found. Uses the finder cache.
	 *
	 * @param CPDefinitionId the cp definition ID
	 * @param key the key
	 * @return the matching cp definition option rel, or <code>null</code> if a matching cp definition option rel could not be found
	 */
	public default CPDefinitionOptionRel fetchByC_K(
		long CPDefinitionId, String key) {

		return fetchByC_K(CPDefinitionId, key, true);
	}

	/**
	 * Returns the cp definition option rel where externalReferenceCode = &#63; and companyId = &#63; or returns <code>null</code> if it could not be found. Uses the finder cache.
	 *
	 * @param externalReferenceCode the external reference code
	 * @param companyId the company ID
	 * @return the matching cp definition option rel, or <code>null</code> if a matching cp definition option rel could not be found
	 */
	public default CPDefinitionOptionRel fetchByERC_C(
		String externalReferenceCode, long companyId) {

		return fetchByERC_C(externalReferenceCode, companyId, true);
	}

	/**
	 * Returns all the cp definition option rels where uuid = &#63;.
	 *
	 * @param uuid the uuid
	 * @return the matching cp definition option rels
	 */
	public default java.util.List<CPDefinitionOptionRel> findByUuid(
		String uuid) {

		return findByUuid(
			uuid, com.liferay.portal.kernel.dao.orm.QueryUtil.ALL_POS,
			com.liferay.portal.kernel.dao.orm.QueryUtil.ALL_POS, null, true);
	}

	/**
	 * Returns a range of all the cp definition option rels where uuid = &#63;.
	 *
	 * <p>
	 * Useful when paginating results. Returns a maximum of <code>end - start</code> instances. <code>start</code> and <code>end</code> are not primary keys, they are indexes in the result set. Thus, <code>0</code> refers to the first result in the set. Setting both <code>start</code> and <code>end</code> to <code>com.liferay.portal.kernel.dao.orm.QueryUtil#ALL_POS</code> will return the full result set. If <code>orderByComparator</code> is specified, then the query will include the given ORDER BY logic. If <code>orderByComparator</code> is absent, then the query will include the default ORDER BY logic from <code>com.liferay.commerce.product.model.impl.CPDefinitionOptionRelModelImpl</code>.
	 * </p>
	 *
	 * @param uuid the uuid
	 * @param start the lower bound of the range of cp definition option rels
	 * @param end the upper bound of the range of cp definition option rels (not inclusive)
	 * @return the range of matching cp definition option rels
	 */
	public default java.util.List<CPDefinitionOptionRel> findByUuid(
		String uuid, int start, int end) {

		return findByUuid(uuid, start, end, null, true);
	}

	/**
	 * Returns an ordered range of all the cp definition option rels where uuid = &#63;.
	 *
	 * <p>
	 * Useful when paginating results. Returns a maximum of <code>end - start</code> instances. <code>start</code> and <code>end</code> are not primary keys, they are indexes in the result set. Thus, <code>0</code> refers to the first result in the set. Setting both <code>start</code> and <code>end</code> to <code>com.liferay.portal.kernel.dao.orm.QueryUtil#ALL_POS</code> will return the full result set. If <code>orderByComparator</code> is specified, then the query will include the given ORDER BY logic. If <code>orderByComparator</code> is absent, then the query will include the default ORDER BY logic from <code>com.liferay.commerce.product.model.impl.CPDefinitionOptionRelModelImpl</code>.
	 * </p>
	 *
	 * @param uuid the uuid
	 * @param start the lower bound of the range of cp definition option rels
	 * @param end the upper bound of the range of cp definition option rels (not inclusive)
	 * @param orderByComparator the comparator to order the results by (optionally <code>null</code>)
	 * @return the ordered range of matching cp definition option rels
	 */
	public default java.util.List<CPDefinitionOptionRel> findByUuid(
		String uuid, int start, int end,
		com.liferay.portal.kernel.util.OrderByComparator<CPDefinitionOptionRel>
			orderByComparator) {

		return findByUuid(uuid, start, end, orderByComparator, true);
	}

	/**
	 * Returns all the cp definition option rels where uuid = &#63; and companyId = &#63;.
	 *
	 * @param uuid the uuid
	 * @param companyId the company ID
	 * @return the matching cp definition option rels
	 */
	public default java.util.List<CPDefinitionOptionRel> findByUuid_C(
		String uuid, long companyId) {

		return findByUuid_C(
			uuid, companyId,
			com.liferay.portal.kernel.dao.orm.QueryUtil.ALL_POS,
			com.liferay.portal.kernel.dao.orm.QueryUtil.ALL_POS, null, true);
	}

	/**
	 * Returns a range of all the cp definition option rels where uuid = &#63; and companyId = &#63;.
	 *
	 * <p>
	 * Useful when paginating results. Returns a maximum of <code>end - start</code> instances. <code>start</code> and <code>end</code> are not primary keys, they are indexes in the result set. Thus, <code>0</code> refers to the first result in the set. Setting both <code>start</code> and <code>end</code> to <code>com.liferay.portal.kernel.dao.orm.QueryUtil#ALL_POS</code> will return the full result set. If <code>orderByComparator</code> is specified, then the query will include the given ORDER BY logic. If <code>orderByComparator</code> is absent, then the query will include the default ORDER BY logic from <code>com.liferay.commerce.product.model.impl.CPDefinitionOptionRelModelImpl</code>.
	 * </p>
	 *
	 * @param uuid the uuid
	 * @param companyId the company ID
	 * @param start the lower bound of the range of cp definition option rels
	 * @param end the upper bound of the range of cp definition option rels (not inclusive)
	 * @return the range of matching cp definition option rels
	 */
	public default java.util.List<CPDefinitionOptionRel> findByUuid_C(
		String uuid, long companyId, int start, int end) {

		return findByUuid_C(uuid, companyId, start, end, null, true);
	}

	/**
	 * Returns an ordered range of all the cp definition option rels where uuid = &#63; and companyId = &#63;.
	 *
	 * <p>
	 * Useful when paginating results. Returns a maximum of <code>end - start</code> instances. <code>start</code> and <code>end</code> are not primary keys, they are indexes in the result set. Thus, <code>0</code> refers to the first result in the set. Setting both <code>start</code> and <code>end</code> to <code>com.liferay.portal.kernel.dao.orm.QueryUtil#ALL_POS</code> will return the full result set. If <code>orderByComparator</code> is specified, then the query will include the given ORDER BY logic. If <code>orderByComparator</code> is absent, then the query will include the default ORDER BY logic from <code>com.liferay.commerce.product.model.impl.CPDefinitionOptionRelModelImpl</code>.
	 * </p>
	 *
	 * @param uuid the uuid
	 * @param companyId the company ID
	 * @param start the lower bound of the range of cp definition option rels
	 * @param end the upper bound of the range of cp definition option rels (not inclusive)
	 * @param orderByComparator the comparator to order the results by (optionally <code>null</code>)
	 * @return the ordered range of matching cp definition option rels
	 */
	public default java.util.List<CPDefinitionOptionRel> findByUuid_C(
		String uuid, long companyId, int start, int end,
		com.liferay.portal.kernel.util.OrderByComparator<CPDefinitionOptionRel>
			orderByComparator) {

		return findByUuid_C(
			uuid, companyId, start, end, orderByComparator, true);
	}

	/**
	 * Returns all the cp definition option rels where groupId = &#63;.
	 *
	 * @param groupId the group ID
	 * @return the matching cp definition option rels
	 */
	public default java.util.List<CPDefinitionOptionRel> findByGroupId(
		long groupId) {

		return findByGroupId(
			groupId, com.liferay.portal.kernel.dao.orm.QueryUtil.ALL_POS,
			com.liferay.portal.kernel.dao.orm.QueryUtil.ALL_POS, null, true);
	}

	/**
	 * Returns a range of all the cp definition option rels where groupId = &#63;.
	 *
	 * <p>
	 * Useful when paginating results. Returns a maximum of <code>end - start</code> instances. <code>start</code> and <code>end</code> are not primary keys, they are indexes in the result set. Thus, <code>0</code> refers to the first result in the set. Setting both <code>start</code> and <code>end</code> to <code>com.liferay.portal.kernel.dao.orm.QueryUtil#ALL_POS</code> will return the full result set. If <code>orderByComparator</code> is specified, then the query will include the given ORDER BY logic. If <code>orderByComparator</code> is absent, then the query will include the default ORDER BY logic from <code>com.liferay.commerce.product.model.impl.CPDefinitionOptionRelModelImpl</code>.
	 * </p>
	 *
	 * @param groupId the group ID
	 * @param start the lower bound of the range of cp definition option rels
	 * @param end the upper bound of the range of cp definition option rels (not inclusive)
	 * @return the range of matching cp definition option rels
	 */
	public default java.util.List<CPDefinitionOptionRel> findByGroupId(
		long groupId, int start, int end) {

		return findByGroupId(groupId, start, end, null, true);
	}

	/**
	 * Returns an ordered range of all the cp definition option rels where groupId = &#63;.
	 *
	 * <p>
	 * Useful when paginating results. Returns a maximum of <code>end - start</code> instances. <code>start</code> and <code>end</code> are not primary keys, they are indexes in the result set. Thus, <code>0</code> refers to the first result in the set. Setting both <code>start</code> and <code>end</code> to <code>com.liferay.portal.kernel.dao.orm.QueryUtil#ALL_POS</code> will return the full result set. If <code>orderByComparator</code> is specified, then the query will include the given ORDER BY logic. If <code>orderByComparator</code> is absent, then the query will include the default ORDER BY logic from <code>com.liferay.commerce.product.model.impl.CPDefinitionOptionRelModelImpl</code>.
	 * </p>
	 *
	 * @param groupId the group ID
	 * @param start the lower bound of the range of cp definition option rels
	 * @param end the upper bound of the range of cp definition option rels (not inclusive)
	 * @param orderByComparator the comparator to order the results by (optionally <code>null</code>)
	 * @return the ordered range of matching cp definition option rels
	 */
	public default java.util.List<CPDefinitionOptionRel> findByGroupId(
		long groupId, int start, int end,
		com.liferay.portal.kernel.util.OrderByComparator<CPDefinitionOptionRel>
			orderByComparator) {

		return findByGroupId(groupId, start, end, orderByComparator, true);
	}

	/**
	 * Returns all the cp definition option rels where companyId = &#63;.
	 *
	 * @param companyId the company ID
	 * @return the matching cp definition option rels
	 */
	public default java.util.List<CPDefinitionOptionRel> findByCompanyId(
		long companyId) {

		return findByCompanyId(
			companyId, com.liferay.portal.kernel.dao.orm.QueryUtil.ALL_POS,
			com.liferay.portal.kernel.dao.orm.QueryUtil.ALL_POS, null, true);
	}

	/**
	 * Returns a range of all the cp definition option rels where companyId = &#63;.
	 *
	 * <p>
	 * Useful when paginating results. Returns a maximum of <code>end - start</code> instances. <code>start</code> and <code>end</code> are not primary keys, they are indexes in the result set. Thus, <code>0</code> refers to the first result in the set. Setting both <code>start</code> and <code>end</code> to <code>com.liferay.portal.kernel.dao.orm.QueryUtil#ALL_POS</code> will return the full result set. If <code>orderByComparator</code> is specified, then the query will include the given ORDER BY logic. If <code>orderByComparator</code> is absent, then the query will include the default ORDER BY logic from <code>com.liferay.commerce.product.model.impl.CPDefinitionOptionRelModelImpl</code>.
	 * </p>
	 *
	 * @param companyId the company ID
	 * @param start the lower bound of the range of cp definition option rels
	 * @param end the upper bound of the range of cp definition option rels (not inclusive)
	 * @return the range of matching cp definition option rels
	 */
	public default java.util.List<CPDefinitionOptionRel> findByCompanyId(
		long companyId, int start, int end) {

		return findByCompanyId(companyId, start, end, null, true);
	}

	/**
	 * Returns an ordered range of all the cp definition option rels where companyId = &#63;.
	 *
	 * <p>
	 * Useful when paginating results. Returns a maximum of <code>end - start</code> instances. <code>start</code> and <code>end</code> are not primary keys, they are indexes in the result set. Thus, <code>0</code> refers to the first result in the set. Setting both <code>start</code> and <code>end</code> to <code>com.liferay.portal.kernel.dao.orm.QueryUtil#ALL_POS</code> will return the full result set. If <code>orderByComparator</code> is specified, then the query will include the given ORDER BY logic. If <code>orderByComparator</code> is absent, then the query will include the default ORDER BY logic from <code>com.liferay.commerce.product.model.impl.CPDefinitionOptionRelModelImpl</code>.
	 * </p>
	 *
	 * @param companyId the company ID
	 * @param start the lower bound of the range of cp definition option rels
	 * @param end the upper bound of the range of cp definition option rels (not inclusive)
	 * @param orderByComparator the comparator to order the results by (optionally <code>null</code>)
	 * @return the ordered range of matching cp definition option rels
	 */
	public default java.util.List<CPDefinitionOptionRel> findByCompanyId(
		long companyId, int start, int end,
		com.liferay.portal.kernel.util.OrderByComparator<CPDefinitionOptionRel>
			orderByComparator) {

		return findByCompanyId(companyId, start, end, orderByComparator, true);
	}

	/**
	 * Returns all the cp definition option rels where CPDefinitionId = &#63;.
	 *
	 * @param CPDefinitionId the cp definition ID
	 * @return the matching cp definition option rels
	 */
	public default java.util.List<CPDefinitionOptionRel> findByCPDefinitionId(
		long CPDefinitionId) {

		return findByCPDefinitionId(
			CPDefinitionId, com.liferay.portal.kernel.dao.orm.QueryUtil.ALL_POS,
			com.liferay.portal.kernel.dao.orm.QueryUtil.ALL_POS, null, true);
	}

	/**
	 * Returns a range of all the cp definition option rels where CPDefinitionId = &#63;.
	 *
	 * <p>
	 * Useful when paginating results. Returns a maximum of <code>end - start</code> instances. <code>start</code> and <code>end</code> are not primary keys, they are indexes in the result set. Thus, <code>0</code> refers to the first result in the set. Setting both <code>start</code> and <code>end</code> to <code>com.liferay.portal.kernel.dao.orm.QueryUtil#ALL_POS</code> will return the full result set. If <code>orderByComparator</code> is specified, then the query will include the given ORDER BY logic. If <code>orderByComparator</code> is absent, then the query will include the default ORDER BY logic from <code>com.liferay.commerce.product.model.impl.CPDefinitionOptionRelModelImpl</code>.
	 * </p>
	 *
	 * @param CPDefinitionId the cp definition ID
	 * @param start the lower bound of the range of cp definition option rels
	 * @param end the upper bound of the range of cp definition option rels (not inclusive)
	 * @return the range of matching cp definition option rels
	 */
	public default java.util.List<CPDefinitionOptionRel> findByCPDefinitionId(
		long CPDefinitionId, int start, int end) {

		return findByCPDefinitionId(CPDefinitionId, start, end, null, true);
	}

	/**
	 * Returns an ordered range of all the cp definition option rels where CPDefinitionId = &#63;.
	 *
	 * <p>
	 * Useful when paginating results. Returns a maximum of <code>end - start</code> instances. <code>start</code> and <code>end</code> are not primary keys, they are indexes in the result set. Thus, <code>0</code> refers to the first result in the set. Setting both <code>start</code> and <code>end</code> to <code>com.liferay.portal.kernel.dao.orm.QueryUtil#ALL_POS</code> will return the full result set. If <code>orderByComparator</code> is specified, then the query will include the given ORDER BY logic. If <code>orderByComparator</code> is absent, then the query will include the default ORDER BY logic from <code>com.liferay.commerce.product.model.impl.CPDefinitionOptionRelModelImpl</code>.
	 * </p>
	 *
	 * @param CPDefinitionId the cp definition ID
	 * @param start the lower bound of the range of cp definition option rels
	 * @param end the upper bound of the range of cp definition option rels (not inclusive)
	 * @param orderByComparator the comparator to order the results by (optionally <code>null</code>)
	 * @return the ordered range of matching cp definition option rels
	 */
	public default java.util.List<CPDefinitionOptionRel> findByCPDefinitionId(
		long CPDefinitionId, int start, int end,
		com.liferay.portal.kernel.util.OrderByComparator<CPDefinitionOptionRel>
			orderByComparator) {

		return findByCPDefinitionId(
			CPDefinitionId, start, end, orderByComparator, true);
	}

	/**
	 * Returns all the cp definition option rels where CPOptionId = &#63;.
	 *
	 * @param CPOptionId the cp option ID
	 * @return the matching cp definition option rels
	 */
	public default java.util.List<CPDefinitionOptionRel> findByCPOptionId(
		long CPOptionId) {

		return findByCPOptionId(
			CPOptionId, com.liferay.portal.kernel.dao.orm.QueryUtil.ALL_POS,
			com.liferay.portal.kernel.dao.orm.QueryUtil.ALL_POS, null, true);
	}

	/**
	 * Returns a range of all the cp definition option rels where CPOptionId = &#63;.
	 *
	 * <p>
	 * Useful when paginating results. Returns a maximum of <code>end - start</code> instances. <code>start</code> and <code>end</code> are not primary keys, they are indexes in the result set. Thus, <code>0</code> refers to the first result in the set. Setting both <code>start</code> and <code>end</code> to <code>com.liferay.portal.kernel.dao.orm.QueryUtil#ALL_POS</code> will return the full result set. If <code>orderByComparator</code> is specified, then the query will include the given ORDER BY logic. If <code>orderByComparator</code> is absent, then the query will include the default ORDER BY logic from <code>com.liferay.commerce.product.model.impl.CPDefinitionOptionRelModelImpl</code>.
	 * </p>
	 *
	 * @param CPOptionId the cp option ID
	 * @param start the lower bound of the range of cp definition option rels
	 * @param end the upper bound of the range of cp definition option rels (not inclusive)
	 * @return the range of matching cp definition option rels
	 */
	public default java.util.List<CPDefinitionOptionRel> findByCPOptionId(
		long CPOptionId, int start, int end) {

		return findByCPOptionId(CPOptionId, start, end, null, true);
	}

	/**
	 * Returns an ordered range of all the cp definition option rels where CPOptionId = &#63;.
	 *
	 * <p>
	 * Useful when paginating results. Returns a maximum of <code>end - start</code> instances. <code>start</code> and <code>end</code> are not primary keys, they are indexes in the result set. Thus, <code>0</code> refers to the first result in the set. Setting both <code>start</code> and <code>end</code> to <code>com.liferay.portal.kernel.dao.orm.QueryUtil#ALL_POS</code> will return the full result set. If <code>orderByComparator</code> is specified, then the query will include the given ORDER BY logic. If <code>orderByComparator</code> is absent, then the query will include the default ORDER BY logic from <code>com.liferay.commerce.product.model.impl.CPDefinitionOptionRelModelImpl</code>.
	 * </p>
	 *
	 * @param CPOptionId the cp option ID
	 * @param start the lower bound of the range of cp definition option rels
	 * @param end the upper bound of the range of cp definition option rels (not inclusive)
	 * @param orderByComparator the comparator to order the results by (optionally <code>null</code>)
	 * @return the ordered range of matching cp definition option rels
	 */
	public default java.util.List<CPDefinitionOptionRel> findByCPOptionId(
		long CPOptionId, int start, int end,
		com.liferay.portal.kernel.util.OrderByComparator<CPDefinitionOptionRel>
			orderByComparator) {

		return findByCPOptionId(
			CPOptionId, start, end, orderByComparator, true);
	}

	/**
	 * Returns all the cp definition option rels where CPDefinitionId = &#63; and required = &#63;.
	 *
	 * @param CPDefinitionId the cp definition ID
	 * @param required the required
	 * @return the matching cp definition option rels
	 */
	public default java.util.List<CPDefinitionOptionRel> findByCPDI_R(
		long CPDefinitionId, boolean required) {

		return findByCPDI_R(
			CPDefinitionId, required,
			com.liferay.portal.kernel.dao.orm.QueryUtil.ALL_POS,
			com.liferay.portal.kernel.dao.orm.QueryUtil.ALL_POS, null, true);
	}

	/**
	 * Returns a range of all the cp definition option rels where CPDefinitionId = &#63; and required = &#63;.
	 *
	 * <p>
	 * Useful when paginating results. Returns a maximum of <code>end - start</code> instances. <code>start</code> and <code>end</code> are not primary keys, they are indexes in the result set. Thus, <code>0</code> refers to the first result in the set. Setting both <code>start</code> and <code>end</code> to <code>com.liferay.portal.kernel.dao.orm.QueryUtil#ALL_POS</code> will return the full result set. If <code>orderByComparator</code> is specified, then the query will include the given ORDER BY logic. If <code>orderByComparator</code> is absent, then the query will include the default ORDER BY logic from <code>com.liferay.commerce.product.model.impl.CPDefinitionOptionRelModelImpl</code>.
	 * </p>
	 *
	 * @param CPDefinitionId the cp definition ID
	 * @param required the required
	 * @param start the lower bound of the range of cp definition option rels
	 * @param end the upper bound of the range of cp definition option rels (not inclusive)
	 * @return the range of matching cp definition option rels
	 */
	public default java.util.List<CPDefinitionOptionRel> findByCPDI_R(
		long CPDefinitionId, boolean required, int start, int end) {

		return findByCPDI_R(CPDefinitionId, required, start, end, null, true);
	}

	/**
	 * Returns an ordered range of all the cp definition option rels where CPDefinitionId = &#63; and required = &#63;.
	 *
	 * <p>
	 * Useful when paginating results. Returns a maximum of <code>end - start</code> instances. <code>start</code> and <code>end</code> are not primary keys, they are indexes in the result set. Thus, <code>0</code> refers to the first result in the set. Setting both <code>start</code> and <code>end</code> to <code>com.liferay.portal.kernel.dao.orm.QueryUtil#ALL_POS</code> will return the full result set. If <code>orderByComparator</code> is specified, then the query will include the given ORDER BY logic. If <code>orderByComparator</code> is absent, then the query will include the default ORDER BY logic from <code>com.liferay.commerce.product.model.impl.CPDefinitionOptionRelModelImpl</code>.
	 * </p>
	 *
	 * @param CPDefinitionId the cp definition ID
	 * @param required the required
	 * @param start the lower bound of the range of cp definition option rels
	 * @param end the upper bound of the range of cp definition option rels (not inclusive)
	 * @param orderByComparator the comparator to order the results by (optionally <code>null</code>)
	 * @return the ordered range of matching cp definition option rels
	 */
	public default java.util.List<CPDefinitionOptionRel> findByCPDI_R(
		long CPDefinitionId, boolean required, int start, int end,
		com.liferay.portal.kernel.util.OrderByComparator<CPDefinitionOptionRel>
			orderByComparator) {

		return findByCPDI_R(
			CPDefinitionId, required, start, end, orderByComparator, true);
	}

	/**
	 * Returns all the cp definition option rels where CPDefinitionId = &#63; and skuContributor = &#63;.
	 *
	 * @param CPDefinitionId the cp definition ID
	 * @param skuContributor the sku contributor
	 * @return the matching cp definition option rels
	 */
	public default java.util.List<CPDefinitionOptionRel> findByC_SC(
		long CPDefinitionId, boolean skuContributor) {

		return findByC_SC(
			CPDefinitionId, skuContributor,
			com.liferay.portal.kernel.dao.orm.QueryUtil.ALL_POS,
			com.liferay.portal.kernel.dao.orm.QueryUtil.ALL_POS, null, true);
	}

	/**
	 * Returns a range of all the cp definition option rels where CPDefinitionId = &#63; and skuContributor = &#63;.
	 *
	 * <p>
	 * Useful when paginating results. Returns a maximum of <code>end - start</code> instances. <code>start</code> and <code>end</code> are not primary keys, they are indexes in the result set. Thus, <code>0</code> refers to the first result in the set. Setting both <code>start</code> and <code>end</code> to <code>com.liferay.portal.kernel.dao.orm.QueryUtil#ALL_POS</code> will return the full result set. If <code>orderByComparator</code> is specified, then the query will include the given ORDER BY logic. If <code>orderByComparator</code> is absent, then the query will include the default ORDER BY logic from <code>com.liferay.commerce.product.model.impl.CPDefinitionOptionRelModelImpl</code>.
	 * </p>
	 *
	 * @param CPDefinitionId the cp definition ID
	 * @param skuContributor the sku contributor
	 * @param start the lower bound of the range of cp definition option rels
	 * @param end the upper bound of the range of cp definition option rels (not inclusive)
	 * @return the range of matching cp definition option rels
	 */
	public default java.util.List<CPDefinitionOptionRel> findByC_SC(
		long CPDefinitionId, boolean skuContributor, int start, int end) {

		return findByC_SC(
			CPDefinitionId, skuContributor, start, end, null, true);
	}

	/**
	 * Returns an ordered range of all the cp definition option rels where CPDefinitionId = &#63; and skuContributor = &#63;.
	 *
	 * <p>
	 * Useful when paginating results. Returns a maximum of <code>end - start</code> instances. <code>start</code> and <code>end</code> are not primary keys, they are indexes in the result set. Thus, <code>0</code> refers to the first result in the set. Setting both <code>start</code> and <code>end</code> to <code>com.liferay.portal.kernel.dao.orm.QueryUtil#ALL_POS</code> will return the full result set. If <code>orderByComparator</code> is specified, then the query will include the given ORDER BY logic. If <code>orderByComparator</code> is absent, then the query will include the default ORDER BY logic from <code>com.liferay.commerce.product.model.impl.CPDefinitionOptionRelModelImpl</code>.
	 * </p>
	 *
	 * @param CPDefinitionId the cp definition ID
	 * @param skuContributor the sku contributor
	 * @param start the lower bound of the range of cp definition option rels
	 * @param end the upper bound of the range of cp definition option rels (not inclusive)
	 * @param orderByComparator the comparator to order the results by (optionally <code>null</code>)
	 * @return the ordered range of matching cp definition option rels
	 */
	public default java.util.List<CPDefinitionOptionRel> findByC_SC(
		long CPDefinitionId, boolean skuContributor, int start, int end,
		com.liferay.portal.kernel.util.OrderByComparator<CPDefinitionOptionRel>
			orderByComparator) {

		return findByC_SC(
			CPDefinitionId, skuContributor, start, end, orderByComparator,
			true);
	}

}
// LIFERAY-SERVICE-BUILDER-HASH:1096029891