/**
 * SPDX-FileCopyrightText: (c) 2000 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

package com.liferay.account.service;

import com.liferay.account.model.AccountGroup;
import com.liferay.petra.sql.dsl.query.DSLQuery;
import com.liferay.portal.kernel.dao.orm.DynamicQuery;
import com.liferay.portal.kernel.exception.PortalException;
import com.liferay.portal.kernel.model.PersistedModel;
import com.liferay.portal.kernel.module.service.Snapshot;
import com.liferay.portal.kernel.util.OrderByComparator;

import java.io.Serializable;

import java.util.List;

/**
 * Provides the local service utility for AccountGroup. This utility wraps
 * <code>com.liferay.account.service.impl.AccountGroupLocalServiceImpl</code> and
 * is an access point for service operations in application layer code running
 * on the local server. Methods of this service will not have security checks
 * based on the propagated JAAS credentials because this service can only be
 * accessed from within the same VM.
 *
 * @author Brian Wing Shun Chan
 * @see AccountGroupLocalService
 * @generated
 */
public class AccountGroupLocalServiceUtil {

	/*
	 * NOTE FOR DEVELOPERS:
	 *
	 * Never modify this class directly. Add custom service methods to <code>com.liferay.account.service.impl.AccountGroupLocalServiceImpl</code> and rerun ServiceBuilder to regenerate this class.
	 */

	/**
	 * Adds the account group to the database. Also notifies the appropriate model listeners.
	 *
	 * <p>
	 * <strong>Important:</strong> Inspect AccountGroupLocalServiceImpl for overloaded versions of the method. If provided, use these entry points to the API, as the implementation logic may require the additional parameters defined there.
	 * </p>
	 *
	 * @param accountGroup the account group
	 * @return the account group that was added
	 */
	public static AccountGroup addAccountGroup(AccountGroup accountGroup) {
		return getService().addAccountGroup(accountGroup);
	}

	public static AccountGroup addAccountGroup(
			String externalReferenceCode, long userId, String description,
			String name,
			com.liferay.portal.kernel.service.ServiceContext serviceContext)
		throws PortalException {

		return getService().addAccountGroup(
			externalReferenceCode, userId, description, name, serviceContext);
	}

	public static AccountGroup checkGuestAccountGroup(long companyId)
		throws PortalException {

		return getService().checkGuestAccountGroup(companyId);
	}

