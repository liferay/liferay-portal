/**
 * SPDX-FileCopyrightText: (c) 2000 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

package com.liferay.message.boards.service.persistence;

import com.liferay.message.boards.model.MBMailingList;
import com.liferay.portal.kernel.dao.orm.DynamicQuery;
import com.liferay.portal.kernel.service.ServiceContext;
import com.liferay.portal.kernel.util.OrderByComparator;

import java.io.Serializable;

import java.util.List;
import java.util.Map;
import java.util.Set;

/**
 * The persistence utility for the message boards mailing list service. This utility wraps <code>com.liferay.message.boards.service.persistence.impl.MBMailingListPersistenceImpl</code> and provides direct access to the database for CRUD operations. This utility should only be used by the service layer, as it must operate within a transaction. Never access this utility in a JSP, controller, model, or other front-end class.
 *
 * <p>
 * Caching information and settings can be found in <code>portal.properties</code>
 * </p>
 *
 * @author Brian Wing Shun Chan
 * @see MBMailingListPersistence
 * @generated
 */
public class MBMailingListUtil {

	/*
	 * NOTE FOR DEVELOPERS:
	 *
	 * Never modify this class directly. Modify <code>service.xml</code> and rerun ServiceBuilder to regenerate this class.
	 */

	/**
	 * @see com.liferay.portal.kernel.service.persistence.BasePersistence#cacheResult(List)
	 */
	public static void cacheResult(List<MBMailingList> mbMailingLists) {
		getPersistence().cacheResult(mbMailingLists);
	}

