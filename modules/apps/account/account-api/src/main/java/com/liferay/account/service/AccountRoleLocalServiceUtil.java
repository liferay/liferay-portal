/**
 * SPDX-FileCopyrightText: (c) 2000 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

package com.liferay.account.service;

import com.liferay.account.model.AccountRole;
import com.liferay.petra.sql.dsl.query.DSLQuery;
import com.liferay.portal.kernel.dao.orm.DynamicQuery;
import com.liferay.portal.kernel.exception.PortalException;
import com.liferay.portal.kernel.model.PersistedModel;
import com.liferay.portal.kernel.module.service.Snapshot;
import com.liferay.portal.kernel.util.OrderByComparator;

import java.io.Serializable;

import java.util.List;
import java.util.Map;

/**
 * Provides the local service utility for AccountRole. This utility wraps
 * <code>com.liferay.account.service.impl.AccountRoleLocalServiceImpl</code> and
 * is an access point for service operations in application layer code running
 * on the local server. Methods of this service will not have security checks
 * based on the propagated JAAS credentials because this service can only be
 * accessed from within the same VM.
 *
 * @author Brian Wing Shun Chan
 * @see AccountRoleLocalService
 * @generated
 */
public class AccountRoleLocalServiceUtil {

	/*
	 * NOTE FOR DEVELOPERS:
	 *
	 * Never modify this class directly. Add custom service methods to <code>com.liferay.account.service.impl.AccountRoleLocalServiceImpl</code> and rerun ServiceBuilder to regenerate this class.
	 */

	/**
	 * Adds the account role to the database. Also notifies the appropriate model listeners.
	 *
	 * <p>
	 * <strong>Important:</strong> Inspect AccountRoleLocalServiceImpl for overloaded versions of the method. If provided, use these entry points to the API, as the implementation logic may require the additional parameters defined there.
	 * </p>
	 *
	 * @param accountRole the account role
	 * @return the account role that was added
	 */
	public static AccountRole addAccountRole(AccountRole accountRole) {
		return getService().addAccountRole(accountRole);
	}

	public static AccountRole addAccountRole(
			String externalReferenceCode, long userId, long accountEntryId,
			String name, Map<java.util.Locale, String> titleMap,
			Map<java.util.Locale, String> descriptionMap)
		throws PortalException {

		return getService().addAccountRole(
			externalReferenceCode, userId, accountEntryId, name, titleMap,
			descriptionMap);
	}

	public static void associateUser(
			long accountEntryId, long accountRoleId, long userId)
		throws PortalException {

		getService().associateUser(accountEntryId, accountRoleId, userId);
	}

	public static void associateUser(
			long accountEntryId, long[] accountRoleIds, long userId)
		throws PortalException {

		getService().associateUser(accountEntryId, accountRoleIds, userId);
	}

	/**
	 * Creates a new account role with the primary key. Does not add the account role to the database.
	 *
	 * @param accountRoleId the primary key for the new account role
	 * @return the new account role
	 */
	public static AccountRole createAccountRole(long accountRoleId) {
		return getService().createAccountRole(accountRoleId);
	}

	/**
	 * @throws PortalException
	 */
	public static PersistedModel createPersistedModel(
			Serializable primaryKeyObj)
		throws PortalException {

		return getService().createPersistedModel(primaryKeyObj);
	}

	/**
	 * Deletes the account role from the database. Also notifies the appropriate model listeners.
	 *
	 * <p>
	 * <strong>Important:</strong> Inspect AccountRoleLocalServiceImpl for overloaded versions of the method. If provided, use these entry points to the API, as the implementation logic may require the additional parameters defined there.
	 * </p>
	 *
	 * @param accountRole the account role
	 * @return the account role that was removed
	 * @throws PortalException
	 */
	public static AccountRole deleteAccountRole(AccountRole accountRole)
		throws PortalException {

		return getService().deleteAccountRole(accountRole);
	}

	/**
	 * Deletes the account role with the primary key from the database. Also notifies the appropriate model listeners.
	 *
	 * <p>
	 * <strong>Important:</strong> Inspect AccountRoleLocalServiceImpl for overloaded versions of the method. If provided, use these entry points to the API, as the implementation logic may require the additional parameters defined there.
	 * </p>
	 *
	 * @param accountRoleId the primary key of the account role
	 * @return the account role that was removed
	 * @throws PortalException if a account role with the primary key could not be found
	 */
	public static AccountRole deleteAccountRole(long accountRoleId)
		throws PortalException {

		return getService().deleteAccountRole(accountRoleId);
	}

	public static void deleteAccountRolesByCompanyId(long companyId)
		throws PortalException {

		getService().deleteAccountRolesByCompanyId(companyId);
	}

	/**
	 * @throws PortalException
	 */
	public static PersistedModel deletePersistedModel(
			PersistedModel persistedModel)
		throws PortalException {

		return getService().deletePersistedModel(persistedModel);
	}

