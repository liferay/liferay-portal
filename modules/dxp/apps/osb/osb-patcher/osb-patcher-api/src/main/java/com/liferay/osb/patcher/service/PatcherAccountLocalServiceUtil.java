/**
 * SPDX-FileCopyrightText: (c) 2025 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

package com.liferay.osb.patcher.service;

import com.liferay.osb.patcher.model.PatcherAccount;
import com.liferay.petra.sql.dsl.query.DSLQuery;
import com.liferay.portal.kernel.dao.orm.DynamicQuery;
import com.liferay.portal.kernel.exception.PortalException;
import com.liferay.portal.kernel.model.PersistedModel;
import com.liferay.portal.kernel.module.service.Snapshot;
import com.liferay.portal.kernel.util.OrderByComparator;

import java.io.Serializable;

import java.util.List;

/**
 * Provides the local service utility for PatcherAccount. This utility wraps
 * <code>com.liferay.osb.patcher.service.impl.PatcherAccountLocalServiceImpl</code> and
 * is an access point for service operations in application layer code running
 * on the local server. Methods of this service will not have security checks
 * based on the propagated JAAS credentials because this service can only be
 * accessed from within the same VM.
 *
 * @author Brian Wing Shun Chan
 * @see PatcherAccountLocalService
 * @generated
 */
public class PatcherAccountLocalServiceUtil {

	/*
	 * NOTE FOR DEVELOPERS:
	 *
	 * Never modify this class directly. Add custom service methods to <code>com.liferay.osb.patcher.service.impl.PatcherAccountLocalServiceImpl</code> and rerun ServiceBuilder to regenerate this class.
	 */

	/**
	 * Adds the patcher account to the database. Also notifies the appropriate model listeners.
	 *
	 * <p>
	 * <strong>Important:</strong> Inspect PatcherAccountLocalServiceImpl for overloaded versions of the method. If provided, use these entry points to the API, as the implementation logic may require the additional parameters defined there.
	 * </p>
	 *
	 * @param patcherAccount the patcher account
	 * @return the patcher account that was added
	 */
	public static PatcherAccount addPatcherAccount(
		PatcherAccount patcherAccount) {

		return getService().addPatcherAccount(patcherAccount);
	}

	public static boolean addPatcherBuildPatcherAccount(
		long patcherBuildId, long patcherAccountId) {

		return getService().addPatcherBuildPatcherAccount(
			patcherBuildId, patcherAccountId);
	}

	public static boolean addPatcherBuildPatcherAccount(
		long patcherBuildId, PatcherAccount patcherAccount) {

		return getService().addPatcherBuildPatcherAccount(
			patcherBuildId, patcherAccount);
	}

	public static boolean addPatcherBuildPatcherAccounts(
		long patcherBuildId, List<PatcherAccount> patcherAccounts) {

		return getService().addPatcherBuildPatcherAccounts(
			patcherBuildId, patcherAccounts);
	}

	public static boolean addPatcherBuildPatcherAccounts(
		long patcherBuildId, long[] patcherAccountIds) {

		return getService().addPatcherBuildPatcherAccounts(
			patcherBuildId, patcherAccountIds);
	}

	public static void clearPatcherBuildPatcherAccounts(long patcherBuildId) {
		getService().clearPatcherBuildPatcherAccounts(patcherBuildId);
	}

