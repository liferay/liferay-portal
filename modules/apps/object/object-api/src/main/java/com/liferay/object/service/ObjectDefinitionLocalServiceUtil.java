/**
 * SPDX-FileCopyrightText: (c) 2000 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

package com.liferay.object.service;

import com.liferay.object.model.ObjectDefinition;
import com.liferay.petra.sql.dsl.query.DSLQuery;
import com.liferay.portal.kernel.dao.orm.DynamicQuery;
import com.liferay.portal.kernel.exception.PortalException;
import com.liferay.portal.kernel.model.PersistedModel;
import com.liferay.portal.kernel.module.service.Snapshot;
import com.liferay.portal.kernel.util.OrderByComparator;

import java.io.Serializable;

import java.util.List;
import java.util.Map;

/**
 * Provides the local service utility for ObjectDefinition. This utility wraps
 * <code>com.liferay.object.service.impl.ObjectDefinitionLocalServiceImpl</code> and
 * is an access point for service operations in application layer code running
 * on the local server. Methods of this service will not have security checks
 * based on the propagated JAAS credentials because this service can only be
 * accessed from within the same VM.
 *
 * @author Marco Leo
 * @see ObjectDefinitionLocalService
 * @generated
 */
public class ObjectDefinitionLocalServiceUtil {

