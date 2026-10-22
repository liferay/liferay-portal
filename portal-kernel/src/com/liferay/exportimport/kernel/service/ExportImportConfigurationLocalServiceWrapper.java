/**
 * SPDX-FileCopyrightText: (c) 2000 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

package com.liferay.exportimport.kernel.service;

import com.liferay.portal.kernel.service.ServiceWrapper;
import com.liferay.portal.kernel.service.persistence.BasePersistence;

/**
 * Provides a wrapper for {@link ExportImportConfigurationLocalService}.
 *
 * @author Brian Wing Shun Chan
 * @see ExportImportConfigurationLocalService
 * @generated
 */
public class ExportImportConfigurationLocalServiceWrapper
	implements ExportImportConfigurationLocalService,
			   ServiceWrapper<ExportImportConfigurationLocalService> {

	public ExportImportConfigurationLocalServiceWrapper() {
		this(null);
	}

	public ExportImportConfigurationLocalServiceWrapper(
		ExportImportConfigurationLocalService
			exportImportConfigurationLocalService) {

		_exportImportConfigurationLocalService =
			exportImportConfigurationLocalService;
	}

	@Override
	public com.liferay.exportimport.kernel.model.ExportImportConfiguration
			addDraftExportImportConfiguration(
				long userId, int type,
				java.util.Map<String, java.io.Serializable> settingsMap)
		throws com.liferay.portal.kernel.exception.PortalException {

		return _exportImportConfigurationLocalService.
			addDraftExportImportConfiguration(userId, type, settingsMap);
	}

	@Override
	public com.liferay.exportimport.kernel.model.ExportImportConfiguration
			addDraftExportImportConfiguration(
				long userId, String name, int type,
				java.util.Map<String, java.io.Serializable> settingsMap)
		throws com.liferay.portal.kernel.exception.PortalException {

		return _exportImportConfigurationLocalService.
			addDraftExportImportConfiguration(userId, name, type, settingsMap);
	}

	/**
	 * Adds the export import configuration to the database. Also notifies the appropriate model listeners.
	 *
	 * <p>
	 * <strong>Important:</strong> Inspect ExportImportConfigurationLocalServiceImpl for overloaded versions of the method. If provided, use these entry points to the API, as the implementation logic may require the additional parameters defined there.
	 * </p>
	 *
	 * @param exportImportConfiguration the export import configuration
	 * @return the export import configuration that was added
	 */
	@Override
	public com.liferay.exportimport.kernel.model.ExportImportConfiguration
		addExportImportConfiguration(
			com.liferay.exportimport.kernel.model.ExportImportConfiguration
				exportImportConfiguration) {

		return _exportImportConfigurationLocalService.
			addExportImportConfiguration(exportImportConfiguration);
	}

	@Override
	public com.liferay.exportimport.kernel.model.ExportImportConfiguration
			addExportImportConfiguration(
				long userId, long groupId, String name, String description,
				int type,
				java.util.Map<String, java.io.Serializable> settingsMap,
				int status,
				com.liferay.portal.kernel.service.ServiceContext serviceContext)
		throws com.liferay.portal.kernel.exception.PortalException {

		return _exportImportConfigurationLocalService.
			addExportImportConfiguration(
				userId, groupId, name, description, type, settingsMap, status,
				serviceContext);
	}

	@Override
	public com.liferay.exportimport.kernel.model.ExportImportConfiguration
			addExportImportConfiguration(
				long userId, long groupId, String name, String description,
				int type,
				java.util.Map<String, java.io.Serializable> settingsMap,
				com.liferay.portal.kernel.service.ServiceContext serviceContext)
		throws com.liferay.portal.kernel.exception.PortalException {

		return _exportImportConfigurationLocalService.
			addExportImportConfiguration(
				userId, groupId, name, description, type, settingsMap,
				serviceContext);
	}

	/**
	 * Creates a new export import configuration with the primary key. Does not add the export import configuration to the database.
	 *
	 * @param exportImportConfigurationId the primary key for the new export import configuration
	 * @return the new export import configuration
	 */
	@Override
	public com.liferay.exportimport.kernel.model.ExportImportConfiguration
		createExportImportConfiguration(long exportImportConfigurationId) {

		return _exportImportConfigurationLocalService.
			createExportImportConfiguration(exportImportConfigurationId);
	}

	/**
	 * @throws PortalException
	 */
	@Override
	public com.liferay.portal.kernel.model.PersistedModel createPersistedModel(
			java.io.Serializable primaryKeyObj)
		throws com.liferay.portal.kernel.exception.PortalException {

		return _exportImportConfigurationLocalService.createPersistedModel(
			primaryKeyObj);
	}

	/**
	 * Deletes the export import configuration from the database. Also notifies the appropriate model listeners.
	 *
	 * <p>
	 * <strong>Important:</strong> Inspect ExportImportConfigurationLocalServiceImpl for overloaded versions of the method. If provided, use these entry points to the API, as the implementation logic may require the additional parameters defined there.
	 * </p>
	 *
	 * @param exportImportConfiguration the export import configuration
	 * @return the export import configuration that was removed
	 */
	@Override
	public com.liferay.exportimport.kernel.model.ExportImportConfiguration
		deleteExportImportConfiguration(
			com.liferay.exportimport.kernel.model.ExportImportConfiguration
				exportImportConfiguration) {

		return _exportImportConfigurationLocalService.
			deleteExportImportConfiguration(exportImportConfiguration);
	}

	/**
	 * Deletes the export import configuration with the primary key from the database. Also notifies the appropriate model listeners.
	 *
	 * <p>
	 * <strong>Important:</strong> Inspect ExportImportConfigurationLocalServiceImpl for overloaded versions of the method. If provided, use these entry points to the API, as the implementation logic may require the additional parameters defined there.
	 * </p>
	 *
	 * @param exportImportConfigurationId the primary key of the export import configuration
	 * @return the export import configuration that was removed
	 * @throws PortalException if a export import configuration with the primary key could not be found
	 */
	@Override
	public com.liferay.exportimport.kernel.model.ExportImportConfiguration
			deleteExportImportConfiguration(long exportImportConfigurationId)
		throws com.liferay.portal.kernel.exception.PortalException {

		return _exportImportConfigurationLocalService.
			deleteExportImportConfiguration(exportImportConfigurationId);
	}

	@Override
	public void deleteExportImportConfigurations(long groupId) {
		_exportImportConfigurationLocalService.deleteExportImportConfigurations(
			groupId);
	}

	/**
	 * @throws PortalException
	 */
	@Override
	public com.liferay.portal.kernel.model.PersistedModel deletePersistedModel(
			com.liferay.portal.kernel.model.PersistedModel persistedModel)
		throws com.liferay.portal.kernel.exception.PortalException {

		return _exportImportConfigurationLocalService.deletePersistedModel(
			persistedModel);
	}

	@Override
	public <T> T dslQuery(com.liferay.petra.sql.dsl.query.DSLQuery dslQuery) {
		return _exportImportConfigurationLocalService.dslQuery(dslQuery);
	}

	@Override
	public int dslQueryCount(
		com.liferay.petra.sql.dsl.query.DSLQuery dslQuery) {

		return _exportImportConfigurationLocalService.dslQueryCount(dslQuery);
	}

	@Override
	public com.liferay.portal.kernel.dao.orm.DynamicQuery dynamicQuery() {
		return _exportImportConfigurationLocalService.dynamicQuery();
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

		return _exportImportConfigurationLocalService.dynamicQuery(
			dynamicQuery);
	}

	/**
	 * Performs a dynamic query on the database and returns a range of the matching rows.
	 *
	 * <p>
	 * Useful when paginating results. Returns a maximum of <code>end - start</code> instances. <code>start</code> and <code>end</code> are not primary keys, they are indexes in the result set. Thus, <code>0</code> refers to the first result in the set. Setting both <code>start</code> and <code>end</code> to <code>com.liferay.portal.kernel.dao.orm.QueryUtil#ALL_POS</code> will return the full result set. If <code>orderByComparator</code> is specified, then the query will include the given ORDER BY logic. If <code>orderByComparator</code> is absent, then the query will include the default ORDER BY logic from <code>com.liferay.portlet.exportimport.model.impl.ExportImportConfigurationModelImpl</code>.
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

		return _exportImportConfigurationLocalService.dynamicQuery(
			dynamicQuery, start, end);
	}

	/**
	 * Performs a dynamic query on the database and returns an ordered range of the matching rows.
	 *
	 * <p>
	 * Useful when paginating results. Returns a maximum of <code>end - start</code> instances. <code>start</code> and <code>end</code> are not primary keys, they are indexes in the result set. Thus, <code>0</code> refers to the first result in the set. Setting both <code>start</code> and <code>end</code> to <code>com.liferay.portal.kernel.dao.orm.QueryUtil#ALL_POS</code> will return the full result set. If <code>orderByComparator</code> is specified, then the query will include the given ORDER BY logic. If <code>orderByComparator</code> is absent, then the query will include the default ORDER BY logic from <code>com.liferay.portlet.exportimport.model.impl.ExportImportConfigurationModelImpl</code>.
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

		return _exportImportConfigurationLocalService.dynamicQuery(
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

		return _exportImportConfigurationLocalService.dynamicQueryCount(
			dynamicQuery);
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

		return _exportImportConfigurationLocalService.dynamicQueryCount(
			dynamicQuery, projection);
	}

	@Override
	public com.liferay.exportimport.kernel.model.ExportImportConfiguration
		fetchExportImportConfiguration(long exportImportConfigurationId) {

		return _exportImportConfigurationLocalService.
			fetchExportImportConfiguration(exportImportConfigurationId);
	}

	@Override
	public com.liferay.portal.kernel.dao.orm.ActionableDynamicQuery
		getActionableDynamicQuery() {

		return _exportImportConfigurationLocalService.
			getActionableDynamicQuery();
	}

	/**
	 * Returns the export import configuration with the primary key.
	 *
	 * @param exportImportConfigurationId the primary key of the export import configuration
	 * @return the export import configuration
	 * @throws PortalException if a export import configuration with the primary key could not be found
	 */
	@Override
	public com.liferay.exportimport.kernel.model.ExportImportConfiguration
			getExportImportConfiguration(long exportImportConfigurationId)
		throws com.liferay.portal.kernel.exception.PortalException {

		return _exportImportConfigurationLocalService.
			getExportImportConfiguration(exportImportConfigurationId);
	}

	@Override
	public java.util.List
		<com.liferay.exportimport.kernel.model.ExportImportConfiguration>
				getExportImportConfigurations(
					com.liferay.portal.kernel.search.Hits hits)
			throws com.liferay.portal.kernel.exception.PortalException {

		return _exportImportConfigurationLocalService.
			getExportImportConfigurations(hits);
	}

	/**
	 * Returns a range of all the export import configurations.
	 *
	 * <p>
	 * Useful when paginating results. Returns a maximum of <code>end - start</code> instances. <code>start</code> and <code>end</code> are not primary keys, they are indexes in the result set. Thus, <code>0</code> refers to the first result in the set. Setting both <code>start</code> and <code>end</code> to <code>com.liferay.portal.kernel.dao.orm.QueryUtil#ALL_POS</code> will return the full result set. If <code>orderByComparator</code> is specified, then the query will include the given ORDER BY logic. If <code>orderByComparator</code> is absent, then the query will include the default ORDER BY logic from <code>com.liferay.portlet.exportimport.model.impl.ExportImportConfigurationModelImpl</code>.
	 * </p>
	 *
	 * @param start the lower bound of the range of export import configurations
	 * @param end the upper bound of the range of export import configurations (not inclusive)
	 * @return the range of export import configurations
	 */
	@Override
	public java.util.List
		<com.liferay.exportimport.kernel.model.ExportImportConfiguration>
			getExportImportConfigurations(int start, int end) {

		return _exportImportConfigurationLocalService.
			getExportImportConfigurations(start, end);
	}

	@Override
	public java.util.List
		<com.liferay.exportimport.kernel.model.ExportImportConfiguration>
			getExportImportConfigurations(long groupId, int type) {

		return _exportImportConfigurationLocalService.
			getExportImportConfigurations(groupId, type);
	}

	@Override
	public java.util.List
		<com.liferay.exportimport.kernel.model.ExportImportConfiguration>
			getExportImportConfigurations(
				long groupId, int type, int start, int end,
				com.liferay.portal.kernel.util.OrderByComparator
					<com.liferay.exportimport.kernel.model.
						ExportImportConfiguration> orderByComparator) {

		return _exportImportConfigurationLocalService.
			getExportImportConfigurations(
				groupId, type, start, end, orderByComparator);
	}

	@Override
	public java.util.List
		<com.liferay.exportimport.kernel.model.ExportImportConfiguration>
			getExportImportConfigurations(
				long companyId, long groupId, String keywords, int type,
				int start, int end,
				com.liferay.portal.kernel.util.OrderByComparator
					<com.liferay.exportimport.kernel.model.
						ExportImportConfiguration> orderByComparator) {

		return _exportImportConfigurationLocalService.
			getExportImportConfigurations(
				companyId, groupId, keywords, type, start, end,
				orderByComparator);
	}

	/**
	 * Returns the number of export import configurations.
	 *
	 * @return the number of export import configurations
	 */
	@Override
	public int getExportImportConfigurationsCount() {
		return _exportImportConfigurationLocalService.
			getExportImportConfigurationsCount();
	}

	@Override
	public int getExportImportConfigurationsCount(long groupId, int type) {
		return _exportImportConfigurationLocalService.
			getExportImportConfigurationsCount(groupId, type);
	}

	@Override
	public int getExportImportConfigurationsCount(
		long companyId, long groupId, String keywords, int type) {

		return _exportImportConfigurationLocalService.
			getExportImportConfigurationsCount(
				companyId, groupId, keywords, type);
	}

	@Override
	public com.liferay.portal.kernel.dao.orm.IndexableActionableDynamicQuery
		getIndexableActionableDynamicQuery() {

		return _exportImportConfigurationLocalService.
			getIndexableActionableDynamicQuery();
	}

	/**
	 * Returns the OSGi service identifier.
	 *
	 * @return the OSGi service identifier
	 */
	@Override
	public String getOSGiServiceIdentifier() {
		return _exportImportConfigurationLocalService.
			getOSGiServiceIdentifier();
	}

	/**
	 * @throws PortalException
	 */
	@Override
	public com.liferay.portal.kernel.model.PersistedModel getPersistedModel(
			java.io.Serializable primaryKeyObj)
		throws com.liferay.portal.kernel.exception.PortalException {

		return _exportImportConfigurationLocalService.getPersistedModel(
			primaryKeyObj);
	}

	@Override
	public com.liferay.exportimport.kernel.model.ExportImportConfiguration
			moveExportImportConfigurationToTrash(
				long userId, long exportImportConfigurationId)
		throws com.liferay.portal.kernel.exception.PortalException {

		return _exportImportConfigurationLocalService.
			moveExportImportConfigurationToTrash(
				userId, exportImportConfigurationId);
	}

	@Override
	public com.liferay.exportimport.kernel.model.ExportImportConfiguration
			restoreExportImportConfigurationFromTrash(
				long userId, long exportImportConfigurationId)
		throws com.liferay.portal.kernel.exception.PortalException {

		return _exportImportConfigurationLocalService.
			restoreExportImportConfigurationFromTrash(
				userId, exportImportConfigurationId);
	}

	@Override
	public com.liferay.portal.kernel.search.BaseModelSearchResult
		<com.liferay.exportimport.kernel.model.ExportImportConfiguration>
				searchExportImportConfigurations(
					long companyId, long groupId, int type, String keywords,
					int start, int end,
					com.liferay.portal.kernel.search.Sort sort)
			throws com.liferay.portal.kernel.exception.PortalException {

		return _exportImportConfigurationLocalService.
			searchExportImportConfigurations(
				companyId, groupId, type, keywords, start, end, sort);
	}

	@Override
	public com.liferay.portal.kernel.search.BaseModelSearchResult
		<com.liferay.exportimport.kernel.model.ExportImportConfiguration>
				searchExportImportConfigurations(
					long companyId, long groupId, int type, String name,
					String description, boolean andSearch, int start, int end,
					com.liferay.portal.kernel.search.Sort sort)
			throws com.liferay.portal.kernel.exception.PortalException {

		return _exportImportConfigurationLocalService.
			searchExportImportConfigurations(
				companyId, groupId, type, name, description, andSearch, start,
				end, sort);
	}

	/**
	 * Updates the export import configuration in the database or adds it if it does not yet exist. Also notifies the appropriate model listeners.
	 *
	 * <p>
	 * <strong>Important:</strong> Inspect ExportImportConfigurationLocalServiceImpl for overloaded versions of the method. If provided, use these entry points to the API, as the implementation logic may require the additional parameters defined there.
	 * </p>
	 *
	 * @param exportImportConfiguration the export import configuration
	 * @return the export import configuration that was updated
	 */
	@Override
	public com.liferay.exportimport.kernel.model.ExportImportConfiguration
		updateExportImportConfiguration(
			com.liferay.exportimport.kernel.model.ExportImportConfiguration
				exportImportConfiguration) {

		return _exportImportConfigurationLocalService.
			updateExportImportConfiguration(exportImportConfiguration);
	}

	@Override
	public com.liferay.exportimport.kernel.model.ExportImportConfiguration
			updateExportImportConfiguration(
				long userId, long exportImportConfigurationId, String name,
				String description,
				java.util.Map<String, java.io.Serializable> settingsMap,
				com.liferay.portal.kernel.service.ServiceContext serviceContext)
		throws com.liferay.portal.kernel.exception.PortalException {

		return _exportImportConfigurationLocalService.
			updateExportImportConfiguration(
				userId, exportImportConfigurationId, name, description,
				settingsMap, serviceContext);
	}

	@Override
	public com.liferay.exportimport.kernel.model.ExportImportConfiguration
			updateStatus(
				long userId, long exportImportConfigurationId, int status)
		throws com.liferay.portal.kernel.exception.PortalException {

		return _exportImportConfigurationLocalService.updateStatus(
			userId, exportImportConfigurationId, status);
	}

	@Override
	public BasePersistence<?> getBasePersistence() {
		return _exportImportConfigurationLocalService.getBasePersistence();
	}

	@Override
	public ExportImportConfigurationLocalService getWrappedService() {
		return _exportImportConfigurationLocalService;
	}

	@Override
	public void setWrappedService(
		ExportImportConfigurationLocalService
			exportImportConfigurationLocalService) {

		_exportImportConfigurationLocalService =
			exportImportConfigurationLocalService;
	}

	private ExportImportConfigurationLocalService
		_exportImportConfigurationLocalService;

}
// LIFERAY-SERVICE-BUILDER-HASH:-1081219159