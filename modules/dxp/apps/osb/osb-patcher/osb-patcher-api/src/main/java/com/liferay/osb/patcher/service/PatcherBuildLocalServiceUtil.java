/**
 * SPDX-FileCopyrightText: (c) 2025 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

package com.liferay.osb.patcher.service;

import com.liferay.osb.patcher.model.PatcherBuild;
import com.liferay.petra.sql.dsl.query.DSLQuery;
import com.liferay.portal.kernel.dao.orm.DynamicQuery;
import com.liferay.portal.kernel.exception.PortalException;
import com.liferay.portal.kernel.model.PersistedModel;
import com.liferay.portal.kernel.module.service.Snapshot;
import com.liferay.portal.kernel.util.OrderByComparator;

import java.io.Serializable;

import java.util.List;

/**
 * Provides the local service utility for PatcherBuild. This utility wraps
 * <code>com.liferay.osb.patcher.service.impl.PatcherBuildLocalServiceImpl</code> and
 * is an access point for service operations in application layer code running
 * on the local server. Methods of this service will not have security checks
 * based on the propagated JAAS credentials because this service can only be
 * accessed from within the same VM.
 *
 * @author Brian Wing Shun Chan
 * @see PatcherBuildLocalService
 * @generated
 */
public class PatcherBuildLocalServiceUtil {

	/*
	 * NOTE FOR DEVELOPERS:
	 *
	 * Never modify this class directly. Add custom service methods to <code>com.liferay.osb.patcher.service.impl.PatcherBuildLocalServiceImpl</code> and rerun ServiceBuilder to regenerate this class.
	 */
	public static boolean addPatcherAccountPatcherBuild(
		long patcherAccountId, long patcherBuildId) {

		return getService().addPatcherAccountPatcherBuild(
			patcherAccountId, patcherBuildId);
	}

	public static boolean addPatcherAccountPatcherBuild(
		long patcherAccountId, PatcherBuild patcherBuild) {

		return getService().addPatcherAccountPatcherBuild(
			patcherAccountId, patcherBuild);
	}

	public static boolean addPatcherAccountPatcherBuilds(
		long patcherAccountId, List<PatcherBuild> patcherBuilds) {

		return getService().addPatcherAccountPatcherBuilds(
			patcherAccountId, patcherBuilds);
	}

	public static boolean addPatcherAccountPatcherBuilds(
		long patcherAccountId, long[] patcherBuildIds) {

		return getService().addPatcherAccountPatcherBuilds(
			patcherAccountId, patcherBuildIds);
	}

	/**
	 * Adds the patcher build to the database. Also notifies the appropriate model listeners.
	 *
	 * <p>
	 * <strong>Important:</strong> Inspect PatcherBuildLocalServiceImpl for overloaded versions of the method. If provided, use these entry points to the API, as the implementation logic may require the additional parameters defined there.
	 * </p>
	 *
	 * @param patcherBuild the patcher build
	 * @return the patcher build that was added
	 */
	public static PatcherBuild addPatcherBuild(PatcherBuild patcherBuild) {
		return getService().addPatcherBuild(patcherBuild);
	}

	public static boolean addPatcherFixPatcherBuild(
		long patcherFixId, long patcherBuildId) {

		return getService().addPatcherFixPatcherBuild(
			patcherFixId, patcherBuildId);
	}

	public static boolean addPatcherFixPatcherBuild(
		long patcherFixId, PatcherBuild patcherBuild) {

		return getService().addPatcherFixPatcherBuild(
			patcherFixId, patcherBuild);
	}

	public static boolean addPatcherFixPatcherBuilds(
		long patcherFixId, List<PatcherBuild> patcherBuilds) {

		return getService().addPatcherFixPatcherBuilds(
			patcherFixId, patcherBuilds);
	}

	public static boolean addPatcherFixPatcherBuilds(
		long patcherFixId, long[] patcherBuildIds) {

		return getService().addPatcherFixPatcherBuilds(
			patcherFixId, patcherBuildIds);
	}

	public static void clearPatcherAccountPatcherBuilds(long patcherAccountId) {
		getService().clearPatcherAccountPatcherBuilds(patcherAccountId);
	}

	public static void clearPatcherFixPatcherBuilds(long patcherFixId) {
		getService().clearPatcherFixPatcherBuilds(patcherFixId);
	}