	/*
	 * NOTE FOR DEVELOPERS:
	 *
	 * Never modify this class directly. Add custom service methods to <code>com.liferay.object.service.impl.ObjectDefinitionLocalServiceImpl</code> and rerun ServiceBuilder to regenerate this class.
	 */
	public static ObjectDefinition addCustomObjectDefinition(
			long userId, long objectFolderId, String className,
			boolean enableComments, boolean enableFormContainer,
			boolean enableFriendlyURLCustomization, boolean enableIndexSearch,
			boolean enableLocalization, boolean enableObjectEntryDraft,
			boolean enableObjectEntrySchedule,
			boolean enableObjectEntrySubscription,
			boolean enableObjectEntryVersioning, String friendlyURLSeparator,
			Map<java.util.Locale, String> labelMap, String name,
			String panelAppOrder, String panelCategoryKey,
			Map<java.util.Locale, String> pluralLabelMap, boolean portlet,
			String scope, String storageType,
			List<com.liferay.object.model.ObjectDefinitionSetting>
				objectDefinitionSettings,
			List<com.liferay.object.model.ObjectField> objectFields,
			List<com.liferay.portal.kernel.model.WorkflowDefinitionLink>
				workflowDefinitionLinks)
		throws PortalException {

		return getService().addCustomObjectDefinition(
			userId, objectFolderId, className, enableComments,
			enableFormContainer, enableFriendlyURLCustomization,
			enableIndexSearch, enableLocalization, enableObjectEntryDraft,
			enableObjectEntrySchedule, enableObjectEntrySubscription,
			enableObjectEntryVersioning, friendlyURLSeparator, labelMap, name,
			panelAppOrder, panelCategoryKey, pluralLabelMap, portlet, scope,
			storageType, objectDefinitionSettings, objectFields,
			workflowDefinitionLinks);
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
	public static ObjectDefinition addObjectDefinition(
		ObjectDefinition objectDefinition) {

		return getService().addObjectDefinition(objectDefinition);
	}

	public static ObjectDefinition addObjectDefinition(
			String externalReferenceCode, long userId, long objectFolderId,
			boolean modifiable, String scope, boolean system)
		throws PortalException {

		return getService().addObjectDefinition(
			externalReferenceCode, userId, objectFolderId, modifiable, scope,
			system);
	}

	public static ObjectDefinition addOrUpdateSystemObjectDefinition(
			long companyId, long objectFolderId,
			com.liferay.object.system.SystemObjectDefinitionManager
				systemObjectDefinitionManager)
		throws PortalException {

		return getService().addOrUpdateSystemObjectDefinition(
			companyId, objectFolderId, systemObjectDefinitionManager);
	}

	public static ObjectDefinition addSystemObjectDefinition(
			String externalReferenceCode, long userId, long objectFolderId,
			String className, String dbTableName, boolean enableComments,
			boolean enableFormContainer, boolean enableFriendlyURLCustomization,
			boolean enableIndexSearch, boolean enableLocalization,
			boolean enableObjectEntryDraft, boolean enableObjectEntrySchedule,
			boolean enableObjectEntrySubscription,
			boolean enableObjectEntryVersioning, String friendlyURLSeparator,
			Map<java.util.Locale, String> labelMap, boolean modifiable,
			String name, String panelAppOrder, String panelCategoryKey,
			String pkObjectFieldDBColumnName, String pkObjectFieldName,
			Map<java.util.Locale, String> pluralLabelMap, boolean portlet,
			String scope, String titleObjectFieldName, int version, int status,
			List<com.liferay.object.model.ObjectDefinitionSetting>
				objectDefinitionSettings,
			List<com.liferay.object.model.ObjectField> objectFields,
			List<com.liferay.portal.kernel.model.WorkflowDefinitionLink>
				workflowDefinitionLinks)
		throws PortalException {

		return getService().addSystemObjectDefinition(
			externalReferenceCode, userId, objectFolderId, className,
			dbTableName, enableComments, enableFormContainer,
			enableFriendlyURLCustomization, enableIndexSearch,
			enableLocalization, enableObjectEntryDraft,
			enableObjectEntrySchedule, enableObjectEntrySubscription,
			enableObjectEntryVersioning, friendlyURLSeparator, labelMap,
			modifiable, name, panelAppOrder, panelCategoryKey,
			pkObjectFieldDBColumnName, pkObjectFieldName, pluralLabelMap,
			portlet, scope, titleObjectFieldName, version, status,
			objectDefinitionSettings, objectFields, workflowDefinitionLinks);
	}

	/**
	 * Creates a new object definition with the primary key. Does not add the object definition to the database.
	 *
	 * @param objectDefinitionId the primary key for the new object definition
	 * @return the new object definition
	 */
	public static ObjectDefinition createObjectDefinition(
		long objectDefinitionId) {

		return getService().createObjectDefinition(objectDefinitionId);
	}

	/**
	 * @throws PortalException
	 */
	public static PersistedModel createPersistedModel(
			Serializable primaryKeyObj)
		throws PortalException {

		return getService().createPersistedModel(primaryKeyObj);
	}

	public static void deleteCompanyObjectDefinitions(long companyId)
		throws PortalException {

		getService().deleteCompanyObjectDefinitions(companyId);
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
	public static ObjectDefinition deleteObjectDefinition(
			long objectDefinitionId)
		throws PortalException {

		return getService().deleteObjectDefinition(objectDefinitionId);
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
	public static ObjectDefinition deleteObjectDefinition(
			ObjectDefinition objectDefinition)
		throws PortalException {

		return getService().deleteObjectDefinition(objectDefinition);
	}

	/**
	 * @throws PortalException
	 */
	public static PersistedModel deletePersistedModel(
			PersistedModel persistedModel)
		throws PortalException {

		return getService().deletePersistedModel(persistedModel);
	}

	public static void deployInactiveObjectDefinition(
		ObjectDefinition objectDefinition) {

		getService().deployInactiveObjectDefinition(objectDefinition);
	}

	public static void deployObjectDefinition(
		ObjectDefinition objectDefinition) {

		getService().deployObjectDefinition(objectDefinition);
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
	 * Useful when paginating results. Returns a maximum of <code>end - start</code> instances. <code>start</code> and <code>end</code> are not primary keys, they are indexes in the result set. Thus, <code>0</code> refers to the first result in the set. Setting both <code>start</code> and <code>end</code> to <code>com.liferay.portal.kernel.dao.orm.QueryUtil#ALL_POS</code> will return the full result set. If <code>orderByComparator</code> is specified, then the query will include the given ORDER BY logic. If <code>orderByComparator</code> is absent, then the query will include the default ORDER BY logic from <code>com.liferay.object.model.impl.ObjectDefinitionModelImpl</code>.
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
	 * Useful when paginating results. Returns a maximum of <code>end - start</code> instances. <code>start</code> and <code>end</code> are not primary keys, they are indexes in the result set. Thus, <code>0</code> refers to the first result in the set. Setting both <code>start</code> and <code>end</code> to <code>com.liferay.portal.kernel.dao.orm.QueryUtil#ALL_POS</code> will return the full result set. If <code>orderByComparator</code> is specified, then the query will include the given ORDER BY logic. If <code>orderByComparator</code> is absent, then the query will include the default ORDER BY logic from <code>com.liferay.object.model.impl.ObjectDefinitionModelImpl</code>.
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

	public static ObjectDefinition enableAccountEntryRestricted(
			com.liferay.object.model.ObjectRelationship objectRelationship)
		throws PortalException {

		return getService().enableAccountEntryRestricted(objectRelationship);
	}

	public static ObjectDefinition
			enableAccountEntryRestrictedForNondefaultStorageType(
				com.liferay.object.model.ObjectField objectField)
		throws PortalException {

		return getService().
			enableAccountEntryRestrictedForNondefaultStorageType(objectField);
	}

	public static ObjectDefinition fetchObjectDefinition(
		long objectDefinitionId) {

		return getService().fetchObjectDefinition(objectDefinitionId);
	}

	public static ObjectDefinition fetchObjectDefinition(
		long companyId, String name) {

		return getService().fetchObjectDefinition(companyId, name);
	}

	public static ObjectDefinition fetchObjectDefinitionByClassName(
		long companyId, String className) {

		return getService().fetchObjectDefinitionByClassName(
			companyId, className);
	}

	public static ObjectDefinition fetchObjectDefinitionByExternalReferenceCode(
		String externalReferenceCode, long companyId) {

		return getService().fetchObjectDefinitionByExternalReferenceCode(
			externalReferenceCode, companyId);
	}

	/**
	 * Returns the object definition with the matching UUID and company.
	 *
	 * @param uuid the object definition's UUID
	 * @param companyId the primary key of the company
	 * @return the matching object definition, or <code>null</code> if a matching object definition could not be found
	 */
	public static ObjectDefinition fetchObjectDefinitionByUuidAndCompanyId(
		String uuid, long companyId) {

		return getService().fetchObjectDefinitionByUuidAndCompanyId(
			uuid, companyId);
	}

	public static ObjectDefinition fetchSystemObjectDefinition(
		long companyId, String name) {

		return getService().fetchSystemObjectDefinition(companyId, name);
	}

	public static com.liferay.portal.kernel.dao.orm.ActionableDynamicQuery
		getActionableDynamicQuery() {

		return getService().getActionableDynamicQuery();
	}

	public static List<ObjectDefinition> getCustomObjectDefinitions(
		int status) {

		return getService().getCustomObjectDefinitions(status);
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
	 * Returns the object definition with the primary key.
	 *
	 * @param objectDefinitionId the primary key of the object definition
	 * @return the object definition
	 * @throws PortalException if a object definition with the primary key could not be found
	 */
	public static ObjectDefinition getObjectDefinition(long objectDefinitionId)
		throws PortalException {

		return getService().getObjectDefinition(objectDefinitionId);
	}

	public static ObjectDefinition getObjectDefinition(
			long companyId, String name)
		throws PortalException {

		return getService().getObjectDefinition(companyId, name);
	}

	public static ObjectDefinition getObjectDefinitionByClassName(
			long companyId, String className)
		throws PortalException {

		return getService().getObjectDefinitionByClassName(
			companyId, className);
	}

	public static ObjectDefinition getObjectDefinitionByExternalReferenceCode(
			String externalReferenceCode, long companyId)
		throws PortalException {

		return getService().getObjectDefinitionByExternalReferenceCode(
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
	public static ObjectDefinition getObjectDefinitionByUuidAndCompanyId(
			String uuid, long companyId)
		throws PortalException {

		return getService().getObjectDefinitionByUuidAndCompanyId(
			uuid, companyId);
	}

	public static List<ObjectDefinition> getObjectDefinitions(
		boolean accountEntryRestricted) {

		return getService().getObjectDefinitions(accountEntryRestricted);
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
	public static List<ObjectDefinition> getObjectDefinitions(
		int start, int end) {

		return getService().getObjectDefinitions(start, end);
	}

	public static List<ObjectDefinition> getObjectDefinitions(
		long companyId, boolean active, boolean system, int status) {

		return getService().getObjectDefinitions(
			companyId, active, system, status);
	}

	public static List<ObjectDefinition> getObjectDefinitions(
		long companyId, boolean active, boolean system, int status, int start,
		int end, OrderByComparator<ObjectDefinition> orderByComparator) {

		return getService().getObjectDefinitions(
			companyId, active, system, status, start, end, orderByComparator);
	}

	public static List<ObjectDefinition> getObjectDefinitions(
		long companyId, boolean active, int status) {

		return getService().getObjectDefinitions(companyId, active, status);
	}

	public static List<ObjectDefinition> getObjectDefinitions(
		long companyId, int status) {

		return getService().getObjectDefinitions(companyId, status);
	}

	/**
	 * Returns the number of object definitions.
	 *
	 * @return the number of object definitions
	 */
	public static int getObjectDefinitionsCount() {
		return getService().getObjectDefinitionsCount();
	}

	public static int getObjectDefinitionsCount(long companyId)
		throws PortalException {

		return getService().getObjectDefinitionsCount(companyId);
	}

	public static int getObjectDefinitionsCount(
			long companyId, boolean active, boolean system, int status)
		throws PortalException {

		return getService().getObjectDefinitionsCount(
			companyId, active, system, status);
	}

	public static List<ObjectDefinition> getObjectFolderObjectDefinitions(
		long objectFolderId) {

		return getService().getObjectFolderObjectDefinitions(objectFolderId);
	}

	public static int getObjectFolderObjectDefinitionsCount(long objectFolderId)
		throws PortalException {

		return getService().getObjectFolderObjectDefinitionsCount(
			objectFolderId);
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

	public static List<ObjectDefinition> getSystemObjectDefinitions() {
		return getService().getSystemObjectDefinitions();
	}

	public static List<ObjectDefinition> getUnmodifiableSystemObjectDefinitions(
		long companyId) {

		return getService().getUnmodifiableSystemObjectDefinitions(companyId);
	}

	public static boolean hasObjectRelationship(long objectDefinitionId) {
		return getService().hasObjectRelationship(objectDefinitionId);
	}

	public static ObjectDefinition publishCustomObjectDefinition(
			long userId, long objectDefinitionId)
		throws PortalException {

		return getService().publishCustomObjectDefinition(
			userId, objectDefinitionId);
	}

	public static ObjectDefinition publishSystemObjectDefinition(
			long userId, long objectDefinitionId)
		throws PortalException {

		return getService().publishSystemObjectDefinition(
			userId, objectDefinitionId);
	}

	public static void undeployObjectDefinition(
		ObjectDefinition objectDefinition) {

		getService().undeployObjectDefinition(objectDefinition);
	}

	public static ObjectDefinition updateCustomObjectDefinition(
			String externalReferenceCode, long objectDefinitionId,
			long accountEntryRestrictedObjectFieldId,
			long descriptionObjectFieldId, long objectFolderId,
			long titleObjectFieldId, boolean accountEntryRestricted,
			boolean active, String className, boolean enableCategorization,
			boolean enableComments, boolean enableFormContainer,
			boolean enableFriendlyURLCustomization, boolean enableIndexSearch,
			boolean enableLocalization, boolean enableObjectEntryDraft,
			boolean enableObjectEntryHistory, boolean enableObjectEntrySchedule,
			boolean enableObjectEntrySubscription,
			boolean enableObjectEntryVersioning, String friendlyURLSeparator,
			Map<java.util.Locale, String> labelMap, String name,
			String panelAppOrder, String panelCategoryKey, boolean portlet,
			Map<java.util.Locale, String> pluralLabelMap, String scope,
			int status,
			List<com.liferay.object.model.ObjectDefinitionSetting>
				objectDefinitionSettings,
			List<com.liferay.portal.kernel.model.WorkflowDefinitionLink>
				workflowDefinitionLinks)
		throws PortalException {

		return getService().updateCustomObjectDefinition(
			externalReferenceCode, objectDefinitionId,
			accountEntryRestrictedObjectFieldId, descriptionObjectFieldId,
			objectFolderId, titleObjectFieldId, accountEntryRestricted, active,
			className, enableCategorization, enableComments,
			enableFormContainer, enableFriendlyURLCustomization,
			enableIndexSearch, enableLocalization, enableObjectEntryDraft,
			enableObjectEntryHistory, enableObjectEntrySchedule,
			enableObjectEntrySubscription, enableObjectEntryVersioning,
			friendlyURLSeparator, labelMap, name, panelAppOrder,
			panelCategoryKey, portlet, pluralLabelMap, scope, status,
			objectDefinitionSettings, workflowDefinitionLinks);
	}

	public static ObjectDefinition updateExternalReferenceCode(
			long objectDefinitionId, String externalReferenceCode)
		throws PortalException {

		return getService().updateExternalReferenceCode(
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
	public static ObjectDefinition updateObjectDefinition(
		ObjectDefinition objectDefinition) {

		return getService().updateObjectDefinition(objectDefinition);
	}

	public static ObjectDefinition updateObjectFolderId(
			long objectDefinitionId, long objectFolderId)
		throws PortalException {

		return getService().updateObjectFolderId(
			objectDefinitionId, objectFolderId);
	}

	public static ObjectDefinition updateSystemObjectDefinition(
			String externalReferenceCode, long objectDefinitionId,
			long objectFolderId, long titleObjectFieldId,
			List<com.liferay.object.model.ObjectDefinitionSetting>
				objectDefinitionSettings,
			List<com.liferay.portal.kernel.model.WorkflowDefinitionLink>
				workflowDefinitionLinks)
		throws PortalException {

		return getService().updateSystemObjectDefinition(
			externalReferenceCode, objectDefinitionId, objectFolderId,
			titleObjectFieldId, objectDefinitionSettings,
			workflowDefinitionLinks);
	}

	public static ObjectDefinition updateTitleObjectFieldId(
			long objectDefinitionId, long titleObjectFieldId)
		throws PortalException {

		return getService().updateTitleObjectFieldId(
			objectDefinitionId, titleObjectFieldId);
	}

	public static void updateUserId(
			long companyId, long oldUserId, long newUserId)
		throws PortalException {

		getService().updateUserId(companyId, oldUserId, newUserId);
	}

	public static ObjectDefinitionLocalService getService() {
		return _serviceSnapshot.get();
	}

	private static final Snapshot<ObjectDefinitionLocalService>
		_serviceSnapshot = new Snapshot<>(
			ObjectDefinitionLocalServiceUtil.class,
			ObjectDefinitionLocalService.class);

}