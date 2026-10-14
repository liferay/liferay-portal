/**
 * SPDX-FileCopyrightText: (c) 2000 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

package com.liferay.fragment.service.persistence;

import com.liferay.fragment.model.FragmentEntry;
import com.liferay.portal.kernel.dao.orm.DynamicQuery;
import com.liferay.portal.kernel.service.ServiceContext;
import com.liferay.portal.kernel.util.OrderByComparator;

import java.io.Serializable;

import java.util.List;
import java.util.Map;
import java.util.Set;

/**
 * The persistence utility for the fragment entry service. This utility wraps <code>com.liferay.fragment.service.persistence.impl.FragmentEntryPersistenceImpl</code> and provides direct access to the database for CRUD operations. This utility should only be used by the service layer, as it must operate within a transaction. Never access this utility in a JSP, controller, model, or other front-end class.
 *
 * <p>
 * Caching information and settings can be found in <code>portal.properties</code>
 * </p>
 *
 * @author Brian Wing Shun Chan
 * @see FragmentEntryPersistence
 * @generated
 */
public class FragmentEntryUtil {

	/*
	 * NOTE FOR DEVELOPERS:
	 *
	 * Never modify this class directly. Modify <code>service.xml</code> and rerun ServiceBuilder to regenerate this class.
	 */

	/**
	 * @see com.liferay.portal.kernel.service.persistence.BasePersistence#cacheResult(List)
	 */
	public static void cacheResult(List<FragmentEntry> fragmentEntries) {
		getPersistence().cacheResult(fragmentEntries);
	}

	/**
	 * @see com.liferay.portal.kernel.service.persistence.BasePersistence#cacheResult(com.liferay.portal.kernel.model.BaseModel)
	 */
	public static void cacheResult(FragmentEntry fragmentEntry) {
		getPersistence().cacheResult(fragmentEntry);
	}

	/**
	 * @see com.liferay.portal.kernel.service.persistence.BasePersistence#clearCache()
	 */
	public static void clearCache() {
		getPersistence().clearCache();
	}

	/**
	 * @see com.liferay.portal.kernel.service.persistence.BasePersistence#clearCache(com.liferay.portal.kernel.model.BaseModel)
	 */
	public static void clearCache(FragmentEntry fragmentEntry) {
		getPersistence().clearCache(fragmentEntry);
	}

	/**
	 * @see com.liferay.portal.kernel.service.persistence.BasePersistence#countWithDynamicQuery(DynamicQuery)
	 */
	public static long countWithDynamicQuery(DynamicQuery dynamicQuery) {
		return getPersistence().countWithDynamicQuery(dynamicQuery);
	}

	/**
	 * @see com.liferay.portal.kernel.service.persistence.BasePersistence#fetchByPrimaryKeys(Set)
	 */
	public static Map<Serializable, FragmentEntry> fetchByPrimaryKeys(
		Set<Serializable> primaryKeys) {

		return getPersistence().fetchByPrimaryKeys(primaryKeys);
	}

	/**
	 * @see com.liferay.portal.kernel.service.persistence.BasePersistence#findWithDynamicQuery(DynamicQuery)
	 */
	public static List<FragmentEntry> findWithDynamicQuery(
		DynamicQuery dynamicQuery) {

		return getPersistence().findWithDynamicQuery(dynamicQuery);
	}

	/**
	 * @see com.liferay.portal.kernel.service.persistence.BasePersistence#findWithDynamicQuery(DynamicQuery, int, int)
	 */
	public static List<FragmentEntry> findWithDynamicQuery(
		DynamicQuery dynamicQuery, int start, int end) {

		return getPersistence().findWithDynamicQuery(dynamicQuery, start, end);
	}

	/**
	 * @see com.liferay.portal.kernel.service.persistence.BasePersistence#findWithDynamicQuery(DynamicQuery, int, int, OrderByComparator)
	 */
	public static List<FragmentEntry> findWithDynamicQuery(
		DynamicQuery dynamicQuery, int start, int end,
		OrderByComparator<FragmentEntry> orderByComparator) {

		return getPersistence().findWithDynamicQuery(
			dynamicQuery, start, end, orderByComparator);
	}

	/**
	 * @see com.liferay.portal.kernel.service.persistence.BasePersistence#update(com.liferay.portal.kernel.model.BaseModel)
	 */
	public static FragmentEntry update(FragmentEntry fragmentEntry) {
		return getPersistence().update(fragmentEntry);
	}

	/**
	 * @see com.liferay.portal.kernel.service.persistence.BasePersistence#update(com.liferay.portal.kernel.model.BaseModel, ServiceContext)
	 */
	public static FragmentEntry update(
		FragmentEntry fragmentEntry, ServiceContext serviceContext) {

		return getPersistence().update(fragmentEntry, serviceContext);
	}

	/**
	 * Returns an ordered range of all the fragment entries where uuid = &#63;.
	 *
	 * <p>
	 * Useful when paginating results. Returns a maximum of <code>end - start</code> instances. <code>start</code> and <code>end</code> are not primary keys, they are indexes in the result set. Thus, <code>0</code> refers to the first result in the set. Setting both <code>start</code> and <code>end</code> to <code>com.liferay.portal.kernel.dao.orm.QueryUtil#ALL_POS</code> will return the full result set. If <code>orderByComparator</code> is specified, then the query will include the given ORDER BY logic. If <code>orderByComparator</code> is absent, then the query will include the default ORDER BY logic from <code>com.liferay.fragment.model.impl.FragmentEntryModelImpl</code>.
	 * </p>
	 *
	 * @param uuid the uuid
	 * @param start the lower bound of the range of fragment entries
	 * @param end the upper bound of the range of fragment entries (not inclusive)
	 * @param orderByComparator the comparator to order the results by (optionally <code>null</code>)
	 * @param useFinderCache whether to use the finder cache
	 * @return the ordered range of matching fragment entries
	 */
	public static List<FragmentEntry> findByUuid(
		String uuid, int start, int end,
		OrderByComparator<FragmentEntry> orderByComparator,
		boolean useFinderCache) {

		return getPersistence().findByUuid(
			uuid, start, end, orderByComparator, useFinderCache);
	}

	/**
	 * Returns the first fragment entry in the ordered set where uuid = &#63;.
	 *
	 * @param uuid the uuid
	 * @param orderByComparator the comparator to order the set by (optionally <code>null</code>)
	 * @return the first matching fragment entry
	 * @throws NoSuchEntryException if a matching fragment entry could not be found
	 */
	public static FragmentEntry findByUuid_First(
			String uuid, OrderByComparator<FragmentEntry> orderByComparator)
		throws com.liferay.fragment.exception.NoSuchEntryException {

		return getPersistence().findByUuid_First(uuid, orderByComparator);
	}

	/**
	 * Returns the first fragment entry in the ordered set where uuid = &#63;.
	 *
	 * @param uuid the uuid
	 * @param orderByComparator the comparator to order the set by (optionally <code>null</code>)
	 * @return the first matching fragment entry, or <code>null</code> if a matching fragment entry could not be found
	 */
	public static FragmentEntry fetchByUuid_First(
		String uuid, OrderByComparator<FragmentEntry> orderByComparator) {

		return getPersistence().fetchByUuid_First(uuid, orderByComparator);
	}

	/**
	 * Removes all the fragment entries where uuid = &#63; from the database.
	 *
	 * @param uuid the uuid
	 */
	public static void removeByUuid(String uuid) {
		getPersistence().removeByUuid(uuid);
	}

	/**
	 * Returns the number of fragment entries where uuid = &#63;.
	 *
	 * @param uuid the uuid
	 * @return the number of matching fragment entries
	 */
	public static int countByUuid(String uuid) {
		return getPersistence().countByUuid(uuid);
	}

	/**
	 * Returns an ordered range of all the fragment entries where uuid = &#63; and head = &#63;.
	 *
	 * <p>
	 * Useful when paginating results. Returns a maximum of <code>end - start</code> instances. <code>start</code> and <code>end</code> are not primary keys, they are indexes in the result set. Thus, <code>0</code> refers to the first result in the set. Setting both <code>start</code> and <code>end</code> to <code>com.liferay.portal.kernel.dao.orm.QueryUtil#ALL_POS</code> will return the full result set. If <code>orderByComparator</code> is specified, then the query will include the given ORDER BY logic. If <code>orderByComparator</code> is absent, then the query will include the default ORDER BY logic from <code>com.liferay.fragment.model.impl.FragmentEntryModelImpl</code>.
	 * </p>
	 *
	 * @param uuid the uuid
	 * @param head the head
	 * @param start the lower bound of the range of fragment entries
	 * @param end the upper bound of the range of fragment entries (not inclusive)
	 * @param orderByComparator the comparator to order the results by (optionally <code>null</code>)
	 * @param useFinderCache whether to use the finder cache
	 * @return the ordered range of matching fragment entries
	 */
	public static List<FragmentEntry> findByUuid_Head(
		String uuid, boolean head, int start, int end,
		OrderByComparator<FragmentEntry> orderByComparator,
		boolean useFinderCache) {

		return getPersistence().findByUuid_Head(
			uuid, head, start, end, orderByComparator, useFinderCache);
	}

	/**
	 * Returns the first fragment entry in the ordered set where uuid = &#63; and head = &#63;.
	 *
	 * @param uuid the uuid
	 * @param head the head
	 * @param orderByComparator the comparator to order the set by (optionally <code>null</code>)
	 * @return the first matching fragment entry
	 * @throws NoSuchEntryException if a matching fragment entry could not be found
	 */
	public static FragmentEntry findByUuid_Head_First(
			String uuid, boolean head,
			OrderByComparator<FragmentEntry> orderByComparator)
		throws com.liferay.fragment.exception.NoSuchEntryException {

		return getPersistence().findByUuid_Head_First(
			uuid, head, orderByComparator);
	}

	/**
	 * Returns the first fragment entry in the ordered set where uuid = &#63; and head = &#63;.
	 *
	 * @param uuid the uuid
	 * @param head the head
	 * @param orderByComparator the comparator to order the set by (optionally <code>null</code>)
	 * @return the first matching fragment entry, or <code>null</code> if a matching fragment entry could not be found
	 */
	public static FragmentEntry fetchByUuid_Head_First(
		String uuid, boolean head,
		OrderByComparator<FragmentEntry> orderByComparator) {

		return getPersistence().fetchByUuid_Head_First(
			uuid, head, orderByComparator);
	}

	/**
	 * Removes all the fragment entries where uuid = &#63; and head = &#63; from the database.
	 *
	 * @param uuid the uuid
	 * @param head the head
	 */
	public static void removeByUuid_Head(String uuid, boolean head) {
		getPersistence().removeByUuid_Head(uuid, head);
	}

	/**
	 * Returns the number of fragment entries where uuid = &#63; and head = &#63;.
	 *
	 * @param uuid the uuid
	 * @param head the head
	 * @return the number of matching fragment entries
	 */
	public static int countByUuid_Head(String uuid, boolean head) {
		return getPersistence().countByUuid_Head(uuid, head);
	}

	/**
	 * Returns an ordered range of all the fragment entries where uuid = &#63; and groupId = &#63;.
	 *
	 * <p>
	 * Useful when paginating results. Returns a maximum of <code>end - start</code> instances. <code>start</code> and <code>end</code> are not primary keys, they are indexes in the result set. Thus, <code>0</code> refers to the first result in the set. Setting both <code>start</code> and <code>end</code> to <code>com.liferay.portal.kernel.dao.orm.QueryUtil#ALL_POS</code> will return the full result set. If <code>orderByComparator</code> is specified, then the query will include the given ORDER BY logic. If <code>orderByComparator</code> is absent, then the query will include the default ORDER BY logic from <code>com.liferay.fragment.model.impl.FragmentEntryModelImpl</code>.
	 * </p>
	 *
	 * @param uuid the uuid
	 * @param groupId the group ID
	 * @param start the lower bound of the range of fragment entries
	 * @param end the upper bound of the range of fragment entries (not inclusive)
	 * @param orderByComparator the comparator to order the results by (optionally <code>null</code>)
	 * @param useFinderCache whether to use the finder cache
	 * @return the ordered range of matching fragment entries
	 */
	public static List<FragmentEntry> findByUUID_G(
		String uuid, long groupId, int start, int end,
		OrderByComparator<FragmentEntry> orderByComparator,
		boolean useFinderCache) {

		return getPersistence().findByUUID_G(
			uuid, groupId, start, end, orderByComparator, useFinderCache);
	}

	/**
	 * Returns the first fragment entry in the ordered set where uuid = &#63; and groupId = &#63;.
	 *
	 * @param uuid the uuid
	 * @param groupId the group ID
	 * @param orderByComparator the comparator to order the set by (optionally <code>null</code>)
	 * @return the first matching fragment entry
	 * @throws NoSuchEntryException if a matching fragment entry could not be found
	 */
	public static FragmentEntry findByUUID_G_First(
			String uuid, long groupId,
			OrderByComparator<FragmentEntry> orderByComparator)
		throws com.liferay.fragment.exception.NoSuchEntryException {

		return getPersistence().findByUUID_G_First(
			uuid, groupId, orderByComparator);
	}

	/**
	 * Returns the first fragment entry in the ordered set where uuid = &#63; and groupId = &#63;.
	 *
	 * @param uuid the uuid
	 * @param groupId the group ID
	 * @param orderByComparator the comparator to order the set by (optionally <code>null</code>)
	 * @return the first matching fragment entry, or <code>null</code> if a matching fragment entry could not be found
	 */
	public static FragmentEntry fetchByUUID_G_First(
		String uuid, long groupId,
		OrderByComparator<FragmentEntry> orderByComparator) {

		return getPersistence().fetchByUUID_G_First(
			uuid, groupId, orderByComparator);
	}

	/**
	 * Removes all the fragment entries where uuid = &#63; and groupId = &#63; from the database.
	 *
	 * @param uuid the uuid
	 * @param groupId the group ID
	 */
	public static void removeByUUID_G(String uuid, long groupId) {
		getPersistence().removeByUUID_G(uuid, groupId);
	}

	/**
	 * Returns the number of fragment entries where uuid = &#63; and groupId = &#63;.
	 *
	 * @param uuid the uuid
	 * @param groupId the group ID
	 * @return the number of matching fragment entries
	 */
	public static int countByUUID_G(String uuid, long groupId) {
		return getPersistence().countByUUID_G(uuid, groupId);
	}

	/**
	 * Returns the fragment entry where uuid = &#63; and groupId = &#63; and head = &#63; or throws a <code>NoSuchEntryException</code> if it could not be found.
	 *
	 * @param uuid the uuid
	 * @param groupId the group ID
	 * @param head the head
	 * @return the matching fragment entry
	 * @throws NoSuchEntryException if a matching fragment entry could not be found
	 */
	public static FragmentEntry findByUUID_G_Head(
			String uuid, long groupId, boolean head)
		throws com.liferay.fragment.exception.NoSuchEntryException {

		return getPersistence().findByUUID_G_Head(uuid, groupId, head);
	}

	/**
	 * Returns the fragment entry where uuid = &#63; and groupId = &#63; and head = &#63; or returns <code>null</code> if it could not be found, optionally using the finder cache.
	 *
	 * @param uuid the uuid
	 * @param groupId the group ID
	 * @param head the head
	 * @param useFinderCache whether to use the finder cache
	 * @return the matching fragment entry, or <code>null</code> if a matching fragment entry could not be found
	 */
	public static FragmentEntry fetchByUUID_G_Head(
		String uuid, long groupId, boolean head, boolean useFinderCache) {

		return getPersistence().fetchByUUID_G_Head(
			uuid, groupId, head, useFinderCache);
	}

	/**
	 * Removes the fragment entry where uuid = &#63; and groupId = &#63; and head = &#63; from the database.
	 *
	 * @param uuid the uuid
	 * @param groupId the group ID
	 * @param head the head
	 * @return the fragment entry that was removed
	 */
	public static FragmentEntry removeByUUID_G_Head(
			String uuid, long groupId, boolean head)
		throws com.liferay.fragment.exception.NoSuchEntryException {

		return getPersistence().removeByUUID_G_Head(uuid, groupId, head);
	}

	/**
	 * Returns the number of fragment entries where uuid = &#63; and groupId = &#63; and head = &#63;.
	 *
	 * @param uuid the uuid
	 * @param groupId the group ID
	 * @param head the head
	 * @return the number of matching fragment entries
	 */
	public static int countByUUID_G_Head(
		String uuid, long groupId, boolean head) {

		return getPersistence().countByUUID_G_Head(uuid, groupId, head);
	}

	/**
	 * Returns an ordered range of all the fragment entries where uuid = &#63; and companyId = &#63;.
	 *
	 * <p>
	 * Useful when paginating results. Returns a maximum of <code>end - start</code> instances. <code>start</code> and <code>end</code> are not primary keys, they are indexes in the result set. Thus, <code>0</code> refers to the first result in the set. Setting both <code>start</code> and <code>end</code> to <code>com.liferay.portal.kernel.dao.orm.QueryUtil#ALL_POS</code> will return the full result set. If <code>orderByComparator</code> is specified, then the query will include the given ORDER BY logic. If <code>orderByComparator</code> is absent, then the query will include the default ORDER BY logic from <code>com.liferay.fragment.model.impl.FragmentEntryModelImpl</code>.
	 * </p>
	 *
	 * @param uuid the uuid
	 * @param companyId the company ID
	 * @param start the lower bound of the range of fragment entries
	 * @param end the upper bound of the range of fragment entries (not inclusive)
	 * @param orderByComparator the comparator to order the results by (optionally <code>null</code>)
	 * @param useFinderCache whether to use the finder cache
	 * @return the ordered range of matching fragment entries
	 */
	public static List<FragmentEntry> findByUuid_C(
		String uuid, long companyId, int start, int end,
		OrderByComparator<FragmentEntry> orderByComparator,
		boolean useFinderCache) {

		return getPersistence().findByUuid_C(
			uuid, companyId, start, end, orderByComparator, useFinderCache);
	}

	/**
	 * Returns the first fragment entry in the ordered set where uuid = &#63; and companyId = &#63;.
	 *
	 * @param uuid the uuid
	 * @param companyId the company ID
	 * @param orderByComparator the comparator to order the set by (optionally <code>null</code>)
	 * @return the first matching fragment entry
	 * @throws NoSuchEntryException if a matching fragment entry could not be found
	 */
	public static FragmentEntry findByUuid_C_First(
			String uuid, long companyId,
			OrderByComparator<FragmentEntry> orderByComparator)
		throws com.liferay.fragment.exception.NoSuchEntryException {

		return getPersistence().findByUuid_C_First(
			uuid, companyId, orderByComparator);
	}

	/**
	 * Returns the first fragment entry in the ordered set where uuid = &#63; and companyId = &#63;.
	 *
	 * @param uuid the uuid
	 * @param companyId the company ID
	 * @param orderByComparator the comparator to order the set by (optionally <code>null</code>)
	 * @return the first matching fragment entry, or <code>null</code> if a matching fragment entry could not be found
	 */
	public static FragmentEntry fetchByUuid_C_First(
		String uuid, long companyId,
		OrderByComparator<FragmentEntry> orderByComparator) {

		return getPersistence().fetchByUuid_C_First(
			uuid, companyId, orderByComparator);
	}

	/**
	 * Removes all the fragment entries where uuid = &#63; and companyId = &#63; from the database.
	 *
	 * @param uuid the uuid
	 * @param companyId the company ID
	 */
	public static void removeByUuid_C(String uuid, long companyId) {
		getPersistence().removeByUuid_C(uuid, companyId);
	}

	/**
	 * Returns the number of fragment entries where uuid = &#63; and companyId = &#63;.
	 *
	 * @param uuid the uuid
	 * @param companyId the company ID
	 * @return the number of matching fragment entries
	 */
	public static int countByUuid_C(String uuid, long companyId) {
		return getPersistence().countByUuid_C(uuid, companyId);
	}

	/**
	 * Returns an ordered range of all the fragment entries where uuid = &#63; and companyId = &#63; and head = &#63;.
	 *
	 * <p>
	 * Useful when paginating results. Returns a maximum of <code>end - start</code> instances. <code>start</code> and <code>end</code> are not primary keys, they are indexes in the result set. Thus, <code>0</code> refers to the first result in the set. Setting both <code>start</code> and <code>end</code> to <code>com.liferay.portal.kernel.dao.orm.QueryUtil#ALL_POS</code> will return the full result set. If <code>orderByComparator</code> is specified, then the query will include the given ORDER BY logic. If <code>orderByComparator</code> is absent, then the query will include the default ORDER BY logic from <code>com.liferay.fragment.model.impl.FragmentEntryModelImpl</code>.
	 * </p>
	 *
	 * @param uuid the uuid
	 * @param companyId the company ID
	 * @param head the head
	 * @param start the lower bound of the range of fragment entries
	 * @param end the upper bound of the range of fragment entries (not inclusive)
	 * @param orderByComparator the comparator to order the results by (optionally <code>null</code>)
	 * @param useFinderCache whether to use the finder cache
	 * @return the ordered range of matching fragment entries
	 */
	public static List<FragmentEntry> findByUuid_C_Head(
		String uuid, long companyId, boolean head, int start, int end,
		OrderByComparator<FragmentEntry> orderByComparator,
		boolean useFinderCache) {

		return getPersistence().findByUuid_C_Head(
			uuid, companyId, head, start, end, orderByComparator,
			useFinderCache);
	}

	/**
	 * Returns the first fragment entry in the ordered set where uuid = &#63; and companyId = &#63; and head = &#63;.
	 *
	 * @param uuid the uuid
	 * @param companyId the company ID
	 * @param head the head
	 * @param orderByComparator the comparator to order the set by (optionally <code>null</code>)
	 * @return the first matching fragment entry
	 * @throws NoSuchEntryException if a matching fragment entry could not be found
	 */
	public static FragmentEntry findByUuid_C_Head_First(
			String uuid, long companyId, boolean head,
			OrderByComparator<FragmentEntry> orderByComparator)
		throws com.liferay.fragment.exception.NoSuchEntryException {

		return getPersistence().findByUuid_C_Head_First(
			uuid, companyId, head, orderByComparator);
	}

	/**
	 * Returns the first fragment entry in the ordered set where uuid = &#63; and companyId = &#63; and head = &#63;.
	 *
	 * @param uuid the uuid
	 * @param companyId the company ID
	 * @param head the head
	 * @param orderByComparator the comparator to order the set by (optionally <code>null</code>)
	 * @return the first matching fragment entry, or <code>null</code> if a matching fragment entry could not be found
	 */
	public static FragmentEntry fetchByUuid_C_Head_First(
		String uuid, long companyId, boolean head,
		OrderByComparator<FragmentEntry> orderByComparator) {

		return getPersistence().fetchByUuid_C_Head_First(
			uuid, companyId, head, orderByComparator);
	}

