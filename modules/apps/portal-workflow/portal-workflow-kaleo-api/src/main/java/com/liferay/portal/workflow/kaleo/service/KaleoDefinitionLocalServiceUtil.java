/**
 * SPDX-FileCopyrightText: (c) 2000 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

package com.liferay.portal.workflow.kaleo.service;

import com.liferay.petra.sql.dsl.query.DSLQuery;
import com.liferay.portal.kernel.dao.orm.DynamicQuery;
import com.liferay.portal.kernel.exception.PortalException;
import com.liferay.portal.kernel.model.PersistedModel;
import com.liferay.portal.kernel.module.service.Snapshot;
import com.liferay.portal.kernel.util.OrderByComparator;
import com.liferay.portal.workflow.kaleo.model.KaleoDefinition;

import java.io.Serializable;

import java.util.List;

/**
 * Provides the local service utility for KaleoDefinition. This utility wraps
 * <code>com.liferay.portal.workflow.kaleo.service.impl.KaleoDefinitionLocalServiceImpl</code> and
 * is an access point for service operations in application layer code running
 * on the local server. Methods of this service will not have security checks
 * based on the propagated JAAS credentials because this service can only be
 * accessed from within the same VM.
 *
 * @author Brian Wing Shun Chan
 * @see KaleoDefinitionLocalService
 * @generated
 */
public class KaleoDefinitionLocalServiceUtil {

	/*
	 * NOTE FOR DEVELOPERS:
	 *
	 * Never modify this class directly. Add custom service methods to <code>com.liferay.portal.workflow.kaleo.service.impl.KaleoDefinitionLocalServiceImpl</code> and rerun ServiceBuilder to regenerate this class.
	 */
	public static KaleoDefinition activateKaleoDefinition(
			long kaleoDefinitionId, long kaleoDefinitionVersionId,
			long startKaleoNodeId,
			com.liferay.portal.kernel.service.ServiceContext serviceContext)
		throws PortalException {

		return getService().activateKaleoDefinition(
			kaleoDefinitionId, kaleoDefinitionVersionId, startKaleoNodeId,
			serviceContext);
	}

	public static KaleoDefinition activateKaleoDefinition(
			long kaleoDefinitionId,
			com.liferay.portal.kernel.service.ServiceContext serviceContext)
		throws PortalException {

		return getService().activateKaleoDefinition(
			kaleoDefinitionId, serviceContext);
	}

	public static KaleoDefinition activateKaleoDefinition(
			String name, int version,
			com.liferay.portal.kernel.service.ServiceContext serviceContext)
		throws PortalException {

		return getService().activateKaleoDefinition(
			name, version, serviceContext);
	}

	/**
	 * Adds the kaleo definition to the database. Also notifies the appropriate model listeners.
	 *
	 * <p>
	 * <strong>Important:</strong> Inspect KaleoDefinitionLocalServiceImpl for overloaded versions of the method. If provided, use these entry points to the API, as the implementation logic may require the additional parameters defined there.
	 * </p>
	 *
	 * @param kaleoDefinition the kaleo definition
	 * @return the kaleo definition that was added
	 */
	public static KaleoDefinition addKaleoDefinition(
		KaleoDefinition kaleoDefinition) {

		return getService().addKaleoDefinition(kaleoDefinition);
	}

	public static KaleoDefinition addKaleoDefinition(
			String externalReferenceCode, String name, String title,
			String description, String content, String scope, boolean system,
			int version,
			com.liferay.portal.kernel.service.ServiceContext serviceContext)
		throws PortalException {

		return getService().addKaleoDefinition(
			externalReferenceCode, name, title, description, content, scope,
			system, version, serviceContext);
	}

	/**
	 * Creates a new kaleo definition with the primary key. Does not add the kaleo definition to the database.
	 *
	 * @param kaleoDefinitionId the primary key for the new kaleo definition
	 * @return the new kaleo definition
	 */
	public static KaleoDefinition createKaleoDefinition(
		long kaleoDefinitionId) {

		return getService().createKaleoDefinition(kaleoDefinitionId);
	}

	/**
	 * @throws PortalException
	 */
	public static PersistedModel createPersistedModel(
			Serializable primaryKeyObj)
		throws PortalException {

		return getService().createPersistedModel(primaryKeyObj);
	}

