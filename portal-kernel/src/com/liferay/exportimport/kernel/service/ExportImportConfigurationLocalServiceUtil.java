/**
 * SPDX-FileCopyrightText: (c) 2000 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

package com.liferay.exportimport.kernel.service;

import com.liferay.exportimport.kernel.model.ExportImportConfiguration;
import com.liferay.petra.sql.dsl.query.DSLQuery;
import com.liferay.portal.kernel.dao.orm.DynamicQuery;
import com.liferay.portal.kernel.exception.PortalException;
import com.liferay.portal.kernel.model.PersistedModel;
import com.liferay.portal.kernel.util.OrderByComparator;

import java.io.Serializable;

import java.util.List;
import java.util.Map;

/**
 * Provides the local service utility for ExportImportConfiguration. This utility wraps
 * <code>com.liferay.portlet.exportimport.service.impl.ExportImportConfigurationLocalServiceImpl</code> and
 * is an access point for service operations in application layer code running
 * on the local server. Methods of this service will not have security checks
 * based on the propagated JAAS credentials because this service can only be
 * accessed from within the same VM.
 *
 * @author Brian Wing Shun Chan
 * @see ExportImportConfigurationLocalService
 * @generated
 */
public class ExportImportConfigurationLocalServiceUtil {

	/*
	 * NOTE FOR DEVELOPERS:
	 *
	 * Never modify this class directly. Add custom service methods to <code>com.liferay.portlet.exportimport.service.impl.ExportImportConfigurationLocalServiceImpl</code> and rerun ServiceBuilder to regenerate this class.
	 */
	public static ExportImportConfiguration addDraftExportImportConfiguration(
			long userId, int type, Map<String, Serializable> settingsMap)
		throws PortalException {

		return getService().addDraftExportImportConfiguration(
			userId, type, settingsMap);
	}