	/**
	 * Removes all the fragment entries where uuid = &#63; and companyId = &#63; and head = &#63; from the database.
	 *
	 * @param uuid the uuid
	 * @param companyId the company ID
	 * @param head the head
	 */
	public static void removeByUuid_C_Head(
		String uuid, long companyId, boolean head) {

		getPersistence().removeByUuid_C_Head(uuid, companyId, head);
	}

	/**
	 * Returns the number of fragment entries where uuid = &#63; and companyId = &#63; and head = &#63;.
	 *
	 * @param uuid the uuid
	 * @param companyId the company ID
	 * @param head the head
	 * @return the number of matching fragment entries
	 */
	public static int countByUuid_C_Head(
		String uuid, long companyId, boolean head) {

		return getPersistence().countByUuid_C_Head(uuid, companyId, head);
	}

	/**
	 * Returns an ordered range of all the fragment entries where fragmentCollectionId = &#63;.
	 *
	 * <p>
	 * Useful when paginating results. Returns a maximum of <code>end - start</code> instances. <code>start</code> and <code>end</code> are not primary keys, they are indexes in the result set. Thus, <code>0</code> refers to the first result in the set. Setting both <code>start</code> and <code>end</code> to <code>com.liferay.portal.kernel.dao.orm.QueryUtil#ALL_POS</code> will return the full result set. If <code>orderByComparator</code> is specified, then the query will include the given ORDER BY logic. If <code>orderByComparator</code> is absent, then the query will include the default ORDER BY logic from <code>com.liferay.fragment.model.impl.FragmentEntryModelImpl</code>.
	 * </p>
	 *
	 * @param fragmentCollectionId the fragment collection ID
	 * @param start the lower bound of the range of fragment entries
	 * @param end the upper bound of the range of fragment entries (not inclusive)
	 * @param orderByComparator the comparator to order the results by (optionally <code>null</code>)
	 * @param useFinderCache whether to use the finder cache
	 * @return the ordered range of matching fragment entries
	 */
	public static List<FragmentEntry> findByFragmentCollectionId(
		long fragmentCollectionId, int start, int end,
		OrderByComparator<FragmentEntry> orderByComparator,
		boolean useFinderCache) {

		return getPersistence().findByFragmentCollectionId(
			fragmentCollectionId, start, end, orderByComparator,
			useFinderCache);
	}

	/**
	 * Returns the first fragment entry in the ordered set where fragmentCollectionId = &#63;.
	 *
	 * @param fragmentCollectionId the fragment collection ID
	 * @param orderByComparator the comparator to order the set by (optionally <code>null</code>)
	 * @return the first matching fragment entry
	 * @throws NoSuchEntryException if a matching fragment entry could not be found
	 */
	public static FragmentEntry findByFragmentCollectionId_First(
			long fragmentCollectionId,
			OrderByComparator<FragmentEntry> orderByComparator)
		throws com.liferay.fragment.exception.NoSuchEntryException {

		return getPersistence().findByFragmentCollectionId_First(
			fragmentCollectionId, orderByComparator);
	}

	/**
	 * Returns the first fragment entry in the ordered set where fragmentCollectionId = &#63;.
	 *
	 * @param fragmentCollectionId the fragment collection ID
	 * @param orderByComparator the comparator to order the set by (optionally <code>null</code>)
	 * @return the first matching fragment entry, or <code>null</code> if a matching fragment entry could not be found
	 */
	public static FragmentEntry fetchByFragmentCollectionId_First(
		long fragmentCollectionId,
		OrderByComparator<FragmentEntry> orderByComparator) {

		return getPersistence().fetchByFragmentCollectionId_First(
			fragmentCollectionId, orderByComparator);
	}

	/**
	 * Removes all the fragment entries where fragmentCollectionId = &#63; from the database.
	 *
	 * @param fragmentCollectionId the fragment collection ID
	 */
	public static void removeByFragmentCollectionId(long fragmentCollectionId) {
		getPersistence().removeByFragmentCollectionId(fragmentCollectionId);
	}

	/**
	 * Returns the number of fragment entries where fragmentCollectionId = &#63;.
	 *
	 * @param fragmentCollectionId the fragment collection ID
	 * @return the number of matching fragment entries
	 */
	public static int countByFragmentCollectionId(long fragmentCollectionId) {
		return getPersistence().countByFragmentCollectionId(
			fragmentCollectionId);
	}

	/**
	 * Returns an ordered range of all the fragment entries where fragmentCollectionId = &#63; and head = &#63;.
	 *
	 * <p>
	 * Useful when paginating results. Returns a maximum of <code>end - start</code> instances. <code>start</code> and <code>end</code> are not primary keys, they are indexes in the result set. Thus, <code>0</code> refers to the first result in the set. Setting both <code>start</code> and <code>end</code> to <code>com.liferay.portal.kernel.dao.orm.QueryUtil#ALL_POS</code> will return the full result set. If <code>orderByComparator</code> is specified, then the query will include the given ORDER BY logic. If <code>orderByComparator</code> is absent, then the query will include the default ORDER BY logic from <code>com.liferay.fragment.model.impl.FragmentEntryModelImpl</code>.
	 * </p>
	 *
	 * @param fragmentCollectionId the fragment collection ID
	 * @param head the head
	 * @param start the lower bound of the range of fragment entries
	 * @param end the upper bound of the range of fragment entries (not inclusive)
	 * @param orderByComparator the comparator to order the results by (optionally <code>null</code>)
	 * @param useFinderCache whether to use the finder cache
	 * @return the ordered range of matching fragment entries
	 */
	public static List<FragmentEntry> findByFragmentCollectionId_Head(
		long fragmentCollectionId, boolean head, int start, int end,
		OrderByComparator<FragmentEntry> orderByComparator,
		boolean useFinderCache) {

		return getPersistence().findByFragmentCollectionId_Head(
			fragmentCollectionId, head, start, end, orderByComparator,
			useFinderCache);
	}

	/**
	 * Returns the first fragment entry in the ordered set where fragmentCollectionId = &#63; and head = &#63;.
	 *
	 * @param fragmentCollectionId the fragment collection ID
	 * @param head the head
	 * @param orderByComparator the comparator to order the set by (optionally <code>null</code>)
	 * @return the first matching fragment entry
	 * @throws NoSuchEntryException if a matching fragment entry could not be found
	 */
	public static FragmentEntry findByFragmentCollectionId_Head_First(
			long fragmentCollectionId, boolean head,
			OrderByComparator<FragmentEntry> orderByComparator)
		throws com.liferay.fragment.exception.NoSuchEntryException {

		return getPersistence().findByFragmentCollectionId_Head_First(
			fragmentCollectionId, head, orderByComparator);
	}

	/**
	 * Returns the first fragment entry in the ordered set where fragmentCollectionId = &#63; and head = &#63;.
	 *
	 * @param fragmentCollectionId the fragment collection ID
	 * @param head the head
	 * @param orderByComparator the comparator to order the set by (optionally <code>null</code>)
	 * @return the first matching fragment entry, or <code>null</code> if a matching fragment entry could not be found
	 */
	public static FragmentEntry fetchByFragmentCollectionId_Head_First(
		long fragmentCollectionId, boolean head,
		OrderByComparator<FragmentEntry> orderByComparator) {

		return getPersistence().fetchByFragmentCollectionId_Head_First(
			fragmentCollectionId, head, orderByComparator);
	}

	/**
	 * Removes all the fragment entries where fragmentCollectionId = &#63; and head = &#63; from the database.
	 *
	 * @param fragmentCollectionId the fragment collection ID
	 * @param head the head
	 */
	public static void removeByFragmentCollectionId_Head(
		long fragmentCollectionId, boolean head) {

		getPersistence().removeByFragmentCollectionId_Head(
			fragmentCollectionId, head);
	}

	/**
	 * Returns the number of fragment entries where fragmentCollectionId = &#63; and head = &#63;.
	 *
	 * @param fragmentCollectionId the fragment collection ID
	 * @param head the head
	 * @return the number of matching fragment entries
	 */
	public static int countByFragmentCollectionId_Head(
		long fragmentCollectionId, boolean head) {

		return getPersistence().countByFragmentCollectionId_Head(
			fragmentCollectionId, head);
	}

	/**
	 * Returns an ordered range of all the fragment entries where type = &#63;.
	 *
	 * <p>
	 * Useful when paginating results. Returns a maximum of <code>end - start</code> instances. <code>start</code> and <code>end</code> are not primary keys, they are indexes in the result set. Thus, <code>0</code> refers to the first result in the set. Setting both <code>start</code> and <code>end</code> to <code>com.liferay.portal.kernel.dao.orm.QueryUtil#ALL_POS</code> will return the full result set. If <code>orderByComparator</code> is specified, then the query will include the given ORDER BY logic. If <code>orderByComparator</code> is absent, then the query will include the default ORDER BY logic from <code>com.liferay.fragment.model.impl.FragmentEntryModelImpl</code>.
	 * </p>
	 *
	 * @param type the type
	 * @param start the lower bound of the range of fragment entries
	 * @param end the upper bound of the range of fragment entries (not inclusive)
	 * @param orderByComparator the comparator to order the results by (optionally <code>null</code>)
	 * @param useFinderCache whether to use the finder cache
	 * @return the ordered range of matching fragment entries
	 */
	public static List<FragmentEntry> findByType(
		int type, int start, int end,
		OrderByComparator<FragmentEntry> orderByComparator,
		boolean useFinderCache) {

		return getPersistence().findByType(
			type, start, end, orderByComparator, useFinderCache);
	}

	/**
	 * Returns the first fragment entry in the ordered set where type = &#63;.
	 *
	 * @param type the type
	 * @param orderByComparator the comparator to order the set by (optionally <code>null</code>)
	 * @return the first matching fragment entry
	 * @throws NoSuchEntryException if a matching fragment entry could not be found
	 */
	public static FragmentEntry findByType_First(
			int type, OrderByComparator<FragmentEntry> orderByComparator)
		throws com.liferay.fragment.exception.NoSuchEntryException {

		return getPersistence().findByType_First(type, orderByComparator);
	}

	/**
	 * Returns the first fragment entry in the ordered set where type = &#63;.
	 *
	 * @param type the type
	 * @param orderByComparator the comparator to order the set by (optionally <code>null</code>)
	 * @return the first matching fragment entry, or <code>null</code> if a matching fragment entry could not be found
	 */
	public static FragmentEntry fetchByType_First(
		int type, OrderByComparator<FragmentEntry> orderByComparator) {

		return getPersistence().fetchByType_First(type, orderByComparator);
	}

	/**
	 * Removes all the fragment entries where type = &#63; from the database.
	 *
	 * @param type the type
	 */
	public static void removeByType(int type) {
		getPersistence().removeByType(type);
	}

	/**
	 * Returns the number of fragment entries where type = &#63;.
	 *
	 * @param type the type
	 * @return the number of matching fragment entries
	 */
	public static int countByType(int type) {
		return getPersistence().countByType(type);
	}

	/**
	 * Returns an ordered range of all the fragment entries where type = &#63; and head = &#63;.
	 *
	 * <p>
	 * Useful when paginating results. Returns a maximum of <code>end - start</code> instances. <code>start</code> and <code>end</code> are not primary keys, they are indexes in the result set. Thus, <code>0</code> refers to the first result in the set. Setting both <code>start</code> and <code>end</code> to <code>com.liferay.portal.kernel.dao.orm.QueryUtil#ALL_POS</code> will return the full result set. If <code>orderByComparator</code> is specified, then the query will include the given ORDER BY logic. If <code>orderByComparator</code> is absent, then the query will include the default ORDER BY logic from <code>com.liferay.fragment.model.impl.FragmentEntryModelImpl</code>.
	 * </p>
	 *
	 * @param type the type
	 * @param head the head
	 * @param start the lower bound of the range of fragment entries
	 * @param end the upper bound of the range of fragment entries (not inclusive)
	 * @param orderByComparator the comparator to order the results by (optionally <code>null</code>)
	 * @param useFinderCache whether to use the finder cache
	 * @return the ordered range of matching fragment entries
	 */
	public static List<FragmentEntry> findByType_Head(
		int type, boolean head, int start, int end,
		OrderByComparator<FragmentEntry> orderByComparator,
		boolean useFinderCache) {

		return getPersistence().findByType_Head(
			type, head, start, end, orderByComparator, useFinderCache);
	}

	/**
	 * Returns the first fragment entry in the ordered set where type = &#63; and head = &#63;.
	 *
	 * @param type the type
	 * @param head the head
	 * @param orderByComparator the comparator to order the set by (optionally <code>null</code>)
	 * @return the first matching fragment entry
	 * @throws NoSuchEntryException if a matching fragment entry could not be found
	 */
	public static FragmentEntry findByType_Head_First(
			int type, boolean head,
			OrderByComparator<FragmentEntry> orderByComparator)
		throws com.liferay.fragment.exception.NoSuchEntryException {

		return getPersistence().findByType_Head_First(
			type, head, orderByComparator);
	}

	/**
	 * Returns the first fragment entry in the ordered set where type = &#63; and head = &#63;.
	 *
	 * @param type the type
	 * @param head the head
	 * @param orderByComparator the comparator to order the set by (optionally <code>null</code>)
	 * @return the first matching fragment entry, or <code>null</code> if a matching fragment entry could not be found
	 */
	public static FragmentEntry fetchByType_Head_First(
		int type, boolean head,
		OrderByComparator<FragmentEntry> orderByComparator) {

		return getPersistence().fetchByType_Head_First(
			type, head, orderByComparator);
	}

	/**
	 * Removes all the fragment entries where type = &#63; and head = &#63; from the database.
	 *
	 * @param type the type
	 * @param head the head
	 */
	public static void removeByType_Head(int type, boolean head) {
		getPersistence().removeByType_Head(type, head);
	}

	/**
	 * Returns the number of fragment entries where type = &#63; and head = &#63;.
	 *
	 * @param type the type
	 * @param head the head
	 * @return the number of matching fragment entries
	 */
	public static int countByType_Head(int type, boolean head) {
		return getPersistence().countByType_Head(type, head);
	}

	/**
	 * Returns an ordered range of all the fragment entries where groupId = &#63; and fragmentCollectionId = &#63;.
	 *
	 * <p>
	 * Useful when paginating results. Returns a maximum of <code>end - start</code> instances. <code>start</code> and <code>end</code> are not primary keys, they are indexes in the result set. Thus, <code>0</code> refers to the first result in the set. Setting both <code>start</code> and <code>end</code> to <code>com.liferay.portal.kernel.dao.orm.QueryUtil#ALL_POS</code> will return the full result set. If <code>orderByComparator</code> is specified, then the query will include the given ORDER BY logic. If <code>orderByComparator</code> is absent, then the query will include the default ORDER BY logic from <code>com.liferay.fragment.model.impl.FragmentEntryModelImpl</code>.
	 * </p>
	 *
	 * @param groupId the group ID
	 * @param fragmentCollectionId the fragment collection ID
	 * @param start the lower bound of the range of fragment entries
	 * @param end the upper bound of the range of fragment entries (not inclusive)
	 * @param orderByComparator the comparator to order the results by (optionally <code>null</code>)
	 * @param useFinderCache whether to use the finder cache
	 * @return the ordered range of matching fragment entries
	 */
	public static List<FragmentEntry> findByG_FCI(
		long groupId, long fragmentCollectionId, int start, int end,
		OrderByComparator<FragmentEntry> orderByComparator,
		boolean useFinderCache) {

		return getPersistence().findByG_FCI(
			groupId, fragmentCollectionId, start, end, orderByComparator,
			useFinderCache);
	}

	/**
	 * Returns the first fragment entry in the ordered set where groupId = &#63; and fragmentCollectionId = &#63;.
	 *
	 * @param groupId the group ID
	 * @param fragmentCollectionId the fragment collection ID
	 * @param orderByComparator the comparator to order the set by (optionally <code>null</code>)
	 * @return the first matching fragment entry
	 * @throws NoSuchEntryException if a matching fragment entry could not be found
	 */
	public static FragmentEntry findByG_FCI_First(
			long groupId, long fragmentCollectionId,
			OrderByComparator<FragmentEntry> orderByComparator)
		throws com.liferay.fragment.exception.NoSuchEntryException {

		return getPersistence().findByG_FCI_First(
			groupId, fragmentCollectionId, orderByComparator);
	}

	/**
	 * Returns the first fragment entry in the ordered set where groupId = &#63; and fragmentCollectionId = &#63;.
	 *
	 * @param groupId the group ID
	 * @param fragmentCollectionId the fragment collection ID
	 * @param orderByComparator the comparator to order the set by (optionally <code>null</code>)
	 * @return the first matching fragment entry, or <code>null</code> if a matching fragment entry could not be found
	 */
	public static FragmentEntry fetchByG_FCI_First(
		long groupId, long fragmentCollectionId,
		OrderByComparator<FragmentEntry> orderByComparator) {

		return getPersistence().fetchByG_FCI_First(
			groupId, fragmentCollectionId, orderByComparator);
	}

	/**
	 * Removes all the fragment entries where groupId = &#63; and fragmentCollectionId = &#63; from the database.
	 *
	 * @param groupId the group ID
	 * @param fragmentCollectionId the fragment collection ID
	 */
	public static void removeByG_FCI(long groupId, long fragmentCollectionId) {
		getPersistence().removeByG_FCI(groupId, fragmentCollectionId);
	}

	/**
	 * Returns the number of fragment entries where groupId = &#63; and fragmentCollectionId = &#63;.
	 *
	 * @param groupId the group ID
	 * @param fragmentCollectionId the fragment collection ID
	 * @return the number of matching fragment entries
	 */
	public static int countByG_FCI(long groupId, long fragmentCollectionId) {
		return getPersistence().countByG_FCI(groupId, fragmentCollectionId);
	}

	/**
	 * Returns an ordered range of all the fragment entries where groupId = &#63; and fragmentCollectionId = &#63; and head = &#63;.
	 *
	 * <p>
	 * Useful when paginating results. Returns a maximum of <code>end - start</code> instances. <code>start</code> and <code>end</code> are not primary keys, they are indexes in the result set. Thus, <code>0</code> refers to the first result in the set. Setting both <code>start</code> and <code>end</code> to <code>com.liferay.portal.kernel.dao.orm.QueryUtil#ALL_POS</code> will return the full result set. If <code>orderByComparator</code> is specified, then the query will include the given ORDER BY logic. If <code>orderByComparator</code> is absent, then the query will include the default ORDER BY logic from <code>com.liferay.fragment.model.impl.FragmentEntryModelImpl</code>.
	 * </p>
	 *
	 * @param groupId the group ID
	 * @param fragmentCollectionId the fragment collection ID
	 * @param head the head
	 * @param start the lower bound of the range of fragment entries
	 * @param end the upper bound of the range of fragment entries (not inclusive)
	 * @param orderByComparator the comparator to order the results by (optionally <code>null</code>)
	 * @param useFinderCache whether to use the finder cache
	 * @return the ordered range of matching fragment entries
	 */
	public static List<FragmentEntry> findByG_FCI_Head(
		long groupId, long fragmentCollectionId, boolean head, int start,
		int end, OrderByComparator<FragmentEntry> orderByComparator,
		boolean useFinderCache) {

		return getPersistence().findByG_FCI_Head(
			groupId, fragmentCollectionId, head, start, end, orderByComparator,
			useFinderCache);
	}

	/**
	 * Returns the first fragment entry in the ordered set where groupId = &#63; and fragmentCollectionId = &#63; and head = &#63;.
	 *
	 * @param groupId the group ID
	 * @param fragmentCollectionId the fragment collection ID
	 * @param head the head
	 * @param orderByComparator the comparator to order the set by (optionally <code>null</code>)
	 * @return the first matching fragment entry
	 * @throws NoSuchEntryException if a matching fragment entry could not be found
	 */
	public static FragmentEntry findByG_FCI_Head_First(
			long groupId, long fragmentCollectionId, boolean head,
			OrderByComparator<FragmentEntry> orderByComparator)
		throws com.liferay.fragment.exception.NoSuchEntryException {

		return getPersistence().findByG_FCI_Head_First(
			groupId, fragmentCollectionId, head, orderByComparator);
	}

	/**
	 * Returns the first fragment entry in the ordered set where groupId = &#63; and fragmentCollectionId = &#63; and head = &#63;.
	 *
	 * @param groupId the group ID
	 * @param fragmentCollectionId the fragment collection ID
	 * @param head the head
	 * @param orderByComparator the comparator to order the set by (optionally <code>null</code>)
	 * @return the first matching fragment entry, or <code>null</code> if a matching fragment entry could not be found
	 */
	public static FragmentEntry fetchByG_FCI_Head_First(
		long groupId, long fragmentCollectionId, boolean head,
		OrderByComparator<FragmentEntry> orderByComparator) {

		return getPersistence().fetchByG_FCI_Head_First(
			groupId, fragmentCollectionId, head, orderByComparator);
	}

	/**
	 * Removes all the fragment entries where groupId = &#63; and fragmentCollectionId = &#63; and head = &#63; from the database.
	 *
	 * @param groupId the group ID
	 * @param fragmentCollectionId the fragment collection ID
	 * @param head the head
	 */
	public static void removeByG_FCI_Head(
		long groupId, long fragmentCollectionId, boolean head) {

		getPersistence().removeByG_FCI_Head(
			groupId, fragmentCollectionId, head);
	}

	/**
	 * Returns the number of fragment entries where groupId = &#63; and fragmentCollectionId = &#63; and head = &#63;.
	 *
	 * @param groupId the group ID
	 * @param fragmentCollectionId the fragment collection ID
	 * @param head the head
	 * @return the number of matching fragment entries
	 */
	public static int countByG_FCI_Head(
		long groupId, long fragmentCollectionId, boolean head) {

		return getPersistence().countByG_FCI_Head(
			groupId, fragmentCollectionId, head);
	}

