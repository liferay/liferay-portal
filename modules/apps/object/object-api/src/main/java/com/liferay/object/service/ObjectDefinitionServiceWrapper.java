/**
 * SPDX-FileCopyrightText: (c) 2000 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

package com.liferay.object.service;

import com.liferay.portal.kernel.service.ServiceWrapper;

/**
 * Provides a wrapper for {@link ObjectDefinitionService}.
 *
 * @author Marco Leo
 * @see ObjectDefinitionService
 * @generated
 */
public class ObjectDefinitionServiceWrapper
	implements ObjectDefinitionService,
			   ServiceWrapper<ObjectDefinitionService> {

	public ObjectDefinitionServiceWrapper() {
		this(null);
	}

	public ObjectDefinitionServiceWrapper(
		ObjectDefinitionService objectDefinitionService) {

		_objectDefinitionService = objectDefinitionService;
	}

	@Override
	public com.liferay.object.model.ObjectDefinition addCustomObjectDefinition(
			long objectFolderId, String className, boolean enableComments,
			boolean enableFormContainer, boolean enableFriendlyURLCustomization,
			boolean enableIndexSearch, boolean enableLocalization,
			boolean enableObjectEntryDraft, boolean enableObjectEntrySchedule,
			boolean enableObjectEntrySubscription,
			boolean enableObjectEntryVersioning, String friendlyURLSeparator,
			java.util.Map<java.util.Locale, String> labelMap, String name,
			String panelAppOrder, String panelCategoryKey,
			java.util.Map<java.util.Locale, String> pluralLabelMap,
			boolean portlet, String scope, String storageType,
			java.util.List<com.liferay.object.model.ObjectDefinitionSetting>
				objectDefinitionSettings,
			java.util.List<com.liferay.object.model.ObjectField> objectFields,
			java.util.List
				<com.liferay.portal.kernel.model.WorkflowDefinitionLink>
					workflowDefinitionLinks)
		throws com.liferay.portal.kernel.exception.PortalException {

		return _objectDefinitionService.addCustomObjectDefinition(
			objectFolderId, className, enableComments, enableFormContainer,
			enableFriendlyURLCustomization, enableIndexSearch,
			enableLocalization, enableObjectEntryDraft,
			enableObjectEntrySchedule, enableObjectEntrySubscription,
			enableObjectEntryVersioning, friendlyURLSeparator, labelMap, name,
			panelAppOrder, panelCategoryKey, pluralLabelMap, portlet, scope,
			storageType, objectDefinitionSettings, objectFields,
			workflowDefinitionLinks);
	}

	@Override
	public com.liferay.object.model.ObjectDefinition addObjectDefinition(
			String externalReferenceCode, long objectFolderId,
			boolean modifiable, String scope, boolean system)
		throws com.liferay.portal.kernel.exception.PortalException {

		return _objectDefinitionService.addObjectDefinition(
			externalReferenceCode, objectFolderId, modifiable, scope, system);
	}

	@Override
	public com.liferay.object.model.ObjectDefinition addSystemObjectDefinition(
			String externalReferenceCode, long userId, long objectFolderId,
			String className, boolean enableComments,
			boolean enableFormContainer, boolean enableFriendlyURLCustomization,
			boolean enableIndexSearch, boolean enableLocalization,
			boolean enableObjectEntryDraft, boolean enableObjectEntrySchedule,
			boolean enableObjectEntrySubscription,
			boolean enableObjectEntryVersioning, String friendlyURLSeparator,
			java.util.Map<java.util.Locale, String> labelMap, String name,
			String panelAppOrder, String panelCategoryKey,
			java.util.Map<java.util.Locale, String> pluralLabelMap,
			boolean portlet, String scope,
			java.util.List<com.liferay.object.model.ObjectDefinitionSetting>
				objectDefinitionSettings,
			java.util.List<com.liferay.object.model.ObjectField> objectFields,
			java.util.List
				<com.liferay.portal.kernel.model.WorkflowDefinitionLink>
					workflowDefinitionLinks)
		throws com.liferay.portal.kernel.exception.PortalException {

		return _objectDefinitionService.addSystemObjectDefinition(
			externalReferenceCode, userId, objectFolderId, className,
			enableComments, enableFormContainer, enableFriendlyURLCustomization,
			enableIndexSearch, enableLocalization, enableObjectEntryDraft,
			enableObjectEntrySchedule, enableObjectEntrySubscription,
			enableObjectEntryVersioning, friendlyURLSeparator, labelMap, name,
			panelAppOrder, panelCategoryKey, pluralLabelMap, portlet, scope,
			objectDefinitionSettings, objectFields, workflowDefinitionLinks);
	}

	@Override
	public com.liferay.object.model.ObjectDefinition deleteObjectDefinition(
			long objectDefinitionId)
		throws com.liferay.portal.kernel.exception.PortalException {

		return _objectDefinitionService.deleteObjectDefinition(
			objectDefinitionId);
	}

	@Override
	public com.liferay.object.model.ObjectDefinition
			fetchObjectDefinitionByExternalReferenceCode(
				String externalReferenceCode, long companyId)
		throws com.liferay.portal.kernel.exception.PortalException {

		return _objectDefinitionService.
			fetchObjectDefinitionByExternalReferenceCode(
				externalReferenceCode, companyId);
	}

	@Override
	public java.util.List<com.liferay.object.model.ObjectDefinition>
		getCMSObjectDefinitions(
			long companyId, String[] objectFolderExternalReferenceCodes) {

		return _objectDefinitionService.getCMSObjectDefinitions(
			companyId, objectFolderExternalReferenceCodes);
	}

	@Override
	public com.liferay.object.model.ObjectDefinition getObjectDefinition(
			long objectDefinitionId)
		throws com.liferay.portal.kernel.exception.PortalException {

		return _objectDefinitionService.getObjectDefinition(objectDefinitionId);
	}

	@Override
	public com.liferay.object.model.ObjectDefinition
			getObjectDefinitionByExternalReferenceCode(
				String externalReferenceCode, long companyId)
		throws com.liferay.portal.kernel.exception.PortalException {

		return _objectDefinitionService.
			getObjectDefinitionByExternalReferenceCode(
				externalReferenceCode, companyId);
	}

	@Override
	public java.util.List<com.liferay.object.model.ObjectDefinition>
		getObjectDefinitions(int start, int end) {

		return _objectDefinitionService.getObjectDefinitions(start, end);
	}

	@Override
	public java.util.List<com.liferay.object.model.ObjectDefinition>
		getObjectDefinitions(long companyId, int start, int end) {

		return _objectDefinitionService.getObjectDefinitions(
			companyId, start, end);
	}

	@Override
	public int getObjectDefinitionsCount()
		throws com.liferay.portal.kernel.exception.PortalException {

		return _objectDefinitionService.getObjectDefinitionsCount();
	}

	@Override
	public int getObjectDefinitionsCount(long companyId)
		throws com.liferay.portal.kernel.exception.PortalException {

		return _objectDefinitionService.getObjectDefinitionsCount(companyId);
	}

	/**
	 * Returns the OSGi service identifier.
	 *
	 * @return the OSGi service identifier
	 */
	@Override
	public String getOSGiServiceIdentifier() {
		return _objectDefinitionService.getOSGiServiceIdentifier();
	}

	@Override
	public com.liferay.object.model.ObjectDefinition
			publishCustomObjectDefinition(long objectDefinitionId)
		throws com.liferay.portal.kernel.exception.PortalException {

		return _objectDefinitionService.publishCustomObjectDefinition(
			objectDefinitionId);
	}

	@Override
	public com.liferay.object.model.ObjectDefinition
			publishSystemObjectDefinition(long objectDefinitionId)
		throws com.liferay.portal.kernel.exception.PortalException {

		return _objectDefinitionService.publishSystemObjectDefinition(
			objectDefinitionId);
	}

	@Override
	public com.liferay.object.model.ObjectDefinition
			updateCustomObjectDefinition(
				String externalReferenceCode, long objectDefinitionId,
				long accountEntryRestrictedObjectFieldId,
				long descriptionObjectFieldId, long objectFolderId,
				long titleObjectFieldId, boolean accountEntryRestricted,
				boolean active, String className, boolean enableCategorization,
				boolean enableComments, boolean enableFormContainer,
				boolean enableFriendlyURLCustomization,
				boolean enableIndexSearch, boolean enableLocalization,
				boolean enableObjectEntryDraft,
				boolean enableObjectEntryHistory,
				boolean enableObjectEntrySchedule,
				boolean enableObjectEntrySubscription,
				boolean enableObjectEntryVersioning,
				String friendlyURLSeparator,
				java.util.Map<java.util.Locale, String> labelMap, String name,
				String panelAppOrder, String panelCategoryKey, boolean portlet,
				java.util.Map<java.util.Locale, String> pluralLabelMap,
				String scope, int status,
				java.util.List<com.liferay.object.model.ObjectDefinitionSetting>
					objectDefinitionSettings,
				java.util.List
					<com.liferay.portal.kernel.model.WorkflowDefinitionLink>
						workflowDefinitionLinks)
		throws com.liferay.portal.kernel.exception.PortalException {

		return _objectDefinitionService.updateCustomObjectDefinition(
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

	@Override
	public com.liferay.object.model.ObjectDefinition
			updateExternalReferenceCode(
				long objectDefinitionId, String externalReferenceCode)
		throws com.liferay.portal.kernel.exception.PortalException {

		return _objectDefinitionService.updateExternalReferenceCode(
			objectDefinitionId, externalReferenceCode);
	}

	@Override
	public com.liferay.object.model.ObjectDefinition
			updateSystemObjectDefinition(
				String externalReferenceCode, long objectDefinitionId,
				long objectFolderId, long titleObjectFieldId,
				java.util.List<com.liferay.object.model.ObjectDefinitionSetting>
					objectDefinitionSettings,
				java.util.List
					<com.liferay.portal.kernel.model.WorkflowDefinitionLink>
						workflowDefinitionLinks)
		throws com.liferay.portal.kernel.exception.PortalException {

		return _objectDefinitionService.updateSystemObjectDefinition(
			externalReferenceCode, objectDefinitionId, objectFolderId,
			titleObjectFieldId, objectDefinitionSettings,
			workflowDefinitionLinks);
	}

	@Override
	public com.liferay.object.model.ObjectDefinition updateTitleObjectFieldId(
			long objectDefinitionId, long titleObjectFieldId)
		throws com.liferay.portal.kernel.exception.PortalException {

		return _objectDefinitionService.updateTitleObjectFieldId(
			objectDefinitionId, titleObjectFieldId);
	}

	@Override
	public ObjectDefinitionService getWrappedService() {
		return _objectDefinitionService;
	}

	@Override
	public void setWrappedService(
		ObjectDefinitionService objectDefinitionService) {

		_objectDefinitionService = objectDefinitionService;
	}

	private ObjectDefinitionService _objectDefinitionService;

}