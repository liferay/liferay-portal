/**
 * SPDX-FileCopyrightText: (c) 2000 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

package com.liferay.account.service;

import com.liferay.account.model.AccountEntryOrganizationRel;
import com.liferay.petra.sql.dsl.query.DSLQuery;
import com.liferay.portal.kernel.dao.orm.DynamicQuery;
import com.liferay.portal.kernel.exception.PortalException;
import com.liferay.portal.kernel.model.PersistedModel;
import com.liferay.portal.kernel.module.service.Snapshot;
import com.liferay.portal.kernel.util.OrderByComparator;

import java.io.Serializable;

import java.util.List;

/**
 * Provides the local service utility for AccountEntryOrganizationRel. This utility wraps
 * <code>com.liferay.account.service.impl.AccountEntryOrganizationRelLocalServiceImpl</code> and
 * is an access point for service operations in application layer code running
 * on the local server. Methods of this service will not have security checks
 * based on the propagated JAAS credentials because this service can only be
 * accessed from within the same VM.
 *
 * @author Brian Wing Shun Chan
 * @see AccountEntryOrganizationRelLocalService
 * @generated
 */
public class AccountEntryOrganizationRelLocalServiceUtil {

	/*
	 * NOTE FOR DEVELOPERS:
	 *
	 * Never modify this class directly. Add custom service methods to <code>com.liferay.account.service.impl.AccountEntryOrganizationRelLocalServiceImpl</code> and rerun ServiceBuilder to regenerate this class.
	 */

	/**
	 * Adds the account entry organization rel to the database. Also notifies the appropriate model listeners.
	 *
	 * <p>
	 * <strong>Important:</strong> Inspect AccountEntryOrganizationRelLocalServiceImpl for overloaded versions of the method. If provided, use these entry points to the API, as the implementation logic may require the additional parameters defined there.
	 * </p>
	 *
	 * @param accountEntryOrganizationRel the account entry organization rel
	 * @return the account entry organization rel that was added
	 */
	public static AccountEntryOrganizationRel addAccountEntryOrganizationRel(
		AccountEntryOrganizationRel accountEntryOrganizationRel) {

		return getService().addAccountEntryOrganizationRel(
			accountEntryOrganizationRel);
	}

	public static AccountEntryOrganizationRel addAccountEntryOrganizationRel(
			long accountEntryId, long organizationId)
		throws PortalException {

		return getService().addAccountEntryOrganizationRel(
			accountEntryId, organizationId);
	}

	public static void addAccountEntryOrganizationRels(
			long accountEntryId, long[] organizationIds)
		throws PortalException {

		getService().addAccountEntryOrganizationRels(
			accountEntryId, organizationIds);
	}