	/**
	 * Returns an ordered range of all the fragment entries where groupId = &#63; and fragmentEntryKey = &#63;.
	 *
	 * <p>
	 * Useful when paginating results. Returns a maximum of <code>end - start</code> instances. <code>start</code> and <code>end</code> are not primary keys, they are indexes in the result set. Thus, <code>0</code> refers to the first result in the set. Setting both <code>start</code> and <code>end</code> to <code>com.liferay.portal.kernel.dao.orm.QueryUtil#ALL_POS</code> will return the full result set. If <code>orderByComparator</code> is specified, then the query will include the given ORDER BY logic. If <code>orderByComparator</code> is absent, then the query will include the default ORDER BY logic from <code>com.liferay.fragment.model.impl.FragmentEntryModelImpl</code>.
	 * </p>
	 *
	 * @param groupId the group ID
	 * @param fragmentEntryKey the fragment entry key
	 * @param start the lower bound of the range of fragment entries
	 * @param end the upper bound of the range of fragment entries (not inclusive)
	 * @param orderByComparator the comparator to order the results by (optionally <code>null</code>)
	 * @param useFinderCache whether to use the finder cache
	 * @return the ordered range of matching fragment entries
	 */
	public static List<FragmentEntry> findByG_FEK(
		long groupId, String fragmentEntryKey, int start, int end,
		OrderByComparator<FragmentEntry> orderByComparator,
		boolean useFinderCache) {

		return getPersistence().findByG_FEK(
			groupId, fragmentEntryKey, start, end, orderByComparator,
			useFinderCache);
	}

	/**
	 * Returns the first fragment entry in the ordered set where groupId = &#63; and fragmentEntryKey = &#63;.
	 *
	 * @param groupId the group ID
	 * @param fragmentEntryKey the fragment entry key
	 * @param orderByComparator the comparator to order the set by (optionally <code>null</code>)
	 * @return the first matching fragment entry
	 * @throws NoSuchEntryException if a matching fragment entry could not be found
	 */
	public static FragmentEntry findByG_FEK_First(
			long groupId, String fragmentEntryKey,
			OrderByComparator<FragmentEntry> orderByComparator)
		throws com.liferay.fragment.exception.NoSuchEntryException {

		return getPersistence().findByG_FEK_First(
			groupId, fragmentEntryKey, orderByComparator);
	}

	/**
	 * Returns the first fragment entry in the ordered set where groupId = &#63; and fragmentEntryKey = &#63;.
	 *
	 * @param groupId the group ID
	 * @param fragmentEntryKey the fragment entry key
	 * @param orderByComparator the comparator to order the set by (optionally <code>null</code>)
	 * @return the first matching fragment entry, or <code>null</code> if a matching fragment entry could not be found
	 */
	public static FragmentEntry fetchByG_FEK_First(
		long groupId, String fragmentEntryKey,
		OrderByComparator<FragmentEntry> orderByComparator) {

		return getPersistence().fetchByG_FEK_First(
			groupId, fragmentEntryKey, orderByComparator);
	}

	/**
	 * Removes all the fragment entries where groupId = &#63; and fragmentEntryKey = &#63; from the database.
	 *
	 * @param groupId the group ID
	 * @param fragmentEntryKey the fragment entry key
	 */
	public static void removeByG_FEK(long groupId, String fragmentEntryKey) {
		getPersistence().removeByG_FEK(groupId, fragmentEntryKey);
	}

	/**
	 * Returns the number of fragment entries where groupId = &#63; and fragmentEntryKey = &#63;.
	 *
	 * @param groupId the group ID
	 * @param fragmentEntryKey the fragment entry key
	 * @return the number of matching fragment entries
	 */
	public static int countByG_FEK(long groupId, String fragmentEntryKey) {
		return getPersistence().countByG_FEK(groupId, fragmentEntryKey);
	}

	/**
	 * Returns the fragment entry where groupId = &#63; and fragmentEntryKey = &#63; and head = &#63; or throws a <code>NoSuchEntryException</code> if it could not be found.
	 *
	 * @param groupId the group ID
	 * @param fragmentEntryKey the fragment entry key
	 * @param head the head
	 * @return the matching fragment entry
	 * @throws NoSuchEntryException if a matching fragment entry could not be found
	 */
	public static FragmentEntry findByG_FEK_Head(
			long groupId, String fragmentEntryKey, boolean head)
		throws com.liferay.fragment.exception.NoSuchEntryException {

		return getPersistence().findByG_FEK_Head(
			groupId, fragmentEntryKey, head);
	}

	/**
	 * Returns the fragment entry where groupId = &#63; and fragmentEntryKey = &#63; and head = &#63; or returns <code>null</code> if it could not be found, optionally using the finder cache.
	 *
	 * @param groupId the group ID
	 * @param fragmentEntryKey the fragment entry key
	 * @param head the head
	 * @param useFinderCache whether to use the finder cache
	 * @return the matching fragment entry, or <code>null</code> if a matching fragment entry could not be found
	 */
	public static FragmentEntry fetchByG_FEK_Head(
		long groupId, String fragmentEntryKey, boolean head,
		boolean useFinderCache) {

		return getPersistence().fetchByG_FEK_Head(
			groupId, fragmentEntryKey, head, useFinderCache);
	}

	/**
	 * Removes the fragment entry where groupId = &#63; and fragmentEntryKey = &#63; and head = &#63; from the database.
	 *
	 * @param groupId the group ID
	 * @param fragmentEntryKey the fragment entry key
	 * @param head the head
	 * @return the fragment entry that was removed
	 */
	public static FragmentEntry removeByG_FEK_Head(
			long groupId, String fragmentEntryKey, boolean head)
		throws com.liferay.fragment.exception.NoSuchEntryException {

		return getPersistence().removeByG_FEK_Head(
			groupId, fragmentEntryKey, head);
	}

	/**
	 * Returns the number of fragment entries where groupId = &#63; and fragmentEntryKey = &#63; and head = &#63;.
	 *
	 * @param groupId the group ID
	 * @param fragmentEntryKey the fragment entry key
	 * @param head the head
	 * @return the number of matching fragment entries
	 */
	public static int countByG_FEK_Head(
		long groupId, String fragmentEntryKey, boolean head) {

		return getPersistence().countByG_FEK_Head(
			groupId, fragmentEntryKey, head);
	}

	/**
	 * Returns all the fragment entries where groupId = &#63; and fragmentCollectionId = &#63; and name LIKE &#63;.
	 *
	 * @param groupId the group ID
	 * @param fragmentCollectionId the fragment collection ID
	 * @param name the name
	 * @return the matching fragment entries
	 */
	public static List<FragmentEntry> findByG_FCI_LikeN(
		long groupId, long fragmentCollectionId, String name) {

		return getPersistence().findByG_FCI_LikeN(
			groupId, fragmentCollectionId, name);
	}

	/**
	 * Returns a range of all the fragment entries where groupId = &#63; and fragmentCollectionId = &#63; and name LIKE &#63;.
	 *
	 * <p>
	 * Useful when paginating results. Returns a maximum of <code>end - start</code> instances. <code>start</code> and <code>end</code> are not primary keys, they are indexes in the result set. Thus, <code>0</code> refers to the first result in the set. Setting both <code>start</code> and <code>end</code> to <code>com.liferay.portal.kernel.dao.orm.QueryUtil#ALL_POS</code> will return the full result set. If <code>orderByComparator</code> is specified, then the query will include the given ORDER BY logic. If <code>orderByComparator</code> is absent, then the query will include the default ORDER BY logic from <code>com.liferay.fragment.model.impl.FragmentEntryModelImpl</code>.
	 * </p>
	 *
	 * @param groupId the group ID
	 * @param fragmentCollectionId the fragment collection ID
	 * @param name the name
	 * @param start the lower bound of the range of fragment entries
	 * @param end the upper bound of the range of fragment entries (not inclusive)
	 * @return the range of matching fragment entries
	 */
	public static List<FragmentEntry> findByG_FCI_LikeN(
		long groupId, long fragmentCollectionId, String name, int start,
		int end) {

		return getPersistence().findByG_FCI_LikeN(
			groupId, fragmentCollectionId, name, start, end);
	}

	/**
	 * Returns an ordered range of all the fragment entries where groupId = &#63; and fragmentCollectionId = &#63; and name LIKE &#63;.
	 *
	 * <p>
	 * Useful when paginating results. Returns a maximum of <code>end - start</code> instances. <code>start</code> and <code>end</code> are not primary keys, they are indexes in the result set. Thus, <code>0</code> refers to the first result in the set. Setting both <code>start</code> and <code>end</code> to <code>com.liferay.portal.kernel.dao.orm.QueryUtil#ALL_POS</code> will return the full result set. If <code>orderByComparator</code> is specified, then the query will include the given ORDER BY logic. If <code>orderByComparator</code> is absent, then the query will include the default ORDER BY logic from <code>com.liferay.fragment.model.impl.FragmentEntryModelImpl</code>.
	 * </p>
	 *
	 * @param groupId the group ID
	 * @param fragmentCollectionId the fragment collection ID
	 * @param name the name
	 * @param start the lower bound of the range of fragment entries
	 * @param end the upper bound of the range of fragment entries (not inclusive)
	 * @param orderByComparator the comparator to order the results by (optionally <code>null</code>)
	 * @return the ordered range of matching fragment entries
	 */
	public static List<FragmentEntry> findByG_FCI_LikeN(
		long groupId, long fragmentCollectionId, String name, int start,
		int end, OrderByComparator<FragmentEntry> orderByComparator) {

		return getPersistence().findByG_FCI_LikeN(
			groupId, fragmentCollectionId, name, start, end, orderByComparator);
	}

	/**
	 * Returns an ordered range of all the fragment entries where groupId = &#63; and fragmentCollectionId = &#63; and name LIKE &#63;.
	 *
	 * <p>
	 * Useful when paginating results. Returns a maximum of <code>end - start</code> instances. <code>start</code> and <code>end</code> are not primary keys, they are indexes in the result set. Thus, <code>0</code> refers to the first result in the set. Setting both <code>start</code> and <code>end</code> to <code>com.liferay.portal.kernel.dao.orm.QueryUtil#ALL_POS</code> will return the full result set. If <code>orderByComparator</code> is specified, then the query will include the given ORDER BY logic. If <code>orderByComparator</code> is absent, then the query will include the default ORDER BY logic from <code>com.liferay.fragment.model.impl.FragmentEntryModelImpl</code>.
	 * </p>
	 *
	 * @param groupId the group ID
	 * @param fragmentCollectionId the fragment collection ID
	 * @param name the name
	 * @param start the lower bound of the range of fragment entries
	 * @param end the upper bound of the range of fragment entries (not inclusive)
	 * @param orderByComparator the comparator to order the results by (optionally <code>null</code>)
	 * @param useFinderCache whether to use the finder cache
	 * @return the ordered range of matching fragment entries
	 */
	public static List<FragmentEntry> findByG_FCI_LikeN(
		long groupId, long fragmentCollectionId, String name, int start,
		int end, OrderByComparator<FragmentEntry> orderByComparator,
		boolean useFinderCache) {

		return getPersistence().findByG_FCI_LikeN(
			groupId, fragmentCollectionId, name, start, end, orderByComparator,
			useFinderCache);
	}

	/**
	 * Returns the first fragment entry in the ordered set where groupId = &#63; and fragmentCollectionId = &#63; and name LIKE &#63;.
	 *
	 * @param groupId the group ID
	 * @param fragmentCollectionId the fragment collection ID
	 * @param name the name
	 * @param orderByComparator the comparator to order the set by (optionally <code>null</code>)
	 * @return the first matching fragment entry
	 * @throws NoSuchEntryException if a matching fragment entry could not be found
	 */
	public static FragmentEntry findByG_FCI_LikeN_First(
			long groupId, long fragmentCollectionId, String name,
			OrderByComparator<FragmentEntry> orderByComparator)
		throws com.liferay.fragment.exception.NoSuchEntryException {

		return getPersistence().findByG_FCI_LikeN_First(
			groupId, fragmentCollectionId, name, orderByComparator);
	}

	/**
	 * Returns the first fragment entry in the ordered set where groupId = &#63; and fragmentCollectionId = &#63; and name LIKE &#63;.
	 *
	 * @param groupId the group ID
	 * @param fragmentCollectionId the fragment collection ID
	 * @param name the name
	 * @param orderByComparator the comparator to order the set by (optionally <code>null</code>)
	 * @return the first matching fragment entry, or <code>null</code> if a matching fragment entry could not be found
	 */
	public static FragmentEntry fetchByG_FCI_LikeN_First(
		long groupId, long fragmentCollectionId, String name,
		OrderByComparator<FragmentEntry> orderByComparator) {

		return getPersistence().fetchByG_FCI_LikeN_First(
			groupId, fragmentCollectionId, name, orderByComparator);
	}

	/**
	 * Removes all the fragment entries where groupId = &#63; and fragmentCollectionId = &#63; and name LIKE &#63; from the database.
	 *
	 * @param groupId the group ID
	 * @param fragmentCollectionId the fragment collection ID
	 * @param name the name
	 */
	public static void removeByG_FCI_LikeN(
		long groupId, long fragmentCollectionId, String name) {

		getPersistence().removeByG_FCI_LikeN(
			groupId, fragmentCollectionId, name);
	}

	/**
	 * Returns the number of fragment entries where groupId = &#63; and fragmentCollectionId = &#63; and name LIKE &#63;.
	 *
	 * @param groupId the group ID
	 * @param fragmentCollectionId the fragment collection ID
	 * @param name the name
	 * @return the number of matching fragment entries
	 */
	public static int countByG_FCI_LikeN(
		long groupId, long fragmentCollectionId, String name) {

		return getPersistence().countByG_FCI_LikeN(
			groupId, fragmentCollectionId, name);
	}

	/**
	 * Returns all the fragment entries where groupId = &#63; and fragmentCollectionId = &#63; and name LIKE &#63; and head = &#63;.
	 *
	 * @param groupId the group ID
	 * @param fragmentCollectionId the fragment collection ID
	 * @param name the name
	 * @param head the head
	 * @return the matching fragment entries
	 */
	public static List<FragmentEntry> findByG_FCI_LikeN_Head(
		long groupId, long fragmentCollectionId, String name, boolean head) {

		return getPersistence().findByG_FCI_LikeN_Head(
			groupId, fragmentCollectionId, name, head);
	}

	/**
	 * Returns a range of all the fragment entries where groupId = &#63; and fragmentCollectionId = &#63; and name LIKE &#63; and head = &#63;.
	 *
	 * <p>
	 * Useful when paginating results. Returns a maximum of <code>end - start</code> instances. <code>start</code> and <code>end</code> are not primary keys, they are indexes in the result set. Thus, <code>0</code> refers to the first result in the set. Setting both <code>start</code> and <code>end</code> to <code>com.liferay.portal.kernel.dao.orm.QueryUtil#ALL_POS</code> will return the full result set. If <code>orderByComparator</code> is specified, then the query will include the given ORDER BY logic. If <code>orderByComparator</code> is absent, then the query will include the default ORDER BY logic from <code>com.liferay.fragment.model.impl.FragmentEntryModelImpl</code>.
	 * </p>
	 *
	 * @param groupId the group ID
	 * @param fragmentCollectionId the fragment collection ID
	 * @param name the name
	 * @param head the head
	 * @param start the lower bound of the range of fragment entries
	 * @param end the upper bound of the range of fragment entries (not inclusive)
	 * @return the range of matching fragment entries
	 */
	public static List<FragmentEntry> findByG_FCI_LikeN_Head(
		long groupId, long fragmentCollectionId, String name, boolean head,
		int start, int end) {

		return getPersistence().findByG_FCI_LikeN_Head(
			groupId, fragmentCollectionId, name, head, start, end);
	}

	/**
	 * Returns an ordered range of all the fragment entries where groupId = &#63; and fragmentCollectionId = &#63; and name LIKE &#63; and head = &#63;.
	 *
	 * <p>
	 * Useful when paginating results. Returns a maximum of <code>end - start</code> instances. <code>start</code> and <code>end</code> are not primary keys, they are indexes in the result set. Thus, <code>0</code> refers to the first result in the set. Setting both <code>start</code> and <code>end</code> to <code>com.liferay.portal.kernel.dao.orm.QueryUtil#ALL_POS</code> will return the full result set. If <code>orderByComparator</code> is specified, then the query will include the given ORDER BY logic. If <code>orderByComparator</code> is absent, then the query will include the default ORDER BY logic from <code>com.liferay.fragment.model.impl.FragmentEntryModelImpl</code>.
	 * </p>
	 *
	 * @param groupId the group ID
	 * @param fragmentCollectionId the fragment collection ID
	 * @param name the name
	 * @param head the head
	 * @param start the lower bound of the range of fragment entries
	 * @param end the upper bound of the range of fragment entries (not inclusive)
	 * @param orderByComparator the comparator to order the results by (optionally <code>null</code>)
	 * @return the ordered range of matching fragment entries
	 */
	public static List<FragmentEntry> findByG_FCI_LikeN_Head(
		long groupId, long fragmentCollectionId, String name, boolean head,
		int start, int end,
		OrderByComparator<FragmentEntry> orderByComparator) {

		return getPersistence().findByG_FCI_LikeN_Head(
			groupId, fragmentCollectionId, name, head, start, end,
			orderByComparator);
	}

	/**
	 * Returns an ordered range of all the fragment entries where groupId = &#63; and fragmentCollectionId = &#63; and name LIKE &#63; and head = &#63;.
	 *
	 * <p>
	 * Useful when paginating results. Returns a maximum of <code>end - start</code> instances. <code>start</code> and <code>end</code> are not primary keys, they are indexes in the result set. Thus, <code>0</code> refers to the first result in the set. Setting both <code>start</code> and <code>end</code> to <code>com.liferay.portal.kernel.dao.orm.QueryUtil#ALL_POS</code> will return the full result set. If <code>orderByComparator</code> is specified, then the query will include the given ORDER BY logic. If <code>orderByComparator</code> is absent, then the query will include the default ORDER BY logic from <code>com.liferay.fragment.model.impl.FragmentEntryModelImpl</code>.
	 * </p>
	 *
	 * @param groupId the group ID
	 * @param fragmentCollectionId the fragment collection ID
	 * @param name the name
	 * @param head the head
	 * @param start the lower bound of the range of fragment entries
	 * @param end the upper bound of the range of fragment entries (not inclusive)
	 * @param orderByComparator the comparator to order the results by (optionally <code>null</code>)
	 * @param useFinderCache whether to use the finder cache
	 * @return the ordered range of matching fragment entries
	 */
	public static List<FragmentEntry> findByG_FCI_LikeN_Head(
		long groupId, long fragmentCollectionId, String name, boolean head,
		int start, int end, OrderByComparator<FragmentEntry> orderByComparator,
		boolean useFinderCache) {

		return getPersistence().findByG_FCI_LikeN_Head(
			groupId, fragmentCollectionId, name, head, start, end,
			orderByComparator, useFinderCache);
	}

	/**
	 * Returns the first fragment entry in the ordered set where groupId = &#63; and fragmentCollectionId = &#63; and name LIKE &#63; and head = &#63;.
	 *
	 * @param groupId the group ID
	 * @param fragmentCollectionId the fragment collection ID
	 * @param name the name
	 * @param head the head
	 * @param orderByComparator the comparator to order the set by (optionally <code>null</code>)
	 * @return the first matching fragment entry
	 * @throws NoSuchEntryException if a matching fragment entry could not be found
	 */
	public static FragmentEntry findByG_FCI_LikeN_Head_First(
			long groupId, long fragmentCollectionId, String name, boolean head,
			OrderByComparator<FragmentEntry> orderByComparator)
		throws com.liferay.fragment.exception.NoSuchEntryException {

		return getPersistence().findByG_FCI_LikeN_Head_First(
			groupId, fragmentCollectionId, name, head, orderByComparator);
	}

	/**
	 * Returns the first fragment entry in the ordered set where groupId = &#63; and fragmentCollectionId = &#63; and name LIKE &#63; and head = &#63;.
	 *
	 * @param groupId the group ID
	 * @param fragmentCollectionId the fragment collection ID
	 * @param name the name
	 * @param head the head
	 * @param orderByComparator the comparator to order the set by (optionally <code>null</code>)
	 * @return the first matching fragment entry, or <code>null</code> if a matching fragment entry could not be found
	 */
	public static FragmentEntry fetchByG_FCI_LikeN_Head_First(
		long groupId, long fragmentCollectionId, String name, boolean head,
		OrderByComparator<FragmentEntry> orderByComparator) {

		return getPersistence().fetchByG_FCI_LikeN_Head_First(
			groupId, fragmentCollectionId, name, head, orderByComparator);
	}

	/**
	 * Removes all the fragment entries where groupId = &#63; and fragmentCollectionId = &#63; and name LIKE &#63; and head = &#63; from the database.
	 *
	 * @param groupId the group ID
	 * @param fragmentCollectionId the fragment collection ID
	 * @param name the name
	 * @param head the head
	 */
	public static void removeByG_FCI_LikeN_Head(
		long groupId, long fragmentCollectionId, String name, boolean head) {

		getPersistence().removeByG_FCI_LikeN_Head(
			groupId, fragmentCollectionId, name, head);
	}

	/**
	 * Returns the number of fragment entries where groupId = &#63; and fragmentCollectionId = &#63; and name LIKE &#63; and head = &#63;.
	 *
	 * @param groupId the group ID
	 * @param fragmentCollectionId the fragment collection ID
	 * @param name the name
	 * @param head the head
	 * @return the number of matching fragment entries
	 */
	public static int countByG_FCI_LikeN_Head(
		long groupId, long fragmentCollectionId, String name, boolean head) {

		return getPersistence().countByG_FCI_LikeN_Head(
			groupId, fragmentCollectionId, name, head);
	}

	/**
	 * Returns an ordered range of all the fragment entries where groupId = &#63; and fragmentCollectionId = &#63; and type = &#63;.
	 *
	 * <p>
	 * Useful when paginating results. Returns a maximum of <code>end - start</code> instances. <code>start</code> and <code>end</code> are not primary keys, they are indexes in the result set. Thus, <code>0</code> refers to the first result in the set. Setting both <code>start</code> and <code>end</code> to <code>com.liferay.portal.kernel.dao.orm.QueryUtil#ALL_POS</code> will return the full result set. If <code>orderByComparator</code> is specified, then the query will include the given ORDER BY logic. If <code>orderByComparator</code> is absent, then the query will include the default ORDER BY logic from <code>com.liferay.fragment.model.impl.FragmentEntryModelImpl</code>.
	 * </p>
	 *
	 * @param groupId the group ID
	 * @param fragmentCollectionId the fragment collection ID
	 * @param type the type
	 * @param start the lower bound of the range of fragment entries
	 * @param end the upper bound of the range of fragment entries (not inclusive)
	 * @param orderByComparator the comparator to order the results by (optionally <code>null</code>)
	 * @param useFinderCache whether to use the finder cache
	 * @return the ordered range of matching fragment entries
	 */
	public static List<FragmentEntry> findByG_FCI_T(
		long groupId, long fragmentCollectionId, int type, int start, int end,
		OrderByComparator<FragmentEntry> orderByComparator,
		boolean useFinderCache) {

		return getPersistence().findByG_FCI_T(
			groupId, fragmentCollectionId, type, start, end, orderByComparator,
			useFinderCache);
	}

