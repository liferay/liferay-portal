/**
 * SPDX-FileCopyrightText: (c) 2000 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

package com.liferay.account.service.persistence;

import com.liferay.account.model.AccountRole;
import com.liferay.portal.kernel.dao.orm.DynamicQuery;
import com.liferay.portal.kernel.service.ServiceContext;
import com.liferay.portal.kernel.util.OrderByComparator;

import java.io.Serializable;

import java.util.List;
import java.util.Map;
import java.util.Set;

/**
 * The persistence utility for the account role service. This utility wraps <code>com.liferay.account.service.persistence.impl.AccountRolePersistenceImpl</code> and provides direct access to the database for CRUD operations. This utility should only be used by the service layer, as it must operate within a transaction. Never access this utility in a JSP, controller, model, or other front-end class.
 *
 * <p>
 * Caching information and settings can be found in <code>portal.properties</code>
 * </p>
 *
 * @author Brian Wing Shun Chan
 * @see AccountRolePersistence
 * @generated
 */
public class AccountRoleUtil {

	/*
	 * NOTE FOR DEVELOPERS:
	 *
	 * Never modify this class directly. Modify <code>service.xml</code> and rerun ServiceBuilder to regenerate this class.
	 */

	/**
	 * @see com.liferay.portal.kernel.service.persistence.BasePersistence#cacheResult(List)
	 */
	public static void cacheResult(List<AccountRole> accountRoles) {
		getPersistence().cacheResult(accountRoles);
	}

