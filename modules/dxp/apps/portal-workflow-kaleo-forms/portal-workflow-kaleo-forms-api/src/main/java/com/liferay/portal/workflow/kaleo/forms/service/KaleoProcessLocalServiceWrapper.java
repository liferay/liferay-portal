/**
 * SPDX-FileCopyrightText: (c) 2000 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

package com.liferay.portal.workflow.kaleo.forms.service;

import com.liferay.portal.kernel.service.ServiceWrapper;
import com.liferay.portal.kernel.service.persistence.BasePersistence;

/**
 * Provides a wrapper for {@link KaleoProcessLocalService}.
 *
 * @author Marcellus Tavares
 * @see KaleoProcessLocalService
 * @generated
 */
public class KaleoProcessLocalServiceWrapper
	implements KaleoProcessLocalService,
			   ServiceWrapper<KaleoProcessLocalService> {

	public KaleoProcessLocalServiceWrapper() {
		this(null);
	}

	public KaleoProcessLocalServiceWrapper(
		KaleoProcessLocalService kaleoProcessLocalService) {

		_kaleoProcessLocalService = kaleoProcessLocalService;
	}

	/**
	 * Adds the kaleo process to the database. Also notifies the appropriate model listeners.
	 *
	 * <p>
	 * <strong>Important:</strong> Inspect KaleoProcessLocalServiceImpl for overloaded versions of the method. If provided, use these entry points to the API, as the implementation logic may require the additional parameters defined there.
	 * </p>
	 *
	 * @param kaleoProcess the kaleo process
	 * @return the kaleo process that was added
	 */
	@Override
	public com.liferay.portal.workflow.kaleo.forms.model.KaleoProcess
		addKaleoProcess(
			com.liferay.portal.workflow.kaleo.forms.model.KaleoProcess
				kaleoProcess) {

		return _kaleoProcessLocalService.addKaleoProcess(kaleoProcess);
	}

	/**
	 * Adds a Kaleo process.
	 *
	 * @param userId the primary key of the Kaleo process's creator/owner
	 * @param groupId the primary key of the Kaleo process's group
	 * @param ddmStructureId the primary key of the Kaleo process's DDM
	 structure
	 * @param nameMap the Kaleo process's locales and localized names
	 * @param descriptionMap the Kaleo process's locales and localized
	 descriptions
	 * @param ddmTemplateId the primary key of the Kaleo process's DDM template
	 * @param workflowDefinitionName the Kaleo process's workflow definition
	 name
	 * @param workflowDefinitionVersion the Kaleo process's workflow definition
	 version
	 * @param kaleoTaskFormPairs the Kaleo task form pairs used to create a
	 Kaleo process link. See <code>KaleoTaskFormPairs</code> in the
	 <code>portal.workflow.kaleo.forms.api</code> module.
	 * @param serviceContext the service context to be applied. This can set
	 guest permissions and group permissions for the Kaleo process.
	 * @return the Kaleo process
	 * @throws PortalException if a portal exception occurred
	 */
	@Override
	public com.liferay.portal.workflow.kaleo.forms.model.KaleoProcess
			addKaleoProcess(
				long userId, long groupId, long ddmStructureId,
				java.util.Map<java.util.Locale, String> nameMap,
				java.util.Map<java.util.Locale, String> descriptionMap,
				long ddmTemplateId, String workflowDefinitionName,
				int workflowDefinitionVersion,
				com.liferay.portal.workflow.kaleo.forms.model.KaleoTaskFormPairs
					kaleoTaskFormPairs,
				com.liferay.portal.kernel.service.ServiceContext serviceContext)
		throws com.liferay.portal.kernel.exception.PortalException {

		return _kaleoProcessLocalService.addKaleoProcess(
			userId, groupId, ddmStructureId, nameMap, descriptionMap,
			ddmTemplateId, workflowDefinitionName, workflowDefinitionVersion,
			kaleoTaskFormPairs, serviceContext);
	}

	/**
	 * Creates a new kaleo process with the primary key. Does not add the kaleo process to the database.
	 *
	 * @param kaleoProcessId the primary key for the new kaleo process
	 * @return the new kaleo process
	 */
	@Override
	public com.liferay.portal.workflow.kaleo.forms.model.KaleoProcess
		createKaleoProcess(long kaleoProcessId) {

		return _kaleoProcessLocalService.createKaleoProcess(kaleoProcessId);
	}

	/**
	 * @throws PortalException
	 */
	@Override
	public com.liferay.portal.kernel.model.PersistedModel createPersistedModel(
			java.io.Serializable primaryKeyObj)
		throws com.liferay.portal.kernel.exception.PortalException {

		return _kaleoProcessLocalService.createPersistedModel(primaryKeyObj);
	}

	/**
	 * Deletes the kaleo process from the database. Also notifies the appropriate model listeners.
	 *
	 * <p>
	 * <strong>Important:</strong> Inspect KaleoProcessLocalServiceImpl for overloaded versions of the method. If provided, use these entry points to the API, as the implementation logic may require the additional parameters defined there.
	 * </p>
	 *
	 * @param kaleoProcess the kaleo process
	 * @return the kaleo process that was removed
	 * @throws PortalException
	 */
	@Override
	public com.liferay.portal.workflow.kaleo.forms.model.KaleoProcess
			deleteKaleoProcess(
				com.liferay.portal.workflow.kaleo.forms.model.KaleoProcess
					kaleoProcess)
		throws com.liferay.portal.kernel.exception.PortalException {

		return _kaleoProcessLocalService.deleteKaleoProcess(kaleoProcess);
	}

	/**
	 * Deletes the kaleo process with the primary key from the database. Also notifies the appropriate model listeners.
	 *
	 * <p>
	 * <strong>Important:</strong> Inspect KaleoProcessLocalServiceImpl for overloaded versions of the method. If provided, use these entry points to the API, as the implementation logic may require the additional parameters defined there.
	 * </p>
	 *
	 * @param kaleoProcessId the primary key of the kaleo process
	 * @return the kaleo process that was removed
	 * @throws PortalException if a kaleo process with the primary key could not be found
	 */
	@Override
	public com.liferay.portal.workflow.kaleo.forms.model.KaleoProcess
			deleteKaleoProcess(long kaleoProcessId)
		throws com.liferay.portal.kernel.exception.PortalException {

		return _kaleoProcessLocalService.deleteKaleoProcess(kaleoProcessId);
	}

	/**
	 * @throws PortalException
	 */
	@Override
	public com.liferay.portal.kernel.model.PersistedModel deletePersistedModel(
			com.liferay.portal.kernel.model.PersistedModel persistedModel)
		throws com.liferay.portal.kernel.exception.PortalException {

		return _kaleoProcessLocalService.deletePersistedModel(persistedModel);
	}

	@Override
	public <T> T dslQuery(com.liferay.petra.sql.dsl.query.DSLQuery dslQuery) {
		return _kaleoProcessLocalService.dslQuery(dslQuery);
	}

	@Override
	public int dslQueryCount(
		com.liferay.petra.sql.dsl.query.DSLQuery dslQuery) {

		return _kaleoProcessLocalService.dslQueryCount(dslQuery);
	}

	@Override
	public com.liferay.portal.kernel.dao.orm.DynamicQuery dynamicQuery() {
		return _kaleoProcessLocalService.dynamicQuery();
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

		return _kaleoProcessLocalService.dynamicQuery(dynamicQuery);
	}

	/**
	 * Performs a dynamic query on the database and returns a range of the matching rows.
	 *
	 * <p>
	 * Useful when paginating results. Returns a maximum of <code>end - start</code> instances. <code>start</code> and <code>end</code> are not primary keys, they are indexes in the result set. Thus, <code>0</code> refers to the first result in the set. Setting both <code>start</code> and <code>end</code> to <code>com.liferay.portal.kernel.dao.orm.QueryUtil#ALL_POS</code> will return the full result set. If <code>orderByComparator</code> is specified, then the query will include the given ORDER BY logic. If <code>orderByComparator</code> is absent, then the query will include the default ORDER BY logic from <code>com.liferay.portal.workflow.kaleo.forms.model.impl.KaleoProcessModelImpl</code>.
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

		return _kaleoProcessLocalService.dynamicQuery(dynamicQuery, start, end);
	}

	/**
	 * Performs a dynamic query on the database and returns an ordered range of the matching rows.
	 *
	 * <p>
	 * Useful when paginating results. Returns a maximum of <code>end - start</code> instances. <code>start</code> and <code>end</code> are not primary keys, they are indexes in the result set. Thus, <code>0</code> refers to the first result in the set. Setting both <code>start</code> and <code>end</code> to <code>com.liferay.portal.kernel.dao.orm.QueryUtil#ALL_POS</code> will return the full result set. If <code>orderByComparator</code> is specified, then the query will include the given ORDER BY logic. If <code>orderByComparator</code> is absent, then the query will include the default ORDER BY logic from <code>com.liferay.portal.workflow.kaleo.forms.model.impl.KaleoProcessModelImpl</code>.
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

		return _kaleoProcessLocalService.dynamicQuery(
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

		return _kaleoProcessLocalService.dynamicQueryCount(dynamicQuery);
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

		return _kaleoProcessLocalService.dynamicQueryCount(
			dynamicQuery, projection);
	}

	@Override
	public com.liferay.portal.workflow.kaleo.forms.model.KaleoProcess
		fetchKaleoProcess(long kaleoProcessId) {

		return _kaleoProcessLocalService.fetchKaleoProcess(kaleoProcessId);
	}

	/**
	 * Returns the kaleo process matching the UUID and group.
	 *
	 * @param uuid the kaleo process's UUID
	 * @param groupId the primary key of the group
	 * @return the matching kaleo process, or <code>null</code> if a matching kaleo process could not be found
	 */
	@Override
	public com.liferay.portal.workflow.kaleo.forms.model.KaleoProcess
		fetchKaleoProcessByUuidAndGroupId(String uuid, long groupId) {

		return _kaleoProcessLocalService.fetchKaleoProcessByUuidAndGroupId(
			uuid, groupId);
	}

	@Override
	public com.liferay.portal.kernel.dao.orm.ActionableDynamicQuery
		getActionableDynamicQuery() {

		return _kaleoProcessLocalService.getActionableDynamicQuery();
	}

	/**
	 * Returns the Kaleo process matching the DDL record set ID.
	 *
	 * @param ddlRecordSetId the primary key of the DDL record set associated
	 with the Kaleo process
	 * @return the Kaleo process
	 * @throws PortalException if a matching Kaleo process could not be found
	 */
	@Override
	public com.liferay.portal.workflow.kaleo.forms.model.KaleoProcess
			getDDLRecordSetKaleoProcess(long ddlRecordSetId)
		throws com.liferay.portal.kernel.exception.PortalException {

		return _kaleoProcessLocalService.getDDLRecordSetKaleoProcess(
			ddlRecordSetId);
	}

	@Override
	public com.liferay.portal.kernel.dao.orm.ExportActionableDynamicQuery
		getExportActionableDynamicQuery(
			com.liferay.exportimport.kernel.lar.PortletDataContext
				portletDataContext) {

		return _kaleoProcessLocalService.getExportActionableDynamicQuery(
			portletDataContext);
	}

	@Override
	public com.liferay.portal.kernel.dao.orm.IndexableActionableDynamicQuery
		getIndexableActionableDynamicQuery() {

		return _kaleoProcessLocalService.getIndexableActionableDynamicQuery();
	}

	/**
	 * Returns the kaleo process with the primary key.
	 *
	 * @param kaleoProcessId the primary key of the kaleo process
	 * @return the kaleo process
	 * @throws PortalException if a kaleo process with the primary key could not be found
	 */
	@Override
	public com.liferay.portal.workflow.kaleo.forms.model.KaleoProcess
			getKaleoProcess(long kaleoProcessId)
		throws com.liferay.portal.kernel.exception.PortalException {

		return _kaleoProcessLocalService.getKaleoProcess(kaleoProcessId);
	}

	/**
	 * Returns the kaleo process matching the UUID and group.
	 *
	 * @param uuid the kaleo process's UUID
	 * @param groupId the primary key of the group
	 * @return the matching kaleo process
	 * @throws PortalException if a matching kaleo process could not be found
	 */
	@Override
	public com.liferay.portal.workflow.kaleo.forms.model.KaleoProcess
			getKaleoProcessByUuidAndGroupId(String uuid, long groupId)
		throws com.liferay.portal.kernel.exception.PortalException {

		return _kaleoProcessLocalService.getKaleoProcessByUuidAndGroupId(
			uuid, groupId);
	}

	/**
	 * Returns a range of all the kaleo processes.
	 *
	 * <p>
	 * Useful when paginating results. Returns a maximum of <code>end - start</code> instances. <code>start</code> and <code>end</code> are not primary keys, they are indexes in the result set. Thus, <code>0</code> refers to the first result in the set. Setting both <code>start</code> and <code>end</code> to <code>com.liferay.portal.kernel.dao.orm.QueryUtil#ALL_POS</code> will return the full result set. If <code>orderByComparator</code> is specified, then the query will include the given ORDER BY logic. If <code>orderByComparator</code> is absent, then the query will include the default ORDER BY logic from <code>com.liferay.portal.workflow.kaleo.forms.model.impl.KaleoProcessModelImpl</code>.
	 * </p>
	 *
	 * @param start the lower bound of the range of kaleo processes
	 * @param end the upper bound of the range of kaleo processes (not inclusive)
	 * @return the range of kaleo processes
	 */
	@Override
	public java.util.List
		<com.liferay.portal.workflow.kaleo.forms.model.KaleoProcess>
			getKaleoProcesses(int start, int end) {

		return _kaleoProcessLocalService.getKaleoProcesses(start, end);
	}

	/**
	 * Returns all the kaleo processes matching the UUID and company.
	 *
	 * @param uuid the UUID of the kaleo processes
	 * @param companyId the primary key of the company
	 * @return the matching kaleo processes, or an empty list if no matches were found
	 */
	@Override
	public java.util.List
		<com.liferay.portal.workflow.kaleo.forms.model.KaleoProcess>
			getKaleoProcessesByUuidAndCompanyId(String uuid, long companyId) {

		return _kaleoProcessLocalService.getKaleoProcessesByUuidAndCompanyId(
			uuid, companyId);
	}

	/**
	 * Returns a range of kaleo processes matching the UUID and company.
	 *
	 * @param uuid the UUID of the kaleo processes
	 * @param companyId the primary key of the company
	 * @param start the lower bound of the range of kaleo processes
	 * @param end the upper bound of the range of kaleo processes (not inclusive)
	 * @param orderByComparator the comparator to order the results by (optionally <code>null</code>)
	 * @return the range of matching kaleo processes, or an empty list if no matches were found
	 */
	@Override
	public java.util.List
		<com.liferay.portal.workflow.kaleo.forms.model.KaleoProcess>
			getKaleoProcessesByUuidAndCompanyId(
				String uuid, long companyId, int start, int end,
				com.liferay.portal.kernel.util.OrderByComparator
					<com.liferay.portal.workflow.kaleo.forms.model.KaleoProcess>
						orderByComparator) {

		return _kaleoProcessLocalService.getKaleoProcessesByUuidAndCompanyId(
			uuid, companyId, start, end, orderByComparator);
	}

	/**
	 * Returns the number of kaleo processes.
	 *
	 * @return the number of kaleo processes
	 */
	@Override
	public int getKaleoProcessesCount() {
		return _kaleoProcessLocalService.getKaleoProcessesCount();
	}

	/**
	 * Returns the OSGi service identifier.
	 *
	 * @return the OSGi service identifier
	 */
	@Override
	public String getOSGiServiceIdentifier() {
		return _kaleoProcessLocalService.getOSGiServiceIdentifier();
	}

	/**
	 * @throws PortalException
	 */
	@Override
	public com.liferay.portal.kernel.model.PersistedModel getPersistedModel(
			java.io.Serializable primaryKeyObj)
		throws com.liferay.portal.kernel.exception.PortalException {

		return _kaleoProcessLocalService.getPersistedModel(primaryKeyObj);
	}

	/**
	 * Updates the kaleo process in the database or adds it if it does not yet exist. Also notifies the appropriate model listeners.
	 *
	 * <p>
	 * <strong>Important:</strong> Inspect KaleoProcessLocalServiceImpl for overloaded versions of the method. If provided, use these entry points to the API, as the implementation logic may require the additional parameters defined there.
	 * </p>
	 *
	 * @param kaleoProcess the kaleo process
	 * @return the kaleo process that was updated
	 */
	@Override
	public com.liferay.portal.workflow.kaleo.forms.model.KaleoProcess
		updateKaleoProcess(
			com.liferay.portal.workflow.kaleo.forms.model.KaleoProcess
				kaleoProcess) {

		return _kaleoProcessLocalService.updateKaleoProcess(kaleoProcess);
	}

	/**
	 * Updates the Kaleo process.
	 *
	 * @param kaleoProcessId the primary key of the Kaleo process
	 * @param ddmStructureId the primary key of the Kaleo process's DDM
	 structure
	 * @param nameMap the Kaleo process's locales and localized names
	 * @param descriptionMap the Kaleo process's locales and localized
	 descriptions
	 * @param ddmTemplateId the primary key of the Kaleo process's DDM template
	 * @param workflowDefinitionName the Kaleo process's workflow definition
	 name
	 * @param workflowDefinitionVersion the Kaleo process's workflow definition
	 version
	 * @param kaleoTaskFormPairs the Kaleo task form pairs. For more
	 information, see the <code>portal.workflow.kaleo.forms.api</code>
	 module's <code>KaleoTaskFormPairs</code> class.
	 * @param serviceContext the service context to be applied. This can set
	 guest permissions and group permissions for the Kaleo process.
	 * @return the Kaleo process
	 * @throws PortalException if a portal exception occurred
	 */
	@Override
	public com.liferay.portal.workflow.kaleo.forms.model.KaleoProcess
			updateKaleoProcess(
				long kaleoProcessId, long ddmStructureId,
				java.util.Map<java.util.Locale, String> nameMap,
				java.util.Map<java.util.Locale, String> descriptionMap,
				long ddmTemplateId, String workflowDefinitionName,
				int workflowDefinitionVersion,
				com.liferay.portal.workflow.kaleo.forms.model.KaleoTaskFormPairs
					kaleoTaskFormPairs,
				com.liferay.portal.kernel.service.ServiceContext serviceContext)
		throws com.liferay.portal.kernel.exception.PortalException {

		return _kaleoProcessLocalService.updateKaleoProcess(
			kaleoProcessId, ddmStructureId, nameMap, descriptionMap,
			ddmTemplateId, workflowDefinitionName, workflowDefinitionVersion,
			kaleoTaskFormPairs, serviceContext);
	}

	@Override
	public BasePersistence<?> getBasePersistence() {
		return _kaleoProcessLocalService.getBasePersistence();
	}

	@Override
	public KaleoProcessLocalService getWrappedService() {
		return _kaleoProcessLocalService;
	}

	@Override
	public void setWrappedService(
		KaleoProcessLocalService kaleoProcessLocalService) {

		_kaleoProcessLocalService = kaleoProcessLocalService;
	}

	private KaleoProcessLocalService _kaleoProcessLocalService;

}
// LIFERAY-SERVICE-BUILDER-HASH:-981113341