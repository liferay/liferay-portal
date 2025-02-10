/**
 * SPDX-FileCopyrightText: (c) 2000 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

package com.liferay.object.service;

import com.liferay.portal.kernel.service.ServiceWrapper;
import com.liferay.portal.kernel.service.persistence.BasePersistence;

/**
 * Provides a wrapper for {@link ObjectDefinitionLocalService}.
 *
 * @author Marco Leo
 * @see ObjectDefinitionLocalService
 * @generated
 */
public class ObjectDefinitionLocalServiceWrapper
	implements ObjectDefinitionLocalService,
			   ServiceWrapper<ObjectDefinitionLocalService> {

	public ObjectDefinitionLocalServiceWrapper() {
		this(null);
	}

	public ObjectDefinitionLocalServiceWrapper(
		ObjectDefinitionLocalService objectDefinitionLocalService) {

		_objectDefinitionLocalService = objectDefinitionLocalService;
	}

	@Override
	public com.liferay.object.model.ObjectDefinition addCustomObjectDefinition(
			long userId, long objectFolderId, String className,
			boolean enableComments, boolean enableFriendlyURLCustomization,
			boolean enableIndexSearch, boolean enableLocalization,
			boolean enableObjectEntryDraft,
			java.util.Map<java.util.Locale, String> labelMap, String name,
			String panelAppOrder, String panelCategoryKey,
			java.util.Map<java.util.Locale, String> pluralLabelMap,
			boolean portlet, String scope, String storageType,
			java.util.List<com.liferay.object.model.ObjectDefinitionSetting>
				objectDefinitionSettings,
			java.util.List<com.liferay.object.model.ObjectField> objectFields)
		throws com.liferay.portal.kernel.exception.PortalException {

		return _objectDefinitionLocalService.addCustomObjectDefinition(
			userId, objectFolderId, className, enableComments,
			enableFriendlyURLCustomization, enableIndexSearch,
			enableLocalization, enableObjectEntryDraft, labelMap, name,
			panelAppOrder, panelCategoryKey, pluralLabelMap, portlet, scope,
			storageType, objectDefinitionSettings, objectFields);
	}

	/**
	 * Adds the object definition to the database. Also notifies the appropriate model listeners.
	 *
	 * <p>
	 * <strong>Important:</strong> Inspect ObjectDefinitionLocalServiceImpl for overloaded versions of the method. If provided, use these entry points to the API, as the implementation logic may require the additional parameters defined there.
	 * </p>
	 *
	 * @param objectDefinition the object definition
	 * @return the object definition that was added
	 */
	@Override
	public com.liferay.object.model.ObjectDefinition addObjectDefinition(
		com.liferay.object.model.ObjectDefinition objectDefinition) {

		return _objectDefinitionLocalService.addObjectDefinition(
			objectDefinition);
	}

	@Override
	public com.liferay.object.model.ObjectDefinition addObjectDefinition(
			String externalReferenceCode, long userId, long objectFolderId,
			boolean modifiable, String scope, boolean system)
		throws com.liferay.portal.kernel.exception.PortalException {

		return _objectDefinitionLocalService.addObjectDefinition(
			externalReferenceCode, userId, objectFolderId, modifiable, scope,
			system);
	}

	@Override
	public com.liferay.object.model.ObjectDefinition
			addOrUpdateSystemObjectDefinition(
				long companyId, long objectFolderId,
				com.liferay.object.system.SystemObjectDefinitionManager
					systemObjectDefinitionManager)
		throws com.liferay.portal.kernel.exception.PortalException {

		return _objectDefinitionLocalService.addOrUpdateSystemObjectDefinition(
			companyId, objectFolderId, systemObjectDefinitionManager);
	}

	@Override
	public com.liferay.object.model.ObjectDefinition addSystemObjectDefinition(
			String externalReferenceCode, long userId, long objectFolderId,
			String className, String dbTableName, boolean enableComments,
			boolean enableFriendlyURLCustomization, boolean enableIndexSearch,
			boolean enableLocalization,
			java.util.Map<java.util.Locale, String> labelMap,
			boolean modifiable, String name, String panelAppOrder,
			String panelCategoryKey, String pkObjectFieldDBColumnName,
			String pkObjectFieldName,
			java.util.Map<java.util.Locale, String> pluralLabelMap,
			boolean portlet, String scope, String titleObjectFieldName,
			int version, int status,
			java.util.List<com.liferay.object.model.ObjectDefinitionSetting>
				objectDefinitionSettings,
			java.util.List<com.liferay.object.model.ObjectField> objectFields)
		throws com.liferay.portal.kernel.exception.PortalException {

		return _objectDefinitionLocalService.addSystemObjectDefinition(
			externalReferenceCode, userId, objectFolderId, className,
			dbTableName, enableComments, enableFriendlyURLCustomization,
			enableIndexSearch, enableLocalization, labelMap, modifiable, name,
			panelAppOrder, panelCategoryKey, pkObjectFieldDBColumnName,
			pkObjectFieldName, pluralLabelMap, portlet, scope,
			titleObjectFieldName, version, status, objectDefinitionSettings,
			objectFields);
	}

	/**
	 * Creates a new object definition with the primary key. Does not add the object definition to the database.
	 *
	 * @param objectDefinitionId the primary key for the new object definition
	 * @return the new object definition
	 */
	@Override
	public com.liferay.object.model.ObjectDefinition createObjectDefinition(
		long objectDefinitionId) {

		return _objectDefinitionLocalService.createObjectDefinition(
			objectDefinitionId);
	}

	/**
	 * @throws PortalException
	 */
	@Override
	public com.liferay.portal.kernel.model.PersistedModel createPersistedModel(
			java.io.Serializable primaryKeyObj)
		throws com.liferay.portal.kernel.exception.PortalException {

		return _objectDefinitionLocalService.createPersistedModel(
			primaryKeyObj);
	}

	@Override
	public void deleteCompanyObjectDefinitions(long companyId)
		throws com.liferay.portal.kernel.exception.PortalException {

		_objectDefinitionLocalService.deleteCompanyObjectDefinitions(companyId);
	}

	/**
	 * Deletes the object definition with the primary key from the database. Also notifies the appropriate model listeners.
	 *
	 * <p>
	 * <strong>Important:</strong> Inspect ObjectDefinitionLocalServiceImpl for overloaded versions of the method. If provided, use these entry points to the API, as the implementation logic may require the additional parameters defined there.
	 * </p>
	 *
	 * @param objectDefinitionId the primary key of the object definition
	 * @return the object definition that was removed
	 * @throws PortalException if a object definition with the primary key could not be found
	 */
	@Override
	public com.liferay.object.model.ObjectDefinition deleteObjectDefinition(
			long objectDefinitionId)
		throws com.liferay.portal.kernel.exception.PortalException {

		return _objectDefinitionLocalService.deleteObjectDefinition(
			objectDefinitionId);
	}

	/**
	 * Deletes the object definition from the database. Also notifies the appropriate model listeners.
	 *
	 * <p>
	 * <strong>Important:</strong> Inspect ObjectDefinitionLocalServiceImpl for overloaded versions of the method. If provided, use these entry points to the API, as the implementation logic may require the additional parameters defined there.
	 * </p>
	 *
	 * @param objectDefinition the object definition
	 * @return the object definition that was removed
	 * @throws PortalException
	 */
	@Override
	public com.liferay.object.model.ObjectDefinition deleteObjectDefinition(
			com.liferay.object.model.ObjectDefinition objectDefinition)
		throws com.liferay.portal.kernel.exception.PortalException {

		return _objectDefinitionLocalService.deleteObjectDefinition(
			objectDefinition);
	}

	/**
	 * @throws PortalException
	 */
	@Override
	public com.liferay.portal.kernel.model.PersistedModel deletePersistedModel(
			com.liferay.portal.kernel.model.PersistedModel persistedModel)
		throws com.liferay.portal.kernel.exception.PortalException {

		return _objectDefinitionLocalService.deletePersistedModel(
			persistedModel);
	}

	@Override
	public void deployInactiveObjectDefinition(
		com.liferay.object.model.ObjectDefinition objectDefinition) {

		_objectDefinitionLocalService.deployInactiveObjectDefinition(
			objectDefinition);
	}

	@Override
	public void deployObjectDefinition(
		com.liferay.object.model.ObjectDefinition objectDefinition) {

		_objectDefinitionLocalService.deployObjectDefinition(objectDefinition);
	}

	@Override
	public <T> T dslQuery(com.liferay.petra.sql.dsl.query.DSLQuery dslQuery) {
		return _objectDefinitionLocalService.dslQuery(dslQuery);
	}

	@Override
	public int dslQueryCount(
		com.liferay.petra.sql.dsl.query.DSLQuery dslQuery) {

		return _objectDefinitionLocalService.dslQueryCount(dslQuery);
	}

	@Override
	public com.liferay.portal.kernel.dao.orm.DynamicQuery dynamicQuery() {
		return _objectDefinitionLocalService.dynamicQuery();
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

		return _objectDefinitionLocalService.dynamicQuery(dynamicQuery);
	}

	/**
	 * Performs a dynamic query on the database and returns a range of the matching rows.
	 *
	 * <p>
	 * Useful when paginating results. Returns a maximum of <code>end - start</code> instances. <code>start</code> and <code>end</code> are not primary keys, they are indexes in the result set. Thus, <code>0</code> refers to the first result in the set. Setting both <code>start</code> and <code>end</code> to <code>com.liferay.portal.kernel.dao.orm.QueryUtil#ALL_POS</code> will return the full result set. If <code>orderByComparator</code> is specified, then the query will include the given ORDER BY logic. If <code>orderByComparator</code> is absent, then the query will include the default ORDER BY logic from <code>com.liferay.object.model.impl.ObjectDefinitionModelImpl</code>.
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

		return _objectDefinitionLocalService.dynamicQuery(
			dynamicQuery, start, end);
	}

	/**
	 * Performs a dynamic query on the database and returns an ordered range of the matching rows.
	 *
	 * <p>
	 * Useful when paginating results. Returns a maximum of <code>end - start</code> instances. <code>start</code> and <code>end</code> are not primary keys, they are indexes in the result set. Thus, <code>0</code> refers to the first result in the set. Setting both <code>start</code> and <code>end</code> to <code>com.liferay.portal.kernel.dao.orm.QueryUtil#ALL_POS</code> will return the full result set. If <code>orderByComparator</code> is specified, then the query will include the given ORDER BY logic. If <code>orderByComparator</code> is absent, then the query will include the default ORDER BY logic from <code>com.liferay.object.model.impl.ObjectDefinitionModelImpl</code>.
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

		return _objectDefinitionLocalService.dynamicQuery(
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

		return _objectDefinitionLocalService.dynamicQueryCount(dynamicQuery);
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

		return _objectDefinitionLocalService.dynamicQueryCount(
			dynamicQuery, projection);
	}

	@Override
	public com.liferay.object.model.ObjectDefinition
			enableAccountEntryRestricted(
				com.liferay.object.model.ObjectRelationship objectRelationship)
		throws com.liferay.portal.kernel.exception.PortalException {

		return _objectDefinitionLocalService.enableAccountEntryRestricted(
			objectRelationship);
	}

	@Override
	public com.liferay.object.model.ObjectDefinition
			enableAccountEntryRestrictedForNondefaultStorageType(
				com.liferay.object.model.ObjectField objectField)
		throws com.liferay.portal.kernel.exception.PortalException {

		return _objectDefinitionLocalService.
			enableAccountEntryRestrictedForNondefaultStorageType(objectField);
	}

	@Override
	public com.liferay.object.model.ObjectDefinition fetchObjectDefinition(
		long objectDefinitionId) {

		return _objectDefinitionLocalService.fetchObjectDefinition(
			objectDefinitionId);
	}

	@Override
	public com.liferay.object.model.ObjectDefinition fetchObjectDefinition(
		long companyId, String name) {

		return _objectDefinitionLocalService.fetchObjectDefinition(
			companyId, name);
	}

	@Override
	public com.liferay.object.model.ObjectDefinition
		fetchObjectDefinitionByClassName(long companyId, String className) {

		return _objectDefinitionLocalService.fetchObjectDefinitionByClassName(
			companyId, className);
	}

	@Override
	public com.liferay.object.model.ObjectDefinition
		fetchObjectDefinitionByExternalReferenceCode(
			String externalReferenceCode, long companyId) {

		return _objectDefinitionLocalService.
			fetchObjectDefinitionByExternalReferenceCode(
				externalReferenceCode, companyId);
	}

	/**
	 * Returns the object definition with the matching UUID and company.
	 *
	 * @param uuid the object definition's UUID
	 * @param companyId the primary key of the company
	 * @return the matching object definition, or <code>null</code> if a matching object definition could not be found
	 */
	@Override
	public com.liferay.object.model.ObjectDefinition
		fetchObjectDefinitionByUuidAndCompanyId(String uuid, long companyId) {

		return _objectDefinitionLocalService.
			fetchObjectDefinitionByUuidAndCompanyId(uuid, companyId);
	}

	@Override
	public com.liferay.object.model.ObjectDefinition
		fetchSystemObjectDefinition(long companyId, String name) {

		return _objectDefinitionLocalService.fetchSystemObjectDefinition(
			companyId, name);
	}

	@Override
	public com.liferay.portal.kernel.dao.orm.ActionableDynamicQuery
		getActionableDynamicQuery() {

		return _objectDefinitionLocalService.getActionableDynamicQuery();
	}

	@Override
	public java.util.List<com.liferay.object.model.ObjectDefinition>
		getBoundObjectDefinitions(long companyId, long rootObjectDefinitionId) {

		return _objectDefinitionLocalService.getBoundObjectDefinitions(
			companyId, rootObjectDefinitionId);
	}

	@Override
	public java.util.List<com.liferay.object.model.ObjectDefinition>
		getCustomObjectDefinitions(int status) {

		return _objectDefinitionLocalService.getCustomObjectDefinitions(status);
	}

	@Override
	public com.liferay.portal.kernel.dao.orm.ExportActionableDynamicQuery
		getExportActionableDynamicQuery(
			com.liferay.exportimport.kernel.lar.PortletDataContext
				portletDataContext) {

		return _objectDefinitionLocalService.getExportActionableDynamicQuery(
			portletDataContext);
	}

	@Override
	public com.liferay.portal.kernel.dao.orm.IndexableActionableDynamicQuery
		getIndexableActionableDynamicQuery() {

		return _objectDefinitionLocalService.
			getIndexableActionableDynamicQuery();
	}

	/**
	 * Returns the object definition with the primary key.
	 *
	 * @param objectDefinitionId the primary key of the object definition
	 * @return the object definition
	 * @throws PortalException if a object definition with the primary key could not be found
	 */
	@Override
	public com.liferay.object.model.ObjectDefinition getObjectDefinition(
			long objectDefinitionId)
		throws com.liferay.portal.kernel.exception.PortalException {

		return _objectDefinitionLocalService.getObjectDefinition(
			objectDefinitionId);
	}

	@Override
	public com.liferay.object.model.ObjectDefinition getObjectDefinition(
			long companyId, String name)
		throws com.liferay.portal.kernel.exception.PortalException {

		return _objectDefinitionLocalService.getObjectDefinition(
			companyId, name);
	}

	@Override
	public com.liferay.object.model.ObjectDefinition
			getObjectDefinitionByExternalReferenceCode(
				String externalReferenceCode, long companyId)
		throws com.liferay.portal.kernel.exception.PortalException {

		return _objectDefinitionLocalService.
			getObjectDefinitionByExternalReferenceCode(
				externalReferenceCode, companyId);
	}

	/**
	 * Returns the object definition with the matching UUID and company.
	 *
	 * @param uuid the object definition's UUID
	 * @param companyId the primary key of the company
	 * @return the matching object definition
	 * @throws PortalException if a matching object definition could not be found
	 */
	@Override
	public com.liferay.object.model.ObjectDefinition
			getObjectDefinitionByUuidAndCompanyId(String uuid, long companyId)
		throws com.liferay.portal.kernel.exception.PortalException {

		return _objectDefinitionLocalService.
			getObjectDefinitionByUuidAndCompanyId(uuid, companyId);
	}

	@Override
	public java.util.List<com.liferay.object.model.ObjectDefinition>
		getObjectDefinitions(boolean accountEntryRestricted) {

		return _objectDefinitionLocalService.getObjectDefinitions(
			accountEntryRestricted);
	}

	/**
	 * Returns a range of all the object definitions.
	 *
	 * <p>
	 * Useful when paginating results. Returns a maximum of <code>end - start</code> instances. <code>start</code> and <code>end</code> are not primary keys, they are indexes in the result set. Thus, <code>0</code> refers to the first result in the set. Setting both <code>start</code> and <code>end</code> to <code>com.liferay.portal.kernel.dao.orm.QueryUtil#ALL_POS</code> will return the full result set. If <code>orderByComparator</code> is specified, then the query will include the given ORDER BY logic. If <code>orderByComparator</code> is absent, then the query will include the default ORDER BY logic from <code>com.liferay.object.model.impl.ObjectDefinitionModelImpl</code>.
	 * </p>
	 *
	 * @param start the lower bound of the range of object definitions
	 * @param end the upper bound of the range of object definitions (not inclusive)
	 * @return the range of object definitions
	 */
	@Override
	public java.util.List<com.liferay.object.model.ObjectDefinition>
		getObjectDefinitions(int start, int end) {

		return _objectDefinitionLocalService.getObjectDefinitions(start, end);
	}

	@Override
	public java.util.List<com.liferay.object.model.ObjectDefinition>
		getObjectDefinitions(
			long companyId, boolean active, boolean system, int status) {

		return _objectDefinitionLocalService.getObjectDefinitions(
			companyId, active, system, status);
	}

	@Override
	public java.util.List<com.liferay.object.model.ObjectDefinition>
		getObjectDefinitions(long companyId, boolean active, int status) {

		return _objectDefinitionLocalService.getObjectDefinitions(
			companyId, active, status);
	}

	@Override
	public java.util.List<com.liferay.object.model.ObjectDefinition>
		getObjectDefinitions(long companyId, int status) {

		return _objectDefinitionLocalService.getObjectDefinitions(
			companyId, status);
	}

	/**
	 * Returns the number of object definitions.
	 *
	 * @return the number of object definitions
	 */
	@Override
	public int getObjectDefinitionsCount() {
		return _objectDefinitionLocalService.getObjectDefinitionsCount();
	}

	@Override
	public int getObjectDefinitionsCount(long companyId)
		throws com.liferay.portal.kernel.exception.PortalException {

		return _objectDefinitionLocalService.getObjectDefinitionsCount(
			companyId);
	}

	@Override
	public java.util.List<com.liferay.object.model.ObjectDefinition>
		getObjectFolderObjectDefinitions(long objectFolderId) {

		return _objectDefinitionLocalService.getObjectFolderObjectDefinitions(
			objectFolderId);
	}

	@Override
	public int getObjectFolderObjectDefinitionsCount(long objectFolderId)
		throws com.liferay.portal.kernel.exception.PortalException {

		return _objectDefinitionLocalService.
			getObjectFolderObjectDefinitionsCount(objectFolderId);
	}

	/**
	 * Returns the OSGi service identifier.
	 *
	 * @return the OSGi service identifier
	 */
	@Override
	public String getOSGiServiceIdentifier() {
		return _objectDefinitionLocalService.getOSGiServiceIdentifier();
	}

	/**
	 * @throws PortalException
	 */
	@Override
	public com.liferay.portal.kernel.model.PersistedModel getPersistedModel(
			java.io.Serializable primaryKeyObj)
		throws com.liferay.portal.kernel.exception.PortalException {

		return _objectDefinitionLocalService.getPersistedModel(primaryKeyObj);
	}

	@Override
	public java.util.List<com.liferay.object.model.ObjectDefinition>
		getSystemObjectDefinitions() {

		return _objectDefinitionLocalService.getSystemObjectDefinitions();
	}

	@Override
	public java.util.List<com.liferay.object.model.ObjectDefinition>
		getUnmodifiableSystemObjectDefinitions(long companyId) {

		return _objectDefinitionLocalService.
			getUnmodifiableSystemObjectDefinitions(companyId);
	}

	@Override
	public boolean hasObjectRelationship(long objectDefinitionId) {
		return _objectDefinitionLocalService.hasObjectRelationship(
			objectDefinitionId);
	}

	@Override
	public com.liferay.object.model.ObjectDefinition
			publishCustomObjectDefinition(long userId, long objectDefinitionId)
		throws com.liferay.portal.kernel.exception.PortalException {

		return _objectDefinitionLocalService.publishCustomObjectDefinition(
			userId, objectDefinitionId);
	}

	@Override
	public com.liferay.object.model.ObjectDefinition
			publishSystemObjectDefinition(long userId, long objectDefinitionId)
		throws com.liferay.portal.kernel.exception.PortalException {

		return _objectDefinitionLocalService.publishSystemObjectDefinition(
			userId, objectDefinitionId);
	}

	@Override
	public void undeployObjectDefinition(
		com.liferay.object.model.ObjectDefinition objectDefinition) {

		_objectDefinitionLocalService.undeployObjectDefinition(
			objectDefinition);
	}

	@Override
	public com.liferay.object.model.ObjectDefinition
			updateCustomObjectDefinition(
				String externalReferenceCode, long objectDefinitionId,
				long accountEntryRestrictedObjectFieldId,
				long descriptionObjectFieldId, long objectFolderId,
				long titleObjectFieldId, boolean accountEntryRestricted,
				boolean active, String className, boolean enableCategorization,
				boolean enableComments, boolean enableFriendlyURLCustomization,
				boolean enableIndexSearch, boolean enableLocalization,
				boolean enableObjectEntryDraft,
				boolean enableObjectEntryHistory,
				java.util.Map<java.util.Locale, String> labelMap, String name,
				String panelAppOrder, String panelCategoryKey, boolean portlet,
				java.util.Map<java.util.Locale, String> pluralLabelMap,
				String scope, int status,
				java.util.List<com.liferay.object.model.ObjectDefinitionSetting>
					objectDefinitionSettings)
		throws com.liferay.portal.kernel.exception.PortalException {

		return _objectDefinitionLocalService.updateCustomObjectDefinition(
			externalReferenceCode, objectDefinitionId,
			accountEntryRestrictedObjectFieldId, descriptionObjectFieldId,
			objectFolderId, titleObjectFieldId, accountEntryRestricted, active,
			className, enableCategorization, enableComments,
			enableFriendlyURLCustomization, enableIndexSearch,
			enableLocalization, enableObjectEntryDraft,
			enableObjectEntryHistory, labelMap, name, panelAppOrder,
			panelCategoryKey, portlet, pluralLabelMap, scope, status,
			objectDefinitionSettings);
	}

	@Override
	public com.liferay.object.model.ObjectDefinition
			updateExternalReferenceCode(
				long objectDefinitionId, String externalReferenceCode)
		throws com.liferay.portal.kernel.exception.PortalException {

		return _objectDefinitionLocalService.updateExternalReferenceCode(
			objectDefinitionId, externalReferenceCode);
	}

	/**
	 * Updates the object definition in the database or adds it if it does not yet exist. Also notifies the appropriate model listeners.
	 *
	 * <p>
	 * <strong>Important:</strong> Inspect ObjectDefinitionLocalServiceImpl for overloaded versions of the method. If provided, use these entry points to the API, as the implementation logic may require the additional parameters defined there.
	 * </p>
	 *
	 * @param objectDefinition the object definition
	 * @return the object definition that was updated
	 */
	@Override
	public com.liferay.object.model.ObjectDefinition updateObjectDefinition(
		com.liferay.object.model.ObjectDefinition objectDefinition) {

		return _objectDefinitionLocalService.updateObjectDefinition(
			objectDefinition);
	}

	@Override
	public com.liferay.object.model.ObjectDefinition updateObjectFolderId(
			long objectDefinitionId, long objectFolderId)
		throws com.liferay.portal.kernel.exception.PortalException {

		return _objectDefinitionLocalService.updateObjectFolderId(
			objectDefinitionId, objectFolderId);
	}

	@Override
	public com.liferay.object.model.ObjectDefinition updatePortlet(
			long objectDefinitionId)
		throws com.liferay.portal.kernel.exception.PortalException {

		return _objectDefinitionLocalService.updatePortlet(objectDefinitionId);
	}

	@Override
	public com.liferay.object.model.ObjectDefinition
		updateRootDescendantNodeObjectDefinition(
			com.liferay.object.model.ObjectDefinition objectDefinition,
			long rootObjectDefinitionId) {

		return _objectDefinitionLocalService.
			updateRootDescendantNodeObjectDefinition(
				objectDefinition, rootObjectDefinitionId);
	}

	@Override
	public com.liferay.object.model.ObjectDefinition
			updateRootObjectDefinitionId(
				long objectDefinitionId, long rootObjectDefinitionId)
		throws com.liferay.portal.kernel.exception.PortalException {

		return _objectDefinitionLocalService.updateRootObjectDefinitionId(
			objectDefinitionId, rootObjectDefinitionId);
	}

	@Override
	public com.liferay.object.model.ObjectDefinition
			updateSystemObjectDefinition(
				String externalReferenceCode, long objectDefinitionId,
				long objectFolderId, long titleObjectFieldId,
				java.util.List<com.liferay.object.model.ObjectDefinitionSetting>
					objectDefinitionSettings)
		throws com.liferay.portal.kernel.exception.PortalException {

		return _objectDefinitionLocalService.updateSystemObjectDefinition(
			externalReferenceCode, objectDefinitionId, objectFolderId,
			titleObjectFieldId, objectDefinitionSettings);
	}

	@Override
	public com.liferay.object.model.ObjectDefinition updateTitleObjectFieldId(
			long objectDefinitionId, long titleObjectFieldId)
		throws com.liferay.portal.kernel.exception.PortalException {

		return _objectDefinitionLocalService.updateTitleObjectFieldId(
			objectDefinitionId, titleObjectFieldId);
	}

	@Override
	public void updateUserId(long companyId, long oldUserId, long newUserId)
		throws com.liferay.portal.kernel.exception.PortalException {

		_objectDefinitionLocalService.updateUserId(
			companyId, oldUserId, newUserId);
	}

	@Override
	public BasePersistence<?> getBasePersistence() {
		return _objectDefinitionLocalService.getBasePersistence();
	}

	@Override
	public ObjectDefinitionLocalService getWrappedService() {
		return _objectDefinitionLocalService;
	}

	@Override
	public void setWrappedService(
		ObjectDefinitionLocalService objectDefinitionLocalService) {

		_objectDefinitionLocalService = objectDefinitionLocalService;
	}

	private ObjectDefinitionLocalService _objectDefinitionLocalService;

}