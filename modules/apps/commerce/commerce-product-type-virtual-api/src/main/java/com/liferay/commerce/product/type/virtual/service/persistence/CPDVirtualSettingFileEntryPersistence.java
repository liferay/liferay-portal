/**
 * SPDX-FileCopyrightText: (c) 2023 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

package com.liferay.commerce.product.type.virtual.service.persistence;

import com.liferay.commerce.product.type.virtual.exception.NoSuchCPDVirtualSettingFileEntryException;
import com.liferay.commerce.product.type.virtual.model.CPDVirtualSettingFileEntry;
import com.liferay.portal.kernel.service.persistence.BasePersistence;

import org.osgi.annotation.versioning.ProviderType;

/**
 * The persistence interface for the cpd virtual setting file entry service.
 *
 * <p>
 * Caching information and settings can be found in <code>portal.properties</code>
 * </p>
 *
 * @author Marco Leo
 * @see CPDVirtualSettingFileEntryUtil
 * @generated
 */
@ProviderType
public interface CPDVirtualSettingFileEntryPersistence
	extends BasePersistence<CPDVirtualSettingFileEntry> {

	/*
	 * NOTE FOR DEVELOPERS:
	 *
	 * Never modify or reference this interface directly. Always use {@link CPDVirtualSettingFileEntryUtil} to access the cpd virtual setting file entry persistence. Modify <code>service.xml</code> and rerun ServiceBuilder to regenerate this interface.
	 */

	/**
	 * Returns an ordered range of all the cpd virtual setting file entries where uuid = &#63;.
	 *
	 * <p>
	 * Useful when paginating results. Returns a maximum of <code>end - start</code> instances. <code>start</code> and <code>end</code> are not primary keys, they are indexes in the result set. Thus, <code>0</code> refers to the first result in the set. Setting both <code>start</code> and <code>end</code> to <code>com.liferay.portal.kernel.dao.orm.QueryUtil#ALL_POS</code> will return the full result set. If <code>orderByComparator</code> is specified, then the query will include the given ORDER BY logic. If <code>orderByComparator</code> is absent, then the query will include the default ORDER BY logic from <code>com.liferay.commerce.product.type.virtual.model.impl.CPDVirtualSettingFileEntryModelImpl</code>.
	 * </p>
	 *
	 * @param uuid the uuid
	 * @param start the lower bound of the range of cpd virtual setting file entries
	 * @param end the upper bound of the range of cpd virtual setting file entries (not inclusive)
	 * @param orderByComparator the comparator to order the results by (optionally <code>null</code>)
	 * @param useFinderCache whether to use the finder cache
	 * @return the ordered range of matching cpd virtual setting file entries
	 */
	public java.util.List<CPDVirtualSettingFileEntry> findByUuid(
		String uuid, int start, int end,
		com.liferay.portal.kernel.util.OrderByComparator
			<CPDVirtualSettingFileEntry> orderByComparator,
		boolean useFinderCache);

	/**
	 * Returns the first cpd virtual setting file entry in the ordered set where uuid = &#63;.
	 *
	 * @param uuid the uuid
	 * @param orderByComparator the comparator to order the set by (optionally <code>null</code>)
	 * @return the first matching cpd virtual setting file entry
	 * @throws NoSuchCPDVirtualSettingFileEntryException if a matching cpd virtual setting file entry could not be found
	 */
	public CPDVirtualSettingFileEntry findByUuid_First(
			String uuid,
			com.liferay.portal.kernel.util.OrderByComparator
				<CPDVirtualSettingFileEntry> orderByComparator)
		throws NoSuchCPDVirtualSettingFileEntryException;

	/**
	 * Returns the first cpd virtual setting file entry in the ordered set where uuid = &#63;.
	 *
	 * @param uuid the uuid
	 * @param orderByComparator the comparator to order the set by (optionally <code>null</code>)
	 * @return the first matching cpd virtual setting file entry, or <code>null</code> if a matching cpd virtual setting file entry could not be found
	 */
	public CPDVirtualSettingFileEntry fetchByUuid_First(
		String uuid,
		com.liferay.portal.kernel.util.OrderByComparator
			<CPDVirtualSettingFileEntry> orderByComparator);

	/**
	 * Removes all the cpd virtual setting file entries where uuid = &#63; from the database.
	 *
	 * @param uuid the uuid
	 */
	public void removeByUuid(String uuid);

	/**
	 * Returns the number of cpd virtual setting file entries where uuid = &#63;.
	 *
	 * @param uuid the uuid
	 * @return the number of matching cpd virtual setting file entries
	 */
	public int countByUuid(String uuid);

	/**
	 * Returns the cpd virtual setting file entry where uuid = &#63; and groupId = &#63; or throws a <code>NoSuchCPDVirtualSettingFileEntryException</code> if it could not be found.
	 *
	 * @param uuid the uuid
	 * @param groupId the group ID
	 * @return the matching cpd virtual setting file entry
	 * @throws NoSuchCPDVirtualSettingFileEntryException if a matching cpd virtual setting file entry could not be found
	 */
	public CPDVirtualSettingFileEntry findByUUID_G(String uuid, long groupId)
		throws NoSuchCPDVirtualSettingFileEntryException;

	/**
	 * Returns the cpd virtual setting file entry where uuid = &#63; and groupId = &#63; or returns <code>null</code> if it could not be found, optionally using the finder cache.
	 *
	 * @param uuid the uuid
	 * @param groupId the group ID
	 * @param useFinderCache whether to use the finder cache
	 * @return the matching cpd virtual setting file entry, or <code>null</code> if a matching cpd virtual setting file entry could not be found
	 */
	public CPDVirtualSettingFileEntry fetchByUUID_G(
		String uuid, long groupId, boolean useFinderCache);

	/**
	 * Removes the cpd virtual setting file entry where uuid = &#63; and groupId = &#63; from the database.
	 *
	 * @param uuid the uuid
	 * @param groupId the group ID
	 * @return the cpd virtual setting file entry that was removed
	 */
	public CPDVirtualSettingFileEntry removeByUUID_G(String uuid, long groupId)
		throws NoSuchCPDVirtualSettingFileEntryException;

	/**
	 * Returns the number of cpd virtual setting file entries where uuid = &#63; and groupId = &#63;.
	 *
	 * @param uuid the uuid
	 * @param groupId the group ID
	 * @return the number of matching cpd virtual setting file entries
	 */
	public int countByUUID_G(String uuid, long groupId);

	/**
	 * Returns an ordered range of all the cpd virtual setting file entries where uuid = &#63; and companyId = &#63;.
	 *
	 * <p>
	 * Useful when paginating results. Returns a maximum of <code>end - start</code> instances. <code>start</code> and <code>end</code> are not primary keys, they are indexes in the result set. Thus, <code>0</code> refers to the first result in the set. Setting both <code>start</code> and <code>end</code> to <code>com.liferay.portal.kernel.dao.orm.QueryUtil#ALL_POS</code> will return the full result set. If <code>orderByComparator</code> is specified, then the query will include the given ORDER BY logic. If <code>orderByComparator</code> is absent, then the query will include the default ORDER BY logic from <code>com.liferay.commerce.product.type.virtual.model.impl.CPDVirtualSettingFileEntryModelImpl</code>.
	 * </p>
	 *
	 * @param uuid the uuid
	 * @param companyId the company ID
	 * @param start the lower bound of the range of cpd virtual setting file entries
	 * @param end the upper bound of the range of cpd virtual setting file entries (not inclusive)
	 * @param orderByComparator the comparator to order the results by (optionally <code>null</code>)
	 * @param useFinderCache whether to use the finder cache
	 * @return the ordered range of matching cpd virtual setting file entries
	 */
	public java.util.List<CPDVirtualSettingFileEntry> findByUuid_C(
		String uuid, long companyId, int start, int end,
		com.liferay.portal.kernel.util.OrderByComparator
			<CPDVirtualSettingFileEntry> orderByComparator,
		boolean useFinderCache);

	/**
	 * Returns the first cpd virtual setting file entry in the ordered set where uuid = &#63; and companyId = &#63;.
	 *
	 * @param uuid the uuid
	 * @param companyId the company ID
	 * @param orderByComparator the comparator to order the set by (optionally <code>null</code>)
	 * @return the first matching cpd virtual setting file entry
	 * @throws NoSuchCPDVirtualSettingFileEntryException if a matching cpd virtual setting file entry could not be found
	 */
	public CPDVirtualSettingFileEntry findByUuid_C_First(
			String uuid, long companyId,
			com.liferay.portal.kernel.util.OrderByComparator
				<CPDVirtualSettingFileEntry> orderByComparator)
		throws NoSuchCPDVirtualSettingFileEntryException;

	/**
	 * Returns the first cpd virtual setting file entry in the ordered set where uuid = &#63; and companyId = &#63;.
	 *
	 * @param uuid the uuid
	 * @param companyId the company ID
	 * @param orderByComparator the comparator to order the set by (optionally <code>null</code>)
	 * @return the first matching cpd virtual setting file entry, or <code>null</code> if a matching cpd virtual setting file entry could not be found
	 */
	public CPDVirtualSettingFileEntry fetchByUuid_C_First(
		String uuid, long companyId,
		com.liferay.portal.kernel.util.OrderByComparator
			<CPDVirtualSettingFileEntry> orderByComparator);

	/**
	 * Removes all the cpd virtual setting file entries where uuid = &#63; and companyId = &#63; from the database.
	 *
	 * @param uuid the uuid
	 * @param companyId the company ID
	 */
	public void removeByUuid_C(String uuid, long companyId);

	/**
	 * Returns the number of cpd virtual setting file entries where uuid = &#63; and companyId = &#63;.
	 *
	 * @param uuid the uuid
	 * @param companyId the company ID
	 * @return the number of matching cpd virtual setting file entries
	 */
	public int countByUuid_C(String uuid, long companyId);

	/**
	 * Returns an ordered range of all the cpd virtual setting file entries where CPDefinitionVirtualSettingId = &#63;.
	 *
	 * <p>
	 * Useful when paginating results. Returns a maximum of <code>end - start</code> instances. <code>start</code> and <code>end</code> are not primary keys, they are indexes in the result set. Thus, <code>0</code> refers to the first result in the set. Setting both <code>start</code> and <code>end</code> to <code>com.liferay.portal.kernel.dao.orm.QueryUtil#ALL_POS</code> will return the full result set. If <code>orderByComparator</code> is specified, then the query will include the given ORDER BY logic. If <code>orderByComparator</code> is absent, then the query will include the default ORDER BY logic from <code>com.liferay.commerce.product.type.virtual.model.impl.CPDVirtualSettingFileEntryModelImpl</code>.
	 * </p>
	 *
	 * @param CPDefinitionVirtualSettingId the cp definition virtual setting ID
	 * @param start the lower bound of the range of cpd virtual setting file entries
	 * @param end the upper bound of the range of cpd virtual setting file entries (not inclusive)
	 * @param orderByComparator the comparator to order the results by (optionally <code>null</code>)
	 * @param useFinderCache whether to use the finder cache
	 * @return the ordered range of matching cpd virtual setting file entries
	 */
	public java.util.List<CPDVirtualSettingFileEntry>
		findByCPDefinitionVirtualSettingId(
			long CPDefinitionVirtualSettingId, int start, int end,
			com.liferay.portal.kernel.util.OrderByComparator
				<CPDVirtualSettingFileEntry> orderByComparator,
			boolean useFinderCache);

	/**
	 * Returns the first cpd virtual setting file entry in the ordered set where CPDefinitionVirtualSettingId = &#63;.
	 *
	 * @param CPDefinitionVirtualSettingId the cp definition virtual setting ID
	 * @param orderByComparator the comparator to order the set by (optionally <code>null</code>)
	 * @return the first matching cpd virtual setting file entry
	 * @throws NoSuchCPDVirtualSettingFileEntryException if a matching cpd virtual setting file entry could not be found
	 */
	public CPDVirtualSettingFileEntry findByCPDefinitionVirtualSettingId_First(
			long CPDefinitionVirtualSettingId,
			com.liferay.portal.kernel.util.OrderByComparator
				<CPDVirtualSettingFileEntry> orderByComparator)
		throws NoSuchCPDVirtualSettingFileEntryException;

	/**
	 * Returns the first cpd virtual setting file entry in the ordered set where CPDefinitionVirtualSettingId = &#63;.
	 *
	 * @param CPDefinitionVirtualSettingId the cp definition virtual setting ID
	 * @param orderByComparator the comparator to order the set by (optionally <code>null</code>)
	 * @return the first matching cpd virtual setting file entry, or <code>null</code> if a matching cpd virtual setting file entry could not be found
	 */
	public CPDVirtualSettingFileEntry fetchByCPDefinitionVirtualSettingId_First(
		long CPDefinitionVirtualSettingId,
		com.liferay.portal.kernel.util.OrderByComparator
			<CPDVirtualSettingFileEntry> orderByComparator);

	/**
	 * Removes all the cpd virtual setting file entries where CPDefinitionVirtualSettingId = &#63; from the database.
	 *
	 * @param CPDefinitionVirtualSettingId the cp definition virtual setting ID
	 */
	public void removeByCPDefinitionVirtualSettingId(
		long CPDefinitionVirtualSettingId);

	/**
	 * Returns the number of cpd virtual setting file entries where CPDefinitionVirtualSettingId = &#63;.
	 *
	 * @param CPDefinitionVirtualSettingId the cp definition virtual setting ID
	 * @return the number of matching cpd virtual setting file entries
	 */
	public int countByCPDefinitionVirtualSettingId(
		long CPDefinitionVirtualSettingId);

	/**
	 * Creates a new cpd virtual setting file entry with the primary key. Does not add the cpd virtual setting file entry to the database.
	 *
	 * @param CPDefinitionVirtualSettingFileEntryId the primary key for the new cpd virtual setting file entry
	 * @return the new cpd virtual setting file entry
	 */
	public CPDVirtualSettingFileEntry create(
		long CPDefinitionVirtualSettingFileEntryId);

	/**
	 * Removes the cpd virtual setting file entry with the primary key from the database. Also notifies the appropriate model listeners.
	 *
	 * @param CPDefinitionVirtualSettingFileEntryId the primary key of the cpd virtual setting file entry
	 * @return the cpd virtual setting file entry that was removed
	 * @throws NoSuchCPDVirtualSettingFileEntryException if a cpd virtual setting file entry with the primary key could not be found
	 */
	public CPDVirtualSettingFileEntry remove(
			long CPDefinitionVirtualSettingFileEntryId)
		throws NoSuchCPDVirtualSettingFileEntryException;

	public CPDVirtualSettingFileEntry updateImpl(
		CPDVirtualSettingFileEntry cpdVirtualSettingFileEntry);

	/**
	 * Returns the cpd virtual setting file entry with the primary key or throws a <code>NoSuchCPDVirtualSettingFileEntryException</code> if it could not be found.
	 *
	 * @param CPDefinitionVirtualSettingFileEntryId the primary key of the cpd virtual setting file entry
	 * @return the cpd virtual setting file entry
	 * @throws NoSuchCPDVirtualSettingFileEntryException if a cpd virtual setting file entry with the primary key could not be found
	 */
	public CPDVirtualSettingFileEntry findByPrimaryKey(
			long CPDefinitionVirtualSettingFileEntryId)
		throws NoSuchCPDVirtualSettingFileEntryException;

	/**
	 * Returns the cpd virtual setting file entry with the primary key or returns <code>null</code> if it could not be found.
	 *
	 * @param CPDefinitionVirtualSettingFileEntryId the primary key of the cpd virtual setting file entry
	 * @return the cpd virtual setting file entry, or <code>null</code> if a cpd virtual setting file entry with the primary key could not be found
	 */
	public CPDVirtualSettingFileEntry fetchByPrimaryKey(
		long CPDefinitionVirtualSettingFileEntryId);

	/**
	 * Returns the cpd virtual setting file entry where uuid = &#63; and groupId = &#63; or returns <code>null</code> if it could not be found. Uses the finder cache.
	 *
	 * @param uuid the uuid
	 * @param groupId the group ID
	 * @return the matching cpd virtual setting file entry, or <code>null</code> if a matching cpd virtual setting file entry could not be found
	 */
	public default CPDVirtualSettingFileEntry fetchByUUID_G(
		String uuid, long groupId) {

		return fetchByUUID_G(uuid, groupId, true);
	}

	/**
	 * Returns all the cpd virtual setting file entries where uuid = &#63;.
	 *
	 * @param uuid the uuid
	 * @return the matching cpd virtual setting file entries
	 */
	public default java.util.List<CPDVirtualSettingFileEntry> findByUuid(
		String uuid) {

		return findByUuid(
			uuid, com.liferay.portal.kernel.dao.orm.QueryUtil.ALL_POS,
			com.liferay.portal.kernel.dao.orm.QueryUtil.ALL_POS, null, true);
	}

	/**
	 * Returns a range of all the cpd virtual setting file entries where uuid = &#63;.
	 *
	 * <p>
	 * Useful when paginating results. Returns a maximum of <code>end - start</code> instances. <code>start</code> and <code>end</code> are not primary keys, they are indexes in the result set. Thus, <code>0</code> refers to the first result in the set. Setting both <code>start</code> and <code>end</code> to <code>com.liferay.portal.kernel.dao.orm.QueryUtil#ALL_POS</code> will return the full result set. If <code>orderByComparator</code> is specified, then the query will include the given ORDER BY logic. If <code>orderByComparator</code> is absent, then the query will include the default ORDER BY logic from <code>com.liferay.commerce.product.type.virtual.model.impl.CPDVirtualSettingFileEntryModelImpl</code>.
	 * </p>
	 *
	 * @param uuid the uuid
	 * @param start the lower bound of the range of cpd virtual setting file entries
	 * @param end the upper bound of the range of cpd virtual setting file entries (not inclusive)
	 * @return the range of matching cpd virtual setting file entries
	 */
	public default java.util.List<CPDVirtualSettingFileEntry> findByUuid(
		String uuid, int start, int end) {

		return findByUuid(uuid, start, end, null, true);
	}

	/**
	 * Returns an ordered range of all the cpd virtual setting file entries where uuid = &#63;.
	 *
	 * <p>
	 * Useful when paginating results. Returns a maximum of <code>end - start</code> instances. <code>start</code> and <code>end</code> are not primary keys, they are indexes in the result set. Thus, <code>0</code> refers to the first result in the set. Setting both <code>start</code> and <code>end</code> to <code>com.liferay.portal.kernel.dao.orm.QueryUtil#ALL_POS</code> will return the full result set. If <code>orderByComparator</code> is specified, then the query will include the given ORDER BY logic. If <code>orderByComparator</code> is absent, then the query will include the default ORDER BY logic from <code>com.liferay.commerce.product.type.virtual.model.impl.CPDVirtualSettingFileEntryModelImpl</code>.
	 * </p>
	 *
	 * @param uuid the uuid
	 * @param start the lower bound of the range of cpd virtual setting file entries
	 * @param end the upper bound of the range of cpd virtual setting file entries (not inclusive)
	 * @param orderByComparator the comparator to order the results by (optionally <code>null</code>)
	 * @return the ordered range of matching cpd virtual setting file entries
	 */
	public default java.util.List<CPDVirtualSettingFileEntry> findByUuid(
		String uuid, int start, int end,
		com.liferay.portal.kernel.util.OrderByComparator
			<CPDVirtualSettingFileEntry> orderByComparator) {

		return findByUuid(uuid, start, end, orderByComparator, true);
	}

	/**
	 * Returns all the cpd virtual setting file entries where uuid = &#63; and companyId = &#63;.
	 *
	 * @param uuid the uuid
	 * @param companyId the company ID
	 * @return the matching cpd virtual setting file entries
	 */
	public default java.util.List<CPDVirtualSettingFileEntry> findByUuid_C(
		String uuid, long companyId) {

		return findByUuid_C(
			uuid, companyId,
			com.liferay.portal.kernel.dao.orm.QueryUtil.ALL_POS,
			com.liferay.portal.kernel.dao.orm.QueryUtil.ALL_POS, null, true);
	}

	/**
	 * Returns a range of all the cpd virtual setting file entries where uuid = &#63; and companyId = &#63;.
	 *
	 * <p>
	 * Useful when paginating results. Returns a maximum of <code>end - start</code> instances. <code>start</code> and <code>end</code> are not primary keys, they are indexes in the result set. Thus, <code>0</code> refers to the first result in the set. Setting both <code>start</code> and <code>end</code> to <code>com.liferay.portal.kernel.dao.orm.QueryUtil#ALL_POS</code> will return the full result set. If <code>orderByComparator</code> is specified, then the query will include the given ORDER BY logic. If <code>orderByComparator</code> is absent, then the query will include the default ORDER BY logic from <code>com.liferay.commerce.product.type.virtual.model.impl.CPDVirtualSettingFileEntryModelImpl</code>.
	 * </p>
	 *
	 * @param uuid the uuid
	 * @param companyId the company ID
	 * @param start the lower bound of the range of cpd virtual setting file entries
	 * @param end the upper bound of the range of cpd virtual setting file entries (not inclusive)
	 * @return the range of matching cpd virtual setting file entries
	 */
	public default java.util.List<CPDVirtualSettingFileEntry> findByUuid_C(
		String uuid, long companyId, int start, int end) {

		return findByUuid_C(uuid, companyId, start, end, null, true);
	}

	/**
	 * Returns an ordered range of all the cpd virtual setting file entries where uuid = &#63; and companyId = &#63;.
	 *
	 * <p>
	 * Useful when paginating results. Returns a maximum of <code>end - start</code> instances. <code>start</code> and <code>end</code> are not primary keys, they are indexes in the result set. Thus, <code>0</code> refers to the first result in the set. Setting both <code>start</code> and <code>end</code> to <code>com.liferay.portal.kernel.dao.orm.QueryUtil#ALL_POS</code> will return the full result set. If <code>orderByComparator</code> is specified, then the query will include the given ORDER BY logic. If <code>orderByComparator</code> is absent, then the query will include the default ORDER BY logic from <code>com.liferay.commerce.product.type.virtual.model.impl.CPDVirtualSettingFileEntryModelImpl</code>.
	 * </p>
	 *
	 * @param uuid the uuid
	 * @param companyId the company ID
	 * @param start the lower bound of the range of cpd virtual setting file entries
	 * @param end the upper bound of the range of cpd virtual setting file entries (not inclusive)
	 * @param orderByComparator the comparator to order the results by (optionally <code>null</code>)
	 * @return the ordered range of matching cpd virtual setting file entries
	 */
	public default java.util.List<CPDVirtualSettingFileEntry> findByUuid_C(
		String uuid, long companyId, int start, int end,
		com.liferay.portal.kernel.util.OrderByComparator
			<CPDVirtualSettingFileEntry> orderByComparator) {

		return findByUuid_C(
			uuid, companyId, start, end, orderByComparator, true);
	}

	/**
	 * Returns all the cpd virtual setting file entries where CPDefinitionVirtualSettingId = &#63;.
	 *
	 * @param CPDefinitionVirtualSettingId the cp definition virtual setting ID
	 * @return the matching cpd virtual setting file entries
	 */
	public default java.util.List<CPDVirtualSettingFileEntry>
		findByCPDefinitionVirtualSettingId(long CPDefinitionVirtualSettingId) {

		return findByCPDefinitionVirtualSettingId(
			CPDefinitionVirtualSettingId,
			com.liferay.portal.kernel.dao.orm.QueryUtil.ALL_POS,
			com.liferay.portal.kernel.dao.orm.QueryUtil.ALL_POS, null, true);
	}

	/**
	 * Returns a range of all the cpd virtual setting file entries where CPDefinitionVirtualSettingId = &#63;.
	 *
	 * <p>
	 * Useful when paginating results. Returns a maximum of <code>end - start</code> instances. <code>start</code> and <code>end</code> are not primary keys, they are indexes in the result set. Thus, <code>0</code> refers to the first result in the set. Setting both <code>start</code> and <code>end</code> to <code>com.liferay.portal.kernel.dao.orm.QueryUtil#ALL_POS</code> will return the full result set. If <code>orderByComparator</code> is specified, then the query will include the given ORDER BY logic. If <code>orderByComparator</code> is absent, then the query will include the default ORDER BY logic from <code>com.liferay.commerce.product.type.virtual.model.impl.CPDVirtualSettingFileEntryModelImpl</code>.
	 * </p>
	 *
	 * @param CPDefinitionVirtualSettingId the cp definition virtual setting ID
	 * @param start the lower bound of the range of cpd virtual setting file entries
	 * @param end the upper bound of the range of cpd virtual setting file entries (not inclusive)
	 * @return the range of matching cpd virtual setting file entries
	 */
	public default java.util.List<CPDVirtualSettingFileEntry>
		findByCPDefinitionVirtualSettingId(
			long CPDefinitionVirtualSettingId, int start, int end) {

		return findByCPDefinitionVirtualSettingId(
			CPDefinitionVirtualSettingId, start, end, null, true);
	}

	/**
	 * Returns an ordered range of all the cpd virtual setting file entries where CPDefinitionVirtualSettingId = &#63;.
	 *
	 * <p>
	 * Useful when paginating results. Returns a maximum of <code>end - start</code> instances. <code>start</code> and <code>end</code> are not primary keys, they are indexes in the result set. Thus, <code>0</code> refers to the first result in the set. Setting both <code>start</code> and <code>end</code> to <code>com.liferay.portal.kernel.dao.orm.QueryUtil#ALL_POS</code> will return the full result set. If <code>orderByComparator</code> is specified, then the query will include the given ORDER BY logic. If <code>orderByComparator</code> is absent, then the query will include the default ORDER BY logic from <code>com.liferay.commerce.product.type.virtual.model.impl.CPDVirtualSettingFileEntryModelImpl</code>.
	 * </p>
	 *
	 * @param CPDefinitionVirtualSettingId the cp definition virtual setting ID
	 * @param start the lower bound of the range of cpd virtual setting file entries
	 * @param end the upper bound of the range of cpd virtual setting file entries (not inclusive)
	 * @param orderByComparator the comparator to order the results by (optionally <code>null</code>)
	 * @return the ordered range of matching cpd virtual setting file entries
	 */
	public default java.util.List<CPDVirtualSettingFileEntry>
		findByCPDefinitionVirtualSettingId(
			long CPDefinitionVirtualSettingId, int start, int end,
			com.liferay.portal.kernel.util.OrderByComparator
				<CPDVirtualSettingFileEntry> orderByComparator) {

		return findByCPDefinitionVirtualSettingId(
			CPDefinitionVirtualSettingId, start, end, orderByComparator, true);
	}

}
// LIFERAY-SERVICE-BUILDER-HASH:-530114316