	/**
	 * @see com.liferay.portal.kernel.service.persistence.BasePersistence#cacheResult(com.liferay.portal.kernel.model.BaseModel)
	 */
	public static void cacheResult(AccountRole accountRole) {
		getPersistence().cacheResult(accountRole);
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
	public static void clearCache(AccountRole accountRole) {
		getPersistence().clearCache(accountRole);
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
	public static Map<Serializable, AccountRole> fetchByPrimaryKeys(
		Set<Serializable> primaryKeys) {

		return getPersistence().fetchByPrimaryKeys(primaryKeys);
	}

	/**
	 * @see com.liferay.portal.kernel.service.persistence.BasePersistence#findWithDynamicQuery(DynamicQuery)
	 */
	public static List<AccountRole> findWithDynamicQuery(
		DynamicQuery dynamicQuery) {

		return getPersistence().findWithDynamicQuery(dynamicQuery);
	}

	/**
	 * @see com.liferay.portal.kernel.service.persistence.BasePersistence#findWithDynamicQuery(DynamicQuery, int, int)
	 */
	public static List<AccountRole> findWithDynamicQuery(
		DynamicQuery dynamicQuery, int start, int end) {

		return getPersistence().findWithDynamicQuery(dynamicQuery, start, end);
	}

	/**
	 * @see com.liferay.portal.kernel.service.persistence.BasePersistence#findWithDynamicQuery(DynamicQuery, int, int, OrderByComparator)
	 */
	public static List<AccountRole> findWithDynamicQuery(
		DynamicQuery dynamicQuery, int start, int end,
		OrderByComparator<AccountRole> orderByComparator) {

		return getPersistence().findWithDynamicQuery(
			dynamicQuery, start, end, orderByComparator);
	}

	/**
	 * @see com.liferay.portal.kernel.service.persistence.BasePersistence#update(com.liferay.portal.kernel.model.BaseModel)
	 */
	public static AccountRole update(AccountRole accountRole) {
		return getPersistence().update(accountRole);
	}

	/**
	 * @see com.liferay.portal.kernel.service.persistence.BasePersistence#update(com.liferay.portal.kernel.model.BaseModel, ServiceContext)
	 */
	public static AccountRole update(
		AccountRole accountRole, ServiceContext serviceContext) {

		return getPersistence().update(accountRole, serviceContext);
	}

	/**
	 * Returns an ordered range of all the account roles where accountEntryId = &#63;.
	 *
	 * <p>
	 * Useful when paginating results. Returns a maximum of <code>end - start</code> instances. <code>start</code> and <code>end</code> are not primary keys, they are indexes in the result set. Thus, <code>0</code> refers to the first result in the set. Setting both <code>start</code> and <code>end</code> to <code>com.liferay.portal.kernel.dao.orm.QueryUtil#ALL_POS</code> will return the full result set. If <code>orderByComparator</code> is specified, then the query will include the given ORDER BY logic. If <code>orderByComparator</code> is absent, then the query will include the default ORDER BY logic from <code>com.liferay.account.model.impl.AccountRoleModelImpl</code>.
	 * </p>
	 *
	 * @param accountEntryId the account entry ID
	 * @param start the lower bound of the range of account roles
	 * @param end the upper bound of the range of account roles (not inclusive)
	 * @param orderByComparator the comparator to order the results by (optionally <code>null</code>)
	 * @param useFinderCache whether to use the finder cache
	 * @return the ordered range of matching account roles
	 */
	public static List<AccountRole> findByAccountEntryId(
		long accountEntryId, int start, int end,
		OrderByComparator<AccountRole> orderByComparator,
		boolean useFinderCache) {

		return getPersistence().findByAccountEntryId(
			accountEntryId, start, end, orderByComparator, useFinderCache);
	}

	/**
	 * Returns the first account role in the ordered set where accountEntryId = &#63;.
	 *
	 * @param accountEntryId the account entry ID
	 * @param orderByComparator the comparator to order the set by (optionally <code>null</code>)
	 * @return the first matching account role
	 * @throws NoSuchRoleException if a matching account role could not be found
	 */
	public static AccountRole findByAccountEntryId_First(
			long accountEntryId,
			OrderByComparator<AccountRole> orderByComparator)
		throws com.liferay.account.exception.NoSuchRoleException {

		return getPersistence().findByAccountEntryId_First(
			accountEntryId, orderByComparator);
	}

	/**
	 * Returns the first account role in the ordered set where accountEntryId = &#63;.
	 *
	 * @param accountEntryId the account entry ID
	 * @param orderByComparator the comparator to order the set by (optionally <code>null</code>)
	 * @return the first matching account role, or <code>null</code> if a matching account role could not be found
	 */
	public static AccountRole fetchByAccountEntryId_First(
		long accountEntryId, OrderByComparator<AccountRole> orderByComparator) {

		return getPersistence().fetchByAccountEntryId_First(
			accountEntryId, orderByComparator);
	}

	/**
	 * Returns an ordered range of all the account roles that the user has permissions to view where accountEntryId = &#63;.
	 *
	 * <p>
	 * Useful when paginating results. Returns a maximum of <code>end - start</code> instances. <code>start</code> and <code>end</code> are not primary keys, they are indexes in the result set. Thus, <code>0</code> refers to the first result in the set. Setting both <code>start</code> and <code>end</code> to <code>com.liferay.portal.kernel.dao.orm.QueryUtil#ALL_POS</code> will return the full result set. If <code>orderByComparator</code> is specified, then the query will include the given ORDER BY logic. If <code>orderByComparator</code> is absent, then the query will include the default ORDER BY logic from <code>com.liferay.account.model.impl.AccountRoleModelImpl</code>.
	 * </p>
	 *
	 * @param accountEntryId the account entry ID
	 * @param start the lower bound of the range of account roles
	 * @param end the upper bound of the range of account roles (not inclusive)
	 * @param orderByComparator the comparator to order the results by (optionally <code>null</code>)
	 * @return the ordered range of matching account roles that the user has permission to view
	 */
	public static List<AccountRole> filterFindByAccountEntryId(
		long accountEntryId, int start, int end,
		OrderByComparator<AccountRole> orderByComparator) {

		return getPersistence().filterFindByAccountEntryId(
			accountEntryId, start, end, orderByComparator);
	}

	/**
	 * Returns an ordered range of all the account roles that the user has permission to view where accountEntryId = any &#63;.
	 *
	 * <p>
	 * Useful when paginating results. Returns a maximum of <code>end - start</code> instances. <code>start</code> and <code>end</code> are not primary keys, they are indexes in the result set. Thus, <code>0</code> refers to the first result in the set. Setting both <code>start</code> and <code>end</code> to <code>com.liferay.portal.kernel.dao.orm.QueryUtil#ALL_POS</code> will return the full result set. If <code>orderByComparator</code> is specified, then the query will include the given ORDER BY logic. If <code>orderByComparator</code> is absent, then the query will include the default ORDER BY logic from <code>com.liferay.account.model.impl.AccountRoleModelImpl</code>.
	 * </p>
	 *
	 * @param accountEntryIds the account entry IDs
	 * @param start the lower bound of the range of account roles
	 * @param end the upper bound of the range of account roles (not inclusive)
	 * @param orderByComparator the comparator to order the results by (optionally <code>null</code>)
	 * @return the ordered range of matching account roles that the user has permission to view
	 */
	public static List<AccountRole> filterFindByAccountEntryId(
		long[] accountEntryIds, int start, int end,
		OrderByComparator<AccountRole> orderByComparator) {

		return getPersistence().filterFindByAccountEntryId(
			accountEntryIds, start, end, orderByComparator);
	}

	/**
	 * Returns an ordered range of all the account roles where accountEntryId = &#63;, optionally using the finder cache.
	 *
	 * <p>
	 * Useful when paginating results. Returns a maximum of <code>end - start</code> instances. <code>start</code> and <code>end</code> are not primary keys, they are indexes in the result set. Thus, <code>0</code> refers to the first result in the set. Setting both <code>start</code> and <code>end</code> to <code>com.liferay.portal.kernel.dao.orm.QueryUtil#ALL_POS</code> will return the full result set. If <code>orderByComparator</code> is specified, then the query will include the given ORDER BY logic. If <code>orderByComparator</code> is absent, then the query will include the default ORDER BY logic from <code>com.liferay.account.model.impl.AccountRoleModelImpl</code>.
	 * </p>
	 *
	 * @param accountEntryIds the account entry IDs
	 * @param start the lower bound of the range of account roles
	 * @param end the upper bound of the range of account roles (not inclusive)
	 * @param orderByComparator the comparator to order the results by (optionally <code>null</code>)
	 * @param useFinderCache whether to use the finder cache
	 * @return the ordered range of matching account roles
	 */
	public static List<AccountRole> findByAccountEntryId(
		long[] accountEntryIds, int start, int end,
		OrderByComparator<AccountRole> orderByComparator,
		boolean useFinderCache) {

		return getPersistence().findByAccountEntryId(
			accountEntryIds, start, end, orderByComparator, useFinderCache);
	}

	/**
	 * Removes all the account roles where accountEntryId = &#63; from the database.
	 *
	 * @param accountEntryId the account entry ID
	 */
	public static void removeByAccountEntryId(long accountEntryId) {
		getPersistence().removeByAccountEntryId(accountEntryId);
	}

	/**
	 * Returns the number of account roles where accountEntryId = &#63;.
	 *
	 * @param accountEntryId the account entry ID
	 * @return the number of matching account roles
	 */
	public static int countByAccountEntryId(long accountEntryId) {
		return getPersistence().countByAccountEntryId(accountEntryId);
	}

	/**
	 * Returns the number of account roles where accountEntryId = any &#63;.
	 *
	 * @param accountEntryIds the account entry IDs
	 * @return the number of matching account roles
	 */
	public static int countByAccountEntryId(long[] accountEntryIds) {
		return getPersistence().countByAccountEntryId(accountEntryIds);
	}

	/**
	 * Returns the number of account roles that the user has permission to view where accountEntryId = &#63;.
	 *
	 * @param accountEntryId the account entry ID
	 * @return the number of matching account roles that the user has permission to view
	 */
	public static int filterCountByAccountEntryId(long accountEntryId) {
		return getPersistence().filterCountByAccountEntryId(accountEntryId);
	}

	/**
	 * Returns the number of account roles that the user has permission to view where accountEntryId = any &#63;.
	 *
	 * @param accountEntryIds the account entry IDs
	 * @return the number of matching account roles that the user has permission to view
	 */
	public static int filterCountByAccountEntryId(long[] accountEntryIds) {
		return getPersistence().filterCountByAccountEntryId(accountEntryIds);
	}

	/**
	 * Returns the account role where roleId = &#63; or throws a <code>NoSuchRoleException</code> if it could not be found.
	 *
	 * @param roleId the role ID
	 * @return the matching account role
	 * @throws NoSuchRoleException if a matching account role could not be found
	 */
	public static AccountRole findByRoleId(long roleId)
		throws com.liferay.account.exception.NoSuchRoleException {

		return getPersistence().findByRoleId(roleId);
	}

	/**
	 * Returns the account role where roleId = &#63; or returns <code>null</code> if it could not be found, optionally using the finder cache.
	 *
	 * @param roleId the role ID
	 * @param useFinderCache whether to use the finder cache
	 * @return the matching account role, or <code>null</code> if a matching account role could not be found
	 */
	public static AccountRole fetchByRoleId(
		long roleId, boolean useFinderCache) {

		return getPersistence().fetchByRoleId(roleId, useFinderCache);
	}

	/**
	 * Removes the account role where roleId = &#63; from the database.
	 *
	 * @param roleId the role ID
	 * @return the account role that was removed
	 */
	public static AccountRole removeByRoleId(long roleId)
		throws com.liferay.account.exception.NoSuchRoleException {

		return getPersistence().removeByRoleId(roleId);
	}

	/**
	 * Returns the number of account roles where roleId = &#63;.
	 *
	 * @param roleId the role ID
	 * @return the number of matching account roles
	 */
	public static int countByRoleId(long roleId) {
		return getPersistence().countByRoleId(roleId);
	}

	/**
	 * Returns an ordered range of all the account roles where companyId = &#63; and accountEntryId = &#63;.
	 *
	 * <p>
	 * Useful when paginating results. Returns a maximum of <code>end - start</code> instances. <code>start</code> and <code>end</code> are not primary keys, they are indexes in the result set. Thus, <code>0</code> refers to the first result in the set. Setting both <code>start</code> and <code>end</code> to <code>com.liferay.portal.kernel.dao.orm.QueryUtil#ALL_POS</code> will return the full result set. If <code>orderByComparator</code> is specified, then the query will include the given ORDER BY logic. If <code>orderByComparator</code> is absent, then the query will include the default ORDER BY logic from <code>com.liferay.account.model.impl.AccountRoleModelImpl</code>.
	 * </p>
	 *
	 * @param companyId the company ID
	 * @param accountEntryId the account entry ID
	 * @param start the lower bound of the range of account roles
	 * @param end the upper bound of the range of account roles (not inclusive)
	 * @param orderByComparator the comparator to order the results by (optionally <code>null</code>)
	 * @param useFinderCache whether to use the finder cache
	 * @return the ordered range of matching account roles
	 */
	public static List<AccountRole> findByC_A(
		long companyId, long accountEntryId, int start, int end,
		OrderByComparator<AccountRole> orderByComparator,
		boolean useFinderCache) {

		return getPersistence().findByC_A(
			companyId, accountEntryId, start, end, orderByComparator,
			useFinderCache);
	}

	/**
	 * Returns the first account role in the ordered set where companyId = &#63; and accountEntryId = &#63;.
	 *
	 * @param companyId the company ID
	 * @param accountEntryId the account entry ID
	 * @param orderByComparator the comparator to order the set by (optionally <code>null</code>)
	 * @return the first matching account role
	 * @throws NoSuchRoleException if a matching account role could not be found
	 */
	public static AccountRole findByC_A_First(
			long companyId, long accountEntryId,
			OrderByComparator<AccountRole> orderByComparator)
		throws com.liferay.account.exception.NoSuchRoleException {

		return getPersistence().findByC_A_First(
			companyId, accountEntryId, orderByComparator);
	}

	/**
	 * Returns the first account role in the ordered set where companyId = &#63; and accountEntryId = &#63;.
	 *
	 * @param companyId the company ID
	 * @param accountEntryId the account entry ID
	 * @param orderByComparator the comparator to order the set by (optionally <code>null</code>)
	 * @return the first matching account role, or <code>null</code> if a matching account role could not be found
	 */
	public static AccountRole fetchByC_A_First(
		long companyId, long accountEntryId,
		OrderByComparator<AccountRole> orderByComparator) {

		return getPersistence().fetchByC_A_First(
			companyId, accountEntryId, orderByComparator);
	}

	/**
	 * Returns an ordered range of all the account roles that the user has permissions to view where companyId = &#63; and accountEntryId = &#63;.
	 *
	 * <p>
	 * Useful when paginating results. Returns a maximum of <code>end - start</code> instances. <code>start</code> and <code>end</code> are not primary keys, they are indexes in the result set. Thus, <code>0</code> refers to the first result in the set. Setting both <code>start</code> and <code>end</code> to <code>com.liferay.portal.kernel.dao.orm.QueryUtil#ALL_POS</code> will return the full result set. If <code>orderByComparator</code> is specified, then the query will include the given ORDER BY logic. If <code>orderByComparator</code> is absent, then the query will include the default ORDER BY logic from <code>com.liferay.account.model.impl.AccountRoleModelImpl</code>.
	 * </p>
	 *
	 * @param companyId the company ID
	 * @param accountEntryId the account entry ID
	 * @param start the lower bound of the range of account roles
	 * @param end the upper bound of the range of account roles (not inclusive)
	 * @param orderByComparator the comparator to order the results by (optionally <code>null</code>)
	 * @return the ordered range of matching account roles that the user has permission to view
	 */
	public static List<AccountRole> filterFindByC_A(
		long companyId, long accountEntryId, int start, int end,
		OrderByComparator<AccountRole> orderByComparator) {

		return getPersistence().filterFindByC_A(
			companyId, accountEntryId, start, end, orderByComparator);
	}

	/**
	 * Returns an ordered range of all the account roles that the user has permission to view where companyId = &#63; and accountEntryId = any &#63;.
	 *
	 * <p>
	 * Useful when paginating results. Returns a maximum of <code>end - start</code> instances. <code>start</code> and <code>end</code> are not primary keys, they are indexes in the result set. Thus, <code>0</code> refers to the first result in the set. Setting both <code>start</code> and <code>end</code> to <code>com.liferay.portal.kernel.dao.orm.QueryUtil#ALL_POS</code> will return the full result set. If <code>orderByComparator</code> is specified, then the query will include the given ORDER BY logic. If <code>orderByComparator</code> is absent, then the query will include the default ORDER BY logic from <code>com.liferay.account.model.impl.AccountRoleModelImpl</code>.
	 * </p>
	 *
	 * @param companyId the company ID
	 * @param accountEntryIds the account entry IDs
	 * @param start the lower bound of the range of account roles
	 * @param end the upper bound of the range of account roles (not inclusive)
	 * @param orderByComparator the comparator to order the results by (optionally <code>null</code>)
	 * @return the ordered range of matching account roles that the user has permission to view
	 */
	public static List<AccountRole> filterFindByC_A(
		long companyId, long[] accountEntryIds, int start, int end,
		OrderByComparator<AccountRole> orderByComparator) {

		return getPersistence().filterFindByC_A(
			companyId, accountEntryIds, start, end, orderByComparator);
	}

	/**
	 * Returns an ordered range of all the account roles where companyId = &#63; and accountEntryId = &#63;, optionally using the finder cache.
	 *
	 * <p>
	 * Useful when paginating results. Returns a maximum of <code>end - start</code> instances. <code>start</code> and <code>end</code> are not primary keys, they are indexes in the result set. Thus, <code>0</code> refers to the first result in the set. Setting both <code>start</code> and <code>end</code> to <code>com.liferay.portal.kernel.dao.orm.QueryUtil#ALL_POS</code> will return the full result set. If <code>orderByComparator</code> is specified, then the query will include the given ORDER BY logic. If <code>orderByComparator</code> is absent, then the query will include the default ORDER BY logic from <code>com.liferay.account.model.impl.AccountRoleModelImpl</code>.
	 * </p>
	 *
	 * @param companyId the company ID
	 * @param accountEntryIds the account entry IDs
	 * @param start the lower bound of the range of account roles
	 * @param end the upper bound of the range of account roles (not inclusive)
	 * @param orderByComparator the comparator to order the results by (optionally <code>null</code>)
	 * @param useFinderCache whether to use the finder cache
	 * @return the ordered range of matching account roles
	 */
	public static List<AccountRole> findByC_A(
		long companyId, long[] accountEntryIds, int start, int end,
		OrderByComparator<AccountRole> orderByComparator,
		boolean useFinderCache) {

		return getPersistence().findByC_A(
			companyId, accountEntryIds, start, end, orderByComparator,
			useFinderCache);
	}

	/**
	 * Removes all the account roles where companyId = &#63; and accountEntryId = &#63; from the database.
	 *
	 * @param companyId the company ID
	 * @param accountEntryId the account entry ID
	 */
	public static void removeByC_A(long companyId, long accountEntryId) {
		getPersistence().removeByC_A(companyId, accountEntryId);
	}

	/**
	 * Returns the number of account roles where companyId = &#63; and accountEntryId = &#63;.
	 *
	 * @param companyId the company ID
	 * @param accountEntryId the account entry ID
	 * @return the number of matching account roles
	 */
	public static int countByC_A(long companyId, long accountEntryId) {
		return getPersistence().countByC_A(companyId, accountEntryId);
	}

	/**
	 * Returns the number of account roles where companyId = &#63; and accountEntryId = any &#63;.
	 *
	 * @param companyId the company ID
	 * @param accountEntryIds the account entry IDs
	 * @return the number of matching account roles
	 */
	public static int countByC_A(long companyId, long[] accountEntryIds) {
		return getPersistence().countByC_A(companyId, accountEntryIds);
	}

	/**
	 * Returns the number of account roles that the user has permission to view where companyId = &#63; and accountEntryId = &#63;.
	 *
	 * @param companyId the company ID
	 * @param accountEntryId the account entry ID
	 * @return the number of matching account roles that the user has permission to view
	 */
	public static int filterCountByC_A(long companyId, long accountEntryId) {
		return getPersistence().filterCountByC_A(companyId, accountEntryId);
	}

	/**
	 * Returns the number of account roles that the user has permission to view where companyId = &#63; and accountEntryId = any &#63;.
	 *
	 * @param companyId the company ID
	 * @param accountEntryIds the account entry IDs
	 * @return the number of matching account roles that the user has permission to view
	 */
	public static int filterCountByC_A(long companyId, long[] accountEntryIds) {
		return getPersistence().filterCountByC_A(companyId, accountEntryIds);
	}

	/**
	 * Returns the account role where externalReferenceCode = &#63; and companyId = &#63; or throws a <code>NoSuchRoleException</code> if it could not be found.
	 *
	 * @param externalReferenceCode the external reference code
	 * @param companyId the company ID
	 * @return the matching account role
	 * @throws NoSuchRoleException if a matching account role could not be found
	 */
	public static AccountRole findByERC_C(
			String externalReferenceCode, long companyId)
		throws com.liferay.account.exception.NoSuchRoleException {

		return getPersistence().findByERC_C(externalReferenceCode, companyId);
	}

	/**
	 * Returns the account role where externalReferenceCode = &#63; and companyId = &#63; or returns <code>null</code> if it could not be found, optionally using the finder cache.
	 *
	 * @param externalReferenceCode the external reference code
	 * @param companyId the company ID
	 * @param useFinderCache whether to use the finder cache
	 * @return the matching account role, or <code>null</code> if a matching account role could not be found
	 */
	public static AccountRole fetchByERC_C(
		String externalReferenceCode, long companyId, boolean useFinderCache) {

		return getPersistence().fetchByERC_C(
			externalReferenceCode, companyId, useFinderCache);
	}

	/**
	 * Removes the account role where externalReferenceCode = &#63; and companyId = &#63; from the database.
	 *
	 * @param externalReferenceCode the external reference code
	 * @param companyId the company ID
	 * @return the account role that was removed
	 */
	public static AccountRole removeByERC_C(
			String externalReferenceCode, long companyId)
		throws com.liferay.account.exception.NoSuchRoleException {

		return getPersistence().removeByERC_C(externalReferenceCode, companyId);
	}

	/**
	 * Returns the number of account roles where externalReferenceCode = &#63; and companyId = &#63;.
	 *
	 * @param externalReferenceCode the external reference code
	 * @param companyId the company ID
	 * @return the number of matching account roles
	 */
	public static int countByERC_C(
		String externalReferenceCode, long companyId) {

		return getPersistence().countByERC_C(externalReferenceCode, companyId);
	}

	/**
	 * Creates a new account role with the primary key. Does not add the account role to the database.
	 *
	 * @param accountRoleId the primary key for the new account role
	 * @return the new account role
	 */
	public static AccountRole create(long accountRoleId) {
		return getPersistence().create(accountRoleId);
	}

	/**
	 * Removes the account role with the primary key from the database. Also notifies the appropriate model listeners.
	 *
	 * @param accountRoleId the primary key of the account role
	 * @return the account role that was removed
	 * @throws NoSuchRoleException if a account role with the primary key could not be found
	 */
	public static AccountRole remove(long accountRoleId)
		throws com.liferay.account.exception.NoSuchRoleException {

		return getPersistence().remove(accountRoleId);
	}

	public static AccountRole updateImpl(AccountRole accountRole) {
		return getPersistence().updateImpl(accountRole);
	}

	/**
	 * Returns the account role with the primary key or throws a <code>NoSuchRoleException</code> if it could not be found.
	 *
	 * @param accountRoleId the primary key of the account role
	 * @return the account role
	 * @throws NoSuchRoleException if a account role with the primary key could not be found
	 */
	public static AccountRole findByPrimaryKey(long accountRoleId)
		throws com.liferay.account.exception.NoSuchRoleException {

		return getPersistence().findByPrimaryKey(accountRoleId);
	}

	/**
	 * Returns the account role with the primary key or returns <code>null</code> if it could not be found.
	 *
	 * @param accountRoleId the primary key of the account role
	 * @return the account role, or <code>null</code> if a account role with the primary key could not be found
	 */
	public static AccountRole fetchByPrimaryKey(long accountRoleId) {
		return getPersistence().fetchByPrimaryKey(accountRoleId);
	}

	/**
	 * Returns the account role where roleId = &#63; or returns <code>null</code> if it could not be found. Uses the finder cache.
	 *
	 * @param roleId the role ID
	 * @return the matching account role, or <code>null</code> if a matching account role could not be found
	 */
	public static AccountRole fetchByRoleId(long roleId) {
		return getPersistence().fetchByRoleId(roleId);
	}

	/**
	 * Returns the account role where externalReferenceCode = &#63; and companyId = &#63; or returns <code>null</code> if it could not be found. Uses the finder cache.
	 *
	 * @param externalReferenceCode the external reference code
	 * @param companyId the company ID
	 * @return the matching account role, or <code>null</code> if a matching account role could not be found
	 */
	public static AccountRole fetchByERC_C(
		String externalReferenceCode, long companyId) {

		return getPersistence().fetchByERC_C(externalReferenceCode, companyId);
	}

	/**
	 * Returns all the account roles where accountEntryId = &#63;.
	 *
	 * @param accountEntryId the account entry ID
	 * @return the matching account roles
	 */
	public static List<AccountRole> findByAccountEntryId(long accountEntryId) {
		return getPersistence().findByAccountEntryId(accountEntryId);
	}

	/**
	 * Returns a range of all the account roles where accountEntryId = &#63;.
	 *
	 * <p>
	 * Useful when paginating results. Returns a maximum of <code>end - start</code> instances. <code>start</code> and <code>end</code> are not primary keys, they are indexes in the result set. Thus, <code>0</code> refers to the first result in the set. Setting both <code>start</code> and <code>end</code> to <code>com.liferay.portal.kernel.dao.orm.QueryUtil#ALL_POS</code> will return the full result set. If <code>orderByComparator</code> is specified, then the query will include the given ORDER BY logic. If <code>orderByComparator</code> is absent, then the query will include the default ORDER BY logic from <code>com.liferay.account.model.impl.AccountRoleModelImpl</code>.
	 * </p>
	 *
	 * @param accountEntryId the account entry ID
	 * @param start the lower bound of the range of account roles
	 * @param end the upper bound of the range of account roles (not inclusive)
	 * @return the range of matching account roles
	 */
	public static List<AccountRole> findByAccountEntryId(
		long accountEntryId, int start, int end) {

		return getPersistence().findByAccountEntryId(
			accountEntryId, start, end);
	}

	/**
	 * Returns an ordered range of all the account roles where accountEntryId = &#63;.
	 *
	 * <p>
	 * Useful when paginating results. Returns a maximum of <code>end - start</code> instances. <code>start</code> and <code>end</code> are not primary keys, they are indexes in the result set. Thus, <code>0</code> refers to the first result in the set. Setting both <code>start</code> and <code>end</code> to <code>com.liferay.portal.kernel.dao.orm.QueryUtil#ALL_POS</code> will return the full result set. If <code>orderByComparator</code> is specified, then the query will include the given ORDER BY logic. If <code>orderByComparator</code> is absent, then the query will include the default ORDER BY logic from <code>com.liferay.account.model.impl.AccountRoleModelImpl</code>.
	 * </p>
	 *
	 * @param accountEntryId the account entry ID
	 * @param start the lower bound of the range of account roles
	 * @param end the upper bound of the range of account roles (not inclusive)
	 * @param orderByComparator the comparator to order the results by (optionally <code>null</code>)
	 * @return the ordered range of matching account roles
	 */
	public static List<AccountRole> findByAccountEntryId(
		long accountEntryId, int start, int end,
		OrderByComparator<AccountRole> orderByComparator) {

		return getPersistence().findByAccountEntryId(
			accountEntryId, start, end, orderByComparator);
	}

	/**
	 * Returns all the account roles where accountEntryId = any &#63;.
	 *
	 * @param accountEntryIds the account entry IDs
	 * @return the matching account roles
	 */
	public static List<AccountRole> findByAccountEntryId(
		long[] accountEntryIds) {

		return getPersistence().findByAccountEntryId(accountEntryIds);
	}

	/**
	 * Returns a range of all the account roles where accountEntryId = any &#63;.
	 *
	 * <p>
	 * Useful when paginating results. Returns a maximum of <code>end - start</code> instances. <code>start</code> and <code>end</code> are not primary keys, they are indexes in the result set. Thus, <code>0</code> refers to the first result in the set. Setting both <code>start</code> and <code>end</code> to <code>com.liferay.portal.kernel.dao.orm.QueryUtil#ALL_POS</code> will return the full result set. If <code>orderByComparator</code> is specified, then the query will include the given ORDER BY logic. If <code>orderByComparator</code> is absent, then the query will include the default ORDER BY logic from <code>com.liferay.account.model.impl.AccountRoleModelImpl</code>.
	 * </p>
	 *
	 * @param accountEntryIds the account entry IDs
	 * @param start the lower bound of the range of account roles
	 * @param end the upper bound of the range of account roles (not inclusive)
	 * @return the range of matching account roles
	 */
	public static List<AccountRole> findByAccountEntryId(
		long[] accountEntryIds, int start, int end) {

		return getPersistence().findByAccountEntryId(
			accountEntryIds, start, end);
	}

	/**
	 * Returns an ordered range of all the account roles where accountEntryId = any &#63;.
	 *
	 * <p>
	 * Useful when paginating results. Returns a maximum of <code>end - start</code> instances. <code>start</code> and <code>end</code> are not primary keys, they are indexes in the result set. Thus, <code>0</code> refers to the first result in the set. Setting both <code>start</code> and <code>end</code> to <code>com.liferay.portal.kernel.dao.orm.QueryUtil#ALL_POS</code> will return the full result set. If <code>orderByComparator</code> is specified, then the query will include the given ORDER BY logic. If <code>orderByComparator</code> is absent, then the query will include the default ORDER BY logic from <code>com.liferay.account.model.impl.AccountRoleModelImpl</code>.
	 * </p>
	 *
	 * @param accountEntryIds the account entry IDs
	 * @param start the lower bound of the range of account roles
	 * @param end the upper bound of the range of account roles (not inclusive)
	 * @param orderByComparator the comparator to order the results by (optionally <code>null</code>)
	 * @return the ordered range of matching account roles
	 */
	public static List<AccountRole> findByAccountEntryId(
		long[] accountEntryIds, int start, int end,
		OrderByComparator<AccountRole> orderByComparator) {

		return getPersistence().findByAccountEntryId(
			accountEntryIds, start, end, orderByComparator);
	}

	/**
	 * Returns all the account roles that the user has permission to view where accountEntryId = &#63;.
	 *
	 * @param accountEntryId the account entry ID
	 * @return the matching account roles that the user has permission to view
	 */
	public static List<AccountRole> filterFindByAccountEntryId(
		long accountEntryId) {

		return getPersistence().filterFindByAccountEntryId(accountEntryId);
	}

	/**
	 * Returns a range of all the account roles that the user has permission to view where accountEntryId = &#63;.
	 *
	 * <p>
	 * Useful when paginating results. Returns a maximum of <code>end - start</code> instances. <code>start</code> and <code>end</code> are not primary keys, they are indexes in the result set. Thus, <code>0</code> refers to the first result in the set. Setting both <code>start</code> and <code>end</code> to <code>com.liferay.portal.kernel.dao.orm.QueryUtil#ALL_POS</code> will return the full result set. If <code>orderByComparator</code> is specified, then the query will include the given ORDER BY logic. If <code>orderByComparator</code> is absent, then the query will include the default ORDER BY logic from <code>com.liferay.account.model.impl.AccountRoleModelImpl</code>.
	 * </p>
	 *
	 * @param accountEntryId the account entry ID
	 * @param start the lower bound of the range of account roles
	 * @param end the upper bound of the range of account roles (not inclusive)
	 * @return the range of matching account roles that the user has permission to view
	 */
	public static List<AccountRole> filterFindByAccountEntryId(
		long accountEntryId, int start, int end) {

		return getPersistence().filterFindByAccountEntryId(
			accountEntryId, start, end);
	}

	/**
	 * Returns all the account roles that the user has permission to view where accountEntryId = any &#63;.
	 *
	 * @param accountEntryIds the account entry IDs
	 * @return the matching account roles that the user has permission to view
	 */
	public static List<AccountRole> filterFindByAccountEntryId(
		long[] accountEntryIds) {

		return getPersistence().filterFindByAccountEntryId(accountEntryIds);
	}

	/**
	 * Returns a range of all the account roles that the user has permission to view where accountEntryId = any &#63;.
	 *
	 * <p>
	 * Useful when paginating results. Returns a maximum of <code>end - start</code> instances. <code>start</code> and <code>end</code> are not primary keys, they are indexes in the result set. Thus, <code>0</code> refers to the first result in the set. Setting both <code>start</code> and <code>end</code> to <code>com.liferay.portal.kernel.dao.orm.QueryUtil#ALL_POS</code> will return the full result set. If <code>orderByComparator</code> is specified, then the query will include the given ORDER BY logic. If <code>orderByComparator</code> is absent, then the query will include the default ORDER BY logic from <code>com.liferay.account.model.impl.AccountRoleModelImpl</code>.
	 * </p>
	 *
	 * @param accountEntryIds the account entry IDs
	 * @param start the lower bound of the range of account roles
	 * @param end the upper bound of the range of account roles (not inclusive)
	 * @return the range of matching account roles that the user has permission to view
	 */
	public static List<AccountRole> filterFindByAccountEntryId(
		long[] accountEntryIds, int start, int end) {

		return getPersistence().filterFindByAccountEntryId(
			accountEntryIds, start, end);
	}

	/**
	 * Returns all the account roles where companyId = &#63; and accountEntryId = &#63;.
	 *
	 * @param companyId the company ID
	 * @param accountEntryId the account entry ID
	 * @return the matching account roles
	 */
	public static List<AccountRole> findByC_A(
		long companyId, long accountEntryId) {

		return getPersistence().findByC_A(companyId, accountEntryId);
	}

	/**
	 * Returns a range of all the account roles where companyId = &#63; and accountEntryId = &#63;.
	 *
	 * <p>
	 * Useful when paginating results. Returns a maximum of <code>end - start</code> instances. <code>start</code> and <code>end</code> are not primary keys, they are indexes in the result set. Thus, <code>0</code> refers to the first result in the set. Setting both <code>start</code> and <code>end</code> to <code>com.liferay.portal.kernel.dao.orm.QueryUtil#ALL_POS</code> will return the full result set. If <code>orderByComparator</code> is specified, then the query will include the given ORDER BY logic. If <code>orderByComparator</code> is absent, then the query will include the default ORDER BY logic from <code>com.liferay.account.model.impl.AccountRoleModelImpl</code>.
	 * </p>
	 *
	 * @param companyId the company ID
	 * @param accountEntryId the account entry ID
	 * @param start the lower bound of the range of account roles
	 * @param end the upper bound of the range of account roles (not inclusive)
	 * @return the range of matching account roles
	 */
	public static List<AccountRole> findByC_A(
		long companyId, long accountEntryId, int start, int end) {

		return getPersistence().findByC_A(
			companyId, accountEntryId, start, end);
	}

	/**
	 * Returns an ordered range of all the account roles where companyId = &#63; and accountEntryId = &#63;.
	 *
	 * <p>
	 * Useful when paginating results. Returns a maximum of <code>end - start</code> instances. <code>start</code> and <code>end</code> are not primary keys, they are indexes in the result set. Thus, <code>0</code> refers to the first result in the set. Setting both <code>start</code> and <code>end</code> to <code>com.liferay.portal.kernel.dao.orm.QueryUtil#ALL_POS</code> will return the full result set. If <code>orderByComparator</code> is specified, then the query will include the given ORDER BY logic. If <code>orderByComparator</code> is absent, then the query will include the default ORDER BY logic from <code>com.liferay.account.model.impl.AccountRoleModelImpl</code>.
	 * </p>
	 *
	 * @param companyId the company ID
	 * @param accountEntryId the account entry ID
	 * @param start the lower bound of the range of account roles
	 * @param end the upper bound of the range of account roles (not inclusive)
	 * @param orderByComparator the comparator to order the results by (optionally <code>null</code>)
	 * @return the ordered range of matching account roles
	 */
	public static List<AccountRole> findByC_A(
		long companyId, long accountEntryId, int start, int end,
		OrderByComparator<AccountRole> orderByComparator) {

		return getPersistence().findByC_A(
			companyId, accountEntryId, start, end, orderByComparator);
	}

	/**
	 * Returns all the account roles where companyId = &#63; and accountEntryId = any &#63;.
	 *
	 * @param companyId the company ID
	 * @param accountEntryIds the account entry IDs
	 * @return the matching account roles
	 */
	public static List<AccountRole> findByC_A(
		long companyId, long[] accountEntryIds) {

		return getPersistence().findByC_A(companyId, accountEntryIds);
	}

	/**
	 * Returns a range of all the account roles where companyId = &#63; and accountEntryId = any &#63;.
	 *
	 * <p>
	 * Useful when paginating results. Returns a maximum of <code>end - start</code> instances. <code>start</code> and <code>end</code> are not primary keys, they are indexes in the result set. Thus, <code>0</code> refers to the first result in the set. Setting both <code>start</code> and <code>end</code> to <code>com.liferay.portal.kernel.dao.orm.QueryUtil#ALL_POS</code> will return the full result set. If <code>orderByComparator</code> is specified, then the query will include the given ORDER BY logic. If <code>orderByComparator</code> is absent, then the query will include the default ORDER BY logic from <code>com.liferay.account.model.impl.AccountRoleModelImpl</code>.
	 * </p>
	 *
	 * @param companyId the company ID
	 * @param accountEntryIds the account entry IDs
	 * @param start the lower bound of the range of account roles
	 * @param end the upper bound of the range of account roles (not inclusive)
	 * @return the range of matching account roles
	 */
	public static List<AccountRole> findByC_A(
		long companyId, long[] accountEntryIds, int start, int end) {

		return getPersistence().findByC_A(
			companyId, accountEntryIds, start, end);
	}

	/**
	 * Returns an ordered range of all the account roles where companyId = &#63; and accountEntryId = any &#63;.
	 *
	 * <p>
	 * Useful when paginating results. Returns a maximum of <code>end - start</code> instances. <code>start</code> and <code>end</code> are not primary keys, they are indexes in the result set. Thus, <code>0</code> refers to the first result in the set. Setting both <code>start</code> and <code>end</code> to <code>com.liferay.portal.kernel.dao.orm.QueryUtil#ALL_POS</code> will return the full result set. If <code>orderByComparator</code> is specified, then the query will include the given ORDER BY logic. If <code>orderByComparator</code> is absent, then the query will include the default ORDER BY logic from <code>com.liferay.account.model.impl.AccountRoleModelImpl</code>.
	 * </p>
	 *
	 * @param companyId the company ID
	 * @param accountEntryIds the account entry IDs
	 * @param start the lower bound of the range of account roles
	 * @param end the upper bound of the range of account roles (not inclusive)
	 * @param orderByComparator the comparator to order the results by (optionally <code>null</code>)
	 * @return the ordered range of matching account roles
	 */
	public static List<AccountRole> findByC_A(
		long companyId, long[] accountEntryIds, int start, int end,
		OrderByComparator<AccountRole> orderByComparator) {

		return getPersistence().findByC_A(
			companyId, accountEntryIds, start, end, orderByComparator);
	}

	/**
	 * Returns all the account roles that the user has permission to view where companyId = &#63; and accountEntryId = &#63;.
	 *
	 * @param companyId the company ID
	 * @param accountEntryId the account entry ID
	 * @return the matching account roles that the user has permission to view
	 */
	public static List<AccountRole> filterFindByC_A(
		long companyId, long accountEntryId) {

		return getPersistence().filterFindByC_A(companyId, accountEntryId);
	}

	/**
	 * Returns a range of all the account roles that the user has permission to view where companyId = &#63; and accountEntryId = &#63;.
	 *
	 * <p>
	 * Useful when paginating results. Returns a maximum of <code>end - start</code> instances. <code>start</code> and <code>end</code> are not primary keys, they are indexes in the result set. Thus, <code>0</code> refers to the first result in the set. Setting both <code>start</code> and <code>end</code> to <code>com.liferay.portal.kernel.dao.orm.QueryUtil#ALL_POS</code> will return the full result set. If <code>orderByComparator</code> is specified, then the query will include the given ORDER BY logic. If <code>orderByComparator</code> is absent, then the query will include the default ORDER BY logic from <code>com.liferay.account.model.impl.AccountRoleModelImpl</code>.
	 * </p>
	 *
	 * @param companyId the company ID
	 * @param accountEntryId the account entry ID
	 * @param start the lower bound of the range of account roles
	 * @param end the upper bound of the range of account roles (not inclusive)
	 * @return the range of matching account roles that the user has permission to view
	 */
	public static List<AccountRole> filterFindByC_A(
		long companyId, long accountEntryId, int start, int end) {

		return getPersistence().filterFindByC_A(
			companyId, accountEntryId, start, end);
	}

	/**
	 * Returns all the account roles that the user has permission to view where companyId = &#63; and accountEntryId = any &#63;.
	 *
	 * @param companyId the company ID
	 * @param accountEntryIds the account entry IDs
	 * @return the matching account roles that the user has permission to view
	 */
	public static List<AccountRole> filterFindByC_A(
		long companyId, long[] accountEntryIds) {

		return getPersistence().filterFindByC_A(companyId, accountEntryIds);
	}

	/**
	 * Returns a range of all the account roles that the user has permission to view where companyId = &#63; and accountEntryId = any &#63;.
	 *
	 * <p>
	 * Useful when paginating results. Returns a maximum of <code>end - start</code> instances. <code>start</code> and <code>end</code> are not primary keys, they are indexes in the result set. Thus, <code>0</code> refers to the first result in the set. Setting both <code>start</code> and <code>end</code> to <code>com.liferay.portal.kernel.dao.orm.QueryUtil#ALL_POS</code> will return the full result set. If <code>orderByComparator</code> is specified, then the query will include the given ORDER BY logic. If <code>orderByComparator</code> is absent, then the query will include the default ORDER BY logic from <code>com.liferay.account.model.impl.AccountRoleModelImpl</code>.
	 * </p>
	 *
	 * @param companyId the company ID
	 * @param accountEntryIds the account entry IDs
	 * @param start the lower bound of the range of account roles
	 * @param end the upper bound of the range of account roles (not inclusive)
	 * @return the range of matching account roles that the user has permission to view
	 */
	public static List<AccountRole> filterFindByC_A(
		long companyId, long[] accountEntryIds, int start, int end) {

		return getPersistence().filterFindByC_A(
			companyId, accountEntryIds, start, end);
	}

	public static AccountRolePersistence getPersistence() {
		return _persistence;
	}

	public static void setPersistence(AccountRolePersistence persistence) {
		_persistence = persistence;
	}

	private static volatile AccountRolePersistence _persistence;

}
// LIFERAY-SERVICE-BUILDER-HASH:1739378818