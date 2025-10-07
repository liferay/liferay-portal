/**
 * SPDX-FileCopyrightText: (c) 2025 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

package com.liferay.osb.patcher.service.persistence;

import com.liferay.osb.patcher.model.PatcherAccount;
import com.liferay.portal.kernel.dao.orm.DynamicQuery;
import com.liferay.portal.kernel.service.ServiceContext;
import com.liferay.portal.kernel.util.OrderByComparator;

import java.io.Serializable;

import java.util.List;
import java.util.Map;
import java.util.Set;

/**
 * The persistence utility for the patcher account service. This utility wraps <code>com.liferay.osb.patcher.service.persistence.impl.PatcherAccountPersistenceImpl</code> and provides direct access to the database for CRUD operations. This utility should only be used by the service layer, as it must operate within a transaction. Never access this utility in a JSP, controller, model, or other front-end class.
 *
 * <p>
 * Caching information and settings can be found in <code>portal.properties</code>
 * </p>
 *
 * @author Brian Wing Shun Chan
 * @see PatcherAccountPersistence
 * @generated
 */
public class PatcherAccountUtil {

	/*
	 * NOTE FOR DEVELOPERS:
	 *
	 * Never modify this class directly. Modify <code>service.xml</code> and rerun ServiceBuilder to regenerate this class.
	 */

	/**
	 * @see com.liferay.portal.kernel.service.persistence.BasePersistence#clearCache()
	 */
	public static void clearCache() {
		getPersistence().clearCache();
	}