	/**
	 * Returns the first fragment entry in the ordered set where groupId = &#63; and fragmentCollectionId = &#63; and type = &#63;.
	 *
	 * @param groupId the group ID
	 * @param fragmentCollectionId the fragment collection ID
	 * @param type the type
	 * @param orderByComparator the comparator to order the set by (optionally <code>null</code>)
	 * @return the first matching fragment entry
	 * @throws NoSuchEntryException if a matching fragment entry could not be found
	 */
	public static FragmentEntry findByG_FCI_T_First(
			long groupId, long fragmentCollectionId, int type,
			OrderByComparator<FragmentEntry> orderByComparator)
		throws com.liferay.fragment.exception.NoSuchEntryException {

		return getPersistence().findByG_FCI_T_First(
			groupId, fragmentCollectionId, type, orderByComparator);
	}

	/**
	 * Returns the first fragment entry in the ordered set where groupId = &#63; and fragmentCollectionId = &#63; and type = &#63;.
	 *
	 * @param groupId the group ID
	 * @param fragmentCollectionId the fragment collection ID
	 * @param type the type
	 * @param orderByComparator the comparator to order the set by (optionally <code>null</code>)
	 * @return the first matching fragment entry, or <code>null</code> if a matching fragment entry could not be found
	 */
	public static FragmentEntry fetchByG_FCI_T_First(
		long groupId, long fragmentCollectionId, int type,
		OrderByComparator<FragmentEntry> orderByComparator) {

		return getPersistence().fetchByG_FCI_T_First(
			groupId, fragmentCollectionId, type, orderByComparator);
	}

	/**
	 * Removes all the fragment entries where groupId = &#63; and fragmentCollectionId = &#63; and type = &#63; from the database.
	 *
	 * @param groupId the group ID
	 * @param fragmentCollectionId the fragment collection ID
	 * @param type the type
	 */
	public static void removeByG_FCI_T(
		long groupId, long fragmentCollectionId, int type) {

		getPersistence().removeByG_FCI_T(groupId, fragmentCollectionId, type);
	}

	/**
	 * Returns the number of fragment entries where groupId = &#63; and fragmentCollectionId = &#63; and type = &#63;.
	 *
	 * @param groupId the group ID
	 * @param fragmentCollectionId the fragment collection ID
	 * @param type the type
	 * @return the number of matching fragment entries
	 */
	public static int countByG_FCI_T(
		long groupId, long fragmentCollectionId, int type) {

		return getPersistence().countByG_FCI_T(
			groupId, fragmentCollectionId, type);
	}

	/**
	 * Returns an ordered range of all the fragment entries where groupId = &#63; and fragmentCollectionId = &#63; and type = &#63; and head = &#63;.
	 *
	 * <p>
	 * Useful when paginating results. Returns a maximum of <code>end - start</code> instances. <code>start</code> and <code>end</code> are not primary keys, they are indexes in the result set. Thus, <code>0</code> refers to the first result in the set. Setting both <code>start</code> and <code>end</code> to <code>com.liferay.portal.kernel.dao.orm.QueryUtil#ALL_POS</code> will return the full result set. If <code>orderByComparator</code> is specified, then the query will include the given ORDER BY logic. If <code>orderByComparator</code> is absent, then the query will include the default ORDER BY logic from <code>com.liferay.fragment.model.impl.FragmentEntryModelImpl</code>.
	 * </p>
	 *
	 * @param groupId the group ID
	 * @param fragmentCollectionId the fragment collection ID
	 * @param type the type
	 * @param head the head
	 * @param start the lower bound of the range of fragment entries
	 * @param end the upper bound of the range of fragment entries (not inclusive)
	 * @param orderByComparator the comparator to order the results by (optionally <code>null</code>)
	 * @param useFinderCache whether to use the finder cache
	 * @return the ordered range of matching fragment entries
	 */
	public static List<FragmentEntry> findByG_FCI_T_Head(
		long groupId, long fragmentCollectionId, int type, boolean head,
		int start, int end, OrderByComparator<FragmentEntry> orderByComparator,
		boolean useFinderCache) {

		return getPersistence().findByG_FCI_T_Head(
			groupId, fragmentCollectionId, type, head, start, end,
			orderByComparator, useFinderCache);
	}

	/**
	 * Returns the first fragment entry in the ordered set where groupId = &#63; and fragmentCollectionId = &#63; and type = &#63; and head = &#63;.
	 *
	 * @param groupId the group ID
	 * @param fragmentCollectionId the fragment collection ID
	 * @param type the type
	 * @param head the head
	 * @param orderByComparator the comparator to order the set by (optionally <code>null</code>)
	 * @return the first matching fragment entry
	 * @throws NoSuchEntryException if a matching fragment entry could not be found
	 */
	public static FragmentEntry findByG_FCI_T_Head_First(
			long groupId, long fragmentCollectionId, int type, boolean head,
			OrderByComparator<FragmentEntry> orderByComparator)
		throws com.liferay.fragment.exception.NoSuchEntryException {

		return getPersistence().findByG_FCI_T_Head_First(
			groupId, fragmentCollectionId, type, head, orderByComparator);
	}

	/**
	 * Returns the first fragment entry in the ordered set where groupId = &#63; and fragmentCollectionId = &#63; and type = &#63; and head = &#63;.
	 *
	 * @param groupId the group ID
	 * @param fragmentCollectionId the fragment collection ID
	 * @param type the type
	 * @param head the head
	 * @param orderByComparator the comparator to order the set by (optionally <code>null</code>)
	 * @return the first matching fragment entry, or <code>null</code> if a matching fragment entry could not be found
	 */
	public static FragmentEntry fetchByG_FCI_T_Head_First(
		long groupId, long fragmentCollectionId, int type, boolean head,
		OrderByComparator<FragmentEntry> orderByComparator) {

		return getPersistence().fetchByG_FCI_T_Head_First(
			groupId, fragmentCollectionId, type, head, orderByComparator);
	}

	/**
	 * Removes all the fragment entries where groupId = &#63; and fragmentCollectionId = &#63; and type = &#63; and head = &#63; from the database.
	 *
	 * @param groupId the group ID
	 * @param fragmentCollectionId the fragment collection ID
	 * @param type the type
	 * @param head the head
	 */
	public static void removeByG_FCI_T_Head(
		long groupId, long fragmentCollectionId, int type, boolean head) {

		getPersistence().removeByG_FCI_T_Head(
			groupId, fragmentCollectionId, type, head);
	}

	/**
	 * Returns the number of fragment entries where groupId = &#63; and fragmentCollectionId = &#63; and type = &#63; and head = &#63;.
	 *
	 * @param groupId the group ID
	 * @param fragmentCollectionId the fragment collection ID
	 * @param type the type
	 * @param head the head
	 * @return the number of matching fragment entries
	 */
	public static int countByG_FCI_T_Head(
		long groupId, long fragmentCollectionId, int type, boolean head) {

		return getPersistence().countByG_FCI_T_Head(
			groupId, fragmentCollectionId, type, head);
	}

	/**
	 * Returns an ordered range of all the fragment entries where groupId = &#63; and fragmentCollectionId = &#63; and status = &#63;.
	 *
	 * <p>
	 * Useful when paginating results. Returns a maximum of <code>end - start</code> instances. <code>start</code> and <code>end</code> are not primary keys, they are indexes in the result set. Thus, <code>0</code> refers to the first result in the set. Setting both <code>start</code> and <code>end</code> to <code>com.liferay.portal.kernel.dao.orm.QueryUtil#ALL_POS</code> will return the full result set. If <code>orderByComparator</code> is specified, then the query will include the given ORDER BY logic. If <code>orderByComparator</code> is absent, then the query will include the default ORDER BY logic from <code>com.liferay.fragment.model.impl.FragmentEntryModelImpl</code>.
	 * </p>
	 *
	 * @param groupId the group ID
	 * @param fragmentCollectionId the fragment collection ID
	 * @param status the status
	 * @param start the lower bound of the range of fragment entries
	 * @param end the upper bound of the range of fragment entries (not inclusive)
	 * @param orderByComparator the comparator to order the results by (optionally <code>null</code>)
	 * @param useFinderCache whether to use the finder cache
	 * @return the ordered range of matching fragment entries
	 */
	public static List<FragmentEntry> findByG_FCI_S(
		long groupId, long fragmentCollectionId, int status, int start, int end,
		OrderByComparator<FragmentEntry> orderByComparator,
		boolean useFinderCache) {

		return getPersistence().findByG_FCI_S(
			groupId, fragmentCollectionId, status, start, end,
			orderByComparator, useFinderCache);
	}

	/**
	 * Returns the first fragment entry in the ordered set where groupId = &#63; and fragmentCollectionId = &#63; and status = &#63;.
	 *
	 * @param groupId the group ID
	 * @param fragmentCollectionId the fragment collection ID
	 * @param status the status
	 * @param orderByComparator the comparator to order the set by (optionally <code>null</code>)
	 * @return the first matching fragment entry
	 * @throws NoSuchEntryException if a matching fragment entry could not be found
	 */
	public static FragmentEntry findByG_FCI_S_First(
			long groupId, long fragmentCollectionId, int status,
			OrderByComparator<FragmentEntry> orderByComparator)
		throws com.liferay.fragment.exception.NoSuchEntryException {

		return getPersistence().findByG_FCI_S_First(
			groupId, fragmentCollectionId, status, orderByComparator);
	}

	/**
	 * Returns the first fragment entry in the ordered set where groupId = &#63; and fragmentCollectionId = &#63; and status = &#63;.
	 *
	 * @param groupId the group ID
	 * @param fragmentCollectionId the fragment collection ID
	 * @param status the status
	 * @param orderByComparator the comparator to order the set by (optionally <code>null</code>)
	 * @return the first matching fragment entry, or <code>null</code> if a matching fragment entry could not be found
	 */
	public static FragmentEntry fetchByG_FCI_S_First(
		long groupId, long fragmentCollectionId, int status,
		OrderByComparator<FragmentEntry> orderByComparator) {

		return getPersistence().fetchByG_FCI_S_First(
			groupId, fragmentCollectionId, status, orderByComparator);
	}

	/**
	 * Removes all the fragment entries where groupId = &#63; and fragmentCollectionId = &#63; and status = &#63; from the database.
	 *
	 * @param groupId the group ID
	 * @param fragmentCollectionId the fragment collection ID
	 * @param status the status
	 */
	public static void removeByG_FCI_S(
		long groupId, long fragmentCollectionId, int status) {

		getPersistence().removeByG_FCI_S(groupId, fragmentCollectionId, status);
	}

	/**
	 * Returns the number of fragment entries where groupId = &#63; and fragmentCollectionId = &#63; and status = &#63;.
	 *
	 * @param groupId the group ID
	 * @param fragmentCollectionId the fragment collection ID
	 * @param status the status
	 * @return the number of matching fragment entries
	 */
	public static int countByG_FCI_S(
		long groupId, long fragmentCollectionId, int status) {

		return getPersistence().countByG_FCI_S(
			groupId, fragmentCollectionId, status);
	}

	/**
	 * Returns an ordered range of all the fragment entries where groupId = &#63; and fragmentCollectionId = &#63; and status = &#63; and head = &#63;.
	 *
	 * <p>
	 * Useful when paginating results. Returns a maximum of <code>end - start</code> instances. <code>start</code> and <code>end</code> are not primary keys, they are indexes in the result set. Thus, <code>0</code> refers to the first result in the set. Setting both <code>start</code> and <code>end</code> to <code>com.liferay.portal.kernel.dao.orm.QueryUtil#ALL_POS</code> will return the full result set. If <code>orderByComparator</code> is specified, then the query will include the given ORDER BY logic. If <code>orderByComparator</code> is absent, then the query will include the default ORDER BY logic from <code>com.liferay.fragment.model.impl.FragmentEntryModelImpl</code>.
	 * </p>
	 *
	 * @param groupId the group ID
	 * @param fragmentCollectionId the fragment collection ID
	 * @param status the status
	 * @param head the head
	 * @param start the lower bound of the range of fragment entries
	 * @param end the upper bound of the range of fragment entries (not inclusive)
	 * @param orderByComparator the comparator to order the results by (optionally <code>null</code>)
	 * @param useFinderCache whether to use the finder cache
	 * @return the ordered range of matching fragment entries
	 */
	public static List<FragmentEntry> findByG_FCI_S_Head(
		long groupId, long fragmentCollectionId, int status, boolean head,
		int start, int end, OrderByComparator<FragmentEntry> orderByComparator,
		boolean useFinderCache) {

		return getPersistence().findByG_FCI_S_Head(
			groupId, fragmentCollectionId, status, head, start, end,
			orderByComparator, useFinderCache);
	}

	/**
	 * Returns the first fragment entry in the ordered set where groupId = &#63; and fragmentCollectionId = &#63; and status = &#63; and head = &#63;.
	 *
	 * @param groupId the group ID
	 * @param fragmentCollectionId the fragment collection ID
	 * @param status the status
	 * @param head the head
	 * @param orderByComparator the comparator to order the set by (optionally <code>null</code>)
	 * @return the first matching fragment entry
	 * @throws NoSuchEntryException if a matching fragment entry could not be found
	 */
	public static FragmentEntry findByG_FCI_S_Head_First(
			long groupId, long fragmentCollectionId, int status, boolean head,
			OrderByComparator<FragmentEntry> orderByComparator)
		throws com.liferay.fragment.exception.NoSuchEntryException {

		return getPersistence().findByG_FCI_S_Head_First(
			groupId, fragmentCollectionId, status, head, orderByComparator);
	}

	/**
	 * Returns the first fragment entry in the ordered set where groupId = &#63; and fragmentCollectionId = &#63; and status = &#63; and head = &#63;.
	 *
	 * @param groupId the group ID
	 * @param fragmentCollectionId the fragment collection ID
	 * @param status the status
	 * @param head the head
	 * @param orderByComparator the comparator to order the set by (optionally <code>null</code>)
	 * @return the first matching fragment entry, or <code>null</code> if a matching fragment entry could not be found
	 */
	public static FragmentEntry fetchByG_FCI_S_Head_First(
		long groupId, long fragmentCollectionId, int status, boolean head,
		OrderByComparator<FragmentEntry> orderByComparator) {

		return getPersistence().fetchByG_FCI_S_Head_First(
			groupId, fragmentCollectionId, status, head, orderByComparator);
	}

	/**
	 * Removes all the fragment entries where groupId = &#63; and fragmentCollectionId = &#63; and status = &#63; and head = &#63; from the database.
	 *
	 * @param groupId the group ID
	 * @param fragmentCollectionId the fragment collection ID
	 * @param status the status
	 * @param head the head
	 */
	public static void removeByG_FCI_S_Head(
		long groupId, long fragmentCollectionId, int status, boolean head) {

		getPersistence().removeByG_FCI_S_Head(
			groupId, fragmentCollectionId, status, head);
	}

	/**
	 * Returns the number of fragment entries where groupId = &#63; and fragmentCollectionId = &#63; and status = &#63; and head = &#63;.
	 *
	 * @param groupId the group ID
	 * @param fragmentCollectionId the fragment collection ID
	 * @param status the status
	 * @param head the head
	 * @return the number of matching fragment entries
	 */
	public static int countByG_FCI_S_Head(
		long groupId, long fragmentCollectionId, int status, boolean head) {

		return getPersistence().countByG_FCI_S_Head(
			groupId, fragmentCollectionId, status, head);
	}

	/**
	 * Returns all the fragment entries where groupId = &#63; and fragmentCollectionId = &#63; and name LIKE &#63; and status = &#63;.
	 *
	 * @param groupId the group ID
	 * @param fragmentCollectionId the fragment collection ID
	 * @param name the name
	 * @param status the status
	 * @return the matching fragment entries
	 */
	public static List<FragmentEntry> findByG_FCI_LikeN_S(
		long groupId, long fragmentCollectionId, String name, int status) {

		return getPersistence().findByG_FCI_LikeN_S(
			groupId, fragmentCollectionId, name, status);
	}

	/**
	 * Returns a range of all the fragment entries where groupId = &#63; and fragmentCollectionId = &#63; and name LIKE &#63; and status = &#63;.
	 *
	 * <p>
	 * Useful when paginating results. Returns a maximum of <code>end - start</code> instances. <code>start</code> and <code>end</code> are not primary keys, they are indexes in the result set. Thus, <code>0</code> refers to the first result in the set. Setting both <code>start</code> and <code>end</code> to <code>com.liferay.portal.kernel.dao.orm.QueryUtil#ALL_POS</code> will return the full result set. If <code>orderByComparator</code> is specified, then the query will include the given ORDER BY logic. If <code>orderByComparator</code> is absent, then the query will include the default ORDER BY logic from <code>com.liferay.fragment.model.impl.FragmentEntryModelImpl</code>.
	 * </p>
	 *
	 * @param groupId the group ID
	 * @param fragmentCollectionId the fragment collection ID
	 * @param name the name
	 * @param status the status
	 * @param start the lower bound of the range of fragment entries
	 * @param end the upper bound of the range of fragment entries (not inclusive)
	 * @return the range of matching fragment entries
	 */
	public static List<FragmentEntry> findByG_FCI_LikeN_S(
		long groupId, long fragmentCollectionId, String name, int status,
		int start, int end) {

		return getPersistence().findByG_FCI_LikeN_S(
			groupId, fragmentCollectionId, name, status, start, end);
	}

	/**
	 * Returns an ordered range of all the fragment entries where groupId = &#63; and fragmentCollectionId = &#63; and name LIKE &#63; and status = &#63;.
	 *
	 * <p>
	 * Useful when paginating results. Returns a maximum of <code>end - start</code> instances. <code>start</code> and <code>end</code> are not primary keys, they are indexes in the result set. Thus, <code>0</code> refers to the first result in the set. Setting both <code>start</code> and <code>end</code> to <code>com.liferay.portal.kernel.dao.orm.QueryUtil#ALL_POS</code> will return the full result set. If <code>orderByComparator</code> is specified, then the query will include the given ORDER BY logic. If <code>orderByComparator</code> is absent, then the query will include the default ORDER BY logic from <code>com.liferay.fragment.model.impl.FragmentEntryModelImpl</code>.
	 * </p>
	 *
	 * @param groupId the group ID
	 * @param fragmentCollectionId the fragment collection ID
	 * @param name the name
	 * @param status the status
	 * @param start the lower bound of the range of fragment entries
	 * @param end the upper bound of the range of fragment entries (not inclusive)
	 * @param orderByComparator the comparator to order the results by (optionally <code>null</code>)
	 * @return the ordered range of matching fragment entries
	 */
	public static List<FragmentEntry> findByG_FCI_LikeN_S(
		long groupId, long fragmentCollectionId, String name, int status,
		int start, int end,
		OrderByComparator<FragmentEntry> orderByComparator) {

		return getPersistence().findByG_FCI_LikeN_S(
			groupId, fragmentCollectionId, name, status, start, end,
			orderByComparator);
	}

	/**
	 * Returns an ordered range of all the fragment entries where groupId = &#63; and fragmentCollectionId = &#63; and name LIKE &#63; and status = &#63;.
	 *
	 * <p>
	 * Useful when paginating results. Returns a maximum of <code>end - start</code> instances. <code>start</code> and <code>end</code> are not primary keys, they are indexes in the result set. Thus, <code>0</code> refers to the first result in the set. Setting both <code>start</code> and <code>end</code> to <code>com.liferay.portal.kernel.dao.orm.QueryUtil#ALL_POS</code> will return the full result set. If <code>orderByComparator</code> is specified, then the query will include the given ORDER BY logic. If <code>orderByComparator</code> is absent, then the query will include the default ORDER BY logic from <code>com.liferay.fragment.model.impl.FragmentEntryModelImpl</code>.
	 * </p>
	 *
	 * @param groupId the group ID
	 * @param fragmentCollectionId the fragment collection ID
	 * @param name the name
	 * @param status the status
	 * @param start the lower bound of the range of fragment entries
	 * @param end the upper bound of the range of fragment entries (not inclusive)
	 * @param orderByComparator the comparator to order the results by (optionally <code>null</code>)
	 * @param useFinderCache whether to use the finder cache
	 * @return the ordered range of matching fragment entries
	 */
	public static List<FragmentEntry> findByG_FCI_LikeN_S(
		long groupId, long fragmentCollectionId, String name, int status,
		int start, int end, OrderByComparator<FragmentEntry> orderByComparator,
		boolean useFinderCache) {

		return getPersistence().findByG_FCI_LikeN_S(
			groupId, fragmentCollectionId, name, status, start, end,
			orderByComparator, useFinderCache);
	}

	/**
	 * Returns the first fragment entry in the ordered set where groupId = &#63; and fragmentCollectionId = &#63; and name LIKE &#63; and status = &#63;.
	 *
	 * @param groupId the group ID
	 * @param fragmentCollectionId the fragment collection ID
	 * @param name the name
	 * @param status the status
	 * @param orderByComparator the comparator to order the set by (optionally <code>null</code>)
	 * @return the first matching fragment entry
	 * @throws NoSuchEntryException if a matching fragment entry could not be found
	 */
	public static FragmentEntry findByG_FCI_LikeN_S_First(
			long groupId, long fragmentCollectionId, String name, int status,
			OrderByComparator<FragmentEntry> orderByComparator)
		throws com.liferay.fragment.exception.NoSuchEntryException {

		return getPersistence().findByG_FCI_LikeN_S_First(
			groupId, fragmentCollectionId, name, status, orderByComparator);
	}

	/**
	 * Returns the first fragment entry in the ordered set where groupId = &#63; and fragmentCollectionId = &#63; and name LIKE &#63; and status = &#63;.
	 *
	 * @param groupId the group ID
	 * @param fragmentCollectionId the fragment collection ID
	 * @param name the name
	 * @param status the status
	 * @param orderByComparator the comparator to order the set by (optionally <code>null</code>)
	 * @return the first matching fragment entry, or <code>null</code> if a matching fragment entry could not be found
	 */
	public static FragmentEntry fetchByG_FCI_LikeN_S_First(
		long groupId, long fragmentCollectionId, String name, int status,
		OrderByComparator<FragmentEntry> orderByComparator) {

		return getPersistence().fetchByG_FCI_LikeN_S_First(
			groupId, fragmentCollectionId, name, status, orderByComparator);
	}

