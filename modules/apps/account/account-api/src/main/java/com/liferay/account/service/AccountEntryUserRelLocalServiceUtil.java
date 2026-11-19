/**
 * SPDX-FileCopyrightText: (c) 2000 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

package com.liferay.account.service;

import com.liferay.account.model.AccountEntryUserRel;
import com.liferay.petra.sql.dsl.query.DSLQuery;
import com.liferay.portal.kernel.dao.orm.DynamicQuery;
import com.liferay.portal.kernel.exception.PortalException;
import com.liferay.portal.kernel.model.PersistedModel;
import com.liferay.portal.kernel.module.service.Snapshot;
import com.liferay.portal.kernel.util.OrderByComparator;

import java.io.Serializable;

import java.util.List;

/**
 * Provides the local service utility for AccountEntryUserRel. This utility wraps
 * <code>com.liferay.account.service.impl.AccountEntryUserRelLocalServiceImpl</code> and
 * is an access point for service operations in application layer code running
 * on the local server. Methods of this service will not have security checks
 * based on the propagated JAAS credentials because this service can only be
 * accessed from within the same VM.
 *
 * @author Brian Wing Shun Chan
 * @see AccountEntryUserRelLocalService
 * @generated
 */
public class AccountEntryUserRelLocalServiceUtil {

	/*
	 * NOTE FOR DEVELOPERS:
	 *
	 * Never modify this class directly. Add custom service methods to <code>com.liferay.account.service.impl.AccountEntryUserRelLocalServiceImpl</code> and rerun ServiceBuilder to regenerate this class.
	 */

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
	public static AccountEntryUserRel addAccountEntryUserRel(
		AccountEntryUserRel accountEntryUserRel) {

		return getService().addAccountEntryUserRel(accountEntryUserRel);
	}

	public static AccountEntryUserRel addAccountEntryUserRel(
			long accountEntryId, long accountUserId)
		throws PortalException {

		return getService().addAccountEntryUserRel(
			accountEntryId, accountUserId);
	}

	public static AccountEntryUserRel addAccountEntryUserRel(
			long accountEntryId, long creatorUserId, String screenName,
			String emailAddress, java.util.Locale locale, String firstName,
			String middleName, String lastName, long prefixListTypeId,
			long suffixListTypeId, String jobTitle,
			com.liferay.portal.kernel.service.ServiceContext serviceContext)
		throws PortalException {

		return getService().addAccountEntryUserRel(
			accountEntryId, creatorUserId, screenName, emailAddress, locale,
			firstName, middleName, lastName, prefixListTypeId, suffixListTypeId,
			jobTitle, serviceContext);
	}

	public static AccountEntryUserRel addAccountEntryUserRelByEmailAddress(
			long accountEntryId, String emailAddress, long[] accountRoleIds,
			String userExternalReferenceCode,
			com.liferay.portal.kernel.service.ServiceContext serviceContext)
		throws PortalException {

		return getService().addAccountEntryUserRelByEmailAddress(
			accountEntryId, emailAddress, accountRoleIds,
			userExternalReferenceCode, serviceContext);
	}

	public static void addAccountEntryUserRels(
			long accountEntryId, long[] accountUserIds)
		throws PortalException {

		getService().addAccountEntryUserRels(accountEntryId, accountUserIds);
	}

	public static AccountEntryUserRel addPersonTypeAccountEntryUserRel(
			long accountEntryId, long creatorUserId, String screenName,
			String emailAddress, java.util.Locale locale, String firstName,
			String middleName, String lastName, long prefixListTypeId,
			long suffixListTypeId, String jobTitle,
			com.liferay.portal.kernel.service.ServiceContext serviceContext)
		throws PortalException {

		return getService().addPersonTypeAccountEntryUserRel(
			accountEntryId, creatorUserId, screenName, emailAddress, locale,
			firstName, middleName, lastName, prefixListTypeId, suffixListTypeId,
			jobTitle, serviceContext);
	}

