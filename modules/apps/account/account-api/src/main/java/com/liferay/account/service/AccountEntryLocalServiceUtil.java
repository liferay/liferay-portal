/**
 * SPDX-FileCopyrightText: (c) 2000 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

package com.liferay.account.service;

import com.liferay.account.model.AccountEntry;
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
 * Provides the local service utility for AccountEntry. This utility wraps
 * <code>com.liferay.account.service.impl.AccountEntryLocalServiceImpl</code> and
 * is an access point for service operations in application layer code running
 * on the local server. Methods of this service will not have security checks
 * based on the propagated JAAS credentials because this service can only be
 * accessed from within the same VM.
 *
 * @author Brian Wing Shun Chan
 * @see AccountEntryLocalService
 * @generated
 */
public class AccountEntryLocalServiceUtil {

	/*
	 * NOTE FOR DEVELOPERS:
	 *
	 * Never modify this class directly. Add custom service methods to <code>com.liferay.account.service.impl.AccountEntryLocalServiceImpl</code> and rerun ServiceBuilder to regenerate this class.
	 */
	public static void activateAccountEntries(long[] accountEntryIds)
		throws PortalException {

		getService().activateAccountEntries(accountEntryIds);
	}

	public static AccountEntry activateAccountEntry(AccountEntry accountEntry)
		throws PortalException {

		return getService().activateAccountEntry(accountEntry);
	}

	public static AccountEntry activateAccountEntry(long accountEntryId)
		throws PortalException {

		return getService().activateAccountEntry(accountEntryId);
	}

	/**
	 * Adds the account entry to the database. Also notifies the appropriate model listeners.
	 *
	 * <p>
	 * <strong>Important:</strong> Inspect AccountEntryLocalServiceImpl for overloaded versions of the method. If provided, use these entry points to the API, as the implementation logic may require the additional parameters defined there.
	 * </p>
	 *
	 * @param accountEntry the account entry
	 * @return the account entry that was added
	 */
	public static AccountEntry addAccountEntry(AccountEntry accountEntry) {
		return getService().addAccountEntry(accountEntry);
	}

	public static AccountEntry addAccountEntry(
			String externalReferenceCode, long userId,
			long parentAccountEntryId, String name, String description,
			String[] domains, String emailAddress, byte[] logoBytes,
			String taxIdNumber, String type, int status,
			com.liferay.portal.kernel.service.ServiceContext serviceContext)
		throws PortalException {

		return getService().addAccountEntry(
			externalReferenceCode, userId, parentAccountEntryId, name,
			description, domains, emailAddress, logoBytes, taxIdNumber, type,
			status, serviceContext);
	}

	public static AccountEntry addOrUpdateAccountEntry(
			String externalReferenceCode, long userId,
			long parentAccountEntryId, String name, String description,
			String[] domains, String emailAddress, byte[] logoBytes,
			String taxIdNumber, String type, int status,
			com.liferay.portal.kernel.service.ServiceContext serviceContext)
		throws PortalException {

		return getService().addOrUpdateAccountEntry(
			externalReferenceCode, userId, parentAccountEntryId, name,
			description, domains, emailAddress, logoBytes, taxIdNumber, type,
			status, serviceContext);
	}

	/**
	 * Creates a new account entry with the primary key. Does not add the account entry to the database.
	 *
	 * @param accountEntryId the primary key for the new account entry
	 * @return the new account entry
	 */
	public static AccountEntry createAccountEntry(long accountEntryId) {
		return getService().createAccountEntry(accountEntryId);
	}

	/**
	 * @throws PortalException
	 */
	public static PersistedModel createPersistedModel(
			Serializable primaryKeyObj)
		throws PortalException {

		return getService().createPersistedModel(primaryKeyObj);
	}

	public static void deactivateAccountEntries(long[] accountEntryIds)
		throws PortalException {

		getService().deactivateAccountEntries(accountEntryIds);
	}

	public static AccountEntry deactivateAccountEntry(AccountEntry accountEntry)
		throws PortalException {

		return getService().deactivateAccountEntry(accountEntry);
	}

	public static AccountEntry deactivateAccountEntry(long accountEntryId)
		throws PortalException {

		return getService().deactivateAccountEntry(accountEntryId);
	}

	public static void deleteAccountEntries(long[] accountEntryIds)
		throws PortalException {

		getService().deleteAccountEntries(accountEntryIds);
	}

