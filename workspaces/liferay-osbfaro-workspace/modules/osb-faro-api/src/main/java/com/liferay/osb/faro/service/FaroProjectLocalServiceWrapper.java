/**
 * SPDX-FileCopyrightText: (c) 2000 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

package com.liferay.osb.faro.service;

import com.liferay.portal.kernel.service.ServiceWrapper;
import com.liferay.portal.kernel.service.persistence.BasePersistence;

/**
 * Provides a wrapper for {@link FaroProjectLocalService}.
 *
 * @author Matthew Kong
 * @see FaroProjectLocalService
 * @generated
 */
public class FaroProjectLocalServiceWrapper
	implements FaroProjectLocalService,
			   ServiceWrapper<FaroProjectLocalService> {

	public FaroProjectLocalServiceWrapper() {
		this(null);
	}

	public FaroProjectLocalServiceWrapper(
		FaroProjectLocalService faroProjectLocalService) {

		_faroProjectLocalService = faroProjectLocalService;
	}

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
	@Override
	public com.liferay.osb.faro.model.FaroProject addFaroProject(
		com.liferay.osb.faro.model.FaroProject faroProject) {

		return _faroProjectLocalService.addFaroProject(faroProject);
	}

	@Override
	public com.liferay.osb.faro.model.FaroProject addFaroProject(
			long userId, String name, String accountKey, String accountName,
			String corpProjectName, String corpProjectUuid,
			java.util.List<String> emailAddressDomains, String friendlyURL,
			String incidentReportEmailAddresses, String serverLocation,
			String services, String state, String subscription,
			String timeZoneId, String weDeployKey)
		throws com.liferay.portal.kernel.exception.PortalException {

		return _faroProjectLocalService.addFaroProject(
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
	@Override
	public com.liferay.osb.faro.model.FaroProject createFaroProject(
		long faroProjectId) {

		return _faroProjectLocalService.createFaroProject(faroProjectId);
	}

	/**
	 * @throws PortalException
	 */
	@Override
	public com.liferay.portal.kernel.model.PersistedModel createPersistedModel(
			java.io.Serializable primaryKeyObj)
		throws com.liferay.portal.kernel.exception.PortalException {

		return _faroProjectLocalService.createPersistedModel(primaryKeyObj);
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
	@Override
	public com.liferay.osb.faro.model.FaroProject deleteFaroProject(
		com.liferay.osb.faro.model.FaroProject faroProject) {

		return _faroProjectLocalService.deleteFaroProject(faroProject);
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
	@Override
	public com.liferay.osb.faro.model.FaroProject deleteFaroProject(
			long faroProjectId)
		throws com.liferay.portal.kernel.exception.PortalException {

		return _faroProjectLocalService.deleteFaroProject(faroProjectId);
	}

	@Override
	public com.liferay.osb.faro.model.FaroProject deleteFaroProjectByGroupId(
			long groupId)
		throws com.liferay.portal.kernel.exception.PortalException {

		return _faroProjectLocalService.deleteFaroProjectByGroupId(groupId);
	}

	/**
	 * @throws PortalException
	 */
	@Override
	public com.liferay.portal.kernel.model.PersistedModel deletePersistedModel(
			com.liferay.portal.kernel.model.PersistedModel persistedModel)
		throws com.liferay.portal.kernel.exception.PortalException {

		return _faroProjectLocalService.deletePersistedModel(persistedModel);
	}

	@Override
	public <T> T dslQuery(com.liferay.petra.sql.dsl.query.DSLQuery dslQuery) {
		return _faroProjectLocalService.dslQuery(dslQuery);
	}

	@Override
	public int dslQueryCount(
		com.liferay.petra.sql.dsl.query.DSLQuery dslQuery) {

		return _faroProjectLocalService.dslQueryCount(dslQuery);
	}

	@Override
	public com.liferay.portal.kernel.dao.orm.DynamicQuery dynamicQuery() {
		return _faroProjectLocalService.dynamicQuery();
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

		return _faroProjectLocalService.dynamicQuery(dynamicQuery);
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
	@Override
	public <T> java.util.List<T> dynamicQuery(
		com.liferay.portal.kernel.dao.orm.DynamicQuery dynamicQuery, int start,
		int end) {

		return _faroProjectLocalService.dynamicQuery(dynamicQuery, start, end);
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
	@Override
	public <T> java.util.List<T> dynamicQuery(
		com.liferay.portal.kernel.dao.orm.DynamicQuery dynamicQuery, int start,
		int end,
		com.liferay.portal.kernel.util.OrderByComparator<T> orderByComparator) {

		return _faroProjectLocalService.dynamicQuery(
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

		return _faroProjectLocalService.dynamicQueryCount(dynamicQuery);
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

		return _faroProjectLocalService.dynamicQueryCount(
			dynamicQuery, projection);
	}

	@Override
	public com.liferay.osb.faro.model.FaroProject fetchFaroProject(
		long faroProjectId) {

		return _faroProjectLocalService.fetchFaroProject(faroProjectId);
	}

	@Override
	public com.liferay.osb.faro.model.FaroProject
		fetchFaroProjectByCorpProjectUuid(String corpProjectUuid) {

		return _faroProjectLocalService.fetchFaroProjectByCorpProjectUuid(
			corpProjectUuid);
	}

	@Override
	public com.liferay.osb.faro.model.FaroProject fetchFaroProjectByGroupId(
		long groupId) {

		return _faroProjectLocalService.fetchFaroProjectByGroupId(groupId);
	}

	@Override
	public com.liferay.osb.faro.model.FaroProject fetchFaroProjectByWeDeployKey(
		String weDeployKey) {

		return _faroProjectLocalService.fetchFaroProjectByWeDeployKey(
			weDeployKey);
	}

	@Override
	public com.liferay.portal.kernel.dao.orm.ActionableDynamicQuery
		getActionableDynamicQuery() {

		return _faroProjectLocalService.getActionableDynamicQuery();
	}

	/**
	 * Returns the faro project with the primary key.
	 *
	 * @param faroProjectId the primary key of the faro project
	 * @return the faro project
	 * @throws PortalException if a faro project with the primary key could not be found
	 */
	@Override
	public com.liferay.osb.faro.model.FaroProject getFaroProject(
			long faroProjectId)
		throws com.liferay.portal.kernel.exception.PortalException {

		return _faroProjectLocalService.getFaroProject(faroProjectId);
	}

	@Override
	public com.liferay.osb.faro.model.FaroProject getFaroProjectByGroupId(
			long groupId)
		throws com.liferay.portal.kernel.exception.PortalException {

		return _faroProjectLocalService.getFaroProjectByGroupId(groupId);
	}

	@Override
	public com.liferay.osb.faro.model.FaroProject getFaroProjectByWeDeployKey(
			String weDeployKey)
		throws com.liferay.portal.kernel.exception.PortalException {

		return _faroProjectLocalService.getFaroProjectByWeDeployKey(
			weDeployKey);
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
	@Override
	public java.util.List<com.liferay.osb.faro.model.FaroProject>
		getFaroProjects(int start, int end) {

		return _faroProjectLocalService.getFaroProjects(start, end);
	}

	@Override
	public java.util.List<com.liferay.osb.faro.model.FaroProject>
		getFaroProjects(String serverLocation) {

		return _faroProjectLocalService.getFaroProjects(serverLocation);
	}

	@Override
	public java.util.List<com.liferay.osb.faro.model.FaroProject>
		getFaroProjectsByEmailAddressDomain(String emailAddressDomains) {

		return _faroProjectLocalService.getFaroProjectsByEmailAddressDomain(
			emailAddressDomains);
	}

	@Override
	public java.util.List<com.liferay.osb.faro.model.FaroProject>
		getFaroProjectsByUserId(long userId) {

		return _faroProjectLocalService.getFaroProjectsByUserId(userId);
	}

	/**
	 * Returns the number of faro projects.
	 *
	 * @return the number of faro projects
	 */
	@Override
	public int getFaroProjectsCount() {
		return _faroProjectLocalService.getFaroProjectsCount();
	}

	@Override
	public com.liferay.portal.kernel.dao.orm.IndexableActionableDynamicQuery
		getIndexableActionableDynamicQuery() {

		return _faroProjectLocalService.getIndexableActionableDynamicQuery();
	}

	@Override
	public java.util.List<com.liferay.osb.faro.model.FaroProject>
			getJoinableFaroProjects(com.liferay.portal.kernel.model.User user)
		throws com.liferay.portal.kernel.exception.PortalException {

		return _faroProjectLocalService.getJoinableFaroProjects(user);
	}

	/**
	 * Returns the OSGi service identifier.
	 *
	 * @return the OSGi service identifier
	 */
	@Override
	public String getOSGiServiceIdentifier() {
		return _faroProjectLocalService.getOSGiServiceIdentifier();
	}

	/**
	 * @throws PortalException
	 */
	@Override
	public com.liferay.portal.kernel.model.PersistedModel getPersistedModel(
			java.io.Serializable primaryKeyObj)
		throws com.liferay.portal.kernel.exception.PortalException {

		return _faroProjectLocalService.getPersistedModel(primaryKeyObj);
	}

	@Override
	public void sendCreatedWorkspaceEmail(String weDeployKey) throws Exception {
		_faroProjectLocalService.sendCreatedWorkspaceEmail(weDeployKey);
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
	@Override
	public com.liferay.osb.faro.model.FaroProject updateFaroProject(
		com.liferay.osb.faro.model.FaroProject faroProject) {

		return _faroProjectLocalService.updateFaroProject(faroProject);
	}

	@Override
	public com.liferay.osb.faro.model.FaroProject updateSalesforceAccountId(
		long faroProjectId, String salesforceAccountId) {

		return _faroProjectLocalService.updateSalesforceAccountId(
			faroProjectId, salesforceAccountId);
	}

	@Override
	public com.liferay.osb.faro.model.FaroProject updateState(
		long faroProjectId, String state) {

		return _faroProjectLocalService.updateState(faroProjectId, state);
	}

	@Override
	public com.liferay.osb.faro.model.FaroProject updateSubscription(
		long faroProjectId, String subscription) {

		return _faroProjectLocalService.updateSubscription(
			faroProjectId, subscription);
	}

	@Override
	public BasePersistence<?> getBasePersistence() {
		return _faroProjectLocalService.getBasePersistence();
	}

	@Override
	public FaroProjectLocalService getWrappedService() {
		return _faroProjectLocalService;
	}

	@Override
	public void setWrappedService(
		FaroProjectLocalService faroProjectLocalService) {

		_faroProjectLocalService = faroProjectLocalService;
	}

	private FaroProjectLocalService _faroProjectLocalService;

}
// LIFERAY-SERVICE-BUILDER-HASH:1622161548