	/**
	 * Creates a new patcher build with the primary key. Does not add the patcher build to the database.
	 *
	 * @param patcherBuildId the primary key for the new patcher build
	 * @return the new patcher build
	 */
	public static PatcherBuild createPatcherBuild(long patcherBuildId) {
		return getService().createPatcherBuild(patcherBuildId);
	}

	/**
	 * @throws PortalException
	 */
	public static PersistedModel createPersistedModel(
			Serializable primaryKeyObj)
		throws PortalException {

		return getService().createPersistedModel(primaryKeyObj);
	}

	public static void deletePatcherAccountPatcherBuild(
		long patcherAccountId, long patcherBuildId) {

		getService().deletePatcherAccountPatcherBuild(
			patcherAccountId, patcherBuildId);
	}

	public static void deletePatcherAccountPatcherBuild(
		long patcherAccountId, PatcherBuild patcherBuild) {

		getService().deletePatcherAccountPatcherBuild(
			patcherAccountId, patcherBuild);
	}

	public static void deletePatcherAccountPatcherBuilds(
		long patcherAccountId, List<PatcherBuild> patcherBuilds) {

		getService().deletePatcherAccountPatcherBuilds(
			patcherAccountId, patcherBuilds);
	}

	public static void deletePatcherAccountPatcherBuilds(
		long patcherAccountId, long[] patcherBuildIds) {

		getService().deletePatcherAccountPatcherBuilds(
			patcherAccountId, patcherBuildIds);
	}

	/**
	 * Deletes the patcher build with the primary key from the database. Also notifies the appropriate model listeners.
	 *
	 * <p>
	 * <strong>Important:</strong> Inspect PatcherBuildLocalServiceImpl for overloaded versions of the method. If provided, use these entry points to the API, as the implementation logic may require the additional parameters defined there.
	 * </p>
	 *
	 * @param patcherBuildId the primary key of the patcher build
	 * @return the patcher build that was removed
	 * @throws PortalException if a patcher build with the primary key could not be found
	 */
	public static PatcherBuild deletePatcherBuild(long patcherBuildId)
		throws PortalException {

		return getService().deletePatcherBuild(patcherBuildId);
	}

	/**
	 * Deletes the patcher build from the database. Also notifies the appropriate model listeners.
	 *
	 * <p>
	 * <strong>Important:</strong> Inspect PatcherBuildLocalServiceImpl for overloaded versions of the method. If provided, use these entry points to the API, as the implementation logic may require the additional parameters defined there.
	 * </p>
	 *
	 * @param patcherBuild the patcher build
	 * @return the patcher build that was removed
	 */
	public static PatcherBuild deletePatcherBuild(PatcherBuild patcherBuild) {
		return getService().deletePatcherBuild(patcherBuild);
	}

	public static void deletePatcherFixPatcherBuild(
		long patcherFixId, long patcherBuildId) {

		getService().deletePatcherFixPatcherBuild(patcherFixId, patcherBuildId);
	}

	public static void deletePatcherFixPatcherBuild(
		long patcherFixId, PatcherBuild patcherBuild) {

		getService().deletePatcherFixPatcherBuild(patcherFixId, patcherBuild);
	}

	public static void deletePatcherFixPatcherBuilds(
		long patcherFixId, List<PatcherBuild> patcherBuilds) {

		getService().deletePatcherFixPatcherBuilds(patcherFixId, patcherBuilds);
	}