	/**
	 * Creates a new patcher account with the primary key. Does not add the patcher account to the database.
	 *
	 * @param patcherAccountId the primary key for the new patcher account
	 * @return the new patcher account
	 */
	public static PatcherAccount createPatcherAccount(long patcherAccountId) {
		return getService().createPatcherAccount(patcherAccountId);
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
	 * Deletes the patcher account with the primary key from the database. Also notifies the appropriate model listeners.
	 *
	 * <p>
	 * <strong>Important:</strong> Inspect PatcherAccountLocalServiceImpl for overloaded versions of the method. If provided, use these entry points to the API, as the implementation logic may require the additional parameters defined there.
	 * </p>
	 *
	 * @param patcherAccountId the primary key of the patcher account
	 * @return the patcher account that was removed
	 * @throws PortalException if a patcher account with the primary key could not be found
	 */
	public static PatcherAccount deletePatcherAccount(long patcherAccountId)
		throws PortalException {

		return getService().deletePatcherAccount(patcherAccountId);
	}

	/**
	 * Deletes the patcher account from the database. Also notifies the appropriate model listeners.
	 *
	 * <p>
	 * <strong>Important:</strong> Inspect PatcherAccountLocalServiceImpl for overloaded versions of the method. If provided, use these entry points to the API, as the implementation logic may require the additional parameters defined there.
	 * </p>
	 *
	 * @param patcherAccount the patcher account
	 * @return the patcher account that was removed
	 */
	public static PatcherAccount deletePatcherAccount(
		PatcherAccount patcherAccount) {

		return getService().deletePatcherAccount(patcherAccount);
	}

	public static void deletePatcherBuildPatcherAccount(
		long patcherBuildId, long patcherAccountId) {

		getService().deletePatcherBuildPatcherAccount(
			patcherBuildId, patcherAccountId);
	}

	public static void deletePatcherBuildPatcherAccount(
		long patcherBuildId, PatcherAccount patcherAccount) {

		getService().deletePatcherBuildPatcherAccount(
			patcherBuildId, patcherAccount);
	}

	public static void deletePatcherBuildPatcherAccounts(
		long patcherBuildId, List<PatcherAccount> patcherAccounts) {

		getService().deletePatcherBuildPatcherAccounts(
			patcherBuildId, patcherAccounts);
	}

	public static void deletePatcherBuildPatcherAccounts(
		long patcherBuildId, long[] patcherAccountIds) {

		getService().deletePatcherBuildPatcherAccounts(
			patcherBuildId, patcherAccountIds);
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
	 * Useful when paginating results. Returns a maximum of <code>end - start</code> instances. <code>start</code> and <code>end</code> are not primary keys, they are indexes in the result set. Thus, <code>0</code> refers to the first result in the set. Setting both <code>start</code> and <code>end</code> to <code>com.liferay.portal.kernel.dao.orm.QueryUtil#ALL_POS</code> will return the full result set. If <code>orderByComparator</code> is specified, then the query will include the given ORDER BY logic. If <code>orderByComparator</code> is absent, then the query will include the default ORDER BY logic from <code>com.liferay.osb.patcher.model.impl.PatcherAccountModelImpl</code>.
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
	 * Useful when paginating results. Returns a maximum of <code>end - start</code> instances. <code>start</code> and <code>end</code> are not primary keys, they are indexes in the result set. Thus, <code>0</code> refers to the first result in the set. Setting both <code>start</code> and <code>end</code> to <code>com.liferay.portal.kernel.dao.orm.QueryUtil#ALL_POS</code> will return the full result set. If <code>orderByComparator</code> is specified, then the query will include the given ORDER BY logic. If <code>orderByComparator</code> is absent, then the query will include the default ORDER BY logic from <code>com.liferay.osb.patcher.model.impl.PatcherAccountModelImpl</code>.
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

	public static PatcherAccount fetchPatcherAccount(long patcherAccountId) {
		return getService().fetchPatcherAccount(patcherAccountId);
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
	 * Returns the patcher account with the primary key.
	 *
	 * @param patcherAccountId the primary key of the patcher account
	 * @return the patcher account
	 * @throws PortalException if a patcher account with the primary key could not be found
	 */
	public static PatcherAccount getPatcherAccount(long patcherAccountId)
		throws PortalException {

		return getService().getPatcherAccount(patcherAccountId);
	}

	/**
	 * Returns a range of all the patcher accounts.
	 *
	 * <p>
	 * Useful when paginating results. Returns a maximum of <code>end - start</code> instances. <code>start</code> and <code>end</code> are not primary keys, they are indexes in the result set. Thus, <code>0</code> refers to the first result in the set. Setting both <code>start</code> and <code>end</code> to <code>com.liferay.portal.kernel.dao.orm.QueryUtil#ALL_POS</code> will return the full result set. If <code>orderByComparator</code> is specified, then the query will include the given ORDER BY logic. If <code>orderByComparator</code> is absent, then the query will include the default ORDER BY logic from <code>com.liferay.osb.patcher.model.impl.PatcherAccountModelImpl</code>.
	 * </p>
	 *
	 * @param start the lower bound of the range of patcher accounts
	 * @param end the upper bound of the range of patcher accounts (not inclusive)
	 * @return the range of patcher accounts
	 */
	public static List<PatcherAccount> getPatcherAccounts(int start, int end) {
		return getService().getPatcherAccounts(start, end);
	}

	/**
	 * Returns the number of patcher accounts.
	 *
	 * @return the number of patcher accounts
	 */
	public static int getPatcherAccountsCount() {
		return getService().getPatcherAccountsCount();
	}

	public static List<PatcherAccount> getPatcherBuildPatcherAccounts(
		long patcherBuildId) {

		return getService().getPatcherBuildPatcherAccounts(patcherBuildId);
	}

	public static List<PatcherAccount> getPatcherBuildPatcherAccounts(
		long patcherBuildId, int start, int end) {

		return getService().getPatcherBuildPatcherAccounts(
			patcherBuildId, start, end);
	}

	public static List<PatcherAccount> getPatcherBuildPatcherAccounts(
		long patcherBuildId, int start, int end,
		OrderByComparator<PatcherAccount> orderByComparator) {

		return getService().getPatcherBuildPatcherAccounts(
			patcherBuildId, start, end, orderByComparator);
	}

	public static int getPatcherBuildPatcherAccountsCount(long patcherBuildId) {
		return getService().getPatcherBuildPatcherAccountsCount(patcherBuildId);
	}

	/**
	 * Returns the patcherBuildIds of the patcher builds associated with the patcher account.
	 *
	 * @param patcherAccountId the patcherAccountId of the patcher account
	 * @return long[] the patcherBuildIds of patcher builds associated with the patcher account
	 */
	public static long[] getPatcherBuildPrimaryKeys(long patcherAccountId) {
		return getService().getPatcherBuildPrimaryKeys(patcherAccountId);
	}

	/**
	 * @throws PortalException
	 */
	public static PersistedModel getPersistedModel(Serializable primaryKeyObj)
		throws PortalException {

		return getService().getPersistedModel(primaryKeyObj);
	}

	public static boolean hasPatcherBuildPatcherAccount(
		long patcherBuildId, long patcherAccountId) {

		return getService().hasPatcherBuildPatcherAccount(
			patcherBuildId, patcherAccountId);
	}

	public static boolean hasPatcherBuildPatcherAccounts(long patcherBuildId) {
		return getService().hasPatcherBuildPatcherAccounts(patcherBuildId);
	}

	public static void setPatcherBuildPatcherAccounts(
		long patcherBuildId, long[] patcherAccountIds) {

		getService().setPatcherBuildPatcherAccounts(
			patcherBuildId, patcherAccountIds);
	}

	/**
	 * Updates the patcher account in the database or adds it if it does not yet exist. Also notifies the appropriate model listeners.
	 *
	 * <p>
	 * <strong>Important:</strong> Inspect PatcherAccountLocalServiceImpl for overloaded versions of the method. If provided, use these entry points to the API, as the implementation logic may require the additional parameters defined there.
	 * </p>
	 *
	 * @param patcherAccount the patcher account
	 * @return the patcher account that was updated
	 */
	public static PatcherAccount updatePatcherAccount(
		PatcherAccount patcherAccount) {

		return getService().updatePatcherAccount(patcherAccount);
	}

	public static PatcherAccountLocalService getService() {
		return _serviceSnapshot.get();
	}

	private static final Snapshot<PatcherAccountLocalService> _serviceSnapshot =
		new Snapshot<>(
			PatcherAccountLocalServiceUtil.class,
			PatcherAccountLocalService.class);

}