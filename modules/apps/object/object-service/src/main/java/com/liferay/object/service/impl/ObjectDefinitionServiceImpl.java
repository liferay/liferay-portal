/**
 * SPDX-FileCopyrightText: (c) 2000 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

package com.liferay.object.service.impl;

import com.liferay.object.constants.ObjectActionKeys;
import com.liferay.object.constants.ObjectConstants;
import com.liferay.object.model.ObjectDefinition;
import com.liferay.object.model.ObjectDefinitionSetting;
import com.liferay.object.model.ObjectField;
import com.liferay.object.model.ObjectFolder;
import com.liferay.object.service.base.ObjectDefinitionServiceBaseImpl;
import com.liferay.portal.aop.AopService;
import com.liferay.portal.kernel.exception.PortalException;
import com.liferay.portal.kernel.security.permission.ActionKeys;
import com.liferay.portal.kernel.security.permission.resource.ModelResourcePermission;
import com.liferay.portal.kernel.security.permission.resource.PortletResourcePermission;
import com.liferay.portal.kernel.workflow.WorkflowConstants;

import java.util.List;
import java.util.Locale;
import java.util.Map;

import org.osgi.service.component.annotations.Component;
import org.osgi.service.component.annotations.Reference;

/**
 * @author Marco Leo
 * @author Brian Wing Shun Chan
 */