	public static void deletePatcherFixPatcherBuilds(
		long patcherFixId, long[] patcherBuildIds) {

		getService().deletePatcherFixPatcherBuilds(
			patcherFixId, patcherBuildIds);
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
	 * Useful when paginating results. Returns a maximum of <code>end - start</code> instances. <code>start</code> and <code>end</code> are not primary keys, they are indexes in the result set. Thus, <code>0</code> refers to the first result in the set. Setting both <code>start</code> and <code>end</code> to <code>com.liferay.portal.kernel.dao.orm.QueryUtil#ALL_POS</code> will return the full result set. If <code>orderByComparator</code> is specified, then the query will include the given ORDER BY logic. If <code>orderByComparator</code> is absent, then the query will include the default ORDER BY logic from <code>com.liferay.osb.patcher.model.impl.PatcherBuildModelImpl</code>.
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
	 * Useful when paginating results. Returns a maximum of <code>end - start</code> instances. <code>start</code> and <code>end</code> are not primary keys, they are indexes in the result set. Thus, <code>0</code> refers to the first result in the set. Setting both <code>start</code> and <code>end</code> to <code>com.liferay.portal.kernel.dao.orm.QueryUtil#ALL_POS</code> will return the full result set. If <code>orderByComparator</code> is specified, then the query will include the given ORDER BY logic. If <code>orderByComparator</code> is absent, then the query will include the default ORDER BY logic from <code>com.liferay.osb.patcher.model.impl.PatcherBuildModelImpl</code>.
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

	public static PatcherBuild fetchPatcherBuild(long patcherBuildId) {
		return getService().fetchPatcherBuild(patcherBuildId);
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

	public static List<PatcherBuild> getPatcherAccountPatcherBuilds(
		long patcherAccountId) {

		return getService().getPatcherAccountPatcherBuilds(patcherAccountId);
	}

	public static List<PatcherBuild> getPatcherAccountPatcherBuilds(
		long patcherAccountId, int start, int end) {

		return getService().getPatcherAccountPatcherBuilds(
			patcherAccountId, start, end);
	}

	public static List<PatcherBuild> getPatcherAccountPatcherBuilds(
		long patcherAccountId, int start, int end,
		OrderByComparator<PatcherBuild> orderByComparator) {

		return getService().getPatcherAccountPatcherBuilds(
			patcherAccountId, start, end, orderByComparator);
	}

	public static int getPatcherAccountPatcherBuildsCount(
		long patcherAccountId) {

		return getService().getPatcherAccountPatcherBuildsCount(
			patcherAccountId);
	}

	/**
	 * Returns the patcherAccountIds of the patcher accounts associated with the patcher build.
	 *
	 * @param patcherBuildId the patcherBuildId of the patcher build
	 * @return long[] the patcherAccountIds of patcher accounts associated with the patcher build
	 */
	public static long[] getPatcherAccountPrimaryKeys(long patcherBuildId) {
		return getService().getPatcherAccountPrimaryKeys(patcherBuildId);
	}

	/**
	 * Returns the patcher build with the primary key.
	 *
	 * @param patcherBuildId the primary key of the patcher build
	 * @return the patcher build
	 * @throws PortalException if a patcher build with the primary key could not be found
	 */
	public static PatcherBuild getPatcherBuild(long patcherBuildId)
		throws PortalException {

		return getService().getPatcherBuild(patcherBuildId);
	}

	public static List<PatcherBuild> getPatcherBuilds(
		boolean latestSupportTicketBuild, String supportTicket) {

		return getService().getPatcherBuilds(
			latestSupportTicketBuild, supportTicket);
	}

	public static List<PatcherBuild> getPatcherBuilds(
		java.util.Date modifiedDate, boolean notified, int[] statuses) {

		return getService().getPatcherBuilds(modifiedDate, notified, statuses);
	}

	/**
	 * Returns a range of all the patcher builds.
	 *
	 * <p>
	 * Useful when paginating results. Returns a maximum of <code>end - start</code> instances. <code>start</code> and <code>end</code> are not primary keys, they are indexes in the result set. Thus, <code>0</code> refers to the first result in the set. Setting both <code>start</code> and <code>end</code> to <code>com.liferay.portal.kernel.dao.orm.QueryUtil#ALL_POS</code> will return the full result set. If <code>orderByComparator</code> is specified, then the query will include the given ORDER BY logic. If <code>orderByComparator</code> is absent, then the query will include the default ORDER BY logic from <code>com.liferay.osb.patcher.model.impl.PatcherBuildModelImpl</code>.
	 * </p>
	 *
	 * @param start the lower bound of the range of patcher builds
	 * @param end the upper bound of the range of patcher builds (not inclusive)
	 * @return the range of patcher builds
	 */
	public static List<PatcherBuild> getPatcherBuilds(int start, int end) {
		return getService().getPatcherBuilds(start, end);
	}

	public static List<PatcherBuild> getPatcherBuilds(
		long patcherFixId, boolean childBuild) {

		return getService().getPatcherBuilds(patcherFixId, childBuild);
	}

	public static List<PatcherBuild> getPatcherBuilds(
		long patcherAccountId, long patcherProductVersionId, int start, int end,
		OrderByComparator<PatcherBuild> orderByComparator) {

		return getService().getPatcherBuilds(
			patcherAccountId, patcherProductVersionId, start, end,
			orderByComparator);
	}

	public static List<PatcherBuild> getPatcherBuilds(
		long patcherProjectVersionId, String name, boolean latestKeyBuild,
		String accountEntryCode) {

		return getService().getPatcherBuilds(
			patcherProjectVersionId, name, latestKeyBuild, accountEntryCode);
	}

	public static List<PatcherBuild> getPatcherBuilds(
		String key, boolean latestKeyBuild) {

		return getService().getPatcherBuilds(key, latestKeyBuild);
	}

	public static List<PatcherBuild> getPatcherBuilds(
		String key, int start, int end,
		OrderByComparator<PatcherBuild> orderByComparator) {

		return getService().getPatcherBuilds(
			key, start, end, orderByComparator);
	}

	public static List<PatcherBuild> getPatcherBuildsByKey(
		String key, double keyVersion, boolean older) {

		return getService().getPatcherBuildsByKey(key, keyVersion, older);
	}

	public static List<PatcherBuild> getPatcherBuildsByPatcherFixId(
		long patcherFixId) {

		return getService().getPatcherBuildsByPatcherFixId(patcherFixId);
	}

	public static List<PatcherBuild> getPatcherBuildsByPatcherProjectVersionId(
		long patcherProjectVersionId) {

		return getService().getPatcherBuildsByPatcherProjectVersionId(
			patcherProjectVersionId);
	}

	public static List<PatcherBuild> getPatcherBuildsBySupportTicket(
		String supportTicket, double supportTicketVersion, boolean older) {

		return getService().getPatcherBuildsBySupportTicket(
			supportTicket, supportTicketVersion, older);
	}

	/**
	 * Returns the number of patcher builds.
	 *
	 * @return the number of patcher builds
	 */
	public static int getPatcherBuildsCount() {
		return getService().getPatcherBuildsCount();
	}

	public static int getPatcherBuildsCount(
		long patcherFixId, long patcherProductVersionId, boolean childBuild,
		int type) {

		return getService().getPatcherBuildsCount(
			patcherFixId, patcherProductVersionId, childBuild, type);
	}

	public static int getPatcherBuildsCountByPatcherProjectVersionId(
		long patcherProjectVersionId) {

		return getService().getPatcherBuildsCountByPatcherProjectVersionId(
			patcherProjectVersionId);
	}

	public static List<PatcherBuild> getPatcherFixPatcherBuilds(
		long patcherFixId) {

		return getService().getPatcherFixPatcherBuilds(patcherFixId);
	}

	public static List<PatcherBuild> getPatcherFixPatcherBuilds(
		long patcherFixId, int start, int end) {

		return getService().getPatcherFixPatcherBuilds(
			patcherFixId, start, end);
	}

	public static List<PatcherBuild> getPatcherFixPatcherBuilds(
		long patcherFixId, int start, int end,
		OrderByComparator<PatcherBuild> orderByComparator) {

		return getService().getPatcherFixPatcherBuilds(
			patcherFixId, start, end, orderByComparator);
	}

	public static int getPatcherFixPatcherBuildsCount(long patcherFixId) {
		return getService().getPatcherFixPatcherBuildsCount(patcherFixId);
	}

	/**
	 * Returns the patcherFixIds of the patcher fixes associated with the patcher build.
	 *
	 * @param patcherBuildId the patcherBuildId of the patcher build
	 * @return long[] the patcherFixIds of patcher fixes associated with the patcher build
	 */
	public static long[] getPatcherFixPrimaryKeys(long patcherBuildId) {
		return getService().getPatcherFixPrimaryKeys(patcherBuildId);
	}

	/**
	 * @throws PortalException
	 */
	public static PersistedModel getPersistedModel(Serializable primaryKeyObj)
		throws PortalException {

		return getService().getPersistedModel(primaryKeyObj);
	}

	public static boolean hasPatcherAccountPatcherBuild(
		long patcherAccountId, long patcherBuildId) {

		return getService().hasPatcherAccountPatcherBuild(
			patcherAccountId, patcherBuildId);
	}

	public static boolean hasPatcherAccountPatcherBuilds(
		long patcherAccountId) {

		return getService().hasPatcherAccountPatcherBuilds(patcherAccountId);
	}

	public static boolean hasPatcherFixes(long patcherFixId) {
		return getService().hasPatcherFixes(patcherFixId);
	}

	public static boolean hasPatcherFixPatcherBuild(
		long patcherFixId, long patcherBuildId) {

		return getService().hasPatcherFixPatcherBuild(
			patcherFixId, patcherBuildId);
	}

	public static boolean hasPatcherFixPatcherBuilds(long patcherFixId) {
		return getService().hasPatcherFixPatcherBuilds(patcherFixId);
	}

	public static void setPatcherAccountPatcherBuilds(
		long patcherAccountId, long[] patcherBuildIds) {

		getService().setPatcherAccountPatcherBuilds(
			patcherAccountId, patcherBuildIds);
	}

	public static void setPatcherFixPatcherBuilds(
		long patcherFixId, long[] patcherBuildIds) {

		getService().setPatcherFixPatcherBuilds(patcherFixId, patcherBuildIds);
	}

	public static PatcherBuild updateComments(
			long patcherBuildId, String comments)
		throws PortalException {

		return getService().updateComments(patcherBuildId, comments);
	}

	public static PatcherBuild updateNotified(
			long patcherBuildId, boolean notified)
		throws PortalException {

		return getService().updateNotified(patcherBuildId, notified);
	}

	public static PatcherBuild updatePatcherBuild(
			long patcherBuildId, boolean latestKeyBuild,
			boolean latestSupportTicketBuild)
		throws PortalException {

		return getService().updatePatcherBuild(
			patcherBuildId, latestKeyBuild, latestSupportTicketBuild);
	}

	public static PatcherBuild updatePatcherBuild(
			long userId, long patcherBuildId, int qaStatus,
			String supportTicket, int type)
		throws Exception {

		return getService().updatePatcherBuild(
			userId, patcherBuildId, qaStatus, supportTicket, type);
	}

	public static PatcherBuild updatePatcherBuild(
			long userId, long patcherBuildId, String fileName, int qaStatus,
			String sourceName, int status)
		throws Exception {

		return getService().updatePatcherBuild(
			userId, patcherBuildId, fileName, qaStatus, sourceName, status);
	}

	/**
	 * Updates the patcher build in the database or adds it if it does not yet exist. Also notifies the appropriate model listeners.
	 *
	 * <p>
	 * <strong>Important:</strong> Inspect PatcherBuildLocalServiceImpl for overloaded versions of the method. If provided, use these entry points to the API, as the implementation logic may require the additional parameters defined there.
	 * </p>
	 *
	 * @param patcherBuild the patcher build
	 * @return the patcher build that was updated
	 */
	public static PatcherBuild updatePatcherBuild(PatcherBuild patcherBuild) {
		return getService().updatePatcherBuild(patcherBuild);
	}

	public static PatcherBuild updatePatcherFixId(
			long patcherBuildId, long patcherFixId)
		throws PortalException {

		return getService().updatePatcherFixId(patcherBuildId, patcherFixId);
	}

	public static PatcherBuild updateQaFields(
			long userId, long patcherBuildId, String qaComments, int qaStatus)
		throws Exception {

		return getService().updateQaFields(
			userId, patcherBuildId, qaComments, qaStatus);
	}

	public static PatcherBuild updateQaStatus(
			long userId, long patcherBuildId, int qaStatus)
		throws Exception {

		return getService().updateQaStatus(userId, patcherBuildId, qaStatus);
	}

	public static PatcherBuild updateRequestKey(
			long patcherBuildId, String requestKey)
		throws PortalException {

		return getService().updateRequestKey(patcherBuildId, requestKey);
	}

	public static PatcherBuild updateStatus(
			long userId, long patcherBuildId, int status)
		throws Exception {

		return getService().updateStatus(userId, patcherBuildId, status);
	}

	public static PatcherBuildLocalService getService() {
		return _serviceSnapshot.get();
	}

	private static final Snapshot<PatcherBuildLocalService> _serviceSnapshot =
		new Snapshot<>(
			PatcherBuildLocalServiceUtil.class, PatcherBuildLocalService.class);

}