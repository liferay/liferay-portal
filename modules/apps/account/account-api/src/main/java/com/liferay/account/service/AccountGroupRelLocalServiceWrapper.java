/**
 * SPDX-FileCopyrightText: (c) 2000 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

package com.liferay.account.service;

import com.liferay.portal.kernel.service.ServiceWrapper;
import com.liferay.portal.kernel.service.persistence.BasePersistence;

/**
 * Provides a wrapper for {@link AccountGroupRelLocalService}.
 *
 * @author Brian Wing Shun Chan
 * @see AccountGroupRelLocalService
 * @generated
 */
public class AccountGroupRelLocalServiceWrapper
	implements AccountGroupRelLocalService,
			   ServiceWrapper<AccountGroupRelLocalService> {

	public AccountGroupRelLocalServiceWrapper() {
		this(null);
	}

	public AccountGroupRelLocalServiceWrapper(
		AccountGroupRelLocalService accountGroupRelLocalService) {

		_accountGroupRelLocalService = accountGroupRelLocalService;
	}

	/**
	 * Adds the account group rel to the database. Also notifies the appropriate model listeners.
	 *
	 * <p>
	 * <strong>Important:</strong> Inspect AccountGroupRelLocalServiceImpl for overloaded versions of the method. If provided, use these entry points to the API, as the implementation logic may require the additional parameters defined there.
	 * </p>
	 *
	 * @param accountGroupRel the account group rel
	 * @return the account group rel that was added
	 */
	@Override
	public com.liferay.account.model.AccountGroupRel addAccountGroupRel(
		com.liferay.account.model.AccountGroupRel accountGroupRel) {

		return _accountGroupRelLocalService.addAccountGroupRel(accountGroupRel);
	}

	@Override
	public com.liferay.account.model.AccountGroupRel addAccountGroupRel(
			long accountGroupId, String className, long classPK)
		throws com.liferay.portal.kernel.exception.PortalException {

		return _accountGroupRelLocalService.addAccountGroupRel(
			accountGroupId, className, classPK);
	}

	@Override
	public void addAccountGroupRels(
			long accountGroupId, String className, long[] classPKs)
		throws com.liferay.portal.kernel.exception.PortalException {

		_accountGroupRelLocalService.addAccountGroupRels(
			accountGroupId, className, classPKs);
	}

	/**
	 * Creates a new account group rel with the primary key. Does not add the account group rel to the database.
	 *
	 * @param accountGroupRelId the primary key for the new account group rel
	 * @return the new account group rel
	 */
	@Override
	public com.liferay.account.model.AccountGroupRel createAccountGroupRel(
		long accountGroupRelId) {

		return _accountGroupRelLocalService.createAccountGroupRel(
			accountGroupRelId);
	}

	/**
	 * @throws PortalException
	 */
	@Override
	public com.liferay.portal.kernel.model.PersistedModel createPersistedModel(
			java.io.Serializable primaryKeyObj)
		throws com.liferay.portal.kernel.exception.PortalException {

		return _accountGroupRelLocalService.createPersistedModel(primaryKeyObj);
	}

	/**
	 * Deletes the account group rel from the database. Also notifies the appropriate model listeners.
	 *
	 * <p>
	 * <strong>Important:</strong> Inspect AccountGroupRelLocalServiceImpl for overloaded versions of the method. If provided, use these entry points to the API, as the implementation logic may require the additional parameters defined there.
	 * </p>
	 *
	 * @param accountGroupRel the account group rel
	 * @return the account group rel that was removed
	 */
	@Override
	public com.liferay.account.model.AccountGroupRel deleteAccountGroupRel(
		com.liferay.account.model.AccountGroupRel accountGroupRel) {

		return _accountGroupRelLocalService.deleteAccountGroupRel(
			accountGroupRel);
	}

	/**
	 * Deletes the account group rel with the primary key from the database. Also notifies the appropriate model listeners.
	 *
	 * <p>
	 * <strong>Important:</strong> Inspect AccountGroupRelLocalServiceImpl for overloaded versions of the method. If provided, use these entry points to the API, as the implementation logic may require the additional parameters defined there.
	 * </p>
	 *
	 * @param accountGroupRelId the primary key of the account group rel
	 * @return the account group rel that was removed
	 * @throws PortalException if a account group rel with the primary key could not be found
	 */
	@Override
	public com.liferay.account.model.AccountGroupRel deleteAccountGroupRel(
			long accountGroupRelId)
		throws com.liferay.portal.kernel.exception.PortalException {

		return _accountGroupRelLocalService.deleteAccountGroupRel(
			accountGroupRelId);
	}

	@Override
	public void deleteAccountGroupRels(
			long accountGroupId, String className, long[] classPKs)
		throws com.liferay.portal.kernel.exception.PortalException {

		_accountGroupRelLocalService.deleteAccountGroupRels(
			accountGroupId, className, classPKs);
	}

	@Override
	public void deleteAccountGroupRels(String className, long[] classPKs) {
		_accountGroupRelLocalService.deleteAccountGroupRels(
			className, classPKs);
	}

	/**
	 * @throws PortalException
	 */
	@Override
	public com.liferay.portal.kernel.model.PersistedModel deletePersistedModel(
			com.liferay.portal.kernel.model.PersistedModel persistedModel)
		throws com.liferay.portal.kernel.exception.PortalException {

		return _accountGroupRelLocalService.deletePersistedModel(
			persistedModel);
	}

	@Override
	public <T> T dslQuery(com.liferay.petra.sql.dsl.query.DSLQuery dslQuery) {
		return _accountGroupRelLocalService.dslQuery(dslQuery);
	}

	@Override
	public int dslQueryCount(
		com.liferay.petra.sql.dsl.query.DSLQuery dslQuery) {

		return _accountGroupRelLocalService.dslQueryCount(dslQuery);
	}

	@Override
	public com.liferay.portal.kernel.dao.orm.DynamicQuery dynamicQuery() {
		return _accountGroupRelLocalService.dynamicQuery();
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

		return _accountGroupRelLocalService.dynamicQuery(dynamicQuery);
	}

	/**
	 * Performs a dynamic query on the database and returns a range of the matching rows.
	 *
	 * <p>
	 * Useful when paginating results. Returns a maximum of <code>end - start</code> instances. <code>start</code> and <code>end</code> are not primary keys, they are indexes in the result set. Thus, <code>0</code> refers to the first result in the set. Setting both <code>start</code> and <code>end</code> to <code>com.liferay.portal.kernel.dao.orm.QueryUtil#ALL_POS</code> will return the full result set. If <code>orderByComparator</code> is specified, then the query will include the given ORDER BY logic. If <code>orderByComparator</code> is absent, then the query will include the default ORDER BY logic from <code>com.liferay.account.model.impl.AccountGroupRelModelImpl</code>.
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

		return _accountGroupRelLocalService.dynamicQuery(
			dynamicQuery, start, end);
	}

	/**
	 * Performs a dynamic query on the database and returns an ordered range of the matching rows.
	 *
	 * <p>
	 * Useful when paginating results. Returns a maximum of <code>end - start</code> instances. <code>start</code> and <code>end</code> are not primary keys, they are indexes in the result set. Thus, <code>0</code> refers to the first result in the set. Setting both <code>start</code> and <code>end</code> to <code>com.liferay.portal.kernel.dao.orm.QueryUtil#ALL_POS</code> will return the full result set. If <code>orderByComparator</code> is specified, then the query will include the given ORDER BY logic. If <code>orderByComparator</code> is absent, then the query will include the default ORDER BY logic from <code>com.liferay.account.model.impl.AccountGroupRelModelImpl</code>.
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

		return _accountGroupRelLocalService.dynamicQuery(
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

		return _accountGroupRelLocalService.dynamicQueryCount(dynamicQuery);
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

		return _accountGroupRelLocalService.dynamicQueryCount(
			dynamicQuery, projection);
	}

	@Override
	public com.liferay.account.model.AccountGroupRel fetchAccountGroupRel(
		long accountGroupRelId) {

		return _accountGroupRelLocalService.fetchAccountGroupRel(
			accountGroupRelId);
	}

	@Override
	public com.liferay.account.model.AccountGroupRel fetchAccountGroupRel(
		long accountGroupId, String className, long classPK) {

		return _accountGroupRelLocalService.fetchAccountGroupRel(
			accountGroupId, className, classPK);
	}

	/**
	 * Returns the account group rel with the primary key.
	 *
	 * @param accountGroupRelId the primary key of the account group rel
	 * @return the account group rel
	 * @throws PortalException if a account group rel with the primary key could not be found
	 */
	@Override
	public com.liferay.account.model.AccountGroupRel getAccountGroupRel(
			long accountGroupRelId)
		throws com.liferay.portal.kernel.exception.PortalException {

		return _accountGroupRelLocalService.getAccountGroupRel(
			accountGroupRelId);
	}

	/**
	 * Returns a range of all the account group rels.
	 *
	 * <p>
	 * Useful when paginating results. Returns a maximum of <code>end - start</code> instances. <code>start</code> and <code>end</code> are not primary keys, they are indexes in the result set. Thus, <code>0</code> refers to the first result in the set. Setting both <code>start</code> and <code>end</code> to <code>com.liferay.portal.kernel.dao.orm.QueryUtil#ALL_POS</code> will return the full result set. If <code>orderByComparator</code> is specified, then the query will include the given ORDER BY logic. If <code>orderByComparator</code> is absent, then the query will include the default ORDER BY logic from <code>com.liferay.account.model.impl.AccountGroupRelModelImpl</code>.
	 * </p>
	 *
	 * @param start the lower bound of the range of account group rels
	 * @param end the upper bound of the range of account group rels (not inclusive)
	 * @return the range of account group rels
	 */
	@Override
	public java.util.List<com.liferay.account.model.AccountGroupRel>
		getAccountGroupRels(int start, int end) {

		return _accountGroupRelLocalService.getAccountGroupRels(start, end);
	}

	@Override
	public java.util.List<com.liferay.account.model.AccountGroupRel>
		getAccountGroupRels(long accountGroupId, String className) {

		return _accountGroupRelLocalService.getAccountGroupRels(
			accountGroupId, className);
	}

	@Override
	public java.util.List<com.liferay.account.model.AccountGroupRel>
		getAccountGroupRels(
			long[] accountGroupIds, String className, long classPK,
			String keywords, int start, int end) {

		return _accountGroupRelLocalService.getAccountGroupRels(
			accountGroupIds, className, classPK, keywords, start, end);
	}

	@Override
	public java.util.List<com.liferay.account.model.AccountGroupRel>
		getAccountGroupRels(String className, long classPK) {

		return _accountGroupRelLocalService.getAccountGroupRels(
			className, classPK);
	}

	@Override
	public java.util.List<com.liferay.account.model.AccountGroupRel>
		getAccountGroupRels(
			String className, long classPK, int start, int end,
			com.liferay.portal.kernel.util.OrderByComparator
				<com.liferay.account.model.AccountGroupRel> orderByComparator) {

		return _accountGroupRelLocalService.getAccountGroupRels(
			className, classPK, start, end, orderByComparator);
	}

	@Override
	public java.util.List<com.liferay.account.model.AccountGroupRel>
		getAccountGroupRelsByAccountGroupId(long accountGroupId) {

		return _accountGroupRelLocalService.getAccountGroupRelsByAccountGroupId(
			accountGroupId);
	}

	/**
	 * Returns the number of account group rels.
	 *
	 * @return the number of account group rels
	 */
	@Override
	public int getAccountGroupRelsCount() {
		return _accountGroupRelLocalService.getAccountGroupRelsCount();
	}

	@Override
	public int getAccountGroupRelsCount(
		long[] accountGroupIds, String className, long classPK,
		String keywords) {

		return _accountGroupRelLocalService.getAccountGroupRelsCount(
			accountGroupIds, className, classPK, keywords);
	}

	@Override
	public int getAccountGroupRelsCount(String className, long classPK) {
		return _accountGroupRelLocalService.getAccountGroupRelsCount(
			className, classPK);
	}

	@Override
	public int getAccountGroupRelsCountByClassName(
		long accountGroupId, String className) {

		return _accountGroupRelLocalService.getAccountGroupRelsCountByClassName(
			accountGroupId, className);
	}

	@Override
	public com.liferay.portal.kernel.dao.orm.ActionableDynamicQuery
		getActionableDynamicQuery() {

		return _accountGroupRelLocalService.getActionableDynamicQuery();
	}

	@Override
	public com.liferay.portal.kernel.dao.orm.IndexableActionableDynamicQuery
		getIndexableActionableDynamicQuery() {

		return _accountGroupRelLocalService.
			getIndexableActionableDynamicQuery();
	}

	/**
	 * Returns the OSGi service identifier.
	 *
	 * @return the OSGi service identifier
	 */
	@Override
	public String getOSGiServiceIdentifier() {
		return _accountGroupRelLocalService.getOSGiServiceIdentifier();
	}

	/**
	 * @throws PortalException
	 */
	@Override
	public com.liferay.portal.kernel.model.PersistedModel getPersistedModel(
			java.io.Serializable primaryKeyObj)
		throws com.liferay.portal.kernel.exception.PortalException {

		return _accountGroupRelLocalService.getPersistedModel(primaryKeyObj);
	}

	/**
	 * Updates the account group rel in the database or adds it if it does not yet exist. Also notifies the appropriate model listeners.
	 *
	 * <p>
	 * <strong>Important:</strong> Inspect AccountGroupRelLocalServiceImpl for overloaded versions of the method. If provided, use these entry points to the API, as the implementation logic may require the additional parameters defined there.
	 * </p>
	 *
	 * @param accountGroupRel the account group rel
	 * @return the account group rel that was updated
	 */
	@Override
	public com.liferay.account.model.AccountGroupRel updateAccountGroupRel(
		com.liferay.account.model.AccountGroupRel accountGroupRel) {

		return _accountGroupRelLocalService.updateAccountGroupRel(
			accountGroupRel);
	}

	@Override
	public BasePersistence<?> getBasePersistence() {
		return _accountGroupRelLocalService.getBasePersistence();
	}

	@Override
	public AccountGroupRelLocalService getWrappedService() {
		return _accountGroupRelLocalService;
	}

	@Override
	public void setWrappedService(
		AccountGroupRelLocalService accountGroupRelLocalService) {

		_accountGroupRelLocalService = accountGroupRelLocalService;
	}

	private AccountGroupRelLocalService _accountGroupRelLocalService;

}
// LIFERAY-SERVICE-BUILDER-HASH:-1320638307