/**
 * SPDX-FileCopyrightText: (c) 2000 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

package com.liferay.portal.kernel.service;

import com.liferay.petra.function.UnsafeFunction;
import com.liferay.portal.kernel.model.Repository;
import com.liferay.portal.kernel.service.persistence.BasePersistence;
import com.liferay.portal.kernel.service.persistence.change.tracking.CTPersistence;

/**
 * Provides a wrapper for {@link RepositoryLocalService}.
 *
 * @author Brian Wing Shun Chan
 * @see RepositoryLocalService
 * @generated
 */
public class RepositoryLocalServiceWrapper
	implements RepositoryLocalService, ServiceWrapper<RepositoryLocalService> {

	public RepositoryLocalServiceWrapper() {
		this(null);
	}

	public RepositoryLocalServiceWrapper(
		RepositoryLocalService repositoryLocalService) {

		_repositoryLocalService = repositoryLocalService;
	}

	/**
	 * Adds the repository to the database. Also notifies the appropriate model listeners.
	 *
	 * <p>
	 * <strong>Important:</strong> Inspect RepositoryLocalServiceImpl for overloaded versions of the method. If provided, use these entry points to the API, as the implementation logic may require the additional parameters defined there.
	 * </p>
	 *
	 * @param repository the repository
	 * @return the repository that was added
	 */
	@Override
	public Repository addRepository(Repository repository) {
		return _repositoryLocalService.addRepository(repository);
	}

	@Override
	public Repository addRepository(
			String externalReferenceCode, long userId, long groupId,
			long classNameId, long parentFolderId, String name,
			String description, String portletId,
			com.liferay.portal.kernel.util.UnicodeProperties
				typeSettingsUnicodeProperties,
			boolean hidden, ServiceContext serviceContext)
		throws com.liferay.portal.kernel.exception.PortalException {

		return _repositoryLocalService.addRepository(
			externalReferenceCode, userId, groupId, classNameId, parentFolderId,
			name, description, portletId, typeSettingsUnicodeProperties, hidden,
			serviceContext);
	}

	@Override
	public void checkRepository(long repositoryId) {
		_repositoryLocalService.checkRepository(repositoryId);
	}

	/**
	 * @throws PortalException
	 */
	@Override
	public com.liferay.portal.kernel.model.PersistedModel createPersistedModel(
			java.io.Serializable primaryKeyObj)
		throws com.liferay.portal.kernel.exception.PortalException {

		return _repositoryLocalService.createPersistedModel(primaryKeyObj);
	}

	/**
	 * Creates a new repository with the primary key. Does not add the repository to the database.
	 *
	 * @param repositoryId the primary key for the new repository
	 * @return the new repository
	 */
	@Override
	public Repository createRepository(long repositoryId) {
		return _repositoryLocalService.createRepository(repositoryId);
	}

	/**
	 * @throws PortalException
	 */
	@Override
	public com.liferay.portal.kernel.model.PersistedModel deletePersistedModel(
			com.liferay.portal.kernel.model.PersistedModel persistedModel)
		throws com.liferay.portal.kernel.exception.PortalException {

		return _repositoryLocalService.deletePersistedModel(persistedModel);
	}

	@Override
	public void deleteRepositories(long groupId)
		throws com.liferay.portal.kernel.exception.PortalException {

		_repositoryLocalService.deleteRepositories(groupId);
	}

	/**
	 * Deletes the repository with the primary key from the database. Also notifies the appropriate model listeners.
	 *
	 * <p>
	 * <strong>Important:</strong> Inspect RepositoryLocalServiceImpl for overloaded versions of the method. If provided, use these entry points to the API, as the implementation logic may require the additional parameters defined there.
	 * </p>
	 *
	 * @param repositoryId the primary key of the repository
	 * @return the repository that was removed
	 * @throws PortalException if a repository with the primary key could not be found
	 */
	@Override
	public Repository deleteRepository(long repositoryId)
		throws com.liferay.portal.kernel.exception.PortalException {

		return _repositoryLocalService.deleteRepository(repositoryId);
	}

	/**
	 * Deletes the repository from the database. Also notifies the appropriate model listeners.
	 *
	 * <p>
	 * <strong>Important:</strong> Inspect RepositoryLocalServiceImpl for overloaded versions of the method. If provided, use these entry points to the API, as the implementation logic may require the additional parameters defined there.
	 * </p>
	 *
	 * @param repository the repository
	 * @return the repository that was removed
	 * @throws PortalException
	 */
	@Override
	public Repository deleteRepository(Repository repository)
		throws com.liferay.portal.kernel.exception.PortalException {

		return _repositoryLocalService.deleteRepository(repository);
	}

	@Override
	public <T> T dslQuery(com.liferay.petra.sql.dsl.query.DSLQuery dslQuery) {
		return _repositoryLocalService.dslQuery(dslQuery);
	}

	@Override
	public int dslQueryCount(
		com.liferay.petra.sql.dsl.query.DSLQuery dslQuery) {

		return _repositoryLocalService.dslQueryCount(dslQuery);
	}

	@Override
	public com.liferay.portal.kernel.dao.orm.DynamicQuery dynamicQuery() {
		return _repositoryLocalService.dynamicQuery();
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

		return _repositoryLocalService.dynamicQuery(dynamicQuery);
	}

	/**
	 * Performs a dynamic query on the database and returns a range of the matching rows.
	 *
	 * <p>
	 * Useful when paginating results. Returns a maximum of <code>end - start</code> instances. <code>start</code> and <code>end</code> are not primary keys, they are indexes in the result set. Thus, <code>0</code> refers to the first result in the set. Setting both <code>start</code> and <code>end</code> to <code>com.liferay.portal.kernel.dao.orm.QueryUtil#ALL_POS</code> will return the full result set. If <code>orderByComparator</code> is specified, then the query will include the given ORDER BY logic. If <code>orderByComparator</code> is absent, then the query will include the default ORDER BY logic from <code>com.liferay.portal.model.impl.RepositoryModelImpl</code>.
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

		return _repositoryLocalService.dynamicQuery(dynamicQuery, start, end);
	}

	/**
	 * Performs a dynamic query on the database and returns an ordered range of the matching rows.
	 *
	 * <p>
	 * Useful when paginating results. Returns a maximum of <code>end - start</code> instances. <code>start</code> and <code>end</code> are not primary keys, they are indexes in the result set. Thus, <code>0</code> refers to the first result in the set. Setting both <code>start</code> and <code>end</code> to <code>com.liferay.portal.kernel.dao.orm.QueryUtil#ALL_POS</code> will return the full result set. If <code>orderByComparator</code> is specified, then the query will include the given ORDER BY logic. If <code>orderByComparator</code> is absent, then the query will include the default ORDER BY logic from <code>com.liferay.portal.model.impl.RepositoryModelImpl</code>.
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

		return _repositoryLocalService.dynamicQuery(
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

		return _repositoryLocalService.dynamicQueryCount(dynamicQuery);
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

		return _repositoryLocalService.dynamicQueryCount(
			dynamicQuery, projection);
	}

	@Override
	public Repository fetchRepository(long repositoryId) {
		return _repositoryLocalService.fetchRepository(repositoryId);
	}

	@Override
	public Repository fetchRepository(long groupId, String portletId) {
		return _repositoryLocalService.fetchRepository(groupId, portletId);
	}

	@Override
	public Repository fetchRepository(
		long groupId, String name, String portletId) {

		return _repositoryLocalService.fetchRepository(
			groupId, name, portletId);
	}

	@Override
	public Repository fetchRepositoryByExternalReferenceCode(
		String externalReferenceCode, long groupId) {

		return _repositoryLocalService.fetchRepositoryByExternalReferenceCode(
			externalReferenceCode, groupId);
	}

	/**
	 * Returns the repository matching the UUID and group.
	 *
	 * @param uuid the repository's UUID
	 * @param groupId the primary key of the group
	 * @return the matching repository, or <code>null</code> if a matching repository could not be found
	 */
	@Override
	public Repository fetchRepositoryByUuidAndGroupId(
		String uuid, long groupId) {

		return _repositoryLocalService.fetchRepositoryByUuidAndGroupId(
			uuid, groupId);
	}

	@Override
	public com.liferay.portal.kernel.dao.orm.ActionableDynamicQuery
		getActionableDynamicQuery() {

		return _repositoryLocalService.getActionableDynamicQuery();
	}

	@Override
	public com.liferay.portal.kernel.dao.orm.ExportActionableDynamicQuery
		getExportActionableDynamicQuery(
			com.liferay.exportimport.kernel.lar.PortletDataContext
				portletDataContext) {

		return _repositoryLocalService.getExportActionableDynamicQuery(
			portletDataContext);
	}

	@Override
	public java.util.List<Repository> getGroupRepositories(long groupId) {
		return _repositoryLocalService.getGroupRepositories(groupId);
	}

	@Override
	public com.liferay.portal.kernel.dao.orm.IndexableActionableDynamicQuery
		getIndexableActionableDynamicQuery() {

		return _repositoryLocalService.getIndexableActionableDynamicQuery();
	}

	/**
	 * Returns the OSGi service identifier.
	 *
	 * @return the OSGi service identifier
	 */
	@Override
	public String getOSGiServiceIdentifier() {
		return _repositoryLocalService.getOSGiServiceIdentifier();
	}

	/**
	 * @throws PortalException
	 */
	@Override
	public com.liferay.portal.kernel.model.PersistedModel getPersistedModel(
			java.io.Serializable primaryKeyObj)
		throws com.liferay.portal.kernel.exception.PortalException {

		return _repositoryLocalService.getPersistedModel(primaryKeyObj);
	}

	/**
	 * Returns a range of all the repositories.
	 *
	 * <p>
	 * Useful when paginating results. Returns a maximum of <code>end - start</code> instances. <code>start</code> and <code>end</code> are not primary keys, they are indexes in the result set. Thus, <code>0</code> refers to the first result in the set. Setting both <code>start</code> and <code>end</code> to <code>com.liferay.portal.kernel.dao.orm.QueryUtil#ALL_POS</code> will return the full result set. If <code>orderByComparator</code> is specified, then the query will include the given ORDER BY logic. If <code>orderByComparator</code> is absent, then the query will include the default ORDER BY logic from <code>com.liferay.portal.model.impl.RepositoryModelImpl</code>.
	 * </p>
	 *
	 * @param start the lower bound of the range of repositories
	 * @param end the upper bound of the range of repositories (not inclusive)
	 * @return the range of repositories
	 */
	@Override
	public java.util.List<Repository> getRepositories(int start, int end) {
		return _repositoryLocalService.getRepositories(start, end);
	}

	@Override
	public java.util.List<Repository> getRepositories(String portletId) {
		return _repositoryLocalService.getRepositories(portletId);
	}

	/**
	 * Returns all the repositories matching the UUID and company.
	 *
	 * @param uuid the UUID of the repositories
	 * @param companyId the primary key of the company
	 * @return the matching repositories, or an empty list if no matches were found
	 */
	@Override
	public java.util.List<Repository> getRepositoriesByUuidAndCompanyId(
		String uuid, long companyId) {

		return _repositoryLocalService.getRepositoriesByUuidAndCompanyId(
			uuid, companyId);
	}

	/**
	 * Returns a range of repositories matching the UUID and company.
	 *
	 * @param uuid the UUID of the repositories
	 * @param companyId the primary key of the company
	 * @param start the lower bound of the range of repositories
	 * @param end the upper bound of the range of repositories (not inclusive)
	 * @param orderByComparator the comparator to order the results by (optionally <code>null</code>)
	 * @return the range of matching repositories, or an empty list if no matches were found
	 */
	@Override
	public java.util.List<Repository> getRepositoriesByUuidAndCompanyId(
		String uuid, long companyId, int start, int end,
		com.liferay.portal.kernel.util.OrderByComparator<Repository>
			orderByComparator) {

		return _repositoryLocalService.getRepositoriesByUuidAndCompanyId(
			uuid, companyId, start, end, orderByComparator);
	}

	/**
	 * Returns the number of repositories.
	 *
	 * @return the number of repositories
	 */
	@Override
	public int getRepositoriesCount() {
		return _repositoryLocalService.getRepositoriesCount();
	}

	/**
	 * Returns the repository with the primary key.
	 *
	 * @param repositoryId the primary key of the repository
	 * @return the repository
	 * @throws PortalException if a repository with the primary key could not be found
	 */
	@Override
	public Repository getRepository(long repositoryId)
		throws com.liferay.portal.kernel.exception.PortalException {

		return _repositoryLocalService.getRepository(repositoryId);
	}

	@Override
	public Repository getRepository(long groupId, String portletId)
		throws com.liferay.portal.kernel.exception.PortalException {

		return _repositoryLocalService.getRepository(groupId, portletId);
	}

	@Override
	public Repository getRepository(long groupId, String name, String portletId)
		throws com.liferay.portal.kernel.exception.PortalException {

		return _repositoryLocalService.getRepository(groupId, name, portletId);
	}

	@Override
	public Repository getRepositoryByExternalReferenceCode(
			String externalReferenceCode, long groupId)
		throws com.liferay.portal.kernel.exception.PortalException {

		return _repositoryLocalService.getRepositoryByExternalReferenceCode(
			externalReferenceCode, groupId);
	}

	/**
	 * Returns the repository matching the UUID and group.
	 *
	 * @param uuid the repository's UUID
	 * @param groupId the primary key of the group
	 * @return the matching repository
	 * @throws PortalException if a matching repository could not be found
	 */
	@Override
	public Repository getRepositoryByUuidAndGroupId(String uuid, long groupId)
		throws com.liferay.portal.kernel.exception.PortalException {

		return _repositoryLocalService.getRepositoryByUuidAndGroupId(
			uuid, groupId);
	}

	@Override
	public com.liferay.portal.kernel.util.UnicodeProperties
			getTypeSettingsProperties(long repositoryId)
		throws com.liferay.portal.kernel.exception.PortalException {

		return _repositoryLocalService.getTypeSettingsProperties(repositoryId);
	}

	@Override
	public void updateRepository(
			long repositoryId, String name, String description)
		throws com.liferay.portal.kernel.exception.PortalException {

		_repositoryLocalService.updateRepository(
			repositoryId, name, description);
	}

	@Override
	public void updateRepository(
			long repositoryId,
			com.liferay.portal.kernel.util.UnicodeProperties
				typeSettingsUnicodeProperties)
		throws com.liferay.portal.kernel.exception.PortalException {

		_repositoryLocalService.updateRepository(
			repositoryId, typeSettingsUnicodeProperties);
	}

	/**
	 * Updates the repository in the database or adds it if it does not yet exist. Also notifies the appropriate model listeners.
	 *
	 * <p>
	 * <strong>Important:</strong> Inspect RepositoryLocalServiceImpl for overloaded versions of the method. If provided, use these entry points to the API, as the implementation logic may require the additional parameters defined there.
	 * </p>
	 *
	 * @param repository the repository
	 * @return the repository that was updated
	 */
	@Override
	public Repository updateRepository(Repository repository) {
		return _repositoryLocalService.updateRepository(repository);
	}

	@Override
	public BasePersistence<?> getBasePersistence() {
		return _repositoryLocalService.getBasePersistence();
	}

	@Override
	public CTPersistence<Repository> getCTPersistence() {
		return _repositoryLocalService.getCTPersistence();
	}

	@Override
	public Class<Repository> getModelClass() {
		return _repositoryLocalService.getModelClass();
	}

	@Override
	public <R, E extends Throwable> R updateWithUnsafeFunction(
			UnsafeFunction<CTPersistence<Repository>, R, E>
				updateUnsafeFunction)
		throws E {

		return _repositoryLocalService.updateWithUnsafeFunction(
			updateUnsafeFunction);
	}

	@Override
	public RepositoryLocalService getWrappedService() {
		return _repositoryLocalService;
	}

	@Override
	public void setWrappedService(
		RepositoryLocalService repositoryLocalService) {

		_repositoryLocalService = repositoryLocalService;
	}

	private RepositoryLocalService _repositoryLocalService;

}