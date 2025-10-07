/**
 * SPDX-FileCopyrightText: (c) 2025 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

package com.liferay.exportimport.report.service;

import com.liferay.portal.kernel.service.ServiceWrapper;
import com.liferay.portal.kernel.service.persistence.BasePersistence;

/**
 * Provides a wrapper for {@link ExportImportReportEntryLocalService}.
 *
 * @author Carlos Correa
 * @see ExportImportReportEntryLocalService
 * @generated
 */
public class ExportImportReportEntryLocalServiceWrapper
	implements ExportImportReportEntryLocalService,
			   ServiceWrapper<ExportImportReportEntryLocalService> {

	public ExportImportReportEntryLocalServiceWrapper() {
		this(null);
	}

	public ExportImportReportEntryLocalServiceWrapper(
		ExportImportReportEntryLocalService
			exportImportReportEntryLocalService) {

		_exportImportReportEntryLocalService =
			exportImportReportEntryLocalService;
	}

	@Override
	public com.liferay.exportimport.report.model.ExportImportReportEntry
		addEmptyExportImportReportEntry(
			long groupId, long companyId, String classExternalReferenceCode,
			long classNameId, long exportImportConfigurationId,
			String modelName, int origin, String scope, String scopeKey) {

		return _exportImportReportEntryLocalService.
			addEmptyExportImportReportEntry(
				groupId, companyId, classExternalReferenceCode, classNameId,
				exportImportConfigurationId, modelName, origin, scope,
				scopeKey);
	}

	@Override
	public com.liferay.exportimport.report.model.ExportImportReportEntry
		addErrorExportImportReportEntry(
			long groupId, long companyId, String classExternalReferenceCode,
			long classNameId, long classPK, long exportImportConfigurationId,
			String errorMessage, String errorStacktrace, String modelName,
			int origin, String scope, String scopeKey) {

		return _exportImportReportEntryLocalService.
			addErrorExportImportReportEntry(
				groupId, companyId, classExternalReferenceCode, classNameId,
				classPK, exportImportConfigurationId, errorMessage,
				errorStacktrace, modelName, origin, scope, scopeKey);
	}

	/**
	 * Adds the export import report entry to the database. Also notifies the appropriate model listeners.
	 *
	 * <p>
	 * <strong>Important:</strong> Inspect ExportImportReportEntryLocalServiceImpl for overloaded versions of the method. If provided, use these entry points to the API, as the implementation logic may require the additional parameters defined there.
	 * </p>
	 *
	 * @param exportImportReportEntry the export import report entry
	 * @return the export import report entry that was added
	 */
	@Override
	public com.liferay.exportimport.report.model.ExportImportReportEntry
		addExportImportReportEntry(
			com.liferay.exportimport.report.model.ExportImportReportEntry
				exportImportReportEntry) {

		return _exportImportReportEntryLocalService.addExportImportReportEntry(
			exportImportReportEntry);
	}

	/**
	 * Creates a new export import report entry with the primary key. Does not add the export import report entry to the database.
	 *
	 * @param exportImportReportEntryId the primary key for the new export import report entry
	 * @return the new export import report entry
	 */
	@Override
	public com.liferay.exportimport.report.model.ExportImportReportEntry
		createExportImportReportEntry(long exportImportReportEntryId) {

		return _exportImportReportEntryLocalService.
			createExportImportReportEntry(exportImportReportEntryId);
	}

	/**
	 * @throws PortalException
	 */
	@Override
	public com.liferay.portal.kernel.model.PersistedModel createPersistedModel(
			java.io.Serializable primaryKeyObj)
		throws com.liferay.portal.kernel.exception.PortalException {

		return _exportImportReportEntryLocalService.createPersistedModel(
			primaryKeyObj);
	}

	/**
	 * Deletes the export import report entry from the database. Also notifies the appropriate model listeners.
	 *
	 * <p>
	 * <strong>Important:</strong> Inspect ExportImportReportEntryLocalServiceImpl for overloaded versions of the method. If provided, use these entry points to the API, as the implementation logic may require the additional parameters defined there.
	 * </p>
	 *
	 * @param exportImportReportEntry the export import report entry
	 * @return the export import report entry that was removed
	 */
	@Override
	public com.liferay.exportimport.report.model.ExportImportReportEntry
		deleteExportImportReportEntry(
			com.liferay.exportimport.report.model.ExportImportReportEntry
				exportImportReportEntry) {

		return _exportImportReportEntryLocalService.
			deleteExportImportReportEntry(exportImportReportEntry);
	}

	/**
	 * Deletes the export import report entry with the primary key from the database. Also notifies the appropriate model listeners.
	 *
	 * <p>
	 * <strong>Important:</strong> Inspect ExportImportReportEntryLocalServiceImpl for overloaded versions of the method. If provided, use these entry points to the API, as the implementation logic may require the additional parameters defined there.
	 * </p>
	 *
	 * @param exportImportReportEntryId the primary key of the export import report entry
	 * @return the export import report entry that was removed
	 * @throws PortalException if a export import report entry with the primary key could not be found
	 */
	@Override
	public com.liferay.exportimport.report.model.ExportImportReportEntry
			deleteExportImportReportEntry(long exportImportReportEntryId)
		throws com.liferay.portal.kernel.exception.PortalException {

		return _exportImportReportEntryLocalService.
			deleteExportImportReportEntry(exportImportReportEntryId);
	}

	/**
	 * @throws PortalException
	 */
	@Override
	public com.liferay.portal.kernel.model.PersistedModel deletePersistedModel(
			com.liferay.portal.kernel.model.PersistedModel persistedModel)
		throws com.liferay.portal.kernel.exception.PortalException {

		return _exportImportReportEntryLocalService.deletePersistedModel(
			persistedModel);
	}

	@Override
	public <T> T dslQuery(com.liferay.petra.sql.dsl.query.DSLQuery dslQuery) {
		return _exportImportReportEntryLocalService.dslQuery(dslQuery);
	}

	@Override
	public int dslQueryCount(
		com.liferay.petra.sql.dsl.query.DSLQuery dslQuery) {

		return _exportImportReportEntryLocalService.dslQueryCount(dslQuery);
	}

	@Override
	public com.liferay.portal.kernel.dao.orm.DynamicQuery dynamicQuery() {
		return _exportImportReportEntryLocalService.dynamicQuery();
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

		return _exportImportReportEntryLocalService.dynamicQuery(dynamicQuery);
	}

	/**
	 * Performs a dynamic query on the database and returns a range of the matching rows.
	 *
	 * <p>
	 * Useful when paginating results. Returns a maximum of <code>end - start</code> instances. <code>start</code> and <code>end</code> are not primary keys, they are indexes in the result set. Thus, <code>0</code> refers to the first result in the set. Setting both <code>start</code> and <code>end</code> to <code>com.liferay.portal.kernel.dao.orm.QueryUtil#ALL_POS</code> will return the full result set. If <code>orderByComparator</code> is specified, then the query will include the given ORDER BY logic. If <code>orderByComparator</code> is absent, then the query will include the default ORDER BY logic from <code>com.liferay.exportimport.report.model.impl.ExportImportReportEntryModelImpl</code>.
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

		return _exportImportReportEntryLocalService.dynamicQuery(
			dynamicQuery, start, end);
	}

	/**
	 * Performs a dynamic query on the database and returns an ordered range of the matching rows.
	 *
	 * <p>
	 * Useful when paginating results. Returns a maximum of <code>end - start</code> instances. <code>start</code> and <code>end</code> are not primary keys, they are indexes in the result set. Thus, <code>0</code> refers to the first result in the set. Setting both <code>start</code> and <code>end</code> to <code>com.liferay.portal.kernel.dao.orm.QueryUtil#ALL_POS</code> will return the full result set. If <code>orderByComparator</code> is specified, then the query will include the given ORDER BY logic. If <code>orderByComparator</code> is absent, then the query will include the default ORDER BY logic from <code>com.liferay.exportimport.report.model.impl.ExportImportReportEntryModelImpl</code>.
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

		return _exportImportReportEntryLocalService.dynamicQuery(
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

		return _exportImportReportEntryLocalService.dynamicQueryCount(
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

		return _exportImportReportEntryLocalService.dynamicQueryCount(
			dynamicQuery, projection);
	}

	@Override
	public com.liferay.exportimport.report.model.ExportImportReportEntry
		fetchExportImportReportEntry(long exportImportReportEntryId) {

		return _exportImportReportEntryLocalService.
			fetchExportImportReportEntry(exportImportReportEntryId);
	}

	@Override
	public com.liferay.portal.kernel.dao.orm.ActionableDynamicQuery
		getActionableDynamicQuery() {

		return _exportImportReportEntryLocalService.getActionableDynamicQuery();
	}

	/**
	 * Returns a range of all the export import report entries.
	 *
	 * <p>
	 * Useful when paginating results. Returns a maximum of <code>end - start</code> instances. <code>start</code> and <code>end</code> are not primary keys, they are indexes in the result set. Thus, <code>0</code> refers to the first result in the set. Setting both <code>start</code> and <code>end</code> to <code>com.liferay.portal.kernel.dao.orm.QueryUtil#ALL_POS</code> will return the full result set. If <code>orderByComparator</code> is specified, then the query will include the given ORDER BY logic. If <code>orderByComparator</code> is absent, then the query will include the default ORDER BY logic from <code>com.liferay.exportimport.report.model.impl.ExportImportReportEntryModelImpl</code>.
	 * </p>
	 *
	 * @param start the lower bound of the range of export import report entries
	 * @param end the upper bound of the range of export import report entries (not inclusive)
	 * @return the range of export import report entries
	 */
	@Override
	public java.util.List
		<com.liferay.exportimport.report.model.ExportImportReportEntry>
			getExportImportReportEntries(int start, int end) {

		return _exportImportReportEntryLocalService.
			getExportImportReportEntries(start, end);
	}

	@Override
	public java.util.List
		<com.liferay.exportimport.report.model.ExportImportReportEntry>
			getExportImportReportEntries(
				long companyId, long exportImportConfigurationId) {

		return _exportImportReportEntryLocalService.
			getExportImportReportEntries(
				companyId, exportImportConfigurationId);
	}

	/**
	 * Returns the number of export import report entries.
	 *
	 * @return the number of export import report entries
	 */
	@Override
	public int getExportImportReportEntriesCount() {
		return _exportImportReportEntryLocalService.
			getExportImportReportEntriesCount();
	}

	/**
	 * Returns the export import report entry with the primary key.
	 *
	 * @param exportImportReportEntryId the primary key of the export import report entry
	 * @return the export import report entry
	 * @throws PortalException if a export import report entry with the primary key could not be found
	 */
	@Override
	public com.liferay.exportimport.report.model.ExportImportReportEntry
			getExportImportReportEntry(long exportImportReportEntryId)
		throws com.liferay.portal.kernel.exception.PortalException {

		return _exportImportReportEntryLocalService.getExportImportReportEntry(
			exportImportReportEntryId);
	}

	@Override
	public com.liferay.portal.kernel.dao.orm.IndexableActionableDynamicQuery
		getIndexableActionableDynamicQuery() {

		return _exportImportReportEntryLocalService.
			getIndexableActionableDynamicQuery();
	}

	/**
	 * Returns the OSGi service identifier.
	 *
	 * @return the OSGi service identifier
	 */
	@Override
	public String getOSGiServiceIdentifier() {
		return _exportImportReportEntryLocalService.getOSGiServiceIdentifier();
	}

	/**
	 * @throws PortalException
	 */
	@Override
	public com.liferay.portal.kernel.model.PersistedModel getPersistedModel(
			java.io.Serializable primaryKeyObj)
		throws com.liferay.portal.kernel.exception.PortalException {

		return _exportImportReportEntryLocalService.getPersistedModel(
			primaryKeyObj);
	}

	/**
	 * Updates the export import report entry in the database or adds it if it does not yet exist. Also notifies the appropriate model listeners.
	 *
	 * <p>
	 * <strong>Important:</strong> Inspect ExportImportReportEntryLocalServiceImpl for overloaded versions of the method. If provided, use these entry points to the API, as the implementation logic may require the additional parameters defined there.
	 * </p>
	 *
	 * @param exportImportReportEntry the export import report entry
	 * @return the export import report entry that was updated
	 */
	@Override
	public com.liferay.exportimport.report.model.ExportImportReportEntry
		updateExportImportReportEntry(
			com.liferay.exportimport.report.model.ExportImportReportEntry
				exportImportReportEntry) {

		return _exportImportReportEntryLocalService.
			updateExportImportReportEntry(exportImportReportEntry);
	}

	@Override
	public BasePersistence<?> getBasePersistence() {
		return _exportImportReportEntryLocalService.getBasePersistence();
	}

	@Override
	public ExportImportReportEntryLocalService getWrappedService() {
		return _exportImportReportEntryLocalService;
	}

	@Override
	public void setWrappedService(
		ExportImportReportEntryLocalService
			exportImportReportEntryLocalService) {

		_exportImportReportEntryLocalService =
			exportImportReportEntryLocalService;
	}

	private ExportImportReportEntryLocalService
		_exportImportReportEntryLocalService;

}