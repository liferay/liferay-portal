/**
 * SPDX-FileCopyrightText: (c) 2000 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

package com.liferay.portal.tools.service.builder.test.service.persistence;

import com.liferay.portal.kernel.dao.orm.DynamicQuery;
import com.liferay.portal.kernel.service.ServiceContext;
import com.liferay.portal.kernel.util.OrderByComparator;
import com.liferay.portal.tools.service.builder.test.model.FinderWhereClauseEntry;

import java.io.Serializable;

import java.util.List;
import java.util.Map;
import java.util.Set;

/**
 * The persistence utility for the finder where clause entry service. This utility wraps <code>com.liferay.portal.tools.service.builder.test.service.persistence.impl.FinderWhereClauseEntryPersistenceImpl</code> and provides direct access to the database for CRUD operations. This utility should only be used by the service layer, as it must operate within a transaction. Never access this utility in a JSP, controller, model, or other front-end class.
 *
 * <p>
 * Caching information and settings can be found in <code>portal.properties</code>
 * </p>
 *
 * @author Brian Wing Shun Chan
 * @see FinderWhereClauseEntryPersistence
 * @generated
 */
public class FinderWhereClauseEntryUtil {

	/*
	 * NOTE FOR DEVELOPERS:
	 *
	 * Never modify this class directly. Modify <code>service.xml</code> and rerun ServiceBuilder to regenerate this class.
	 */

	/**
	 * @see com.liferay.portal.kernel.service.persistence.BasePersistence#cacheResult(List)
	 */
	public static void cacheResult(
		List<FinderWhereClauseEntry> finderWhereClauseEntries) {

		getPersistence().cacheResult(finderWhereClauseEntries);
	}

