/**
 * SPDX-FileCopyrightText: (c) 2000 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

package com.liferay.portal.workflow.kaleo.service;

import com.liferay.petra.function.UnsafeFunction;
import com.liferay.portal.kernel.service.ServiceWrapper;
import com.liferay.portal.kernel.service.persistence.BasePersistence;
import com.liferay.portal.kernel.service.persistence.change.tracking.CTPersistence;
import com.liferay.portal.workflow.kaleo.model.KaleoDefinition;

/**
 * Provides a wrapper for {@link KaleoDefinitionLocalService}.
 *
 * @author Brian Wing Shun Chan
 * @see KaleoDefinitionLocalService
 * @generated
 */
public class KaleoDefinitionLocalServiceWrapper
	implements KaleoDefinitionLocalService,
			   ServiceWrapper<KaleoDefinitionLocalService> {

	public KaleoDefinitionLocalServiceWrapper() {
		this(null);
	}

	public KaleoDefinitionLocalServiceWrapper(
		KaleoDefinitionLocalService kaleoDefinitionLocalService) {

		_kaleoDefinitionLocalService = kaleoDefinitionLocalService;
	}

	@Override
	public KaleoDefinition activateKaleoDefinition(
			long kaleoDefinitionId, long kaleoDefinitionVersionId,
			long startKaleoNodeId,
			com.liferay.portal.kernel.service.ServiceContext serviceContext)
		throws com.liferay.portal.kernel.exception.PortalException {

		return _kaleoDefinitionLocalService.activateKaleoDefinition(
			kaleoDefinitionId, kaleoDefinitionVersionId, startKaleoNodeId,
			serviceContext);
	}

	@Override
	public KaleoDefinition activateKaleoDefinition(
			long kaleoDefinitionId,
			com.liferay.portal.kernel.service.ServiceContext serviceContext)
		throws com.liferay.portal.kernel.exception.PortalException {

		return _kaleoDefinitionLocalService.activateKaleoDefinition(
			kaleoDefinitionId, serviceContext);
	}

	@Override
	public KaleoDefinition activateKaleoDefinition(
			String name, int version,
			com.liferay.portal.kernel.service.ServiceContext serviceContext)
		throws com.liferay.portal.kernel.exception.PortalException {

		return _kaleoDefinitionLocalService.activateKaleoDefinition(
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
	@Override
	public KaleoDefinition addKaleoDefinition(KaleoDefinition kaleoDefinition) {
		return _kaleoDefinitionLocalService.addKaleoDefinition(kaleoDefinition);
	}

	@Override
	public KaleoDefinition addKaleoDefinition(
			String externalReferenceCode, String name, String title,
			String description, String content, String scope, boolean system,
			int version,
			com.liferay.portal.kernel.service.ServiceContext serviceContext)
		throws com.liferay.portal.kernel.exception.PortalException {

		return _kaleoDefinitionLocalService.addKaleoDefinition(
			externalReferenceCode, name, title, description, content, scope,
			system, version, serviceContext);
	}

	/**
	 * Creates a new kaleo definition with the primary key. Does not add the kaleo definition to the database.
	 *
	 * @param kaleoDefinitionId the primary key for the new kaleo definition
	 * @return the new kaleo definition
	 */
	@Override
	public KaleoDefinition createKaleoDefinition(long kaleoDefinitionId) {
		return _kaleoDefinitionLocalService.createKaleoDefinition(
			kaleoDefinitionId);
	}

	/**
	 * @throws PortalException
	 */
	@Override
	public com.liferay.portal.kernel.model.PersistedModel createPersistedModel(
			java.io.Serializable primaryKeyObj)
		throws com.liferay.portal.kernel.exception.PortalException {

		return _kaleoDefinitionLocalService.createPersistedModel(primaryKeyObj);
	}

	@Override
	public KaleoDefinition deactivateKaleoDefinition(
			String name, int version,
			com.liferay.portal.kernel.service.ServiceContext serviceContext)
		throws com.liferay.portal.kernel.exception.PortalException {

		return _kaleoDefinitionLocalService.deactivateKaleoDefinition(
			name, version, serviceContext);
	}

	@Override
	public void deleteCompanyKaleoDefinitions(long companyId) {
		_kaleoDefinitionLocalService.deleteCompanyKaleoDefinitions(companyId);
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
	@Override
	public KaleoDefinition deleteKaleoDefinition(
		KaleoDefinition kaleoDefinition) {

		return _kaleoDefinitionLocalService.deleteKaleoDefinition(
			kaleoDefinition);
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
	@Override
	public KaleoDefinition deleteKaleoDefinition(long kaleoDefinitionId)
		throws com.liferay.portal.kernel.exception.PortalException {

		return _kaleoDefinitionLocalService.deleteKaleoDefinition(
			kaleoDefinitionId);
	}

	@Override
	public void deleteKaleoDefinition(
			String name,
			com.liferay.portal.kernel.service.ServiceContext serviceContext)
		throws com.liferay.portal.kernel.exception.PortalException {

		_kaleoDefinitionLocalService.deleteKaleoDefinition(
			name, serviceContext);
	}

	/**
	 * @throws PortalException
	 */
	@Override
	public com.liferay.portal.kernel.model.PersistedModel deletePersistedModel(
			com.liferay.portal.kernel.model.PersistedModel persistedModel)
		throws com.liferay.portal.kernel.exception.PortalException {

		return _kaleoDefinitionLocalService.deletePersistedModel(
			persistedModel);
	}

	@Override
	public <T> T dslQuery(com.liferay.petra.sql.dsl.query.DSLQuery dslQuery) {
		return _kaleoDefinitionLocalService.dslQuery(dslQuery);
	}

	@Override
	public int dslQueryCount(
		com.liferay.petra.sql.dsl.query.DSLQuery dslQuery) {

		return _kaleoDefinitionLocalService.dslQueryCount(dslQuery);
	}

	@Override
	public com.liferay.portal.kernel.dao.orm.DynamicQuery dynamicQuery() {
		return _kaleoDefinitionLocalService.dynamicQuery();
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

		return _kaleoDefinitionLocalService.dynamicQuery(dynamicQuery);
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
	@Override
	public <T> java.util.List<T> dynamicQuery(
		com.liferay.portal.kernel.dao.orm.DynamicQuery dynamicQuery, int start,
		int end) {

		return _kaleoDefinitionLocalService.dynamicQuery(
			dynamicQuery, start, end);
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
	@Override
	public <T> java.util.List<T> dynamicQuery(
		com.liferay.portal.kernel.dao.orm.DynamicQuery dynamicQuery, int start,
		int end,
		com.liferay.portal.kernel.util.OrderByComparator<T> orderByComparator) {

		return _kaleoDefinitionLocalService.dynamicQuery(
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

		return _kaleoDefinitionLocalService.dynamicQueryCount(dynamicQuery);
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

		return _kaleoDefinitionLocalService.dynamicQueryCount(
			dynamicQuery, projection);
	}

	@Override
	public KaleoDefinition fetchKaleoDefinition(long kaleoDefinitionId) {
		return _kaleoDefinitionLocalService.fetchKaleoDefinition(
			kaleoDefinitionId);
	}

	@Override
	public KaleoDefinition fetchKaleoDefinition(
		String name,
		com.liferay.portal.kernel.service.ServiceContext serviceContext) {

		return _kaleoDefinitionLocalService.fetchKaleoDefinition(
			name, serviceContext);
	}

	@Override
	public KaleoDefinition fetchKaleoDefinitionByExternalReferenceCode(
		String externalReferenceCode, long companyId) {

		return _kaleoDefinitionLocalService.
			fetchKaleoDefinitionByExternalReferenceCode(
				externalReferenceCode, companyId);
	}

	/**
	 * Returns the kaleo definition matching the UUID and group.
	 *
	 * @param uuid the kaleo definition's UUID
	 * @param groupId the primary key of the group
	 * @return the matching kaleo definition, or <code>null</code> if a matching kaleo definition could not be found
	 */
	@Override
	public KaleoDefinition fetchKaleoDefinitionByUuidAndGroupId(
		String uuid, long groupId) {

		return _kaleoDefinitionLocalService.
			fetchKaleoDefinitionByUuidAndGroupId(uuid, groupId);
	}

	@Override
	public com.liferay.portal.kernel.dao.orm.ActionableDynamicQuery
		getActionableDynamicQuery() {

		return _kaleoDefinitionLocalService.getActionableDynamicQuery();
	}

	@Override
	public com.liferay.portal.kernel.dao.orm.ExportActionableDynamicQuery
		getExportActionableDynamicQuery(
			com.liferay.exportimport.kernel.lar.PortletDataContext
				portletDataContext) {

		return _kaleoDefinitionLocalService.getExportActionableDynamicQuery(
			portletDataContext);
	}

	@Override
	public com.liferay.portal.kernel.dao.orm.IndexableActionableDynamicQuery
		getIndexableActionableDynamicQuery() {

		return _kaleoDefinitionLocalService.
			getIndexableActionableDynamicQuery();
	}

	/**
	 * Returns the kaleo definition with the primary key.
	 *
	 * @param kaleoDefinitionId the primary key of the kaleo definition
	 * @return the kaleo definition
	 * @throws PortalException if a kaleo definition with the primary key could not be found
	 */
	@Override
	public KaleoDefinition getKaleoDefinition(long kaleoDefinitionId)
		throws com.liferay.portal.kernel.exception.PortalException {

		return _kaleoDefinitionLocalService.getKaleoDefinition(
			kaleoDefinitionId);
	}

	@Override
	public KaleoDefinition getKaleoDefinition(
			String name,
			com.liferay.portal.kernel.service.ServiceContext serviceContext)
		throws com.liferay.portal.kernel.exception.PortalException {

		return _kaleoDefinitionLocalService.getKaleoDefinition(
			name, serviceContext);
	}

	@Override
	public KaleoDefinition getKaleoDefinitionByExternalReferenceCode(
			String externalReferenceCode, long companyId)
		throws com.liferay.portal.kernel.exception.PortalException {

		return _kaleoDefinitionLocalService.
			getKaleoDefinitionByExternalReferenceCode(
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
	@Override
	public KaleoDefinition getKaleoDefinitionByUuidAndGroupId(
			String uuid, long groupId)
		throws com.liferay.portal.kernel.exception.PortalException {

		return _kaleoDefinitionLocalService.getKaleoDefinitionByUuidAndGroupId(
			uuid, groupId);
	}

	@Override
	public java.util.List<KaleoDefinition> getKaleoDefinitions(
		boolean active, int start, int end) {

		return _kaleoDefinitionLocalService.getKaleoDefinitions(
			active, start, end);
	}

	@Override
	public java.util.List<KaleoDefinition> getKaleoDefinitions(
		boolean active, int start, int end,
		com.liferay.portal.kernel.util.OrderByComparator<KaleoDefinition>
			orderByComparator,
		com.liferay.portal.kernel.service.ServiceContext serviceContext) {

		return _kaleoDefinitionLocalService.getKaleoDefinitions(
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
	@Override
	public java.util.List<KaleoDefinition> getKaleoDefinitions(
		int start, int end) {

		return _kaleoDefinitionLocalService.getKaleoDefinitions(start, end);
	}

	@Override
	public java.util.List<KaleoDefinition> getKaleoDefinitions(
		int start, int end,
		com.liferay.portal.kernel.util.OrderByComparator<KaleoDefinition>
			orderByComparator,
		com.liferay.portal.kernel.service.ServiceContext serviceContext) {

		return _kaleoDefinitionLocalService.getKaleoDefinitions(
			start, end, orderByComparator, serviceContext);
	}

	/**
	 * Returns all the kaleo definitions matching the UUID and company.
	 *
	 * @param uuid the UUID of the kaleo definitions
	 * @param companyId the primary key of the company
	 * @return the matching kaleo definitions, or an empty list if no matches were found
	 */
	@Override
	public java.util.List<KaleoDefinition>
		getKaleoDefinitionsByUuidAndCompanyId(String uuid, long companyId) {

		return _kaleoDefinitionLocalService.
			getKaleoDefinitionsByUuidAndCompanyId(uuid, companyId);
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
	@Override
	public java.util.List<KaleoDefinition>
		getKaleoDefinitionsByUuidAndCompanyId(
			String uuid, long companyId, int start, int end,
			com.liferay.portal.kernel.util.OrderByComparator<KaleoDefinition>
				orderByComparator) {

		return _kaleoDefinitionLocalService.
			getKaleoDefinitionsByUuidAndCompanyId(
				uuid, companyId, start, end, orderByComparator);
	}

	/**
	 * Returns the number of kaleo definitions.
	 *
	 * @return the number of kaleo definitions
	 */
	@Override
	public int getKaleoDefinitionsCount() {
		return _kaleoDefinitionLocalService.getKaleoDefinitionsCount();
	}

	@Override
	public int getKaleoDefinitionsCount(
		boolean active,
		com.liferay.portal.kernel.service.ServiceContext serviceContext) {

		return _kaleoDefinitionLocalService.getKaleoDefinitionsCount(
			active, serviceContext);
	}

	@Override
	public int getKaleoDefinitionsCount(
		com.liferay.portal.kernel.service.ServiceContext serviceContext) {

		return _kaleoDefinitionLocalService.getKaleoDefinitionsCount(
			serviceContext);
	}

	@Override
	public int getKaleoDefinitionsCount(
		String name, boolean active,
		com.liferay.portal.kernel.service.ServiceContext serviceContext) {

		return _kaleoDefinitionLocalService.getKaleoDefinitionsCount(
			name, active, serviceContext);
	}

	@Override
	public int getKaleoDefinitionsCount(
		String name,
		com.liferay.portal.kernel.service.ServiceContext serviceContext) {

		return _kaleoDefinitionLocalService.getKaleoDefinitionsCount(
			name, serviceContext);
	}

	/**
	 * Returns the OSGi service identifier.
	 *
	 * @return the OSGi service identifier
	 */
	@Override
	public String getOSGiServiceIdentifier() {
		return _kaleoDefinitionLocalService.getOSGiServiceIdentifier();
	}

	/**
	 * @throws PortalException
	 */
	@Override
	public com.liferay.portal.kernel.model.PersistedModel getPersistedModel(
			java.io.Serializable primaryKeyObj)
		throws com.liferay.portal.kernel.exception.PortalException {

		return _kaleoDefinitionLocalService.getPersistedModel(primaryKeyObj);
	}

	@Override
	public java.util.List<KaleoDefinition> getScopeKaleoDefinitions(
		String scope, boolean active, int start, int end,
		com.liferay.portal.kernel.util.OrderByComparator<KaleoDefinition>
			orderByComparator,
		com.liferay.portal.kernel.service.ServiceContext serviceContext) {

		return _kaleoDefinitionLocalService.getScopeKaleoDefinitions(
			scope, active, start, end, orderByComparator, serviceContext);
	}

	@Override
	public java.util.List<KaleoDefinition> getScopeKaleoDefinitions(
		String scope, int start, int end,
		com.liferay.portal.kernel.util.OrderByComparator<KaleoDefinition>
			orderByComparator,
		com.liferay.portal.kernel.service.ServiceContext serviceContext) {

		return _kaleoDefinitionLocalService.getScopeKaleoDefinitions(
			scope, start, end, orderByComparator, serviceContext);
	}

	@Override
	public int getScopeKaleoDefinitionsCount(
		String scope, boolean active,
		com.liferay.portal.kernel.service.ServiceContext serviceContext) {

		return _kaleoDefinitionLocalService.getScopeKaleoDefinitionsCount(
			scope, active, serviceContext);
	}

	@Override
	public int getScopeKaleoDefinitionsCount(
		String scope,
		com.liferay.portal.kernel.service.ServiceContext serviceContext) {

		return _kaleoDefinitionLocalService.getScopeKaleoDefinitionsCount(
			scope, serviceContext);
	}

	@Override
	public KaleoDefinition updatedKaleoDefinition(
			String externalReferenceCode, long kaleoDefinitionId, String title,
			String description, String content, boolean system,
			com.liferay.portal.kernel.service.ServiceContext serviceContext)
		throws com.liferay.portal.kernel.exception.PortalException {

		return _kaleoDefinitionLocalService.updatedKaleoDefinition(
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
	@Override
	public KaleoDefinition updateKaleoDefinition(
		KaleoDefinition kaleoDefinition) {

		return _kaleoDefinitionLocalService.updateKaleoDefinition(
			kaleoDefinition);
	}

	@Override
	public BasePersistence<?> getBasePersistence() {
		return _kaleoDefinitionLocalService.getBasePersistence();
	}

	@Override
	public CTPersistence<KaleoDefinition> getCTPersistence() {
		return _kaleoDefinitionLocalService.getCTPersistence();
	}

	@Override
	public Class<KaleoDefinition> getModelClass() {
		return _kaleoDefinitionLocalService.getModelClass();
	}

	@Override
	public <R, E extends Throwable> R updateWithUnsafeFunction(
			UnsafeFunction<CTPersistence<KaleoDefinition>, R, E>
				updateUnsafeFunction)
		throws E {

		return _kaleoDefinitionLocalService.updateWithUnsafeFunction(
			updateUnsafeFunction);
	}

	@Override
	public KaleoDefinitionLocalService getWrappedService() {
		return _kaleoDefinitionLocalService;
	}

	@Override
	public void setWrappedService(
		KaleoDefinitionLocalService kaleoDefinitionLocalService) {

		_kaleoDefinitionLocalService = kaleoDefinitionLocalService;
	}

	private KaleoDefinitionLocalService _kaleoDefinitionLocalService;

}
// LIFERAY-SERVICE-BUILDER-HASH:-1599064282