	public static <T> T dslQuery(DSLQuery dslQuery) {
		return getService().dslQuery(dslQuery);
	}

	public static int dslQueryCount(DSLQuery dslQuery) {
		return getService().dslQueryCount(dslQuery);
	}

	public static DynamicQuery dynamicQuery() {
		return getService().dynamicQuery();
	}

	/**
	 * Performs a dynamic query on the database and returns the matching rows.
	 *
	 * @param dynamicQuery the dynamic query
	 * @return the matching rows
	 */
	public static <T> List<T> dynamicQuery(DynamicQuery dynamicQuery) {
		return getService().dynamicQuery(dynamicQuery);
	}

	/**
	 * Performs a dynamic query on the database and returns a range of the matching rows.
	 *
	 * <p>
	 * Useful when paginating results. Returns a maximum of <code>end - start</code> instances. <code>start</code> and <code>end</code> are not primary keys, they are indexes in the result set. Thus, <code>0</code> refers to the first result in the set. Setting both <code>start</code> and <code>end</code> to <code>com.liferay.portal.kernel.dao.orm.QueryUtil#ALL_POS</code> will return the full result set. If <code>orderByComparator</code> is specified, then the query will include the given ORDER BY logic. If <code>orderByComparator</code> is absent, then the query will include the default ORDER BY logic from <code>com.liferay.account.model.impl.AccountRoleModelImpl</code>.
	 * </p>
	 *
	 * @param dynamicQuery the dynamic query
	 * @param start the lower bound of the range of model instances
	 * @param end the upper bound of the range of model instances (not inclusive)
	 * @return the range of matching rows
	 */
	public static <T> List<T> dynamicQuery(
		DynamicQuery dynamicQuery, int start, int end) {

		return getService().dynamicQuery(dynamicQuery, start, end);
	}

	/**
	 * Performs a dynamic query on the database and returns an ordered range of the matching rows.
	 *
	 * <p>
	 * Useful when paginating results. Returns a maximum of <code>end - start</code> instances. <code>start</code> and <code>end</code> are not primary keys, they are indexes in the result set. Thus, <code>0</code> refers to the first result in the set. Setting both <code>start</code> and <code>end</code> to <code>com.liferay.portal.kernel.dao.orm.QueryUtil#ALL_POS</code> will return the full result set. If <code>orderByComparator</code> is specified, then the query will include the given ORDER BY logic. If <code>orderByComparator</code> is absent, then the query will include the default ORDER BY logic from <code>com.liferay.account.model.impl.AccountRoleModelImpl</code>.
	 * </p>
	 *
	 * @param dynamicQuery the dynamic query
	 * @param start the lower bound of the range of model instances
	 * @param end the upper bound of the range of model instances (not inclusive)
	 * @param orderByComparator the comparator to order the results by (optionally <code>null</code>)
	 * @return the ordered range of matching rows
	 */
	public static <T> List<T> dynamicQuery(
		DynamicQuery dynamicQuery, int start, int end,
		OrderByComparator<T> orderByComparator) {

		return getService().dynamicQuery(
			dynamicQuery, start, end, orderByComparator);
	}

	/**
	 * Returns the number of rows matching the dynamic query.
	 *
	 * @param dynamicQuery the dynamic query
	 * @return the number of rows matching the dynamic query
	 */
	public static long dynamicQueryCount(DynamicQuery dynamicQuery) {
		return getService().dynamicQueryCount(dynamicQuery);
	}

	/**
	 * Returns the number of rows matching the dynamic query.
	 *
	 * @param dynamicQuery the dynamic query
	 * @param projection the projection to apply to the query
	 * @return the number of rows matching the dynamic query
	 */
	public static long dynamicQueryCount(
		DynamicQuery dynamicQuery,
		com.liferay.portal.kernel.dao.orm.Projection projection) {

		return getService().dynamicQueryCount(dynamicQuery, projection);
	}

	public static AccountRole fetchAccountRole(long accountRoleId) {
		return getService().fetchAccountRole(accountRoleId);
	}

	public static AccountRole fetchAccountRoleByExternalReferenceCode(
		String externalReferenceCode, long companyId) {

		return getService().fetchAccountRoleByExternalReferenceCode(
			externalReferenceCode, companyId);
	}

	public static AccountRole fetchAccountRoleByRoleId(long roleId) {
		return getService().fetchAccountRoleByRoleId(roleId);
	}

	/**
	 * Returns the account role with the primary key.
	 *
	 * @param accountRoleId the primary key of the account role
	 * @return the account role
	 * @throws PortalException if a account role with the primary key could not be found
	 */
	public static AccountRole getAccountRole(long accountRoleId)
		throws PortalException {

		return getService().getAccountRole(accountRoleId);
	}

	public static AccountRole getAccountRoleByExternalReferenceCode(
			String externalReferenceCode, long companyId)
		throws PortalException {

		return getService().getAccountRoleByExternalReferenceCode(
			externalReferenceCode, companyId);
	}

