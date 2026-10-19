/**
 * SPDX-FileCopyrightText: (c) 2000 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

package com.liferay.osb.faro.service;

import com.liferay.osb.faro.model.FaroProject;
import com.liferay.petra.sql.dsl.query.DSLQuery;
import com.liferay.portal.kernel.dao.orm.DynamicQuery;
import com.liferay.portal.kernel.exception.PortalException;
import com.liferay.portal.kernel.model.PersistedModel;
import com.liferay.portal.kernel.module.service.Snapshot;
import com.liferay.portal.kernel.util.OrderByComparator;

import java.io.Serializable;

import java.util.List;

/**
 * Provides the local service utility for FaroProject. This utility wraps
 * <code>com.liferay.osb.faro.service.impl.FaroProjectLocalServiceImpl</code> and
 * is an access point for service operations in application layer code running
 * on the local server. Methods of this service will not have security checks
 * based on the propagated JAAS credentials because this service can only be
 * accessed from within the same VM.
 *
 * @author Matthew Kong
 * @see FaroProjectLocalService
 * @generated
 */
public class FaroProjectLocalServiceUtil {

	/*
	 * NOTE FOR DEVELOPERS:
	 *
	 * Never modify this class directly. Add custom service methods to <code>com.liferay.osb.faro.service.impl.FaroProjectLocalServiceImpl</code> and rerun ServiceBuilder to regenerate this class.
	 */

	/**
	 * Adds the faro project to the database. Also notifies the appropriate model listeners.
	 *
	 * <p>
	 * <strong>Important:</strong> Inspect FaroProjectLocalServiceImpl for overloaded versions of the method. If provided, use these entry points to the API, as the implementation logic may require the additional parameters defined there.
	 * </p>
	 *
	 * @param faroProject the faro project
	 * @return the faro project that was added
	 */
	public static FaroProject addFaroProject(FaroProject faroProject) {
		return getService().addFaroProject(faroProject);
	}

	public static FaroProject addFaroProject(
			long userId, String name, String accountKey, String accountName,
			String corpProjectName, String corpProjectUuid,
			List<String> emailAddressDomains, String friendlyURL,
			String incidentReportEmailAddresses, String serverLocation,
			String services, String state, String subscription,
			String timeZoneId, String weDeployKey)
		throws PortalException {

		return getService().addFaroProject(
			userId, name, accountKey, accountName, corpProjectName,
			corpProjectUuid, emailAddressDomains, friendlyURL,
			incidentReportEmailAddresses, serverLocation, services, state,
			subscription, timeZoneId, weDeployKey);
	}

