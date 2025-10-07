/**
 * SPDX-FileCopyrightText: (c) 2000 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

package com.liferay.account.service;

import com.liferay.portal.kernel.service.ServiceWrapper;
import com.liferay.portal.kernel.service.persistence.BasePersistence;

/**
 * Provides a wrapper for {@link AccountGroupLocalService}.
 *
 * @author Brian Wing Shun Chan
 * @see AccountGroupLocalService
 * @generated
 */
public class AccountGroupLocalServiceWrapper
	implements AccountGroupLocalService,
			   ServiceWrapper<AccountGroupLocalService> {

	public AccountGroupLocalServiceWrapper() {
		this(null);
	}

	public AccountGroupLocalServiceWrapper(
		AccountGroupLocalService accountGroupLocalService) {

		_accountGroupLocalService = accountGroupLocalService;
	}

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
	@Override
	public com.liferay.account.model.AccountGroup addAccountGroup(
		com.liferay.account.model.AccountGroup accountGroup) {

		return _accountGroupLocalService.addAccountGroup(accountGroup);
	}

	@Override
	public com.liferay.account.model.AccountGroup addAccountGroup(
			String externalReferenceCode, long userId, String description,
			String name,
			com.liferay.portal.kernel.service.ServiceContext serviceContext)
		throws com.liferay.portal.kernel.exception.PortalException {

		return _accountGroupLocalService.addAccountGroup(
			externalReferenceCode, userId, description, name, serviceContext);
	}

	@Override
	public com.liferay.account.model.AccountGroup checkGuestAccountGroup(
			long companyId)
		throws com.liferay.portal.kernel.exception.PortalException {

		return _accountGroupLocalService.checkGuestAccountGroup(companyId);
	}

	/**
	 * Creates a new account group with the primary key. Does not add the account group to the database.
	 *
	 * @param accountGroupId the primary key for the new account group
	 * @return the new account group
	 */
	@Override
	public com.liferay.account.model.AccountGroup createAccountGroup(
		long accountGroupId) {

		return _accountGroupLocalService.createAccountGroup(accountGroupId);
	}

	/**
	 * @throws PortalException
	 */
	@Override
	public com.liferay.portal.kernel.model.PersistedModel createPersistedModel(
			java.io.Serializable primaryKeyObj)
		throws com.liferay.portal.kernel.exception.PortalException {

		return _accountGroupLocalService.createPersistedModel(primaryKeyObj);
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
	@Override
	public com.liferay.account.model.AccountGroup deleteAccountGroup(
			com.liferay.account.model.AccountGroup accountGroup)
		throws com.liferay.portal.kernel.exception.PortalException {

		return _accountGroupLocalService.deleteAccountGroup(accountGroup);
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
	@Override
	public com.liferay.account.model.AccountGroup deleteAccountGroup(
			long accountGroupId)
		throws com.liferay.portal.kernel.exception.PortalException {

		return _accountGroupLocalService.deleteAccountGroup(accountGroupId);
	}

	/**
	 * @throws PortalException
	 */
	@Override
	public com.liferay.portal.kernel.model.PersistedModel deletePersistedModel(
			com.liferay.portal.kernel.model.PersistedModel persistedModel)
		throws com.liferay.portal.kernel.exception.PortalException {

		return _accountGroupLocalService.deletePersistedModel(persistedModel);
	}

	@Override
	public <T> T dslQuery(com.liferay.petra.sql.dsl.query.DSLQuery dslQuery) {
		return _accountGroupLocalService.dslQuery(dslQuery);
	}

	@Override
	public int dslQueryCount(
		com.liferay.petra.sql.dsl.query.DSLQuery dslQuery) {

		return _accountGroupLocalService.dslQueryCount(dslQuery);
	}

	@Override
	public com.liferay.portal.kernel.dao.orm.DynamicQuery dynamicQuery() {
		return _accountGroupLocalService.dynamicQuery();
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

		return _accountGroupLocalService.dynamicQuery(dynamicQuery);
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
	@Override
	public <T> java.util.List<T> dynamicQuery(
		com.liferay.portal.kernel.dao.orm.DynamicQuery dynamicQuery, int start,
		int end) {

		return _accountGroupLocalService.dynamicQuery(dynamicQuery, start, end);
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
	@Override
	public <T> java.util.List<T> dynamicQuery(
		com.liferay.portal.kernel.dao.orm.DynamicQuery dynamicQuery, int start,
		int end,
		com.liferay.portal.kernel.util.OrderByComparator<T> orderByComparator) {

		return _accountGroupLocalService.dynamicQuery(
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

		return _accountGroupLocalService.dynamicQueryCount(dynamicQuery);
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

		return _accountGroupLocalService.dynamicQueryCount(
			dynamicQuery, projection);
	}

	@Override
	public com.liferay.account.model.AccountGroup fetchAccountGroup(
		long accountGroupId) {

		return _accountGroupLocalService.fetchAccountGroup(accountGroupId);
	}

	@Override
	public com.liferay.account.model.AccountGroup
		fetchAccountGroupByExternalReferenceCode(
			String externalReferenceCode, long companyId) {

		return _accountGroupLocalService.
			fetchAccountGroupByExternalReferenceCode(
				externalReferenceCode, companyId);
	}

	/**
	 * Returns the account group with the matching UUID and company.
	 *
	 * @param uuid the account group's UUID
	 * @param companyId the primary key of the company
	 * @return the matching account group, or <code>null</code> if a matching account group could not be found
	 */
	@Override
	public com.liferay.account.model.AccountGroup
		fetchAccountGroupByUuidAndCompanyId(String uuid, long companyId) {

		return _accountGroupLocalService.fetchAccountGroupByUuidAndCompanyId(
			uuid, companyId);
	}

	/**
	 * Returns the account group with the primary key.
	 *
	 * @param accountGroupId the primary key of the account group
	 * @return the account group
	 * @throws PortalException if a account group with the primary key could not be found
	 */
	@Override
	public com.liferay.account.model.AccountGroup getAccountGroup(
			long accountGroupId)
		throws com.liferay.portal.kernel.exception.PortalException {

		return _accountGroupLocalService.getAccountGroup(accountGroupId);
	}

	@Override
	public com.liferay.account.model.AccountGroup
			getAccountGroupByExternalReferenceCode(
				String externalReferenceCode, long companyId)
		throws com.liferay.portal.kernel.exception.PortalException {

		return _accountGroupLocalService.getAccountGroupByExternalReferenceCode(
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
	@Override
	public com.liferay.account.model.AccountGroup
			getAccountGroupByUuidAndCompanyId(String uuid, long companyId)
		throws com.liferay.portal.kernel.exception.PortalException {

		return _accountGroupLocalService.getAccountGroupByUuidAndCompanyId(
			uuid, companyId);
	}

	@Override
	public long[] getAccountGroupIds(long accountEntryId) {
		return _accountGroupLocalService.getAccountGroupIds(accountEntryId);
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
	@Override
	public java.util.List<com.liferay.account.model.AccountGroup>
		getAccountGroups(int start, int end) {

		return _accountGroupLocalService.getAccountGroups(start, end);
	}

	@Override
	public java.util.List<com.liferay.account.model.AccountGroup>
		getAccountGroups(
			long companyId, int start, int end,
			com.liferay.portal.kernel.util.OrderByComparator
				<com.liferay.account.model.AccountGroup> orderByComparator) {

		return _accountGroupLocalService.getAccountGroups(
			companyId, start, end, orderByComparator);
	}

	@Override
	public java.util.List<com.liferay.account.model.AccountGroup>
		getAccountGroups(
			long companyId, String name, int start, int end,
			com.liferay.portal.kernel.util.OrderByComparator
				<com.liferay.account.model.AccountGroup> orderByComparator) {

		return _accountGroupLocalService.getAccountGroups(
			companyId, name, start, end, orderByComparator);
	}

	@Override
	public java.util.List<com.liferay.account.model.AccountGroup>
		getAccountGroupsByAccountEntryId(
			long accountEntryId, int start, int end) {

		return _accountGroupLocalService.getAccountGroupsByAccountEntryId(
			accountEntryId, start, end);
	}

	@Override
	public java.util.List<com.liferay.account.model.AccountGroup>
		getAccountGroupsByAccountGroupId(long[] accountGroupIds) {

		return _accountGroupLocalService.getAccountGroupsByAccountGroupId(
			accountGroupIds);
	}

	/**
	 * Returns the number of account groups.
	 *
	 * @return the number of account groups
	 */
	@Override
	public int getAccountGroupsCount() {
		return _accountGroupLocalService.getAccountGroupsCount();
	}

	@Override
	public int getAccountGroupsCount(long companyId) {
		return _accountGroupLocalService.getAccountGroupsCount(companyId);
	}

	@Override
	public long getAccountGroupsCount(long companyId, String name) {
		return _accountGroupLocalService.getAccountGroupsCount(companyId, name);
	}

	@Override
	public int getAccountGroupsCountByAccountEntryId(long accountEntryId) {
		return _accountGroupLocalService.getAccountGroupsCountByAccountEntryId(
			accountEntryId);
	}

	@Override
	public com.liferay.portal.kernel.dao.orm.ActionableDynamicQuery
		getActionableDynamicQuery() {

		return _accountGroupLocalService.getActionableDynamicQuery();
	}

	@Override
	public com.liferay.account.model.AccountGroup getDefaultAccountGroup(
		long companyId) {

		return _accountGroupLocalService.getDefaultAccountGroup(companyId);
	}

	@Override
	public com.liferay.portal.kernel.dao.orm.ExportActionableDynamicQuery
		getExportActionableDynamicQuery(
			com.liferay.exportimport.kernel.lar.PortletDataContext
				portletDataContext) {

		return _accountGroupLocalService.getExportActionableDynamicQuery(
			portletDataContext);
	}

	@Override
	public com.liferay.portal.kernel.dao.orm.IndexableActionableDynamicQuery
		getIndexableActionableDynamicQuery() {

		return _accountGroupLocalService.getIndexableActionableDynamicQuery();
	}

	@Override
	public com.liferay.account.model.AccountGroup getOrAddEmptyAccountGroup(
			String externalReferenceCode, long companyId, long userId,
			String name)
		throws com.liferay.portal.kernel.exception.PortalException {

		return _accountGroupLocalService.getOrAddEmptyAccountGroup(
			externalReferenceCode, companyId, userId, name);
	}

	/**
	 * Returns the OSGi service identifier.
	 *
	 * @return the OSGi service identifier
	 */
	@Override
	public String getOSGiServiceIdentifier() {
		return _accountGroupLocalService.getOSGiServiceIdentifier();
	}

	/**
	 * @throws PortalException
	 */
	@Override
	public com.liferay.portal.kernel.model.PersistedModel getPersistedModel(
			java.io.Serializable primaryKeyObj)
		throws com.liferay.portal.kernel.exception.PortalException {

		return _accountGroupLocalService.getPersistedModel(primaryKeyObj);
	}

	@Override
	public boolean hasDefaultAccountGroup(long companyId) {
		return _accountGroupLocalService.hasDefaultAccountGroup(companyId);
	}

	@Override
	public com.liferay.portal.kernel.search.BaseModelSearchResult
		<com.liferay.account.model.AccountGroup> searchAccountGroups(
			long companyId, String keywords, int start, int end,
			com.liferay.portal.kernel.util.OrderByComparator
				<com.liferay.account.model.AccountGroup> orderByComparator) {

		return _accountGroupLocalService.searchAccountGroups(
			companyId, keywords, start, end, orderByComparator);
	}

	@Override
	public com.liferay.portal.kernel.search.BaseModelSearchResult
		<com.liferay.account.model.AccountGroup> searchAccountGroups(
			long companyId, String keywords,
			java.util.LinkedHashMap<String, Object> params, int start, int end,
			com.liferay.portal.kernel.util.OrderByComparator
				<com.liferay.account.model.AccountGroup> orderByComparator) {

		return _accountGroupLocalService.searchAccountGroups(
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
	@Override
	public com.liferay.account.model.AccountGroup updateAccountGroup(
		com.liferay.account.model.AccountGroup accountGroup) {

		return _accountGroupLocalService.updateAccountGroup(accountGroup);
	}

	@Override
	public com.liferay.account.model.AccountGroup updateAccountGroup(
			String externalReferenceCode, long accountGroupId,
			String description, String name,
			com.liferay.portal.kernel.service.ServiceContext serviceContext)
		throws com.liferay.portal.kernel.exception.PortalException {

		return _accountGroupLocalService.updateAccountGroup(
			externalReferenceCode, accountGroupId, description, name,
			serviceContext);
	}

	@Override
	public com.liferay.account.model.AccountGroup updateExternalReferenceCode(
			com.liferay.account.model.AccountGroup accountGroup,
			String externalReferenceCode)
		throws com.liferay.portal.kernel.exception.PortalException {

		return _accountGroupLocalService.updateExternalReferenceCode(
			accountGroup, externalReferenceCode);
	}

	@Override
	public com.liferay.account.model.AccountGroup updateExternalReferenceCode(
			long accountGroupId, String externalReferenceCode)
		throws com.liferay.portal.kernel.exception.PortalException {

		return _accountGroupLocalService.updateExternalReferenceCode(
			accountGroupId, externalReferenceCode);
	}

	@Override
	public BasePersistence<?> getBasePersistence() {
		return _accountGroupLocalService.getBasePersistence();
	}

	@Override
	public AccountGroupLocalService getWrappedService() {
		return _accountGroupLocalService;
	}

	@Override
	public void setWrappedService(
		AccountGroupLocalService accountGroupLocalService) {

		_accountGroupLocalService = accountGroupLocalService;
	}

	private AccountGroupLocalService _accountGroupLocalService;

}