@Component(
	property = {
		"json.web.service.context.name=object",
		"json.web.service.context.path=ObjectDefinition"
	},
	service = AopService.class
)
public class ObjectDefinitionServiceImpl
	extends ObjectDefinitionServiceBaseImpl {

	@Override
	public ObjectDefinition addCustomObjectDefinition(
			long objectFolderId, String className, boolean enableComments,
			boolean enableFriendlyURLCustomization, boolean enableIndexSearch,
			boolean enableLocalization, boolean enableObjectEntryDraft,
			Map<Locale, String> labelMap, String name, String panelAppOrder,
			String panelCategoryKey, Map<Locale, String> pluralLabelMap,
			boolean portlet, String scope, String storageType,
			List<ObjectDefinitionSetting> objectDefinitionSettings,
			List<ObjectField> objectFields)
		throws PortalException {

		_portletResourcePermission.check(
			getPermissionChecker(), null,
			ObjectActionKeys.ADD_OBJECT_DEFINITION);

		_objectFolderModelResourcePermission.check(
			getPermissionChecker(), objectFolderId,
			ObjectActionKeys.ADD_OBJECT_DEFINITION);

		return objectDefinitionLocalService.addCustomObjectDefinition(
			getUserId(), objectFolderId, className, enableComments,
			enableFriendlyURLCustomization, enableIndexSearch,
			enableLocalization, enableObjectEntryDraft, labelMap, name,
			panelAppOrder, panelCategoryKey, pluralLabelMap, portlet, scope,
			storageType, objectDefinitionSettings, objectFields);
	}

	@Override
	public ObjectDefinition addObjectDefinition(
			String externalReferenceCode, long objectFolderId,
			boolean modifiable, String scope, boolean system)
		throws PortalException {

		_portletResourcePermission.check(
			getPermissionChecker(), null,
			ObjectActionKeys.ADD_OBJECT_DEFINITION);

		_objectFolderModelResourcePermission.check(
			getPermissionChecker(), objectFolderId,
			ObjectActionKeys.ADD_OBJECT_DEFINITION);

		return objectDefinitionLocalService.addObjectDefinition(
			externalReferenceCode, getUserId(), objectFolderId, modifiable,
			scope, system);
	}

	@Override
	public ObjectDefinition addSystemObjectDefinition(
			String externalReferenceCode, long userId, long objectFolderId,
			boolean enableComments, boolean enableFriendlyURLCustomization,
			boolean enableIndexSearch, boolean enableLocalization,
			Map<Locale, String> labelMap, String name, String panelAppOrder,
			String panelCategoryKey, Map<Locale, String> pluralLabelMap,
			boolean portlet, String scope,
			List<ObjectDefinitionSetting> objectDefinitionSettings,
			List<ObjectField> objectFields)
		throws PortalException {

		_portletResourcePermission.check(
			getPermissionChecker(), null,
			ObjectActionKeys.ADD_OBJECT_DEFINITION);

		_objectFolderModelResourcePermission.check(
			getPermissionChecker(), objectFolderId,
			ObjectActionKeys.ADD_OBJECT_DEFINITION);

		return objectDefinitionLocalService.addSystemObjectDefinition(
			externalReferenceCode, userId, objectFolderId, null, null,
			enableComments, enableFriendlyURLCustomization, enableIndexSearch,
			enableLocalization, labelMap, true, name, panelAppOrder,
			panelCategoryKey, null, null, pluralLabelMap, portlet, scope, null,
			1, WorkflowConstants.STATUS_DRAFT, objectDefinitionSettings,
			objectFields);
	}

	@Override
	public ObjectDefinition deleteObjectDefinition(long objectDefinitionId)
		throws PortalException {

		_objectDefinitionModelResourcePermission.check(
			getPermissionChecker(), objectDefinitionId, ActionKeys.DELETE);

		return objectDefinitionLocalService.deleteObjectDefinition(
			objectDefinitionId);
	}

	@Override
	public ObjectDefinition fetchObjectDefinitionByExternalReferenceCode(
			String externalReferenceCode, long companyId)
		throws PortalException {

		ObjectDefinition objectDefinition =
			objectDefinitionLocalService.
				fetchObjectDefinitionByExternalReferenceCode(
					externalReferenceCode, companyId);

		if (objectDefinition != null) {
			_objectDefinitionModelResourcePermission.check(
				getPermissionChecker(), objectDefinition, ActionKeys.VIEW);
		}

		return objectDefinition;
	}

	@Override
	public ObjectDefinition getObjectDefinition(long objectDefinitionId)
		throws PortalException {

		_objectDefinitionModelResourcePermission.check(
			getPermissionChecker(), objectDefinitionId, ActionKeys.VIEW);

		return objectDefinitionLocalService.getObjectDefinition(
			objectDefinitionId);
	}

	@Override
	public ObjectDefinition getObjectDefinitionByExternalReferenceCode(
			String externalReferenceCode, long companyId)
		throws PortalException {

		ObjectDefinition objectDefinition =
			objectDefinitionLocalService.
				getObjectDefinitionByExternalReferenceCode(
					externalReferenceCode, companyId);

		_objectDefinitionModelResourcePermission.check(
			getPermissionChecker(), objectDefinition, ActionKeys.VIEW);

		return objectDefinition;
	}

	@Override
	public List<ObjectDefinition> getObjectDefinitions(int start, int end) {
		return objectDefinitionLocalService.getObjectDefinitions(start, end);
	}

	@Override
	public List<ObjectDefinition> getObjectDefinitions(
		long companyId, int start, int end) {

		return objectDefinitionPersistence.findByCompanyId(
			companyId, start, end);
	}

	@Override
	public int getObjectDefinitionsCount() throws PortalException {
		return objectDefinitionLocalService.getObjectDefinitionsCount();
	}

	@Override
	public int getObjectDefinitionsCount(long companyId)
		throws PortalException {

		return objectDefinitionLocalService.getObjectDefinitionsCount(
			companyId);
	}

	@Override
	public ObjectDefinition publishCustomObjectDefinition(
			long objectDefinitionId)
		throws PortalException {

		_portletResourcePermission.check(
			getPermissionChecker(), null,
			ObjectActionKeys.PUBLISH_OBJECT_DEFINITION);

		return objectDefinitionLocalService.publishCustomObjectDefinition(
			getUserId(), objectDefinitionId);
	}

	@Override
	public ObjectDefinition publishSystemObjectDefinition(
			long objectDefinitionId)
		throws PortalException {

		_portletResourcePermission.check(
			getPermissionChecker(), null,
			ObjectActionKeys.PUBLISH_OBJECT_DEFINITION);

		return objectDefinitionLocalService.publishSystemObjectDefinition(
			getUserId(), objectDefinitionId);
	}

	@Override
	public ObjectDefinition updateCustomObjectDefinition(
			String externalReferenceCode, long objectDefinitionId,
			long accountEntryRestrictedObjectFieldId,
			long descriptionObjectFieldId, long objectFolderId,
			long titleObjectFieldId, boolean accountEntryRestricted,
			boolean active, String className, boolean enableCategorization,
			boolean enableComments, boolean enableFriendlyURLCustomization,
			boolean enableIndexSearch, boolean enableLocalization,
			boolean enableObjectEntryDraft, boolean enableObjectEntryHistory,
			Map<Locale, String> labelMap, String name, String panelAppOrder,
			String panelCategoryKey, boolean portlet,
			Map<Locale, String> pluralLabelMap, String scope, int status,
			List<ObjectDefinitionSetting> objectDefinitionSettings)
		throws PortalException {

		_objectDefinitionModelResourcePermission.check(
			getPermissionChecker(), objectDefinitionId, ActionKeys.UPDATE);

		_objectFolderModelResourcePermission.check(
			getPermissionChecker(), objectFolderId,
			ObjectActionKeys.ADD_OBJECT_DEFINITION);

		return objectDefinitionLocalService.updateCustomObjectDefinition(
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
	public ObjectDefinition updateExternalReferenceCode(
			long objectDefinitionId, String externalReferenceCode)
		throws PortalException {

		_objectDefinitionModelResourcePermission.check(
			getPermissionChecker(), objectDefinitionId, ActionKeys.UPDATE);

		return objectDefinitionLocalService.updateExternalReferenceCode(
			objectDefinitionId, externalReferenceCode);
	}

	@Override
	public ObjectDefinition updateRootObjectDefinitionId(
			long objectDefinitionId, long rootObjectDefinitionId)
		throws PortalException {

		_objectDefinitionModelResourcePermission.check(
			getPermissionChecker(), objectDefinitionId, ActionKeys.UPDATE);

		return objectDefinitionLocalService.updateRootObjectDefinitionId(
			objectDefinitionId, rootObjectDefinitionId);
	}

	@Override
	public ObjectDefinition updateSystemObjectDefinition(
			String externalReferenceCode, long objectDefinitionId,
			long objectFolderId, long titleObjectFieldId,
			List<ObjectDefinitionSetting> objectDefinitionSettings)
		throws PortalException {

		_objectDefinitionModelResourcePermission.check(
			getPermissionChecker(), objectDefinitionId, ActionKeys.UPDATE);

		_objectFolderModelResourcePermission.check(
			getPermissionChecker(), objectFolderId,
			ObjectActionKeys.ADD_OBJECT_DEFINITION);

		return objectDefinitionLocalService.updateSystemObjectDefinition(
			externalReferenceCode, objectDefinitionId, objectFolderId,
			titleObjectFieldId, objectDefinitionSettings);
	}

	@Override
	public ObjectDefinition updateTitleObjectFieldId(
			long objectDefinitionId, long titleObjectFieldId)
		throws PortalException {

		_objectDefinitionModelResourcePermission.check(
			getPermissionChecker(), objectDefinitionId, ActionKeys.UPDATE);

		return objectDefinitionLocalService.updateTitleObjectFieldId(
			objectDefinitionId, titleObjectFieldId);
	}

	@Reference(
		target = "(model.class.name=com.liferay.object.model.ObjectDefinition)"
	)
	private ModelResourcePermission<ObjectDefinition>
		_objectDefinitionModelResourcePermission;

	@Reference(
		target = "(model.class.name=com.liferay.object.model.ObjectFolder)"
	)
	private ModelResourcePermission<ObjectFolder>
		_objectFolderModelResourcePermission;

	@Reference(target = "(resource.name=" + ObjectConstants.RESOURCE_NAME + ")")
	private PortletResourcePermission _portletResourcePermission;

}