	/**
	 * @see com.liferay.portal.kernel.service.persistence.BasePersistence#cacheResult(com.liferay.portal.kernel.model.BaseModel)
	 */
	public static void cacheResult(MBMailingList mbMailingList) {
		getPersistence().cacheResult(mbMailingList);
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
	public static void clearCache(MBMailingList mbMailingList) {
		getPersistence().clearCache(mbMailingList);
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
	public static Map<Serializable, MBMailingList> fetchByPrimaryKeys(
		Set<Serializable> primaryKeys) {

		return getPersistence().fetchByPrimaryKeys(primaryKeys);
	}

	/**
	 * @see com.liferay.portal.kernel.service.persistence.BasePersistence#findWithDynamicQuery(DynamicQuery)
	 */
	public static List<MBMailingList> findWithDynamicQuery(
		DynamicQuery dynamicQuery) {

		return getPersistence().findWithDynamicQuery(dynamicQuery);
	}

	/**
	 * @see com.liferay.portal.kernel.service.persistence.BasePersistence#findWithDynamicQuery(DynamicQuery, int, int)
	 */
	public static List<MBMailingList> findWithDynamicQuery(
		DynamicQuery dynamicQuery, int start, int end) {

		return getPersistence().findWithDynamicQuery(dynamicQuery, start, end);
	}

	/**
	 * @see com.liferay.portal.kernel.service.persistence.BasePersistence#findWithDynamicQuery(DynamicQuery, int, int, OrderByComparator)
	 */
	public static List<MBMailingList> findWithDynamicQuery(
		DynamicQuery dynamicQuery, int start, int end,
		OrderByComparator<MBMailingList> orderByComparator) {

		return getPersistence().findWithDynamicQuery(
			dynamicQuery, start, end, orderByComparator);
	}

	/**
	 * @see com.liferay.portal.kernel.service.persistence.BasePersistence#update(com.liferay.portal.kernel.model.BaseModel)
	 */
	public static MBMailingList update(MBMailingList mbMailingList) {
		return getPersistence().update(mbMailingList);
	}

	/**
	 * @see com.liferay.portal.kernel.service.persistence.BasePersistence#update(com.liferay.portal.kernel.model.BaseModel, ServiceContext)
	 */
	public static MBMailingList update(
		MBMailingList mbMailingList, ServiceContext serviceContext) {

		return getPersistence().update(mbMailingList, serviceContext);
	}

	/**
	 * Returns an ordered range of all the message boards mailing lists where uuid = &#63;.
	 *
	 * <p>
	 * Useful when paginating results. Returns a maximum of <code>end - start</code> instances. <code>start</code> and <code>end</code> are not primary keys, they are indexes in the result set. Thus, <code>0</code> refers to the first result in the set. Setting both <code>start</code> and <code>end</code> to <code>com.liferay.portal.kernel.dao.orm.QueryUtil#ALL_POS</code> will return the full result set. If <code>orderByComparator</code> is specified, then the query will include the given ORDER BY logic. If <code>orderByComparator</code> is absent, then the query will include the default ORDER BY logic from <code>com.liferay.message.boards.model.impl.MBMailingListModelImpl</code>.
	 * </p>
	 *
	 * @param uuid the uuid
	 * @param start the lower bound of the range of message boards mailing lists
	 * @param end the upper bound of the range of message boards mailing lists (not inclusive)
	 * @param orderByComparator the comparator to order the results by (optionally <code>null</code>)
	 * @param useFinderCache whether to use the finder cache
	 * @return the ordered range of matching message boards mailing lists
	 */
	public static List<MBMailingList> findByUuid(
		String uuid, int start, int end,
		OrderByComparator<MBMailingList> orderByComparator,
		boolean useFinderCache) {

		return getPersistence().findByUuid(
			uuid, start, end, orderByComparator, useFinderCache);
	}

	/**
	 * Returns the first message boards mailing list in the ordered set where uuid = &#63;.
	 *
	 * @param uuid the uuid
	 * @param orderByComparator the comparator to order the set by (optionally <code>null</code>)
	 * @return the first matching message boards mailing list
	 * @throws NoSuchMailingListException if a matching message boards mailing list could not be found
	 */
	public static MBMailingList findByUuid_First(
			String uuid, OrderByComparator<MBMailingList> orderByComparator)
		throws com.liferay.message.boards.exception.NoSuchMailingListException {

		return getPersistence().findByUuid_First(uuid, orderByComparator);
	}

	/**
	 * Returns the first message boards mailing list in the ordered set where uuid = &#63;.
	 *
	 * @param uuid the uuid
	 * @param orderByComparator the comparator to order the set by (optionally <code>null</code>)
	 * @return the first matching message boards mailing list, or <code>null</code> if a matching message boards mailing list could not be found
	 */
	public static MBMailingList fetchByUuid_First(
		String uuid, OrderByComparator<MBMailingList> orderByComparator) {

		return getPersistence().fetchByUuid_First(uuid, orderByComparator);
	}

	/**
	 * Removes all the message boards mailing lists where uuid = &#63; from the database.
	 *
	 * @param uuid the uuid
	 */
	public static void removeByUuid(String uuid) {
		getPersistence().removeByUuid(uuid);
	}

	/**
	 * Returns the number of message boards mailing lists where uuid = &#63;.
	 *
	 * @param uuid the uuid
	 * @return the number of matching message boards mailing lists
	 */
	public static int countByUuid(String uuid) {
		return getPersistence().countByUuid(uuid);
	}

	/**
	 * Returns the message boards mailing list where uuid = &#63; and groupId = &#63; or throws a <code>NoSuchMailingListException</code> if it could not be found.
	 *
	 * @param uuid the uuid
	 * @param groupId the group ID
	 * @return the matching message boards mailing list
	 * @throws NoSuchMailingListException if a matching message boards mailing list could not be found
	 */
	public static MBMailingList findByUUID_G(String uuid, long groupId)
		throws com.liferay.message.boards.exception.NoSuchMailingListException {

		return getPersistence().findByUUID_G(uuid, groupId);
	}

	/**
	 * Returns the message boards mailing list where uuid = &#63; and groupId = &#63; or returns <code>null</code> if it could not be found, optionally using the finder cache.
	 *
	 * @param uuid the uuid
	 * @param groupId the group ID
	 * @param useFinderCache whether to use the finder cache
	 * @return the matching message boards mailing list, or <code>null</code> if a matching message boards mailing list could not be found
	 */
	public static MBMailingList fetchByUUID_G(
		String uuid, long groupId, boolean useFinderCache) {

		return getPersistence().fetchByUUID_G(uuid, groupId, useFinderCache);
	}

	/**
	 * Removes the message boards mailing list where uuid = &#63; and groupId = &#63; from the database.
	 *
	 * @param uuid the uuid
	 * @param groupId the group ID
	 * @return the message boards mailing list that was removed
	 */
	public static MBMailingList removeByUUID_G(String uuid, long groupId)
		throws com.liferay.message.boards.exception.NoSuchMailingListException {

		return getPersistence().removeByUUID_G(uuid, groupId);
	}

	/**
	 * Returns the number of message boards mailing lists where uuid = &#63; and groupId = &#63;.
	 *
	 * @param uuid the uuid
	 * @param groupId the group ID
	 * @return the number of matching message boards mailing lists
	 */
	public static int countByUUID_G(String uuid, long groupId) {
		return getPersistence().countByUUID_G(uuid, groupId);
	}

	/**
	 * Returns an ordered range of all the message boards mailing lists where uuid = &#63; and companyId = &#63;.
	 *
	 * <p>
	 * Useful when paginating results. Returns a maximum of <code>end - start</code> instances. <code>start</code> and <code>end</code> are not primary keys, they are indexes in the result set. Thus, <code>0</code> refers to the first result in the set. Setting both <code>start</code> and <code>end</code> to <code>com.liferay.portal.kernel.dao.orm.QueryUtil#ALL_POS</code> will return the full result set. If <code>orderByComparator</code> is specified, then the query will include the given ORDER BY logic. If <code>orderByComparator</code> is absent, then the query will include the default ORDER BY logic from <code>com.liferay.message.boards.model.impl.MBMailingListModelImpl</code>.
	 * </p>
	 *
	 * @param uuid the uuid
	 * @param companyId the company ID
	 * @param start the lower bound of the range of message boards mailing lists
	 * @param end the upper bound of the range of message boards mailing lists (not inclusive)
	 * @param orderByComparator the comparator to order the results by (optionally <code>null</code>)
	 * @param useFinderCache whether to use the finder cache
	 * @return the ordered range of matching message boards mailing lists
	 */
	public static List<MBMailingList> findByUuid_C(
		String uuid, long companyId, int start, int end,
		OrderByComparator<MBMailingList> orderByComparator,
		boolean useFinderCache) {

		return getPersistence().findByUuid_C(
			uuid, companyId, start, end, orderByComparator, useFinderCache);
	}

	/**
	 * Returns the first message boards mailing list in the ordered set where uuid = &#63; and companyId = &#63;.
	 *
	 * @param uuid the uuid
	 * @param companyId the company ID
	 * @param orderByComparator the comparator to order the set by (optionally <code>null</code>)
	 * @return the first matching message boards mailing list
	 * @throws NoSuchMailingListException if a matching message boards mailing list could not be found
	 */
	public static MBMailingList findByUuid_C_First(
			String uuid, long companyId,
			OrderByComparator<MBMailingList> orderByComparator)
		throws com.liferay.message.boards.exception.NoSuchMailingListException {

		return getPersistence().findByUuid_C_First(
			uuid, companyId, orderByComparator);
	}

	/**
	 * Returns the first message boards mailing list in the ordered set where uuid = &#63; and companyId = &#63;.
	 *
	 * @param uuid the uuid
	 * @param companyId the company ID
	 * @param orderByComparator the comparator to order the set by (optionally <code>null</code>)
	 * @return the first matching message boards mailing list, or <code>null</code> if a matching message boards mailing list could not be found
	 */
	public static MBMailingList fetchByUuid_C_First(
		String uuid, long companyId,
		OrderByComparator<MBMailingList> orderByComparator) {

		return getPersistence().fetchByUuid_C_First(
			uuid, companyId, orderByComparator);
	}

	/**
	 * Removes all the message boards mailing lists where uuid = &#63; and companyId = &#63; from the database.
	 *
	 * @param uuid the uuid
	 * @param companyId the company ID
	 */
	public static void removeByUuid_C(String uuid, long companyId) {
		getPersistence().removeByUuid_C(uuid, companyId);
	}

	/**
	 * Returns the number of message boards mailing lists where uuid = &#63; and companyId = &#63;.
	 *
	 * @param uuid the uuid
	 * @param companyId the company ID
	 * @return the number of matching message boards mailing lists
	 */
	public static int countByUuid_C(String uuid, long companyId) {
		return getPersistence().countByUuid_C(uuid, companyId);
	}

	/**
	 * Returns the message boards mailing list where groupId = &#63; and categoryId = &#63; or throws a <code>NoSuchMailingListException</code> if it could not be found.
	 *
	 * @param groupId the group ID
	 * @param categoryId the category ID
	 * @return the matching message boards mailing list
	 * @throws NoSuchMailingListException if a matching message boards mailing list could not be found
	 */
	public static MBMailingList findByG_C(long groupId, long categoryId)
		throws com.liferay.message.boards.exception.NoSuchMailingListException {

		return getPersistence().findByG_C(groupId, categoryId);
	}

	/**
	 * Returns the message boards mailing list where groupId = &#63; and categoryId = &#63; or returns <code>null</code> if it could not be found, optionally using the finder cache.
	 *
	 * @param groupId the group ID
	 * @param categoryId the category ID
	 * @param useFinderCache whether to use the finder cache
	 * @return the matching message boards mailing list, or <code>null</code> if a matching message boards mailing list could not be found
	 */
	public static MBMailingList fetchByG_C(
		long groupId, long categoryId, boolean useFinderCache) {

		return getPersistence().fetchByG_C(groupId, categoryId, useFinderCache);
	}

	/**
	 * Removes the message boards mailing list where groupId = &#63; and categoryId = &#63; from the database.
	 *
	 * @param groupId the group ID
	 * @param categoryId the category ID
	 * @return the message boards mailing list that was removed
	 */
	public static MBMailingList removeByG_C(long groupId, long categoryId)
		throws com.liferay.message.boards.exception.NoSuchMailingListException {

		return getPersistence().removeByG_C(groupId, categoryId);
	}

	/**
	 * Returns the number of message boards mailing lists where groupId = &#63; and categoryId = &#63;.
	 *
	 * @param groupId the group ID
	 * @param categoryId the category ID
	 * @return the number of matching message boards mailing lists
	 */
	public static int countByG_C(long groupId, long categoryId) {
		return getPersistence().countByG_C(groupId, categoryId);
	}

	/**
	 * Creates a new message boards mailing list with the primary key. Does not add the message boards mailing list to the database.
	 *
	 * @param mailingListId the primary key for the new message boards mailing list
	 * @return the new message boards mailing list
	 */
	public static MBMailingList create(long mailingListId) {
		return getPersistence().create(mailingListId);
	}

	/**
	 * Removes the message boards mailing list with the primary key from the database. Also notifies the appropriate model listeners.
	 *
	 * @param mailingListId the primary key of the message boards mailing list
	 * @return the message boards mailing list that was removed
	 * @throws NoSuchMailingListException if a message boards mailing list with the primary key could not be found
	 */
	public static MBMailingList remove(long mailingListId)
		throws com.liferay.message.boards.exception.NoSuchMailingListException {

		return getPersistence().remove(mailingListId);
	}

	public static MBMailingList updateImpl(MBMailingList mbMailingList) {
		return getPersistence().updateImpl(mbMailingList);
	}

	/**
	 * Returns the message boards mailing list with the primary key or throws a <code>NoSuchMailingListException</code> if it could not be found.
	 *
	 * @param mailingListId the primary key of the message boards mailing list
	 * @return the message boards mailing list
	 * @throws NoSuchMailingListException if a message boards mailing list with the primary key could not be found
	 */
	public static MBMailingList findByPrimaryKey(long mailingListId)
		throws com.liferay.message.boards.exception.NoSuchMailingListException {

		return getPersistence().findByPrimaryKey(mailingListId);
	}

	/**
	 * Returns the message boards mailing list with the primary key or returns <code>null</code> if it could not be found.
	 *
	 * @param mailingListId the primary key of the message boards mailing list
	 * @return the message boards mailing list, or <code>null</code> if a message boards mailing list with the primary key could not be found
	 */
	public static MBMailingList fetchByPrimaryKey(long mailingListId) {
		return getPersistence().fetchByPrimaryKey(mailingListId);
	}

	/**
	 * Returns the message boards mailing list where uuid = &#63; and groupId = &#63; or returns <code>null</code> if it could not be found. Uses the finder cache.
	 *
	 * @param uuid the uuid
	 * @param groupId the group ID
	 * @return the matching message boards mailing list, or <code>null</code> if a matching message boards mailing list could not be found
	 */
	public static MBMailingList fetchByUUID_G(String uuid, long groupId) {
		return getPersistence().fetchByUUID_G(uuid, groupId);
	}

	/**
	 * Returns the message boards mailing list where groupId = &#63; and categoryId = &#63; or returns <code>null</code> if it could not be found. Uses the finder cache.
	 *
	 * @param groupId the group ID
	 * @param categoryId the category ID
	 * @return the matching message boards mailing list, or <code>null</code> if a matching message boards mailing list could not be found
	 */
	public static MBMailingList fetchByG_C(long groupId, long categoryId) {
		return getPersistence().fetchByG_C(groupId, categoryId);
	}

	/**
	 * Returns all the message boards mailing lists where uuid = &#63;.
	 *
	 * @param uuid the uuid
	 * @return the matching message boards mailing lists
	 */
	public static List<MBMailingList> findByUuid(String uuid) {
		return getPersistence().findByUuid(uuid);
	}

	/**
	 * Returns a range of all the message boards mailing lists where uuid = &#63;.
	 *
	 * <p>
	 * Useful when paginating results. Returns a maximum of <code>end - start</code> instances. <code>start</code> and <code>end</code> are not primary keys, they are indexes in the result set. Thus, <code>0</code> refers to the first result in the set. Setting both <code>start</code> and <code>end</code> to <code>com.liferay.portal.kernel.dao.orm.QueryUtil#ALL_POS</code> will return the full result set. If <code>orderByComparator</code> is specified, then the query will include the given ORDER BY logic. If <code>orderByComparator</code> is absent, then the query will include the default ORDER BY logic from <code>com.liferay.message.boards.model.impl.MBMailingListModelImpl</code>.
	 * </p>
	 *
	 * @param uuid the uuid
	 * @param start the lower bound of the range of message boards mailing lists
	 * @param end the upper bound of the range of message boards mailing lists (not inclusive)
	 * @return the range of matching message boards mailing lists
	 */
	public static List<MBMailingList> findByUuid(
		String uuid, int start, int end) {

		return getPersistence().findByUuid(uuid, start, end);
	}

	/**
	 * Returns an ordered range of all the message boards mailing lists where uuid = &#63;.
	 *
	 * <p>
	 * Useful when paginating results. Returns a maximum of <code>end - start</code> instances. <code>start</code> and <code>end</code> are not primary keys, they are indexes in the result set. Thus, <code>0</code> refers to the first result in the set. Setting both <code>start</code> and <code>end</code> to <code>com.liferay.portal.kernel.dao.orm.QueryUtil#ALL_POS</code> will return the full result set. If <code>orderByComparator</code> is specified, then the query will include the given ORDER BY logic. If <code>orderByComparator</code> is absent, then the query will include the default ORDER BY logic from <code>com.liferay.message.boards.model.impl.MBMailingListModelImpl</code>.
	 * </p>
	 *
	 * @param uuid the uuid
	 * @param start the lower bound of the range of message boards mailing lists
	 * @param end the upper bound of the range of message boards mailing lists (not inclusive)
	 * @param orderByComparator the comparator to order the results by (optionally <code>null</code>)
	 * @return the ordered range of matching message boards mailing lists
	 */
	public static List<MBMailingList> findByUuid(
		String uuid, int start, int end,
		OrderByComparator<MBMailingList> orderByComparator) {

		return getPersistence().findByUuid(uuid, start, end, orderByComparator);
	}

	/**
	 * Returns all the message boards mailing lists where uuid = &#63; and companyId = &#63;.
	 *
	 * @param uuid the uuid
	 * @param companyId the company ID
	 * @return the matching message boards mailing lists
	 */
	public static List<MBMailingList> findByUuid_C(
		String uuid, long companyId) {

		return getPersistence().findByUuid_C(uuid, companyId);
	}

	/**
	 * Returns a range of all the message boards mailing lists where uuid = &#63; and companyId = &#63;.
	 *
	 * <p>
	 * Useful when paginating results. Returns a maximum of <code>end - start</code> instances. <code>start</code> and <code>end</code> are not primary keys, they are indexes in the result set. Thus, <code>0</code> refers to the first result in the set. Setting both <code>start</code> and <code>end</code> to <code>com.liferay.portal.kernel.dao.orm.QueryUtil#ALL_POS</code> will return the full result set. If <code>orderByComparator</code> is specified, then the query will include the given ORDER BY logic. If <code>orderByComparator</code> is absent, then the query will include the default ORDER BY logic from <code>com.liferay.message.boards.model.impl.MBMailingListModelImpl</code>.
	 * </p>
	 *
	 * @param uuid the uuid
	 * @param companyId the company ID
	 * @param start the lower bound of the range of message boards mailing lists
	 * @param end the upper bound of the range of message boards mailing lists (not inclusive)
	 * @return the range of matching message boards mailing lists
	 */
	public static List<MBMailingList> findByUuid_C(
		String uuid, long companyId, int start, int end) {

		return getPersistence().findByUuid_C(uuid, companyId, start, end);
	}

	/**
	 * Returns an ordered range of all the message boards mailing lists where uuid = &#63; and companyId = &#63;.
	 *
	 * <p>
	 * Useful when paginating results. Returns a maximum of <code>end - start</code> instances. <code>start</code> and <code>end</code> are not primary keys, they are indexes in the result set. Thus, <code>0</code> refers to the first result in the set. Setting both <code>start</code> and <code>end</code> to <code>com.liferay.portal.kernel.dao.orm.QueryUtil#ALL_POS</code> will return the full result set. If <code>orderByComparator</code> is specified, then the query will include the given ORDER BY logic. If <code>orderByComparator</code> is absent, then the query will include the default ORDER BY logic from <code>com.liferay.message.boards.model.impl.MBMailingListModelImpl</code>.
	 * </p>
	 *
	 * @param uuid the uuid
	 * @param companyId the company ID
	 * @param start the lower bound of the range of message boards mailing lists
	 * @param end the upper bound of the range of message boards mailing lists (not inclusive)
	 * @param orderByComparator the comparator to order the results by (optionally <code>null</code>)
	 * @return the ordered range of matching message boards mailing lists
	 */
	public static List<MBMailingList> findByUuid_C(
		String uuid, long companyId, int start, int end,
		OrderByComparator<MBMailingList> orderByComparator) {

		return getPersistence().findByUuid_C(
			uuid, companyId, start, end, orderByComparator);
	}

	public static MBMailingListPersistence getPersistence() {
		return _persistence;
	}

	public static void setPersistence(MBMailingListPersistence persistence) {
		_persistence = persistence;
	}

	private static volatile MBMailingListPersistence _persistence;

}
// LIFERAY-SERVICE-BUILDER-HASH:-821612561