	public static ExportImportConfiguration addDraftExportImportConfiguration(
			long userId, String name, int type,
			Map<String, Serializable> settingsMap)
		throws PortalException {

		return getService().addDraftExportImportConfiguration(
			userId, name, type, settingsMap);
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
	public static ExportImportConfiguration addExportImportConfiguration(
		ExportImportConfiguration exportImportConfiguration) {

		return getService().addExportImportConfiguration(
			exportImportConfiguration);
	}

	public static ExportImportConfiguration addExportImportConfiguration(
			long userId, long groupId, String name, String description,
			int type, Map<String, Serializable> settingsMap, int status,
			com.liferay.portal.kernel.service.ServiceContext serviceContext)
		throws PortalException {

		return getService().addExportImportConfiguration(
			userId, groupId, name, description, type, settingsMap, status,
			serviceContext);
	}

	public static ExportImportConfiguration addExportImportConfiguration(
			long userId, long groupId, String name, String description,
			int type, Map<String, Serializable> settingsMap,
			com.liferay.portal.kernel.service.ServiceContext serviceContext)
		throws PortalException {

		return getService().addExportImportConfiguration(
			userId, groupId, name, description, type, settingsMap,
			serviceContext);
	}

	/**
	 * Creates a new export import configuration with the primary key. Does not add the export import configuration to the database.
	 *
	 * @param exportImportConfigurationId the primary key for the new export import configuration
	 * @return the new export import configuration
	 */
	public static ExportImportConfiguration createExportImportConfiguration(
		long exportImportConfigurationId) {

		return getService().createExportImportConfiguration(
			exportImportConfigurationId);
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
	 * Deletes the export import configuration from the database. Also notifies the appropriate model listeners.
	 *
	 * <p>
	 * <strong>Important:</strong> Inspect ExportImportConfigurationLocalServiceImpl for overloaded versions of the method. If provided, use these entry points to the API, as the implementation logic may require the additional parameters defined there.
	 * </p>
	 *
	 * @param exportImportConfiguration the export import configuration
	 * @return the export import configuration that was removed
	 */
	public static ExportImportConfiguration deleteExportImportConfiguration(
		ExportImportConfiguration exportImportConfiguration) {

		return getService().deleteExportImportConfiguration(
			exportImportConfiguration);
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
	public static ExportImportConfiguration deleteExportImportConfiguration(
			long exportImportConfigurationId)
		throws PortalException {

		return getService().deleteExportImportConfiguration(
			exportImportConfigurationId);
	}

	public static void deleteExportImportConfigurations(long groupId) {
		getService().deleteExportImportConfigurations(groupId);
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
	 * Useful when paginating results. Returns a maximum of <code>end - start</code> instances. <code>start</code> and <code>end</code> are not primary keys, they are indexes in the result set. Thus, <code>0</code> refers to the first result in the set. Setting both <code>start</code> and <code>end</code> to <code>com.liferay.portal.kernel.dao.orm.QueryUtil#ALL_POS</code> will return the full result set. If <code>orderByComparator</code> is specified, then the query will include the given ORDER BY logic. If <code>orderByComparator</code> is absent, then the query will include the default ORDER BY logic from <code>com.liferay.portlet.exportimport.model.impl.ExportImportConfigurationModelImpl</code>.
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
	 * Useful when paginating results. Returns a maximum of <code>end - start</code> instances. <code>start</code> and <code>end</code> are not primary keys, they are indexes in the result set. Thus, <code>0</code> refers to the first result in the set. Setting both <code>start</code> and <code>end</code> to <code>com.liferay.portal.kernel.dao.orm.QueryUtil#ALL_POS</code> will return the full result set. If <code>orderByComparator</code> is specified, then the query will include the given ORDER BY logic. If <code>orderByComparator</code> is absent, then the query will include the default ORDER BY logic from <code>com.liferay.portlet.exportimport.model.impl.ExportImportConfigurationModelImpl</code>.
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

	public static ExportImportConfiguration fetchExportImportConfiguration(
		long exportImportConfigurationId) {

		return getService().fetchExportImportConfiguration(
			exportImportConfigurationId);
	}

	public static com.liferay.portal.kernel.dao.orm.ActionableDynamicQuery
		getActionableDynamicQuery() {

		return getService().getActionableDynamicQuery();
	}

	/**
	 * Returns the export import configuration with the primary key.
	 *
	 * @param exportImportConfigurationId the primary key of the export import configuration
	 * @return the export import configuration
	 * @throws PortalException if a export import configuration with the primary key could not be found
	 */
	public static ExportImportConfiguration getExportImportConfiguration(
			long exportImportConfigurationId)
		throws PortalException {

		return getService().getExportImportConfiguration(
			exportImportConfigurationId);
	}

	public static List<ExportImportConfiguration> getExportImportConfigurations(
			com.liferay.portal.kernel.search.Hits hits)
		throws PortalException {

		return getService().getExportImportConfigurations(hits);
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
	public static List<ExportImportConfiguration> getExportImportConfigurations(
		int start, int end) {

		return getService().getExportImportConfigurations(start, end);
	}

	public static List<ExportImportConfiguration> getExportImportConfigurations(
		long groupId, int type) {

		return getService().getExportImportConfigurations(groupId, type);
	}

	public static List<ExportImportConfiguration> getExportImportConfigurations(
		long groupId, int type, int start, int end,
		OrderByComparator<ExportImportConfiguration> orderByComparator) {

		return getService().getExportImportConfigurations(
			groupId, type, start, end, orderByComparator);
	}

	public static List<ExportImportConfiguration> getExportImportConfigurations(
		long companyId, long groupId, String keywords, int type, int start,
		int end,
		OrderByComparator<ExportImportConfiguration> orderByComparator) {

		return getService().getExportImportConfigurations(
			companyId, groupId, keywords, type, start, end, orderByComparator);
	}

	/**
	 * Returns the number of export import configurations.
	 *
	 * @return the number of export import configurations
	 */
	public static int getExportImportConfigurationsCount() {
		return getService().getExportImportConfigurationsCount();
	}

	public static int getExportImportConfigurationsCount(
		long groupId, int type) {

		return getService().getExportImportConfigurationsCount(groupId, type);
	}

	public static int getExportImportConfigurationsCount(
		long companyId, long groupId, String keywords, int type) {

		return getService().getExportImportConfigurationsCount(
			companyId, groupId, keywords, type);
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

	public static ExportImportConfiguration
			moveExportImportConfigurationToTrash(
				long userId, long exportImportConfigurationId)
		throws PortalException {

		return getService().moveExportImportConfigurationToTrash(
			userId, exportImportConfigurationId);
	}

	public static ExportImportConfiguration
			restoreExportImportConfigurationFromTrash(
				long userId, long exportImportConfigurationId)
		throws PortalException {

		return getService().restoreExportImportConfigurationFromTrash(
			userId, exportImportConfigurationId);
	}

	public static com.liferay.portal.kernel.search.BaseModelSearchResult
		<ExportImportConfiguration> searchExportImportConfigurations(
				long companyId, long groupId, int type, String keywords,
				int start, int end, com.liferay.portal.kernel.search.Sort sort)
			throws PortalException {

		return getService().searchExportImportConfigurations(
			companyId, groupId, type, keywords, start, end, sort);
	}

	public static com.liferay.portal.kernel.search.BaseModelSearchResult
		<ExportImportConfiguration> searchExportImportConfigurations(
				long companyId, long groupId, int type, String name,
				String description, boolean andSearch, int start, int end,
				com.liferay.portal.kernel.search.Sort sort)
			throws PortalException {

		return getService().searchExportImportConfigurations(
			companyId, groupId, type, name, description, andSearch, start, end,
			sort);
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
	public static ExportImportConfiguration updateExportImportConfiguration(
		ExportImportConfiguration exportImportConfiguration) {

		return getService().updateExportImportConfiguration(
			exportImportConfiguration);
	}

	public static ExportImportConfiguration updateExportImportConfiguration(
			long userId, long exportImportConfigurationId, String name,
			String description, Map<String, Serializable> settingsMap,
			com.liferay.portal.kernel.service.ServiceContext serviceContext)
		throws PortalException {

		return getService().updateExportImportConfiguration(
			userId, exportImportConfigurationId, name, description, settingsMap,
			serviceContext);
	}

	public static ExportImportConfiguration updateStatus(
			long userId, long exportImportConfigurationId, int status)
		throws PortalException {

		return getService().updateStatus(
			userId, exportImportConfigurationId, status);
	}

	public static ExportImportConfigurationLocalService getService() {
		return _service;
	}

	public static void setService(
		ExportImportConfigurationLocalService service) {

		_service = service;
	}

	private static volatile ExportImportConfigurationLocalService _service;

}
// LIFERAY-SERVICE-BUILDER-HASH:-1919963081