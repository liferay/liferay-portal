/**
 * SPDX-FileCopyrightText: (c) 2000 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

package com.liferay.portal.reports.engine.console.service;

import com.liferay.portal.kernel.service.ServiceWrapper;
import com.liferay.portal.kernel.service.persistence.BasePersistence;

/**
 * Provides a wrapper for {@link DefinitionLocalService}.
 *
 * @author Brian Wing Shun Chan
 * @see DefinitionLocalService
 * @generated
 */
public class DefinitionLocalServiceWrapper
	implements DefinitionLocalService, ServiceWrapper<DefinitionLocalService> {

	public DefinitionLocalServiceWrapper() {
		this(null);
	}

	public DefinitionLocalServiceWrapper(
		DefinitionLocalService definitionLocalService) {

		_definitionLocalService = definitionLocalService;
	}

	/**
	 * Adds the definition to the database. Also notifies the appropriate model listeners.
	 *
	 * <p>
	 * <strong>Important:</strong> Inspect DefinitionLocalServiceImpl for overloaded versions of the method. If provided, use these entry points to the API, as the implementation logic may require the additional parameters defined there.
	 * </p>
	 *
	 * @param definition the definition
	 * @return the definition that was added
	 */
	@Override
	public com.liferay.portal.reports.engine.console.model.Definition
		addDefinition(
			com.liferay.portal.reports.engine.console.model.Definition
				definition) {

		return _definitionLocalService.addDefinition(definition);
	}

	@Override
	public com.liferay.portal.reports.engine.console.model.Definition
			addDefinition(
				long userId, long groupId,
				java.util.Map<java.util.Locale, String> nameMap,
				java.util.Map<java.util.Locale, String> descriptionMap,
				long sourceId, String reportParameters, String fileName,
				java.io.InputStream inputStream,
				com.liferay.portal.kernel.service.ServiceContext serviceContext)
		throws com.liferay.portal.kernel.exception.PortalException {

		return _definitionLocalService.addDefinition(
			userId, groupId, nameMap, descriptionMap, sourceId,
			reportParameters, fileName, inputStream, serviceContext);
	}

	/**
	 * Creates a new definition with the primary key. Does not add the definition to the database.
	 *
	 * @param definitionId the primary key for the new definition
	 * @return the new definition
	 */
	@Override
	public com.liferay.portal.reports.engine.console.model.Definition
		createDefinition(long definitionId) {

		return _definitionLocalService.createDefinition(definitionId);
	}

	/**
	 * @throws PortalException
	 */
	@Override
	public com.liferay.portal.kernel.model.PersistedModel createPersistedModel(
			java.io.Serializable primaryKeyObj)
		throws com.liferay.portal.kernel.exception.PortalException {

		return _definitionLocalService.createPersistedModel(primaryKeyObj);
	}

	/**
	 * Deletes the definition from the database. Also notifies the appropriate model listeners.
	 *
	 * <p>
	 * <strong>Important:</strong> Inspect DefinitionLocalServiceImpl for overloaded versions of the method. If provided, use these entry points to the API, as the implementation logic may require the additional parameters defined there.
	 * </p>
	 *
	 * @param definition the definition
	 * @return the definition that was removed
	 * @throws PortalException
	 */
	@Override
	public com.liferay.portal.reports.engine.console.model.Definition
			deleteDefinition(
				com.liferay.portal.reports.engine.console.model.Definition
					definition)
		throws com.liferay.portal.kernel.exception.PortalException {

		return _definitionLocalService.deleteDefinition(definition);
	}

	/**
	 * Deletes the definition with the primary key from the database. Also notifies the appropriate model listeners.
	 *
	 * <p>
	 * <strong>Important:</strong> Inspect DefinitionLocalServiceImpl for overloaded versions of the method. If provided, use these entry points to the API, as the implementation logic may require the additional parameters defined there.
	 * </p>
	 *
	 * @param definitionId the primary key of the definition
	 * @return the definition that was removed
	 * @throws PortalException if a definition with the primary key could not be found
	 */
	@Override
	public com.liferay.portal.reports.engine.console.model.Definition
			deleteDefinition(long definitionId)
		throws com.liferay.portal.kernel.exception.PortalException {

		return _definitionLocalService.deleteDefinition(definitionId);
	}

	@Override
	public void deleteDefinitions(long groupId)
		throws com.liferay.portal.kernel.exception.PortalException {

		_definitionLocalService.deleteDefinitions(groupId);
	}

	@Override
	public void deleteDefinitionTemplates(
			long companyId, String attachmentsDirectory)
		throws com.liferay.portal.kernel.exception.PortalException {

		_definitionLocalService.deleteDefinitionTemplates(
			companyId, attachmentsDirectory);
	}

	/**
	 * @throws PortalException
	 */
	@Override
	public com.liferay.portal.kernel.model.PersistedModel deletePersistedModel(
			com.liferay.portal.kernel.model.PersistedModel persistedModel)
		throws com.liferay.portal.kernel.exception.PortalException {

		return _definitionLocalService.deletePersistedModel(persistedModel);
	}

	@Override
	public <T> T dslQuery(com.liferay.petra.sql.dsl.query.DSLQuery dslQuery) {
		return _definitionLocalService.dslQuery(dslQuery);
	}

	@Override
	public int dslQueryCount(
		com.liferay.petra.sql.dsl.query.DSLQuery dslQuery) {

		return _definitionLocalService.dslQueryCount(dslQuery);
	}

	@Override
	public com.liferay.portal.kernel.dao.orm.DynamicQuery dynamicQuery() {
		return _definitionLocalService.dynamicQuery();
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

		return _definitionLocalService.dynamicQuery(dynamicQuery);
	}

	/**
	 * Performs a dynamic query on the database and returns a range of the matching rows.
	 *
	 * <p>
	 * Useful when paginating results. Returns a maximum of <code>end - start</code> instances. <code>start</code> and <code>end</code> are not primary keys, they are indexes in the result set. Thus, <code>0</code> refers to the first result in the set. Setting both <code>start</code> and <code>end</code> to <code>com.liferay.portal.kernel.dao.orm.QueryUtil#ALL_POS</code> will return the full result set. If <code>orderByComparator</code> is specified, then the query will include the given ORDER BY logic. If <code>orderByComparator</code> is absent, then the query will include the default ORDER BY logic from <code>com.liferay.portal.reports.engine.console.model.impl.DefinitionModelImpl</code>.
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

		return _definitionLocalService.dynamicQuery(dynamicQuery, start, end);
	}

	/**
	 * Performs a dynamic query on the database and returns an ordered range of the matching rows.
	 *
	 * <p>
	 * Useful when paginating results. Returns a maximum of <code>end - start</code> instances. <code>start</code> and <code>end</code> are not primary keys, they are indexes in the result set. Thus, <code>0</code> refers to the first result in the set. Setting both <code>start</code> and <code>end</code> to <code>com.liferay.portal.kernel.dao.orm.QueryUtil#ALL_POS</code> will return the full result set. If <code>orderByComparator</code> is specified, then the query will include the given ORDER BY logic. If <code>orderByComparator</code> is absent, then the query will include the default ORDER BY logic from <code>com.liferay.portal.reports.engine.console.model.impl.DefinitionModelImpl</code>.
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

		return _definitionLocalService.dynamicQuery(
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

		return _definitionLocalService.dynamicQueryCount(dynamicQuery);
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

		return _definitionLocalService.dynamicQueryCount(
			dynamicQuery, projection);
	}

	@Override
	public com.liferay.portal.reports.engine.console.model.Definition
		fetchDefinition(long definitionId) {

		return _definitionLocalService.fetchDefinition(definitionId);
	}

	/**
	 * Returns the definition matching the UUID and group.
	 *
	 * @param uuid the definition's UUID
	 * @param groupId the primary key of the group
	 * @return the matching definition, or <code>null</code> if a matching definition could not be found
	 */
	@Override
	public com.liferay.portal.reports.engine.console.model.Definition
		fetchDefinitionByUuidAndGroupId(String uuid, long groupId) {

		return _definitionLocalService.fetchDefinitionByUuidAndGroupId(
			uuid, groupId);
	}

	@Override
	public com.liferay.portal.kernel.dao.orm.ActionableDynamicQuery
		getActionableDynamicQuery() {

		return _definitionLocalService.getActionableDynamicQuery();
	}

	@Override
	public String[] getAttachmentsFileNames(
		com.liferay.portal.reports.engine.console.model.Definition definition) {

		return _definitionLocalService.getAttachmentsFileNames(definition);
	}

	/**
	 * Returns the definition with the primary key.
	 *
	 * @param definitionId the primary key of the definition
	 * @return the definition
	 * @throws PortalException if a definition with the primary key could not be found
	 */
	@Override
	public com.liferay.portal.reports.engine.console.model.Definition
			getDefinition(long definitionId)
		throws com.liferay.portal.kernel.exception.PortalException {

		return _definitionLocalService.getDefinition(definitionId);
	}

	/**
	 * Returns the definition matching the UUID and group.
	 *
	 * @param uuid the definition's UUID
	 * @param groupId the primary key of the group
	 * @return the matching definition
	 * @throws PortalException if a matching definition could not be found
	 */
	@Override
	public com.liferay.portal.reports.engine.console.model.Definition
			getDefinitionByUuidAndGroupId(String uuid, long groupId)
		throws com.liferay.portal.kernel.exception.PortalException {

		return _definitionLocalService.getDefinitionByUuidAndGroupId(
			uuid, groupId);
	}

	/**
	 * Returns a range of all the definitions.
	 *
	 * <p>
	 * Useful when paginating results. Returns a maximum of <code>end - start</code> instances. <code>start</code> and <code>end</code> are not primary keys, they are indexes in the result set. Thus, <code>0</code> refers to the first result in the set. Setting both <code>start</code> and <code>end</code> to <code>com.liferay.portal.kernel.dao.orm.QueryUtil#ALL_POS</code> will return the full result set. If <code>orderByComparator</code> is specified, then the query will include the given ORDER BY logic. If <code>orderByComparator</code> is absent, then the query will include the default ORDER BY logic from <code>com.liferay.portal.reports.engine.console.model.impl.DefinitionModelImpl</code>.
	 * </p>
	 *
	 * @param start the lower bound of the range of definitions
	 * @param end the upper bound of the range of definitions (not inclusive)
	 * @return the range of definitions
	 */
	@Override
	public java.util.List
		<com.liferay.portal.reports.engine.console.model.Definition>
			getDefinitions(int start, int end) {

		return _definitionLocalService.getDefinitions(start, end);
	}

	/**
	 * Returns all the definitions matching the UUID and company.
	 *
	 * @param uuid the UUID of the definitions
	 * @param companyId the primary key of the company
	 * @return the matching definitions, or an empty list if no matches were found
	 */
	@Override
	public java.util.List
		<com.liferay.portal.reports.engine.console.model.Definition>
			getDefinitionsByUuidAndCompanyId(String uuid, long companyId) {

		return _definitionLocalService.getDefinitionsByUuidAndCompanyId(
			uuid, companyId);
	}

	/**
	 * Returns a range of definitions matching the UUID and company.
	 *
	 * @param uuid the UUID of the definitions
	 * @param companyId the primary key of the company
	 * @param start the lower bound of the range of definitions
	 * @param end the upper bound of the range of definitions (not inclusive)
	 * @param orderByComparator the comparator to order the results by (optionally <code>null</code>)
	 * @return the range of matching definitions, or an empty list if no matches were found
	 */
	@Override
	public java.util.List
		<com.liferay.portal.reports.engine.console.model.Definition>
			getDefinitionsByUuidAndCompanyId(
				String uuid, long companyId, int start, int end,
				com.liferay.portal.kernel.util.OrderByComparator
					<com.liferay.portal.reports.engine.console.model.Definition>
						orderByComparator) {

		return _definitionLocalService.getDefinitionsByUuidAndCompanyId(
			uuid, companyId, start, end, orderByComparator);
	}

	/**
	 * Returns the number of definitions.
	 *
	 * @return the number of definitions
	 */
	@Override
	public int getDefinitionsCount() {
		return _definitionLocalService.getDefinitionsCount();
	}

	@Override
	public com.liferay.portal.kernel.dao.orm.ExportActionableDynamicQuery
		getExportActionableDynamicQuery(
			com.liferay.exportimport.kernel.lar.PortletDataContext
				portletDataContext) {

		return _definitionLocalService.getExportActionableDynamicQuery(
			portletDataContext);
	}

	@Override
	public com.liferay.portal.kernel.dao.orm.IndexableActionableDynamicQuery
		getIndexableActionableDynamicQuery() {

		return _definitionLocalService.getIndexableActionableDynamicQuery();
	}

	/**
	 * Returns the OSGi service identifier.
	 *
	 * @return the OSGi service identifier
	 */
	@Override
	public String getOSGiServiceIdentifier() {
		return _definitionLocalService.getOSGiServiceIdentifier();
	}

	/**
	 * @throws PortalException
	 */
	@Override
	public com.liferay.portal.kernel.model.PersistedModel getPersistedModel(
			java.io.Serializable primaryKeyObj)
		throws com.liferay.portal.kernel.exception.PortalException {

		return _definitionLocalService.getPersistedModel(primaryKeyObj);
	}

	/**
	 * Updates the definition in the database or adds it if it does not yet exist. Also notifies the appropriate model listeners.
	 *
	 * <p>
	 * <strong>Important:</strong> Inspect DefinitionLocalServiceImpl for overloaded versions of the method. If provided, use these entry points to the API, as the implementation logic may require the additional parameters defined there.
	 * </p>
	 *
	 * @param definition the definition
	 * @return the definition that was updated
	 */
	@Override
	public com.liferay.portal.reports.engine.console.model.Definition
		updateDefinition(
			com.liferay.portal.reports.engine.console.model.Definition
				definition) {

		return _definitionLocalService.updateDefinition(definition);
	}

	@Override
	public com.liferay.portal.reports.engine.console.model.Definition
			updateDefinition(
				long definitionId,
				java.util.Map<java.util.Locale, String> nameMap,
				java.util.Map<java.util.Locale, String> descriptionMap,
				long sourceId, String reportParameters, String fileName,
				java.io.InputStream inputStream,
				com.liferay.portal.kernel.service.ServiceContext serviceContext)
		throws com.liferay.portal.kernel.exception.PortalException {

		return _definitionLocalService.updateDefinition(
			definitionId, nameMap, descriptionMap, sourceId, reportParameters,
			fileName, inputStream, serviceContext);
	}

	@Override
	public void updateDefinitionResources(
			com.liferay.portal.reports.engine.console.model.Definition
				definition,
			String[] communityPermissions, String[] guestPermissions)
		throws com.liferay.portal.kernel.exception.PortalException {

		_definitionLocalService.updateDefinitionResources(
			definition, communityPermissions, guestPermissions);
	}

	@Override
	public BasePersistence<?> getBasePersistence() {
		return _definitionLocalService.getBasePersistence();
	}

	@Override
	public DefinitionLocalService getWrappedService() {
		return _definitionLocalService;
	}

	@Override
	public void setWrappedService(
		DefinitionLocalService definitionLocalService) {

		_definitionLocalService = definitionLocalService;
	}

	private DefinitionLocalService _definitionLocalService;

}
// LIFERAY-SERVICE-BUILDER-HASH:309064223