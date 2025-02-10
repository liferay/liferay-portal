/**
 * SPDX-FileCopyrightText: (c) 2000 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

package com.liferay.object.service;

import com.liferay.object.model.ObjectDefinition;
import com.liferay.portal.kernel.exception.PortalException;
import com.liferay.portal.kernel.module.service.Snapshot;

import java.util.List;
import java.util.Map;

/**
 * Provides the remote service utility for ObjectDefinition. This utility wraps
 * <code>com.liferay.object.service.impl.ObjectDefinitionServiceImpl</code> and is an
 * access point for service operations in application layer code running on a
 * remote server. Methods of this service are expected to have security checks
 * based on the propagated JAAS credentials because this service can be
 * accessed remotely.
 *
 * @author Marco Leo
 * @see ObjectDefinitionService
 * @generated
 */
public class ObjectDefinitionServiceUtil {

	/*
	 * NOTE FOR DEVELOPERS:
	 *
	 * Never modify this class directly. Add custom service methods to <code>com.liferay.object.service.impl.ObjectDefinitionServiceImpl</code> and rerun ServiceBuilder to regenerate this class.
	 */
	public static ObjectDefinition addCustomObjectDefinition(
			long objectFolderId, String className, boolean enableComments,
			boolean enableFriendlyURLCustomization, boolean enableIndexSearch,
			boolean enableLocalization, boolean enableObjectEntryDraft,
			Map<java.util.Locale, String> labelMap, String name,
			String panelAppOrder, String panelCategoryKey,
			Map<java.util.Locale, String> pluralLabelMap, boolean portlet,
			String scope, String storageType,
			List<com.liferay.object.model.ObjectDefinitionSetting>
				objectDefinitionSettings,
			List<com.liferay.object.model.ObjectField> objectFields)
		throws PortalException {

		return getService().addCustomObjectDefinition(
			objectFolderId, className, enableComments,
			enableFriendlyURLCustomization, enableIndexSearch,
			enableLocalization, enableObjectEntryDraft, labelMap, name,
			panelAppOrder, panelCategoryKey, pluralLabelMap, portlet, scope,
			storageType, objectDefinitionSettings, objectFields);
	}

	public static ObjectDefinition addObjectDefinition(
			String externalReferenceCode, long objectFolderId,
			boolean modifiable, String scope, boolean system)
		throws PortalException {

		return getService().addObjectDefinition(
			externalReferenceCode, objectFolderId, modifiable, scope, system);
	}

	public static ObjectDefinition addSystemObjectDefinition(
			String externalReferenceCode, long userId, long objectFolderId,
			boolean enableComments, boolean enableFriendlyURLCustomization,
			boolean enableIndexSearch, boolean enableLocalization,
			Map<java.util.Locale, String> labelMap, String name,
			String panelAppOrder, String panelCategoryKey,
			Map<java.util.Locale, String> pluralLabelMap, boolean portlet,
			String scope,
			List<com.liferay.object.model.ObjectDefinitionSetting>
				objectDefinitionSettings,
			List<com.liferay.object.model.ObjectField> objectFields)
		throws PortalException {

		return getService().addSystemObjectDefinition(
			externalReferenceCode, userId, objectFolderId, enableComments,
			enableFriendlyURLCustomization, enableIndexSearch,
			enableLocalization, labelMap, name, panelAppOrder, panelCategoryKey,
			pluralLabelMap, portlet, scope, objectDefinitionSettings,
			objectFields);
	}

	public static ObjectDefinition deleteObjectDefinition(
			long objectDefinitionId)
		throws PortalException {

		return getService().deleteObjectDefinition(objectDefinitionId);
	}

	public static ObjectDefinition fetchObjectDefinitionByExternalReferenceCode(
			String externalReferenceCode, long companyId)
		throws PortalException {

		return getService().fetchObjectDefinitionByExternalReferenceCode(
			externalReferenceCode, companyId);
	}

