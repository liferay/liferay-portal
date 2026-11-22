/**
 * SPDX-FileCopyrightText: (c) 2000 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

package com.liferay.portal.workflow.kaleo.forms.service;

import com.liferay.petra.sql.dsl.query.DSLQuery;
import com.liferay.portal.kernel.dao.orm.DynamicQuery;
import com.liferay.portal.kernel.exception.PortalException;
import com.liferay.portal.kernel.model.PersistedModel;
import com.liferay.portal.kernel.module.service.Snapshot;
import com.liferay.portal.kernel.util.OrderByComparator;
import com.liferay.portal.workflow.kaleo.forms.model.KaleoProcess;

import java.io.Serializable;

import java.util.List;
import java.util.Map;

/**
 * Provides the local service utility for KaleoProcess. This utility wraps
 * <code>com.liferay.portal.workflow.kaleo.forms.service.impl.KaleoProcessLocalServiceImpl</code> and
 * is an access point for service operations in application layer code running
 * on the local server. Methods of this service will not have security checks
 * based on the propagated JAAS credentials because this service can only be
 * accessed from within the same VM.
 *
 * @author Marcellus Tavares
 * @see KaleoProcessLocalService
 * @generated
 */
public class KaleoProcessLocalServiceUtil {

	/*
	 * NOTE FOR DEVELOPERS:
	 *
	 * Never modify this class directly. Add custom service methods to <code>com.liferay.portal.workflow.kaleo.forms.service.impl.KaleoProcessLocalServiceImpl</code> and rerun ServiceBuilder to regenerate this class.
	 */

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
	public static KaleoProcess addKaleoProcess(KaleoProcess kaleoProcess) {
		return getService().addKaleoProcess(kaleoProcess);
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
	public static KaleoProcess addKaleoProcess(
			long userId, long groupId, long ddmStructureId,
			Map<java.util.Locale, String> nameMap,
			Map<java.util.Locale, String> descriptionMap, long ddmTemplateId,
			String workflowDefinitionName, int workflowDefinitionVersion,
			com.liferay.portal.workflow.kaleo.forms.model.KaleoTaskFormPairs
				kaleoTaskFormPairs,
			com.liferay.portal.kernel.service.ServiceContext serviceContext)
		throws PortalException {

		return getService().addKaleoProcess(
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
	public static KaleoProcess createKaleoProcess(long kaleoProcessId) {
		return getService().createKaleoProcess(kaleoProcessId);
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
	public static KaleoProcess deleteKaleoProcess(KaleoProcess kaleoProcess)
		throws PortalException {

		return getService().deleteKaleoProcess(kaleoProcess);
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
	public static KaleoProcess deleteKaleoProcess(long kaleoProcessId)
		throws PortalException {

		return getService().deleteKaleoProcess(kaleoProcessId);
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
	 * Useful when paginating results. Returns a maximum of <code>end - start</code> instances. <code>start</code> and <code>end</code> are not primary keys, they are indexes in the result set. Thus, <code>0</code> refers to the first result in the set. Setting both <code>start</code> and <code>end</code> to <code>com.liferay.portal.kernel.dao.orm.QueryUtil#ALL_POS</code> will return the full result set. If <code>orderByComparator</code> is specified, then the query will include the given ORDER BY logic. If <code>orderByComparator</code> is absent, then the query will include the default ORDER BY logic from <code>com.liferay.portal.workflow.kaleo.forms.model.impl.KaleoProcessModelImpl</code>.
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
	 * Useful when paginating results. Returns a maximum of <code>end - start</code> instances. <code>start</code> and <code>end</code> are not primary keys, they are indexes in the result set. Thus, <code>0</code> refers to the first result in the set. Setting both <code>start</code> and <code>end</code> to <code>com.liferay.portal.kernel.dao.orm.QueryUtil#ALL_POS</code> will return the full result set. If <code>orderByComparator</code> is specified, then the query will include the given ORDER BY logic. If <code>orderByComparator</code> is absent, then the query will include the default ORDER BY logic from <code>com.liferay.portal.workflow.kaleo.forms.model.impl.KaleoProcessModelImpl</code>.
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

	public static KaleoProcess fetchKaleoProcess(long kaleoProcessId) {
		return getService().fetchKaleoProcess(kaleoProcessId);
	}

	/**
	 * Returns the kaleo process matching the UUID and group.
	 *
	 * @param uuid the kaleo process's UUID
	 * @param groupId the primary key of the group
	 * @return the matching kaleo process, or <code>null</code> if a matching kaleo process could not be found
	 */
	public static KaleoProcess fetchKaleoProcessByUuidAndGroupId(
		String uuid, long groupId) {

		return getService().fetchKaleoProcessByUuidAndGroupId(uuid, groupId);
	}

	public static com.liferay.portal.kernel.dao.orm.ActionableDynamicQuery
		getActionableDynamicQuery() {

		return getService().getActionableDynamicQuery();
	}

	/**
	 * Returns the Kaleo process matching the DDL record set ID.
	 *
	 * @param ddlRecordSetId the primary key of the DDL record set associated
	 with the Kaleo process
	 * @return the Kaleo process
	 * @throws PortalException if a matching Kaleo process could not be found
	 */
	public static KaleoProcess getDDLRecordSetKaleoProcess(long ddlRecordSetId)
		throws PortalException {

		return getService().getDDLRecordSetKaleoProcess(ddlRecordSetId);
	}

	public static com.liferay.portal.kernel.dao.orm.ExportActionableDynamicQuery
		getExportActionableDynamicQuery(
			com.liferay.exportimport.kernel.lar.PortletDataContext
				portletDataContext) {

		return getService().getExportActionableDynamicQuery(portletDataContext);
	}

	public static
		com.liferay.portal.kernel.dao.orm.IndexableActionableDynamicQuery
			getIndexableActionableDynamicQuery() {

		return getService().getIndexableActionableDynamicQuery();
	}

	/**
	 * Returns the kaleo process with the primary key.
	 *
	 * @param kaleoProcessId the primary key of the kaleo process
	 * @return the kaleo process
	 * @throws PortalException if a kaleo process with the primary key could not be found
	 */
	public static KaleoProcess getKaleoProcess(long kaleoProcessId)
		throws PortalException {

		return getService().getKaleoProcess(kaleoProcessId);
	}

	/**
	 * Returns the kaleo process matching the UUID and group.
	 *
	 * @param uuid the kaleo process's UUID
	 * @param groupId the primary key of the group
	 * @return the matching kaleo process
	 * @throws PortalException if a matching kaleo process could not be found
	 */
	public static KaleoProcess getKaleoProcessByUuidAndGroupId(
			String uuid, long groupId)
		throws PortalException {

		return getService().getKaleoProcessByUuidAndGroupId(uuid, groupId);
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
	public static List<KaleoProcess> getKaleoProcesses(int start, int end) {
		return getService().getKaleoProcesses(start, end);
	}

	/**
	 * Returns all the kaleo processes matching the UUID and company.
	 *
	 * @param uuid the UUID of the kaleo processes
	 * @param companyId the primary key of the company
	 * @return the matching kaleo processes, or an empty list if no matches were found
	 */
	public static List<KaleoProcess> getKaleoProcessesByUuidAndCompanyId(
		String uuid, long companyId) {

		return getService().getKaleoProcessesByUuidAndCompanyId(
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
	public static List<KaleoProcess> getKaleoProcessesByUuidAndCompanyId(
		String uuid, long companyId, int start, int end,
		OrderByComparator<KaleoProcess> orderByComparator) {

		return getService().getKaleoProcessesByUuidAndCompanyId(
			uuid, companyId, start, end, orderByComparator);
	}

	/**
	 * Returns the number of kaleo processes.
	 *
	 * @return the number of kaleo processes
	 */
	public static int getKaleoProcessesCount() {
		return getService().getKaleoProcessesCount();
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
	public static KaleoProcess updateKaleoProcess(KaleoProcess kaleoProcess) {
		return getService().updateKaleoProcess(kaleoProcess);
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
	public static KaleoProcess updateKaleoProcess(
			long kaleoProcessId, long ddmStructureId,
			Map<java.util.Locale, String> nameMap,
			Map<java.util.Locale, String> descriptionMap, long ddmTemplateId,
			String workflowDefinitionName, int workflowDefinitionVersion,
			com.liferay.portal.workflow.kaleo.forms.model.KaleoTaskFormPairs
				kaleoTaskFormPairs,
			com.liferay.portal.kernel.service.ServiceContext serviceContext)
		throws PortalException {

		return getService().updateKaleoProcess(
			kaleoProcessId, ddmStructureId, nameMap, descriptionMap,
			ddmTemplateId, workflowDefinitionName, workflowDefinitionVersion,
			kaleoTaskFormPairs, serviceContext);
	}

	public static KaleoProcessLocalService getService() {
		return _serviceSnapshot.get();
	}

	private static final Snapshot<KaleoProcessLocalService> _serviceSnapshot =
		new Snapshot<>(
			KaleoProcessLocalServiceUtil.class, KaleoProcessLocalService.class);

}
// LIFERAY-SERVICE-BUILDER-HASH:92150814