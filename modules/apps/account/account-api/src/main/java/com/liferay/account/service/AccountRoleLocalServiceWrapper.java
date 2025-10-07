/**
 * SPDX-FileCopyrightText: (c) 2000 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

package com.liferay.account.service;

import com.liferay.portal.kernel.service.ServiceWrapper;
import com.liferay.portal.kernel.service.persistence.BasePersistence;

/**
 * Provides a wrapper for {@link AccountRoleLocalService}.
 *
 * @author Brian Wing Shun Chan
 * @see AccountRoleLocalService
 * @generated
 */
public class AccountRoleLocalServiceWrapper
	implements AccountRoleLocalService,
			   ServiceWrapper<AccountRoleLocalService> {

	public AccountRoleLocalServiceWrapper() {
		this(null);
	}

	public AccountRoleLocalServiceWrapper(
		AccountRoleLocalService accountRoleLocalService) {

		_accountRoleLocalService = accountRoleLocalService;
	}

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
	@Override
	public com.liferay.account.model.AccountRole addAccountRole(
		com.liferay.account.model.AccountRole accountRole) {

		return _accountRoleLocalService.addAccountRole(accountRole);
	}

	@Override
	public com.liferay.account.model.AccountRole addAccountRole(
			String externalReferenceCode, long userId, long accountEntryId,
			String name, java.util.Map<java.util.Locale, String> titleMap,
			java.util.Map<java.util.Locale, String> descriptionMap)
		throws com.liferay.portal.kernel.exception.PortalException {

		return _accountRoleLocalService.addAccountRole(
			externalReferenceCode, userId, accountEntryId, name, titleMap,
			descriptionMap);
	}

	@Override
	public void associateUser(
			long accountEntryId, long accountRoleId, long userId)
		throws com.liferay.portal.kernel.exception.PortalException {

		_accountRoleLocalService.associateUser(
			accountEntryId, accountRoleId, userId);
	}

	@Override
	public void associateUser(
			long accountEntryId, long[] accountRoleIds, long userId)
		throws com.liferay.portal.kernel.exception.PortalException {

		_accountRoleLocalService.associateUser(
			accountEntryId, accountRoleIds, userId);
	}

	/**
	 * Creates a new account role with the primary key. Does not add the account role to the database.
	 *
	 * @param accountRoleId the primary key for the new account role
	 * @return the new account role
	 */
	@Override
	public com.liferay.account.model.AccountRole createAccountRole(
		long accountRoleId) {

		return _accountRoleLocalService.createAccountRole(accountRoleId);
	}

	/**
	 * @throws PortalException
	 */
	@Override
	public com.liferay.portal.kernel.model.PersistedModel createPersistedModel(
			java.io.Serializable primaryKeyObj)
		throws com.liferay.portal.kernel.exception.PortalException {

		return _accountRoleLocalService.createPersistedModel(primaryKeyObj);
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
	@Override
	public com.liferay.account.model.AccountRole deleteAccountRole(
			com.liferay.account.model.AccountRole accountRole)
		throws com.liferay.portal.kernel.exception.PortalException {

		return _accountRoleLocalService.deleteAccountRole(accountRole);
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
	@Override
	public com.liferay.account.model.AccountRole deleteAccountRole(
			long accountRoleId)
		throws com.liferay.portal.kernel.exception.PortalException {

		return _accountRoleLocalService.deleteAccountRole(accountRoleId);
	}

	@Override
	public void deleteAccountRolesByCompanyId(long companyId)
		throws com.liferay.portal.kernel.exception.PortalException {

		_accountRoleLocalService.deleteAccountRolesByCompanyId(companyId);
	}

	/**
	 * @throws PortalException
	 */
	@Override
	public com.liferay.portal.kernel.model.PersistedModel deletePersistedModel(
			com.liferay.portal.kernel.model.PersistedModel persistedModel)
		throws com.liferay.portal.kernel.exception.PortalException {

		return _accountRoleLocalService.deletePersistedModel(persistedModel);
	}

	@Override
	public <T> T dslQuery(com.liferay.petra.sql.dsl.query.DSLQuery dslQuery) {
		return _accountRoleLocalService.dslQuery(dslQuery);
	}

	@Override
	public int dslQueryCount(
		com.liferay.petra.sql.dsl.query.DSLQuery dslQuery) {

		return _accountRoleLocalService.dslQueryCount(dslQuery);
	}

	@Override
	public com.liferay.portal.kernel.dao.orm.DynamicQuery dynamicQuery() {
		return _accountRoleLocalService.dynamicQuery();
	}

	/**
	 * Performs a dynamic query on the database and returns the matching rows.
	 *
	 * @param dynamicQuery the dynamic query
	 * @return the matching rows
	 */
	@Override
	public <T> java.util.List<T> dynamicQuery(
		com.liferay.portal.kernel.dao.orm.DynamicQuery dynamicQuery) {

		return _accountRoleLocalService.dynamicQuery(dynamicQuery);
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
	@Override
	public <T> java.util.List<T> dynamicQuery(
		com.liferay.portal.kernel.dao.orm.DynamicQuery dynamicQuery, int start,
		int end) {

		return _accountRoleLocalService.dynamicQuery(dynamicQuery, start, end);
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
	@Override
	public <T> java.util.List<T> dynamicQuery(
		com.liferay.portal.kernel.dao.orm.DynamicQuery dynamicQuery, int start,
		int end,
		com.liferay.portal.kernel.util.OrderByComparator<T> orderByComparator) {

		return _accountRoleLocalService.dynamicQuery(
			dynamicQuery, start, end, orderByComparator);
	}

	/**
	 * Returns the number of rows matching the dynamic query.
	 *
	 * @param dynamicQuery the dynamic query
	 * @return the number of rows matching the dynamic query
	 */
	@Override
	public long dynamicQueryCount(
		com.liferay.portal.kernel.dao.orm.DynamicQuery dynamicQuery) {

		return _accountRoleLocalService.dynamicQueryCount(dynamicQuery);
	}

	/**
	 * Returns the number of rows matching the dynamic query.
	 *
	 * @param dynamicQuery the dynamic query
	 * @param projection the projection to apply to the query
	 * @return the number of rows matching the dynamic query
	 */
	@Override
	public long dynamicQueryCount(
		com.liferay.portal.kernel.dao.orm.DynamicQuery dynamicQuery,
		com.liferay.portal.kernel.dao.orm.Projection projection) {

		return _accountRoleLocalService.dynamicQueryCount(
			dynamicQuery, projection);
	}

	@Override
	public com.liferay.account.model.AccountRole fetchAccountRole(
		long accountRoleId) {

		return _accountRoleLocalService.fetchAccountRole(accountRoleId);
	}

	@Override
	public com.liferay.account.model.AccountRole
		fetchAccountRoleByExternalReferenceCode(
			String externalReferenceCode, long companyId) {

		return _accountRoleLocalService.fetchAccountRoleByExternalReferenceCode(
			externalReferenceCode, companyId);
	}

	@Override
	public com.liferay.account.model.AccountRole fetchAccountRoleByRoleId(
		long roleId) {

		return _accountRoleLocalService.fetchAccountRoleByRoleId(roleId);
	}

	/**
	 * Returns the account role with the primary key.
	 *
	 * @param accountRoleId the primary key of the account role
	 * @return the account role
	 * @throws PortalException if a account role with the primary key could not be found
	 */
	@Override
	public com.liferay.account.model.AccountRole getAccountRole(
			long accountRoleId)
		throws com.liferay.portal.kernel.exception.PortalException {

		return _accountRoleLocalService.getAccountRole(accountRoleId);
	}

	@Override
	public com.liferay.account.model.AccountRole
			getAccountRoleByExternalReferenceCode(
				String externalReferenceCode, long companyId)
		throws com.liferay.portal.kernel.exception.PortalException {

		return _accountRoleLocalService.getAccountRoleByExternalReferenceCode(
			externalReferenceCode, companyId);
	}

	@Override
	public com.liferay.account.model.AccountRole getAccountRoleByRoleId(
			long roleId)
		throws com.liferay.portal.kernel.exception.PortalException {

		return _accountRoleLocalService.getAccountRoleByRoleId(roleId);
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
	@Override
	public java.util.List<com.liferay.account.model.AccountRole>
		getAccountRoles(int start, int end) {

		return _accountRoleLocalService.getAccountRoles(start, end);
	}

	@Override
	public java.util.List<com.liferay.account.model.AccountRole>
			getAccountRoles(long accountEntryId, long userId)
		throws com.liferay.portal.kernel.exception.PortalException {

		return _accountRoleLocalService.getAccountRoles(accountEntryId, userId);
	}

	@Override
	public java.util.List<com.liferay.account.model.AccountRole>
		getAccountRolesByAccountEntryIds(
			long companyId, long[] accountEntryIds) {

		return _accountRoleLocalService.getAccountRolesByAccountEntryIds(
			companyId, accountEntryIds);
	}

	@Override
	public java.util.List<com.liferay.account.model.AccountRole>
		getAccountRolesByAccountEntryIds(long[] accountEntryIds) {

		return _accountRoleLocalService.getAccountRolesByAccountEntryIds(
			accountEntryIds);
	}

	/**
	 * Returns the number of account roles.
	 *
	 * @return the number of account roles
	 */
	@Override
	public int getAccountRolesCount() {
		return _accountRoleLocalService.getAccountRolesCount();
	}

	@Override
	public com.liferay.portal.kernel.dao.orm.ActionableDynamicQuery
		getActionableDynamicQuery() {

		return _accountRoleLocalService.getActionableDynamicQuery();
	}

	@Override
	public com.liferay.portal.kernel.dao.orm.IndexableActionableDynamicQuery
		getIndexableActionableDynamicQuery() {

		return _accountRoleLocalService.getIndexableActionableDynamicQuery();
	}

	@Override
	public com.liferay.account.model.AccountRole getOrAddEmptyAccountRole(
			String externalReferenceCode, long companyId, long userId,
			long accountEntryId, String name)
		throws com.liferay.portal.kernel.exception.PortalException {

		return _accountRoleLocalService.getOrAddEmptyAccountRole(
			externalReferenceCode, companyId, userId, accountEntryId, name);
	}

	/**
	 * Returns the OSGi service identifier.
	 *
	 * @return the OSGi service identifier
	 */
	@Override
	public String getOSGiServiceIdentifier() {
		return _accountRoleLocalService.getOSGiServiceIdentifier();
	}

	/**
	 * @throws PortalException
	 */
	@Override
	public com.liferay.portal.kernel.model.PersistedModel getPersistedModel(
			java.io.Serializable primaryKeyObj)
		throws com.liferay.portal.kernel.exception.PortalException {

		return _accountRoleLocalService.getPersistedModel(primaryKeyObj);
	}

	@Override
	public boolean hasUserAccountRole(
			long accountEntryId, long accountRoleId, long userId)
		throws com.liferay.portal.kernel.exception.PortalException {

		return _accountRoleLocalService.hasUserAccountRole(
			accountEntryId, accountRoleId, userId);
	}

	@Override
	public com.liferay.portal.kernel.search.BaseModelSearchResult
		<com.liferay.account.model.AccountRole> searchAccountRoles(
			long companyId, long[] accountEntryIds, String keywords,
			java.util.LinkedHashMap<String, Object> params, int start, int end,
			com.liferay.portal.kernel.util.OrderByComparator<?>
				orderByComparator) {

		return _accountRoleLocalService.searchAccountRoles(
			companyId, accountEntryIds, keywords, params, start, end,
			orderByComparator);
	}

	@Override
	public void setUserAccountRoles(
			long accountEntryId, long[] accountRoleIds, long userId)
		throws com.liferay.portal.kernel.exception.PortalException {

		_accountRoleLocalService.setUserAccountRoles(
			accountEntryId, accountRoleIds, userId);
	}

	@Override
	public void unassociateUser(
			long accountEntryId, long accountRoleId, long userId)
		throws com.liferay.portal.kernel.exception.PortalException {

		_accountRoleLocalService.unassociateUser(
			accountEntryId, accountRoleId, userId);
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
	@Override
	public com.liferay.account.model.AccountRole updateAccountRole(
		com.liferay.account.model.AccountRole accountRole) {

		return _accountRoleLocalService.updateAccountRole(accountRole);
	}

	@Override
	public BasePersistence<?> getBasePersistence() {
		return _accountRoleLocalService.getBasePersistence();
	}

	@Override
	public AccountRoleLocalService getWrappedService() {
		return _accountRoleLocalService;
	}

	@Override
	public void setWrappedService(
		AccountRoleLocalService accountRoleLocalService) {

		_accountRoleLocalService = accountRoleLocalService;
	}

	private AccountRoleLocalService _accountRoleLocalService;

}