	/**
	 * Creates a new faro project with the primary key. Does not add the faro project to the database.
	 *
	 * @param faroProjectId the primary key for the new faro project
	 * @return the new faro project
	 */
	public static FaroProject createFaroProject(long faroProjectId) {
		return getService().createFaroProject(faroProjectId);
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
	 * Deletes the faro project from the database. Also notifies the appropriate model listeners.
	 *
	 * <p>
	 * <strong>Important:</strong> Inspect FaroProjectLocalServiceImpl for overloaded versions of the method. If provided, use these entry points to the API, as the implementation logic may require the additional parameters defined there.
	 * </p>
	 *
	 * @param faroProject the faro project
	 * @return the faro project that was removed
	 */
	public static FaroProject deleteFaroProject(FaroProject faroProject) {
		return getService().deleteFaroProject(faroProject);
	}

	/**
	 * Deletes the faro project with the primary key from the database. Also notifies the appropriate model listeners.
	 *
	 * <p>
	 * <strong>Important:</strong> Inspect FaroProjectLocalServiceImpl for overloaded versions of the method. If provided, use these entry points to the API, as the implementation logic may require the additional parameters defined there.
	 * </p>
	 *
	 * @param faroProjectId the primary key of the faro project
	 * @return the faro project that was removed
	 * @throws PortalException if a faro project with the primary key could not be found
	 */
	public static FaroProject deleteFaroProject(long faroProjectId)
		throws PortalException {

		return getService().deleteFaroProject(faroProjectId);
	}

	public static FaroProject deleteFaroProjectByGroupId(long groupId)
		throws PortalException {

		return getService().deleteFaroProjectByGroupId(groupId);
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
	 * Useful when paginating results. Returns a maximum of <code>end - start</code> instances. <code>start</code> and <code>end</code> are not primary keys, they are indexes in the result set. Thus, <code>0</code> refers to the first result in the set. Setting both <code>start</code> and <code>end</code> to <code>com.liferay.portal.kernel.dao.orm.QueryUtil#ALL_POS</code> will return the full result set. If <code>orderByComparator</code> is specified, then the query will include the given ORDER BY logic. If <code>orderByComparator</code> is absent, then the query will include the default ORDER BY logic from <code>com.liferay.osb.faro.model.impl.FaroProjectModelImpl</code>.
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
	 * Useful when paginating results. Returns a maximum of <code>end - start</code> instances. <code>start</code> and <code>end</code> are not primary keys, they are indexes in the result set. Thus, <code>0</code> refers to the first result in the set. Setting both <code>start</code> and <code>end</code> to <code>com.liferay.portal.kernel.dao.orm.QueryUtil#ALL_POS</code> will return the full result set. If <code>orderByComparator</code> is specified, then the query will include the given ORDER BY logic. If <code>orderByComparator</code> is absent, then the query will include the default ORDER BY logic from <code>com.liferay.osb.faro.model.impl.FaroProjectModelImpl</code>.
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

	public static FaroProject fetchFaroProject(long faroProjectId) {
		return getService().fetchFaroProject(faroProjectId);
	}

	public static FaroProject fetchFaroProjectByCorpProjectUuid(
		String corpProjectUuid) {

		return getService().fetchFaroProjectByCorpProjectUuid(corpProjectUuid);
	}

	public static FaroProject fetchFaroProjectByGroupId(long groupId) {
		return getService().fetchFaroProjectByGroupId(groupId);
	}

	public static FaroProject fetchFaroProjectByWeDeployKey(
		String weDeployKey) {

		return getService().fetchFaroProjectByWeDeployKey(weDeployKey);
	}

	public static com.liferay.portal.kernel.dao.orm.ActionableDynamicQuery
		getActionableDynamicQuery() {

		return getService().getActionableDynamicQuery();
	}

	/**
	 * Returns the faro project with the primary key.
	 *
	 * @param faroProjectId the primary key of the faro project
	 * @return the faro project
	 * @throws PortalException if a faro project with the primary key could not be found
	 */
	public static FaroProject getFaroProject(long faroProjectId)
		throws PortalException {

		return getService().getFaroProject(faroProjectId);
	}

	public static FaroProject getFaroProjectByGroupId(long groupId)
		throws PortalException {

		return getService().getFaroProjectByGroupId(groupId);
	}

	public static FaroProject getFaroProjectByWeDeployKey(String weDeployKey)
		throws PortalException {

		return getService().getFaroProjectByWeDeployKey(weDeployKey);
	}

	/**
	 * Returns a range of all the faro projects.
	 *
	 * <p>
	 * Useful when paginating results. Returns a maximum of <code>end - start</code> instances. <code>start</code> and <code>end</code> are not primary keys, they are indexes in the result set. Thus, <code>0</code> refers to the first result in the set. Setting both <code>start</code> and <code>end</code> to <code>com.liferay.portal.kernel.dao.orm.QueryUtil#ALL_POS</code> will return the full result set. If <code>orderByComparator</code> is specified, then the query will include the given ORDER BY logic. If <code>orderByComparator</code> is absent, then the query will include the default ORDER BY logic from <code>com.liferay.osb.faro.model.impl.FaroProjectModelImpl</code>.
	 * </p>
	 *
	 * @param start the lower bound of the range of faro projects
	 * @param end the upper bound of the range of faro projects (not inclusive)
	 * @return the range of faro projects
	 */
	public static List<FaroProject> getFaroProjects(int start, int end) {
		return getService().getFaroProjects(start, end);
	}

	public static List<FaroProject> getFaroProjects(String serverLocation) {
		return getService().getFaroProjects(serverLocation);
	}

	public static List<FaroProject> getFaroProjectsByEmailAddressDomain(
		String emailAddressDomains) {

		return getService().getFaroProjectsByEmailAddressDomain(
			emailAddressDomains);
	}

	public static List<FaroProject> getFaroProjectsByUserId(long userId) {
		return getService().getFaroProjectsByUserId(userId);
	}

	/**
	 * Returns the number of faro projects.
	 *
	 * @return the number of faro projects
	 */
	public static int getFaroProjectsCount() {
		return getService().getFaroProjectsCount();
	}

	public static
		com.liferay.portal.kernel.dao.orm.IndexableActionableDynamicQuery
			getIndexableActionableDynamicQuery() {

		return getService().getIndexableActionableDynamicQuery();
	}

	public static List<FaroProject> getJoinableFaroProjects(
			com.liferay.portal.kernel.model.User user)
		throws PortalException {

		return getService().getJoinableFaroProjects(user);
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

	public static void sendCreatedWorkspaceEmail(String weDeployKey)
		throws Exception {

		getService().sendCreatedWorkspaceEmail(weDeployKey);
	}

	/**
	 * Updates the faro project in the database or adds it if it does not yet exist. Also notifies the appropriate model listeners.
	 *
	 * <p>
	 * <strong>Important:</strong> Inspect FaroProjectLocalServiceImpl for overloaded versions of the method. If provided, use these entry points to the API, as the implementation logic may require the additional parameters defined there.
	 * </p>
	 *
	 * @param faroProject the faro project
	 * @return the faro project that was updated
	 */
	public static FaroProject updateFaroProject(FaroProject faroProject) {
		return getService().updateFaroProject(faroProject);
	}

	public static FaroProject updateSalesforceAccountId(
		long faroProjectId, String salesforceAccountId) {

		return getService().updateSalesforceAccountId(
			faroProjectId, salesforceAccountId);
	}

	public static FaroProject updateState(long faroProjectId, String state) {
		return getService().updateState(faroProjectId, state);
	}

	public static FaroProject updateSubscription(
		long faroProjectId, String subscription) {

		return getService().updateSubscription(faroProjectId, subscription);
	}

	public static FaroProjectLocalService getService() {
		return _serviceSnapshot.get();
	}

	private static final Snapshot<FaroProjectLocalService> _serviceSnapshot =
		new Snapshot<>(
			FaroProjectLocalServiceUtil.class, FaroProjectLocalService.class);

}
// LIFERAY-SERVICE-BUILDER-HASH:-1714361864