	/**
	 * @see com.liferay.portal.kernel.service.persistence.BasePersistence#clearCache(com.liferay.portal.kernel.model.BaseModel)
	 */
	public static void clearCache(PatcherAccount patcherAccount) {
		getPersistence().clearCache(patcherAccount);
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
	public static Map<Serializable, PatcherAccount> fetchByPrimaryKeys(
		Set<Serializable> primaryKeys) {

		return getPersistence().fetchByPrimaryKeys(primaryKeys);
	}

	/**
	 * @see com.liferay.portal.kernel.service.persistence.BasePersistence#findWithDynamicQuery(DynamicQuery)
	 */
	public static List<PatcherAccount> findWithDynamicQuery(
		DynamicQuery dynamicQuery) {

		return getPersistence().findWithDynamicQuery(dynamicQuery);
	}

	/**
	 * @see com.liferay.portal.kernel.service.persistence.BasePersistence#findWithDynamicQuery(DynamicQuery, int, int)
	 */
	public static List<PatcherAccount> findWithDynamicQuery(
		DynamicQuery dynamicQuery, int start, int end) {

		return getPersistence().findWithDynamicQuery(dynamicQuery, start, end);
	}

	/**
	 * @see com.liferay.portal.kernel.service.persistence.BasePersistence#findWithDynamicQuery(DynamicQuery, int, int, OrderByComparator)
	 */
	public static List<PatcherAccount> findWithDynamicQuery(
		DynamicQuery dynamicQuery, int start, int end,
		OrderByComparator<PatcherAccount> orderByComparator) {

		return getPersistence().findWithDynamicQuery(
			dynamicQuery, start, end, orderByComparator);
	}

	/**
	 * @see com.liferay.portal.kernel.service.persistence.BasePersistence#update(com.liferay.portal.kernel.model.BaseModel)
	 */
	public static PatcherAccount update(PatcherAccount patcherAccount) {
		return getPersistence().update(patcherAccount);
	}

	/**
	 * @see com.liferay.portal.kernel.service.persistence.BasePersistence#update(com.liferay.portal.kernel.model.BaseModel, ServiceContext)
	 */
	public static PatcherAccount update(
		PatcherAccount patcherAccount, ServiceContext serviceContext) {

		return getPersistence().update(patcherAccount, serviceContext);
	}

	/**
	 * Returns all the patcher accounts where companyId = &#63;.
	 *
	 * @param companyId the company ID
	 * @return the matching patcher accounts
	 */
	public static List<PatcherAccount> findByCompanyId(long companyId) {
		return getPersistence().findByCompanyId(companyId);
	}

	/**
	 * Returns a range of all the patcher accounts where companyId = &#63;.
	 *
	 * <p>
	 * Useful when paginating results. Returns a maximum of <code>end - start</code> instances. <code>start</code> and <code>end</code> are not primary keys, they are indexes in the result set. Thus, <code>0</code> refers to the first result in the set. Setting both <code>start</code> and <code>end</code> to <code>QueryUtil#ALL_POS</code> will return the full result set. If <code>orderByComparator</code> is specified, then the query will include the given ORDER BY logic. If <code>orderByComparator</code> is absent, then the query will include the default ORDER BY logic from <code>PatcherAccountModelImpl</code>.
	 * </p>
	 *
	 * @param companyId the company ID
	 * @param start the lower bound of the range of patcher accounts
	 * @param end the upper bound of the range of patcher accounts (not inclusive)
	 * @return the range of matching patcher accounts
	 */
	public static List<PatcherAccount> findByCompanyId(
		long companyId, int start, int end) {

		return getPersistence().findByCompanyId(companyId, start, end);
	}

	/**
	 * Returns an ordered range of all the patcher accounts where companyId = &#63;.
	 *
	 * <p>
	 * Useful when paginating results. Returns a maximum of <code>end - start</code> instances. <code>start</code> and <code>end</code> are not primary keys, they are indexes in the result set. Thus, <code>0</code> refers to the first result in the set. Setting both <code>start</code> and <code>end</code> to <code>QueryUtil#ALL_POS</code> will return the full result set. If <code>orderByComparator</code> is specified, then the query will include the given ORDER BY logic. If <code>orderByComparator</code> is absent, then the query will include the default ORDER BY logic from <code>PatcherAccountModelImpl</code>.
	 * </p>
	 *
	 * @param companyId the company ID
	 * @param start the lower bound of the range of patcher accounts
	 * @param end the upper bound of the range of patcher accounts (not inclusive)
	 * @param orderByComparator the comparator to order the results by (optionally <code>null</code>)
	 * @return the ordered range of matching patcher accounts
	 */
	public static List<PatcherAccount> findByCompanyId(
		long companyId, int start, int end,
		OrderByComparator<PatcherAccount> orderByComparator) {

		return getPersistence().findByCompanyId(
			companyId, start, end, orderByComparator);
	}

	/**
	 * Returns an ordered range of all the patcher accounts where companyId = &#63;.
	 *
	 * <p>
	 * Useful when paginating results. Returns a maximum of <code>end - start</code> instances. <code>start</code> and <code>end</code> are not primary keys, they are indexes in the result set. Thus, <code>0</code> refers to the first result in the set. Setting both <code>start</code> and <code>end</code> to <code>QueryUtil#ALL_POS</code> will return the full result set. If <code>orderByComparator</code> is specified, then the query will include the given ORDER BY logic. If <code>orderByComparator</code> is absent, then the query will include the default ORDER BY logic from <code>PatcherAccountModelImpl</code>.
	 * </p>
	 *
	 * @param companyId the company ID
	 * @param start the lower bound of the range of patcher accounts
	 * @param end the upper bound of the range of patcher accounts (not inclusive)
	 * @param orderByComparator the comparator to order the results by (optionally <code>null</code>)
	 * @param useFinderCache whether to use the finder cache
	 * @return the ordered range of matching patcher accounts
	 */
	public static List<PatcherAccount> findByCompanyId(
		long companyId, int start, int end,
		OrderByComparator<PatcherAccount> orderByComparator,
		boolean useFinderCache) {

		return getPersistence().findByCompanyId(
			companyId, start, end, orderByComparator, useFinderCache);
	}

	/**
	 * Returns the first patcher account in the ordered set where companyId = &#63;.
	 *
	 * @param companyId the company ID
	 * @param orderByComparator the comparator to order the set by (optionally <code>null</code>)
	 * @return the first matching patcher account
	 * @throws NoSuchPatcherAccountException if a matching patcher account could not be found
	 */
	public static PatcherAccount findByCompanyId_First(
			long companyId, OrderByComparator<PatcherAccount> orderByComparator)
		throws com.liferay.osb.patcher.exception.NoSuchPatcherAccountException {

		return getPersistence().findByCompanyId_First(
			companyId, orderByComparator);
	}

	/**
	 * Returns the first patcher account in the ordered set where companyId = &#63;.
	 *
	 * @param companyId the company ID
	 * @param orderByComparator the comparator to order the set by (optionally <code>null</code>)
	 * @return the first matching patcher account, or <code>null</code> if a matching patcher account could not be found
	 */
	public static PatcherAccount fetchByCompanyId_First(
		long companyId, OrderByComparator<PatcherAccount> orderByComparator) {

		return getPersistence().fetchByCompanyId_First(
			companyId, orderByComparator);
	}

	/**
	 * Returns the last patcher account in the ordered set where companyId = &#63;.
	 *
	 * @param companyId the company ID
	 * @param orderByComparator the comparator to order the set by (optionally <code>null</code>)
	 * @return the last matching patcher account
	 * @throws NoSuchPatcherAccountException if a matching patcher account could not be found
	 */
	public static PatcherAccount findByCompanyId_Last(
			long companyId, OrderByComparator<PatcherAccount> orderByComparator)
		throws com.liferay.osb.patcher.exception.NoSuchPatcherAccountException {

		return getPersistence().findByCompanyId_Last(
			companyId, orderByComparator);
	}

	/**
	 * Returns the last patcher account in the ordered set where companyId = &#63;.
	 *
	 * @param companyId the company ID
	 * @param orderByComparator the comparator to order the set by (optionally <code>null</code>)
	 * @return the last matching patcher account, or <code>null</code> if a matching patcher account could not be found
	 */
	public static PatcherAccount fetchByCompanyId_Last(
		long companyId, OrderByComparator<PatcherAccount> orderByComparator) {

		return getPersistence().fetchByCompanyId_Last(
			companyId, orderByComparator);
	}

	/**
	 * Returns the patcher accounts before and after the current patcher account in the ordered set where companyId = &#63;.
	 *
	 * @param patcherAccountId the primary key of the current patcher account
	 * @param companyId the company ID
	 * @param orderByComparator the comparator to order the set by (optionally <code>null</code>)
	 * @return the previous, current, and next patcher account
	 * @throws NoSuchPatcherAccountException if a patcher account with the primary key could not be found
	 */
	public static PatcherAccount[] findByCompanyId_PrevAndNext(
			long patcherAccountId, long companyId,
			OrderByComparator<PatcherAccount> orderByComparator)
		throws com.liferay.osb.patcher.exception.NoSuchPatcherAccountException {

		return getPersistence().findByCompanyId_PrevAndNext(
			patcherAccountId, companyId, orderByComparator);
	}

	/**
	 * Returns all the patcher accounts that the user has permission to view where companyId = &#63;.
	 *
	 * @param companyId the company ID
	 * @return the matching patcher accounts that the user has permission to view
	 */
	public static List<PatcherAccount> filterFindByCompanyId(long companyId) {
		return getPersistence().filterFindByCompanyId(companyId);
	}

	/**
	 * Returns a range of all the patcher accounts that the user has permission to view where companyId = &#63;.
	 *
	 * <p>
	 * Useful when paginating results. Returns a maximum of <code>end - start</code> instances. <code>start</code> and <code>end</code> are not primary keys, they are indexes in the result set. Thus, <code>0</code> refers to the first result in the set. Setting both <code>start</code> and <code>end</code> to <code>QueryUtil#ALL_POS</code> will return the full result set. If <code>orderByComparator</code> is specified, then the query will include the given ORDER BY logic. If <code>orderByComparator</code> is absent, then the query will include the default ORDER BY logic from <code>PatcherAccountModelImpl</code>.
	 * </p>
	 *
	 * @param companyId the company ID
	 * @param start the lower bound of the range of patcher accounts
	 * @param end the upper bound of the range of patcher accounts (not inclusive)
	 * @return the range of matching patcher accounts that the user has permission to view
	 */
	public static List<PatcherAccount> filterFindByCompanyId(
		long companyId, int start, int end) {

		return getPersistence().filterFindByCompanyId(companyId, start, end);
	}

	/**
	 * Returns an ordered range of all the patcher accounts that the user has permissions to view where companyId = &#63;.
	 *
	 * <p>
	 * Useful when paginating results. Returns a maximum of <code>end - start</code> instances. <code>start</code> and <code>end</code> are not primary keys, they are indexes in the result set. Thus, <code>0</code> refers to the first result in the set. Setting both <code>start</code> and <code>end</code> to <code>QueryUtil#ALL_POS</code> will return the full result set. If <code>orderByComparator</code> is specified, then the query will include the given ORDER BY logic. If <code>orderByComparator</code> is absent, then the query will include the default ORDER BY logic from <code>PatcherAccountModelImpl</code>.
	 * </p>
	 *
	 * @param companyId the company ID
	 * @param start the lower bound of the range of patcher accounts
	 * @param end the upper bound of the range of patcher accounts (not inclusive)
	 * @param orderByComparator the comparator to order the results by (optionally <code>null</code>)
	 * @return the ordered range of matching patcher accounts that the user has permission to view
	 */
	public static List<PatcherAccount> filterFindByCompanyId(
		long companyId, int start, int end,
		OrderByComparator<PatcherAccount> orderByComparator) {

		return getPersistence().filterFindByCompanyId(
			companyId, start, end, orderByComparator);
	}

	/**
	 * Returns the patcher accounts before and after the current patcher account in the ordered set of patcher accounts that the user has permission to view where companyId = &#63;.
	 *
	 * @param patcherAccountId the primary key of the current patcher account
	 * @param companyId the company ID
	 * @param orderByComparator the comparator to order the set by (optionally <code>null</code>)
	 * @return the previous, current, and next patcher account
	 * @throws NoSuchPatcherAccountException if a patcher account with the primary key could not be found
	 */
	public static PatcherAccount[] filterFindByCompanyId_PrevAndNext(
			long patcherAccountId, long companyId,
			OrderByComparator<PatcherAccount> orderByComparator)
		throws com.liferay.osb.patcher.exception.NoSuchPatcherAccountException {

		return getPersistence().filterFindByCompanyId_PrevAndNext(
			patcherAccountId, companyId, orderByComparator);
	}

	/**
	 * Removes all the patcher accounts where companyId = &#63; from the database.
	 *
	 * @param companyId the company ID
	 */
	public static void removeByCompanyId(long companyId) {
		getPersistence().removeByCompanyId(companyId);
	}

	/**
	 * Returns the number of patcher accounts where companyId = &#63;.
	 *
	 * @param companyId the company ID
	 * @return the number of matching patcher accounts
	 */
	public static int countByCompanyId(long companyId) {
		return getPersistence().countByCompanyId(companyId);
	}

	/**
	 * Returns the number of patcher accounts that the user has permission to view where companyId = &#63;.
	 *
	 * @param companyId the company ID
	 * @return the number of matching patcher accounts that the user has permission to view
	 */
	public static int filterCountByCompanyId(long companyId) {
		return getPersistence().filterCountByCompanyId(companyId);
	}

	/**
	 * Returns the patcher account where accountEntryCode = &#63; or throws a <code>NoSuchPatcherAccountException</code> if it could not be found.
	 *
	 * @param accountEntryCode the account entry code
	 * @return the matching patcher account
	 * @throws NoSuchPatcherAccountException if a matching patcher account could not be found
	 */
	public static PatcherAccount findByAccountEntryCode(String accountEntryCode)
		throws com.liferay.osb.patcher.exception.NoSuchPatcherAccountException {

		return getPersistence().findByAccountEntryCode(accountEntryCode);
	}

	/**
	 * Returns the patcher account where accountEntryCode = &#63; or returns <code>null</code> if it could not be found. Uses the finder cache.
	 *
	 * @param accountEntryCode the account entry code
	 * @return the matching patcher account, or <code>null</code> if a matching patcher account could not be found
	 */
	public static PatcherAccount fetchByAccountEntryCode(
		String accountEntryCode) {

		return getPersistence().fetchByAccountEntryCode(accountEntryCode);
	}

	/**
	 * Returns the patcher account where accountEntryCode = &#63; or returns <code>null</code> if it could not be found, optionally using the finder cache.
	 *
	 * @param accountEntryCode the account entry code
	 * @param useFinderCache whether to use the finder cache
	 * @return the matching patcher account, or <code>null</code> if a matching patcher account could not be found
	 */
	public static PatcherAccount fetchByAccountEntryCode(
		String accountEntryCode, boolean useFinderCache) {

		return getPersistence().fetchByAccountEntryCode(
			accountEntryCode, useFinderCache);
	}

	/**
	 * Removes the patcher account where accountEntryCode = &#63; from the database.
	 *
	 * @param accountEntryCode the account entry code
	 * @return the patcher account that was removed
	 */
	public static PatcherAccount removeByAccountEntryCode(
			String accountEntryCode)
		throws com.liferay.osb.patcher.exception.NoSuchPatcherAccountException {

		return getPersistence().removeByAccountEntryCode(accountEntryCode);
	}

	/**
	 * Returns the number of patcher accounts where accountEntryCode = &#63;.
	 *
	 * @param accountEntryCode the account entry code
	 * @return the number of matching patcher accounts
	 */
	public static int countByAccountEntryCode(String accountEntryCode) {
		return getPersistence().countByAccountEntryCode(accountEntryCode);
	}

	/**
	 * Returns all the patcher accounts where companyId = &#63; and accountEntryCode LIKE &#63;.
	 *
	 * @param companyId the company ID
	 * @param accountEntryCode the account entry code
	 * @return the matching patcher accounts
	 */
	public static List<PatcherAccount> findByC_LikeA(
		long companyId, String accountEntryCode) {

		return getPersistence().findByC_LikeA(companyId, accountEntryCode);
	}

	/**
	 * Returns a range of all the patcher accounts where companyId = &#63; and accountEntryCode LIKE &#63;.
	 *
	 * <p>
	 * Useful when paginating results. Returns a maximum of <code>end - start</code> instances. <code>start</code> and <code>end</code> are not primary keys, they are indexes in the result set. Thus, <code>0</code> refers to the first result in the set. Setting both <code>start</code> and <code>end</code> to <code>QueryUtil#ALL_POS</code> will return the full result set. If <code>orderByComparator</code> is specified, then the query will include the given ORDER BY logic. If <code>orderByComparator</code> is absent, then the query will include the default ORDER BY logic from <code>PatcherAccountModelImpl</code>.
	 * </p>
	 *
	 * @param companyId the company ID
	 * @param accountEntryCode the account entry code
	 * @param start the lower bound of the range of patcher accounts
	 * @param end the upper bound of the range of patcher accounts (not inclusive)
	 * @return the range of matching patcher accounts
	 */
	public static List<PatcherAccount> findByC_LikeA(
		long companyId, String accountEntryCode, int start, int end) {

		return getPersistence().findByC_LikeA(
			companyId, accountEntryCode, start, end);
	}

	/**
	 * Returns an ordered range of all the patcher accounts where companyId = &#63; and accountEntryCode LIKE &#63;.
	 *
	 * <p>
	 * Useful when paginating results. Returns a maximum of <code>end - start</code> instances. <code>start</code> and <code>end</code> are not primary keys, they are indexes in the result set. Thus, <code>0</code> refers to the first result in the set. Setting both <code>start</code> and <code>end</code> to <code>QueryUtil#ALL_POS</code> will return the full result set. If <code>orderByComparator</code> is specified, then the query will include the given ORDER BY logic. If <code>orderByComparator</code> is absent, then the query will include the default ORDER BY logic from <code>PatcherAccountModelImpl</code>.
	 * </p>
	 *
	 * @param companyId the company ID
	 * @param accountEntryCode the account entry code
	 * @param start the lower bound of the range of patcher accounts
	 * @param end the upper bound of the range of patcher accounts (not inclusive)
	 * @param orderByComparator the comparator to order the results by (optionally <code>null</code>)
	 * @return the ordered range of matching patcher accounts
	 */
	public static List<PatcherAccount> findByC_LikeA(
		long companyId, String accountEntryCode, int start, int end,
		OrderByComparator<PatcherAccount> orderByComparator) {

		return getPersistence().findByC_LikeA(
			companyId, accountEntryCode, start, end, orderByComparator);
	}

	/**
	 * Returns an ordered range of all the patcher accounts where companyId = &#63; and accountEntryCode LIKE &#63;.
	 *
	 * <p>
	 * Useful when paginating results. Returns a maximum of <code>end - start</code> instances. <code>start</code> and <code>end</code> are not primary keys, they are indexes in the result set. Thus, <code>0</code> refers to the first result in the set. Setting both <code>start</code> and <code>end</code> to <code>QueryUtil#ALL_POS</code> will return the full result set. If <code>orderByComparator</code> is specified, then the query will include the given ORDER BY logic. If <code>orderByComparator</code> is absent, then the query will include the default ORDER BY logic from <code>PatcherAccountModelImpl</code>.
	 * </p>
	 *
	 * @param companyId the company ID
	 * @param accountEntryCode the account entry code
	 * @param start the lower bound of the range of patcher accounts
	 * @param end the upper bound of the range of patcher accounts (not inclusive)
	 * @param orderByComparator the comparator to order the results by (optionally <code>null</code>)
	 * @param useFinderCache whether to use the finder cache
	 * @return the ordered range of matching patcher accounts
	 */
	public static List<PatcherAccount> findByC_LikeA(
		long companyId, String accountEntryCode, int start, int end,
		OrderByComparator<PatcherAccount> orderByComparator,
		boolean useFinderCache) {

		return getPersistence().findByC_LikeA(
			companyId, accountEntryCode, start, end, orderByComparator,
			useFinderCache);
	}

	/**
	 * Returns the first patcher account in the ordered set where companyId = &#63; and accountEntryCode LIKE &#63;.
	 *
	 * @param companyId the company ID
	 * @param accountEntryCode the account entry code
	 * @param orderByComparator the comparator to order the set by (optionally <code>null</code>)
	 * @return the first matching patcher account
	 * @throws NoSuchPatcherAccountException if a matching patcher account could not be found
	 */
	public static PatcherAccount findByC_LikeA_First(
			long companyId, String accountEntryCode,
			OrderByComparator<PatcherAccount> orderByComparator)
		throws com.liferay.osb.patcher.exception.NoSuchPatcherAccountException {

		return getPersistence().findByC_LikeA_First(
			companyId, accountEntryCode, orderByComparator);
	}

	/**
	 * Returns the first patcher account in the ordered set where companyId = &#63; and accountEntryCode LIKE &#63;.
	 *
	 * @param companyId the company ID
	 * @param accountEntryCode the account entry code
	 * @param orderByComparator the comparator to order the set by (optionally <code>null</code>)
	 * @return the first matching patcher account, or <code>null</code> if a matching patcher account could not be found
	 */
	public static PatcherAccount fetchByC_LikeA_First(
		long companyId, String accountEntryCode,
		OrderByComparator<PatcherAccount> orderByComparator) {

		return getPersistence().fetchByC_LikeA_First(
			companyId, accountEntryCode, orderByComparator);
	}

	/**
	 * Returns the last patcher account in the ordered set where companyId = &#63; and accountEntryCode LIKE &#63;.
	 *
	 * @param companyId the company ID
	 * @param accountEntryCode the account entry code
	 * @param orderByComparator the comparator to order the set by (optionally <code>null</code>)
	 * @return the last matching patcher account
	 * @throws NoSuchPatcherAccountException if a matching patcher account could not be found
	 */
	public static PatcherAccount findByC_LikeA_Last(
			long companyId, String accountEntryCode,
			OrderByComparator<PatcherAccount> orderByComparator)
		throws com.liferay.osb.patcher.exception.NoSuchPatcherAccountException {

		return getPersistence().findByC_LikeA_Last(
			companyId, accountEntryCode, orderByComparator);
	}

	/**
	 * Returns the last patcher account in the ordered set where companyId = &#63; and accountEntryCode LIKE &#63;.
	 *
	 * @param companyId the company ID
	 * @param accountEntryCode the account entry code
	 * @param orderByComparator the comparator to order the set by (optionally <code>null</code>)
	 * @return the last matching patcher account, or <code>null</code> if a matching patcher account could not be found
	 */
	public static PatcherAccount fetchByC_LikeA_Last(
		long companyId, String accountEntryCode,
		OrderByComparator<PatcherAccount> orderByComparator) {

		return getPersistence().fetchByC_LikeA_Last(
			companyId, accountEntryCode, orderByComparator);
	}

	/**
	 * Returns the patcher accounts before and after the current patcher account in the ordered set where companyId = &#63; and accountEntryCode LIKE &#63;.
	 *
	 * @param patcherAccountId the primary key of the current patcher account
	 * @param companyId the company ID
	 * @param accountEntryCode the account entry code
	 * @param orderByComparator the comparator to order the set by (optionally <code>null</code>)
	 * @return the previous, current, and next patcher account
	 * @throws NoSuchPatcherAccountException if a patcher account with the primary key could not be found
	 */
	public static PatcherAccount[] findByC_LikeA_PrevAndNext(
			long patcherAccountId, long companyId, String accountEntryCode,
			OrderByComparator<PatcherAccount> orderByComparator)
		throws com.liferay.osb.patcher.exception.NoSuchPatcherAccountException {

		return getPersistence().findByC_LikeA_PrevAndNext(
			patcherAccountId, companyId, accountEntryCode, orderByComparator);
	}

	/**
	 * Returns all the patcher accounts that the user has permission to view where companyId = &#63; and accountEntryCode LIKE &#63;.
	 *
	 * @param companyId the company ID
	 * @param accountEntryCode the account entry code
	 * @return the matching patcher accounts that the user has permission to view
	 */
	public static List<PatcherAccount> filterFindByC_LikeA(
		long companyId, String accountEntryCode) {

		return getPersistence().filterFindByC_LikeA(
			companyId, accountEntryCode);
	}

	/**
	 * Returns a range of all the patcher accounts that the user has permission to view where companyId = &#63; and accountEntryCode LIKE &#63;.
	 *
	 * <p>
	 * Useful when paginating results. Returns a maximum of <code>end - start</code> instances. <code>start</code> and <code>end</code> are not primary keys, they are indexes in the result set. Thus, <code>0</code> refers to the first result in the set. Setting both <code>start</code> and <code>end</code> to <code>QueryUtil#ALL_POS</code> will return the full result set. If <code>orderByComparator</code> is specified, then the query will include the given ORDER BY logic. If <code>orderByComparator</code> is absent, then the query will include the default ORDER BY logic from <code>PatcherAccountModelImpl</code>.
	 * </p>
	 *
	 * @param companyId the company ID
	 * @param accountEntryCode the account entry code
	 * @param start the lower bound of the range of patcher accounts
	 * @param end the upper bound of the range of patcher accounts (not inclusive)
	 * @return the range of matching patcher accounts that the user has permission to view
	 */
	public static List<PatcherAccount> filterFindByC_LikeA(
		long companyId, String accountEntryCode, int start, int end) {

		return getPersistence().filterFindByC_LikeA(
			companyId, accountEntryCode, start, end);
	}

	/**
	 * Returns an ordered range of all the patcher accounts that the user has permissions to view where companyId = &#63; and accountEntryCode LIKE &#63;.
	 *
	 * <p>
	 * Useful when paginating results. Returns a maximum of <code>end - start</code> instances. <code>start</code> and <code>end</code> are not primary keys, they are indexes in the result set. Thus, <code>0</code> refers to the first result in the set. Setting both <code>start</code> and <code>end</code> to <code>QueryUtil#ALL_POS</code> will return the full result set. If <code>orderByComparator</code> is specified, then the query will include the given ORDER BY logic. If <code>orderByComparator</code> is absent, then the query will include the default ORDER BY logic from <code>PatcherAccountModelImpl</code>.
	 * </p>
	 *
	 * @param companyId the company ID
	 * @param accountEntryCode the account entry code
	 * @param start the lower bound of the range of patcher accounts
	 * @param end the upper bound of the range of patcher accounts (not inclusive)
	 * @param orderByComparator the comparator to order the results by (optionally <code>null</code>)
	 * @return the ordered range of matching patcher accounts that the user has permission to view
	 */
	public static List<PatcherAccount> filterFindByC_LikeA(
		long companyId, String accountEntryCode, int start, int end,
		OrderByComparator<PatcherAccount> orderByComparator) {

		return getPersistence().filterFindByC_LikeA(
			companyId, accountEntryCode, start, end, orderByComparator);
	}

	/**
	 * Returns the patcher accounts before and after the current patcher account in the ordered set of patcher accounts that the user has permission to view where companyId = &#63; and accountEntryCode LIKE &#63;.
	 *
	 * @param patcherAccountId the primary key of the current patcher account
	 * @param companyId the company ID
	 * @param accountEntryCode the account entry code
	 * @param orderByComparator the comparator to order the set by (optionally <code>null</code>)
	 * @return the previous, current, and next patcher account
	 * @throws NoSuchPatcherAccountException if a patcher account with the primary key could not be found
	 */
	public static PatcherAccount[] filterFindByC_LikeA_PrevAndNext(
			long patcherAccountId, long companyId, String accountEntryCode,
			OrderByComparator<PatcherAccount> orderByComparator)
		throws com.liferay.osb.patcher.exception.NoSuchPatcherAccountException {

		return getPersistence().filterFindByC_LikeA_PrevAndNext(
			patcherAccountId, companyId, accountEntryCode, orderByComparator);
	}

	/**
	 * Removes all the patcher accounts where companyId = &#63; and accountEntryCode LIKE &#63; from the database.
	 *
	 * @param companyId the company ID
	 * @param accountEntryCode the account entry code
	 */
	public static void removeByC_LikeA(
		long companyId, String accountEntryCode) {

		getPersistence().removeByC_LikeA(companyId, accountEntryCode);
	}

	/**
	 * Returns the number of patcher accounts where companyId = &#63; and accountEntryCode LIKE &#63;.
	 *
	 * @param companyId the company ID
	 * @param accountEntryCode the account entry code
	 * @return the number of matching patcher accounts
	 */
	public static int countByC_LikeA(long companyId, String accountEntryCode) {
		return getPersistence().countByC_LikeA(companyId, accountEntryCode);
	}

	/**
	 * Returns the number of patcher accounts that the user has permission to view where companyId = &#63; and accountEntryCode LIKE &#63;.
	 *
	 * @param companyId the company ID
	 * @param accountEntryCode the account entry code
	 * @return the number of matching patcher accounts that the user has permission to view
	 */
	public static int filterCountByC_LikeA(
		long companyId, String accountEntryCode) {

		return getPersistence().filterCountByC_LikeA(
			companyId, accountEntryCode);
	}

	/**
	 * Caches the patcher account in the entity cache if it is enabled.
	 *
	 * @param patcherAccount the patcher account
	 */
	public static void cacheResult(PatcherAccount patcherAccount) {
		getPersistence().cacheResult(patcherAccount);
	}

	/**
	 * Caches the patcher accounts in the entity cache if it is enabled.
	 *
	 * @param patcherAccounts the patcher accounts
	 */
	public static void cacheResult(List<PatcherAccount> patcherAccounts) {
		getPersistence().cacheResult(patcherAccounts);
	}

	/**
	 * Creates a new patcher account with the primary key. Does not add the patcher account to the database.
	 *
	 * @param patcherAccountId the primary key for the new patcher account
	 * @return the new patcher account
	 */
	public static PatcherAccount create(long patcherAccountId) {
		return getPersistence().create(patcherAccountId);
	}

	/**
	 * Removes the patcher account with the primary key from the database. Also notifies the appropriate model listeners.
	 *
	 * @param patcherAccountId the primary key of the patcher account
	 * @return the patcher account that was removed
	 * @throws NoSuchPatcherAccountException if a patcher account with the primary key could not be found
	 */
	public static PatcherAccount remove(long patcherAccountId)
		throws com.liferay.osb.patcher.exception.NoSuchPatcherAccountException {

		return getPersistence().remove(patcherAccountId);
	}

	public static PatcherAccount updateImpl(PatcherAccount patcherAccount) {
		return getPersistence().updateImpl(patcherAccount);
	}

	/**
	 * Returns the patcher account with the primary key or throws a <code>NoSuchPatcherAccountException</code> if it could not be found.
	 *
	 * @param patcherAccountId the primary key of the patcher account
	 * @return the patcher account
	 * @throws NoSuchPatcherAccountException if a patcher account with the primary key could not be found
	 */
	public static PatcherAccount findByPrimaryKey(long patcherAccountId)
		throws com.liferay.osb.patcher.exception.NoSuchPatcherAccountException {

		return getPersistence().findByPrimaryKey(patcherAccountId);
	}

	/**
	 * Returns the patcher account with the primary key or returns <code>null</code> if it could not be found.
	 *
	 * @param patcherAccountId the primary key of the patcher account
	 * @return the patcher account, or <code>null</code> if a patcher account with the primary key could not be found
	 */
	public static PatcherAccount fetchByPrimaryKey(long patcherAccountId) {
		return getPersistence().fetchByPrimaryKey(patcherAccountId);
	}

	/**
	 * Returns all the patcher accounts.
	 *
	 * @return the patcher accounts
	 */
	public static List<PatcherAccount> findAll() {
		return getPersistence().findAll();
	}

	/**
	 * Returns a range of all the patcher accounts.
	 *
	 * <p>
	 * Useful when paginating results. Returns a maximum of <code>end - start</code> instances. <code>start</code> and <code>end</code> are not primary keys, they are indexes in the result set. Thus, <code>0</code> refers to the first result in the set. Setting both <code>start</code> and <code>end</code> to <code>QueryUtil#ALL_POS</code> will return the full result set. If <code>orderByComparator</code> is specified, then the query will include the given ORDER BY logic. If <code>orderByComparator</code> is absent, then the query will include the default ORDER BY logic from <code>PatcherAccountModelImpl</code>.
	 * </p>
	 *
	 * @param start the lower bound of the range of patcher accounts
	 * @param end the upper bound of the range of patcher accounts (not inclusive)
	 * @return the range of patcher accounts
	 */
	public static List<PatcherAccount> findAll(int start, int end) {
		return getPersistence().findAll(start, end);
	}

	/**
	 * Returns an ordered range of all the patcher accounts.
	 *
	 * <p>
	 * Useful when paginating results. Returns a maximum of <code>end - start</code> instances. <code>start</code> and <code>end</code> are not primary keys, they are indexes in the result set. Thus, <code>0</code> refers to the first result in the set. Setting both <code>start</code> and <code>end</code> to <code>QueryUtil#ALL_POS</code> will return the full result set. If <code>orderByComparator</code> is specified, then the query will include the given ORDER BY logic. If <code>orderByComparator</code> is absent, then the query will include the default ORDER BY logic from <code>PatcherAccountModelImpl</code>.
	 * </p>
	 *
	 * @param start the lower bound of the range of patcher accounts
	 * @param end the upper bound of the range of patcher accounts (not inclusive)
	 * @param orderByComparator the comparator to order the results by (optionally <code>null</code>)
	 * @return the ordered range of patcher accounts
	 */
	public static List<PatcherAccount> findAll(
		int start, int end,
		OrderByComparator<PatcherAccount> orderByComparator) {

		return getPersistence().findAll(start, end, orderByComparator);
	}

	/**
	 * Returns an ordered range of all the patcher accounts.
	 *
	 * <p>
	 * Useful when paginating results. Returns a maximum of <code>end - start</code> instances. <code>start</code> and <code>end</code> are not primary keys, they are indexes in the result set. Thus, <code>0</code> refers to the first result in the set. Setting both <code>start</code> and <code>end</code> to <code>QueryUtil#ALL_POS</code> will return the full result set. If <code>orderByComparator</code> is specified, then the query will include the given ORDER BY logic. If <code>orderByComparator</code> is absent, then the query will include the default ORDER BY logic from <code>PatcherAccountModelImpl</code>.
	 * </p>
	 *
	 * @param start the lower bound of the range of patcher accounts
	 * @param end the upper bound of the range of patcher accounts (not inclusive)
	 * @param orderByComparator the comparator to order the results by (optionally <code>null</code>)
	 * @param useFinderCache whether to use the finder cache
	 * @return the ordered range of patcher accounts
	 */
	public static List<PatcherAccount> findAll(
		int start, int end, OrderByComparator<PatcherAccount> orderByComparator,
		boolean useFinderCache) {

		return getPersistence().findAll(
			start, end, orderByComparator, useFinderCache);
	}

	/**
	 * Removes all the patcher accounts from the database.
	 */
	public static void removeAll() {
		getPersistence().removeAll();
	}

	/**
	 * Returns the number of patcher accounts.
	 *
	 * @return the number of patcher accounts
	 */
	public static int countAll() {
		return getPersistence().countAll();
	}

	/**
	 * Returns the primaryKeys of patcher builds associated with the patcher account.
	 *
	 * @param pk the primary key of the patcher account
	 * @return long[] of the primaryKeys of patcher builds associated with the patcher account
	 */
	public static long[] getPatcherBuildPrimaryKeys(long pk) {
		return getPersistence().getPatcherBuildPrimaryKeys(pk);
	}

	/**
	 * Returns all the patcher account associated with the patcher build.
	 *
	 * @param pk the primary key of the patcher build
	 * @return the patcher accounts associated with the patcher build
	 */
	public static List<PatcherAccount> getPatcherBuildPatcherAccounts(long pk) {
		return getPersistence().getPatcherBuildPatcherAccounts(pk);
	}

	/**
	 * Returns all the patcher account associated with the patcher build.
	 *
	 * <p>
	 * Useful when paginating results. Returns a maximum of <code>end - start</code> instances. <code>start</code> and <code>end</code> are not primary keys, they are indexes in the result set. Thus, <code>0</code> refers to the first result in the set. Setting both <code>start</code> and <code>end</code> to <code>QueryUtil#ALL_POS</code> will return the full result set. If <code>orderByComparator</code> is specified, then the query will include the given ORDER BY logic. If <code>orderByComparator</code> is absent, then the query will include the default ORDER BY logic from <code>PatcherAccountModelImpl</code>.
	 * </p>
	 *
	 * @param pk the primary key of the patcher build
	 * @param start the lower bound of the range of patcher builds
	 * @param end the upper bound of the range of patcher builds (not inclusive)
	 * @return the range of patcher accounts associated with the patcher build
	 */
	public static List<PatcherAccount> getPatcherBuildPatcherAccounts(
		long pk, int start, int end) {

		return getPersistence().getPatcherBuildPatcherAccounts(pk, start, end);
	}

	/**
	 * Returns all the patcher account associated with the patcher build.
	 *
	 * <p>
	 * Useful when paginating results. Returns a maximum of <code>end - start</code> instances. <code>start</code> and <code>end</code> are not primary keys, they are indexes in the result set. Thus, <code>0</code> refers to the first result in the set. Setting both <code>start</code> and <code>end</code> to <code>QueryUtil#ALL_POS</code> will return the full result set. If <code>orderByComparator</code> is specified, then the query will include the given ORDER BY logic. If <code>orderByComparator</code> is absent, then the query will include the default ORDER BY logic from <code>PatcherAccountModelImpl</code>.
	 * </p>
	 *
	 * @param pk the primary key of the patcher build
	 * @param start the lower bound of the range of patcher builds
	 * @param end the upper bound of the range of patcher builds (not inclusive)
	 * @param orderByComparator the comparator to order the results by (optionally <code>null</code>)
	 * @return the ordered range of patcher accounts associated with the patcher build
	 */
	public static List<PatcherAccount> getPatcherBuildPatcherAccounts(
		long pk, int start, int end,
		OrderByComparator<PatcherAccount> orderByComparator) {

		return getPersistence().getPatcherBuildPatcherAccounts(
			pk, start, end, orderByComparator);
	}

	/**
	 * Returns the number of patcher builds associated with the patcher account.
	 *
	 * @param pk the primary key of the patcher account
	 * @return the number of patcher builds associated with the patcher account
	 */
	public static int getPatcherBuildsSize(long pk) {
		return getPersistence().getPatcherBuildsSize(pk);
	}

	/**
	 * Returns <code>true</code> if the patcher build is associated with the patcher account.
	 *
	 * @param pk the primary key of the patcher account
	 * @param patcherBuildPK the primary key of the patcher build
	 * @return <code>true</code> if the patcher build is associated with the patcher account; <code>false</code> otherwise
	 */
	public static boolean containsPatcherBuild(long pk, long patcherBuildPK) {
		return getPersistence().containsPatcherBuild(pk, patcherBuildPK);
	}

	/**
	 * Returns <code>true</code> if the patcher account has any patcher builds associated with it.
	 *
	 * @param pk the primary key of the patcher account to check for associations with patcher builds
	 * @return <code>true</code> if the patcher account has any patcher builds associated with it; <code>false</code> otherwise
	 */
	public static boolean containsPatcherBuilds(long pk) {
		return getPersistence().containsPatcherBuilds(pk);
	}

	/**
	 * Adds an association between the patcher account and the patcher build. Also notifies the appropriate model listeners and clears the mapping table finder cache.
	 *
	 * @param pk the primary key of the patcher account
	 * @param patcherBuildPK the primary key of the patcher build
	 * @return <code>true</code> if an association between the patcher account and the patcher build was added; <code>false</code> if they were already associated
	 */
	public static boolean addPatcherBuild(long pk, long patcherBuildPK) {
		return getPersistence().addPatcherBuild(pk, patcherBuildPK);
	}

	/**
	 * Adds an association between the patcher account and the patcher build. Also notifies the appropriate model listeners and clears the mapping table finder cache.
	 *
	 * @param pk the primary key of the patcher account
	 * @param patcherBuild the patcher build
	 * @return <code>true</code> if an association between the patcher account and the patcher build was added; <code>false</code> if they were already associated
	 */
	public static boolean addPatcherBuild(
		long pk, com.liferay.osb.patcher.model.PatcherBuild patcherBuild) {

		return getPersistence().addPatcherBuild(pk, patcherBuild);
	}

	/**
	 * Adds an association between the patcher account and the patcher builds. Also notifies the appropriate model listeners and clears the mapping table finder cache.
	 *
	 * @param pk the primary key of the patcher account
	 * @param patcherBuildPKs the primary keys of the patcher builds
	 * @return <code>true</code> if at least one association between the patcher account and the patcher builds was added; <code>false</code> if they were all already associated
	 */
	public static boolean addPatcherBuilds(long pk, long[] patcherBuildPKs) {
		return getPersistence().addPatcherBuilds(pk, patcherBuildPKs);
	}

	/**
	 * Adds an association between the patcher account and the patcher builds. Also notifies the appropriate model listeners and clears the mapping table finder cache.
	 *
	 * @param pk the primary key of the patcher account
	 * @param patcherBuilds the patcher builds
	 * @return <code>true</code> if at least one association between the patcher account and the patcher builds was added; <code>false</code> if they were all already associated
	 */
	public static boolean addPatcherBuilds(
		long pk,
		List<com.liferay.osb.patcher.model.PatcherBuild> patcherBuilds) {

		return getPersistence().addPatcherBuilds(pk, patcherBuilds);
	}

	/**
	 * Clears all associations between the patcher account and its patcher builds. Also notifies the appropriate model listeners and clears the mapping table finder cache.
	 *
	 * @param pk the primary key of the patcher account to clear the associated patcher builds from
	 */
	public static void clearPatcherBuilds(long pk) {
		getPersistence().clearPatcherBuilds(pk);
	}

	/**
	 * Removes the association between the patcher account and the patcher build. Also notifies the appropriate model listeners and clears the mapping table finder cache.
	 *
	 * @param pk the primary key of the patcher account
	 * @param patcherBuildPK the primary key of the patcher build
	 */
	public static void removePatcherBuild(long pk, long patcherBuildPK) {
		getPersistence().removePatcherBuild(pk, patcherBuildPK);
	}

	/**
	 * Removes the association between the patcher account and the patcher build. Also notifies the appropriate model listeners and clears the mapping table finder cache.
	 *
	 * @param pk the primary key of the patcher account
	 * @param patcherBuild the patcher build
	 */
	public static void removePatcherBuild(
		long pk, com.liferay.osb.patcher.model.PatcherBuild patcherBuild) {

		getPersistence().removePatcherBuild(pk, patcherBuild);
	}

	/**
	 * Removes the association between the patcher account and the patcher builds. Also notifies the appropriate model listeners and clears the mapping table finder cache.
	 *
	 * @param pk the primary key of the patcher account
	 * @param patcherBuildPKs the primary keys of the patcher builds
	 */
	public static void removePatcherBuilds(long pk, long[] patcherBuildPKs) {
		getPersistence().removePatcherBuilds(pk, patcherBuildPKs);
	}

	/**
	 * Removes the association between the patcher account and the patcher builds. Also notifies the appropriate model listeners and clears the mapping table finder cache.
	 *
	 * @param pk the primary key of the patcher account
	 * @param patcherBuilds the patcher builds
	 */
	public static void removePatcherBuilds(
		long pk,
		List<com.liferay.osb.patcher.model.PatcherBuild> patcherBuilds) {

		getPersistence().removePatcherBuilds(pk, patcherBuilds);
	}

	/**
	 * Sets the patcher builds associated with the patcher account, removing and adding associations as necessary. Also notifies the appropriate model listeners and clears the mapping table finder cache.
	 *
	 * @param pk the primary key of the patcher account
	 * @param patcherBuildPKs the primary keys of the patcher builds to be associated with the patcher account
	 */
	public static void setPatcherBuilds(long pk, long[] patcherBuildPKs) {
		getPersistence().setPatcherBuilds(pk, patcherBuildPKs);
	}

	/**
	 * Sets the patcher builds associated with the patcher account, removing and adding associations as necessary. Also notifies the appropriate model listeners and clears the mapping table finder cache.
	 *
	 * @param pk the primary key of the patcher account
	 * @param patcherBuilds the patcher builds to be associated with the patcher account
	 */
	public static void setPatcherBuilds(
		long pk,
		List<com.liferay.osb.patcher.model.PatcherBuild> patcherBuilds) {

		getPersistence().setPatcherBuilds(pk, patcherBuilds);
	}

	public static PatcherAccountPersistence getPersistence() {
		return _persistence;
	}

	public static void setPersistence(PatcherAccountPersistence persistence) {
		_persistence = persistence;
	}

	private static volatile PatcherAccountPersistence _persistence;

}