	public static KaleoDefinition deactivateKaleoDefinition(
			String name, int version,
			com.liferay.portal.kernel.service.ServiceContext serviceContext)
		throws PortalException {

		return getService().deactivateKaleoDefinition(
			name, version, serviceContext);
	}

	public static void deleteCompanyKaleoDefinitions(long companyId) {
		getService().deleteCompanyKaleoDefinitions(companyId);
	}

	/**
	 * Deletes the kaleo definition from the database. Also notifies the appropriate model listeners.
	 *
	 * <p>
	 * <strong>Important:</strong> Inspect KaleoDefinitionLocalServiceImpl for overloaded versions of the method. If provided, use these entry points to the API, as the implementation logic may require the additional parameters defined there.
	 * </p>
	 *
	 * @param kaleoDefinition the kaleo definition
	 * @return the kaleo definition that was removed
	 */
	public static KaleoDefinition deleteKaleoDefinition(
		KaleoDefinition kaleoDefinition) {

		return getService().deleteKaleoDefinition(kaleoDefinition);
	}

	/**
	 * Deletes the kaleo definition with the primary key from the database. Also notifies the appropriate model listeners.
	 *
	 * <p>
	 * <strong>Important:</strong> Inspect KaleoDefinitionLocalServiceImpl for overloaded versions of the method. If provided, use these entry points to the API, as the implementation logic may require the additional parameters defined there.
	 * </p>
	 *
	 * @param kaleoDefinitionId the primary key of the kaleo definition
	 * @return the kaleo definition that was removed
	 * @throws PortalException if a kaleo definition with the primary key could not be found
	 */
	public static KaleoDefinition deleteKaleoDefinition(long kaleoDefinitionId)
		throws PortalException {

		return getService().deleteKaleoDefinition(kaleoDefinitionId);
	}