	/**
	 * Removes all the fragment entries where groupId = &#63; and fragmentCollectionId = &#63; and name LIKE &#63; and status = &#63; from the database.
	 *
	 * @param groupId the group ID
	 * @param fragmentCollectionId the fragment collection ID
	 * @param name the name
	 * @param status the status
	 */
	public static void removeByG_FCI_LikeN_S(
		long groupId, long fragmentCollectionId, String name, int status) {

		getPersistence().removeByG_FCI_LikeN_S(
			groupId, fragmentCollectionId, name, status);
	}

	/**
	 * Returns the number of fragment entries where groupId = &#63; and fragmentCollectionId = &#63; and name LIKE &#63; and status = &#63;.
	 *
	 * @param groupId the group ID
	 * @param fragmentCollectionId the fragment collection ID
	 * @param name the name
	 * @param status the status
	 * @return the number of matching fragment entries
	 */
	public static int countByG_FCI_LikeN_S(
		long groupId, long fragmentCollectionId, String name, int status) {

		return getPersistence().countByG_FCI_LikeN_S(
			groupId, fragmentCollectionId, name, status);
	}

	/**
	 * Returns all the fragment entries where groupId = &#63; and fragmentCollectionId = &#63; and name LIKE &#63; and status = &#63; and head = &#63;.
	 *
	 * @param groupId the group ID
	 * @param fragmentCollectionId the fragment collection ID
	 * @param name the name
	 * @param status the status
	 * @param head the head
	 * @return the matching fragment entries
	 */
	public static List<FragmentEntry> findByG_FCI_LikeN_S_Head(
		long groupId, long fragmentCollectionId, String name, int status,
		boolean head) {

		return getPersistence().findByG_FCI_LikeN_S_Head(
			groupId, fragmentCollectionId, name, status, head);
	}

	/**
	 * Returns a range of all the fragment entries where groupId = &#63; and fragmentCollectionId = &#63; and name LIKE &#63; and status = &#63; and head = &#63;.
	 *
	 * <p>
	 * Useful when paginating results. Returns a maximum of <code>end - start</code> instances. <code>start</code> and <code>end</code> are not primary keys, they are indexes in the result set. Thus, <code>0</code> refers to the first result in the set. Setting both <code>start</code> and <code>end</code> to <code>com.liferay.portal.kernel.dao.orm.QueryUtil#ALL_POS</code> will return the full result set. If <code>orderByComparator</code> is specified, then the query will include the given ORDER BY logic. If <code>orderByComparator</code> is absent, then the query will include the default ORDER BY logic from <code>com.liferay.fragment.model.impl.FragmentEntryModelImpl</code>.
	 * </p>
	 *
	 * @param groupId the group ID
	 * @param fragmentCollectionId the fragment collection ID
	 * @param name the name
	 * @param status the status
	 * @param head the head
	 * @param start the lower bound of the range of fragment entries
	 * @param end the upper bound of the range of fragment entries (not inclusive)
	 * @return the range of matching fragment entries
	 */
	public static List<FragmentEntry> findByG_FCI_LikeN_S_Head(
		long groupId, long fragmentCollectionId, String name, int status,
		boolean head, int start, int end) {

		return getPersistence().findByG_FCI_LikeN_S_Head(
			groupId, fragmentCollectionId, name, status, head, start, end);
	}

	/**
	 * Returns an ordered range of all the fragment entries where groupId = &#63; and fragmentCollectionId = &#63; and name LIKE &#63; and status = &#63; and head = &#63;.
	 *
	 * <p>
	 * Useful when paginating results. Returns a maximum of <code>end - start</code> instances. <code>start</code> and <code>end</code> are not primary keys, they are indexes in the result set. Thus, <code>0</code> refers to the first result in the set. Setting both <code>start</code> and <code>end</code> to <code>com.liferay.portal.kernel.dao.orm.QueryUtil#ALL_POS</code> will return the full result set. If <code>orderByComparator</code> is specified, then the query will include the given ORDER BY logic. If <code>orderByComparator</code> is absent, then the query will include the default ORDER BY logic from <code>com.liferay.fragment.model.impl.FragmentEntryModelImpl</code>.
	 * </p>
	 *
	 * @param groupId the group ID
	 * @param fragmentCollectionId the fragment collection ID
	 * @param name the name
	 * @param status the status
	 * @param head the head
	 * @param start the lower bound of the range of fragment entries
	 * @param end the upper bound of the range of fragment entries (not inclusive)
	 * @param orderByComparator the comparator to order the results by (optionally <code>null</code>)
	 * @return the ordered range of matching fragment entries
	 */
	public static List<FragmentEntry> findByG_FCI_LikeN_S_Head(
		long groupId, long fragmentCollectionId, String name, int status,
		boolean head, int start, int end,
		OrderByComparator<FragmentEntry> orderByComparator) {

		return getPersistence().findByG_FCI_LikeN_S_Head(
			groupId, fragmentCollectionId, name, status, head, start, end,
			orderByComparator);
	}

	/**
	 * Returns an ordered range of all the fragment entries where groupId = &#63; and fragmentCollectionId = &#63; and name LIKE &#63; and status = &#63; and head = &#63;.
	 *
	 * <p>
	 * Useful when paginating results. Returns a maximum of <code>end - start</code> instances. <code>start</code> and <code>end</code> are not primary keys, they are indexes in the result set. Thus, <code>0</code> refers to the first result in the set. Setting both <code>start</code> and <code>end</code> to <code>com.liferay.portal.kernel.dao.orm.QueryUtil#ALL_POS</code> will return the full result set. If <code>orderByComparator</code> is specified, then the query will include the given ORDER BY logic. If <code>orderByComparator</code> is absent, then the query will include the default ORDER BY logic from <code>com.liferay.fragment.model.impl.FragmentEntryModelImpl</code>.
	 * </p>
	 *
	 * @param groupId the group ID
	 * @param fragmentCollectionId the fragment collection ID
	 * @param name the name
	 * @param status the status
	 * @param head the head
	 * @param start the lower bound of the range of fragment entries
	 * @param end the upper bound of the range of fragment entries (not inclusive)
	 * @param orderByComparator the comparator to order the results by (optionally <code>null</code>)
	 * @param useFinderCache whether to use the finder cache
	 * @return the ordered range of matching fragment entries
	 */
	public static List<FragmentEntry> findByG_FCI_LikeN_S_Head(
		long groupId, long fragmentCollectionId, String name, int status,
		boolean head, int start, int end,
		OrderByComparator<FragmentEntry> orderByComparator,
		boolean useFinderCache) {

		return getPersistence().findByG_FCI_LikeN_S_Head(
			groupId, fragmentCollectionId, name, status, head, start, end,
			orderByComparator, useFinderCache);
	}

	/**
	 * Returns the first fragment entry in the ordered set where groupId = &#63; and fragmentCollectionId = &#63; and name LIKE &#63; and status = &#63; and head = &#63;.
	 *
	 * @param groupId the group ID
	 * @param fragmentCollectionId the fragment collection ID
	 * @param name the name
	 * @param status the status
	 * @param head the head
	 * @param orderByComparator the comparator to order the set by (optionally <code>null</code>)
	 * @return the first matching fragment entry
	 * @throws NoSuchEntryException if a matching fragment entry could not be found
	 */
	public static FragmentEntry findByG_FCI_LikeN_S_Head_First(
			long groupId, long fragmentCollectionId, String name, int status,
			boolean head, OrderByComparator<FragmentEntry> orderByComparator)
		throws com.liferay.fragment.exception.NoSuchEntryException {

		return getPersistence().findByG_FCI_LikeN_S_Head_First(
			groupId, fragmentCollectionId, name, status, head,
			orderByComparator);
	}

	/**
	 * Returns the first fragment entry in the ordered set where groupId = &#63; and fragmentCollectionId = &#63; and name LIKE &#63; and status = &#63; and head = &#63;.
	 *
	 * @param groupId the group ID
	 * @param fragmentCollectionId the fragment collection ID
	 * @param name the name
	 * @param status the status
	 * @param head the head
	 * @param orderByComparator the comparator to order the set by (optionally <code>null</code>)
	 * @return the first matching fragment entry, or <code>null</code> if a matching fragment entry could not be found
	 */
	public static FragmentEntry fetchByG_FCI_LikeN_S_Head_First(
		long groupId, long fragmentCollectionId, String name, int status,
		boolean head, OrderByComparator<FragmentEntry> orderByComparator) {

		return getPersistence().fetchByG_FCI_LikeN_S_Head_First(
			groupId, fragmentCollectionId, name, status, head,
			orderByComparator);
	}

	/**
	 * Removes all the fragment entries where groupId = &#63; and fragmentCollectionId = &#63; and name LIKE &#63; and status = &#63; and head = &#63; from the database.
	 *
	 * @param groupId the group ID
	 * @param fragmentCollectionId the fragment collection ID
	 * @param name the name
	 * @param status the status
	 * @param head the head
	 */
	public static void removeByG_FCI_LikeN_S_Head(
		long groupId, long fragmentCollectionId, String name, int status,
		boolean head) {

		getPersistence().removeByG_FCI_LikeN_S_Head(
			groupId, fragmentCollectionId, name, status, head);
	}

	/**
	 * Returns the number of fragment entries where groupId = &#63; and fragmentCollectionId = &#63; and name LIKE &#63; and status = &#63; and head = &#63;.
	 *
	 * @param groupId the group ID
	 * @param fragmentCollectionId the fragment collection ID
	 * @param name the name
	 * @param status the status
	 * @param head the head
	 * @return the number of matching fragment entries
	 */
	public static int countByG_FCI_LikeN_S_Head(
		long groupId, long fragmentCollectionId, String name, int status,
		boolean head) {

		return getPersistence().countByG_FCI_LikeN_S_Head(
			groupId, fragmentCollectionId, name, status, head);
	}

	/**
	 * Returns an ordered range of all the fragment entries where groupId = &#63; and fragmentCollectionId = &#63; and type = &#63; and status = &#63;.
	 *
	 * <p>
	 * Useful when paginating results. Returns a maximum of <code>end - start</code> instances. <code>start</code> and <code>end</code> are not primary keys, they are indexes in the result set. Thus, <code>0</code> refers to the first result in the set. Setting both <code>start</code> and <code>end</code> to <code>com.liferay.portal.kernel.dao.orm.QueryUtil#ALL_POS</code> will return the full result set. If <code>orderByComparator</code> is specified, then the query will include the given ORDER BY logic. If <code>orderByComparator</code> is absent, then the query will include the default ORDER BY logic from <code>com.liferay.fragment.model.impl.FragmentEntryModelImpl</code>.
	 * </p>
	 *
	 * @param groupId the group ID
	 * @param fragmentCollectionId the fragment collection ID
	 * @param type the type
	 * @param status the status
	 * @param start the lower bound of the range of fragment entries
	 * @param end the upper bound of the range of fragment entries (not inclusive)
	 * @param orderByComparator the comparator to order the results by (optionally <code>null</code>)
	 * @param useFinderCache whether to use the finder cache
	 * @return the ordered range of matching fragment entries
	 */
	public static List<FragmentEntry> findByG_FCI_T_S(
		long groupId, long fragmentCollectionId, int type, int status,
		int start, int end, OrderByComparator<FragmentEntry> orderByComparator,
		boolean useFinderCache) {

		return getPersistence().findByG_FCI_T_S(
			groupId, fragmentCollectionId, type, status, start, end,
			orderByComparator, useFinderCache);
	}

	/**
	 * Returns the first fragment entry in the ordered set where groupId = &#63; and fragmentCollectionId = &#63; and type = &#63; and status = &#63;.
	 *
	 * @param groupId the group ID
	 * @param fragmentCollectionId the fragment collection ID
	 * @param type the type
	 * @param status the status
	 * @param orderByComparator the comparator to order the set by (optionally <code>null</code>)
	 * @return the first matching fragment entry
	 * @throws NoSuchEntryException if a matching fragment entry could not be found
	 */
	public static FragmentEntry findByG_FCI_T_S_First(
			long groupId, long fragmentCollectionId, int type, int status,
			OrderByComparator<FragmentEntry> orderByComparator)
		throws com.liferay.fragment.exception.NoSuchEntryException {

		return getPersistence().findByG_FCI_T_S_First(
			groupId, fragmentCollectionId, type, status, orderByComparator);
	}

	/**
	 * Returns the first fragment entry in the ordered set where groupId = &#63; and fragmentCollectionId = &#63; and type = &#63; and status = &#63;.
	 *
	 * @param groupId the group ID
	 * @param fragmentCollectionId the fragment collection ID
	 * @param type the type
	 * @param status the status
	 * @param orderByComparator the comparator to order the set by (optionally <code>null</code>)
	 * @return the first matching fragment entry, or <code>null</code> if a matching fragment entry could not be found
	 */
	public static FragmentEntry fetchByG_FCI_T_S_First(
		long groupId, long fragmentCollectionId, int type, int status,
		OrderByComparator<FragmentEntry> orderByComparator) {

		return getPersistence().fetchByG_FCI_T_S_First(
			groupId, fragmentCollectionId, type, status, orderByComparator);
	}

	/**
	 * Removes all the fragment entries where groupId = &#63; and fragmentCollectionId = &#63; and type = &#63; and status = &#63; from the database.
	 *
	 * @param groupId the group ID
	 * @param fragmentCollectionId the fragment collection ID
	 * @param type the type
	 * @param status the status
	 */
	public static void removeByG_FCI_T_S(
		long groupId, long fragmentCollectionId, int type, int status) {

		getPersistence().removeByG_FCI_T_S(
			groupId, fragmentCollectionId, type, status);
	}

	/**
	 * Returns the number of fragment entries where groupId = &#63; and fragmentCollectionId = &#63; and type = &#63; and status = &#63;.
	 *
	 * @param groupId the group ID
	 * @param fragmentCollectionId the fragment collection ID
	 * @param type the type
	 * @param status the status
	 * @return the number of matching fragment entries
	 */
	public static int countByG_FCI_T_S(
		long groupId, long fragmentCollectionId, int type, int status) {

		return getPersistence().countByG_FCI_T_S(
			groupId, fragmentCollectionId, type, status);
	}

	/**
	 * Returns an ordered range of all the fragment entries where groupId = &#63; and fragmentCollectionId = &#63; and type = &#63; and status = &#63; and head = &#63;.
	 *
	 * <p>
	 * Useful when paginating results. Returns a maximum of <code>end - start</code> instances. <code>start</code> and <code>end</code> are not primary keys, they are indexes in the result set. Thus, <code>0</code> refers to the first result in the set. Setting both <code>start</code> and <code>end</code> to <code>com.liferay.portal.kernel.dao.orm.QueryUtil#ALL_POS</code> will return the full result set. If <code>orderByComparator</code> is specified, then the query will include the given ORDER BY logic. If <code>orderByComparator</code> is absent, then the query will include the default ORDER BY logic from <code>com.liferay.fragment.model.impl.FragmentEntryModelImpl</code>.
	 * </p>
	 *
	 * @param groupId the group ID
	 * @param fragmentCollectionId the fragment collection ID
	 * @param type the type
	 * @param status the status
	 * @param head the head
	 * @param start the lower bound of the range of fragment entries
	 * @param end the upper bound of the range of fragment entries (not inclusive)
	 * @param orderByComparator the comparator to order the results by (optionally <code>null</code>)
	 * @param useFinderCache whether to use the finder cache
	 * @return the ordered range of matching fragment entries
	 */
	public static List<FragmentEntry> findByG_FCI_T_S_Head(
		long groupId, long fragmentCollectionId, int type, int status,
		boolean head, int start, int end,
		OrderByComparator<FragmentEntry> orderByComparator,
		boolean useFinderCache) {

		return getPersistence().findByG_FCI_T_S_Head(
			groupId, fragmentCollectionId, type, status, head, start, end,
			orderByComparator, useFinderCache);
	}

	/**
	 * Returns the first fragment entry in the ordered set where groupId = &#63; and fragmentCollectionId = &#63; and type = &#63; and status = &#63; and head = &#63;.
	 *
	 * @param groupId the group ID
	 * @param fragmentCollectionId the fragment collection ID
	 * @param type the type
	 * @param status the status
	 * @param head the head
	 * @param orderByComparator the comparator to order the set by (optionally <code>null</code>)
	 * @return the first matching fragment entry
	 * @throws NoSuchEntryException if a matching fragment entry could not be found
	 */
	public static FragmentEntry findByG_FCI_T_S_Head_First(
			long groupId, long fragmentCollectionId, int type, int status,
			boolean head, OrderByComparator<FragmentEntry> orderByComparator)
		throws com.liferay.fragment.exception.NoSuchEntryException {

		return getPersistence().findByG_FCI_T_S_Head_First(
			groupId, fragmentCollectionId, type, status, head,
			orderByComparator);
	}

	/**
	 * Returns the first fragment entry in the ordered set where groupId = &#63; and fragmentCollectionId = &#63; and type = &#63; and status = &#63; and head = &#63;.
	 *
	 * @param groupId the group ID
	 * @param fragmentCollectionId the fragment collection ID
	 * @param type the type
	 * @param status the status
	 * @param head the head
	 * @param orderByComparator the comparator to order the set by (optionally <code>null</code>)
	 * @return the first matching fragment entry, or <code>null</code> if a matching fragment entry could not be found
	 */
	public static FragmentEntry fetchByG_FCI_T_S_Head_First(
		long groupId, long fragmentCollectionId, int type, int status,
		boolean head, OrderByComparator<FragmentEntry> orderByComparator) {

		return getPersistence().fetchByG_FCI_T_S_Head_First(
			groupId, fragmentCollectionId, type, status, head,
			orderByComparator);
	}

	/**
	 * Removes all the fragment entries where groupId = &#63; and fragmentCollectionId = &#63; and type = &#63; and status = &#63; and head = &#63; from the database.
	 *
	 * @param groupId the group ID
	 * @param fragmentCollectionId the fragment collection ID
	 * @param type the type
	 * @param status the status
	 * @param head the head
	 */
	public static void removeByG_FCI_T_S_Head(
		long groupId, long fragmentCollectionId, int type, int status,
		boolean head) {

		getPersistence().removeByG_FCI_T_S_Head(
			groupId, fragmentCollectionId, type, status, head);
	}

	/**
	 * Returns the number of fragment entries where groupId = &#63; and fragmentCollectionId = &#63; and type = &#63; and status = &#63; and head = &#63;.
	 *
	 * @param groupId the group ID
	 * @param fragmentCollectionId the fragment collection ID
	 * @param type the type
	 * @param status the status
	 * @param head the head
	 * @return the number of matching fragment entries
	 */
	public static int countByG_FCI_T_S_Head(
		long groupId, long fragmentCollectionId, int type, int status,
		boolean head) {

		return getPersistence().countByG_FCI_T_S_Head(
			groupId, fragmentCollectionId, type, status, head);
	}

	/**
	 * Returns an ordered range of all the fragment entries where externalReferenceCode = &#63; and groupId = &#63;.
	 *
	 * <p>
	 * Useful when paginating results. Returns a maximum of <code>end - start</code> instances. <code>start</code> and <code>end</code> are not primary keys, they are indexes in the result set. Thus, <code>0</code> refers to the first result in the set. Setting both <code>start</code> and <code>end</code> to <code>com.liferay.portal.kernel.dao.orm.QueryUtil#ALL_POS</code> will return the full result set. If <code>orderByComparator</code> is specified, then the query will include the given ORDER BY logic. If <code>orderByComparator</code> is absent, then the query will include the default ORDER BY logic from <code>com.liferay.fragment.model.impl.FragmentEntryModelImpl</code>.
	 * </p>
	 *
	 * @param externalReferenceCode the external reference code
	 * @param groupId the group ID
	 * @param start the lower bound of the range of fragment entries
	 * @param end the upper bound of the range of fragment entries (not inclusive)
	 * @param orderByComparator the comparator to order the results by (optionally <code>null</code>)
	 * @param useFinderCache whether to use the finder cache
	 * @return the ordered range of matching fragment entries
	 */
	public static List<FragmentEntry> findByERC_G(
		String externalReferenceCode, long groupId, int start, int end,
		OrderByComparator<FragmentEntry> orderByComparator,
		boolean useFinderCache) {

		return getPersistence().findByERC_G(
			externalReferenceCode, groupId, start, end, orderByComparator,
			useFinderCache);
	}

	/**
	 * Returns the first fragment entry in the ordered set where externalReferenceCode = &#63; and groupId = &#63;.
	 *
	 * @param externalReferenceCode the external reference code
	 * @param groupId the group ID
	 * @param orderByComparator the comparator to order the set by (optionally <code>null</code>)
	 * @return the first matching fragment entry
	 * @throws NoSuchEntryException if a matching fragment entry could not be found
	 */
	public static FragmentEntry findByERC_G_First(
			String externalReferenceCode, long groupId,
			OrderByComparator<FragmentEntry> orderByComparator)
		throws com.liferay.fragment.exception.NoSuchEntryException {

		return getPersistence().findByERC_G_First(
			externalReferenceCode, groupId, orderByComparator);
	}

	/**
	 * Returns the first fragment entry in the ordered set where externalReferenceCode = &#63; and groupId = &#63;.
	 *
	 * @param externalReferenceCode the external reference code
	 * @param groupId the group ID
	 * @param orderByComparator the comparator to order the set by (optionally <code>null</code>)
	 * @return the first matching fragment entry, or <code>null</code> if a matching fragment entry could not be found
	 */
	public static FragmentEntry fetchByERC_G_First(
		String externalReferenceCode, long groupId,
		OrderByComparator<FragmentEntry> orderByComparator) {

		return getPersistence().fetchByERC_G_First(
			externalReferenceCode, groupId, orderByComparator);
	}

	/**
	 * Removes all the fragment entries where externalReferenceCode = &#63; and groupId = &#63; from the database.
	 *
	 * @param externalReferenceCode the external reference code
	 * @param groupId the group ID
	 */
	public static void removeByERC_G(
		String externalReferenceCode, long groupId) {

		getPersistence().removeByERC_G(externalReferenceCode, groupId);
	}