	public static void deleteAccountEntriesByCompanyId(long companyId) {
		getService().deleteAccountEntriesByCompanyId(companyId);
	}

	/**
	 * Deletes the account entry from the database. Also notifies the appropriate model listeners.
	 *
	 * <p>
	 * <strong>Important:</strong> Inspect AccountEntryLocalServiceImpl for overloaded versions of the method. If provided, use these entry points to the API, as the implementation logic may require the additional parameters defined there.
	 * </p>
	 *
	 * @param accountEntry the account entry
	 * @return the account entry that was removed
	 * @throws PortalException
	 */
	public static AccountEntry deleteAccountEntry(AccountEntry accountEntry)
		throws PortalException {

		return getService().deleteAccountEntry(accountEntry);
	}

	/**
	 * Deletes the account entry with the primary key from the database. Also notifies the appropriate model listeners.
	 *
	 * <p>
	 * <strong>Important:</strong> Inspect AccountEntryLocalServiceImpl for overloaded versions of the method. If provided, use these entry points to the API, as the implementation logic may require the additional parameters defined there.
	 * </p>
	 *
	 * @param accountEntryId the primary key of the account entry
	 * @return the account entry that was removed
	 * @throws PortalException if a account entry with the primary key could not be found
	 */
	public static AccountEntry deleteAccountEntry(long accountEntryId)
		throws PortalException {

		return getService().deleteAccountEntry(accountEntryId);
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
	 * Useful when paginating results. Returns a maximum of <code>end - start</code> instances. <code>start</code> and <code>end</code> are not primary keys, they are indexes in the result set. Thus, <code>0</code> refers to the first result in the set. Setting both <code>start</code> and <code>end</code> to <code>com.liferay.portal.kernel.dao.orm.QueryUtil#ALL_POS</code> will return the full result set. If <code>orderByComparator</code> is specified, then the query will include the given ORDER BY logic. If <code>orderByComparator</code> is absent, then the query will include the default ORDER BY logic from <code>com.liferay.account.model.impl.AccountEntryModelImpl</code>.
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
	 * Useful when paginating results. Returns a maximum of <code>end - start</code> instances. <code>start</code> and <code>end</code> are not primary keys, they are indexes in the result set. Thus, <code>0</code> refers to the first result in the set. Setting both <code>start</code> and <code>end</code> to <code>com.liferay.portal.kernel.dao.orm.QueryUtil#ALL_POS</code> will return the full result set. If <code>orderByComparator</code> is specified, then the query will include the given ORDER BY logic. If <code>orderByComparator</code> is absent, then the query will include the default ORDER BY logic from <code>com.liferay.account.model.impl.AccountEntryModelImpl</code>.
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

	public static AccountEntry fetchAccountEntry(long accountEntryId) {
		return getService().fetchAccountEntry(accountEntryId);
	}

	public static AccountEntry fetchAccountEntryByExternalReferenceCode(
		String externalReferenceCode, long companyId) {

		return getService().fetchAccountEntryByExternalReferenceCode(
			externalReferenceCode, companyId);
	}

	/**
	 * Returns the account entry with the matching UUID and company.
	 *
	 * @param uuid the account entry's UUID
	 * @param companyId the primary key of the company
	 * @return the matching account entry, or <code>null</code> if a matching account entry could not be found
	 */
	public static AccountEntry fetchAccountEntryByUuidAndCompanyId(
		String uuid, long companyId) {

		return getService().fetchAccountEntryByUuidAndCompanyId(
			uuid, companyId);
	}

	public static AccountEntry fetchPersonAccountEntry(long userId) {
		return getService().fetchPersonAccountEntry(userId);
	}

	public static AccountEntry fetchSupplierAccountEntry(long userId) {
		return getService().fetchSupplierAccountEntry(userId);
	}

	public static AccountEntry fetchUserAccountEntry(
		long userId, long accountEntryId) {

		return getService().fetchUserAccountEntry(userId, accountEntryId);
	}

	/**
	 * Returns a range of all the account entries.
	 *
	 * <p>
	 * Useful when paginating results. Returns a maximum of <code>end - start</code> instances. <code>start</code> and <code>end</code> are not primary keys, they are indexes in the result set. Thus, <code>0</code> refers to the first result in the set. Setting both <code>start</code> and <code>end</code> to <code>com.liferay.portal.kernel.dao.orm.QueryUtil#ALL_POS</code> will return the full result set. If <code>orderByComparator</code> is specified, then the query will include the given ORDER BY logic. If <code>orderByComparator</code> is absent, then the query will include the default ORDER BY logic from <code>com.liferay.account.model.impl.AccountEntryModelImpl</code>.
	 * </p>
	 *
	 * @param start the lower bound of the range of account entries
	 * @param end the upper bound of the range of account entries (not inclusive)
	 * @return the range of account entries
	 */
	public static List<AccountEntry> getAccountEntries(int start, int end) {
		return getService().getAccountEntries(start, end);
	}

	public static List<AccountEntry> getAccountEntries(
		long companyId, int status, int start, int end,
		OrderByComparator<AccountEntry> orderByComparator) {

		return getService().getAccountEntries(
			companyId, status, start, end, orderByComparator);
	}

	/**
	 * Returns the number of account entries.
	 *
	 * @return the number of account entries
	 */
	public static int getAccountEntriesCount() {
		return getService().getAccountEntriesCount();
	}

	public static int getAccountEntriesCount(long companyId, int status) {
		return getService().getAccountEntriesCount(companyId, status);
	}

	/**
	 * Returns the account entry with the primary key.
	 *
	 * @param accountEntryId the primary key of the account entry
	 * @return the account entry
	 * @throws PortalException if a account entry with the primary key could not be found
	 */
	public static AccountEntry getAccountEntry(long accountEntryId)
		throws PortalException {

		return getService().getAccountEntry(accountEntryId);
	}

	public static AccountEntry getAccountEntryByExternalReferenceCode(
			String externalReferenceCode, long companyId)
		throws PortalException {

		return getService().getAccountEntryByExternalReferenceCode(
			externalReferenceCode, companyId);
	}

	/**
	 * Returns the account entry with the matching UUID and company.
	 *
	 * @param uuid the account entry's UUID
	 * @param companyId the primary key of the company
	 * @return the matching account entry
	 * @throws PortalException if a matching account entry could not be found
	 */
	public static AccountEntry getAccountEntryByUuidAndCompanyId(
			String uuid, long companyId)
		throws PortalException {

		return getService().getAccountEntryByUuidAndCompanyId(uuid, companyId);
	}

	public static com.liferay.portal.kernel.dao.orm.ActionableDynamicQuery
		getActionableDynamicQuery() {

		return getService().getActionableDynamicQuery();
	}

	public static com.liferay.portal.kernel.dao.orm.ExportActionableDynamicQuery
		getExportActionableDynamicQuery(
			com.liferay.exportimport.kernel.lar.PortletDataContext
				portletDataContext) {

		return getService().getExportActionableDynamicQuery(portletDataContext);
	}

	public static AccountEntry getGuestAccountEntry(long companyId)
		throws PortalException {

		return getService().getGuestAccountEntry(companyId);
	}

	public static
		com.liferay.portal.kernel.dao.orm.IndexableActionableDynamicQuery
			getIndexableActionableDynamicQuery() {

		return getService().getIndexableActionableDynamicQuery();
	}

	public static AccountEntry getOrAddEmptyAccountEntry(
			String externalReferenceCode, long companyId, long userId,
			String name, String type)
		throws PortalException {

		return getService().getOrAddEmptyAccountEntry(
			externalReferenceCode, companyId, userId, name, type);
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

	public static List<AccountEntry> getUserAccountEntries(
			long userId, Long parentAccountEntryId, String keywords,
			String[] types, int start, int end)
		throws PortalException {

		return getService().getUserAccountEntries(
			userId, parentAccountEntryId, keywords, types, start, end);
	}

	public static List<AccountEntry> getUserAccountEntries(
			long userId, Long parentAccountEntryId, String keywords,
			String[] types, Integer status, int start, int end)
		throws PortalException {

		return getService().getUserAccountEntries(
			userId, parentAccountEntryId, keywords, types, status, start, end);
	}

	public static List<AccountEntry> getUserAccountEntries(
			long userId, Long parentAccountEntryId, String keywords,
			String[] types, Integer status, int start, int end,
			OrderByComparator<AccountEntry> orderByComparator)
		throws PortalException {

		return getService().getUserAccountEntries(
			userId, parentAccountEntryId, keywords, types, status, start, end,
			orderByComparator);
	}

	public static int getUserAccountEntriesCount(
			long userId, Long parentAccountEntryId, String keywords,
			String[] types)
		throws PortalException {

		return getService().getUserAccountEntriesCount(
			userId, parentAccountEntryId, keywords, types);
	}

	public static int getUserAccountEntriesCount(
			long userId, Long parentAccountEntryId, String keywords,
			String[] types, Integer status)
		throws PortalException {

		return getService().getUserAccountEntriesCount(
			userId, parentAccountEntryId, keywords, types, status);
	}

	public static com.liferay.portal.kernel.search.BaseModelSearchResult
		<AccountEntry> searchAccountEntries(
			long companyId, String keywords,
			java.util.LinkedHashMap<String, Object> params, int cur, int delta,
			String orderByField, boolean reverse) {

		return getService().searchAccountEntries(
			companyId, keywords, params, cur, delta, orderByField, reverse);
	}

	/**
	 * Updates the account entry in the database or adds it if it does not yet exist. Also notifies the appropriate model listeners.
	 *
	 * <p>
	 * <strong>Important:</strong> Inspect AccountEntryLocalServiceImpl for overloaded versions of the method. If provided, use these entry points to the API, as the implementation logic may require the additional parameters defined there.
	 * </p>
	 *
	 * @param accountEntry the account entry
	 * @return the account entry that was updated
	 */
	public static AccountEntry updateAccountEntry(AccountEntry accountEntry) {
		return getService().updateAccountEntry(accountEntry);
	}

	public static AccountEntry updateAccountEntry(
			String externalReferenceCode, long accountEntryId,
			long parentAccountEntryId, String name, String description,
			boolean deleteLogo, String[] domains, String emailAddress,
			byte[] logoBytes, String taxIdNumber, int status,
			com.liferay.portal.kernel.service.ServiceContext serviceContext)
		throws PortalException {

		return getService().updateAccountEntry(
			externalReferenceCode, accountEntryId, parentAccountEntryId, name,
			description, deleteLogo, domains, emailAddress, logoBytes,
			taxIdNumber, status, serviceContext);
	}

	public static AccountEntry updateDefaultBillingAddressId(
			long accountEntryId, long addressId)
		throws PortalException {

		return getService().updateDefaultBillingAddressId(
			accountEntryId, addressId);
	}

	public static AccountEntry updateDefaultShippingAddressId(
			long accountEntryId, long addressId)
		throws PortalException {

		return getService().updateDefaultShippingAddressId(
			accountEntryId, addressId);
	}

	public static AccountEntry updateDomains(
			long accountEntryId, String[] domains)
		throws PortalException {

		return getService().updateDomains(accountEntryId, domains);
	}

	public static AccountEntry updateExternalReferenceCode(
			AccountEntry accountEntry, String externalReferenceCode)
		throws PortalException {

		return getService().updateExternalReferenceCode(
			accountEntry, externalReferenceCode);
	}

	public static AccountEntry updateExternalReferenceCode(
			long accountEntryId, String externalReferenceCode)
		throws PortalException {

		return getService().updateExternalReferenceCode(
			accountEntryId, externalReferenceCode);
	}

	public static AccountEntry updateRestrictMembership(
			long accountEntryId, boolean restrictMembership)
		throws PortalException {

		return getService().updateRestrictMembership(
			accountEntryId, restrictMembership);
	}

	public static AccountEntry updateStatus(
			AccountEntry accountEntry, int status)
		throws PortalException {

		return getService().updateStatus(accountEntry, status);
	}

	public static AccountEntry updateStatus(long accountEntryId, int status)
		throws PortalException {

		return getService().updateStatus(accountEntryId, status);
	}

	public static AccountEntry updateStatus(
			long userId, long accountEntryId, int status,
			com.liferay.portal.kernel.service.ServiceContext serviceContext,
			Map<String, Serializable> workflowContext)
		throws PortalException {

		return getService().updateStatus(
			userId, accountEntryId, status, serviceContext, workflowContext);
	}

	public static AccountEntryLocalService getService() {
		return _serviceSnapshot.get();
	}

	private static final Snapshot<AccountEntryLocalService> _serviceSnapshot =
		new Snapshot<>(
			AccountEntryLocalServiceUtil.class, AccountEntryLocalService.class);

}