	/**
	 * Creates a new account group with the primary key. Does not add the account group to the database.
	 *
	 * @param accountGroupId the primary key for the new account group
	 * @return the new account group
	 */
	public static AccountGroup createAccountGroup(long accountGroupId) {
		return getService().createAccountGroup(accountGroupId);
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
	 * Deletes the account group from the database. Also notifies the appropriate model listeners.
	 *
	 * <p>
	 * <strong>Important:</strong> Inspect AccountGroupLocalServiceImpl for overloaded versions of the method. If provided, use these entry points to the API, as the implementation logic may require the additional parameters defined there.
	 * </p>
	 *
	 * @param accountGroup the account group
	 * @return the account group that was removed
	 * @throws PortalException
	 */
	public static AccountGroup deleteAccountGroup(AccountGroup accountGroup)
		throws PortalException {

		return getService().deleteAccountGroup(accountGroup);
	}

	/**
	 * Deletes the account group with the primary key from the database. Also notifies the appropriate model listeners.
	 *
	 * <p>
	 * <strong>Important:</strong> Inspect AccountGroupLocalServiceImpl for overloaded versions of the method. If provided, use these entry points to the API, as the implementation logic may require the additional parameters defined there.
	 * </p>
	 *
	 * @param accountGroupId the primary key of the account group
	 * @return the account group that was removed
	 * @throws PortalException if a account group with the primary key could not be found
	 */
	public static AccountGroup deleteAccountGroup(long accountGroupId)
		throws PortalException {

		return getService().deleteAccountGroup(accountGroupId);
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
	 * Useful when paginating results. Returns a maximum of <code>end - start</code> instances. <code>start</code> and <code>end</code> are not primary keys, they are indexes in the result set. Thus, <code>0</code> refers to the first result in the set. Setting both <code>start</code> and <code>end</code> to <code>com.liferay.portal.kernel.dao.orm.QueryUtil#ALL_POS</code> will return the full result set. If <code>orderByComparator</code> is specified, then the query will include the given ORDER BY logic. If <code>orderByComparator</code> is absent, then the query will include the default ORDER BY logic from <code>com.liferay.account.model.impl.AccountGroupModelImpl</code>.
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
	 * Useful when paginating results. Returns a maximum of <code>end - start</code> instances. <code>start</code> and <code>end</code> are not primary keys, they are indexes in the result set. Thus, <code>0</code> refers to the first result in the set. Setting both <code>start</code> and <code>end</code> to <code>com.liferay.portal.kernel.dao.orm.QueryUtil#ALL_POS</code> will return the full result set. If <code>orderByComparator</code> is specified, then the query will include the given ORDER BY logic. If <code>orderByComparator</code> is absent, then the query will include the default ORDER BY logic from <code>com.liferay.account.model.impl.AccountGroupModelImpl</code>.
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

	public static AccountGroup fetchAccountGroup(long accountGroupId) {
		return getService().fetchAccountGroup(accountGroupId);
	}

	public static AccountGroup fetchAccountGroupByExternalReferenceCode(
		String externalReferenceCode, long companyId) {

		return getService().fetchAccountGroupByExternalReferenceCode(
			externalReferenceCode, companyId);
	}

	/**
	 * Returns the account group with the matching UUID and company.
	 *
	 * @param uuid the account group's UUID
	 * @param companyId the primary key of the company
	 * @return the matching account group, or <code>null</code> if a matching account group could not be found
	 */
	public static AccountGroup fetchAccountGroupByUuidAndCompanyId(
		String uuid, long companyId) {

		return getService().fetchAccountGroupByUuidAndCompanyId(
			uuid, companyId);
	}

	/**
	 * Returns the account group with the primary key.
	 *
	 * @param accountGroupId the primary key of the account group
	 * @return the account group
	 * @throws PortalException if a account group with the primary key could not be found
	 */
	public static AccountGroup getAccountGroup(long accountGroupId)
		throws PortalException {

		return getService().getAccountGroup(accountGroupId);
	}

	public static AccountGroup getAccountGroupByExternalReferenceCode(
			String externalReferenceCode, long companyId)
		throws PortalException {

		return getService().getAccountGroupByExternalReferenceCode(
			externalReferenceCode, companyId);
	}

	/**
	 * Returns the account group with the matching UUID and company.
	 *
	 * @param uuid the account group's UUID
	 * @param companyId the primary key of the company
	 * @return the matching account group
	 * @throws PortalException if a matching account group could not be found
	 */
	public static AccountGroup getAccountGroupByUuidAndCompanyId(
			String uuid, long companyId)
		throws PortalException {

		return getService().getAccountGroupByUuidAndCompanyId(uuid, companyId);
	}

	public static long[] getAccountGroupIds(long accountEntryId) {
		return getService().getAccountGroupIds(accountEntryId);
	}

	/**
	 * Returns a range of all the account groups.
	 *
	 * <p>
	 * Useful when paginating results. Returns a maximum of <code>end - start</code> instances. <code>start</code> and <code>end</code> are not primary keys, they are indexes in the result set. Thus, <code>0</code> refers to the first result in the set. Setting both <code>start</code> and <code>end</code> to <code>com.liferay.portal.kernel.dao.orm.QueryUtil#ALL_POS</code> will return the full result set. If <code>orderByComparator</code> is specified, then the query will include the given ORDER BY logic. If <code>orderByComparator</code> is absent, then the query will include the default ORDER BY logic from <code>com.liferay.account.model.impl.AccountGroupModelImpl</code>.
	 * </p>
	 *
	 * @param start the lower bound of the range of account groups
	 * @param end the upper bound of the range of account groups (not inclusive)
	 * @return the range of account groups
	 */
	public static List<AccountGroup> getAccountGroups(int start, int end) {
		return getService().getAccountGroups(start, end);
	}

	public static List<AccountGroup> getAccountGroups(
		long companyId, int start, int end,
		OrderByComparator<AccountGroup> orderByComparator) {

		return getService().getAccountGroups(
			companyId, start, end, orderByComparator);
	}

	public static List<AccountGroup> getAccountGroups(
		long companyId, String name, int start, int end,
		OrderByComparator<AccountGroup> orderByComparator) {

		return getService().getAccountGroups(
			companyId, name, start, end, orderByComparator);
	}

	public static List<AccountGroup> getAccountGroupsByAccountEntryId(
		long accountEntryId, int start, int end) {

		return getService().getAccountGroupsByAccountEntryId(
			accountEntryId, start, end);
	}

	public static List<AccountGroup> getAccountGroupsByAccountGroupId(
		long[] accountGroupIds) {

		return getService().getAccountGroupsByAccountGroupId(accountGroupIds);
	}

	/**
	 * Returns the number of account groups.
	 *
	 * @return the number of account groups
	 */
	public static int getAccountGroupsCount() {
		return getService().getAccountGroupsCount();
	}

	public static int getAccountGroupsCount(long companyId) {
		return getService().getAccountGroupsCount(companyId);
	}

	public static long getAccountGroupsCount(long companyId, String name) {
		return getService().getAccountGroupsCount(companyId, name);
	}

	public static int getAccountGroupsCountByAccountEntryId(
		long accountEntryId) {

		return getService().getAccountGroupsCountByAccountEntryId(
			accountEntryId);
	}

	public static com.liferay.portal.kernel.dao.orm.ActionableDynamicQuery
		getActionableDynamicQuery() {

		return getService().getActionableDynamicQuery();
	}

	public static AccountGroup getDefaultAccountGroup(long companyId) {
		return getService().getDefaultAccountGroup(companyId);
	}

	public static com.liferay.portal.kernel.dao.orm.ExportActionableDynamicQuery
		getExportActionableDynamicQuery(
			com.liferay.exportimport.kernel.lar.PortletDataContext
				portletDataContext) {

		return getService().getExportActionableDynamicQuery(portletDataContext);
	}

	public static
		com.liferay.portal.kernel.dao.orm.IndexableActionableDynamicQuery
			getIndexableActionableDynamicQuery() {

		return getService().getIndexableActionableDynamicQuery();
	}

	public static AccountGroup getOrAddEmptyAccountGroup(
			String externalReferenceCode, long companyId, long userId,
			String name)
		throws PortalException {

		return getService().getOrAddEmptyAccountGroup(
			externalReferenceCode, companyId, userId, name);
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

	public static boolean hasDefaultAccountGroup(long companyId) {
		return getService().hasDefaultAccountGroup(companyId);
	}

	public static com.liferay.portal.kernel.search.BaseModelSearchResult
		<AccountGroup> searchAccountGroups(
			long companyId, String keywords, int start, int end,
			OrderByComparator<AccountGroup> orderByComparator) {

		return getService().searchAccountGroups(
			companyId, keywords, start, end, orderByComparator);
	}

	public static com.liferay.portal.kernel.search.BaseModelSearchResult
		<AccountGroup> searchAccountGroups(
			long companyId, String keywords,
			java.util.LinkedHashMap<String, Object> params, int start, int end,
			OrderByComparator<AccountGroup> orderByComparator) {

		return getService().searchAccountGroups(
			companyId, keywords, params, start, end, orderByComparator);
	}

	/**
	 * Updates the account group in the database or adds it if it does not yet exist. Also notifies the appropriate model listeners.
	 *
	 * <p>
	 * <strong>Important:</strong> Inspect AccountGroupLocalServiceImpl for overloaded versions of the method. If provided, use these entry points to the API, as the implementation logic may require the additional parameters defined there.
	 * </p>
	 *
	 * @param accountGroup the account group
	 * @return the account group that was updated
	 */
	public static AccountGroup updateAccountGroup(AccountGroup accountGroup) {
		return getService().updateAccountGroup(accountGroup);
	}

	public static AccountGroup updateAccountGroup(
			String externalReferenceCode, long accountGroupId,
			String description, String name,
			com.liferay.portal.kernel.service.ServiceContext serviceContext)
		throws PortalException {

		return getService().updateAccountGroup(
			externalReferenceCode, accountGroupId, description, name,
			serviceContext);
	}

	public static AccountGroup updateExternalReferenceCode(
			AccountGroup accountGroup, String externalReferenceCode)
		throws PortalException {

		return getService().updateExternalReferenceCode(
			accountGroup, externalReferenceCode);
	}

	public static AccountGroup updateExternalReferenceCode(
			long accountGroupId, String externalReferenceCode)
		throws PortalException {

		return getService().updateExternalReferenceCode(
			accountGroupId, externalReferenceCode);
	}

	public static AccountGroupLocalService getService() {
		return _serviceSnapshot.get();
	}

	private static final Snapshot<AccountGroupLocalService> _serviceSnapshot =
		new Snapshot<>(
			AccountGroupLocalServiceUtil.class, AccountGroupLocalService.class);

}