	/**
	 * Returns the number of fragment entries where externalReferenceCode = &#63; and groupId = &#63;.
	 *
	 * @param externalReferenceCode the external reference code
	 * @param groupId the group ID
	 * @return the number of matching fragment entries
	 */
	public static int countByERC_G(String externalReferenceCode, long groupId) {
		return getPersistence().countByERC_G(externalReferenceCode, groupId);
	}

	/**
	 * Returns the fragment entry where externalReferenceCode = &#63; and groupId = &#63; and head = &#63; or throws a <code>NoSuchEntryException</code> if it could not be found.
	 *
	 * @param externalReferenceCode the external reference code
	 * @param groupId the group ID
	 * @param head the head
	 * @return the matching fragment entry
	 * @throws NoSuchEntryException if a matching fragment entry could not be found
	 */
	public static FragmentEntry findByERC_G_Head(
			String externalReferenceCode, long groupId, boolean head)
		throws com.liferay.fragment.exception.NoSuchEntryException {

		return getPersistence().findByERC_G_Head(
			externalReferenceCode, groupId, head);
	}

	/**
	 * Returns the fragment entry where externalReferenceCode = &#63; and groupId = &#63; and head = &#63; or returns <code>null</code> if it could not be found, optionally using the finder cache.
	 *
	 * @param externalReferenceCode the external reference code
	 * @param groupId the group ID
	 * @param head the head
	 * @param useFinderCache whether to use the finder cache
	 * @return the matching fragment entry, or <code>null</code> if a matching fragment entry could not be found
	 */
	public static FragmentEntry fetchByERC_G_Head(
		String externalReferenceCode, long groupId, boolean head,
		boolean useFinderCache) {

		return getPersistence().fetchByERC_G_Head(
			externalReferenceCode, groupId, head, useFinderCache);
	}

	/**
	 * Removes the fragment entry where externalReferenceCode = &#63; and groupId = &#63; and head = &#63; from the database.
	 *
	 * @param externalReferenceCode the external reference code
	 * @param groupId the group ID
	 * @param head the head
	 * @return the fragment entry that was removed
	 */
	public static FragmentEntry removeByERC_G_Head(
			String externalReferenceCode, long groupId, boolean head)
		throws com.liferay.fragment.exception.NoSuchEntryException {

		return getPersistence().removeByERC_G_Head(
			externalReferenceCode, groupId, head);
	}

	/**
	 * Returns the number of fragment entries where externalReferenceCode = &#63; and groupId = &#63; and head = &#63;.
	 *
	 * @param externalReferenceCode the external reference code
	 * @param groupId the group ID
	 * @param head the head
	 * @return the number of matching fragment entries
	 */
	public static int countByERC_G_Head(
		String externalReferenceCode, long groupId, boolean head) {

		return getPersistence().countByERC_G_Head(
			externalReferenceCode, groupId, head);
	}

	/**
	 * Returns the fragment entry where headId = &#63; or throws a <code>NoSuchEntryException</code> if it could not be found.
	 *
	 * @param headId the head ID
	 * @return the matching fragment entry
	 * @throws NoSuchEntryException if a matching fragment entry could not be found
	 */
	public static FragmentEntry findByHeadId(long headId)
		throws com.liferay.fragment.exception.NoSuchEntryException {

		return getPersistence().findByHeadId(headId);
	}

	/**
	 * Returns the fragment entry where headId = &#63; or returns <code>null</code> if it could not be found, optionally using the finder cache.
	 *
	 * @param headId the head ID
	 * @param useFinderCache whether to use the finder cache
	 * @return the matching fragment entry, or <code>null</code> if a matching fragment entry could not be found
	 */
	public static FragmentEntry fetchByHeadId(
		long headId, boolean useFinderCache) {

		return getPersistence().fetchByHeadId(headId, useFinderCache);
	}

	/**
	 * Removes the fragment entry where headId = &#63; from the database.
	 *
	 * @param headId the head ID
	 * @return the fragment entry that was removed
	 */
	public static FragmentEntry removeByHeadId(long headId)
		throws com.liferay.fragment.exception.NoSuchEntryException {

		return getPersistence().removeByHeadId(headId);
	}

	/**
	 * Returns the number of fragment entries where headId = &#63;.
	 *
	 * @param headId the head ID
	 * @return the number of matching fragment entries
	 */
	public static int countByHeadId(long headId) {
		return getPersistence().countByHeadId(headId);
	}

	/**
	 * Creates a new fragment entry with the primary key. Does not add the fragment entry to the database.
	 *
	 * @param fragmentEntryId the primary key for the new fragment entry
	 * @return the new fragment entry
	 */
	public static FragmentEntry create(long fragmentEntryId) {
		return getPersistence().create(fragmentEntryId);
	}

	/**
	 * Removes the fragment entry with the primary key from the database. Also notifies the appropriate model listeners.
	 *
	 * @param fragmentEntryId the primary key of the fragment entry
	 * @return the fragment entry that was removed
	 * @throws NoSuchEntryException if a fragment entry with the primary key could not be found
	 */
	public static FragmentEntry remove(long fragmentEntryId)
		throws com.liferay.fragment.exception.NoSuchEntryException {

		return getPersistence().remove(fragmentEntryId);
	}

	public static FragmentEntry updateImpl(FragmentEntry fragmentEntry) {
		return getPersistence().updateImpl(fragmentEntry);
	}

	/**
	 * Returns the fragment entry with the primary key or throws a <code>NoSuchEntryException</code> if it could not be found.
	 *
	 * @param fragmentEntryId the primary key of the fragment entry
	 * @return the fragment entry
	 * @throws NoSuchEntryException if a fragment entry with the primary key could not be found
	 */
	public static FragmentEntry findByPrimaryKey(long fragmentEntryId)
		throws com.liferay.fragment.exception.NoSuchEntryException {

		return getPersistence().findByPrimaryKey(fragmentEntryId);
	}

	/**
	 * Returns the fragment entry with the primary key or returns <code>null</code> if it could not be found.
	 *
	 * @param fragmentEntryId the primary key of the fragment entry
	 * @return the fragment entry, or <code>null</code> if a fragment entry with the primary key could not be found
	 */
	public static FragmentEntry fetchByPrimaryKey(long fragmentEntryId) {
		return getPersistence().fetchByPrimaryKey(fragmentEntryId);
	}

	/**
	 * Returns the fragment entry where uuid = &#63; and groupId = &#63; and head = &#63; or returns <code>null</code> if it could not be found. Uses the finder cache.
	 *
	 * @param uuid the uuid
	 * @param groupId the group ID
	 * @param head the head
	 * @return the matching fragment entry, or <code>null</code> if a matching fragment entry could not be found
	 */
	public static FragmentEntry fetchByUUID_G_Head(
		String uuid, long groupId, boolean head) {

		return getPersistence().fetchByUUID_G_Head(uuid, groupId, head);
	}

	/**
	 * Returns the fragment entry where groupId = &#63; and fragmentEntryKey = &#63; and head = &#63; or returns <code>null</code> if it could not be found. Uses the finder cache.
	 *
	 * @param groupId the group ID
	 * @param fragmentEntryKey the fragment entry key
	 * @param head the head
	 * @return the matching fragment entry, or <code>null</code> if a matching fragment entry could not be found
	 */
	public static FragmentEntry fetchByG_FEK_Head(
		long groupId, String fragmentEntryKey, boolean head) {

		return getPersistence().fetchByG_FEK_Head(
			groupId, fragmentEntryKey, head);
	}

	/**
	 * Returns the fragment entry where externalReferenceCode = &#63; and groupId = &#63; and head = &#63; or returns <code>null</code> if it could not be found. Uses the finder cache.
	 *
	 * @param externalReferenceCode the external reference code
	 * @param groupId the group ID
	 * @param head the head
	 * @return the matching fragment entry, or <code>null</code> if a matching fragment entry could not be found
	 */
	public static FragmentEntry fetchByERC_G_Head(
		String externalReferenceCode, long groupId, boolean head) {

		return getPersistence().fetchByERC_G_Head(
			externalReferenceCode, groupId, head);
	}

	/**
	 * Returns the fragment entry where headId = &#63; or returns <code>null</code> if it could not be found. Uses the finder cache.
	 *
	 * @param headId the head ID
	 * @return the matching fragment entry, or <code>null</code> if a matching fragment entry could not be found
	 */
	public static FragmentEntry fetchByHeadId(long headId) {
		return getPersistence().fetchByHeadId(headId);
	}

	/**
	 * Returns all the fragment entries where uuid = &#63;.
	 *
	 * @param uuid the uuid
	 * @return the matching fragment entries
	 */
	public static List<FragmentEntry> findByUuid(String uuid) {
		return getPersistence().findByUuid(uuid);
	}

	/**
	 * Returns a range of all the fragment entries where uuid = &#63;.
	 *
	 * <p>
	 * Useful when paginating results. Returns a maximum of <code>end - start</code> instances. <code>start</code> and <code>end</code> are not primary keys, they are indexes in the result set. Thus, <code>0</code> refers to the first result in the set. Setting both <code>start</code> and <code>end</code> to <code>com.liferay.portal.kernel.dao.orm.QueryUtil#ALL_POS</code> will return the full result set. If <code>orderByComparator</code> is specified, then the query will include the given ORDER BY logic. If <code>orderByComparator</code> is absent, then the query will include the default ORDER BY logic from <code>com.liferay.fragment.model.impl.FragmentEntryModelImpl</code>.
	 * </p>
	 *
	 * @param uuid the uuid
	 * @param start the lower bound of the range of fragment entries
	 * @param end the upper bound of the range of fragment entries (not inclusive)
	 * @return the range of matching fragment entries
	 */
	public static List<FragmentEntry> findByUuid(
		String uuid, int start, int end) {

		return getPersistence().findByUuid(uuid, start, end);
	}

	/**
	 * Returns an ordered range of all the fragment entries where uuid = &#63;.
	 *
	 * <p>
	 * Useful when paginating results. Returns a maximum of <code>end - start</code> instances. <code>start</code> and <code>end</code> are not primary keys, they are indexes in the result set. Thus, <code>0</code> refers to the first result in the set. Setting both <code>start</code> and <code>end</code> to <code>com.liferay.portal.kernel.dao.orm.QueryUtil#ALL_POS</code> will return the full result set. If <code>orderByComparator</code> is specified, then the query will include the given ORDER BY logic. If <code>orderByComparator</code> is absent, then the query will include the default ORDER BY logic from <code>com.liferay.fragment.model.impl.FragmentEntryModelImpl</code>.
	 * </p>
	 *
	 * @param uuid the uuid
	 * @param start the lower bound of the range of fragment entries
	 * @param end the upper bound of the range of fragment entries (not inclusive)
	 * @param orderByComparator the comparator to order the results by (optionally <code>null</code>)
	 * @return the ordered range of matching fragment entries
	 */
	public static List<FragmentEntry> findByUuid(
		String uuid, int start, int end,
		OrderByComparator<FragmentEntry> orderByComparator) {

		return getPersistence().findByUuid(uuid, start, end, orderByComparator);
	}

	/**
	 * Returns all the fragment entries where uuid = &#63; and head = &#63;.
	 *
	 * @param uuid the uuid
	 * @param head the head
	 * @return the matching fragment entries
	 */
	public static List<FragmentEntry> findByUuid_Head(
		String uuid, boolean head) {

		return getPersistence().findByUuid_Head(uuid, head);
	}

	/**
	 * Returns a range of all the fragment entries where uuid = &#63; and head = &#63;.
	 *
	 * <p>
	 * Useful when paginating results. Returns a maximum of <code>end - start</code> instances. <code>start</code> and <code>end</code> are not primary keys, they are indexes in the result set. Thus, <code>0</code> refers to the first result in the set. Setting both <code>start</code> and <code>end</code> to <code>com.liferay.portal.kernel.dao.orm.QueryUtil#ALL_POS</code> will return the full result set. If <code>orderByComparator</code> is specified, then the query will include the given ORDER BY logic. If <code>orderByComparator</code> is absent, then the query will include the default ORDER BY logic from <code>com.liferay.fragment.model.impl.FragmentEntryModelImpl</code>.
	 * </p>
	 *
	 * @param uuid the uuid
	 * @param head the head
	 * @param start the lower bound of the range of fragment entries
	 * @param end the upper bound of the range of fragment entries (not inclusive)
	 * @return the range of matching fragment entries
	 */
	public static List<FragmentEntry> findByUuid_Head(
		String uuid, boolean head, int start, int end) {

		return getPersistence().findByUuid_Head(uuid, head, start, end);
	}

	/**
	 * Returns an ordered range of all the fragment entries where uuid = &#63; and head = &#63;.
	 *
	 * <p>
	 * Useful when paginating results. Returns a maximum of <code>end - start</code> instances. <code>start</code> and <code>end</code> are not primary keys, they are indexes in the result set. Thus, <code>0</code> refers to the first result in the set. Setting both <code>start</code> and <code>end</code> to <code>com.liferay.portal.kernel.dao.orm.QueryUtil#ALL_POS</code> will return the full result set. If <code>orderByComparator</code> is specified, then the query will include the given ORDER BY logic. If <code>orderByComparator</code> is absent, then the query will include the default ORDER BY logic from <code>com.liferay.fragment.model.impl.FragmentEntryModelImpl</code>.
	 * </p>
	 *
	 * @param uuid the uuid
	 * @param head the head
	 * @param start the lower bound of the range of fragment entries
	 * @param end the upper bound of the range of fragment entries (not inclusive)
	 * @param orderByComparator the comparator to order the results by (optionally <code>null</code>)
	 * @return the ordered range of matching fragment entries
	 */
	public static List<FragmentEntry> findByUuid_Head(
		String uuid, boolean head, int start, int end,
		OrderByComparator<FragmentEntry> orderByComparator) {

		return getPersistence().findByUuid_Head(
			uuid, head, start, end, orderByComparator);
	}

	/**
	 * Returns all the fragment entries where uuid = &#63; and groupId = &#63;.
	 *
	 * @param uuid the uuid
	 * @param groupId the group ID
	 * @return the matching fragment entries
	 */
	public static List<FragmentEntry> findByUUID_G(String uuid, long groupId) {
		return getPersistence().findByUUID_G(uuid, groupId);
	}

	/**
	 * Returns a range of all the fragment entries where uuid = &#63; and groupId = &#63;.
	 *
	 * <p>
	 * Useful when paginating results. Returns a maximum of <code>end - start</code> instances. <code>start</code> and <code>end</code> are not primary keys, they are indexes in the result set. Thus, <code>0</code> refers to the first result in the set. Setting both <code>start</code> and <code>end</code> to <code>com.liferay.portal.kernel.dao.orm.QueryUtil#ALL_POS</code> will return the full result set. If <code>orderByComparator</code> is specified, then the query will include the given ORDER BY logic. If <code>orderByComparator</code> is absent, then the query will include the default ORDER BY logic from <code>com.liferay.fragment.model.impl.FragmentEntryModelImpl</code>.
	 * </p>
	 *
	 * @param uuid the uuid
	 * @param groupId the group ID
	 * @param start the lower bound of the range of fragment entries
	 * @param end the upper bound of the range of fragment entries (not inclusive)
	 * @return the range of matching fragment entries
	 */
	public static List<FragmentEntry> findByUUID_G(
		String uuid, long groupId, int start, int end) {

		return getPersistence().findByUUID_G(uuid, groupId, start, end);
	}

	/**
	 * Returns an ordered range of all the fragment entries where uuid = &#63; and groupId = &#63;.
	 *
	 * <p>
	 * Useful when paginating results. Returns a maximum of <code>end - start</code> instances. <code>start</code> and <code>end</code> are not primary keys, they are indexes in the result set. Thus, <code>0</code> refers to the first result in the set. Setting both <code>start</code> and <code>end</code> to <code>com.liferay.portal.kernel.dao.orm.QueryUtil#ALL_POS</code> will return the full result set. If <code>orderByComparator</code> is specified, then the query will include the given ORDER BY logic. If <code>orderByComparator</code> is absent, then the query will include the default ORDER BY logic from <code>com.liferay.fragment.model.impl.FragmentEntryModelImpl</code>.
	 * </p>
	 *
	 * @param uuid the uuid
	 * @param groupId the group ID
	 * @param start the lower bound of the range of fragment entries
	 * @param end the upper bound of the range of fragment entries (not inclusive)
	 * @param orderByComparator the comparator to order the results by (optionally <code>null</code>)
	 * @return the ordered range of matching fragment entries
	 */
	public static List<FragmentEntry> findByUUID_G(
		String uuid, long groupId, int start, int end,
		OrderByComparator<FragmentEntry> orderByComparator) {

		return getPersistence().findByUUID_G(
			uuid, groupId, start, end, orderByComparator);
	}

	/**
	 * Returns all the fragment entries where uuid = &#63; and companyId = &#63;.
	 *
	 * @param uuid the uuid
	 * @param companyId the company ID
	 * @return the matching fragment entries
	 */
	public static List<FragmentEntry> findByUuid_C(
		String uuid, long companyId) {

		return getPersistence().findByUuid_C(uuid, companyId);
	}

	/**
	 * Returns a range of all the fragment entries where uuid = &#63; and companyId = &#63;.
	 *
	 * <p>
	 * Useful when paginating results. Returns a maximum of <code>end - start</code> instances. <code>start</code> and <code>end</code> are not primary keys, they are indexes in the result set. Thus, <code>0</code> refers to the first result in the set. Setting both <code>start</code> and <code>end</code> to <code>com.liferay.portal.kernel.dao.orm.QueryUtil#ALL_POS</code> will return the full result set. If <code>orderByComparator</code> is specified, then the query will include the given ORDER BY logic. If <code>orderByComparator</code> is absent, then the query will include the default ORDER BY logic from <code>com.liferay.fragment.model.impl.FragmentEntryModelImpl</code>.
	 * </p>
	 *
	 * @param uuid the uuid
	 * @param companyId the company ID
	 * @param start the lower bound of the range of fragment entries
	 * @param end the upper bound of the range of fragment entries (not inclusive)
	 * @return the range of matching fragment entries
	 */
	public static List<FragmentEntry> findByUuid_C(
		String uuid, long companyId, int start, int end) {

		return getPersistence().findByUuid_C(uuid, companyId, start, end);
	}

	/**
	 * Returns an ordered range of all the fragment entries where uuid = &#63; and companyId = &#63;.
	 *
	 * <p>
	 * Useful when paginating results. Returns a maximum of <code>end - start</code> instances. <code>start</code> and <code>end</code> are not primary keys, they are indexes in the result set. Thus, <code>0</code> refers to the first result in the set. Setting both <code>start</code> and <code>end</code> to <code>com.liferay.portal.kernel.dao.orm.QueryUtil#ALL_POS</code> will return the full result set. If <code>orderByComparator</code> is specified, then the query will include the given ORDER BY logic. If <code>orderByComparator</code> is absent, then the query will include the default ORDER BY logic from <code>com.liferay.fragment.model.impl.FragmentEntryModelImpl</code>.
	 * </p>
	 *
	 * @param uuid the uuid
	 * @param companyId the company ID
	 * @param start the lower bound of the range of fragment entries
	 * @param end the upper bound of the range of fragment entries (not inclusive)
	 * @param orderByComparator the comparator to order the results by (optionally <code>null</code>)
	 * @return the ordered range of matching fragment entries
	 */
	public static List<FragmentEntry> findByUuid_C(
		String uuid, long companyId, int start, int end,
		OrderByComparator<FragmentEntry> orderByComparator) {

		return getPersistence().findByUuid_C(
			uuid, companyId, start, end, orderByComparator);
	}

	/**
	 * Returns all the fragment entries where uuid = &#63; and companyId = &#63; and head = &#63;.
	 *
	 * @param uuid the uuid
	 * @param companyId the company ID
	 * @param head the head
	 * @return the matching fragment entries
	 */
	public static List<FragmentEntry> findByUuid_C_Head(
		String uuid, long companyId, boolean head) {

		return getPersistence().findByUuid_C_Head(uuid, companyId, head);
	}

	/**
	 * Returns a range of all the fragment entries where uuid = &#63; and companyId = &#63; and head = &#63;.
	 *
	 * <p>
	 * Useful when paginating results. Returns a maximum of <code>end - start</code> instances. <code>start</code> and <code>end</code> are not primary keys, they are indexes in the result set. Thus, <code>0</code> refers to the first result in the set. Setting both <code>start</code> and <code>end</code> to <code>com.liferay.portal.kernel.dao.orm.QueryUtil#ALL_POS</code> will return the full result set. If <code>orderByComparator</code> is specified, then the query will include the given ORDER BY logic. If <code>orderByComparator</code> is absent, then the query will include the default ORDER BY logic from <code>com.liferay.fragment.model.impl.FragmentEntryModelImpl</code>.
	 * </p>
	 *
	 * @param uuid the uuid
	 * @param companyId the company ID
	 * @param head the head
	 * @param start the lower bound of the range of fragment entries
	 * @param end the upper bound of the range of fragment entries (not inclusive)
	 * @return the range of matching fragment entries
	 */
	public static List<FragmentEntry> findByUuid_C_Head(
		String uuid, long companyId, boolean head, int start, int end) {

		return getPersistence().findByUuid_C_Head(
			uuid, companyId, head, start, end);
	}

	/**
	 * Returns an ordered range of all the fragment entries where uuid = &#63; and companyId = &#63; and head = &#63;.
	 *
	 * <p>
	 * Useful when paginating results. Returns a maximum of <code>end - start</code> instances. <code>start</code> and <code>end</code> are not primary keys, they are indexes in the result set. Thus, <code>0</code> refers to the first result in the set. Setting both <code>start</code> and <code>end</code> to <code>com.liferay.portal.kernel.dao.orm.QueryUtil#ALL_POS</code> will return the full result set. If <code>orderByComparator</code> is specified, then the query will include the given ORDER BY logic. If <code>orderByComparator</code> is absent, then the query will include the default ORDER BY logic from <code>com.liferay.fragment.model.impl.FragmentEntryModelImpl</code>.
	 * </p>
	 *
	 * @param uuid the uuid
	 * @param companyId the company ID
	 * @param head the head
	 * @param start the lower bound of the range of fragment entries
	 * @param end the upper bound of the range of fragment entries (not inclusive)
	 * @param orderByComparator the comparator to order the results by (optionally <code>null</code>)
	 * @return the ordered range of matching fragment entries
	 */
	public static List<FragmentEntry> findByUuid_C_Head(
		String uuid, long companyId, boolean head, int start, int end,
		OrderByComparator<FragmentEntry> orderByComparator) {

		return getPersistence().findByUuid_C_Head(
			uuid, companyId, head, start, end, orderByComparator);
	}