	public static ObjectDefinition getObjectDefinition(long objectDefinitionId)
		throws PortalException {

		return getService().getObjectDefinition(objectDefinitionId);
	}

	public static ObjectDefinition getObjectDefinitionByExternalReferenceCode(
			String externalReferenceCode, long companyId)
		throws PortalException {

		return getService().getObjectDefinitionByExternalReferenceCode(
			externalReferenceCode, companyId);
	}

	public static List<ObjectDefinition> getObjectDefinitions(
		int start, int end) {

		return getService().getObjectDefinitions(start, end);
	}

	public static List<ObjectDefinition> getObjectDefinitions(
		long companyId, int start, int end) {

		return getService().getObjectDefinitions(companyId, start, end);
	}

	public static int getObjectDefinitionsCount() throws PortalException {
		return getService().getObjectDefinitionsCount();
	}

	public static int getObjectDefinitionsCount(long companyId)
		throws PortalException {

		return getService().getObjectDefinitionsCount(companyId);
	}

	/**
	 * Returns the OSGi service identifier.
	 *
	 * @return the OSGi service identifier
	 */
	public static String getOSGiServiceIdentifier() {
		return getService().getOSGiServiceIdentifier();
	}

	public static ObjectDefinition publishCustomObjectDefinition(
			long objectDefinitionId)
		throws PortalException {

		return getService().publishCustomObjectDefinition(objectDefinitionId);
	}

	public static ObjectDefinition publishSystemObjectDefinition(
			long objectDefinitionId)
		throws PortalException {

		return getService().publishSystemObjectDefinition(objectDefinitionId);
	}

	public static ObjectDefinition updateCustomObjectDefinition(
			String externalReferenceCode, long objectDefinitionId,
			long accountEntryRestrictedObjectFieldId,
			long descriptionObjectFieldId, long objectFolderId,
			long titleObjectFieldId, boolean accountEntryRestricted,
			boolean active, String className, boolean enableCategorization,
			boolean enableComments, boolean enableFriendlyURLCustomization,
			boolean enableIndexSearch, boolean enableLocalization,
			boolean enableObjectEntryDraft, boolean enableObjectEntryHistory,
			Map<java.util.Locale, String> labelMap, String name,
			String panelAppOrder, String panelCategoryKey, boolean portlet,
			Map<java.util.Locale, String> pluralLabelMap, String scope,
			int status,
			List<com.liferay.object.model.ObjectDefinitionSetting>
				objectDefinitionSettings)
		throws PortalException {

		return getService().updateCustomObjectDefinition(
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

	public static ObjectDefinition updateExternalReferenceCode(
			long objectDefinitionId, String externalReferenceCode)
		throws PortalException {

		return getService().updateExternalReferenceCode(
			objectDefinitionId, externalReferenceCode);
	}

	public static ObjectDefinition updateRootObjectDefinitionId(
			long objectDefinitionId, long rootObjectDefinitionId)
		throws PortalException {

		return getService().updateRootObjectDefinitionId(
			objectDefinitionId, rootObjectDefinitionId);
	}

	public static ObjectDefinition updateSystemObjectDefinition(
			String externalReferenceCode, long objectDefinitionId,
			long objectFolderId, long titleObjectFieldId,
			List<com.liferay.object.model.ObjectDefinitionSetting>
				objectDefinitionSettings)
		throws PortalException {

		return getService().updateSystemObjectDefinition(
			externalReferenceCode, objectDefinitionId, objectFolderId,
			titleObjectFieldId, objectDefinitionSettings);
	}

	public static ObjectDefinition updateTitleObjectFieldId(
			long objectDefinitionId, long titleObjectFieldId)
		throws PortalException {

		return getService().updateTitleObjectFieldId(
			objectDefinitionId, titleObjectFieldId);
	}

	public static ObjectDefinitionService getService() {
		return _serviceSnapshot.get();
	}

	private static final Snapshot<ObjectDefinitionService> _serviceSnapshot =
		new Snapshot<>(
			ObjectDefinitionServiceUtil.class, ObjectDefinitionService.class);

}