	/**
	 * Creates a new account entry organization rel with the primary key. Does not add the account entry organization rel to the database.
	 *
	 * @param accountEntryOrganizationRelId the primary key for the new account entry organization rel
	 * @return the new account entry organization rel
	 */
	public static AccountEntryOrganizationRel createAccountEntryOrganizationRel(
		long accountEntryOrganizationRelId) {

		return getService().createAccountEntryOrganizationRel(
			accountEntryOrganizationRelId);
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
	 * Deletes the account entry organization rel from the database. Also notifies the appropriate model listeners.
	 *
	 * <p>
	 * <strong>Important:</strong> Inspect AccountEntryOrganizationRelLocalServiceImpl for overloaded versions of the method. If provided, use these entry points to the API, as the implementation logic may require the additional parameters defined there.
	 * </p>
	 *
	 * @param accountEntryOrganizationRel the account entry organization rel
	 * @return the account entry organization rel that was removed
	 */
	public static AccountEntryOrganizationRel deleteAccountEntryOrganizationRel(
		AccountEntryOrganizationRel accountEntryOrganizationRel) {

		return getService().deleteAccountEntryOrganizationRel(
			accountEntryOrganizationRel);
	}

	/**
	 * Deletes the account entry organization rel with the primary key from the database. Also notifies the appropriate model listeners.
	 *
	 * <p>
	 * <strong>Important:</strong> Inspect AccountEntryOrganizationRelLocalServiceImpl for overloaded versions of the method. If provided, use these entry points to the API, as the implementation logic may require the additional parameters defined there.
	 * </p>
	 *
	 * @param accountEntryOrganizationRelId the primary key of the account entry organization rel
	 * @return the account entry organization rel that was removed
	 * @throws PortalException if a account entry organization rel with the primary key could not be found
	 */
	public static AccountEntryOrganizationRel deleteAccountEntryOrganizationRel(
			long accountEntryOrganizationRelId)
		throws PortalException {

		return getService().deleteAccountEntryOrganizationRel(
			accountEntryOrganizationRelId);
	}

	public static void deleteAccountEntryOrganizationRel(
			long accountEntryId, long organizationId)
		throws PortalException {

		getService().deleteAccountEntryOrganizationRel(
			accountEntryId, organizationId);
	}

	public static void deleteAccountEntryOrganizationRels(
			long accountEntryId, long[] organizationIds)
		throws PortalException {

		getService().deleteAccountEntryOrganizationRels(
			accountEntryId, organizationIds);
	}

	public static void deleteAccountEntryOrganizationRelsByAccountEntryId(
		long accountEntryId) {

		getService().deleteAccountEntryOrganizationRelsByAccountEntryId(
			accountEntryId);
	}

	public static void deleteAccountEntryOrganizationRelsByOrganizationId(
		long organizationId) {

		getService().deleteAccountEntryOrganizationRelsByOrganizationId(
			organizationId);
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
	 * Useful when paginating results. Returns a maximum of <code>end - start</code> instances. <code>start</code> and <code>end</code> are not primary keys, they are indexes in the result set. Thus, <code>0</code> refers to the first result in the set. Setting both <code>start</code> and <code>end</code> to <code>com.liferay.portal.kernel.dao.orm.QueryUtil#ALL_POS</code> will return the full result set. If <code>orderByComparator</code> is specified, then the query will include the given ORDER BY logic. If <code>orderByComparator</code> is absent, then the query will include the default ORDER BY logic from <code>com.liferay.account.model.impl.AccountEntryOrganizationRelModelImpl</code>.
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
	 * Useful when paginating results. Returns a maximum of <code>end - start</code> instances. <code>start</code> and <code>end</code> are not primary keys, they are indexes in the result set. Thus, <code>0</code> refers to the first result in the set. Setting both <code>start</code> and <code>end</code> to <code>com.liferay.portal.kernel.dao.orm.QueryUtil#ALL_POS</code> will return the full result set. If <code>orderByComparator</code> is specified, then the query will include the given ORDER BY logic. If <code>orderByComparator</code> is absent, then the query will include the default ORDER BY logic from <code>com.liferay.account.model.impl.AccountEntryOrganizationRelModelImpl</code>.
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

	public static AccountEntryOrganizationRel fetchAccountEntryOrganizationRel(
		long accountEntryOrganizationRelId) {

		return getService().fetchAccountEntryOrganizationRel(
			accountEntryOrganizationRelId);
	}

	public static AccountEntryOrganizationRel fetchAccountEntryOrganizationRel(
		long accountEntryId, long organizationId) {

		return getService().fetchAccountEntryOrganizationRel(
			accountEntryId, organizationId);
	}

	/**
	 * Returns the account entry organization rel with the primary key.
	 *
	 * @param accountEntryOrganizationRelId the primary key of the account entry organization rel
	 * @return the account entry organization rel
	 * @throws PortalException if a account entry organization rel with the primary key could not be found
	 */
	public static AccountEntryOrganizationRel getAccountEntryOrganizationRel(
			long accountEntryOrganizationRelId)
		throws PortalException {

		return getService().getAccountEntryOrganizationRel(
			accountEntryOrganizationRelId);
	}

	/**
	 * Returns a range of all the account entry organization rels.
	 *
	 * <p>
	 * Useful when paginating results. Returns a maximum of <code>end - start</code> instances. <code>start</code> and <code>end</code> are not primary keys, they are indexes in the result set. Thus, <code>0</code> refers to the first result in the set. Setting both <code>start</code> and <code>end</code> to <code>com.liferay.portal.kernel.dao.orm.QueryUtil#ALL_POS</code> will return the full result set. If <code>orderByComparator</code> is specified, then the query will include the given ORDER BY logic. If <code>orderByComparator</code> is absent, then the query will include the default ORDER BY logic from <code>com.liferay.account.model.impl.AccountEntryOrganizationRelModelImpl</code>.
	 * </p>
	 *
	 * @param start the lower bound of the range of account entry organization rels
	 * @param end the upper bound of the range of account entry organization rels (not inclusive)
	 * @return the range of account entry organization rels
	 */
	public static List<AccountEntryOrganizationRel>
		getAccountEntryOrganizationRels(int start, int end) {

		return getService().getAccountEntryOrganizationRels(start, end);
	}

	public static List<AccountEntryOrganizationRel>
		getAccountEntryOrganizationRels(long accountEntryId) {

		return getService().getAccountEntryOrganizationRels(accountEntryId);
	}

	public static List<AccountEntryOrganizationRel>
		getAccountEntryOrganizationRelsByOrganizationId(long organizationId) {

		return getService().getAccountEntryOrganizationRelsByOrganizationId(
			organizationId);
	}

	public static List<AccountEntryOrganizationRel>
		getAccountEntryOrganizationRelsByOrganizationId(
			long organizationId, int start, int end) {

		return getService().getAccountEntryOrganizationRelsByOrganizationId(
			organizationId, start, end);
	}

	/**
	 * Returns the number of account entry organization rels.
	 *
	 * @return the number of account entry organization rels
	 */
	public static int getAccountEntryOrganizationRelsCount() {
		return getService().getAccountEntryOrganizationRelsCount();
	}

	public static int getAccountEntryOrganizationRelsCount(
		long accountEntryId) {

		return getService().getAccountEntryOrganizationRelsCount(
			accountEntryId);
	}

	public static int getAccountEntryOrganizationRelsCountByOrganizationId(
		long organizationId) {

		return getService().
			getAccountEntryOrganizationRelsCountByOrganizationId(
				organizationId);
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

	public static boolean hasAccountEntryOrganizationRel(
		long accountEntryId, long organizationId) {

		return getService().hasAccountEntryOrganizationRel(
			accountEntryId, organizationId);
	}

	/**
	 * Creates an AccountEntryOrganizationRel for each given organizationId,
	 * unless it already exists, and removes existing
	 * AccountEntryOrganizationRels if their organizationId is not present in
	 * the given organizationIds.
	 *
	 * @param accountEntryId
	 * @param organizationIds
	 * @throws PortalException
	 * @review
	 */
	public static void setAccountEntryOrganizationRels(
			long accountEntryId, long[] organizationIds)
		throws PortalException {

		getService().setAccountEntryOrganizationRels(
			accountEntryId, organizationIds);
	}

	/**
	 * Updates the account entry organization rel in the database or adds it if it does not yet exist. Also notifies the appropriate model listeners.
	 *
	 * <p>
	 * <strong>Important:</strong> Inspect AccountEntryOrganizationRelLocalServiceImpl for overloaded versions of the method. If provided, use these entry points to the API, as the implementation logic may require the additional parameters defined there.
	 * </p>
	 *
	 * @param accountEntryOrganizationRel the account entry organization rel
	 * @return the account entry organization rel that was updated
	 */
	public static AccountEntryOrganizationRel updateAccountEntryOrganizationRel(
		AccountEntryOrganizationRel accountEntryOrganizationRel) {

		return getService().updateAccountEntryOrganizationRel(
			accountEntryOrganizationRel);
	}

	public static AccountEntryOrganizationRelLocalService getService() {
		return _serviceSnapshot.get();
	}

	private static final Snapshot<AccountEntryOrganizationRelLocalService>
		_serviceSnapshot = new Snapshot<>(
			AccountEntryOrganizationRelLocalServiceUtil.class,
			AccountEntryOrganizationRelLocalService.class);

}
// LIFERAY-SERVICE-BUILDER-HASH:431109800