	/**
	 * Returns all the fragment entries where fragmentCollectionId = &#63;.
	 *
	 * @param fragmentCollectionId the fragment collection ID
	 * @return the matching fragment entries
	 */
	public static List<FragmentEntry> findByFragmentCollectionId(
		long fragmentCollectionId) {

		return getPersistence().findByFragmentCollectionId(
			fragmentCollectionId);
	}

	/**
	 * Returns a range of all the fragment entries where fragmentCollectionId = &#63;.
	 *
	 * <p>
	 * Useful when paginating results. Returns a maximum of <code>end - start</code> instances. <code>start</code> and <code>end</code> are not primary keys, they are indexes in the result set. Thus, <code>0</code> refers to the first result in the set. Setting both <code>start</code> and <code>end</code> to <code>com.liferay.portal.kernel.dao.orm.QueryUtil#ALL_POS</code> will return the full result set. If <code>orderByComparator</code> is specified, then the query will include the given ORDER BY logic. If <code>orderByComparator</code> is absent, then the query will include the default ORDER BY logic from <code>com.liferay.fragment.model.impl.FragmentEntryModelImpl</code>.
	 * </p>
	 *
	 * @param fragmentCollectionId the fragment collection ID
	 * @param start the lower bound of the range of fragment entries
	 * @param end the upper bound of the range of fragment entries (not inclusive)
	 * @return the range of matching fragment entries
	 */
	public static List<FragmentEntry> findByFragmentCollectionId(
		long fragmentCollectionId, int start, int end) {

		return getPersistence().findByFragmentCollectionId(
			fragmentCollectionId, start, end);
	}

	/**
	 * Returns an ordered range of all the fragment entries where fragmentCollectionId = &#63;.
	 *
	 * <p>
	 * Useful when paginating results. Returns a maximum of <code>end - start</code> instances. <code>start</code> and <code>end</code> are not primary keys, they are indexes in the result set. Thus, <code>0</code> refers to the first result in the set. Setting both <code>start</code> and <code>end</code> to <code>com.liferay.portal.kernel.dao.orm.QueryUtil#ALL_POS</code> will return the full result set. If <code>orderByComparator</code> is specified, then the query will include the given ORDER BY logic. If <code>orderByComparator</code> is absent, then the query will include the default ORDER BY logic from <code>com.liferay.fragment.model.impl.FragmentEntryModelImpl</code>.
	 * </p>
	 *
	 * @param fragmentCollectionId the fragment collection ID
	 * @param start the lower bound of the range of fragment entries
	 * @param end the upper bound of the range of fragment entries (not inclusive)
	 * @param orderByComparator the comparator to order the results by (optionally <code>null</code>)
	 * @return the ordered range of matching fragment entries
	 */
	public static List<FragmentEntry> findByFragmentCollectionId(
		long fragmentCollectionId, int start, int end,
		OrderByComparator<FragmentEntry> orderByComparator) {

		return getPersistence().findByFragmentCollectionId(
			fragmentCollectionId, start, end, orderByComparator);
	}

	/**
	 * Returns all the fragment entries where fragmentCollectionId = &#63; and head = &#63;.
	 *
	 * @param fragmentCollectionId the fragment collection ID
	 * @param head the head
	 * @return the matching fragment entries
	 */
	public static List<FragmentEntry> findByFragmentCollectionId_Head(
		long fragmentCollectionId, boolean head) {

		return getPersistence().findByFragmentCollectionId_Head(
			fragmentCollectionId, head);
	}

	/**
	 * Returns a range of all the fragment entries where fragmentCollectionId = &#63; and head = &#63;.
	 *
	 * <p>
	 * Useful when paginating results. Returns a maximum of <code>end - start</code> instances. <code>start</code> and <code>end</code> are not primary keys, they are indexes in the result set. Thus, <code>0</code> refers to the first result in the set. Setting both <code>start</code> and <code>end</code> to <code>com.liferay.portal.kernel.dao.orm.QueryUtil#ALL_POS</code> will return the full result set. If <code>orderByComparator</code> is specified, then the query will include the given ORDER BY logic. If <code>orderByComparator</code> is absent, then the query will include the default ORDER BY logic from <code>com.liferay.fragment.model.impl.FragmentEntryModelImpl</code>.
	 * </p>
	 *
	 * @param fragmentCollectionId the fragment collection ID
	 * @param head the head
	 * @param start the lower bound of the range of fragment entries
	 * @param end the upper bound of the range of fragment entries (not inclusive)
	 * @return the range of matching fragment entries
	 */
	public static List<FragmentEntry> findByFragmentCollectionId_Head(
		long fragmentCollectionId, boolean head, int start, int end) {

		return getPersistence().findByFragmentCollectionId_Head(
			fragmentCollectionId, head, start, end);
	}

	/**
	 * Returns an ordered range of all the fragment entries where fragmentCollectionId = &#63; and head = &#63;.
	 *
	 * <p>
	 * Useful when paginating results. Returns a maximum of <code>end - start</code> instances. <code>start</code> and <code>end</code> are not primary keys, they are indexes in the result set. Thus, <code>0</code> refers to the first result in the set. Setting both <code>start</code> and <code>end</code> to <code>com.liferay.portal.kernel.dao.orm.QueryUtil#ALL_POS</code> will return the full result set. If <code>orderByComparator</code> is specified, then the query will include the given ORDER BY logic. If <code>orderByComparator</code> is absent, then the query will include the default ORDER BY logic from <code>com.liferay.fragment.model.impl.FragmentEntryModelImpl</code>.
	 * </p>
	 *
	 * @param fragmentCollectionId the fragment collection ID
	 * @param head the head
	 * @param start the lower bound of the range of fragment entries
	 * @param end the upper bound of the range of fragment entries (not inclusive)
	 * @param orderByComparator the comparator to order the results by (optionally <code>null</code>)
	 * @return the ordered range of matching fragment entries
	 */
	public static List<FragmentEntry> findByFragmentCollectionId_Head(
		long fragmentCollectionId, boolean head, int start, int end,
		OrderByComparator<FragmentEntry> orderByComparator) {

		return getPersistence().findByFragmentCollectionId_Head(
			fragmentCollectionId, head, start, end, orderByComparator);
	}

	/**
	 * Returns all the fragment entries where type = &#63;.
	 *
	 * @param type the type
	 * @return the matching fragment entries
	 */
	public static List<FragmentEntry> findByType(int type) {
		return getPersistence().findByType(type);
	}

	/**
	 * Returns a range of all the fragment entries where type = &#63;.
	 *
	 * <p>
	 * Useful when paginating results. Returns a maximum of <code>end - start</code> instances. <code>start</code> and <code>end</code> are not primary keys, they are indexes in the result set. Thus, <code>0</code> refers to the first result in the set. Setting both <code>start</code> and <code>end</code> to <code>com.liferay.portal.kernel.dao.orm.QueryUtil#ALL_POS</code> will return the full result set. If <code>orderByComparator</code> is specified, then the query will include the given ORDER BY logic. If <code>orderByComparator</code> is absent, then the query will include the default ORDER BY logic from <code>com.liferay.fragment.model.impl.FragmentEntryModelImpl</code>.
	 * </p>
	 *
	 * @param type the type
	 * @param start the lower bound of the range of fragment entries
	 * @param end the upper bound of the range of fragment entries (not inclusive)
	 * @return the range of matching fragment entries
	 */
	public static List<FragmentEntry> findByType(int type, int start, int end) {
		return getPersistence().findByType(type, start, end);
	}

	/**
	 * Returns an ordered range of all the fragment entries where type = &#63;.
	 *
	 * <p>
	 * Useful when paginating results. Returns a maximum of <code>end - start</code> instances. <code>start</code> and <code>end</code> are not primary keys, they are indexes in the result set. Thus, <code>0</code> refers to the first result in the set. Setting both <code>start</code> and <code>end</code> to <code>com.liferay.portal.kernel.dao.orm.QueryUtil#ALL_POS</code> will return the full result set. If <code>orderByComparator</code> is specified, then the query will include the given ORDER BY logic. If <code>orderByComparator</code> is absent, then the query will include the default ORDER BY logic from <code>com.liferay.fragment.model.impl.FragmentEntryModelImpl</code>.
	 * </p>
	 *
	 * @param type the type
	 * @param start the lower bound of the range of fragment entries
	 * @param end the upper bound of the range of fragment entries (not inclusive)
	 * @param orderByComparator the comparator to order the results by (optionally <code>null</code>)
	 * @return the ordered range of matching fragment entries
	 */
	public static List<FragmentEntry> findByType(
		int type, int start, int end,
		OrderByComparator<FragmentEntry> orderByComparator) {

		return getPersistence().findByType(type, start, end, orderByComparator);
	}

	/**
	 * Returns all the fragment entries where type = &#63; and head = &#63;.
	 *
	 * @param type the type
	 * @param head the head
	 * @return the matching fragment entries
	 */
	public static List<FragmentEntry> findByType_Head(int type, boolean head) {
		return getPersistence().findByType_Head(type, head);
	}

	/**
	 * Returns a range of all the fragment entries where type = &#63; and head = &#63;.
	 *
	 * <p>
	 * Useful when paginating results. Returns a maximum of <code>end - start</code> instances. <code>start</code> and <code>end</code> are not primary keys, they are indexes in the result set. Thus, <code>0</code> refers to the first result in the set. Setting both <code>start</code> and <code>end</code> to <code>com.liferay.portal.kernel.dao.orm.QueryUtil#ALL_POS</code> will return the full result set. If <code>orderByComparator</code> is specified, then the query will include the given ORDER BY logic. If <code>orderByComparator</code> is absent, then the query will include the default ORDER BY logic from <code>com.liferay.fragment.model.impl.FragmentEntryModelImpl</code>.
	 * </p>
	 *
	 * @param type the type
	 * @param head the head
	 * @param start the lower bound of the range of fragment entries
	 * @param end the upper bound of the range of fragment entries (not inclusive)
	 * @return the range of matching fragment entries
	 */
	public static List<FragmentEntry> findByType_Head(
		int type, boolean head, int start, int end) {

		return getPersistence().findByType_Head(type, head, start, end);
	}

	/**
	 * Returns an ordered range of all the fragment entries where type = &#63; and head = &#63;.
	 *
	 * <p>
	 * Useful when paginating results. Returns a maximum of <code>end - start</code> instances. <code>start</code> and <code>end</code> are not primary keys, they are indexes in the result set. Thus, <code>0</code> refers to the first result in the set. Setting both <code>start</code> and <code>end</code> to <code>com.liferay.portal.kernel.dao.orm.QueryUtil#ALL_POS</code> will return the full result set. If <code>orderByComparator</code> is specified, then the query will include the given ORDER BY logic. If <code>orderByComparator</code> is absent, then the query will include the default ORDER BY logic from <code>com.liferay.fragment.model.impl.FragmentEntryModelImpl</code>.
	 * </p>
	 *
	 * @param type the type
	 * @param head the head
	 * @param start the lower bound of the range of fragment entries
	 * @param end the upper bound of the range of fragment entries (not inclusive)
	 * @param orderByComparator the comparator to order the results by (optionally <code>null</code>)
	 * @return the ordered range of matching fragment entries
	 */
	public static List<FragmentEntry> findByType_Head(
		int type, boolean head, int start, int end,
		OrderByComparator<FragmentEntry> orderByComparator) {

		return getPersistence().findByType_Head(
			type, head, start, end, orderByComparator);
	}

	/**
	 * Returns all the fragment entries where groupId = &#63; and fragmentCollectionId = &#63;.
	 *
	 * @param groupId the group ID
	 * @param fragmentCollectionId the fragment collection ID
	 * @return the matching fragment entries
	 */
	public static List<FragmentEntry> findByG_FCI(
		long groupId, long fragmentCollectionId) {

		return getPersistence().findByG_FCI(groupId, fragmentCollectionId);
	}

	/**
	 * Returns a range of all the fragment entries where groupId = &#63; and fragmentCollectionId = &#63;.
	 *
	 * <p>
	 * Useful when paginating results. Returns a maximum of <code>end - start</code> instances. <code>start</code> and <code>end</code> are not primary keys, they are indexes in the result set. Thus, <code>0</code> refers to the first result in the set. Setting both <code>start</code> and <code>end</code> to <code>com.liferay.portal.kernel.dao.orm.QueryUtil#ALL_POS</code> will return the full result set. If <code>orderByComparator</code> is specified, then the query will include the given ORDER BY logic. If <code>orderByComparator</code> is absent, then the query will include the default ORDER BY logic from <code>com.liferay.fragment.model.impl.FragmentEntryModelImpl</code>.
	 * </p>
	 *
	 * @param groupId the group ID
	 * @param fragmentCollectionId the fragment collection ID
	 * @param start the lower bound of the range of fragment entries
	 * @param end the upper bound of the range of fragment entries (not inclusive)
	 * @return the range of matching fragment entries
	 */
	public static List<FragmentEntry> findByG_FCI(
		long groupId, long fragmentCollectionId, int start, int end) {

		return getPersistence().findByG_FCI(
			groupId, fragmentCollectionId, start, end);
	}

	/**
	 * Returns an ordered range of all the fragment entries where groupId = &#63; and fragmentCollectionId = &#63;.
	 *
	 * <p>
	 * Useful when paginating results. Returns a maximum of <code>end - start</code> instances. <code>start</code> and <code>end</code> are not primary keys, they are indexes in the result set. Thus, <code>0</code> refers to the first result in the set. Setting both <code>start</code> and <code>end</code> to <code>com.liferay.portal.kernel.dao.orm.QueryUtil#ALL_POS</code> will return the full result set. If <code>orderByComparator</code> is specified, then the query will include the given ORDER BY logic. If <code>orderByComparator</code> is absent, then the query will include the default ORDER BY logic from <code>com.liferay.fragment.model.impl.FragmentEntryModelImpl</code>.
	 * </p>
	 *
	 * @param groupId the group ID
	 * @param fragmentCollectionId the fragment collection ID
	 * @param start the lower bound of the range of fragment entries
	 * @param end the upper bound of the range of fragment entries (not inclusive)
	 * @param orderByComparator the comparator to order the results by (optionally <code>null</code>)
	 * @return the ordered range of matching fragment entries
	 */
	public static List<FragmentEntry> findByG_FCI(
		long groupId, long fragmentCollectionId, int start, int end,
		OrderByComparator<FragmentEntry> orderByComparator) {

		return getPersistence().findByG_FCI(
			groupId, fragmentCollectionId, start, end, orderByComparator);
	}

	/**
	 * Returns all the fragment entries where groupId = &#63; and fragmentCollectionId = &#63; and head = &#63;.
	 *
	 * @param groupId the group ID
	 * @param fragmentCollectionId the fragment collection ID
	 * @param head the head
	 * @return the matching fragment entries
	 */
	public static List<FragmentEntry> findByG_FCI_Head(
		long groupId, long fragmentCollectionId, boolean head) {

		return getPersistence().findByG_FCI_Head(
			groupId, fragmentCollectionId, head);
	}

	/**
	 * Returns a range of all the fragment entries where groupId = &#63; and fragmentCollectionId = &#63; and head = &#63;.
	 *
	 * <p>
	 * Useful when paginating results. Returns a maximum of <code>end - start</code> instances. <code>start</code> and <code>end</code> are not primary keys, they are indexes in the result set. Thus, <code>0</code> refers to the first result in the set. Setting both <code>start</code> and <code>end</code> to <code>com.liferay.portal.kernel.dao.orm.QueryUtil#ALL_POS</code> will return the full result set. If <code>orderByComparator</code> is specified, then the query will include the given ORDER BY logic. If <code>orderByComparator</code> is absent, then the query will include the default ORDER BY logic from <code>com.liferay.fragment.model.impl.FragmentEntryModelImpl</code>.
	 * </p>
	 *
	 * @param groupId the group ID
	 * @param fragmentCollectionId the fragment collection ID
	 * @param head the head
	 * @param start the lower bound of the range of fragment entries
	 * @param end the upper bound of the range of fragment entries (not inclusive)
	 * @return the range of matching fragment entries
	 */
	public static List<FragmentEntry> findByG_FCI_Head(
		long groupId, long fragmentCollectionId, boolean head, int start,
		int end) {

		return getPersistence().findByG_FCI_Head(
			groupId, fragmentCollectionId, head, start, end);
	}

	/**
	 * Returns an ordered range of all the fragment entries where groupId = &#63; and fragmentCollectionId = &#63; and head = &#63;.
	 *
	 * <p>
	 * Useful when paginating results. Returns a maximum of <code>end - start</code> instances. <code>start</code> and <code>end</code> are not primary keys, they are indexes in the result set. Thus, <code>0</code> refers to the first result in the set. Setting both <code>start</code> and <code>end</code> to <code>com.liferay.portal.kernel.dao.orm.QueryUtil#ALL_POS</code> will return the full result set. If <code>orderByComparator</code> is specified, then the query will include the given ORDER BY logic. If <code>orderByComparator</code> is absent, then the query will include the default ORDER BY logic from <code>com.liferay.fragment.model.impl.FragmentEntryModelImpl</code>.
	 * </p>
	 *
	 * @param groupId the group ID
	 * @param fragmentCollectionId the fragment collection ID
	 * @param head the head
	 * @param start the lower bound of the range of fragment entries
	 * @param end the upper bound of the range of fragment entries (not inclusive)
	 * @param orderByComparator the comparator to order the results by (optionally <code>null</code>)
	 * @return the ordered range of matching fragment entries
	 */
	public static List<FragmentEntry> findByG_FCI_Head(
		long groupId, long fragmentCollectionId, boolean head, int start,
		int end, OrderByComparator<FragmentEntry> orderByComparator) {

		return getPersistence().findByG_FCI_Head(
			groupId, fragmentCollectionId, head, start, end, orderByComparator);
	}

	/**
	 * Returns all the fragment entries where groupId = &#63; and fragmentEntryKey = &#63;.
	 *
	 * @param groupId the group ID
	 * @param fragmentEntryKey the fragment entry key
	 * @return the matching fragment entries
	 */
	public static List<FragmentEntry> findByG_FEK(
		long groupId, String fragmentEntryKey) {

		return getPersistence().findByG_FEK(groupId, fragmentEntryKey);
	}

	/**
	 * Returns a range of all the fragment entries where groupId = &#63; and fragmentEntryKey = &#63;.
	 *
	 * <p>
	 * Useful when paginating results. Returns a maximum of <code>end - start</code> instances. <code>start</code> and <code>end</code> are not primary keys, they are indexes in the result set. Thus, <code>0</code> refers to the first result in the set. Setting both <code>start</code> and <code>end</code> to <code>com.liferay.portal.kernel.dao.orm.QueryUtil#ALL_POS</code> will return the full result set. If <code>orderByComparator</code> is specified, then the query will include the given ORDER BY logic. If <code>orderByComparator</code> is absent, then the query will include the default ORDER BY logic from <code>com.liferay.fragment.model.impl.FragmentEntryModelImpl</code>.
	 * </p>
	 *
	 * @param groupId the group ID
	 * @param fragmentEntryKey the fragment entry key
	 * @param start the lower bound of the range of fragment entries
	 * @param end the upper bound of the range of fragment entries (not inclusive)
	 * @return the range of matching fragment entries
	 */
	public static List<FragmentEntry> findByG_FEK(
		long groupId, String fragmentEntryKey, int start, int end) {

		return getPersistence().findByG_FEK(
			groupId, fragmentEntryKey, start, end);
	}

	/**
	 * Returns an ordered range of all the fragment entries where groupId = &#63; and fragmentEntryKey = &#63;.
	 *
	 * <p>
	 * Useful when paginating results. Returns a maximum of <code>end - start</code> instances. <code>start</code> and <code>end</code> are not primary keys, they are indexes in the result set. Thus, <code>0</code> refers to the first result in the set. Setting both <code>start</code> and <code>end</code> to <code>com.liferay.portal.kernel.dao.orm.QueryUtil#ALL_POS</code> will return the full result set. If <code>orderByComparator</code> is specified, then the query will include the given ORDER BY logic. If <code>orderByComparator</code> is absent, then the query will include the default ORDER BY logic from <code>com.liferay.fragment.model.impl.FragmentEntryModelImpl</code>.
	 * </p>
	 *
	 * @param groupId the group ID
	 * @param fragmentEntryKey the fragment entry key
	 * @param start the lower bound of the range of fragment entries
	 * @param end the upper bound of the range of fragment entries (not inclusive)
	 * @param orderByComparator the comparator to order the results by (optionally <code>null</code>)
	 * @return the ordered range of matching fragment entries
	 */
	public static List<FragmentEntry> findByG_FEK(
		long groupId, String fragmentEntryKey, int start, int end,
		OrderByComparator<FragmentEntry> orderByComparator) {

		return getPersistence().findByG_FEK(
			groupId, fragmentEntryKey, start, end, orderByComparator);
	}

	/**
	 * Returns all the fragment entries where groupId = &#63; and fragmentCollectionId = &#63; and type = &#63;.
	 *
	 * @param groupId the group ID
	 * @param fragmentCollectionId the fragment collection ID
	 * @param type the type
	 * @return the matching fragment entries
	 */
	public static List<FragmentEntry> findByG_FCI_T(
		long groupId, long fragmentCollectionId, int type) {

		return getPersistence().findByG_FCI_T(
			groupId, fragmentCollectionId, type);
	}

	/**
	 * Returns a range of all the fragment entries where groupId = &#63; and fragmentCollectionId = &#63; and type = &#63;.
	 *
	 * <p>
	 * Useful when paginating results. Returns a maximum of <code>end - start</code> instances. <code>start</code> and <code>end</code> are not primary keys, they are indexes in the result set. Thus, <code>0</code> refers to the first result in the set. Setting both <code>start</code> and <code>end</code> to <code>com.liferay.portal.kernel.dao.orm.QueryUtil#ALL_POS</code> will return the full result set. If <code>orderByComparator</code> is specified, then the query will include the given ORDER BY logic. If <code>orderByComparator</code> is absent, then the query will include the default ORDER BY logic from <code>com.liferay.fragment.model.impl.FragmentEntryModelImpl</code>.
	 * </p>
	 *
	 * @param groupId the group ID
	 * @param fragmentCollectionId the fragment collection ID
	 * @param type the type
	 * @param start the lower bound of the range of fragment entries
	 * @param end the upper bound of the range of fragment entries (not inclusive)
	 * @return the range of matching fragment entries
	 */
	public static List<FragmentEntry> findByG_FCI_T(
		long groupId, long fragmentCollectionId, int type, int start, int end) {

		return getPersistence().findByG_FCI_T(
			groupId, fragmentCollectionId, type, start, end);
	}