	public static com.liferay.portal.kernel.model.Ticket
			addUserInvitationTicket(
				long accountEntryId, long[] accountRoleIds, String emailAddress,
				com.liferay.portal.kernel.model.User inviter,
				com.liferay.portal.kernel.service.ServiceContext serviceContext)
		throws PortalException {

		return getService().addUserInvitationTicket(
			accountEntryId, accountRoleIds, emailAddress, inviter,
			serviceContext);
	}

	/**
	 * Creates a new account entry user rel with the primary key. Does not add the account entry user rel to the database.
	 *
	 * @param accountEntryUserRelId the primary key for the new account entry user rel
	 * @return the new account entry user rel
	 */
	public static AccountEntryUserRel createAccountEntryUserRel(
		long accountEntryUserRelId) {

		return getService().createAccountEntryUserRel(accountEntryUserRelId);
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
	 * Deletes the account entry user rel from the database. Also notifies the appropriate model listeners.
	 *
	 * <p>
	 * <strong>Important:</strong> Inspect AccountEntryUserRelLocalServiceImpl for overloaded versions of the method. If provided, use these entry points to the API, as the implementation logic may require the additional parameters defined there.
	 * </p>
	 *
	 * @param accountEntryUserRel the account entry user rel
	 * @return the account entry user rel that was removed
	 */
	public static AccountEntryUserRel deleteAccountEntryUserRel(
		AccountEntryUserRel accountEntryUserRel) {

		return getService().deleteAccountEntryUserRel(accountEntryUserRel);
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
	public static AccountEntryUserRel deleteAccountEntryUserRel(
			long accountEntryUserRelId)
		throws PortalException {

		return getService().deleteAccountEntryUserRel(accountEntryUserRelId);
	}

	public static void deleteAccountEntryUserRelByEmailAddress(
			long accountEntryId, String emailAddress)
		throws PortalException {

		getService().deleteAccountEntryUserRelByEmailAddress(
			accountEntryId, emailAddress);
	}

	public static void deleteAccountEntryUserRels(
			long accountEntryId, long[] accountUserIds)
		throws PortalException {

		getService().deleteAccountEntryUserRels(accountEntryId, accountUserIds);
	}

	public static void deleteAccountEntryUserRelsByAccountEntryId(
		long accountEntryId) {

		getService().deleteAccountEntryUserRelsByAccountEntryId(accountEntryId);
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
	 * Useful when paginating results. Returns a maximum of <code>end - start</code> instances. <code>start</code> and <code>end</code> are not primary keys, they are indexes in the result set. Thus, <code>0</code> refers to the first result in the set. Setting both <code>start</code> and <code>end</code> to <code>com.liferay.portal.kernel.dao.orm.QueryUtil#ALL_POS</code> will return the full result set. If <code>orderByComparator</code> is specified, then the query will include the given ORDER BY logic. If <code>orderByComparator</code> is absent, then the query will include the default ORDER BY logic from <code>com.liferay.account.model.impl.AccountEntryUserRelModelImpl</code>.
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
	 * Useful when paginating results. Returns a maximum of <code>end - start</code> instances. <code>start</code> and <code>end</code> are not primary keys, they are indexes in the result set. Thus, <code>0</code> refers to the first result in the set. Setting both <code>start</code> and <code>end</code> to <code>com.liferay.portal.kernel.dao.orm.QueryUtil#ALL_POS</code> will return the full result set. If <code>orderByComparator</code> is specified, then the query will include the given ORDER BY logic. If <code>orderByComparator</code> is absent, then the query will include the default ORDER BY logic from <code>com.liferay.account.model.impl.AccountEntryUserRelModelImpl</code>.
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

	public static AccountEntryUserRel fetchAccountEntryUserRel(
		long accountEntryUserRelId) {

		return getService().fetchAccountEntryUserRel(accountEntryUserRelId);
	}

	public static AccountEntryUserRel fetchAccountEntryUserRel(
		long accountEntryId, long accountUserId) {

		return getService().fetchAccountEntryUserRel(
			accountEntryId, accountUserId);
	}

	/**
	 * Returns the account entry user rel with the primary key.
	 *
	 * @param accountEntryUserRelId the primary key of the account entry user rel
	 * @return the account entry user rel
	 * @throws PortalException if a account entry user rel with the primary key could not be found
	 */
	public static AccountEntryUserRel getAccountEntryUserRel(
			long accountEntryUserRelId)
		throws PortalException {

		return getService().getAccountEntryUserRel(accountEntryUserRelId);
	}

	public static AccountEntryUserRel getAccountEntryUserRel(
			long accountEntryId, long accountUserId)
		throws PortalException {

		return getService().getAccountEntryUserRel(
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
	public static List<AccountEntryUserRel> getAccountEntryUserRels(
		int start, int end) {

		return getService().getAccountEntryUserRels(start, end);
	}

	public static List<AccountEntryUserRel>
		getAccountEntryUserRelsByAccountEntryId(long accountEntryId) {

		return getService().getAccountEntryUserRelsByAccountEntryId(
			accountEntryId);
	}

	public static List<AccountEntryUserRel>
		getAccountEntryUserRelsByAccountEntryId(
			long accountEntryId, int start, int end) {

		return getService().getAccountEntryUserRelsByAccountEntryId(
			accountEntryId, start, end);
	}

	public static List<AccountEntryUserRel>
		getAccountEntryUserRelsByAccountUserId(long accountUserId) {

		return getService().getAccountEntryUserRelsByAccountUserId(
			accountUserId);
	}

	/**
	 * Returns the number of account entry user rels.
	 *
	 * @return the number of account entry user rels
	 */
	public static int getAccountEntryUserRelsCount() {
		return getService().getAccountEntryUserRelsCount();
	}

	public static long getAccountEntryUserRelsCountByAccountEntryId(
		long accountEntryId) {

		return getService().getAccountEntryUserRelsCountByAccountEntryId(
			accountEntryId);
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

	public static boolean hasAccountEntryUserRel(
		long accountEntryId, long userId) {

		return getService().hasAccountEntryUserRel(accountEntryId, userId);
	}

	public static void inviteUser(
			long accountEntryId, long[] accountRoleIds, String emailAddress,
			com.liferay.portal.kernel.model.User inviter,
			com.liferay.portal.kernel.service.ServiceContext serviceContext)
		throws PortalException {

		getService().inviteUser(
			accountEntryId, accountRoleIds, emailAddress, inviter,
			serviceContext);
	}

	public static void setAccountEntryUserRels(
			long accountEntryId, long[] accountUserIds)
		throws PortalException {

		getService().setAccountEntryUserRels(accountEntryId, accountUserIds);
	}

	public static void setPersonTypeAccountEntryUser(
			long accountEntryId, long userId)
		throws PortalException {

		getService().setPersonTypeAccountEntryUser(accountEntryId, userId);
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
	public static AccountEntryUserRel updateAccountEntryUserRel(
		AccountEntryUserRel accountEntryUserRel) {

		return getService().updateAccountEntryUserRel(accountEntryUserRel);
	}

	public static void updateAccountEntryUserRels(
			long[] addAccountEntryIds, long[] deleteAccountEntryIds,
			long accountUserId)
		throws PortalException {

		getService().updateAccountEntryUserRels(
			addAccountEntryIds, deleteAccountEntryIds, accountUserId);
	}

	public static AccountEntryUserRelLocalService getService() {
		return _serviceSnapshot.get();
	}

	private static final Snapshot<AccountEntryUserRelLocalService>
		_serviceSnapshot = new Snapshot<>(
			AccountEntryUserRelLocalServiceUtil.class,
			AccountEntryUserRelLocalService.class);

}
// LIFERAY-SERVICE-BUILDER-HASH:1188505420