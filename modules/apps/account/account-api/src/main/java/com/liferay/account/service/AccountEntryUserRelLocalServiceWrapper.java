/**
 * SPDX-FileCopyrightText: (c) 2000 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

package com.liferay.account.service;

import com.liferay.portal.kernel.service.ServiceWrapper;
import com.liferay.portal.kernel.service.persistence.BasePersistence;

/**
 * Provides a wrapper for {@link AccountEntryUserRelLocalService}.
 *
 * @author Brian Wing Shun Chan
 * @see AccountEntryUserRelLocalService
 * @generated
 */
public class AccountEntryUserRelLocalServiceWrapper
	implements AccountEntryUserRelLocalService,
			   ServiceWrapper<AccountEntryUserRelLocalService> {

	public AccountEntryUserRelLocalServiceWrapper() {
		this(null);
	}

	public AccountEntryUserRelLocalServiceWrapper(
		AccountEntryUserRelLocalService accountEntryUserRelLocalService) {

		_accountEntryUserRelLocalService = accountEntryUserRelLocalService;
	}

	/**
	 * Adds the account entry user rel to the database. Also notifies the appropriate model listeners.
	 *
	 * <p>
	 * <strong>Important:</strong> Inspect AccountEntryUserRelLocalServiceImpl for overloaded versions of the method. If provided, use these entry points to the API, as the implementation logic may require the additional parameters defined there.
	 * </p>
	 *
	 * @param accountEntryUserRel the account entry user rel
	 * @return the account entry user rel that was added
	 */
	@Override
	public com.liferay.account.model.AccountEntryUserRel addAccountEntryUserRel(
		com.liferay.account.model.AccountEntryUserRel accountEntryUserRel) {

		return _accountEntryUserRelLocalService.addAccountEntryUserRel(
			accountEntryUserRel);
	}

	@Override
	public com.liferay.account.model.AccountEntryUserRel addAccountEntryUserRel(
			long accountEntryId, long accountUserId)
		throws com.liferay.portal.kernel.exception.PortalException {

		return _accountEntryUserRelLocalService.addAccountEntryUserRel(
			accountEntryId, accountUserId);
	}

	@Override
	public com.liferay.account.model.AccountEntryUserRel addAccountEntryUserRel(
			long accountEntryId, long creatorUserId, String screenName,
			String emailAddress, java.util.Locale locale, String firstName,
			String middleName, String lastName, long prefixListTypeId,
			long suffixListTypeId, String jobTitle,
			com.liferay.portal.kernel.service.ServiceContext serviceContext)
		throws com.liferay.portal.kernel.exception.PortalException {

		return _accountEntryUserRelLocalService.addAccountEntryUserRel(
			accountEntryId, creatorUserId, screenName, emailAddress, locale,
			firstName, middleName, lastName, prefixListTypeId, suffixListTypeId,
			jobTitle, serviceContext);
	}

	@Override
	public com.liferay.account.model.AccountEntryUserRel
			addAccountEntryUserRelByEmailAddress(
				long accountEntryId, String emailAddress, long[] accountRoleIds,
				String userExternalReferenceCode,
				com.liferay.portal.kernel.service.ServiceContext serviceContext)
		throws com.liferay.portal.kernel.exception.PortalException {

		return _accountEntryUserRelLocalService.
			addAccountEntryUserRelByEmailAddress(
				accountEntryId, emailAddress, accountRoleIds,
				userExternalReferenceCode, serviceContext);
	}

	@Override
	public void addAccountEntryUserRels(
			long accountEntryId, long[] accountUserIds)
		throws com.liferay.portal.kernel.exception.PortalException {

		_accountEntryUserRelLocalService.addAccountEntryUserRels(
			accountEntryId, accountUserIds);
	}

	@Override
	public com.liferay.account.model.AccountEntryUserRel
			addPersonTypeAccountEntryUserRel(
				long accountEntryId, long creatorUserId, String screenName,
				String emailAddress, java.util.Locale locale, String firstName,
				String middleName, String lastName, long prefixListTypeId,
				long suffixListTypeId, String jobTitle,
				com.liferay.portal.kernel.service.ServiceContext serviceContext)
		throws com.liferay.portal.kernel.exception.PortalException {

		return _accountEntryUserRelLocalService.
			addPersonTypeAccountEntryUserRel(
				accountEntryId, creatorUserId, screenName, emailAddress, locale,
				firstName, middleName, lastName, prefixListTypeId,
				suffixListTypeId, jobTitle, serviceContext);
	}

	@Override
	public com.liferay.portal.kernel.model.Ticket addUserInvitationTicket(
			long accountEntryId, long[] accountRoleIds, String emailAddress,
			com.liferay.portal.kernel.model.User inviter,
			com.liferay.portal.kernel.service.ServiceContext serviceContext)
		throws com.liferay.portal.kernel.exception.PortalException {

		return _accountEntryUserRelLocalService.addUserInvitationTicket(
			accountEntryId, accountRoleIds, emailAddress, inviter,
			serviceContext);
	}

	/**
	 * Creates a new account entry user rel with the primary key. Does not add the account entry user rel to the database.
	 *
	 * @param accountEntryUserRelId the primary key for the new account entry user rel
	 * @return the new account entry user rel
	 */
	@Override
	public com.liferay.account.model.AccountEntryUserRel
		createAccountEntryUserRel(long accountEntryUserRelId) {

		return _accountEntryUserRelLocalService.createAccountEntryUserRel(
			accountEntryUserRelId);
	}

	/**
	 * @throws PortalException
	 */
	@Override
	public com.liferay.portal.kernel.model.PersistedModel createPersistedModel(
			java.io.Serializable primaryKeyObj)
		throws com.liferay.portal.kernel.exception.PortalException {

		return _accountEntryUserRelLocalService.createPersistedModel(
			primaryKeyObj);
	}

	/**
	 * Deletes the account entry user rel from the database. Also notifies the appropriate model listeners.
	 *
	 * <p>
	 * <strong>Important:</strong> Inspect AccountEntryUserRelLocalServiceImpl for overloaded versions of the method. If provided, use these entry points to the API, as the implementation logic may require the additional parameters defined there.
	 * </p>
	 *
	 * @param accountEntryUserRel the account entry user rel
	 * @return the account entry user rel that was removed
	 */
	@Override
	public com.liferay.account.model.AccountEntryUserRel
		deleteAccountEntryUserRel(
			com.liferay.account.model.AccountEntryUserRel accountEntryUserRel) {

		return _accountEntryUserRelLocalService.deleteAccountEntryUserRel(
			accountEntryUserRel);
	}

	/**
	 * Deletes the account entry user rel with the primary key from the database. Also notifies the appropriate model listeners.
	 *
	 * <p>
	 * <strong>Important:</strong> Inspect AccountEntryUserRelLocalServiceImpl for overloaded versions of the method. If provided, use these entry points to the API, as the implementation logic may require the additional parameters defined there.
	 * </p>
	 *
	 * @param accountEntryUserRelId the primary key of the account entry user rel
	 * @return the account entry user rel that was removed
	 * @throws PortalException if a account entry user rel with the primary key could not be found
	 */
	@Override
	public com.liferay.account.model.AccountEntryUserRel
			deleteAccountEntryUserRel(long accountEntryUserRelId)
		throws com.liferay.portal.kernel.exception.PortalException {

		return _accountEntryUserRelLocalService.deleteAccountEntryUserRel(
			accountEntryUserRelId);
	}

	@Override
	public void deleteAccountEntryUserRelByEmailAddress(
			long accountEntryId, String emailAddress)
		throws com.liferay.portal.kernel.exception.PortalException {

		_accountEntryUserRelLocalService.
			deleteAccountEntryUserRelByEmailAddress(
				accountEntryId, emailAddress);
	}

	@Override
	public void deleteAccountEntryUserRels(
			long accountEntryId, long[] accountUserIds)
		throws com.liferay.portal.kernel.exception.PortalException {

		_accountEntryUserRelLocalService.deleteAccountEntryUserRels(
			accountEntryId, accountUserIds);
	}

	@Override
	public void deleteAccountEntryUserRelsByAccountEntryId(
		long accountEntryId) {

		_accountEntryUserRelLocalService.
			deleteAccountEntryUserRelsByAccountEntryId(accountEntryId);
	}

	/**
	 * @throws PortalException
	 */
	@Override
	public com.liferay.portal.kernel.model.PersistedModel deletePersistedModel(
			com.liferay.portal.kernel.model.PersistedModel persistedModel)
		throws com.liferay.portal.kernel.exception.PortalException {

		return _accountEntryUserRelLocalService.deletePersistedModel(
			persistedModel);
	}

	@Override
	public <T> T dslQuery(com.liferay.petra.sql.dsl.query.DSLQuery dslQuery) {
		return _accountEntryUserRelLocalService.dslQuery(dslQuery);
	}

	@Override
	public int dslQueryCount(
		com.liferay.petra.sql.dsl.query.DSLQuery dslQuery) {

		return _accountEntryUserRelLocalService.dslQueryCount(dslQuery);
	}

	@Override
	public com.liferay.portal.kernel.dao.orm.DynamicQuery dynamicQuery() {
		return _accountEntryUserRelLocalService.dynamicQuery();
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

		return _accountEntryUserRelLocalService.dynamicQuery(dynamicQuery);
	}

	/**
	 * Performs a dynamic query on the database and returns a range of the matching rows.
	 *
	 * <p>
	 * Useful when paginating results. Returns a maximum of <code>end - start</code> instances. <code>start</code> and <code>end</code> are not primary keys, they are indexes in the result set. Thus, <code>0</code> refers to the first result in the set. Setting both <code>start</code> and <code>end</code> to <code>com.liferay.portal.kernel.dao.orm.QueryUtil#ALL_POS</code> will return the full result set. If <code>orderByComparator</code> is specified, then the query will include the given ORDER BY logic. If <code>orderByComparator</code> is absent, then the query will include the default ORDER BY logic from <code>com.liferay.account.model.impl.AccountEntryUserRelModelImpl</code>.
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

		return _accountEntryUserRelLocalService.dynamicQuery(
			dynamicQuery, start, end);
	}

	/**
	 * Performs a dynamic query on the database and returns an ordered range of the matching rows.
	 *
	 * <p>
	 * Useful when paginating results. Returns a maximum of <code>end - start</code> instances. <code>start</code> and <code>end</code> are not primary keys, they are indexes in the result set. Thus, <code>0</code> refers to the first result in the set. Setting both <code>start</code> and <code>end</code> to <code>com.liferay.portal.kernel.dao.orm.QueryUtil#ALL_POS</code> will return the full result set. If <code>orderByComparator</code> is specified, then the query will include the given ORDER BY logic. If <code>orderByComparator</code> is absent, then the query will include the default ORDER BY logic from <code>com.liferay.account.model.impl.AccountEntryUserRelModelImpl</code>.
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

		return _accountEntryUserRelLocalService.dynamicQuery(
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

		return _accountEntryUserRelLocalService.dynamicQueryCount(dynamicQuery);
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

		return _accountEntryUserRelLocalService.dynamicQueryCount(
			dynamicQuery, projection);
	}

	@Override
	public com.liferay.account.model.AccountEntryUserRel
		fetchAccountEntryUserRel(long accountEntryUserRelId) {

		return _accountEntryUserRelLocalService.fetchAccountEntryUserRel(
			accountEntryUserRelId);
	}

	@Override
	public com.liferay.account.model.AccountEntryUserRel
		fetchAccountEntryUserRel(long accountEntryId, long accountUserId) {

		return _accountEntryUserRelLocalService.fetchAccountEntryUserRel(
			accountEntryId, accountUserId);
	}

	/**
	 * Returns the account entry user rel with the primary key.
	 *
	 * @param accountEntryUserRelId the primary key of the account entry user rel
	 * @return the account entry user rel
	 * @throws PortalException if a account entry user rel with the primary key could not be found
	 */
	@Override
	public com.liferay.account.model.AccountEntryUserRel getAccountEntryUserRel(
			long accountEntryUserRelId)
		throws com.liferay.portal.kernel.exception.PortalException {

		return _accountEntryUserRelLocalService.getAccountEntryUserRel(
			accountEntryUserRelId);
	}

	@Override
	public com.liferay.account.model.AccountEntryUserRel getAccountEntryUserRel(
			long accountEntryId, long accountUserId)
		throws com.liferay.portal.kernel.exception.PortalException {

		return _accountEntryUserRelLocalService.getAccountEntryUserRel(
			accountEntryId, accountUserId);
	}

	/**
	 * Returns a range of all the account entry user rels.
	 *
	 * <p>
	 * Useful when paginating results. Returns a maximum of <code>end - start</code> instances. <code>start</code> and <code>end</code> are not primary keys, they are indexes in the result set. Thus, <code>0</code> refers to the first result in the set. Setting both <code>start</code> and <code>end</code> to <code>com.liferay.portal.kernel.dao.orm.QueryUtil#ALL_POS</code> will return the full result set. If <code>orderByComparator</code> is specified, then the query will include the given ORDER BY logic. If <code>orderByComparator</code> is absent, then the query will include the default ORDER BY logic from <code>com.liferay.account.model.impl.AccountEntryUserRelModelImpl</code>.
	 * </p>
	 *
	 * @param start the lower bound of the range of account entry user rels
	 * @param end the upper bound of the range of account entry user rels (not inclusive)
	 * @return the range of account entry user rels
	 */
	@Override
	public java.util.List<com.liferay.account.model.AccountEntryUserRel>
		getAccountEntryUserRels(int start, int end) {

		return _accountEntryUserRelLocalService.getAccountEntryUserRels(
			start, end);
	}

	@Override
	public java.util.List<com.liferay.account.model.AccountEntryUserRel>
		getAccountEntryUserRelsByAccountEntryId(long accountEntryId) {

		return _accountEntryUserRelLocalService.
			getAccountEntryUserRelsByAccountEntryId(accountEntryId);
	}

	@Override
	public java.util.List<com.liferay.account.model.AccountEntryUserRel>
		getAccountEntryUserRelsByAccountEntryId(
			long accountEntryId, int start, int end) {

		return _accountEntryUserRelLocalService.
			getAccountEntryUserRelsByAccountEntryId(accountEntryId, start, end);
	}

	@Override
	public java.util.List<com.liferay.account.model.AccountEntryUserRel>
		getAccountEntryUserRelsByAccountUserId(long accountUserId) {

		return _accountEntryUserRelLocalService.
			getAccountEntryUserRelsByAccountUserId(accountUserId);
	}

	/**
	 * Returns the number of account entry user rels.
	 *
	 * @return the number of account entry user rels
	 */
	@Override
	public int getAccountEntryUserRelsCount() {
		return _accountEntryUserRelLocalService.getAccountEntryUserRelsCount();
	}

	@Override
	public long getAccountEntryUserRelsCountByAccountEntryId(
		long accountEntryId) {

		return _accountEntryUserRelLocalService.
			getAccountEntryUserRelsCountByAccountEntryId(accountEntryId);
	}

	@Override
	public com.liferay.portal.kernel.dao.orm.ActionableDynamicQuery
		getActionableDynamicQuery() {

		return _accountEntryUserRelLocalService.getActionableDynamicQuery();
	}

	@Override
	public com.liferay.portal.kernel.dao.orm.IndexableActionableDynamicQuery
		getIndexableActionableDynamicQuery() {

		return _accountEntryUserRelLocalService.
			getIndexableActionableDynamicQuery();
	}

	/**
	 * Returns the OSGi service identifier.
	 *
	 * @return the OSGi service identifier
	 */
	@Override
	public String getOSGiServiceIdentifier() {
		return _accountEntryUserRelLocalService.getOSGiServiceIdentifier();
	}

	/**
	 * @throws PortalException
	 */
	@Override
	public com.liferay.portal.kernel.model.PersistedModel getPersistedModel(
			java.io.Serializable primaryKeyObj)
		throws com.liferay.portal.kernel.exception.PortalException {

		return _accountEntryUserRelLocalService.getPersistedModel(
			primaryKeyObj);
	}

	@Override
	public boolean hasAccountEntryUserRel(long accountEntryId, long userId) {
		return _accountEntryUserRelLocalService.hasAccountEntryUserRel(
			accountEntryId, userId);
	}

	@Override
	public void inviteUser(
			long accountEntryId, long[] accountRoleIds, String emailAddress,
			com.liferay.portal.kernel.model.User inviter,
			com.liferay.portal.kernel.service.ServiceContext serviceContext)
		throws com.liferay.portal.kernel.exception.PortalException {

		_accountEntryUserRelLocalService.inviteUser(
			accountEntryId, accountRoleIds, emailAddress, inviter,
			serviceContext);
	}

	@Override
	public void setAccountEntryUserRels(
			long accountEntryId, long[] accountUserIds)
		throws com.liferay.portal.kernel.exception.PortalException {

		_accountEntryUserRelLocalService.setAccountEntryUserRels(
			accountEntryId, accountUserIds);
	}

	@Override
	public void setPersonTypeAccountEntryUser(long accountEntryId, long userId)
		throws com.liferay.portal.kernel.exception.PortalException {

		_accountEntryUserRelLocalService.setPersonTypeAccountEntryUser(
			accountEntryId, userId);
	}

	/**
	 * Updates the account entry user rel in the database or adds it if it does not yet exist. Also notifies the appropriate model listeners.
	 *
	 * <p>
	 * <strong>Important:</strong> Inspect AccountEntryUserRelLocalServiceImpl for overloaded versions of the method. If provided, use these entry points to the API, as the implementation logic may require the additional parameters defined there.
	 * </p>
	 *
	 * @param accountEntryUserRel the account entry user rel
	 * @return the account entry user rel that was updated
	 */
	@Override
	public com.liferay.account.model.AccountEntryUserRel
		updateAccountEntryUserRel(
			com.liferay.account.model.AccountEntryUserRel accountEntryUserRel) {

		return _accountEntryUserRelLocalService.updateAccountEntryUserRel(
			accountEntryUserRel);
	}

	@Override
	public void updateAccountEntryUserRels(
			long[] addAccountEntryIds, long[] deleteAccountEntryIds,
			long accountUserId)
		throws com.liferay.portal.kernel.exception.PortalException {

		_accountEntryUserRelLocalService.updateAccountEntryUserRels(
			addAccountEntryIds, deleteAccountEntryIds, accountUserId);
	}

	@Override
	public BasePersistence<?> getBasePersistence() {
		return _accountEntryUserRelLocalService.getBasePersistence();
	}

	@Override
	public AccountEntryUserRelLocalService getWrappedService() {
		return _accountEntryUserRelLocalService;
	}

	@Override
	public void setWrappedService(
		AccountEntryUserRelLocalService accountEntryUserRelLocalService) {

		_accountEntryUserRelLocalService = accountEntryUserRelLocalService;
	}

	private AccountEntryUserRelLocalService _accountEntryUserRelLocalService;

}
// LIFERAY-SERVICE-BUILDER-HASH:-459220170