	public static void deleteKaleoDefinition(
			String name,
			com.liferay.portal.kernel.service.ServiceContext serviceContext)
		throws PortalException {

		getService().deleteKaleoDefinition(name, serviceContext);
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
	 * Useful when paginating results. Returns a maximum of <code>end - start</code> instances. <code>start</code> and <code>end</code> are not primary keys, they are indexes in the result set. Thus, <code>0</code> refers to the first result in the set. Setting both <code>start</code> and <code>end</code> to <code>com.liferay.portal.kernel.dao.orm.QueryUtil#ALL_POS</code> will return the full result set. If <code>orderByComparator</code> is specified, then the query will include the given ORDER BY logic. If <code>orderByComparator</code> is absent, then the query will include the default ORDER BY logic from <code>com.liferay.portal.workflow.kaleo.model.impl.KaleoDefinitionModelImpl</code>.
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
	 * Useful when paginating results. Returns a maximum of <code>end - start</code> instances. <code>start</code> and <code>end</code> are not primary keys, they are indexes in the result set. Thus, <code>0</code> refers to the first result in the set. Setting both <code>start</code> and <code>end</code> to <code>com.liferay.portal.kernel.dao.orm.QueryUtil#ALL_POS</code> will return the full result set. If <code>orderByComparator</code> is specified, then the query will include the given ORDER BY logic. If <code>orderByComparator</code> is absent, then the query will include the default ORDER BY logic from <code>com.liferay.portal.workflow.kaleo.model.impl.KaleoDefinitionModelImpl</code>.
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

	public static KaleoDefinition fetchKaleoDefinition(long kaleoDefinitionId) {
		return getService().fetchKaleoDefinition(kaleoDefinitionId);
	}

	public static KaleoDefinition fetchKaleoDefinition(
		String name,
		com.liferay.portal.kernel.service.ServiceContext serviceContext) {

		return getService().fetchKaleoDefinition(name, serviceContext);
	}

	public static KaleoDefinition fetchKaleoDefinitionByExternalReferenceCode(
		String externalReferenceCode, long companyId) {

		return getService().fetchKaleoDefinitionByExternalReferenceCode(
			externalReferenceCode, companyId);
	}

	/**
	 * Returns the kaleo definition matching the UUID and group.
	 *
	 * @param uuid the kaleo definition's UUID
	 * @param groupId the primary key of the group
	 * @return the matching kaleo definition, or <code>null</code> if a matching kaleo definition could not be found
	 */
	public static KaleoDefinition fetchKaleoDefinitionByUuidAndGroupId(
		String uuid, long groupId) {

		return getService().fetchKaleoDefinitionByUuidAndGroupId(uuid, groupId);
	}

	public static com.liferay.portal.kernel.dao.orm.ActionableDynamicQuery
		getActionableDynamicQuery() {

		return getService().getActionableDynamicQuery();
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
	 * Returns the kaleo definition with the primary key.
	 *
	 * @param kaleoDefinitionId the primary key of the kaleo definition
	 * @return the kaleo definition
	 * @throws PortalException if a kaleo definition with the primary key could not be found
	 */
	public static KaleoDefinition getKaleoDefinition(long kaleoDefinitionId)
		throws PortalException {

		return getService().getKaleoDefinition(kaleoDefinitionId);
	}

	public static KaleoDefinition getKaleoDefinition(
			String name,
			com.liferay.portal.kernel.service.ServiceContext serviceContext)
		throws PortalException {

		return getService().getKaleoDefinition(name, serviceContext);
	}

	public static KaleoDefinition getKaleoDefinitionByExternalReferenceCode(
			String externalReferenceCode, long companyId)
		throws PortalException {

		return getService().getKaleoDefinitionByExternalReferenceCode(
			externalReferenceCode, companyId);
	}

	/**
	 * Returns the kaleo definition matching the UUID and group.
	 *
	 * @param uuid the kaleo definition's UUID
	 * @param groupId the primary key of the group
	 * @return the matching kaleo definition
	 * @throws PortalException if a matching kaleo definition could not be found
	 */
	public static KaleoDefinition getKaleoDefinitionByUuidAndGroupId(
			String uuid, long groupId)
		throws PortalException {

		return getService().getKaleoDefinitionByUuidAndGroupId(uuid, groupId);
	}

	public static List<KaleoDefinition> getKaleoDefinitions(
		boolean active, int start, int end) {

		return getService().getKaleoDefinitions(active, start, end);
	}

	public static List<KaleoDefinition> getKaleoDefinitions(
		boolean active, int start, int end,
		OrderByComparator<KaleoDefinition> orderByComparator,
		com.liferay.portal.kernel.service.ServiceContext serviceContext) {

		return getService().getKaleoDefinitions(
			active, start, end, orderByComparator, serviceContext);
	}

	/**
	 * Returns a range of all the kaleo definitions.
	 *
	 * <p>
	 * Useful when paginating results. Returns a maximum of <code>end - start</code> instances. <code>start</code> and <code>end</code> are not primary keys, they are indexes in the result set. Thus, <code>0</code> refers to the first result in the set. Setting both <code>start</code> and <code>end</code> to <code>com.liferay.portal.kernel.dao.orm.QueryUtil#ALL_POS</code> will return the full result set. If <code>orderByComparator</code> is specified, then the query will include the given ORDER BY logic. If <code>orderByComparator</code> is absent, then the query will include the default ORDER BY logic from <code>com.liferay.portal.workflow.kaleo.model.impl.KaleoDefinitionModelImpl</code>.
	 * </p>
	 *
	 * @param start the lower bound of the range of kaleo definitions
	 * @param end the upper bound of the range of kaleo definitions (not inclusive)
	 * @return the range of kaleo definitions
	 */
	public static List<KaleoDefinition> getKaleoDefinitions(
		int start, int end) {

		return getService().getKaleoDefinitions(start, end);
	}

	public static List<KaleoDefinition> getKaleoDefinitions(
		int start, int end,
		OrderByComparator<KaleoDefinition> orderByComparator,
		com.liferay.portal.kernel.service.ServiceContext serviceContext) {

		return getService().getKaleoDefinitions(
			start, end, orderByComparator, serviceContext);
	}

	/**
	 * Returns all the kaleo definitions matching the UUID and company.
	 *
	 * @param uuid the UUID of the kaleo definitions
	 * @param companyId the primary key of the company
	 * @return the matching kaleo definitions, or an empty list if no matches were found
	 */
	public static List<KaleoDefinition> getKaleoDefinitionsByUuidAndCompanyId(
		String uuid, long companyId) {

		return getService().getKaleoDefinitionsByUuidAndCompanyId(
			uuid, companyId);
	}

	/**
	 * Returns a range of kaleo definitions matching the UUID and company.
	 *
	 * @param uuid the UUID of the kaleo definitions
	 * @param companyId the primary key of the company
	 * @param start the lower bound of the range of kaleo definitions
	 * @param end the upper bound of the range of kaleo definitions (not inclusive)
	 * @param orderByComparator the comparator to order the results by (optionally <code>null</code>)
	 * @return the range of matching kaleo definitions, or an empty list if no matches were found
	 */
	public static List<KaleoDefinition> getKaleoDefinitionsByUuidAndCompanyId(
		String uuid, long companyId, int start, int end,
		OrderByComparator<KaleoDefinition> orderByComparator) {

		return getService().getKaleoDefinitionsByUuidAndCompanyId(
			uuid, companyId, start, end, orderByComparator);
	}

	/**
	 * Returns the number of kaleo definitions.
	 *
	 * @return the number of kaleo definitions
	 */
	public static int getKaleoDefinitionsCount() {
		return getService().getKaleoDefinitionsCount();
	}

	public static int getKaleoDefinitionsCount(
		boolean active,
		com.liferay.portal.kernel.service.ServiceContext serviceContext) {

		return getService().getKaleoDefinitionsCount(active, serviceContext);
	}

	public static int getKaleoDefinitionsCount(
		com.liferay.portal.kernel.service.ServiceContext serviceContext) {

		return getService().getKaleoDefinitionsCount(serviceContext);
	}

	public static int getKaleoDefinitionsCount(
		String name, boolean active,
		com.liferay.portal.kernel.service.ServiceContext serviceContext) {

		return getService().getKaleoDefinitionsCount(
			name, active, serviceContext);
	}

	public static int getKaleoDefinitionsCount(
		String name,
		com.liferay.portal.kernel.service.ServiceContext serviceContext) {

		return getService().getKaleoDefinitionsCount(name, serviceContext);
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

	public static List<KaleoDefinition> getScopeKaleoDefinitions(
		String scope, boolean active, int start, int end,
		OrderByComparator<KaleoDefinition> orderByComparator,
		com.liferay.portal.kernel.service.ServiceContext serviceContext) {

		return getService().getScopeKaleoDefinitions(
			scope, active, start, end, orderByComparator, serviceContext);
	}

	public static List<KaleoDefinition> getScopeKaleoDefinitions(
		String scope, int start, int end,
		OrderByComparator<KaleoDefinition> orderByComparator,
		com.liferay.portal.kernel.service.ServiceContext serviceContext) {

		return getService().getScopeKaleoDefinitions(
			scope, start, end, orderByComparator, serviceContext);
	}

	public static int getScopeKaleoDefinitionsCount(
		String scope, boolean active,
		com.liferay.portal.kernel.service.ServiceContext serviceContext) {

		return getService().getScopeKaleoDefinitionsCount(
			scope, active, serviceContext);
	}

	public static int getScopeKaleoDefinitionsCount(
		String scope,
		com.liferay.portal.kernel.service.ServiceContext serviceContext) {

		return getService().getScopeKaleoDefinitionsCount(
			scope, serviceContext);
	}

	public static KaleoDefinition updatedKaleoDefinition(
			String externalReferenceCode, long kaleoDefinitionId, String title,
			String description, String content, boolean system,
			com.liferay.portal.kernel.service.ServiceContext serviceContext)
		throws PortalException {

		return getService().updatedKaleoDefinition(
			externalReferenceCode, kaleoDefinitionId, title, description,
			content, system, serviceContext);
	}

	/**
	 * Updates the kaleo definition in the database or adds it if it does not yet exist. Also notifies the appropriate model listeners.
	 *
	 * <p>
	 * <strong>Important:</strong> Inspect KaleoDefinitionLocalServiceImpl for overloaded versions of the method. If provided, use these entry points to the API, as the implementation logic may require the additional parameters defined there.
	 * </p>
	 *
	 * @param kaleoDefinition the kaleo definition
	 * @return the kaleo definition that was updated
	 */
	public static KaleoDefinition updateKaleoDefinition(
		KaleoDefinition kaleoDefinition) {

		return getService().updateKaleoDefinition(kaleoDefinition);
	}

	public static KaleoDefinitionLocalService getService() {
		return _serviceSnapshot.get();
	}

	private static final Snapshot<KaleoDefinitionLocalService>
		_serviceSnapshot = new Snapshot<>(
			KaleoDefinitionLocalServiceUtil.class,
			KaleoDefinitionLocalService.class);

}
// LIFERAY-SERVICE-BUILDER-HASH:-333319507