	/**
	 * Returns an ordered range of all the fragment entries where groupId = &#63; and fragmentCollectionId = &#63; and type = &#63;.
	 *
	 * <p>
	 * Useful when paginating results. Returns a maximum of <code>end - start</code> instances. <code>start</code> and <code>end</code> are not primary keys, they are indexes in the result set. Thus, <code>0</code> refers to the first result in the set. Setting both <code>start</code> and <code>end</code> to <code>com.liferay.portal.kernel.dao.orm.QueryUtil#ALL_POS</code> will return the full result set. If <code>orderByComparator</code> is specified, then the query will include the given ORDER BY logic. If <code>orderByComparator</code> is absent, then the query will include the default ORDER BY logic from <code>com.liferay.fragment.model.impl.FragmentEntryModelImpl</code>.
	 * </p>
	 *
	 * @param groupId the group ID
	 * @param fragmentCollectionId the fragment collection ID
	 * @param type the type
	 * @param start the lower bound of the range of fragment entries
	 * @param end the upper bound of the range of fragment entries (not inclusive)
	 * @param orderByComparator the comparator to order the results by (optionally <code>null</code>)
	 * @return the ordered range of matching fragment entries
	 */
	public static List<FragmentEntry> findByG_FCI_T(
		long groupId, long fragmentCollectionId, int type, int start, int end,
		OrderByComparator<FragmentEntry> orderByComparator) {

		return getPersistence().findByG_FCI_T(
			groupId, fragmentCollectionId, type, start, end, orderByComparator);
	}

	/**
	 * Returns all the fragment entries where groupId = &#63; and fragmentCollectionId = &#63; and type = &#63; and head = &#63;.
	 *
	 * @param groupId the group ID
	 * @param fragmentCollectionId the fragment collection ID
	 * @param type the type
	 * @param head the head
	 * @return the matching fragment entries
	 */
	public static List<FragmentEntry> findByG_FCI_T_Head(
		long groupId, long fragmentCollectionId, int type, boolean head) {

		return getPersistence().findByG_FCI_T_Head(
			groupId, fragmentCollectionId, type, head);
	}

	/**
	 * Returns a range of all the fragment entries where groupId = &#63; and fragmentCollectionId = &#63; and type = &#63; and head = &#63;.
	 *
	 * <p>
	 * Useful when paginating results. Returns a maximum of <code>end - start</code> instances. <code>start</code> and <code>end</code> are not primary keys, they are indexes in the result set. Thus, <code>0</code> refers to the first result in the set. Setting both <code>start</code> and <code>end</code> to <code>com.liferay.portal.kernel.dao.orm.QueryUtil#ALL_POS</code> will return the full result set. If <code>orderByComparator</code> is specified, then the query will include the given ORDER BY logic. If <code>orderByComparator</code> is absent, then the query will include the default ORDER BY logic from <code>com.liferay.fragment.model.impl.FragmentEntryModelImpl</code>.
	 * </p>
	 *
	 * @param groupId the group ID
	 * @param fragmentCollectionId the fragment collection ID
	 * @param type the type
	 * @param head the head
	 * @param start the lower bound of the range of fragment entries
	 * @param end the upper bound of the range of fragment entries (not inclusive)
	 * @return the range of matching fragment entries
	 */
	public static List<FragmentEntry> findByG_FCI_T_Head(
		long groupId, long fragmentCollectionId, int type, boolean head,
		int start, int end) {

		return getPersistence().findByG_FCI_T_Head(
			groupId, fragmentCollectionId, type, head, start, end);
	}

	/**
	 * Returns an ordered range of all the fragment entries where groupId = &#63; and fragmentCollectionId = &#63; and type = &#63; and head = &#63;.
	 *
	 * <p>
	 * Useful when paginating results. Returns a maximum of <code>end - start</code> instances. <code>start</code> and <code>end</code> are not primary keys, they are indexes in the result set. Thus, <code>0</code> refers to the first result in the set. Setting both <code>start</code> and <code>end</code> to <code>com.liferay.portal.kernel.dao.orm.QueryUtil#ALL_POS</code> will return the full result set. If <code>orderByComparator</code> is specified, then the query will include the given ORDER BY logic. If <code>orderByComparator</code> is absent, then the query will include the default ORDER BY logic from <code>com.liferay.fragment.model.impl.FragmentEntryModelImpl</code>.
	 * </p>
	 *
	 * @param groupId the group ID
	 * @param fragmentCollectionId the fragment collection ID
	 * @param type the type
	 * @param head the head
	 * @param start the lower bound of the range of fragment entries
	 * @param end the upper bound of the range of fragment entries (not inclusive)
	 * @param orderByComparator the comparator to order the results by (optionally <code>null</code>)
	 * @return the ordered range of matching fragment entries
	 */
	public static List<FragmentEntry> findByG_FCI_T_Head(
		long groupId, long fragmentCollectionId, int type, boolean head,
		int start, int end,
		OrderByComparator<FragmentEntry> orderByComparator) {

		return getPersistence().findByG_FCI_T_Head(
			groupId, fragmentCollectionId, type, head, start, end,
			orderByComparator);
	}

	/**
	 * Returns all the fragment entries where groupId = &#63; and fragmentCollectionId = &#63; and status = &#63;.
	 *
	 * @param groupId the group ID
	 * @param fragmentCollectionId the fragment collection ID
	 * @param status the status
	 * @return the matching fragment entries
	 */
	public static List<FragmentEntry> findByG_FCI_S(
		long groupId, long fragmentCollectionId, int status) {

		return getPersistence().findByG_FCI_S(
			groupId, fragmentCollectionId, status);
	}

	/**
	 * Returns a range of all the fragment entries where groupId = &#63; and fragmentCollectionId = &#63; and status = &#63;.
	 *
	 * <p>
	 * Useful when paginating results. Returns a maximum of <code>end - start</code> instances. <code>start</code> and <code>end</code> are not primary keys, they are indexes in the result set. Thus, <code>0</code> refers to the first result in the set. Setting both <code>start</code> and <code>end</code> to <code>com.liferay.portal.kernel.dao.orm.QueryUtil#ALL_POS</code> will return the full result set. If <code>orderByComparator</code> is specified, then the query will include the given ORDER BY logic. If <code>orderByComparator</code> is absent, then the query will include the default ORDER BY logic from <code>com.liferay.fragment.model.impl.FragmentEntryModelImpl</code>.
	 * </p>
	 *
	 * @param groupId the group ID
	 * @param fragmentCollectionId the fragment collection ID
	 * @param status the status
	 * @param start the lower bound of the range of fragment entries
	 * @param end the upper bound of the range of fragment entries (not inclusive)
	 * @return the range of matching fragment entries
	 */
	public static List<FragmentEntry> findByG_FCI_S(
		long groupId, long fragmentCollectionId, int status, int start,
		int end) {

		return getPersistence().findByG_FCI_S(
			groupId, fragmentCollectionId, status, start, end);
	}

	/**
	 * Returns an ordered range of all the fragment entries where groupId = &#63; and fragmentCollectionId = &#63; and status = &#63;.
	 *
	 * <p>
	 * Useful when paginating results. Returns a maximum of <code>end - start</code> instances. <code>start</code> and <code>end</code> are not primary keys, they are indexes in the result set. Thus, <code>0</code> refers to the first result in the set. Setting both <code>start</code> and <code>end</code> to <code>com.liferay.portal.kernel.dao.orm.QueryUtil#ALL_POS</code> will return the full result set. If <code>orderByComparator</code> is specified, then the query will include the given ORDER BY logic. If <code>orderByComparator</code> is absent, then the query will include the default ORDER BY logic from <code>com.liferay.fragment.model.impl.FragmentEntryModelImpl</code>.
	 * </p>
	 *
	 * @param groupId the group ID
	 * @param fragmentCollectionId the fragment collection ID
	 * @param status the status
	 * @param start the lower bound of the range of fragment entries
	 * @param end the upper bound of the range of fragment entries (not inclusive)
	 * @param orderByComparator the comparator to order the results by (optionally <code>null</code>)
	 * @return the ordered range of matching fragment entries
	 */
	public static List<FragmentEntry> findByG_FCI_S(
		long groupId, long fragmentCollectionId, int status, int start, int end,
		OrderByComparator<FragmentEntry> orderByComparator) {

		return getPersistence().findByG_FCI_S(
			groupId, fragmentCollectionId, status, start, end,
			orderByComparator);
	}

	/**
	 * Returns all the fragment entries where groupId = &#63; and fragmentCollectionId = &#63; and status = &#63; and head = &#63;.
	 *
	 * @param groupId the group ID
	 * @param fragmentCollectionId the fragment collection ID
	 * @param status the status
	 * @param head the head
	 * @return the matching fragment entries
	 */
	public static List<FragmentEntry> findByG_FCI_S_Head(
		long groupId, long fragmentCollectionId, int status, boolean head) {

		return getPersistence().findByG_FCI_S_Head(
			groupId, fragmentCollectionId, status, head);
	}

	/**
	 * Returns a range of all the fragment entries where groupId = &#63; and fragmentCollectionId = &#63; and status = &#63; and head = &#63;.
	 *
	 * <p>
	 * Useful when paginating results. Returns a maximum of <code>end - start</code> instances. <code>start</code> and <code>end</code> are not primary keys, they are indexes in the result set. Thus, <code>0</code> refers to the first result in the set. Setting both <code>start</code> and <code>end</code> to <code>com.liferay.portal.kernel.dao.orm.QueryUtil#ALL_POS</code> will return the full result set. If <code>orderByComparator</code> is specified, then the query will include the given ORDER BY logic. If <code>orderByComparator</code> is absent, then the query will include the default ORDER BY logic from <code>com.liferay.fragment.model.impl.FragmentEntryModelImpl</code>.
	 * </p>
	 *
	 * @param groupId the group ID
	 * @param fragmentCollectionId the fragment collection ID
	 * @param status the status
	 * @param head the head
	 * @param start the lower bound of the range of fragment entries
	 * @param end the upper bound of the range of fragment entries (not inclusive)
	 * @return the range of matching fragment entries
	 */
	public static List<FragmentEntry> findByG_FCI_S_Head(
		long groupId, long fragmentCollectionId, int status, boolean head,
		int start, int end) {

		return getPersistence().findByG_FCI_S_Head(
			groupId, fragmentCollectionId, status, head, start, end);
	}

	/**
	 * Returns an ordered range of all the fragment entries where groupId = &#63; and fragmentCollectionId = &#63; and status = &#63; and head = &#63;.
	 *
	 * <p>
	 * Useful when paginating results. Returns a maximum of <code>end - start</code> instances. <code>start</code> and <code>end</code> are not primary keys, they are indexes in the result set. Thus, <code>0</code> refers to the first result in the set. Setting both <code>start</code> and <code>end</code> to <code>com.liferay.portal.kernel.dao.orm.QueryUtil#ALL_POS</code> will return the full result set. If <code>orderByComparator</code> is specified, then the query will include the given ORDER BY logic. If <code>orderByComparator</code> is absent, then the query will include the default ORDER BY logic from <code>com.liferay.fragment.model.impl.FragmentEntryModelImpl</code>.
	 * </p>
	 *
	 * @param groupId the group ID
	 * @param fragmentCollectionId the fragment collection ID
	 * @param status the status
	 * @param head the head
	 * @param start the lower bound of the range of fragment entries
	 * @param end the upper bound of the range of fragment entries (not inclusive)
	 * @param orderByComparator the comparator to order the results by (optionally <code>null</code>)
	 * @return the ordered range of matching fragment entries
	 */
	public static List<FragmentEntry> findByG_FCI_S_Head(
		long groupId, long fragmentCollectionId, int status, boolean head,
		int start, int end,
		OrderByComparator<FragmentEntry> orderByComparator) {

		return getPersistence().findByG_FCI_S_Head(
			groupId, fragmentCollectionId, status, head, start, end,
			orderByComparator);
	}

	/**
	 * Returns all the fragment entries where groupId = &#63; and fragmentCollectionId = &#63; and type = &#63; and status = &#63;.
	 *
	 * @param groupId the group ID
	 * @param fragmentCollectionId the fragment collection ID
	 * @param type the type
	 * @param status the status
	 * @return the matching fragment entries
	 */
	public static List<FragmentEntry> findByG_FCI_T_S(
		long groupId, long fragmentCollectionId, int type, int status) {

		return getPersistence().findByG_FCI_T_S(
			groupId, fragmentCollectionId, type, status);
	}

	/**
	 * Returns a range of all the fragment entries where groupId = &#63; and fragmentCollectionId = &#63; and type = &#63; and status = &#63;.
	 *
	 * <p>
	 * Useful when paginating results. Returns a maximum of <code>end - start</code> instances. <code>start</code> and <code>end</code> are not primary keys, they are indexes in the result set. Thus, <code>0</code> refers to the first result in the set. Setting both <code>start</code> and <code>end</code> to <code>com.liferay.portal.kernel.dao.orm.QueryUtil#ALL_POS</code> will return the full result set. If <code>orderByComparator</code> is specified, then the query will include the given ORDER BY logic. If <code>orderByComparator</code> is absent, then the query will include the default ORDER BY logic from <code>com.liferay.fragment.model.impl.FragmentEntryModelImpl</code>.
	 * </p>
	 *
	 * @param groupId the group ID
	 * @param fragmentCollectionId the fragment collection ID
	 * @param type the type
	 * @param status the status
	 * @param start the lower bound of the range of fragment entries
	 * @param end the upper bound of the range of fragment entries (not inclusive)
	 * @return the range of matching fragment entries
	 */
	public static List<FragmentEntry> findByG_FCI_T_S(
		long groupId, long fragmentCollectionId, int type, int status,
		int start, int end) {

		return getPersistence().findByG_FCI_T_S(
			groupId, fragmentCollectionId, type, status, start, end);
	}

	/**
	 * Returns an ordered range of all the fragment entries where groupId = &#63; and fragmentCollectionId = &#63; and type = &#63; and status = &#63;.
	 *
	 * <p>
	 * Useful when paginating results. Returns a maximum of <code>end - start</code> instances. <code>start</code> and <code>end</code> are not primary keys, they are indexes in the result set. Thus, <code>0</code> refers to the first result in the set. Setting both <code>start</code> and <code>end</code> to <code>com.liferay.portal.kernel.dao.orm.QueryUtil#ALL_POS</code> will return the full result set. If <code>orderByComparator</code> is specified, then the query will include the given ORDER BY logic. If <code>orderByComparator</code> is absent, then the query will include the default ORDER BY logic from <code>com.liferay.fragment.model.impl.FragmentEntryModelImpl</code>.
	 * </p>
	 *
	 * @param groupId the group ID
	 * @param fragmentCollectionId the fragment collection ID
	 * @param type the type
	 * @param status the status
	 * @param start the lower bound of the range of fragment entries
	 * @param end the upper bound of the range of fragment entries (not inclusive)
	 * @param orderByComparator the comparator to order the results by (optionally <code>null</code>)
	 * @return the ordered range of matching fragment entries
	 */
	public static List<FragmentEntry> findByG_FCI_T_S(
		long groupId, long fragmentCollectionId, int type, int status,
		int start, int end,
		OrderByComparator<FragmentEntry> orderByComparator) {

		return getPersistence().findByG_FCI_T_S(
			groupId, fragmentCollectionId, type, status, start, end,
			orderByComparator);
	}

	/**
	 * Returns all the fragment entries where groupId = &#63; and fragmentCollectionId = &#63; and type = &#63; and status = &#63; and head = &#63;.
	 *
	 * @param groupId the group ID
	 * @param fragmentCollectionId the fragment collection ID
	 * @param type the type
	 * @param status the status
	 * @param head the head
	 * @return the matching fragment entries
	 */
	public static List<FragmentEntry> findByG_FCI_T_S_Head(
		long groupId, long fragmentCollectionId, int type, int status,
		boolean head) {

		return getPersistence().findByG_FCI_T_S_Head(
			groupId, fragmentCollectionId, type, status, head);
	}

	/**
	 * Returns a range of all the fragment entries where groupId = &#63; and fragmentCollectionId = &#63; and type = &#63; and status = &#63; and head = &#63;.
	 *
	 * <p>
	 * Useful when paginating results. Returns a maximum of <code>end - start</code> instances. <code>start</code> and <code>end</code> are not primary keys, they are indexes in the result set. Thus, <code>0</code> refers to the first result in the set. Setting both <code>start</code> and <code>end</code> to <code>com.liferay.portal.kernel.dao.orm.QueryUtil#ALL_POS</code> will return the full result set. If <code>orderByComparator</code> is specified, then the query will include the given ORDER BY logic. If <code>orderByComparator</code> is absent, then the query will include the default ORDER BY logic from <code>com.liferay.fragment.model.impl.FragmentEntryModelImpl</code>.
	 * </p>
	 *
	 * @param groupId the group ID
	 * @param fragmentCollectionId the fragment collection ID
	 * @param type the type
	 * @param status the status
	 * @param head the head
	 * @param start the lower bound of the range of fragment entries
	 * @param end the upper bound of the range of fragment entries (not inclusive)
	 * @return the range of matching fragment entries
	 */
	public static List<FragmentEntry> findByG_FCI_T_S_Head(
		long groupId, long fragmentCollectionId, int type, int status,
		boolean head, int start, int end) {

		return getPersistence().findByG_FCI_T_S_Head(
			groupId, fragmentCollectionId, type, status, head, start, end);
	}

	/**
	 * Returns an ordered range of all the fragment entries where groupId = &#63; and fragmentCollectionId = &#63; and type = &#63; and status = &#63; and head = &#63;.
	 *
	 * <p>
	 * Useful when paginating results. Returns a maximum of <code>end - start</code> instances. <code>start</code> and <code>end</code> are not primary keys, they are indexes in the result set. Thus, <code>0</code> refers to the first result in the set. Setting both <code>start</code> and <code>end</code> to <code>com.liferay.portal.kernel.dao.orm.QueryUtil#ALL_POS</code> will return the full result set. If <code>orderByComparator</code> is specified, then the query will include the given ORDER BY logic. If <code>orderByComparator</code> is absent, then the query will include the default ORDER BY logic from <code>com.liferay.fragment.model.impl.FragmentEntryModelImpl</code>.
	 * </p>
	 *
	 * @param groupId the group ID
	 * @param fragmentCollectionId the fragment collection ID
	 * @param type the type
	 * @param status the status
	 * @param head the head
	 * @param start the lower bound of the range of fragment entries
	 * @param end the upper bound of the range of fragment entries (not inclusive)
	 * @param orderByComparator the comparator to order the results by (optionally <code>null</code>)
	 * @return the ordered range of matching fragment entries
	 */
	public static List<FragmentEntry> findByG_FCI_T_S_Head(
		long groupId, long fragmentCollectionId, int type, int status,
		boolean head, int start, int end,
		OrderByComparator<FragmentEntry> orderByComparator) {

		return getPersistence().findByG_FCI_T_S_Head(
			groupId, fragmentCollectionId, type, status, head, start, end,
			orderByComparator);
	}

	/**
	 * Returns all the fragment entries where externalReferenceCode = &#63; and groupId = &#63;.
	 *
	 * @param externalReferenceCode the external reference code
	 * @param groupId the group ID
	 * @return the matching fragment entries
	 */
	public static List<FragmentEntry> findByERC_G(
		String externalReferenceCode, long groupId) {

		return getPersistence().findByERC_G(externalReferenceCode, groupId);
	}

	/**
	 * Returns a range of all the fragment entries where externalReferenceCode = &#63; and groupId = &#63;.
	 *
	 * <p>
	 * Useful when paginating results. Returns a maximum of <code>end - start</code> instances. <code>start</code> and <code>end</code> are not primary keys, they are indexes in the result set. Thus, <code>0</code> refers to the first result in the set. Setting both <code>start</code> and <code>end</code> to <code>com.liferay.portal.kernel.dao.orm.QueryUtil#ALL_POS</code> will return the full result set. If <code>orderByComparator</code> is specified, then the query will include the given ORDER BY logic. If <code>orderByComparator</code> is absent, then the query will include the default ORDER BY logic from <code>com.liferay.fragment.model.impl.FragmentEntryModelImpl</code>.
	 * </p>
	 *
	 * @param externalReferenceCode the external reference code
	 * @param groupId the group ID
	 * @param start the lower bound of the range of fragment entries
	 * @param end the upper bound of the range of fragment entries (not inclusive)
	 * @return the range of matching fragment entries
	 */
	public static List<FragmentEntry> findByERC_G(
		String externalReferenceCode, long groupId, int start, int end) {

		return getPersistence().findByERC_G(
			externalReferenceCode, groupId, start, end);
	}

	/**
	 * Returns an ordered range of all the fragment entries where externalReferenceCode = &#63; and groupId = &#63;.
	 *
	 * <p>
	 * Useful when paginating results. Returns a maximum of <code>end - start</code> instances. <code>start</code> and <code>end</code> are not primary keys, they are indexes in the result set. Thus, <code>0</code> refers to the first result in the set. Setting both <code>start</code> and <code>end</code> to <code>com.liferay.portal.kernel.dao.orm.QueryUtil#ALL_POS</code> will return the full result set. If <code>orderByComparator</code> is specified, then the query will include the given ORDER BY logic. If <code>orderByComparator</code> is absent, then the query will include the default ORDER BY logic from <code>com.liferay.fragment.model.impl.FragmentEntryModelImpl</code>.
	 * </p>
	 *
	 * @param externalReferenceCode the external reference code
	 * @param groupId the group ID
	 * @param start the lower bound of the range of fragment entries
	 * @param end the upper bound of the range of fragment entries (not inclusive)
	 * @param orderByComparator the comparator to order the results by (optionally <code>null</code>)
	 * @return the ordered range of matching fragment entries
	 */
	public static List<FragmentEntry> findByERC_G(
		String externalReferenceCode, long groupId, int start, int end,
		OrderByComparator<FragmentEntry> orderByComparator) {

		return getPersistence().findByERC_G(
			externalReferenceCode, groupId, start, end, orderByComparator);
	}

	public static FragmentEntryPersistence getPersistence() {
		return _persistence;
	}

	public static void setPersistence(FragmentEntryPersistence persistence) {
		_persistence = persistence;
	}

	private static volatile FragmentEntryPersistence _persistence;

}
// LIFERAY-SERVICE-BUILDER-HASH:-1441400092