	/**
	 * @see com.liferay.portal.kernel.service.persistence.BasePersistence#cacheResult(com.liferay.portal.kernel.model.BaseModel)
	 */
	public static void cacheResult(
		FinderWhereClauseEntry finderWhereClauseEntry) {

		getPersistence().cacheResult(finderWhereClauseEntry);
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
	public static void clearCache(
		FinderWhereClauseEntry finderWhereClauseEntry) {

		getPersistence().clearCache(finderWhereClauseEntry);
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
	public static Map<Serializable, FinderWhereClauseEntry> fetchByPrimaryKeys(
		Set<Serializable> primaryKeys) {

		return getPersistence().fetchByPrimaryKeys(primaryKeys);
	}

	/**
	 * @see com.liferay.portal.kernel.service.persistence.BasePersistence#findWithDynamicQuery(DynamicQuery)
	 */
	public static List<FinderWhereClauseEntry> findWithDynamicQuery(
		DynamicQuery dynamicQuery) {

		return getPersistence().findWithDynamicQuery(dynamicQuery);
	}

	/**
	 * @see com.liferay.portal.kernel.service.persistence.BasePersistence#findWithDynamicQuery(DynamicQuery, int, int)
	 */
	public static List<FinderWhereClauseEntry> findWithDynamicQuery(
		DynamicQuery dynamicQuery, int start, int end) {

		return getPersistence().findWithDynamicQuery(dynamicQuery, start, end);
	}

	/**
	 * @see com.liferay.portal.kernel.service.persistence.BasePersistence#findWithDynamicQuery(DynamicQuery, int, int, OrderByComparator)
	 */
	public static List<FinderWhereClauseEntry> findWithDynamicQuery(
		DynamicQuery dynamicQuery, int start, int end,
		OrderByComparator<FinderWhereClauseEntry> orderByComparator) {

		return getPersistence().findWithDynamicQuery(
			dynamicQuery, start, end, orderByComparator);
	}

	/**
	 * @see com.liferay.portal.kernel.service.persistence.BasePersistence#update(com.liferay.portal.kernel.model.BaseModel)
	 */
	public static FinderWhereClauseEntry update(
		FinderWhereClauseEntry finderWhereClauseEntry) {

		return getPersistence().update(finderWhereClauseEntry);
	}

	/**
	 * @see com.liferay.portal.kernel.service.persistence.BasePersistence#update(com.liferay.portal.kernel.model.BaseModel, ServiceContext)
	 */
	public static FinderWhereClauseEntry update(
		FinderWhereClauseEntry finderWhereClauseEntry,
		ServiceContext serviceContext) {

		return getPersistence().update(finderWhereClauseEntry, serviceContext);
	}

	/**
	 * Returns an ordered range of all the finder where clause entries where headId = &#63;.
	 *
	 * <p>
	 * Useful when paginating results. Returns a maximum of <code>end - start</code> instances. <code>start</code> and <code>end</code> are not primary keys, they are indexes in the result set. Thus, <code>0</code> refers to the first result in the set. Setting both <code>start</code> and <code>end</code> to <code>com.liferay.portal.kernel.dao.orm.QueryUtil#ALL_POS</code> will return the full result set. If <code>orderByComparator</code> is specified, then the query will include the given ORDER BY logic. If <code>orderByComparator</code> is absent, then the query will include the default ORDER BY logic from <code>com.liferay.portal.tools.service.builder.test.model.impl.FinderWhereClauseEntryModelImpl</code>.
	 * </p>
	 *
	 * @param headId the head ID
	 * @param start the lower bound of the range of finder where clause entries
	 * @param end the upper bound of the range of finder where clause entries (not inclusive)
	 * @param orderByComparator the comparator to order the results by (optionally <code>null</code>)
	 * @param useFinderCache whether to use the finder cache
	 * @return the ordered range of matching finder where clause entries
	 */
	public static List<FinderWhereClauseEntry> findByHeadId(
		long headId, int start, int end,
		OrderByComparator<FinderWhereClauseEntry> orderByComparator,
		boolean useFinderCache) {

		return getPersistence().findByHeadId(
			headId, start, end, orderByComparator, useFinderCache);
	}

	/**
	 * Returns the first finder where clause entry in the ordered set where headId = &#63;.
	 *
	 * @param headId the head ID
	 * @param orderByComparator the comparator to order the set by (optionally <code>null</code>)
	 * @return the first matching finder where clause entry
	 * @throws NoSuchFinderWhereClauseEntryException if a matching finder where clause entry could not be found
	 */
	public static FinderWhereClauseEntry findByHeadId_First(
			long headId,
			OrderByComparator<FinderWhereClauseEntry> orderByComparator)
		throws com.liferay.portal.tools.service.builder.test.exception.
			NoSuchFinderWhereClauseEntryException {

		return getPersistence().findByHeadId_First(headId, orderByComparator);
	}

	/**
	 * Returns the first finder where clause entry in the ordered set where headId = &#63;.
	 *
	 * @param headId the head ID
	 * @param orderByComparator the comparator to order the set by (optionally <code>null</code>)
	 * @return the first matching finder where clause entry, or <code>null</code> if a matching finder where clause entry could not be found
	 */
	public static FinderWhereClauseEntry fetchByHeadId_First(
		long headId,
		OrderByComparator<FinderWhereClauseEntry> orderByComparator) {

		return getPersistence().fetchByHeadId_First(headId, orderByComparator);
	}

	/**
	 * Removes all the finder where clause entries where headId = &#63; from the database.
	 *
	 * @param headId the head ID
	 */
	public static void removeByHeadId(long headId) {
		getPersistence().removeByHeadId(headId);
	}

	/**
	 * Returns the number of finder where clause entries where headId = &#63;.
	 *
	 * @param headId the head ID
	 * @return the number of matching finder where clause entries
	 */
	public static int countByHeadId(long headId) {
		return getPersistence().countByHeadId(headId);
	}

	/**
	 * Returns an ordered range of all the finder where clause entries where name = &#63;.
	 *
	 * <p>
	 * Useful when paginating results. Returns a maximum of <code>end - start</code> instances. <code>start</code> and <code>end</code> are not primary keys, they are indexes in the result set. Thus, <code>0</code> refers to the first result in the set. Setting both <code>start</code> and <code>end</code> to <code>com.liferay.portal.kernel.dao.orm.QueryUtil#ALL_POS</code> will return the full result set. If <code>orderByComparator</code> is specified, then the query will include the given ORDER BY logic. If <code>orderByComparator</code> is absent, then the query will include the default ORDER BY logic from <code>com.liferay.portal.tools.service.builder.test.model.impl.FinderWhereClauseEntryModelImpl</code>.
	 * </p>
	 *
	 * @param name the name
	 * @param start the lower bound of the range of finder where clause entries
	 * @param end the upper bound of the range of finder where clause entries (not inclusive)
	 * @param orderByComparator the comparator to order the results by (optionally <code>null</code>)
	 * @param useFinderCache whether to use the finder cache
	 * @return the ordered range of matching finder where clause entries
	 */
	public static List<FinderWhereClauseEntry> findByName_Nickname(
		String name, int start, int end,
		OrderByComparator<FinderWhereClauseEntry> orderByComparator,
		boolean useFinderCache) {

		return getPersistence().findByName_Nickname(
			name, start, end, orderByComparator, useFinderCache);
	}

	/**
	 * Returns the first finder where clause entry in the ordered set where name = &#63;.
	 *
	 * @param name the name
	 * @param orderByComparator the comparator to order the set by (optionally <code>null</code>)
	 * @return the first matching finder where clause entry
	 * @throws NoSuchFinderWhereClauseEntryException if a matching finder where clause entry could not be found
	 */
	public static FinderWhereClauseEntry findByName_Nickname_First(
			String name,
			OrderByComparator<FinderWhereClauseEntry> orderByComparator)
		throws com.liferay.portal.tools.service.builder.test.exception.
			NoSuchFinderWhereClauseEntryException {

		return getPersistence().findByName_Nickname_First(
			name, orderByComparator);
	}

	/**
	 * Returns the first finder where clause entry in the ordered set where name = &#63;.
	 *
	 * @param name the name
	 * @param orderByComparator the comparator to order the set by (optionally <code>null</code>)
	 * @return the first matching finder where clause entry, or <code>null</code> if a matching finder where clause entry could not be found
	 */
	public static FinderWhereClauseEntry fetchByName_Nickname_First(
		String name,
		OrderByComparator<FinderWhereClauseEntry> orderByComparator) {

		return getPersistence().fetchByName_Nickname_First(
			name, orderByComparator);
	}

	/**
	 * Removes all the finder where clause entries where name = &#63; from the database.
	 *
	 * @param name the name
	 */
	public static void removeByName_Nickname(String name) {
		getPersistence().removeByName_Nickname(name);
	}

	/**
	 * Returns the number of finder where clause entries where name = &#63;.
	 *
	 * @param name the name
	 * @return the number of matching finder where clause entries
	 */
	public static int countByName_Nickname(String name) {
		return getPersistence().countByName_Nickname(name);
	}

	/**
	 * Returns an ordered range of all the finder where clause entries where status = &#63;.
	 *
	 * <p>
	 * Useful when paginating results. Returns a maximum of <code>end - start</code> instances. <code>start</code> and <code>end</code> are not primary keys, they are indexes in the result set. Thus, <code>0</code> refers to the first result in the set. Setting both <code>start</code> and <code>end</code> to <code>com.liferay.portal.kernel.dao.orm.QueryUtil#ALL_POS</code> will return the full result set. If <code>orderByComparator</code> is specified, then the query will include the given ORDER BY logic. If <code>orderByComparator</code> is absent, then the query will include the default ORDER BY logic from <code>com.liferay.portal.tools.service.builder.test.model.impl.FinderWhereClauseEntryModelImpl</code>.
	 * </p>
	 *
	 * @param status the status
	 * @param start the lower bound of the range of finder where clause entries
	 * @param end the upper bound of the range of finder where clause entries (not inclusive)
	 * @param orderByComparator the comparator to order the results by (optionally <code>null</code>)
	 * @param useFinderCache whether to use the finder cache
	 * @return the ordered range of matching finder where clause entries
	 */
	public static List<FinderWhereClauseEntry> findByStatus(
		int status, int start, int end,
		OrderByComparator<FinderWhereClauseEntry> orderByComparator,
		boolean useFinderCache) {

		return getPersistence().findByStatus(
			status, start, end, orderByComparator, useFinderCache);
	}

	/**
	 * Returns the first finder where clause entry in the ordered set where status = &#63;.
	 *
	 * @param status the status
	 * @param orderByComparator the comparator to order the set by (optionally <code>null</code>)
	 * @return the first matching finder where clause entry
	 * @throws NoSuchFinderWhereClauseEntryException if a matching finder where clause entry could not be found
	 */
	public static FinderWhereClauseEntry findByStatus_First(
			int status,
			OrderByComparator<FinderWhereClauseEntry> orderByComparator)
		throws com.liferay.portal.tools.service.builder.test.exception.
			NoSuchFinderWhereClauseEntryException {

		return getPersistence().findByStatus_First(status, orderByComparator);
	}

	/**
	 * Returns the first finder where clause entry in the ordered set where status = &#63;.
	 *
	 * @param status the status
	 * @param orderByComparator the comparator to order the set by (optionally <code>null</code>)
	 * @return the first matching finder where clause entry, or <code>null</code> if a matching finder where clause entry could not be found
	 */
	public static FinderWhereClauseEntry fetchByStatus_First(
		int status,
		OrderByComparator<FinderWhereClauseEntry> orderByComparator) {

		return getPersistence().fetchByStatus_First(status, orderByComparator);
	}

	/**
	 * Removes all the finder where clause entries where status = &#63; from the database.
	 *
	 * @param status the status
	 */
	public static void removeByStatus(int status) {
		getPersistence().removeByStatus(status);
	}

	/**
	 * Returns the number of finder where clause entries where status = &#63;.
	 *
	 * @param status the status
	 * @return the number of matching finder where clause entries
	 */
	public static int countByStatus(int status) {
		return getPersistence().countByStatus(status);
	}

	/**
	 * Returns an ordered range of all the finder where clause entries where name = &#63; and status = &#63;.
	 *
	 * <p>
	 * Useful when paginating results. Returns a maximum of <code>end - start</code> instances. <code>start</code> and <code>end</code> are not primary keys, they are indexes in the result set. Thus, <code>0</code> refers to the first result in the set. Setting both <code>start</code> and <code>end</code> to <code>com.liferay.portal.kernel.dao.orm.QueryUtil#ALL_POS</code> will return the full result set. If <code>orderByComparator</code> is specified, then the query will include the given ORDER BY logic. If <code>orderByComparator</code> is absent, then the query will include the default ORDER BY logic from <code>com.liferay.portal.tools.service.builder.test.model.impl.FinderWhereClauseEntryModelImpl</code>.
	 * </p>
	 *
	 * @param name the name
	 * @param status the status
	 * @param start the lower bound of the range of finder where clause entries
	 * @param end the upper bound of the range of finder where clause entries (not inclusive)
	 * @param orderByComparator the comparator to order the results by (optionally <code>null</code>)
	 * @param useFinderCache whether to use the finder cache
	 * @return the ordered range of matching finder where clause entries
	 */
	public static List<FinderWhereClauseEntry> findByName_Status(
		String name, int status, int start, int end,
		OrderByComparator<FinderWhereClauseEntry> orderByComparator,
		boolean useFinderCache) {

		return getPersistence().findByName_Status(
			name, status, start, end, orderByComparator, useFinderCache);
	}

	/**
	 * Returns the first finder where clause entry in the ordered set where name = &#63; and status = &#63;.
	 *
	 * @param name the name
	 * @param status the status
	 * @param orderByComparator the comparator to order the set by (optionally <code>null</code>)
	 * @return the first matching finder where clause entry
	 * @throws NoSuchFinderWhereClauseEntryException if a matching finder where clause entry could not be found
	 */
	public static FinderWhereClauseEntry findByName_Status_First(
			String name, int status,
			OrderByComparator<FinderWhereClauseEntry> orderByComparator)
		throws com.liferay.portal.tools.service.builder.test.exception.
			NoSuchFinderWhereClauseEntryException {

		return getPersistence().findByName_Status_First(
			name, status, orderByComparator);
	}

	/**
	 * Returns the first finder where clause entry in the ordered set where name = &#63; and status = &#63;.
	 *
	 * @param name the name
	 * @param status the status
	 * @param orderByComparator the comparator to order the set by (optionally <code>null</code>)
	 * @return the first matching finder where clause entry, or <code>null</code> if a matching finder where clause entry could not be found
	 */
	public static FinderWhereClauseEntry fetchByName_Status_First(
		String name, int status,
		OrderByComparator<FinderWhereClauseEntry> orderByComparator) {

		return getPersistence().fetchByName_Status_First(
			name, status, orderByComparator);
	}

	/**
	 * Removes all the finder where clause entries where name = &#63; and status = &#63; from the database.
	 *
	 * @param name the name
	 * @param status the status
	 */
	public static void removeByName_Status(String name, int status) {
		getPersistence().removeByName_Status(name, status);
	}

	/**
	 * Returns the number of finder where clause entries where name = &#63; and status = &#63;.
	 *
	 * @param name the name
	 * @param status the status
	 * @return the number of matching finder where clause entries
	 */
	public static int countByName_Status(String name, int status) {
		return getPersistence().countByName_Status(name, status);
	}

	/**
	 * Creates a new finder where clause entry with the primary key. Does not add the finder where clause entry to the database.
	 *
	 * @param finderWhereClauseEntryId the primary key for the new finder where clause entry
	 * @return the new finder where clause entry
	 */
	public static FinderWhereClauseEntry create(long finderWhereClauseEntryId) {
		return getPersistence().create(finderWhereClauseEntryId);
	}

	/**
	 * Removes the finder where clause entry with the primary key from the database. Also notifies the appropriate model listeners.
	 *
	 * @param finderWhereClauseEntryId the primary key of the finder where clause entry
	 * @return the finder where clause entry that was removed
	 * @throws NoSuchFinderWhereClauseEntryException if a finder where clause entry with the primary key could not be found
	 */
	public static FinderWhereClauseEntry remove(long finderWhereClauseEntryId)
		throws com.liferay.portal.tools.service.builder.test.exception.
			NoSuchFinderWhereClauseEntryException {

		return getPersistence().remove(finderWhereClauseEntryId);
	}

	public static FinderWhereClauseEntry updateImpl(
		FinderWhereClauseEntry finderWhereClauseEntry) {

		return getPersistence().updateImpl(finderWhereClauseEntry);
	}

	/**
	 * Returns the finder where clause entry with the primary key or throws a <code>NoSuchFinderWhereClauseEntryException</code> if it could not be found.
	 *
	 * @param finderWhereClauseEntryId the primary key of the finder where clause entry
	 * @return the finder where clause entry
	 * @throws NoSuchFinderWhereClauseEntryException if a finder where clause entry with the primary key could not be found
	 */
	public static FinderWhereClauseEntry findByPrimaryKey(
			long finderWhereClauseEntryId)
		throws com.liferay.portal.tools.service.builder.test.exception.
			NoSuchFinderWhereClauseEntryException {

		return getPersistence().findByPrimaryKey(finderWhereClauseEntryId);
	}

	/**
	 * Returns the finder where clause entry with the primary key or returns <code>null</code> if it could not be found.
	 *
	 * @param finderWhereClauseEntryId the primary key of the finder where clause entry
	 * @return the finder where clause entry, or <code>null</code> if a finder where clause entry with the primary key could not be found
	 */
	public static FinderWhereClauseEntry fetchByPrimaryKey(
		long finderWhereClauseEntryId) {

		return getPersistence().fetchByPrimaryKey(finderWhereClauseEntryId);
	}

	/**
	 * Returns all the finder where clause entries where headId = &#63;.
	 *
	 * @param headId the head ID
	 * @return the matching finder where clause entries
	 */
	public static List<FinderWhereClauseEntry> findByHeadId(long headId) {
		return getPersistence().findByHeadId(headId);
	}

	/**
	 * Returns a range of all the finder where clause entries where headId = &#63;.
	 *
	 * <p>
	 * Useful when paginating results. Returns a maximum of <code>end - start</code> instances. <code>start</code> and <code>end</code> are not primary keys, they are indexes in the result set. Thus, <code>0</code> refers to the first result in the set. Setting both <code>start</code> and <code>end</code> to <code>com.liferay.portal.kernel.dao.orm.QueryUtil#ALL_POS</code> will return the full result set. If <code>orderByComparator</code> is specified, then the query will include the given ORDER BY logic. If <code>orderByComparator</code> is absent, then the query will include the default ORDER BY logic from <code>com.liferay.portal.tools.service.builder.test.model.impl.FinderWhereClauseEntryModelImpl</code>.
	 * </p>
	 *
	 * @param headId the head ID
	 * @param start the lower bound of the range of finder where clause entries
	 * @param end the upper bound of the range of finder where clause entries (not inclusive)
	 * @return the range of matching finder where clause entries
	 */
	public static List<FinderWhereClauseEntry> findByHeadId(
		long headId, int start, int end) {

		return getPersistence().findByHeadId(headId, start, end);
	}

	/**
	 * Returns an ordered range of all the finder where clause entries where headId = &#63;.
	 *
	 * <p>
	 * Useful when paginating results. Returns a maximum of <code>end - start</code> instances. <code>start</code> and <code>end</code> are not primary keys, they are indexes in the result set. Thus, <code>0</code> refers to the first result in the set. Setting both <code>start</code> and <code>end</code> to <code>com.liferay.portal.kernel.dao.orm.QueryUtil#ALL_POS</code> will return the full result set. If <code>orderByComparator</code> is specified, then the query will include the given ORDER BY logic. If <code>orderByComparator</code> is absent, then the query will include the default ORDER BY logic from <code>com.liferay.portal.tools.service.builder.test.model.impl.FinderWhereClauseEntryModelImpl</code>.
	 * </p>
	 *
	 * @param headId the head ID
	 * @param start the lower bound of the range of finder where clause entries
	 * @param end the upper bound of the range of finder where clause entries (not inclusive)
	 * @param orderByComparator the comparator to order the results by (optionally <code>null</code>)
	 * @return the ordered range of matching finder where clause entries
	 */
	public static List<FinderWhereClauseEntry> findByHeadId(
		long headId, int start, int end,
		OrderByComparator<FinderWhereClauseEntry> orderByComparator) {

		return getPersistence().findByHeadId(
			headId, start, end, orderByComparator);
	}

	/**
	 * Returns all the finder where clause entries where name = &#63;.
	 *
	 * @param name the name
	 * @return the matching finder where clause entries
	 */
	public static List<FinderWhereClauseEntry> findByName_Nickname(
		String name) {

		return getPersistence().findByName_Nickname(name);
	}

	/**
	 * Returns a range of all the finder where clause entries where name = &#63;.
	 *
	 * <p>
	 * Useful when paginating results. Returns a maximum of <code>end - start</code> instances. <code>start</code> and <code>end</code> are not primary keys, they are indexes in the result set. Thus, <code>0</code> refers to the first result in the set. Setting both <code>start</code> and <code>end</code> to <code>com.liferay.portal.kernel.dao.orm.QueryUtil#ALL_POS</code> will return the full result set. If <code>orderByComparator</code> is specified, then the query will include the given ORDER BY logic. If <code>orderByComparator</code> is absent, then the query will include the default ORDER BY logic from <code>com.liferay.portal.tools.service.builder.test.model.impl.FinderWhereClauseEntryModelImpl</code>.
	 * </p>
	 *
	 * @param name the name
	 * @param start the lower bound of the range of finder where clause entries
	 * @param end the upper bound of the range of finder where clause entries (not inclusive)
	 * @return the range of matching finder where clause entries
	 */
	public static List<FinderWhereClauseEntry> findByName_Nickname(
		String name, int start, int end) {

		return getPersistence().findByName_Nickname(name, start, end);
	}

	/**
	 * Returns an ordered range of all the finder where clause entries where name = &#63;.
	 *
	 * <p>
	 * Useful when paginating results. Returns a maximum of <code>end - start</code> instances. <code>start</code> and <code>end</code> are not primary keys, they are indexes in the result set. Thus, <code>0</code> refers to the first result in the set. Setting both <code>start</code> and <code>end</code> to <code>com.liferay.portal.kernel.dao.orm.QueryUtil#ALL_POS</code> will return the full result set. If <code>orderByComparator</code> is specified, then the query will include the given ORDER BY logic. If <code>orderByComparator</code> is absent, then the query will include the default ORDER BY logic from <code>com.liferay.portal.tools.service.builder.test.model.impl.FinderWhereClauseEntryModelImpl</code>.
	 * </p>
	 *
	 * @param name the name
	 * @param start the lower bound of the range of finder where clause entries
	 * @param end the upper bound of the range of finder where clause entries (not inclusive)
	 * @param orderByComparator the comparator to order the results by (optionally <code>null</code>)
	 * @return the ordered range of matching finder where clause entries
	 */
	public static List<FinderWhereClauseEntry> findByName_Nickname(
		String name, int start, int end,
		OrderByComparator<FinderWhereClauseEntry> orderByComparator) {

		return getPersistence().findByName_Nickname(
			name, start, end, orderByComparator);
	}

	/**
	 * Returns all the finder where clause entries where status = &#63;.
	 *
	 * @param status the status
	 * @return the matching finder where clause entries
	 */
	public static List<FinderWhereClauseEntry> findByStatus(int status) {
		return getPersistence().findByStatus(status);
	}

	/**
	 * Returns a range of all the finder where clause entries where status = &#63;.
	 *
	 * <p>
	 * Useful when paginating results. Returns a maximum of <code>end - start</code> instances. <code>start</code> and <code>end</code> are not primary keys, they are indexes in the result set. Thus, <code>0</code> refers to the first result in the set. Setting both <code>start</code> and <code>end</code> to <code>com.liferay.portal.kernel.dao.orm.QueryUtil#ALL_POS</code> will return the full result set. If <code>orderByComparator</code> is specified, then the query will include the given ORDER BY logic. If <code>orderByComparator</code> is absent, then the query will include the default ORDER BY logic from <code>com.liferay.portal.tools.service.builder.test.model.impl.FinderWhereClauseEntryModelImpl</code>.
	 * </p>
	 *
	 * @param status the status
	 * @param start the lower bound of the range of finder where clause entries
	 * @param end the upper bound of the range of finder where clause entries (not inclusive)
	 * @return the range of matching finder where clause entries
	 */
	public static List<FinderWhereClauseEntry> findByStatus(
		int status, int start, int end) {

		return getPersistence().findByStatus(status, start, end);
	}

	/**
	 * Returns an ordered range of all the finder where clause entries where status = &#63;.
	 *
	 * <p>
	 * Useful when paginating results. Returns a maximum of <code>end - start</code> instances. <code>start</code> and <code>end</code> are not primary keys, they are indexes in the result set. Thus, <code>0</code> refers to the first result in the set. Setting both <code>start</code> and <code>end</code> to <code>com.liferay.portal.kernel.dao.orm.QueryUtil#ALL_POS</code> will return the full result set. If <code>orderByComparator</code> is specified, then the query will include the given ORDER BY logic. If <code>orderByComparator</code> is absent, then the query will include the default ORDER BY logic from <code>com.liferay.portal.tools.service.builder.test.model.impl.FinderWhereClauseEntryModelImpl</code>.
	 * </p>
	 *
	 * @param status the status
	 * @param start the lower bound of the range of finder where clause entries
	 * @param end the upper bound of the range of finder where clause entries (not inclusive)
	 * @param orderByComparator the comparator to order the results by (optionally <code>null</code>)
	 * @return the ordered range of matching finder where clause entries
	 */
	public static List<FinderWhereClauseEntry> findByStatus(
		int status, int start, int end,
		OrderByComparator<FinderWhereClauseEntry> orderByComparator) {

		return getPersistence().findByStatus(
			status, start, end, orderByComparator);
	}

	/**
	 * Returns all the finder where clause entries where name = &#63; and status = &#63;.
	 *
	 * @param name the name
	 * @param status the status
	 * @return the matching finder where clause entries
	 */
	public static List<FinderWhereClauseEntry> findByName_Status(
		String name, int status) {

		return getPersistence().findByName_Status(name, status);
	}

	/**
	 * Returns a range of all the finder where clause entries where name = &#63; and status = &#63;.
	 *
	 * <p>
	 * Useful when paginating results. Returns a maximum of <code>end - start</code> instances. <code>start</code> and <code>end</code> are not primary keys, they are indexes in the result set. Thus, <code>0</code> refers to the first result in the set. Setting both <code>start</code> and <code>end</code> to <code>com.liferay.portal.kernel.dao.orm.QueryUtil#ALL_POS</code> will return the full result set. If <code>orderByComparator</code> is specified, then the query will include the given ORDER BY logic. If <code>orderByComparator</code> is absent, then the query will include the default ORDER BY logic from <code>com.liferay.portal.tools.service.builder.test.model.impl.FinderWhereClauseEntryModelImpl</code>.
	 * </p>
	 *
	 * @param name the name
	 * @param status the status
	 * @param start the lower bound of the range of finder where clause entries
	 * @param end the upper bound of the range of finder where clause entries (not inclusive)
	 * @return the range of matching finder where clause entries
	 */
	public static List<FinderWhereClauseEntry> findByName_Status(
		String name, int status, int start, int end) {

		return getPersistence().findByName_Status(name, status, start, end);
	}

	/**
	 * Returns an ordered range of all the finder where clause entries where name = &#63; and status = &#63;.
	 *
	 * <p>
	 * Useful when paginating results. Returns a maximum of <code>end - start</code> instances. <code>start</code> and <code>end</code> are not primary keys, they are indexes in the result set. Thus, <code>0</code> refers to the first result in the set. Setting both <code>start</code> and <code>end</code> to <code>com.liferay.portal.kernel.dao.orm.QueryUtil#ALL_POS</code> will return the full result set. If <code>orderByComparator</code> is specified, then the query will include the given ORDER BY logic. If <code>orderByComparator</code> is absent, then the query will include the default ORDER BY logic from <code>com.liferay.portal.tools.service.builder.test.model.impl.FinderWhereClauseEntryModelImpl</code>.
	 * </p>
	 *
	 * @param name the name
	 * @param status the status
	 * @param start the lower bound of the range of finder where clause entries
	 * @param end the upper bound of the range of finder where clause entries (not inclusive)
	 * @param orderByComparator the comparator to order the results by (optionally <code>null</code>)
	 * @return the ordered range of matching finder where clause entries
	 */
	public static List<FinderWhereClauseEntry> findByName_Status(
		String name, int status, int start, int end,
		OrderByComparator<FinderWhereClauseEntry> orderByComparator) {

		return getPersistence().findByName_Status(
			name, status, start, end, orderByComparator);
	}

	public static FinderWhereClauseEntryPersistence getPersistence() {
		return _persistence;
	}

	public static void setPersistence(
		FinderWhereClauseEntryPersistence persistence) {

		_persistence = persistence;
	}

	private static volatile FinderWhereClauseEntryPersistence _persistence;

}
// LIFERAY-SERVICE-BUILDER-HASH:1431613585