	public static AccountRole getAccountRoleByRoleId(long roleId)
		throws PortalException {

		return getService().getAccountRoleByRoleId(roleId);
	}

	/**
	 * Returns a range of all the account roles.
	 *
	 * <p>
	 * Useful when paginating results. Returns a maximum of <code>end - start</code> instances. <code>start</code> and <code>end</code> are not primary keys, they are indexes in the result set. Thus, <code>0</code> refers to the first result in the set. Setting both <code>start</code> and <code>end</code> to <code>com.liferay.portal.kernel.dao.orm.QueryUtil#ALL_POS</code> will return the full result set. If <code>orderByComparator</code> is specified, then the query will include the given ORDER BY logic. If <code>orderByComparator</code> is absent, then the query will include the default ORDER BY logic from <code>com.liferay.account.model.impl.AccountRoleModelImpl</code>.
	 * </p>
	 *
	 * @param start the lower bound of the range of account roles
	 * @param end the upper bound of the range of account roles (not inclusive)
	 * @return the range of account roles
	 */
	public static List<AccountRole> getAccountRoles(int start, int end) {
		return getService().getAccountRoles(start, end);
	}

	public static List<AccountRole> getAccountRoles(
			long accountEntryId, long userId)
		throws PortalException {

		return getService().getAccountRoles(accountEntryId, userId);
	}

	public static List<AccountRole> getAccountRolesByAccountEntryIds(
		long companyId, long[] accountEntryIds) {

		return getService().getAccountRolesByAccountEntryIds(
			companyId, accountEntryIds);
	}

	public static List<AccountRole> getAccountRolesByAccountEntryIds(
		long[] accountEntryIds) {

		return getService().getAccountRolesByAccountEntryIds(accountEntryIds);
	}

	/**
	 * Returns the number of account roles.
	 *
	 * @return the number of account roles
	 */
	public static int getAccountRolesCount() {
		return getService().getAccountRolesCount();
	}

	public static com.liferay.portal.kernel.dao.orm.ActionableDynamicQuery
		getActionableDynamicQuery() {

		return getService().getActionableDynamicQuery();
	}

	public static
		com.liferay.portal.kernel.dao.orm.IndexableActionableDynamicQuery
			getIndexableActionableDynamicQuery() {

		return getService().getIndexableActionableDynamicQuery();
	}

	public static AccountRole getOrAddEmptyAccountRole(
			String externalReferenceCode, long companyId, long userId,
			long accountEntryId, String name)
		throws PortalException {

		return getService().getOrAddEmptyAccountRole(
			externalReferenceCode, companyId, userId, accountEntryId, name);
	}

	/**
	 * Returns the OSGi service identifier.
	 *
	 * @return the OSGi service identifier
	 */
	public static String getOSGiServiceIdentifier() {
		return getService().getOSGiServiceIdentifier();
	}

	/**
	 * @throws PortalException
	 */
	public static PersistedModel getPersistedModel(Serializable primaryKeyObj)
		throws PortalException {

		return getService().getPersistedModel(primaryKeyObj);
	}

	public static boolean hasUserAccountRole(
			long accountEntryId, long accountRoleId, long userId)
		throws PortalException {

		return getService().hasUserAccountRole(
			accountEntryId, accountRoleId, userId);
	}

	public static com.liferay.portal.kernel.search.BaseModelSearchResult
		<AccountRole> searchAccountRoles(
			long companyId, long[] accountEntryIds, String keywords,
			java.util.LinkedHashMap<String, Object> params, int start, int end,
			OrderByComparator<?> orderByComparator) {

		return getService().searchAccountRoles(
			companyId, accountEntryIds, keywords, params, start, end,
			orderByComparator);
	}

	public static void setUserAccountRoles(
			long accountEntryId, long[] accountRoleIds, long userId)
		throws PortalException {

		getService().setUserAccountRoles(
			accountEntryId, accountRoleIds, userId);
	}

	public static void unassociateUser(
			long accountEntryId, long accountRoleId, long userId)
		throws PortalException {

		getService().unassociateUser(accountEntryId, accountRoleId, userId);
	}

	/**
	 * Updates the account role in the database or adds it if it does not yet exist. Also notifies the appropriate model listeners.
	 *
	 * <p>
	 * <strong>Important:</strong> Inspect AccountRoleLocalServiceImpl for overloaded versions of the method. If provided, use these entry points to the API, as the implementation logic may require the additional parameters defined there.
	 * </p>
	 *
	 * @param accountRole the account role
	 * @return the account role that was updated
	 */
	public static AccountRole updateAccountRole(AccountRole accountRole) {
		return getService().updateAccountRole(accountRole);
	}

	public static AccountRoleLocalService getService() {
		return _serviceSnapshot.get();
	}

	private static final Snapshot<AccountRoleLocalService> _serviceSnapshot =
		new Snapshot<>(
			AccountRoleLocalServiceUtil.class, AccountRoleLocalService.class);

}